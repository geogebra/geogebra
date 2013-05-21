package geogebra.html5.awt;

import geogebra.common.awt.GAffineTransform;
import geogebra.common.awt.GPathIterator;
import geogebra.common.awt.GRectangle;
import geogebra.common.awt.GRectangle2D;
import geogebra.html5.openjdk.awt.geom.Shape;

public class GEllipse2DDoubleW implements geogebra.common.awt.GEllipse2DDouble,
geogebra.html5.awt.GShapeW{

	private geogebra.html5.openjdk.awt.geom.Ellipse2D.Double impl;
	
	public GEllipse2DDoubleW(){
		impl = new geogebra.html5.openjdk.awt.geom.Ellipse2D.Double();
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
	

	public void setFrame(double xUL, double yUL, double d, double e) {
		impl.setFrame(xUL,yUL,d,e);

	}

	public void setFrameFromCenter(double i, double j, double d, double e) {
		impl.setFrameFromCenter(i, j, d, e);

	}

	public Shape getGawtShape() {
		return impl;
    }

}
