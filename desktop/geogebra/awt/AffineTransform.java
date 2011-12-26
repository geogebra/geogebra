package geogebra.awt;

import geogebra.common.awt.Shape;

public class AffineTransform implements geogebra.common.awt.AffineTransform {

	private java.awt.geom.AffineTransform at;

	public AffineTransform() {
		at = new java.awt.geom.AffineTransform();
	}

	public AffineTransform(java.awt.geom.AffineTransform a) {
		at = a;
	}

	java.awt.geom.AffineTransform getImpl() {
		return at;
	}

	public void setTransform(geogebra.common.awt.AffineTransform a) {
		at.setTransform(((AffineTransform)a).getImpl());
	}

	public void setTransform(double m00, double m10, double m01, double m11, double m02, double m12) {
		at.setTransform(m00, m10, m01, m11, m02, m12);
	}

	public void concatenate(geogebra.common.awt.AffineTransform a) {
		at.concatenate(((AffineTransform)a).getImpl());
	}

	public double getScaleX() {
		return at.getScaleX();
	}
	
	public double getScaleY() {
		return at.getScaleY();
	}
	
	public double getShearX() {
		return at.getShearX();
	}
	
	public double getShearY() {
		return at.getShearY();
	}

	public static java.awt.geom.AffineTransform getAwtAffineTransform(geogebra.common.awt.AffineTransform a) {
		if (!(a instanceof AffineTransform))
			return null;
		return ((AffineTransform)a).getImpl();
	}

	public Shape createTransformedShape(Object shape) {
		java.awt.Shape ret = null;
		if(shape instanceof geogebra.awt.GenericShape)
			ret = at.createTransformedShape(geogebra.awt.GenericShape.getAwtShape((geogebra.awt.GenericShape)shape));
		if(shape instanceof java.awt.Shape)
			ret = at.createTransformedShape((java.awt.Shape)shape);
		return new geogebra.awt.GenericShape(ret);
	}
}
