package org.geogebra.common.kernel.prover.adapters;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoConicFivePoints;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

public class PointOnPathAdapter extends ProverAdapter {

	public PPolynomial[] getBotanaPolynomials(GeoElement path)
			throws NoSymbolicParametersException {
		Kernel kernel = path.getKernel();
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		if (path instanceof GeoLine) {
			if (botanaVars == null) {
				botanaVars = new PVariable[2];
				botanaVars[0] = new PVariable(kernel); // ,true
				botanaVars[1] = new PVariable(kernel);
			}
			PVariable[] fv = ((SymbolicParametersBotanaAlgo) path)
					.getBotanaVars(path); // 4 variables
			if (fv == null) {
				fallback(kernel);
				return null;
			}
			botanaPolynomials = new PPolynomial[1];
			botanaPolynomials[0] = PPolynomial.collinear(fv[0], fv[1], fv[2],
					fv[3], botanaVars[0], botanaVars[1]);
			return botanaPolynomials;
		}

		if (path instanceof GeoConic) {
			if (((GeoConic) path).isCircle()) {
				if (botanaVars == null) {
					botanaVars = new PVariable[2];
					botanaVars[0] = new PVariable(kernel); // ,true
					botanaVars[1] = new PVariable(kernel);
				}
				PVariable[] fv = ((SymbolicParametersBotanaAlgo) path)
						.getBotanaVars(path); // 4 variables
				if (fv == null) {
					fallback(kernel);
					return null;
				}

				botanaPolynomials = new PPolynomial[1];
				// If this new point is D, and ABC is already a triangle with
				// the circumcenter O,
				// then here we must claim that e.g. AO=OD:
				botanaPolynomials[0] = PPolynomial.equidistant(fv[2], fv[3],
						fv[0], fv[1], botanaVars[0], botanaVars[1]);
				return botanaPolynomials;
			}
			if (((GeoConic) path).isParabola()) {
				if (botanaVars == null) {
					botanaVars = new PVariable[4];
					// point P on parabola
					botanaVars[0] = new PVariable(kernel); // ,true
					botanaVars[1] = new PVariable(kernel);
					// T- projection of P on AB
					botanaVars[2] = new PVariable(kernel);
					botanaVars[3] = new PVariable(kernel);
				}
				PVariable[] vparabola = ((SymbolicParametersBotanaAlgo) path)
						.getBotanaVars(path);
				if (vparabola == null) {
					fallback(kernel);
					return null;
				}

				botanaPolynomials = new PPolynomial[3];

				// FP = PT
				botanaPolynomials[0] = PPolynomial.equidistant(vparabola[8],
						vparabola[9], botanaVars[0], botanaVars[1],
						botanaVars[2], botanaVars[3]);

				// A,T,B collinear
				botanaPolynomials[1] = PPolynomial.collinear(vparabola[4],
						vparabola[5], botanaVars[2], botanaVars[3],
						vparabola[6], vparabola[7]);

				// PT orthogonal AB
				botanaPolynomials[2] = PPolynomial.perpendicular(botanaVars[0],
						botanaVars[1], botanaVars[2], botanaVars[3],
						vparabola[4], vparabola[5], vparabola[6], vparabola[7]);

				return botanaPolynomials;
			}
			if (((GeoConic) path).isEllipse()
					|| ((GeoConic) path).isHyperbola()) {
				if (botanaVars == null) {
					botanaVars = new PVariable[4];
					// P - point on ellipse/hyperbola
					botanaVars[0] = new PVariable(kernel); // ,true
					botanaVars[1] = new PVariable(kernel);
					// distances between point on ellipse/hyperbola
					// and foci points
					botanaVars[2] = new PVariable(kernel);
					botanaVars[3] = new PVariable(kernel);
				}

				PVariable[] vellipse = ((SymbolicParametersBotanaAlgo) path)
						.getBotanaVars(path);
				if (vellipse == null) {
					fallback(kernel);
					return null;
				}

				if (path.getParentAlgorithm() instanceof AlgoConicFivePoints) {
					botanaPolynomials = new PPolynomial[2];
					botanaPolynomials[0] = new PPolynomial(vellipse[0])
							.subtract(new PPolynomial(botanaVars[0]));
					botanaPolynomials[1] = new PPolynomial(vellipse[1])
							.subtract(new PPolynomial(botanaVars[1]));
					return botanaPolynomials;
				}

				botanaPolynomials = new PPolynomial[3];

				PPolynomial e_1 = new PPolynomial(botanaVars[2]);
				PPolynomial e_2 = new PPolynomial(botanaVars[3]);
				PPolynomial d1 = new PPolynomial(vellipse[2]);
				PPolynomial d2 = new PPolynomial(vellipse[3]);

				// d1+d2 = e1'+e2'
				botanaPolynomials[0] = d1.add(d2).subtract(e_1).subtract(e_2);

				// e1'^2=Polynomial.sqrDistance(a1,a2,p1,p2)
				botanaPolynomials[1] = PPolynomial.sqrDistance(botanaVars[0],
						botanaVars[1], vellipse[6], vellipse[7])
						.subtract(e_1.multiply(e_1));

				// e2'^2=Polynomial.sqrDistance(b1,b2,p1,p2)
				botanaPolynomials[2] = PPolynomial.sqrDistance(botanaVars[0],
						botanaVars[1], vellipse[8], vellipse[9])
						.subtract(e_2.multiply(e_2));

				return botanaPolynomials;

			}
		}
		fallback(kernel);
		return null;
		// throw new NoSymbolicParametersException();
	}

	void fallback(Kernel kernel) {
		// In the general case set up two dummy variables. They will be used
		// by the numerical substitution later in the prover.
		if (botanaVars != null) {
			return;
		}
		botanaVars = new PVariable[2];
		botanaVars[0] = new PVariable(kernel);
		botanaVars[1] = new PVariable(kernel);
	}

}
