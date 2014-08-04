package geogebra.awt;

import geogebra.common.awt.GAffineTransform;
import geogebra.common.awt.GPathIterator;
import geogebra.common.awt.GRectangle;
import geogebra.common.awt.GRectangle2D;

import java.awt.Shape;

public class GEllipse2DFloatD implements geogebra.awt.GRectangularShapeD,
		geogebra.common.awt.GEllipse2DFloat {

	private java.awt.geom.Ellipse2D.Float impl;

	public GEllipse2DFloatD(int i, int j, int k, int l) {
		impl = new java.awt.geom.Ellipse2D.Float(i, j, k, l);
	}

	public GEllipse2DFloatD() {
		impl = new java.awt.geom.Ellipse2D.Float();
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

	public boolean intersects(double x, double y, double w, double h) {
		return impl.intersects(x, y, w, h);
	}

	public boolean intersects(GRectangle2D r) {
		return impl.intersects(geogebra.awt.GGenericRectangle2DD
				.getAWTRectangle2D(r));
	}

	public Shape getAwtShape() {
		return impl;
	}

}
