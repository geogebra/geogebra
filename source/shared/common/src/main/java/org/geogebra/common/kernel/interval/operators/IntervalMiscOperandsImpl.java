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

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;

import org.geogebra.common.kernel.interval.Interval;

public class IntervalMiscOperandsImpl implements IntervalMiscOperands {

	private final IntervalNodeEvaluator evaluator;

	/**
	 *
	 * @param evaluator {@link IntervalNodeEvaluator}
	 */
	public IntervalMiscOperandsImpl(IntervalNodeEvaluator evaluator) {
		this.evaluator = evaluator;
	}

	@Override
	public Interval exp(Interval interval) {
		if (interval.isUndefined()) {
			return undefined();
		}

		if (interval.isInverted()) {
			return evaluator.computeUnaryInverted(interval, this::exp);
		}

		return new Interval(RMath.prev(Math.exp(interval.getLow())),
				RMath.next(Math.exp(interval.getHigh())));
	}

	@Override
	public Interval log(Interval interval) {
		if (interval.isInverted()) {
			return evaluator.computeUnaryInverted(interval, this::log);
		}

		if (!interval.isUndefined() && interval.getHigh() >= 0) {
			double low = interval.getLow();
			return new Interval(low <= 0 ? NEGATIVE_INFINITY : RMath.prev(Math.log(low)),
					RMath.next(Math.log(interval.getHigh())));
		}

		return undefined();
	}

	@Override
	public Interval log2(Interval interval) {
		if (!interval.isUndefined()) {
			Interval logExp2 = evaluator.log(new Interval(2, 2));
			return evaluator.divide(log(interval), logExp2);
		}

		return interval;
	}

	@Override
	public Interval log10(Interval interval) {
		if (!interval.isUndefined()) {
			Interval logExp10 = evaluator.log(new Interval(10, 10));
			return evaluator.divide(log(interval), logExp10);
		}

		return interval;
	}

	@Override
	public Interval hull(Interval interval, Interval other) {
		if (interval.isUndefined() && other.isUndefined()) {
			interval.setUndefined();
		} else if (interval.isUndefined()) {
			interval.set(other);
		} else if (!other.isUndefined()) {
			interval.set(Math.min(interval.getLow(), other.getLow()),
					Math.max(interval.getHigh(), other.getHigh()));
		}

		return interval;
	}

	@Override
	public Interval intersect(Interval interval, Interval other) {
		if (interval.isUndefined() || other.isUndefined()) {
			interval.setUndefined();
		} else {
			double low = Math.max(interval.getLow(), other.getLow());
			double high = Math.min(interval.getHigh(), other.getHigh());
			if (low <= high) {
				interval.set(low, high);
			} else {
				interval.setUndefined();
			}
		}
		return interval;
	}

	@Override
	public Interval union(Interval interval, Interval other) {
		if (!interval.isOverlap(other)) {
			return undefined();
		}
		interval.set(Math.min(interval.getLow(), other.getLow()),
				Math.max(interval.getHigh(), other.getHigh()));
		return interval;
	}

	@Override
	public Interval difference(Interval interval, Interval other) {
		if (interval.isUndefined() || other.isWhole()) {
			interval.setUndefined();
			return interval;
		}

		if (interval.isOverlap(other)) {
			if (interval.getLow() < other.getLow() && other.getHigh() < interval.getHigh()) {
				return undefined();
			}

			if ((other.getLow() <= interval.getLow() && other.getHigh() == POSITIVE_INFINITY)
				|| (other.getHigh() >= interval.getHigh() && other.getLow() == NEGATIVE_INFINITY)) {
				interval.setUndefined();
				return interval;
			}

			if (other.getLow() <= interval.getLow()) {
				interval.halfOpenLeft(other.getHigh(), interval.getHigh());
			} else {
				interval.halfOpenRight(interval.getLow(), other.getLow());
			}
		}
		return interval;
	}

	@Override
	public Interval abs(Interval interval) {
		if (interval.isUndefined() || interval.getLow() >= 0) {
			return interval;
		}

		if (interval.isInverted()) {
			Interval intervalLow = abs(interval.extractLow());
			Interval intervalHigh = abs(interval.extractHigh());
			return intervalLow.getLow() < intervalHigh.getLow()
					? intervalLow
					: intervalHigh;
		}

		if (interval.isWhole()) {
			interval.set(0, POSITIVE_INFINITY);
			return interval;
		}

		if (interval.getHigh() <= 0) {
			interval.negative();
		} else {
			interval.set(0, Math.max(-interval.getLow(), interval.getHigh()));
		}

		return interval;
	}
}
