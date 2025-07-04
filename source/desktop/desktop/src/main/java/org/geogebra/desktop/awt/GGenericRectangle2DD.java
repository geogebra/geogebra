package org.geogebra.desktop.awt;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.util.debug.Log;

public class GGenericRectangle2DD implements GRectangle2DD {

	private Rectangle2D impl;

	public GGenericRectangle2DD() {
		impl = new Rectangle2D.Double();
	}

	public GGenericRectangle2DD(Rectangle2D bounds2d) {
		impl = bounds2d;
	}

	@Override
	public double getY() {
		return impl.getY();
	}

	@Override
	public double getX() {
		return impl.getX();
	}

	@Override
	public double getWidth() {
		return impl.getWidth();
	}

	@Override
	public double getHeight() {
		return impl.getHeight();
	}

	@Override
	public void setRect(double x, double y, double width, double height) {
		impl.setRect(x, y, width, height);

	}

	@Override
	public void setFrame(double x, double y, double width, double height) {
		impl.setFrame(x, y, width, height);

	}

	@Override
	public boolean intersects(double minX, double minY, double lengthX,
			double lengthY) {
		return impl.intersects(minX, minY, lengthX, lengthY);
	}

	/**
	 * @param rectangle rectangle
	 * @return whether this intersects given rectangle
	 */
	public boolean intersects(GRectangle rectangle) {
		return impl.intersects(GRectangleD.getAWTRectangle(rectangle));
	}

	/**
	 * @param r2d cross-platform rectangle
	 * @return native rectangle
	 */
	public static Rectangle2D getAWTRectangle2D(GRectangle2D r2d) {
		if (r2d instanceof GGenericRectangle2DD) {
			return ((GGenericRectangle2DD) r2d).impl;
		} else if (r2d instanceof GRectangleD) {
			return ((GRectangleD) r2d).impl;
		}
		if (r2d != null) {
			Log.debug("other type");
		}
		return null;

	}

	@Override
	public boolean contains(double xTry, double yTry) {
		return impl.contains(xTry, yTry);
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
	public GRectangleD getBounds() {
		return new GRectangleD(impl.getBounds());
	}

	@Override
	public GRectangle2D getBounds2D() {
		return new GGenericRectangle2DD(impl.getBounds2D());
	}

	/**
	 * @param rectangle rectangle
	 * @return whether this contains given rectangle
	 */
	public boolean contains(GRectangle rectangle) {
		return impl.contains(GRectangleD.getAWTRectangle(rectangle));
	}

	@Override
	public GPathIterator getPathIterator(GAffineTransform affineTransform) {
		return new GPathIteratorD(impl.getPathIterator(
				GAffineTransformD.getAwtAffineTransform(affineTransform)));
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
	public GRectangle2D createIntersection(GRectangle2D r) {
		return new GGenericRectangle2DD(impl
				.createIntersection(GGenericRectangle2DD.getAWTRectangle2D(r)));
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

	@Override
	public void add(double x, double y) {
		impl.add(x, y);
	}

}
