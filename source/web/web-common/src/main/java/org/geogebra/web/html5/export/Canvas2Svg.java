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
