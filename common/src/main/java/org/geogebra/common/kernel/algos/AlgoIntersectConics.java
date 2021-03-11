/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoIntersectConics.java
 *
 * Created on 1. Dezember 2001
 */

package org.geogebra.common.kernel.algos;

import java.util.ArrayList;
import java.util.Arrays;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.EquationSolver;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.PointPair;
import org.geogebra.common.kernel.PointPairList;
import org.geogebra.common.kernel.SystemOfEquationsSolver;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.adapters.IntersectConicsAdapter;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Computes intersection points of two conic sections
 *
 * @author Markus Hohenwarter
 */
public class AlgoIntersectConics extends AlgoIntersect implements SymbolicParametersBotanaAlgo {

	/**
	 * number of old distances that are used to compute the mean distance change of
	 * one point
	 **/
	static final int DIST_MEMORY_SIZE = 8;

	private GeoConic A;
	private GeoConic B;
	private GeoPoint[] P;
	private GeoPoint[] D;
	private GeoPoint[] Q; // points
	/**
	 * pre-existing intersection points before this Algo is constructed
	 */
	ArrayList<GeoPointND> preexistPoints;
	/**
	 * Defined points from P
	 **/
	ArrayList<GeoPoint> newPoints;

	private GeoConic degConic;
	private GeoLine tempLine;
	private int[] age; // for points in D
	private int[] permutation; // of computed intersection points Q to output
	// points P
	private double[][] distTable;
	private boolean[] isQonPath;
	private boolean[] isPalive; // has P ever been defined?
	private boolean firstIntersection = true;
	// private int i;
	private boolean isLimitedPathSituation;
	private boolean isPermutationNeeded = true;
	private boolean possibleSpecialCase = false;
	private int specialCasePointOnCircleIndex = 0; // index of point on both
	// circles

	private final PointPairList pointList = new PointPairList();

	private SystemOfEquationsSolver sysSolver;

	private IntersectConicsAdapter proverAdapter;

	@Override
	public Commands getClassName() {
		return Commands.Intersect;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_INTERSECT;
	}

	/**
	 * @param cons construction
	 */
	public AlgoIntersectConics(Construction cons) {
		super(cons);
		init(cons);
	}

	/**
	 * @param cons                  construction
	 * @param addToConstructionList whether to add to construction list
	 */
	public AlgoIntersectConics(Construction cons, boolean addToConstructionList) {
		super(cons, addToConstructionList);
		init(cons);
	}

	private void init(Construction cons1) {
		EquationSolver eqnSolver = cons1.getKernel().getEquationSolver();
		sysSolver = cons1.getKernel().getSystemOfEquationsSolver(eqnSolver);

		degConic = new GeoConic(cons1);
	}

	/**
	 * @param cons construction
	 * @param A    first conic
	 * @param B    second conic
	 */
	public AlgoIntersectConics(Construction cons, GeoConic A, GeoConic B) {
		this(cons);

		this.A = A;
		this.B = B;
		isLimitedPathSituation = A.isLimitedPath() || B.isLimitedPath();

		// init temp vars
		P = new GeoPoint[4]; // output
		D = new GeoPoint[4];
		Q = new GeoPoint[4];

		preexistPoints = new ArrayList<>();
		newPoints = new ArrayList<>();
		isQonPath = new boolean[4];
		isPalive = new boolean[4];
		age = new int[4];
		permutation = new int[] { 0, 1, 2, 3 };
		distTable = new double[4][4];
		for (int i = 0; i < 4; i++) {
			P[i] = new GeoPoint(cons);
			Q[i] = new GeoPoint(cons);
			D[i] = new GeoPoint(cons);
		}

		// check possible special case
		possibleSpecialCase = handleSpecialCase();

		setInputOutput(); // for AlgoElement

		ArrayList<GeoPointND> list1 = A.getPointsOnConic();
		ArrayList<GeoPointND> list2 = B.getPointsOnConic();

		if (list1 != null && list2 != null) {
			for (int i = 0; i < list1.size(); i++) {
				if (list1.get(i).getIncidenceList() != null
						&& list1.get(i).getIncidenceList().contains(B)) {
					preexistPoints.add(list1.get(i));
				}
			}
		}

		initForNearToRelationship();
		compute();
		addIncidence();
	}

	/**
	 * @author Tam
	 *
	 *         for special cases of e.g. AlgoIntersectLineConic
	 */
	private void addIncidence() {
		for (int i = 0; i < 4; i++) {
			P[i].addIncidence(A, false);
			P[i].addIncidence(B, false);
		}
	}

	// for AlgoElement
	@Override
	public void setInputOutput() {
		input = new GeoElement[2];
		input[0] = A;
		input[1] = B;

		super.setOutput(P);
		noUndefinedPointsInAlgebraView();
		setDependencies(); // done by AlgoElement
	}

	@Override
	public GeoPoint[] getIntersectionPoints() {
		return P;
	}

	/**
	 * Made public for LocusEqu
	 *
	 * @return first conic
	 */
	public GeoConic getA() {
		return A;
	}

	/**
	 * Made public for LocusEqu
	 *
	 * @return second conic
	 */
	public GeoConic getB() {
		return B;
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
		isPermutationNeeded = true;
		for (int i = 0; i < P.length; i++) {
			age[i] = 0;
			isQonPath[i] = true;
			isPalive[i] = false;
		}
	}

	// calc intersections of conics A and B
	@Override
	public final void compute() {

		if (permutation[3] == 0) {
			Log.error("error in AlgoIntersectConics");
		}

		// check if conics A and B are defined
		if (!(A.isDefined() && B.isDefined())) {
			for (int i = 0; i < P.length; i++) {
				P[i].setUndefined();
			}
			return;
		}

		// check for special case of two circles with common point
		if (possibleSpecialCase) {
			if (handleSpecialCase()) {
				return;
			}
		}

		// continous: use near-to-heuristic between old and new intersection
		// points
		// non-continous: use computeContinous() to init a permutation and then
		// always use this permutation
		boolean continuous = isPermutationNeeded || kernel.isContinuous();
		if (continuous) {
			computeContinuous();
		} else {
			computeNonContinuous();
		}

		matchExistingIntersections();
		avoidDoubleTangentPoint();
	}

