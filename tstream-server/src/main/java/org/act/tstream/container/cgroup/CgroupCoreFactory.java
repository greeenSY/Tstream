package org.act.tstream.container.cgroup;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.act.tstream.container.SubSystemType;
import org.act.tstream.container.cgroup.core.BlkioCore;
import org.act.tstream.container.cgroup.core.CgroupCore;
import org.act.tstream.container.cgroup.core.CpuCore;
import org.act.tstream.container.cgroup.core.CpuacctCore;
import org.act.tstream.container.cgroup.core.CpusetCore;
import org.act.tstream.container.cgroup.core.DevicesCore;
import org.act.tstream.container.cgroup.core.FreezerCore;
import org.act.tstream.container.cgroup.core.MemoryCore;
import org.act.tstream.container.cgroup.core.NetClsCore;
import org.act.tstream.container.cgroup.core.NetPrioCore;

public class CgroupCoreFactory {

	public static Map<SubSystemType, CgroupCore> getInstance(
			Set<SubSystemType> types, String dir) {
		Map<SubSystemType, CgroupCore> result = new HashMap<SubSystemType, CgroupCore>();
		for (SubSystemType type : types) {
			switch (type) {
			case blkio:
				result.put(SubSystemType.blkio, new BlkioCore(dir));
				break;
			case cpuacct:
				result.put(SubSystemType.cpuacct, new CpuacctCore(dir));
				break;
			case cpuset:
				result.put(SubSystemType.cpuset, new CpusetCore(dir));
				break;
			case cpu:
				result.put(SubSystemType.cpu, new CpuCore(dir));
				break;
			case devices:
				result.put(SubSystemType.devices, new DevicesCore(dir));
				break;
			case freezer:
				result.put(SubSystemType.freezer, new FreezerCore(dir));
				break;
			case memory:
				result.put(SubSystemType.memory, new MemoryCore(dir));
				break;
			case net_cls:
				result.put(SubSystemType.net_cls, new NetClsCore(dir));
				break;
			case net_prio:
				result.put(SubSystemType.net_prio, new NetPrioCore(dir));
				break;
			default:
				break;
			}
		}
		return result;
	}

}
