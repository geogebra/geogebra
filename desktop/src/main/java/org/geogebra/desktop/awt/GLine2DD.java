package org.geogebra.desktop.awt;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;

public class GLine2DD implements GLine2D, GShapeD {
	private Line2D impl;

	public GLine2DD() {
		impl = new Line2D.Double();
	}

	public GLine2DD(Line2D line) {
		impl = line;
	}

	public boolean intersects(int i, int j, int k, int l) {
		return impl.intersects(i, j, k, l);
	}

	public boolean contains(int x, int y) {
		return impl.contains(x, y);
	}

	public GRectangle getBounds() {
		return new GRectangleD(impl.getBounds());
	}

	public GRectangle2D getBounds2D() {
		return new GGenericRectangle2DD(impl.getBounds2D());
	}

	public boolean contains(GRectangle2D rectangle) {
		return impl.contains(GRectangleD.getAWTRectangle2D(rectangle));
	}

	public boolean contains(double xTry, double yTry) {
		return impl.contains(xTry, yTry);
	}

	public Shape getAwtShape() {
		return impl;
	}

	@Override
	public void setLine(double x1, double y1, double x2, double y2) {
		impl.setLine(x1, y1, x2, y2);

	}

	public GPathIterator getPathIterator(GAffineTransform affineTransform) {
		return new GPathIteratorD(
impl.getPathIterator(
				GAffineTransformD.getAwtAffineTransform(affineTransform)));
	}

	public GPathIterator getPathIterator(GAffineTransform at, double flatness) {
		return new GPathIteratorD(
impl.getPathIterator(
				GAffineTransformD.getAwtAffineTransform(at),
				flatness));
	}

	public boolean intersects(double x, double y, double w, double h) {
		return impl.intersects(x, y, w, h);
	}

	public boolean intersects(GRectangle2D r) {
		return impl.intersects(GGenericRectangle2DD.getAWTRectangle2D(r));
	}

	public GPoint2D getP1() {
		Point2D p1 = impl.getP1();
		if (p1 == null)
			return null;
		return new GPoint2DD(p1.getX(), p1.getY());
	}

	public GPoint2D getP2() {
		Point2D p2 = impl.getP2();
		if (p2 == null)
			return null;
		return new GPoint2DD(p2.getX(), p2.getY());
	}

}
