package org.geogebra.gwtutil;

import elemental2.dom.DomGlobal;
import elemental2.promise.Promise;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, name = "window", namespace = JsPackage.GLOBAL)
public class FileSystemAPI {
	public static native Promise<FileSystemFileHandle> showSaveFilePicker(JsPropertyMap<?> options);

	@JsOverlay
	public static boolean isSupported() {
		return Js.asPropertyMap(DomGlobal.window).has("showSaveFilePicker");
	}
}
