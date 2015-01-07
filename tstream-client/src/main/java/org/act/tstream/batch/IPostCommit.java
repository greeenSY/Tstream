package org.act.tstream.batch;

import org.act.tstream.batch.BatchId;

import backtype.storm.topology.BasicOutputCollector;


public interface IPostCommit {
	/**
	 * Do after commit
	 * Don't care failure of postCommit
	 * 
	 * @param id
	 */
	void postCommit(BatchId id, BasicOutputCollector collector);
}
