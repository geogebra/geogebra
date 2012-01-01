package geogebra.euclidian;

import geogebra.awt.Point2D;

public class PathPoint extends geogebra.common.euclidian.PathPoint{

	private static final long serialVersionUID = 1L;

	boolean lineTo;

	PathPoint(double x, double y, boolean lineTo) {
		setX(x);
		setY(y);
		this.lineTo = lineTo;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double distance(double x, double y) {
	    return geogebra.common.awt.Point2D.distanceSq(getX(), getY(), x, y);
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public void setX(double x) {
		this.x = (int) x;
	}

	@Override
	public void setY(double y) {
		this.y = (int) y;
	}

	@Override
	public double distance(geogebra.common.awt.Point2D q) {
		return distance(q.getX(), q.getY());
	}


}
