package geogebra.common.awt;

import java.awt.Point;

public abstract class Line2D implements Shape{

	public abstract void setLine(double x1, double y1, double x2, double y2);
	public abstract Point2D getP1();
	public abstract Point2D getP2();
}
