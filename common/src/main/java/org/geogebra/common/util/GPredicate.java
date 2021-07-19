package org.geogebra.common.util;

/**
 * Represents a predicate (boolean-valued function) of one argument.
 * 
 * This class is a workaround for missing Predicate class on Android API 23-.
 *
 * @param <T>
 *            the type of the input to the predicate
 */
public interface GPredicate<T> {
	public boolean test(T object);
}
