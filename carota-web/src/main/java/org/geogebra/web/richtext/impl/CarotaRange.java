package org.geogebra.web.richtext.impl;

import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true)
public class CarotaRange {

	public native String plainText();

	@JsProperty
	public native int getStart();

	@JsProperty
	public native int getEnd();

	public native void setFormatting(String key, Object val);

	public native String getListStyle();

	public native CarotaFormatting getFormatting();

	public native <T> T getFormattingValue(String key, T fallback);
}
