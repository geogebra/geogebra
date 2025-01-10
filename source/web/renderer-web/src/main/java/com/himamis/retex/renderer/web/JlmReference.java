package com.himamis.retex.renderer.web;

import elemental2.core.Function;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public class JlmReference {
	@JsProperty
	public static JlmApi jlmlib;

	@JsProperty
	public static Function jlmOnInit;

}