	private void matchExistingIntersections() {
		if (preexistPoints.size() == 0) {
			return;
		}

		newPoints.clear();

		for (int i = 0; i < 4; i++) {
			if (P[i].isDefined()) {
				newPoints.add(P[i]);
			}
		}

		if (newPoints.size() == 0) {
			return;
		}

		double gap = Double.POSITIVE_INFINITY;
		double minDistance = Double.POSITIVE_INFINITY;
		double d = Double.POSITIVE_INFINITY;
		int closestPointIndex = 0; // for preexist point

		for (int i = 0; i < 4; i++) {
			for (int j = i + 1; j < 4; j++) {
				if (P[i].isDefined() && P[j].isDefined()) {
					d = P[i].distance(P[j]);
					if (d < gap) {
						gap = d;
					}
				}
			}
		}

		for (int i = 0; i < 4; i++) {
			if (P[i].isDefined()) {
				minDistance = Double.POSITIVE_INFINITY;
				for (int j = 0; j < preexistPoints.size(); j++) {
					d = preexistPoints.get(j).distance(P[i]);
					if (d < minDistance) {
						minDistance = d;
						closestPointIndex = j;
					}
				}

				if (DoubleUtil.isGreaterEqual(gap / 2, minDistance)) {
					P[i].setCoordsFromPoint(preexistPoints.get(closestPointIndex));
				}
			}
		}
	}

	/**
	 * There is an important special case we handle separately: Both conic sections
	 * are circles and one is defined through a point A on the other one. In this
	 * case the first intersection point should always be A.
	 *
	 * @return true if this special case was handled.
	 */
	private boolean handleSpecialCase() {

		// we need two circles
		if (A.type != GeoConicNDConstants.CONIC_CIRCLE
				|| B.type != GeoConicNDConstants.CONIC_CIRCLE) {
			return false;
		}

		// check if we have a point on A that is also on B
		GeoPointND pointOnConic = getPointFrom1on2(A, B);
		if (pointOnConic == null) {
			// check if we have a point on B that is also on A
			pointOnConic = getPointFrom1on2(B, A);
		}

		// if we didn't have a common point, there's no special case
		if (pointOnConic == null) {
			return false;
		}

		// intersect the two circles
		intersectConicsWithEqualSubmatrixS(A, B, Q, Kernel.STANDARD_PRECISION);

		// pointOnConic should be first intersection point
		// Note: if the first intersection point was already set when a file
		// was loaded, then we need to make sure that we don't lose this
		// information
		int firstIndex = specialCasePointOnCircleIndex;
		int secondIndex = (firstIndex + 1) % 2;

		if (firstIntersection && didSetIntersectionPoint(firstIndex)) {
			if (!P[firstIndex].isEqual(pointOnConic)) {
				// pointOnConic is NOT equal to the loaded intersection point:
				// we need to swap the indices
				int temp = firstIndex;
				firstIndex = secondIndex;
				secondIndex = temp;

				specialCasePointOnCircleIndex = firstIndex;
			}
			firstIntersection = false;
		}

		P[firstIndex].setCoordsFromPoint(pointOnConic);

		// the other intersection point should be the second one
		boolean didSetP1 = false;
		for (int i = 0; i < 2; i++) {
			if (!Q[i].isEqual(P[firstIndex])) {
				P[secondIndex].setCoords(Q[i]);
				didSetP1 = true;
				break;
			}
		}
		if (!didSetP1) { // this happens when both intersection points are equal
			P[secondIndex].setCoordsFromPoint(pointOnConic);
		}

		if (isLimitedPathSituation) {
			// make sure the points are on a limited path
			for (int i = 0; i < 2; i++) {
				if (!pointLiesOnBothPaths(P[i])) {
					P[i].setUndefined();
				}
			}
		}

		return true;
	}

	private static GeoPointND getPointFrom1on2(GeoConic A, GeoConic B) {
		GeoPointND pointOnConic = null;

		// check if a point on A is also on B
		// get points on conic and see if one of them is on line g
		ArrayList<GeoPointND> pointsOnConic = A.getPointsOnConic();
		if (pointsOnConic != null) {
			int size = pointsOnConic.size();
			for (int i = 0; i < size; i++) {
				GeoPointND p = pointsOnConic.get(i);
				if (p.isLabelSet() && p.getIncidenceList() != null
						&& p.getIncidenceList().contains(B)) {

					// TODO: a potential temporary fix for #94.
					if (A.isOnPath(p, Kernel.STANDARD_PRECISION)
							&& B.isOnPath(p, Kernel.STANDARD_PRECISION)) {
						pointOnConic = p;
					}

					break;
				}
			}
		}

		return pointOnConic;
	}

	/**
	 * Use the current permutation to set output points P from computed points Q.
	 */
	private void computeNonContinuous() {
		// calc new intersection points Q
		intersectConics(A, B, Q);

		// use fixed permutation to set output points P
		for (int i = 0; i < P.length; i++) {
			P[i].setCoords(Q[permutation[i]]);
		}

		if (isLimitedPathSituation) {
			// make sure the points are on a limited path
			for (int i = 0; i < P.length; i++) {
				if (!pointLiesOnBothPaths(P[i])) {
					P[i].setUndefined();
				}
			}
		}
	}

