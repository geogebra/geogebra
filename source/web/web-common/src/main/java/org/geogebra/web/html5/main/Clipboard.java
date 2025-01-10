package org.geogebra.web.html5.main;

import elemental2.core.JsArray;
import elemental2.dom.Blob;
import elemental2.promise.Promise;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "navigator.clipboard")
public class Clipboard {

	public static native Promise<JsArray<ClipboardItem>> read();

	public static native Promise<String> readText();

	public static native Promise<Object> write(JsArray<ClipboardItem> items);

	public static native Promise<Object> writeText(String text);

	@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "ClipboardItem")
	public static class ClipboardItem {
		@JsProperty
		public JsArray<String> types;

		public native Promise<Blob> getType(String type);

		@SuppressWarnings("unused")
		@JsConstructor
		public ClipboardItem(JsPropertyMap<Object> flavors) {
			// native
		}
	}
}
