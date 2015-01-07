package org.act.tstream.callback;

import org.act.tstream.callback.RunnableCallback;
import org.act.tstream.utils.JStormUtils;

/**
 * Killer callback
 * 
 * @author yannian
 * 
 */

public class AsyncLoopDefaultKill extends RunnableCallback {

	@Override
	public <T> Object execute(T... args) {
		Exception e = (Exception) args[0];
		JStormUtils.halt_process(1, "Async loop died!");
		return e;
	}

	@Override
	public void run() {
		JStormUtils.halt_process(1, "Async loop died!");
	}
}
