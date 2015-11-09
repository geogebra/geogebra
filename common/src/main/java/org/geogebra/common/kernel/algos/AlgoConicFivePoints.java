/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoConicFivePoints.java
 *
 * Created on 15. November 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.LocusEquation;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 *
 * @author Markus
 * @version
 */
public class AlgoConicFivePoints extends AlgoElement {

	// #4156 these are rather arbitrary tradeoffs between compatibility and
	// numeric stability
	private static final double MULTIPLIER_MIN = 0.001;
	private static final double MULTIPLIER_MAX = 1000;
	protected GeoPoint[] P; // input five points
	protected GeoConicND conic; // output
	private boolean criticalCase; // true when 5 points is on a parabola

	private double[][] A, B, C;
	private double e1, e2;
	private GeoVec3D[] line;
	private int i, j;

	public AlgoConicFivePoints(Construction cons, String label,
			GeoPointND[] inputP) {
		this(cons, inputP);
		conic.setLabel(label);
	}

	protected void setInputPoints() {
		input = P;
	}

	protected GeoPoint[] createPoints2D(GeoPointND[] inputP) {
		return (GeoPoint[]) inputP;
	}

	public AlgoConicFivePoints(Construction cons, GeoPointND[] inputP) {
		super(cons);
		this.P = createPoints2D(inputP);
		conic = newGeoConic(cons);

		setInputOutput(); // for AlgoElement

		line = new GeoVec3D[4];
		for (i = 0; i < 4; i++) {
			line[i] = new GeoLine(cons);
		}
		A = new double[3][3];
		B = new double[3][3];
		C = new double[3][3];
		checkCriticalCase();
		initCoords();
		compute();
		addIncidence();

		/*
		 * moved into addIncidence() for (int i=0; i < P.length; i++) {
		 * conic.addPointOnConic(P[i]); }
		 */

	}

	/**
	 * init Coords values
	 */
	protected void initCoords() {
		// none here
	}

	/**
	 * 
	 * @param cons
	 *            construction
	 * @return output conic
	 */
	protected GeoConicND newGeoConic(Construction cons) {
		return new GeoConic(cons);
	}

	private void checkCriticalCase() {
		criticalCase = false;

		for (int i = 0; i < P.length; i++) {
			if (P[i].getIncidenceList() == null)
				return;
		}

		ArrayList<GeoElement> firstList = P[0].getIncidenceList();

		for (int j = 0; j < firstList.size(); j++) {
			if (firstList.get(j).isGeoConic()) {
				GeoConic p = (GeoConic) firstList.get(j);
				if (p.getType() == GeoConic.CONIC_PARABOLA) {
					criticalCase = true;
					for (int i = 1; i < 5; i++) {
						if (!P[i].getIncidenceList().contains(p)) {
							criticalCase = false;
							break;
						}
					}
				}
			}

			if (criticalCase) {
				break;
			}
		}

	}

	/**
	 * @author Tam
	 * 
	 *         for special cases of e.g. AlgoIntersectLineConic
	 */
	private void addIncidence() {
		for (int i = 0; i < P.length; ++i) {
			P[i].addIncidence(conic, false);
		}
	}

