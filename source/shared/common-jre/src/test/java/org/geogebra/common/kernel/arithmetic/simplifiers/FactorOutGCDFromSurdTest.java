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

public class FactorOutGCDFromSurdTest extends BaseSimplifyTestSetup {

	@Test
	public void testFactorAdditionOfPositives() {
		shouldSimplify("2 + 2sqrt(2)", "2 (1 + sqrt(2))");
		shouldSimplify("2sqrt(2) + 2", "2 (1 + sqrt(2))");

		shouldSimplify("2 + 6sqrt(2)", "2 (1 + 3sqrt(2))");
		shouldSimplify("6sqrt(2) + 2", "2 (1 + 3sqrt(2))");
	}

	@Test
	public void testFactorSubtractionOfPositives() {
		shouldSimplify("2 - 2sqrt(2)", "2 (1 - sqrt(2))");

		shouldSimplify("12 - 4sqrt(2)", "4 (3 - sqrt(2))");
		shouldSimplify("4 - 12sqrt(2)", "4 (1 - 3sqrt(2))");
		shouldSimplify("20 - 6sqrt(2)", "2 (10 - 3sqrt(2))");
	}

	@Test
	public void testFactorSubtractionOfNegatives() {
		shouldSimplify("(-2 - 2sqrt(2))", "-2 (1 + sqrt(2))");;
		shouldSimplify("-10 - 2sqrt(2)", "-2 (5 + sqrt(2))");
	}

	@Test
	public void testFactorOutNominator() {
		shouldSimplify("(2 + 2sqrt(2)) / -4", "-((2 + 2sqrt(2))/4)",
				getSimplifier(), new PositiveDenominator(utils));
		shouldSimplify("(-2 - 2sqrt(2)) / 4", "(-2 (1 + sqrt(2))) / 4");
		shouldSimplify("(2 - 2sqrt(2)) / 4", "(2 (1 - sqrt(2))) / 4");
	}

	@Test
	public void testFactorOutMultipliedExpressions() {
		shouldSimplify("3 (2 + 2sqrt(2))", "6 (1 + sqrt(2))");
		shouldSimplify("3 (2 - 10sqrt(2))", "6 (1 - 5sqrt(2))");
		shouldSimplify("3 (-2 - 10sqrt(2))", "-6 (1 + 5sqrt(2))");
	}

	@Test
	public void testMinusTimesMinusShouldFlipOperand() {
		shouldSimplify("-6 (-2 - 2sqrt(2))", "12 (1 + sqrt(2))");
	}

	@Test
	void testFactorOutFromExpandedForm() {
		shouldSimplify("(-10 (sqrt(2) + 2) - 4sqrt(3) - 4sqrt(6))",
				"-10 (sqrt(2) + 2) - 4sqrt(3) - 4sqrt(6)");
	}

	@Test
	void testShouldBeTheSame() {
		shouldSimplify("(-5 - 2sqrt(3) + 5sqrt(6) + sqrt(2))",
				"(-5 - 2sqrt(3) + 5sqrt(6) + sqrt(2))");
	}

	@Test
	void wip() {
		shouldSimplify("(2sqrt(6) + 2sqrt(3))", "2 (sqrt(6) + sqrt(3))");
	}

	@Override
	protected Class<? extends SimplifyNode> getSimplifierClass() {
		return FactorOutGCDFromSurd.class;
	}
}
