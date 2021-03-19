package org.geogebra.web.html5.util;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public class Base64 {

	public static native Object base64ToBytes(String str);

	public static native String bytesToBase64(Object obj);
}
