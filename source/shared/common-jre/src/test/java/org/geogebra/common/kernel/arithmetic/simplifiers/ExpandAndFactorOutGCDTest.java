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

package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.junit.jupiter.api.Test;

public class ExpandAndFactorOutGCDTest extends BaseSimplifyTestSetup {

	@Test
	public void testSimplify() {
		shouldSimplify("(1 + sqrt(2))(1 + sqrt(3))", "1+sqrt(2)+sqrt(3)+sqrt(6)");
		shouldSimplify("(1 - sqrt(2))(1 + sqrt(3))", "1-sqrt(2)+sqrt(3)-sqrt(6)");
		shouldSimplify("(1 - sqrt(2))(1 - sqrt(3))", "1-sqrt(2)-sqrt(3)+sqrt(6)");
		shouldSimplify("(1 + sqrt(2))(1 - sqrt(3))", "1+sqrt(2)-sqrt(3)-sqrt(6)");
		shouldSimplify("(2 + sqrt(5)) (-10 + sqrt(5))", "-8sqrt(5)-15");
	}

	@Test
	public void withCompleteSquare() {
		shouldSimplify("(-8 - sqrt(10))(sqrt(4) + 7)", "-9sqrt(10) - 72");
	}

	@Test
	public void withMinusSigns() {
		shouldSimplify("((-5 + sqrt(2)) (-1 - sqrt(3))) / -2",
				"(5 - sqrt(2) + 5sqrt(3) - sqrt(6))/ - 2");
		shouldSimplify("(-8 + 2sqrt(2))(-2 - sqrt(6))", "-4 (sqrt(2) + sqrt(3) -  2sqrt(6) - 4)");
	}

	@Test
	public void withSameSqrt() {
		shouldSimplify("(-1 + sqrt(3)) * (1 - sqrt(3))", "2sqrt(3) - 4");
	}

	@Test
	void testExpandWithGeos() {
		evaluate("a = -5");
		evaluate("b = 2");
		evaluate("c = -1");
		evaluate("d = 3");
		shouldSimplify("((a + sqrt(b)) (c - sqrt(d))) / -2",
				"(5 - sqrt(2) + 5sqrt(3) - sqrt(6))/ -2");
	}

	@Override
	protected Class<? extends SimplifyNode> getSimplifierClass() {
		return ExpandAndFactorOutGCD.class;
	}
}
