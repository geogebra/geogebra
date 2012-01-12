package geogebra.web.awt;

import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.PathIterator;
import geogebra.common.awt.Point2D;
import geogebra.common.awt.Rectangle2D;
import geogebra.common.euclidian.PathPoint;

public class Rectangle implements geogebra.common.awt.Rectangle {

	private geogebra.web.kernel.gawt.Rectangle impl;
	
	public Rectangle() {
		impl = new geogebra.web.kernel.gawt.Rectangle();
	}
	
	public Rectangle(geogebra.common.awt.Rectangle r) {
		impl = ((Rectangle)r).impl;
	}
	
	public Rectangle(int w, int h) {
		impl = new geogebra.web.kernel.gawt.Rectangle(w, h);
	}
	
	public Rectangle(geogebra.web.kernel.gawt.Rectangle r) {
	    impl = new geogebra.web.kernel.gawt.Rectangle(r.x, r.y, r.width, r.height);    
	}

	public Rectangle(int x, int y, int w, int h) {
	    impl = new geogebra.web.kernel.gawt.Rectangle(x, y, w, h);
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

	
	public void setBounds(int x, int y, int width, int height) {
		impl.setBounds(x, y, width, height);
	}

	
	public void setLocation(int x, int y) {
		impl.setLocation(x, y);
	}

	
	public void setBounds(geogebra.common.awt.Rectangle rectangle) {
		impl.setBounds((int) rectangle.getX(), (int) rectangle.getY(), (int) rectangle.getWidth(), (int) rectangle.getHeight());
	}

	
	public boolean contains(geogebra.common.awt.Rectangle r) {
		return impl.contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	
	public void add(geogebra.common.awt.Rectangle r) {
		 int x1 = (int) Math.min(impl.x, r.getX());
	     int x2 = (int) Math.max(impl.x + impl.width, r.getX() + r.getWidth());
	     int y1 = (int) Math.min(impl.y, r.getY());
	     int y2 = (int) Math.max(impl.y + impl.height, r.getY() + r.getHeight());
	     impl.setBounds(x1, y1, x2 - x1, y2 - y1);
	}

	
	public double getMinX() {
		return impl.getMinX();
	}

	
	public double getMinY() {
		return impl.getMinY();
	}

	
	public double getMaxX() {
		return impl.getMaxX();
	}

	
	public double getMaxY() {
		return impl.getMaxY();
	}

	
	public boolean contains(double x, double y) {
		return impl.contains(x, y);
	}

	
	public void add(double x, double y) {
		impl.add(x, y);
	}

	
	public void setRect(double x, double y, double width, double height) {
		impl.setRect(x, y, width, height);
	}

	
	public void setFrame(double x, double y, double width, double height) {
		impl.setFrameFromDiagonal(x, y, width, height);
	}

	
	public boolean intersects(double minX, double minY, double lengthX,
	        double lengthY) {
		return impl.intersects(minX, minY, lengthX, lengthY);
	}

	
	public boolean intersects(geogebra.common.awt.Rectangle r) {
		return impl.intersects(Rectangle.getGawtRectangle(r)) ;
	}
	
	public static geogebra.web.kernel.gawt.Rectangle getGawtRectangle(geogebra.common.awt.Rectangle r) {
		return new geogebra.web.kernel.gawt.Rectangle((int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight());
	}

	/*
	public boolean contains(PathPoint prevP) {
	    // TODO Auto-generated method stub
	    return false;
    }
    */

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

	public PathIterator getPathIterator(AffineTransform affineTransform) {
		return new geogebra.web.awt.PathIterator(impl.getPathIterator(geogebra.web.awt.AffineTransform.getGawtAffineTransform(affineTransform)));
	}

	
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return new geogebra.web.awt.PathIterator(impl.getPathIterator(geogebra.web.awt.AffineTransform.getGawtAffineTransform(at), flatness));
	}

	
	public boolean intersects(Rectangle2D r) {
		return impl.intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}
	


	public boolean contains(Point2D p) {
		if (p==null) return false;
		return impl.contains(p.getX(), p.getY());
    }

	public geogebra.common.awt.Rectangle union(
            geogebra.common.awt.Rectangle bounds) {
	    return new geogebra.web.awt.Rectangle(
	    		impl.union(geogebra.web.awt.Rectangle.getGawtRectangle(bounds)));
    }

	public Rectangle2D createIntersection(Rectangle2D r) {
	    return new geogebra.web.awt.Rectangle2D(
	    		impl.createIntersection(geogebra.web.awt.Rectangle2D.getGawtRectangle2D(r)));

    }

	public void setSize(int width, int height) {
	    impl.setSize(width, height);
    }
}
