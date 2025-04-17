package org.geogebra.web.full.move.googledrive.api;

import org.geogebra.gwtutil.JsRunnable;

import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true)
public class GoogleClient {

	/**
	 * @param param parameter
	 * @param version version
	 * @param callback callback
	 */
	public native void load(String param, String version, JsRunnable callback);

	/**
	 * Request upload to Drive.
	 * @param requestBody request body
	 * @return request object
	 */
	public native GoogleUploadRequest request(JsPropertyMap<Object> requestBody);
}
