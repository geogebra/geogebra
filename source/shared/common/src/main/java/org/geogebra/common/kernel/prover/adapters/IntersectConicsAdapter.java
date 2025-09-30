package org.geogebra.common.kernel.prover.adapters;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoIntersectConics;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

public class IntersectConicsAdapter {
	private HashMap<GeoElementND, PPolynomial[]> botanaPolynomials;
	private HashMap<GeoElementND, PVariable[]> botanaVars;

	public PPolynomial[] getBotanaPolynomials(GeoElementND geo, GeoConic a, GeoConic b,
			AlgoIntersectConics algo) throws NoSymbolicParametersException {
		if (botanaPolynomials != null) {
			PPolynomial[] ret = botanaPolynomials.get(geo);
			if (ret != null) {
				return ret;
			}
		}
		Kernel kernel = a.getKernel();
		// Special cases first.

		if (a.isCircle() && b.isCircle()) {
			PVariable[] botanaVarsThis = new PVariable[2];
			if (botanaVars == null) {
				botanaVars = new HashMap<>();
			}
			if (botanaVars.containsKey(geo)) {
				botanaVarsThis = botanaVars.get(geo);
			} else {
				// Intersection point (we create only one):
				botanaVarsThis = new PVariable[2];
				botanaVarsThis[0] = new PVariable(kernel);
				botanaVarsThis[1] = new PVariable(kernel);
				botanaVars.put(geo, botanaVarsThis);
			}

			/*
			 * If this point is not shown, then force a criterion that the
			 * symbolic intersection must differ from that point. See below.
			 */
			int excludePoint = 0;
			if (!algo.isInConstructionList() && algo.existingIntersections() == 1) {
				/*
				 * This case is present if we explicitly point to one
				 * intersection point of a line and a circle. If the circles
				 * already have a common point, then the user may point to the
				 * other intersection point. In this case we explicitly claim
				 * that the intersection point differs from the common point.
				 */
				excludePoint = 1;
			}

			PPolynomial[] botanaPolynomialsThis = null;
			/*
			 * Force a criterion that the two intersection points must differ:
			 * See page 150 in Zoltan's diss, 1st paragraph. TODO: This is very
			 * ugly.
			 */
			PVariable[] botanaVarsOther = new PVariable[2];
			Iterator<Entry<GeoElementND, PVariable[]>> it = botanaVars.entrySet().iterator();
			boolean found = false;
			while (it.hasNext()) {
				Entry<GeoElementND, PVariable[]> entry = it.next();
				GeoElementND otherGeo = entry.getKey();
				/*
				 * This should be at most one element. There is one element if
				 * we found the second intersection point, otherwise (for the
				 * first intersection point) there is no otherGeo yet, so we
				 * will not create any polynomials here (yet).
				 */
				if (!otherGeo.equals(geo)) {
					botanaPolynomialsThis = new PPolynomial[3 + excludePoint];
					botanaVarsOther = entry.getValue();
					botanaPolynomialsThis[2 + excludePoint] = PPolynomial
							.sqrDistance(botanaVarsThis[0], botanaVarsThis[1], botanaVarsOther[0],
									botanaVarsOther[1])
							.multiply(new PPolynomial(new PVariable(kernel)))
									.subtract(new PPolynomial(1));
					found = true;
				}
			}
			if (!found) {
				botanaPolynomialsThis = new PPolynomial[2 + excludePoint];
			}

			PVariable[] vA = a.getBotanaVars(a); // 4 variables from the first
													// circle
			PVariable[] vB = b.getBotanaVars(b); // 4 variables from the first
													// circle

			botanaPolynomialsThis[0] = PPolynomial.equidistant(vA[2], vA[3], vA[0], vA[1],
					botanaVarsThis[0], botanaVarsThis[1]);
			botanaPolynomialsThis[1] = PPolynomial.equidistant(vB[2], vB[3], vB[0], vB[1],
					botanaVarsThis[0], botanaVarsThis[1]);

			if (botanaPolynomials == null) {
				botanaPolynomials = new HashMap<>();
			}

			/*
			 * If this point is not shown, then force a criterion that the
			 * symbolic intersection must differ from that point. See above.
			 */
			if (excludePoint > 0) {
				botanaVarsOther = ((GeoPoint) algo.getPreexistPoint(0))
						.getBotanaVars(algo.getPreexistPoint(0));
				botanaPolynomialsThis[botanaPolynomialsThis.length - 1] = PPolynomial
						.sqrDistance(botanaVarsThis[0], botanaVarsThis[1], botanaVarsOther[0],
								botanaVarsOther[1])
						.multiply(new PPolynomial(new PVariable(kernel)))
								.subtract(new PPolynomial(1));
			}

			botanaPolynomials.put(geo, botanaPolynomialsThis);

			/*
			 * TODO: We created the botanaPolynomials by building up an array
			 * here from at most three parts. It would be nicer to do it in a
			 * more sophisticated way.
			 */
			return botanaPolynomialsThis;
		}

		/* General case */
		PVariable[] botanaVarsThis = new PVariable[2];
		if (botanaVars == null) {
			botanaVars = new HashMap<>();
		}
		if (botanaVars.containsKey(geo)) {
			botanaVarsThis = botanaVars.get(geo);
		} else {
			// Intersection point (we create only one):
			botanaVarsThis = new PVariable[2];
			botanaVarsThis[0] = new PVariable(kernel);
			botanaVarsThis[1] = new PVariable(kernel);
			botanaVars.put(geo, botanaVarsThis);
		}
		if (botanaPolynomials == null) {
			PPolynomial[] conic1Polys = a.getBotanaPolynomials(a);
			PVariable[] conic1Vars = a.getBotanaVars(a);
			PPolynomial[] conic2Polys = b.getBotanaPolynomials(b);
			PVariable[] conic2Vars = b.getBotanaVars(b);

			int conic1PolysNo = conic1Polys.length;
			int conic2PolysNo = conic2Polys.length;

			PPolynomial[] botanaPolynomialsThis = new PPolynomial[conic1PolysNo
					+ conic2PolysNo];

			for (int i = 0; i < conic1PolysNo; i++) {
				botanaPolynomialsThis[i] = conic1Polys[i]
						.substitute(conic1Vars[0], botanaVarsThis[0])
						.substitute(conic1Vars[1], botanaVarsThis[1]);
			}
			for (int i = 0; i < conic2PolysNo; i++) {
				botanaPolynomialsThis[conic1PolysNo + i] = conic2Polys[i]
						.substitute(conic2Vars[0], botanaVarsThis[0])
						.substitute(conic2Vars[1], botanaVarsThis[1]);
			}

			if (botanaPolynomials == null) {
				botanaPolynomials = new HashMap<>();
			}
			botanaPolynomials.put(geo, botanaPolynomialsThis);

			return botanaPolynomialsThis;
		}
		throw new NoSymbolicParametersException();
	}

	public PVariable[] getBotanaVars(GeoElementND geo) {
		return botanaVars.get(geo);
	}
}
