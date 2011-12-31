package geogebra.awt;

import geogebra.common.awt.Rectangle;
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
		Application.debug("other type");
		return null;
		
	}
}
