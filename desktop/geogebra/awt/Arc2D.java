package geogebra.awt;

import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.PathIterator;
import geogebra.common.awt.Point2D;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.Rectangle2D;

import java.awt.Shape;

public class Arc2D extends geogebra.common.awt.Arc2D implements RectangularShape{

	private java.awt.geom.Arc2D.Double impl;
	
	public Arc2D(){
		impl = new java.awt.geom.Arc2D.Double();
	}

	public boolean contains(double xTry, double yTry) {
		return impl.contains(xTry, yTry);
	}
	
	public boolean intersects(double x, double y, double lengthX,
			double lengthY) {
		return impl.intersects(x, y, lengthX, lengthY);
	}
	
	public boolean intersects(int i, int j, int k, int l) {
		return impl.intersects(i, j, k, l);
	}
	public boolean contains(int x, int y) {
		return impl.contains(x,y);
	}
	
	public geogebra.awt.Rectangle getBounds() {
		return new geogebra.awt.Rectangle(impl.getBounds());
	}
	public Rectangle2D getBounds2D() {
		return new geogebra.awt.GenericRectangle2D(impl.getBounds2D());
	}
	public boolean contains(Rectangle rectangle) {
		return impl.contains(geogebra.awt.Rectangle.getAWTRectangle(rectangle));
	}
	
	public PathIterator getPathIterator(AffineTransform affineTransform) {
		return new geogebra.awt.PathIterator(impl.getPathIterator(geogebra.awt.AffineTransform.getAwtAffineTransform(affineTransform)));
	}
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return new geogebra.awt.PathIterator(impl.getPathIterator(geogebra.awt.AffineTransform.getAwtAffineTransform(at), flatness));
	}

	public boolean intersects(Rectangle2D r) {
		return impl.intersects(geogebra.awt.GenericRectangle2D.getAWTRectangle2D(r));
	}

	public Shape getAwtShape() {
		return impl;
	}

	@Override
	public void setArc(double x, double y, double w, double h,
			   double angSt, double angExt, int closure) {
		impl.setArc(x, y, w, h, angSt, angExt, closure);
		
	}

	@Override
	public Point2D getStartPoint() {
		//impl.getStartPoint() return-type is java.awt.geom.Point2D
		//but it returns with java.awt.geom.Point2D.Double
		return new geogebra.awt.Point2D((java.awt.geom.Point2D.Double)impl.getStartPoint());
	}

	@Override
	public Point2D getEndPoint() {
		return new geogebra.awt.Point2D((java.awt.geom.Point2D.Double)impl.getEndPoint());
	}

	@Override
	public void setArcByCenter(double x, double y, double radius, double angSt,
			double angExt, int closure) {
		impl.setArcByCenter(x, y, radius, angSt, angExt, closure);
		
	}
	

}
