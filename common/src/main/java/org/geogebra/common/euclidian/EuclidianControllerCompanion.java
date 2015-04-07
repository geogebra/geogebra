package org.geogebra.common.euclidian;

import java.util.ArrayList;

import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.PathNormalizer;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoCirclePointRadius;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoJoinPointsSegment;
import org.geogebra.common.kernel.algos.AlgoMidpoint;
import org.geogebra.common.kernel.algos.AlgoPolarLine;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

/**
 * Class that creates geos for EuclidianController. Needed for special 3D stuff.
 * 
 * @author mathieu
 *
 */
public class EuclidianControllerCompanion {

	protected EuclidianController ec;

	public EuclidianControllerCompanion(EuclidianController ec) {
		this.ec = ec;
	}

	protected GeoAngle createAngle(GeoPointND A, GeoPointND B, GeoPointND C) {
		ec.checkZooming();

		return ec.getAlgoDispatcher().Angle(null, (GeoPoint) A, (GeoPoint) B,
				(GeoPoint) C);
	}

	protected GeoElement[] createAngles(GeoPolygon p) {
		return ec.getAlgoDispatcher().Angles(null, p);
	}

	protected GeoAngle createAngle(GeoVectorND v1, GeoVectorND v2) {
		return ec.getAlgoDispatcher().Angle(null, (GeoVector) v1,
				(GeoVector) v2);
	}

	public GeoAngle createAngle(GeoPointND A, GeoPointND B, GeoNumberValue num,
			boolean clockWise) {
		return (GeoAngle) ec.getAlgoDispatcher().Angle(null, (GeoPoint) A,
				(GeoPoint) B, num, !clockWise)[0];
	}

	protected GeoAngle createLineAngle(GeoLineND g, GeoLineND h) {
		return ec.getAlgoDispatcher().createLineAngle((GeoLine) g, (GeoLine) h);
	}

	protected GeoElement[] translate(GeoElement geo, GeoVectorND vec) {
		ec.checkZooming();

		return ec.getAlgoDispatcher().Translate(null, geo, (GeoVector) vec);
	}

	protected GeoElement[] mirrorAtPoint(GeoElement geo, GeoPointND point) {
		return ec.getAlgoDispatcher().Mirror(null, geo, (GeoPoint) point);
	}

	protected GeoElement[] mirrorAtLine(GeoElement geo, GeoLineND line) {
		return ec.getAlgoDispatcher().Mirror(null, geo, (GeoLine) line);
	}

	public GeoElement[] dilateFromPoint(GeoElement geo, NumberValue num,
			GeoPointND point) {
		return ec.kernel.getAlgoDispatcher().Dilate(null, geo, num,
				(GeoPoint) point);
	}

