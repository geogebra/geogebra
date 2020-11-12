package org.geogebra.web.full.move.googledrive.api;

import org.geogebra.web.html5.util.JsConsumer;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class GoogleUploadRequest {

	public native Object execute(JsConsumer<FileRequestResponseType> callback);

	@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
	public static class FileRequestResponseType {
		public String error;
		public String id;
		public String title;
	}
}
