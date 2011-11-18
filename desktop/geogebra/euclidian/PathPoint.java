package geogebra.euclidian;

import java.awt.geom.Point2D;

public class PathPoint extends Point2D.Double {
	boolean lineTo;
	
	PathPoint(double x, double y, boolean lineTo) {
		this.x = x;
		this.y = y;
		this.lineTo = lineTo;
	}
}
