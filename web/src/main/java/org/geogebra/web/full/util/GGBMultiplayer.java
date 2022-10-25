package org.geogebra.web.full.util;

import org.geogebra.common.util.InjectJsInterop;
import org.geogebra.gwtutil.JsConsumer;

import elemental2.core.JsArray;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class GGBMultiplayer {

	@JsConstructor
	@SuppressWarnings("unused")
	public GGBMultiplayer(Object api, String teamId, JsPropertyMap<?> config, String token) {
		// native constructor
	}

	public native void start(String userName);

	public native void terminate();

	public native void addUserChangeListener(JsConsumer<JsArray<Object>> userChangeListener);

	public native void disconnect();

	public native void addConnectionChangeListener(JsConsumer<ConnectionChangeEvent> callback);

	@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
	public static class ConnectionChangeEvent {
		@InjectJsInterop
		public boolean connected;
	}
}
