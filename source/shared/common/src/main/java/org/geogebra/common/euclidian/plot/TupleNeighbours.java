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

package org.geogebra.common.euclidian.plot;

import static org.geogebra.common.kernel.interval.IntervalSetOps.connectedInterval;
import static org.geogebra.common.kernel.interval.IntervalSetOps.empty;
import static org.geogebra.common.kernel.interval.IntervalSetOps.invertedGap;

import org.geogebra.common.kernel.interval.IntervalSet;
import org.geogebra.common.kernel.interval.function.IntervalTuple;

public class TupleNeighbours {
	private IntervalTuple left;
	private IntervalTuple current;
	private IntervalTuple right;

	/**
	 * Constructor
	 */
	public TupleNeighbours() {
		// unset
	}

	/**
	 * @param left neighbour tuple
	 * @param current neighbour tuple
	 * @param right neighbour tuple
	 */
	public TupleNeighbours(IntervalTuple left, IntervalTuple current, IntervalTuple right) {
		set(left, current, right);
	}

	/**
	 * @param left neighbour tuple
	 * @param current neighbour tuple
	 * @param right neighbour tuple
	 */
	public void set(IntervalTuple left, IntervalTuple current, IntervalTuple right) {
		this.left = left;
		this.current = current;
		this.right = right;
	}

	/**
	 * @return whether left exists and is defined
	 */
	public boolean hasLeft() {
		return left != null && !left.isEmpty();
	}

	/**
	 * @return whether right exists and is defined
	 */
	public boolean hasRight() {
		return right != null && !right.isEmpty();
	}

	/**
	 * @return left x low
	 */
	public double leftXLow() {
		return lowX(left.xSet());
	}

	private double lowX(IntervalSet set) {
		return connectedInterval(set).getLow();
	}

	private double payloadLow(IntervalSet set) {
		if (set.isWhole()) {
			return Double.NEGATIVE_INFINITY;
		}
		return set.isInverted() ? invertedGap(set).getLow() : connectedInterval(set).getLow();
	}

	/**
	 * @return left x high
	 */
	public double leftXHigh() {
		return highX(left.xSet());
	}

	private double highX(IntervalSet set) {
		return connectedInterval(set).getHigh();
	}

	private double payloadHigh(IntervalSet set) {
		if (set.isWhole()) {
			return Double.POSITIVE_INFINITY;
		}
		return set.isInverted() ? invertedGap(set).getHigh() : connectedInterval(set).getHigh();
	}

	/**
	 * @return left y low
	 */
	public double leftYLow() {
		return payloadLow(left.ySet());
	}

	/**
	 * @return left y high
	 */
	public double leftYHigh() {
		return payloadHigh(left.ySet());
	}

	/**
	 * @return current x low
	 */
	public double currentXLow() {
		return lowX(current.xSet());
	}

	/**
	 * @return current x high
	 */
	public double currentXHigh() {
		return highX(current.xSet());
	}

	/**
	 * @return current y low
	 */
	public double currentYLow() {
		return payloadLow(current.ySet());
	}

	/**
	 * @return current y high
	 */
	public double currentYHigh() {
		return payloadHigh(current.ySet());
	}

	/**
	 * @return right x low
	 */
	public double rightXLow() {
		return lowX(right.xSet());
	}

	/**
	 * @return right x high
	 */
	public double rightXHigh() {
		return highX(right.xSet());
	}

	/**
	 * @return right y low
	 */
	public double rightYLow() {
		return payloadLow(right.ySet());
	}

	/**
	 * @return right y high
	 */
	public double rightYHigh() {
		return payloadHigh(right.ySet());
	}

	/**
	 * @return left
	 */
	public IntervalTuple left() {
		return left;
	}

	/**
	 * @return topology of left y interval
	 */
	public IntervalSet leftTopology() {
		return hasLeft() ? left.ySet() : empty();
	}

	/**
	 * @return current
	 */
	public IntervalTuple current() {
		return current;
	}

	/**
	 * @return topology of current y interval
	 */
	public IntervalSet currentTopology() {
		return current.ySet();
	}

	/**
	 * @return right
	 */
	public IntervalTuple right() {
		return right;
	}

	/**
	 * @return topology of right y interval
	 */
	public IntervalSet rightTopology() {
		return hasRight() ? right.ySet() : empty();
	}

	@Override
	public String toString() {
		return "TupleNeighbours(\n"
				+ left + ", \n"
				+ current + ", \n"
				+ right + ");";
	}

	boolean isLeftInverted() {
		return leftTopology().isInverted();
	}

	boolean isRightInverted() {
		return rightTopology().isInverted();
	}

	public boolean isLeftWhole() {
		return leftTopology().isWhole();
	}

	public boolean isRightWhole() {
		return rightTopology().isWhole();
	}

	boolean isLeftEmpty() {
		return leftTopology().isEmpty();
	}

	boolean isRightEmpty() {
		return rightTopology().isEmpty();
	}
}
