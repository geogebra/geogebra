package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Manager3DInterface;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.Region;
import geogebra.common.kernel.Transform;
import geogebra.common.kernel.Matrix.CoordMatrix;
import geogebra.common.kernel.Matrix.CoordMatrix4x4;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoAnglePoints;
import geogebra.common.kernel.algos.AlgoCircleThreePoints;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoOrthoLinePointLine;
import geogebra.common.kernel.algos.AlgoPolygon;
import geogebra.common.kernel.arithmetic.Equation;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumberValue;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoSurfaceFinite;
import geogebra.common.kernel.kernelND.Geo3DVec;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoCoordSys2D;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPlaneND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoQuadricND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.kernel.kernelND.GeoVectorND;
import geogebra.common.kernel.kernelND.HasVolume;
import geogebra.common.plugin.GeoClass;

/**
 * Class that for manage all 3D methods in AbstractKernel.
 * 
 * @author mathieu
 * 
 */
public class Manager3D implements Manager3DInterface {

	private Kernel kernel;
	private Construction cons;

	/**
	 * @param kernel
	 */
	public Manager3D(Kernel kernel) {
		this.kernel = kernel;
		this.cons = kernel.getConstruction();
	}

	/** Point3D label with cartesian coordinates (x,y,z) */
	final public GeoPoint3D Point3D(String label, double x, double y, double z, boolean coords2D) {
		GeoPoint3D p = new GeoPoint3D(cons);
		if (coords2D)
			p.setCartesian();
		else
			p.setCartesian3D();
		p.setCoords(x, y, z, 1.0);
		p.setLabel(label); // invokes add()

		return p;
	}

	/**
	 * Point dependent on arithmetic expression with variables, represented by a
	 * tree. e.g. P = (4t, 2s)
	 */
	final public GeoPoint3D DependentPoint3D(String label, ExpressionNode root) {
		AlgoDependentPoint3D algo = new AlgoDependentPoint3D(cons, label, root);
		GeoPoint3D P = algo.getPoint3D();
		P.setCartesian3D();
		P.update();
		return P;
	}

	final public GeoVector3D DependentVector3D(String label, ExpressionNode root) {
		AlgoDependentVector3D algo = new AlgoDependentVector3D(cons, label,
				root);
		GeoVector3D P = algo.getVector3D();
		return P;
	}

	final public GeoVector3D Vector3D(String label, double x, double y, double z) {
		GeoVector3D v = new GeoVector3D(cons, x, y, z);
		v.setLabel(label); // invokes add()
		return v;
	}

	/**
	 * Vector named label from Point P to Q
	 */
	final public GeoVector3D Vector3D(String label, GeoPointND P, GeoPointND Q) {
		AlgoVector3D algo = new AlgoVector3D(cons, label, P, Q);
		GeoVector3D v = (GeoVector3D) algo.getVector();
		v.setEuclidianVisible(true);
		v.update();
		kernel.notifyUpdate(v);
		return v;
	}

	/** Point in region with cartesian coordinates (x,y,z) */
	final public GeoPoint3D Point3DIn(String label, Region region,
			Coords coords, boolean addToConstruction, boolean coords2D) {
		boolean oldMacroMode = false;

		if (!addToConstruction) {
			oldMacroMode = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);

		}
		// Application.debug("Point3DIn - \n x="+x+"\n y="+y+"\n z="+z);
		AlgoPoint3DInRegion algo = new AlgoPoint3DInRegion(cons, label, region,
				coords);
		GeoPoint3D p = algo.getP();

		if (coords2D)
			p.setCartesian();
		else
			p.setCartesian3D();
		p.update();
		
