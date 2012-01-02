package geogebra.awt;

import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.PathIterator;
import geogebra.common.awt.Point2D;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.Rectangle2D;
import geogebra.main.Application;


public class GeneralPath extends geogebra.common.awt.GeneralPath implements geogebra.awt.Shape{
	
	private java.awt.geom.GeneralPath impl = new java.awt.geom.GeneralPath();
	public GeneralPath(java.awt.geom.GeneralPath g){
		impl = g;
	}
	public GeneralPath() {
		impl = new java.awt.geom.GeneralPath();			
	}
	
	public static java.awt.geom.GeneralPath getAwtGeneralPath(geogebra.common.awt.GeneralPath gp){
		if(!(gp instanceof geogebra.awt.GeneralPath)){
			if (gp!= null) Application.debug("other type");
			return null;
		}
		return ((geogebra.awt.GeneralPath)gp).impl;
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

	public boolean intersects(int i, int j, int k, int l) {
		return impl.intersects(i,j,k,l);
	}

	public boolean contains(int x, int y) {
		return impl.contains(x,y);
	}

	public Rectangle getBounds() {
		return new geogebra.awt.Rectangle(impl.getBounds());
	}

	public Rectangle2D getBounds2D() {
		return new geogebra.awt.GenericRectangle2D(impl.getBounds2D());
	}

	public boolean contains(Rectangle rectangle) {
		return impl.contains(geogebra.awt.Rectangle.getAWTRectangle(rectangle));
	}

	public boolean contains(double xTry, double yTry) {
		return impl.contains(xTry, yTry);
	}
	public java.awt.Shape getAwtShape() {
		return impl;
	}
	public PathIterator getPathIterator(AffineTransform affineTransform) {
		// TODO Auto-generated method stub
		return new geogebra.awt.PathIterator(impl.getPathIterator(geogebra.awt.AffineTransform.getAwtAffineTransform(affineTransform)));
	}
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return new geogebra.awt.PathIterator(impl.getPathIterator(geogebra.awt.AffineTransform.getAwtAffineTransform(at), flatness));
	}
	public boolean intersects(double x, double y, double w, double h) {
		return impl.intersects(x, y, w, h);
	}
	
	@Override
	public boolean intersects(Rectangle2D r) {
		return impl.intersects(geogebra.awt.GenericRectangle2D.getAWTRectangle2D(r));
	}
	@Override
	public geogebra.common.awt.Shape createTransformedShape(
			AffineTransform affineTransform) {
		return (geogebra.common.awt.Shape) impl.createTransformedShape((java.awt.geom.AffineTransform) affineTransform);
	}
	
	@Override
	public Point2D getCurrentPoint() {
		if (impl.getCurrentPoint() == null){
			return null;
		}
		return new geogebra.awt.Point2D(impl.getCurrentPoint().getX(),impl.getCurrentPoint().getY());
	}
	@Override
	public boolean contains(Rectangle2D p) {
		return impl.contains(geogebra.awt.GenericRectangle2D.getAWTRectangle2D(p));
	}
	@Override
	public boolean contains(double arg0, double arg1, double arg2, double arg3) {
		return impl.contains(arg0, arg1, arg2, arg3);
	}
	@Override
	public boolean contains(Point2D p) {
		if (p==null) return false;
		return impl.contains(p.getX(), p.getY());
	}
}
