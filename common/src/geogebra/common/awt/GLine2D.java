package geogebra.common.awt;

public abstract class GLine2D implements GShape{

	public abstract void setLine(double x1, double y1, double x2, double y2);
	public abstract GPoint2D getP1();
	public abstract GPoint2D getP2();
}
