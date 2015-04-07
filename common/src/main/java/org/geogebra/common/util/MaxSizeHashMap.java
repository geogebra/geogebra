package org.geogebra.common.util;

import java.util.Iterator;
import java.util.LinkedHashMap;

public class MaxSizeHashMap<V, T> extends LinkedHashMap<V, T> {

	private static final long serialVersionUID = 1L;

	private int maxSize;

	public MaxSizeHashMap(int maxSize) {
		this.maxSize = maxSize;
	}

	@Override
	public T put(V key, T value) {
		if (size() >= maxSize) {
			Iterator<?> it = entrySet().iterator();
			it.next();
			it.remove();
		}

		return super.put(key, value);
	}

}
