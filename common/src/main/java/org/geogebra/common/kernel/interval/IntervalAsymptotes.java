package org.geogebra.common.kernel.interval;

import org.geogebra.common.kernel.arithmetic.MyDouble;

/**
 * Class to detect and fix asymptotes and cut off points
 *
 * Note: whole interval [-INFINITY, INFINITY] in already evaluated samples means,
 * that data should be fixed there.
 */
public class IntervalAsymptotes {
	private final IntervalTupleList samples;

	/**
	 * Constructor
	 * @param samples the evaluated data
	 *
	 */
	public IntervalAsymptotes(IntervalTupleList samples) {
		this.samples = samples;
	}

	/**
	 * Check samples for cut-off points and fix them.
	 */
	public void process() {
		for (int index = 1; index < samples.count() - 1; index++) {
			Interval value = value(index);
			if (value.isWhole()) {
				checkAsymptote(index);
			} else if (value.isUndefined()) {
				fixGraph(index);
			} else if (value(index).isSemiInfinite()) {
				fixSemiInfinite(index);
			}
		}
	}

	private void fixSemiInfinite(int index) {
		Interval value = value(index);
		Interval left = leftValue(index);
		if (left.isFinite()) {
			completeSemiInfinite(value, left);
		} else {
			Interval right = rightValue(index);
			if (right.isFinite()) {
				completeSemiInfinite(value, right);
			}
		}
	}

	private void completeSemiInfinite(Interval value, Interval neighbour) {
		if (value.isLowInfinite()) {
			value.setHigh(neighbour.getLow());
		} else {
			value.setLow(neighbour.getHigh());
		}
	}

	private void checkAsymptote(int index) {
		Interval leftValue = leftValue(index);
		Interval rightValue = rightValue(index);
		if (MyDouble.isFinite(leftValue.getHigh()) || MyDouble.isFinite(rightValue.getLow())) {
			samples.get(index).markAsAsymptote();
		}
	}

	private void fixGraph(int index) {

		Interval left = leftValue(index);
		Interval right = rightValue(index);

		if (isCloseTo(left, right)) {
			connect(left, value(index), right);
		}
	}

	private void connect(Interval left, Interval value, Interval right) {
		double diffLow = right.getLow() - left.getLow();
		double diffHigh = right.getHigh() - left.getHigh();
		value.set(left.getLow() + diffLow / 2, left.getHigh() + diffHigh / 2);
	}

	private boolean isCloseTo(Interval left, Interval right) {
		double diffLow = Math.abs(right.getLow() - left.getLow());
		double diffHigh = Math.abs(right.getHigh() - left.getHigh());
		if (diffLow == Double.POSITIVE_INFINITY || diffHigh == Double.POSITIVE_INFINITY) {
			return false;
		}
		return left.isFinite() && right.isFinite()
				&& (diffHigh < 2 && diffLow < 2);
	}

	private Interval value(int index) {
		IntervalTuple tuple = samples.get(index);
		return tuple != null ? tuple.y() : IntervalConstants.empty();
	}

	private Interval leftValue(int index) {
		IntervalTuple prev = prev(index);
		return prev != null ? prev.y() : IntervalConstants.empty();
	}

	private Interval rightValue(int index) {
		IntervalTuple next = next(index);
		return next != null ? next.y() : IntervalConstants.empty();
	}

	private IntervalTuple prev(int index) {
		return samples.get(index - 1);
	}

	private IntervalTuple next(int index) {
		return samples.get(index + 1);
	}
}
