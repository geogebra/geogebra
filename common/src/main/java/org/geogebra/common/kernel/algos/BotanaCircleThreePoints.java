package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;

public class BotanaCircleThreePoints {
	private Variable[] botanaVars;
	private Polynomial[] botanaPolynomials;

	public Polynomial[] getPolynomials(GeoElement[] input) {
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		Variable[] circle1vars, circle2vars, circle3vars;
		circle1vars = ((SymbolicParametersBotanaAlgo) input[0])
				.getBotanaVars(input[0]);

		if (botanaVars == null) {
			botanaVars = new Variable[4];
			// Virtual center:
			botanaVars[0] = new Variable();
			botanaVars[1] = new Variable();
			// Point on the circle:
			botanaVars[2] = circle1vars[0];
			botanaVars[3] = circle1vars[1];
		}
		Variable[] centerVars = { botanaVars[0], botanaVars[1] };
		circle2vars = ((SymbolicParametersBotanaAlgo) input[1])
				.getBotanaVars(input[1]);
		circle3vars = ((SymbolicParametersBotanaAlgo) input[2])
				.getBotanaVars(input[2]);

		botanaPolynomials = new Polynomial[2];
		// AO=OB
		botanaPolynomials[0] = Polynomial.equidistant(circle1vars[0],
				circle1vars[1], centerVars[0], centerVars[1], circle2vars[0],
				circle2vars[1]);
		// AO=OC
		botanaPolynomials[1] = Polynomial.equidistant(circle1vars[0],
				circle1vars[1], centerVars[0], centerVars[1], circle3vars[0],
				circle3vars[1]);

		return botanaPolynomials;
	}

	public Variable[] getVars() {
		return botanaVars;
	}
}
