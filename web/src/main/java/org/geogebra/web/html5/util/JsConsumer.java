package org.geogebra.web.html5.util;

import jsinterop.annotations.JsFunction;

@JsFunction
public interface JsConsumer<T> {
	void accept(T t);
}
