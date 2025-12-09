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

import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true)
public class GoogleClient {

	/**
	 * @param param parameter
	 * @param version version
	 * @param callback callback
	 */
	public native void load(String param, String version, JsRunnable callback);

	/**
	 * Request upload to Drive.
	 * @param requestBody request body
	 * @return request object
	 */
	public native GoogleUploadRequest request(JsPropertyMap<Object> requestBody);
}
