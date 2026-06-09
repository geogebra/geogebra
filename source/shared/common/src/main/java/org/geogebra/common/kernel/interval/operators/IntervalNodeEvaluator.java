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

import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalSetOps.connected;
import static org.geogebra.common.kernel.interval.IntervalSetOps.connectedInterval;
import static org.geogebra.common.kernel.interval.IntervalSetOps.empty;
import static org.geogebra.common.kernel.interval.IntervalSetOps.fromLegacy;
import static org.geogebra.common.kernel.interval.IntervalSetOps.leftRayFromInverted;
import static org.geogebra.common.kernel.interval.IntervalSetOps.legacyInverted;
import static org.geogebra.common.kernel.interval.IntervalSetOps.rightRayFromInverted;
import static org.geogebra.common.kernel.interval.IntervalSetOps.toLegacy;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalSet;
import org.geogebra.common.kernel.interval.IntervalSetOps;
import org.geogebra.common.kernel.interval.evaluators.IntervalNodePowerEvaluator;
import org.geogebra.common.kernel.interval.node.IntervalNode;

/**
 * Evaluates interval operations over both the legacy {@link Interval} API and the
 * topology-aware {@link IntervalSet} model.
 *
 * <p>The {@code *Set} methods define the main semantic contract. The legacy
 * wrappers preserve the older API by converting at the boundary.
 */
public class IntervalNodeEvaluator {
	private final IntervalAlgebra algebra;
	private final IntervalMultiply multiply;
	private final IntervalRoot nroot;
	private final IntervalTrigonometric trigonometric;
	private final IntervalMiscOperands misc;
	private final IntervalDivide divide;

	private final IntervalSinCos sinCos;
	private final IntervalNodePowerEvaluator power;
	private final IntervalInverse inverse;

	/**
	 * Creates an interval evaluator with the standard operator implementations for
	 * arithmetic, roots, trigonometric functions, miscellaneous unary operations,
	 * and power handling.
	 */
	public IntervalNodeEvaluator() {
		algebra = new IntervalAlgebra(this);
		multiply = new IntervalMultiply();
		nroot = new IntervalRoot(this);
		trigonometric = new IntervalTrigonometric();
		misc = new IntervalMiscOperands(this);
		power = new IntervalNodePowerEvaluator(this);
		divide = new IntervalDivide(this);
		sinCos = new IntervalSinCos(this);
		inverse = new IntervalInverse(divide);
	}

	/**
	 * Multiplies two legacy intervals.
	 *
	 * @param interval left factor
	 * @param other right factor
	 * @return the product of the two intervals
	 */
	public Interval multiply(Interval interval, Interval other) {
		return toLegacy(multiplySet(fromLegacy(interval), fromLegacy(other)));
	}

	/**
	 * Multiplies two interval sets.
	 *
	 * @param interval left factor
	 * @param other right factor
	 * @return the product of the two sets
	 */
	public IntervalSet multiplySet(IntervalSet interval, IntervalSet other) {
		return multiply.computeSet(interval, other);
	}

	/**
	 * Divides one legacy interval by another.
	 *
	 * @param interval numerator
	 * @param other divisor
	 * @return the quotient set in legacy interval form
	 */
	public Interval divide(Interval interval, Interval other) {
		return divide.compute(interval, other);
	}

	/**
	 * Divides one interval set by another.
	 *
	 * @param interval numerator
	 * @param other divisor
	 * @return the quotient set
	 */
	public IntervalSet divideSet(IntervalSet interval, IntervalSet other) {
		return divide.computeSet(interval, other);
	}

	/**
	 * Returns the multiplicative inverse of a legacy interval.
	 *
	 * @param interval interval to invert
	 * @return the set {@code 1 / interval} in legacy form
	 */
	public Interval inverse(Interval interval) {
		return inverse.compute(interval);
	}

	/**
	 * Returns the multiplicative inverse of an interval set.
	 *
	 * @param set interval set to invert
	 * @return the set {@code 1 / set}
	 */
	public IntervalSet inverseSet(IntervalSet set) {
		return inverse.computeSet(set);
	}

	/**
	 * Raises an interval set to a scalar power.
	 *
	 * @param set base set
	 * @param power scalar exponent
	 * @return {@code set ^ power}
	 */
	public IntervalSet powSet(IntervalSet set, double power) {
		return powSet(set, connected(power, power));
	}

