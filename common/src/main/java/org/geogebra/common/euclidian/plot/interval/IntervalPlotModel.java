package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalFunctionSampler;
import org.geogebra.common.kernel.interval.IntervalTuple;
import org.geogebra.common.kernel.interval.IntervalTupleList;

/**
 * Model for Interval plotter.
 *
 * @author laszlo
 */
public class IntervalPlotModel {
	private final IntervalTuple range;
	private final IntervalFunctionSampler sampler;
	private IntervalTupleList points;
	private IntervalPath path;
	private final EuclidianView view;
	private Interval oldDomain;

	/**
	 * Constructor
	 * @param range to plot.
	 * @param sampler to retrieve function data from.
	 * @param view {@link EuclidianView}
	 */
	public IntervalPlotModel(IntervalTuple range,
			IntervalFunctionSampler sampler,
			EuclidianView view) {
		this.range = range;
		this.sampler = sampler;
		this.view = view;
	}

	public void setPath(IntervalPath path) {
		this.path = path;
	}

	/**
	 * Updates what's necessary.
	 */
	public void update() {
		updatePath();
	}

		/**
		 * Updates the entire model.
		 */
	public void updateAll() {
		updateRanges();
		updateSampler();
		updatePath();
	}

	private void updateRanges() {
		range.set(view.domain(), view.range());
		oldDomain = view.domain();
	}

	void updateSampler() {
		sampler.update(range);
		points = sampler.result();
	}

	public boolean isEmpty() {
		return points.isEmpty();
	}

	private void updatePath() {
		path.update();
	}

	/**
	 * update function domain to plot due to the visible x range.
	 */
	public void updateDomain() {
		if (view.domain().equals(oldDomain)) {
			return;
		}
		double oldMin = oldDomain.getLow();
		double oldMax = oldDomain.getHigh();
		oldDomain = view.domain();
		double min = view.domain().getLow();
		double max = view.domain().getHigh();
		if (oldMax < max && oldMin > min) {
			points = sampler.extendDomain(min, max);
		} else if (oldMax < max) {
			extendMax();
		} else if (oldMin > min) {
			extendMin();
		}
	}

	private void extendMin() {
		if (points.isEmpty()) {
			return;
		}

		IntervalTupleList newPoints = sampler.evaluateOn(view.getXmin(),
				points.get(0).x().getLow());
		points.prepend(newPoints);
		points.cutFrom(view.getXmax());
	}

	private void extendMax() {
		if (points.isEmpty()) {
			return;
		}

		IntervalTupleList newPoints = sampler.evaluateOn(
				points.get(points.count() - 1).x().getHigh(),
				view.getXmax());
		points.append(newPoints);
		points.cutTo(view.getXmin());
	}

	/**
	 * Clears the entire model.
	 */
	public void clear() {
		points.clear();
		path.reset();
	}

	GPoint getLabelPoint() {
		return path.getLabelPoint();
	}

	public IntervalTuple pointAt(int index) {
		return points.get(index);
	}

	/**
	 *
	 * @param point to check around
	 * @return if the function is ascending from point to the right.
	 */
	public boolean isAscending(IntervalTuple point) {
		if (point.index() > points.count() - 1) {
			return false;
		}

		IntervalTuple next = pointAt(point.index() + 1);
		return next != null && next.y().isGreaterThan(point.y());
	}

	/**
	 *
	 * @return the number of interval tuples aka points.
	 */
	public int pointCount() {
		return points.count();
	}
}