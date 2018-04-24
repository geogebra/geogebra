package org.geogebra.common.util;

/**
 * Generic callback interface
 *
 * @param <T>
 *            callback parameter type
 */
public interface AsyncOperation<T> {
	/**
	 * @param obj
	 *            callback parameter
	 */
	public abstract void callback(T obj);

}
