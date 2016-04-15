package org.geogebra.desktop.awt;

import java.awt.geom.Point2D;

import org.geogebra.common.awt.GPoint2D;

public class GPoint2DD extends GPoint2D {
	private Point2D.Double impl;

	public GPoint2DD() {
		impl = new Point2D.Double();
	}

	public GPoint2DD(double x, double y) {
		impl = new Point2D.Double(x, y);
	}

	public GPoint2DD(Point2D.Double point) {
		this();
		impl = point;
	}

	public static Point2D getAwtPoint2D(GPoint2D p) {
		if (p == null)
			return null;
		return new java.awt.geom.Point2D.Double(p.getX(), p.getY());
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
		impl.x = x;

	}

	@Override
	public void setY(double y) {
		impl.y = y;
	}

	@Override
	public double distance(org.geogebra.common.awt.GPoint2D q) {
		// TODO Auto-generated method stub
		return impl.distance(q.getX(), q.getY());
	}

	@Override
	public double distance(double x, double y) {
		return impl.distance(x, y);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof GPoint2DD))
			return false;
		return impl.equals(((GPoint2DD) o).impl);
	}

	@Override
	public int hashCode() {
		return impl.hashCode();
	}

}
