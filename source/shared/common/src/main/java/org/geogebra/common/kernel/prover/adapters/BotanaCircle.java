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
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

public class BotanaCircle {
	private PVariable[] botanaVars;

	public PVariable[] getBotanaVars(GeoElementND P, GeoElementND M)
			throws NoSymbolicParametersException {
		if (botanaVars == null) {
			PVariable[] circle1vars, centerVars;
			circle1vars = ((SymbolicParametersBotanaAlgo) P).getBotanaVars(P);
			centerVars = ((SymbolicParametersBotanaAlgo) M).getBotanaVars(M);
			if (circle1vars == null || centerVars == null) {
				fallback(P.getKernel());
				return null;
			}

			botanaVars = new PVariable[4];
			// Center:
			botanaVars[0] = centerVars[0];
			botanaVars[1] = centerVars[1];
			// Point on the circle:
			botanaVars[2] = circle1vars[0];
			botanaVars[3] = circle1vars[1];
		}
		return botanaVars;
	}

	void fallback(Kernel kernel) {
		// In the general case set up two dummy variables. They will be used
		// by the numerical substitution later in the prover.
		if (botanaVars != null) {
			return;
		}
		botanaVars = new PVariable[4];
		botanaVars[0] = new PVariable(kernel);
		botanaVars[1] = new PVariable(kernel);
		botanaVars[2] = new PVariable(kernel);
		botanaVars[3] = new PVariable(kernel);
	}

}