package org.geogebra.web.richtext.impl;

import com.google.gwt.canvas.dom.client.Context2d;

import jsinterop.annotations.JsType;

@JsType(isNative = true)
public class CarotaDocument {
	public native void draw(Context2d canvasElement);

	public native void select(int start, int end);

	public native void select(int start, int end, boolean foo);

	public native CarotaNode byCoordinate(int x, int y);
}
