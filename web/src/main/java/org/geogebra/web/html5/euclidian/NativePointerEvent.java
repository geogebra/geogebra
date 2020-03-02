package org.geogebra.web.html5.euclidian;

import com.google.gwt.dom.client.Element;

import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * Interface for pointer events, to be replaced by Elemental2
 */
@JsType
class NativePointerEvent {
	@JsProperty
	public native double getX();

	@JsProperty
	public native double getY();

	@JsProperty
	public native double getPointerId();

	@JsProperty
	public native String getPointerType();

	@JsProperty
	public native Element getTarget();

	public native void preventDefault();

	@JsProperty
	public native boolean getAltKey();

	@JsProperty
	public native boolean getCtrlKey();

	@JsProperty
	public native boolean getShiftKey();

	@JsProperty
	public native int getButton();
}
