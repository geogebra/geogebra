package org.geogebra.common.kernel.interval.evaluators;

import static org.geogebra.common.kernel.interval.IntervalConstants.PRECISION;
import static org.geogebra.common.kernel.interval.IntervalConstants.aroundZero;
import static org.geogebra.common.kernel.interval.IntervalConstants.one;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.geogebra.common.kernel.interval.function.GeoFunctionConverter;
import org.geogebra.common.kernel.interval.function.IntervalNodeFunction;
import org.geogebra.common.kernel.interval.operators.IntervalNodeEvaluator;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

public class IntervalNodePowerEvaluatorTest extends BaseUnitTest {

	private final IntervalNodeEvaluator evaluator = new IntervalNodeEvaluator();

	public static final GeoFunctionConverter converter = new GeoFunctionConverter();
	public static final Interval X_AROUND_ZERO =
			interval(-2.0539125955565396E-15, 0.19999999999999796);

	@Test
	public void evaluateXSquared()  {
		assertEquals(
				evaluator.pow(interval(1, 2), 2),
				valueOnInterval("x^2", 1, 2));
	}

	@Test
	public void evaluateXExponential()  {
		assertEquals(
				evaluator.pow(interval(1, 2), Math.E),
				valueOnInterval("x^e", 1, 2));
	}

	@Test
	public void evaluateXOnNegativePower()  {
		assertEquals(
				evaluator.inverse(evaluator.pow(interval(1, 2), 2)),
				valueOnInterval("x^-2", 1, 2));
	}

	@Test
	public void evaluateXPowerHalf()  {
		assertEquals(
				evaluator.nthRoot(interval(1, 16), 2),
				valueOnInterval("x^(1/2)", 1, 16));
	}

	@Test
	public void evaluateXPowerForth()  {
		assertEquals(evaluator.nthRoot(interval(1, 16), 4),
				valueOnInterval("x^(1/4)", 1, 16));
	}

	@Test
	public void evaluateXPowerTwoThird()  {
		assertEquals(evaluator.nthRoot(evaluator.pow(interval(1, 16), 2), 3),
				valueOnInterval("x^(2/3)", 1, 16));
	}

	@Test
	public void evaluateXOnNegativeFractionPower()  {
		assertEquals(
				evaluator.sqrt(evaluator.pow(evaluator.inverse(interval(9, 10)), 3)),
				valueOnInterval("x^(-3/2)", 9, 10));
	}

	@Test
	public void evaluateXOnDoublePower()  {
		assertEquals(evaluator.sqrt(interval(9, 10)),
				valueOnInterval("x^0.5", 9, 10));
	}

	@Test
	public void evaluatePowerOfNegativeFraction()  {
		String definition = "x^-(2/9)";
		assertEquals(interval(0.6715486801956773, 0.6745703694731457),
				valueOnInterval(definition, -6, -5.88));
	}

	@Test
	public void powerOfPower()  {
		assertEquals(interval(0.7348672461377986),
				valueOnInterval("(((x)^(1/9))^-1)^2", -4, -4));

	}

	@Test
	public void evaluatePowerOfFractionNegativeNominator()  {
		String definition = "x^(-2/9)";
		assertEquals(interval(0.6715486801956773, 0.6745703694731457),
				valueOnInterval(definition, -6, -5.88));
		assertEquals(interval(0.6715486801956773, 0.6745703694731457),
				valueOnInterval(definition, 5.88, 6));
	}

	@Test
	public void evaluatePowerOfNegativeFractionDenominator()  {
		String definition = "x^(2/-9)";
		assertEquals(interval(0.6715486801956773, 0.6745703694731457),
				valueOnInterval(definition, -6, -5.88));
		assertEquals(interval(0.6715486801956773, 0.6745703694731457),
				valueOnInterval(definition, 5.88, 6));
	}

	@Test
	public void evaluatePowerOfFractionMinus1under3()  {
		shouldBeXPowerOnMinusThird();
	}

	private void shouldBeXPowerOnMinusThird()  {
		IntervalNodeFunction function = converter.convert(add("x^(-1/3)"));
		assertEquals(IntervalConstants.one(), function.value(IntervalConstants.one()));
		assertEquals(IntervalConstants.one().negative(),
				function.value(IntervalConstants.one().negative()));
	}

