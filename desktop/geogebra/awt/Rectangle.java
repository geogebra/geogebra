package geogebra.awt;

import java.awt.geom.Rectangle2D;

public class Rectangle extends geogebra.common.awt.Rectangle{

	private java.awt.Rectangle impl;
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

	@Override
	public double getY() {
		return impl.getY();
	}

	@Override
	public double getX() {
		return impl.getX();
	}

	@Override
	public double getWidth() {
		return impl.getWidth();
	}

	@Override
	public double getHeight() {
		return impl.getHeight();
	}

	

	@Override
	public void setBounds(int x, int y, int width, int height) {
		impl.setBounds(x, y, width, height);
		
	}

	@Override
	public void setLocation(int x, int y) {
		impl.setLocation(x, y);
		
	}

	@Override
	public void setBounds(geogebra.common.awt.Rectangle r) {
		impl.setBounds((int)r.getX(),(int)r.getY(),(int)r.getWidth(),(int)r.getHeight());
		
	}

	@Override
	public boolean contains(double x, double y) {
		return impl.contains(x, y);
	}
	/**
	 * @param frameBounds
	 * @return
	 */
	public static java.awt.Rectangle getAWTRectangle(geogebra.common.awt.Rectangle frameBounds) {

		return ((geogebra.awt.Rectangle)frameBounds).impl;
	}
	@Override
	public boolean contains(geogebra.common.awt.Rectangle labelRectangle) {
		// TODO Auto-generated method stub
		return impl.contains(getAWTRectangle(labelRectangle));
		
		//return false;
	}
	@Override
	public void add(geogebra.common.awt.Rectangle bb) {
		impl.add(((Rectangle)bb).impl);
		
	}
	@Override
	public double getMinX() {
		return impl.getMinX();
	}
	@Override
	public double getMinY() {
		return impl.getMinY();
	}
	@Override
	public double getMaxX() {
		return impl.getMaxX();
	}
	@Override
	public double getMaxY() {
		return impl.getMinY();
	}
	@Override
	public void add(double x, double y) {
		impl.add(x, y);		
	}
	@Override
	public void setRect(double x, double y, double width, double height) {
		impl.setRect(x, y, width, height);
	}
	@Override
	public void setFrame(double x, double y, double width, double height) {
		impl.setFrame(x, y, width, height);	
	}
	@Override
	public boolean intersects(double x, double y, double lengthX,
			double lengthY) {
		return impl.intersects(x, y, lengthX, lengthY);
	}
	@Override
	public boolean intersects(geogebra.common.awt.Rectangle viewRect) {
		return impl.intersects(Rectangle.getAWTRectangle(viewRect)) ;
	}

}
