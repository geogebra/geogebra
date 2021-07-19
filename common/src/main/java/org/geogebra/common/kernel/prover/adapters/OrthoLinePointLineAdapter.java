package org.geogebra.common.kernel.prover.adapters;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;
import org.geogebra.common.util.debug.Log;

public class OrthoLinePointLineAdapter extends ProverAdapter {
	public PPolynomial[] getBotanaPolynomials(GeoLine l, GeoPoint P)
			throws NoSymbolicParametersException {
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}
		if (P != null && l != null) {
			Kernel kernel = P.getKernel();
			PVariable[] vP = P.getBotanaVars(P);
			PVariable[] vL = l.getBotanaVars(l);

			if (botanaVars == null) {
				botanaVars = new PVariable[4]; // storing 2 new variables, plus
												// the coordinates of P
				botanaVars[0] = new PVariable(kernel);
				botanaVars[1] = new PVariable(kernel);
				botanaVars[2] = vP[0];
				botanaVars[3] = vP[1];
				Log.trace("Orthogonal line at " + P.getLabelSimple() + " to "
						+ l.getLabelSimple()
						+ " implicitly introduces feet point (" + botanaVars[0]
						+ "," + botanaVars[1] + ")");
			}

			botanaPolynomials = new PPolynomial[2];

			// We describe the new point simply with rotation of l=:AB around P
			// by 90 degrees.
			// I.e., b1-a1=n2-p2, b2-a2=p1-n1 => b1-a1+p2-n2=0, p1-b2+a2-n1=0
			PPolynomial p1 = new PPolynomial(vP[0]);
			PPolynomial p2 = new PPolynomial(vP[1]);
			PPolynomial a1 = new PPolynomial(vL[0]);
			PPolynomial a2 = new PPolynomial(vL[1]);
			PPolynomial b1 = new PPolynomial(vL[2]);
			PPolynomial b2 = new PPolynomial(vL[3]);
			PPolynomial n1 = new PPolynomial(botanaVars[0]);
			PPolynomial n2 = new PPolynomial(botanaVars[1]);
			botanaPolynomials[0] = b1.subtract(a1).add(p2).subtract(n2);
			botanaPolynomials[1] = p1.subtract(b2).add(a2).subtract(n1);

			return botanaPolynomials;
		}
		throw new NoSymbolicParametersException();
	}
}
