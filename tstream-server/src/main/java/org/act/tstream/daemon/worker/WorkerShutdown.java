package org.act.tstream.daemon.worker;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.act.tstream.callback.AsyncLoopThread;
import org.apache.log4j.Logger;

import backtype.storm.messaging.IConnection;
import backtype.storm.messaging.IContext;
import backtype.storm.scheduler.WorkerSlot;

import org.act.tstream.cluster.ClusterState;
import org.act.tstream.cluster.StormClusterState;
import org.act.tstream.daemon.worker.metrics.MetricReporter;
import org.act.tstream.task.ShutdownableDameon;
import org.act.tstream.task.TaskShutdownDameon;
import org.act.tstream.utils.JStormUtils;

/**
 * Shutdown worker
 * 
 * @author yannian/Longda
 * 
 */
public class WorkerShutdown implements ShutdownableDameon {
	private static Logger LOG = Logger.getLogger(WorkerShutdown.class);

	public static final String HOOK_SIGNAL = "USR2";

	private List<TaskShutdownDameon> shutdowntasks;
	private AtomicBoolean active;
	private ConcurrentHashMap<WorkerSlot, IConnection> nodeportSocket;
	private IContext context;
	private List<AsyncLoopThread> threads;
	private StormClusterState zkCluster;
	private ClusterState cluster_state;
	private MetricReporter metricReporter;

	// active nodeportSocket context zkCluster zkClusterstate
	public WorkerShutdown(WorkerData workerData,
			List<TaskShutdownDameon> _shutdowntasks,
			List<AsyncLoopThread> _threads, MetricReporter metricReporter) {

		this.shutdowntasks = _shutdowntasks;
		this.threads = _threads;

		this.active = workerData.getActive();
		this.nodeportSocket = workerData.getNodeportSocket();
		this.context = workerData.getContext();
		this.zkCluster = workerData.getZkCluster();
		this.cluster_state = workerData.getZkClusterstate();
		this.metricReporter = metricReporter;

		Runtime.getRuntime().addShutdownHook(new Thread(this));

		// PreCleanupTasks preCleanupTasks = new PreCleanupTasks();
		// // install signals
		// Signal sig = new Signal(HOOK_SIGNAL);
		// Signal.handle(sig, preCleanupTasks);
	}

	@Override
	public void shutdown() {

		active.set(false);

		metricReporter.shutdown();

		// shutdown tasks
		for (ShutdownableDameon task : shutdowntasks) {
			task.shutdown();
		}

		// shutdown worker's demon thread
		// refreshconn, refreshzk, hb, drainer
		for (AsyncLoopThread t : threads) {
			LOG.info("Begin to shutdown " + t.getThread().getName());
			t.cleanup();
			JStormUtils.sleepMs(100);
			t.interrupt();
			// try {
			// t.join();
			// } catch (InterruptedException e) {
			// LOG.error("join thread", e);
			// }
			LOG.info("Successfully " + t.getThread().getName());
		}

		// send data to close connection
		for (WorkerSlot k : nodeportSocket.keySet()) {
			IConnection value = nodeportSocket.get(k);
			value.close();
		}

		context.term();

		// close ZK client
		try {
			zkCluster.disconnect();
			cluster_state.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.info("Shutdown error,", e);
		}

		JStormUtils.halt_process(0, "!!!Shutdown!!!");
	}

	public void join() throws InterruptedException {
		for (TaskShutdownDameon task : shutdowntasks) {
			task.join();
		}
		for (AsyncLoopThread t : threads) {
			t.join();
		}

	}

	public boolean waiting() {
		Boolean isExistsWait = false;
		for (ShutdownableDameon task : shutdowntasks) {
			if (task.waiting()) {
				isExistsWait = true;
				break;
			}
		}
		for (AsyncLoopThread thr : threads) {
			if (thr.isSleeping()) {
				isExistsWait = true;
				break;
			}
		}
		return isExistsWait;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		shutdown();
	}

	// class PreCleanupTasks implements SignalHandler {
	//
	// @Override
	// public void handle(Signal arg0) {
	// LOG.info("Receive " + arg0.getName() + ", begin to do pre_cleanup job");
	//
	// for (ShutdownableDameon task : shutdowntasks) {
	// task.shutdown();
	// }
	//
	// LOG.info("Successfully do pre_cleanup job");
	// }
	//
	// }

}
