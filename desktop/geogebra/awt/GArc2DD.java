package geogebra.awt;

import geogebra.common.awt.GAffineTransform;
import geogebra.common.awt.GPathIterator;
import geogebra.common.awt.GPoint2D;
import geogebra.common.awt.GRectangle2D;

import java.awt.Shape;

public class GArc2DD extends geogebra.common.awt.GArc2D implements
		GRectangularShapeD {

	private java.awt.geom.Arc2D.Double impl;

	public GArc2DD() {
		impl = new java.awt.geom.Arc2D.Double();
	}

	public boolean contains(double xTry, double yTry) {
		return impl.contains(xTry, yTry);
	}

	public boolean intersects(double x, double y, double lengthX, double lengthY) {
		return impl.intersects(x, y, lengthX, lengthY);
	}

	public boolean intersects(int i, int j, int k, int l) {
		return impl.intersects(i, j, k, l);
	}

	public boolean contains(int x, int y) {
		return impl.contains(x, y);
	}

	public geogebra.awt.GRectangleD getBounds() {
		return new geogebra.awt.GRectangleD(impl.getBounds());
	}

	public GRectangle2D getBounds2D() {
		return new geogebra.awt.GGenericRectangle2DD(impl.getBounds2D());
	}

	public boolean contains(GRectangle2D rectangle) {
		return impl.contains(geogebra.awt.GRectangleD
				.getAWTRectangle2D(rectangle));
	}

	public GPathIterator getPathIterator(GAffineTransform affineTransform) {
		return new geogebra.awt.GPathIteratorD(
				impl.getPathIterator(geogebra.awt.GAffineTransformD
						.getAwtAffineTransform(affineTransform)));
	}

	public GPathIterator getPathIterator(GAffineTransform at, double flatness) {
		return new geogebra.awt.GPathIteratorD(impl.getPathIterator(
				geogebra.awt.GAffineTransformD.getAwtAffineTransform(at),
				flatness));
	}

	public boolean intersects(GRectangle2D r) {
		return impl.intersects(geogebra.awt.GGenericRectangle2DD
				.getAWTRectangle2D(r));
	}

	public Shape getAwtShape() {
		return impl;
	}

	@Override
	public void setArc(double x, double y, double w, double h, double angSt,
			double angExt, int closure) {
		impl.setArc(x, y, w, h, angSt, angExt, closure);

	}

	@Override
	public GPoint2D getStartPoint() {
		// impl.getStartPoint() return-type is java.awt.geom.Point2D
		// but it returns with java.awt.geom.Point2D.Double
		return new geogebra.awt.GPoint2DD(
				(java.awt.geom.Point2D.Double) impl.getStartPoint());
	}

	@Override
	public GPoint2D getEndPoint() {
		return new geogebra.awt.GPoint2DD(
				(java.awt.geom.Point2D.Double) impl.getEndPoint());
	}

	@Override
	public void setArcByCenter(double x, double y, double radius, double angSt,
			double angExt, int closure) {
		impl.setArcByCenter(x, y, radius, angSt, angExt, closure);

	}

}
