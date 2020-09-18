package org.geogebra.web.full.main.embed;

import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = "gmath", name = "Canvas")
public class GMCanvas {

	@JsProperty
	public GMCanvasController controller;

	@SuppressWarnings("unused")
	public GMCanvas(String elementSelector, JsPropertyMap<Object> settings) {
		// native
	}

	public native String toJSON();

	public native void loadFromJSON(String content);
}