	/**
	 * Raises an interval set to an interval-set exponent.
	 *
	 * @param set base set
	 * @param power exponent set
	 * @return {@code set ^ power}
	 */
	public IntervalSet powSet(IntervalSet set, IntervalSet power) {
		return algebra.powSet(set, power);
	}

	/**
	 * Raises a legacy interval to a scalar power.
	 *
	 * @param interval base interval
	 * @param power scalar exponent
	 * @return {@code interval ^ power}
	 */
	public Interval pow(Interval interval, double power) {
		return toLegacy(powSet(fromLegacy(interval), power));
	}

	/**
	 * Raises a legacy interval to a legacy interval exponent.
	 *
	 * @param interval base interval
	 * @param other exponent interval
	 * @return {@code interval ^ other}
	 */
	public Interval pow(Interval interval, Interval other) {
		return toLegacy(powSet(fromLegacy(interval), fromLegacy(other)));
	}

	/**
	 * Returns the nth root of a legacy interval.
	 *
	 * @param interval interval to root
	 * @param other root index as a singleton interval
	 * @return the nth root of {@code interval}
	 */
	public Interval nthRoot(Interval interval, Interval other) {
		return toLegacy(nthRootSet(fromLegacy(interval), fromLegacy(other)));
	}

	/**
	 * Returns the nth root of an interval set when the root index is a connected
	 * singleton.
	 *
	 * @param set1 radicand
	 * @param set2 root index as an interval set
	 * @return the nth root result, or {@link IntervalSetOps#empty()} if the index
	 *         is not a connected singleton
	 */
	public IntervalSet nthRootSet(IntervalSet set1, IntervalSet set2) {
		if (!IntervalSetOps.isSingleton(set2)) {
			return empty();
		}
		return nthRootSet(set1, connectedInterval(set2).getLow());
	}

	/**
	 * Returns the nth root of a legacy interval.
	 *
	 * @param interval interval to root
	 * @param n root index
	 * @return the nth root of {@code interval}
	 */
	public Interval nthRoot(Interval interval, double n) {
		return toLegacy(nthRootSet(fromLegacy(interval), n));
	}

	private IntervalSet nthRootSet(IntervalSet set, double n) {
		return nroot.computeSet(set, n);
	}

	/**
	 * Returns the set difference of two legacy intervals.
	 *
	 * @param interval minuend
	 * @param other subtrahend
	 * @return {@code interval \ other}
	 */
	public Interval difference(Interval interval, Interval other) {
		return toLegacy(differenceSet(fromLegacy(interval), fromLegacy(other)));
	}

	/**
	 * Returns the set difference of two interval sets.
	 *
	 * @param set1 minuend
	 * @param set2 subtrahend
	 * @return {@code set1 \ set2}
	 */
	public IntervalSet differenceSet(IntervalSet set1, IntervalSet set2) {
		return misc.difference(set1, set2);
	}

	/**
	 * Returns the legacy modulo result of two intervals.
	 *
	 * @param interval dividend
	 * @param other divisor
	 * @return {@code interval mod other}
	 */
	public Interval fmod(Interval interval, Interval other) {
		return toLegacy(fmodSet(fromLegacy(interval), fromLegacy(other)));
	}

	/**
	 * @param interval input interval
	 * @return the cosine image of {@code interval}
	 */
	public Interval cos(Interval interval) {
		return toLegacy(cosSet(fromLegacy(interval)));
	}

	/**
	 * @param set input set
	 * @return the cosine image of {@code set}
	 */
	public IntervalSet cosSet(IntervalSet set) {
		return sinCos.cos(set);
	}

	/**
	 * @param interval input interval
	 * @return the secant image of {@code interval}
	 */
	public Interval sec(Interval interval) {
		return toLegacy(inverseSet(cosSet(fromLegacy(interval))));
	}

	/**
	 * @param interval input interval
	 * @return the cosecant image of {@code interval}
	 */
	public Interval csc(Interval interval) {
		return toLegacy(inverseSet(sinSet(fromLegacy(interval))));
	}

	/**
	 * @param interval input interval
	 * @return the cotangent image of {@code interval}
	 */
	public Interval cot(Interval interval) {
		return toLegacy(divideSet(cosSet(fromLegacy(interval)), sinSet(fromLegacy(interval))));
	}