	/**
	 * 
	 * @param a
	 *            first geo
	 * @param b
	 *            second geo
	 * @return single intersection points from geos a,b
	 */
	public GeoPointND getSingleIntersectionPoint(GeoElement a, GeoElement b,
			boolean coords2D) {
		GeoPointND point = null;

		// first hit is a line
		if (a.isGeoLine()) {
			if (b.isGeoLine()) {
				if (!((GeoLine) a).linDep((GeoLine) b)) {
					point = ec.getAlgoDispatcher().IntersectLines(null,
							(GeoLine) a, (GeoLine) b);
				} else
					return null;
			} else if (b.isGeoConic()) {
				point = ec.getAlgoDispatcher().IntersectLineConicSingle(null,
						(GeoLine) a, (GeoConic) b, ec.xRW, ec.yRW);
			} else if (b.isGeoCurveCartesian()) {
				return (GeoPointND) ec.getAlgoDispatcher().IntersectLineCurve(
						null, (GeoLine) a, (GeoCurveCartesian) b)[0];
			} else if (b.isGeoFunctionable()) {
				// line and function
				GeoFunction f = ((GeoFunctionable) b).getGeoFunction();
				if (f.isPolynomialFunction(false)) {
					point = ec.getAlgoDispatcher()
							.IntersectPolynomialLineSingle(null, f,
									(GeoLine) a, ec.xRW, ec.yRW);
				}
				GeoPoint initPoint = new GeoPoint(ec.kernel.getConstruction());
				initPoint.setCoords(ec.xRW, ec.yRW, 1.0);
				point = ec.getAlgoDispatcher().IntersectFunctionLine(null, f,
						(GeoLine) a, initPoint);
			} else {
				return null;
			}
		}
		// first hit is a conic
		else if (a.isGeoConic()) {
			if (b.isGeoLine()) {
				point = ec.getAlgoDispatcher().IntersectLineConicSingle(null,
						(GeoLine) b, (GeoConic) a, ec.xRW, ec.yRW);
			} else if (b.isGeoConic() && !a.isEqual(b)) {
				point = ec.getAlgoDispatcher().IntersectConicsSingle(null,
						(GeoConic) a, (GeoConic) b, ec.xRW, ec.yRW);
			} else {
				return null;
			}
		}
		// first hit is a function
		else if (a.isGeoFunctionable()) {
			GeoFunction aFun = ((GeoFunctionable) a).getGeoFunction();
			if (b.isGeoLine()) {
				// line and function
				if (aFun.isPolynomialFunction(false)) {
					point = ec.getAlgoDispatcher()
							.IntersectPolynomialLineSingle(null, aFun,
									(GeoLine) b, ec.xRW, ec.yRW);
				} else {
					GeoPoint initPoint = new GeoPoint(
							ec.kernel.getConstruction());
					initPoint.setCoords(ec.xRW, ec.yRW, 1.0);
					point = ec.getAlgoDispatcher().IntersectFunctionLine(null,
							aFun, (GeoLine) b, initPoint);
				}
			} else if (b.isGeoFunctionable()) {
				GeoFunction bFun = ((GeoFunctionable) b).getGeoFunction();
				if (aFun.isPolynomialFunction(false)
						&& bFun.isPolynomialFunction(false)) {
					return ec.getAlgoDispatcher().IntersectPolynomialsSingle(
							null, aFun, bFun, ec.xRW, ec.yRW);
				}
				GeoPoint initPoint = new GeoPoint(ec.kernel.getConstruction());
				initPoint.setCoords(ec.xRW, ec.yRW, 1.0);
				point = ec.getAlgoDispatcher().IntersectFunctions(null, aFun,
						bFun, initPoint);
			} else {
				return null;
			}
		} else if (a.isGeoCurveCartesian()) {
			if (b.isGeoCurveCartesian()) {
				return (GeoPointND) ec.getAlgoDispatcher()
						.IntersectCurveCurveSingle(null, (GeoCurveCartesian) a,
								(GeoCurveCartesian) b, ec.xRW, ec.yRW)[0];
			} else if (b.isGeoLine()) {
				return (GeoPointND) ec.getAlgoDispatcher().IntersectLineCurve(
						null, (GeoLine) b, (GeoCurveCartesian) a)[0];
			}
		}

		if (point != null) {
			if (!coords2D) {
				point.setCartesian3D();
				point.update();
			}
		}

		return point;
	}

	protected GeoElement[] orthogonal(GeoPointND point, GeoLineND line) {
		ec.checkZooming();

		return new GeoElement[] { ec.getAlgoDispatcher().OrthogonalLine(null,
				(GeoPoint) point, (GeoLine) line) };
	}

	/**
	 * 
	 * @param forPreviewable
	 * @param path
	 * @param x
	 * @param y
	 * @param z
	 * @param complex
	 * @param coords2D
	 * @return new point for the path
	 */
	public GeoPointND createNewPoint(String label, boolean forPreviewable,
			Path path, double x, double y, double z, boolean complex,
			boolean coords2D) {

		return ec.createNewPoint2D(label, forPreviewable, path, x, y, complex,
				coords2D);
	}

	/**
	 * 
	 * @param segment
	 * @return midpoint for segment
	 */
	protected GeoElement midpoint(GeoSegmentND segment) {

		return ec.getAlgoDispatcher().Midpoint(null, (GeoSegment) segment);

	}

	/**
	 * 
	 * @param conic
	 * @return center of conic
	 */
	protected GeoElement midpoint(GeoConicND conic) {

		return (GeoElement) ec.getAlgoDispatcher().Center(null, conic);

	}

