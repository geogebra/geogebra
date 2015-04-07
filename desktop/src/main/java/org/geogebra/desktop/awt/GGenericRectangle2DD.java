package org.geogebra.desktop.awt;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.desktop.main.AppD;

public class GGenericRectangle2DD implements org.geogebra.desktop.awt.GRectangle2DD {

	private java.awt.geom.Rectangle2D impl;

	public GGenericRectangle2DD() {
		impl = new java.awt.geom.Rectangle2D.Double();
	}

	public GGenericRectangle2DD(java.awt.geom.Rectangle2D bounds2d) {
		impl = bounds2d;
	}

	public double getY() {
		return impl.getY();
	}

	public double getX() {
		return impl.getX();
	}

	public double getWidth() {
		return impl.getWidth();
	}

	public double getHeight() {
		return impl.getHeight();
	}

	public void setRect(double x, double y, double width, double height) {
		impl.setRect(x, y, width, height);

	}

	public void setFrame(double x, double y, double width, double height) {
		impl.setFrame(x, y, width, height);

	}

	public boolean intersects(double minX, double minY, double lengthX,
			double lengthY) {
		return impl.intersects(minX, minY, lengthX, lengthY);
	}

	public boolean intersects(GRectangle viewRect) {
		return impl.intersects(org.geogebra.desktop.awt.GRectangleD
				.getAWTRectangle(viewRect));
	}

	public static java.awt.geom.Rectangle2D getAWTRectangle2D(
			org.geogebra.common.awt.GRectangle2D r2d) {
		if (r2d instanceof org.geogebra.desktop.awt.GGenericRectangle2DD) {
			return ((org.geogebra.desktop.awt.GGenericRectangle2DD) r2d).impl;
		} else if (r2d instanceof org.geogebra.desktop.awt.GRectangleD) {
			return ((org.geogebra.desktop.awt.GRectangleD) r2d).impl;
		}
		if (r2d != null)
			AppD.debug("other type");
		return null;

	}

	public boolean contains(double xTry, double yTry) {
		return impl.contains(xTry, yTry);
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

	public boolean contains(GRectangle rectangle) {
		return impl.contains(org.geogebra.desktop.awt.GRectangleD
				.getAWTRectangle(rectangle));
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

	public GRectangle2D createIntersection(GRectangle2D r) {
		return new org.geogebra.desktop.awt.GGenericRectangle2DD(
				impl.createIntersection(org.geogebra.desktop.awt.GGenericRectangle2DD
						.getAWTRectangle2D(r)));
	}

	@Override
	public double getMinX() {
		return impl.getMinX();
	}

	@Override
	public double getMaxX() {
		return impl.getMaxX();
	}

	@Override
	public double getMinY() {
		return impl.getMinY();
	}

	@Override
	public double getMaxY() {
		return impl.getMaxY();
	}

	@Override
	public boolean intersectsLine(double x1, double y1, double x2, double y2) {
		return impl.intersectsLine(x1, y1, x2, y2);
	}

	@Override
	public boolean contains(GRectangle2D rectangle) {
		return impl.contains(GRectangleD.getAWTRectangle2D(rectangle));
	}

	@Override
	public Rectangle2D getImpl() {
		return impl;
	}

}