	/**
	 * @param interval input interval
	 * @return the sine image of {@code interval}
	 */
	public Interval sin(Interval interval) {
		return toLegacy(sinSet(fromLegacy(interval)));
	}

	/**
	 * @param set input set
	 * @return the sine image of {@code set}
	 */
	public IntervalSet sinSet(IntervalSet set) {
		return sinCos.sin(set);
	}

	/**
	 * @param interval input interval
	 * @return the tangent image of {@code interval}
	 */
	public Interval tan(Interval interval) {
		return toLegacy(tanSet(fromLegacy(interval)));

	}

	private IntervalSet tanSet(IntervalSet set) {
		return divideSet(sinSet(set), cosSet(set));
	}

	/**
	 * @param interval input interval
	 * @return the arcsine image of {@code interval}
	 */
	public Interval asin(Interval interval) {
		return toLegacy(asinSet(fromLegacy(interval)));
	}

	/**
	 * @param set input set
	 * @return the arcsine image of {@code set}
	 */
	public IntervalSet asinSet(IntervalSet set) {
		return trigonometric.asin(set);
	}

	/**
	 * @param interval input interval
	 * @return the arccosine image of {@code interval}
	 */
	public Interval acos(Interval interval) {
		return toLegacy(acosSet(fromLegacy(interval)));
	}

	/**
	 * @param set input set
	 * @return the arccosine image of {@code set}
	 */
	public IntervalSet acosSet(IntervalSet set) {
		return trigonometric.acos(set);
	}

	/**
	 * @param interval input interval
	 * @return the arctangent image of {@code interval}
	 */
	public Interval atan(Interval interval) {
		return toLegacy(atanSet(fromLegacy(interval)));
	}

	/**
	 * @param set input set
	 * @return the arctangent image of {@code set}
	 */
	public IntervalSet atanSet(IntervalSet set) {
		return trigonometric.atan(set);
	}

	/**
	 * @param interval input interval
	 * @return the hyperbolic sine image of {@code interval}
	 */
	public Interval sinh(Interval interval) {
		return toLegacy(sinhSet(fromLegacy(interval)));
	}

	/**
	 * @param set input set
	 * @return the hyperbolic sine image of {@code set}
	 */
	public IntervalSet sinhSet(IntervalSet set) {
		return trigonometric.sinh(set);
	}

	/**
	 * @param interval input interval
	 * @return the hyperbolic cosine image of {@code interval}
	 */
	public Interval cosh(Interval interval) {
		return toLegacy(coshSet(fromLegacy(interval)));
	}

	/**
	 * @param set input set
	 * @return the hyperbolic cosine image of {@code set}
	 */
	public IntervalSet coshSet(IntervalSet set) {
		return trigonometric.cosh(set);
	}

	/**
	 * @param interval input interval
	 * @return the hyperbolic tangent image of {@code interval}
	 */
	public Interval tanh(Interval interval) {
		return toLegacy(tanhSet(fromLegacy(interval)));
	}

	/**
	 * @param set input set
	 * @return the hyperbolic tangent image of {@code set}
	 */
	public IntervalSet tanhSet(IntervalSet set) {
		return trigonometric.tanh(set);
	}

	/**
	 * @param interval input interval
	 * @return the exponential image of {@code interval}
	 */
	public Interval exp(Interval interval) {
		return toLegacy(expSet(fromLegacy(interval)));
	}

	/**
	 * @param set input set
	 * @return the exponential image of {@code set}
	 */
	public IntervalSet expSet(IntervalSet set) {
		return misc.exp(set);
	}

	/**
	 * @param interval input interval
	 * @return the natural logarithm image of {@code interval}
	 */
	public Interval log(Interval interval) {
		return toLegacy(logSet(fromLegacy(interval)));
	}

	/**
	 * @param set input set
	 * @return the natural logarithm image of {@code set}
	 */
	public IntervalSet logSet(IntervalSet set) {
		return misc.log(set);
	}

	/**
	 * @param interval input interval
	 * @return the square-root image of {@code interval}
	 */
	public Interval sqrt(Interval interval) {
		return toLegacy(sqrtSet(fromLegacy(interval)));
	}

	/**
	 * @param set input set
	 * @return the square-root image of {@code set}
	 */
	public IntervalSet sqrtSet(IntervalSet set) {
		return nroot.computeSet(set, 2);
	}