	/**
	 * 
	 * @param p1
	 *            first point
	 * @param p2
	 *            second point
	 * @return midpoint for two points
	 */
	protected GeoElement midpoint(GeoPointND p1, GeoPointND p2) {

		AlgoMidpoint algo = new AlgoMidpoint(ec.kernel.getConstruction(),
				(GeoPoint) p1, (GeoPoint) p2);
		return algo.getPoint();

	}

	/**
	 * 
	 * @param geoPoint1
	 *            first point
	 * @param geoPoint2
	 *            second point
	 * @param value
	 *            n vertices
	 * @return regular polygon
	 */
	public GeoElement[] regularPolygon(GeoPointND geoPoint1,
			GeoPointND geoPoint2, GeoNumberValue value) {
		ec.kernel.addingPolygon();
		GeoElement[] elms = ec.getAlgoDispatcher().RegularPolygon(null,
				geoPoint1, geoPoint2, value);
		ec.kernel.notifyPolygonAdded();
		return elms;
		// return kernel.getAlgoDispatcher().RegularPolygon(null, geoPoint1,
		// geoPoint2, value);
	}

	/**
	 * 
	 * @param cons
	 * @param p1
	 * @param p2
	 * @return segment [p1 p2] algorithm
	 */
	protected AlgoElement segmentAlgo(Construction cons, GeoPointND p1,
			GeoPointND p2) {
		return new AlgoJoinPointsSegment(cons, (GeoPoint) p1, (GeoPoint) p2,
				null);
	}

	protected GeoElement[] createCircle2(GeoPointND p0, GeoPointND p1) {

		return new GeoElement[] { ec.getAlgoDispatcher().Circle(null,
				(GeoPoint) p0, (GeoPoint) p1) };
	}

	protected GeoElement semicircle(GeoPointND A, GeoPointND B) {
		return ec.getAlgoDispatcher().Semicircle(null, (GeoPoint) A,
				(GeoPoint) B);
	}

	protected GeoConicND circle(Construction cons, GeoPointND center,
			NumberValue radius) {
		AlgoCirclePointRadius algo = new AlgoCirclePointRadius(cons, null,
				(GeoPoint) center, radius);
		return algo.getCircle();
	}

	protected GeoElement[] angularBisector(GeoLineND g, GeoLineND h) {
		return ec.getAlgoDispatcher().AngularBisector(null, (GeoLine) g,
				(GeoLine) h);
	}

	protected GeoElement angularBisector(GeoPointND A, GeoPointND B,
			GeoPointND C) {
		return ec.getAlgoDispatcher().AngularBisector(null, (GeoPoint) A,
				(GeoPoint) B, (GeoPoint) C);
	}

	protected GeoElement circleArcSector(GeoPointND p1, GeoPointND p2,
			GeoPointND p3, int type) {
		return ec.getAlgoDispatcher().CircleArcSector(null, (GeoPoint) p1,
				(GeoPoint) p2, (GeoPoint) p3, type);
	}

	protected GeoElement circumcircleArc(GeoPointND p1, GeoPointND p2,
			GeoPointND p3) {
		return ec.getAlgoDispatcher().CircumcircleArc(null, (GeoPoint) p1,
				(GeoPoint) p2, (GeoPoint) p3);
	}

	protected GeoElement circumcircleSector(GeoPointND p1, GeoPointND p2,
			GeoPointND p3) {
		return ec.getAlgoDispatcher().CircumcircleSector(null, (GeoPoint) p1,
				(GeoPoint) p2, (GeoPoint) p3);
	}

