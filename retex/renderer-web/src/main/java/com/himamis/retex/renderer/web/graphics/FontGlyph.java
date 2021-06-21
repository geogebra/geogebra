package com.himamis.retex.renderer.web.graphics;

import elemental2.dom.BaseRenderingContext2D;
import elemental2.dom.CanvasRenderingContext2D;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class FontGlyph {
	public BaseRenderingContext2D.FillStyleUnionType fill;
	public Object unitsPerEm;
	public double size;
}
