package org.geogebra.gwtutil;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public class SafeExamBrowser {

	public SebSecurity security;

	@JsProperty(name = "SafeExamBrowser")
	public static native SafeExamBrowser get();

	@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
	public static class SebSecurity {
		public String configKey;

		public native void updateKeys(JsConsumer<Void> callback);
	}
}
