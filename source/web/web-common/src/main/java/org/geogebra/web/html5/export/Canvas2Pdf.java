/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.html5.export;

import org.geogebra.gwtutil.JsRunnable;

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
	public static native Canvas2Pdf get();

	public native void runAfterFontsLoaded(JsRunnable callback);

	public native void setFontPath(String path);

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

		public native void removePage();
	}
}
