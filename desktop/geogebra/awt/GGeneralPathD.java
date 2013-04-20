package geogebra.awt;

import geogebra.common.awt.GAffineTransform;
import geogebra.common.awt.GPathIterator;
import geogebra.common.awt.GPoint2D;
import geogebra.common.awt.GRectangle;
import geogebra.common.awt.GRectangle2D;
import geogebra.common.awt.GShape;
import geogebra.main.AppD;


public class GGeneralPathD extends geogebra.common.awt.GGeneralPath implements geogebra.awt.GShapeD{
	
	private java.awt.geom.GeneralPath impl = new java.awt.geom.GeneralPath();
	public GGeneralPathD(java.awt.geom.GeneralPath g){
		impl = g;
	}
	public GGeneralPathD() {
		impl = new java.awt.geom.GeneralPath();			
	}
	
	public GGeneralPathD(int rule) {
		impl = new java.awt.geom.GeneralPath(rule);
	}
	public static java.awt.geom.GeneralPath getAwtGeneralPath(geogebra.common.awt.GGeneralPath gp){
		if(!(gp instanceof geogebra.awt.GGeneralPathD)){
			if (gp!= null) AppD.debug("other type");
			return null;
		}
		return ((geogebra.awt.GGeneralPathD)gp).impl;
	}

	
	@Override
	public synchronized void moveTo(float f, float g) {
		impl.moveTo(f,g);
		
	}
	
	@Override
    public synchronized void reset() {
		impl.reset();
    }
    
	@Override
    public synchronized void lineTo(float x, float y) {
    	impl.lineTo(x, y);
    }
	
	@Override
    public synchronized void closePath() {
    	impl.closePath();
    }
	
	@Override
    public synchronized void append(GShape s, boolean connect) {
		if(!(s instanceof GShapeD))
			return;
		impl.append(((GShapeD)s).getAwtShape(),connect);

    }
	
	

	public boolean intersects(int i, int j, int k, int l) {
		return impl.intersects(i,j,k,l);
	}

	public boolean contains(int x, int y) {
		return impl.contains(x,y);
	}

	public GRectangle getBounds() {
		return new geogebra.awt.GRectangleD(impl.getBounds());
	}

	public GRectangle2D getBounds2D() {
		return new geogebra.awt.GGenericRectangle2DD(impl.getBounds2D());
	}

	public boolean contains(GRectangle rectangle) {
		return impl.contains(geogebra.awt.GRectangleD.getAWTRectangle(rectangle));
	}

	public boolean contains(double xTry, double yTry) {
		return impl.contains(xTry, yTry);
	}
	public java.awt.Shape getAwtShape() {
		return impl;
	}
	public GPathIterator getPathIterator(GAffineTransform affineTransform) {
		// TODO Auto-generated method stub
		return new geogebra.awt.GPathIteratorD(impl.getPathIterator(geogebra.awt.GAffineTransformD.getAwtAffineTransform(affineTransform)));
	}
	public GPathIterator getPathIterator(GAffineTransform at, double flatness) {
		return new geogebra.awt.GPathIteratorD(impl.getPathIterator(geogebra.awt.GAffineTransformD.getAwtAffineTransform(at), flatness));
	}
	public boolean intersects(double x, double y, double w, double h) {
		return impl.intersects(x, y, w, h);
	}
	
	@Override
	public boolean intersects(GRectangle2D r) {
		return impl.intersects(geogebra.awt.GGenericRectangle2DD.getAWTRectangle2D(r));
	}
	@Override
	public geogebra.common.awt.GShape createTransformedShape(
			GAffineTransform affineTransform) {
		return (geogebra.common.awt.GShape) impl.createTransformedShape((java.awt.geom.AffineTransform) affineTransform);
	}
	
	@Override
	public GPoint2D getCurrentPoint() {
		if (impl.getCurrentPoint() == null){
			return null;
		}
		return new geogebra.awt.GPoint2DD(impl.getCurrentPoint().getX(),impl.getCurrentPoint().getY());
	}
	@Override
	public boolean contains(GRectangle2D p) {
		return impl.contains(geogebra.awt.GGenericRectangle2DD.getAWTRectangle2D(p));
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
		impl.curveTo((float)f, (float)g, (float)h, (float)i, (float)j, (float)k);
		
	}
}
