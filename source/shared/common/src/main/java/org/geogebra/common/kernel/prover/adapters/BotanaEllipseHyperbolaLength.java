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

import org.geogebra.common.kernel.algos.AlgoDependentNumber;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

public class BotanaEllipseHyperbolaLength extends ProverAdapter {

	public PPolynomial[] getBotanaPolynomials(GeoPointND focus1,
			GeoPointND focus2, GeoNumberValue length)
			throws NoSymbolicParametersException {
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		GeoPoint F1 = (GeoPoint) focus1;
		GeoPoint F2 = (GeoPoint) focus2;

		/* SPECIAL CASE 1: radius is a segment */
		if (length instanceof GeoSegment) {
			throw new NoSymbolicParametersException();
			/*
			 * Here we do the full work for this segment. It would be nicer to
			 * put this code into GeoSegment but we need to use the square of
			 * the length of the segment in this special case.
			 */
			// GeoSegment s = (GeoSegment) this.getInput(1);
			// if (botanaVars == null) {
			// Variable[] centerBotanaVars = P.getBotanaVars(P);
			// botanaVars = new Variable[4];
			// // center P
			// botanaVars[0] = centerBotanaVars[0];
			// botanaVars[1] = centerBotanaVars[1];
			// // point C on the circle
			// botanaVars[2] = new Variable();
			// botanaVars[3] = new Variable();
			// }
			// GeoPoint A = s.getStartPoint();
			// GeoPoint B = s.getEndPoint();
			// Variable[] ABotanaVars = A.getBotanaVars(A);
			// Variable[] BBotanaVars = B.getBotanaVars(B);
			//
			// botanaPolynomials = new Polynomial[2];
			// // C-P == B-A <=> C-P-B+A == 0
			// botanaPolynomials[0] = new Polynomial(botanaVars[2])
			// .subtract(new Polynomial(botanaVars[0]))
			// .subtract(new Polynomial(BBotanaVars[0]))
			// .add(new Polynomial(ABotanaVars[0]));
			// botanaPolynomials[1] = new Polynomial(botanaVars[3])
			// .subtract(new Polynomial(botanaVars[1]))
			// .subtract(new Polynomial(BBotanaVars[1]))
			// .add(new Polynomial(ABotanaVars[1]));
			// // done for both coordinates!
			// return botanaPolynomials;
		}

		/* SPECIAL CASE 2: radius is an expression */

		GeoNumeric num = null;
		if (length instanceof GeoNumeric) {
			num = (GeoNumeric) length;
		}
		if (F1 == null || F2 == null || num == null) {
			throw new NoSymbolicParametersException();
		}

		if (botanaVars == null) {
			PVariable[] centerBotanaVars = F1.getBotanaVars(F1);
			PVariable[] centerBotanaVars2 = F2.getBotanaVars(F2);
			botanaVars = new PVariable[7];
			// center
			botanaVars[0] = centerBotanaVars[0];
			botanaVars[1] = centerBotanaVars[1];

			botanaVars[2] = centerBotanaVars2[0];
			botanaVars[3] = centerBotanaVars2[1];
			// point on circle
			botanaVars[4] = new PVariable(F1.getKernel());
			botanaVars[5] = new PVariable(F1.getKernel());
			// radius
			botanaVars[6] = new PVariable(F1.getKernel());
		}

		botanaPolynomials = new PPolynomial[2];
		PPolynomial[] extraPolys = null;
		if (num.getParentAlgorithm() instanceof AlgoDependentNumber) {
			extraPolys = num.getBotanaPolynomials(num);
		}
		/*
		 * Note that we read the Botana variables just after reading the Botana
		 * polynomials since the variables are set after the polys are set.
		 */
		PVariable[] radiusBotanaVars = num.getBotanaVars(num);
		int k = 0;
		// r^2
		PPolynomial sqrR = PPolynomial.sqr(new PPolynomial(radiusBotanaVars[0]));
		// define radius
		if (extraPolys != null) {
			botanaPolynomials = new PPolynomial[extraPolys.length + 1];
			for (k = 0; k < extraPolys.length; k++) {
				botanaPolynomials[k] = extraPolys[k];
			}
		}
		// ((A-(x,y))^2+(B-(x,y))^2-100)^2=4*(B-(x,y))^2*(A-(x,y))^2
		// define circle
		// botanaPolynomials[k] =;
		PPolynomial f1distSq = PPolynomial.sqrDistance(botanaVars[0],
				botanaVars[1], botanaVars[4], botanaVars[5]);
		PPolynomial f2distSq = PPolynomial.sqrDistance(botanaVars[2],
				botanaVars[3], botanaVars[4], botanaVars[5]);
		PPolynomial lhs = PPolynomial.sqr(f1distSq.add(f2distSq).subtract(sqrR));
		PPolynomial rhs = f1distSq.multiply(f2distSq)
				.multiply(new PPolynomial(4));
		botanaPolynomials[k] = lhs.subtract(rhs);

		return botanaPolynomials;

	}
}
