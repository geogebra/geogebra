package org.geogebra.common.kernel.prover;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.geogebra.common.kernel.algos.AlgoDependentBoolean;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Term;
import org.geogebra.common.kernel.prover.polynomial.Variable;
import org.geogebra.common.util.Prover;
import org.geogebra.common.util.Prover.NDGCondition;
import org.geogebra.common.util.debug.Log;

/**
 * Detects polynomial NDG conditions and turns into human readable form
 * 
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 * 
 */
public class NDGDetector {

	private HashMap<String, NDGCondition> lookupTable;
	private Prover prover;
	private HashMap<Variable, Integer> substitutions;

	/**
	 * Creates an NDGDetector instance. The NDG detector will try to detect
	 * geometrical meanings of polynomials for the same prover and
	 * substitutions.
	 * 
	 * @param prover
	 *            The prover we are create this instance for.
	 * @param substitutions
	 *            Fix substitutions.
	 */
	NDGDetector(Prover prover, HashMap<Variable, Integer> substitutions) {
		lookupTable = new HashMap<String, NDGCondition>();
		this.prover = prover;
		this.substitutions = substitutions;
	}

	/**
	 * Returns the NDG condition (as a GeoGebra object) if the input polynomial
	 * is detected as a recognizable geometrically meaningful condition
	 * (collinearity, equality etc.).
	 * 
	 * @param p
	 *            input polynomial
	 * @return the NDG condition
	 */
	public NDGCondition detect(Polynomial p) {

		/*
		 * Maybe this condition was already detected, or marked as unreadable.
		 * By using the lookup table, we don't have to do heavy computations
		 * twice.
		 */
		NDGCondition ndgc = null;
		String keyString = p.substitute(substitutions).toString();
		ndgc = lookupTable.get(keyString);
		if (lookupTable.containsKey(keyString)) {
			ndgc = lookupTable.get(keyString);
			if (ndgc != null
					&& ndgc.getReadability() == Double.POSITIVE_INFINITY) {
				return null;
			}
			return ndgc;
		}

		Log.debug("Trying to detect polynomial " + p);

		GeoElement statement = prover.getStatement();
		if (statement == null) {
			return ndgc;
		}
		
		// CHECKING FORMULA WITH QUANTITIES
		
		if (statement.getParentAlgorithm() instanceof AlgoDependentBoolean) {
			// list of segments -> variables
			ArrayList<Entry<GeoElement, Variable>> varSubstListOfSegs = ((AlgoDependentBoolean) statement
					.getParentAlgorithm()).getVarSubstListOfSegs();
			// create list of variables -> segments
			HashMap<Variable, GeoElement> geos = new HashMap<Variable, GeoElement>();
			for (int i = 0; i < varSubstListOfSegs.size(); ++i) {
				Entry<GeoElement, Variable> e = varSubstListOfSegs.get(i);
				GeoElement g = e.getKey();
				Variable v = e.getValue();
				geos.put(v, g);
			}

			/* contains only geometric quantities (now segments)? */
			boolean qFormula = true;
			String lhs = "", rhs = "";
			TreeMap<Term, Integer> tm1 = p.getTerms();

			outerloop: for (Term t1 : tm1.keySet()) { // e.g. 5*v1^3*v2
				Integer coeff = tm1.get(t1); // e.g. 5
				if (coeff > 0 && !lhs.isEmpty()) { // bridging + on lhs
					lhs += "+";
				}
				if (coeff > 1) { // writing out coeff on lhs
					lhs += coeff;
				}
				if (coeff < 0 && !rhs.isEmpty()) { // bridging + on rhs
					rhs += "+";
				}
				if (coeff < -1) { // writing out -coeff on rhs
					rhs += (-coeff);
				}
				TreeMap<Variable, Integer> tm2 = t1.getTerm();
				/* e.g. v1->3, v2->1 */
				for (Variable t2 : tm2.keySet()) { // e.g. v1
					if (!geos.containsKey(t2)) {
						qFormula = false;
						break outerloop;
					}
					GeoElement g = geos.get(t2);
					String label = g.getLabelSimple();
					if (coeff > 0) {
						lhs += label;
					} else {
						rhs += label;
					}
					int exponent = tm2.get(t2);
					if (exponent > 1) {
						String expString = "^" + exponent;
						if (coeff > 0) {
							lhs += expString;
						} else {
							rhs += expString;
						}
					}
				}
			}
			if (qFormula) {
				if (lhs.isEmpty() || rhs.isEmpty()) {
					// This must be an uninteresting case, e.g. a+b+c=0
					Log.debug(p + " means " + (lhs.isEmpty() ? rhs : lhs)
							+ "=0, uninteresting");
					return null;
				}
				ndgc = new NDGCondition();
				ndgc.setCondition(lhs + "=" + rhs);
				ndgc.setReadability(2);
				Log.debug(p + " means " + lhs + "=" + rhs);
				return ndgc;
			}
			Log.debug(p + " cannot be described by quantities only");
			return null; /* this formula cannot be translated to quantities */
		}

		List<GeoElement> freePoints = ProverBotanasMethod
				.getFreePoints(statement);
		HashSet<GeoElement> freePointsSet = new HashSet<GeoElement>(freePoints);

		// CHECKING COLLINEARITY

		Combinations triplets = new Combinations(freePointsSet, 3);

		while (triplets.hasNext()) {
			HashSet<Object> triplet = (HashSet<Object>) triplets.next();
			Iterator<Object> it = triplet.iterator();
			// GeoElement[] points = (GeoElement[]) triplet.toArray();
			// This is not working directly, so we have to do it manually:
			int i = 0;
			GeoElement[] points = new GeoElement[triplet.size()];
			while (it.hasNext()) {
				points[i] = (GeoElement) it.next();
				i++;
			}
			Variable[] fv1 = ((SymbolicParametersBotanaAlgo) points[0])
					.getBotanaVars(points[0]);
			Variable[] fv2 = ((SymbolicParametersBotanaAlgo) points[1])
					.getBotanaVars(points[1]);
			Variable[] fv3 = ((SymbolicParametersBotanaAlgo) points[2])
					.getBotanaVars(points[2]);
			// Creating the polynomial for collinearity:
			Polynomial coll = Polynomial.collinear(fv1[0], fv1[1], fv2[0],
					fv2[1], fv3[0], fv3[1]).substitute(substitutions);
			if (Polynomial.areAssociates1(p, coll)) {
				Log.debug(p + " means collinearity for " + triplet);
				ndgc = new NDGCondition();
				ndgc.setGeos(points);
				Arrays.sort(ndgc.getGeos());
				ndgc.setCondition("AreCollinear");
				lookupTable.put(keyString, ndgc);
				return ndgc;
			}
		}

		// CHECKING STRONG EQUALITY

		Combinations pairs = new Combinations(freePointsSet, 2);

		while (pairs.hasNext()) {
			HashSet<Object> pair = (HashSet<Object>) pairs.next();
			Iterator<Object> it = pair.iterator();
			// GeoElement[] points = (GeoElement[]) pair.toArray();
			// This is not working directly, so we have to do it manually:
			int i = 0;
			GeoElement[] points = new GeoElement[pair.size()];
			while (it.hasNext()) {
				points[i] = (GeoElement) it.next();
				i++;
			}
			Variable[] fv1 = ((SymbolicParametersBotanaAlgo) points[0])
					.getBotanaVars(points[0]);
			Variable[] fv2 = ((SymbolicParametersBotanaAlgo) points[1])
					.getBotanaVars(points[1]);
			// Creating the polynomial for equality:
			Polynomial eq = Polynomial.sqrDistance(fv1[0], fv1[1], fv2[0],
					fv2[1]).substitute(substitutions);
			if (Polynomial.areAssociates1(p, eq)) {
				Log.debug(p + " means equality for " + pair);
				ndgc = new NDGCondition();
				ndgc.setGeos(points);
				Arrays.sort(ndgc.getGeos());
				ndgc.setCondition("AreEqual");
				ndgc.setReadability(0.5);
				lookupTable.put(keyString, ndgc);
				return ndgc;
			}
		}

		HashSet<Variable> freeXvars = new HashSet<Variable>();
		HashMap<Variable, GeoElement> xvarGeo = new HashMap<Variable, GeoElement>();
		HashSet<Variable> freeYvars = new HashSet<Variable>();
		HashMap<Variable, GeoElement> yvarGeo = new HashMap<Variable, GeoElement>();
		Iterator<GeoElement> it = prover.getStatement().getAllPredecessors()
				.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo.isGeoPoint()
					&& (geo instanceof SymbolicParametersBotanaAlgo)) {
				Variable x = ((SymbolicParametersBotanaAlgo) geo)
						.getBotanaVars(geo)[0];
				if (x.isFree()) {
					freeXvars.add(x);
					xvarGeo.put(x, geo);
				}
				Variable y = ((SymbolicParametersBotanaAlgo) geo)
						.getBotanaVars(geo)[1];
				if (y.isFree()) {
					freeYvars.add(y);
					yvarGeo.put(y, geo);
				}
			}
		}

		// CHECKING EQUALITY (WHERE WE CAN GIVE SUFFICIENT CONDITIONS ONLY)

		pairs = new Combinations(freeXvars, 2);

		while (pairs.hasNext()) {
			HashSet<Object> pair = (HashSet<Object>) pairs.next();
			Iterator<Object> itc = pair.iterator();
			// GeoElement[] points = (GeoElement[]) pair.toArray();
			// This is not working directly, so we have to do it manually:
			int i = 0;
			Variable[] coords = new Variable[pair.size()];
			GeoElement[] points = new GeoElement[pair.size()];
			while (itc.hasNext()) {
				coords[i] = (Variable) itc.next();
				points[i] = xvarGeo.get(coords[i]);
				i++;
			}
			Polynomial xeq = (new Polynomial(coords[0])
					.subtract(new Polynomial(coords[1])))
					.substitute(substitutions);
			if (Polynomial.areAssociates1(p, xeq)) {
				Log.debug(p + " means x-equality for " + pair);
				ndgc = new NDGCondition();
				ndgc.setGeos(points);
				Arrays.sort(ndgc.getGeos());
				ndgc.setCondition("xAreEqual");
				ndgc.setReadability(Double.POSITIVE_INFINITY); // we don't want
																// this
																// condition
				lookupTable.put(keyString, ndgc);
				return ndgc;
			}
		}

		pairs = new Combinations(freeYvars, 2);

		while (pairs.hasNext()) {
			HashSet<Object> pair = (HashSet<Object>) pairs.next();
			Iterator<Object> itc = pair.iterator();
			// GeoElement[] points = (GeoElement[]) pair.toArray();
			// This is not working directly, so we have to do it manually:
			int i = 0;
			Variable[] coords = new Variable[pair.size()];
			GeoElement[] points = new GeoElement[pair.size()];
			while (itc.hasNext()) {
				coords[i] = (Variable) itc.next();
				points[i] = yvarGeo.get(coords[i]);
				i++;
			}
			Polynomial yeq = (new Polynomial(coords[0])
					.subtract(new Polynomial(coords[1])))
					.substitute(substitutions);
			if (Polynomial.areAssociates1(p, yeq)) {
				Log.debug(p + " means y-equality for " + pair);
				ndgc = new NDGCondition();
				ndgc.setGeos(points);
				Arrays.sort(ndgc.getGeos());
				ndgc.setCondition("yAreEqual");
				ndgc.setReadability(Double.POSITIVE_INFINITY); // we don't want
																// this
																// condition
				lookupTable.put(keyString, ndgc);
				return ndgc;
			}
		}

		// CHECKING PERPENDICULARITY AND PARALLELISM

		Combinations pairs1 = new Combinations(freePointsSet, 2);

		while (pairs1.hasNext()) {
			HashSet<Object> pair1 = (HashSet<Object>) pairs1.next();
			Iterator<Object> it1 = pair1.iterator();
			// GeoElement[] points = (GeoElement[]) pair.toArray();
			// This is not working directly, so we have to do it manually:
			int i = 0;
			GeoElement[] points = new GeoElement[4];
			while (it1.hasNext()) {
				points[i] = (GeoElement) it1.next();
				i++;
			}

			Combinations pairs2 = new Combinations(freePointsSet, 2);
			while (pairs2.hasNext()) {
				HashSet<Object> pair2 = (HashSet<Object>) pairs2.next();
				Iterator<Object> it2 = pair2.iterator();
				// GeoElement[] points = (GeoElement[]) pair.toArray();
				// This is not working directly, so we have to do it manually:
				i = 2;
				while (it2.hasNext()) {
					points[i] = (GeoElement) it2.next();
					i++;
				}

				Variable[] fv1 = ((SymbolicParametersBotanaAlgo) points[0])
						.getBotanaVars(points[0]);
				Variable[] fv2 = ((SymbolicParametersBotanaAlgo) points[1])
						.getBotanaVars(points[1]);
				Variable[] fv3 = ((SymbolicParametersBotanaAlgo) points[2])
						.getBotanaVars(points[0]);
				Variable[] fv4 = ((SymbolicParametersBotanaAlgo) points[3])
						.getBotanaVars(points[1]);
				// Creating the polynomial for perpendicularity:
				Polynomial eq = Polynomial.perpendicular(fv1[0], fv1[1],
						fv2[0], fv2[1], fv3[0], fv3[1], fv4[0], fv4[1])
						.substitute(substitutions);
				if (Polynomial.areAssociates1(p, eq)) {
					Log.debug(p + " means perpendicularity for " + pair1
							+ " and " + pair2);
					ndgc = new NDGCondition();
					ndgc.setGeos(points);
					ndgc.setCondition("ArePerpendicular");
					ndgc.setReadability(0.75);
					lookupTable.put(keyString, ndgc);
					return ndgc;
				}
				// Creating the polynomial for parallelism:
				eq = Polynomial.parallel(fv1[0], fv1[1], fv2[0], fv2[1],
						fv3[0], fv3[1], fv4[0], fv4[1]).substitute(
						substitutions);
				if (Polynomial.areAssociates1(p, eq)) {
					Log.debug(p + " means parallelism for " + pair1 + " and "
							+ pair2);
					ndgc = new NDGCondition();
					ndgc.setGeos(points);
					ndgc.setCondition("AreParallel");
					ndgc.setReadability(0.75);
					lookupTable.put(keyString, ndgc);
					return ndgc;
				}
			}
		}

		// CHECKING ISOSCELES TRIANGLES

		pairs = new Combinations(freePointsSet, 2);

		while (pairs.hasNext()) {
			HashSet<Object> pair = (HashSet<Object>) pairs.next();
			Iterator<Object> it1 = pair.iterator();
			// GeoElement[] points = (GeoElement[]) pair.toArray();
			// This is not working directly, so we have to do it manually:
			int i = 0;
			GeoElement[] points = new GeoElement[4];
			while (it1.hasNext()) {
				points[i] = (GeoElement) it1.next();
				i += 2;
			}
			it = freePointsSet.iterator();
			while (it.hasNext()) {
				points[1] = points[3] = it.next();
				Variable[] fv1 = ((SymbolicParametersBotanaAlgo) points[0])
						.getBotanaVars(points[0]);
				Variable[] fv2 = ((SymbolicParametersBotanaAlgo) points[1])
						.getBotanaVars(points[1]);
				Variable[] fv3 = ((SymbolicParametersBotanaAlgo) points[2])
						.getBotanaVars(points[2]);
				// Creating the polynomial for being isosceles:
				Polynomial eq = Polynomial.equidistant(fv1[0], fv1[1], fv2[0],
						fv2[1], fv3[0], fv3[1]).substitute(substitutions);
				if (Polynomial.areAssociates1(p, eq)) {
					Log.debug(p + " means being isosceles triangle for base "
							+ pair + " and opposite vertex " + points[1]);
					ndgc = new NDGCondition();
					ndgc.setGeos(points);
					ndgc.setCondition("IsIsoscelesTriangle");
					ndgc.setReadability(1.25);
					lookupTable.put(keyString, ndgc);
					return ndgc;
				}
			}
		}

		// Unsuccessful run:
		Log.debug("No human readable geometrical meaning found for " + p);
		lookupTable.put(keyString, null);

		return null;
	}
}
