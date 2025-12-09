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

package org.geogebra.web.html5;

import org.geogebra.gwtutil.JsRunnable;

import elemental2.core.Function;
import elemental2.core.JsArray;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class GiacNative implements JsPropertyMap<Object> {

	public JsRunnable postRun;

	/**
	 * @param name C function name
	 * @param returnType return type
	 * @param argTypes argument types
	 * @return wrapped function
	 */
	public native Function cwrap(String name, String returnType, JsArray<String> argTypes);

	/**
	 * @return cwrap function as object
	 */
	@JsProperty
	public native Function getCwrap();

}
