package org.geogebra.common.kernel.interval.evaluators;

import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.nthRoot;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.pow;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.sqrt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.geogebra.common.kernel.interval.function.IntervalFunction;
import org.junit.Assert;
import org.junit.Test;

public class IntervalPowerEvaluatorTest extends BaseUnitTest {

	public static final Interval X_AROUND_ZERO =
			interval(-2.0539125955565396E-15, 0.19999999999999796);

	@Test
	public void evaluateXSquared() throws Exception {
		assertEquals(
				pow(interval(1, 2), 2),
				evalOnInterval("x^2", 1, 2));
	}

	@Test
	public void evaluateXExponental() throws Exception {
		assertEquals(
				pow(interval(1, 2), Math.E),
				evalOnInterval("x^e", 1, 2));
	}

	@Test
	public void evaluateXOnNegativePower() throws Exception {
		assertEquals(
				pow(interval(1, 2), 2).multiplicativeInverse(),
				evalOnInterval("x^-2", 1, 2));
	}

	@Test
	public void evaluateXPowerHalf() throws Exception {
		assertEquals(
				nthRoot(interval(1, 16), 2),
				evalOnInterval("x^(1/2)", 1, 16));
	}

	private Interval evalOnInterval(String definition, double low, double high) throws Exception {
		GeoFunction geo = add(definition);
		return (new IntervalFunction(geo)).evaluate(interval(low, high));
	}

	@Test
	public void evaluateXPowerForth() throws Exception {
		assertEquals(nthRoot(interval(1, 16), 4),
				evalOnInterval("x^(1/4)", 1, 16));
	}

	@Test
	public void evaluateXPowerTwoThird() throws Exception {
		assertEquals(nthRoot(pow(interval(1, 16), 2), 3),
				evalOnInterval("x^(2/3)", 1, 16));
	}

	@Test
	public void evaluateXOnNegativeFractionPower() throws Exception {
		assertEquals(
				sqrt(pow(interval(9, 10), 3)).multiplicativeInverse(),
				evalOnInterval("x^(-3/2)", 9, 10));
	}

	@Test
	public void evaluateXOnDoublePower() throws Exception {
		assertEquals(sqrt(interval(9, 10)),
				evalOnInterval("x^0.5", 9, 10));
	}

	@Test
	public void evaluatePowerOfNegativeFraction() throws Exception {
		String definition = "x^-(2/9)";
		assertEquals(interval(0.6715486801956773, 0.6745703694731457),
				evalOnInterval(definition, -6, -5.88));
	}

	@Test
	public void powerOfPower() throws Exception {
		String definition = "(((x)^(1/9))^-1)^2";
		assertEquals(interval(0.7348672461377986),
				evalOnInterval(definition, -4, -4));

	}

	@Test
	public void evaluatePowerOfFractionNegativeNominator() throws Exception {
		GeoFunction geo = add("x^(-2/9)");
		IntervalFunction function = new IntervalFunction(geo);
		assertEquals(interval(0.6715486801956773, 0.6745703694731457),
				function.evaluate(interval(-6, -5.88)));
		assertEquals(interval(0.6715486801956773, 0.6745703694731457),
				function.evaluate(interval(5.88, 6)));
	}

	@Test
	public void evaluatePowerOfNegativeFractionDenominator() throws Exception {
		GeoFunction geo = add("x^(2/-9)");
		IntervalFunction function = new IntervalFunction(geo);
		assertEquals(interval(0.6715486801956773, 0.6745703694731457),
				function.evaluate(interval(-6, -5.88)));
		assertEquals(interval(0.6715486801956773, 0.6745703694731457),
				function.evaluate(interval(5.88, 6)));
	}

	@Test
	public void evaluatePowerOfFractionMinus1under3() throws Exception {
		GeoFunction geo = add("x^(-1/3)");
		IntervalFunction function = new IntervalFunction(geo);
		shouldBeXPowerOnMinusThird(function);
	}

	private void shouldBeXPowerOnMinusThird(IntervalFunction function) throws Exception {
		Assert.assertEquals(IntervalConstants.one(), function.evaluate(IntervalConstants.one()));
		assertEquals(IntervalConstants.one().negative(),
				function.evaluate(IntervalConstants.one().negative()));
	}

	@Test
	public void evaluatePowerOfFraction1underMinus3() throws Exception {
		GeoFunction geo = add("x^(1/-3)");
		IntervalFunction function = new IntervalFunction(geo);
		Interval result = function.evaluate(interval(-1, 1));
		assertTrue("result should be inverted", result.isInverted());
	}

	@Test
	public void evaluatePowerOfNegativeFraction1under3() throws Exception {
		GeoFunction geo = add("x^-(1/3)");
		IntervalFunction function = new IntervalFunction(geo);
		shouldBeXPowerOnMinusThird(function);
	}

	@Test
	public void evaluateXPowerOfZero() throws Exception {
		GeoFunction geo = add("x^0");
		IntervalFunction function = new IntervalFunction(geo);
		shouldBeOne(function);
	}

	@Test
	public void evaluateZeroPowerOfZeroMultipliedByXPowerOfZero() {
		GeoFunction geo = add("0^0*x^0");
		IntervalFunction function = new IntervalFunction(geo);
		shouldBeOne(function);
	}

	private void shouldBeOne(IntervalFunction function) {
		assertEquals(IntervalConstants.one(), function.evaluate(IntervalConstants.whole()));
	}

	@Test
	public void xInverseOnPowerOfMinus2ShouldBeXSquared() {
		Interval x = interval(-2.0539125955565396E-15, 0.19999999999999796);
		Interval inverse = x.multiplicativeInverse();
		Interval pow = pow(inverse, -2);
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
		IntervalFunction f1 = newFunction(description1);
		IntervalFunction f2 = newFunction(description2);
		assertEquals(f1.evaluate(x), f2.evaluate(x));
	}

	private IntervalFunction newFunction(String description) {
		return new IntervalFunction(add(description));

	}

	@Test
	public void xInverseAndPOWDoubleApply2() {
		Interval pow1 = pow(X_AROUND_ZERO, -1);
		Interval pow2 = pow(pow1, -1);
		assertEquals(X_AROUND_ZERO, pow2);
	}

	@Test
	public void inverseOfXInverse() {
		assertEquals(X_AROUND_ZERO,
				X_AROUND_ZERO.multiplicativeInverse().multiplicativeInverse());
	}
}