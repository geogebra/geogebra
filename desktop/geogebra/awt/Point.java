package geogebra.awt;

public class Point extends geogebra.common.awt.Point {
	
	public Point(double x, double y) {
		super((int) x,(int) y);
	}

	public Point() {
		super();
	}

	public java.awt.Point getAwtPoint() {
		return new java.awt.Point(x, y);
	}

}
