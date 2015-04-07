package org.geogebra.desktop.awt;

import java.awt.Shape;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.GeneralPathClipped;
import org.geogebra.desktop.main.AppD;

public class GGenericShapeD implements org.geogebra.desktop.awt.GShapeD {
	private java.awt.Shape impl;

	private GGenericShapeD() {
	}

	public boolean intersects(int i, int j, int k, int l) {
		return impl.intersects(i, j, k, l);
	}

	public static java.awt.Shape getAwtShape(org.geogebra.common.awt.GShape s) {
		if (s instanceof org.geogebra.desktop.awt.GShapeD)
			return ((org.geogebra.desktop.awt.GShapeD) s).getAwtShape();
		if (s instanceof GeneralPathClipped)
			return org.geogebra.desktop.awt.GGeneralPathD
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
		return new org.geogebra.desktop.awt.GPathIteratorD(
				impl.getPathIterator(org.geogebra.desktop.awt.GAffineTransformD
						.getAwtAffineTransform(affineTransform)));
	}

	public GPathIterator getPathIterator(GAffineTransform at, double flatness) {
		return new org.geogebra.desktop.awt.GPathIteratorD(impl.getPathIterator(
				org.geogebra.desktop.awt.GAffineTransformD.getAwtAffineTransform(at),
				flatness));
	}

	public boolean intersects(double x, double y, double w, double h) {
		return impl.intersects(x, y, w, h);
	}

	public boolean intersects(GRectangle2D r) {
		return impl.intersects(org.geogebra.desktop.awt.GGenericRectangle2DD
				.getAWTRectangle2D(r));
	}
}
