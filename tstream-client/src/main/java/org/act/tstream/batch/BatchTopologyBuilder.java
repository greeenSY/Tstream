package org.act.tstream.batch;

import org.act.tstream.batch.impl.BatchSpoutTrigger;
import org.act.tstream.batch.impl.CoordinatedBolt;
import org.act.tstream.batch.util.BatchDef;
import org.apache.log4j.Logger;

import backtype.storm.topology.BoltDeclarer;
import backtype.storm.topology.IBasicBolt;
import backtype.storm.topology.SpoutDeclarer;
import backtype.storm.topology.TopologyBuilder;

import org.act.tstream.batch.BatchTopologyBuilder;
import org.act.tstream.batch.IBatchSpout;
import org.act.tstream.batch.ICommitter;
import org.act.tstream.batch.IPostCommit;
import org.act.tstream.batch.IPrepareCommit;

public class BatchTopologyBuilder {
	private static final Logger LOG = Logger
			.getLogger(BatchTopologyBuilder.class);

	private TopologyBuilder topologyBuilder;

	private SpoutDeclarer spoutDeclarer;

	public BatchTopologyBuilder(String topologyName) {
		topologyBuilder = new TopologyBuilder();

		spoutDeclarer = topologyBuilder.setSpout(BatchDef.SPOUT_TRIGGER,
				new BatchSpoutTrigger(), 1);
	}

	public BoltDeclarer setSpout(String id, IBatchSpout spout, int paralel) {

		BoltDeclarer boltDeclarer = this
				.setBolt(id, (IBatchSpout) spout, paralel);
		boltDeclarer.allGrouping(BatchDef.SPOUT_TRIGGER,
				BatchDef.COMPUTING_STREAM_ID);

		return boltDeclarer;
	}

	public BoltDeclarer setBolt(String id, IBasicBolt bolt, int paralel) {
		CoordinatedBolt coordinatedBolt = new CoordinatedBolt(bolt);

		BoltDeclarer boltDeclarer = topologyBuilder.setBolt(id,
				coordinatedBolt, paralel);

		if (bolt instanceof IPrepareCommit) {
			boltDeclarer.allGrouping(BatchDef.SPOUT_TRIGGER,
					BatchDef.PREPARE_STREAM_ID);
		}

		if (bolt instanceof ICommitter) {
			boltDeclarer.allGrouping(BatchDef.SPOUT_TRIGGER,
					BatchDef.COMMIT_STREAM_ID);
			boltDeclarer.allGrouping(BatchDef.SPOUT_TRIGGER,
					BatchDef.REVERT_STREAM_ID);
		}

		if (bolt instanceof IPostCommit) {
			boltDeclarer.allGrouping(BatchDef.SPOUT_TRIGGER,
					BatchDef.POST_STREAM_ID);
		}

		return boltDeclarer;
	}

	public TopologyBuilder getTopologyBuilder() {
		return topologyBuilder;
	}

}
