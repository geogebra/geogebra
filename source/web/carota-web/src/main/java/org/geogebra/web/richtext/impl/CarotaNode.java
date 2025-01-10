package org.geogebra.web.richtext.impl;

import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true)
public class CarotaNode {
	@JsProperty
	public native int getOrdinal();
}
