package org.geogebra.common.kernel.interval;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalOperands.divide;

public class IntervalMiscOperandsImpl implements IntervalMiscOperands {

	@Override
	public Interval exp(Interval interval) {
		if (!interval.isUndefined()) {
			interval.set(RMath.expLow(interval.getLow()),
					RMath.expHigh(interval.getHigh()));
		}
		return interval;
	}

	@Override
	public Interval log(Interval interval) {
		if (!interval.isUndefined()) {
			if (interval.getHigh() < 0) {
				interval.setUndefined();
			} else {
				double low = interval.getLow();
				interval.set(low <= 0 ? NEGATIVE_INFINITY : RMath.logLow(low),
						RMath.logHigh(interval.getHigh()));
			}
		}
		return interval;
	}

	@Override
	public Interval log2(Interval interval) {
		if (!interval.isUndefined()) {
			Interval logExp2 = IntervalOperands.log(new Interval(2, 2));
			return divide(log(interval), logExp2);
		}

		return interval;
	}

	@Override
	public Interval log10(Interval interval) {
		if (!interval.isUndefined()) {
			Interval logExp10 = IntervalOperands.log(new Interval(10, 10));
			return divide(log(interval), logExp10);
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
