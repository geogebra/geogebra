package org.geogebra.web.html5.util.debug.firebase;

import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType
public class FirebaseAnalytics {

	public native void logEvent(String name, JsPropertyMap<Object> params);
}
