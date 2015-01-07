package org.act.tstream.stats.keyAvg;

import java.util.HashMap;
import java.util.Map;

import org.act.tstream.callback.RunnableCallback;

import org.act.tstream.stats.StatFunction;
import org.act.tstream.utils.Pair;

public class KeyAvgExtractor extends RunnableCallback {

	@SuppressWarnings("unchecked")
	@Override
	public <T> Object execute(T... args) {
		Map<Object, Double> result = null;
		if (args != null && args.length > 0) {
			Map<Object, Pair<Long, Long>> v = (Map<Object, Pair<Long, Long>>) args[0];
			result = StatFunction.extract_key_avg(v);
		}

		if (result == null) {
			result = new HashMap<Object, Double>();
		}

		return result;
	}
}
