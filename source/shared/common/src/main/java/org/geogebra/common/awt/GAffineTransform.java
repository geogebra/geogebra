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

	public GPoint2D transform(GPoint2D src, GPoint2D dest);

	public void transform(double[] labelCoords, int i, double[] labelCoords2,
			int j, int k);

	// public void transform(float[] labelCoords, int i, float[] labelCoords2,
	// int j, int k);

	public GAffineTransform createInverse() throws Exception;

	public void scale(double xscale, double yscale);

	public void translate(double ax, double ay);

	public double getTranslateX();

	public double getTranslateY();

	// public void transform(float[] pointCoords, int pointIdx, double[] coords,
	// int j, int k);

	public void rotate(double theta);

	public boolean isIdentity();

	public void setToTranslation(double tx, double ty);

	public void setToScale(double sx, double sy);

	public void getMatrix(double[] m);

	public void setToRotation(double theta);

	public void setToRotation(double theta, double x, double y);

	// public void transform(double[] doubleCoords, int pointIdx, float[]
	// coords,
	// int j, int k);
}
