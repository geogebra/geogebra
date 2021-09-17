package org.geogebra.web.html5.util;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public class WebFont {
	@JsProperty(name = "WebFont")
	public static native WebFont get();

	public native void load(JsPropertyMap<?> toLoad);
}
