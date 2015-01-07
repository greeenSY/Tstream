package org.act.tstream.stats.incval;

import java.util.HashMap;
import java.util.Map;

import org.act.tstream.callback.RunnableCallback;

import org.act.tstream.stats.StatFunction;
import org.act.tstream.stats.rolling.UpdateParams;

public class IncValUpdater extends RunnableCallback {

	@SuppressWarnings("unchecked")
	@Override
	public <T> Object execute(T... args) {
		Map<Object, Long> curr = null;
		if (args != null && args.length > 0) {
			UpdateParams p = (UpdateParams) args[0];
			if (p.getCurr() != null) {
				curr = (Map<Object, Long>) p.getCurr();
			} else {
				curr = new HashMap<Object, Long>();
			}
			Object[] incArgs = p.getArgs();

			Long amt = 1l;

			if (incArgs.length > 1) {
				amt = Long.parseLong(String.valueOf(incArgs[1]));
			}
			StatFunction.incr_val(curr, incArgs[0], amt);

		}
		return curr;
	}

}
