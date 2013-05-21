package geogebra.html5.awt;

import geogebra.common.awt.GAffineTransform;
import geogebra.common.awt.GPathIterator;
import geogebra.common.awt.GRectangle;
import geogebra.common.awt.GRectangle2D;

public class GEllipse2DFloatW implements geogebra.common.awt.GEllipse2DFloat, geogebra.html5.awt.GShapeW {

	private geogebra.html5.openjdk.awt.geom.Ellipse2D.Float impl;

	public GEllipse2DFloatW(int i, int j, int k, int l) {
		impl = new geogebra.html5.openjdk.awt.geom.Ellipse2D.Float(i, j, k, l);
	}

	public GEllipse2DFloatW(){
		impl = new geogebra.html5.openjdk.awt.geom.Ellipse2D.Float();
	}
	
	public boolean intersects(int i, int j, int k, int l) {
	    return impl.intersects(i,j,k,l);
    }

	public boolean contains(int x, int y) {
	    return impl.contains(x,y);
    }

	public geogebra.common.awt.GRectangle getBounds() {
	    return new geogebra.html5.awt.GRectangleW(impl.getBounds());
    }

	public GRectangle2D getBounds2D() {
		return new geogebra.html5.awt.GRectangle2DW(impl.getBounds2D());
    }
	

	public boolean contains(GRectangle rectangle) {
		return impl.contains(geogebra.html5.awt.GRectangleW.getGawtRectangle(rectangle));
	}

	public boolean contains(double xTry, double yTry) {
		return impl.contains(xTry,yTry);
	}
	

	public GPathIterator getPathIterator(GAffineTransform affineTransform) {
		return new geogebra.html5.awt.GPathIteratorW(impl.getPathIterator(geogebra.html5.awt.GAffineTransformW.getGawtAffineTransform(affineTransform)));
	}

	
	public GPathIterator getPathIterator(GAffineTransform at, double flatness) {
		return new geogebra.html5.awt.GPathIteratorW(impl.getPathIterator(geogebra.html5.awt.GAffineTransformW.getGawtAffineTransform(at), flatness));
	}

	public boolean intersects(double x, double y, double w, double h) {
		return impl.intersects(x,y,w,h);
	}
	
	public boolean intersects(GRectangle2D r) {
		return impl.intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	public geogebra.html5.openjdk.awt.geom.Shape getGawtShape() {
		return impl;
	}
}
