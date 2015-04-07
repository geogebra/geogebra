package org.geogebra.common.awt;

public interface GAffineTransform {
	public void setTransform(GAffineTransform a);

	public void setTransform(double m00, double m10, double m01, double m11,
			double m02, double m12);

	public void concatenate(GAffineTransform a);

	public double getScaleX();

	public double getScaleY();

	public double getShearX();

	public double getShearY();

	public GShape createTransformedShape(GShape shape);

	public GPoint2D transform(GPoint2D p, GPoint2D p2);

	public void transform(double[] labelCoords, int i, double[] labelCoords2,
			int j, int k);

	public void transform(float[] labelCoords, int i, float[] labelCoords2,
			int j, int k);

	public org.geogebra.common.awt.GAffineTransform createInverse()
			throws Exception;

	public void scale(double xscale, double d);

	public void translate(double ax, double ay);

	public double getTranslateX();

	public double getTranslateY();

	public void transform(float[] pointCoords, int pointIdx, double[] coords,
			int j, int k);

	public void rotate(double theta);
}
