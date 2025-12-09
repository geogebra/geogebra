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

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.geogebra.common.util.DoubleUtil;

public class IntervalRoot {

	private final IntervalNodeEvaluator evaluator;

	/**
	 *
	 * @param evaluator {@link IntervalNodeEvaluator}
	 */
	public IntervalRoot(IntervalNodeEvaluator evaluator) {

		this.evaluator = evaluator;
	}

	/**
	 * Computes the nth root of the interval
	 * if other (=n) is a singleton
	 * @param other interval
	 * @return nth root of the interval.
	 */
	Interval compute(Interval interval, Interval other) {
		if (!other.isSingleton()) {
			return undefined();
		}

		double power = other.getLow();
		return compute(interval, power);
	}

	/**
	 * Computes x^(1/n)
	 * @param n the root
	 * @return nth root of the interval.
	 */
	Interval compute(Interval interval, double n) {
		if (interval.isUndefined()) {
			return undefined();
		}

		if (interval.isInverted()) {
			if (isOdd(n)) {
				return compute(interval.uninvert(), n).invert();
			}

			return evaluator.unionInvertedResults(compute(interval.extractLow(), n),
					compute(interval.extractHigh(), n));
		}

		double power = 1 / n;
		if (isPositiveOdd(n)) {
			return new Interval(oddFractionPower(interval.getLow(), power),
					oddFractionPower(interval.getHigh(), power));
		}
		return evaluator.pow(interval, power).round();
	}

	private double oddFractionPower(double x, double power) {
		double fractionPower = Math.pow(Math.abs(x), power);
		return x > 0
				? Math.max(IntervalConstants.PRECISION, fractionPower)
				: Math.min(-IntervalConstants.PRECISION, -fractionPower);
	}

	private boolean isPositiveOdd(double n) {
		return n > 0 && isOdd(n);
	}

	private boolean isOdd(double n) {
		return DoubleUtil.isInteger(n) && ((int) n) % 2 != 0;
	}
}
