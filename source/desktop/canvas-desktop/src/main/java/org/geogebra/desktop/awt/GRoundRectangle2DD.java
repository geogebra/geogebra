package org.geogebra.desktop.awt;

import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GRoundRectangle2D;

public class GRoundRectangle2DD implements GRoundRectangle2D, GShapeD {

	RoundRectangle2D impl = new RoundRectangle2D.Double();

	@Override
	public void setRoundRect(double x, double y, double width, double height,
			double rx, double ry) {
		impl.setRoundRect(x, y, width, height, rx, ry);
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
	public GRectangle getBounds() {
		return new GRectangleD(impl.getBounds());
	}

	@Override
	public GRectangle2D getBounds2D() {
		return new GGenericRectangle2DD(impl.getBounds2D());
	}

	@Override
	public boolean contains(GRectangle2D rectangle) {
		return impl.contains(((GRectangle2DD) rectangle).getImpl());
	}

	@Override
	public boolean contains(double x, double y) {
		return impl.contains(x, y);
	}

	@Override
	public GPathIterator getPathIterator(GAffineTransform affineTransform) {
		return new GPathIteratorD(impl.getPathIterator(
				GAffineTransformD.getAwtAffineTransform(affineTransform)));
	}

	@Override
	public boolean intersects(double x, double y, double w, double h) {
		return impl.intersects(x, y, w, h);
	}

	@Override
	public boolean intersects(GRectangle2D r) {
		return impl.intersects(((GRectangle2DD) r).getImpl());
	}

	@Override
	public Shape getAwtShape() {
		return impl;
	}
}
