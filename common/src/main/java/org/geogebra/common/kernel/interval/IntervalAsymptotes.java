package org.geogebra.common.kernel.interval;

/**
 * Class to detect and fix asymptotes and cut off points
 *
 * Note: whole interval [-INFINITY, INFINITY] in already evaluated samples means,
 * that data should be fixed there.
 */
public class IntervalAsymptotes {
	private static final int PEAK_MULTIPLIER = 4;
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
			if (isWholeButNotTheNeighbours(index) || isPeak(index)) {
				samples.valueAt(index).setUndefined();
			}
		}
	}

	private boolean isPeak(int index) {
		Interval left = value(index - 1);
		Interval value = value(index);
		Interval right = value(index + 1);
		if (left.isUndefined() || right.isUndefined()) {
			return false;
		}

		return (areFinitelyEqual(left, right)
				&& value.getLength() > PEAK_MULTIPLIER * right.getLength())
				|| isNegativeInfinityAndPeak(left, value, right)
				|| isPositiveInfinityAndPeak(left, value, right);
	}

	private boolean isPositiveInfinityAndPeak(Interval left, Interval value, Interval right) {
		return left.isPositiveInfinity() && right.isPositiveInfinity()
				&& !value.isPositiveInfinity();
	}

	private boolean isNegativeInfinityAndPeak(Interval left, Interval value, Interval right) {
		return left.isNegativeInfinity() && right.isNegativeInfinity()
				&& !value.isNegativeInfinity();
	}

	private boolean areFinitelyEqual(Interval left, Interval right) {
		return left.isFinite() && right.isFinite() && left.almostEqual(right);
	}

	private boolean isWholeButNotTheNeighbours(int index) {
		return value(index).isWhole()
				&& !leftValue(index).isWhole()
				&& !rightValue(index).isWhole();
	}

	private Interval value(int index) {
		IntervalTuple tuple = samples.get(index);
		return tuple != null ? tuple.y() : IntervalConstants.undefined();
	}

	private Interval leftValue(int index) {
		IntervalTuple prev = prev(index);
		return prev != null ? prev.y() : IntervalConstants.undefined();
	}

	private Interval rightValue(int index) {
		IntervalTuple next = next(index);
		return next != null ? next.y() : IntervalConstants.undefined();
	}

	private IntervalTuple prev(int index) {
		return samples.get(index - 1);
	}

	private IntervalTuple next(int index) {
		return samples.get(index + 1);
	}
}
