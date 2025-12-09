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
