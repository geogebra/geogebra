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

package org.geogebra.common.euclidian.plot.interval;

import static org.geogebra.common.kernel.interval.IntervalSetOps.connectedInterval;
import static org.geogebra.common.kernel.interval.IntervalSetOps.empty;
import static org.geogebra.common.kernel.interval.IntervalSetOps.invertedGap;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalSet;
import org.geogebra.common.kernel.interval.function.IntervalTuple;

/**
 * Class to correct interval path at limits
 */
public class DrawInvertedInterval {
	private final IntervalPathPlotter gp;
	private final QueryFunctionData data;
	private final EuclidianViewBounds bounds;
	private IntervalSet lastYSet = empty();
	private final JoinLines join;

	/**
	 * Constructor.
	 * @param gp {@link IntervalPathPlotter}
	 * @param bounds {@link EuclidianViewBounds}
	 * @param data {@link IntervalFunctionModelImpl}
	 */
	public DrawInvertedInterval(IntervalPathPlotter gp, QueryFunctionData data,
			EuclidianViewBounds bounds) {
		this.gp = gp;
		this.data = data;
		this.bounds = bounds;
		join = new JoinLines(bounds, gp);
	}

	/**
	 * Complete inverted interval on demand.
	 *
	 * @param idx tuple index in model
	 * @param ySet inverted value to handle.
	 * @return the last y interval.
	 */
	public IntervalSet drawJoined(int idx,
			IntervalSet ySet) {
		lastYSet = ySet;
		IntervalSet yTopology = data.yTopologyAt(idx);
		if (lastYSet.isEmpty() || yTopology.isWhole()) {
			lastYSet = empty();
		} else {
			draw(idx);
		}
		return this.lastYSet;
	}

	/**
	 * Complete inverted interval.
	 *
	 * @param index of tuple in the model
	 */
	public void draw(int index) {
		if (hasNextToJoin(index)) {
			drawSegmentsJoined(index);
		} else {
			drawSegments(index);
		}

		if (!isInvertedNextTo(index)) {
			lastYSet = empty();
		}
	}

	private void drawSegmentsJoined(int index) {
		join.inverted(data.neighboursAt(index));
	}

	private void drawSegments(int index) {
		IntervalTuple current = data.at(index);
		Interval x = connectedInterval(current.xSet());
		Interval y = invertedGap(current.ySet());
		drawTopSegment(x, y);
		drawBottomSegment(x, y);
	}

	private boolean hasNextToJoin(int index) {
		return data.hasNext(index)
				&& !data.yTopologyAt(index + 1).isInverted();
	}

	private void drawBottomSegment(Interval x, Interval y) {
		if (y.getHigh() > bounds.getYmin()) {
			gp.segment(bounds, x.getLow(), bounds.getYmin(),
					x.getHigh(), y.getLow());
		}
	}

	private void drawTopSegment(Interval x, Interval y) {
		if (y.getLow() < bounds.getYmax()) {
			gp.segment(bounds,
					x.getHigh(), bounds.getYmax(),
					x.getHigh(), y.getHigh());
		}
	}

	private boolean isInvertedNextTo(int idx) {
		return data.hasNext(idx) && data.yTopologyAt(idx + 1).isInverted();
	}
}
