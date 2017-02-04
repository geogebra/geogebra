package org.geogebra.common.kernel.prover;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.geogebra.common.cas.GeoGebraCAS;
import org.geogebra.common.cas.singularws.SingularWebService;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.advanced.AlgoDynamicCoordinates;
import org.geogebra.common.kernel.algos.AlgoAngularBisectorPoints;
import org.geogebra.common.kernel.algos.AlgoCirclePointRadius;
import org.geogebra.common.kernel.algos.AlgoDependentBoolean;
import org.geogebra.common.kernel.algos.AlgoDependentNumber;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoEllipseHyperbolaFociPoint;
import org.geogebra.common.kernel.algos.AlgoIntersectConics;
import org.geogebra.common.kernel.algos.AlgoIntersectLineConic;
import org.geogebra.common.kernel.algos.AlgoPointOnPath;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgoAre;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;
import org.geogebra.common.main.App;
import org.geogebra.common.main.ProverSettings;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.ExtendedBoolean;
import org.geogebra.common.util.Prover;
import org.geogebra.common.util.Prover.NDGCondition;
import org.geogebra.common.util.Prover.ProofResult;
import org.geogebra.common.util.Prover.ProverEngine;
import org.geogebra.common.util.debug.Log;

/**
 * A prover which uses Francisco Botana's method to prove geometric theorems.
 * 
 * @author Zoltan Kovacs
 * @author Csilla Solyom-Gecse
 *
 */

public class ProverBotanasMethod {

	private static HashMap<List<Variable>, GeoElement> botanaVarsInv;

