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

package org.geogebra.common.kernel.interval.operators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.kernel.Kernel;
import org.junit.Test;

public class RMathTest {

	@Test
	public void testMulLo() {
		double n = RMath.mulLow(2, 3);
		assertTrue(n < 6);
		shouldEqualWithMaxPrecision(n, 6);
	}

	@Test
	public void testMulHi() {
		double n = RMath.mulHigh(2, 3);
		assertTrue(n > 6);
		shouldEqualWithMaxPrecision(n, 6);
	}

	private void shouldEqualWithMaxPrecision(double x, double y) {
		assertEquals(x, y, Kernel.MAX_PRECISION);
	}

	@Test
	public void testDivLo() {
		double n = RMath.divLow(2, 3);
		double d = 2.0 / 3.0;
		assertTrue(n < d);
		shouldEqualWithMaxPrecision(n, d);
	}

	@Test
	public void testDivHi() {
		double n = RMath.divHigh(2, 3);
		double d = 2.0 / 3.0;
		assertTrue(n > d);
		assertEquals(n, d, Kernel.MAX_PRECISION);
	}

	@Test
	public void testPowLow() {
		shouldEqualWithMaxPrecision(4 - Kernel.MAX_PRECISION, RMath.powLow(-2, 2));
	}

	@Test
	public void testPow4Low() {
		shouldEqualWithMaxPrecision(16 - Kernel.MAX_PRECISION, RMath.powLow(2, 4));
	}

	@Test
	public void testPow4High() {
		shouldEqualWithMaxPrecision(16 + Kernel.MAX_PRECISION, RMath.powHigh(2, 4));
	}

	@Test
	public void testPowHigh() {
		shouldEqualWithMaxPrecision(4 + Kernel.MAX_PRECISION, RMath.powHigh(-2, 2));
	}
}