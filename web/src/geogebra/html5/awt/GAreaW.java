package geogebra.html5.awt;

import geogebra.common.awt.GAffineTransform;
import geogebra.common.awt.GPathIterator;
import geogebra.common.awt.GRectangle;
import geogebra.common.awt.GRectangle2D;
import geogebra.common.awt.GShape;

/**
 * Wrapper for gawt Area
 * @author Gabor
 *
 */
public class GAreaW implements geogebra.common.awt.GArea, geogebra.html5.awt.GShapeW {
	
	private geogebra.web.openjdk.awt.geom.Area impl;
	/**
	 * Creates new area
	 */
	public GAreaW() {
		impl = new geogebra.web.openjdk.awt.geom.Area();
	}
	/**
	 * Creates new area
	 */
	public GAreaW(GShape shape) {
		impl = new geogebra.web.openjdk.awt.geom.Area(geogebra.html5.awt.GenericShape.getGawtShape(shape));
	}

	public boolean intersects(int i, int j, int k, int l) {
		return impl.intersects(i, j, k, l);
	}

	
	public boolean contains(int x, int y) {
		return impl.contains(x, y);
	}

	
	public GRectangle getBounds() {
		return new geogebra.html5.awt.GRectangleW(impl.getBounds());
	}

	
	public GRectangle2D getBounds2D() {
		return new geogebra.html5.awt.GRectangle2DW(impl.getBounds2D());
	}

	
	public boolean contains(GRectangle r) {
		return impl.contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	
	public boolean contains(double xTry, double yTry) {
		return impl.contains(xTry, yTry);
	}

	
	public GPathIterator getPathIterator(GAffineTransform affineTransform) {
		return new geogebra.html5.awt.GPathIteratorW(
				impl.getPathIterator((geogebra.web.openjdk.awt.geom.AffineTransform) affineTransform));
	}

	
	public GPathIterator getPathIterator(GAffineTransform at, double flatness) {
		return new geogebra.html5.awt.GPathIteratorW(
				impl.getPathIterator((geogebra.web.openjdk.awt.geom.AffineTransform) at, flatness));
	}

	
	public boolean intersects(double x, double y, double w, double h) {
		return impl.intersects(x, y, w, h);
	}

	
	public boolean intersects(GRectangle2D r) {
		return impl.intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	
	public void subtract(geogebra.common.awt.GArea shape) {
		impl.subtract(getGawtArea( shape));
	}

	
	public void intersect(geogebra.common.awt.GArea shape) {
		impl.intersect(getGawtArea( shape));
	}

	
	public void exclusiveOr(geogebra.common.awt.GArea shape) {
		impl.exclusiveOr(getGawtArea( shape));
	}

	
	public void add(geogebra.common.awt.GArea shape) {
		impl.add(getGawtArea( shape));
	}
	
	/**
	 * Unwraps Gawt area from given common Area.
	 * @param shape
	 * @return wrapped area or null in case of wrong input type
	 */
	public static geogebra.web.openjdk.awt.geom.Area getGawtArea(geogebra.common.awt.GArea shape){
		if(!(shape instanceof GAreaW))
			return null;
		return ((GAreaW)shape).impl;
	}
	public boolean isEmpty() {
	    return impl.isEmpty();
    }
	
	public geogebra.web.openjdk.awt.geom.Shape getGawtShape() {
	    return impl;
    }

}
