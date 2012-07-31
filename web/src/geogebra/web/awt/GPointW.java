package geogebra.web.awt;


public class GPointW extends geogebra.common.awt.GPoint {
	
	private geogebra.web.openjdk.awt.geom.Point impl;

	public GPointW() {
		impl = new geogebra.web.openjdk.awt.geom.Point();
	}
	
	public GPointW(int x, int y) {
		impl = new geogebra.web.openjdk.awt.geom.Point(x, y);
	}
	
	
	public int getX() {
		return (int)impl.getX();
	}

	
	public int getY() {
		return (int)impl.getY();
	}

	
	public void setX(double x) {
		impl.setLocation(x, getY());
	}

	
	public void setY(double y) {
		impl.setLocation(getX(), y);
	}

	
	public double distance(geogebra.common.awt.GPoint q) {
		return impl.distance(q.getX(), q.getY());
	}

	
    public double distance(double x, double y) {
    	return impl.distance(x, y);
    }
}
