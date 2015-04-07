package org.geogebra.desktop.awt;

import java.awt.geom.Path2D;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.desktop.main.AppD;

public class GGeneralPathD implements org.geogebra.common.awt.GGeneralPath,
		org.geogebra.desktop.awt.GShapeD {

	private java.awt.geom.GeneralPath impl = new java.awt.geom.GeneralPath();

	public GGeneralPathD(java.awt.geom.GeneralPath g) {
		impl = g;
	}

	public GGeneralPathD() {
		// default winding rule changed for ggb50 (for Polygons) #3983
		impl = new java.awt.geom.GeneralPath(Path2D.WIND_EVEN_ODD);
	}

	public GGeneralPathD(int rule) {
		impl = new java.awt.geom.GeneralPath(rule);
	}

	public static java.awt.geom.GeneralPath getAwtGeneralPath(
			org.geogebra.common.awt.GGeneralPath gp) {
		if (!(gp instanceof org.geogebra.desktop.awt.GGeneralPathD)) {
			if (gp != null)
				AppD.debug("other type");
			return null;
		}
		return ((org.geogebra.desktop.awt.GGeneralPathD) gp).impl;
	}

	public synchronized void moveTo(float f, float g) {
		impl.moveTo(f, g);

	}

	public synchronized void reset() {
		impl.reset();
	}

	public synchronized void lineTo(float x, float y) {
		impl.lineTo(x, y);
	}

	public synchronized void closePath() {
		impl.closePath();
	}

	public synchronized void append(GShape s, boolean connect) {
		if (!(s instanceof GShapeD))
			return;
		impl.append(((GShapeD) s).getAwtShape(), connect);

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

	public boolean contains(GRectangle rectangle) {
		return impl.contains(org.geogebra.desktop.awt.GRectangleD
				.getAWTRectangle(rectangle));
	}

	public boolean contains(double xTry, double yTry) {
		return impl.contains(xTry, yTry);
	}

	public java.awt.Shape getAwtShape() {
		return impl;
	}

	public GPathIterator getPathIterator(GAffineTransform affineTransform) {
		// TODO Auto-generated method stub
		return new org.geogebra.desktop.awt.GPathIteratorD(
				impl.getPathIterator(org.geogebra.desktop.awt.GAffineTransformD
						.getAwtAffineTransform(affineTransform)));
	}

	public boolean intersects(double x, double y, double w, double h) {
		return impl.intersects(x, y, w, h);
	}

	public boolean intersects(GRectangle2D r) {
		return impl.intersects(org.geogebra.desktop.awt.GGenericRectangle2DD
				.getAWTRectangle2D(r));
	}

	public org.geogebra.common.awt.GShape createTransformedShape(
			GAffineTransform affineTransform) {
		return new GGenericShapeD(
				impl.createTransformedShape(((GAffineTransformD) affineTransform)
						.getImpl()));
	}

	public GPoint2D getCurrentPoint() {
		if (impl.getCurrentPoint() == null) {
			return null;
		}
		return new org.geogebra.desktop.awt.GPoint2DD(impl.getCurrentPoint().getX(), impl
				.getCurrentPoint().getY());
	}

	public boolean contains(GRectangle2D p) {
		return impl.contains(org.geogebra.desktop.awt.GGenericRectangle2DD
				.getAWTRectangle2D(p));
	}

	public boolean contains(double arg0, double arg1, double arg2, double arg3) {
		return impl.contains(arg0, arg1, arg2, arg3);
	}

	public boolean contains(GPoint2D p) {
		if (p == null)
			return false;
		return impl.contains(p.getX(), p.getY());
	}

	public void curveTo(float f, float g, float h, float i, float j, float k) {
		impl.curveTo((float) f, (float) g, (float) h, (float) i, (float) j,
				(float) k);

	}
}
