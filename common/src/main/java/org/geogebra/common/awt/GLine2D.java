package org.geogebra.common.awt;

public interface GLine2D extends GShape {

	public abstract void setLine(double x1, double y1, double x2, double y2);

	public abstract GPoint2D getP1();

	public abstract GPoint2D getP2();
}
