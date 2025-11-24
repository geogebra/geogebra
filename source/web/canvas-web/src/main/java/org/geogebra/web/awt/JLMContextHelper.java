package org.geogebra.web.awt;

import jsinterop.base.Js;

public class JLMContextHelper {

	/**
	 * Gets 2D context
	 *
	 * @param context
	 *            context
	 * @return context
	 */
	public static JLMContext2D as(Object context) {
		return Js.uncheckedCast(context);
	}

}
