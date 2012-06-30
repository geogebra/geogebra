package geogebra.common.awt;

public interface Rectangle extends GRectangle2D {
	
	public double getY();
	public double getX();
	public double getWidth();
	public double getHeight();

	public void setBounds(int xLabel, int i, int width, int height);

	public void setLocation(int xLabel, int i);

	public void setBounds(Rectangle rectangle);

	public boolean contains(Rectangle labelRectangle);
	public void add(Rectangle bb);
	public double getMinX();
	public double getMinY();
	public double getMaxX();
	public double getMaxY();
	public boolean contains(double d, double e);
	public void add(double x, double y);
	//public boolean contains(PathPoint prevP);
	public boolean contains(GPoint2D p1);
	public Rectangle union(Rectangle bounds);
	public void setSize(int width, int height);

}
