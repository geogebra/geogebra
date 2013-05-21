package geogebra.html5.awt;

public class GPoint2DW extends geogebra.common.awt.GPoint2D {
	
	private geogebra.html5.openjdk.awt.geom.Point2D.Double impl;

	public GPoint2DW() {
		impl = new geogebra.html5.openjdk.awt.geom.Point2D.Double();
	}
	
	public GPoint2DW(double x, double y) {
		impl = new geogebra.html5.openjdk.awt.geom.Point2D.Double(x, y);
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

	
	public double distance(geogebra.common.awt.GPoint2D q) {
		return impl.distance(q.getX(), q.getY());
	}

	
    public double distance(double x, double y) {
	    return impl.distance(x,y);
    }

	public static geogebra.html5.openjdk.awt.geom.Point2D.Double getGawtPoint2D(
			geogebra.common.awt.GPoint2D p) {
		if (p==null) return null;
		return new geogebra.html5.openjdk.awt.geom.Point2D.Double(p.getX(),p.getY());
    }

}
