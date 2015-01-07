package org.act.tstream.callback;

import org.apache.log4j.Logger;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;

import org.act.tstream.callback.DefaultWatcherCallBack;
import org.act.tstream.callback.WatcherCallBack;
import org.act.tstream.zk.ZkEventTypes;
import org.act.tstream.zk.ZkKeeperStates;

/**
 * Default ZK watch callback
 * 
 * @author yannian
 * 
 */
public class DefaultWatcherCallBack implements WatcherCallBack {

	private static Logger LOG = Logger.getLogger(DefaultWatcherCallBack.class);

	@Override
	public void execute(KeeperState state, EventType type, String path) {
		LOG.info("Zookeeper state update:" + ZkKeeperStates.getStateName(state)
				+ "," + ZkEventTypes.getStateName(type) + "," + path);
	}

}
