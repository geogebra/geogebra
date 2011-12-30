package geogebra.common.awt;

import geogebra.common.euclidian.PathPoint;

public interface AffineTransform {
	public void setTransform(AffineTransform a);
	public void setTransform(double m00, double m10, double m01, double m11, double m02, double m12);
	public void concatenate(AffineTransform a);
	public double getScaleX();
	public double getScaleY();
	public double getShearX();
	public double getShearY();
	public Shape createTransformedShape(Object shape);
	public void transform(PathPoint p, PathPoint p2);
}
