package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class CheckIfTrivialTest extends BaseSimplifyTest {

	@Override
	protected Class<? extends SimplifyNode> getSimplifierClass() {
		return CheckIfTrivial.class;
	}

	@ParameterizedTest
	@CsvSource({
			"sqrt(4)sqrt(2), 2sqrt(2)",
			"(-(-2) + sqrt(5)) (-10 + sqrt(5)), (2 + sqrt(5)) (-10 + sqrt(5))",
			"((-2 - sqrt(8)) (-6)) / -4, ((-2 - sqrt(8)) (-6)) / -4"
			})
	public void testReduceToIntegers(String definition, String expected) {
		shouldSimplify(definition, expected);
	}

	@ParameterizedTest
	@CsvSource({
		"(0 + sqrt(3) ) ( 4 - sqrt(5) ) / 11, sqrt(3) (4 - sqrt(5)) / 11",
	    "(2 + sqrt(0) ) ( 4 - sqrt(5) ) / 11, 2 (4 - sqrt(5)) / 11",
	    "(2 + sqrt(3) ) ( 4 - sqrt(0) ) / 11, (2 + sqrt(3))(4) / 11",
	    "0/sqrt(2), 0",
	    "sqrt(2)/0, \u221e",
	    "0/0, -\u221e",
	})
	public void testIgnoreZeros(String definition, String expected) {
		shouldSimplify(definition, expected);

	}

	@ParameterizedTest
	@CsvSource({
		"(-(2sqrt(2)) - 2) / 4, (-(2sqrt(2)) - 2) / 4",
		"(-1 + sqrt(2)) sqrt(5), (-1 + sqrt(2)) sqrt(5)",

	})
	public void testShouldNotChange(String definition, String expected) {
		shouldSimplify(definition, expected);
	}
}
