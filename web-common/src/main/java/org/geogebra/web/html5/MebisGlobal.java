package org.geogebra.web.html5;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public class MebisGlobal {

	public static native void toggleFullscreen();

	public static native boolean nativeLogin();
}
