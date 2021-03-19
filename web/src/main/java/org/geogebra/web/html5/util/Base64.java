package org.geogebra.web.html5.util;

import elemental2.core.ArrayBuffer;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public class Base64 {

	public static native ArrayBuffer base64ToBytes(String str);

	public static native String bytesToBase64(ArrayBuffer obj);
}
