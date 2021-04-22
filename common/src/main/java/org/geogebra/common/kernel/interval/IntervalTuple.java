package org.geogebra.common.kernel.interval;

import org.geogebra.common.util.DoubleUtil;

/**
 * Tuple of (x, y) intervals
 *
 * @author laszlo
 */
public class IntervalTuple {
	private final Interval x;
	private final Interval y;
	private int index = -1;
	private boolean asymptote = false;

	/**
	 *
	 * @param x interval of x coordinates.
	 * @param y interval of y coordinates.
	 */
	public IntervalTuple(Interval x, Interval y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Sets the tuple as (x, y) interval pair.
	 *
	 * @param x interval of x coordinates
	 * @param y interval of y coordinates
	 */
	public void set(Interval x, Interval y) {
		this.x.set(x);
		this.y.set(y);
	}

	/**
	 * Constructs an empty tuple.
	 */
	public IntervalTuple() {
		this(new Interval(), new Interval());
	}

	/**
	 *
	 * @return the interval of x coordinates
	 */
	public Interval x() {
		return x;
	}

	/**
	 *
	 * @return the interval of y coordinates
	 */
	public Interval y() {
		return y;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IntervalTuple) {
			IntervalTuple other = (IntervalTuple) obj;
			return x.equals(other.x) && y.equals(other.y);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return DoubleUtil.hashCode(x.getLength() + y.getLength());
	}

	@Override
	public String toString() {
		return "{x: " + x().toShortString() + ": " + y().toShortString() + "}";
	}

	public boolean isYNaN() {
		return y == null || y.isEmpty();
	}

	/**
	 *
	 * @param previous tuple
	 * @return if this tuple is right after the previous one (no gap)
	 */
	public boolean follows(IntervalTuple previous) {
		return hasValue() && previous.hasValue()
			&& DoubleUtil.isEqual(x.getLow(), previous.x.getHigh());
	}

	private boolean hasValue() {
		return !y.isEmpty();
	}

	/**
	 *
	 * @return if tuple is an empty one
	 */
	public boolean isEmpty() {
		return y.isEmpty();
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int index() {
		return index;
	}

	public boolean isUndefined() {
		return y.isUndefined();
	}

	public boolean isAsymptote() {
		return asymptote;
	}

	public void markAsAsymptote() {
		asymptote = true;
	}

	/**
	 *
	 * @param tuple to check
	 * @return if tuple is contained by this range
	 */
	public boolean contains(IntervalTuple tuple) {
		return x.contains(tuple.x) && y.contains(tuple.y);
	}
}
