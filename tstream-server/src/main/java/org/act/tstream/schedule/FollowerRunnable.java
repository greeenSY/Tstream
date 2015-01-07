package org.act.tstream.schedule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.act.tstream.callback.RunnableCallback;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.thrift7.TException;

import backtype.storm.Config;
import backtype.storm.utils.Utils;

import org.act.tstream.client.ConfigExtension;
import org.act.tstream.cluster.Cluster;
import org.act.tstream.cluster.StormClusterState;
import org.act.tstream.cluster.StormConfig;
import org.act.tstream.daemon.nimbus.NimbusData;
import org.act.tstream.task.Assignment;
import org.act.tstream.utils.JStormServerUtils;
import org.act.tstream.utils.JStormUtils;
import org.act.tstream.utils.NetWorkUtils;
import org.act.tstream.utils.PathUtils;

public class FollowerRunnable implements Runnable {

	private static final Logger LOG = Logger.getLogger(FollowerRunnable.class);

	private NimbusData data;

	private int sleepTime;

	private volatile boolean state = true;

	private RunnableCallback callback;

	private final String hostPort;

	@SuppressWarnings("unchecked")
	public FollowerRunnable(final NimbusData data, int sleepTime) {
		this.data = data;
		this.sleepTime = sleepTime;
		if (!ConfigExtension.isNimbusUseIp(data.getConf())) {
			this.hostPort = NetWorkUtils.hostname()
					+ ":"
					+ String.valueOf(Utils.getInt(data.getConf().get(
							Config.NIMBUS_THRIFT_PORT)));
		} else {
			this.hostPort = NetWorkUtils.ip()
					+ ":"
					+ String.valueOf(Utils.getInt(data.getConf().get(
							Config.NIMBUS_THRIFT_PORT)));
		}
		try {
			this.tryToBeLeader(data.getConf());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			LOG.error("try to be leader error.", e1);
			throw new RuntimeException(e1);
		}
		try {
			data.getStormClusterState().register_nimbus_host(hostPort);
		} catch (Exception e) {
			LOG.error("register nimbus host fail!", e);
			throw new RuntimeException();
		}
		callback = new RunnableCallback() {
			@Override
			public void run() {
				if (!data.isLeader())
					check();
			}
		};
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		LOG.info("Follower Thread starts!");
		while (state) {
			try {
				Thread.sleep(sleepTime);
				if (data.isLeader()) {
					data.getStormClusterState()
							.unregister_nimbus_host(hostPort);
					checkOwnMaster();
					continue;
				}
				if (!data.getStormClusterState().leader_existed()) {
					this.tryToBeLeader(data.getConf());
					continue;
				}
				check();
				data.getStormClusterState().update_follower_hb(hostPort,
						data.uptime());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				continue;
			} catch (Exception e) {
				if (state) {
					LOG.error("Unknow exception ", e);
				}
			}
		}
		LOG.info("Follower Thread has closed!");
	}

	public void clean() {
		state = false;
	}

	private synchronized void check() {

		StormClusterState clusterState = data.getStormClusterState();

		try {
			String master_stormdist_root = StormConfig.masterStormdistRoot(data
					.getConf());

			List<String> code_ids = PathUtils
					.read_dir_contents(master_stormdist_root);

			List<String> assignments_ids = clusterState.assignments(callback);

			List<String> done_ids = new ArrayList<String>();

			for (String id : code_ids) {
				if (assignments_ids.contains(id)) {
					done_ids.add(id);
				}
			}

			for (String id : done_ids) {
				assignments_ids.remove(id);
				code_ids.remove(id);
			}

			for (String topologyId : code_ids) {
				deleteLocalTopology(topologyId);
			}

			for (String id : assignments_ids) {
				Assignment assignment = clusterState.assignment_info(id, null);
				downloadCodeFromMaster(assignment, id);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOG.error("Get stormdist dir error!", e);
			return;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.error("Check error!", e);
			return;
		}
	}

	private void deleteLocalTopology(String topologyId) throws IOException {
		String dir_to_delete = StormConfig.masterStormdistRoot(data.getConf(),
				topologyId);
		try {
			PathUtils.rmr(dir_to_delete);
			LOG.info("delete:" + dir_to_delete + "successfully!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOG.error("delete:" + dir_to_delete + "fail!", e);
		}
	}

	private void downloadCodeFromMaster(Assignment assignment, String topologyId)
			throws IOException, TException {
		try {
			String localRoot = StormConfig.masterStormdistRoot(data.getConf(),
					topologyId);
			String tmpDir = StormConfig.masterInbox(data.getConf()) + "/"
					+ UUID.randomUUID().toString();
			String masterCodeDir = assignment.getMasterCodeDir();
			JStormServerUtils.downloadCodeFromMaster(data.getConf(), tmpDir,
					masterCodeDir, topologyId, false);

			FileUtils.moveDirectory(new File(tmpDir), new File(localRoot));
		} catch (TException e) {
			// TODO Auto-generated catch block
			LOG.error(e + " downloadStormCode failed " + "topologyId:"
					+ topologyId + "masterCodeDir:"
					+ assignment.getMasterCodeDir());
			throw e;
		}
		LOG.info("Finished downloading code for topology id " + topologyId
				+ " from " + assignment.getMasterCodeDir());
	}

	private void tryToBeLeader(final Map conf) throws Exception {
		RunnableCallback masterCallback = new RunnableCallback() {
			@Override
			public void run() {
				try {
					tryToBeLeader(conf);
				} catch (Exception e) {
					LOG.error("To be master error", e);
					JStormUtils.halt_process(30,
							"Cant't to be master" + e.getMessage());
				}
			}
		};
		data.setLeader(data.getStormClusterState().try_to_be_leader(
				Cluster.MASTER_SUBTREE, hostPort, masterCallback));
	}

	/**
	 * Check whether current node is master or not
	 * 
	 * @throws Exception
	 */
	private void checkOwnMaster() throws Exception {
		int retry_times = 10;

		StormClusterState zkClient = data.getStormClusterState();
		for (int i = 0; i < retry_times; i++, JStormUtils.sleepMs(sleepTime)) {

			if (zkClient.leader_existed() == false) {
				continue;
			}

			String zkHost = zkClient.get_leader_host();
			if (hostPort.equals(zkHost) == true) {
				// current process own master
				return;
			}
			LOG.warn("Current Nimbus has start thrift, but fail to own zk master :"
					+ zkHost);
		}

		// current process doesn't own master
		String err = "Current Nimubs fail to own nimbus_master, should halt process";
		LOG.error(err);
		JStormUtils.halt_process(0, err);

	}

}
