package org.act.tstream.callback.impl;

import org.act.tstream.cluster.StormStatus;
import org.act.tstream.daemon.nimbus.NimbusData;
import org.act.tstream.daemon.nimbus.StatusType;

/**
 * The action when nimbus receive rebalance command. Rebalance command is only
 * valid when current status is active
 * 
 * 1. change current topology status as rebalancing 2. do_rebalance action after
 * 2 * TIMEOUT seconds
 * 
 * @author Lixin/Longda
 * 
 */
public class RebalanceTransitionCallback extends DelayStatusTransitionCallback {


	public RebalanceTransitionCallback(NimbusData data, String topologyid,
			StormStatus status) {
		super(data, topologyid, status, StatusType.rebalancing, StatusType.do_rebalance);
	}

	

}
