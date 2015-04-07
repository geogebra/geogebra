package org.geogebra.common.awt;

public interface GRectangle2D extends GRectangularShape {

	public double getY();

	public double getX();

	public double getWidth();

	public double getHeight();

	public void setRect(double x, double y, double width, double height);

	public void setFrame(double x, double y, double width, double height);

	public boolean intersects(double minX, double minY, double lengthX,
			double lengthY);

	public boolean intersects(GRectangle2D viewRect);

	public GRectangle2D createIntersection(GRectangle2D r);

	public double getMinX();

	public double getMaxX();

	public double getMinY();

	public double getMaxY();

	public boolean intersectsLine(double xc, double yc, double xe, double ye);

}
