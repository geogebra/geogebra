package geogebra.common.awt;


public abstract class GeneralPath implements geogebra.common.awt.Shape {

	public abstract void moveTo(float f, float g);

	public abstract void reset();

	public abstract void lineTo(float f, float g);

	public abstract void closePath();

	public abstract Shape createTransformedShape(AffineTransform affineTransform);

	public abstract Point2D getCurrentPoint();

	public abstract boolean contains(Rectangle2D p);

	public abstract boolean contains(double arg0, double arg1, double arg2, double arg3);

	public abstract boolean intersects(Rectangle2D arg0);

	public abstract boolean contains(Point2D p);

}