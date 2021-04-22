package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.GeneralPathClipped;

public class IntervalPathPlotterImpl implements IntervalPathPlotter {
	private final GeneralPathClipped gp;

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
	public void draw(GGraphics2D g2) {
		g2.draw(gp);
	}
}
