package org.geogebra.web.richtext.impl;

import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true)
public class CarotaFormatting {

	@JsProperty
	public native void setSize(double fontSize);

}
