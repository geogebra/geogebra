package org.geogebra.gwtutil;

import jsinterop.annotations.JsType;

@JsType(isNative = true)
public class Security {

	/**
	 * @param state whether environment is deemed secure.
	 */
	public native void isEnvironmentSecure(JsConsumer<String> state);

	/**
	 * @param enable true to lock down
	 * @param onSuccess success callback
	 * @param onFailure failure callback
	 */
	public native void lockDown(boolean enable,
			JsConsumer<Boolean> onSuccess, JsConsumer<Boolean> onFailure);
}
