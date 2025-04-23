package org.geogebra.common.kernel.arithmetic;

/**
 * Object that can be split into multiple objects of the same type.
 * @param <T> output type of splitting
 */
public interface Splittable<T> {

	/**
	 * Split this into parts.
	 * @return parts
	 */
	T[] split();

}
