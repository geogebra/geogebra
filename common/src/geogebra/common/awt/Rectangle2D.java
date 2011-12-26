package geogebra.common.awt;

import geogebra.common.awt.Rectangle;

public abstract class Rectangle2D {

	public abstract double getY();
	public abstract double getX();
	public abstract double getWidth();
	public abstract double getHeight();
	public abstract void setRect(double x,double y,double width,double height);
	public abstract void setFrame(double x,double y,double width,double height);
	public abstract boolean intersects(double minX, double minY, double lengthX,
			double lengthY);
	public abstract boolean intersects(Rectangle viewRect);

}
