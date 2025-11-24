package org.geogebra.web.awt;

import elemental2.dom.DomGlobal;

public class Log {

	/**
	 * Print message to the console.
	 * @param message message to print
	 */
	public static void debug(String message) {
		DomGlobal.console.log(message);
	}
}
