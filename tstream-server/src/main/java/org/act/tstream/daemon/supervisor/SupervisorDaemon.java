package org.act.tstream.daemon.supervisor;

import java.util.Map;

public interface SupervisorDaemon {

	public String getId();

	public Map getConf();

	public void ShutdownAllWorkers();

}
