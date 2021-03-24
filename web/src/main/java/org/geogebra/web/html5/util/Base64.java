package org.geogebra.web.html5.util;

import elemental2.core.Uint8Array;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "base64Util")
public class Base64 {

	public static native Uint8Array base64ToBytes(String str);

	public static native String bytesToBase64(Uint8Array obj);
}
