/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoTangentLine.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoIntersectConics;
import org.geogebra.common.kernel.algos.AlgoIntersectLineConic;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.kernel.matrix.CoordMatrix;
import org.geogebra.common.kernel.matrix.CoordMatrixUtil;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 *
 * @author Markus
 */
public class AlgoIntersectConics3D extends AlgoIntersect3D {

	private GeoConicND A;
	private GeoQuadricND B; // input
	private GeoPoint3D[] P; // output
	private GeoPoint3D[] D; // output

	/** 2d line description of intersection of the two coord sys when exists */
	private GeoLine l2d;
	/** 2d conic description of A and B when B included in A coord sys */
	private GeoConic A2d;
	private GeoConic B2d;
	/** 2d points created by using AlgoIntersectLineConic.intersectLineConic */
	private GeoPoint[] points2d;
	/** 2d intersect conics helper algo */
	private AlgoIntersectConics algo2d;
	private AlgoIntersectPlaneQuadric algoPlane;

	/** matrix so that (x y 0 z) = AUGMENT_DIM * (x y z) */
	final static private CoordMatrix AUGMENT_DIM = new CoordMatrix(4, 3,
			new double[] { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1 });

	/** matrix so that (x y z) = REDUCE_DIM * (x y 0 z) */
	final static private CoordMatrix REDUCE_DIM = AUGMENT_DIM.transposeCopy();

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param A
	 *            first conic
	 * @param B
	 *            second conic
	 */
	AlgoIntersectConics3D(Construction cons, String label, GeoConicND A,
			GeoConicND B) {
		this(cons, A, B);
		LabelManager.setLabels(label, P);
	}

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param labels
	 *            output labels
	 * @param A
	 *            first conic
	 * @param B
	 *            second conic
	 */
	AlgoIntersectConics3D(Construction cons, String[] labels, GeoConicND A,
			GeoConicND B) {
		this(cons, A, B);
		LabelManager.setLabels(labels, P);
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
	 */
	AlgoIntersectConics3D(Construction cons) {
		super(cons);

		// helper algo
		l2d = new GeoLine(cons);
		A2d = new GeoConic(cons);
		B2d = new GeoConic(cons);
		algo2d = new AlgoIntersectConics(cons, false);
		points2d = new GeoPoint[4];
	}

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param A
	 *            first conic
	 * @param B
	 *            second conic
	 */
	AlgoIntersectConics3D(Construction cons, GeoConicND A, GeoQuadricND B) {

		this(cons);

		P = new GeoPoint3D[4];
		D = new GeoPoint3D[4];

		for (int i = 0; i < 4; i++) {
			P[i] = new GeoPoint3D(cons);
			D[i] = new GeoPoint3D(cons);
			points2d[i] = new GeoPoint(cons);
		}

		this.A = A;
		this.B = B;

		setInputOutput(); // for AlgoElement

		compute();
	}

	// for AlgoElement
	@Override
	public void setInputOutput() {
		input = new GeoElement[2];
		input[0] = A;
		input[1] = B;

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

	@Override
	protected void setCoords(GeoPointND destination, GeoPointND source) {
		destination.setCoords(source.getCoordsInD3(), false);
	}

	@Override
	public final String toString(StringTemplate tpl) {
		return getLoc().getPlain("IntersectionPointOfAB", A.getLabel(tpl),
				B.getLabel(tpl));
	}

	@Override
	public void compute() {
		if (B.isGeoConic()) {
			intersectConics3D(A, (GeoConicND) B, P);
		} else {
			if (algoPlane == null) {
				algoPlane = new AlgoIntersectPlaneQuadric(cons, A, B, false);
			}
			algoPlane.compute();
			intersectSamePlane(A, algoPlane.getConic(), P);
			// TODO limited quadric
		}
	}

	/**
	 * calc intersection points between A, B
	 * 
	 * @param cA
	 *            first conic
	 * @param cB
	 *            second conic
	 * @param pts
	 *            intersection points
	 */
	public final void intersectConics3D(GeoConicND cA, GeoConicND cB,
			GeoPoint3D[] pts) {
		if (!cA.isDefined() || !cB.isDefined()) {
			setPointsUndefined(pts);
			return;
		}
		CoordSys csA = cA.getCoordSys();
		CoordSys csB = cB.getCoordSys();
		// check if coord sys are incident
		Coords cross = csA.getNormal().crossProduct(csB.getNormal());
		if (!cross.equalsForKernel(0, Kernel.MIN_PRECISION)) { // not same plane
			Coords[] intersection = CoordMatrixUtil.intersectPlanes(
					cA.getCoordSys().getMatrixOrthonormal(),
					cB.getCoordSys().getMatrixOrthonormal());
			Coords op = csA.getNormalProjection(intersection[0])[1];
			Coords dp = csA.getNormalProjection(intersection[1])[1];
			l2d.setCoords(dp.getY(), -dp.getX(),
					-dp.getY() * op.getX() + dp.getX() * op.getY());
			AlgoIntersectLineConic.intersectLineConic(l2d, cA, points2d,
					Kernel.STANDARD_PRECISION);

			pts[0].setCoords(csA.getPoint(points2d[0].x, points2d[0].y), false);
			checkIsOnConic(cB, pts[0]);
			pts[1].setCoords(csA.getPoint(points2d[1].x, points2d[1].y), false);
			checkIsOnConic(cB, pts[1]);

			if (!pts[0].isDefined() && pts[1].isDefined()) {
				pts[0].setCoords(pts[1].getCoords(), false);
				pts[1].setUndefined();
			}

			pts[2].setUndefined();
			pts[3].setUndefined();

		} else { // parallel plane

			Coords op = csA.getNormalProjection(csB.getOrigin())[1];
			if (!DoubleUtil.isZero(op.getZ())) { // coord sys strictly parallel
				setPointsUndefined(pts); // TODO infinite points ?
			} else { // coord sys included

				intersectSamePlane(cA, cB, pts);
			}
		}

	}

	private void intersectSamePlane(GeoConicND cA, GeoConicND cB,
			GeoPoint3D[] pts) {
		transformConics(cA, cB, A2d, B2d);
		// Log.debug(sBinA.get(1,1)+","+B2d.matrix[0]+"");
		algo2d.intersectConics(A2d, B2d, points2d);
		setPointsUndefined(pts);
		for (int i = 0; i < 4; i++) {
			pts[i].setCoords(
					cA.getCoordSys().getPoint(points2d[i].x, points2d[i].y),
					false);
		}
	}

	/**
	 * @param A
	 *            first conic
	 * @param B
	 *            second conic
	 * @param A2d
	 *            2d representation of first conic
	 * @param B2d
	 *            2d representation of second conic
	 */
	public static void transformConics(GeoConicND A, GeoConicND B, GeoConic A2d,
			GeoConic B2d) {
		CoordSys csA = A.getCoordSys();
		CoordSys csB = B.getCoordSys();

		CoordMatrix BtoA = REDUCE_DIM.mul(csB.getMatrixOrthonormal().inverse()
				.mul(csA.getMatrixOrthonormal())).mul(AUGMENT_DIM);
		// Log.debug("\nBtoA=\n"+BtoA);

		CoordMatrix sB = B.getSymmetricMatrix();
		CoordMatrix sBinA = BtoA.transposeCopy().mul(sB).mul(BtoA);

		A2d.setMatrix(A.getMatrix());
		B2d.setMatrix(sBinA);
	}

	private static void checkIsOnConic(GeoConicND B, GeoPoint3D p) {
		if (!p.isDefined()) {
			return;
		}

		Coords pp = B.getCoordSys().getNormalProjection(p.getCoords())[1];
		Coords pp2d = new Coords(3);
		pp2d.setX(pp.getX());
		pp2d.setY(pp.getY());
		pp2d.setZ(pp.getW());
		if (!B.isOnFullConic(pp2d, Kernel.MIN_PRECISION)) {
			p.setUndefined();
		}

	}

	private static void setPointsUndefined(GeoPoint3D[] P) {
		for (int i = 0; i < 4; i++) {
			P[i].setUndefined();
		}

	}

	@Override
	public final void initForNearToRelationship() {
		// TODO
	}
}
