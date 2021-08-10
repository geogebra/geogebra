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
		for (int i = 0; i < model.getPoints().count(); i++) {
			IntervalTuple tuple = model.pointAt(i);
			boolean moveNeeded = isMoveNeeded(tuple);
			if (moveNeeded) {
				skip();
			} else {
				drawTuple(i, tuple);
			}
			moveTo = moveNeeded;
		}
	}

	private void drawTuple(int i, IntervalTuple tuple) {
		if (lastY.isEmpty()) {
			moveToCurveBegin(i, tuple);
			storeY(tuple);
		} else {
			if (tuple.isInverted()) {
				lastY = corrector.handleInvertedInterval(i);
			} else {
				plotInterval(lastY, tuple.x(), tuple.y());
				storeY(tuple);
			}
		}
	}

	private void storeY(IntervalTuple tuple) {
		lastY.set(bounds.toScreenIntervalY(tuple.y()));
	}

	private void skip() {
		lastY.setEmpty();
	}

	private boolean isMoveNeeded(IntervalTuple tuple) {
		return tuple.isEmpty()
				|| tuple.isUndefined();
	}

	private void moveToCurveBegin(int i, IntervalTuple point) {
		Interval x = bounds.toScreenIntervalX(point.x());
		Interval y = bounds.toScreenIntervalY(point.y());
		if (y.isEmpty()) {
			return;
		}
		boolean inverted = point.y().isInverted();
		if (model.isAscendingAfter(i)) {
			// -sqrt(1/x)
			gp.moveTo(x.getLow(), inverted ? bounds.getHeight() : y.getHigh());
			gp.lineTo(x.getHigh(), inverted ? bounds.getHeight() : y.getLow());
		} else {
			// sqrt(1/x)
			gp.moveTo(x.getLow(), inverted ? 0 : y.getLow());
			gp.lineTo(x.getHigh(), inverted ? 0 : y.getHigh());
		}
	}

	/**
	 * Resets path
	 */
	void reset() {
		gp.reset();
		labelPoint = null;
		lastY.setEmpty();
	}

	private void plotInterval(Interval lastY, Interval x0, Interval y0) {
		Interval x = bounds.toScreenIntervalX(x0);
		Interval y = bounds.toScreenIntervalY(y0);
		if (y.isGreaterThan(lastY)) {
			plotHigh(x, y);
		} else {
			plotLow(x, y);
		}

		if (labelPoint == null && bounds.isOnView(x0.getLow(), y0.getLow())) {
			this.labelPoint = labelPositionCalculator.calculate(x0.getLow(),
					y0.getLow());
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