	public void movePoint(boolean repaint, AbstractEvent event) {
		ec.movedGeoPoint.setCoords(Kernel.checkDecimalFraction(ec.xRW),
				Kernel.checkDecimalFraction(ec.yRW), 1.0);

		if (event.isAltDown()) {

			// 1/24 -> steps of 15 degrees (for circle)
			// otherwise use Object Properties -> Algebra -> Increment
			// double multiplier = event.isAltDown() ? 1.0/24.0 :
			// movedGeoPoint.getAnimationStep();

			double multiplier = ec.movedGeoPoint.getAnimationStep();

			int n = (int) Math.ceil(1.0 / multiplier);

			if (n < 1) {
				n = 1;
			}

			if (ec.movedGeoPoint.hasPath()) {

				double dist = Double.MAX_VALUE;

				Path path = ec.movedGeoPoint.getPath();

				double t = ec.movedGeoPoint.getPathParameter().t;

				// convert to 0 <= t < 1
				t = PathNormalizer.toNormalizedPathParameter(t,
						path.getMinParameter(), path.getMaxParameter());

				double t_1 = t;

				// find closest parameter
				// avoid rounding errors by using an int & multiplier
				for (int i = 0; i < n; i++) {
					if (Math.abs(t - i * multiplier) < dist) {
						t_1 = i * multiplier;
						dist = Math.abs(t - i * multiplier);
					}
				}

				ec.movedGeoPoint.getPathParameter().t = PathNormalizer
						.toParentPathParameter(t_1, path.getMinParameter(),
								path.getMaxParameter());

				path.pathChanged(ec.movedGeoPoint);
				ec.movedGeoPoint.updateCoords();

			}
		}

		((GeoElement) ec.movedGeoPoint).updateCascade();
		ec.movedGeoPointDragged = true;

		if (repaint) {
			ec.kernel.notifyRepaint();
		}
	}

	/**
	 * move plane
	 * 
	 * @param repaint
	 * @param event
	 */
	protected void movePlane(boolean repaint, AbstractEvent event) {
		// only used in 3D
	}

	/**
	 * @param forPreviewable
	 *            in 3D we might want a preview
	 */
	protected GeoPointND createNewPoint(boolean forPreviewable, boolean complex) {

		ec.checkZooming(forPreviewable);

		GeoPointND ret = ec.getAlgoDispatcher().Point(null,
				Kernel.checkDecimalFraction(ec.xRW),
				Kernel.checkDecimalFraction(ec.yRW), complex);
		return ret;
	}

	protected GeoPointND createNewPoint(boolean forPreviewable, Path path,
			boolean complex) {
		return createNewPoint(null, forPreviewable, path,
				Kernel.checkDecimalFraction(ec.xRW),
				Kernel.checkDecimalFraction(ec.yRW), 0, complex, true);
	}

	protected GeoPointND createNewPoint(boolean forPreviewable, Region region,
			boolean complex) {
		return ec.createNewPoint(null, forPreviewable, region,
				Kernel.checkDecimalFraction(ec.xRW),
				Kernel.checkDecimalFraction(ec.yRW), 0, complex, true);
	}

	protected void processModeLock(GeoPointND point) {
		Coords coords = point.getInhomCoordsInD2();
		ec.xRW = coords.getX();
		ec.yRW = coords.getY();
	}

	protected void processModeLock(Path path) {
		ec.checkZooming();

		GeoPoint p = ec.getAlgoDispatcher().Point(null, path, ec.xRW, ec.yRW,
				false, false, true);
		p.update();
		ec.xRW = p.inhomX;
		ec.yRW = p.inhomY;
	}

	public ArrayList<GeoElement> removeParentsOfView(ArrayList<GeoElement> list) {
		return list;
	}

	/**
	 * 
	 * @param clockwise
	 * @return clockwise (resp. not(clockwise)) if clockwise is displayed as it
	 *         in the view (used for EuclidianViewForPlane)
	 */
	public boolean viewOrientationForClockwise(boolean clockwise) {
		return clockwise;
	}

	public GeoElement[] rotateByAngle(GeoElement geoRot, GeoNumberValue phi,
			GeoPointND Q) {

		return ec.kernel.getAlgoDispatcher().Rotate(null, geoRot, phi, Q);
	}

	/**
	 * 
	 * @param a
	 *            point
	 * @param c
	 *            conic
	 * @return tangent point/conic
	 */
	protected GeoElement[] tangent(GeoPointND a, GeoConicND c) {
		return ec.getAlgoDispatcher().Tangent(null, a, c);
	}

