package geogebra.html5.awt;

import geogebra.common.awt.GPoint2D;

public class GRectangleW extends geogebra.html5.awt.GRectangle2DW implements geogebra.common.awt.GRectangle {
	
	private geogebra.html5.openjdk.awt.geom.Rectangle impl;
	
	public GRectangleW() {
		impl = new geogebra.html5.openjdk.awt.geom.Rectangle();
	}
	
	public GRectangleW(geogebra.common.awt.GRectangle r) {
		impl = ((GRectangleW)r).impl;
	}
	
	public GRectangleW(int w, int h) {
		impl = new geogebra.html5.openjdk.awt.geom.Rectangle(w, h);
	}
	
	public GRectangleW(geogebra.html5.openjdk.awt.geom.Rectangle r) {
	    impl = new geogebra.html5.openjdk.awt.geom.Rectangle(r.x, r.y, r.width, r.height);    
	}

	public GRectangleW(int x, int y, int w, int h) {
	    impl = new geogebra.html5.openjdk.awt.geom.Rectangle(x, y, w, h);
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

	
	public void setBounds(int x, int y, int width, int height) {
		impl.setBounds(x, y, width, height);
	}

	
	public void setLocation(int x, int y) {
		impl.setLocation(x, y);
	}

	
	public void setBounds(geogebra.common.awt.GRectangle rectangle) {
		impl.setBounds((int) rectangle.getX(), (int) rectangle.getY(), (int) rectangle.getWidth(), (int) rectangle.getHeight());
	}

	
	@Override
    public boolean contains(geogebra.common.awt.GRectangle r) {
		return impl.contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	
	public void add(geogebra.common.awt.GRectangle r) {
		 int x1 = (int) Math.min(impl.x, r.getX());
	     int x2 = (int) Math.max(impl.x + impl.width, r.getX() + r.getWidth());
	     int y1 = (int) Math.min(impl.y, r.getY());
	     int y2 = (int) Math.max(impl.y + impl.height, r.getY() + r.getHeight());
	     impl.setBounds(x1, y1, x2 - x1, y2 - y1);
	}

	
	public double getMinX() {
		return impl.getMinX();
	}

	
	public double getMinY() {
		return impl.getMinY();
	}

	
	public double getMaxX() {
		return impl.getMaxX();
	}

	
	public double getMaxY() {
		return impl.getMaxY();
	}
	
	public void add(double x, double y) {
		impl.add(x, y);
	}
	
	public static geogebra.html5.openjdk.awt.geom.Rectangle getGawtRectangle(geogebra.common.awt.GRectangle r) {
		if(!(r instanceof GRectangleW))
			return null;
		return ((GRectangleW)r).impl;
	}

	public boolean contains(GPoint2D p) {
		if (p==null) return false;
		return impl.contains(p.getX(), p.getY());
    }

	public geogebra.common.awt.GRectangle union(
            geogebra.common.awt.GRectangle bounds) {
	    return new geogebra.html5.awt.GRectangleW(
	    		impl.union(geogebra.html5.awt.GRectangleW.getGawtRectangle(bounds)));
    }

	
	public void setSize(int width, int height) {
	    impl.setSize(width, height);
    }
	
	@Override
    protected geogebra.html5.openjdk.awt.geom.Rectangle getImpl(){
		return impl;
	}
}
