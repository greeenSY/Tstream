package org.act.tstream.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import backtype.storm.utils.Utils;

import org.act.tstream.client.ConfigExtension;
import org.act.tstream.client.WorkerAssignment;

public class JStromServerConfigExtension extends ConfigExtension {

	public static List<WorkerAssignment> getUserDefineAssignment(Map conf) {
		List<WorkerAssignment> ret = new ArrayList<WorkerAssignment>();
		if (conf.get(USE_USERDEFINE_ASSIGNMENT) == null)
			return ret;
		for (String worker : (List<String>) conf.get(USE_USERDEFINE_ASSIGNMENT)) {
			ret.add(WorkerAssignment.parseFromObj(Utils.from_json(worker)));
		}
		return ret;
	}

	public static boolean isUseOldAssignment(Map conf) {
		return JStormUtils.parseBoolean(conf.get(USE_OLD_ASSIGNMENT), false);
	}

	public static long getMemSizePerWorker(Map conf) {
		long size = JStormUtils.parseLong(conf.get(MEMSIZE_PER_WORKER),
				JStormUtils.SIZE_1_G * 2);
		return size > 0 ? size : JStormUtils.SIZE_1_G * 2;
	}

	public static int getCpuSlotPerWorker(Map conf) {
		int slot = JStormUtils.parseInt(conf.get(CPU_SLOT_PER_WORKER), 1);
		return slot > 0 ? slot : 1;
	}
}
