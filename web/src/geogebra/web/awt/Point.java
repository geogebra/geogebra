package geogebra.web.awt;

public class Point extends geogebra.common.awt.Point {
	
	private geogebra.web.kernel.gawt.Point impl;

	public Point() {
		impl = new geogebra.web.kernel.gawt.Point();
	}
	
	public Point(int x, int y) {
		impl = new geogebra.web.kernel.gawt.Point(x, y);
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

	
	public double distance(geogebra.common.awt.Point q) {
		return impl.distance(q.getX(), q.getY());
	}

	
    public double distance(double x, double y) {
	    // TODO Auto-generated method stub
	    return 0;
    }

}
