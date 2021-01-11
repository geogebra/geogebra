/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.PathMover;
import org.geogebra.common.kernel.PathMoverGeneric;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.Transform;
import org.geogebra.common.kernel.algos.AlgoConicPartCircumcircle;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoJoinPointsRay;
import org.geogebra.common.kernel.algos.AlgoRayPointVector;
import org.geogebra.common.kernel.algos.AlgoTranslate;
import org.geogebra.common.kernel.algos.AlgoUnitVectorLine;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoRayND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.DoubleUtil;

/**
 * @author Markus Hohenwarter
 */
final public class GeoRay extends GeoLine implements LimitedPath, GeoRayND {

	private boolean allowOutlyingIntersections = false;
	private boolean keepTypeOnGeometricTransform = true;
	private Coords pnt2D;

	/**
	 * Creates ray with start point A.
	 * 
	 * @param c
	 *            construction
	 * @param A
	 *            start point
	 */
	public GeoRay(Construction c, GeoPoint A) {
		this(c);
		setStartPoint(A);
	}

	/**
	 * Creates new ray
	 * 
	 * @param c
	 *            construction
	 */
	public GeoRay(Construction c) {
		super(c);
		setConstructionDefaults();
		setModeIfEquationFormIsNotForced(toStringMode);
	}

	/**
	 * Copy constructor
	 * 
	 * @param ray
	 *            template ray
	 */
	public GeoRay(GeoRay ray) {
		this(ray.cons);
		set(ray);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.RAY;
	}

	@Override
	public GeoLine copy() {
		return new GeoRay(this);
	}

	@Override
	public GeoElement copyInternal(Construction cons1) {
		GeoRay ray = new GeoRay(cons1,
				(GeoPoint) startPoint.copyInternal(cons1));
		ray.set(this);
		return ray;
	}

	@Override
	public void set(GeoElementND geo) {
		super.set(geo);
		if (!geo.isGeoRay()) {
			return;
		}

		GeoRay ray = (GeoRay) geo;
		keepTypeOnGeometricTransform = ray.keepTypeOnGeometricTransform;

		startPoint = (GeoPoint) GeoLine.updatePoint(cons, startPoint,
				ray.startPoint);

		// Need to adjust the second defining object too, see #3770
		if (getParentAlgorithm() instanceof AlgoJoinPointsRay
				&& geo.getParentAlgorithm() instanceof AlgoJoinPointsRay) {
			((AlgoJoinPointsRay) getParentAlgorithm()).getQ()
					.set(((AlgoJoinPointsRay) geo.getParentAlgorithm()).getQ());
		} else if (getParentAlgorithm() instanceof AlgoRayPointVector
				&& geo.getParentAlgorithm() instanceof AlgoRayPointVector) {
			((AlgoRayPointVector) getParentAlgorithm()).getv().set(
					((AlgoRayPointVector) geo.getParentAlgorithm()).getv());
		}
	}

	/**
	 * Sets this ray using direction line and start point
	 * 
	 * @param s
	 *            start point
	 * @param direction
	 *            line
	 */
	public void set(GeoPoint s, GeoVec3D direction) {
		super.set(direction);
		setStartPoint(s);
	}

	@Override
	public void setVisualStyle(GeoElement geo, boolean setAuxiliaryProperty) {
		super.setVisualStyle(geo, setAuxiliaryProperty);

		if (geo.isGeoRay()) {
			GeoRay ray = (GeoRay) geo;
			allowOutlyingIntersections = ray.allowOutlyingIntersections;
		}
	}

	/*
	 * Path interface
	 */
	@Override
	public void pointChanged(GeoPointND P) {
		super.pointChanged(P);

		// ensure that the point doesn't get outside the ray
		// i.e. ensure 0 <= t
		PathParameter pp = P.getPathParameter();
		if (pp.t < 0.0) {
			P.setCoords2D(startPoint.x, startPoint.y, startPoint.z);
			P.updateCoordsFrom2D(false, null);
			pp.t = 0.0;
		}
	}

