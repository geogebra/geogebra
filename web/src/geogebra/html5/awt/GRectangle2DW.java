package geogebra.html5.awt;

import geogebra.common.awt.GAffineTransform;
import geogebra.common.awt.GPathIterator;
import geogebra.common.awt.GRectangle;
import geogebra.web.openjdk.awt.geom.Shape;

public class GRectangle2DW implements geogebra.common.awt.GRectangle2D, geogebra.html5.awt.GShapeW {
	
	private geogebra.web.openjdk.awt.geom.Rectangle2D impl;


	public GRectangle2DW() {
		impl = new geogebra.web.openjdk.awt.geom.Rectangle2D.Double();
	}
	
	public GRectangle2DW(geogebra.web.openjdk.awt.geom.Rectangle2D bounds2d) {
		impl = bounds2d;
	}
	public GRectangle2DW(int x, int y, int width, int height) {
		impl = new geogebra.web.openjdk.awt.geom.Rectangle2D.Double(x, y, width, height);
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

	
	public boolean intersects(GRectangle r) {
		return getImpl().intersects(geogebra.html5.awt.GRectangleW.getGawtRectangle(r));
	}
	
	
	public boolean intersects(int i, int j, int k, int l) {
	    return getImpl().intersects(i,j,k,l);
    }

	public boolean contains(int x, int y) {
	    return getImpl().contains(x,y);
    }

	public GRectangle getBounds() {
	    return new geogebra.html5.awt.GRectangleW(getImpl().getBounds());
    }

	public geogebra.common.awt.GRectangle2D getBounds2D() {
		return new geogebra.html5.awt.GRectangle2DW(getImpl().getBounds2D());
    }

	public boolean contains(GRectangle rectangle) {
	    return getImpl().contains(geogebra.html5.awt.GRectangleW.getGawtRectangle(rectangle));
    }

	public boolean contains(double xTry, double yTry) {
	    return getImpl().contains(xTry, yTry);
    }

	public GPathIterator getPathIterator(GAffineTransform affineTransform) {
		return new geogebra.html5.awt.GPathIteratorW(getImpl().getPathIterator(geogebra.html5.awt.GAffineTransformW.getGawtAffineTransform(affineTransform)));
    }

	public GPathIterator getPathIterator(GAffineTransform at, double flatness) {
		return new geogebra.html5.awt.GPathIteratorW(getImpl().getPathIterator(geogebra.html5.awt.GAffineTransformW.getGawtAffineTransform(at), flatness));
    }

	public boolean intersects(geogebra.common.awt.GRectangle2D r) {
		return getImpl().intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

	public geogebra.common.awt.GRectangle2D createIntersection(
            geogebra.common.awt.GRectangle2D r) {
	    return new geogebra.html5.awt.GRectangle2DW(
	    		getImpl().createIntersection(geogebra.html5.awt.GRectangle2DW.getGawtRectangle2D(r)));

    }

	public static geogebra.web.openjdk.awt.geom.Rectangle2D getGawtRectangle2D(
			geogebra.common.awt.GRectangle2D p){
		if(!(p instanceof geogebra.html5.awt.GRectangle2DW))
			return null;
		return ((geogebra.html5.awt.GRectangle2DW)p).getImpl();
    }

	public Shape getGawtShape() {
		return getImpl();
    }

	protected geogebra.web.openjdk.awt.geom.Rectangle2D getImpl() {
	    return impl;
    }

	
	

}
