package org.geogebra.web.richtext.impl;

import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = "carota")
public class CarotaText {

	@JsProperty
	public native CarotaCache getCache();

	@JsProperty
	public native void setSelectionColor(String color);
}
