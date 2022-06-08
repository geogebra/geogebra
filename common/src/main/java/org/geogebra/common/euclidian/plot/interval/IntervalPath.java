package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.plot.LabelPositionCalculator;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalTuple;

public class IntervalPath {
	public static final double CLAMPED_INFINITY = Double.MAX_VALUE;
	private final IntervalPathPlotter gp;
	private final EuclidianViewBounds bounds;
	private final IntervalPlotModel model;
	private Interval lastY;
	private final DrawInterval drawInterval;
	private final DrawInvertedInterval drawInvertedInterval;

	private final LabelPositionCalculator labelPositionCalculator;
	private GPoint labelPoint = null;

	/**
	 * Constructor.
	 * @param gp {@link IntervalPathPlotter}
	 * @param bounds {@link EuclidianViewBounds}
	 * @param model {@link IntervalPlotModel}
	 */
	public IntervalPath(IntervalPathPlotter gp, EuclidianViewBounds bounds,
			IntervalPlotModel model) {
		this.gp = gp;
		this.bounds = bounds;
		this.model = model;
		labelPositionCalculator = new LabelPositionCalculator(bounds);
		lastY = new Interval();
		drawInterval = new DrawInterval(gp, bounds);
		drawInvertedInterval = new DrawInvertedInterval(gp, model, bounds);
	}

	/**
	 * Update the path based on the model.
	 */
	public synchronized void update() {
		reset();
		model.forEach(index -> drawAt(index));
	}

	private void drawAt(int index) {
		if (model.isUndefinedAt(index)) {
			noJoinForNextTuple();
		} else {
			drawTupleAt(index);
		}
		drawInterval.setJoinToPrevious(!model.isUndefinedAt(index));
	}

	private void noJoinForNextTuple() {
		lastY.setUndefined();
	}

	private void drawTupleAt(int index) {
		if (isJoinNeeded()) {
			drawTupleJoined(index);
		} else {
			drawTupleIndependent(index);
		}
	}

	private boolean isJoinNeeded() {
		return !lastY.isUndefined();
	}

	private void drawTupleJoined(int index) {
		IntervalTuple tuple = model.at(index);
		if (tuple.isInverted()) {
			drawInvertedJoined(index);
		} else if (tuple.y().isWhole()) {
			drawWhole(tuple.x());
		} else if (!lastY.isUndefined()) {
			drawNonInverted(tuple);
		}
		calculateLabelPoint(model.at(index));
	}

	private void drawNonInverted(IntervalTuple tuple) {
		if (tuple.y().hasInfinity()) {
			drawNormalInfinity(tuple);
		} else {
			drawNormalJoined(tuple);
		}
	}

	private void drawInvertedJoined(int index) {
		if (!isJoinNeeded() || model.isWholeAt(index)) {
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
		if (Double.isInfinite(y.getHigh())) {
			gp.leftToTop(bounds, x, y);
			lastY.set(0);
		} else {
			gp.leftToBottom(bounds, x, y);
			lastY.set(bounds.getHeight());
		}
	}

	private void drawWhole(Interval x) {
		drawInterval.drawWhole(x);
		noJoinForNextTuple();
	}

	private void drawTupleIndependent(int index) {
		if (model.isInvertedAt(index)) {
			drawInvertedInterval.draw(index);
		} else {
			Interval lastValue = drawInterval.drawIndependent(model.at(index));
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