		if (!addToConstruction) {
			cons.setSuppressLabelCreation(oldMacroMode);
		}
		return p;
	}

	/** Point in region with cartesian coordinates (x,y,z) */
	final public GeoPoint3D Point3DIn(Region region, Coords coords, boolean coords2D) {
		AlgoPoint3DInRegion algo = new AlgoPoint3DInRegion(cons, region, coords);
		GeoPoint3D p = algo.getP();
		if (coords2D)
			p.setCartesian();
		else
			p.setCartesian3D();
		p.update();
		return p;
	}

	/** Point in region */
	final public GeoPoint3D Point3DIn(String label, Region region, boolean coords2D) {
		return Point3DIn(label, region, null, true, coords2D);
	}

	/** Point3D on a 1D path with cartesian coordinates (x,y,z) */
	final public GeoPoint3D Point3D(String label, Path path, double x,
			double y, double z, boolean addToConstruction, boolean coords2D) {
		boolean oldMacroMode = false;
		if (!addToConstruction) {
			oldMacroMode = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);

		}
		AlgoPoint3DOnPath algo = new AlgoPoint3DOnPath(cons, label, path, x, y,
				z);
		GeoPoint3D p = algo.getP();
		if (coords2D)
			p.setCartesian();
		else
			p.setCartesian3D();
		p.update();
		if (!addToConstruction) {
			cons.setSuppressLabelCreation(oldMacroMode);
		}
		return p;
	}

	/** Point3D on a 1D path without cartesian coordinates */
	final public GeoPoint3D Point3D(String label, Path path, boolean coords2D) {
		// try (0,0,0)
		// AlgoPoint3DOnPath algo = new AlgoPoint3DOnPath(cons, label, path, 0,
		// 0, 0);
		// GeoPoint3D p = algo.getP();
		GeoPoint3D p = Point3D(label, path, 0, 0, 0, true, coords2D);

		/*
		 * TODO below // try (1,0,0) if (!p.isDefined()) { p.setCoords(1,0,1);
		 * algo.update(); }
		 * 
		 * // try (random(),0) if (!p.isDefined()) {
		 * p.setCoords(Math.random(),0,1); algo.update(); }
		 */

		return p;
	}

	/**
	 * Midpoint M = (P + Q)/2
	 */
	final public GeoPoint3D Midpoint(String label, GeoPointND P, GeoPointND Q) {
		AlgoMidpoint3D algo = new AlgoMidpoint3D(cons, label, P, Q);
		GeoPoint3D M = algo.getPoint();
		return M;
	}

	public GeoPointND Midpoint(String label, GeoSegmentND segment) {

		AlgoMidpoint3D algo = new AlgoMidpointSegment3D(cons, label, segment);
		GeoPoint3D M = algo.getPoint();
		return M;

	}

	/** Segment3D label linking points v1 and v2 */
	/*
	 * final public GeoSegment3D Segment3D(String label, Ggb3DVector v1,
	 * Ggb3DVector v2){ GeoSegment3D s = new GeoSegment3D(cons,v1,v2);
	 * s.setLabel(label); return s; }
	 */

	/** Segment3D label linking points P1 and P2 */
	final public GeoSegment3D Segment3D(String label, GeoPointND P1,
			GeoPointND P2) {
		AlgoJoinPoints3D algo = new AlgoJoinPoints3D(cons, label, P1, P2,
				GeoClass.SEGMENT3D);
		GeoSegment3D s = (GeoSegment3D) algo.getCS();
		return s;
	}

	/** Line3D label linking points P1 and P2 */
	final public GeoLine3D Line3D(String label, GeoPointND P1, GeoPointND P2) {
		AlgoJoinPoints3D algo = new AlgoJoinPoints3D(cons, label, P1, P2,
				GeoClass.LINE3D);
		GeoLine3D l = (GeoLine3D) algo.getCS();
		return l;
	}

	final public GeoLineND Line3D(String label, GeoPointND P, GeoLineND l) {
		AlgoLinePointLine3D algo = new AlgoLinePointLine3D(cons, label, P, l);
		GeoLineND g = algo.getLine();
		return g;
	}

	final public GeoLineND Line3D(String label, GeoPointND P, GeoVectorND v) {
		AlgoLinePointVector3D algo = new AlgoLinePointVector3D(cons, label, P,
				v);
		GeoLineND g = algo.getLine();
		return g;
	}

	/** Ray3D label linking points P1 and P2 */
	final public GeoRay3D Ray3D(String label, GeoPointND P1, GeoPointND P2) {
		// Application.debug("Kernel3D : Ray3D");
		// AlgoJoinPointsRay3D algo = new AlgoJoinPointsRay3D(cons, label, P1,
		// P2);
		// GeoRay3D l = algo.getRay3D();
		AlgoJoinPoints3D algo = new AlgoJoinPoints3D(cons, label, P1, P2,
				GeoClass.RAY3D);
		GeoRay3D l = (GeoRay3D) algo.getCS();
		return l;
	}

	public GeoLineND OrthogonalLine3D(String label, GeoPointND point,
			GeoCoordSys2D cs) {
		AlgoOrthoLinePointPlane algo = new AlgoOrthoLinePointPlane(cons, label,
				point, cs);
		return algo.getLine();
	}

	public GeoLineND OrthogonalLine3D(String label, GeoPointND point,
			GeoLineND line) {
		AlgoOrthoLinePointLine3D algo = new AlgoOrthoLinePointLine3D(cons,
				label, point, line);
		return algo.getLine();
	}

	public GeoLineND OrthogonalLine3D(String label, GeoPointND point,
			GeoDirectionND line, GeoDirectionND direction) {
		
		//when have space as direction, just to say it's not as in 2D
		if (line instanceof GeoLineND && direction==((Construction3D) cons).getSpace())
			return OrthogonalLine3D(label, point, (GeoLineND) line);	
		
		//when xOy plane as direction, check if it's only 2D objects, then return 2D line
		if (direction==((Construction3D) cons).getXOYPlane() && (point instanceof GeoPoint) && (line instanceof GeoLine)){
			AlgoOrthoLinePointLine algo = new AlgoOrthoLinePointLineXOYPlane(cons, label, (GeoPoint) point, (GeoLine) line);
			return algo.getLine();
		}
		
		AlgoOrthoLinePointDirectionDirection algo = new AlgoOrthoLinePointDirectionDirection(
				cons, label, point, line, direction);
		return algo.getLine();
	}

	public GeoLineND OrthogonalLine3D(String label, GeoLineND line1,
			GeoLineND line2) {
		AlgoOrthoLineLineLine algo = new AlgoOrthoLineLineLine(cons, label,
				line1, line2);
		return algo.getLine();
	}

	public GeoVectorND OrthogonalVector3D(String label, GeoCoordSys2D plane) {
		AlgoOrthoVectorPlane algo = new AlgoOrthoVectorPlane(cons, label, plane);
		return algo.getVector();
	}

	public GeoVectorND UnitOrthogonalVector3D(String label, GeoCoordSys2D plane) {
		AlgoUnitOrthoVectorPlane algo = new AlgoUnitOrthoVectorPlane(cons,
				label, plane);
		return algo.getVector();
	}

	/**
	 * Polygon3D linking points P1, P2, ...
	 * 
	 * @param label
	 *            name of the polygon
	 * @param points
	 *            vertices of the polygon
	 * @return the polygon
	 */
	final public GeoElement[] Polygon3D(String[] label, GeoPointND[] points) {

		AlgoPolygon3D algo = new AlgoPolygon3D(cons, label, points, null);

		return algo.getOutput();

	}

	final public GeoElement[] Polygon3D(String[] label, GeoPointND[] points,
			GeoDirectionND direction) {
		AlgoPolygon algo = new AlgoPolygon3DDirection(cons, label, points,
				direction);

		return algo.getOutput();

	}

	final public GeoElement[] PolyLine3D(String[] labels, GeoPointND[] P) {
		AlgoPolyLine3D algo = new AlgoPolyLine3D(cons, labels, P);
		return algo.getOutput();
	}

	final public GeoElement[] PolyLine3D(String[] labels, GeoList pointList) {
		AlgoPolyLine3D algo = new AlgoPolyLine3D(cons, labels, pointList);
		return algo.getOutput();
	}

	/**
	 * Prism with vertices (last one is first vertex of second parallel face)
	 * 
	 * @param labels names
	 * @param points vertices
	 * @return the polyhedron
	 */
	final public GeoElement[] Prism(String[] labels, GeoPointND[] points) {

		AlgoPolyhedronPointsPrism algo = new AlgoPolyhedronPointsPrism(cons,
				labels, points);

		return algo.getOutput();

	}

	final public GeoElement[] Prism(String[] labels, GeoPolygon polygon,
			GeoPointND point) {

		AlgoPolyhedronPointsPrism algo = new AlgoPolyhedronPointsPrism(cons,
				labels, polygon, point);

		return algo.getOutput();

	}

	final public GeoElement[] Prism(String[] labels, GeoPolygon polygon,
			NumberValue height) {

		AlgoPolyhedronPointsPrism algo = new AlgoPolyhedronPointsPrism(cons,
				labels, polygon, height);

		return algo.getOutput();
	}

	/**
	 * Pyramid with vertices (last one as apex)
	 * 
	 * @param labels names
	 * @param points vertices
	 * @return the polyhedron
	 */
	final public GeoElement[] Pyramid(String[] labels, GeoPointND[] points) {

		AlgoPolyhedronPointsPyramid algo = new AlgoPolyhedronPointsPyramid(
				cons, labels, points);

		return algo.getOutput();

	}
	
	final public GeoElement[] Pyramid(String[] labels, GeoPolygon polygon,
			GeoPointND point) {

		AlgoPolyhedronPointsPyramid algo = new AlgoPolyhedronPointsPyramid(cons,
				labels, polygon, point);

		return algo.getOutput();

	}
	

	final public GeoElement[] Pyramid(String[] labels, GeoPolygon polygon,
			NumberValue height) {

		AlgoPolyhedronPointsPyramid algo = new AlgoPolyhedronPointsPyramid(cons,
				labels, polygon, height);

		return algo.getOutput();
	}

	/** Line a x + b y + c z + d = 0 named label */
	final public GeoPlane3D Plane3D(String label, double a, double b, double c,
			double d) {
		GeoPlane3D plane = new GeoPlane3D(cons, label, a, b, c, d);
		return plane;
	}

	/**
	 * Line dependent on coefficients of arithmetic expressions with variables,
	 * represented by trees.
	 */
	final public GeoPlane3D DependentPlane3D(String label, Equation equ) {
		AlgoDependentPlane3D algo = new AlgoDependentPlane3D(cons, label, equ);
		return algo.getPlane();
	}

	final public GeoPlane3D Plane3D(String label, GeoPointND point,
			GeoLineND line) {
		AlgoPlaneThroughPointAndLine algo = new AlgoPlaneThroughPointAndLine(
				cons, label, point, line);
		return algo.getPlane();
	}

	final public GeoPlane3D Plane3D(String label, GeoPointND point,
			GeoCoordSys2D cs) {
		AlgoPlaneThroughPointAndPlane algo = new AlgoPlaneThroughPointAndPlane(
				cons, label, point, cs);
		return algo.getPlane();
	}

	/**
	 * Plane named label through Point P orthogonal to line l
	 */
	final public GeoPlane3D OrthogonalPlane3D(String label, GeoPointND point,
			GeoLineND line) {

		return new AlgoOrthoPlanePointLine(cons, label, point, line).getPlane();
	}

	/**
	 * Plane named label through Point P orthogonal to line l
	 */
	final public GeoPlane3D OrthogonalPlane3D(String label, GeoPointND point,
			GeoVectorND vector) {

		return new AlgoOrthoPlanePointVector(cons, label, point, vector)
				.getPlane();
	}

	final public GeoPlane3D PlaneBisector(String label, GeoPointND point1,
			GeoPointND point2) {

		return new AlgoOrthoPlaneBisectorPointPoint(cons, label, point1, point2)
				.getPlane();
	}

	final public GeoPlane3D PlaneBisector(String label, GeoSegmentND segment) {

		return new AlgoOrthoPlaneBisectorSegment(cons, label, segment)
				.getPlane();
	}

	/** Sphere label linking with center o and radius r */
	final public GeoQuadric3D Sphere(String label, GeoPointND M, NumberValue r) {
		AlgoSpherePointRadius algo = new AlgoSpherePointRadius(cons, label, M,
				r);
		return algo.getSphere();
	}

	/**
	 * Sphere with midpoint M through point P
	 */
	final public GeoQuadric3D Sphere(String label, GeoPointND M, GeoPointND P) {
		AlgoSphereTwoPoints algo = new AlgoSphereTwoPoints(cons, label, M, P);
		return algo.getSphere();
	}

	/**
	 * Cone
	 */
	final public GeoQuadric3D Cone(String label, GeoPointND origin,
			GeoVectorND direction, NumberValue angle) {
		AlgoQuadric algo = new AlgoConeInfinitePointVectorNumber(cons, label,
				origin, direction, angle);
		return algo.getQuadric();
	}

	final public GeoQuadric3D Cone(String label, GeoPointND origin,
			GeoPointND secondPoint, NumberValue angle) {
		AlgoQuadric algo = new AlgoConeInfinitePointPointNumber(cons, label, origin,
				secondPoint, angle);
		return algo.getQuadric();
	}

	final public GeoQuadric3D Cone(String label, GeoPointND origin,
			GeoLineND axis, NumberValue angle) {
		AlgoConePointLineAngle algo = new AlgoConePointLineAngle(cons, label,
				origin, axis, angle);
		return algo.getQuadric();
	}

	final public GeoElement[] ConeLimited(String[] labels, GeoPointND origin,
			GeoPointND secondPoint, NumberValue r) {
		AlgoQuadricLimitedPointPointRadius algo = new AlgoQuadricLimitedPointPointRadiusCone(
				cons, labels, origin, secondPoint, r);
		return algo.getOutput();
	}
	

	final public GeoElement[] ConeLimited(String[] labels, GeoConicND bottom,
			NumberValue height){
		AlgoQuadricLimitedConicHeightCone algo = new AlgoQuadricLimitedConicHeightCone(
				cons, labels, bottom, height);
		algo.update();// ensure volume is correctly computed
		return algo.getOutput();
	}

	/**
	 * Cylinder
	 */
	final public GeoQuadric3D Cylinder(String label, GeoPointND origin,
			GeoVectorND direction, NumberValue r) {
		AlgoQuadric algo = new AlgoCylinderInfinitePointVectorNumber(cons, label,
				origin, direction, r);
		return algo.getQuadric();
	}

	final public GeoQuadric3D Cylinder(String label, GeoPointND origin,
			GeoPointND secondPoint, NumberValue r) {
		AlgoQuadric algo = new AlgoCylinderInfinitePointPointNumber(cons, label, origin,
				secondPoint, r);
		return algo.getQuadric();
	}

	final public GeoQuadric3D Cylinder(String label, GeoLineND axis,
			NumberValue r) {
		AlgoQuadric algo = new AlgoCylinderAxisRadius(cons, label, axis, r);
		return algo.getQuadric();
	}

	final public GeoElement[] CylinderLimited(String[] labels,
			GeoPointND origin, GeoPointND secondPoint, NumberValue r) {
		AlgoQuadricLimitedPointPointRadius algo = new AlgoQuadricLimitedPointPointRadiusCylinder(
				cons, labels, origin, secondPoint, r);
		algo.update();// ensure volume is correctly computed
		return algo.getOutput();
	}
	
	
	final public GeoElement[] CylinderLimited(String[] labels, GeoConicND bottom,
			NumberValue height){
		AlgoQuadricLimitedConicHeightCylinder algo = new AlgoQuadricLimitedConicHeightCylinder(
				cons, labels, bottom, height);
		algo.update();// ensure volume is correctly computed
		return algo.getOutput();
	}
	

	final public GeoQuadric3DPart QuadricSide(String label, GeoQuadricND quadric) {
		AlgoQuadric algo = new AlgoQuadricSide(cons, label,
				(GeoQuadric3DLimited) quadric);
		return (GeoQuadric3DPart) algo.getQuadric();
	}

	final public GeoConic3D QuadricBottom(String label, GeoQuadricND quadric) {
		AlgoQuadricEnd algo = new AlgoQuadricEndBottom(cons, label,
				(GeoQuadric3DLimited) quadric);
		return algo.getSection();
	}

	final public GeoConic3D QuadricTop(String label, GeoQuadricND quadric) {
		AlgoQuadricEnd algo = new AlgoQuadricEndTop(cons, label,
				(GeoQuadric3DLimited) quadric);
		return algo.getSection();
	}

	/**
	 * circle through points A, B, C
	 */
	final public GeoConic3D Circle3D(String label, GeoPointND A, GeoPointND B,
			GeoPointND C) {
		AlgoCircleThreePoints algo = new AlgoCircle3DThreePoints(cons, label,
				A, B, C);
		GeoConic3D circle = (GeoConic3D) algo.getCircle();
		// circle.setToSpecific();
		circle.update();
		kernel.notifyUpdate(circle);
		return circle;
	}

	public GeoConic3D Circle3D(String label, GeoLineND axis, GeoPointND A) {
		AlgoCircle3DAxisPoint algo = new AlgoCircle3DAxisPoint(cons, label,
				axis, A);
		GeoConic3D circle = algo.getCircle();
		// circle.setToSpecific();
		circle.update();
		kernel.notifyUpdate(circle);
		return circle;
	}

	public GeoConicND Circle3D(String label, GeoPointND A, NumberValue radius,
			GeoDirectionND axis) {

		AlgoCircle3DPointDirection algo = new AlgoCircle3DPointRadiusDirection(
				cons, label, A, radius, axis);
		GeoConic3D circle = algo.getCircle();
		// circle.setToSpecific();
		circle.update();
		kernel.notifyUpdate(circle);
		return circle;

	}

	public GeoConicND Circle3D(String label, GeoPointND A, GeoPointND B,
			GeoDirectionND axis) {

		AlgoCircle3DPointDirection algo = new AlgoCircle3DPointPointDirection(
				cons, label, A, B, axis);
		GeoConic3D circle = algo.getCircle();
		// circle.setToSpecific();
		circle.update();
		kernel.notifyUpdate(circle);
		return circle;

	}

	/**
	 * plane through points A, B, C
	 */
	final public GeoPlane3D Plane3D(String label, GeoPointND A, GeoPointND B,
			GeoPointND C) {
		AlgoPlaneThreePoints algo = new AlgoPlaneThreePoints(cons, label, A, B,
				C);
		GeoPlane3D plane = (GeoPlane3D) algo.getCoordSys();
		return plane;
	}

	final public GeoPlane3D Plane3D(String label, GeoCoordSys2D cs2D) {
		AlgoPlaneCS2D algo = new AlgoPlaneCS2D(cons, label, cs2D);
		GeoPlane3D plane = (GeoPlane3D) algo.getCoordSys();
		return plane;
	}

	final public GeoPlane3D Plane3D(GeoCoordSys2D cs2D) {
		AlgoPlaneCS2D algo = new AlgoPlaneCS2D(cons, cs2D);
		GeoPlane3D plane = (GeoPlane3D) algo.getCoordSys();
		return plane;
	}

	// //////////////////////////////////////////////
	// INTERSECTION (POINTS)

	/**
	 * Calculate the intersection of two coord sys (eg line and plane) or the
	 * intersection of two 2D coord sys (eg two planes).
	 * 
	 * @param label
	 *            name of the point
	 * @param cs1
	 *            first coord sys
	 * @param cs2
	 *            second coord sys
	 * @return point intersection
	 */
	final public GeoElement Intersect(String label, GeoElement cs1,
			GeoElement cs2) {

		AlgoIntersectCoordSys algo = null;

		if (cs1 instanceof GeoLineND) {
			if (cs2 instanceof GeoLineND)
				algo = new AlgoIntersectCS1D1D(cons, label, (GeoLineND) cs1,
						(GeoLineND) cs2);
			else if (cs2 instanceof GeoCoordSys2D)
				algo = new AlgoIntersectCS1D2D(cons, label, cs1, cs2);
		} else if (cs1 instanceof GeoCoordSys2D) {
			if (cs2 instanceof GeoLineND)
				algo = new AlgoIntersectCS1D2D(cons, label, cs1, cs2);
			// else
			// algo = new AlgoIntersectCS2D2D(cons,label, (GeoCoordSys2D) cs1,
			// (GeoCoordSys2D) cs2);
		}

		return algo.getIntersection();
	}

	public GeoElement[] IntersectionPoint(String[] labels, GeoLineND g,
			GeoSurfaceFinite p) {

		if (p instanceof GeoPolygon) {
			AlgoElement algo = new AlgoIntersectLinePolygon3D(cons, labels, g,
					(GeoPolygon) p);

			return algo.getOutput();
		}
		return null;
	}

	public GeoElement[] IntersectionPoint(String[] labels, GeoPlaneND plane,
			GeoElement s) {

		if (s instanceof GeoPolygon) {
			AlgoIntersectPlanePolygon algo = new AlgoIntersectPlanePolygon(
					cons, labels, (GeoPlane3D) plane, (GeoPolygon) s);
			return algo.getOutput();
		}
		
		if (s instanceof GeoPolyhedron) {
			AlgoIntersectPlanePolyhedron algo = new AlgoIntersectPlanePolyhedron(
					cons, labels, (GeoPlane3D) plane, (GeoPolyhedron) s);
			return algo.getOutput();
		}
		
		
		return null;
	}

	public GeoElement[] IntersectionSegment(String[] labels, GeoLineND g,
			GeoSurfaceFinite p) {

		AlgoIntersectLinePolygonalRegion3D algo;
		if (p instanceof GeoPolygon) {
			algo = new AlgoIntersectLinePolygonalRegion3D(cons, labels, g,
					(GeoPolygon) p);
			// Application.debug(algo);
			return algo.getOutput();
		}
		return null;

	}
	
	public GeoElement[] IntersectPath(String[] labels, GeoLineND g,
			GeoSurfaceFinite p) {

		AlgoIntersectPathLinePolygon3D algo;
		if (p instanceof GeoPolygon) {
			algo = new AlgoIntersectPathLinePolygon3D(cons, labels, (GeoElement) g,
					(GeoPolygon) p);
			// Application.debug(algo);
			return algo.getOutput();
		}
		return null;

	}
	
	public GeoElement[] IntersectPath(String[] labels, GeoPlaneND plane,
			GeoElement p) {

		
		if (p instanceof GeoPolygon) {
			AlgoIntersectPathPlanePolygon3D algo = new AlgoIntersectPathPlanePolygon3D(cons, labels, (GeoPlane3D) plane,
					(GeoPolygon) p);
			return algo.getOutput();
		}
		
		/*
		if (p instanceof GeoPolyhedron) {
			AlgoIntersectPathPlanePolyhedron algo = new AlgoIntersectPathPlanePolyhedron(cons, labels, (GeoPlane3D) plane,
					(GeoPolyhedron) p);
			return algo.getOutput();
		}
		*/
		
		return null;

	}
	
	
	public GeoElement[] IntersectRegion(String[] labels, GeoPlaneND plane,
			GeoElement p, int[] outputSizes) {

		
		if (p instanceof GeoPolyhedron) {
			AlgoIntersectRegionPlanePolyhedron algo = new AlgoIntersectRegionPlanePolyhedron(cons, labels, (GeoPlane3D) plane,
					(GeoPolyhedron) p, outputSizes);
			return algo.getOutput();
		}
		
		return null;

	}
	
	public GeoElement[] IntersectRegion(GeoPlaneND plane,
			GeoElement p) {

		
		if (p instanceof GeoPolyhedron) {
			AlgoIntersectRegionPlanePolyhedron algo = new AlgoIntersectRegionPlanePolyhedron(cons, (GeoPlane3D) plane,
					(GeoPolyhedron) p);
			algo.update();
			return algo.getOutput();
		}
		
		return null;

	}


	public GeoElement[] IntersectionSegment(String[] labels, GeoPlaneND plane,
			GeoSurfaceFinite s) {

		if (s instanceof GeoPolygon) {
			AlgoIntersectPlanePolygonalRegion algo = new AlgoIntersectPlanePolygonalRegion(
					cons, labels, (GeoPlane3D) plane, (GeoPolygon) s);
			return algo.getOutput();
		}
		return null;
	}

	public GeoConic3D Intersect(String label, GeoPlaneND plane,
			GeoQuadricND quadric) {

		AlgoIntersectPlaneQuadric algo = new AlgoIntersectPlaneQuadric(cons,
				label, (GeoPlane3D) plane, quadric);

		return algo.getConic();
	}
	
	public GeoConicND IntersectQuadricLimited(String label, GeoPlaneND plane,
			GeoQuadricND quadric){
		AlgoIntersectPlaneQuadric algo = new AlgoIntersectPlaneQuadricLimited(cons,
				label, (GeoPlane3D) plane, quadric);

		return algo.getConic();
	}

	public GeoConicND IntersectQuadricLimited(GeoPlaneND plane,
			GeoQuadricND quadric){
		AlgoIntersectPlaneQuadric algo = new AlgoIntersectPlaneQuadricLimited(cons,
				(GeoPlane3D) plane, quadric);

		return algo.getConic();
	}


	public GeoConic3D Intersect(GeoPlaneND plane, GeoQuadricND quadric) {

		AlgoIntersectPlaneQuadric algo = new AlgoIntersectPlaneQuadric(cons,
				(GeoPlane3D) plane, quadric);

		return algo.getConic();
	}
	
	
	public GeoElement[] IntersectAsCircle(String[] labels, GeoQuadricND quadric1,
			GeoQuadricND quadric2){

		AlgoIntersectQuadricsAsCircle algo = new AlgoIntersectQuadricsAsCircle(cons,
				labels, quadric1, quadric2);

		return algo.getOutput();
	}
	
	public GeoElement[] IntersectAsCircle(GeoQuadricND quadric1,
			GeoQuadricND quadric2){

		AlgoIntersectQuadricsAsCircle algo = new AlgoIntersectQuadricsAsCircle(cons,
				quadric1, quadric2);

		return algo.getOutput();
	}

	// //////////////////////////////////////////////
	// FUNCTIONS (2 VARS)

	final public GeoFunctionNVar Function2Var(String label, NumberValue zcoord,
			GeoNumeric localVarU, NumberValue Ufrom, NumberValue Uto,
			GeoNumeric localVarV, NumberValue Vfrom, NumberValue Vto) {

		AlgoFunctionNVarND algo = new AlgoFunctionNVarND(cons, label,
				new NumberValue[] { zcoord }, new GeoNumeric[] { localVarU,
						localVarV }, new NumberValue[] { Ufrom, Vfrom },
				new NumberValue[] { Uto, Vto });

		return algo.getFunction();

	}

	final public GeoFunctionNVar Function2Var(String label, GeoFunctionNVar f,
			NumberValue xFrom, NumberValue xTo, NumberValue yFrom,
			NumberValue yTo) {

		AlgoFunctionNVarND algo = new AlgoFunctionNVarND(cons, label, f,
				new NumberValue[] { xFrom, yFrom }, new NumberValue[] { xTo,
						yTo });

		return algo.getFunction();

	}

	// //////////////////////////////////////////////
	// 3D CURVE (1 VAR)

	/**
	 * 3D Cartesian curve command: Curve[ <expression x-coord>, <expression
	 * y-coord>, <expression z-coord>, <number-var>, <from>, <to> ]
	 */
	final public GeoCurveCartesian3D CurveCartesian3D(String label,
			NumberValue xcoord, NumberValue ycoord, NumberValue zcoord,
			GeoNumeric localVar, NumberValue from, NumberValue to) {
		AlgoCurveCartesian3D algo = new AlgoCurveCartesian3D(cons, label,
				new NumberValue[] { xcoord, ycoord, zcoord }, localVar, from,
				to);
		return (GeoCurveCartesian3D) algo.getCurve();
	}

	// //////////////////////////////////////////////
	// 3D SURFACE (2 VARS)

	public GeoElement SurfaceCartesian3D(String label, NumberValue xcoord,
			NumberValue ycoord, NumberValue zcoord, GeoNumeric uVar,
			NumberValue uFrom, NumberValue uTo, GeoNumeric vVar,
			NumberValue vFrom, NumberValue vTo) {

		AlgoSurfaceCartesian3D algo = new AlgoSurfaceCartesian3D(cons, label,
				new NumberValue[] { xcoord, ycoord, zcoord }, new GeoNumeric[] {
						uVar, vVar }, new NumberValue[] { uFrom, vFrom },
				new NumberValue[] { uTo, vTo });
		return algo.getSurface();

	}


	/********************************************************************
	 * POINT ALGOS (Intersection, ClosestPoint)
	 ********************************************************************/

	/**
	 * intersect line and conic
	 */
	private AlgoIntersectLineConic3D getIntersectionAlgorithm(GeoLineND g,
			GeoConicND c) {
		AlgoElement existingAlgo = kernel.getAlgoDispatcher().findExistingIntersectionAlgorithm(
				(GeoElement) g, c);
		if (existingAlgo != null)
			return (AlgoIntersectLineConic3D) existingAlgo;

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectLineConic3D algo = new AlgoIntersectLineConic3D(cons, g, c);
		algo.setPrintedInXML(false);
		kernel.getAlgoDispatcher().addIntersectionAlgorithm(algo); // remember this algorithm
		return algo;
	}

	/**
	 * IntersectLineConic yields intersection points named label1, label2 of
	 * line g and conic c
	 */
	final public GeoPoint3D[] IntersectLineConic(String[] labels, GeoLineND g,
			GeoConicND c) {
		AlgoIntersectLineConic3D algo = getIntersectionAlgorithm(g, c);
		algo.setPrintedInXML(true);
		GeoPoint3D[] points = algo.getIntersectionPoints();
		GeoElement.setLabels(labels, points);
		return points;
	}

	/**
	 * get only one intersection point of two conics that is near to the given
	 * location (xRW, yRW)
	 */
	final public GeoPoint3D IntersectLineConicSingle(String label, GeoLineND g,
			GeoConicND c, double xRW, double yRW, CoordMatrix mat) {

		AlgoIntersectLineConic3D algo = getIntersectionAlgorithm(g, c);

		int index = algo.getClosestPointIndex(xRW, yRW, mat);

		AlgoIntersectSingle3D salgo = new AlgoIntersectSingle3D(label, algo,
				index);
		GeoPoint3D point = salgo.getPoint();
		return point;
	}

	/**
	 * get only one intersection point of two conics choice depends on command
	 * input
	 */
	final public GeoPoint3D IntersectLineConicSingle(String label, GeoLineND g,
			GeoConicND c, NumberValue index) {

		return IntersectLineConicSingle(label, g, c, (int) index.getDouble() - 1);
	}
	
	/**
	 * get only one intersection point of two conics choice depends on command
	 * input
	 */
	final public GeoPoint3D IntersectLineConicSingle(String label, GeoLineND g,
			GeoConicND c, int index) {
		AlgoIntersectLineConic3D algo = getIntersectionAlgorithm(g, c); // index
																		// - 1
																		// to
																		// start
																		// at 0
		AlgoIntersectSingle3D salgo = new AlgoIntersectSingle3D(label, algo,
				index);
		GeoPoint3D point = salgo.getPoint();
		return point;
	}

	/**
	 * get only one intersection point of two conics, near to refPoint
	 */
	public GeoPoint3D IntersectLineConicSingle(String label, GeoLineND g,
			GeoConicND c, GeoPointND refPoint) {
		AlgoIntersectLineConic3D algo = getIntersectionAlgorithm(g, c); // index
																		// - 1
																		// to
																		// start
																		// at 0
		AlgoIntersectSingle3D salgo = new AlgoIntersectSingle3D(label, algo,
				refPoint);
		GeoPoint3D point = salgo.getPoint();
		return point;
	}

	/**
	 * intersect conics
	 */
	public AlgoIntersectConics3D getIntersectionAlgorithm(GeoConicND A,
			GeoConicND B) {
		AlgoElement existingAlgo = kernel.getAlgoDispatcher().findExistingIntersectionAlgorithm(A,
				B);
		if (existingAlgo != null)
			return (AlgoIntersectConics3D) existingAlgo;

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectConics3D algo = new AlgoIntersectConics3D(cons, A, B);
		algo.setPrintedInXML(false);
		kernel.getAlgoDispatcher().addIntersectionAlgorithm(algo); // remember this algorithm
		return algo;
	}

	/**
	 * IntersectConics3D yields intersection points named label1, label2 of
	 * conics A and B
	 */
	final public GeoPoint3D[] IntersectConics(String[] labels, GeoConicND A,
			GeoConicND B) {
		AlgoIntersectConics3D algo = getIntersectionAlgorithm(A, B);
		algo.setPrintedInXML(true);
		GeoPoint3D[] points = algo.getIntersectionPoints();
		GeoElement.setLabels(labels, points);
		return points;
	}

	final public GeoPoint3D IntersectConicsSingle(String label, GeoConicND A,
			GeoConicND B, double xRW, double yRW, CoordMatrix mat) {

		AlgoIntersectConics3D algo = getIntersectionAlgorithm(A, B);

		int index = algo.getClosestPointIndex(xRW, yRW, mat);
		AlgoIntersectSingle3D salgo = new AlgoIntersectSingle3D(label, algo,
				index);
		GeoPoint3D point = salgo.getPoint();
		return point;
	}

	final public GeoPoint3D IntersectConicsSingle(String label, GeoConicND A,
			GeoConicND B, NumberValue index) {
		return IntersectConicsSingle(label,A,B,(int) index.getDouble() - 1);
	}
	
	final public GeoPoint3D IntersectConicsSingle(String label, GeoConicND A,
			GeoConicND B, int index) {
		AlgoIntersectConics3D algo = getIntersectionAlgorithm(A, B); // index -
																		// 1 to
																		// start
																		// at 0
		AlgoIntersectSingle3D salgo = new AlgoIntersectSingle3D(label, algo,
				index);
		GeoPoint3D point = salgo.getPoint();
		return point;
	}	
	

	final public GeoPoint3D IntersectConicsSingle(String label, GeoConicND A,
			GeoConicND B, GeoPointND refPoint) {
		AlgoIntersectConics3D algo = getIntersectionAlgorithm(A, B); // index -
																		// 1 to
																		// start
																		// at 0
		AlgoIntersectSingle3D salgo = new AlgoIntersectSingle3D(label, algo,
				refPoint);
		GeoPoint3D point = salgo.getPoint();
		return point;
	}

	/**
	 * intersect line/quadric
	 */
	private AlgoIntersectLineQuadric3D getIntersectionAlgorithm(GeoLineND A,
			GeoQuadricND B) {
		AlgoElement existingAlgo = kernel.getAlgoDispatcher().findExistingIntersectionAlgorithm(
				(GeoElement) A, B);
		if (existingAlgo != null)
			return (AlgoIntersectLineQuadric3D) existingAlgo;

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectLineQuadric3D algo = new AlgoIntersectLineQuadric3D(cons,
				A, B);
		algo.setPrintedInXML(false);
		kernel.getAlgoDispatcher().addIntersectionAlgorithm(algo); // remember this algorithm
		return algo;
	}

	public GeoPointND[] IntersectLineQuadric(String[] labels, GeoLineND A,
			GeoQuadricND B) {
		AlgoIntersectLineQuadric3D algo = getIntersectionAlgorithm(A,
				B);
		algo.setPrintedInXML(true);
		GeoPoint3D[] points = algo.getIntersectionPoints();
		GeoElement.setLabels(labels, points);
		return points;
	}

	/**
	 * get only one intersection point of line and quadric choice depends on
	 * command input
	 */
	final public GeoPoint3D IntersectLineQuadricSingle(String label,
			GeoLineND g, GeoQuadricND q, NumberValue index) {

		return IntersectLineQuadricSingle(label, g, q, (int) index.getDouble() - 1);
	}
	
	/**
	 * get only one intersection point of line and quadric choice depends on
	 * command input
	 */
	final public GeoPoint3D IntersectLineQuadricSingle(String label,
			GeoLineND g, GeoQuadricND q, int index) {
		AlgoIntersectLineQuadric3D algo = getIntersectionAlgorithm(g, q); // index
																			// -
																			// 1
																			// to
																			// start
																			// at
																			// 0
		AlgoIntersectSingle3D salgo = new AlgoIntersectSingle3D(label, algo,
				index);
		GeoPoint3D point = salgo.getPoint();
		return point;
	}

	/**
	 * get only one intersection point of two conics that is near to the given
	 * location (xRW, yRW)
	 */
	final public GeoPoint3D IntersectLineQuadricSingle(String label,
			GeoLineND g, GeoQuadricND q, double xRW, double yRW,
			CoordMatrix4x4 mat) {

		AlgoIntersectLineQuadric3D algo = getIntersectionAlgorithm(g, q);

		int index = algo.getClosestPointIndex(xRW, yRW, mat);
		AlgoIntersectSingle3D salgo = new AlgoIntersectSingle3D(label, algo,
				index);
		GeoPoint3D point = salgo.getPoint();
		return point;
	}

	final public GeoPoint3D IntersectLineQuadricSingle(String label,
			GeoLineND g, GeoQuadricND q, GeoPointND refPoint) {
		AlgoIntersectLineQuadric3D algo = getIntersectionAlgorithm(g, q); // index
																			// -
																			// 1
																			// to
																			// start
																			// at
																			// 0
		AlgoIntersectSingle3D salgo = new AlgoIntersectSingle3D(label, algo,
				refPoint);
		GeoPoint3D point = salgo.getPoint();
		return point;
	}

	/**
	 * intersect plane/conic
	 */
	private AlgoIntersectPlaneConic getIntersectionAlgorithm(GeoCoordSys2D A,
			GeoConicND B) {
		AlgoElement existingAlgo = kernel.getAlgoDispatcher().findExistingIntersectionAlgorithm(
				(GeoElement) A, B);
		if (existingAlgo != null)
			return (AlgoIntersectPlaneConic) existingAlgo;

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectPlaneConic algo = new AlgoIntersectPlaneConic(cons, A, B);
		algo.setPrintedInXML(false);
		kernel.getAlgoDispatcher().addIntersectionAlgorithm(algo); // remember this algorithm
		return algo;
	}

	public GeoPointND[] IntersectPlaneConic(String[] labels, GeoCoordSys2D A,
			GeoConicND B) {
		AlgoIntersectPlaneConic algo = getIntersectionAlgorithm(A, B);
		algo.setPrintedInXML(true);
		GeoPoint3D[] points = algo.getIntersectionPoints();
		GeoElement.setLabels(labels, points);
		return points;
	}

	final public GeoElement IntersectPlanes(String label, GeoCoordSys2D cs1,
			GeoCoordSys2D cs2) {

		AlgoIntersectCS2D2D algo = new AlgoIntersectCS2D2D(cons, label, cs1,
				cs2);
		return algo.getIntersection();
	}

	final public GeoElement IntersectPlanes(GeoCoordSys2D cs1, GeoCoordSys2D cs2) {

		AlgoIntersectCS2D2D algo = new AlgoIntersectCS2D2D(cons, cs1, cs2);
		return algo.getIntersection();
	}
	
	public GeoElement ClosestPoint(String label, GeoLineND g, GeoLineND h) {
		AlgoClosestPointLines3D algo =  new AlgoClosestPointLines3D(cons, label, g, h);
		return algo.getPoint();
	}
	
	public GeoPoint3D ClosestPoint(String label, Path p, GeoPointND P) {
		AlgoClosestPoint3D algo =  new AlgoClosestPoint3D(cons, label, p, P);
		return (GeoPoint3D) algo.getP();
	}
	
	public GeoElement ClosestPoint(String label, Region r, GeoPointND P) {
		AlgoClosestPointToRegion3D algo =  new AlgoClosestPointToRegion3D(cons, label, r, P);
		return algo.getOutputPoint();
	}
	

	/********************************************************************
	 * MEASURES (lengths, angles)
	 ********************************************************************/

	/**
	 * Angle named label between three points
	 */
	final public GeoAngle Angle3D(String label, GeoPointND A, GeoPointND B,
			GeoPointND C) {
		AlgoAnglePoints algo = new AlgoAnglePoints3D(cons, label, A, B, C);
		GeoAngle angle = algo.getAngle();
		return angle;
	}

	/**
	 * Length named label of vector v
	 * 
	 * @param label
	 * @param v
	 * @return length of the vector
	 */
	final public GeoNumeric Length(String label, GeoVectorND v) {
		AlgoLengthVector3D algo = new AlgoLengthVector3D(cons, label, v);
		GeoNumeric num = algo.getLength();
		return num;
	}

	final public GeoElement[] ArchimedeanSolid(String[] labels, GeoPointND A,
			GeoPointND B, GeoDirectionND v, Commands name) {
		AlgoArchimedeanSolid algo = new AlgoArchimedeanSolid(cons, labels, A,
				B, v, name);
		return algo.getOutput();
	}

	public GeoNumeric Distance(String label, GeoLineND g, GeoLineND h) {
		
		AlgoDistanceLines3D algo = new AlgoDistanceLines3D(cons, label, g, h);
		
		return algo.getDistance();
	}

	
	/********************************************************************
	 * TRANSFORMATIONS
	 ********************************************************************/

	final public GeoElement[] Translate3D(String label, GeoElement geoTrans,
			GeoVectorND v) {
		Transform3D t = new TransformTranslate3D(cons, v);
		return t.transform(geoTrans, label);
	}

	public Geo3DVec newGeo3DVec(double x, double y, double z) {
		return new geogebra3D.kernel3D.Geo3DVec(kernel, x, y, z);
	}

	final public GeoElement[] Rotate3D(String label, GeoPointND geoRot,
			GeoNumberValue phi, GeoPointND center, GeoDirectionND orientation) {
		Transform t = new TransformRotate3D(cons, phi, center, orientation);
		return t.transform((GeoElement) geoRot, label);
	}

	final public GeoElement[] Rotate3D(String label, GeoPointND geoRot,
			GeoNumberValue phi, GeoLineND line) {
		Transform t = new TransformRotate3D(cons, phi, line);
		return t.transform((GeoElement) geoRot, label);
	}


	final public GeoNumeric Volume(String label, HasVolume hasVolume){
		AlgoVolume algo = new AlgoVolume(cons, label, hasVolume);
		return algo.getVolume();
	}

}
