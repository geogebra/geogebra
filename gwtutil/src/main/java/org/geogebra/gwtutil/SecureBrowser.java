package org.geogebra.gwtutil;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public final class SecureBrowser {

	public Security security;

	private SecureBrowser() {
		// use SecureBrowser.get() instead, may return null
	}

	@JsProperty(name = "SecureBrowser")
	public static native SecureBrowser get();
}
