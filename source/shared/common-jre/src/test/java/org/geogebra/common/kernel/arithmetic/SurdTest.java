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

package org.geogebra.common.kernel.arithmetic;

import org.junit.jupiter.api.Test;

class SurdTest extends SymbolicArithmeticTest {

	@Test
	void testSurd() {
		t("sqrt(8)", "2\u221a(2)");
		t("sqrt(1024 * 2 * 3)", "32\u221a(6)");
	}

	@Test
	void testOne() {
		t("sqrt(1)", "1");
	}

	@Test
	void testNegativeNumbers() {
		t("sqrt(-3)", "?");
	}

	@Test
	void testRationalNumbers() {
		t("sqrt(1.3)", "1.14");
	}

	@Test
	void testPrimes() {
		t("sqrt(17)", "4.12");
	}

	@Test
	void testLargeNumbers() {
		t("sqrt(32614907904)", "180595.98");
	}
}
