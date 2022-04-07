package org.geogebra.web.html5.util.debug;

import java.util.Map;

import javax.annotation.Nullable;

import org.geogebra.common.util.debug.Analytics;
import org.geogebra.web.html5.util.debug.firebase.Firebase;
import org.geogebra.web.html5.util.debug.firebase.FirebaseAnalytics;

import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

public class AnalyticsW extends Analytics {

	private final FirebaseAnalytics analytics;

	/**
	 * Creates an Analytics instance for the web platform.
	 */
	public AnalyticsW() {
		analytics = Firebase.get();
		if (!"function".equals(Js.typeof(Js.asPropertyMap(analytics).get("logEvent")))) {
			throw new IllegalStateException();
		}
	}

	@Override
	protected void recordEvent(String name, @Nullable Map<String, Object> params) {
		JsPropertyMap<Object> map = params != null ? convertToJsPropertyMap(params) : null;
		analytics.logEvent(name, map);
	}

	private JsPropertyMap<Object> convertToJsPropertyMap(Map<String, Object> map) {
		JsPropertyMap<Object> jsMap = JsPropertyMap.of();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			jsMap.set(entry.getKey(), entry.getValue());
		}
		return jsMap;
	}
}
