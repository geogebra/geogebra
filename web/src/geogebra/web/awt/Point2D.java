package geogebra.web.awt;

public class Point2D extends geogebra.common.awt.Point2D {
	
	private geogebra.web.kernel.gawt.Point2D.Double impl;

	public Point2D() {
		impl = new geogebra.web.kernel.gawt.Point2D.Double();
	}
	
	public Point2D(double x, double y) {
		impl = new geogebra.web.kernel.gawt.Point2D.Double(x, y);
	}
	
	
	public double getX() {
		return impl.getX();
	}

	
	public double getY() {
		return impl.getY();
	}

	
	public void setX(double x) {
		impl.setLocation(x, getY());
	}

	
	public void setY(double y) {
		impl.setLocation(getX(), y);
	}

	
	public double distance(geogebra.common.awt.Point2D q) {
		return impl.distance(q.getX(), q.getY());
	}

	
    public double distance(double x, double y) {
	    return impl.distance(x,y);
    }

	public static geogebra.web.kernel.gawt.Point2D.Double getGawtPoint2D(
			geogebra.common.awt.Point2D p) {
		if (p==null) return null;
		return new geogebra.web.kernel.gawt.Point2D.Double(p.getX(),p.getY());
    }

}
