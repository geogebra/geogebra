package org.geogebra.web.html5.export;

import elemental2.dom.CanvasPattern;
import elemental2.dom.CanvasRenderingContext2D;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType(namespace = JsPackage.GLOBAL, isNative = true, name = "window")
public class Canvas2Pdf {

	@JsProperty(name = "canvas2pdf")
	public static native Object get();

	@JsType(isNative = true, namespace = "canvas2pdf")
	public static class PdfContext extends CanvasRenderingContext2D {

		@SuppressWarnings("unused")
		@JsConstructor
		public PdfContext(double width, double height, JsPropertyMap<?> pageOptions) {
			// left blank
		}

		/**
		 * @return PDF document as base64 string
		 */
		public native String getPDFbase64();

		/**
		 * Add new page
		 */
		public native void addPage();

		public native CanvasPattern createPattern(Object context, String repetition);
	}
}
