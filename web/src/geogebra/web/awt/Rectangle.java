package geogebra.web.awt;

public class Rectangle extends geogebra.common.awt.Rectangle {

	private geogebra.web.kernel.gawt.Rectangle impl;
	
	public Rectangle() {
		impl = new geogebra.web.kernel.gawt.Rectangle();
	}
	
	public Rectangle(geogebra.common.awt.Rectangle r) {
		impl = ((Rectangle)r).impl;
	}
	
	public Rectangle(int w, int h) {
		impl = new geogebra.web.kernel.gawt.Rectangle(w, h);
	}
	
	public Rectangle(geogebra.web.kernel.gawt.Rectangle r) {
	    impl = new geogebra.web.kernel.gawt.Rectangle(r.x, r.y, r.width, r.height);    
	}

	public Rectangle(int x, int y, int w, int h) {
	    impl = new geogebra.web.kernel.gawt.Rectangle(x, y, w, h);
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
	public void setBounds(geogebra.common.awt.Rectangle rectangle) {
		impl.setBounds((int) rectangle.getX(), (int) rectangle.getY(), (int) rectangle.getWidth(), (int) rectangle.getHeight());
	}

	@Override
	public boolean contains(geogebra.common.awt.Rectangle r) {
		return impl.contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	@Override
	public void add(geogebra.common.awt.Rectangle r) {
		 int x1 = (int) Math.min(impl.x, r.getX());
	     int x2 = (int) Math.max(impl.x + impl.width, r.getX() + r.getWidth());
	     int y1 = (int) Math.min(impl.y, r.getY());
	     int y2 = (int) Math.max(impl.y + impl.height, r.getY() + r.getHeight());
	     impl.setBounds(x1, y1, x2 - x1, y2 - y1);
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
		return impl.getMaxY();
	}

	@Override
	public boolean contains(double x, double y) {
		return impl.contains(x, y);
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
		impl.setFrameFromDiagonal(x, y, width, height);
	}

	@Override
	public boolean intersects(double minX, double minY, double lengthX,
	        double lengthY) {
		return impl.intersects(minX, minY, lengthX, lengthY);
	}

	@Override
	public boolean intersects(geogebra.common.awt.Rectangle r) {
		return impl.intersects(Rectangle.getWebRectangle(r)) ;
	}
	
	public static geogebra.web.kernel.gawt.Rectangle getWebRectangle(geogebra.common.awt.Rectangle r) {
		return new geogebra.web.kernel.gawt.Rectangle((int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight());
	}
}
