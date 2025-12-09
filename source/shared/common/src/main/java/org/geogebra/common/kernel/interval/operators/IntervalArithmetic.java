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
 * Interval arithmetic.
 */
public interface IntervalArithmetic {

	/**
	 * Interval multiplication
	 *
	 * @param other to multiply this interval with.
	 * @return this as result.
	 */
	Interval multiply(Interval interval, Interval other);

	/**
	 * Interval division
	 *
	 * @param other to divide this interval with.
	 * @return this as result.
	 */
	Interval divide(Interval interval, Interval other);
}
