package geogebra.web.awt;

import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.PathIterator;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.Rectangle2D;
import geogebra.common.awt.Shape;

public class Area implements geogebra.common.awt.Area {
	
	private geogebra.web.kernel.gawt.Area impl;
	
	public Area() {
		impl = new geogebra.web.kernel.gawt.Area();
	}
	
	public Area(Shape shape) {
		impl = new geogebra.web.kernel.gawt.Area((geogebra.web.kernel.gawt.Shape) shape);
	}

	@Override
	public boolean intersects(int i, int j, int k, int l) {
		return impl.intersects(i, j, k, l);
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
	public void subtract(geogebra.common.awt.Area shape) {
		impl.subtract((geogebra.web.kernel.gawt.Area) shape);
	}

	@Override
	public void intersect(geogebra.common.awt.Area shape) {
		impl.intersect((geogebra.web.kernel.gawt.Area) shape);
	}

	@Override
	public void exclusiveOr(geogebra.common.awt.Area shape) {
		impl.exclusiveOr((geogebra.web.kernel.gawt.Area) shape);
	}

	@Override
	public void add(geogebra.common.awt.Area shape) {
		impl.add((geogebra.web.kernel.gawt.Area) shape);
	}

}
