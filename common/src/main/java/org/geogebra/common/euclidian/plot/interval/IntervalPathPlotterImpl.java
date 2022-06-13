package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.GeneralPathClipped;
import org.geogebra.common.kernel.interval.Interval;

public class IntervalPathPlotterImpl implements IntervalPathPlotter {
	public static final int PLOT_MARGIN = 5;
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
		segment(bounds.toScreenCoordXd(x1),
				bounds.toScreenCoordYd(y1),
				bounds.toScreenCoordXd(x2),
				bounds.toScreenCoordYd(y2));
	}

	@Override
	public void draw(GGraphics2D g2) {
		g2.draw(gp);
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
