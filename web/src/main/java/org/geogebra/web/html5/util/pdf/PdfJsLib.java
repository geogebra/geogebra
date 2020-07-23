package org.geogebra.web.html5.util.pdf;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
final public class PdfJsLib {

	private PdfJsLib() {
		// use PdfJsLib.get() instead, may return null
	}

	@JsProperty(name = "pdfjsLib")
	public static native PdfJsLib get();

	public native PdfDocumentLoadingTask getDocument(String src);
}
