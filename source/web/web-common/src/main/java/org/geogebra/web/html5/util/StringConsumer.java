package org.geogebra.web.html5.util;

import jsinterop.annotations.JsFunction;

/**
 * Consumer of strings.
 */
@JsFunction
public interface StringConsumer {
	void consume(String s);
}
