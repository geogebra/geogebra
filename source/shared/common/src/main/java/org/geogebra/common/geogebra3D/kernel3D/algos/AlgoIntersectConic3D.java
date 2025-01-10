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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoIntersectLineConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicPartND;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 *
 * @author mathieu
 * 
 */
public abstract class AlgoIntersectConic3D extends AlgoIntersect3D {

	protected GeoElement firstGeo; // input
	protected GeoConicND c; // input
	protected GeoPoint3D[] P; // output
	private GeoPoint3D[] D;

	/** 2d description of g when included in conic coord sys */
	private GeoLine g2d;
	/** 2d points created by using AlgoIntersectLineConic.intersectLineConic */
	private GeoPoint[] points2d;

	private Coords p2d;
	private Coords tmpCoords;

	/**
	 * @param cons
	 *            construction
	 */
	AlgoIntersectConic3D(Construction cons) {

		super(cons);

		p2d = new Coords(3);

		// helper algo
		g2d = new GeoLine(cons);
		points2d = new GeoPoint[2];
		for (int i = 0; i < 2; i++) {
			points2d[i] = new GeoPoint(cons);
		}

	}

	AlgoIntersectConic3D(Construction cons, GeoElement firstGeo, GeoConicND c) {

		this(cons);

		this.firstGeo = firstGeo;
		this.c = c;

		P = new GeoPoint3D[2];
		D = new GeoPoint3D[2];

		for (int i = 0; i < 2; i++) {
			P[i] = new GeoPoint3D(cons);
			D[i] = new GeoPoint3D(cons);
		}

		setInputOutput(); // for AlgoElement

		compute();
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = firstGeo;
		input[1] = c;

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
	 * @return conic input
	 */
	GeoConicND getConic() {
		return c;
	}

	@Override
	public final String toString(StringTemplate tpl) {
		return getLoc().getPlain("IntersectionPointOfAB", c.getLabel(tpl),
				firstGeo.getLabel(tpl));
	}

	/**
	 * 
	 * @return start point for first geo
	 */
	protected abstract Coords getFirstGeoStartInhomCoords();

	/**
	 * 
	 * @return direction for first geo
	 */
	protected abstract Coords getFirstGeoDirectionInD3();

	/**
	 * 
	 * @param p
	 *            point coords
	 * @return true if coords are in the first geo as limited path
	 */
	protected abstract boolean getFirstGeoRespectLimitedPath(Coords p);

	@Override
	public void compute() {
		intersect(c, P);
	}

	/**
	 * calc intersection points with the conic
	 * 
	 * @param conic
	 *            conic
	 * @param pts
	 *            points
	 */
	protected final void intersect(GeoConicND conic, GeoPoint3D[] pts) {

		CoordSys cs = conic.getCoordSys();
		Coords o = getFirstGeoStartInhomCoords();
		Coords d = getFirstGeoDirectionInD3();

		// project line on conic coord sys
		Coords dp = cs.getNormalProjection(d)[1];
		if (!DoubleUtil.isZero(dp.getZ())) { // line intersect conic coord sys
			Coords globalCoords = new Coords(4);
			Coords inPlaneCoords = new Coords(4);
			o.projectPlaneThruV(cs.getMatrixOrthonormal(), d, globalCoords,
					inPlaneCoords);
			p2d.setX(inPlaneCoords.getX());
			p2d.setY(inPlaneCoords.getY());
			p2d.setZ(inPlaneCoords.getW());
			// check if intersect point is on conic
			if (conic.isOnFullConic(p2d, Kernel.MIN_PRECISION)
					&& getFirstGeoRespectLimitedPath(globalCoords)) {
				pts[0].setCoords(globalCoords, false);
			} else {
				setPointsUndefined();
			}
		} else { // line parallel to conic coord sys
			Coords op = cs.getNormalProjection(o)[1];
			if (!DoubleUtil.isZero(op.getZ())) { // line not included
				setPointsUndefined(); // TODO infinite points ?
			} else { // line included
				g2d.setCoords(dp.getY(), -dp.getX(),
						-dp.getY() * op.getX() + dp.getX() * op.getY());
				intersectLineIncluded(conic, pts, cs, g2d);
			}
		}
	}

	/**
	 * intersect with line included
	 * 
	 * @param conic
	 *            conic
	 * @param points
	 *            points
	 * @param cs
	 *            conic coord sys
	 * @param g
	 *            line
	 */
	protected void intersectLineIncluded(GeoConicND conic, GeoPoint3D[] points,
			CoordSys cs, GeoLine g) {
		AlgoIntersectLineConic.intersectLineConic(g, conic, points2d,
				Kernel.STANDARD_PRECISION);
		points[0].setCoords(cs.getPoint(points2d[0].x, points2d[0].y), false);
		checkIsOnFirstGeo(points[0]);
		checkIsOnConicPart(conic, points2d[0], points[0]);
		points[1].setCoords(cs.getPoint(points2d[1].x, points2d[1].y), false);
		checkIsOnFirstGeo(points[1]);
		checkIsOnConicPart(conic, points2d[1], points[1]);
	}

	private void checkIsOnConicPart(GeoConicND conic, GeoPoint point,
			GeoPoint3D p3d) {
		if (conic.isGeoConicPart()) {
			if (!p3d.isDefined()) {
				return;
			}

			if (tmpCoords == null) {
				tmpCoords = new Coords(3);
				tmpCoords.setZ(1);
			}

			tmpCoords.setX(point.x);
			tmpCoords.setY(point.y);
			if (!((GeoConicPartND) conic).getParameters().isOnPath(tmpCoords)) {
				p3d.setUndefined();
			}
		}
	}

	/**
	 * if p is really on first geo
	 * 
	 * @param p
	 *            point
	 */
	protected abstract void checkIsOnFirstGeo(GeoPoint3D p);

	private void setPointsUndefined() {
		for (int i = 0; i < 2; i++) {
			P[i].setUndefined();
		}

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

	/**
	 * 
	 * @return first geo
	 */
	protected GeoElement getFirstGeo() {
		return firstGeo;
	}

	/*
	 * 
	 * @param i index (0 or 1)
	 * 
	 * @return i-th 2D point last computed
	 * 
	 * public GeoPoint getPoint2D(int i){ return points2d[i]; }
	 */
}