	/**
	 * @param interval input interval
	 * @return the absolute-value image of {@code interval}
	 */
	public Interval abs(Interval interval) {
		return toLegacy(absSet(fromLegacy(interval)));
	}

	/**
	 * @param set input set
	 * @return the absolute-value image of {@code set}
	 */
	public IntervalSet absSet(IntervalSet set) {
		return misc.abs(set);
	}

	/**
	 * @param interval input interval
	 * @return the base-10 logarithm image of {@code interval}
	 */
	public Interval log10(Interval interval) {
		return toLegacy(log10Set(fromLegacy(interval)));
	}

	/**
	 * @param set input set
	 * @return the base-10 logarithm image of {@code set}
	 */
	public IntervalSet log10Set(IntervalSet set) {
		return misc.log10(set);
	}

	/**
	 * @param interval input interval
	 * @return the base-2 logarithm image of {@code interval}
	 */
	public Interval log2(Interval interval) {
		return toLegacy(log2Set(fromLegacy(interval)));
	}

	/**
	 * @param set input set
	 * @return the base-2 logarithm image of {@code set}
	 */
	public IntervalSet log2Set(IntervalSet set) {
		return misc.log2(set);
	}

	/**
	 * Returns the convex hull of two legacy intervals.
	 *
	 * @param interval first interval
	 * @param other second interval
	 * @return the smallest interval set containing both inputs, in legacy form
	 */
	public Interval hull(Interval interval, Interval other) {
		return toLegacy(hullSet(fromLegacy(interval), fromLegacy(other)));
	}

	/**
	 * Returns the convex hull of two interval sets.
	 *
	 * @param set1 first set
	 * @param set2 second set
	 * @return the smallest set returned by the hull operation
	 */
	public IntervalSet hullSet(IntervalSet set1, IntervalSet set2) {
		return misc.hull(set1, set2);
	}

	/**
	 * Returns the intersection of two legacy intervals.
	 *
	 * @param interval first interval
	 * @param other second interval
	 * @return the intersection in legacy form
	 */
	public Interval intersect(Interval interval, Interval other) {
		return toLegacy(intersectSet(fromLegacy(interval), fromLegacy(other)));
	}

	/**
	 * Returns the intersection of two interval sets.
	 *
	 * @param set1 first set
	 * @param set2 second set
	 * @return the intersection of the inputs
	 */
	public IntervalSet intersectSet(IntervalSet set1, IntervalSet set2) {
		return misc.intersect(set1, set2);
	}

	/**
	 * Returns the union of two legacy intervals when that union is representable by
	 * the legacy interval model.
	 *
	 * @param interval first interval
	 * @param other second interval
	 * @return the union in legacy form, or {@code undefined()} when the legacy model
	 *         cannot represent it
	 */
	public Interval union(Interval interval, Interval other) {
		return toLegacy(unionSet(fromLegacy(interval), fromLegacy(other)));
	}

	/**
	 * Returns the union of two interval sets when that union is representable by the
	 * current set model.
	 *
	 * @param set1 first set
	 * @param set2 second set
	 * @return the union of the inputs
	 */
	public IntervalSet unionSet(IntervalSet set1, IntervalSet set2) {
		return misc.union(set1, set2);
	}

	/**
	 * Applies a unary operator to the two rays of an inverted interval set and
	 * recombines the results.
	 *
	 * @param set inverted operand
	 * @param operator unary operator to apply to each ray
	 * @return the recombined result
	 */
	public IntervalSet computeUnaryInverted(IntervalSet set, UnaryIntervalOperator operator) {
		IntervalSet setLow = operator.exec(leftRayFromInverted(set));
		IntervalSet setHigh = operator.exec(rightRayFromInverted(set));
		return unionInvertedSet(setLow, setHigh);
	}

	IntervalSet unionInvertedSet(IntervalSet result1, IntervalSet result2) {
		// Compatibility bridge: union of two unary-inverted results still relies on
		// legacy Interval encoding because IntervalSet has no native union primitive.
		return fromLegacy(unionInvertedResults(toLegacy(result1), toLegacy(result2)));
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

		return legacyInverted(result1.getHigh(), result2.getLow());
	}

	private boolean isNegativeOrEmpty(Interval interval) {
		return interval.getHigh() < 0 || interval.isUndefined();
	}

	/**
	 * Adds two legacy intervals.
	 *
	 * @param value1 first addend
	 * @param value2 second addend
	 * @return {@code value1 + value2}
	 */
	public Interval plus(Interval value1, Interval value2) {
		return toLegacy(plusSet(fromLegacy(value1), fromLegacy(value2)));
	}

