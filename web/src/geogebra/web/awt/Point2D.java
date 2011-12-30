package geogebra.web.awt;

public class Point2D extends geogebra.common.awt.Point2D {
	
	private geogebra.web.kernel.gawt.Point2D.Double impl;

	public Point2D() {
		impl = new geogebra.web.kernel.gawt.Point2D.Double();
	}
	
	public Point2D(double x, double y) {
		impl = new geogebra.web.kernel.gawt.Point2D.Double(x, y);
	}
	
	@Override
	public double getX() {
		return impl.getX();
	}

	@Override
	public double getY() {
		return impl.getY();
	}

	@Override
	public void setX(double x) {
		impl.setLocation(x, getY());
	}

	@Override
	public void setY(double y) {
		impl.setLocation(getX(), y);
	}

	@Override
	public double distance(geogebra.common.awt.Point2D q) {
		return impl.distance(q.getX(), q.getY());
	}

	@Override
    public double distance(double x, double y) {
	    // TODO Auto-generated method stub
	    return 0;
    }

}
