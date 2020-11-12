package org.geogebra.web.full.move.googledrive.api;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true)
public class GoogleAuthorization {

	public native Response getToken();

	@JsType(isNative = true)
	public static class Response {
		public String access_token;
		public String error;
		public String details;
	}

	@JsFunction
	public interface AuthorizeCallbackFunction {
		void callback(Response response);
	}

	public native void authorize(JsPropertyMap<Object> config, AuthorizeCallbackFunction callback);
}
