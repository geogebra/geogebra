package geogebra.kernel.discrete;

import java.awt.geom.Point2D;

public class MyLine {
	Point2D.Double p1, p2;

	public  MyLine(Point2D.Double p1, Point2D.Double p2) {
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public double lengthSquared() {
		return (p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y);
	}
	
}

