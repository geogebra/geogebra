package org.geogebra.common.jre.util;

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
				.replace("^-1",
						Unicode.SUPERSCRIPT_MINUS + "" + Unicode.SUPERSCRIPT_1)
				.replace("deg", Unicode.DEGREE_STRING);
	}
}