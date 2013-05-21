/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoIntersectLineConic.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.LocusEquation;
import geogebra.common.kernel.PointPairList;
import geogebra.common.kernel.RestrictionAlgoForLocusEquation;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;
import geogebra.common.kernel.prover.NoSymbolicParametersException;
import geogebra.common.kernel.prover.Polynomial;
import geogebra.common.kernel.prover.Variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 
 * @author Markus
 */
public class AlgoIntersectLineConic extends AlgoIntersect implements SymbolicParametersBotanaAlgo,
	RestrictionAlgoForLocusEquation{
	/** input line */
	protected GeoLine g;
	/** input conic */
	protected GeoConic c;

	private GeoPoint[] D; // D: old points; Q: new points, not yet permuted
	protected GeoPoint[] P, Q; // output -- Q permuted according to D
	protected int intersectionType;

	private HashMap<GeoElement,Polynomial[]> botanaPolynomials;
	private HashMap<GeoElement,Variable[]> botanaVars;
			
	private int age[]; // of defined points D
	private int permutation[]; // of computed intersection points Q to output
								// points P
	private double[][] distTable;
	private boolean isQonPath[]; // for every new intersection point Q: is it on
									// both paths?

	// for every resulting point P: has it ever been defined, i.e. is it alive?
	private boolean isPalive[];

	//private int i;
	private boolean isDefinedAsTangent;
	private boolean firstIntersection = true;
	private boolean isPermutationNeeded = true;
	private GeoPoint tangentPoint;

	private PointPairList pointList = new PointPairList();

	// for segments, rays and conic parts we need to check the
	// intersection points at the end of compute()
	private boolean isLimitedPathSituation;
	protected boolean possibleSpecialCase = false;
	protected boolean handlingSpecialCase = false;
	protected int specialCasePointOnCircleIndex = 0; // index of point on line
														// and conic

	@Override
	public Commands getClassName() {
		return Commands.Intersect;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_INTERSECT;
	}

	public AlgoIntersectLineConic(Construction cons, GeoLine g, GeoConic c) {
		super(cons);
		this.g = g;
		this.c = c;

		isLimitedPathSituation = g.isLimitedPath() || c.isLimitedPath();

		// check special cases

		// if g is defined as a tangent of c, we dont't need
		// to compute anything
		if (g.getParentAlgorithm() instanceof AlgoTangentPoint) {
			AlgoTangentPoint algo = (AlgoTangentPoint) g.getParentAlgorithm();
			tangentPoint = algo.getTangentPoint(c, g);
			isDefinedAsTangent = (tangentPoint != null);
		} else if (g.getParentAlgorithm() instanceof AlgoTangentLine) {
			AlgoTangentLine algo = (AlgoTangentLine) g.getParentAlgorithm();
			tangentPoint = algo.getTangentPoint(c, g);
			isDefinedAsTangent = (tangentPoint != null);
		}

		initElements();

		setInputOutput(); // for AlgoElement
		initForNearToRelationship();
		compute();
		addIncidence(); //must be after compute()

	}

    /**
     * @author Tam
     * 
     * for special cases of e.g. AlgoIntersectLineConic
     */
	private void addIncidence() {
		for (int i = 0; i < P.length; ++i) {
			P[i].addIncidence(g);
			P[i].addIncidence(c);
		}
	}

	// for subclasses
	protected void initElements() {
		// g is defined as tangent of c
		if (isDefinedAsTangent) {
			P = new GeoPoint[1];
			P[0] = new GeoPoint(cons);
			// Q and D are not defined here
		}
		// standard case
		else {
			P = new GeoPoint[2];
			D = new GeoPoint[2];
			Q = new GeoPoint[2];
			distTable = new double[2][2];
			age = new int[2];
			permutation = new int[2];
			isQonPath = new boolean[2];
			isPalive = new boolean[2];

			for (int i = 0; i < 2; i++) {
				Q[i] = new GeoPoint(cons);
				P[i] = new GeoPoint(cons);
				D[i] = new GeoPoint(cons);
			}

			// check possible special case
			possibleSpecialCase = handleSpecialCase();
		}
	}

	// for AlgoElement
	@Override
	public void setInputOutput() {
		input = new GeoElement[2];
		input[0] = c;
		input[1] = g;

		super.setOutput(P);
		noUndefinedPointsInAlgebraView();
		setDependencies(); // done by AlgoElement
	}

	@Override
	public final GeoPoint[] getIntersectionPoints() {
		return P;
	}

	// Made public for LocusEqu
	public GeoLine getLine() {
		return g;
	}

	// Made public for LocusEqu
	public GeoConic getConic() {
		return c;
	}

	@Override
	protected GeoPoint[] getLastDefinedIntersectionPoints() {
		return D;
	}

	@Override
	public boolean isNearToAlgorithm() {
		return true;
	}

	@Override
	public final void initForNearToRelationship() {
		if (isDefinedAsTangent)
			return;

		isPermutationNeeded = true; // for non-continuous intersections
		for (int i = 0; i < P.length; i++) {
			age[i] = 0;
			isQonPath[i] = true;
			isPalive[i] = false;
		}
	}

	// calc intersections of conic c and line g
	@Override
	public void compute() {
		
		
		/*
		 * no probabilistic checking anymore, see #1044
		 */
		// within addIncidenceWithProbabilisticChecking(), updateCascade() is
		// called
		// and we don't what this.compute() to be invoked repeatedly.
		/*
		if (handlingSpecialCase)
			return;
		*/

		// g is defined as tangent of c
		if (isDefinedAsTangent) {
			P[0].setCoords(tangentPoint);
			return;
		}

		// check for special case of line through point on conic
		if (possibleSpecialCase) {
			if (handleSpecialCase())
				return;
		}

		// continous: use near-to-heuristic between old and new intersection
		// points
		// non-continous: use computeContinous() to init a permutation and then
		// always use this permutation
		boolean continous = isPermutationNeeded || kernel.isContinuous()
				|| kernel.getLoadingMode();
		if (continous) {
			computeContinous();
		} else {
			computeNonContinous();
		}

		avoidDoubleTangentPoint();
	}

	/**
	 * There is an important special case we handle separately: The conic
	 * section c is intersected with a line passing through a point A on c. In
	 * this case the first intersection point should always be A.
	 * 
	 * Definition of special case:
	 * There is a pre-exist point which is both dependent and incident to the conic and the line.
	 * 
	 * i.e. there is a special case if and only if there exists one point S such that 
	 * (1) the conic was constructed through S, or S is already some intersection point of the conic with other object;
	 * (2) the line was constructed through S, or S is already some intersection point of the line with other object.
	 * 
	 * Therefore, "addIncidence()" should be called in the following Algos:
	 * AlgoJoinPoints, AlgoJoinPointsRay, AlgoJoinPointsSegment
	 * AlgoLinePointLine, AlgoLinePointVector, AlgoOrthoLinePointLine, AlgoOrthoLinePointVector, AlgoOrthoLinePointConic
	 * AlgoConicFivePoints, AlgoEllipseFociPoint, AlgoHyperbolaFociPoint
	 * AlgoIntersectLineXXX, AlgoIntersectXXXLine
	 * AlgoIntersectConicXXX, AlgoIntersectXXXConic
	 * AlgoPointOnPath
	 * GeoLine.setStartPoint, GeoLine.setEndPoint
	 * 
	 * @return true if this special case was handled.
	 */
	private boolean handleSpecialCase() {
		// see the use in this.compute()
		handlingSpecialCase = true;

		// Numerical check does not work, because when a point incidentally stands on g and c, it may not be considered
		// as special case
		/*
		 * if (g.startPoint != null && c.isOnPath(g.startPoint,
		 * AbstractKernel.MIN_PRECISION)) { pointOnConic = g.startPoint; } else
		 * if (g.endPoint != null && c.isOnPath(g.endPoint,
		 * AbstractKernel.MIN_PRECISION)) { pointOnConic = g.endPoint; } else {
		 * // get points on conic and see if one of them is on line g ArrayList
		 * pointsOnConic = c.getPointsOnConic(); if (pointsOnConic != null) {
		 * int size = pointsOnConic.size(); for (int i=0; i < size; i++) {
		 * GeoPoint p = (GeoPoint) pointsOnConic.get(i); if (g.isOnPath(p,
		 * AbstractKernel.MIN_PRECISION)) { pointOnConic = p; break; } } } }
		 */

		GeoPoint existingIntersection = null;

		// find a point from conic c on line g
		ArrayList<GeoPoint> pointsOnConic = c.getPointsOnConic();
		if (pointsOnConic != null) {
			// get a point from pointsOnConic to see if it is on g.
			for (int i = 0; i < pointsOnConic.size(); ++i) {
				GeoPoint p = pointsOnConic.get(i);
				if (p.isLabelSet()) { // an existing intersection should be a
										// labeled one
					if (p.getIncidenceList() != null
							&& p.getIncidenceList().contains(g)) {

						// TODO: this is just a TEMPORARY FIX for #94.
						//if (g.isOnPath(p, Kernel.EPSILON)
							//	&& c.isOnPath(p, Kernel.EPSILON))
							//existingIntersection = p;

						existingIntersection = p;
						break;
					} /* else if (!(p.getNonIncidenceList() != null && p //no probabilistic checking anymore. See #1044
							.getNonIncidenceList().contains(g))
							&& p.addIncidenceWithProbabilisticChecking(g) 
							) {
						existingIntersection = p;
						break;
					} */
				}
			}
		}

		// if existingIntersection is still not found, find a point from line g
		// on conic c
		if (existingIntersection == null) {
			ArrayList<GeoPoint> pointsOnLine = g.getPointsOnLine();

			if (pointsOnLine != null) {
				// get a point from pointsOnLine to see if it is on c.
				for (int i = 0; i < pointsOnLine.size(); ++i) {
					GeoPoint p = pointsOnLine.get(i);
					if (p.isLabelSet()) { // an existing intersection should be
											// a labeled one
						if (p.getIncidenceList() != null
								&& p.getIncidenceList().contains(c)) {

							// TODO: this is just a TEMPORARY FIX for #94.
							//if (g.isOnPath(p, Kernel.EPSILON)
								//	&& c.isOnPath(p, Kernel.EPSILON))
							//	existingIntersection = p;

							existingIntersection = p;
							break;
						} /* else if (p.addIncidenceWithProbabilisticChecking(c)) { //no probabilistic checking anymore. See #1044
							existingIntersection = p;
							AbstractApplication.debug(p);
							break;
						} */
					}
				}
			}
		}

		// TODO: maybe there's a point neither from conic c nor from line g that
		// is an existing intersection!
		// efficient algorithm for this might only rely on automatic proving

		// when there is no more ProbabilisticChecking
		handlingSpecialCase = false;

		// if existingIntersection is still not found, report no special case
		// handled
		if (existingIntersection == null) {
			return false;
		}

		// calc new intersection points Q
		intersect(c, Q);

		// pointOnConic should be first intersection point
		// Note: if the first intersection point was already set when a file
		// was loaded, then we need to make sure that we don't lose this
		// information
		int firstIndex = specialCasePointOnCircleIndex;
		int secondIndex = (firstIndex + 1) % 2;

		if (firstIntersection && didSetIntersectionPoint(firstIndex)) {
			if (!P[firstIndex].isEqual(existingIntersection)) {
				// pointOnConic is NOT equal to the loaded intersection point:
				// we need to swap the indices
				int temp = firstIndex;
				firstIndex = secondIndex;
				secondIndex = temp;

				specialCasePointOnCircleIndex = firstIndex;
			}
			firstIntersection = false;
		}

		// pointOnConic should be first intersection point
		P[firstIndex].setCoords(existingIntersection);

		// the other intersection point should be the second one
		boolean didSetP1 = false;
		for (int i = 0; i < 2; i++) {
			if (!Q[i].isEqual(P[firstIndex])) {
				P[secondIndex].setCoords(Q[i]);
				didSetP1 = true;
				break;
			}
		}
		if (!didSetP1) // this happens when both intersection points are equal
			P[secondIndex].setCoords(existingIntersection);

		if (isLimitedPathSituation) {
			// make sure the points are on a limited path
			for (int i = 0; i < 2; i++) {
				if (!pointLiesOnBothPaths(P[i]))
					P[i].setUndefined();
			}
		}

		return true;
	}

	/**
	 * Use the current permutation to set output points P from computed points
	 * Q.
	 */
	private void computeNonContinous() {
		// calc new intersection points Q
		intersect(c, Q);

		// use fixed permutation to set output points P
		for (int i = 0; i < P.length; i++) {
			P[i].setCoords(Q[permutation[i]]);
		}

		if (isLimitedPathSituation) {
			// make sure the points are on a limited path
			for (int i = 0; i < P.length; i++) {
				if (!pointLiesOnBothPaths(P[i]))
					P[i].setUndefined();
			}
		}
	}

	/**
	 * We want to find a permutation of Q, so that the distances between old
	 * points Di and new points Qi are minimal.
	 */
	private void computeContinous() {
		/*
		 * D ... old defined points P ... current points Q ... new points
		 * 
		 * We want to find a permutation of Q, so that the distances between old
		 * point Di and new Point Qi are minimal.
		 */
		// remember the defined points D, so that Di = Pi if Pi is defined
		// and set age
		boolean noSingularity = !P[0].isEqual(P[1]); // singularity check
		for (int i = 0; i < 2; i++) {
			boolean finite = P[i].isFinite();

			// don't do this if P[0] = P[1]
			if (noSingularity && finite) {
				D[i].setCoords(P[i]);
				age[i] = 0;
			} else {
				age[i]++;
			}

			// update alive state
			isPalive[i] = isPalive[i] || finite || P[i].labelSet;
		}

		// calc new intersection points Q
		intersect(c, Q);

		// for limited paths we have to distinguish between intersection points
		// Q
		// that lie on both limited paths or not. This is important for choosing
		// the right permutation in setNearTo()
		if (isLimitedPathSituation) {
			updateQonPath();
		}

		if (firstIntersection) {
			// init points in order P[0], P[1]
			int count = 0;
			for (int i = 0; i < Q.length; i++) {
				// make sure interesection points lie on limited paths
				if (Q[i].isDefined() && pointLiesOnBothPaths(Q[i])) {
					P[count].setCoords(Q[i]);
					D[count].setCoords(P[count]);
					firstIntersection = false;
					count++;
				}
			}

			//for (i = count; i < P.length; i++) {
				//P[i].setUndefined();
			//}
			return;
		}

		// calc distance table
		AlgoIntersectConics.distanceTable(D, age, Q, distTable);

		// find permutation and calculate new mean distances
		AlgoIntersectConics.setNearTo(P, isPalive, Q, isQonPath, distTable,
				pointList, permutation, !isPermutationNeeded, 0.000001);
		isPermutationNeeded = false;

		/*
		 * System.out.print("permutation: "); for (int i=0; i <
		 * permutation.length; i++) { System.out.print(permutation[i] + " "); }
		 * Application.debug();
		 */

		// make sure interesection points lie on limited paths
		if (isLimitedPathSituation)
			handleLimitedPaths();
	}

	/**
	 * Checks whether the computed intersection points really lie on the limited
	 * paths. Note: points D[] and P[] may be changed here.
	 */
	private void handleLimitedPaths() {
		// singularity check
		boolean noSingularity = !P[0].isEqual(P[1]);

		for (int i = 0; i < P.length; i++) {
			if (P[i].isDefined()) {
				if (!pointLiesOnBothPaths(P[i])) {
					// the intersection point should be undefined as it doesn't
					// lie
					// on both (limited) paths. However, we want to keep the
					// information
					// of P[i]'s position for our near-to-approach to achieve
					// continous movements.
					// That's why we remember D[i] now
					if (noSingularity && P[i].isFinite()) {
						D[i].setCoords(P[i]);
						// the age will be increased by 1 at the
						// next call of compute() as P[i] will be undefined
						age[i] = -1;
					}
					P[i].setUndefined();
				}
			}
		}
	}

	/**
	 * Checks wether Q[i] lies on g and c and sets isQonPath[] accordingly.
	 */
	private void updateQonPath() {
		for (int i = 0; i < Q.length; i++) {
			isQonPath[i] = pointLiesOnBothPaths(Q[i]);
		}
	}

	private boolean pointLiesOnBothPaths(GeoPoint P) {
		return g.isIntersectionPointIncident(P, Kernel.MIN_PRECISION)
				&& c.isIntersectionPointIncident(P, Kernel.MIN_PRECISION);
	}

	// INTERSECTION TYPES
	public static final int INTERSECTION_PRODUCING_LINE = 1;
	public static final int INTERSECTION_ASYMPTOTIC_LINE = 2;
	public static final int INTERSECTION_MEETING_LINE = 3;
	public static final int INTERSECTION_TANGENT_LINE = 4;
	public static final int INTERSECTION_SECANT_LINE = 5;
	public static final int INTERSECTION_PASSING_LINE = 6;

	/**
	 * Intersects conic c with line g and always sets two GeoPoints (sol). If
	 * there are no real intersections, the coords of GeoPoints are set to
	 * Double.NaN.
	 * 
	 * Also store the intersection type.
	 * 
	 * @returns type of intersection
	 */
	private int intersect(GeoConic c, GeoPoint[] sol) {
		boolean ok = false;
		int ret = INTERSECTION_PASSING_LINE;

		if (c.isDefined() && g.isDefined()) {
			double epsilon = Kernel.MAX_PRECISION;
			while (epsilon <= Kernel.MIN_PRECISION) {
				ret = intersectLineConic(g, c, sol, epsilon);
				ok = testPoints(g, c, sol, Kernel.MIN_PRECISION);
				if (ok) {
					break;
				}
				epsilon *= 10.0;
			}
		}

		// intersection failed
		if (!ok) {
			// Application.debug("INTERSECT LINE CONIC FAILED: epsilon = " +
			// epsilon);
			for (int i = 0; i < 2; i++)
				sol[i].setUndefined();
		}
		intersectionType = ret;
		return ret;
	}

	// do the actual computations
	public final static int intersectLineConic(GeoLine g, GeoConicND c,
			GeoPoint[] sol, double eps) {
		double[] A = c.matrix;

		// get arbitrary point of line
		double px, py;
		if (Math.abs(g.x) > Math.abs(g.y)) {
			px = -g.z / g.x;
			py = 0.0d;
		} else {
			px = 0.0d;
			py = -g.z / g.y;
		}

		// we have to solve u t� + 2d t + w = 0
		// to intersect line g: X = p + t v with conic
		// calc u, d, w:
		// u = v.S.v (S is upper left submatrix of A)
		// d = p.S.v + a.v
		// w = evaluate(p)
		// dis = d^2 - uw, err(dis) = 2d err(d) - u err(w) - w err(u)
		// for simplicity, suppose err(d), err(w), err(u) <= epsilon, then delta
		// = err(dis) <= (|2d|+|u|+|w|)epsilon

		// precalc S.v for u and d
		double SvX = A[0] * g.y - A[3] * g.x;
		double SvY = A[3] * g.y - A[1] * g.x;
		double u = g.y * SvX - g.x * SvY;
		double d = px * SvX + py * SvY + A[4] * g.y - A[5] * g.x;
		double w = c.evaluate(px, py);

		// estimate err for delta; also avoid this too be too large
		double delta = Math.min(Kernel.MIN_PRECISION,
				Math.max(1, Math.abs(2 * d) + Math.abs(u) + Math.abs(w))
						* eps);

		// Erzeugende, Asymptote oder Treffgerade
		if (Kernel.isZero(u,eps)) {
			// Erzeugende oder Asymptote
			if (Kernel.isZero(d,eps)) {
				// Erzeugende
				if (Kernel.isZero(w,eps)) {
					sol[0].setUndefined();
					sol[1].setUndefined();
					return INTERSECTION_PRODUCING_LINE;
				}
				// Asymptote
				// w != 0
				sol[0].setUndefined();
				sol[1].setUndefined();
				return INTERSECTION_ASYMPTOTIC_LINE;

			}
			// Treffgerade
			// d != 0
			double t0 = -w / (2.0 * d);
			if (d < 0) {
				sol[0].setCoords(px + t0 * g.y, py - t0 * g.x, 1.0d);
				sol[1].setUndefined();
			} else { // d > 0
				sol[0].setUndefined();
				sol[1].setCoords(px + t0 * g.y, py - t0 * g.x, 1.0d);
			}
			return INTERSECTION_MEETING_LINE;
		}
		// Tangente, Sekante, Passante
		// u != 0
		double dis = d * d - u * w;
		// Tangente

		// if (AbstractKernel.isZero(dis)) {
		if (Kernel.isEqual(dis, 0, delta)) {
			double t1 = -d / u;
			sol[0].setCoords(px + t1 * g.y, py - t1 * g.x, 1.0);
			sol[1].setCoords(sol[0]);
			return INTERSECTION_TANGENT_LINE;
		}
		// Sekante oder Passante

		// Sekante

		// Double line => one intersection point
		if (c.type == GeoConicNDConstants.CONIC_DOUBLE_LINE) {
			double t1 = -d / u;
			sol[0].setCoords(px + t1 * g.y, py - t1 * g.x, 1.0);
			sol[1].setCoords(sol[0]);
			return INTERSECTION_SECANT_LINE;
		}
		if (dis > 0) {
			dis = Math.sqrt(dis);
			// For accuracy, calculate one root using:
			// (-d +/- dis) / u
			// and the other using:
			// w / (-d +/- dis)
			// Choose the sign of the +/- so that d+dis gets larger in
			// magnitude
			boolean swap = d < 0.0;
			if (swap) {
				dis = -dis;
			}
			double q = -(d + dis);
			double t1 = swap ? w / q : q / u;
			double t2 = swap ? q / u : w / q;

			sol[0].setCoords(px + t1 * g.y, py - t1 * g.x, 1.0);
			sol[1].setCoords(px + t2 * g.y, py - t2 * g.x, 1.0);

			return INTERSECTION_SECANT_LINE;
		}
		// Passante
		// dis < 0
		sol[0].setUndefined();
		sol[1].setUndefined();
		return INTERSECTION_PASSING_LINE;
	}

	/**
	 * Tests if at least one point lies on conic c and line g.
	 */
	final static private boolean testPoints(GeoLine g, GeoConic c,
			GeoPoint[] P, double eps) {
		boolean foundPoint = false;
		for (int i = 0; i < P.length; i++) {
			if (P[i].isDefined()) {
				if (!(c.isOnFullConic(P[i], eps) && g.isOnFullLine(P[i], eps)))
					P[i].setUndefined();
				else
					foundPoint = true;
			}
		}
		return foundPoint;
	}

	public Variable[] getBotanaVars(GeoElement geo) {
		return botanaVars.get(geo);
	}

	public Polynomial[] getBotanaPolynomials(GeoElement geo) throws NoSymbolicParametersException {
		if (botanaPolynomials != null) {
			Polynomial[] ret = botanaPolynomials.get(geo);
			if (ret != null)
				return ret;
		}
		
		// We cannot decide a statement properly if the "line" is a segment or the conic is not a circle:
		if (g != null && c != null && !g.isGeoSegment() && c.isCircle()) {
			Variable[] botanaVarsThis = new Variable[2];
			if (botanaVars == null) {
				botanaVars = new HashMap<GeoElement, Variable[]>();
			}
			if (botanaVars.containsKey(geo)) {
				botanaVarsThis = botanaVars.get(geo);
			} else {
				// Intersection point (we create only one):
				botanaVarsThis = new Variable[2];
				botanaVarsThis[0] = new Variable();
				botanaVarsThis[1] = new Variable();
				botanaVars.put(geo, botanaVarsThis);
			}

			Polynomial[] botanaPolynomialsThis = null;
			// Force NDG that the two intersection points must differ:
			// TODO: This is very ugly.
			Variable[] botanaVarsOther = new Variable[2];
			Iterator<GeoElement> it = botanaVars.keySet().iterator();
			boolean found = false;
			while (it.hasNext()) {
				GeoElement otherGeo = it.next();
				// This should be one element:
				if (!otherGeo.equals(geo)) {
					botanaPolynomialsThis = new Polynomial[3];
					botanaVarsOther = botanaVars.get(otherGeo);
					botanaPolynomialsThis[2] = (Polynomial.sqrDistance(botanaVarsThis[0], botanaVarsThis[1], botanaVarsOther[0], botanaVarsOther[1])
							.multiply(new Polynomial(new Variable()))).subtract(new Polynomial(1));
					found = true;
				}
			}
			if (!found) {
				botanaPolynomialsThis = new Polynomial[2];
			}
			
			Variable[] vg = g.getBotanaVars(geo); // 4 variables from the line
			Variable[] vc = c.getBotanaVars(geo); // 4 variables from the circle
			botanaPolynomialsThis[0] = Polynomial.collinear(vg[0], vg[1], vg[2], vg[3], botanaVarsThis[0], botanaVarsThis[1]); 
			botanaPolynomialsThis[1] = Polynomial.equidistant(vc[2], vc[3], vc[0], vc[1], botanaVarsThis[0], botanaVarsThis[1]);

			if (botanaPolynomials == null) {
				botanaPolynomials = new HashMap<GeoElement, Polynomial[]>();
			}
			botanaPolynomials.put(geo, botanaPolynomialsThis);
			
			return botanaPolynomialsThis;
			
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public boolean isLocusEquable() {
		return true;
	}
	
	public EquationElementInterface buildEquationElementForGeo(GeoElement geo, EquationScopeInterface scope) {
		return LocusEquation.eqnIntersectLineConic(geo, this, scope);
	}

}