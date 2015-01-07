package org.act.tstream.schedule.default_assign.Selector;

import java.util.List;

import org.act.tstream.schedule.default_assign.ResourceWorkerSlot;

public interface Selector {
	public List<ResourceWorkerSlot> select(List<ResourceWorkerSlot> result,
			String name);
}
