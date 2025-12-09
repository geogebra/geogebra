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

import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

public class BotanaCircleThreePoints extends ProverAdapter {

	public PPolynomial[] getPolynomials(GeoElement[] input)
			throws NoSymbolicParametersException {
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		PVariable[] circle1vars, circle2vars, circle3vars;
		circle1vars = ((SymbolicParametersBotanaAlgo) input[0])
				.getBotanaVars(input[0]);

		if (botanaVars == null) {
			botanaVars = new PVariable[4];
			// Virtual center:
			botanaVars[0] = new PVariable(input[0].getKernel());
			botanaVars[1] = new PVariable(input[0].getKernel());
			// Point on the circle:
			botanaVars[2] = circle1vars[0];
			botanaVars[3] = circle1vars[1];
		}
		PVariable[] centerVars = { botanaVars[0], botanaVars[1] };
		circle2vars = ((SymbolicParametersBotanaAlgo) input[1])
				.getBotanaVars(input[1]);
		circle3vars = ((SymbolicParametersBotanaAlgo) input[2])
				.getBotanaVars(input[2]);

		botanaPolynomials = new PPolynomial[2];
		// AO=OB
		botanaPolynomials[0] = PPolynomial.equidistant(circle1vars[0],
				circle1vars[1], centerVars[0], centerVars[1], circle2vars[0],
				circle2vars[1]);
		// AO=OC
		botanaPolynomials[1] = PPolynomial.equidistant(circle1vars[0],
				circle1vars[1], centerVars[0], centerVars[1], circle3vars[0],
				circle3vars[1]);

		return botanaPolynomials;
	}

}