	/**
	 * @param l
	 *            line
	 * @param c
	 *            conic
	 * @return tangent line/conic
	 */
	protected GeoElement[] tangent(GeoLineND l, GeoConicND c) {
		return ec.getAlgoDispatcher().Tangent(null, l, c);
	}

	/**
	 * @param c1
	 *            conic
	 * @param c2
	 *            conic
	 * @return tangent conic/conic
	 */
	protected GeoElement[] tangent(GeoConicND c1, GeoConicND c2) {
		return ec.getAlgoDispatcher().CommonTangents(null, c1, c2);
	}

	/**
	 * polar line to P relativ to c
	 * 
	 * @param P
	 *            point
	 * @param c
	 *            conic
	 * @return polar line
	 */
	protected GeoElement polarLine(GeoPointND P, GeoConicND c) {
		AlgoPolarLine algo = new AlgoPolarLine(ec.kernel.getConstruction(),
				null, c, P);
		return (GeoElement) algo.getLine();
	}

	/**
	 * @param l
	 *            line
	 * @param c
	 *            conic
	 * @return diameter line
	 */
	protected GeoElement diameterLine(GeoLineND l, GeoConicND c) {
		return ec.getAlgoDispatcher().DiameterLine(null, l, c);
	}

	/**
	 * @param v
	 *            vector
	 * @param c
	 *            conic
	 * @return diameter line
	 */
	protected GeoElement diameterLine(GeoVectorND v, GeoConicND c) {
		return ec.getAlgoDispatcher().DiameterLine(null, v, c);
	}

	/**
	 * 
	 * @param segment
	 *            segment
	 * @return segment perpendicular bisector
	 */
	protected GeoElement lineBisector(GeoSegmentND segment) {
		return ec.getAlgoDispatcher().LineBisector(null, (GeoSegment) segment);
	}

	/**
	 * 
	 * @param a
	 *            first point
	 * @param b
	 *            second point
	 * @return [ab] perpendicular bisector
	 */
	protected GeoElement lineBisector(GeoPointND a, GeoPointND b) {
		return ec.getAlgoDispatcher().LineBisector(null, (GeoPoint) a,
				(GeoPoint) b);
	}

	/**
	 * @param points
	 *            points
	 * @return conic throught 5 points
	 */
	protected GeoConicND conic5(GeoPointND[] points) {
		GeoPoint[] p = new GeoPoint[5];
		for (int i = 0; i < 5; i++) {
			p[i] = (GeoPoint) points[i];
		}
		return ec.getAlgoDispatcher().Conic(null, p);
	}

	/**
	 * 
	 * @param a
	 *            first focus
	 * @param b
	 *            second focus
	 * @param c
	 *            point on ellipse/hyperbola
	 * @param type
	 *            ellipse/hyperbola
	 * @return ellipse/hyperbola
	 */
	protected GeoConicND ellipseHyperbola(GeoPointND a, GeoPointND b,
			GeoPointND c, int type) {
		return ec.getAlgoDispatcher().EllipseHyperbola(null, a, b, c, type);
	}

	/**
	 * 
	 * @param a
	 *            focus
	 * @param l
	 *            line
	 * @return parabola
	 */
	protected GeoConicND parabola(GeoPointND a, GeoLineND l) {
		return ec.getAlgoDispatcher().Parabola(null, a, l);
	}

	/**
	 * 
	 * @param a
	 *            start point
	 * @param v
	 *            vector
	 * @return vector equal to v with a for start point
	 */
	protected GeoElement vectorPoint(GeoPointND a, GeoVectorND v) {

		GeoPoint endPoint = (GeoPoint) ec.getAlgoDispatcher().Translate(null,
				(GeoPoint) a, (GeoVector) v)[0];
		return ec.getAlgoDispatcher().Vector(null, (GeoPoint) a, endPoint);
	}

	/**
	 * 
	 * @param a
	 *            dependent point
	 * @param b
	 *            point on path
	 * @return locus
	 */
	protected GeoElement locus(GeoPointND a, GeoPointND b) {

		return ec.getAlgoDispatcher().Locus(null, a, b);
	}

}