	// calc intersections of conics A and B, with the permutation
	// according to a near-to relationship with the old permutation
	// then store the current permutation to int[] storePermutation.
	final private void computeContinuous() {
		/*
		 * D ... old defined points P ... current points Q ... new points
		 *
		 * We want to find a permutation of Q, so that the sum of squared distances
		 * between old point Di and new Point Qi is minimal. The distances are weighed
		 * by Di's age (i.e. how long it has not been reset by a finit intersection
		 * point).
		 */

		// if there are only two points P[i] that are defined and equal
		// we are in a singularity situation
		boolean noSingularity = !isSingularitySituation();

		// remember the defined points D, so that Di = Pi if Pi is finite
		// and set age
		for (int i = 0; i < 4; i++) {
			boolean finite = P[i].isFinite();

			if (noSingularity && finite) {
				D[i].setCoords(P[i]);
				age[i] = 0;
			} else {
				age[i]++;
			}

			// update alive state
			isPalive[i] = isPalive[i] || finite || P[i].isLabelSet();
		}

		// calc new intersection Points Q
		intersectConics(A, B, Q);

		// for limited paths we have to distinguish between intersection points
		// Q
		// that lie on both limited paths or not. This is important for choosing
		// the right permutation in setNearTo()
		if (isLimitedPathSituation) {
			updateQonPath();
		}

		if (firstIntersection) {
			// init points in order P[0], P[1] , ...
			int count = 0;
			for (int i = 0; i < Q.length; i++) {
				// make sure intersection points lie on limited paths
				if (Q[i].isDefined() && pointLiesOnBothPaths(Q[i])) {
					P[count].setCoords(Q[i]);
					D[count].setCoords(P[count]);
					firstIntersection = false;
					count++;
				}
			}
			// make points loaded from XML undefined TRAC-643
			for (int i = count; i < P.length; i++) {
				P[i].setUndefined();

			}
			return;
		}

		// calc distance table of defined points D and new points Q
		distanceTable(D, age, Q, distTable);

		// find permutation
		setNearTo(P, isPalive, Q, isQonPath, distTable, pointList, permutation,
				!isPermutationNeeded, 1.0 / Math.min(getKernel().getXscale(),
						getKernel().getYscale()));

		isPermutationNeeded = false;

		// make sure intersection points lie on limited paths
		if (isLimitedPathSituation) {
			handleLimitedPaths();
		}
	}

