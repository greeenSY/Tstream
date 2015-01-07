package org.act.tstream.task.acker;

import org.act.tstream.utils.JStormUtils;

public class AckObject {
	public Long val = null;
	public Integer spout_task = null;
	public boolean failed = false;

	// val xor value
	public void update_ack(Object value) {
		synchronized (this) {
			if (val == null) {
				val = Long.valueOf(0);
			}
			val = JStormUtils.bit_xor(val, value);
		}
	}
}
