package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.geogebra.common.kernel.interval.function.IntervalTuple;

/**
 * Class to correct interval path at limits
 */
public class DrawInvertedInterval {
	private final IntervalPathPlotter gp;
	private final QueryFunctionData data;
	private final EuclidianViewBounds bounds;
	private Interval lastY = new Interval();
	private final JoinLines join;

	/**
	 * Constructor.
	 * @param gp {@link IntervalPathPlotter}
	 * @param bounds {@link EuclidianViewBounds}
	 * @param data {@link IntervalFunctionModelImpl}
	 */
	public DrawInvertedInterval(IntervalPathPlotter gp, QueryFunctionData data,
			EuclidianViewBounds bounds) {
		this.gp = gp;
		this.data = data;
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
		if (lastY.isUndefined() || data.isWholeAt(idx)) {
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
		join.inverted(data.neighboursAt(index));
	}

	private void drawSegments(int index) {
		IntervalTuple current = data.at(index);
		drawTopSegment(current);
		drawBottomSegment(current);
	}

	private boolean hasNextToJoin(int index) {
		return data.hasNext(index)
				&& !data.isInvertedAt(index + 1);
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
		return data.getCount() > idx && data.isInvertedAt(idx + 1);
	}
}