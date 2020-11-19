package org.geogebra.web.full.move.googledrive.api;

import org.geogebra.common.util.InjectJsInterop;
import org.geogebra.web.html5.util.JsConsumer;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class GoogleUploadRequest {

	public native Object execute(JsConsumer<FileRequestResponseType> callback);

	@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
	public static class FileRequestResponseType {
		@InjectJsInterop public String error;
		@InjectJsInterop public String id;
		@InjectJsInterop public String title;
	}
}
