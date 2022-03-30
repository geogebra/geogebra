package org.geogebra.web.html5.util.debug.firebase;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public class Firebase {

	protected Firebase() {
	}

	@JsProperty(name = "firebase")
	public static native FirebaseAnalytics get();
}
