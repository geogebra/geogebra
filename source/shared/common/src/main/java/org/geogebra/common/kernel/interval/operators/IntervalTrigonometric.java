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

import static org.geogebra.common.kernel.interval.IntervalConstants.PI_HALF_HIGH;
import static org.geogebra.common.kernel.interval.IntervalConstants.PI_HIGH;
import static org.geogebra.common.kernel.interval.IntervalSetOps.connected;
import static org.geogebra.common.kernel.interval.IntervalSetOps.connectedInterval;
import static org.geogebra.common.kernel.interval.IntervalSetOps.empty;
import static org.geogebra.common.kernel.interval.IntervalSetOps.inverted;
import static org.geogebra.common.kernel.interval.IntervalSetOps.invertedGap;
import static org.geogebra.common.kernel.interval.IntervalSetOps.whole;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalSet;

public class IntervalTrigonometric {

	/**
	 *
	 * @return arc sine of the set
	 */
	public IntervalSet asin(IntervalSet set) {
		if (set.isEmpty()) {
			return empty();
		}

		if (set.isWhole()) {
			return connected(-PI_HALF_HIGH, PI_HALF_HIGH);
		}

		if (set.isConnected()) {
			return asinConnected(connectedInterval(set));
		}

		return asinInverted(invertedGap(set));
	}

	private IntervalSet asinConnected(Interval interval) {
		if (interval.getHigh() < -1 || interval.getLow() > 1) {
			return empty();
		}

		double low = interval.getLow() <= -1 ? -PI_HALF_HIGH
				: RMath.prev(Math.asin(interval.getLow()));
		double high = interval.getHigh() >= 1 ? PI_HALF_HIGH
				: RMath.next(Math.asin(interval.getHigh()));
		return connected(low, high);
	}

	private IntervalSet asinInverted(Interval gap) {
		double low = gap.getLow();
		double high = gap.getHigh();

		if (high < -1 || low > 1) {
			return connected(-PI_HALF_HIGH, PI_HALF_HIGH);
		}

		if (low <= -1 && high >= 1) {
			return empty();
		}

		if (low <= -1) {
			return connected(RMath.prev(Math.asin(high)), PI_HALF_HIGH);
		}

		if (high >= 1) {
			return connected(-PI_HALF_HIGH, RMath.next(Math.asin(low)));
		}

		return inverted(Math.asin(low), Math.asin(high));
	}

	/**
	 *
	 * @return arc cosine of the interval
	 */
	public IntervalSet acos(IntervalSet set) {
		if (set.isEmpty()) {
			return empty();
		}

		if (set.isWhole()) {
			return connected(0, PI_HIGH);
		}

		if (set.isConnected()) {
			return acosConnected(connectedInterval(set));
		}

		return acosInverted(invertedGap(set));
	}

	private IntervalSet acosConnected(Interval interval) {
		if (interval.getHigh() < -1 || interval.getLow() > 1) {
			return empty();
		}
		double low = interval.getHigh() >= 1 ? 0 : RMath.prev(Math.acos(interval.getHigh()));
		double high = interval.getLow() <= -1 ? PI_HIGH
				: RMath.next(Math.acos(interval.getLow()));
		return connected(low, high);
	}

	private IntervalSet acosInverted(Interval gap) {
		double low = gap.getLow();
		double high = gap.getHigh();

		if (high < -1 || low > 1) {
			return connected(0, PI_HIGH);
		}

		if (low <= -1 && high >= 1) {
			return empty();
		}

		if (low <= -1) {
			return connected(0, RMath.next(Math.acos(high)));
		}

		if (high >= 1) {
			return connected(RMath.prev(Math.acos(low)), PI_HIGH);
		}

		return inverted(Math.acos(high), Math.acos(low));
	}

	/**
	 *
	 * @return arc tangent of the interval
	 */
	public IntervalSet atan(IntervalSet set) {
		if (set.isEmpty()) {
			return set;
		}

		if (set.isWhole()) {
			return connected(-PI_HALF_HIGH, PI_HALF_HIGH);
		}

		if (set.isInverted()) {
			Interval gap = invertedGap(set);
			return inverted(RMath.prev(Math.atan(gap.getLow())),
					RMath.next(Math.atan(gap.getHigh())));
		}

		Interval interval = connectedInterval(set);
		return connected(RMath.prev(Math.atan(interval.getLow())),
				RMath.next(Math.atan(interval.getHigh())));
	}

	/**
	 *
	 * @return hyperbolic sine of the set
	 */
	public IntervalSet sinh(IntervalSet set) {
		if (set.isEmpty()) {
			return empty();
		}
		if (set.isWhole()) {
			return whole();
		}
		if (set.isInverted()) {
			Interval gap = invertedGap(set);
			return inverted(RMath.prev(Math.sinh(gap.getLow())),
					RMath.next(Math.sinh(gap.getHigh())));
		}
		Interval interval = connectedInterval(set);
		return connected(RMath.prev(Math.sinh(interval.getLow())),
				RMath.next(Math.sinh(interval.getHigh())));
	}

	/**
	 *
	 * @return hyperbolic cosine of the interval
	 */
	public IntervalSet cosh(IntervalSet set) {
		if (set.isEmpty()) {
			return empty();
		}

		if (set.isWhole()) {
			return connected(1, Double.POSITIVE_INFINITY);
		}

		if (set.isInverted()) {
			Interval gap = invertedGap(set);
			if (gap.getLow() >= 0 || gap.getHigh() <= 0) {
				return connected(1, Double.POSITIVE_INFINITY);
			}
			double minAbs = Math.min(-gap.getLow(), gap.getHigh());
			return connected(RMath.prev(Math.cosh(minAbs)), Double.POSITIVE_INFINITY);
		}

		Interval interval = connectedInterval(set);

		double low = interval.getLow();
		double high = interval.getHigh();
		if (high < 0) {
			double prev = RMath.prev(Math.cosh(high));
			return connected(prev, RMath.next(Math.cosh(low)));
		} else if (low >= 0) {
			return connected(RMath.prev(Math.cosh(low)), RMath.next(Math.cosh(high)));
		}

		return connected(1, RMath.next(Math.cosh(-low > high ? low : high)));
	}

	/**
	 *
	 * @return hyperbolic tangent of the interval
	 */
	public IntervalSet tanh(IntervalSet set) {
		if (set.isEmpty()) {
			return empty();
		}
		if (set.isWhole()) {
			return connected(-1, 1);
		}
		if (set.isInverted()) {
			Interval gap = invertedGap(set);
			return inverted(RMath.prev(Math.tanh(gap.getLow())),
					RMath.next(Math.tanh(gap.getHigh())));
		}
		Interval interval = connectedInterval(set);
		return connected(RMath.prev(Math.tanh(interval.getLow())),
				RMath.next(Math.tanh(interval.getHigh())));
	}
}
