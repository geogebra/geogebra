package geogebra.web.awt;

import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.PathIterator;
import geogebra.common.awt.Rectangle;

public class Rectangle2D implements geogebra.common.awt.Rectangle2D {
	
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
		return impl.getY();
	}

	
	public double getX() {
		return impl.getX();
	}

	
	public double getWidth() {
		return impl.getWidth();
	}

	
	public double getHeight() {
		return impl.getHeight();
	}

	
	public void setRect(double x, double y, double width, double height) {
		impl.setRect(x, y, width, height);
	}

	
	public void setFrame(double x, double y, double width, double height) {
		impl.setFrame(x, y, width, height);
	}

	
	public boolean intersects(double minX, double minY, double lengthX,
	        double lengthY) {
		return impl.intersects(minX, minY, lengthX, lengthY);
	}

	
	public boolean intersects(Rectangle r) {
		return impl.intersects(r.getX(), r.getY(), r.getHeight(), r.getWidth());
	}

	public boolean intersects(int i, int j, int k, int l) {
	    // TODO Auto-generated method stub
	    return false;
    }

	public boolean contains(int x, int y) {
	    // TODO Auto-generated method stub
	    return false;
    }

	public Rectangle getBounds() {
	    // TODO Auto-generated method stub
	    return null;
    }

	public geogebra.common.awt.Rectangle2D getBounds2D() {
	    // TODO Auto-generated method stub
	    return null;
    }

	public boolean contains(Rectangle rectangle) {
	    // TODO Auto-generated method stub
	    return false;
    }

	public boolean contains(double xTry, double yTry) {
	    // TODO Auto-generated method stub
	    return false;
    }

	public PathIterator getPathIterator(AffineTransform affineTransform) {
	    // TODO Auto-generated method stub
	    return null;
    }

	public PathIterator getPathIterator(AffineTransform at, double flatness) {
	    // TODO Auto-generated method stub
	    return null;
    }

	public boolean intersects(geogebra.common.awt.Rectangle2D r) {
	    // TODO Auto-generated method stub
	    return false;
    }

}
