package org.act.tstream.callback.impl;

import org.act.tstream.callback.BaseCallback;

import org.act.tstream.cluster.StormStatus;
import org.act.tstream.daemon.nimbus.StatusType;

/**
 * Set the topology status as Active
 * 
 */
public class ActiveTransitionCallback extends BaseCallback {

	@Override
	public <T> Object execute(T... args) {

		return new StormStatus(StatusType.active);
	}

}
