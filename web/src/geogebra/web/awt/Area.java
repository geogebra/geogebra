package geogebra.web.awt;

import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.PathIterator;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.Rectangle2D;
import geogebra.common.awt.Shape;

/**
 * Wrapper for gawt Area
 * @author Gabor
 *
 */
public class Area implements geogebra.common.awt.Area {
	
	private geogebra.web.kernel.gawt.Area impl;
	/**
	 * Creates new area
	 */
	public Area() {
		impl = new geogebra.web.kernel.gawt.Area();
	}
	/**
	 * Creates new area
	 */
	public Area(Shape shape) {
		impl = new geogebra.web.kernel.gawt.Area(geogebra.web.awt.GenericShape.getWebShape(shape));
	}

	public boolean intersects(int i, int j, int k, int l) {
		return impl.intersects(i, j, k, l);
	}

	
	public boolean contains(int x, int y) {
		return impl.contains(x, y);
	}

	
	public Rectangle getBounds() {
		return new geogebra.web.awt.Rectangle(impl.getBounds());
	}

	
	public Rectangle2D getBounds2D() {
		return new geogebra.web.awt.Rectangle2D(impl.getBounds2D());
	}

	
	public boolean contains(Rectangle r) {
		return impl.contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	
	public boolean contains(double xTry, double yTry) {
		return impl.contains(xTry, yTry);
	}

	
	public PathIterator getPathIterator(AffineTransform affineTransform) {
		return (PathIterator) impl.getPathIterator((geogebra.web.kernel.gawt.AffineTransform) affineTransform);
	}

	
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return (PathIterator) impl.getPathIterator((geogebra.web.kernel.gawt.AffineTransform) at, flatness);
	}

	
	public boolean intersects(double x, double y, double w, double h) {
		return impl.intersects(x, y, w, h);
	}

	
	public boolean intersects(Rectangle2D r) {
		return impl.intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	
	public void subtract(geogebra.common.awt.Area shape) {
		impl.subtract(getGawtArea( shape));
	}

	
	public void intersect(geogebra.common.awt.Area shape) {
		impl.intersect(getGawtArea( shape));
	}

	
	public void exclusiveOr(geogebra.common.awt.Area shape) {
		impl.exclusiveOr(getGawtArea( shape));
	}

	
	public void add(geogebra.common.awt.Area shape) {
		impl.add(getGawtArea( shape));
	}
	
	/**
	 * Unwraps Gawt area from given common Area.
	 * @param shape
	 * @return wrapped area or null in case of wrong input type
	 */
	public static geogebra.web.kernel.gawt.Area getGawtArea(geogebra.common.awt.Area shape){
		if(!(shape instanceof Area))
			return null;
		return ((Area)shape).impl;
	}
	public boolean isEmpty() {
	    return impl.isEmpty();
    }

}
