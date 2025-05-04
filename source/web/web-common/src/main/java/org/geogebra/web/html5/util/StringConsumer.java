package org.geogebra.web.html5.util;

import jsinterop.annotations.JsFunction;

/**
 * Consumer of strings. Like {@code java.function.Consumer}, but with JsFunction annotation.
 */
@JsFunction
public interface StringConsumer {

	/**
	 * @param s string argument
	 */
	void consume(String s);
}
