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

package org.geogebra.test;

import org.geogebra.editor.share.util.Unicode;

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