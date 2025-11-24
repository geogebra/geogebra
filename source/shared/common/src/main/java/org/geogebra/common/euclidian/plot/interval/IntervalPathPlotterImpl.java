package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.GeneralPathClipped;
import org.geogebra.common.kernel.interval.Interval;

public class IntervalPathPlotterImpl implements IntervalPathPlotter {
	public static final int PLOT_MARGIN = 1;
	private final GeneralPathClipped gp;

	/**
	 * @param gp path
	 */
	public IntervalPathPlotterImpl(GeneralPathClipped gp) {
		this.gp = gp;
	}

	@Override
	public void reset() {
		gp.reset();
	}

	@Override
	public void moveTo(double x, double y) {
		gp.moveTo(x, y);
	}

	@Override
	public void lineTo(double x, double y) {
		gp.lineTo(x, y);
	}

	@Override
	public void segment(double x1, double y1, double x2, double y2) {
		gp.moveTo(x1, y1);
		gp.lineTo(x2, y2);
	}

	@Override
	public void segment(EuclidianViewBounds bounds, double x1, double y1, double x2, double y2) {
		segmentClipped(bounds.toScreenCoordXd(x1),
				bounds.toScreenCoordYd(y1),
				bounds.toScreenCoordXd(x2),
				bounds.toScreenCoordYd(y2),
				bounds.getHeight());
	}

	private void segmentClipped(double screenX1, double screenY1,
			double screenX2, double screenY2, int height) {
		if (isSegmentOffscreen(screenY1, screenY2, height)) {
			return;
		}

		segment(screenX1, screenY1, screenX2, screenY2);
	}

	static boolean isSegmentOffscreen(double screenY1, double screenY2, int height) {
		if (screenY1 < PLOT_MARGIN && screenY2 < PLOT_MARGIN) {
			return true;
		}

		return screenY1 > height - PLOT_MARGIN && screenY2 > height - PLOT_MARGIN;
	}

	@Override
	public void draw(GGraphics2D g2) {
		gp.draw(g2);
	}

	@Override
	public void leftToTop(EuclidianViewBounds bounds, Interval x, Interval y) {
		segment(bounds,
				x.getLow(),
				y.getLow(),
				x.middle(),
				bounds.getYmax());

	}

	@Override
	public void leftToBottom(EuclidianViewBounds bounds, Interval x, Interval y) {
		segment(bounds, x.getLow(), y.getHigh(),
				x.middle(),
				bounds.getYmin());
	}
}
