package geogebra.awt;

import geogebra.common.awt.GAffineTransform;
import geogebra.common.awt.GPathIterator;
import geogebra.common.awt.GRectangle;
import geogebra.common.awt.GRectangle2D;

import java.awt.Shape;

public class GLine2DD implements geogebra.common.awt.GLine2D,
		geogebra.awt.GShapeD {
	private java.awt.geom.Line2D impl;

	public GLine2DD() {
		impl = new java.awt.geom.Line2D.Double();
	}

	public boolean intersects(int i, int j, int k, int l) {
		return impl.intersects(i, j, k, l);
	}

	public boolean contains(int x, int y) {
		return impl.contains(x, y);
	}

	public GRectangle getBounds() {
		return new geogebra.awt.GRectangleD(impl.getBounds());
	}

	public GRectangle2D getBounds2D() {
		return new geogebra.awt.GGenericRectangle2DD(impl.getBounds2D());
	}

	public boolean contains(GRectangle2D rectangle) {
		return impl.contains(geogebra.awt.GRectangleD
				.getAWTRectangle2D(rectangle));
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
		// TODO Auto-generated method stub
		return new geogebra.awt.GPathIteratorD(
				impl.getPathIterator(geogebra.awt.GAffineTransformD
						.getAwtAffineTransform(affineTransform)));
	}

	public GPathIterator getPathIterator(GAffineTransform at, double flatness) {
		// TODO Auto-generated method stub
		return new geogebra.awt.GPathIteratorD(impl.getPathIterator(
				geogebra.awt.GAffineTransformD.getAwtAffineTransform(at),
				flatness));
	}

	public boolean intersects(double x, double y, double w, double h) {
		// TODO Auto-generated method stub
		return impl.intersects(x, y, w, h);
	}

	public boolean intersects(GRectangle2D r) {
		// TODO Auto-generated method stub
		return impl.intersects(geogebra.awt.GGenericRectangle2DD
				.getAWTRectangle2D(r));
	}

	public geogebra.common.awt.GPoint2D getP1() {
		java.awt.geom.Point2D p1 = impl.getP1();
		if (p1 == null)
			return null;
		return new GPoint2DD(p1.getX(), p1.getY());
	}

	public geogebra.common.awt.GPoint2D getP2() {
		java.awt.geom.Point2D p2 = impl.getP2();
		if (p2 == null)
			return null;
		return new GPoint2DD(p2.getX(), p2.getY());
	}

}
