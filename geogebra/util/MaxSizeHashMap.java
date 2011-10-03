package geogebra.util;

import java.util.Iterator;
import java.util.LinkedHashMap;

public class MaxSizeHashMap<V, T> extends LinkedHashMap<V,T> {
	
	private int maxSize;
	private Iterator it;
	
	public MaxSizeHashMap(int maxSize) {		
		this.maxSize = maxSize;
		it = entrySet().iterator();
	}
	
	public T put(V key, T value) {
		if (size() >= maxSize) {
			Iterator it = entrySet().iterator();
			Object removed = it.next();
			it.remove();
		}
		
		return super.put(key, value);
	}
	
}
