package geogebra.common.awt;

import geogebra.common.awt.Rectangle;

public interface Rectangle2D extends RectangularShape{

	public double getY();
	public double getX();
	public double getWidth();
	public double getHeight();
	public void setRect(double x,double y,double width,double height);
	public void setFrame(double x,double y,double width,double height);
	public boolean intersects(double minX, double minY, double lengthX,
			double lengthY);
	public boolean intersects(Rectangle viewRect);
	public Rectangle2D createIntersection(Rectangle2D r);

}
