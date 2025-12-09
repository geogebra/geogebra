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

package org.geogebra.gwtutil;

import elemental2.dom.DomGlobal;
import jsinterop.base.Js;

public class ExceptionUnwrapper {

	/**
	 * @param thrown exception
	 */
	public static void printErrorMessage(Object thrown) {
		// This contains the stacktrace in gwt dev mode.
		Object backingJsObject = Js.asPropertyMap(thrown).nestedGet("backingJsObject.stack");
		if (Js.asPropertyMap(DomGlobal.console).has("error")) {
			if (Js.isTruthy(backingJsObject)) {
				DomGlobal.console.error(backingJsObject);
			} else {
				DomGlobal.console.error(thrown);
			}
		}
	}
}
