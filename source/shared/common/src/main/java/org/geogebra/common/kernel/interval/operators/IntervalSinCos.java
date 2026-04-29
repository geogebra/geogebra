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

import static org.geogebra.common.kernel.interval.IntervalConstants.PI_HIGH;
import static org.geogebra.common.kernel.interval.IntervalConstants.PI_LOW;
import static org.geogebra.common.kernel.interval.IntervalConstants.PI_TWICE_LOW;
import static org.geogebra.common.kernel.interval.IntervalConstants.piTwice;
import static org.geogebra.common.kernel.interval.IntervalSetOps.connected;
import static org.geogebra.common.kernel.interval.IntervalSetOps.connectedInterval;
import static org.geogebra.common.kernel.interval.IntervalSetOps.empty;
import static org.geogebra.common.kernel.interval.IntervalSetOps.isInfiniteSingleton;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.geogebra.common.kernel.interval.IntervalSet;
import org.geogebra.common.util.DoubleUtil;

public class IntervalSinCos {

	private final IntervalNodeEvaluator evaluator;

	/**
	 *
	 * @param evaluator {@link IntervalNodeEvaluator}
	 */
	public IntervalSinCos(IntervalNodeEvaluator evaluator) {
		this.evaluator = evaluator;
	}

	IntervalSet cos(IntervalSet set) {
		if (set.isEmpty()) {
			return empty();
		}

		if (set.isInverted()) {
			return fullTrigRange();
		}

		if (set.isWhole()) {
			return fullTrigRange();
		}
		return cosConnected(set);
	}

	private IntervalSet fullTrigRange() {
		return connected(-1, 1);
	}

	private IntervalSet cosConnected(IntervalSet set) {
		if (isInfiniteSingleton(set)) {
			return fullTrigRange();
		}

		IntervalSet cache = evaluator.fmodSet(
				connectedInterval(set).getLow() < 0
						? normalizeNegativeLowerBound(set)
						: set,
				connected(piTwice()));
		Interval cacheInterval = connectedInterval(cache);

		if (cacheInterval.getWidth() >= PI_TWICE_LOW) {
			return fullTrigRange();
		}

		double low = cacheInterval.getLow();
		double high = cacheInterval.getHigh();

		if (low >= PI_HIGH) {
			IntervalSet result = cos(connected(cacheInterval.getLow() - PI_HIGH,
					cacheInterval.getHigh() - PI_LOW));
			Interval cosInterval = connectedInterval(result);
			return connected(-cosInterval.getHigh(), -cosInterval.getLow());
		}

		if (cacheInterval.isExactSingleton()) {
			double value = Math.cos(low);
			if (DoubleUtil.isEqual(value, 0, 1E-15)) {
				return connected(0, 0);
			}
		}

		double rlo = RMath.prev(Math.cos(high));
		double rhi = RMath.next(Math.cos(low));
		// it's ensured that t.lo < pi and that t.lo >= 0
		if (high <= PI_LOW) {
			// when t.hi < pi
			// [cos(t.lo), cos(t.hi)]
			return connected(rlo, rhi);
		} else if (high <= PI_TWICE_LOW) {
			// when t.hi < 2pi
			// [-1, max(cos(t.lo), cos(t.hi))]
			return connected(-1, Math.max(rlo, rhi));
		}
		// t.lo < pi and t.hi > 2pi

		return fullTrigRange();
	}

	private IntervalSet normalizeNegativeLowerBound(IntervalSet set) {
		double low = connectedInterval(set).getLow();
		double high = connectedInterval(set).getHigh();
		if (low == Double.NEGATIVE_INFINITY) {
			return connected(0, Double.POSITIVE_INFINITY);
		}

		double n = Math.ceil(-low / PI_TWICE_LOW);
		return connected(low + PI_TWICE_LOW * n, high + PI_TWICE_LOW * n);

	}

	/**
	 *
	 * @return sine of the set
	 */
	public IntervalSet sin(IntervalSet set) {
		if (set.isEmpty()) {
			return empty();
		}

		if (set.isInverted()) {
			return fullTrigRange();
		}

		if (set.isWhole()) {
			return fullTrigRange();
		}

		if (isInfiniteSingleton(set)) {
			return empty();
		}

		return cos(shiftByHalfPi(set));
	}

	private IntervalSet shiftByHalfPi(IntervalSet set) {
		return connected(
				connectedInterval(set).getLow() - IntervalConstants.PI_HALF_HIGH,
				connectedInterval(set).getHigh() - IntervalConstants.PI_HALF_LOW);
	}

}
