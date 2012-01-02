package geogebra.common.awt;

import geogebra.common.awt.Point2D;

public abstract class Arc2D implements RectangularShape{

	public final static int OPEN = 0;
	public final static int PIE = 2;

	public abstract void setArc(double d, double e, double f, double g, double degrees,
			double degrees2, int open2);
	public abstract Point2D getStartPoint();
	public abstract Point2D getEndPoint();

}
