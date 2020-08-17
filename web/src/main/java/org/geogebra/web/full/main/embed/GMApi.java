package org.geogebra.web.full.main.embed;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public final class GMApi {

	private GMApi() {
		// use GraspableMathApi::get, might be null
	}

	@JsProperty(name = "gmath")
	public static native GMApi get();
}
