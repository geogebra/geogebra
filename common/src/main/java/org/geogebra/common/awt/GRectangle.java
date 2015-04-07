package org.geogebra.common.awt;

public interface GRectangle extends GRectangle2D {

	public double getY();

	public double getX();

	public double getWidth();

	public double getHeight();

	public void setBounds(int xLabel, int i, int width, int height);

	public void setLocation(int xLabel, int i);

	public void setBounds(GRectangle rectangle);

	public void add(GRectangle bb);

	public void add(double x, double y);

	// public boolean contains(PathPoint prevP);
	public boolean contains(GPoint2D p1);

	public GRectangle union(GRectangle bounds);

	public void setSize(int width, int height);

}
