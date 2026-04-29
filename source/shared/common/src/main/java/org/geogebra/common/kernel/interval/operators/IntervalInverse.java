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
import static org.geogebra.common.kernel.interval.IntervalSetOps.fromLegacy;
import static org.geogebra.common.kernel.interval.IntervalSetOps.toLegacy;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalSet;

public class IntervalInverse {

	public static final double ZERO_TOLERANCE = 1E-6;
	private final IntervalDivide divide;
	private final IntervalSet one;

	/**
	 * Creates an inverse operator backed by interval division.
	 *
	 * @param divide divider used to compute {@code 1 / x}
	 */
	public IntervalInverse(IntervalDivide divide) {
		this.divide = divide;
		this.one = fromLegacy(one());
	}

	/**
	 * Returns the multiplicative inverse of a legacy interval.
	 *
	 * <p>The legacy entry point preserves the older near-zero contract and returns
	 * {@code undefined()} before delegating to the interval-set path.
	 *
	 * @param interval interval to invert
	 * @return the set {@code 1 / interval} in legacy form
	 */
	public Interval compute(Interval interval) {
		// Preserve the legacy public inverse contract: near-zero inputs short-circuit
		// before entering the generic IntervalSet seam.
		if (interval.isZeroWithDelta(ZERO_TOLERANCE)) {
			return undefined();
		}
		return toLegacy(computeSet(fromLegacy(interval)));
	}

	/**
	 * Returns the multiplicative inverse of an interval set.
	 *
	 * @param set set to invert
	 * @return the quotient set {@code 1 / set}
	 */
	public IntervalSet computeSet(IntervalSet set) {
		return divide.computeSet(one, set);
	}
}
