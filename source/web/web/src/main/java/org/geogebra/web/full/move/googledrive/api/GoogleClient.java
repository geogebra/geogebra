package org.geogebra.web.full.move.googledrive.api;

import org.geogebra.gwtutil.JsRunnable;

import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true)
public class GoogleClient {

	public native void load(String param, String version, JsRunnable callback);

	public native GoogleUploadRequest request(JsPropertyMap<Object> requestBody);
}
