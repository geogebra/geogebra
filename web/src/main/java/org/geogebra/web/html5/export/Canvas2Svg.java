package org.geogebra.web.html5.export;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(namespace = JsPackage.GLOBAL, name = "C2S")
public class Canvas2Svg {
	public static native Canvas2Svg get(double width,
			double height) /*-{
		return new $wnd.C2S(width, height);
	}-*/;

	public native String getSerializedSvg(boolean foo);
}
