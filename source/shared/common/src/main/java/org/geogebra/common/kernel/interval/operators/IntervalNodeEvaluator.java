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

package org.geogebra.common.kernel.interval.operators;

import static org.geogebra.common.kernel.interval.IntervalConstants.one;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.evaluators.IntervalNodePowerEvaluator;
import org.geogebra.common.kernel.interval.node.IntervalNode;

public class IntervalNodeEvaluator {
	private final IntervalAlgebra algebra;
	private final IntervalMultiply multiply;
	private final IntervalRoot nroot;
	private final IntervalTrigonometric trigonometric;
	private final IntervalMiscOperandsImpl misc;
	private final IntervalDivide divide;

	private final IntervalSinCos sinCos;
	private final IntervalNodePowerEvaluator power;

	/**
	 * Constructor
	 */
	public IntervalNodeEvaluator() {
		algebra = new IntervalAlgebra(this);
		multiply = new IntervalMultiply();
		nroot = new IntervalRoot(this);
		trigonometric = new IntervalTrigonometric();
		misc = new IntervalMiscOperandsImpl(this);
		power = new IntervalNodePowerEvaluator(this);
		divide = new IntervalDivide(this);
		sinCos = new IntervalSinCos(this);
	}

	/**
	 * @param interval first interval
	 * @param other second interval
	 * @return interval * other
	 */
	public Interval multiply(Interval interval, Interval other) {
		return multiply.compute(interval, other);
	}

	/**
	 * @param interval first interval
	 * @param other second interval
	 * @return interval / other
	 */
	public Interval divide(Interval interval, Interval other) {
		return divide.compute(interval, other);
	}

	/**
	 * The multiplication inverse, 1 / interval.
	 * @param interval to make the inverse from.
	 * @return the multiplication inverse.
	 */
	public Interval inverse(Interval interval) {
		if (interval.isZeroWithDelta(1E-6)) {
			return undefined();
		}
		return divide.compute(one(), interval);
	}

	/**
	 *
	 * @param power of the interval
	 * @return power of the interval
	 */
	public Interval pow(Interval interval, double power) {
		return algebra.pow(interval, power);
	}

	/**
	 * Power of an interval where power is also an interval
	 * that must be a singleton, ie [n, n]
	 *
	 * @param other interval power.
	 * @return this as result.
	 */
	public Interval pow(Interval interval, Interval other) {
		return algebra.pow(interval, other);
	}

	/**
	 * Computes the nth root of the interval
	 * if other (=n) is a singleton
	 *
	 * @param other interval
	 * @return nth root of the interval.
	 */
	public Interval nthRoot(Interval interval, Interval other) {
		return nroot.compute(interval, other);
	}

	/**
	 * Computes x^(1/n)
	 *
	 * @param n the root
	 * @return nth root of the interval.
	 */
	public Interval nthRoot(Interval interval, double n) {
		return nroot.compute(interval, n);
	}

	/**
	 * @param interval first interval
	 * @param other second interval
	 * @return set difference interval \ other
	 */
	public Interval difference(Interval interval, Interval other) {
		return misc.difference(interval, other);
	}

	/**
	 * Computes x mod y (x - k * y), result is stored in input interval
	 *
	 * @param other argument.
	 */
	public void fmod(Interval interval, Interval other) {
		algebra.fmod(interval, other);
	}

	/**
	 *
	 * @return cosine of the interval.
	 */
	public Interval cos(Interval interval) {
		return sinCos.cos(interval);
	}

	/**
	 *
	 * @return secant of the interval
	 */
	public Interval sec(Interval interval) {
		return inverse(cos(interval));
	}

	/**
	 *
	 * @return 1 / sin(x)
	 */
	public Interval csc(Interval interval) {
		return inverse(sin(interval));
	}

	/**
	 *
	 * @return cotangent of the interval
	 */
	public Interval cot(Interval interval) {
		Interval copy = new Interval(interval);
		return divide(cos(interval), sin(copy));
	}

	/**
	 *
	 * @return sine of the interval.
	 */
	public Interval sin(Interval interval) {
		return sinCos.sin(interval);
	}

	/**
	 *
	 * @return tangent of the interval.
	 */
	public Interval tan(Interval interval) {
		Interval copy = new Interval(interval);
		return divide(sin(interval), cos(copy));

	}

	/**
	 *
	 * @return arc sine of the interval
	 */
	public Interval asin(Interval interval) {
		return trigonometric.asin(interval);
	}

	/**
	 *
	 * @return arc cosine of the interval
	 */
	public Interval acos(Interval interval) {
		return trigonometric.acos(interval);
	}

