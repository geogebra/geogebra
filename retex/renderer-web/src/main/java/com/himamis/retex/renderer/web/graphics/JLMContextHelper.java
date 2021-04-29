package com.himamis.retex.renderer.web.graphics;

import jsinterop.base.Js;

public class JLMContextHelper {


	/**
	 * Gets 2D context
	 *
	 * @param canvas
	 *            canvas
	 * @return context
	 */
	public static JLMContext2d as(Object context) {
		return Js.uncheckedCast(context);
	}

}
