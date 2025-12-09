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

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.plot.LabelPositionCalculator;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.function.IntervalTuple;

public class IntervalPath {
	public static final double CLAMPED_INFINITY = Double.MAX_VALUE;
	private final IntervalPathPlotter gp;
	private final EuclidianViewBounds bounds;
	private final QueryFunctionData data;
	private Interval lastY;
	private final DrawInterval drawInterval;
	private final DrawInvertedInterval drawInvertedInterval;

	private final LabelPositionCalculator labelPositionCalculator;
	private GPoint labelPoint = null;

	private int lastPiece = 0;

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
		lastY = new Interval();
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
		IntervalTuple tuple = data.at(index);
		if (tuple.isUndefined() || isPieceChanged(tuple)) {
			noJoinForNextTuple();
		} else {
			drawTupleAt(index);
		}
		drawInterval.setJoinToPrevious(!tuple.isUndefined()
				&& !isPieceChanged(tuple));
	}

	private void noJoinForNextTuple() {
		lastY.setUndefined();
	}

	private void drawTupleAt(int index) {
		if (isJoinNeeded(index)) {
			drawTupleJoined(index);
		} else {
			drawTupleIndependent(index);
		}
	}

	private boolean isPieceChanged(IntervalTuple tuple) {
		if (tuple.piece() != lastPiece) {
			lastPiece = tuple.piece();
			return true;
		}

		return false;
	}

	private boolean isJoinNeeded(int index) {
		return !(lastY.isUndefined() || isPieceChanged(data.at(index)));
	}

	private void drawTupleJoined(int index) {
		IntervalTuple tuple = data.at(index);
		if (tuple.isInverted()) {
			drawInvertedJoined(index);
		} else if (tuple.y().isWhole()) {
			drawWhole(tuple.x());
		} else if (!lastY.isUndefined()) {
			drawNonInverted(tuple);
		}
		calculateLabelPoint(data.at(index));
	}

	private void drawNonInverted(IntervalTuple tuple) {
		if (tuple.y().hasInfinity()) {
			drawNormalInfinity(tuple);
		} else {
			drawNormalJoined(tuple);
		}
	}

	private void drawInvertedJoined(int index) {
		if (!isJoinNeeded(index) || data.isWholeAt(index)) {
			noJoinForNextTuple();
		} else {
			lastY = drawInvertedInterval.drawJoined(index, lastY);
		}
	}

	private void drawNormalJoined(IntervalTuple tuple) {
		Interval screenY = bounds.toScreenIntervalY(tuple.y());

		drawInterval.drawJoined(lastY,
				bounds.toScreenIntervalX(tuple.x()),
				screenY);
		lastY.set(screenY);
	}

	private void drawNormalInfinity(IntervalTuple tuple) {
		Interval x = tuple.x();
		Interval y = tuple.y();
		if (bounds.range().contains(y.getLow()) && Double.isInfinite(y.getHigh())) {
			gp.leftToTop(bounds, x, y);
			lastY.set(0);
		} else if (bounds.range().contains(y.getHigh())) {
			gp.leftToBottom(bounds, x, y);
			lastY.set(bounds.getHeight());
		} else {
			lastY.setUndefined();
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
			Interval lastValue = drawInterval.drawIndependent(data.at(index));
			lastY.set(lastValue);
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
		if (labelPoint == null && bounds.isOnView(tuple.x().getLow(), tuple.y().getLow())) {
			this.labelPoint = labelPositionCalculator.calculate(tuple.x().getLow(),
					tuple.y().getLow());
		}
	}

	public GPoint getLabelPoint() {
		return labelPoint;
	}
}