	@Override
	public Commands getClassName() {
		return Commands.Conic;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_CONIC_FIVE_POINTS;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {

		setInputPoints();

		super.setOutputLength(1);
		super.setOutput(0, conic);
		setDependencies(); // done by AlgoElement
	}

	public GeoConicND getConic() {
		return conic;
	}

	GeoPoint[] getPoints() {
		return P;
	}

	/**
	 * Method created for LocusEqu project.
	 * 
	 * @return a copy of inner array so it cannot be manipulated from outside.
	 */
	public GeoPoint[] getAllPoints() {
		GeoPoint[] copy = new GeoPoint[this.getPoints().length];
		System.arraycopy(this.getPoints(), 0, copy, 0, copy.length);
		return copy;
	}

	// compute conic through five points P[0] ... P[4]
	@Override
	public void compute() {
		// compute lines P0 P1, P2 P3,
		// P0 P2, P1 P3
		GeoVec3D.cross(P[0], P[1], line[0]);
		GeoVec3D.cross(P[2], P[3], line[1]);
		GeoVec3D.cross(P[0], P[2], line[2]);
		GeoVec3D.cross(P[1], P[3], line[3]);

		// compute degenerate conics A = line[0] u line[1],
		// B = line[2] u line[3]
		degCone(line[0], line[1], A);
		degCone(line[2], line[3], B);

		e1 = evalMatrix(B, P[4]);
		e2 = -evalMatrix(A, P[4]);


		// try to avoid tiny/huge value for matrix
		if (shouldInvert(e1) && shouldInvert(e2)) {
			if (hugeForMatrix(e1, A) && hugeForMatrix(e2, A)) {
				e2 = e2 / e1;
				e1 = 1;
			} else {
				double tmp = e1;

				e1 = 1 / e2;
				e2 = 1 / tmp;
			}
		}
		linComb(A, B, e1, e2, C);

		/***
		 * critical case: five points lie on an unstable conic now only for
		 * parabola. Need more tests for: one line; two lines; one point; two
		 * points
		 */

		if (criticalCase) {
			conic.errDetS = Double.POSITIVE_INFINITY;
		} else {
			conic.errDetS = Kernel.MIN_PRECISION;
		}

		conic.setMatrix(C);
		// System.out.println(conic.getTypeString());

	}

	/**
	 * Compares a value with matrix entries TODO the 1E10 constant here is a bit
	 * arbitrary, see #5201
	 * 
	 * @param e12
	 * @param M
	 * @return
	 */
	private boolean hugeForMatrix(double e12, double M[][]) {
		for (int i = 0; i < 3; i++)
			for (int j = 0; i < 3; i++) {
				// e12 is much bigger than any matrix entry
				if (!Kernel.isZero(M[i][j], Kernel.MIN_PRECISION)
						&& Math.abs(e12) > 1E10 * M[i][j]) {
					return true;
				}
				;
			}
		return false;
	}

	private boolean shouldInvert(double d) {
		return (!Kernel.isZero(d) && Math.abs(d) < MULTIPLIER_MIN)
				|| Math.abs(d) > MULTIPLIER_MAX;
	}

	// compute degenerate conic from lines a, b
	// the result is written into A as a NON-SYMMETRIC Matrix
	final private static void degCone(GeoVec3D a, GeoVec3D b, double[][] A) {
		// A = a . b^t
		A[0][0] = a.x * b.x;
		A[0][1] = a.x * b.y;
		A[0][2] = a.x * b.z;
		A[1][0] = a.y * b.x;
		A[1][1] = a.y * b.y;
		A[1][2] = a.y * b.z;
		A[2][0] = a.z * b.x;
		A[2][1] = a.z * b.y;
		A[2][2] = a.z * b.z;
	}

	// computes P.A.P, where A is a (possibly not symmetric) 3x3 matrix
	final private static double evalMatrix(double[][] A, GeoPoint P) {
		return A[0][0] * P.x * P.x + A[1][1] * P.y * P.y + A[2][2] * P.z * P.z
				+ (A[0][1] + A[1][0]) * P.x * P.y + (A[0][2] + A[2][0]) * P.x
				* P.z + (A[1][2] + A[2][1]) * P.y * P.z;
	}

	// computes the linear combination C = l * A + m * B
	final private void linComb(double[][] A, double[][] B, double l, double m,
			double[][] C) {
		for (i = 0; i < 3; i++) {
			for (j = 0; j < 3; j++) {
				C[i][j] = l * A[i][j] + m * B[i][j];
			}
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("ConicThroughABCDE", P[0].getLabel(tpl),
				P[1].getLabel(tpl), P[2].getLabel(tpl), P[3].getLabel(tpl),
				P[4].getLabel(tpl));
	}

	@Override
	public boolean isLocusEquable() {
		return true;
	}

	@Override
	public EquationElementInterface buildEquationElementForGeo(GeoElement geo,
			EquationScopeInterface scope) {
		return LocusEquation.eqnConicFivePoints(geo, this, scope);
	}
}
