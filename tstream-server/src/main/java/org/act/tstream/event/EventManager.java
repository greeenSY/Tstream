package org.act.tstream.event;

import org.act.tstream.callback.RunnableCallback;

public interface EventManager {
	public void add(RunnableCallback event_fn);

	public boolean waiting();

	public void shutdown();
}
