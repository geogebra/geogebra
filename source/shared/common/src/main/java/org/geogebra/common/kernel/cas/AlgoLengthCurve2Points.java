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

package org.geogebra.common.kernel.cas;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.LengthCurve;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * @author Victor Franco Espino
 * @version 19-04-2007
 * 
 *          Calculate Curve Length between the points A and B: integral from t0
 *          to t1 on T = sqrt(a'(t)^2+b'(t)^2)
 */

public class AlgoLengthCurve2Points extends AlgoUsingTempCASalgo {

	private GeoPointND A; // input
	private GeoPointND B; // input
	private GeoCurveCartesianND c;
	private GeoCurveCartesianND derivative;
	private GeoNumeric length; // output
	private UnivariateFunction lengthCurve; // is T = sqrt(a'(t)^2+b'(t)^2)

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param c
	 *            curve
	 * @param A
	 *            start point
	 * @param B
	 *            end point
	 */
	public AlgoLengthCurve2Points(Construction cons, String label,
			GeoCurveCartesianND c, GeoPointND A, GeoPointND B) {
		super(cons);
		this.A = A;
		this.B = B;
		this.c = c;
		length = new GeoNumeric(cons);

		refreshCASResults();

		setInputOutput();
		compute();
		length.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Length;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = c;
		input[1] = A.toGeoElement();
		input[2] = B.toGeoElement();

		setOnlyOutput(length);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting length
	 */
	public GeoNumeric getLength() {
		return length;
	}

	@Override
	public final void compute() {
		if (!derivative.isDefined()) {
			length.setUndefined();
			return;
		}

		double a = c.getClosestParameter(A, c.getMinParameter());
		double b = c.getClosestParameter(B, c.getMinParameter());
		double lenVal = Math.abs(
				AlgoIntegralDefinite.numericIntegration(lengthCurve, a, b));
		length.setValue(lenVal);
	}

	@Override
	public void refreshCASResults() {
		// First derivative of curve f
		algoCAS = new AlgoDerivative(cons, c, null, null, true,
				new EvalInfo(false));
		derivative = (GeoCurveCartesianND) ((AlgoDerivative) algoCAS)
				.getResult();
		cons.removeFromConstructionList(algoCAS);
		lengthCurve = new LengthCurve(derivative);
	}

	// locus equability makes no sense here
}
