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

import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.Test;

@Issue("APPS-8189")
class MyMath2Test {

	private static final double DELTA = 1E-7;

	@Test
	void psiOfNegativeNumbers() {
		assertEquals(2 - 0.5772156649015329 - 2 * Math.log(2), MyMath2.psi(-0.5), DELTA);
		assertEquals(Double.NaN, MyMath2.psi(-1));
		assertEquals(Double.NaN, MyMath2.psi(-1E300));
		assertEquals(Double.NaN, MyMath2.psi(Double.NEGATIVE_INFINITY));
		assertEquals(1E12, MyMath2.psi(-1E-12), 1E12 * DELTA);
		assertEquals(1E20, MyMath2.psi(-1E-20), 1E20 * DELTA);
		assertEquals(Math.log(0x1p51), MyMath2.psi(-0x1p51 - 0.5), DELTA);
	}

	@Test
	void trigammaOfNegativeNumbers() {
		assertEquals(Math.PI * Math.PI / 2 + 4, MyMath2.polyGamma(new MyDouble(null, 1), -0.5), DELTA);
		assertEquals(Math.PI * Math.PI, MyMath2.polyGamma(new MyDouble(null, 1), -0x1p51 - 0.5), DELTA);
		assertEquals(Double.NaN, MyMath2.polyGamma(new MyDouble(null, 1), -1E300));
		assertEquals(1E24, MyMath2.polyGamma(new MyDouble(null, 1), -1E-12), 1E24 * DELTA);
	}
}
