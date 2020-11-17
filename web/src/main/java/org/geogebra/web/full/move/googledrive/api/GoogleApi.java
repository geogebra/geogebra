package org.geogebra.web.full.move.googledrive.api;

import org.geogebra.web.html5.util.JsRunnable;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public class GoogleApi {

	protected GoogleApi() {
		// use GoogleApi.get() instead, may return null
	}

	public native void load(String param, JsPropertyMap<Object> properties);

	@JsProperty(name = "gapi")
	public static native GoogleApi get();

	@JsProperty(name = "client")
	public native GoogleClient getClient();

	@JsProperty(name = "auth")
	public native GoogleAuthorization getAuthorization();

	@JsProperty(name = "GGW_loadGoogleDrive")
	public static native void setOnloadCallback(JsRunnable callback);
}
