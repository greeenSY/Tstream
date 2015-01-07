package org.act.tstream.daemon.worker;

import org.act.tstream.callback.RunnableCallback;

import org.act.tstream.utils.JStormUtils;

public class WorkerHaltRunable extends RunnableCallback {

	@Override
	public void run() {
		JStormUtils.halt_process(1, "Task died");
	}

}
