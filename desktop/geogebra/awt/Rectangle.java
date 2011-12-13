package geogebra.awt;

public class Rectangle extends geogebra.common.awt.Rectangle{

	private java.awt.Rectangle impl;
	public Rectangle(){
		impl = new java.awt.Rectangle();
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
	public boolean contains(int x, int y) {
		return impl.contains(x, y);
	}
	/**
	 * @param frameBounds
	 * @return
	 */
	public static java.awt.Rectangle getAWTRectangle(geogebra.common.awt.Rectangle frameBounds) {

		return ((geogebra.awt.Rectangle)frameBounds).impl;
	}

}
