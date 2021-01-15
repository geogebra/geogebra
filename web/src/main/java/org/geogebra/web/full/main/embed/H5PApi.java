package org.geogebra.web.full.main.embed;

import elemental2.core.JsArray;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType
public class H5PApi {
	@JsProperty
	public JsArray<H5PApiInstance> instances;

	@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
	public static class H5PApiInstance {
		native void trigger(String resize);
	}
}
