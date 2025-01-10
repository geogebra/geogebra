package org.geogebra.common.awt;

public interface GGeneralPath extends GShape {

	public abstract void moveTo(double x, double y);

	public abstract void reset();

	public abstract void lineTo(double x, double y);

	public abstract void append(GShape s, boolean connect);

	public abstract void closePath();

	public abstract GShape createTransformedShape(
			GAffineTransform affineTransform);

	public abstract GPoint2D getCurrentPoint();

	@Override
	public abstract boolean contains(GRectangle2D p);

	public abstract boolean contains(double x, double y, double w, double h);

	@Override
	public abstract boolean intersects(GRectangle2D arg0);

	public abstract boolean contains(GPoint2D p);

	public abstract void curveTo(double x1, double y1, double x2, double y2,
			double x3, double y3);

	public abstract void quadTo(double x, double y, double x1, double y1);

}