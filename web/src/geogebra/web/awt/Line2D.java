package geogebra.web.awt;

import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.PathIterator;
import geogebra.common.awt.Point2D;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.Rectangle2D;

public class Line2D extends geogebra.common.awt.Line2D implements Shape {
	private geogebra.web.kernel.gawt.Line2D impl;
	
	public Line2D() {
		impl = new geogebra.web.kernel.gawt.Line2D.Double();
	}
	
	public boolean intersects(int x, int y, int w, int h) {
		return impl.intersects(x, y, w, h);
	}

	
	public boolean contains(int x, int y) {
		return impl.contains(x,y);
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
		return impl.contains(xTry,yTry);
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

	
	public geogebra.web.kernel.gawt.Shape getGawtShape() {
		return impl;
	}

	
	public void setLine(double x1, double y1, double x2, double y2) {
		impl.setLine(x1, y1, x2, y2);
	}

	@Override
    public Point2D getP1() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public Point2D getP2() {
	    // TODO Auto-generated method stub
	    return null;
    }

}
