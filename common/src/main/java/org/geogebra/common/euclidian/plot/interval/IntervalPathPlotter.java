package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.kernel.interval.Interval;

public interface IntervalPathPlotter {
	void reset();

	void moveTo(double x, double y);

	void lineTo(double x, double y);

	void segment(double x1, double y1, double x2, double y2);

	void segment(EuclidianViewBounds bounds, double x1, double y1, double x2, double y2);

	void draw(GGraphics2D g2);

	void leftToTop(EuclidianViewBounds bounds, Interval x, Interval y);

	void leftToBottom(EuclidianViewBounds bounds, Interval x, Interval y);
}
