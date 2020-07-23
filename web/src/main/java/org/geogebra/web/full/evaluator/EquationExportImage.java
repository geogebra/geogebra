package org.geogebra.web.full.evaluator;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class EquationExportImage {
	@JsProperty
	public native void setSvg(String svg);

	@JsProperty
	public native void setBaseline(double baseline);

	@JsProperty
	public native void setError(String message);
}
