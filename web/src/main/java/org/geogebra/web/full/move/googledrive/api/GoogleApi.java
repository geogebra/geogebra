package org.geogebra.web.full.move.googledrive.api;

import org.geogebra.web.html5.util.JsRunnable;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public class GoogleApi {

	public static JsRunnable GGW_loadGoogleDrive;

	protected GoogleApi() {
		// use GoogleApi.get() instead, may return null
	}

	@JsProperty(name = "gapi")
	public static native GoogleApi get();

	public native void load(String param, JsPropertyMap<Object> properties);

	@JsProperty(name = "client")
	public native GoogleClient getClient();

	@JsProperty(name = "auth")
	public native GoogleAuthorization getAuthorization();
}
