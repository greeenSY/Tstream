package org.act.tstream.task;

import org.act.tstream.utils.TimeUtils;

/**
 * Get how long task runs
 * 
 * @author yannian
 * 
 */
public class UptimeComputer {
	int start_time = 0;

	public UptimeComputer() {
		start_time = TimeUtils.current_time_secs();
	}

	public synchronized int uptime() {
		return TimeUtils.time_delta(start_time);
	}
}
