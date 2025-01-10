package org.geogebra.desktop.awt;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;

public class GEllipse2DDoubleD implements GRectangularShapeD, GEllipse2DDouble {

	private Ellipse2D.Double impl;

	public GEllipse2DDoubleD() {
		impl = new Ellipse2D.Double();
	}

	public GEllipse2DDoubleD(Ellipse2D.Double ellipse) {
		impl = ellipse;
	}

	public GEllipse2DDoubleD(double x, double y, double w, double h) {
		impl = new Ellipse2D.Double(x, y, w, h);
	}

	@Override
	public void setFrame(double xUL, double yUL, double diameter,
			double diameter2) {
		impl.setFrame(xUL, yUL, diameter, diameter2);
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
		return impl.contains(GRectangleD.getAWTRectangle2D(rectangle));
	}

	@Override
	public boolean contains(double xTry, double yTry) {
		return impl.contains(xTry, yTry);
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
		return impl.intersects(GGenericRectangle2DD.getAWTRectangle2D(r));
	}

	@Override
	public Shape getAwtShape() {
		return impl;
	}

	@Override
	public void setFrameFromCenter(double centerX, double centerY,
			double cornerX, double cornerY) {
		impl.setFrameFromCenter(centerX, centerY, cornerX, cornerY);

	}

}
