package org.geogebra.web.html5.main;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public class Clipboard {
	public native static void copyGraphicsToClipboard(String imageData);

	public native static boolean isCopyImageToClipboardAvailable();
}
