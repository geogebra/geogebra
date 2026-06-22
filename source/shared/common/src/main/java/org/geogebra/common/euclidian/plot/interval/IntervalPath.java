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

import static org.geogebra.common.kernel.interval.IntervalSetOps.connected;
import static org.geogebra.common.kernel.interval.IntervalSetOps.connectedInterval;
import static org.geogebra.common.kernel.interval.IntervalSetOps.empty;
import static org.geogebra.common.kernel.interval.IntervalSetOps.invertedGap;
import static org.geogebra.common.kernel.interval.IntervalSetOps.zero;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.plot.LabelPositionCalculator;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalSet;
import org.geogebra.common.kernel.interval.IntervalSetOps;
import org.geogebra.common.kernel.interval.function.IntervalTuple;

public class IntervalPath {
	public static final double CLAMPED_INFINITY = Double.MAX_VALUE;
	private final IntervalPathPlotter gp;
	private final EuclidianViewBounds bounds;
	private final QueryFunctionData data;
	private IntervalSet lastY;
	private final DrawInterval drawInterval;
	private final DrawInvertedInterval drawInvertedInterval;

	private final LabelPositionCalculator labelPositionCalculator;
	private GPoint labelPoint = null;

	/**
	 * Constructor.
	 * @param gp {@link IntervalPathPlotter}
	 * @param bounds {@link EuclidianViewBounds}
	 * @param data {@link IntervalFunctionModelImpl}
	 */
	public IntervalPath(IntervalPathPlotter gp, EuclidianViewBounds bounds,
			QueryFunctionData data) {
		this.gp = gp;
		this.bounds = bounds;
		this.data = data;
		labelPositionCalculator = new LabelPositionCalculator(bounds);
		lastY = empty();
		drawInterval = new DrawInterval(gp, bounds);
		drawInvertedInterval = new DrawInvertedInterval(gp, data, bounds);
	}

	/**
	 * Update the path based on the model.
	 */
	public synchronized void update() {
		reset();
		data.forEach(this::drawAt);
	}

	private void drawAt(int index) {
		IntervalSet ySet = data.yTopologyAt(index);
		if (shouldBreakPath(ySet)) {
			noJoinForNextTuple();
		} else {
			drawTupleAt(index);
		}
		drawInterval.setJoinToPrevious(shouldDraw(ySet));
	}

	private boolean shouldBreakPath(IntervalSet ySet) {
		return ySet.isEmpty() || ySet.isOverflow();
	}

	private boolean shouldDraw(IntervalSet ySet) {
		return !shouldBreakPath(ySet);
	}

	private void noJoinForNextTuple() {
		lastY = empty();
	}

	private void drawTupleAt(int index) {
		if (isJoinNeeded()) {
			drawTupleJoined(index);
		} else {
			drawTupleIndependent(index);
		}
	}

	private boolean isJoinNeeded() {
		return !(lastY.isEmpty());
	}

	private void drawTupleJoined(int index) {
		IntervalTuple tuple = data.at(index);
		IntervalSet yTopology = data.yTopologyAt(index);
		if (yTopology.isEmpty()) {
			noJoinForNextTuple();
		} else if (yTopology.isInverted()) {
			drawInvertedJoined(index);
		} else if (yTopology.isWhole()) {
			drawWhole(connectedInterval(tuple.xSet()));
		} else if (!lastY.isEmpty()) {
			drawNonInverted(tuple);
		}
		calculateLabelPoint(data.at(index));
	}

	private void drawNonInverted(IntervalTuple tuple) {
		if (IntervalSetOps.hasInfinity(tuple.ySet())) {
			drawNormalInfinity(tuple);
		} else {
			drawNormalJoined(tuple);
		}
	}

	private void drawInvertedJoined(int index) {
		if (!isJoinNeeded()) {
			noJoinForNextTuple();
		} else {
			lastY = drawInvertedInterval.drawJoined(index, lastY);
		}
	}

	private void drawNormalJoined(IntervalTuple tuple) {
		Interval screenY = bounds.toScreenIntervalY(connectedInterval(tuple.ySet()));
		drawInterval.drawJoined(lastY, bounds.toScreenIntervalX(connectedInterval(tuple.xSet())),
				screenY);
		lastY = connected(screenY);
	}

	private void drawNormalInfinity(IntervalTuple tuple) {
		Interval x = connectedInterval(tuple.xSet());
		Interval y = connectedInterval(tuple.ySet());
		if (bounds.range().contains(y.getLow()) && Double.isInfinite(y.getHigh())) {
			gp.leftToTop(bounds, x, y);
			lastY = zero();
		} else if (bounds.range().contains(y.getHigh())) {
			gp.leftToBottom(bounds, x, y);
			int height = bounds.getHeight();
			lastY = connected(height, height);
		} else {
			lastY = empty();
		}
	}

	private void drawWhole(Interval x) {
		drawInterval.drawWhole(x);
		noJoinForNextTuple();
	}

	private void drawTupleIndependent(int index) {
		if (data.isInvertedAt(index)) {
			drawInvertedInterval.draw(index);
		} else {
			lastY = drawInterval.drawIndependent(data.at(index));
		}
	}

	/**
	 * Resets path
	 */
	void reset() {
		gp.reset();
		labelPoint = null;
		noJoinForNextTuple();
	}

	private void calculateLabelPoint(IntervalTuple tuple) {
		if (labelPoint != null) {
			return;
		}

		Interval x = connectedInterval(tuple.xSet());
		IntervalSet ySet = tuple.ySet();
		if (ySet.isWhole() || ySet.isEmpty()) {
			this.labelPoint = labelPositionCalculator.calculate(0, 0);
			return;
		}

		if (!ySet.isInverted() && !ySet.isConnected()) {
			return;
		}

		Interval y = ySet.isInverted() ? invertedGap(ySet) : connectedInterval(ySet);
		if (bounds.isOnView(x.getLow(), y.getLow())) {
			this.labelPoint = labelPositionCalculator.calculate(x.getLow(), y.getLow());
		}
	}

	public GPoint getLabelPoint() {
		return labelPoint;
	}
}
