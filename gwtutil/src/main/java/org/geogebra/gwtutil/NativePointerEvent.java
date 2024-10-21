package org.geogebra.gwtutil;

import elemental2.dom.Element;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * Interface for pointer events, to be replaced by Elemental2
 */
@JsType
public class NativePointerEvent {
	@JsProperty
	public native double getClientX();

	@JsProperty
	public native double getClientY();

	@JsProperty
	public native double getOffsetX();

	@JsProperty
	public native double getOffsetY();

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

	@JsProperty
	public native boolean getMetaKey();
}
