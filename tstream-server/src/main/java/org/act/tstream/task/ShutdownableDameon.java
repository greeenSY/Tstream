package org.act.tstream.task;

import backtype.storm.daemon.Shutdownable;

import org.act.tstream.cluster.DaemonCommon;

public interface ShutdownableDameon extends Shutdownable, DaemonCommon,
		Runnable {

}
