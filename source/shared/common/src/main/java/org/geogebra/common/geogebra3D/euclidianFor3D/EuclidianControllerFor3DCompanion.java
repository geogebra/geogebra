package org.geogebra.common.geogebra3D.euclidianFor3D;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianControllerCompanion;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoJoinPoints3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoMidpoint3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoPolarLine3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Manager3DInterface;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.plugin.GeoClass;

/**
 * 3D controller companion
 *
 */
public class EuclidianControllerFor3DCompanion
		extends EuclidianControllerCompanion {

	/**
	 * @param ec
	 *            3D controller
	 */
	public EuclidianControllerFor3DCompanion(EuclidianController ec) {
		super(ec);
	}

	@Override
	protected GeoAngle createAngle(GeoPointND A, GeoPointND B, GeoPointND C) {
		GeoDirectionND orientation = ec.getView().getDirection();

		if (A.isGeoElement3D() || B.isGeoElement3D() || C.isGeoElement3D()) {
			// at least one 3D geo
			if (orientation == ec.getKernel().getSpace()) {
				// space is default orientation for 3D objects
				return getManager3D().angle3D(null, A, B, C);
			}
			// use view orientation
			return getManager3D().angle3D(null, A, B, C, orientation);
		}

		// 2D geos
		if (orientation == ec.getKernel().getXOYPlane()) {
			// xOy plane is default orientation for 2D objects
			return super.createAngle(A, B, C);
		}

		// use view orientation
		return getManager3D().angle3D(null, A, B, C, orientation);
	}

	@Override
	protected GeoElement[] createAngles(GeoPolygon p) {
		if (p.isGeoElement3D()) {
			return getManager3D().angles3D(null, p, true);
		}

		return super.createAngles(p);
	}

	@Override
	protected GeoAngle createAngle(GeoVectorND v1, GeoVectorND v2) {
		GeoDirectionND orientation = ec.getView().getDirection();

		if (v1.isGeoElement3D() || v2.isGeoElement3D()) {
			// at least one 3D geo
			if (orientation == ec.getKernel().getSpace()) {
				// space is default orientation for 3D objects
				return getManager3D().angle3D(null, v1, v2);
			}

			// use view orientation
			return getManager3D().angle3D(null, v1, v2, orientation);
		}

		// 2D polygon
		if (orientation == ec.getKernel().getXOYPlane()) {
			// xOy plane is default orientation for 2D objects
			return super.createAngle(v1, v2);
		}

		// use view orientation
		return getManager3D().angle3D(null, v1, v2, orientation);
	}

	@Override
	public GeoAngle createAngle(GeoPointND p1, GeoPointND p2, GeoNumberValue a,
			boolean clockWise) {
		GeoDirectionND direction = ec.getView().getDirection();

		if (direction == ec.getKernel().getXOYPlane()
				|| direction == ec.getKernel().getSpace()) { // use xOy plane
			if (p1.isGeoElement3D() || p2.isGeoElement3D()) {
				return (GeoAngle) getManager3D().angle(null, p1, p2,
						a, ec.getKernel().getXOYPlane(), !clockWise)[0];
			}

			return super.createAngle(p1, p2, a, clockWise);
		}

		return (GeoAngle) getManager3D().angle(null, p1, p2, a,
				direction, !clockWise)[0];
	}

	@Override
	protected GeoAngle createLineAngle(GeoLineND g, GeoLineND h) {
		GeoDirectionND orientation = ec.getView().getDirection();

		if (g.isGeoElement3D() || h.isGeoElement3D()) {
			// at least one 3D geo
			if (orientation == ec.getKernel().getSpace()) {
				// space is default orientation for 3D objects
				return getManager3D().createLineAngle(g, h);
			}

			// use view orientation
			return getManager3D().createLineAngle(g, h, orientation);
		}

		// 2D geos
		if (orientation == ec.getKernel().getXOYPlane()) {
			// xOy plane is default orientation for 2D objects
			return super.createLineAngle(g, h);
		}

		// use view orientation
		return getManager3D().createLineAngle(g, h, orientation);
	}

	@Override
	protected GeoElement[] translate(GeoElement geo, GeoVectorND vec) {
		if (geo.isGeoElement3D() || vec.isGeoElement3D()) {
			return getManager3D().translate3D(null, geo, vec);
		}

		return super.translate(geo, vec);
	}

	@Override
	protected GeoElement[] mirrorAtPoint(GeoElement geo, GeoPointND point) {
		if (geo.isGeoElement3D() || point.isGeoElement3D()) {
			return getManager3D().mirror3D(null, geo, point);
		}

		return super.mirrorAtPoint(geo, point);
	}

	@Override
	protected GeoElement[] mirrorAtLine(GeoElement geo, GeoLineND line) {
		if (geo.isGeoElement3D() || line.isGeoElement3D()) {
			return getManager3D().mirror3D(null, geo, line);
		}

		return super.mirrorAtLine(geo, line);
	}

	@Override
	public GeoElement[] dilateFromPoint(GeoElement geo, GeoNumberValue num,
			GeoPointND point) {
		if (geo.isGeoElement3D() || point.isGeoElement3D()) {
			return getManager3D().dilate3D(null, geo, num, point);
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
	 * @param coords2D
	 *            closest coords
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
		if (!a.isGeoElement3D() && !b.isGeoElement3D()) {
			return getSingleIntersectionPointFrom2D(a, b, coords2D);
		}

		GeoPointND point = null;

		// first hit is a line
		if (a.isGeoLine()) {
			if (b.isGeoLine()) {
				point = (GeoPoint3D) getManager3D().intersect(null,
						(GeoLineND) a, (GeoLineND) b);
			} else if (b.isGeoConic()) {
				point = getManager3D().intersectLineConicSingle(null,
						(GeoLineND) a, (GeoConicND) b, ec.xRW, ec.yRW,
						ec.getView().getInverseMatrix());
			} else {
				return null;
			}
		}
		// first hit is a conic
		else if (a.isGeoConic()) {
			if (b.isGeoLine()) {
				point = getManager3D().intersectLineConicSingle(null,
						(GeoLineND) b, (GeoConicND) a, ec.xRW, ec.yRW,
						ec.getView().getInverseMatrix());
			} else if (b.isGeoConic() && !a.isEqual(b)) {
				point = getManager3D().intersectConicsSingle(null,
						(GeoConicND) a, (GeoConicND) b, ec.xRW, ec.yRW,
						ec.getView().getInverseMatrix());
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
		return new GeoElement[] { (GeoElement) getManager3D()
				.orthogonalLine3D(null, point, line,
						ec.getView().getDirection()) };
	}

	@Override
	public GeoPointND createNewPoint(String label, boolean forPreviewable,
			Path path, double x, double y, double z, boolean complex,
			boolean coords2D) {
		// check if the path is 3D geo or contains a 3D geo
		GeoElement geo = path.toGeoElement();
		if (geo.isGeoElement3D() || (geo.isGeoList()
				&& ((GeoList) geo).containsGeoElement3D())) {
			return getManager3D().point3D(label, path, x,
					y, z, !forPreviewable, coords2D);
		}

		// else use 2D
		return ec.createNewPoint2D(label, forPreviewable, path, x, y, complex,
				coords2D);
	}

	@Override
	protected GeoElement midpoint(GeoSegmentND segment) {
		if (segment.isGeoElement3D()) {
			return (GeoElement) getManager3D().midpoint(null,
					segment);
		}

		return super.midpoint(segment);
	}

	@Override
	protected GeoElement midpoint(GeoConicND conic) {
		if (conic.isGeoElement3D()) {
			return (GeoElement) getManager3D().center(null, conic);
		}

		return super.midpoint(conic);
	}

	@Override
	protected GeoElement midpoint(GeoPointND p1, GeoPointND p2) {
		if (p1.isGeoElement3D() || p2.isGeoElement3D()) {
			AlgoMidpoint3D algo = new AlgoMidpoint3D(
					ec.getKernel().getConstruction(), p1, p2);
			return algo.getPoint();
		}

		return super.midpoint(p1, p2);
	}

	@Override
	public GeoElement[] regularPolygon(GeoPointND geoPoint1,
			GeoPointND geoPoint2, GeoNumberValue value, GeoCoordSys2D direction) {
		if (geoPoint1.isGeoElement3D() || geoPoint2.isGeoElement3D()) {
			return getManager3D().regularPolygon(null, geoPoint1,
					geoPoint2, value,
					direction == null ? ec.getView().getDirection()
							: direction);
		}

		return ec.getKernel().getAlgoDispatcher().regularPolygon(null,
				geoPoint1,
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
		return new GeoElement[] { getManager3D().circle3D(null, p0,
				p1, ec.getView().getDirection()) };
	}

	@Override
	protected GeoConicND circle(Construction cons, GeoPointND center,
			GeoNumberValue radius) {
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
			GeoNumberValue radius) {
		return getManager3D().circle3D(null, center, radius,
				ec.getView().getDirection());
	}

	@Override
	protected GeoElement[] angularBisector(GeoLineND g, GeoLineND h) {
		if (g.isGeoElement3D() || h.isGeoElement3D()) {
			return getManager3D().angularBisector3D(null, g, h);
		}

		return super.angularBisector(g, h);
	}

	@Override
	protected GeoElement angularBisector(GeoPointND A, GeoPointND B,
			GeoPointND C) {
		if (A.isGeoElement3D() || B.isGeoElement3D() || C.isGeoElement3D()) {
			return getManager3D().angularBisector3D(null, A, B, C);
		}

		return super.angularBisector(A, B, C);
	}

	@Override
	protected GeoElement circleArcSector(GeoPointND A, GeoPointND B,
			GeoPointND C, int type) {
		GeoDirectionND orientation = ec.getView().getDirection();

		// use view orientation
		return (GeoElement) getManager3D().circleArcSector3D(null, A,
				B, C, orientation, type);
	}

	@Override
	protected GeoElement semicircle(GeoPointND A, GeoPointND B) {
		GeoDirectionND orientation = ec.getView().getDirection();

		// use view orientation
		return (GeoElement) getManager3D().semicircle3D(null, A, B,
				orientation);
	}

	@Override
	protected GeoElement circumcircleArc(GeoPointND p1, GeoPointND p2,
			GeoPointND p3) {
		if (p1.isGeoElement3D() || p2.isGeoElement3D() || p3.isGeoElement3D()) {
			return (GeoElement) getManager3D().circumcircleArc3D(null,
					p1, p2, p3);
		}

		return super.circumcircleArc(p1, p2, p3);
	}

	@Override
	protected GeoElement circumcircleSector(GeoPointND p1, GeoPointND p2,
			GeoPointND p3) {
		if (p1.isGeoElement3D() || p2.isGeoElement3D() || p3.isGeoElement3D()) {
			return (GeoElement) getManager3D()
					.circumcircleSector3D(null, p1, p2, p3);
		}

		return super.circumcircleSector(p1, p2, p3);
	}

	@Override
	protected GeoElement[] tangent(GeoPointND a, GeoConicND c) {
		return getManager3D().tangent3D(null, a, c);
	}

	@Override
	protected GeoElement[] tangent(GeoLineND l, GeoConicND c) {
		return getManager3D().tangent3D(null, l, c);
	}

	@Override
	protected GeoElement[] tangent(GeoConicND c1, GeoConicND c2) {
		return getManager3D().commonTangents3D(null, c1, c2);
	}

	@Override
	protected GeoElementND polarLine(GeoPointND P, GeoConicND c) {
		if (P.isGeoElement3D() || c.isGeoElement3D()) {
			AlgoPolarLine3D algo = new AlgoPolarLine3D(
					ec.getKernel().getConstruction(), null, c, P);
			return algo.getLine();
		}

		return super.polarLine(P, c);
	}

	@Override
	protected GeoElement diameterLine(GeoLineND l, GeoConicND c) {
		return getManager3D().diameterLine3D(null, l, c);
	}

	@Override
	protected GeoElement diameterLine(GeoVectorND v, GeoConicND c) {
		return getManager3D().diameterLine3D(null, v, c);
	}

	@Override
	protected GeoElement lineBisector(GeoSegmentND segment) {
		return getManager3D().lineBisector3D(null, segment,
				ec.getView().getDirection());
	}

	@Override
	protected GeoElement lineBisector(GeoPointND a, GeoPointND b) {
		return getManager3D().lineBisector3D(null, a, b,
				ec.getView().getDirection());
	}

	@Override
	protected GeoConicND conic5(GeoPointND[] points) {
		for (int i = 0; i < 5; i++) {
			if (points[i].isGeoElement3D()) {
				return getManager3D().conic3D(null, points);
			}
		}

		return super.conic5(points);
	}

	@Override
	protected GeoConicND ellipseHyperbola(GeoPointND a, GeoPointND b,
			GeoPointND c, int type) {
		return getManager3D().ellipseHyperbola3D(null, a, b, c,
				ec.getView().getDirection(), type);
	}

	@Override
	protected GeoConicND parabola(GeoPointND a, GeoLineND l) {
		if (a.isGeoElement3D() || l.isGeoElement3D()) {
			return getManager3D().parabola3D(null, a, l);
		}

		return super.parabola(a, l);
	}

	@Override
	protected GeoElement vectorPoint(GeoPointND a, GeoVectorND v) {
		if (a.isGeoElement3D() || v.isGeoElement3D()) {
			GeoPointND endPoint = (GeoPointND) getManager3D()
					.translate3D(null, a, v)[0];
			return getManager3D().vector3D(null, a, endPoint);
		}

		return super.vectorPoint(a, v);
	}

	@Override
	protected GeoElement locus(GeoPointND a, GeoPointND b) {
		if (a.isGeoElement3D() || b.isGeoElement3D()) {
			return getManager3D().locus3D(null, a, b);
		}

		return super.locus(a, b);
	}

	private Manager3DInterface getManager3D() {
		return ec.getKernel().getManager3D();
	}
}
