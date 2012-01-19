package geogebra.web.awt;

import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.PathIterator;
import geogebra.common.awt.Rectangle;
import geogebra.web.kernel.gawt.Shape;

public class Rectangle2D implements geogebra.common.awt.Rectangle2D, geogebra.web.awt.Shape {
	
	private geogebra.web.kernel.gawt.Rectangle2D impl;


	public Rectangle2D() {
		impl = new geogebra.web.kernel.gawt.Rectangle2D.Double();
	}
	
	public Rectangle2D(geogebra.web.kernel.gawt.Rectangle2D bounds2d) {
		impl = bounds2d;
	}
	public Rectangle2D(int x, int y, int width, int height) {
		impl = new geogebra.web.kernel.gawt.Rectangle2D.Double(x, y, width, height);
    }

	
	public double getY() {
		return getImpl().getY();
	}

	
	public double getX() {
		return getImpl().getX();
	}

	
	public double getWidth() {
		return getImpl().getWidth();
	}

	
	public double getHeight() {
		return getImpl().getHeight();
	}

	
	public void setRect(double x, double y, double width, double height) {
		getImpl().setRect(x, y, width, height);
	}

	
	public void setFrame(double x, double y, double width, double height) {
		getImpl().setFrame(x, y, width, height);
	}

	
	public boolean intersects(double minX, double minY, double lengthX,
	        double lengthY) {
		return getImpl().intersects(minX, minY, lengthX, lengthY);
	}

	
	public boolean intersects(Rectangle r) {
		return getImpl().intersects(r.getX(), r.getY(), r.getHeight(), r.getWidth());
	}
	
	
	public boolean intersects(int i, int j, int k, int l) {
	    return getImpl().intersects(i,j,k,l);
    }

	public boolean contains(int x, int y) {
	    return getImpl().contains(x,y);
    }

	public Rectangle getBounds() {
	    return new geogebra.web.awt.Rectangle(getImpl().getBounds());
    }

	public geogebra.common.awt.Rectangle2D getBounds2D() {
		return new geogebra.web.awt.Rectangle2D(getImpl().getBounds2D());
    }

	public boolean contains(Rectangle rectangle) {
	    return getImpl().contains(geogebra.web.awt.Rectangle.getGawtRectangle(rectangle));
    }

	public boolean contains(double xTry, double yTry) {
	    return getImpl().contains(xTry, yTry);
    }

	public PathIterator getPathIterator(AffineTransform affineTransform) {
		return new geogebra.web.awt.PathIterator(getImpl().getPathIterator(geogebra.web.awt.AffineTransform.getGawtAffineTransform(affineTransform)));
    }

	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return new geogebra.web.awt.PathIterator(getImpl().getPathIterator(geogebra.web.awt.AffineTransform.getGawtAffineTransform(at), flatness));
    }

	public boolean intersects(geogebra.common.awt.Rectangle2D r) {
		return getImpl().intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

	public geogebra.common.awt.Rectangle2D createIntersection(
            geogebra.common.awt.Rectangle2D r) {
	    return new geogebra.web.awt.Rectangle2D(
	    		getImpl().createIntersection(geogebra.web.awt.Rectangle2D.getGawtRectangle2D(r)));

    }

	public static geogebra.web.kernel.gawt.Rectangle2D getGawtRectangle2D(
			geogebra.common.awt.Rectangle2D p){
		if(!(p instanceof geogebra.web.awt.Rectangle2D))
			return null;
		return ((geogebra.web.awt.Rectangle2D)p).getImpl();
    }

	public Shape getGawtShape() {
		return getImpl();
    }

	protected geogebra.web.kernel.gawt.Rectangle2D getImpl() {
	    return impl;
    }

	
	

}
