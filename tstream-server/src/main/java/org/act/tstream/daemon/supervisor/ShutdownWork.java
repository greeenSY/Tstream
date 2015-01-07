package org.act.tstream.daemon.supervisor;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.act.tstream.callback.RunnableCallback;
import org.apache.log4j.Logger;

import org.act.tstream.client.ConfigExtension;
import org.act.tstream.cluster.StormConfig;
import org.act.tstream.daemon.worker.ProcessSimulator;
import org.act.tstream.utils.JStormUtils;
import org.act.tstream.utils.PathUtils;

public class ShutdownWork extends RunnableCallback {

	private static Logger LOG = Logger.getLogger(ShutdownWork.class);
	
	/**
	 * shutdown all workers
	 * 
	 * @param conf
	 * @param supervisorId
	 * @param removed
	 * @param workerThreadPids
	 * @param cgroupManager
	 */
	public void shutWorker(Map conf, String supervisorId,
			Map<String, String> removed,
			ConcurrentHashMap<String, String> workerThreadPids,
			CgroupManager cgroupManager) {

		Map<String, List<String>> workerId2Pids = new HashMap<String, List<String>>();
		
		boolean localMode = false;

		int maxWaitTime = 0;

		for (Entry<String, String> entry : removed.entrySet()) {
			String workerId = entry.getKey();
			String topologyId = entry.getValue();

			LOG.info("Begin to shut down " + topologyId + ":" + workerId);
			try {

				// STORM-LOCAL-DIR/workers/workerId/pids
				String workerPidPath = StormConfig.worker_pids_root(conf,
						workerId);

				List<String> pids = PathUtils.read_dir_contents(workerPidPath);
				workerId2Pids.put(workerId, pids);

				String threadPid = workerThreadPids.get(workerId);

				// local mode
				if (threadPid != null) {
					ProcessSimulator.killProcess(threadPid);
					localMode = true;
					continue;
				}
					
				for (String pid : pids) {
					JStormUtils.process_killed(Integer.parseInt(pid));
				}
				
				maxWaitTime = ConfigExtension
						.getTaskCleanupTimeoutSec(conf);
				// The best design is get getTaskCleanupTimeoutSec from 
				// topology configuration, but topology configuration is likely
				// to be deleted before kill worker, so in order to simplify 
				// the logical, just get task.cleanup.timeout.sec from 
				// supervisor configuration

			} catch (Exception e) {
				LOG.info("Failed to shutdown ", e);
			}
	
		}
		
		JStormUtils.sleepMs(maxWaitTime * 1000);

		for (Entry<String, List<String>> entry : workerId2Pids.entrySet()) {
			String workerId = entry.getKey();
			List<String> pids = entry.getValue();

			if (localMode == false) {
				for (String pid : pids) {
	
					JStormUtils.ensure_process_killed(Integer.parseInt(pid));
					if (cgroupManager != null) {
						cgroupManager.shutDownWorker(workerId, true);
					}
				}
			}

			tryCleanupWorkerDir(conf, workerId);
			LOG.info("Successfully shut down "  + workerId);
		}
	}

	/**
	 * clean the directory , subdirectories of STORM-LOCAL-DIR/workers/workerId
	 * 
	 * 
	 * @param conf
	 * @param workerId
	 * @throws IOException
	 */
	public void tryCleanupWorkerDir(Map conf, String workerId) {
		try {
			// delete heartbeat dir LOCAL_DIR/workers/workid/heartbeats
			PathUtils.rmr(StormConfig.worker_heartbeats_root(conf, workerId));
			// delete pid dir, LOCAL_DIR/workers/workerid/pids
			PathUtils.rmr(StormConfig.worker_pids_root(conf, workerId));
			// delete workerid dir, LOCAL_DIR/worker/workerid
			PathUtils.rmr(StormConfig.worker_root(conf, workerId));
		} catch (Exception e) {
			LOG.warn(e + "Failed to cleanup worker " + workerId
					+ ". Will retry later");
		}
	}
}
