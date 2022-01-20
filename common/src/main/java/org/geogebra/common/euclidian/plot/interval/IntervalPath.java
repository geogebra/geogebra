package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.plot.LabelPositionCalculator;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalTuple;
import org.geogebra.common.util.DoubleUtil;

public class IntervalPath {
	public static final double CLAMPED_INFINITY = Double.MAX_VALUE;
	private final IntervalPathPlotter gp;
	private final EuclidianViewBounds bounds;
	private final IntervalPlotModel model;
	private Interval lastY;
	private Interval lastValidY;
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
		lastValidY = new Interval();
		corrector = new PathCorrector(gp, model, bounds);
	}

	/**
	 * Update the path based on the model.
	 */
	public synchronized void update() {
		if (model.hasValidData()) {
			reset();
			plotAll();
		}
	}

	private void plotAll() {
		for (int i = 0; i < model.pointCount(); i++) {
			handleTuple(i);
			if (!lastY.isUndefined()) {
				lastValidY.set(lastY);
			}
		}
	}

	private void handleTuple(int i) {
		IntervalTuple tuple = model.pointAt(i);
		boolean shouldSkip = shouldSkip(tuple);
		if (shouldSkip) {
			skip();
		} else if (lastY.isUndefined()) {
			if (tuple.isInverted()) {
				corrector.drawInvertedInterval(i);
			} else {
				moveToFirst(i, tuple);
			}
		} else {
			drawTuple(i, tuple);
			calculateLabelPoint(tuple);
		}
		moveTo = shouldSkip;
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
		moveTo(x.getLow(), 0);
		lineTo(x.getLow(), bounds.getHeight());
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
		moveTo(x.getHigh(), y.getLow());
		lineTo(x.getHigh(), y.getHigh());
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
			moveTo(x.getLow(), y.getLow());
		} else {
			lineTo(x.getLow(), y.getLow());
		}

		lineTo(x.getHigh(), y.getHigh());
	}

	private void plotLow(Interval x, Interval y) {
		if (moveTo) {
			moveTo(x.getLow(), y.getHigh());
		} else {
			lineTo(x.getLow(), y.getHigh());
		}

		lineTo(x.getHigh(), y.getLow());
	}

	private void moveTo(double low, double high) {
		gp.moveTo(clamp(low), clamp(high));
	}

	void lineTo(double low, double high) {
		gp.lineTo(clamp(low), clamp(high));
	}

	private double clamp(double value) {
		if (DoubleUtil.isEqual(value, Double.POSITIVE_INFINITY)) {
			return CLAMPED_INFINITY;
		}

		if (DoubleUtil.isEqual(value, Double.NEGATIVE_INFINITY)) {
			return -CLAMPED_INFINITY;
		}
		return value;
	}

	public GPoint getLabelPoint() {
		return labelPoint;
	}
}