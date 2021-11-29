package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.plot.LabelPositionCalculator;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalTuple;

public class IntervalPath {
	private final IntervalPathPlotter gp;
	private final EuclidianViewBounds bounds;
	private final IntervalPlotModel model;
	private Interval lastY;
	private boolean moveTo;
	private final PathCorrector corrector;

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
		corrector = new PathCorrector(gp, model, bounds);
	}

	/**
	 * Update the path based on the model.
	 */
	public synchronized void update() {
		if (model.getCount() > 1) {
			reset();
			plotAll();
		}
	}

	private void plotAll() {
		for (int i = 0; i < model.pointCount(); i++) {
			IntervalTuple tuple = model.pointAt(i);
			boolean shouldSkip = shouldSkip(tuple);
			if (shouldSkip) {
				skip();
			} else if (lastY.isUndefined()) {
				moveToFirst(i, tuple);
			} else {
				drawTuple(i, tuple);
				calculateLabelPoint(tuple);
			}
			moveTo = shouldSkip;
		}
	}

	private void drawTuple(int i, IntervalTuple tuple) {
		if (tuple.isInverted()) {
			lastY = corrector.handleInvertedInterval(i, lastY);
		} else if (tuple.y().isWhole()) {
			drawWhole(tuple);
		} else {
			plotInterval(lastY, tuple);
			storeY(tuple);
		}
	}

	private void drawWhole(IntervalTuple tuple) {
		Interval x = bounds.toScreenIntervalX(tuple.x());
		gp.moveTo(x.getLow(), 0);
		gp.lineTo(x.getLow(), bounds.getHeight());
		skip();
	}

	private void moveToFirst(int i, IntervalTuple point) {
		Interval x = bounds.toScreenIntervalX(point.x());
		Interval y = bounds.toScreenIntervalY(point.y());

		if (y.isUndefined()) {
			lastY.setUndefined();
			return;
		}

		if (point.y().isInverted() && !model.isInvertedAt(i + 1)) {
			lastY.set(corrector.beginFromInfinity(i, x, y));
		} else {
			line(x, y);
		}
	}

	private void storeY(IntervalTuple tuple) {
		lastY.set(bounds.toScreenIntervalY(tuple.y()));
	}

	private void skip() {
		lastY.setUndefined();
	}

	private boolean shouldSkip(IntervalTuple tuple) {
		return tuple.isEmpty() || tuple.y().isPositiveInfinity();
	}

	private void line(Interval x, Interval y) {
		gp.moveTo(x.getHigh(), y.getLow());
		gp.lineTo(x.getHigh(), y.getHigh());
		lastY.set(y);
	}

	/**
	 * Resets path
	 */
	void reset() {
		gp.reset();
		labelPoint = null;
		lastY.setUndefined();
	}

	private void plotInterval(Interval lastY, IntervalTuple tuple) {
		Interval x = bounds.toScreenIntervalX(tuple.x());
		Interval y = bounds.toScreenIntervalY(tuple.y());
		if (y.isGreaterThan(lastY)) {
			plotHigh(x, y);
		} else {
			plotLow(x, y);
		}
	}

	private void calculateLabelPoint(IntervalTuple tuple) {
		if (labelPoint == null && bounds.isOnView(tuple.x().getLow(), tuple.y().getLow())) {
			this.labelPoint = labelPositionCalculator.calculate(tuple.x().getLow(),
					tuple.y().getLow());
		}
	}

	private void plotHigh(Interval x, Interval y) {
		if (moveTo) {
			gp.moveTo(x.getLow(), y.getLow());
		} else {
			lineTo(x.getLow(), y.getLow());
		}

		lineTo(x.getHigh(), y.getHigh());
	}

	private void plotLow(Interval x, Interval y) {
		if (moveTo) {
			gp.moveTo(x.getLow(), y.getHigh());
		} else {
			lineTo(x.getLow(), y.getHigh());
		}

		lineTo(x.getHigh(), y.getLow());
	}

	private void lineTo(double low, double high) {
		gp.lineTo(low, high);
	}

	public GPoint getLabelPoint() {
		return labelPoint;
	}
}