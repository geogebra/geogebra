package org.geogebra.web.full.euclidian.inline.table;

import org.geogebra.common.util.InjectJsInterop;

import jsinterop.annotations.JsType;

@JsType
class Point {

	@InjectJsInterop
	public int x;
	@InjectJsInterop
	public int y;

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
}