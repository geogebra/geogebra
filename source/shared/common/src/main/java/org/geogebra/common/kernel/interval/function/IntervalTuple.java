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
		return false;
	}

	@Override
	public int hashCode() {
		return DoubleUtil.hashCode(x.getLength() + y.getLength());
	}

	@Override
	public String toString() {
		return "{x: " + x().toShortString() + ": " + y().toShortString() + "}";
	}

	/**
	 * @return piece
	 */
	public int piece() {
		return piece;
	}
}
