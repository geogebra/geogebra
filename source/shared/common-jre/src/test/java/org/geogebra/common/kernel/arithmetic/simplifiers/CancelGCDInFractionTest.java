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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class CancelGCDInFractionTest extends BaseSimplifyTestSetup {
	@Test
	public void testAccept() {
		shouldAccept("(-8 - sqrt(10)) / 54");
		shouldAccept("(9(-8 - sqrt(10))) / 54");
		shouldAccept("9(-8 - sqrt(10)) / 54");
		shouldAccept("(12 (1 + sqrt(2))) / 4");
		shouldAccept("(-(2 (1 - sqrt(2)))) / 4");
	}

	@ParameterizedTest
	@CsvSource({
			"2 / (2sqrt(3)), 1 / sqrt(3)",
			"9(-8 - sqrt(10)) / 54, ((-8 - sqrt(10))) / 6",
			"2 (-1 + sqrt(2)) / 4, (-1 + sqrt(2)) / 2",
			"(12 (1 + sqrt(2))) / 4, 3 + 3sqrt(2)",
			"(-(2 (1 - sqrt(2)))) / 4, (-1 + sqrt(2)) / 2",
			"(-9 (8 + sqrt(10))) / 54, (-8 - sqrt(10)) / 6",
			"(3 (1 - sqrt(6))) / -5, (3 (1 - sqrt(6))) / -5",
			"(4 (sqrt(5) + 1)) / 4, sqrt(5) + 1",
			"((sqrt(5) + 1) * 4) / 4, sqrt(5) + 1"})
	public void testCancelGCD(String definition, String expected) {
		shouldSimplify(definition, expected);
	}

	@Test
	public void nestedMultiplication() {
		shouldSimplify("((2 * (1 - sqrt(2)) (sqrt(2) - 5))) / 4", "((sqrt(2)-5)(1-sqrt(2)))/2");
	}

	@Test
	public void shouldNotChange() {
		shouldSimplify("(3 (1 - sqrt(6))) / -5", "(3 (1 - sqrt(6))) / -5");
		shouldSimplify("(-(sqrt(2) - 3)) / 7", "(-(sqrt(2) - 3)) / 7");
		shouldSimplify("(-4 (5 - 2sqrt(2))) / 17", "(-4 (5 - 2sqrt(2))) / 17");
	}

	@Test
	public void testSimplifyConstantFractions() {
		shouldSimplify("12 / 8", " 3 / 2");
		shouldSimplify("-1 / 3", " -1 / 3");
		shouldSimplify("1 / -3", "1 / -3");
		shouldSimplify("12 / 3", "4");
	}

	@Test
	void testNoMultiplierLeft() {
		shouldSimplify("(-4 (sqrt(2) + sqrt(3) - 2sqrt(6) - 4)) / -2",
				"2sqrt(2) + 2sqrt(3) - 4sqrt(6) - 8");
	}

	@Test
	public void wip() {
		shouldSimplify("12 / 8", " 3 / 2");
	}

	@Override
	protected Class<? extends SimplifyNode> getSimplifierClass() {
		return CancelGCDInFraction.class;
	}
}
