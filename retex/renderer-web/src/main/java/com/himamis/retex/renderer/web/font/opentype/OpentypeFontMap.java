package com.himamis.retex.renderer.web.font.opentype;

import elemental2.core.JsArray;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public class OpentypeFontMap {

	@JsProperty(name = "__JLM2_GWT_FONTS__")
	public static JsPropertyMap<JsArray<Object>> map;
}
