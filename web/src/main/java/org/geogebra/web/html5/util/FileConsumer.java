package org.geogebra.web.html5.util;

import jsinterop.annotations.JsFunction;

@JsFunction
public interface FileConsumer {
	void consume(Object file);
}
