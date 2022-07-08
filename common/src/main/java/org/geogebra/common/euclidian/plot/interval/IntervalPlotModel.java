package org.geogebra.common.euclidian.plot.interval;

import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.plot.TupleNeighbours;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;
import org.geogebra.common.kernel.interval.samplers.IntervalFunctionSampler;

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
	private TupleNeighbours neighbours = new TupleNeighbours();

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
		updateSampledData();
		updatePath();
	}

	private void  updateRanges() {
		range.set(bounds.domain(), bounds.range());
		oldDomain = bounds.domain();
	}

	void updateSampledData() {
		sampler.update(range);
		points = sampler.result();
	}

	public boolean isEmpty() {
		return points.isEmpty();
	}

	private void updatePath() {
		if (hasValidData()) {
			path.update();
		}
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
			extendMax(oldMax);
		} else if (oldMin > min) {
			extendMin(oldMin);
		}
	}

	private void extendMin(double oldMin) {
		points.prepend(sampler.evaluate(bounds.getXmin(), getMinToExtend(oldMin)));
		points.cutFrom(bounds.getXmax());
	}

	private double getMinToExtend(double oldMin) {
		if (points.isEmpty()) {
			return oldMin;
		}

		Interval x = points.get(0).x();
		return  Math.min(oldMin, x.getLow());
	}

	private void extendMax(double oldMax) {
		points.append(sampler.evaluate(getMaxToExtend(oldMax), bounds.getXmax()));
		points.cutTo(bounds.getXmin());
	}

	private double getMaxToExtend(double oldMax) {
		if (points.isEmpty()) {
			return oldMax;
		}

		Interval x = points.get(pointCount() - 1).x();
		return  Math.max(x.getHigh(), oldMax);
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
	public IntervalTuple at(int index) {
		return points.get(index);
	}

	public boolean hasNext(int index) {
		return index < pointCount();
	}

	public boolean isInvertedAt(int index) {
		return index >= points.count() || at(index).isInverted();
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
		return index >= points.count() || at(index).y().isWhole();
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
		return points != null && countDefined() > 1;
	}

	private long countDefined() {
		return points.stream().filter(t -> !t.y().isUndefined()).count();
	}

	private boolean isValidIndex(int index) {
		return index < points.count();
	}

	public boolean nonDegenerated(int index) {
		return !isInvertedPositiveInfinity(index);
	}

	private boolean isInvertedPositiveInfinity(int index) {
		return isValidIndex(index)
				&& at(index).y().isPositiveInfinity()
				&& isInvertedAt(index);
	}

	/**
	 * Iterates through and calls the given action on every tuple in model.
	 *
	 * @param action to call on.
	 */
	public void forEach(IntConsumer action) {
		Interval xRange = IntervalPlotSettings.visisbleXRange();
		if (xRange.isUndefined()) {
			allIndexes().forEach(action);
		} else {
			allIndexes().filter(index -> xRange.contains(at(index).x()))
					.forEach(action);
		}
	}

	private IntStream allIndexes() {
		return IntStream.range(0, pointCount());
	}

	/**
	 * @param index to get the neighbours at.
	 * @return the neighbours around tuple given by index (including itself)
	 */
	public TupleNeighbours neighboursAt(int index) {
		neighbours.set(at(index - 1), at(index), at(index + 1));
		return neighbours;
	}

	/**
	 *
	 * @return the first invisible tuple on the left
	 * to draw (join if needed) the first tuple to.
	 */
	public IntervalTuple getAnchor() {
		IntervalTuple tuple = at(0);
		double length = tuple.x().getLength();
		Interval x = new Interval(tuple.x().getLow() - length, tuple.x().getLow());
		return sampler.evaluate(x).get(0);
	}
}