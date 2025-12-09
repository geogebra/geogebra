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

package org.geogebra.common.kernel.prover.adapters;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

public class BotanaIncircle extends ProverAdapter {

	public PPolynomial[] getPolynomials(GeoElement[] input)
			throws NoSymbolicParametersException {
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		if (input[0] != null && input[1] != null && input[2] != null) {
			GeoPoint A1 = (GeoPoint) input[0].toGeoElement();
			GeoPoint B1 = (GeoPoint) input[1].toGeoElement();
			GeoPoint C1 = (GeoPoint) input[2].toGeoElement();
			PVariable[] vA = A1.getBotanaVars(A1);
			PVariable[] vB = B1.getBotanaVars(B1);
			PVariable[] vC = C1.getBotanaVars(C1);

			if (botanaVars == null) {
				Kernel kernel = input[0].getKernel();
				botanaVars = new PVariable[8];
				// I, the incenter
				botanaVars[0] = new PVariable(kernel);
				botanaVars[1] = new PVariable(kernel);
				// Fa, the feet of I projected on BC
				botanaVars[2] = new PVariable(kernel);
				botanaVars[3] = new PVariable(kernel);
				// Fb, the feet of I projected on AC
				botanaVars[4] = new PVariable(kernel);
				botanaVars[5] = new PVariable(kernel);
				// Fc, the feet of I projected on AB
				botanaVars[6] = new PVariable(kernel);
				botanaVars[7] = new PVariable(kernel);
			}

			botanaPolynomials = new PPolynomial[8];

			// IFa=IFb
			botanaPolynomials[0] = PPolynomial.equidistant(botanaVars[2], botanaVars[3],
					botanaVars[0], botanaVars[1], botanaVars[4], botanaVars[5]);
			// IFb=IFc
			botanaPolynomials[1] = PPolynomial.equidistant(botanaVars[4], botanaVars[5],
					botanaVars[0], botanaVars[1], botanaVars[6], botanaVars[7]);
			// A,Fb,C are collinear
			botanaPolynomials[2] = PPolynomial.collinear(vA[0], vA[1], botanaVars[4], botanaVars[5],
					vC[0], vC[1]);
			// A,Fc,B are collinear
			botanaPolynomials[3] = PPolynomial.collinear(vA[0], vA[1], botanaVars[6], botanaVars[7],
					vB[0], vB[1]);
			// B,Fa,C are collinear
			botanaPolynomials[4] = PPolynomial.collinear(vB[0], vB[1], botanaVars[2], botanaVars[3],
					vC[0], vC[1]);
			// AC is perpendicular to IFb
			botanaPolynomials[5] = PPolynomial.perpendicular(vA[0], vA[1], vC[0], vC[1],
					botanaVars[0], botanaVars[1], botanaVars[4], botanaVars[5]);
			// AB is perpendicular to IFc
			botanaPolynomials[6] = PPolynomial.perpendicular(vA[0], vA[1], vB[0], vB[1],
					botanaVars[0], botanaVars[1], botanaVars[6], botanaVars[7]);
			// BC is perpendicular to IFa
			botanaPolynomials[7] = PPolynomial.perpendicular(vB[0], vB[1], vC[0], vC[1],
					botanaVars[0], botanaVars[1], botanaVars[2], botanaVars[3]);
			return botanaPolynomials;

		}
		throw new NoSymbolicParametersException();

	}
}
