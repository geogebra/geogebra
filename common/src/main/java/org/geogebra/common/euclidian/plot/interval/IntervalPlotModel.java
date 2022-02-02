package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoFunction;
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

	private void updatePath() {
		path.update();
	}

	/**
	 * update function domain to plot due to the visible x range.
	 */
	public void updateDomain() {
		if (bounds.domain().equals(oldDomain)) {
			return;
		}
		double oldMin = oldDomain.getLow();
		double oldMax = oldDomain.getHigh();
		oldDomain = bounds.domain();
		double min = bounds.domain().getLow();
		double max = bounds.domain().getHigh();
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

		IntervalTupleList newPoints = sampler.evaluateOn(bounds.getXmin(),
				points.get(0).x().getLow());
		points.prepend(newPoints);
		points.cutFrom(bounds.getXmax());
	}

	private void extendMax() {
		if (points.isEmpty()) {
			return;
		}

		IntervalTupleList newPoints = sampler.evaluateOn(
				points.get(points.count() - 1).x().getHigh(),
				bounds.getXmax());
		points.append(newPoints);
		points.cutTo(bounds.getXmin());
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

	/**
	 *
	 * @param index to get point at
	 * @return corresponding point if index is valid, null otherwise.
	 */
	public IntervalTuple pointAt(int index) {
		return points.get(index);
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
		return index >= points.count()
				|| pointAt(index).isEmpty();
	}

	public boolean isInvertedAt(int index) {
		return index >= points.count() || pointAt(index).isInverted();
	}

	/**
	 *
	 * @return count of points in model
	 */
	public int getCount() {
		return points.count();
	}

	/**
	 *
	 * @param index of the tuple.
	 * @return if the tuple value of a given index is whole or not.
	 */
	public boolean isWholeAt(int index) {
		return index >= points.count() || pointAt(index).y().isWhole();
	}

	/**
	 *
	 * @return the number of interval tuples aka points.
	 */
	public int pointCount() {
		return points.count();
	}

	public GeoFunction getGeoFunction() {
		return sampler.getGeoFunction();
	}

	public boolean hasValidData() {
		return pointCount() > 1 && !isAllWhole();
	}

	private boolean isAllWhole() {
		return points.stream().filter(p -> p.y().isWhole()).count() == pointCount();
	}
}