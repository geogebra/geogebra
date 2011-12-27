package geogebra.web.awt;

import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.PathIterator;
import geogebra.common.awt.Point2D;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.Rectangle2D;

public class GeneralPath extends geogebra.common.awt.GeneralPath implements
        Shape {
	
	private geogebra.web.kernel.gawt.GeneralPath impl = new geogebra.web.kernel.gawt.GeneralPath();

	public GeneralPath() {
		impl = new geogebra.web.kernel.gawt.GeneralPath();
	}
	
	public GeneralPath(geogebra.web.kernel.gawt.GeneralPath g) {
		impl = g;
	}
	
	@Override
	public boolean intersects(int rx, int ry, int rw, int rh) {
		return impl.intersects(rx, ry, rw, rh);
	}

	@Override
	public boolean contains(int x, int y) {
		return impl.contains(x, y);
	}

	@Override
	public Rectangle getBounds() {
		return new geogebra.web.awt.Rectangle(impl.getBounds());
	}

	@Override
	public Rectangle2D getBounds2D() {
		return new geogebra.web.awt.Rectangle2D(impl.getBounds2D());
	}

	@Override
	public boolean contains(Rectangle r) {
		return impl.contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	@Override
	public boolean contains(double xTry, double yTry) {
		return impl.contains(xTry, yTry);
	}

	@Override
	public PathIterator getPathIterator(AffineTransform affineTransform) {
		return (PathIterator) impl.getPathIterator((geogebra.web.kernel.gawt.AffineTransform) affineTransform);
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return (PathIterator) impl.getPathIterator((geogebra.web.kernel.gawt.AffineTransform) at, flatness);
	}

	@Override
	public boolean intersects(double x, double y, double w, double h) {
		return impl.intersects(x, y, w, h);
	}

	@Override
	public boolean intersects(Rectangle2D r) {
		return impl.intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	@Override
	public geogebra.web.kernel.gawt.Shape getWebShape() {
		return impl;
	}

	@Override
	public void moveTo(float x, float y) {
		impl.moveTo(x, y);
	}

	@Override
	public void reset() {
		impl.reset();
	}

	@Override
	public void lineTo(float x, float y) {
		impl.lineTo(x, y);
	}

	@Override
	public void closePath() {
		impl.closePath();
	}

	@Override
	public geogebra.common.awt.Shape createTransformedShape(
	        AffineTransform affineTransform) {
		return (geogebra.common.awt.Shape) impl.createTransformedShape((geogebra.web.kernel.gawt.AffineTransform) affineTransform);
	}

	@Override
	public Point2D getCurrentPoint() {
		return new geogebra.web.awt.Point2D(impl.getCurrentPoint().getX(),impl.getCurrentPoint().getY());
	}

}
