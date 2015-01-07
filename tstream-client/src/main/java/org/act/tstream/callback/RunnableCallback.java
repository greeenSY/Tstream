package org.act.tstream.callback;

import org.act.tstream.callback.Callback;


/**
 * Base Runnable/Callback function
 * 
 * @author yannian
 * 
 */
public class RunnableCallback implements Runnable, Callback {

	@Override
	public <T> Object execute(T... args) {
		return null;
	}

	@Override
	public void run() {

	}

	public Exception error() {
		return null;
	}

	public Object getResult() {
		return null;
	}

	/**
	 * Called by exception
	 */
	public void shutdown() {
	}
	
	/**
	 * Normal quit
	 */
	public void cleanup() {
	    
	}
	
	public String getThreadName() {
		return null;
	}

}
