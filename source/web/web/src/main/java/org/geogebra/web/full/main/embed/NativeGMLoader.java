package org.geogebra.web.full.main.embed;

import org.geogebra.gwtutil.JsRunnable;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
final class NativeGMLoader {

	private NativeGMLoader() {
		// not instantiable
	}

	public static native void loadGM(JsRunnable onLoadCallback,
			JsPropertyMap<Object> settings);
}