package geogebra.web.awt;

import geogebra.common.awt.GAffineTransform;
import geogebra.common.awt.GPathIterator;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.GRectangle2D;

public class GQuadCurve2DW extends geogebra.common.awt.GQuadCurve2D implements GShapeW{

	private geogebra.web.openjdk.awt.geom.QuadCurve2D.Double impl;
	
	public GQuadCurve2DW(){
		impl = new geogebra.web.openjdk.awt.geom.QuadCurve2D.Double();
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

	public GRectangle2D getBounds2D() {
		return new geogebra.web.awt.GRectangle2DW(impl.getBounds2D());
    }
	

	public boolean contains(Rectangle rectangle) {
		return impl.contains(geogebra.web.awt.Rectangle.getGawtRectangle(rectangle));
	}

	public boolean contains(double xTry, double yTry) {
		return impl.contains(xTry,yTry);
	}
	

	public GPathIterator getPathIterator(GAffineTransform affineTransform) {
		return new geogebra.web.awt.GPathIteratorW(impl.getPathIterator(geogebra.web.awt.GAffineTransformW.getGawtAffineTransform(affineTransform)));
	}

	
	public GPathIterator getPathIterator(GAffineTransform at, double flatness) {
		return new geogebra.web.awt.GPathIteratorW(impl.getPathIterator(geogebra.web.awt.GAffineTransformW.getGawtAffineTransform(at), flatness));
	}

	public boolean intersects(double x, double y, double w, double h) {
		return impl.intersects(x,y,w,h);
	}
	
	public boolean intersects(GRectangle2D r) {
		return impl.intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	@Override
	public void setCurve(double[] parpoints, int i) {
		impl.setCurve(parpoints, i);
	}

	public geogebra.web.openjdk.awt.geom.Shape getGawtShape() {
	    return impl;
    }

}
