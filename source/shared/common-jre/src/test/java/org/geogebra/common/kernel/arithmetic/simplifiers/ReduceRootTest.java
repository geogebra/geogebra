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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ReduceRootTest extends BaseSimplifyTestSetup {

	@ParameterizedTest
	@CsvSource({
			"sqrt(16), 4",
			"(-8 + sqrt(4)) / (-2 + sqrt(8)), (-8 + 2) / (-2 + 2sqrt(2))",
	})
	public void testRootsOfSquaresShouldReduceToInteger(String definition, String expected) {
		shouldSimplify(definition, expected);
	}

	@ParameterizedTest
	@CsvSource({
			"sqrt(3 + 4), sqrt(7)",
			"sqrt(4*5 + 6), sqrt(26)",
			"sqrt(4*5 + 6 - 3), sqrt(23)",
	})
	public void testRadicandShouldBeReduced(String definition, String expected) {
		shouldSimplify(definition, expected);
	}

	@ParameterizedTest
	@CsvSource({
			"sqrt(18), 3sqrt(2)",
			"2sqrt(18), 6sqrt(2)",
			"sqrt(40 + 4*8), 6 sqrt(2)",
			"sqrt(72), 6 sqrt(2)",
			"2 * sqrt(3 + 4), 2 * sqrt(7)",
			"14 + 2 * sqrt(3 + 4), 14 + 2 * sqrt(7)",
			"(-8 + sqrt(4)) / (-2 + sqrt(8)), (-8 + 2) / (-2 + 2sqrt(2))",
	})
	public void testSurds(String definition, String expected) {
		shouldSimplify(definition, expected);
	}

	@Override
	protected Class<? extends SimplifyNode> getSimplifierClass() {
		return ReduceRoot.class;
	}
}
