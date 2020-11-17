package org.geogebra.web.full.move.googledrive.api;

import org.geogebra.common.util.InjectJsInterop;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class GoogleDriveDocument {
	@InjectJsInterop public String id;
	@InjectJsInterop public String name;
}
