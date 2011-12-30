package geogebra.euclidian;

import geogebra.awt.Point2D;

public class PathPoint extends Point2D implements geogebra.common.euclidian.PathPoint{

	private static final long serialVersionUID = 1L;

	boolean lineTo;

	PathPoint(double x, double y, boolean lineTo) {
		setX(x);
		setY(y);
		this.lineTo = lineTo;
	}


}
