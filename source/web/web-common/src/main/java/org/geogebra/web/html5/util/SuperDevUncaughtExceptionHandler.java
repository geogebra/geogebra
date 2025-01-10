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
