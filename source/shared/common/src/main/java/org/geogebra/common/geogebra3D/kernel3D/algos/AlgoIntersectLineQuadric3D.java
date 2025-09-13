/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.util.DoubleUtil;

/**
 *
 * @author Markus
 */
public class AlgoIntersectLineQuadric3D extends AlgoIntersect3D {
	/** INTERSECTION TYPE: producing line */
	public static final int INTERSECTION_PRODUCING_LINE = 1;
	/** INTERSECTION TYPE: asymptotic line */
	public static final int INTERSECTION_ASYMPTOTIC_LINE = 2;
	/** INTERSECTION TYPE: meeting line */
	public static final int INTERSECTION_MEETING_LINE = 3;
	/** INTERSECTION TYPE: tangent line */
	public static final int INTERSECTION_TANGENT_LINE = 4;
	/** INTERSECTION TYPE: secant line */
	public static final int INTERSECTION_SECANT_LINE = 5;
	/** INTERSECTION TYPE: passing line */
	public static final int INTERSECTION_PASSING_LINE = 6;
	private GeoLineND g; // input
	private GeoQuadricND q; // input
	private GeoPoint3D[] D; // D: old points;
	/** output points permutation of Q according to D */
	protected GeoPoint3D[] P;
	/** new output points, not yet permuted */
	protected GeoPoint3D[] Q;
	private int intersectionType;
	private boolean permuted;

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param g
	 *            line
	 * @param q
	 *            quadric
	 */
	AlgoIntersectLineQuadric3D(Construction cons, String label, GeoLineND g,
			GeoQuadric3D q) {
		this(cons, g, q);
		LabelManager.setLabels(label, P); // TODO change to P
	}

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param labels
	 *            output labels
	 * @param g
	 *            line
	 * @param q
	 *            quadric
	 */
	AlgoIntersectLineQuadric3D(Construction cons, String[] labels, GeoLineND g,
			GeoQuadric3D q) {
		this(cons, g, q);
		LabelManager.setLabels(labels, P); // TODO change to P
	}

	@Override
	public Commands getClassName() {
		return Commands.Intersect;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_INTERSECT;
	}

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param g
	 *            line
	 * @param q
	 *            qaudric
	 */
	AlgoIntersectLineQuadric3D(Construction cons, GeoLineND g, GeoQuadricND q) {
		super(cons);
		this.g = g;
		this.q = q;

		P = new GeoPoint3D[2];
		Q = new GeoPoint3D[2];
		D = new GeoPoint3D[2];

		for (int i = 0; i < 2; i++) {
			P[i] = new GeoPoint3D(cons);
			Q[i] = new GeoPoint3D(cons);
			D[i] = new GeoPoint3D(cons);
		}

		initForNearToRelationship();
		computeNoPermutation();

		if (cons.getApplication()
				.fileVersionBefore(5, 0, 281, 0)) {
			// was not permuted at that time
			permuted = false;
		} else {
			if (Q[1].isDefined() && !Q[0].isDefined()) {
				permuted = true;
			} else {
				permuted = false;
			}
		}
		setPermutation();

		setInputOutput(); // for AlgoElement
	}

