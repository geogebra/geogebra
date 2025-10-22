package org.geogebra.gwtutil;

import jsinterop.annotations.JsFunction;

/**
 * JavaScript function taking one argument of given type
 * @param <T> argument type
 */
@JsFunction
@FunctionalInterface
public interface JsConsumer<T> {
	/**
	 * Call the function.
	 * @param t argument
	 */
	void accept(T t);
}

