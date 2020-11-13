package org.geogebra.common.kernel.interval;

/**
 * Tuple of (x, y) intervals
 *
 * @author laszlo
 */
public class IntervalTuple {
	private final Interval x;
	private final Interval y;

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
}
