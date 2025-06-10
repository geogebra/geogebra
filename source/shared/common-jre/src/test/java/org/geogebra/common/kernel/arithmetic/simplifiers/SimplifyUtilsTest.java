package org.geogebra.common.kernel.arithmetic.simplifiers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MinusOne;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.Operation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

public class SimplifyUtilsTest extends BaseSimplifyTestSetup {

	private SimplifyUtils utils;

	@BeforeEach
	public void setUp() {
		utils = new SimplifyUtils(getKernel());
	}

	@Override
	protected Class<? extends SimplifyNode> getSimplifierClass() {
		return null;
	}

	@ParameterizedTest
	@CsvSource({
			"2 (1 + sqrt(4)), 6",
			"(1 + sqrt(4) - 1) 2, 4",
			"2 (1 + sqrt(2)), 2 + 2sqrt(2)",
			"(1 + sqrt(2)) 2, 2 + 2sqrt(2)"
	})
	public void testExpand(String from, String to) {
		GeoNumeric original = newSymbolicNumeric(from);
		GeoNumeric expected = newSymbolicNumeric(to);
		ExpressionNode actual = utils.expand(original.getDefinition());
		shouldSerialize(expected.getDefinition(), actual);
	}

	@Test
	public void testMultiplyByMinusOne() {
		mulShouldBe("-sqrt(2)", "sqrt(2)");

	}

