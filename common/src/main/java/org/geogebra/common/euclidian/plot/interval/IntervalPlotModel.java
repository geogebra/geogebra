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

	public IntervalTupleList getPoints() {
		return points;
	}

	public void updatePath() {
		path.update();
	}

	/**
	 * update function domain to plot due to the visible x range.
	 */
	public void updateDomain() {
		double oldMin = oldDomain.getLow();
		double oldMax = oldDomain.getHigh();
		oldDomain = view.domain();
		double min = view.domain().getLow();
		double max = view.domain().getHigh();
		if (oldMax < max && oldMin > min) {
			extendDomain();
		} else if (oldMax > max && oldMin < min) {
			shrinkDomain();
		} else {
			moveDomain(oldMax - max);
		}
	}

	private void shrinkDomain() {
		if (isEmpty()) {
			return;
		}

		shrinkMin();
		shrinkMax();
	}

	private void extendDomain() {
		extendMin();
		extendMax();
	}

	private void extendMin() {
		IntervalTupleList newPoints = sampler.extendMin(view.getXmin());
		points.prepend(newPoints);
	}

	private void shrinkMin() {
		int offscreenCount = sampler.shrinkMin(view.getXmin());
		int maxPointsToRemove = Math.min(points.count(), offscreenCount);
		int keepPointCount = countVisibleFrom(maxPointsToRemove);

		int toRemove = maxPointsToRemove - keepPointCount;
		if (toRemove > 0 && toRemove < points.count()) {
			points.removeFromHead(toRemove);
		}
	}

	private int countVisibleFrom(int maxPointsToRemove) {
		int count = 0;
		for (int i = 0; i < maxPointsToRemove; i++) {
			if (points.get(i).x().getLow() > view.getXmin()) {
				count++;
			}
		}
		return count;
	}

	private void shrinkMax() {
		int removeCount = sampler.shrinkMax(view.getXmax());
		int count = 0;
		for (int i = points.count() - 1; i > points.count() - 1 - removeCount; i--) {
			if (i >= 0 && points.get(i).x().getHigh() < view.getXmax()) {
				count++;
			}
		}

		int toRemove = removeCount - count;
		if (toRemove > 0 && toRemove < points.count()) {
			points.removeFromTail(toRemove);
		}
	}

	private void extendMax() {
		IntervalTupleList newPoints = sampler.extendMax(view.getXmax());
		points.append(newPoints);
	}

	private void moveDomain(double difference) {
		if (difference < 0) {
			extendMax();
		} else {
			extendMin();
		}
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
}