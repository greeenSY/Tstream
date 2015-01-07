package org.act.tstream.schedule;

import org.act.tstream.daemon.nimbus.NimbusData;
import org.act.tstream.daemon.nimbus.NimbusUtils;
import org.act.tstream.daemon.nimbus.StatusType;

public class DelayEventRunnable implements Runnable {

	private NimbusData data;
	private String topologyid;
	private StatusType status;

	public DelayEventRunnable(NimbusData data, String topologyid,
			StatusType status) {
		this.data = data;
		this.topologyid = topologyid;
		this.status = status;
	}

	@Override
	public void run() {
		NimbusUtils.transition(data, topologyid, false, status);
	}

}
