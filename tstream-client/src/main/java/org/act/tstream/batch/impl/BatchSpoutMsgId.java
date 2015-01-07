package org.act.tstream.batch.impl;

import java.io.Serializable;

import org.act.tstream.batch.BatchId;
import org.act.tstream.batch.util.BatchStatus;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import org.act.tstream.batch.impl.BatchSpoutMsgId;

public class BatchSpoutMsgId implements Serializable{

	/**  */
	private static final long serialVersionUID = 2899009971479957517L;
	
	private final BatchId batchId;
	private BatchStatus batchStatus;
	
	protected BatchSpoutMsgId(BatchId batchId, BatchStatus batchStatus) {
		this.batchId = batchId;
		this.batchStatus = batchStatus;
	}
	
	public static BatchSpoutMsgId mkInstance() {
		BatchId batchId = BatchId.mkInstance();
		BatchStatus batchStatus = BatchStatus.COMPUTING;
		
		return new BatchSpoutMsgId(batchId, batchStatus);
	}
	
	
	public BatchStatus getBatchStatus() {
		return batchStatus;
	}

	public void setBatchStatus(BatchStatus batchStatus) {
		this.batchStatus = batchStatus;
	}

	public BatchId getBatchId() {
		return batchId;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
