package org.geogebra.test;

import com.himamis.retex.editor.share.util.Unicode;

public class TestStringUtil {
	/**
	 * @param ascii
	 *            ascii math
	 * @return unicode math (superscript powers)
	 */
	public static String unicode(String ascii) {
		return ascii.replace("^2", Unicode.SUPERSCRIPT_2 + "")
				.replace("^3", Unicode.SUPERSCRIPT_3 + "")
				.replace("^4", Unicode.SUPERSCRIPT_4 + "")
				.replace("^5", Unicode.SUPERSCRIPT_5 + "")
				.replace("^6", Unicode.SUPERSCRIPT_6 + "")
				.replace("^-1",
						Unicode.SUPERSCRIPT_MINUS + "" + Unicode.SUPERSCRIPT_1)
				.replace("deg", Unicode.DEGREE_STRING)
				.replace("@pi", Unicode.PI_STRING)
				.replace("@inf", Unicode.INFINITY + "")
				.replace("@theta", Unicode.theta_STRING);
	}
}