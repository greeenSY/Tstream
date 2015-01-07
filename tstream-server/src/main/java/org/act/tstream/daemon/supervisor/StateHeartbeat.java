package org.act.tstream.daemon.supervisor;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import org.act.tstream.daemon.worker.State;
import org.act.tstream.daemon.worker.WorkerHeartbeat;

/**
 * Worker's state and Hearbeat
 * 
 * @author Xin.Zhou
 */
public class StateHeartbeat {
	private State state;
	private WorkerHeartbeat hb;

	public StateHeartbeat(State state, WorkerHeartbeat hb) {
		this.state = state;
		this.hb = hb;
	}

	public State getState() {
		return this.state;
	}

	public WorkerHeartbeat getHeartbeat() {
		return this.hb;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
