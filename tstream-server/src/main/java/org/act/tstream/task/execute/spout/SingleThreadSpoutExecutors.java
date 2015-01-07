package org.act.tstream.task.execute.spout;

import java.util.Map;

import org.apache.log4j.Logger;

import backtype.storm.task.TopologyContext;
import backtype.storm.utils.DisruptorQueue;
import backtype.storm.utils.WorkerClassLoader;

import org.act.tstream.metric.MetricDef;
import org.act.tstream.metric.Metrics;
import org.act.tstream.stats.CommonStatsRolling;
import org.act.tstream.task.TaskStatus;
import org.act.tstream.task.TaskTransfer;
import org.act.tstream.task.acker.Acker;
import org.act.tstream.task.comm.TaskSendTargets;
import org.act.tstream.task.comm.TupleInfo;
import org.act.tstream.task.error.ITaskReportErr;
import org.act.tstream.utils.RotatingMap;
import com.codahale.metrics.Gauge;

/**
 * spout executor
 * 
 * All spout actions will be done here
 * 
 * @author yannian/Longda
 * 
 */
public class SingleThreadSpoutExecutors extends SpoutExecutors {
	private static Logger LOG = Logger
			.getLogger(SingleThreadSpoutExecutors.class);

	public SingleThreadSpoutExecutors(backtype.storm.spout.ISpout _spout,
			TaskTransfer _transfer_fn,
			Map<Integer, DisruptorQueue> innerTaskTransfer, Map _storm_conf,
			DisruptorQueue deserializeQueue, TaskSendTargets sendTargets,
			TaskStatus taskStatus, TopologyContext topology_context,
			TopologyContext _user_context, CommonStatsRolling _task_stats,
			ITaskReportErr _report_error) {
		super(_spout, _transfer_fn, innerTaskTransfer, _storm_conf,
				deserializeQueue, sendTargets, taskStatus, topology_context,
				_user_context, _task_stats, _report_error);

		// sending Tuple's TimeCacheMap
		pending = new RotatingMap<Long, TupleInfo>(Acker.TIMEOUT_BUCKET_NUM, null, true);
		Metrics.register(idStr, MetricDef.PENDING_MAP, new Gauge<Integer>() {

			@Override
			public Integer getValue() {
				return pending.size();
			}
			
		}, String.valueOf(taskId), Metrics.MetricType.TASK);

		super.prepare(sendTargets, _transfer_fn, topology_context);
	}
	
	@Override
	public String getThreadName() {
		return idStr + "-" +SingleThreadSpoutExecutors.class.getSimpleName();
	}

	@Override
	public void run() {
		WorkerClassLoader.switchThreadContext();
		try {

			executeEvent();

			super.nextTuple();
		} finally {
			WorkerClassLoader.restoreThreadContext();
		}

	}

	private void executeEvent() {
		try {
			exeQueue.consumeBatch(this);

		} catch (Exception e) {
			if (taskStatus.isShutdown() == false) {
				LOG.error("Actor occur unknow exception ", e);
			}
		}

	}

}
