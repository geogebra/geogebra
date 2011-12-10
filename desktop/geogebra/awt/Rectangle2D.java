package geogebra.awt;

public class Rectangle2D extends geogebra.common.awt.Rectangle2D{

	private java.awt.geom.Rectangle2D impl;
	public Rectangle2D(){
		impl = new java.awt.geom.Rectangle2D.Double();
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
	public void setRect(double x, double y, double width, double height) {
		impl.setRect(x, y, width, height);
		
	}

}
