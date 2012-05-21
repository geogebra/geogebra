package geogebra.common.kernel.prover;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import geogebra.common.kernel.algos.SymbolicParametersAlgo;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.prover.Prover.NDGCondition;
import geogebra.common.kernel.prover.Prover.ProofResult;
import geogebra.common.main.AbstractApplication;

/**
 * A prover which uses Francisco Botana's method to prove geometric theorems.
 * 
 * @author Zoltan Kovacs
 *
 */
public class ProverBotanasMethod {
	
	private static HashSet<GeoElement> getFreePoints(GeoElement statement) {
		HashSet<GeoElement> freePoints = new HashSet<GeoElement>();
		Iterator<GeoElement> it = statement.getAllPredecessors().iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo.isGeoPoint() && geo.getParentAlgorithm() == null) { // this is a free point
				freePoints.add(geo);
			}
		}
		return freePoints;
	}
	
	/** 
	 * Creates those polynomials which describe that none of 3 free points
	 * can lie on the same line. 
	 * @return the NDG polynomials (in denial form)
	 */
	private static Polynomial[] create3FreePointsNeverCollinearNDG(Prover prover) {
		// Creating the set of free points first:
		HashSet<GeoElement> freePoints = getFreePoints(prover.statement);
		int setSize = freePoints.size();
		// The output will contain $\binom{n}{3}$ elements:
		Polynomial[] ret = new Polynomial[setSize * (setSize - 1) * (setSize - 2) / 6];
		int i = 0;
		// Creating the set of triplets:
		HashSet<HashSet<GeoElement>> triplets = new HashSet<HashSet<GeoElement>>();
		Iterator<GeoElement> it1 = freePoints.iterator();
		while (it1.hasNext()) {
			GeoElement geo1 = it1.next();
			Iterator<GeoElement> it2 = freePoints.iterator();
			while (it2.hasNext()) {
				GeoElement geo2 = it2.next();
				if (!geo1.isEqual(geo2)) {
					Iterator<GeoElement> it3 = freePoints.iterator();
					while (it3.hasNext()) {
						GeoElement geo3 = it3.next();
						if (!geo1.isEqual(geo3) && !geo2.isEqual(geo3)) {
							HashSet<GeoElement> triplet = new HashSet<GeoElement>();
							triplet.add(geo1);
							triplet.add(geo2);
							triplet.add(geo3);
							// Only the significantly new triplets will be processed:
							if (!triplets.contains(triplet)) {
								triplets.add(triplet);
								AbstractApplication.debug(geo1.getLabelSimple() + 
										geo2.getLabelSimple() + 
										geo3.getLabelSimple() + " should never be collinear");
								Variable[] fv1 = ((SymbolicParametersAlgo)geo1).getBotanaVars();
								Variable[] fv2 = ((SymbolicParametersAlgo)geo2).getBotanaVars();
								Variable[] fv3 = ((SymbolicParametersAlgo)geo3).getBotanaVars();
								// Creating the polynomial for collinearity:
								Polynomial p = Polynomial.collinear(fv1[0], fv1[1],
										fv2[0], fv2[1], fv3[0], fv3[1]);
								// Rabinowitsch trick for prohibiting collinearity:
								ret[i] = p.multiply(new Polynomial(new Variable())).subtract(new Polynomial(1));
								// FIXME: this always introduces an extra variable, shouldn't do
								NDGCondition ndgc = new NDGCondition();
								ndgc.condition = "AreCollinear";
								ndgc.geos = new GeoElement[3];
								ndgc.geos[0] = geo1;
								ndgc.geos[1] = geo2;
								ndgc.geos[2] = geo3;
								Arrays.sort(ndgc.geos);
								prover.addNDGcondition(ndgc);
								i++;
							}
						}
					}
				}
			}
		}
		return ret;
	}
	
	/**
	 * Uses a minimal heuristics to fix the first four variables to certain "easy" numbers.
	 * The first two variables (usually the coordinates of the first point) are set to 0,
	 * and the second two variables (usually the coordinates of the second point) are set to 0 and 1.
	 * @param statement the input statement
	 * @return the string of the extra polynomials (e.g. "a1,a2,b1,b2-1")
	 */
	static Polynomial[] fixValues(GeoElement statement) {
		HashSet<GeoElement> freePoints = getFreePoints(statement);
		int setSize = freePoints.size();
		int retSize = 0;
		if (setSize >= 2)
			retSize = 4;
		if (retSize == 1)
			retSize = 2;
		Polynomial[] ret = new Polynomial[retSize];
		Iterator<GeoElement> it = freePoints.iterator();
		int i = 0;
		while (it.hasNext() && i<4) {
			Variable[] fv = ((SymbolicParametersAlgo) it.next()).getBotanaVars();
			if (i==0) {
				ret[i] = new Polynomial(fv[0]);
				++i;
				ret[i] = new Polynomial(fv[1]);
				++i;
			}
			else {
				ret[i] = new Polynomial(fv[0]);
				++i;
				ret[i] = new Polynomial(fv[1]).subtract(new Polynomial(1));
				++i;
			}
		}
		return ret;
	}
	
	/**
	 * Proves the statement by using Botana's method 
	 * @param prover the prover input object 
	 * @return if the statement is true
	 */
	public static ProofResult prove(Prover prover) {
		// Getting the hypotheses:
		Polynomial[] hypotheses = null;
		Iterator<GeoElement> it = prover.statement.getAllPredecessors().iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			// AbstractApplication.debug(geo);
			if (geo instanceof SymbolicParametersAlgo) {
				try {
					Polynomial[] geoPolys = ((SymbolicParametersAlgo) geo).getBotanaPolynomials();
					if (geoPolys != null) {
						int nHypotheses = 0;
						if (hypotheses != null)
							nHypotheses = hypotheses.length;
						Polynomial[] allPolys = new Polynomial[nHypotheses + geoPolys.length];
						for (int i=0; i<nHypotheses; ++i)
							allPolys[i] = hypotheses[i];
						for (int i=0; i<geoPolys.length; ++i)
							allPolys[nHypotheses + i] = geoPolys[i];
						hypotheses = allPolys;
					}
				} catch (NoSymbolicParametersException e) {
					return ProofResult.UNKNOWN;
				}
			}
		}
		try {
			// The statement polynomials. If there are more ones, then a new equation
			// system will be created and solved for each.
			Polynomial[] statements = ((SymbolicParametersAlgo) prover.statement.getParentAlgorithm()).getBotanaPolynomials();
			// The NDG conditions (automatically created):
			Polynomial[] ndgConditions = null;
			if (AbstractApplication.freePointsNeverCollinear)
				ndgConditions = create3FreePointsNeverCollinearNDG(prover);
			// Fix points (heuristics):
			Polynomial[] fixValues = null;
			if (AbstractApplication.useFixCoordinates)
				fixValues = fixValues(prover.statement);
			int nHypotheses = 0;
			int nNdgConditions = 0;
			int nStatements = 0;
			int nFixValues = 0;
			if (hypotheses != null)
				nHypotheses = hypotheses.length;
			if (ndgConditions != null)
				nNdgConditions = ndgConditions.length;
			if (statements != null)
				nStatements = statements.length;
			if (fixValues != null)
				nFixValues = fixValues.length;
			
			// These polynomials will be in the equation system always:
			Polynomial[] eqSystem = new Polynomial[nHypotheses + nNdgConditions + nFixValues + 1];
			for (int j=0; j<nHypotheses; ++j)
				eqSystem[j] = hypotheses[j];
			for (int j=0; j<nNdgConditions; ++j)
				eqSystem[j + nHypotheses] = ndgConditions[j];
			for (int j=0; j<nFixValues; ++j)
				eqSystem[j + nHypotheses + nNdgConditions] = fixValues[j];
			
			boolean ans = true;
			// Solving the equation system for each polynomial of the statement:
			for (int i=0; i<nStatements && ans; ++i) {
				// Rabinowitsch trick
				Polynomial spoly = statements[i].multiply(new Polynomial(new Variable())).subtract(new Polynomial(1));
				// FIXME: this always introduces an extra variable, shouldn't do
				eqSystem[nHypotheses + nNdgConditions + nFixValues] = spoly;
				if (Polynomial.solvable(eqSystem)) // FIXME: here seems NPE if SingularWS not initialized 
					ans = false;
			}
			if (ans)
				return ProofResult.TRUE;
			return ProofResult.FALSE;
		} catch (NoSymbolicParametersException e) {
			return ProofResult.UNKNOWN;
		}
	}
}