	// for AlgoElement
	@Override
	public void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) g;
		input[1] = q;

		setOutput(P);
		noUndefinedPointsInAlgebraView();
		setDependencies(); // done by AlgoElement
	}

	@Override
	public final GeoPoint3D[] getIntersectionPoints() {
		return P;
	}

	@Override
	protected GeoPoint3D[] getLastDefinedIntersectionPoints() {
		return D;
	}

	/**
	 * 
	 * @return line input
	 */
	GeoLineND getLine() {
		return g;
	}

	/**
	 * 
	 * @return conic input
	 */
	GeoQuadricND getQuadric() {
		return q;
	}

	@Override
	public final String toString(StringTemplate tpl) {
		return getLoc().getPlain("IntersectionPointOfAB", q.getLabel(tpl),
				g.getLabel(tpl));
	}

	@Override
	public void compute() {
		computeNoPermutation();
		setPermutation();
	}

	final private void computeNoPermutation() {

		// g: X' = p + tv (X' is inhom coords)
		// q: XAX = 0 (the second X is transposed; X = (X',1) is hom coords)
		// we have to solve
		// u t^2 + 2b t + w = 0
		// where
		// u = v.S.v
		// b = p.S.v + a.v
		// w = evaluate(p)

		// precalc S.v for u and b
		double[] m = q.getFlatMatrix();
		double v1 = g.getDirectionInD3().getX();
		double v2 = g.getDirectionInD3().getY();
		double v3 = g.getDirectionInD3().getZ();
		double Sv1 = m[0] * v1 + m[4] * v2 + m[5] * v3;
		double Sv2 = m[4] * v1 + m[1] * v2 + m[6] * v3;
		double Sv3 = m[5] * v1 + m[6] * v2 + m[2] * v3;

		double p1 = g.getStartInhomCoords().getX();
		double p2 = g.getStartInhomCoords().getY();
		double p3 = g.getStartInhomCoords().getZ();
		double u = v1 * Sv1 + v2 * Sv2 + v3 * Sv3;
		double b = g.getStartInhomCoords().getX() * Sv1
				+ g.getStartInhomCoords().getY() * Sv2
				+ g.getStartInhomCoords().getZ() * Sv3 + m[7] * v1 + m[8] * v2
				+ m[9] * v3;

		double w = p1 * (m[0] * p1 + m[4] * p2 + m[5] * p3 + m[7])
				+ p2 * (m[4] * p1 + m[1] * p2 + m[6] * p3 + m[8])
				+ p3 * (m[5] * p1 + m[6] * p2 + m[2] * p3 + m[9]) + m[7] * p1
				+ m[8] * p2 + m[9] * p3 + m[3];

		if (DoubleUtil.isZero(u)) { // no quadratic term
			if (DoubleUtil.isZero(b)) { // no linear term: 0 t = -w
				if (DoubleUtil.isZero(w)) { // whole line is contained in q
					Q[0].setUndefined();
					Q[1].setUndefined();
					intersectionType = INTERSECTION_PRODUCING_LINE;
				} else { // w != 0, Asymptote
					Q[0].setUndefined();
					Q[1].setUndefined();
					intersectionType = INTERSECTION_ASYMPTOTIC_LINE;
				}
			} else { // b != 0, t = -w/ (2b)
				double t0 = -w / (2.0 * b);
				if (b < 0) {
					Q[0].setCoords(g.getPointInD(3, t0));
					Q[1].setUndefined();
				} else { // b > 0
					Q[0].setUndefined();
					Q[1].setCoords(g.getPointInD(3, t0));
				}
				intersectionType = INTERSECTION_MEETING_LINE;
			}
		} else { // u != 0
			double dis = b * b - u * w;
			if (DoubleUtil.isZero(dis)) { // Tangent
				double t1 = -b / u;
				Q[0].setCoords(g.getPointInD(3, t1));
				Q[1].setCoords(Q[0].getCoords());
				intersectionType = INTERSECTION_TANGENT_LINE;
			} else { // two solutions
				if (dis > 0) {
					dis = Math.sqrt(dis);
					// For accuracy, if b > 0 then we choose
					// t1 = -(b+dis) / u
					// t2 = (-b + dis) / u = w / -(b+dis)
					// if b < 0 then we choose
					// t1 = (-b - dis) / u = w / (-b+dis) = w / -(b-dis)
					// t2 = -(b-dis) / u

					boolean swap = b < 0.0;
					if (swap) {
						dis = -dis;
					}
					double n = -(b + dis);
					double t1 = swap ? w / n : n / u;
					double t2 = swap ? n / u : w / n;

					Q[0].setCoords(g.getPointInD(3, t1));
					Q[1].setCoords(g.getPointInD(3, t2));

					intersectionType = INTERSECTION_SECANT_LINE;
				} else { // dis < 0, no solution
					Q[0].setUndefined();
					Q[1].setUndefined();
					intersectionType = INTERSECTION_PASSING_LINE;
				}
			}
		}

		for (int i = 0; i < 2; i++) {
			checkIsOnLine(Q[i]);
		}

	}

	final private void setPermutation() {
		if (permuted) {
			P[0].setCoordsFromPoint(Q[1]);
			P[1].setCoordsFromPoint(Q[0]);
		} else {
			P[0].setCoordsFromPoint(Q[0]);
			P[1].setCoordsFromPoint(Q[1]);
		}
	}

	private void checkIsOnLine(GeoPoint3D p) {
		if (!p.isDefined()) {
			return;
		}
		if (!g.respectLimitedPath(p.getCoords(), Kernel.MIN_PRECISION)) {
			p.setUndefined();
		}
	}

	/** @return intersection type (tangent, asymptote, ...) */
	public int getIntersectionType() {
		return intersectionType;
	}

	/**
	 * Returns the index in output[] of the intersection point that is closest
	 * to the coordinates (xRW, yRW) TODO: move to an interface
	 */
	/*
	 * int getClosestPointIndex(double xRW, double yRW, CoordMatrix4x4 mat) {
	 * GeoPoint3D[] P = getIntersectionPoints(); double x, y, lengthSqr, mindist
	 * = Double.POSITIVE_INFINITY; int minIndex = 0; for (int i = 0; i <
	 * P.length; i++) { Coords toSceneInhomCoords =
	 * mat.mul(P[i].getCoords().getCoordsLast1()).getInhomCoords(); x =
	 * (toSceneInhomCoords.getX() - xRW); y = (toSceneInhomCoords.getY() - yRW);
	 * lengthSqr = x * x + y * y; if (lengthSqr < mindist) { mindist =
	 * lengthSqr; minIndex = i; } }
	 * 
	 * return minIndex; }
	 */

	@Override
	public final void initForNearToRelationship() {
		// TODO
	}

	@Override
	protected GeoElement getOutputForCmdXML(int i) {
		// we need to anticipate next loading in what order points will be
		// re-created to make them correctly labeled
		// * if permuted we need to set P[1] first
		// * if first point set is undefined but not second point, we need to
		// permute them since on next load they will be
		if (permuted) {
			if (P[0].isDefined()) {
				// else: P[0] and P[1] are defined, so remove permutation
				if (P[1].isDefined()) {
					return P[1 - i];
				}
				// else: P[0] is defined and not P[1], so keep permutation
				return super.getOutputForCmdXML(i);
			}
			// P[0] is undefined
			if (P[1].isDefined()) {
				// P[1] is defined and not P[0], so remove permutation
				return P[1 - i];
			}
			// else
			if (P[1].isLabelSet()) {
				// P[1] is undefined but labeled and P[0] undefined, so remove
				// permutation
				return P[1 - i];
			}
			// only P[0] is labeled, so keep permutation
			return super.getOutputForCmdXML(i);
		}

		// else: not permuted
		if (!P[0].isDefined() && P[1].isDefined()) {
			// P[1] is defined and not P[0], so permute
			return P[1 - i];
		}

		// else: P[0] is defined (or P[1] is undefined): keep as is
		return super.getOutputForCmdXML(i);
	}

}
