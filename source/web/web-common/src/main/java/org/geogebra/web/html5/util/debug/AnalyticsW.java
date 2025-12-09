/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.html5.util.debug;

import java.util.Map;

import javax.annotation.CheckForNull;

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
	protected void recordEvent(String name, @CheckForNull Map<String, Object> params) {
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