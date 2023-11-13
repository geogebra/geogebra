package org.geogebra.common.util;

/**
 * Converts and object T to a String representation.
 */
public interface ToStringConverter<T> {

	/**
	 * Convert the object of type T to a String representation.
	 * @param object converted object
	 * @return a String of the object.
	 */
	String convert(T object);
}
