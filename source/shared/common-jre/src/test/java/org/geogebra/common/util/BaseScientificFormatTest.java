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

package org.geogebra.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BaseScientificFormatTest {

	DummyFormat sciFormat = new DummyFormat();

	@Test
	void testPrettyPrint() {
		assertEquals("1.0000", sciFormat.prettyPrint("1.0000E0"));
		assertEquals("10.000", sciFormat.prettyPrint("1.0000E1"));
		assertEquals("0.10000", sciFormat.prettyPrint("1.0000E-1"));
		assertEquals("0.010000", sciFormat.prettyPrint("1.0000E-2"));
		assertEquals("-1.0000", sciFormat.prettyPrint("-1.0000E0"));
		assertEquals("-10.000", sciFormat.prettyPrint("-1.0000E1"));
		assertEquals("-0.10000", sciFormat.prettyPrint("-1.0000E-1"));
		assertEquals("1.2345", sciFormat.prettyPrint("1.2345E0"));
		assertEquals("1.0000E30", sciFormat.prettyPrint("1.0000E30"));
		assertEquals("1.0000E-30", sciFormat.prettyPrint("1.0000E-30"));
		assertEquals("-1.0000E30", sciFormat.prettyPrint("-1.0000E30"));
		assertEquals("-1.0000E-30", sciFormat.prettyPrint("-1.0000E-30"));
		assertEquals("0.0000", sciFormat.prettyPrint("0.0000E0"));
	}

	private static class DummyFormat extends ScientificFormatAdapter {
		public DummyFormat() {
			super(false, 10);
			setSigDigits(5);
		}

		@Override
		public String format(double d) {
			return "";
		}
	}
}
