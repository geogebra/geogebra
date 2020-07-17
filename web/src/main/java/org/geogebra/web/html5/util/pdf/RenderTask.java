package org.geogebra.web.html5.util.pdf;

import elemental2.promise.Promise;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class RenderTask {
	@JsProperty
	public Promise<Void> promise;
}
