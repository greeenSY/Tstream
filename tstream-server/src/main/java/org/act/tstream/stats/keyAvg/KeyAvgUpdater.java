package org.act.tstream.stats.keyAvg;

import java.util.HashMap;
import java.util.Map;

import org.act.tstream.callback.RunnableCallback;

import org.act.tstream.stats.StatFunction;
import org.act.tstream.stats.rolling.UpdateParams;
import org.act.tstream.utils.Pair;

public class KeyAvgUpdater extends RunnableCallback {

	@SuppressWarnings("unchecked")
	@Override
	public <T> Object execute(T... args) {
		Map<Object, Pair<Long, Long>> curr = null;
		if (args != null && args.length > 0) {
			UpdateParams p = (UpdateParams) args[0];
			if (p.getCurr() != null) {
				curr = (Map<Object, Pair<Long, Long>>) p.getCurr();
			} else {
				curr = new HashMap<Object, Pair<Long, Long>>();
			}
			Object[] keyAvgArgs = p.getArgs();

			Long amt = 1l;
			if (keyAvgArgs.length > 1) {
				amt = Long.parseLong(String.valueOf(keyAvgArgs[1]));
			}
			StatFunction.update_keyed_avg(curr, keyAvgArgs[0], amt);
		}
		return curr;
	}
}
