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
import static org.geogebra.common.kernel.interval.IntervalConstants.pi;
import static org.geogebra.common.kernel.interval.IntervalConstants.piTwice;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;

public class IntervalSinCos {

	private final IntervalNodeEvaluator evaluator;

	private final Interval cache = new Interval();

	/**
	 *
	 * @param evaluator {@link IntervalNodeEvaluator}
	 */
	public IntervalSinCos(IntervalNodeEvaluator evaluator) {
		this.evaluator = evaluator;
	}

	Interval cos(Interval interval) {
		if (interval.isInverted()) {
			Interval result = evaluator.unionInvertedResults(cos(interval.extractLow()),
					cos(interval.extractHigh()));
			return result.isUndefined() ? defaultInterval() : result;
		}
		return cosNonInverted(interval);
	}

	Interval cosNonInverted(Interval interval) {
		if (interval.isUndefined()) {
			return undefined();
		}

		if (interval.isUndefined() || interval.isInfiniteSingleton()) {
			return defaultInterval();
		}

		initCache(interval);

		evaluator.fmod(cache, piTwice());
		if (cache.getWidth() >= PI_TWICE_LOW) {
			return defaultInterval();
		}

		if (cache.getLow() >= PI_HIGH) {
			Interval result = cos(cache.subtract(pi()));
			result.negative();
			return result;
		}

		double low = cache.getLow();
		double high = cache.getHigh();
		double rlo = RMath.prev(Math.cos(high));
		double rhi = RMath.next(Math.cos(low));
		// it's ensured that t.lo < pi and that t.lo >= 0
		if (high <= PI_LOW) {
			// when t.hi < pi
			// [cos(t.lo), cos(t.hi)]
			return new Interval(rlo, rhi);
		} else if (high <= PI_TWICE_LOW) {
			// when t.hi < 2pi
			// [-1, max(cos(t.lo), cos(t.hi))]
			return new Interval(-1, Math.max(rlo, rhi));
		}
		// t.lo < pi and t.hi > 2pi

		return defaultInterval();
	}

	private Interval defaultInterval() {
		return new Interval(-1, 1);
	}

	private void initCache(Interval interval) {
		if (interval.getLow() < 0) {
			negativeCache(interval);
		} else {
			cache.set(interval);
		}
	}

	private void negativeCache(Interval interval) {
		double low = interval.getLow();
		double high = interval.getHigh();
		if (low == Double.NEGATIVE_INFINITY) {
			cache.set(0, Double.POSITIVE_INFINITY);
		} else {
			double n = Math.ceil(-low / PI_TWICE_LOW);
			cache.set(low + PI_TWICE_LOW * n,
					high + PI_TWICE_LOW * n);
		}
	}

	/**
	 *
	 * @return sine of the interval
	 */
	public Interval sin(Interval interval) {
		if (interval.isUndefined()) {
			return undefined();
		}

		if (interval.isInverted()) {
			return defaultInterval();
		} else if (interval.isUndefined() || interval.isInfiniteSingleton()) {
			return undefined();
		}

		return cos(new Interval(interval).subtract(IntervalConstants.piHalf()));
	}

}
