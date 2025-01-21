package org.geogebra.common.kernel.arithmetic.simplifiers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.Operation;
import org.junit.Before;
import org.junit.Test;

public class SimplifyUtilsTest extends BaseSimplifyTest {

	private SimplifyUtils utils;

	@Before
	public void setUp() {
		utils = new SimplifyUtils(getKernel());
	}

	@Override
	protected Class<? extends SimplifyNode> getSimplifierClass() {
		return null;
	}

	@Test
	public void testExpand() {
		shouldExpand("2 (1 + sqrt(4))", "6");
		shouldExpand("(1 + sqrt(4) - 1) 2", "4");
		shouldExpand("2 (1 + sqrt(2))", "2 + 2sqrt(2)");
		shouldExpand("(1 + sqrt(2)) 2", "2 + 2sqrt(2)");
	}

	private void shouldExpand(String from, String to) {
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

	@Test
	public void testSqrtPositiveInteger() {
		sqrtShouldNotBeValid("sqrt(0.0001+10)");
		sqrtShouldNotBeValid("sqrt(1 / 4)");
		sqrtShouldNotBeValid("sqrt(2.5)");
		sqrtShouldBeValid("sqrt(1+2+3+4)");
		sqrtShouldBeValid("sqrt(4*0.25)");
	}

	private void sqrtShouldBeValid(String def) {
		GeoNumeric original = newSymbolicNumeric(def);
		assertTrue(def + " is not square root of a positive integer.",
				SimplifyUtils.isSqrtOfPositiveInteger(original.getDefinition()));
	}

	private void sqrtShouldNotBeValid(String def) {
		GeoNumeric original = newSymbolicNumeric(def);
		assertFalse(def + " is square root of a positive integer.",
				SimplifyUtils.isSqrtOfPositiveInteger(original.getDefinition()));
	}

	@Test
	public void testNodeSupported() {
		shouldSupportNode("1");
		shouldSupportNode("sqrt(2)");
		shouldSupportNode("sqrt(2) + 1");
		shouldSupportNode("1 + sqrt(2)");
		shouldSupportNode("sqrt(2) - 1");
		shouldSupportNode("1 - sqrt(2)");
		shouldNotSupportNode("sqrt(-2)");
	}

	@Test
	public void testAccept() {
		shouldNotSupportNode("sqrt(-2)");
	}

	private void shouldSupportNode(String def) {
		GeoNumeric numeric = newSymbolicNumeric(def);
		assertTrue(def + " is not supported",
				SimplifyUtils.isNodeSupported(numeric.getDefinition()));
	}

	private void shouldNotSupportNode(String def) {
		GeoNumeric numeric = newSymbolicNumeric(def);
		assertFalse(def + " should not be supported",
				SimplifyUtils.isNodeSupported(numeric.getDefinition()));
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
		ExpressionValue actual = utils.negative(original.getDefinition());
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
		GeoElementND geo = add(def);
		assertEquals(number, utils.getNumberForGCD(geo.getDefinition()));
	}

	@Test
	public void minusConjugateTest() {
		GeoElementND a = add("-2 + sqrt(5)");
		assertEquals("2 + sqrt(5)", utils.getMinusConjugate(a.getDefinition(), Operation.PLUS)
				.toString(StringTemplate.defaultTemplate));
	}
}
