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

package org.geogebra.common.kernel.interval;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.interval.operators.RMath;

public final class IntervalConstants {
	public static final double PI_LOW = Math.PI - 1E-16;
	public static final double PI_HIGH = Math.PI + 1E-16;
	public static final double PI_HALF_LOW = PI_LOW / 2.0;
	public static final double PI_HALF_HIGH = PI_HIGH / 2.0;
	public static final double PI_TWICE_LOW = PI_LOW * 2.0;
	public static final double PI_TWICE_HIGH = PI_HIGH * 2.0;
	public static final double PRECISION = Kernel.MAX_PRECISION;

	/**
	 *
	 * @return a newly created empty interval.
	 */
	public static Interval undefined() {
		return new Interval(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
	}

	/**
	 *
	 * @return a newly created zero interval.
	 */
	public static Interval zero() {
		return new Interval(0);
	}

	/**
	 *
	 * @return a newly created zero interval.
	 */
	public static Interval one() {
		return new Interval(1);
	}

	/**
	 *
	 * @return a newly created whole interval.
	 */
	public static Interval whole() {
		return new Interval(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
	}

	/**
	 *
	 * @return a newly created PI interval.
	 */
	public static Interval pi() {
		return new Interval(PI_LOW, PI_HIGH);
	}

	/**
	 *
	 * @return a newly created PI interval.
	 */
	public static Interval piTwice() {
		return new Interval(PI_TWICE_LOW, PI_TWICE_HIGH);
	}

	/**
	 *
	 * @return a newly created PI/2 interval.
	 */
	public static Interval piHalf() {
		return new Interval(PI_HALF_LOW, PI_HALF_HIGH);
	}

	/**
	 *
	 * @return a newly created positive infinity singleton interval.
	 */
	public static Interval positiveInfinity() {
		return new Interval(Double.POSITIVE_INFINITY,
				Double.POSITIVE_INFINITY);
	}

	/**
	 *
	 * @return a newly created negative infinity singleton interval.
	 */
	public static Interval negativeInfinity() {
		return new Interval(Double.NEGATIVE_INFINITY,
				Double.NEGATIVE_INFINITY);
	}

	/**
	 *
	 * @return a "-0" interval for 1/-inf compatibility.
	 */
	public static Interval zeroWithNegativeSign() {
		return new Interval(-1E-7, 1E-7);
	}

	/**
	 *
	 * @return interval that is considered to be zero.
	 */
	public static Interval zeroWithinPrecision() {
		return new Interval(RMath.next(-PRECISION), RMath.prev(PRECISION));
	}

	/**
	 *
	 * @return a small interval that contains 0.
	 */
	public static Interval aroundZero() {
		return new Interval(-1E-4, 1E-4);
	}

	private IntervalConstants() {
		throw new IllegalStateException("Constants class");
	}
}