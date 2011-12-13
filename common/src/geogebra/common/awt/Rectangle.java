package geogebra.common.awt;

public abstract class Rectangle {
	
	public abstract double getY();
	public abstract double getX();
	public abstract double getWidth();
	public abstract double getHeight();

	public abstract void setBounds(int xLabel, int i, int width, int height);

	public abstract void setLocation(int xLabel, int i);

	public abstract void setBounds(Rectangle rectangle);

	public abstract boolean contains(int x, int y);

}
