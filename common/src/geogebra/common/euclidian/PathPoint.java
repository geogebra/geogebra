package geogebra.common.euclidian;

public class PathPoint extends geogebra.common.awt.Point2D{

	private static final long serialVersionUID = 1L;

	protected double x;
	protected double y;
	boolean lineTo;

	public PathPoint(double x, double y, boolean lineTo) {
		setX(x);
		setY(y);
		this.lineTo = lineTo;
	}

	@Override
	public double getX() {
		return this.x;
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
		this.x = x;
	}
	
	@Override
	public void setY(double y) {
		this.y = y;
	}
	
	@Override
	public double distance(geogebra.common.awt.Point2D q) {
		return distance(q.getX(), q.getY());
	}

	public boolean getLineTo() {
		return lineTo;
	}

}
