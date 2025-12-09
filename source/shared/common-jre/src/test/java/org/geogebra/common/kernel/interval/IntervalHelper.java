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

import org.geogebra.common.kernel.interval.operators.RMath;

public class IntervalHelper {

	/**
	 * Create an interval containing a single value.
	 * @param singleton single value
	 * @return interval
	 */
	public static Interval interval(double singleton) {
		return new Interval(singleton);
	}

	/**
	 * @param low minimum
	 * @param high maximum
	 * @return inverted interval
	 */
	public static Interval invertedInterval(double low, double high) {
		return interval(low, high).invert();
	}

	/**
	 * @param low minimum
	 * @param high maximum
	 * @return interval
	 */
	public static Interval interval(double low, double high) {
		return new Interval(low, high);
	}

	/**
	 * Makes an interval [value - PRECISION, value + PRECISION]
	 * @param value to make an interval around.
	 * @return interval [value - PRECISION, value + PRECISION]
	 */
	public static Interval around(double value) {
		return around(value, IntervalConstants.PRECISION);
	}

	/**
	 * Makes an interval around a value as center and precision as radius.
	 *
	 * @param value to make an interval around.
	 * @param precision the radius around value
	 * @return interval [value - precision, value + precision]
	 */
	public static Interval around(double value, double precision) {
		double epsilon = RMath.prev(precision);
		return interval(value - epsilon, value + epsilon);
	}
}
