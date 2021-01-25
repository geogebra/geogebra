package org.geogebra.web.richtext.impl;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "carota")
public class CarotaRuns {

	@JsProperty
	public native CarotaFormatting getDefaultFormatting();
}
