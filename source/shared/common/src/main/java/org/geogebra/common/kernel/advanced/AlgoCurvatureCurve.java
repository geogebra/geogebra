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

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.debug.Log;

/**
 * @author Victor Franco Espino, Markus Hohenwarter
 * @version 11-02-2007
 * 
 *          Calculate Curvature for curve: k(t) = (a'(t)b''(t)-a''(t)b'(t))/T^3,
 *          T = sqrt(a'(t)^2+b'(t)^2)
 */
public class AlgoCurvatureCurve extends AlgoElement {

	private GeoPointND A; // input
	private GeoCurveCartesianND f;
	private GeoNumeric K; // output
	private GeoConicND gc = null;
	private double[] deriv = null;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param A
	 *            point on curve
	 * @param f
	 *            curve
	 */
	public AlgoCurvatureCurve(Construction cons, String label, GeoPointND A,
			GeoCurveCartesianND f) {
		this(cons, A, f);

		if (label != null) {
			K.setLabel(label);
		} else {
			// if we don't have a label we could try k
			K.setLabel("k");
		}
	}

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param A
	 *            point on curve
	 * @param f
	 *            conic
	 */
	public AlgoCurvatureCurve(Construction cons, String label, GeoPointND A,
			GeoConicND f) {
		this(cons, A, f);

		if (label != null) {
			K.setLabel(label);
		} else {
			// if we don't have a label we could try k
			K.setLabel("k");
		}
	}

	/**
	 * @param cons
	 *            construction
	 * @param A
	 *            point on curve
	 * @param f
	 *            curve
	 */
	public AlgoCurvatureCurve(Construction cons, GeoPointND A,
			GeoCurveCartesianND f) {
		super(cons);
		this.f = f;
		this.A = A;
		K = new GeoNumeric(cons);

		setInputOutput();
		compute();
	}

	/**
	 * @param cons
	 *            construction
	 * @param A
	 *            point on curve
	 * @param gc
	 *            conic
	 */
	public AlgoCurvatureCurve(Construction cons, GeoPointND A, GeoConicND gc) {
		super(cons);
		this.gc = gc;
		deriv = new double[2];
		this.A = A;
		K = new GeoNumeric(cons);

		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Curvature;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = A.toGeoElement();
		if (gc != null) {
			input[1] = gc;
		} else {
			input[1] = f;
		}

		setOnlyOutput(K);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return curvature
	 */
	public GeoNumeric getResult() {
		return K;
	}

	@Override
	public final void compute() {

		if (gc != null) {
			gc.evaluateFirstDerivative(A, deriv);
			K.setValue(Math.abs(gc.evaluateCurvatureFromDerivative(deriv)));
			return;
		}

		if (f.isDefined()) {
			try {
				double t = f.getClosestParameterForCurvature(A,
						f.getMinParameter());
				K.setValue(f.evaluateCurvature(t));
			} catch (Exception ex) {
				Log.debug(ex);
				K.setUndefined();
			}
		} else {
			K.setUndefined();
		}
	}

}