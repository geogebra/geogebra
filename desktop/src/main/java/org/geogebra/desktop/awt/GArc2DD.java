package org.geogebra.desktop.awt;

import java.awt.Shape;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GArc2D;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle2D;

public class GArc2DD implements GRectangularShapeD, GArc2D {

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

	public org.geogebra.desktop.awt.GRectangleD getBounds() {
		return new org.geogebra.desktop.awt.GRectangleD(impl.getBounds());
	}

	public GRectangle2D getBounds2D() {
		return new org.geogebra.desktop.awt.GGenericRectangle2DD(impl.getBounds2D());
	}

	public boolean contains(GRectangle2D rectangle) {
		return impl.contains(org.geogebra.desktop.awt.GRectangleD
				.getAWTRectangle2D(rectangle));
	}

	public GPathIterator getPathIterator(GAffineTransform affineTransform) {
		return new org.geogebra.desktop.awt.GPathIteratorD(
				impl.getPathIterator(org.geogebra.desktop.awt.GAffineTransformD
						.getAwtAffineTransform(affineTransform)));
	}

	public GPathIterator getPathIterator(GAffineTransform at, double flatness) {
		return new org.geogebra.desktop.awt.GPathIteratorD(impl.getPathIterator(
				org.geogebra.desktop.awt.GAffineTransformD.getAwtAffineTransform(at),
				flatness));
	}

	public boolean intersects(GRectangle2D r) {
		return impl.intersects(org.geogebra.desktop.awt.GGenericRectangle2DD
				.getAWTRectangle2D(r));
	}

	public Shape getAwtShape() {
		return impl;
	}

	public void setArc(double x, double y, double w, double h, double angSt,
			double angExt, int closure) {
		impl.setArc(x, y, w, h, angSt, angExt, closure);

	}

	public GPoint2D getStartPoint() {
		// impl.getStartPoint() return-type is java.awt.geom.Point2D
		// but it returns with java.awt.geom.Point2D.Double
		return new org.geogebra.desktop.awt.GPoint2DD(
				(java.awt.geom.Point2D.Double) impl.getStartPoint());
	}

	public GPoint2D getEndPoint() {
		return new org.geogebra.desktop.awt.GPoint2DD(
				(java.awt.geom.Point2D.Double) impl.getEndPoint());
	}

	public void setArcByCenter(double x, double y, double radius, double angSt,
			double angExt, int closure) {
		impl.setArcByCenter(x, y, radius, angSt, angExt, closure);

	}

}
