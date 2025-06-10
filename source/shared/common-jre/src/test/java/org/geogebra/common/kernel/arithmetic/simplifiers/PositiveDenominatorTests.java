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
