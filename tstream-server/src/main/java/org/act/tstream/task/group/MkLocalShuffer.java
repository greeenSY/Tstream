package org.act.tstream.task.group;

import java.util.ArrayList;
import java.util.List;

import org.act.tstream.utils.JStormUtils;
import org.act.tstream.utils.RandomRange;

public class MkLocalShuffer {

	private List<Integer> outTasks;
	private RandomRange randomrange;
	private boolean isLocal;

	public MkLocalShuffer(List<Integer> workerTasks, List<Integer> allOutTasks) {
		List<Integer> localOutTasks = new ArrayList<Integer>();

		for (Integer outTask : allOutTasks) {
			if (workerTasks.contains(outTask)) {
				localOutTasks.add(outTask);
			}
		}

		if (localOutTasks.size() != 0) {
			this.outTasks = localOutTasks;
			isLocal = true;
		} else {
			this.outTasks = new ArrayList<Integer>() ;
			this.outTasks.addAll(allOutTasks);
			isLocal = false;
		}

		randomrange = new RandomRange(outTasks.size());
	}

	public List<Integer> grouper(List<Object> values) {
		int index = randomrange.nextInt();

		return JStormUtils.mk_list(outTasks.get(index));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