	/**
	 * Adds two interval sets.
	 *
	 * @param set1 first addend
	 * @param set2 second addend
	 * @return {@code set1 + set2}
	 */
	public IntervalSet plusSet(IntervalSet set1, IntervalSet set2) {
		if (set1.isEmpty() || set2.isEmpty()) {
			return IntervalSetOps.empty();
		}

		// Compatibility bridge: legacy add() is not equivalent for all topology-driven
		// callers here (for example, 0^0 paths rely on the old undefined propagation),
		// so plusSet keeps the pre-existing explicit empty/inverted semantics.
		if (set1.isInverted() && set2.isInverted()) {
			return IntervalSetOps.empty();
		}

		Interval interval1 = toLegacy(set1);
		Interval interval2 = toLegacy(set2);
		double low = interval1.getLow() + interval2.getLow();
		double high = interval1.getHigh() + interval2.getHigh();
		if (set1.isInverted() || set2.isInverted()) {
			return IntervalSet.inverted(low, high);
		}

		return IntervalSet.connected(low, high);
	}

	/**
	 * Subtracts one legacy interval from another.
	 *
	 * @param value1 minuend
	 * @param value2 subtrahend
	 * @return {@code value1 - value2}
	 */
	public Interval minus(Interval value1, Interval value2) {
		return toLegacy(minusSet(fromLegacy(value1), fromLegacy(value2)));
	}

	/**
	 * Subtracts one interval set from another.
	 *
	 * @param set1 minuend
	 * @param set2 subtrahend
	 * @return {@code set1 - set2}
	 */
	public IntervalSet minusSet(IntervalSet set1, IntervalSet set2) {
		if (set1.isEmpty() || set2.isEmpty()) {
			return IntervalSetOps.empty();
		}

		return fromLegacy(toLegacy(set1).subtract(toLegacy(set2)));
	}

	/**
	 * Handles power evaluation for a legacy interval base and exponent together with
	 * the parsed exponent node.
	 *
	 * @param leftValue base interval
	 * @param rightValue exponent value
	 * @param right exponent syntax node
	 * @return the power result in legacy form
	 */
	public Interval handlePower(Interval leftValue, Interval rightValue, IntervalNode right) {
		return toLegacy(handlePowerSet(fromLegacy(leftValue),
				fromLegacy(rightValue), right));
	}

	/**
	 * Handles power evaluation for an interval-set base and exponent together with
	 * the parsed exponent node.
	 *
	 * @param leftValue base set
	 * @param rightValue exponent set
	 * @param right exponent syntax node
	 * @return the power result
	 */
	public IntervalSet handlePowerSet(IntervalSet leftValue, IntervalSet rightValue,
			IntervalNode right) {
		return power.handle(leftValue, rightValue, right);
	}

	/**
	 * @param interval input interval
	 * @return the multiplicative inverse of {@code interval}
	 */
	public Interval multiplicativeInverse(Interval interval) {
		return inverse(interval);
	}

	/**
	 * @param interval input set
	 * @return the multiplicative inverse of {@code interval}
	 */
	public IntervalSet multiplicativeInverseSet(IntervalSet interval) {
		return inverseSet(interval);
	}

	/**
	 * Returns the logarithm of {@code arg} in the given base.
	 *
	 * @param base logarithm base
	 * @param arg logarithm argument
	 * @return {@code log_base(arg)}
	 */
	public Interval logBase(Interval base, Interval arg) {
		return toLegacy(logBaseSet(fromLegacy(base), fromLegacy(arg)));
	}

	/**
	 * Returns the logarithm of {@code arg} in the given base.
	 *
	 * @param baseSet logarithm base
	 * @param argSet logarithm argument
	 * @return {@code log_base(arg)}
	 */
	public IntervalSet logBaseSet(IntervalSet baseSet, IntervalSet argSet) {
		return divideSet(logSet(argSet), logSet(baseSet));
	}

	/**
	 * Returns the modulo result of two interval sets.
	 *
	 * @param set1 dividend
	 * @param set2 divisor
	 * @return {@code set1 mod set2}
	 */
	public IntervalSet fmodSet(IntervalSet set1, IntervalSet set2) {
		return algebra.fmodSet(set1, set2);
	}
}
