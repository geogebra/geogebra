package org.geogebra.web.html5.util.debug;

import java.util.Map;

import javax.annotation.Nullable;

import org.geogebra.common.util.debug.Analytics;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.util.debug.firebase.Firebase;
import org.geogebra.web.html5.util.debug.firebase.FirebaseAnalytics;

import jsinterop.base.JsPropertyMap;

public class AnalyticsW extends Analytics {

	private FirebaseAnalytics analytics;

	public AnalyticsW() {
		try {
			Firebase firebase = Firebase.get();
			analytics = firebase.analytics();
		} catch (Throwable exception) {
			Log.debug("Firebase Analytics is not available.");
		}
	}

	@Override
	protected void recordEvent(String name, @Nullable Map<String, Object> params) {
		if (analytics != null) {
			JsPropertyMap<Object> map = params != null ? convertToJsPropertyMap(params) : null;
			analytics.logEvent(name, map);
		} else {
			Log.debug("Firebase Analytics is not available, event with name '" + name
					+ "' is ignored.");
		}
	}

	private JsPropertyMap<Object> convertToJsPropertyMap(Map<String, Object> map) {
		JsPropertyMap<Object> jsMap = JsPropertyMap.of();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			jsMap.set(entry.getKey(), entry.getValue());
		}
		return jsMap;
	}
}
