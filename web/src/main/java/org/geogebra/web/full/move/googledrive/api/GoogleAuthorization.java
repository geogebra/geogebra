package org.geogebra.web.full.move.googledrive.api;

import org.geogebra.common.util.InjectJsInterop;
import org.geogebra.web.html5.util.JsConsumer;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true)
public class GoogleAuthorization {

	public native Response getToken();

	@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
	public static class Response {
		@InjectJsInterop public String access_token;
		@InjectJsInterop public String error;
		@InjectJsInterop public String details;
	}

	public native void authorize(JsPropertyMap<Object> config, JsConsumer<Response> callback);
}
