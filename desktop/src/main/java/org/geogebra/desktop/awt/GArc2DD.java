package org.geogebra.desktop.awt;

import java.awt.Shape;
import java.awt.geom.Arc2D;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GArc2D;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle2D;

public class GArc2DD implements GRectangularShapeD, GArc2D {

	private Arc2D.Double impl;

	public GArc2DD() {
		impl = new Arc2D.Double();
	}

	@Override
	public boolean contains(double xTry, double yTry) {
		return impl.contains(xTry, yTry);
	}

	@Override
	public boolean intersects(double x, double y, double lengthX,
			double lengthY) {
		return impl.intersects(x, y, lengthX, lengthY);
	}

	@Override
	public boolean intersects(int x, int y, int w, int h) {
		return impl.intersects(x, y, w, h);
	}

	@Override
	public boolean contains(int x, int y) {
		return impl.contains(x, y);
	}

	@Override
	public GRectangleD getBounds() {
		return new GRectangleD(impl.getBounds());
	}

	@Override
	public GRectangle2D getBounds2D() {
		return new GGenericRectangle2DD(impl.getBounds2D());
	}

	@Override
	public boolean contains(GRectangle2D rectangle) {
		return impl.contains(GRectangleD.getAWTRectangle2D(rectangle));
	}

	@Override
	public GPathIterator getPathIterator(GAffineTransform affineTransform) {
		return new GPathIteratorD(impl.getPathIterator(
				GAffineTransformD.getAwtAffineTransform(affineTransform)));
	}

	@Override
	public boolean intersects(GRectangle2D r) {
		return impl.intersects(GGenericRectangle2DD.getAWTRectangle2D(r));
	}

	@Override
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
		return new GPoint2D(impl.getStartPoint().getX(), impl.getStartPoint().getY());
	}

	@Override
	public GPoint2D getEndPoint() {
		return new GPoint2D(impl.getEndPoint().getX(), impl.getEndPoint().getY());
	}

	@Override
	public void setArcByCenter(double x, double y, double radius, double angSt,
			double angleExt, int closure) {
		impl.setArcByCenter(x, y, radius, angSt, angleExt, closure);

	}

}
