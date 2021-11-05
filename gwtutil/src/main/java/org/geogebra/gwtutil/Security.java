package org.geogebra.gwtutil;

import jsinterop.annotations.JsType;

@JsType(isNative = true)
public class Security {

	public native void isEnvironmentSecure(JsConsumer<String> state);

	public native void lockDown(boolean enable,
			JsConsumer<Boolean> onSuccess, JsConsumer<Boolean> onFailure);
}
