package geogebra.common.awt;

public abstract class Rectangle extends Rectangle2D {
	
	public abstract double getY();
	public abstract double getX();
	public abstract double getWidth();
	public abstract double getHeight();

	public abstract void setBounds(int xLabel, int i, int width, int height);

	public abstract void setLocation(int xLabel, int i);

	public abstract void setBounds(Rectangle rectangle);

	public abstract boolean contains(Rectangle labelRectangle);
	public abstract void add(Rectangle bb);
	public abstract double getMinX();
	public abstract double getMinY();
	public abstract double getMaxX();
	public abstract double getMaxY();
	public abstract boolean contains(double d, double e);
	public abstract void add(double x, double y);

}
