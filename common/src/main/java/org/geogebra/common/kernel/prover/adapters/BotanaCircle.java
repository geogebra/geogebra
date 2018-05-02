package org.geogebra.common.kernel.prover.adapters;

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
}
