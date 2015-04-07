package org.geogebra.desktop.awt;

import java.awt.Shape;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;

public class GEllipse2DDoubleD implements org.geogebra.desktop.awt.GRectangularShapeD,
		org.geogebra.common.awt.GEllipse2DDouble {

	private java.awt.geom.Ellipse2D.Double impl;

	/*
	 * public Double(java.awt.geom.Ellipse2D.Double ellipse2d) { impl =
	 * ellipse2d; }
	 */
	public GEllipse2DDoubleD() {
		impl = new java.awt.geom.Ellipse2D.Double();
	}

	public GEllipse2DDoubleD(java.awt.geom.Ellipse2D.Double ellipse) {
		impl = ellipse;
	}

	public GEllipse2DDoubleD(int i, int j, int k, int l) {
		impl = new java.awt.geom.Ellipse2D.Double(i, j, k, l);
	}

	public void setFrame(double xUL, double yUL, double diameter,
			double diameter2) {
		impl.setFrame(xUL, yUL, diameter, diameter2);
	}

	public boolean intersects(int i, int j, int k, int l) {
		return impl.intersects(i, j, k, l);
	}

	public boolean contains(int x, int y) {
		return impl.contains(x, y);
	}

	public GRectangle getBounds() {
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

	public Shape getAwtShape() {
		return impl;
	}

	public static java.awt.geom.Ellipse2D.Double getAwtEllipse2DDouble(
			java.awt.geom.Ellipse2D.Double ellipse) {
		if (ellipse == null)
			return null;
		return new java.awt.geom.Ellipse2D.Double(ellipse.getX(),
				ellipse.getY(), ellipse.getWidth(), ellipse.getHeight());

		/*
		 * if(ellipse instanceof geogebra.awt.Shape) return
		 * ((geogebra.awt.Ellipse2DDouble)ellipse).getAwtShape(); if (ellipse!=
		 * null) Application.debug("other type"); return null;
		 */
	}

	public void setFrameFromCenter(double i, double j, double d, double e) {
		impl.setFrameFromCenter(i, j, d, e);

	}

}
