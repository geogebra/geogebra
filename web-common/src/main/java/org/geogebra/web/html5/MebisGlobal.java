package org.geogebra.web.html5;

import org.geogebra.gwtutil.JsConsumer;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public class MebisGlobal {

	public static native void toggleFullscreen();

	public static native void nativeLogin();

	public static native void refreshToken(JsConsumer<String> run);
}
