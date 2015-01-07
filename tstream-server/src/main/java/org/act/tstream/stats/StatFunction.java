package org.act.tstream.stats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.act.tstream.callback.RunnableCallback;

import org.act.tstream.common.stats.StatBuckets;
import org.act.tstream.stats.incval.IncValExtractor;
import org.act.tstream.stats.incval.IncValMerger;
import org.act.tstream.stats.incval.IncValUpdater;
import org.act.tstream.stats.keyAvg.KeyAvgExtractor;
import org.act.tstream.stats.keyAvg.KeyAvgMerge;
import org.act.tstream.stats.keyAvg.KeyAvgUpdater;
import org.act.tstream.stats.rolling.RollingWindowSet;
import org.act.tstream.utils.JStormUtils;
import org.act.tstream.utils.Pair;

public class StatFunction {

	public static final Integer NUM_STAT_BUCKETS = StatBuckets.NUM_STAT_BUCKETS;

	/**
	 * create simple counter statics
	 * 
	 * @param num_buckets
	 * @param bucket_sizes
	 * @return
	 */
	public static RollingWindowSet keyed_counter_rolling_window_set(
			int num_buckets, Integer[] bucket_sizes) {

		RunnableCallback updater = new IncValUpdater();
		RunnableCallback merger = new IncValMerger();

		RunnableCallback extractor = new IncValExtractor();
		return RollingWindowSet.rolling_window_set(updater, merger, extractor,
				num_buckets, bucket_sizes);
	}

	/**
	 * create averge statics
	 * 
	 * @param num_buckets
	 * @param bucket_sizes
	 * @return
	 */
	public static RollingWindowSet keyed_avg_rolling_window_set(
			int num_buckets, Integer[] bucket_sizes) {
		RunnableCallback updater = new KeyAvgUpdater();

		RunnableCallback merger = new KeyAvgMerge();

		RunnableCallback extractor = new KeyAvgExtractor();

		return RollingWindowSet.rolling_window_set(updater, merger, extractor,
				num_buckets, bucket_sizes);
	}

	public static void incr_val(Map<Object, Long> map, Object key, Long amt) {
		Long value = Long.valueOf(0);
		if (map.containsKey(key)) {
			value = map.get(key);
		}
		value = (Long) JStormUtils.add(value, amt);
		map.put(key, value);
	}

	public static void incr_val(Map<Object, Long> map, Object key) {
		incr_val(map, key, Long.valueOf(1));
	}

	public static synchronized Pair<Long, Long> update_avg(
			Pair<Long, Long> curr, long val) {
		curr.setFirst(curr.getFirst() + val);
		curr.setSecond(curr.getSecond() + 1);
		return curr;
	}

	public static Pair<Long, Long> merge_avg(List<Pair<Long, Long>> avg) {
		Pair<Long, Long> rtn = new Pair<Long, Long>(0l, 0l);
		for (Pair<Long, Long> p : avg) {
			rtn.setFirst(rtn.getFirst() + p.getFirst());
			rtn.setSecond(rtn.getSecond() + p.getSecond());
		}
		return rtn;
	}

	public static double extract_avg(Pair<Long, Long> p) {
		if (p.getSecond() == 0) {
			return 0d;
		}
		return (p.getFirst() * 1.0) / p.getSecond();
	}

	public static void update_keyed_avg(Map<Object, Pair<Long, Long>> map,
			Object key, long val) {
		Pair<Long, Long> p = map.get(key);
		if (p == null) {
			p = new Pair<Long, Long>(0l, 0l);
		}
		update_avg(p, val);
		map.put(key, p);
	}

	public static Pair<Long, Long> merge_keyed_avg(List<Pair<Long, Long>> avg) {
		return merge_avg(avg);
	}

	public static Map<Object, Double> extract_key_avg(
			Map<Object, Pair<Long, Long>> map) {
		Map<Object, Double> rtn = new HashMap<Object, Double>();
		if (map != null) {
			for (Entry<Object, Pair<Long, Long>> e : map.entrySet()) {
				rtn.put(e.getKey(), extract_avg(e.getValue()));
			}
		}
		return rtn;
	}

	public static Map<Object, Long> counter_extract(Map<Object, Long> v) {
		if (v == null) {
			return new HashMap<Object, Long>();
		}
		return v;
	}

}
