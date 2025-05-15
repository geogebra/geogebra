package org.geogebra.web.html5.util;

import elemental2.core.Uint8Array;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public final class FFlate {

	/** Callback for compression */
	@JsFunction
	public interface ZipCallback {
		/**
		 * @param err (optional) error
		 * @param data zipped file
		 */
		void call(Object err, Uint8Array data);
	}

	/** Callback for decompression */
	@JsFunction
	public interface UnzipCallback {
		/**
		 * @param err (optional) error
		 * @param data map {file name &#8594; file content}
		 */
		void call(Object err, JsPropertyMap<Uint8Array> data);
	}

	private FFlate() {
		// use FFlate.get() instead, may return null
	}

	@JsProperty(name = "fflate")
	public static native FFlate get();

	public native Uint8Array zipSync(JsPropertyMap<Object> fflatePrepared);

	public native JsPropertyMap<Uint8Array> unzipSync(Uint8Array uint8Array);

	public native void zip(JsPropertyMap<Object> fflatePrepared, ZipCallback callback);

	public native void unzip(Object bytes, UnzipCallback callback);

	public native Uint8Array strToU8(String str);

	public native String strFromU8(Uint8Array obj);

}
