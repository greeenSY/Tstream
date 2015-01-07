package org.act.tstream.callback.impl;

import org.act.tstream.callback.BaseCallback;

import org.act.tstream.cluster.StormStatus;
import org.act.tstream.daemon.nimbus.NimbusData;
import org.act.tstream.daemon.nimbus.TopologyAssign;
import org.act.tstream.daemon.nimbus.TopologyAssignEvent;

/**
 * 1. every Config.NIMBUS_MONITOR_FREQ_SECS will call MonitorRunnable 2.
 * MonitorRunnable will call NimbusData.transition 3. NimbusData.transition will
 * this callback
 * 
 * 
 */
public class ReassignTransitionCallback extends BaseCallback {

	private NimbusData data;
	private String topologyid;
	private StormStatus oldStatus;

	public ReassignTransitionCallback(NimbusData data, String topologyid) {
		this.data = data;
		this.topologyid = topologyid;
		this.oldStatus = null;
	}

	public ReassignTransitionCallback(NimbusData data, String topologyid,
			StormStatus oldStatus) {
		this.data = data;
		this.topologyid = topologyid;
		this.oldStatus = oldStatus;
	}

	@Override
	public <T> Object execute(T... args) {

		// default is true
		TopologyAssignEvent assignEvent = new TopologyAssignEvent();
		assignEvent.setTopologyId(topologyid);
		assignEvent.setScratch(false);
		assignEvent.setOldStatus(oldStatus);

		TopologyAssign.push(assignEvent);

		return null;
	}

}
