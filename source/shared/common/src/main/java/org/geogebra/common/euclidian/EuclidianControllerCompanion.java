package org.geogebra.common.euclidian;

import java.util.ArrayList;

import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.PathNormalizer;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.algos.AlgoCirclePointRadius;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoJoinPointsSegment;
import org.geogebra.common.kernel.algos.AlgoMidpoint;
import org.geogebra.common.kernel.algos.AlgoPolarLine;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.geos.Lineable2D;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.DialogManager.CreateGeoForRotate;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.DoubleUtil;

import com.google.j2objc.annotations.Weak;

/**
 * Class that creates geos for EuclidianController. Needed for special 3D stuff.
 * 
 * @author mathieu
 *
 */
public class EuclidianControllerCompanion {
	/** controller */
	@Weak
	protected EuclidianController ec;

	/**
	 * @param ec
	 *            controller
	 */
	public EuclidianControllerCompanion(EuclidianController ec) {
		setEuclidianController(ec);
	}

	/**
	 * @param ec
	 *            controller
	 */
	protected void setEuclidianController(EuclidianController ec) {
		this.ec = ec;
	}

	/**
	 * @param A
	 *            point
	 * @param B
	 *            point
	 * @param C
	 *            point
	 * @return angle
	 */
	protected GeoAngle createAngle(GeoPointND A, GeoPointND B, GeoPointND C) {
		return ec.getAlgoDispatcher().angle(null, (GeoPoint) A, (GeoPoint) B,
				(GeoPoint) C);
	}

	/**
	 * @param p
	 *            polygon
	 * @return angles
	 */
	protected GeoElement[] createAngles(GeoPolygon p) {
		return ec.getAlgoDispatcher().angles(null, p, true);
	}

	/**
	 * @param v1
	 *            vector
	 * @param v2
	 *            vector
	 * @return angle between vectors
	 */
	protected GeoAngle createAngle(GeoVectorND v1, GeoVectorND v2) {
		return ec.getAlgoDispatcher().angle(null, (GeoVector) v1,
				(GeoVector) v2);
	}

	/**
	 * @param A
	 *            point
	 * @param B
	 *            apex
	 * @param num
	 *            angle size
	 * @param clockWise
	 *            orientation
	 * @return angle with given size
	 */
	public GeoAngle createAngle(GeoPointND A, GeoPointND B, GeoNumberValue num,
			boolean clockWise) {
		return (GeoAngle) ec.getAlgoDispatcher().angle(null, (GeoPoint) A,
				(GeoPoint) B, num, !clockWise)[0];
	}

	/**
	 * @param g
	 *            line
	 * @param h
	 *            line
	 * @return angle between lines
	 */
	protected GeoAngle createLineAngle(GeoLineND g, GeoLineND h) {
		return ec.getAlgoDispatcher().createLineAngle((GeoLine) g, (GeoLine) h);
	}

	/**
	 * @param geo
	 *            preimage
	 * @param vec
	 *            translation vector
	 * @return translated object
	 */
	protected GeoElement[] translate(GeoElement geo, GeoVectorND vec) {
		return ec.getAlgoDispatcher().translate(null, geo, (GeoVector) vec);
	}

	/**
	 * @param geo
	 *            preimage
	 * @param point
	 *            mirror
	 * @return reflected element
	 */
	protected GeoElement[] mirrorAtPoint(GeoElement geo, GeoPointND point) {
		return ec.getAlgoDispatcher().mirror(null, geo, (GeoPoint) point);
	}

	/**
	 * @param geo
	 *            preimage
	 * @param line
	 *            mirror
	 * @return reflected element
	 */
	protected GeoElement[] mirrorAtLine(GeoElement geo, GeoLineND line) {
		return ec.getAlgoDispatcher().mirror(null, geo, (GeoLine) line);
	}

