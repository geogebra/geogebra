package geogebra.html5.awt;

import geogebra.common.awt.GAffineTransform;
import geogebra.common.awt.GPathIterator;
import geogebra.common.awt.GPoint2D;
import geogebra.common.awt.GRectangle;
import geogebra.common.awt.GRectangle2D;

public class GArc2DW extends geogebra.common.awt.GArc2D implements GShapeW {

	private geogebra.web.openjdk.awt.geom.Arc2D.Double impl;

	public GArc2DW(){
		impl = new geogebra.web.openjdk.awt.geom.Arc2D.Double();
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
		return new geogebra.html5.awt.GPathIteratorW( 
				impl.getPathIterator(geogebra.html5.awt.GAffineTransformW.getGawtAffineTransform(affineTransform)));
	}

	
	public GPathIterator getPathIterator(GAffineTransform at, double flatness) {
		return new geogebra.html5.awt.GPathIteratorW(
				impl.getPathIterator(geogebra.html5.awt.GAffineTransformW.getGawtAffineTransform(at), flatness));
	}

	public boolean intersects(double x, double y, double w, double h) {
		return impl.intersects(x,y,w,h);
	}
	
	public boolean intersects(GRectangle2D r) {
		return impl.intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	@Override
	public void setArc(double d, double e, double f, double g, double degrees,
	        double degrees2, int open2) {
		impl.setArc(d,e,f,g,degrees,degrees2,open2);

	}

	@Override
	public GPoint2D getStartPoint() {
		geogebra.web.openjdk.awt.geom.Point2D p = impl.getStartPoint();
		return new GPoint2D.Double(p.getX(), p.getY());
	}

	@Override
	public GPoint2D getEndPoint() {
		geogebra.web.openjdk.awt.geom.Point2D p = impl.getEndPoint();
		return new GPoint2D.Double(p.getX(), p.getY());
	}

	@Override
	public void setArcByCenter(double x, double y, double radius, double angSt,
			double angExt, int closure) {
	    impl.setArcByCenter(x, y, radius, angSt, angExt, closure);
	    
    }

	public geogebra.web.openjdk.awt.geom.Shape getGawtShape() {
	    return impl;
    }

}
