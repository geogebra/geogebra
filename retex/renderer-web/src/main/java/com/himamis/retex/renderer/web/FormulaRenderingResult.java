package com.himamis.retex.renderer.web;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
public class FormulaRenderingResult {
	public int width;
	public int height;
	public double baseline;
	public double pixelRatio;
}
