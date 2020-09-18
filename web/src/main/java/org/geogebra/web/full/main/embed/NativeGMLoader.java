package org.geogebra.web.full.main.embed;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
class NativeGMLoader {

	@JsFunction
	public interface OnLoadCallback {
		void callback();
	}

	private NativeGMLoader() {
		// not instantiable
	}

	public static native void loadGM(OnLoadCallback onLoadCallback,
			JsPropertyMap<Object> settings);
}