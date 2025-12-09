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

import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.Element;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(namespace = JsPackage.GLOBAL, isNative = true, name = "canvasToSvg")
public class Canvas2Svg {

	@SuppressWarnings("unused")
	public Canvas2Svg(double width, double height) {
		// native
	}

	public native String getSerializedSvg(boolean useNumericEntities);

	public native SVGPattern createPattern(CanvasRenderingContext2D context,
			String repetition);

	@JsProperty
	public native void setFillStyle(SVGPattern pattern);

	@JsType(namespace = JsPackage.GLOBAL, isNative = true, name = "Object")
	public static class SVGPattern {
		@JsProperty(name = "__root")
		public native Element getRoot();
	}
}
