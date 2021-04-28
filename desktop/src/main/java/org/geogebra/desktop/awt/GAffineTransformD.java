package org.geogebra.desktop.awt;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GShape;

public class GAffineTransformD implements GAffineTransform {

	private AffineTransform at;

	public GAffineTransformD() {
		at = new AffineTransform();
	}

	public GAffineTransformD(AffineTransform a) {
		at = a;
	}

	AffineTransform getImpl() {
		return at;
	}

	@Override
	public void setTransform(GAffineTransform a) {
		at.setTransform(((GAffineTransformD) a).getImpl());
	}

	@Override
	public void setTransform(double m00, double m10, double m01, double m11,
			double m02, double m12) {
		at.setTransform(m00, m10, m01, m11, m02, m12);
	}

	@Override
	public void concatenate(GAffineTransform a) {
		at.concatenate(((GAffineTransformD) a).getImpl());
	}

	@Override
	public double getScaleX() {
		return at.getScaleX();
	}

	@Override
	public double getScaleY() {
		return at.getScaleY();
	}

	@Override
	public double getShearX() {
		return at.getShearX();
	}

	@Override
	public double getShearY() {
		return at.getShearY();
	}

	@Override
	public double getTranslateX() {
		return at.getTranslateX();
	}

	@Override
	public double getTranslateY() {
		return at.getTranslateY();
	}

	public static AffineTransform getAwtAffineTransform(GAffineTransform a) {
		if (!(a instanceof GAffineTransformD)) {
			return null;
		}
		return ((GAffineTransformD) a).getImpl();
	}

	@Override
	public GShape createTransformedShape(GShape shape) {
		Shape ret = null;
		ret = at.createTransformedShape(GGenericShapeD.getAwtShape(shape));
		return new GGenericShapeD(ret);
	}

	@Override
	public GPoint2D transform(GPoint2D p, GPoint2D p2) {
		Point2D point = new Point2D.Double(p.getX(), p.getY());
		Point2D point2 = null;

		if (p2 != null) {
			point2 = new Point2D.Double(p2.getX(), p2.getY());
		}

		point2 = at.transform(point, point2);

		if (p2 != null) {
			p2.setLocation(point2.getX(), point2.getY());
			return p2;
		}
		return new GPoint2D(point2.getX(), point2.getY());
	}

	@Override
	public void transform(double[] labelCoords, int i, double[] labelCoords2,
			int j, int k) {
		at.transform(labelCoords, i, labelCoords2, j, k);

	}

	@Override
	public GAffineTransform createInverse() throws Exception {
		return new GAffineTransformD(at.createInverse());
	}

	@Override
	public void scale(double xscale, double d) {
		at.scale(xscale, d);

	}

	@Override
	public void translate(double ax, double ay) {
		at.translate(ax, ay);

	}

	// @Override
	// public void transform(float[] srcPts, int srcOff, float[] dstPts,
	// int dstOff, int numPts) {
	// at.transform(srcPts, srcOff, dstPts, dstOff, numPts);
	//
	// }

	// @Override
	// public void transform(float[] srcPts, int srcOff, double[] dstPts,
	// int dstOff, int numPts) {
	// at.transform(srcPts, srcOff, dstPts, dstOff, numPts);
	//
	// }

	// public void transform(double[] doubleCoords, int pointIdx, float[]
	// coords,
	// int j, int k) {
	// at.transform(doubleCoords, pointIdx, coords, j, k);
	//
	// }

	@Override
	public void rotate(double theta) {
		at.rotate(theta);
	}

	@Override
	public boolean isIdentity() {
		return at.isIdentity();
	}

	@Override
	public void setToTranslation(double tx, double ty) {
		at.setToTranslation(tx, ty);
	}

	@Override
	public void setToScale(double sx, double sy) {
		at.setToScale(sx, sy);
	}

	@Override
	public void getMatrix(double[] flatmatrix) {
		at.getMatrix(flatmatrix);
	}

	@Override
	public void setToRotation(double theta) {
		at.setToRotation(theta);
	}

	@Override
	public void setToRotation(double theta, double x, double y) {
		at.setToRotation(theta, x, y);
	}

}
