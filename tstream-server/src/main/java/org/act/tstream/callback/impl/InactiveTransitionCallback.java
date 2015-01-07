package org.act.tstream.callback.impl;

import org.act.tstream.callback.BaseCallback;

import org.act.tstream.cluster.StormStatus;
import org.act.tstream.daemon.nimbus.StatusType;

/**
 * 
 * set Topology status as inactive
 * 
 * Here just return inactive status Later, it will set inactive status to ZK
 */
public class InactiveTransitionCallback extends BaseCallback {

	@Override
	public <T> Object execute(T... args) {

		return new StormStatus(StatusType.inactive);
	}

}
