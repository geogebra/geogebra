package org.geogebra.web.full.main.embed;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsType;

@JsType(isNative = true)
public class GMCanvasController {

	@JsFunction
	public interface OnEventCallback {
		void callback();
	}

	public native void on(String event, OnEventCallback callback);

	public native void undo();

	public native void redo();
}
