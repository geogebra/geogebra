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
import org.geogebra.common.geogebra3D.euclidianForPlane.EuclidianViewForPlaneCompanion;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCurveCartesian3D;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.ParametricCurve;
import org.geogebra.common.kernel.kernelND.CurveEvaluable;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * For 3D curve, evaluator that returns NaN when z != 0
 * 
 * @author mathieu
 *
 */
public class CurveEvaluableForPlane implements CurveEvaluable {

	ParametricCurve parent;
	Coords parentOut;
	Coords parentOutInView;
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
	 * @param companion
	 *            view for plane companion
	 */
	public CurveEvaluableForPlane(ParametricCurve parent,
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
		if (Double.isInfinite(z) || Double.isNaN(z) || !DoubleUtil.isZero(z)) {
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
		if (parent.isGeoFunction()) {
			return companion.getView().getXmin();
		}
		return parent.getMinParameter();
	}

	@Override
	public double getMaxParameter() {
		if (parent.isGeoFunction()) {
			return companion.getView().getXmax();
		}
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
