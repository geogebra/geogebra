package geogebra.awt;

import java.awt.Shape;

import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.PathIterator;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.Rectangle2D;
import geogebra.common.euclidian.PathPoint;
import geogebra.main.Application;

public class GenericRectangle2D implements geogebra.awt.Rectangle2D{

	private java.awt.geom.Rectangle2D impl;
	public GenericRectangle2D(){
		impl = new java.awt.geom.Rectangle2D.Double();
	}
	
	public GenericRectangle2D(java.awt.geom.Rectangle2D bounds2d) {
		impl = bounds2d;
	}

	
	public double getY() {
		return impl.getY();
	}

	
	public double getX() {
		return impl.getX();
	}

	
	public double getWidth() {
		return impl.getWidth();
	}

	
	public double getHeight() {
		return impl.getHeight();
	}

	
	public void setRect(double x, double y, double width, double height) {
		impl.setRect(x, y, width, height);
		
	}

	
	public void setFrame(double x, double y, double width, double height) {
		impl.setFrame(x, y, width, height);
		
	}

	
	public boolean intersects(double minX, double minY, double lengthX,
			double lengthY) {
		return impl.intersects(minX, minY, lengthX, lengthY);
	}

	
	public boolean intersects(Rectangle viewRect) {
		return impl.intersects(geogebra.awt.Rectangle.getAWTRectangle(viewRect));
	}
	
	public static java.awt.geom.Rectangle2D getAWTRectangle2D(geogebra.common.awt.Rectangle2D r2d) {
		if (r2d instanceof geogebra.awt.GenericRectangle2D){
			return ((geogebra.awt.GenericRectangle2D)r2d).impl;
		} else if(r2d instanceof geogebra.awt.Rectangle){
			return ((geogebra.awt.Rectangle)r2d).impl;
		}
		if (r2d!= null) Application.debug("other type");
		return null;
		
	}

	public boolean contains(double xTry, double yTry) {
		// TODO Auto-generated method stub
		return false;
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
		return (PathIterator) impl.getPathIterator(geogebra.awt.AffineTransform.getAwtAffineTransform(affineTransform));
	}
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return (PathIterator) impl.getPathIterator(geogebra.awt.AffineTransform.getAwtAffineTransform(at), flatness);
	}

	public boolean intersects(Rectangle2D r) {
		return impl.intersects(geogebra.awt.GenericRectangle2D.getAWTRectangle2D(r));
	}
	
	public Shape getAwtShape() {
		return impl;
	}

}
