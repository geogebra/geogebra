package org.geogebra.common.kernel.arithmetic.simplifiers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

public class SurdAdditionTest extends BaseAppTest {
	@ParameterizedTest
	@CsvSource({
			"16 - 6sqrt(2), 2 (8 - 3sqrt(2))",
			"-16 + 6sqrt(2), 2 (-8 + 3sqrt(2))",
			"-16 - 6sqrt(2), -2 (8 + 3sqrt(2))",
			"2sqrt(3) + 2sqrt(4), 2 (1 + sqrt(3))",
			"2sqrt(4) + 2sqrt(3), 2 (2 + sqrt(3))",
			"2sqrt(6) + 2sqrt(3), 2 (sqrt(6) + sqrt(3))",
			"2sqrt(6) + 4sqrt(3), 2 (sqrt(6) + 2sqrt(3))",
			"5sqrt(10) + 5sqrt(2), 5 (sqrt(10) + sqrt(2))",
			"-5sqrt(10) + 5sqrt(2), 5 (-sqrt(10) + sqrt(2))",
			"5sqrt(10) - 5sqrt(2), 5 (sqrt(10) - sqrt(2))",
			"-5sqrt(10) - 5sqrt(2), -5 (sqrt(10) + sqrt(2))",
	})
	void testFactorOut(String definition, String expected) {
		SurdAddition surdAddition = newTag(definition);
		assertEquals(expected, surdAddition.factorOut().toString(StringTemplate.defaultTemplate));
	}

	private SurdAddition newTag(String definition) {
		GeoElementND tag = add(definition);
		return new SurdAddition(tag.getDefinition(), new SimplifyUtils(getKernel()));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"1 + sqrt(2)",
			"1 - sqrt(2)"
	})
	void factorOutShouldHaveNoEffect(String definition) {
		assertNull(newTag(definition).factorOut());
	}

	@ParameterizedTest
	@CsvSource({
			"-1, 3 + sqrt(2), -(3 + sqrt(2))",
			"-1, -3 - sqrt(2), 3 + sqrt(2)",
			"-1, -3 + sqrt(2), 3 - sqrt(2)",
			"3, 3 + sqrt(2), 9 + 3sqrt(2)",
			"-3, 3 + sqrt(2), -(9 + 3sqrt(2))",
			"-3, -3 + sqrt(2), 9 - 3sqrt(2)",
			"-3, -3 - sqrt(2), 9 + 3sqrt(2)",
	})
	public void testMultiply(int multiplier, String definition, String expected) {
		SurdAddition tag = newTag(definition);
		assertEquals(expected, tag.multiply(multiplier).toString(StringTemplate.defaultTemplate));
	}

	@ParameterizedTest
	@CsvSource({
			"sqrt(3), 3 + sqrt(2), 3sqrt(3) + sqrt(6)",
			"sqrt(3), sqrt(2) + 3, sqrt(6) + 3sqrt(3)",
			"2sqrt(3), sqrt(2) + 3, 2sqrt(6) + 6sqrt(3)",
	})
	public void testMultiplyWithExpression(String multiplierDef, String definition,
			String expected) {
		SurdAddition tag = newTag(definition);
		GeoElementND multiplier = add(multiplierDef);
		ExpressionNode expectedResult = add(definition).getDefinition().multiply(multiplier);
		ExpressionValue ev = tag.multiply(multiplier.getDefinition());
		assertAll(() -> assertEquals(expected, ev.toString(StringTemplate.defaultTemplate)),
				() -> assertEquals(expectedResult.evaluateDouble(), ev.evaluateDouble(),
						Kernel.STANDARD_PRECISION));

	}
}
