package org.geogebra.common.kernel.interval.function;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.util.DoubleUtil;

/**
 * Tuple of (x, y) intervals
 *
 * @author laszlo
 */
public class IntervalTuple {
	private final Interval x;
	private final Interval y;
	private int piece = 0;

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
	 *
	 * @param x interval of x coordinates.
	 * @param y interval of y coordinates.
	 * @param piece where the tuple belongs (for different If() command parts)
	 */
	public IntervalTuple(Interval x, Interval y, int piece) {
		this(x, y);
		this.piece = piece;
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

	/**
	 *
	 * @return if tuple is an empty one
	 */
	public boolean isUndefined() {
		return y.isUndefined();
	}

	/**
	 *
	 * @return if tuple y value is inverted or not
	 */
	public boolean isInverted() {
		return y.isInverted();
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

	public void setPiece(int piece) {
		this.piece = piece;
	}

	public int piece() {
		return piece;
	}
}
