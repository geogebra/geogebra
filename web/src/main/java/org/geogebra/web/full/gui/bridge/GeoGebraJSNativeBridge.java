package org.geogebra.web.full.gui.bridge;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public class GeoGebraJSNativeBridge {
	private GeoGebraJSNativeBridge() {
	}

	@JsProperty(name = "GeoGebraJSNativeBridge")
	public static native GeoGebraJSNativeBridge get();

	@JsProperty
	public native void share(String base64, String name, String fileType);

	@JsProperty
	public native void savePreference(String key, String value);

	@JsProperty
	public native void login();

	@JsProperty
	public native void openFromFileClickedNative();

	@JsProperty
	public native void callPlugin(String action, Object[] argument);
}
