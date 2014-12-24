package geogebra.awt;

public class GPointD extends geogebra.common.awt.GPoint {

	public GPointD(double x, double y) {
		super((int) x, (int) y);
	}

	public GPointD() {
		super();
	}

	public java.awt.Point getAwtPoint() {
		return new java.awt.Point(x, y);
	}

}
