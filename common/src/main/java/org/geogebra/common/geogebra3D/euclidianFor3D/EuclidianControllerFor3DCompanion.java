package org.geogebra.common.geogebra3D.euclidianFor3D;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianControllerCompanion;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoJoinPoints3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoMidpoint3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoPolarLine3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.plugin.GeoClass;

public class EuclidianControllerFor3DCompanion extends
		EuclidianControllerCompanion {

	public EuclidianControllerFor3DCompanion(EuclidianController ec) {
		super(ec);
	}

	@Override
	protected GeoAngle createAngle(GeoPointND A, GeoPointND B, GeoPointND C) {

		GeoDirectionND orientation = ec.view.getDirection();

		if (((GeoElement) A).isGeoElement3D()
				|| ((GeoElement) B).isGeoElement3D()
				|| ((GeoElement) C).isGeoElement3D()) { // at least one 3D geo
			if (orientation == ec.kernel.getSpace()) { // space is default
														// orientation for 3D
														// objects
				return ec.kernel.getManager3D().Angle3D(null, A, B, C);
			}
			return ec.kernel.getManager3D().Angle3D(null, A, B, C, orientation); // use
																					// view
																					// orientation

		}

		// 2D geos
		if (orientation == ec.kernel.getXOYPlane()) { // xOy plane is default
														// orientation for 2D
														// objects
			return super.createAngle(A, B, C);
		}
		return ec.kernel.getManager3D().Angle3D(null, A, B, C, orientation); // use
																				// view
																				// orientation

	}

	@Override
	protected GeoElement[] createAngles(GeoPolygon p) {

		GeoDirectionND orientation = ec.view.getDirection();

		if (p.isGeoElement3D()) { // 3D polygon
			if (orientation == ec.kernel.getSpace()) { // space is default
														// orientation for 3D
														// objects
				return ec.kernel.getManager3D().Angles3D(null, p);
			}
			return ec.kernel.getManager3D().Angles3D(null, p, orientation); // use
																			// view
																			// orientation
		}

		// 2D polygon
		if (orientation == ec.kernel.getXOYPlane()) { // xOy plane is default
														// orientation for 2D
														// objects
			return super.createAngles(p);
		}
		return ec.kernel.getManager3D().Angles3D(null, p, orientation); // use
																		// view
																		// orientation

	}

	@Override
	protected GeoAngle createAngle(GeoVectorND v1, GeoVectorND v2) {

		GeoDirectionND orientation = ec.view.getDirection();

		if (v1.isGeoElement3D() || v2.isGeoElement3D()) { // at least one 3D geo
			if (orientation == ec.kernel.getSpace()) { // space is default
														// orientation for 3D
														// objects
				return ec.kernel.getManager3D().Angle3D(null, v1, v2);
			}
			return ec.kernel.getManager3D().Angle3D(null, v1, v2, orientation); // use
																				// view
																				// orientation
		}

		// 2D polygon
		if (orientation == ec.kernel.getXOYPlane()) { // xOy plane is default
														// orientation for 2D
														// objects
			return super.createAngle(v1, v2);
		}
		return ec.kernel.getManager3D().Angle3D(null, v1, v2, orientation); // use
																			// view
																			// orientation

	}

	@Override
	public GeoAngle createAngle(GeoPointND p1, GeoPointND p2, GeoNumberValue a,
			boolean clockWise) {

		GeoDirectionND direction = ec.view.getDirection();

		if (direction == ec.kernel.getXOYPlane()
				|| direction == ec.kernel.getSpace()) { // use xOy plane
			if (p1.isGeoElement3D() || p2.isGeoElement3D()) {
				return (GeoAngle) ec.kernel.getManager3D().Angle(null, p1, p2,
						a, ec.kernel.getXOYPlane(), !clockWise)[0];
			}

			return super.createAngle(p1, p2, a, clockWise);
		}

		return (GeoAngle) ec.kernel.getManager3D().Angle(null, p1, p2, a,
				direction, !clockWise)[0];
	}

	@Override
	protected GeoAngle createLineAngle(GeoLineND g, GeoLineND h) {

		GeoDirectionND orientation = ec.view.getDirection();

		if (g.isGeoElement3D() || h.isGeoElement3D()) { // at least one 3D geo
			if (orientation == ec.kernel.getSpace()) { // space is default
														// orientation for 3D
														// objects
				return ec.kernel.getManager3D().createLineAngle(g, h);
			}
			return ec.kernel.getManager3D().createLineAngle(g, h, orientation); // use
																				// view
																				// orientation
		}

		// 2D geos
		if (orientation == ec.kernel.getXOYPlane()) { // xOy plane is default
														// orientation for 2D
														// objects
			return super.createLineAngle(g, h);
		}
		return ec.kernel.getManager3D().createLineAngle(g, h, orientation); // use
																			// view
																			// orientation

	}

	@Override
	protected GeoElement[] translate(GeoElement geo, GeoVectorND vec) {
		if (geo.isGeoElement3D() || ((GeoElement) vec).isGeoElement3D()) {
			return ec.kernel.getManager3D().Translate3D(null, geo, vec);
		}

		return super.translate(geo, vec);

	}

	@Override
	protected GeoElement[] mirrorAtPoint(GeoElement geo, GeoPointND point) {
		if (geo.isGeoElement3D() || ((GeoElement) point).isGeoElement3D()) {
			return ec.kernel.getManager3D().Mirror3D(null, geo, point);
		}

		return super.mirrorAtPoint(geo, point);

	}

	@Override
	protected GeoElement[] mirrorAtLine(GeoElement geo, GeoLineND line) {
		if (geo.isGeoElement3D() || ((GeoElement) line).isGeoElement3D()) {
			return ec.kernel.getManager3D().Mirror3D(null, geo, line);
		}

		return super.mirrorAtLine(geo, line);

	}

	@Override
	public GeoElement[] dilateFromPoint(GeoElement geo, NumberValue num,
			GeoPointND point) {

		if (geo.isGeoElement3D() || ((GeoElement) point).isGeoElement3D()) {
			return ec.kernel.getManager3D().Dilate3D(null, geo, num, point);
		}

		return super.dilateFromPoint(geo, num, point);
	}

	/**
	 * Method used when geos are both 2D
	 * 
	 * @param a
	 *            first geo
	 * @param b
	 *            second geo
	 * @return single intersection point
	 */
	public GeoPointND getSingleIntersectionPointFrom2D(GeoElement a,
			GeoElement b, boolean coords2D) {
		return super.getSingleIntersectionPoint(a, b, coords2D);
	}

	@Override
	public GeoPointND getSingleIntersectionPoint(GeoElement a, GeoElement b,
			boolean coords2D) {

		// check if a and b are two 2D geos
		if (!a.isGeoElement3D() && !b.isGeoElement3D())
			return getSingleIntersectionPointFrom2D(a, b, coords2D);

		GeoPointND point = null;

		// first hit is a line
		if (a.isGeoLine()) {
			if (b.isGeoLine()) {
				/*
				 * if (!((GeoLine) a).linDep((GeoLine) b)) { return ec.kernel
				 * .IntersectLines(null, (GeoLine) a, (GeoLine) b); }
				 */
				point = (GeoPoint3D) ec.kernel.getManager3D().Intersect(null,
						a, b);
			} else if (b.isGeoConic()) {
				point = ec.kernel.getManager3D().IntersectLineConicSingle(null,
						(GeoLineND) a, (GeoConicND) b, ec.xRW, ec.yRW,
						ec.view.getInverseMatrix());
				/*
				 * } else if (b.isGeoFunctionable()) { // line and function
				 * GeoFunction f = ((GeoFunctionable) b).getGeoFunction(); if
				 * (f.isPolynomialFunction(false)) { return
				 * ec.kernel.IntersectPolynomialLineSingle(null, f, (GeoLine) a,
				 * xRW, yRW); } GeoPoint2 initPoint = new GeoPoint2(
				 * ec.kernel.getConstruction()); initPoint.setCoords(xRW, yRW,
				 * 1.0); return ec.kernel.IntersectFunctionLine(null, f,
				 * (GeoLine) a, initPoint);
				 */
			} else {
				return null;
			}
		}
		// first hit is a conic
		else if (a.isGeoConic()) {
			if (b.isGeoLine()) {
				point = ec.kernel.getManager3D().IntersectLineConicSingle(null,
						(GeoLineND) b, (GeoConicND) a, ec.xRW, ec.yRW,
						ec.view.getInverseMatrix());
			} else if (b.isGeoConic() && !a.isEqual(b)) {
				point = ec.kernel.getManager3D().IntersectConicsSingle(null,
						(GeoConicND) a, (GeoConicND) b, ec.xRW, ec.yRW,
						ec.view.getInverseMatrix());
			} else {
				return null;
			}
		}

		if (point != null) {
			if (coords2D) {
				point.setCartesian();
			} else {
				point.setCartesian3D();
			}
			point.update();
		}

		return point;

	}

	@Override
	protected GeoElement[] orthogonal(GeoPointND point, GeoLineND line) {
		return new GeoElement[] { (GeoElement) ec.kernel.getManager3D()
				.OrthogonalLine3D(null, point, line, ec.view.getDirection()) };

	}

	@Override
	public GeoPointND createNewPoint(String label, boolean forPreviewable,
			Path path, double x, double y, double z, boolean complex,
			boolean coords2D) {

		// check if the path is 3D geo or contains a 3D geo
		GeoElement geo = path.toGeoElement();
		if (geo.isGeoElement3D()
				|| (geo.isGeoList() && ((GeoList) geo).containsGeoElement3D())) {
			ec.checkZooming(forPreviewable);

			GeoPointND point = ec.kernel.getManager3D().Point3D(label, path, x,
					y, z, !forPreviewable, coords2D);

			return point;
		}

		// else use 2D
		return ec.createNewPoint2D(label, forPreviewable, path, x, y, complex,
				coords2D);
	}

	@Override
	protected GeoElement midpoint(GeoSegmentND segment) {

		if (((GeoElement) segment).isGeoElement3D()) {
			return (GeoElement) ec.kernel.getManager3D()
					.Midpoint(null, segment);
		}

		return super.midpoint(segment);

	}

	@Override
	protected GeoElement midpoint(GeoConicND conic) {

		if (((GeoElement) conic).isGeoElement3D()) {
			return (GeoElement) ec.kernel.getManager3D().Center(null, conic);
		}

		return super.midpoint(conic);

	}

	@Override
	protected GeoElement midpoint(GeoPointND p1, GeoPointND p2) {

		if (((GeoElement) p1).isGeoElement3D()
				|| ((GeoElement) p2).isGeoElement3D()) {

			AlgoMidpoint3D algo = new AlgoMidpoint3D(
					ec.kernel.getConstruction(), p1, p2);
			return algo.getPoint();
		}

		return super.midpoint(p1, p2);
	}

	@Override
	public GeoElement[] regularPolygon(GeoPointND geoPoint1,
			GeoPointND geoPoint2, GeoNumberValue value) {

		if (geoPoint1.isGeoElement3D() || geoPoint2.isGeoElement3D()) {
			return ec.kernel.getManager3D().RegularPolygon(null, geoPoint1,
					geoPoint2, value, ec.view.getDirection());
		}

		return ec.kernel.getAlgoDispatcher().RegularPolygon(null, geoPoint1,
				geoPoint2, value);
	}

	@Override
	protected AlgoElement segmentAlgo(Construction cons, GeoPointND p1,
			GeoPointND p2) {
		if (p1.isGeoElement3D() || p2.isGeoElement3D()) {
			return new AlgoJoinPoints3D(cons, p1, p2, null, GeoClass.SEGMENT3D);
		}

		return super.segmentAlgo(cons, p1, p2);
	}

	@Override
	protected GeoElement[] createCircle2(GeoPointND p0, GeoPointND p1) {

		if (p0.isGeoElement3D() || p1.isGeoElement3D()) {
			return createCircle2For3D(p0, p1);
		}
		return super.createCircle2(p0, p1);
	}

	/**
	 * 
	 * @param p0
	 *            center
	 * @param p1
	 *            point on circle
	 * @return circle in the current plane
	 */
	protected GeoElement[] createCircle2For3D(GeoPointND p0, GeoPointND p1) {
		return new GeoElement[] { ec.kernel.getManager3D().Circle3D(null, p0,
				p1, ec.view.getDirection()) };
	}

	@Override
	protected GeoConicND circle(Construction cons, GeoPointND center,
			NumberValue radius) {
		if (center.isGeoElement3D()) {
			return circleFor3D(cons, center, radius);
		}

		return super.circle(cons, center, radius);
	}

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param center
	 *            center
	 * @param radius
	 *            radius
	 * @return circle in the current plane
	 */
	protected GeoConicND circleFor3D(Construction cons, GeoPointND center,
			NumberValue radius) {
		return ec.kernel.getManager3D().Circle3D(null, center, radius,
				ec.view.getDirection());
	}

	@Override
	protected GeoElement[] angularBisector(GeoLineND g, GeoLineND h) {

		if (g.isGeoElement3D() || h.isGeoElement3D()) {
			return ec.kernel.getManager3D().AngularBisector3D(null, g, h);
		}

		return super.angularBisector(g, h);
	}

	@Override
	protected GeoElement angularBisector(GeoPointND A, GeoPointND B,
			GeoPointND C) {
		if (A.isGeoElement3D() || B.isGeoElement3D() || C.isGeoElement3D()) {
			return ec.kernel.getManager3D().AngularBisector3D(null, A, B, C);
		}
		return super.angularBisector(A, B, C);
	}

	@Override
	protected GeoElement circleArcSector(GeoPointND A, GeoPointND B,
			GeoPointND C, int type) {

		GeoDirectionND orientation = ec.view.getDirection();

		return (GeoElement) ec.kernel.getManager3D().CircleArcSector3D(null, A,
				B, C, orientation, type); // use view orientation

	}

	@Override
	protected GeoElement semicircle(GeoPointND A, GeoPointND B) {

		GeoDirectionND orientation = ec.view.getDirection();

		return (GeoElement) ec.kernel.getManager3D().Semicircle3D(null, A, B,
				orientation); // use view orientation

	}

	@Override
	protected GeoElement circumcircleArc(GeoPointND p1, GeoPointND p2,
			GeoPointND p3) {
		if (p1.isGeoElement3D() || p2.isGeoElement3D() || p3.isGeoElement3D()) {
			return (GeoElement) ec.kernel.getManager3D().CircumcircleArc3D(
					null, p1, p2, p3);
		}

		return super.circumcircleArc(p1, p2, p3);
	}

	@Override
	protected GeoElement circumcircleSector(GeoPointND p1, GeoPointND p2,
			GeoPointND p3) {
		if (p1.isGeoElement3D() || p2.isGeoElement3D() || p3.isGeoElement3D()) {
			return (GeoElement) ec.kernel.getManager3D().CircumcircleSector3D(
					null, p1, p2, p3);
		}

		return super.circumcircleSector(p1, p2, p3);
	}

	@Override
	protected GeoElement[] tangent(GeoPointND a, GeoConicND c) {
		return ec.kernel.getManager3D().Tangent3D(null, a, c);
	}

	@Override
	protected GeoElement[] tangent(GeoLineND l, GeoConicND c) {
		return ec.kernel.getManager3D().Tangent3D(null, l, c);
	}

	@Override
	protected GeoElement[] tangent(GeoConicND c1, GeoConicND c2) {
		return ec.kernel.getManager3D().CommonTangents3D(null, c1, c2);
	}

	@Override
	protected GeoElement polarLine(GeoPointND P, GeoConicND c) {

		if (P.isGeoElement3D() || c.isGeoElement3D()) {
			AlgoPolarLine3D algo = new AlgoPolarLine3D(
					ec.kernel.getConstruction(), null, c, P);
			return (GeoElement) algo.getLine();
		}

		return super.polarLine(P, c);
	}

	@Override
	protected GeoElement diameterLine(GeoLineND l, GeoConicND c) {
		return ec.kernel.getManager3D().DiameterLine3D(null, l, c);
	}

	@Override
	protected GeoElement diameterLine(GeoVectorND v, GeoConicND c) {
		return ec.kernel.getManager3D().DiameterLine3D(null, v, c);
	}

	@Override
	protected GeoElement lineBisector(GeoSegmentND segment) {

		return ec.kernel.getManager3D().LineBisector3D(null, segment,
				ec.view.getDirection());

	}

	@Override
	protected GeoElement lineBisector(GeoPointND a, GeoPointND b) {
		return ec.kernel.getManager3D().LineBisector3D(null, a, b,
				ec.view.getDirection());
	}

	@Override
	protected GeoConicND conic5(GeoPointND[] points) {
		for (int i = 0; i < 5; i++) {
			if (points[i].isGeoElement3D()) {
				return ec.kernel.getManager3D().Conic3D(null, points);
			}
		}
		return super.conic5(points);
	}

	@Override
	protected GeoConicND ellipseHyperbola(GeoPointND a, GeoPointND b,
			GeoPointND c, int type) {
		return ec.kernel.getManager3D().EllipseHyperbola3D(null, a, b, c,
				ec.view.getDirection(), type);
	}

	@Override
	protected GeoConicND parabola(GeoPointND a, GeoLineND l) {
		if (a.isGeoElement3D() || l.isGeoElement3D()) {
			return ec.kernel.getManager3D().Parabola3D(null, a, l);
		}
		return super.parabola(a, l);
	}

	@Override
	protected GeoElement vectorPoint(GeoPointND a, GeoVectorND v) {

		if (a.isGeoElement3D() || v.isGeoElement3D()) {
			GeoPointND endPoint = (GeoPointND) ec.kernel.getManager3D()
					.Translate3D(null, (GeoElement) a, v)[0];
			return ec.kernel.getManager3D().Vector3D(null, a, endPoint);
		}

		return super.vectorPoint(a, v);

	}

	@Override
	protected GeoElement locus(GeoPointND a, GeoPointND b) {

		if (a.isGeoElement3D() || b.isGeoElement3D()) {
			return ec.kernel.getManager3D().Locus3D(null, a, b);
		}

		return super.locus(a, b);
	}

}