	/**
	 * Checks whether the computed intersection points really lie on the limited
	 * paths. Note: points D[] and P[] may be changed here.
	 */
	private void handleLimitedPaths() {
		// singularity check
		boolean noSingularity = !isSingularitySituation();

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
	 * Checks whether Q[i] lies on g and c and sets isQonPath[] accordingly.
	 */
	private void updateQonPath() {
		for (int i = 0; i < Q.length; i++) {
			isQonPath[i] = pointLiesOnBothPaths(Q[i]);
		}
	}

	private boolean pointLiesOnBothPaths(GeoPoint Pt) {
		return A.isIntersectionPointIncident(Pt, Kernel.MIN_PRECISION)
				&& B.isIntersectionPointIncident(Pt, Kernel.MIN_PRECISION);
	}

	/**
	 * Returns wheter we are in a singularity situation. This is the case whenever
	 * there are only two points P[i] that are defined and equal.
	 */
	private boolean isSingularitySituation() {
		int count = 0;
		int[] index = new int[P.length];

		for (int i = 0; i < P.length; i++) {
			if (P[i].isDefined()) {
				index[count] = i;
				count++;
				if (count > 2) {
					return false;
				}
			}
		}

		// we have a singularity if there are two defined points
		// that are equal
		return (count == 2 && P[index[0]].isEqual(P[index[1]]));
	}

	/**
	 * calc four intersection Points of conics A and B. write result into points
	 *
	 * @param conic1 first conic
	 * @param conic2 second conic
	 * @param points output array
	 */
	final public void intersectConics(GeoConic conic1, GeoConic conic2, GeoPoint[] points) {

		if (!(conic1.isDefined() && conic2.isDefined())) {
			for (int i = 0; i < points.length; i++) {
				points[i].setUndefined();
			}
			return;
		}

		boolean ok = false;
		int i = 0;

		// equal conics have no intersection points, unless they are themselves
		// single points.
		if (conic1.equals(conic2)) {

			for (i = 0; i < points.length; i++) {
				points[i].setUndefined();
			} /*
			 * TODO if (conic1.type == GeoConicNDConstants.CONIC_SINGLE_POINT){
			 * points[0].setCoords(conic1.getSinglePoint()); }
			 */
			return;
		}

		// input is already degenerate
		if (conic1.isLineConic()) {
			intersectWithDegenerate(conic2, conic1, points, Kernel.STANDARD_PRECISION);
			ok = testPoints(conic1, conic2, points, Kernel.MIN_PRECISION);
		} else if (conic2.isLineConic()) {
			intersectWithDegenerate(conic1, conic2, points, Kernel.STANDARD_PRECISION);
			ok = testPoints(conic1, conic2, points, Kernel.MIN_PRECISION);
		}

		// STANDARD PROCEDURE
		double epsilon = Kernel.MAX_PRECISION;
		while (!ok && epsilon <= Kernel.MIN_PRECISION) {

			// find intersection points conics through intersection points
			ok = calcIntersectionPoints(conic1, conic2, points, epsilon);

			// try it with lower precision
			epsilon *= 10.0;
		}

		// did not find intersections
		if (!ok) {
			for (i = 0; i < points.length; i++) {
				points[i].setUndefined();
			}
		}

		// for non-continous kernel: move defined intersection points to front
		else if (!kernel.isContinuous()) {
			moveDefinedPointsToFront(points);
		}
	}

	/**
	 * Arranges intersection points Q so that all defined intersection points are at
	 * the beginning of the array.
	 */
	private static void moveDefinedPointsToFront(GeoPoint[] points) {
		for (int i = 0; i < points.length; i++) {
			if (points[i].isDefined()) {
				// move defined intersection point as far to the front as
				// possible
				int j = i - 1;
				boolean move = false;
				while (j >= 0 && !points[j].isDefined()) {
					move = true;
					j--;
				}
				if (move) {
					j++;
					points[j].setCoords(points[i]);
					points[i].setUndefined();
				}
			}
		}
	}

	/**
	 * Intersect conic with degenerate conic degConic. Write result into points.
	 */
	final static private void intersectWithDegenerate(GeoConic conic, GeoConic degConic,
			GeoPoint[] points, double eps) {
		if (degConic.isDefined()) {
			switch (degConic.getType()) {
			case GeoConicNDConstants.CONIC_INTERSECTING_LINES:
			case GeoConicNDConstants.CONIC_PARALLEL_LINES:

				// check if both conics share a line
				// eg Intersect[-7x y - 10x - 7y = 10, 2x y - 9x + 2y = 9]
				if (conic.isLineConic()) {
					if (conic.lines[0].isEqual(degConic.lines[0])
							|| conic.lines[0].isEqual(degConic.lines[1])
							|| conic.lines[1].isEqual(degConic.lines[0])
							|| conic.lines[1].isEqual(degConic.lines[1])) {

						// infinite solutions, don't return any
						for (int i = 0; i < 4; i++) {
							points[i].setUndefined();
						}
						return;
					}

				}
				AlgoIntersectLineConic.intersectLineConic(degConic.lines[0], conic, points, eps);
				points[2].setCoords(points[0]);
				points[3].setCoords(points[1]);
				AlgoIntersectLineConic.intersectLineConic(degConic.lines[1], conic, points, eps);
				return;

			case GeoConicNDConstants.CONIC_EMPTY:
				// this shouldn't happen: try it with doubleline conic
				degConic.enforceDoubleLine();

				// fall through
			case GeoConicNDConstants.CONIC_DOUBLE_LINE:
				AlgoIntersectLineConic.intersectLineConic(degConic.lines[0], conic, points, eps);
				points[2].setUndefined();
				points[3].setUndefined();
				return;

			case GeoConicNDConstants.CONIC_SINGLE_POINT:
				points[0].setCoords(degConic.getSinglePoint());
				points[1].setUndefined();
				points[2].setUndefined();
				points[3].setUndefined();
				return;
			}
		}

		// something went wrong: no intersections
		for (int i = 0; i < 4; i++) {
			points[i].setUndefined();
		}
	}

	/**
	 * Tests if at least one point lies on conics A and B.
	 */
	final private static boolean testPoints(GeoConic A, GeoConic B, GeoPoint[] P, double eps) {
		boolean foundPoint = false;
		for (int i = 0; i < P.length; i++) {
			if (P[i].isDefined()) {

				// if we have eg -7.000000048833772 check if -7 works as well or better
				double x = P[i].inhomX;
				double y = P[i].inhomY;
				double x2 = DoubleUtil.checkDecimalFraction(x, 100000000);
				double y2 = DoubleUtil.checkDecimalFraction(y, 100000000);
				if (x != x2 || y != y2) {
					GeoPoint pt = new GeoPoint(P[i].getConstruction(), x2, y2, 1);

					if (DoubleUtil.isGreaterEqual(B.distance(P[i]), B.distance(pt))
							&& DoubleUtil.isGreaterEqual(A.distance(P[i]), A.distance(pt))) {
						// rounded point is at least as close to both conics
						// -> let's take that one instead
						// eg Intersect(-x² + 6x + 20y = -291, -x y - x + 2y = -83)
						P[i].setCoords(x2, y2, 1);
					}
				}

				if (!(A.isOnFullConic(P[i], eps) && B.isOnFullConic(P[i], eps))) {
					P[i].setUndefined();
				} else {
					foundPoint = true;
				}
			}
		}
		return foundPoint;
	}

	static final private double absCrossProduct(double a1, double a2, double b1, double b2) {
		return Math.abs(a1 * b2 - a2 * b1);
	}

	/**
	 * Calculates the intersection points of the conic sections 1 and 2.
	 */
	final private boolean calcIntersectionPoints(GeoConic conic1, GeoConic conic2,
			GeoPoint[] points, double eps) {
		/*
		 * Pluecker mu method: Solves the cubic equation det(A + x B) = 0 or det(x A +
		 * B) = 0 to get degenerate conics C = A + x B or C = x A + B that pass through
		 * all intersection points of A and B.
		 */

		double[] flatDeg = new double[6]; // flat matrix of degenerate conic

		// test wheter conics A and B have proportional submatrix S
		// => degnerate is single line
		// (e.g. for circles)

		double[] Amatrix = conic1.getFlatMatrix();
		double[] Bmatrix = conic2.getFlatMatrix();

		if (absCrossProduct(Amatrix[0], Amatrix[1], Bmatrix[0], Bmatrix[1]) < eps
				&& absCrossProduct(Amatrix[0], Amatrix[3], Bmatrix[0], Bmatrix[3]) < eps
				&& absCrossProduct(Amatrix[1], Amatrix[3], Bmatrix[1], Bmatrix[3]) < eps) {

			return intersectConicsWithEqualSubmatrixS(conic1, conic2, points, eps);
		}

		if (isZero(Amatrix[0]) && isZero(Amatrix[1]) && isZero(Bmatrix[3])) {
			// eg Intersect(7x y + 3x - 9y = -820, -7x^2 - 7y^2 - 4x + 14y = -1220)
			return intersectSpecial(conic1, conic2, points, eps);
		}

		if (isZero(Bmatrix[0]) && isZero(Bmatrix[1]) && isZero(Amatrix[3])) {
			// eg Intersect(-7x^2 - 7y^2 - 4x + 14y = -1220, 7x y + 3x - 9y = -820)
			return intersectSpecial(conic2, conic1, points, eps);
		}

		// STANDARD CASE
		// We search for det(A + x B) = 0 to get a degenerate conic section C
		// with C = A + x B that includes all intersection points of A and B.
		// This leads to a cubic equation for x.
		double[] eqn = new double[4];
		double[] flatA = new double[6]; // flat matrix of conic A
		double[] flatB = new double[6]; // flat matrix of conic B

		// copy and normalize flat matrices
		for (int i = 0; i < 6; i++) {
			flatA[i] = Amatrix[i];
			flatB[i] = Bmatrix[i];
		}
		normalizeArray(flatA);
		normalizeArray(flatB);

		// compute coefficients of cubic equation
		// sol[0] + sol[1] x + sol[2] x^2 + sol[3] x^3 = 0
		// constant
		eqn[0] = flatA[2] * (flatA[0] * flatA[1] - flatA[3] * flatA[3])
				+ flatA[4] * (2.0 * flatA[3] * flatA[5] - flatA[1] * flatA[4])
				- flatA[0] * flatA[5] * flatA[5];
		// x
		eqn[1] = flatB[0] * (flatA[1] * flatA[2] - flatA[5] * flatA[5])
				+ flatB[1] * (flatA[0] * flatA[2] - flatA[4] * flatA[4])
				+ flatB[2] * (flatA[0] * flatA[1] - flatA[3] * flatA[3])
				+ 2.0 * (flatB[3] * (flatA[4] * flatA[5] - flatA[2] * flatA[3])
				+ flatB[4] * (flatA[3] * flatA[5] - flatA[1] * flatA[4])
				+ flatB[5] * (flatA[3] * flatA[4] - flatA[0] * flatA[5]));
		// x^2
		eqn[2] = flatA[0] * (flatB[1] * flatB[2] - flatB[5] * flatB[5])
				+ flatA[1] * (flatB[0] * flatB[2] - flatB[4] * flatB[4])
				+ flatA[2] * (flatB[0] * flatB[1] - flatB[3] * flatB[3])
				+ 2.0 * (flatA[3] * (flatB[4] * flatB[5] - flatB[2] * flatB[3])
				+ flatA[4] * (flatB[3] * flatB[5] - flatB[1] * flatB[4])
				+ flatA[5] * (flatB[3] * flatB[4] - flatB[0] * flatB[5]));
		// x^3
		eqn[3] = flatB[2] * (flatB[0] * flatB[1] - flatB[3] * flatB[3])
				+ flatB[4] * (2.0 * flatB[3] * flatB[5] - flatB[1] * flatB[4])
				- flatB[0] * flatB[5] * flatB[5];

		// Log.debug(eqn[3] + " x^3 + " + eqn[2] + " x^2 + " + eqn[1] + " x + " + eqn[0]
		// );

		// solve cubic equation and sort solutions
		double[] sol = new double[3];
		int solnr = EquationSolver.solveCubicS(eqn, sol, eps);
		if (solnr > -1) {
			Arrays.sort(sol, 0, solnr);
		}

		// Go through cubic equation's solutions and take first degenerate conic
		// with det(A + x B) < eps.
		for (int i = 0; i < solnr; i++) {
			// A + x B
			for (int j = 0; j < 6; j++) {
				flatDeg[j] = (flatA[j] + sol[i] * flatB[j]);
			}

			// check if det(A + x B) = 0
			degConic.setDegenerateMatrixFromArray(flatDeg);

			// try first conic
			intersectWithDegenerate(conic1, degConic, points, eps);
			if (testPoints(conic1, conic2, points, Kernel.MIN_PRECISION)) {
				return true;
			}

			// try second conic
			intersectWithDegenerate(conic2, degConic, points, eps);
			if (testPoints(conic1, conic2, points, Kernel.MIN_PRECISION)) {
				return true;
			}
		}

		// DESPARATE MODE
		// we did not find a degenerate conic with the solutions from above
		// so we try det(x A + B) now

		// change equation from {0, 1, 2, 3} to {3, 2, 1, 0}
		// i.e. intersect(A,B) = intersect(B,A)
		// Application.debug("CHANGE EQUATION");

		double temp = eqn[0];
		eqn[0] = eqn[3];
		eqn[3] = temp;
		temp = eqn[1];
		eqn[1] = eqn[2];
		eqn[2] = temp;

		// solve cubic equation and sort solutions
		solnr = EquationSolver.solveCubicS(eqn, sol, eps);
		if (solnr > -1) {
			Arrays.sort(sol, 0, solnr);
		}

		// Go through cubic equation's solutions and take first degenerate conic
		// that gives us intersection points
		for (int i = 0; i < solnr; i++) {
			// x A + B
			for (int j = 0; j < 6; j++) {
				flatDeg[j] = (sol[i] * flatA[j] + flatB[j]);
			}
			degConic.setDegenerateMatrixFromArray(flatDeg);

			// try first conic
			intersectWithDegenerate(conic1, degConic, points, eps);
			if (testPoints(conic1, conic2, points, Kernel.MIN_PRECISION)) {
				return true;
			}

			// try second conic
			intersectWithDegenerate(conic2, degConic, points, eps);
			if (testPoints(conic1, conic2, points, Kernel.MIN_PRECISION)) {
				return true;
			}
		}

		// If intersection points not found
		// try with another algorithm - solving system of algebraic equations of
		// conics
		/* Author ddrakulic */

		double[] param1 = new double[6];
		param1[0] = Amatrix[0]; // x^2
		param1[1] = 2 * Amatrix[3]; // xy
		param1[2] = Amatrix[1]; // y^2
		param1[3] = 2 * Amatrix[4]; // x
		param1[4] = 2 * Amatrix[5]; // y
		param1[5] = Amatrix[2]; // constant

		double[] param2 = new double[6];
		param2[0] = Bmatrix[0]; // x^2
		param2[1] = 2 * Bmatrix[3]; // xy
		param2[2] = Bmatrix[1]; // y^2
		param2[3] = 2 * Bmatrix[4]; // x
		param2[4] = 2 * Bmatrix[5]; // y
		param2[5] = Bmatrix[2]; // constant

		double[][] res = new double[4][2];
		// Solving system of equations
		solnr = sysSolver.solveSystemOfQuadraticEquations(param1, param2, res, eps);

		if (solnr == -1 || solnr > res.length) {
			return false;
		}

		for (int i = 0; i < solnr; i++) {
			points[i].setCoords(res[i][0], res[i][1], 1.0d);
		}

		for (int i = solnr; i < 4; i++) {
			points[i].setUndefined();
		}

		return testPoints(conic1, conic2, points, Kernel.MIN_PRECISION);
	}

	// special case when a=b=n=0
	private boolean intersectSpecial(GeoConic c1, GeoConic c2, GeoPoint[] points, double eps) {

		double[] Amatrix = c1.getFlatMatrix();
		double[] Bmatrix = c2.getFlatMatrix();

		// a x^2 + b y^2 + c + 2d x y + 2e x + 2f y = 0
		// k x^2 + l y^2 + m + 2n x y + 2o x + 2p y = 0
		// double a = Amatrix[0];
		// double b = Amatrix[1];
		double c = Amatrix[2];
		double d = Amatrix[3];
		double e = Amatrix[4];
		double f = Amatrix[5];

		double k = Bmatrix[0];
		double l = Bmatrix[1];
		double m = Bmatrix[2];
		double o = Bmatrix[4];
		double p = Bmatrix[5];

		double[] eqn = new double[5];

		if (tempLine == null) {
			tempLine = new GeoLine(cons);
		}

		ArrayList<MyPoint> set = new ArrayList<>();

		// intersect vertical lines
		fillQuarticRoots(eqn, c, d, e, f, k, l, m, o, p);
		int roots = kernel.getEquationSolver().polynomialRoots(eqn, false);
		intersectLines(true, eqn, roots, points, tempLine, c1, c2, set, eps);

		// now try with x/y swapped and check horizontal lines
		// eg Intersect(-9x² + 20x + 2y = -1106, 5x y + 6x - 2y = 96)
		// swap x/y so
		// swap e/f k/l o/p
		fillQuarticRoots(eqn, c, d, f, e, l, k, m, p, o);
		roots = kernel.getEquationSolver().polynomialRoots(eqn, false);
		intersectLines(false, eqn, roots, points, tempLine, c1, c2, set, eps);

		// found some points
		if (set.size() > 0) {

			for (int i = 0; i < points.length; i++) {

				if (i < set.size()) {
					MyPoint pt = set.get(i);
					points[i].setCoords(pt.x, pt.y, 1);
				} else {
					points[i].setUndefined();
				}

			}

			return true;

		}

		// no intersections found
		return false;
	}

	private void intersectLines(boolean vertical, double[] eqn, int roots, GeoPoint[] points,
			GeoLine tempLine, GeoConic c1, GeoConic c2, ArrayList<MyPoint> set, double eps) {
		for (int i = 0; i < roots; i++) {

			tempLine.setCoords(vertical ? 1 : 0, vertical ? 0 : 1, -eqn[i]);

			// try first conic
			AlgoIntersectLineConic.intersectLineConic(tempLine, c1, points, eps);
			if (testPoints(c1, c2, points, 10 * eps)) {
				savePoints(set, points);
			}

			// vertical line may be tangential to first conic, hard to intersect so
			// try second conic, may find duplicate points (OK)
			AlgoIntersectLineConic.intersectLineConic(tempLine, c2, points, eps);
			if (testPoints(c1, c2, points, 10 * eps)) {
				savePoints(set, points);
			}

		}
	}

	// in CAS
	// c+2d x y + 2e x + 2f y=0
	// k x^2 +l y^2 +m +2o x +2p y=0
	// Solve($1,y)
	// Substitute($2,$3)
	// $4 (2d x + 2f)^2
	// Simplify($5)

	// now solve this to find x-xoords of roots
	// 4d*d k x^4
	//
	// + (8d*d o + 8d f k) * x^3
	//
	// + (4d*d m + 4e*e l + 4f*f k - 8d e p + 16d f o)* x^2
	//
	// + (8f*f o - 4c d p + 4c e l + 8d f m - 8e f p)* x
	//
	// + c*c l + 4f*f m - 4c f p = 0
	private void fillQuarticRoots(double[] eqn, double c, double d, double e,
			double f, double k, double l, double m,	double o, double p) {
		eqn[4] = 4 * d * d * k;
		eqn[3] = 8 * d * d * o + 8 * d * f * k;
		eqn[2] = 4 * d * d * m + 4 * e * e * l + 4 * f * f * k - 8 * d * e * p + 16 * d * f * o;
		eqn[1] = 8 * f * f * o - 4 * c * d * p + 4 * c * e * l + 8 * d * f * m - 8 * e * f * p;
		eqn[0] = c * c * l + 4 * f * f * m - 4 * c * f * p;

	}

	private void savePoints(ArrayList<MyPoint> set, GeoPoint[] points) {
		for (int i = 0; i < points.length; i++) {
			if (points[i] != null && points[i].isDefined()
					&& points[i].isFinite() && !contains(set, points[i])) {
				MyPoint pt = new MyPoint(points[i].x, points[i].y);
				set.add(pt);
			}
		}

	}

	private boolean contains(ArrayList<MyPoint> set, GeoPoint geoPoint) {

		for (int i = 0; i < set.size(); i++) {
			MyPoint pt = set.get(i);
			if (DoubleUtil.isEqual(pt.x, geoPoint.inhomX)
					&& DoubleUtil.isEqual(pt.y, geoPoint.inhomY)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * If A and B have same submatrix S, the intersection points are on a (double)
	 * line.
	 *
	 * @param points resulting intersection points
	 * @return true if points were found
	 */
	private boolean intersectConicsWithEqualSubmatrixS(GeoConic c1, GeoConic c2,
			GeoPoint[] points, double eps) {
		if (tempLine == null) {
			tempLine = new GeoLine(cons);
		}

		// set line passing through intersection points (e.g. of two circles)
		// x1 x + y1 x + k1 = 0
		double x1;
		double y1;
		double k1;

		double[] c1matrix = c1.getFlatMatrix();
		double[] c2matrix = c2.getFlatMatrix();
		// c1matrix = {a,b,c,d,e,f}
		// c2matrix = {k,l,m,n,o,p}
		// line passing through intersection points is different for some degenerate
		// cases calculated below:

		if (isZero(c1matrix[0]) && isZero(c2matrix[0]) && isZero(c1matrix[1])
				&& isZero(c2matrix[1])) {

			// special case a=b=k=l=0
			// line passing through both intersection points is
			// y * (2 d p - 2 f n) + x * (2 d o - 2 e n) + d m - c n = 0
			x1 = 2 * (c1matrix[3] * c2matrix[4] - c2matrix[3] * c1matrix[4]);

			y1 = 2 * (c1matrix[3] * c2matrix[5] - c2matrix[3] * c1matrix[5]);

			k1 = c1matrix[3] * c2matrix[2] - c2matrix[3] * c1matrix[2];

		} else if (isZero(c1matrix[0]) && isZero(c2matrix[0]) && isZero(c1matrix[3])
				&& isZero(c2matrix[3])) {

			// special case a = d = k = n =0
			// ie no x^2 or xy terms in either

			// eg Intersect( -y² + 18x - 16y = -8 , -4y² + 5x - 8y = 2 )

			// line is
			// y(((2 * b) * p) * y) - (((2 * f) * l) * y) = ((((-2) * b) * o) * x) + (((2 *
			// e) * l) * x) - (b * m) + (c * l)

			// 2 (b o - e l)
			x1 = 2 * (c1matrix[1] * c2matrix[4] - c1matrix[4] * c2matrix[1]);

			// (b p - f l)
			y1 = 2 * (c1matrix[1] * c2matrix[5] - c1matrix[5] * c2matrix[1]);

			// b m - c l
			k1 = c1matrix[1] * c2matrix[2] - c1matrix[2] * c2matrix[1];

		} else {

			// slightly more general case
			// eg Intersect( -5x² - 9x + 9y = 0 , 2x² - 12x + 14y = 7 )

			// Intersect(a x² + b y² + c + 2d x y + 2e x + 2f y = 0, k x² + (k b) / a y² + m
			// + 2 k d / a x y + 2o x + 2p y = 0)
			// then line through intersection point is (from CAS):
			// y = (x * (((-a) * o) + (e * k)) / ((a * p) - (f * k))) + (((-a) * m) + (c *
			// k)) / (((2 * a) * p) - ((2 * f) * k))

			// 2 e k - 2 a o
			x1 = 2 * (c1matrix[4] * c2matrix[0] - c1matrix[0] * c2matrix[4]);

			// 2 f k - 2 a p
			y1 = 2 * (c1matrix[5] * c2matrix[0] - c1matrix[0] * c2matrix[5]);

			// a m - c k
			k1 = c1matrix[2] * c2matrix[0] - c1matrix[0] * c2matrix[2];

		}

		// line is now
		// y1 y + x1 x + k1 = 0

		tempLine.setCoords(x1, y1, k1);

		// try first conic
		AlgoIntersectLineConic.intersectLineConic(tempLine, c1, points, eps);
		if (testPoints(c1, c2, points, Kernel.MIN_PRECISION)) {
			return true;
		}

		// try second conic
		AlgoIntersectLineConic.intersectLineConic(tempLine, c2, points, eps);
		return testPoints(c1, c2, points, Kernel.MIN_PRECISION);
	}

	private boolean isZero(double a) {
		return MyDouble.exactEqual(a, 0);
	}

	/**
	 * Divides the given array by its maximum absolute value.
	 */
	private static void normalizeArray(double[] array) {
		// find max abs value in array
		double max = 0;
		for (int i = 0; i < array.length; i++) {
			double abs = Math.abs(array[i]);
			if (abs > max) {
				max = abs;
			}
		}

		// divide array by max
		for (int i = 0; i < array.length; i++) {
			array[i] /= max;
		}
	}

	/***************************************************************
	 * NEAREST DISTANCE RELATION
	 ***************************************************************/

	/**
	 * set tabel[i][j] to square distance between D[i] and Q[j]. distSqr(D[i], Q[j])
	 * := (D[i] - Q[j])^2 + age[i]. age[i] tells for every D[i], how long it has
	 * been undefined (old points' distances should be larger). Undefined (NaN) or
	 * infinite distances are set to max of all defined distances + 1. If there are
	 * no defined distances, all distances are set to 0.
	 *
	 * @param D     old points
	 * @param age   how long corresponding D has been undefined
	 * @param Q     new points, not permutated
	 * @param table output distance table
	 */
	final public static void distanceTable(GeoPoint[] D, int[] age, GeoPoint[] Q,
			double[][] table) {
		int i, j;
		boolean foundUndefined = false;
		double dist, max = -1.0;

		// calc all distances and maximum distance (max)
		for (i = 0; i < D.length; i++) {
			// checkFixedPoint = meanDistance[i] == 0.0;
			for (j = 0; j < Q.length; j++) {
				dist = D[i].distanceSqr(Q[j]) + age[i];

				if (Double.isInfinite(dist) || Double.isNaN(dist)) {
					dist = -1; // mark as undefined
					foundUndefined = true;
				} else if (dist > max) {
					max = dist;
				}
				table[i][j] = dist;
			}
		}

		if (foundUndefined) {
			max = max + 1;
			// set undefined distances to max (marked as -1)
			for (j = 0; j < Q.length; j++) {
				for (i = 0; i < D.length; i++) {
					// check if entry is marked as undefined (Q[j])
					if (table[i][j] == -1) {
						// set all distances to Q[j] to max+1
						table[i][j] = max;
					}
				}
			}
		}
	}

	/**
	 * Sets Pi = Qj according to near to heuristic (using the closest pairs of
	 * points in ascending distance order).
	 *
	 * For limitedPaths we also have to make sure that we only use points from Q to
	 * set P that really lie on both paths.
	 *
	 * @param P           output array
	 * @param isPalive    whether the respective P point was defined already
	 * @param Q           new permutation
	 * @param isQonPath   whether respective element of Q is on both (limited) paths
	 * @param distTable   distance matrix for old points D and new Q
	 * @param pointList   temporary point relation list
	 *
	 * @param permutation is an output parameter for the permutation of points Q
	 *                    used to set points P, e.g. permuation {1,0} means that
	 *                    P[0]=Q[1] and P[1]=Q[0]
	 * @param needStrict  false to ignore eps
	 * @param eps         precision: if csome intersection points are closer than
	 *                    this, don't permute
	 */
	final static void setNearTo(GeoPoint[] P, boolean[] isPalive, GeoPoint[] Q, boolean[] isQonPath,
			double[][] distTable, PointPairList pointList, int[] permutation,
			boolean needStrict, double eps) {
		int indexP, indexQ;

		pointList.clear();
		for (indexP = 0; indexP < P.length; indexP++) {
			for (indexQ = 0; indexQ < Q.length; indexQ++) {
				// sorted inserting
				pointList.insertPointPair(indexP, isPalive[indexP], indexQ, isQonPath[indexQ],
						distTable[indexP][indexQ]);
			}
		}

		double gap = Double.POSITIVE_INFINITY;
		double temp;
		for (int i = 0; i < Q.length; i++) {
			for (int j = i + 1; j < Q.length; j++) {
				temp = Q[i].distanceSqr(Q[j]);
				if (temp < gap) {
					gap = temp;
				}
			}
		}

		if (!needStrict || (gap > eps * eps)) {

			PointPair pair;
			int currentSize = -1;

			while (!pointList.isEmpty() && pointList.size() != currentSize) {
				currentSize = pointList.size();
				// take first pair from pointList
				pair = pointList.getHead();
				indexP = pair.indexP;
				indexQ = pair.indexQ;

				if (pair.isPalive && pair.isQonPath
						&& pointList.getClosestPWithindexQ(pair.indexQ) == pair.indexP
						&& pointList.getClosestQWithindexP(pair.indexP) == pair.indexQ) {
					// workingList.insertPointPair(pair.indexP, isPalive,
					// indexQ, isQonPath, distance)

					// remove all other pairs with P[indexP] or Q[indexQ] from
					// list
					pointList.removeAllPairs(pair);

				}

				P[indexP].setCoords(Q[indexQ]);
				permutation[indexP] = indexQ;
			}

			while (!pointList.isEmpty()) {

				// take first pair from pointList
				pair = pointList.getHead();
				indexP = pair.indexP;
				indexQ = pair.indexQ;

				// remove all other pairs with P[indexP] or Q[indexQ] from list
				pointList.removeAllPairs(pair);

				P[indexP].setCoords(Q[indexQ]);
				permutation[indexP] = indexQ;
			}

		} else {

			// keep permutations
			for (int i = 0; i < P.length; i++) {
				P[i].setCoords(Q[permutation[i]]);
			}
		}

	}

	/**
	 * Sets Pi = Qj according to near to heuristic (using the closest pairs of
	 * points in ascending distance order).
	 *
	 * For limitedPaths we also have to make sure that we only use points from Q to
	 * set P that really lie on both paths.
	 *
	 * @param P           output array for best fitting permutation
	 * @param isPalive    whether the respective P point was defined already
	 * @param Q           new permutation
	 * @param isQonPath   whether respective element of Q is on both (limited) paths
	 * @param distTable   distance matrix for old points D and new Q
	 * @param pointList   temporary point relation list
	 *
	 * @param permutation is an output parameter for the permutation of points Q
	 *                    used to set points P, e.g. permuation {1,0} means that
	 *                    P[0]=Q[1] and P[1]=Q[0]
	 */
	final static void setNearTo(GeoPoint[] P, boolean[] isPalive, GeoPoint[] Q, boolean[] isQonPath,
			double[][] distTable, PointPairList pointList, int[] permutation) {
		int indexP, indexQ;
		pointList.clear();
		for (indexP = 0; indexP < P.length; indexP++) {
			for (indexQ = 0; indexQ < Q.length; indexQ++) {
				// sorted inserting
				pointList.insertPointPair(indexP, isPalive[indexP], indexQ, isQonPath[indexQ],
						distTable[indexP][indexQ]);
			}
		}

		if (pointList.isStrict()) {

			PointPair pair;
			while (!pointList.isEmpty()) {
				// take first pair from pointList
				pair = pointList.getHead();
				indexP = pair.indexP;
				indexQ = pair.indexQ;

				// remove all other pairs with P[indexP] or Q[indexQ] from list
				pointList.removeAllPairs(pair);

				P[indexP].setCoords(Q[indexQ]);
				permutation[indexP] = indexQ;
			}

		} else {

			// keep permutations
			for (int i = 0; i < P.length; i++) {
				P[i] = Q[permutation[i]];
			}
		}

	}

	/*
	 * This code is very similar to AlgoIntersectLineConics. TODO: Maybe commonize.
	 */
	@Override
	public PVariable[] getBotanaVars(GeoElementND geo) {
		return getProverAdapter().getBotanaVars(geo);
	}

	@Override
	public PPolynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {
		return getProverAdapter().getBotanaPolynomials(geo, A, B, this);
	}

	private IntersectConicsAdapter getProverAdapter() {
		if (proverAdapter == null) {
			proverAdapter = new IntersectConicsAdapter();
		}
		return proverAdapter;
	}

	/**
	 * The number of intersections not generated by this algo.
	 *
	 * @return previously existing number of intersections
	 */
	public int existingIntersections() {
		if (preexistPoints != null) {
			return preexistPoints.size();
		}
		return 0;
	}

	/**
	 * @param i index
	 * @return esisting intersection
	 */
	public GeoPointND getPreexistPoint(int i) {
		if (preexistPoints != null) {
			return preexistPoints.get(i);
		}
		return null;
	}
}