	/**
	 * Inverse mapping of botanaVars for a given statement.
	 * 
	 * @param statement
	 *            the input statement
	 */
	static void updateBotanaVarsInv(GeoElement statement) {
		if (botanaVarsInv == null) {
			botanaVarsInv = new HashMap<List<Variable>, GeoElement>();
		}
		Iterator<GeoElement> it = statement.getAllPredecessors().iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (!(geo instanceof GeoNumeric)) {
				Variable[] vars = ((SymbolicParametersBotanaAlgo) geo)
						.getBotanaVars(geo);
				if (vars != null) {
					List<Variable> varsList = Arrays.asList(vars);
					botanaVarsInv.put(varsList, geo);
				}
			}
		}
	}

	/**
	 * Compute free points in a statement.
	 * 
	 * @param statement
	 *            the input statement
	 * @return list of free points
	 */
	public static List<GeoElement> getFreePoints(GeoElement statement) {
		List<GeoElement> freePoints = new ArrayList<GeoElement>();
		Iterator<GeoElement> it = statement.getAllPredecessors().iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo.isGeoPoint() && geo.getParentAlgorithm() == null) {
				/* this is a free point */
				freePoints.add(geo);
			}
		}
		return freePoints;
	}

	/**
	 * Compute predecessor free points of an element, used for locus/envelope.
	 * Works similarly like getFreePoints(), but geos whose parent is
	 * AlgoDynamicCoordinates will be used as free points and their predecessors
	 * can be ignored.
	 * 
	 * @param geo
	 *            input geo
	 * @return list of free points
	 */
	public static HashSet<GeoElement> getLocusFreePoints(GeoElement geo) {
		HashSet<GeoElement> freePoints = new HashSet<GeoElement>();
		AlgoElement algo = geo.getParentAlgorithm();
		if (algo != null) {
			for (GeoElement g : algo.getInput()) {
				AlgoElement a = g.getParentAlgorithm();
				if (g.isGeoPoint() && a == null) {
					/* this is a free point */
					freePoints.add(g);
				} else if (g.isGeoPoint()
						&& a instanceof AlgoDynamicCoordinates) {
					/* this will be considered as a free point */
					freePoints.add(g);
				} else {
					/* find recursively the parents */
					freePoints.addAll(getLocusFreePoints(g));
				}
			}
		}
		return freePoints;
	}

	/**
	 * Creates those polynomials which describe that none of 3 free points can
	 * lie on the same line.
	 * 
	 * @param prover
	 *            the underlying prover
	 * 
	 * @return the NDG polynomials (in denial form)
	 */
	static Polynomial[] create3FreePointsNeverCollinearNDG(Prover prover) {
		/* Creating the set of free points first: */
		List<GeoElement> freePoints = getFreePoints(prover.getStatement());
		int setSize = freePoints.size();

		/* Creating NDGs: */
		NDGCondition ndgc = new NDGCondition();
		if (setSize > 3) {
			ndgc.setCondition("DegeneratePolygon");
		} else {
			ndgc.setCondition("AreCollinear");
		}
		GeoElement[] geos = new GeoElement[setSize];
		int i = 0;
		Iterator<GeoElement> it = freePoints.iterator();
		while (it.hasNext()) {
			geos[i++] = it.next();
		}
		ndgc.setGeos(geos);
		Arrays.sort(ndgc.getGeos());
		prover.addNDGcondition(ndgc);

		/* The output will contain $\binom{n}{3}$ elements: */
		Polynomial[] ret = new Polynomial[setSize * (setSize - 1)
				* (setSize - 2) / 6];
		i = 0;
		/* Creating the set of triplets: */
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
							/*
							 * Only the significantly new triplets will be
							 * processed:
							 */
							if (!triplets.contains(triplet)) {
								triplets.add(triplet);
								Variable[] fv1 = ((SymbolicParametersBotanaAlgo) geo1)
										.getBotanaVars(geo1);
								Variable[] fv2 = ((SymbolicParametersBotanaAlgo) geo2)
										.getBotanaVars(geo2);
								Variable[] fv3 = ((SymbolicParametersBotanaAlgo) geo3)
										.getBotanaVars(geo3);
								/* Creating the polynomial for collinearity: */
								Polynomial p = Polynomial.collinear(fv1[0],
										fv1[1], fv2[0], fv2[1], fv3[0], fv3[1]);
								Log.info("Forcing non-collinearity for points "
										+ geo1.getLabelSimple() + ", "
										+ geo2.getLabelSimple() + " and "
										+ geo3.getLabelSimple());
								/*
								 * Rabinowitsch trick for prohibiting
								 * collinearity:
								 */
								ret[i] = p
										.multiply(
												new Polynomial(new Variable()))
										.subtract(new Polynomial(1));
								/*
								 * FIXME: this always introduces an extra
								 * variable, shouldn't do
								 */
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
	 * Uses a minimal heuristics to fix the first four variables to certain
	 * "easy" numbers. The first two variables (usually the coordinates of the
	 * first point) are set to 0, and the second two variables (usually the
	 * coordinates of the second point) are set to 0 and 1.
	 * 
	 * @param prover
	 *            the input prover
	 * @param coords
	 *            number of fixed coordinates
	 * @return a HashMap, containing the substitutions
	 */
	static HashMap<Variable, Long> fixValues(Prover prover, int coords) {

		long[] fixCoords = { 0, 0, 0, 1 };

		GeoElement statement = prover.getStatement();
		List<GeoElement> freePoints = getFreePoints(statement);
		List<GeoElement> fixedPoints = new ArrayList<GeoElement>();
		/* Adding free points: */
		for (GeoElement ge : freePoints) {
			fixedPoints.add(ge);
		}

		HashMap<Variable, Long> ret = new HashMap<Variable, Long>();

		Iterator<GeoElement> it = fixedPoints.iterator();
		GeoElement[] geos = new GeoElement[2];
		int i = 0, j = 0;
		while (it.hasNext() && i < 2 && j < coords) {
			GeoElement geo = it.next();
			Variable[] fv = ((SymbolicParametersBotanaAlgo) geo)
					.getBotanaVars(geo);
			geos[i] = geo;
			ret.put(fv[0], fixCoords[j]);
			++j;
			if (j < coords) {
				ret.put(fv[1], fixCoords[j]);
				++i;
				++j;
			}
		}

		/* We implicitly assumed that the first two points are different: */
		if (i == 2 && prover.isReturnExtraNDGs()) {
			NDGCondition ndgc = new NDGCondition();
			ndgc.setCondition("AreEqual");
			ndgc.setGeos(geos);
			Arrays.sort(ndgc.getGeos());
			prover.addNDGcondition(ndgc);
		}
		return ret;
	}

	/**
	 * Translation of a geometric statement into an algebraic one. We use
	 * polynomials and integer coefficients. The computations assume that a
	 * complex algebraic geometry approach will be used based on the Groebner
	 * basis method (or Wu's characteristic method, but that's not yet
	 * implemented).
	 */
	public static class AlgebraicStatement {
		/**
		 * The statement in geometric form, e.g. AreCollinear[D,E,F].
		 */
		public GeoElement geoStatement;
		/**
		 * The prover which uses this class.
		 */
		Prover geoProver;
		/**
		 * The set of polynomials which are the translations of the geometric
		 * hypotheses and the thesis. The thesis is stored reductio ad absurdum.
		 */
		public Set<Polynomial> polynomials;
		/**
		 * Should the "false" result be interpreted as undefined?
		 */
		boolean interpretFalseAsUndefined = false;
		/**
		 * Should the "true" result be interpreted as undefined?
		 */
		boolean interpretTrueAsUndefined = false;

		private boolean disallowFixSecondPoint = false;

		/**
		 * @return the polynomials
		 */
		public Set<Polynomial> getPolynomials() {
			return polynomials;
		}

		/**
		 * Add a polynomial to the system manually.
		 * 
		 * @param p
		 *            the polynomial to be added
		 */
		public void addPolynomial(Polynomial p) {
			polynomials.add(p);
		}

		/**
		 * Number of maximal fix coordinates. -1 if no limit. Sometimes we need
		 * to limit the maximum if the construction contains constrained point
		 * on a path.
		 */
		int maxFixcoords = -1;

		/**
		 * A map of substitutions, used only in locus equations and envelopes.
		 */
		public HashMap<Variable, Long> substitutions;
		/**
		 * The variables for x and y, used only in locus equations and
		 * envelopes.
		 */
		public Variable[] curveVars = new Variable[2];

		/**
		 * The result of the proof (even if no computation was done). Sometimes
		 * it can be predicted without any further computations.
		 */
		ProofResult result = null;

		/**
		 * The result of the computation.
		 * 
		 * @return result
		 */
		public ProofResult getResult() {
			return result;
		}

		/**
		 * Create an algebraic equation system of the statement given in the
		 * construction, by using the underlying prover settings.
		 * 
		 * @param statement
		 *            the statement to be proven
		 * @param movingPoint
		 *            use numerical approximation for this element instead of
		 *            computing it symbolically. This can be useful if an
		 *            element cannot be precisely described symbolically: only
		 *            its generalized formula can be described symbolically.
		 * @param prover
		 *            the underlying prover
		 */

		public AlgebraicStatement(GeoElement statement, GeoElement movingPoint,
				Prover prover) {
			if (statement.kernel.getGeoGebraCAS().getCurrentCAS().isLoaded()) {
				algebraicTranslation(statement, movingPoint, prover);
			} else {
				result = ProofResult.PROCESSING;
			}
		}

		private void setHypotheses(GeoElement movingPoint) {
			polynomials = new HashSet<Polynomial>();
			int nHypotheses = 0;
			TreeSet<GeoElement> predecessors = new TreeSet<GeoElement>();
			TreeSet<GeoElement> allPredecessors = geoStatement
					.getAllPredecessors();
			if (geoProver.getProverEngine() == ProverEngine.LOCUS_EXPLICIT) {
				allPredecessors.add(geoStatement);
			}

			Iterator<GeoElement> it;
			/*
			 * The algo of the moving point will be computed numerically after
			 * the end of the symbolic computations. This is not ideal since
			 * some symbolic algos e.g. AlgoIntersectLineConic want to use
			 * existing equations and those will be symbolic in all cases, that
			 * is, it would be better to compute all numerical equations first
			 * or (even better) during the symbolic process.
			 */
			GeoElement numerical = null;
			AlgoElement numAlgo;
			if (movingPoint != null
					&& (numAlgo = movingPoint.getParentAlgorithm()) != null) {
				numerical = (GeoElement) numAlgo.getInput(0);

				/*
				 * Normally we don't want to use the numerical formula for
				 * linear objects. Now we still compute the numerical formula
				 * for most of the cases.
				 */
				if (numerical instanceof GeoSegment
						|| numerical instanceof GeoConicPart) {
					// we don't want the equation of the length
					numerical = null;
				}
			}

			/*
			 * Remove geos directly related with AlgoDependentNumber algos since
			 * we don't want to add them twice (they will be invoked during
			 * their occurrence on a higher level in their geos). Hopefully this
			 * is OK in general, that is, we never need using
			 * AlgoDependentNumber's polynomials directly. If this is still the
			 * case, our idea here must be redesigned. Also remove geos which
			 * should be computed numerically.
			 */
			it = allPredecessors.iterator();
			while (it.hasNext()) {
				GeoElement geo = it.next();
				if (!(geo instanceof GeoNumeric && geo
						.getParentAlgorithm() instanceof AlgoDependentNumber)) {
					predecessors.add(geo);
				}
			}
			ProverSettings proverSettings = ProverSettings.get();
			it = predecessors.iterator();
			while (it.hasNext()) {
				GeoElement geo = it.next();
				if (geo instanceof SymbolicParametersBotanaAlgo) {
					try {
						if (geo instanceof GeoLine
								&& ((GeoLine) geo).hasFixedSlope()
								&& !(geoProver
										.getProverEngine() == ProverEngine.LOCUS_EXPLICIT
										|| geoProver
												.getProverEngine() == ProverEngine.LOCUS_IMPLICIT)) {
							Log.info(
									"Statements containing axes or fixed slope lines are unsupported");
							result = ProofResult.UNKNOWN;
							return;
						}
						if (proverSettings.captionAlgebra) {
							geo.setCaption(null);
						}
						String command = geo
								.getDefinition(StringTemplate.noLocalDefault);
						if (!("".equals(command))) {
							Log.debug(geo.getLabelSimple() + " = "
									+ geo.getDefinition(
											StringTemplate.noLocalDefault)
									+ " /* "
									+ geo.getDefinitionDescription(
											StringTemplate.noLocalDefault)
									+ " */");
						} else {
							String description = geo
									.getAlgebraDescriptionDefault();
							if ((geo instanceof GeoLine
									&& ((GeoLine) geo).hasFixedSlope())
									|| (geo instanceof GeoNumeric)) {
								Log.debug(description);
							} else if (!description.startsWith("xOyPlane")) {
								/*
								 * handling GeoGebra3D's definition for xy-plane
								 */
								Log.debug(description + " /* free point */");
								Variable[] v = new Variable[2];
								v = ((SymbolicParametersBotanaAlgo) geo)
										.getBotanaVars(geo);
								if (proverSettings.captionAlgebra) {
									geo.setCaptionBotanaVars("(" + v[0].toTeX()
											+ "," + v[1].toTeX() + ")");
								}
								if (v != null) {
									Log.debug("// Free point "
											+ geo.getLabelSimple() + "(" + v[0]
											+ "," + v[1] + ")");
								}
							}
						}
						Polynomial[] geoPolys = ((SymbolicParametersBotanaAlgo) geo)
								.getBotanaPolynomials(geo);

						/*
						 * Check if the construction step could be reliably
						 * translated to an algebraic representation. This is
						 * the case for linear constructions (parallel,
						 * perpendicular etc.) but not for quadratic ones
						 * (intersection of conics etc.). In the latter case the
						 * equation system may be solvable even if
						 * geometrically, "seemingly" the statement is true. To
						 * avoid such confusing cases, it's better to report
						 * undefined instead of false.
						 */
						AlgoElement algo = geo.getParentAlgorithm();
						if (algo instanceof AlgoAngularBisectorPoints
								|| algo instanceof AlgoEllipseHyperbolaFociPoint
								|| (algo instanceof AlgoIntersectConics
										&& ((AlgoIntersectConics) algo)
												.existingIntersections() != 1)
								|| (algo instanceof AlgoIntersectLineConic
										&& ((AlgoIntersectLineConic) algo)
												.existingIntersections() != 1)) {
							interpretFalseAsUndefined = true;
							Log.info("Due to " + algo
									+ " is not 1-1 algebraic mapping, FALSE will be interpreted as UNKNOWN");
						}

						if (algo instanceof AlgoCirclePointRadius) {
							disallowFixSecondPoint = true;
						}

						/*
						 * Consider the following case: Let AB a segment and C a
						 * point on it. Move C to A. Now let's check if
						 * Prove[A==C] returns false. Since C is on a line and
						 * normally A=(0,0) and B=(0,1), thus x(C)=0 follows.
						 * But we set x(C) to be a free variable in the
						 * AlgoPointOnPath equation and y(C) to be dependent
						 * which is a bad idea for Cox's method: this scenario
						 * cannot be constructed (the converse scenario: x(C) is
						 * dependent and y(C) is free would be fine), so Cox's
						 * method will return true (because a non-constructible
						 * setting is always contradictory)---even if the
						 * statement is false. So we avoid setting B=(0,1) for
						 * Cox's method when there is a point on a path,
						 * otherwise we will get true for a false statement! See
						 * Example 52 in Zoltan's diss on page 176---here we
						 * need to generalize B to avoid getting true. This will
						 * slow down some things, but that's the price for the
						 * correct behavior. Note that non-linear paths are not
						 * affected.
						 */
						if (algo instanceof AlgoPointOnPath
								&& algo.input[0] instanceof GeoLine
								&& proverSettings.transcext) {
							maxFixcoords = 2;
						}

						if (geoPolys != null) {
							if (geo instanceof GeoPoint) {
								Variable[] v = new Variable[2];
								v = ((SymbolicParametersBotanaAlgo) geo)
										.getBotanaVars(geo);
								Log.debug("// Constrained point "
										+ geo.getLabelSimple() + "(" + v[0]
										+ "," + v[1] + ")");
								if (proverSettings.captionAlgebra) {
									geo.setCaptionBotanaVars("(" + v[0].toTeX()
											+ "," + v[1].toTeX() + ")");
								}
							}
							boolean useThisPoly = true;
							if (algo != null && algo instanceof AlgoPointOnPath
									&& geoProver
											.getProverEngine() == ProverEngine.LOCUS_EXPLICIT) {
								/*
								 * Is this an Envelope command with geo on the
								 * virtual path? In this case we should not
								 * change to the numerical approach.
								 */
								if (!algo.equals(
										geoStatement.getParentAlgorithm())) {
									/*
									 * Skip this object for now: it is a point
									 * on a path. Its coordinates will be used
									 * directly (with substitution) or---for the
									 * moving point---the numerical poly will be
									 * used.
									 */
									useThisPoly = false;
								}
							}
							if (geo.equals(numerical)) {
								// don't create the symbolic equation for a
								// numerically used object
								useThisPoly = false;
							}
							if (numerical == null) {
								// there is no numerical object,
								// so we still use this poly
								useThisPoly = true;
							}
							if (useThisPoly) {
								Log.debug("Hypotheses:");
								for (Polynomial p : geoPolys) {
									polynomials.add(p);
									nHypotheses++;
									Log.debug((nHypotheses) + ". " + p);
									if (proverSettings.captionAlgebra) {
										geo.addCaptionBotanaPolynomial(
												p.toTeX());
									}
								}
							} else {
								Log.debug(
										"This object will be computed numerically");
							}
						}
					} catch (NoSymbolicParametersException e) {
						Log.info(geo.getParentAlgorithm()
								+ " is not fully implemented");
						result = ProofResult.UNKNOWN;
						return;
					}
				} else {
					Log.info(geo.getParentAlgorithm() + " unimplemented");
					result = ProofResult.UNKNOWN;
					return;
				}
			}
			/*
			 * Processing numerical object. The equation computed by GeoGebra
			 * will be used.
			 */
			Log.debug("Processing numerical object");
			if (numerical != null) {
				try {
					Variable[] vars = ((SymbolicParametersBotanaAlgo) movingPoint)
							.getBotanaVars(movingPoint);
					String strForGiac = getFormulaString(numerical);
					Kernel kernel = geoStatement.kernel;
					GeoGebraCAS cas = (GeoGebraCAS) kernel.getGeoGebraCAS();
					String giacOutput4 = "";
					try {
						String giacOutput = cas.getCurrentCAS()
								.evaluateRaw(strForGiac);
						// create a poly instead of equation
						String strForGiac2 = "lhs(" + giacOutput + ")-rhs("
								+ giacOutput + ")";
						String giacOutput2 = cas.getCurrentCAS()
								.evaluateRaw(strForGiac2);
						// create a poly with integer coeffs: lcm of
						// denominators
						String strForGiac3 = "lcm(denom(coeff(" + giacOutput2
								+ ")))";
						String giacOutput3 = cas.getCurrentCAS()
								.evaluateRaw(strForGiac3);
						// multiply with the lcm
						String strForGiac4 = "expand((" + giacOutput2 + ") * "
								+ giacOutput3 + ")";
						giacOutput4 = cas.getCurrentCAS()
								.evaluateRaw(strForGiac4);
					} catch (Throwable t) {
						Log.debug("Unhandled case (Giac)");
					}
					String outputSubst = giacOutput4
							.replaceAll("x", vars[0].toString())
							.replaceAll("y", vars[1].toString());
					/*
					 * Now we have the equation in terms of the Botana
					 * variables. Next, we have to convert the equation to a
					 * Botana polynomial. This piece of code is borrowed from
					 * AlgoDependentNumber.
					 */
					ValidExpression resultVE = cas.getCASparser()
							.parseGeoGebraCASInputAndResolveDummyVars(
									outputSubst, kernel, null);
					PolynomialNode polyNode = new PolynomialNode();
					ExpressionNode en = new ExpressionNode(kernel, resultVE);
					AlgoDependentNumber adn = new AlgoDependentNumber(
							geoStatement.getConstruction(), en, false, null,
							false);
					adn.setBotanaVars(vars);
					adn.buildPolynomialTree(en, polyNode);
					adn.expressionNodeToPolynomial(en, polyNode);
					while (polyNode.getPoly() == null) {
						adn.expressionNodeToPolynomial(en, polyNode);
					}
					/* Finally we obtain the Botana polynomial. */
					Polynomial botanaPolynomial = polyNode.getPoly();
					/* Don't use this algo any longer. */
					movingPoint.getConstruction().removeFromAlgorithmList(adn);
					movingPoint.getConstruction()
							.removeFromConstructionList(adn);
					Log.debug("Hypothesis:");
					polynomials.add(botanaPolynomial);
					nHypotheses++;
					Log.debug((nHypotheses) + ". " + botanaPolynomial);
					if (proverSettings.captionAlgebra) {
						numerical.addCaptionBotanaPolynomial(
								botanaPolynomial.toTeX());
					}
				} catch (NoSymbolicParametersException e) {
					Log.info("Unhandled case on processing numerical objects");
					result = ProofResult.UNKNOWN;
					return;
				}
			}
			Log.debug("Hypotheses have been processed.");
		}

		/**
		 * @param numerical
		 *            numerical object
		 * @return equation as string
		 */
		protected String getFormulaString(GeoElement numerical) {

			return numerical.getFormulaString(
					StringTemplate.giacTemplateInternal, true);
		}

		private void setThesis() {
			try {
				interpretTrueAsUndefined = false;
				/*
				 * The sets of statement polynomials. The last equation of each
				 * set will be negated.
				 */

				Polynomial[][] statements = null;
				AlgoElement ae = geoStatement.getParentAlgorithm();
				if (ae != null) {
					statements = ((SymbolicParametersBotanaAlgoAre) ae)
							.getBotanaPolynomials();
				} else {
					statements = new Polynomial[1][1];
					if (geoStatement instanceof GeoBoolean) {
						if (((GeoBoolean) geoStatement).getBoolean()) {
							statements[0][0] = new Polynomial(0);
						} else {
							statements[0][0] = new Polynomial(1);
						}
					} else {
						Log.debug(
								"Unhandled case, statement is UNKNOWN at the moment");
						result = ProofResult.UNKNOWN;
						return;
					}
				}

				/* case input was an expression */
				if (statements == null) {

					/*
					 * Disallow fixing the second point. This is crucial,
					 * otherwise false theorems like Segment[A,B]==1 will be
					 * proven.
					 */
					maxFixcoords = 2;

					AlgoElement algo = geoStatement.getParentAlgorithm();
					/* get expression string for giac */
					String strForGiac = ((AlgoDependentBoolean) algo)
							.getStrForGiac();
					String userStrForGiac = ((AlgoDependentBoolean) algo)
							.getUserGiacString();

					GeoGebraCAS cas = (GeoGebraCAS) geoStatement.getKernel()
							.getGeoGebraCAS();
					try {
						/* K: extended polynomial */
						String output = cas.getCurrentCAS()
								.evaluateRaw(strForGiac);
						/* F: user's polynomial formula */
						String userOutput = cas.getCurrentCAS()
								.evaluateRaw(userStrForGiac);
						/*
						 * T = K/F: the factor between user's formula and the
						 * extended one
						 */
						String casResult = cas.getCurrentCAS().evaluateRaw(
								"simplify(" + output + "/" + userOutput + ")");
						/* unhandled input expression */
						if (output.contains("?") || userOutput.contains("?")
								|| casResult.contains("?")) {
							this.result = ProofResult.UNKNOWN;
							return;
						}
						/* T is not empty */
						/*
						 * Put possible extended factors into the NDG list. Here
						 * we simply parse the Giac output. This code is ugly,
						 * TODO: use a more elegant way.
						 */
						if (geoProver
								.getProverEngine() != ProverEngine.LOCUS_IMPLICIT
								&& !("{}".equals(casResult))) {
							// skip { and }
							casResult = casResult.substring(1,
									casResult.length() - 1);
							// factorization of the result
							String factResult = cas.getCurrentCAS()
									.evaluateRaw("factor(" + casResult + ")");
							// removing leading - from a product (if any)
							if (factResult.length() > 1 && factResult
									.substring(0, 2).equals("-(")) {
								factResult = factResult.substring(1);
							}
							// split regarding to )*(
							String[] factors = factResult.split("\\)\\*\\(");
							// if there are more factors, the first and last
							// still contain ( and ), trim them
							if (factors.length > 1) {
								factors[0] = factors[0].substring(1);
								factors[factors.length
										- 1] = factors[factors.length - 1]
												.substring(0,
														factors[factors.length
																- 1].length()
																- 1);
							}
							boolean polyIsConst = false;
							if (factors.length == 1 && factors[0]
									.matches("[-+]?\\d*\\.?\\d+")) {
								polyIsConst = true; // poly is a number
							}
							// list of polynomial factors
							ArrayList<Polynomial> polyListOfFactors = new ArrayList<Polynomial>();
							if (!polyIsConst) {
								for (int i = 0; i < factors.length; i++) {
									// parse factors into expression
									ValidExpression resultVE = (geoStatement
											.getKernel().getGeoGebraCAS())
													.getCASparser()
													.parseGeoGebraCASInputAndResolveDummyVars(
															factors[i],
															geoStatement
																	.getKernel(),
															null);
									PolynomialNode polyRoot = new PolynomialNode();
									// build polynomial to parsed expression
									((AlgoDependentBoolean) algo)
											.buildPolynomialTree(
													(ExpressionNode) resultVE,
													polyRoot);
									((AlgoDependentBoolean) algo)
											.expressionNodeToPolynomial(
													(ExpressionNode) resultVE,
													polyRoot);
									while (polyRoot.getPoly() == null) {
										((AlgoDependentBoolean) algo)
												.expressionNodeToPolynomial(
														(ExpressionNode) resultVE,
														polyRoot);
									}
									// add polynomial to list of polys
									Polynomial poly = polyRoot.getPoly();
									if (poly != null) {
										polyListOfFactors.add(poly);
									}
								}
							}

							for (Polynomial p : polyListOfFactors) {
								NDGCondition ndgc = new NDGDetector(geoProver,
										null).detect(p);
								if (ndgc != null) {
									geoProver.addNDGcondition(ndgc);
								}
							}
							/*
							 * Put possible extended factors into the NDG list,
							 * end.
							 */

						}
						/* giac output is not empty */
						if (!("{}".equals(output))) {
							ValidExpression validExpression = (geoStatement
									.getKernel().getGeoGebraCAS())
											.getCASparser()
											.parseGeoGebraCASInputAndResolveDummyVars(
													output,
													geoStatement.getKernel(),
													null);
							PolynomialNode polyRoot = new PolynomialNode();
							ExpressionNode expNode = new ExpressionNode(
									geoStatement.getKernel(),
									((ExpressionNode) validExpression)
											.getLeft());
							MyList list = new MyList(geoStatement.getKernel());
							ExpressionNode root = null;
							if (expNode.getLeft() instanceof MyList) {
								list = ((MyList) expNode.getLeft()).getMyList();
							}
							if (list.getListElement(0).isExpressionNode()) {
								root = (ExpressionNode) list.getListElement(0);
							}

							((AlgoDependentBoolean) algo)
									.buildPolynomialTree(root, polyRoot);
							((AlgoDependentBoolean) algo)
									.expressionNodeToPolynomial(root, polyRoot);
							while (polyRoot.getPoly() == null) {
								((AlgoDependentBoolean) algo)
										.expressionNodeToPolynomial(root,
												polyRoot);
							}
							/* get distance polynomials */
							ArrayList<Polynomial> extraPolys = ((AlgoDependentBoolean) algo)
									.getExtraPolys();
							statements = new Polynomial[1][extraPolys.size()
									+ 1];
							int index = 0;
							for (Polynomial p : extraPolys) {
								statements[0][index] = p;
								index++;
							}

							/* add input polynomial */
							statements[0][index] = polyRoot.getPoly();
						}
						/* case giac result was empty */
						else {
							statements = new Polynomial[1][1];
							statements[0][0] = new Polynomial(0);
						}
					} catch (Throwable e) {
						Log.debug(
								"Unsuccessful run on evaluating the expression, statement is UNKNOWN at the moment");
						result = ProofResult.UNKNOWN;
						return;
					}
				}
				if (disallowFixSecondPoint) {
					maxFixcoords = 2;
				}

				AlgoElement algo = geoStatement.getParentAlgorithm();
				if (algo instanceof AlgoAreCongruent) {
					if (algo.input[0] instanceof GeoAngle
							&& algo.input[1] instanceof GeoAngle) {
						interpretTrueAsUndefined = true;
					}
				}
				if (algo instanceof AlgoDependentBoolean) {
					Operation operation = ((AlgoDependentBoolean) algo)
							.getOperation();
					if (operation == Operation.IS_ELEMENT_OF) {
						if (algo.input[0] instanceof GeoConic
								&& (((GeoConic) algo.input[0]).isEllipse()
										|| ((GeoConic) algo.input[0])
												.isHyperbola())) {
							interpretTrueAsUndefined = true;
						} else if (algo.input[1] instanceof GeoConic
								&& (((GeoConic) algo.input[1]).isEllipse()
										|| ((GeoConic) algo.input[1])
												.isHyperbola())) {
							interpretTrueAsUndefined = true;
						}
					} else if (operation == Operation.EQUAL_BOOLEAN) {
						if ((algo.input[0] instanceof GeoAngle
								&& algo.input[1] instanceof GeoAngle)) {
							interpretTrueAsUndefined = true;
						}
					}
				}

				int k = polynomials.size();

				int minus = 1;
				if (geoProver
						.getProverEngine() == ProverEngine.LOCUS_IMPLICIT) {
					minus = 0;
				}
				ProverSettings proverSettings = ProverSettings.get();
				Log.debug("Thesis equations (non-denied ones):");
				for (int i = 0; i < statements.length; ++i) {
					for (int j = 0; j < statements[i].length - minus; ++j) {
						Log.debug((k + 1) + ". " + statements[i][j]);
						polynomials.add(statements[i][j]);
						if (proverSettings.captionAlgebra) {
							geoStatement.addCaptionBotanaPolynomial(
									statements[i][j].toTeX());
						}
						k++;
					}
				}

				if (geoProver
						.getProverEngine() == ProverEngine.LOCUS_IMPLICIT) {
					Log.debug("Not using refutation");
					return;
				}

				/*
				 * Rabinowitsch trick for the last polynomials of the theses of
				 * the statement. Here we use that NOT (A and B and C) == (NOT
				 * A) or (NOT b) or (NOT c), and disjunctions can be algebraized
				 * by using products.
				 */
				Log.debug(
						"Thesis reductio ad absurdum (denied statement), product of factors:");
				Polynomial spoly = new Polynomial(1);
				Variable z = new Variable();
				/*
				 * It is OK to use the same variable for each factor since it is
				 * enough to find one counterexample only for one of the theses.
				 * See
				 * http://link.springer.com/article/10.1007%2Fs10817-009-9133-x
				 * Appendix, Proposition 6 and Corollary 2 to read more on this.
				 * FIXME: this always introduces an extra variable, shouldn't
				 * do.
				 */
				for (int i = 0; i < statements.length; ++i) {
					Polynomial factor = (statements[i][statements[i].length
							- 1]);
					Log.debug("(" + factor + ")*" + z + "-1");
					factor = factor.multiply(new Polynomial(z))
							.subtract(new Polynomial(1));
					spoly = spoly.multiply(factor);
				}
				polynomials.add(spoly);
				Log.debug("that is,");
				Log.debug((k + 1) + ". " + spoly);
				if (proverSettings.captionAlgebra) {
					geoStatement.addCaptionBotanaPolynomial(spoly.toTeX());
				}

			} catch (NoSymbolicParametersException e) {
				Log.debug(
						"Unsuccessful run, statement is UNKNOWN at the moment");
				result = ProofResult.UNKNOWN;
				return;
			}

		}

		private void algebraicTranslation(GeoElement statement,
				GeoElement numerical, Prover prover) {
			ProverSettings proverSettings = ProverSettings.get();
			geoStatement = statement;
			geoProver = prover;

			/*
			 * Make sure that the prover has the same statement. FIXME: this is
			 * redundant, it would be enough to set the prover here.
			 */
			prover.setStatement(statement);
			setHypotheses(numerical);
			if (result != null) {
				return;
			}
			updateBotanaVarsInv(statement);
			if (prover.getProverEngine() == ProverEngine.LOCUS_EXPLICIT) {
				return;
			}
			setThesis();
			if (result != null) {
				return;
			}

			/*
			 * Only for the Prove command makes sense to set up extra NDG
			 * conditions
			 */
			if (prover.getProverEngine() != ProverEngine.RECIOS_PROVER
					&& proverSettings.freePointsNeverCollinear != null
					&& proverSettings.freePointsNeverCollinear
					&& !(prover.isReturnExtraNDGs())) {
				for (Polynomial p : create3FreePointsNeverCollinearNDG(
						prover)) {
					polynomials.add(p);
				}
			}
		}
	}

	/**
	 * Proves the statement by using Botana's method
	 * 
	 * @param prover
	 *            the prover input object
	 * @return if the statement is true
	 */
	public ProofResult prove(Prover prover) {

		GeoElement statement = prover.getStatement();
		ProverSettings proverSettings = ProverSettings.get();
		/*
		 * Decide quickly if proving this kind of statement is already
		 * implemented at all:
		 */
		if (!(statement
				.getParentAlgorithm() instanceof SymbolicParametersBotanaAlgoAre)) {
			Log.info(statement.getParentAlgorithm() + " unimplemented");
			return ProofResult.UNKNOWN;
			/*
			 * If not, let's not spend any time here, but give up immediately.
			 */
		}

		/* If Singular is not available, let's try Giac (mainly on web) */
		SingularWebService singularWS = App.getSingularWS();
		if (singularWS == null || (!singularWS.isAvailable())) {
			proverSettings.transcext = false;
		}

		/* The NDG conditions (automatically created): */
		if (proverSettings.freePointsNeverCollinear == null) {
			if (App.singularWSisAvailable()) {
				/* SingularWS will use Cox' method */
				proverSettings.freePointsNeverCollinear = false;
			} else {
				proverSettings.freePointsNeverCollinear = true;
			}
		}

		AlgebraicStatement as = new AlgebraicStatement(statement, null, prover);

		/*
		 * It's possible that we already know the answer without computing
		 * anything on the polynomials. If yes, we quit here and return the
		 * known result.
		 */
		if (as.result != null) {
			return as.result;
		}

		/* Set substitutions. */
		HashMap<Variable, Long> substitutions = null;
		int fixcoords = 0;
		if (prover.isReturnExtraNDGs()) {
			fixcoords = proverSettings.useFixCoordinatesProveDetails;
		} else {
			fixcoords = proverSettings.useFixCoordinatesProve;
		}
		if (as.maxFixcoords >= 0 && as.maxFixcoords < fixcoords) {
			fixcoords = as.maxFixcoords;
		}
		if (fixcoords > 0) {
			substitutions = fixValues(prover, fixcoords);
			Log.debug("substitutions: " + substitutions);
		}

		if (prover.isReturnExtraNDGs()) {
			/* START OF PROVEDETAILS. */
			Set<Set<Polynomial>> eliminationIdeal;
			NDGDetector ndgd = new NDGDetector(prover, substitutions);

			boolean found = false;
			int permutation = 0;
			int MAX_PERMUTATIONS = 1; /*
										 * Giac cannot permute the variables at
										 * the moment.
										 */
			if (App.singularWSisAvailable()) {
				/*
				 * TODO: Limit MAX_PERMUTATIONS to (#freevars-#substitutes)! to
				 * prevent unneeded computations:
				 */
				MAX_PERMUTATIONS = 8; /*
										 * intuitively set, see Polynomial.java
										 * for more on info (Pappus6 will work
										 * with 7, too)
										 */
				/* Pappus6 is at http://www.tube.geogebra.org/student/m57255 */
			}
			while (!found && permutation < MAX_PERMUTATIONS) {

				eliminationIdeal = Polynomial.eliminate(
						as.polynomials
								.toArray(new Polynomial[as.polynomials.size()]),
						substitutions, statement.getKernel(), permutation++,
						true, false);
				if (eliminationIdeal == null) {
					return ProofResult.UNKNOWN;
				}

				Iterator<Set<Polynomial>> ndgSet = eliminationIdeal.iterator();

				List<HashSet<GeoPoint>> xEqualSet = new ArrayList<HashSet<GeoPoint>>();
				xEqualSet.add(new HashSet<GeoPoint>());
				List<HashSet<GeoPoint>> yEqualSet = new ArrayList<HashSet<GeoPoint>>();
				yEqualSet.add(new HashSet<GeoPoint>());
				boolean xyRewrite = (eliminationIdeal.size() == 2);

				List<NDGCondition> bestNdgSet = new ArrayList<NDGCondition>();
				double bestScore = Double.POSITIVE_INFINITY;
				int ndgI = 0;
				while (ndgSet.hasNext()) {
					ndgI++;
					Log.debug("Considering NDG " + ndgI + "...");
					List<NDGCondition> ndgcl = new ArrayList<NDGCondition>();
					double score = 0.0;
					/*
					 * All NDGs must be translatable into human readable form.
					 */
					boolean readable = true;
					Set<Polynomial> thisNdgSet = ndgSet.next();
					Iterator<Polynomial> ndg = thisNdgSet.iterator();
					while (ndg.hasNext() && readable) {
						Polynomial poly = ndg.next();
						if (poly.isZero()) {

							/*
							 * Here we know that the statement is reported to be
							 * not generally true.
							 */
							Log.debug("Statement is NOT GENERALLY TRUE");

							if (as.interpretFalseAsUndefined) {
								Log.debug("Interpreting FALSE as UNKNOWN");
								return ProofResult.UNKNOWN;
							}
							return ProofResult.FALSE;
						}

						/*
						 * Here we know that the statement is reported to be
						 * generally true with some NDGs.
						 */
						if (!poly.isConstant()) {
							if (as.interpretTrueAsUndefined) {
								Log.debug("Interpreting TRUE as UNKNOWN");
								return ProofResult.UNKNOWN;
							}
							NDGCondition ndgc = ndgd.detect(poly);
							if (ndgc == null) {
								readable = false;
							} else {
								/*
								 * Check if this elimination ideal equals to
								 * {xM-xN,yM-yN}:
								 */
								xyRewrite = (xyRewrite
										&& thisNdgSet.size() == 1);
								/*
								 * Note that in some cases the CAS may return
								 * (xM-xN)*(-1) which consists of two factors,
								 * so thisNdgSet.size() == 1 will fail. Until
								 * now there is no experience of such behavior
								 * for such simple ideals, so maybe this check
								 * is OK.
								 */
								if (xyRewrite) {
									if (ndgc.getCondition()
											.equals("xAreEqual")) {
										HashSet<GeoPoint> points = new HashSet<GeoPoint>();
										points.add(
												(GeoPoint) ndgc.getGeos()[0]);
										points.add(
												(GeoPoint) ndgc.getGeos()[1]);
										xEqualSet.add(points);
									}
									if (ndgc.getCondition()
											.equals("yAreEqual")) {
										HashSet<GeoPoint> points = new HashSet<GeoPoint>();
										points.add(
												(GeoPoint) ndgc.getGeos()[0]);
										points.add(
												(GeoPoint) ndgc.getGeos()[1]);
										yEqualSet.add(points);
									}
									if (xEqualSet.size() == 1
											&& xEqualSet.equals(yEqualSet)) {
										/*
										 * If yes, set the condition to
										 * AreEqual(M,N) and readable enough:
										 */
										ndgc.setCondition("AreEqual");
										ndgc.setReadability(0.5);
									}
								}

								ndgcl.add(ndgc);
								score += ndgc.getReadability();
							}
						}
					}
					/*
					 * Now we take the set if the conditions are readable and
					 * the set is the current best. TODO: Here we should
					 * simplify the NDGs, i.e. if one of them is a logical
					 * consequence of others, then it should be eliminated.
					 */
					if (readable && score < bestScore) {
						Log.debug("Found a better NDG score (" + score
								+ ") than " + bestScore);
						bestScore = score;
						bestNdgSet = ndgcl;
						found = true;
					} else {
						if (readable) {
							Log.debug("Not better than previous NDG score ("
									+ bestScore + "), this is " + score);
						} else {
							Log.debug("...unreadable");
						}
					}
				}
				if (found) {
					Iterator<NDGCondition> ndgc = bestNdgSet.iterator();
					while (ndgc.hasNext()) {
						prover.addNDGcondition(ndgc.next());
					}
				}
			}
			/*
			 * No readable NDGs was found, search for another prover to make a
			 * better job:
			 */
			if (!found) {
				Log.debug("Statement is TRUE but NDGs are UNREADABLE");
				return ProofResult.TRUE_NDG_UNREADABLE;
			}
			/* END OF PROVEDETAILS. */

			/* START OF PROVE. */
		} else {
			ExtendedBoolean solvable = Polynomial.solvable(
					as.polynomials
							.toArray(new Polynomial[as.polynomials.size()]),
					substitutions, statement.getKernel(),
					proverSettings.transcext);
			if (ExtendedBoolean.UNKNOWN.equals(solvable)) {
				/*
				 * Prover returned with no success, search for another prover:
				 */
				Log.debug(
						"Unsuccessful run, statement is UNKNOWN at the moment");
				return ProofResult.UNKNOWN;
			}
			if (solvable.boolVal()) {
				if (!proverSettings.transcext) {
					/*
					 * We cannot reliably tell if the statement is really false:
					 */
					Log.debug(
							"No transcext support, system is solvable, statement is UNKNOWN");
					return ProofResult.UNKNOWN;
				}
				/* Here we know that the statement is not generally true. */
				Log.debug("Statement is NOT GENERALLY TRUE");

				if (as.interpretFalseAsUndefined
						&& !as.interpretTrueAsUndefined) {
					Log.debug("Interpreting FALSE as UNKNOWN");
					return ProofResult.UNKNOWN;
				}
				return ProofResult.FALSE;
			}
		}
		if (as.interpretTrueAsUndefined) {
			Log.debug("Interpreting TRUE as UNKNOWN");
			return ProofResult.UNKNOWN;
		}
		Log.debug("Statement is GENERALLY TRUE");
		return ProofResult.TRUE;
	}
}