	@Override
	public void pathChanged(GeoPointND PI) {

		// if kernel doesn't use path/region parameters, do as if point changed
		// its coords
		if (!getKernel().usePathAndRegionParameters(PI)) {
			pointChanged(PI);
			return;
		}

		GeoPoint P = (GeoPoint) PI;

		PathParameter pp = P.getPathParameter();
		if (pp.t < 0.0) {
			pp.t = 0;
		}

		// calc point for given parameter
		P.x = startPoint.inhomX + pp.t * y;
		P.y = startPoint.inhomY - pp.t * x;
		P.z = 1.0;
	}

	@Override
	public boolean allowOutlyingIntersections() {
		return allowOutlyingIntersections;
	}

	@Override
	public void setAllowOutlyingIntersections(boolean flag) {
		allowOutlyingIntersections = flag;
	}

	@Override
	public boolean keepsTypeOnGeometricTransform() {
		return keepTypeOnGeometricTransform;
	}

	@Override
	public void setKeepTypeOnGeometricTransform(boolean flag) {
		keepTypeOnGeometricTransform = flag;
	}

	@Override
	public boolean isLimitedPath() {
		return true;
	}

	@Override
	public boolean isIntersectionPointIncident(GeoPoint p, double eps) {
		if (allowOutlyingIntersections) {
			return isOnFullLine(p, eps);
		}
		return isOnPath(p, eps);
	}

	/**
	 * Returns the smallest possible parameter value for this path (may be
	 * Double.NEGATIVE_INFINITY)
	 * 
	 * @return smallest possible parameter
	 */
	@Override
	public double getMinParameter() {
		return 0;
	}

	/**
	 * Returns the largest possible parameter value for this path (may be
	 * Double.POSITIVE_INFINITY)
	 * 
	 * @return largest possible parameter
	 */
	@Override
	public double getMaxParameter() {
		return Double.POSITIVE_INFINITY;
	}

