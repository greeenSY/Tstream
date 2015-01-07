package org.act.tstream.schedule;

import java.util.Map;
import java.util.Set;

import org.act.tstream.schedule.default_assign.ResourceWorkerSlot;
import org.act.tstream.utils.FailedAssignTopologyException;

public interface IToplogyScheduler {
	void prepare(Map conf);

	Set<ResourceWorkerSlot> assignTasks(TopologyAssignContext contex)
			throws FailedAssignTopologyException;
}
