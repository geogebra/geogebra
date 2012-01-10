package geogebra.web.awt;

import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.PathIterator;
import geogebra.common.awt.Point2D;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.Rectangle2D;

public class Arc2D extends geogebra.common.awt.Arc2D {

	private geogebra.web.kernel.gawt.Arc2D.Double impl;

	public Arc2D(){
		impl = new geogebra.web.kernel.gawt.Arc2D.Double();
	}
	
	public boolean intersects(int i, int j, int k, int l) {
	    return impl.intersects(i,j,k,l);
    }

	public boolean contains(int x, int y) {
	    return impl.contains(x,y);
    }

	public geogebra.common.awt.Rectangle getBounds() {
	    return new geogebra.web.awt.Rectangle(impl.getBounds());
    }

	public Rectangle2D getBounds2D() {
		return new geogebra.web.awt.Rectangle2D(impl.getBounds2D());
    }
	

	public boolean contains(Rectangle rectangle) {
		return impl.contains(geogebra.web.awt.Rectangle.getGawtRectangle(rectangle));
	}

	public boolean contains(double xTry, double yTry) {
		return impl.contains(xTry,yTry);
	}
	

	public PathIterator getPathIterator(AffineTransform affineTransform) {
		return (PathIterator) impl.getPathIterator(geogebra.web.awt.AffineTransform.getGawtAffineTransform(affineTransform));
	}

	
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return (PathIterator) impl.getPathIterator(geogebra.web.awt.AffineTransform.getGawtAffineTransform(at), flatness);
	}

	public boolean intersects(double x, double y, double w, double h) {
		return impl.intersects(x,y,w,h);
	}
	
	public boolean intersects(Rectangle2D r) {
		return impl.intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	@Override
	public void setArc(double d, double e, double f, double g, double degrees,
	        double degrees2, int open2) {
		// TODO Auto-generated method stub

	}

	@Override
	public Point2D getStartPoint() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Point2D getEndPoint() {
		// TODO Auto-generated method stub
		return null;
	}

}
