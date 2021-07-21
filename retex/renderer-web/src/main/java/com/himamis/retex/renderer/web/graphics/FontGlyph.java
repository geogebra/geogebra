package com.himamis.retex.renderer.web.graphics;

import elemental2.dom.BaseRenderingContext2D;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class FontGlyph {
	public BaseRenderingContext2D.FillStyleUnionType fill;
	public double unitsPerEm;
	public double size;

	@JsOverlay
	public final Object getAt(int i) {
		return Js.asArrayLike(this).getAt(i);
	}
}
