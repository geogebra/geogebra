package org.geogebra.web.html5.util;

import elemental2.core.ArrayBuffer;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public class FFlate {

	@JsFunction
	public interface ZipCallback {
		void call(Object err, ArrayBuffer data);
	}

	private FFlate() {
		// use FFlate.get() instead, may return null
	}

	@JsProperty(name = "fflate")
	public static native FFlate get();

	public native String zipSync(JsPropertyMap<Object> fflatePrepared);

	public native void zip(JsPropertyMap<Object> fflatePrepared, ZipCallback callback);

	public native Object strToU8(String str);

}
