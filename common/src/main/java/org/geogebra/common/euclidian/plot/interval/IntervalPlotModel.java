package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.plot.LabelPositionCalculator;
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
	private final LabelPositionCalculator labelPositionCalculator;
	private IntervalTupleList points;
	private IntervalPath path;
	private final EuclidianView view;
	private Interval oldDomain;
	private GPoint labelPoint;

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
		labelPositionCalculator = new LabelPositionCalculator(view);
	}

	public void setPath(IntervalPath path) {
		this.path = path;
	}

	/**
	 * Updates what's necessary.
	 */
	public void update() {
		updatePath();
		updateLabelPosition();
	}

		/**
		 * Updates the entire model.
		 */
	public void updateAll() {
		updateRanges();
		updateSampler();
		updatePath();
		logDomain();
		updateLabelPosition();
	}

	private void updateLabelPosition() {
		if (points.isEmpty()) {
			return;
		}
		IntervalTuple firstPoint = points.get(0);
		if (firstPoint == null) {
			return;
		}
		labelPoint = labelPositionCalculator.calculate(firstPoint.x().getHigh(),
				firstPoint.y().getLow());
	}

	private void updateRanges() {
		range.set(view.domain(), view.range());
		oldDomain = view.domain();
		logDomain();
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

		logDomain();

	}

	private void logDomain() {
		if (points == null) {
			return;
		}
//		Log.debug("points: " + points.count());
	}

	private void shrinkDomain() {
		shrinkMin();
		shrinkMax();
	}

	private void extendDomain() {
		extendMin();
		extendMax();
	}

	private int extendMin() {
		IntervalTupleList newPoints = sampler.extendMin(view.getXmin());
		points.prepend(newPoints);
		return newPoints.count();
	}

	private void shrinkMin() {
		int removeCount = sampler.shrinkMin(view.getXmin());
		points.removeFromHead(removeCount);
	}

	private void shrinkMax() {
		int removeCount = sampler.shrinkMax(view.getXmax());
		points.removeFromTail(removeCount);
	}

	private int extendMax() {
		IntervalTupleList newPoints = sampler.extendMax(view.getXmax());
		points.append(newPoints);
		return newPoints.count();
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
		return labelPoint;
	}
}