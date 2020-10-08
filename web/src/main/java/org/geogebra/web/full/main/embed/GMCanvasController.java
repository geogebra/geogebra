package org.geogebra.web.full.main.embed;

import org.geogebra.web.html5.util.JsRunnable;

import jsinterop.annotations.JsType;

@JsType(isNative = true)
public class GMCanvasController {

	public native void on(String event, JsRunnable callback);

	public native void undo();

	public native void redo();
}
