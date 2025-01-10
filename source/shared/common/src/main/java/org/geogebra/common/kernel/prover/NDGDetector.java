package org.geogebra.common.kernel.prover;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoDependentBoolean;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PTerm;
import org.geogebra.common.kernel.prover.polynomial.PVariable;
import org.geogebra.common.util.Prover;
import org.geogebra.common.util.Prover.NDGCondition;
import org.geogebra.common.util.debug.Log;

/**
 * Detects polynomial NDG conditions and turns into human readable form
 * 
 * @author Zoltan Kovacs
 * 
 */
public class NDGDetector {

	private HashMap<String, NDGCondition> lookupTable;
	private Prover prover;
	private HashMap<PVariable, BigInteger> substitutions;
	private Set<PVariable> freeVariables;

	/**
	 * Creates an NDGDetector instance. The NDG detector will try to detect
	 * geometrical meanings of polynomials for the same prover and
	 * substitutions.
	 * 
	 * @param prover
	 *            The prover we are create this instance for.
	 * @param substitutionsInput
	 *            Fix substitutions.
	 * @param freeVariablesInput
	 *            the input set of free variables
	 */
	NDGDetector(Prover prover,
			HashMap<PVariable, BigInteger> substitutionsInput,
			Set<PVariable> freeVariablesInput) {
		lookupTable = new HashMap<>();
		this.prover = prover;
		this.substitutions = substitutionsInput;
		this.freeVariables = freeVariablesInput;
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
	public NDGCondition detect(PPolynomial p) {

		GeoElement statement = prover.getStatement();
		if (statement == null) {
			return null;
		}

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

		// CHECKING FORMULA WITH QUANTITIES

		if (statement.getParentAlgorithm() instanceof AlgoDependentBoolean) {
			// list of segments -> variables
			ArrayList<Entry<GeoElement, PVariable>> varSubstListOfSegs = ((AlgoDependentBoolean) statement
					.getParentAlgorithm()).getProverAdapter()
							.getVarSubstListOfSegs();
			// create list of variables -> segments
			HashMap<PVariable, GeoElement> geos = new HashMap<>();
			if (varSubstListOfSegs != null) {
				for (int i = 0; i < varSubstListOfSegs.size(); ++i) {
					Entry<GeoElement, PVariable> e = varSubstListOfSegs.get(i);
					GeoElement g = e.getKey();
					PVariable v = e.getValue();
					geos.put(v, g);
				}

				/* contains only geometric quantities (now segments)? */
				boolean qFormula = true;
				Kernel kernel = statement.getKernel();

				TreeMap<PTerm, BigInteger> tm1 = p.getTerms();
				ExpressionNode lhs = new ExpressionNode(kernel, 0);
				ExpressionNode rhs = new ExpressionNode(kernel, 0);
				/* are there any expressions on both sides? */
				boolean lt = false;
				boolean rt = false;

				outerloop: for (Entry<PTerm, BigInteger> entry : tm1
						.entrySet()) { // e.g. 5*v1^3*v2
					BigInteger coeff = entry.getValue(); // e.g. 5
			
					/* always use the absolute value */
					ExpressionNode c = new ExpressionNode(kernel,
							coeff.abs().longValue()); // FIXME
					
					TreeMap<PVariable, Integer> tm2 = entry.getKey().getTerm();
					ExpressionNode en = new ExpressionNode(kernel, 1);
					/* e.g. v1->3, v2->1 */

					TreeSet<GeoElement> geoSet = new TreeSet<>();
					HashMap<GeoElement, ExpressionNode> bases = new HashMap<>();
					for (Entry<PVariable, Integer> entry0 : tm2.entrySet()) { // e.g.
																				// v1
						PVariable t2 = entry0.getKey();
						if (!geos.containsKey(t2)) {
							qFormula = false;
							break outerloop;
						}
						GeoElement g = geos.get(t2);
						ExpressionValue t = g.toValidExpression();
						int exponent = entry0.getValue();
						ExpressionNode base = new ExpressionNode(kernel, t);
						if (exponent > 1) {
							base = base.power(exponent);
						}
						geoSet.add(g);
						bases.put(g, base);
					}
					/*
					 * This will sort the terms in order of creation of the
					 * GeoElements, but this is mostly the same as alphabetical
					 * order. Actually, most users want sorting in order of
					 * creation.
					 */
					Iterator<GeoElement> it = geoSet.descendingIterator();
					while (it.hasNext()) {
						GeoElement g = it.next();
						en = en.multiply(bases.get(g));
					}

					if (coeff.compareTo(BigInteger.ZERO) > 0) {
						lhs = lhs.plus(c.multiply(en));
						lt = true;
					} else {
						rhs = rhs.plus(c.multiply(en));
						rt = true;
					}
				}
				Equation eq = new Equation(kernel, lhs, rhs);

				if (qFormula) {
					if (!lt || !rt) {
						// This must be an uninteresting case, e.g. a+b+c=0
						Log.debug(p + " means " + eq + ", uninteresting");
						return null;
					}
					ndgc = new NDGCondition();
					/*
					 * TODO: Later eventually we want to use the equation, not
					 * just an exported string.
					 */
					ndgc.setCondition(
							eq.toString(StringTemplate.defaultTemplate));
					ndgc.setReadability(2);
					Log.debug(p + " means " + eq);
					return ndgc;
				}
				Log.debug(p + " cannot be described by quantities only");
			}
		}

		List<GeoElement> freePoints = ProverBotanasMethod
				.getFreePoints(statement);
		HashSet<GeoElement> freePointsSet = new HashSet<>(freePoints);

		// CHECKING COLLINEARITY

		Combinations<GeoElement> triplets = new Combinations<>(freePointsSet,
				3);

		while (triplets.hasNext()) {
			Set<GeoElement> triplet = triplets.next();
			Iterator<GeoElement> it = triplet.iterator();
			// GeoElement[] points = (GeoElement[]) triplet.toArray();
			// This is not working directly, so we have to do it manually:
			int i = 0;
			GeoElement[] points = new GeoElement[triplet.size()];
			while (it.hasNext()) {
				points[i] = it.next();
				i++;
			}
			PVariable[] fv1, fv2, fv3;
			try {
				fv1 = ((SymbolicParametersBotanaAlgo) points[0])
						.getBotanaVars(points[0]);
				fv2 = ((SymbolicParametersBotanaAlgo) points[1])
						.getBotanaVars(points[1]);
				fv3 = ((SymbolicParametersBotanaAlgo) points[2])
						.getBotanaVars(points[2]);

			} catch (NoSymbolicParametersException e) {
				Log.debug("Cannot get Botana vars during NDG detection");
				return null;
			}
			// Creating the polynomial for collinearity:
			PPolynomial coll = PPolynomial
					.collinear(fv1[0], fv1[1], fv2[0], fv2[1], fv3[0], fv3[1])
					.substitute(substitutions);
			if (PPolynomial.areAssociates1(p, coll)) {
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

		Combinations<GeoElement> pairs = new Combinations<>(freePointsSet, 2);

		while (pairs.hasNext()) {
			Set<GeoElement> pair = pairs.next();
			Iterator<GeoElement> it = pair.iterator();
			// GeoElement[] points = (GeoElement[]) pair.toArray();
			// This is not working directly, so we have to do it manually:
			int i = 0;
			GeoElement[] points = new GeoElement[pair.size()];
			while (it.hasNext()) {
				points[i] = it.next();
				i++;
			}
			PVariable[] fv1, fv2;
			try {
				fv1 = ((SymbolicParametersBotanaAlgo) points[0])
						.getBotanaVars(points[0]);
				fv2 = ((SymbolicParametersBotanaAlgo) points[1])
						.getBotanaVars(points[1]);
			} catch (NoSymbolicParametersException e) {
				Log.debug("Cannot get Botana vars during NDG detection");
				return null;
			}

			// Creating the polynomial for equality:
			PPolynomial eq = PPolynomial
					.sqrDistance(fv1[0], fv1[1], fv2[0], fv2[1])
					.substitute(substitutions);
			if (PPolynomial.areAssociates1(p, eq)) {
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

		HashSet<PVariable> freeXvars = new HashSet<>();
		HashMap<PVariable, GeoElement> xvarGeo = new HashMap<>();
		HashSet<PVariable> freeYvars = new HashSet<>();
		HashMap<PVariable, GeoElement> yvarGeo = new HashMap<>();
		Iterator<GeoElement> it = prover.getStatement().getAllPredecessors()
				.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo.isGeoPoint()
					&& (geo instanceof SymbolicParametersBotanaAlgo)) {
				PVariable x, y;
				try {
					x = ((SymbolicParametersBotanaAlgo) geo)
							.getBotanaVars(geo)[0];
					if (freeVariables.contains(x)) {
						freeXvars.add(x);
						xvarGeo.put(x, geo);
					}
					y = ((SymbolicParametersBotanaAlgo) geo)
							.getBotanaVars(geo)[1];
					if (freeVariables.contains(y)) {
						freeYvars.add(y);
						yvarGeo.put(y, geo);
					}
				} catch (NoSymbolicParametersException e) {
					Log.debug("Cannot get Botana vars during NDG detection");
					return null;
				}
			}
		}

		// CHECKING EQUALITY (WHERE WE CAN GIVE SUFFICIENT CONDITIONS ONLY)

		Combinations<PVariable> pairs2 = new Combinations<>(freeXvars, 2);

		while (pairs2.hasNext()) {
			Set<PVariable> pair = pairs2.next();
			Iterator<PVariable> itc = pair.iterator();
			// GeoElement[] points = (GeoElement[]) pair.toArray();
			// This is not working directly, so we have to do it manually:
			int i = 0;
			PVariable[] coords = new PVariable[pair.size()];
			GeoElement[] points = new GeoElement[pair.size()];
			while (itc.hasNext()) {
				coords[i] = itc.next();
				points[i] = xvarGeo.get(coords[i]);
				i++;
			}
			PPolynomial xeq = (new PPolynomial(coords[0])
					.subtract(new PPolynomial(coords[1])))
							.substitute(substitutions);
			if (PPolynomial.areAssociates1(p, xeq)) {
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

		pairs2 = new Combinations<>(freeYvars, 2);

		while (pairs2.hasNext()) {
			Set<PVariable> pair = pairs2.next();
			Iterator<PVariable> itc = pair.iterator();
			// GeoElement[] points = (GeoElement[]) pair.toArray();
			// This is not working directly, so we have to do it manually:
			int i = 0;
			PVariable[] coords = new PVariable[pair.size()];
			GeoElement[] points = new GeoElement[pair.size()];
			while (itc.hasNext()) {
				coords[i] = itc.next();
				points[i] = yvarGeo.get(coords[i]);
				i++;
			}
			PPolynomial yeq = (new PPolynomial(coords[0])
					.subtract(new PPolynomial(coords[1])))
							.substitute(substitutions);
			if (PPolynomial.areAssociates1(p, yeq)) {
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

		// CHECKING PERPENDICULARITY, PARALLELISM AND CONGRUENCE

		Combinations<GeoElement> pairs1 = new Combinations<>(freePointsSet, 2);

		while (pairs1.hasNext()) {
			Set<GeoElement> pair1 = pairs1.next();
			Iterator<GeoElement> it1 = pair1.iterator();
			// GeoElement[] points = (GeoElement[]) pair.toArray();
			// This is not working directly, so we have to do it manually:
			int i = 0;
			GeoElement[] points = new GeoElement[4];
			while (it1.hasNext()) {
				points[i] = it1.next();
				i++;
			}

			Combinations<GeoElement> pairs3 = new Combinations<>(freePointsSet,
					2);
			while (pairs3.hasNext()) {
				Set<GeoElement> pair2 = pairs3.next();
				Iterator<GeoElement> it2 = pair2.iterator();
				// GeoElement[] points = (GeoElement[]) pair.toArray();
				// This is not working directly, so we have to do it manually:
				i = 2;
				while (it2.hasNext()) {
					points[i] = it2.next();
					i++;
				}

				PVariable[] fv1, fv2, fv3, fv4;
				try {
					fv1 = ((SymbolicParametersBotanaAlgo) points[0])
							.getBotanaVars(points[0]);
					fv2 = ((SymbolicParametersBotanaAlgo) points[1])
							.getBotanaVars(points[1]);
					fv3 = ((SymbolicParametersBotanaAlgo) points[2])
							.getBotanaVars(points[0]);
					fv4 = ((SymbolicParametersBotanaAlgo) points[3])
							.getBotanaVars(points[1]);
				} catch (NoSymbolicParametersException e) {
					Log.debug("Cannot get Botana vars during NDG detection");
					return null;
				}

				// Creating the polynomial for perpendicularity:
				PPolynomial eq = PPolynomial
						.perpendicular(fv1[0], fv1[1], fv2[0], fv2[1], fv3[0],
								fv3[1], fv4[0], fv4[1])
						.substitute(substitutions);
				if (PPolynomial.areAssociates1(p, eq)) {
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
				eq = PPolynomial
						.parallel(fv1[0], fv1[1], fv2[0], fv2[1], fv3[0],
								fv3[1], fv4[0], fv4[1])
						.substitute(substitutions);
				if (PPolynomial.areAssociates1(p, eq)) {
					Log.debug(p + " means parallelism for " + pair1 + " and "
							+ pair2);
					ndgc = new NDGCondition();
					ndgc.setGeos(points);
					ndgc.setCondition("AreParallel");
					ndgc.setReadability(0.75);
					lookupTable.put(keyString, ndgc);
					return ndgc;
				}
				// Creating the polynomial for congruence:
				eq = PPolynomial.sqrDistance(fv1[0], fv1[1], fv2[0], fv2[1])
						.subtract(PPolynomial.sqrDistance(fv3[0], fv3[1],
								fv4[0], fv4[1]))
						.substitute(substitutions);
				if (PPolynomial.areAssociates1(p, eq)) {
					Log.debug(p + " means congruence for " + pair1 + " and "
							+ pair2);
					ndgc = new NDGCondition();
					ndgc.setGeos(points);
					ndgc.setCondition("AreCongruent");
					ndgc.setReadability(0.75);
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

