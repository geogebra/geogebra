package org.geogebra.common.util.clipper;

public class DoubleRect {
	public double left;
    public double top;
    public double right;
    public double bottom;

    public DoubleRect() {

    }

    public DoubleRect( double l, double t, double r, double b ) {
        left = l;
        top = t;
        right = r;
        bottom = b;
    }

    public DoubleRect( DoubleRect ir ) {
        left = ir.left;
        top = ir.top;
        right = ir.right;
        bottom = ir.bottom;
    }
}
