package org.geogebra.common.awt;

public interface GGeneralPath extends GShape {

	public abstract void moveTo(double f, double g);

	public abstract void reset();

	public abstract void lineTo(double f, double g);

	public abstract void append(GShape s, boolean connect);

	public abstract void closePath();

	public abstract GShape createTransformedShape(
			GAffineTransform affineTransform);

	public abstract GPoint2D getCurrentPoint();

	@Override
	public abstract boolean contains(GRectangle2D p);

	public abstract boolean contains(double arg0, double arg1, double arg2,
			double arg3);

	@Override
	public abstract boolean intersects(GRectangle2D arg0);

	public abstract boolean contains(GPoint2D p);

	public abstract void curveTo(double parpoints, double parpoints2,
			double parpoints3, double parpoints4, double parpoints5,
			double parpoints6);

}