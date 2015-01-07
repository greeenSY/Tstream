package org.act.tstream.task.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import org.act.tstream.daemon.worker.WorkerData;
import org.act.tstream.utils.IntervalCheck;
import org.act.tstream.utils.JStormUtils;
import org.act.tstream.utils.RandomRange;
import com.esotericsoftware.minlog.Log;

/**
 * 
 * 
 * @author zhongyan.feng
 * @version 
 */
public class MkLocalFirst {
	private static final Logger LOG = Logger.getLogger(MkLocalFirst.class);

	private List<Integer> allOutTasks;
	private List<Integer> outTasks;
	private RandomRange randomrange;
	private boolean isLocalWorker;
	private WorkerData workerData;
	private IntervalCheck intervalCheck;
	private Set<Integer> lastLocalNodeTasks;

	public MkLocalFirst(List<Integer> workerTasks, List<Integer> allOutTasks,
			WorkerData workerData) {

		intervalCheck = new IntervalCheck();
		intervalCheck.setInterval(60);

		this.allOutTasks = allOutTasks;
		this.workerData = workerData;

		List<Integer> localWorkerOutTasks = new ArrayList<Integer>();

		for (Integer outTask : allOutTasks) {
			if (workerTasks.contains(outTask)) {
				localWorkerOutTasks.add(outTask);
			}
		}

		if (localWorkerOutTasks.size() != 0) {
			isLocalWorker = true;
			this.outTasks = localWorkerOutTasks;
			randomrange = new RandomRange(outTasks.size());

			return;
		} else {
			this.isLocalWorker = false;
			this.outTasks = allOutTasks;
			randomrange = new RandomRange(outTasks.size());
			refreshLocalNodeTasks();
			return;
		}

	}

	/**
	 * Don't need to take care of multiple thread,
	 * One task one thread
	 */
	private void refreshLocalNodeTasks() {
		Set<Integer> localNodeTasks = workerData.getLocalNodeTasks();

		if (localNodeTasks == null) {
			this.outTasks = allOutTasks;
			randomrange = new RandomRange(outTasks.size());
			return;
		}

		if (localNodeTasks.equals(lastLocalNodeTasks)) {
			// no local task changed
			return;
		}
		LOG.info("Old localNodeTasks:" + lastLocalNodeTasks + ", new:" + localNodeTasks);
		lastLocalNodeTasks = localNodeTasks;

		List<Integer> localNodeOutTasks = new ArrayList<Integer>();

		for (Integer outTask : allOutTasks) {
			if (localNodeTasks.contains(outTask)) {
				localNodeOutTasks.add(outTask);
			}
		}

		if (localNodeOutTasks.isEmpty() == false) {
			this.outTasks = localNodeOutTasks;
		} else {
			this.outTasks = allOutTasks;
		}

		randomrange = new RandomRange(outTasks.size());
	}

	private List<Integer> innerGroup(List<Object> values) {
		int index = randomrange.nextInt();

		return JStormUtils.mk_list(outTasks.get(index));
	}

	public List<Integer> grouper(List<Object> values) {
		if (isLocalWorker == true) {
			return innerGroup(values);
		}

		if (intervalCheck.check()) {
			refreshLocalNodeTasks();
		}

		return innerGroup(values);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
