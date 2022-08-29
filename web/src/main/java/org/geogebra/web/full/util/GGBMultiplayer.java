package org.geogebra.web.full.util;

import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class GGBMultiplayer {

	@JsConstructor
	@SuppressWarnings("unused")
	public GGBMultiplayer(Object api, JsPropertyMap<?> config) {
		// native constructor
	}

	public native void start(String sharingKey, String userName);
}
