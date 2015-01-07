package org.act.tstream.event;

import org.act.tstream.callback.RunnableCallback;

public class EventManagerZkPusher extends RunnableCallback {

	private EventManager eventManager;

	private RunnableCallback cb;

	/**
	 * @param cb
	 * @param eventManager
	 */
	public EventManagerZkPusher(RunnableCallback cb, EventManager eventManager) {
		this.eventManager = eventManager;
		this.cb = cb;
	}

	@Override
	public void run() {
		eventManager.add(cb);
	}

}
