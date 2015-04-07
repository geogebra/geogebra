package org.geogebra.common.geogebra3D.euclidianFor3D;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCurveCartesian3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.CurveEvaluable;
import org.geogebra.common.kernel.roots.RealRootFunction;

/**
 * For 3D curve, evaluator that returns NaN when z != 0
 * 
 * @author mathieu
 *
 */
public class CurveEvaluableFor3D implements CurveEvaluable {

	private GeoCurveCartesian3D parent;
	private double[] parentOut;

	private FunMustBeZero funZ;

	/**
	 * Function that returns NaN if parent z != 0
	 * 
	 * @author mathieu
	 *
	 */
	private class FunMustBeZero implements RealRootFunction {

		private RealRootFunction parentFun;

		public FunMustBeZero() {
		}

		public void setParentFun(RealRootFunction parentFun) {
			this.parentFun = parentFun;
		}

		public double evaluate(double t) {
			double z = parentFun.evaluate(t);
			if (!Kernel.isZero(z)) {
				return Double.NaN;
			}

			return 0;
		}

	}

	/**
	 * constructor
	 * 
	 * @param parent
	 *            curve
	 */
	public CurveEvaluableFor3D(GeoCurveCartesian3D parent) {
		this.parent = parent;
		parentOut = new double[3];
		funZ = new FunMustBeZero();
	}

	public void evaluateCurve(double t, double[] out) {
		parent.evaluateCurve(t, parentOut);
		double z = parentOut[2];
		if (Double.isInfinite(z) || Double.isNaN(z) || !Kernel.isZero(z)) {
			out[0] = Double.NaN;
		} else {
			for (int i = 0; i < out.length; i++) {
				out[i] = parentOut[i];
			}
		}

	}

	public double[] getDefinedInterval(double a, double b) {
		funZ.setParentFun(parent.getFun(2));
		return GeoCurveCartesian3D.getDefinedInterval(a, b, parent.getFun(0),
				parent.getFun(1), funZ);
	}

	public double[] newDoubleArray() {
		return new double[2];
	}

	public double getMinParameter() {
		return parent.getMinParameter();
	}

	public double getMaxParameter() {
		return parent.getMaxParameter();
	}

	public boolean getTrace() {
		return parent.getTrace();
	}

	public boolean isClosedPath() {
		return parent.isClosedPath();
	}

	public boolean isFunctionInX() {
		return parent.isFunctionInX();
	}

	public GeoElement toGeoElement() {
		return parent.toGeoElement();
	}

	public double distanceMax(double[] p1, double[] p2) {
		return Math.max(Math.abs(p1[0] - p2[0]), Math.abs(p1[1] - p2[1]));
	}

}
