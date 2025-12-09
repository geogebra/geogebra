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

package org.geogebra.web.full.move.googledrive.api;

import org.geogebra.gwtutil.JsRunnable;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public class GoogleApi {

	protected GoogleApi() {
		// use GoogleApi.get() instead, may return null
	}

	public native void load(String param, JsPropertyMap<Object> properties);

	@JsProperty(name = "gapi")
	public static native GoogleApi get();

	@JsProperty(name = "client")
	public native GoogleClient getClient();

	@JsProperty(name = "auth")
	public native GoogleAuthorization getAuthorization();

	@JsProperty(name = "GGW_loadGoogleDrive")
	public static native void setOnloadCallback(JsRunnable callback);
}
