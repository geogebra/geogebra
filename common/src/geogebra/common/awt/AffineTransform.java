package geogebra.common.awt;

public interface AffineTransform {
	public void setTransform(AffineTransform a);
	public void setTransform(double m00, double m10, double m01, double m11, double m02, double m12);
	public void concatenate(AffineTransform a);
	public double getScaleX();
	public double getScaleY();
	public double getShearX();
	public double getShearY();
	public Shape createTransformedShape(Shape shape);
	public void transform(Point2D p, Point2D p2);
	public void transform(double[] labelCoords, int i, double[] labelCoords2,
			int j, int k);
	public geogebra.common.awt.AffineTransform createInverse() throws Exception;
	public void scale(double xscale, double d);
	public void translate(double ax, double ay);
	public double getTranslateX();
	public double getTranslateY();
}
