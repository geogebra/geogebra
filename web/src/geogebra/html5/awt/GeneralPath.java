package geogebra.html5.awt;

import geogebra.common.awt.GAffineTransform;
import geogebra.common.awt.GPathIterator;
import geogebra.common.awt.GPoint2D;
import geogebra.common.awt.GRectangle;
import geogebra.common.awt.GRectangle2D;
import geogebra.common.awt.GShape;

public class GeneralPath extends geogebra.common.awt.GGeneralPath implements
        GShapeW {
	
    static final byte SEG_MOVETO  = (byte) GPathIterator.SEG_MOVETO;
    static final byte SEG_LINETO  = (byte) GPathIterator.SEG_LINETO;
    static final byte SEG_QUADTO  = (byte) GPathIterator.SEG_QUADTO;
    static final byte SEG_CUBICTO = (byte) GPathIterator.SEG_CUBICTO;
    static final byte SEG_CLOSE   = (byte) GPathIterator.SEG_CLOSE;

    private geogebra.web.openjdk.awt.geom.GeneralPath impl = new geogebra.web.openjdk.awt.geom.GeneralPath();

	public GeneralPath() {
		impl = new geogebra.web.openjdk.awt.geom.GeneralPath();
	}
	
	public GeneralPath(geogebra.web.openjdk.awt.geom.GeneralPath g) {
		impl = g;
	}
	
	
	public GeneralPath(int rule) {
		impl = new geogebra.web.openjdk.awt.geom.GeneralPath(rule);
    }

	public boolean intersects(int rx, int ry, int rw, int rh) {
		return impl.intersects(rx, ry, rw, rh);
	}

	
	public boolean contains(int x, int y) {
		return impl.contains(x, y);
	}

	
	public GRectangle getBounds() {
		return new geogebra.html5.awt.GRectangleW(impl.getBounds());
	}

	
	public GRectangle2D getBounds2D() {
		return new geogebra.html5.awt.GRectangle2DW(impl.getBounds2D());
	}

	
	public boolean contains(GRectangle r) {
		return impl.contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	
	public boolean contains(double xTry, double yTry) {
		return impl.contains(xTry, yTry);
	}

	
	public GPathIterator getPathIterator(GAffineTransform affineTransform) {
		return new geogebra.html5.awt.GPathIteratorW(impl.getPathIterator(geogebra.html5.awt.GAffineTransformW.getGawtAffineTransform(affineTransform)));
	}

	
	public GPathIterator getPathIterator(GAffineTransform at, double flatness) {
		return new geogebra.html5.awt.GPathIteratorW(impl.getPathIterator(geogebra.html5.awt.GAffineTransformW.getGawtAffineTransform(at), flatness));
	}

	
	public boolean intersects(double x, double y, double w, double h) {
		return impl.intersects(x, y, w, h);
	}

	
	public boolean intersects(GRectangle2D r) {
		return impl.intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	
	public geogebra.web.openjdk.awt.geom.Shape getGawtShape() {
		return impl;
	}

	
	@Override
    public void moveTo(float x, float y) {
		impl.moveTo(x, y);
	}

	
	@Override
    public void reset() {
		impl.reset();
	}

	
	@Override
    public void lineTo(float x, float y) {
		impl.lineTo(x, y);
	}

	
	@Override
    public void closePath() {
		impl.closePath();
	}
	
	@Override
	public void append(GShape s, boolean connect){
		impl.append(((GShapeW) s).getGawtShape(), connect);
	}

	
	@Override
    public geogebra.common.awt.GShape createTransformedShape(
	        GAffineTransform affineTransform) {
		return (geogebra.common.awt.GShape) impl.createTransformedShape(geogebra.html5.awt.GAffineTransformW.getGawtAffineTransform(affineTransform));
	}

	
	@Override
    public GPoint2D getCurrentPoint() {
		if(impl.getCurrentPoint()==null)
			return null;
		return new geogebra.html5.awt.GPoint2DW(impl.getCurrentPoint().getX(),impl.getCurrentPoint().getY());
	}

	@Override
    public boolean contains(GRectangle2D p) {
	    return impl.contains(geogebra.html5.awt.GRectangle2DW.getGawtRectangle2D(p));
    }

	@Override
    public boolean contains(double arg0, double arg1, double arg2, double arg3) {
	    return impl.contains(arg0, arg1, arg2, arg3);
    }

	@Override
    public boolean contains(GPoint2D p) {
		if (p==null) return false;
		return impl.contains(p.getX(), p.getY());
    }

	@Override
    public void curveTo(float f, float g, float h, float i, float j, float k) {
		impl.curveTo(f, g, h, i, j, k);
	    
    }

	public void quadTo(float f, float g, float h, float i) {
		impl.quadTo(f,g,h,i);
	}
}
