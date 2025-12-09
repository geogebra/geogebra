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

package org.geogebra.web.html5.util;

import org.geogebra.gwtutil.ExceptionUnwrapper;

import com.google.gwt.core.client.GWT;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLIFrameElement;
import jsinterop.base.Js;

public class SuperDevUncaughtExceptionHandler {
	/**
	 * Registers handler for UnhandledExceptions that are wrapped by GWT by
	 * default
	 */
	public static void register() {
		HTMLIFrameElement ifr = Js.uncheckedCast(
				DomGlobal.document.querySelector("iframe#" + GWT.getModuleName()));
		if (ifr != null) {
			ifr.contentWindow.addEventListener("error", evt -> {
				Object javaEx = Js.asPropertyMap(evt).nestedGet("error.__java$exception");
				if (javaEx instanceof Exception) {
					Throwable cause = (Exception) javaEx;
					while (cause.getCause() != null) {
						cause = cause.getCause();
					}
					ExceptionUnwrapper.printErrorMessage(cause);
					evt.preventDefault();
				}
			});
		}
	}
}
