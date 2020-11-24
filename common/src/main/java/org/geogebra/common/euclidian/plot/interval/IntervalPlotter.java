package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.GeneralPathClipped;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalFunctionSampler;
import org.geogebra.common.kernel.interval.IntervalTuple;
import org.geogebra.common.kernel.interval.IntervalTupleList;

/**
 * Function plotter based on interval arithmetic
 *
 * @author laszlo
 */
public class IntervalPlotter {
	private final EuclidianView view;
	private IntervalFunctionSampler evaluator;
	private IntervalTupleList points;
	private IntervalTuple range;
	private final GeneralPathClipped gp;
	private boolean enabled;
	private boolean moveTo;

	/**
	 * Creates a disabled plotter
	 */
	public IntervalPlotter(EuclidianView view, GeneralPathClipped gp) {
		this.view = view;
		this.gp = gp;
		this.enabled = false;
	}

	/**
	 * Enables plotter
	 */
	public void enableFor(GeoFunction function) {
		range = new IntervalTuple();
		updateRanges();
		int numberOfSamples = view.getWidth();
		evaluator = new IntervalFunctionSampler(function, range, numberOfSamples);
		updateEvaluator();
		update();
		enabled = true;
	}

	/**
	 * Update path to draw.
	 */
	public void update() {
		updatePath();
	}

	private void updateRanges() {
		range.x().set(view.getXmin(), view.getXmax());
		range.y().set(view.getYmin(), view.getYmax());
	}

	private void updatePath() {
		if (points.isEmpty()) {
			return;
		}

		gp.reset();

		Interval lastY = new Interval();
		for (IntervalTuple point: points) {
			if (point != null) {
				plotInterval(lastY, point);
			}

			moveTo = point == null;
		}
	}

	private void plotInterval(Interval lastY, IntervalTuple point) {
		Interval x = view.toScreenIntervalX(point.x());
		Interval y = view.toScreenIntervalY(point.y());
		if (y.isGreaterThan(lastY)) {
			plotHigh(x, y);
		} else {
			plotLow(x, y);
		}

		lastY.set(y);
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

	/**
	 * Updates and recomputes all.
	 */
	public void updateEvaluator() {
		updateRanges();
		evaluator.update(range);
		points = evaluator.result();
		updatePath();
	}

	/**
	 * Draws result to Graphics
	 *
	 * @param g2 {@link GGraphics2D}
	 */
	public void draw(GGraphics2D g2) {
		g2.draw(gp);
	}

	/**
	 *
	 * @return if plotter is enabled.
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Disable interval plotter.
	 */
	public void disable() {
		enabled = false;
	}
}