	@Override
	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}

	/**
	 * returns all class-specific xml tags for saveXML
	 */
	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);

		// allowOutlyingIntersections
		sb.append("\t<outlyingIntersections val=\"");
		sb.append(allowOutlyingIntersections);
		sb.append("\"/>\n");

		// keepTypeOnGeometricTransform
		sb.append("\t<keepTypeOnTransform val=\"");
		sb.append(keepTypeOnGeometricTransform);
		sb.append("\"/>\n");

	}

	/**
	 * Creates a new ray using a geometric transform.
	 * 
	 * @param t
	 *            transform
	 */

	@Override
	public GeoElement[] createTransformedObject(Transform t,
			String transformedLabel) {
		AlgoElement parent = keepTypeOnGeometricTransform ? getParentAlgorithm()
				: null;

		// CREATE RAY
		if (parent instanceof AlgoJoinPointsRay) {
			// transform points
			AlgoJoinPointsRay algo = (AlgoJoinPointsRay) parent;
			GeoPointND[] points = { algo.getP(), algo.getQ() };
			points = t.transformPoints(points);
			if (t.isAffine()) {
				GeoElement ray = (GeoElement) kernel.rayND(transformedLabel,
						points[0], points[1]);
				ray.setVisualStyleForTransformations(this);
				GeoElement[] geos = { ray, (GeoElement) points[0],
						(GeoElement) points[1] };
				return geos;
			}
			GeoPoint inf = new GeoPoint(cons);
			inf.setCoords(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
					1);
			inf = (GeoPoint) t.doTransform(inf);
			AlgoConicPartCircumcircle ae = new AlgoConicPartCircumcircle(cons,
					Transform.transformedGeoLabel(this), (GeoPoint) points[0],
					(GeoPoint) points[1], inf,
					GeoConicNDConstants.CONIC_PART_ARC);
			cons.removeFromAlgorithmList(ae);
			GeoElement arc = ae.getConicPart(); // GeoConicPart
			arc.setVisualStyleForTransformations(this);
			GeoElement[] geos = { arc, (GeoElement) points[0],
					(GeoElement) points[1] };
			return geos;
		} else if (parent instanceof AlgoRayPointVector) {
			// transform startpoint
			GeoPointND[] points = { getStartPoint() };
			points = t.transformPoints(points);

			boolean oldSuppressLabelCreation = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			AlgoUnitVectorLine ad = new AlgoUnitVectorLine(cons, this, false);
			cons.removeFromAlgorithmList(ad);
			GeoVectorND direction = ad.getVector();
			if (t.isAffine()) {

				direction = (GeoVector) t.doTransform(direction);
				cons.setSuppressLabelCreation(oldSuppressLabelCreation);

				// ray through transformed point with direction of transformed
				// line
				GeoElement ray = kernel.getAlgoDispatcher().ray(
						transformedLabel, (GeoPoint) points[0],
						(GeoVector) direction);
				ray.setVisualStyleForTransformations(this);
				GeoElement[] geos = new GeoElement[] { ray,
						(GeoElement) points[0] };
				return geos;
			}
			AlgoTranslate at = new AlgoTranslate(cons, getStartPoint(),
					(GeoVector) direction);
			cons.removeFromAlgorithmList(at);
			GeoPoint thirdPoint = (GeoPoint) at.getResult();
			GeoPoint inf = new GeoPoint(cons);
			inf.setCoords(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
					1);

			GeoPointND[] points2 = new GeoPointND[] { thirdPoint, inf };
			points2 = t.transformPoints(points2);
			cons.setSuppressLabelCreation(oldSuppressLabelCreation);
			AlgoConicPartCircumcircle ae = new AlgoConicPartCircumcircle(cons,
					Transform.transformedGeoLabel(this), (GeoPoint) points[0],
					(GeoPoint) points2[0], (GeoPoint) points2[1],
					GeoConicNDConstants.CONIC_PART_ARC);
			GeoElement arc = ae.getConicPart(); // GeoConicPart
			arc.setVisualStyleForTransformations(this);
			GeoElement[] geos = { arc, (GeoElement) points[0] };
			return geos;

		} else {
			// create LINE
			GeoElement transformedLine = t.getTransformedLine(this);
			transformedLine.setLabel(transformedLabel);
			GeoElement[] ret = { transformedLine };
			return ret;
		}
	}

	@Override
	public boolean isGeoRay() {
		return true;
	}

	// Michael Borcherds 2008-04-30
	@Override
	public boolean isEqual(GeoElementND geo) {
		// return false if it's a different type, otherwise check direction and
		// start point
		if (!geo.isGeoRay()) {
			return false;
		}

		return isSameDirection((GeoLine) geo)
				&& ((GeoRay) geo).getStartPoint().isEqual(getStartPoint());

	}

	@Override
	public boolean isOnPath(Coords Pnd, double eps) {
		if (pnt2D == null) {
			pnt2D = new Coords(3);
		}
		pnt2D.setCoordsIn2DView(Pnd);
		if (!isOnFullLine2D(pnt2D, eps)) {
			return false;
		}

		return respectLimitedPath(pnt2D, eps);

	}

	@Override
	public boolean respectLimitedPath(Coords Pnd, double eps) {
		if (pnt2D == null) {
			pnt2D = new Coords(3);
		}
		pnt2D.setCoordsIn2DView(Pnd);
		PathParameter pp = getTempPathParameter();
		doPointChanged(pnt2D, pp);
		double t = pp.getT();

		return t >= -eps;
	}

	@Override
	public boolean isAllEndpointsLabelsSet() {
		return startPoint.isLabelSet();
	}

	@Override
	public boolean respectLimitedPath(double parameter) {
		return DoubleUtil.isGreaterEqual(parameter, 0);
	}

	@Override
	public GeoElement copyFreeRay() {
		GeoPoint startPoint1 = (GeoPoint) getStartPoint().copyInternal(cons);

		double[] direction = new double[3];
		getDirection(direction);

		GeoVector directionVec = new GeoVector(cons);
		directionVec.setCoords(direction);

		AlgoRayPointVector algo = new AlgoRayPointVector(cons,
				startPoint1, directionVec);

		return algo.getRay();
	}

	@Override
	public void matrixTransform(double p, double q, double r, double s) {

		super.matrixTransform(p, q, r, s);

		startPoint.matrixTransform(p, q, r, s);
	}

	@Override
	public void matrixTransform(double a00, double a01, double a02, double a10, double a11,
			double a12, double a20, double a21, double a22) {
		super.matrixTransform(a00, a01, a02, a10, a11, a12, a20, a21, a22);

		startPoint.matrixTransform(a00, a01, a02, a10, a11, a12, a20, a21, a22);
	}

}
