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

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;
import org.geogebra.common.util.debug.Log;

/**
 *
 * @author Markus
 */
public class AlgoParabolaPointLine extends AlgoParabolaPointLineND
		implements SymbolicParametersBotanaAlgo {

	private PPolynomial[] botanaPolynomials;
	private PVariable[] botanaVars;

	public AlgoParabolaPointLine(Construction cons, String label, GeoPointND F,
			GeoLineND l) {
		super(cons, label, F, l);
	}

	public AlgoParabolaPointLine(Construction cons, GeoPointND F, GeoLineND l) {
		super(cons, F, l);
	}

	@Override
	protected GeoConicND newGeoConic(Construction cons1) {
		return new GeoConic(cons1);
	}

	// compute parabola with focus F and line l
	@Override
	public final void compute() {
		parabola.setParabola(F, (GeoLine) line);
	}

	@Override
	public PVariable[] getBotanaVars(GeoElementND geo) {
		return botanaVars;
	}

	@Override
	public PPolynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {

		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		GeoPoint F1 = (GeoPoint) this.F;
		GeoLine l1 = (GeoLine) this.line;

		if (F1 != null && l1 != null) {
			PVariable[] vF = F1.getBotanaVars(F1);
			PVariable[] vl = l1.getBotanaVars(l1);

			if (botanaVars == null) {
				botanaVars = new PVariable[10];
				// P
				botanaVars[0] = new PVariable(kernel);
				botanaVars[1] = new PVariable(kernel);
				// T
				botanaVars[2] = new PVariable(kernel);
				botanaVars[3] = new PVariable(kernel);
				// A
				botanaVars[4] = vl[0];
				botanaVars[5] = vl[1];
				// B
				botanaVars[6] = vl[2];
				botanaVars[7] = vl[3];
				// F
				botanaVars[8] = vF[0];
				botanaVars[9] = vF[1];
				Log.trace("Parabola " + geo.getLabelSimple() + "("
						+ botanaVars[0] + "," + botanaVars[1]
						+ ") implicitly introduces feet point (" + botanaVars[2]
						+ "," + botanaVars[3] + ") on directrix "
						+ l1.getLabelSimple());
			}

			botanaPolynomials = new PPolynomial[3];

			// |FP| = |PT|
			botanaPolynomials[0] = PPolynomial.equidistant(vF[0], vF[1],
					botanaVars[0], botanaVars[1], botanaVars[2], botanaVars[3]);

			// A,T,B collinear
			botanaPolynomials[1] = PPolynomial.collinear(vl[0], vl[1], vl[2],
					vl[3], botanaVars[2], botanaVars[3]);

			// PT orthogonal AB
			botanaPolynomials[2] = PPolynomial.perpendicular(botanaVars[0],
					botanaVars[1], botanaVars[2], botanaVars[3], vl[0], vl[1],
					vl[2], vl[3]);

			return botanaPolynomials;
		}
		throw new NoSymbolicParametersException();

	}
}
