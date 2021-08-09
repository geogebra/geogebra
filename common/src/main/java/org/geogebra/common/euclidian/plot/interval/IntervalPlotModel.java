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
	private final EuclidianViewBounds bounds;
	private Interval oldDomain;

	/**
	 * Constructor
	 * @param range to plot.
	 * @param sampler to retrieve function data from.
	 * @param bounds {@link EuclidianView}
	 */
	public IntervalPlotModel(IntervalTuple range,
			IntervalFunctionSampler sampler,
			EuclidianViewBounds bounds) {
		this.range = range;
		this.sampler = sampler;
		this.bounds = bounds;
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
		range.set(bounds.domain(), bounds.range());
		oldDomain = bounds.domain();
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
		oldDomain = bounds.domain();
		double min = bounds.domain().getLow();
		double max = bounds.domain().getHigh();
		if (oldMax < max && oldMin > min) {
			extendDomain();
		} else if (oldMax > max && oldMin < min) {
			shrinkDomain();
		} else {
			moveDomain(oldMax - max);
		}
		updatePath();
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
		IntervalTupleList newPoints = sampler.extendMin(bounds.getXmin());
		points.prepend(newPoints);
	}

	private void shrinkMin() {
		int offscreenCount = sampler.shrinkMin(bounds.getXmin());
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
			if (points.get(i).x().getLow() > bounds.getXmin()) {
				count++;
			}
		}
		return count;
	}

	private void shrinkMax() {
		int removeCount = sampler.shrinkMax(bounds.getXmax());
		int count = 0;
		for (int i = points.count() - 1; i > points.count() - 1 - removeCount; i--) {
			if (i >= 0 && points.get(i).x().getHigh() < bounds.getXmax()) {
				count++;
			}
		}

		int toRemove = removeCount - count;
		if (toRemove > 0 && toRemove < points.count()) {
			points.removeFromTail(toRemove);
		}
	}

	private void extendMax() {
		IntervalTupleList newPoints = sampler.extendMax(bounds.getXmax());
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
	 * @param point to check around
	 * @return if the function is ascending from point to the right.
	 */
	public boolean isAscendingBefore(IntervalTuple point) {
		if (point.index() > points.count() - 1) {
			return false;
		}

		return isAscendingBefore(point.index() + 1);
	}

	/**
	 * @param index of the point to check around
	 * @return if the function is ascending from point to the left.
	 */
	public boolean isAscendingBefore(int index) {
		return points.isAscendingBefore(index);
	}

	/**
	 * @param index of the point to check around
	 * @return if the function is ascending from point to the right.
	 */
	public boolean isAscendingAfter(int index) {
		return points.isAscendingAfter(index);
	}

	/**
	 *
	 * @param index of the tuple.
	 * @return if the tuple of a given index is empty or not.
	 */
	public boolean isEmptyAt(int index) {
		return index >= points.count() || pointAt(index).isEmpty();
	}

	public boolean isInvertedAt(int index) {
		return index >= points.count() || pointAt(index).isInverted();
	}

	public boolean isUndefinedAt(int index) {
		return index >= points.count() || pointAt(index).isUndefined();
	}

	/**
	 *
	 * @return count of points in model
	 */
	public int getCount() {
		return points.count();
	}
}