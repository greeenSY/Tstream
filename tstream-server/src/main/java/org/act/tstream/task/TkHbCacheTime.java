package org.act.tstream.task;

import org.act.tstream.task.heartbeat.TaskHeartbeat;
import org.act.tstream.utils.TimeUtils;

/**
 * TkHbCacheTime is describle taskheartcache (Map<topologyId, Map<taskid,
 * Map<tkHbCacheTime, time>>>)
 */

public class TkHbCacheTime {
	private int nimbusTime;
	private int taskReportedTime;
	private int taskAssignedTime;

	public int getNimbusTime() {
		return nimbusTime;
	}

	public void setNimbusTime(int nimbusTime) {
		this.nimbusTime = nimbusTime;
	}

	public int getTaskReportedTime() {
		return taskReportedTime;
	}

	public void setTaskReportedTime(int taskReportedTime) {
		this.taskReportedTime = taskReportedTime;
	}

	public int getTaskAssignedTime() {
		return taskAssignedTime;
	}

	public void setTaskAssignedTime(int taskAssignedTime) {
		this.taskAssignedTime = taskAssignedTime;
	}

	public void update(TaskHeartbeat zkTaskHeartbeat) {
		int nowSecs = TimeUtils.current_time_secs();
		this.nimbusTime = nowSecs;
		this.taskReportedTime = zkTaskHeartbeat.getTimeSecs();
		this.taskAssignedTime = zkTaskHeartbeat.getTimeSecs()
				- zkTaskHeartbeat.getUptimeSecs();
	}

}
