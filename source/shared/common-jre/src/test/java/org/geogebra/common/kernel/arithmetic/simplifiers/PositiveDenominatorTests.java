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

public class PositiveDenominatorTests extends BaseSimplifyTestSetup {

	@ParameterizedTest
	@CsvSource({
		"(2 (sqrt(2) - 1)) / -5, -((2sqrt(2) - 2) / 5)",
		"(3+sqrt(2)) / -5,-((3+sqrt(2)) / 5)",
		"-(3+sqrt(2)) / -5,(3+sqrt(2)) / 5",
		"-7 (3+sqrt(2)) / -5, (-(21 + 7sqrt(2)) / 5)",
		"7 (3+sqrt(2)) / -5, (21 + 7sqrt(2)) / 5",
		"7 (-3-sqrt(2)) / -5, (-(21 - 7sqrt(2))) / 5",
		"7 (-3-sqrt(2)) / 5, (-(21 - 7sqrt(2))) / 5 ",
		"(3 (-3 - sqrt(2)) / 7), (-(9 - 3sqrt(2))) / 7"
	})
	public void testApply(String definition, String expected) {
		shouldSimplify(definition, expected);
	}

	@Override
	protected Class<? extends SimplifyNode> getSimplifierClass() {
		return PositiveDenominator.class;
	}
}