	private void mulShouldBe(String from, String to) {
		GeoNumeric original = newSymbolicNumeric(from);
		GeoNumeric expected = newSymbolicNumeric(to);
		ExpressionValue actual = utils.mulByMinusOne(original.getDefinition());
		shouldSerialize(expected.getDefinition(), actual.wrap());
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"sqrt(0.0001+10)",
			"sqrt(1 / 4)",
			"sqrt(2.5)"
	})
	public void sqrtShouldNotBeValid(String def) {
		GeoNumeric original = newSymbolicNumeric(def);
		Assertions.assertFalse(
				ExpressionValueUtils.isSqrtValid(original.getDefinition()),
				def + " is square root of a positive integer.");
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"sqrt(1+2+3+4)",
			"sqrt(4*0.25)"
	})
	public void sqrtShouldBeValid(String def) {
		GeoNumeric original = newSymbolicNumeric(def);
		Assertions.assertTrue(
				ExpressionValueUtils.isSqrtValid(original.getDefinition()),
				def + " is not square root of a positive integer.");
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"1",
			"sqrt(2)",
			"sqrt(2) + 1",
			"1 + sqrt(2)",
			"sqrt(2) - 1",
			"1 - sqrt(2)"
	})
	public void testNodeSupported(String definition) {
		GeoNumeric numeric = newSymbolicNumeric(definition);
		Assertions.assertTrue(ExpressionValueUtils.isNodeSupported(numeric.getDefinition()),
				definition + " is not supported");
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"sqrt(-2)",
			"sqrt(-2) + 1",
			"1 + sqrt(-2)",
			"sqrt(-2) - 1",
			"1 - sqrt(-2)"
	})
	public void testNodeNotSupportNode(String def) {
		GeoNumeric numeric = newSymbolicNumeric(def);
		Assertions.assertFalse(ExpressionValueUtils.isNodeSupported(numeric.getDefinition()),
				def + " should not be supported");
	}

	@Test
	public void testNegative() {
		negativeShouldBe("-3 - sqrt(2)", "-(3 + sqrt(2))");
		negativeShouldBe("-sqrt(2) - 3", "-(sqrt(2) + 3)");
	}

	@Test
	public void fixMe() {
		mulShouldBe("1 + sqrt(2)", "-1 - sqrt(2)");

	}

	private void negativeShouldBe(String from, String to) {
		GeoNumeric original = newSymbolicNumeric(from);
		GeoNumeric expected = newSymbolicNumeric(to);
		ExpressionNode node = original.getDefinition();
		ExpressionValue actual = utils.mulByMinusOne(node)
				.wrap().multiply(new MinusOne(utils.kernel));
		shouldSerialize(expected.getDefinition(), actual.wrap());
	}

	@Test
	public void testNumberForGCD() {
		numberForGCDShouldBe("2", 2);
		numberForGCDShouldBe("2sqrt(2)", 2);
		numberForGCDShouldBe("-2sqrt(2)", -2);
		numberForGCDShouldBe("-2(sqrt(2))", -2);
		numberForGCDShouldBe("(-2(sqrt(2)))", -2);
	}

	private void numberForGCDShouldBe(String def, int number) {
		GeoElementND geo = evaluateGeoElement(def);
		assertEquals(number, utils.getNumberForGCD(geo.getDefinition()));
	}

	@Test
	public void minusConjugateTest() {
		GeoElementND a = evaluateGeoElement("-2 + sqrt(5)");
		assertEquals("2 + sqrt(5)", utils.getMinusConjugate(a.getDefinition(), Operation.PLUS)
				.toString(StringTemplate.defaultTemplate));
	}

	@ParameterizedTest
	@CsvSource({
			"2 * 6sqrt(2), 12sqrt(2)",
			"3*sqrt(7)*2, 6sqrt(7)",
			"3*sqrt(7)sqrt(3)*2, 6sqrt(21)",
			"2*2*sqrt(8)*2, 16sqrt(2)",
			"sqrt(2) * -sqrt(6), -2sqrt(3)",
			"-sqrt(2) * -sqrt(6), 2sqrt(3)",
			"2 * -sqrt(6), -2sqrt(6)",
			"-2 * -sqrt(6), 2sqrt(6)",
			"2sqrt(2) * -sqrt(6), -4sqrt(3)"
	})
	public void testReduceProduct(String definition, String simplified) {
		GeoElementND product = evaluateGeoElement(definition);
		assertEquals(simplified, utils.reduceProduct(product.getDefinition())
				.toOutputValueString(StringTemplate.defaultTemplate));
	}

	@Test
	void wip() {
		testReduceProduct("8sqrt(8)", "16sqrt(2)");
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"1 + sqrt(3)",
			"1 - sqrt(3)",
			"1 + 2sqrt(3)",
			"6sqrt(7) + 2sqrt(3)",
			"-6 - 2sqrt(3)"
	})
	public void testIsAtomicAddSubNode(String definition) {
		assertTrue(ExpressionValueUtils.isAtomicSurdAdditionNode(
				evaluateGeoElement(definition).getDefinition()));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"1 + sqrt(3) + sqrt(3)",
			"1 + (2sqrt(3) + 1)"
	})
	public void testNotSimpleTag(String definition) {
		assertFalse(ExpressionValueUtils.isAtomicSurdAdditionNode(
				evaluateGeoElement(definition).getDefinition()));
	}

	@ParameterizedTest
	@CsvSource({
			"10 - 3sqrt(2) - 2sqrt(3) + 5sqrt(6), -10 + 3sqrt(2) + 2sqrt(3) - 5sqrt(6)",
			"10 + -3sqrt(2) + -2sqrt(3) + 5sqrt(6), -10 + 3sqrt(2) + 2sqrt(3) - 5sqrt(6)",
			"4 + sqrt(2), -4 - sqrt(2)",
			"-4 + sqrt(2), 4 - sqrt(2)",
			"-4 - sqrt(2), 4 + sqrt(2)",
			"sqrt(2) + 4, -sqrt(2) - 4",
			"sqrt(2) - 4, -sqrt(2) + 4",
			"-sqrt(2) - 4, sqrt(2) + 4",
			"(-2-sqrt(3))(4), (2 + sqrt(3)) * 4",
			"(-2-sqrt(3))sqrt(5), (2 + sqrt(3)) sqrt(5)",
			"-2sqrt(2) - sqrt(14), 2sqrt(2) + sqrt(14)"
	})
	public void testNegateTagByTag(String definition, String simplified) {
		GeoElementND product = evaluateGeoElement(definition);
		ExpressionNode node = product.getDefinition();
		ExpressionNode negated = utils.negateTagByTag(node);
		assertAll(
				() -> assertEquals(node.multiply(-1).evaluateDouble(), negated.evaluateDouble()),
				() -> assertEquals(simplified,
						negated.toOutputValueString(StringTemplate.defaultTemplate)));
	}

	@ParameterizedTest
	@CsvSource({
			"3, -3",
			"-(-3), -3",
			"-3, 3",
			"sqrt(2), -sqrt(2)",
			"-sqrt(2), sqrt(2)",
			"5sqrt(2), -5sqrt(2)",
			"-5sqrt(2), 5sqrt(2)",
			"-(-5sqrt(2)), -5 sqrt(2)",
			"3 + sqrt(2), -3 - sqrt(2)",
			"-3 + 5sqrt(2), 3 - 5sqrt(2)",
			"-3 + -5sqrt(2), 3 + 5sqrt(2)",
			"-3 - 5sqrt(2), 3 + 5sqrt(2)",
			"-3 - -5sqrt(2), 3 - 5sqrt(2)"
	})
	void tagByTag(String definition, String simplified) {
		testNegateTagByTag(definition, simplified);
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"3",
			"-3",
			"sqrt(2)",
			"-sqrt(2)",
			"5sqrt(2)",
			"-5sqrt(2)",
	})
	void testIsAtomic(String definition) {
		assertTrue(ExpressionValueUtils.isAtomic(evaluateGeoElement(definition)));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"3 + sqrt(7)",
			"-3 + 2sqrt(2)",
			"sqrt(2) sqrt(3)",
			"-sqrt(2) - 2",
			"5sqrt(2) - sqrt(5)",
			"-5sqrt(2) + 1",
	})
	void testIsNotAtomic(String definition) {
		assertTrue(ExpressionValueUtils.isAtomic(evaluateGeoElement(definition)));
	}
}
