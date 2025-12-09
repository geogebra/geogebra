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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ScientificFormatTest {

	@Test
	public void testPrettyPrint() {
		ScientificFormat sf = new ScientificFormat(5, 10, false);
		assertEquals("1.0000", sf.prettyPrint("1.0000e+0"));
		assertEquals("10.000", sf.prettyPrint("1.0000e+1"));
		assertEquals(".10000", sf.prettyPrint("1.0000e-1"));
		assertEquals("-1.0000", sf.prettyPrint("-1.0000e+0"));
		assertEquals("-10.000", sf.prettyPrint("-1.0000e+1"));
		assertEquals("-.10000", sf.prettyPrint("-1.0000e-1"));
		assertEquals("1.2345", sf.prettyPrint("1.2345e+0"));
		assertEquals("1.0000E30", sf.prettyPrint("1.0000e+30"));
		assertEquals("1.0000E-30", sf.prettyPrint("1.0000e-30"));
		assertEquals("-1.0000E30", sf.prettyPrint("-1.0000e+30"));
		assertEquals("-1.0000E-30", sf.prettyPrint("-1.0000e-30"));
		assertEquals("0.0000", sf.prettyPrint("0.0000e+0"));
	}
}
