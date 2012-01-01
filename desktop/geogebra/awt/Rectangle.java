package geogebra.awt;

import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.PathIterator;
import geogebra.common.awt.Rectangle2D;
import geogebra.common.euclidian.PathPoint;
import geogebra.main.Application;

import java.awt.Shape;

public class Rectangle implements geogebra.awt.Rectangle2D, geogebra.common.awt.Rectangle{

	java.awt.Rectangle impl;
	public Rectangle(){
		impl = new java.awt.Rectangle();
	}
	public Rectangle(geogebra.common.awt.Rectangle r){
		impl = ((Rectangle)r).impl;
	}
	public Rectangle(int x, int y, int w, int h){
		impl = new java.awt.Rectangle(x,y,w,h);
	}
	public Rectangle(int w, int h){
		impl = new java.awt.Rectangle(w,h);
	}
	
	public Rectangle(java.awt.Rectangle frameBounds) {
		impl = frameBounds;
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

	

	
	public void setBounds(int x, int y, int width, int height) {
		impl.setBounds(x, y, width, height);
		
	}

	
	public void setLocation(int x, int y) {
		impl.setLocation(x, y);
		
	}

	
	public void setBounds(geogebra.common.awt.Rectangle r) {
		impl.setBounds((int)r.getX(),(int)r.getY(),(int)r.getWidth(),(int)r.getHeight());
		
	}

	
	public boolean contains(double x, double y) {
		return impl.contains(x, y);
	}
	/**
	 * @param rect Common rectangle to unwrap
	 * @return java.awt.Rectangle from the wrapper or null for wrong input type 
	 */
	public static java.awt.Rectangle getAWTRectangle(geogebra.common.awt.Rectangle rect) {
		if(!(rect instanceof Rectangle)){
			if (rect!= null) Application.debug("other type");
			return null;
		}
		return ((Rectangle)rect).impl;
	}
	
	public boolean contains(geogebra.common.awt.Rectangle labelRectangle) {
		// TODO Auto-generated method stub
		return impl.contains(getAWTRectangle(labelRectangle));
		
		//return false;
	}
	
	public void add(geogebra.common.awt.Rectangle bb) {
		impl.add(((Rectangle)bb).impl);
		
	}
	
	public double getMinX() {
		return impl.getMinX();
	}
	
	public double getMinY() {
		return impl.getMinY();
	}
	
	public double getMaxX() {
		return impl.getMaxX();
	}
	
	public double getMaxY() {
		return impl.getMinY();
	}
	
	public void add(double x, double y) {
		impl.add(x, y);		
	}
	
	public void setRect(double x, double y, double width, double height) {
		impl.setRect(x, y, width, height);
	}
	
	public void setFrame(double x, double y, double width, double height) {
		impl.setFrame(x, y, width, height);	
	}
	
	public boolean intersects(double x, double y, double lengthX,
			double lengthY) {
		return impl.intersects(x, y, lengthX, lengthY);
	}
	
	public boolean intersects(geogebra.common.awt.Rectangle viewRect) {
		return impl.intersects(Rectangle.getAWTRectangle(viewRect)) ;
	}
	public boolean contains(PathPoint prevP) {
		return impl.contains(Point2D.getAwtPoint2D(prevP));
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
