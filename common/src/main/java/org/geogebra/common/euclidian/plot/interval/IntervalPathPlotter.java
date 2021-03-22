package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.awt.GGraphics2D;

public interface IntervalPathPlotter {
	void reset();

	void moveTo(double x, double y);

	void lineTo(double x, double y);

	void draw(GGraphics2D g2);
}