	/**
	 * @param geo
	 *            preimage
	 * @param num
	 *            coefficient
	 * @param point
	 *            point
	 * @return dilated element
	 */
	public GeoElement[] dilateFromPoint(GeoElement geo, GeoNumberValue num,
			GeoPointND point) {
		return ec.kernel.getAlgoDispatcher().dilate(null, geo, num,
				(GeoPoint) point);
	}

	/**
	 * 
	 * @param a
	 *            first geo
	 * @param b
	 *            second geo
	 * @param coords2D
	 *            closest coords
	 * @return single intersection points from geos a,b
	 */
	public GeoPointND getSingleIntersectionPoint(GeoElement a, GeoElement b,
			boolean coords2D) {
		GeoPointND point = null;

		// first hit is a line
		if (a.isGeoLine()) {
			if (b.isGeoLine()) {
				if (!((GeoLine) a).linDep((GeoLine) b)) {
					point = ec.getAlgoDispatcher().intersectLines(null,
							(GeoLine) a, (GeoLine) b);
				} else {
					return null;
				}
			} else if (b.isGeoConic()) {
				point = ec.getAlgoDispatcher().intersectLineConicSingle(null,
						(GeoLine) a, (GeoConic) b, ec.xRW, ec.yRW);
			} else if (b.isGeoCurveCartesian()) {
				return (GeoPointND) ec.getAlgoDispatcher().intersectLineCurve(
						null, (GeoLine) a, (GeoCurveCartesian) b)[0];
			} else if (b.isRealValuedFunction()) {
				// line and function
				GeoFunctionable f = (GeoFunctionable) b;
				if (f.isPolynomialFunction(false)) {
					ec.getAlgoDispatcher()
							.intersectPolynomialLineSingle(null, f, (GeoLine) a,
									ec.xRW, ec.yRW);
				}
				GeoPoint initPoint = new GeoPoint(ec.kernel.getConstruction());
				initPoint.setCoords(ec.xRW, ec.yRW, 1.0);
				point = ec.getAlgoDispatcher().intersectFunctionLine(null, f,
						(GeoLine) a, initPoint);
			} else {
				return null;
			}
		}
		// first hit is a conic
		else if (a.isGeoConic()) {
			if (b.isGeoLine()) {
				point = ec.getAlgoDispatcher().intersectLineConicSingle(null,
						(GeoLine) b, (GeoConic) a, ec.xRW, ec.yRW);
			} else if (b.isGeoConic() && !a.isEqual(b)) {
				point = ec.getAlgoDispatcher().intersectConicsSingle(null,
						(GeoConic) a, (GeoConic) b, ec.xRW, ec.yRW);
			} else {
				return null;
			}
		}
		// first hit is a function
		else if (a.isRealValuedFunction()) {
			GeoFunctionable aFun = (GeoFunctionable) a;
			if (b.isGeoLine()) {
				// line and function
				if (aFun.isPolynomialFunction(false)) {
					point = ec.getAlgoDispatcher()
							.intersectPolynomialLineSingle(null, aFun,
									(GeoLine) b, ec.xRW, ec.yRW);
				} else {
					GeoPoint initPoint = new GeoPoint(
							ec.kernel.getConstruction());
					initPoint.setCoords(ec.xRW, ec.yRW, 1.0);
					point = ec.getAlgoDispatcher().intersectFunctionLine(null,
							aFun, (GeoLine) b, initPoint);
				}
			} else if (b.isRealValuedFunction()) {
				GeoFunctionable bFun = (GeoFunctionable) b;
				if (aFun.isPolynomialFunction(false)
						&& bFun.isPolynomialFunction(false)) {
					return ec.getAlgoDispatcher().intersectPolynomialsSingle(
							null, aFun, bFun, ec.xRW, ec.yRW);
				}
				GeoPoint initPoint = new GeoPoint(ec.kernel.getConstruction());
				initPoint.setCoords(ec.xRW, ec.yRW, 1.0);
				point = ec.getAlgoDispatcher().intersectFunctions(null, aFun,
						bFun, initPoint);
			} else {
				return null;
			}
		} else if (a.isGeoCurveCartesian()) {
			if (b.isGeoCurveCartesian()) {
				return (GeoPointND) ec.getAlgoDispatcher()
						.intersectCurveCurveSingle(null, (GeoCurveCartesian) a,
								(GeoCurveCartesian) b, ec.xRW, ec.yRW)[0];
			} else if (b.isGeoLine()) {
				return (GeoPointND) ec.getAlgoDispatcher().intersectLineCurve(
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

	/**
	 * @param point
	 *            point
	 * @param line
	 *            line
	 * @return line orthogonal to the given one, going through a point
	 */
	protected GeoElement[] orthogonal(GeoPointND point, Lineable2D line) {
		return new GeoElement[] { ec.getAlgoDispatcher().orthogonalLine(null,
				(GeoPoint) point, line) };
	}

	/**
	 * @param point
	 *            point
	 * @param line
	 *            line
	 * @return line orthogonal to the given one, going through a point
	 */
	protected GeoElement[] orthogonal(GeoPointND point, GeoLineND line) {
		return new GeoElement[] { ec.getAlgoDispatcher().orthogonalLine(null,
				(GeoPoint) point, (GeoLine) line) };
	}

	/**
	 * Creates point on path
	 * 
	 * @param label
	 *            point label
	 * @param forPreviewable
	 *            whether it's for preview
	 * @param path
	 *            parent path
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param z
	 *            z-coord
	 * @param complex
	 *            whether it's for complex number
	 * @param coords2D
	 *            whether to force coord type to 2D
	 * @return new point for the pathD
	 */
	public GeoPointND createNewPoint(String label, boolean forPreviewable,
			Path path, double x, double y, double z, boolean complex,
			boolean coords2D) {

		return ec.createNewPoint2D(label, forPreviewable, path, x, y, complex,
				coords2D);
	}

	/**
	 * @param segment
	 *            segment
	 * @return midpoint for segment
	 */
	protected GeoElement midpoint(GeoSegmentND segment) {
		GeoElement mp = ec.getAlgoDispatcher().midpoint((GeoSegment) segment);
		mp.setLabel(null);
		return mp;
	}

	/**
	 * @param geoPolygon
	 *            Polygon
	 * @return Polygon's Centroid
	 */
	public GeoElement centroid(GeoPolygon geoPolygon) {
		GeoElement centroid = ec.getAlgoDispatcher().centroid(geoPolygon);
		centroid.setLabel(null);
		return centroid;
	}

	/**
	 * 
	 * @param conic
	 *            conic curve
	 * @return center of conic
	 */
	protected GeoElement midpoint(GeoConicND conic) {
		return (GeoElement) ec.getAlgoDispatcher().center(null, conic);
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
	 * @param direction
	 *            direction
	 * @return regular polygon
	 */
	public GeoElement[] regularPolygon(GeoPointND geoPoint1,
			GeoPointND geoPoint2, GeoNumberValue value, GeoCoordSys2D direction) {
		return ec.getAlgoDispatcher().regularPolygon(null,
				geoPoint1, geoPoint2, value);
	}

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param p1
	 *            start point
	 * @param p2
	 *            end point
	 * @return segment [p1 p2] algorithm
	 */
	protected AlgoElement segmentAlgo(Construction cons, GeoPointND p1,
			GeoPointND p2) {
		return new AlgoJoinPointsSegment(cons, (GeoPoint) p1, (GeoPoint) p2,
				null, true);
	}

	/**
	 * @param p0
	 *            center
	 * @param p1
	 *            point on circle
	 * @return circle
	 */
	protected GeoElement[] createCircle2(GeoPointND p0, GeoPointND p1) {
		return new GeoElement[] { ec.getAlgoDispatcher().circle(null,
				(GeoPoint) p0, (GeoPoint) p1) };
	}

	/**
	 * @param A
	 *            startpoint
	 * @param B
	 *            endpoint
	 * @return semicircle
	 */
	protected GeoElement semicircle(GeoPointND A, GeoPointND B) {
		return ec.getAlgoDispatcher().semicircle(null, (GeoPoint) A,
				(GeoPoint) B);
	}

	/**
	 * @param cons
	 *            construction
	 * @param center
	 *            center
	 * @param radius
	 *            radius
	 * @return circle
	 */
	protected GeoConicND circle(Construction cons, GeoPointND center,
			GeoNumberValue radius) {
		AlgoCirclePointRadius algo = new AlgoCirclePointRadius(cons,
				(GeoPoint) center, radius);
		algo.getCircle().setLabel(null);
		return algo.getCircle();
	}

	/**
	 * @param g
	 *            first edge
	 * @param h
	 *            second edge
	 * @return angle
	 */
	protected GeoElement[] angularBisector(GeoLineND g, GeoLineND h) {
		return ec.getAlgoDispatcher().angularBisector(null, (GeoLine) g,
				(GeoLine) h);
	}

	/**
	 * @param A
	 *            first leg
	 * @param B
	 *            apex
	 * @param C
	 *            second leg
	 * @return bisector
	 */
	protected GeoElement angularBisector(GeoPointND A, GeoPointND B,
			GeoPointND C) {
		return ec.getAlgoDispatcher().angularBisector(null, (GeoPoint) A,
				(GeoPoint) B, (GeoPoint) C);
	}

	/**
	 * @param p1
	 *            point
	 * @param p2
	 *            center
	 * @param p3
	 *            point
	 * @param type
	 *            sector or arc
	 * @return circle sector or arc
	 */
	protected GeoElement circleArcSector(GeoPointND p1, GeoPointND p2,
			GeoPointND p3, int type) {
		return ec.getAlgoDispatcher().circleArcSector(null, (GeoPoint) p1,
				(GeoPoint) p2, (GeoPoint) p3, type);
	}

	/**
	 * @param p1
	 *            first point
	 * @param p2
	 *            second point
	 * @param p3
	 *            third point
	 * @return circumcircle arc
	 */
	protected GeoElement circumcircleArc(GeoPointND p1, GeoPointND p2,
			GeoPointND p3) {
		return ec.getAlgoDispatcher().circumcircleArc(null, (GeoPoint) p1,
				(GeoPoint) p2, (GeoPoint) p3);
	}

	/**
	 * @param p1
	 *            first point
	 * @param p2
	 *            second point
	 * @param p3
	 *            third point
	 * @return circumcircular sector
	 */
	protected GeoElement circumcircleSector(GeoPointND p1, GeoPointND p2,
			GeoPointND p3) {
		return ec.getAlgoDispatcher().circumcircleSector(null, (GeoPoint) p1,
				(GeoPoint) p2, (GeoPoint) p3);
	}

	/**
	 * @param event
	 *            mouse move event
	 */
	public void movePoint(AbstractEvent event) {
		Coords oldCoords = ec.movedGeoPoint.getCoordsInD3();
		if (ec.getMovedGeoPoint().isGeoElement3D()) {
			oldCoords = oldCoords.copy();
		}
		ec.movedGeoPoint.setCoords(DoubleUtil.checkDecimalFraction(ec.xRW),
				DoubleUtil.checkDecimalFraction(ec.yRW), 1.0);

		if (event.isAltDown()) {
			double multiplier = ec.movedGeoPoint.getAnimationStep();

			int n = (int) Math.ceil(1.0 / multiplier);

			if (n < 1) {
				n = 1;
			}

			if (ec.movedGeoPoint.isPointOnPath()) {
				double minDist = Double.MAX_VALUE;

				Path path = ec.movedGeoPoint.getPath();

				double t = ec.movedGeoPoint.getPathParameter().t;

				// convert to 0 <= t < 1
				t = PathNormalizer.toNormalizedPathParameter(t,
						path.getMinParameter(), path.getMaxParameter());

				double t_1 = t;

				// find closest parameter
				// avoid rounding errors by using an int & multiplier
				for (int i = 0; i < n; i++) {
					double dist = Math.abs(t - i * multiplier);
					if (dist < minDist) {
						t_1 = i * multiplier;
						minDist = dist;
					}
				}

				ec.movedGeoPoint.getPathParameter().t = PathNormalizer
						.toParentPathParameter(t_1, path.getMinParameter(),
								path.getMaxParameter());

				path.pathChanged(ec.movedGeoPoint);
				ec.movedGeoPoint.updateCoords();
			}
		}

		if (!oldCoords.isEqual(ec.movedGeoPoint.getCoordsInD3())) {
			ec.movedGeoPoint.updateCascade();
		}
	}

	/**
	 * move plane
	 * 
	 * @param repaint
	 *            whether to repaint afterwards
	 * @param event
	 *            mouse event
	 */
	protected void movePlane(boolean repaint, AbstractEvent event) {
		// only used in 3D
	}

	/**
	 * @param forPreviewable
	 *            in 3D we might want a preview
	 * @param complex
	 *            whether to use complex coords
	 * @return point
	 */
	protected GeoPointND createNewPoint(boolean forPreviewable,
			boolean complex) {
		GeoPointND ret = ec.getAlgoDispatcher().point(
				DoubleUtil.checkDecimalFraction(ec.xRW),
				DoubleUtil.checkDecimalFraction(ec.yRW), complex);
		ret.setLabel(null);
		return ret;
	}

	/**
	 * @param forPreviewable
	 *            whether it's preview
	 * @param path
	 *            parent path
	 * @param complex
	 *            whether to use complex coords
	 * @return new point
	 */
	protected GeoPointND createNewPoint(boolean forPreviewable, Path path,
			boolean complex) {
		return createNewPoint(null, forPreviewable, path,
				DoubleUtil.checkDecimalFraction(ec.xRW),
				DoubleUtil.checkDecimalFraction(ec.yRW), 0, complex, true);
	}

	/**
	 * @param forPreviewable
	 *            whether it's preview
	 * @param region
	 *            parent region
	 * @param complex
	 *            whether to use complex coords
	 * @return new point
	 */
	protected GeoPointND createNewPoint(boolean forPreviewable, Region region,
			boolean complex) {
		return ec.createNewPoint(null, forPreviewable, region,
				DoubleUtil.checkDecimalFraction(ec.xRW),
				DoubleUtil.checkDecimalFraction(ec.yRW), 0, complex, true);
	}

	/**
	 * @param point
	 *            position for mode locking
	 */
	protected void processModeLock(GeoPointND point) {
		Coords coords = point.getInhomCoordsInD2();
		ec.xRW = coords.getX();
		ec.yRW = coords.getY();
	}

	/**
	 * @param path
	 *            path for mode locking
	 */
	protected void processModeLock(Path path) {
		GeoPoint p = ec.getAlgoDispatcher().point(null, path, ec.xRW, ec.yRW,
				false, false, true);
		p.update();
		ec.xRW = p.inhomX;
		ec.yRW = p.inhomY;
		p.remove();
	}

	/**
	 * For view from plane remove the parent of the view
	 * Must return a new list that can be altered by caller.
	 *
	 * @param list
	 *            list of elements
	 * @return filtered list
	 */
	public ArrayList<GeoElement> removeParentsOfView(
			ArrayList<GeoElement> list) {
		return new ArrayList<>(list);
	}

	/**
	 * 
	 * @param clockwise
	 *            input orientation
	 * @param creator
	 *            contains object used to check clockwise orientation
	 * @return clockwise (resp. not(clockwise)) if clockwise is displayed as it in
	 *         the view (used for EuclidianViewForPlane)
	 */
	public boolean viewOrientationForClockwise(boolean clockwise, CreateGeoForRotate creator) {
		return clockwise;
	}

	/**
	 * @param geoRot
	 *            rotated element
	 * @param phi
	 *            angle
	 * @param Q
	 *            rotation center
	 * @return rotated element
	 */
	public GeoElement[] rotateByAngle(GeoElement geoRot, GeoNumberValue phi,
			GeoPointND Q) {
		return ec.kernel.getAlgoDispatcher().rotate(null, geoRot, phi, Q);
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
		return ec.getAlgoDispatcher().tangent(null, a, c);
	}

	/**
	 * @param l
	 *            line
	 * @param c
	 *            conic
	 * @return tangent line/conic
	 */
	protected GeoElement[] tangent(GeoLineND l, GeoConicND c) {
		return ec.getAlgoDispatcher().tangent(null, l, c);
	}

	/**
	 * @param c1
	 *            conic
	 * @param c2
	 *            conic
	 * @return tangent conic/conic
	 */
	protected GeoElement[] tangent(GeoConicND c1, GeoConicND c2) {
		return ec.getAlgoDispatcher().commonTangents(null, c1, c2);
	}

	/**
	 * polar line to P relative to c
	 * 
	 * @param P
	 *            point
	 * @param c
	 *            conic
	 * @return polar line
	 */
	protected GeoElementND polarLine(GeoPointND P, GeoConicND c) {
		AlgoPolarLine algo = new AlgoPolarLine(ec.kernel.getConstruction(),
				null, c, P);
		return algo.getLine();
	}

	/**
	 * @param l
	 *            line
	 * @param c
	 *            conic
	 * @return diameter line
	 */
	protected GeoElement diameterLine(GeoLineND l, GeoConicND c) {
		return ec.getAlgoDispatcher().diameterLine(null, l, c);
	}

	/**
	 * @param v
	 *            vector
	 * @param c
	 *            conic
	 * @return diameter line
	 */
	protected GeoElement diameterLine(GeoVectorND v, GeoConicND c) {
		return ec.getAlgoDispatcher().diameterLine(null, v, c);
	}

	/**
	 * 
	 * @param segment
	 *            segment
	 * @return segment perpendicular bisector
	 */
	protected GeoElement lineBisector(GeoSegmentND segment) {
		return ec.getAlgoDispatcher().lineBisector(null, (GeoSegment) segment);
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
		return ec.getAlgoDispatcher().lineBisector(null, (GeoPoint) a,
				(GeoPoint) b);
	}

	/**
	 * @param points
	 *            points
	 * @return conic through 5 points
	 */
	protected GeoConicND conic5(GeoPointND[] points) {
		GeoPoint[] p = new GeoPoint[5];
		for (int i = 0; i < 5; i++) {
			p[i] = (GeoPoint) points[i];
		}
		return ec.getAlgoDispatcher().conic(null, p);
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
		return ec.getAlgoDispatcher().ellipseHyperbola(null, a, b, c, type);
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
		return ec.getAlgoDispatcher().parabola(null, a, l);
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
		GeoPoint endPoint = (GeoPoint) ec.getAlgoDispatcher().translate(null,
				a, (GeoVector) v)[0];
		return ec.getAlgoDispatcher().vector(null, (GeoPoint) a, endPoint);
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
		return ec.getAlgoDispatcher().locus(null, a, b);
	}

	/**
	 * set coords of the point to mouse loc
	 * 
	 * @param loc
	 *            point
	 * @return true if set to real world coords, false if set to absolute
	 *         position on screen
	 */
	public boolean setCoordsToMouseLoc(GeoPointND loc) {
		loc.setCoords(ec.mouseLoc.x, ec.mouseLoc.y, 1.0);
		return true;
	}

	/**
	 * @param event
	 *            mouse event
	 */
	public void setMouseLocation(AbstractEvent event) {
		ec.setMouseLocation(event.isAltDown(), event.getX(), event.getY());
	}

	/**
	 *
	 * @return percentage for which we capture point to grid
	 */
	public double getPointCapturingPercentage() {
		return EuclidianStyleConstants.POINT_CAPTURING_GRID;
	}

}
