package geogebra.common.awt;

public abstract class Point2D {
	public abstract class Double {
		
	}
	
	public abstract double getX();
	public abstract double getY();
	public abstract void setX(double x);
	public abstract void setY(double y);
	public abstract double distance(Point2D q);
	public abstract double distance(double x, double y);
}
