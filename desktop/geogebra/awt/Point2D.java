package geogebra.awt;

public class Point2D extends geogebra.common.awt.Point2D {
	private java.awt.geom.Point2D.Double impl;
	public Point2D(){
		impl = new java.awt.geom.Point2D.Double();
	}
	public Point2D(double x, double y) {
		impl = new java.awt.geom.Point2D.Double(x,y);
	}
	
	
	public static java.awt.geom.Point2D getAwtPoint2D(geogebra.common.awt.Point2D p) {
		if (p==null) return null;
		return new java.awt.geom.Point2D.Double(p.getX(),p.getY());
	}

	
	@Override
	public double getX() {
		return impl.x;
	}

	@Override
	public double getY() {
		return impl.y;
	}

	@Override
	public void setX(double x) {
		impl.x=x;

	}

	@Override
	public void setY(double y) {
		impl.y=y;
	}
	@Override
	public double distance(geogebra.common.awt.Point2D q) {
		// TODO Auto-generated method stub
		return impl.distance(q.getX(),q.getY());
	}
	@Override
	public double distance(double x, double y) {
		return impl.distance(x,y);
	}


}
