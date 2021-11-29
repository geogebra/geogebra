package org.geogebra.common.kernel.interval;

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
			if (isWholeButNotTheNeighbours(index)) {
				samples.valueAt(index).setUndefined();
			}
		}
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