	/**
	 *
	 * @return arc tangent of the interval
	 */
	public Interval atan(Interval interval) {
		return trigonometric.atan(interval);
	}

	/**
	 *
	 * @return hyperbolic sine of the interval
	 */
	public Interval sinh(Interval interval) {
		return trigonometric.sinh(interval);
	}

	/**
	 *
	 * @return hyperbolic cosine of the interval
	 */
	public Interval cosh(Interval interval) {
		return trigonometric.cosh(interval);
	}

	/**
	 *
	 * @return hyperbolic tangent of the interval
	 */
	public Interval tanh(Interval interval) {
		return trigonometric.tanh(interval);
	}

	/**
	 * @param interval interval
	 * @return e^interval
	 */
	public Interval exp(Interval interval) {
		return misc.exp(interval);
	}

	/**
	 * @param interval interval
	 * @return logarighm of the interval
	 */
	public Interval log(Interval interval) {
		return misc.log(interval);
	}

	/**
	 *
	 * @return square root of the interval.
	 */
	public Interval sqrt(Interval interval) {
		return nroot.compute(interval, 2);
	}

	/**
	 * @param interval interval
	 * @return absolute value of the interval
	 */
	public Interval abs(Interval interval) {
		return misc.abs(interval);
	}

	/**
	 * @param interval interval
	 * @return base 10 logarighm of the interval
	 */
	public Interval log10(Interval interval) {
		return misc.log10(interval);
	}

	/**
	 * @param interval interval
	 * @return base 2 logarithm of the interval
	 */
	public Interval log2(Interval interval) {
		return misc.log2(interval);
	}

	/**
	 * @param interval first interval
	 * @param other second interval
	 * @return hull of the intervals
	 */
	public Interval hull(Interval interval, Interval other) {
		return misc.hull(interval, other);
	}

	/**
	 * @param interval first interval
	 * @param other second interval
	 * @return intersection of the intervalss
	 */
	public Interval intersect(Interval interval, Interval other) {
		return misc.intersect(interval, other);
	}

	/**
	 * If the union is not an interval, the result is undefined.
	 * @param interval first interval
	 * @param other second interval
	 * @return union of the intervalss
	 */
	public Interval union(Interval interval, Interval other) {
		return misc.union(interval, other);
	}

	/**
	 * Compute unary operator result if operand is inverted.
	 *
	 * @param interval the operand.
	 * @param operator a unary operator.
	 * @return unified result
	 */
	public Interval computeUnaryInverted(Interval interval, UnaryIntervalOperator operator) {
		Interval intervalLow = operator.exec(interval.extractLow());
		Interval intervalHigh = operator.exec(interval.extractHigh());
		return unionInvertedResults(intervalLow, intervalHigh);
	}

	Interval unionInvertedResults(Interval result1, Interval result2) {
		if (result1.equals(result2) || result1.isPositive() && isNegativeOrEmpty(result2)) {
			return result1;
		}

		if (isNegativeOrEmpty(result1) && result2.isPositive()) {
			return result2;
		}

		if (isNegativeOrEmpty(result1) && isNegativeOrEmpty(result2)) {
			return undefined();
		}

		return new Interval(/* low= */ result1.getHigh(), /* high = */ result2.getLow()).invert();
	}

	private boolean isNegativeOrEmpty(Interval interval) {
		return interval.isNegative() || interval.isUndefined();
	}

	/**
	 * @param value1 first interval
	 * @param value2 second interval
	 * @return value1 + value2
	 */
	public Interval plus(Interval value1, Interval value2) {
		return new Interval(value1).add(value2);
	}

	/**
	 * @param value1 first interval
	 * @param value2 second interval
	 * @return value1 - value2
	 */
	public Interval minus(Interval value1, Interval value2) {
		return new Interval(value1).subtract(value2);
	}

	/**
	 * @param leftValue base
	 * @param rightValue exponent value
	 * @param right exponent definition
	 * @return leftValue ^ rightValue
	 */
	public Interval handlePower(Interval leftValue, Interval rightValue, IntervalNode right) {
		return power.handle(leftValue, rightValue, right);
	}

	/**
	 * @param interval interval
	 * @return 1/interval
	 */
	public Interval multiplicativeInverse(Interval interval) {
		return divide.compute(one(), interval);
	}

	/**
	 * @param base logarithm base
	 * @param arg logarith argument
	 * @return log(base,arg)
	 */
	public Interval logBase(Interval base, Interval arg) {
		return divide(log(arg), log(base));
	}
}