	@Test
	public void evaluatePowerOfFraction1underMinus3() {
		Interval result = valueOnInterval("x^(1/-3)", -1, 1);
		assertTrue("result should be inverted", result.isInverted());
	}

	private Interval valueOnInterval(String definition, double low, double high) {
		IntervalNodeFunction function = converter.convert(add(definition));
		return function.value(interval(low, high));
	}

	@Test
	public void evaluatePowerOfNegativeFraction1under3()  {
		shouldBeXPowerOnMinusThird();
	}

	@Test
	public void evaluateZerothPowerOfX()  {
		shouldBeOne("x^0");
	}

	@Test
	public void evaluateXthPowerOfZero()  {
		assertTrue("0^x should be undefined around 0",
				valueOnInterval("0^x", -PRECISION, PRECISION).isUndefined());
	}

	@Test
	public void evaluateZeroPowerOfZeroMultipliedByXPowerOfZero() {
		shouldBeOne("0^0*x^0");
	}

	private void shouldBeOne(String definition) {
		IntervalNodeFunction function = converter.convert(add(definition));
		assertEquals(IntervalConstants.one(), function.value(IntervalConstants.whole()));
	}
	
	@Test
	public void xInverseOnPowerOfMinus2ShouldBeXSquared() {
		Interval x = interval(-2.0539125955565396E-15, 0.19999999999999796);
		Interval inverse = evaluator.inverse(x);
		Interval pow = evaluator.pow(inverse, -2);
		assertEquals(undefined().invert(), pow);
	}

	@Test
	public void xInverseAndPOWMinus1() {
		shouldBeSameAt("x^-1", "1/x",
				interval(-2.0539125955565396E-15, 0.19999999999999796));
	}

	@Test
	public void nrootOfXInverseAndPowFraction() {
		shouldBeSameAt("nroot(1/x, 9)", "(1/x)^(1/9)",
				interval(-2.0539125955565396E-15, 0.19999999999999796));
	}

	@Test
	public void xInverseAndPOWDoubleApply() {
		shouldBeSameAt("(x^-1)^-1", "1/(1/x)", X_AROUND_ZERO);
	}

	private void shouldBeSameAt(String description1, String description2, Interval x) {

		IntervalNodeFunction f1 = converter.convert(add(description1));

		IntervalNodeFunction f2 = converter.convert(add(description2));
		assertEquals(f1.value(x), f2.value(x));
	}

	@Test
	public void xInverseAndPOWDoubleApply2() {
		Interval pow1 = evaluator.pow(X_AROUND_ZERO, -1);
		Interval pow2 = evaluator.pow(pow1, -1);
		assertEquals(X_AROUND_ZERO, pow2);
	}

	@Test
	public void inverseOfXInverse() {
		assertEquals(X_AROUND_ZERO,
				evaluator.multiplicativeInverse(evaluator.multiplicativeInverse(X_AROUND_ZERO)));
	}

	@Test
	public void zeroOnPower() {
		assertEquals(undefined(), evaluator.pow(zero(), interval(-5)));
		assertEquals(undefined(), evaluator.pow(zero(), interval(-999, -5)));
		assertEquals(one(), evaluator.pow(aroundZero(), interval(0)));
		assertEquals(one(), evaluator.pow(zero(), interval(0)));
		assertEquals(zero(), evaluator.pow(zero(), interval(5)));
		assertEquals(zero(), evaluator.pow(zero(), interval(6, 10000)));
	}

	@Test
	public void zeroValueOnPower() {
		Interval x = interval(0.5, 0.515625);
		Interval sin0 = evaluator.sin(zero());
		assertEquals(zero(), evaluator.pow(sin0, x));
	}

	@Test
	@Issue("APPS-6809")
	public void exponentCloseToZero() {
		Interval base = interval(2);
		Interval exponent1 = interval(1E-9);
		Interval exponent2 = interval(10E-9);
		assertNotEquals(evaluator.pow(base, exponent1), evaluator.pow(base, exponent2));
	}

	@Test
	@Issue("APPS-6809")
	public void exponentCloseToInteger() {
		Interval base = interval(2);
		Interval exponent1 = interval(2.000000001);
		Interval exponent2 = interval(2.000000009);
		assertNotEquals(evaluator.pow(base, exponent1), evaluator.pow(base, exponent2));
	}
}