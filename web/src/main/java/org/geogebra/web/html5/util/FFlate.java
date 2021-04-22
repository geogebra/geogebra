package org.geogebra.web.html5.util;

import elemental2.core.Uint8Array;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public class FFlate {

	@JsFunction
	public interface ZipCallback {
		void call(Object err, Uint8Array data);
	}

	@JsFunction
	public interface UnzipCallback {
		void call(Object err, JsPropertyMap<Uint8Array> data);
	}

	private FFlate() {
		// use FFlate.get() instead, may return null
	}

	@JsProperty(name = "fflate")
	public static native FFlate get();

	public native Uint8Array zipSync(JsPropertyMap<Object> fflatePrepared);

	public native void zip(JsPropertyMap<Object> fflatePrepared, ZipCallback callback);

	public native void unzip(Object bytes, UnzipCallback callback);

	public native Uint8Array strToU8(String str);

	public native String strFromU8(Uint8Array obj);

}
