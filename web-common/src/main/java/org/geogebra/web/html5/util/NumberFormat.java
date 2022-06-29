package org.geogebra.web.html5.util;

import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = "Intl")
public class NumberFormat {

	@SuppressWarnings("unused")
	@JsConstructor
	public NumberFormat(String s, JsPropertyMap<?> properties) {
		// native
	}

	public native String format(double x);
}
