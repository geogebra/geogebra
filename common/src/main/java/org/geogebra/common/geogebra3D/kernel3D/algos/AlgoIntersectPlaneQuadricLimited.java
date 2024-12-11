/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoJoinPointsSegment
 *
 * Created on 21. August 2003
 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConicSection;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DLimited;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DPart;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.PathNormalizer;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 *
 * @author mathieu
 * 
 * 
 */
public class AlgoIntersectPlaneQuadricLimited
		extends AlgoIntersectPlaneQuadric {

	private AlgoIntersectPlaneConic algoBottom;
	private AlgoIntersectPlaneConic algoTop;

	private GeoPoint3D[] bottomP;
	private GeoPoint3D[] topP;

	private Coords tmpCoords;

	/**
	 * Creates new AlgoIntersectLinePlane
	 * 
	 * @param cons
	 *            the construction
	 * @param plane
	 *            plane
	 * @param quadric
	 *            quadric
	 */
	AlgoIntersectPlaneQuadricLimited(Construction cons, GeoPlane3D plane,
			GeoQuadricND quadric) {

		super(cons, plane, quadric);
	}

	/**
	 * Creates new AlgoIntersectLinePlane
	 * 
	 * @param cons
	 *            the construction
	 * @param label
	 *            name of conic
	 * @param plane
	 *            plane
	 * @param quadric
	 *            quadric
	 */
	AlgoIntersectPlaneQuadricLimited(Construction cons, String label,
			GeoPlane3D plane, GeoQuadricND quadric) {

		this(cons, plane, quadric);

		conic.setLabel(label);

		/*
		 * //labels String conicLabel = null; String[] bottomLabels = null;
		 * String[] topLabels = null; if (labels!=null){ conicLabel = labels[0];
		 * if (labels.length>2){ bottomLabels = new String[2]; bottomLabels[0] =
		 * labels[1]; bottomLabels[1] = labels[2]; if (labels.length>4){
		 * topLabels = new String[2]; topLabels[0] = labels[3]; topLabels[1] =
		 * labels[4]; }
		 * 
		 * } }
		 * 
		 * conic.setLabel(conicLabel); GeoElement.setLabels(bottomLabels,
		 * bottomP); GeoElement.setLabels(topLabels, topP);
		 */

	}

	@Override
	protected GeoConic3D newConic(Construction cons1) {
		return new GeoConicSection(cons1, true);
	}

	@Override
	protected void end() {

		// algo for intersect points with bottom and top
		algoBottom = new AlgoIntersectPlaneConic(cons);
		algoTop = new AlgoIntersectPlaneConic(cons);
		cons.removeFromConstructionList(algoBottom);
		cons.removeFromConstructionList(algoTop);
		bottomP = new GeoPoint3D[2];
		for (int i = 0; i < 2; i++) {
			bottomP[i] = new GeoPoint3D(cons);
		}

		topP = new GeoPoint3D[2];
		for (int i = 0; i < 2; i++) {
			topP[i] = new GeoPoint3D(cons);
		}

		super.end();
	}

	// /////////////////////////////////////////////
	// COMPUTE

	/**
	 * 
	 * @return bottom of the quadric as a conic
	 */
	protected GeoConicND getBottom() {
		return ((GeoQuadric3DLimited) quadric).getBottom();
	}

	/**
	 * 
	 * @return top of the quadric as a conic
	 */
	protected GeoConicND getTop() {
		return ((GeoQuadric3DLimited) quadric).getTop();
	}

	/**
	 * 
	 * @return side of the quadric as a surface
	 */
	protected GeoQuadric3DPart getSide() {
		return ((GeoQuadric3DLimited) quadric).getSide();
	}

	@Override
	public void compute() {

		super.compute();

		// set part points
		double[] bottomParameters = setPartPoints(algoBottom, getBottom(),
				bottomP);
		double[] topParameters = setPartPoints(algoTop, getTop(), topP);

		/*
		 * Log.debug(bottomParameters[0]+","+ bottomParameters[1]+","+
		 * topParameters[0]+","+ topParameters[1]);
		 */

		switch (conic.getType()) {
		case GeoConicNDConstants.CONIC_CIRCLE:
		case GeoConicNDConstants.CONIC_ELLIPSE:

			// if some parameters are NaN, force to be in topParameters
			if (Double.isNaN(bottomParameters[0])) {
				bottomParameters[0] = topParameters[0];
				bottomParameters[1] = topParameters[1];
				// bottomP[0] = topP[0];
				topParameters[0] = Double.NaN;
			}

			// check if top parameters are equal : then no hole for top
			if (DoubleUtil.isEqual(topParameters[0], topParameters[1])) {
				topParameters[0] = Double.NaN;
			}

			// if topParameters are NaN, and not bottomParameters,
			// set twice the "middle" parameter for topParameters to check the
			// order
			// Log.debug(topParameters[0]+","+bottomParameters[0]);
			if (Double.isNaN(topParameters[0])) {
				if (!Double.isNaN(bottomParameters[0])) {
					// if parameters are equal, no hole
					if (DoubleUtil.isEqual(bottomParameters[0],
							bottomParameters[1])) {
						if (planeOutsideAxis()) { // just single point
							setSinglePoint(bottomP[0], topP[0]);
						} else { // no hole
							bottomParameters[0] = Double.NaN;
						}
					} else {
						// calc "midpoint" on conic
						double midParameter = (bottomParameters[0]
								+ bottomParameters[1]) / 2;
						PathParameter pp = new PathParameter(midParameter);
						Coords P = new Coords(3);
						conic.pathChangedWithoutCheck(P, pp, false);
						P = conic.getPoint(P.getX(), P.getY(), new Coords(4));
						// check if "midpoint" is on quadric side
						// Log.debug("\n"+P+"\n"+ql.getSide().isInRegion(P));
						if (getSide().isInRegion(P)) {
							// set "midpoint"
							topParameters[0] = midParameter;
						} else {
							// set symmetric "midpoint"
							topParameters[0] = midParameter + Math.PI;
							if (midParameter < 0) {
								topParameters[0] = midParameter + Math.PI;
							} else {
								topParameters[0] = midParameter - Math.PI;
							}

						}
						topParameters[1] = topParameters[0];
					}
				} else { // no intersection : check if the plane is not totally
							// outside the quadric
					if (planeOutsideAxis()) {
						conic.setUndefined();
						return;
					}
				}
			}
			break;

		case GeoConicNDConstants.CONIC_HYPERBOLA:
		case GeoConicNDConstants.CONIC_PARABOLA:

			if (Double.isNaN(bottomParameters[0])) {
				if (Double.isNaN(topParameters[0])) { // no intersection with
														// ends of the quadric :
														// hyperbola is
														// completely outside
					conic.setUndefined();
				} else if (DoubleUtil.isEqual(topParameters[0], topParameters[1])) { // single
																					// point
					setSinglePoint(topP[0], topP[1]);
				}
			} else if (DoubleUtil.isEqual(bottomParameters[0],
					bottomParameters[1])) { // single
											// point
				setSinglePoint(bottomP[0], bottomP[1]);
			}

			break;
		}

		// set parameters to conic
		GeoConicSection cp = (GeoConicSection) conic;

		/*
		 * App.error(bottomParameters[0]+","+ bottomParameters[1]+","+
		 * topParameters[0]+","+ topParameters[1]);
		 */

		// Log.debug("\n"+bottomP[0]+"\n"+bottomP[1]+"\n"+topP[0]+"\n"+topP[1]);

		/*
		 * Log.debug(PathNormalizer.infFunction(bottomParameters[0])+","+
		 * PathNormalizer.infFunction(topParameters[0])+","+
		 * PathNormalizer.infFunction(bottomParameters[1]-2)+","+
		 * PathNormalizer.infFunction(topParameters[1]-2));
		 */

		cp.setParameters(bottomParameters[0], bottomParameters[1],
				topParameters[0], topParameters[1]);

	}

	protected double getBottomParameter() {
		return ((GeoQuadric3DLimited) quadric).getBottomParameter();
	}

	protected double getTopParameter() {
		return ((GeoQuadric3DLimited) quadric).getTopParameter();
	}

	private boolean planeOutsideAxis() {

		if (tmpCoords == null) {
			tmpCoords = new Coords(4);
		}

		// calc parameter (on quadric axis) of the intersection point between
		// plane and quadrix axis
		quadric.getMidpoint3D().projectPlaneThruVInPlaneCoords(
				plane.getCoordSys().getMatrixOrthonormal(),
				quadric.getEigenvec3D(2), tmpCoords);
		double parameter = -tmpCoords.getZ();

		// check if parameter is between quadric min and max
		double min = getBottomParameter();
		double max = getTopParameter();
		if (min > max) {
			double m = min;
			min = max;
			max = m;
		}

		// check if min > parameter
		if (DoubleUtil.isGreater(min, parameter)) {
			// check if parameter is close to min in comparison to max - min
			return !DoubleUtil.isEpsilonToX(min - parameter, max - min);
		}

		// check if max < parameter
		if (DoubleUtil.isGreater(parameter, max)) {
			// check if parameter is close to max in comparison to max - min
			return !DoubleUtil.isEpsilonToX(parameter - max, max - min);
		}

		// min < parameter < max
		return false;
	}

	/**
	 * set conic as single point at p1 location if p1 is define, else at p2
	 * location
	 * 
	 * @param p1
	 *            first point
	 * @param p2
	 *            second point
	 */
	private void setSinglePoint(GeoPointND p1, GeoPointND p2) {
		if (p1.isDefined()) {
			conic.setSinglePoint(p1);
		} else {
			conic.setSinglePoint(p2);
		}
	}

	private double[] setPartPoints(AlgoIntersectPlaneConic algo, GeoConicND c,
			GeoPoint3D[] points) {

		// check if c is point or undefined
		if (// c==null
			// ||
		!c.isDefined() || c.getType() == GeoConicNDConstants.CONIC_EMPTY
		// || c.getType()==GeoConicNDConstants.CONIC_SINGLE_POINT
		) {
			return new double[] { Double.NaN, Double.NaN };
		}

		// calc points
		algo.intersect(plane, c, points);

		// Log.debug(points[0].isDefined());

		if (!points[0].isDefined()) {
			return new double[] { Double.NaN, Double.NaN };
		}

		Coords c0 = points[0].getCoordsInD2(conic.getCoordSys());
		Coords c1 = points[1].getCoordsInD2(conic.getCoordSys());

		double[] ret = new double[2];

		if (c0.equalsForKernel(c1) && conic
				.getType() == GeoConicNDConstants.CONIC_INTERSECTING_LINES) {
			// force compute parameter for the two liness
			PathParameter pp = new PathParameter();
			conic.lines[0].doPointChanged(c0, pp);
			ret[0] = PathNormalizer.inverseInfFunction(pp.getT());
			conic.lines[1].doPointChanged(c1, pp);
			ret[1] = PathNormalizer.inverseInfFunction(pp.getT()) + 2;
		} else {
			// get parameters to limit the conic
			PathParameter pp = new PathParameter();
			conic.pointChanged(c0, pp, false);
			ret[0] = pp.getT();
			conic.pointChanged(c1, pp, false);
			ret[1] = pp.getT();
		}

		return ret;

	}

	/**
	 * 
	 * @param index
	 *            index (0 or 1)
	 * @return last bottom point computed
	 */
	public GeoPoint3D getBottomPoint(int index) {
		return bottomP[index];
	}

	/**
	 * 
	 * @param index
	 *            index (0 or 1)
	 * @return last top point computed
	 */
	public GeoPoint3D getTopPoint(int index) {
		return topP[index];
	}

}
