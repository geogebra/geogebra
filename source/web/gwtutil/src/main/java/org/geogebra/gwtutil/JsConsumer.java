package org.geogebra.gwtutil;

import jsinterop.annotations.JsFunction;

@JsFunction
public interface JsConsumer<T> {
	void accept(T t);
}

