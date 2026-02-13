/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.geogebra3D.euclidianFor3D;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCurveCartesian3D;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.CurveEvaluable;
import org.geogebra.common.util.DoubleUtil;

/**
 * For 3D curve, evaluator that returns NaN when z != 0
 * 
 * @author mathieu
 *
 */
public class CurveEvaluableFor3D implements CurveEvaluable {

	private final GeoCurveCartesian3D parent;
	private final double[] parentOut;

	private final FunMustBeZero funZ;

	/**
	 * Function that returns NaN if parent z != 0
	 * 
	 * @author mathieu
	 *
	 */
	private static final class FunMustBeZero implements UnivariateFunction {

		private UnivariateFunction parentFun;

		private void setParentFun(UnivariateFunction parentFun) {
			this.parentFun = parentFun;
		}

		@Override
		public double value(double t) {
			double z = parentFun.value(t);
			if (!DoubleUtil.isZero(z)) {
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

	@Override
	public void evaluateCurve(double t, double[] out) {
		parent.evaluateCurve(t, parentOut);
		double z = parentOut[2];
		if (Double.isInfinite(z) || Double.isNaN(z) || !DoubleUtil.isZero(z)) {
			out[0] = Double.NaN;
		} else {
			for (int i = 0; i < out.length; i++) {
				out[i] = parentOut[i];
			}
		}

	}

	@Override
	public double[] getDefinedInterval(double a, double b) {
		funZ.setParentFun(parent.getFun(2));
		return GeoCurveCartesian3D.getDefinedInterval(a, b, parent.getFun(0),
				parent.getFun(1), funZ);
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
