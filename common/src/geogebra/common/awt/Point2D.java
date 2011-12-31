package geogebra.common.awt;

public interface Point2D {
	public class Double {
		
	}
	
	public double getX();
	public double getY();
	public void setX(double x);
	public void setY(double y);
	public double distance(Point2D q);
	public double distance(double x, double y);
}
