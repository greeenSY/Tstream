package org.act.tstream.utils;

public interface ExpiredCallback<K, V> {
	public void expire(K key, V val);
}
