package org.geogebra.common.awt;

public interface GRectangle extends GRectangle2D {

	@Override
	public double getY();

	@Override
	public double getX();

	@Override
	public double getWidth();

	@Override
	public double getHeight();

	public void setBounds(int x, int y, int width, int height);

	public void setLocation(int xLabel, int i);

	public void setBounds(GRectangle rectangle);

	public void add(GRectangle bb);

	@Override
	public void add(double x, double y);

	// public boolean contains(PathPoint prevP);
	public boolean contains(GPoint2D p1);

	public GRectangle union(GRectangle bounds);

	public void setSize(int width, int height);

}
