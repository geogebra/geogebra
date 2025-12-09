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

import org.geogebra.common.kernel.interval.Interval;

/**
 * Misc interval operands.
 */
public interface IntervalMiscOperands {
	/**
	 * Gives e^x where e is the euclidean constant
	 *
	 * @return e^x
	 */
	Interval exp(Interval interval);

	/**
	 *
	 * @return the natural logarithm of the interval.
	 */
	Interval log(Interval interval);

	/**
	 *
	 * @return base 10 logarithm of the interval
	 */
	Interval log10(Interval interval);

	/**
	 *
	 * @return base 2 logarithm of the interval
	 */
	Interval log2(Interval interval);

	/**
	 * From interval-arithmetic.js:
	 *
	 * Computes an interval that has all the values of this and other, note that it may be
	 * possible that values that don't belong to either this or other are included in the
	 * interval that represents the hull
	 *
	 * @param other to compute the hull with
	 * @return this as result.
	 */
	Interval hull(Interval interval, Interval other);

	/**
	 * Computes an interval that has all the values that belong to both x and y
	 *
	 * @param interval to intersect with
	 * @return this as result
	 */
	Interval intersect(Interval interval, Interval other);

	/**
	 * Union of intervals
	 * @param other to union with.
	 * @return this as result.
	 */
	Interval union(Interval interval, Interval other);

	/**
	 * Computes the difference between two intervals,
	 * i.e. an interval with all the values of this interval that are
	 * not in "other".
	 * @param other to difference with.
	 * @return this as result.
	 */
	Interval difference(Interval interval, Interval other);

	/**
	 * Absolute value of the interval.
	 *
	 * @return this as result.
	 */
	Interval abs(Interval interval);
}
