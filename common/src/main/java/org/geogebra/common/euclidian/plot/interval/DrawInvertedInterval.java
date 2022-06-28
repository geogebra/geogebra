package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.geogebra.common.kernel.interval.function.IntervalTuple;

/**
 * Class to correct interval path at limits
 */
public class DrawInvertedInterval {
	private final IntervalPathPlotter gp;
	private final IntervalPlotModel model;
	private final EuclidianViewBounds bounds;
	private Interval lastY = new Interval();
	private JoinLines join;

	/**
	 * Constructor.
	 * @param gp {@link IntervalPathPlotter}
	 * @param bounds {@link EuclidianViewBounds}
	 * @param model {@link IntervalPlotModel}
	 */
	public DrawInvertedInterval(IntervalPathPlotter gp, IntervalPlotModel model,
			EuclidianViewBounds bounds) {
		this.gp = gp;
		this.model = model;
		this.bounds = bounds;
		join = new JoinLines(bounds, gp);
	}

	/**
	 * Complete inverted interval on demand.
	 *
	 * @param idx tuple index in model
	 * @param y inverted value to handle.
	 * @return the last y interval.
	 */
	public Interval drawJoined(int idx,
			Interval y) {
		lastY = y;
		if (lastY.isUndefined() || model.isWholeAt(idx)) {
			this.lastY.setUndefined();
		} else {
			draw(idx);
		}
		return this.lastY;
	}

	/**
	 * Complete inverted interval.
	 *
	 * @param index of tuple in the model
	 */
	public void draw(int index) {
		if (hasNextToJoin(index)) {
			drawSegmentsJoined(index);
		} else {
			drawSegments(index);
		}

		if (!isInvertedNextTo(index)) {
			lastY.set(IntervalConstants.undefined());
		}
	}

	private void drawSegmentsJoined(int index) {
		join.inverted(model.neighboursAt(index));
	}

	private void drawSegments(int index) {
		IntervalTuple current = model.at(index);
		drawTopSegment(current);
		drawBottomSegment(current);
	}

	private boolean hasNextToJoin(int index) {
		return model.hasNext(index)
				&& !model.isInvertedAt(index + 1);
	}

	private void drawBottomSegment(IntervalTuple current) {
		Interval y = current.y();
		if (y.getHigh() > bounds.getYmin()) {
			gp.segment(bounds, current.x().getLow(), bounds.getYmin(),
					current.x().getHigh(), y.getLow());
		}
	}

	private void drawTopSegment(IntervalTuple current) {
		Interval y = current.y();
		if (y.getLow() < bounds.getYmax()) {
			Interval x = current.x();
			gp.segment(bounds,
					x.getHigh(), bounds.getYmax(),
					x.getHigh(), y.getHigh());
		}
	}

	private boolean isInvertedNextTo(int idx) {
		return model.pointCount() > idx && model.isInvertedAt(idx + 1);
	}
}