package geogebra.web.awt;

import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.PathIterator;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.Rectangle2D;
import geogebra.common.awt.Shape;

public class GenericShape implements Shape {

	private geogebra.web.kernel.gawt.Shape impl;
	
	private GenericShape(){}
	
	
	public boolean intersects(int x, int y, int w, int h) {
		return impl.intersects(x, y, w, h);
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
		return (PathIterator) impl.getPathIterator(geogebra.web.awt.AffineTransform.getWebTransform(affineTransform));
	}

	
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return (PathIterator) impl.getPathIterator(geogebra.web.awt.AffineTransform.getWebTransform(at),flatness);
	}

	
	public boolean intersects(double x, double y, double w, double h) {
		return impl.intersects(x, y, w, h);
	}

	
	public boolean intersects(Rectangle2D r) {
		return impl.intersects(r.getX(),r.getY(),r.getWidth(),r.getHeight());
	}


	public static geogebra.web.kernel.gawt.Shape getWebShape(Shape shape) {
	    if(!(shape instanceof geogebra.web.awt.Shape))
	    	return null;
	    return ((geogebra.web.awt.Shape)shape).getWebShape();
    }

}
