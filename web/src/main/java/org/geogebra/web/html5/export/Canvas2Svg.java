package org.geogebra.web.html5.export;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(namespace = JsPackage.GLOBAL, isNative = true, name = "C2S")
public class Canvas2Svg {

	@SuppressWarnings("unused")
	public Canvas2Svg(double width, double height) {
		// native
	}

	public native String getSerializedSvg(boolean useNumericEntities);
}
