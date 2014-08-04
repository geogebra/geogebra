package geogebra.awt;

import geogebra.common.awt.GAffineTransform;
import geogebra.common.awt.GPathIterator;
import geogebra.common.awt.GRectangle2D;
import geogebra.common.euclidian.GeneralPathClipped;
import geogebra.main.AppD;

import java.awt.Shape;

public class GGenericShapeD implements geogebra.awt.GShapeD {
	private java.awt.Shape impl;

	private GGenericShapeD() {
	}

	public boolean intersects(int i, int j, int k, int l) {
		return impl.intersects(i, j, k, l);
	}

	public static java.awt.Shape getAwtShape(geogebra.common.awt.GShape s) {
		if (s instanceof geogebra.awt.GShapeD)
			return ((geogebra.awt.GShapeD) s).getAwtShape();
		if (s instanceof GeneralPathClipped)
			return geogebra.awt.GGeneralPathD
					.getAwtGeneralPath(((GeneralPathClipped) s)
							.getGeneralPath());
		if (s != null)
			AppD.debug("other type " + s);
		return null;
	}

	public GGenericShapeD(java.awt.Shape s) {
		this();
		impl = s;
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

	public boolean contains(double xTry, double yTry) {
		return impl.contains(xTry, yTry);
	}

	public void setImpl(java.awt.Shape newImpl) {
		impl = newImpl;
	}

	public Shape getAwtShape() {
		return impl;
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
}
