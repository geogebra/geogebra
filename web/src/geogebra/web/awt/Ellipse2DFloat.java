package geogebra.web.awt;

import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.PathIterator;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.Rectangle2D;

public class Ellipse2DFloat implements geogebra.common.awt.Ellipse2DFloat {

	private geogebra.web.openjdk.awt.geom.Ellipse2D.Float impl;
	
	public Ellipse2DFloat(){
		impl = new geogebra.web.openjdk.awt.geom.Ellipse2D.Float();
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
		return new geogebra.web.awt.PathIterator(impl.getPathIterator(geogebra.web.awt.AffineTransform.getGawtAffineTransform(affineTransform)));
	}

	
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return new geogebra.web.awt.PathIterator(impl.getPathIterator(geogebra.web.awt.AffineTransform.getGawtAffineTransform(at), flatness));
	}

	public boolean intersects(double x, double y, double w, double h) {
		return impl.intersects(x,y,w,h);
	}
	
	public boolean intersects(Rectangle2D r) {
		return impl.intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}
}
