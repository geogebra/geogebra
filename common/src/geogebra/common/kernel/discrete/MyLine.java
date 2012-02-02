package geogebra.common.kernel.discrete;

import geogebra.common.awt.Point2D;

public class MyLine {
	public Point2D p1;
	public Point2D p2;

	public  MyLine(Point2D p1, Point2D p2) {
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public double lengthSquared() {
		return (p1.getX() - p2.getX()) * (p1.getX() - p2.getX()) + (p1.getY() - p2.getY()) * (p1.getY() - p2.getY());
	}
	
}

