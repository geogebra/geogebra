package org.geogebra.common.geogebra3D.euclidianFor3D;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.geogebra.common.geogebra3D.euclidianForPlane.EuclidianViewForPlaneCompanion;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCurveCartesian3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.CurveEvaluable;

/**
 * For 3D curve, evaluator that returns NaN when z != 0
 * 
 * @author mathieu
 *
 */
public class CurveEvaluableForPlane implements CurveEvaluable {

	GeoCurveCartesian3D parent;
	Coords parentOut, parentOutInView;
	EuclidianViewForPlaneCompanion companion;

	private FunMustBeZero funZ;

	/**
	 * Function that returns NaN if parent z != 0
	 * 
	 * @author mathieu
	 *
	 */
	private class FunMustBeZero implements UnivariateFunction {

		protected FunMustBeZero() {
		}

		@Override
		public double value(double t) {

			parent.evaluateCurve(t, parentOut.val);
			parentOutInView = companion.getCoordsForView(parentOut);
			double z = parentOutInView.getZ();

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
	 * @param companion
	 *            view for plane companion
	 */
	public CurveEvaluableForPlane(GeoCurveCartesian3D parent,
			EuclidianViewForPlaneCompanion companion) {
		this.parent = parent;
		this.companion = companion;

		parentOut = Coords.createInhomCoorsInD3();
		funZ = new FunMustBeZero();
	}

	@Override
	public void evaluateCurve(double t, double[] out) {
		parent.evaluateCurve(t, parentOut.val);
		parentOutInView = companion.getCoordsForView(parentOut);
		double z = parentOutInView.getZ();
		if (Double.isInfinite(z) || Double.isNaN(z) || !Kernel.isZero(z)) {
			out[0] = Double.NaN;
		} else {
			for (int i = 0; i < out.length; i++) {
				out[i] = parentOutInView.val[i];
			}
		}

	}

	@Override
	public double[] getDefinedInterval(double a, double b) {
		return GeoCurveCartesian3D.getDefinedInterval(a, b, parent.getFun(0),
				parent.getFun(1), parent.getFun(2), funZ);
	}

	@Override
	public double[] newDoubleArray() {
		return new double[2];
	}

	@Override
	public double getMinParameter() {
		return parent.getMinParameter();
	}

	@Override
	public double getMaxParameter() {
		return parent.getMaxParameter();
	}

	@Override
	public boolean getTrace() {
		return parent.getTrace();
	}

	@Override
	public boolean isClosedPath() {
		return parent.isClosedPath();
	}

	@Override
	public boolean isFunctionInX() {
		return parent.isFunctionInX();
	}

	@Override
	public GeoElement toGeoElement() {
		return parent.toGeoElement();
	}

	@Override
	public double distanceMax(double[] p1, double[] p2) {
		return Math.max(Math.abs(p1[0] - p2[0]), Math.abs(p1[1] - p2[1]));
	}

}
