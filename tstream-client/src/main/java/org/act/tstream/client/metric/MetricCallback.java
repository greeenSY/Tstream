package org.act.tstream.client.metric;

import com.codahale.metrics.Metric;

public interface MetricCallback<T extends Metric> {
	void callback(T metric);
}
