package org.geogebra.common.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUMap<K, V> extends LinkedHashMap<K, V> {

	private static final int MAX_ENTRIES = 100;

	/**
	 * https://docs.oracle.com/javase/6/docs/api/java/util/LinkedHashMap.html#
	 * removeEldestEntry(java.util.Map.Entry)
	 * 
	 * @param eldest
	 *            entry
	 * @return whether eldest enry should be removed
	 */
	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return size() > MAX_ENTRIES;
	}

}