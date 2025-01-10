package org.geogebra.web.html5.export;

import org.geogebra.common.util.InjectJsInterop;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public class Gifshot {

	@JsFunction
	public interface GifshotCallback {
		void consume(GifshotResult result);
	}

	public native void createGIF(JsPropertyMap<Object> settings, GifshotCallback callback);

	@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
	public static class GifshotResult {
		@InjectJsInterop public String image;
		@InjectJsInterop public Object error;
	}
}
