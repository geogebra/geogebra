package geogebra.common.awt;


public abstract class GGeneralPath implements geogebra.common.awt.GShape {

	public abstract void moveTo(float f, float g);

	public abstract void reset();

	public abstract void lineTo(float f, float g);

	public abstract void append(GShape s, boolean connect);
	
	public abstract void closePath();

	public abstract GShape createTransformedShape(GAffineTransform affineTransform);

	public abstract GPoint2D getCurrentPoint();

	public abstract boolean contains(GRectangle2D p);

	public abstract boolean contains(double arg0, double arg1, double arg2, double arg3);

	public abstract boolean intersects(GRectangle2D arg0);

	public abstract boolean contains(GPoint2D p);

	public abstract void curveTo(float parpoints, float parpoints2, float parpoints3, float parpoints4, float parpoints5, float parpoints6);

}