/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/

package edu.uci.ics.jung.algorithms.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * An implementation of <code>Map</code> that returns the constructor-supplied
 * value for any input.
 *
 * @param <K>
 *            the key type
 * @param <V>
 *            the value type
 */
public class ConstantMap<K, V> implements Map<K, V> {

	private Map<K, V> delegate;

	/**
	 * Creates an instance whose {@code get} method always returns {@code value}
	 * .
	 */
	public ConstantMap(V value) {
		delegate = Collections.<K, V> unmodifiableMap(
				Collections.<K, V> singletonMap(null, value));
	}

	@Override
	public V get(Object key) {
		return delegate.get(null);
	}

	@Override
	public void clear() {
		delegate.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return true;
	}

	@Override
	public boolean containsValue(Object value) {
		return delegate.containsValue(value);
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return delegate.entrySet();
	}

	@Override
	public boolean equals(Object o) {
		return delegate.equals(o);
	}

	@Override
	public int hashCode() {
		return delegate.hashCode();
	}

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return delegate.keySet();
	}

	@Override
	public V put(K key, V value) {
		return delegate.put(key, value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> t) {
		delegate.putAll(t);
	}

	@Override
	public V remove(Object key) {
		return delegate.remove(key);
	}

	@Override
	public int size() {
		return delegate.size();
	}

	@Override
	public Collection<V> values() {
		return delegate.values();
	}
}