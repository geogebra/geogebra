package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.Construction3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConicPart3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConicSection;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCurveCartesian3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DLimited;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DPart;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoRay3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSegment3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSpace;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import org.geogebra.common.geogebra3D.kernel3D.transform.Transform3D;
import org.geogebra.common.geogebra3D.kernel3D.transform.TransformDilate3D;
import org.geogebra.common.geogebra3D.kernel3D.transform.TransformMirror3D;
import org.geogebra.common.geogebra3D.kernel3D.transform.TransformRotate3D;
import org.geogebra.common.geogebra3D.kernel3D.transform.TransformTranslate3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Manager3DInterface;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.Transform;
import org.geogebra.common.kernel.Matrix.CoordMatrix;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoCircleThreePoints;
import org.geogebra.common.kernel.algos.AlgoDependentNumber;
import org.geogebra.common.kernel.algos.AlgoDependentPoint;
import org.geogebra.common.kernel.algos.AlgoDistancePoints;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoJoinPointsSegment;
import org.geogebra.common.kernel.algos.AlgoMidpoint;
import org.geogebra.common.kernel.algos.AlgoOrthoLinePointLine;
import org.geogebra.common.kernel.algos.AlgoPolygon;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic3D.MyVec3DNode;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoSurfaceFinite;
import org.geogebra.common.kernel.kernelND.Geo3DVec;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicPartND;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.kernelND.HasHeight;
import org.geogebra.common.kernel.kernelND.HasVolume;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;

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
	final public GeoPoint3D Point3D(String label, double x, double y, double z,
			boolean coords2D) {
		GeoPoint3D p = new GeoPoint3D(cons);
		if (coords2D)
			p.setCartesian();
		else
			p.setCartesian3D();
		p.setCoords(x, y, z, 1.0);
		p.setLabel(label); // invokes add()

		return p;
	}

	final public GeoPoint3D Point3D(double x, double y, double z,
			boolean coords2D) {
		GeoPoint3D p = new GeoPoint3D(cons);
		if (coords2D)
			p.setCartesian();
		else
			p.setCartesian3D();
		p.setCoords(x, y, z, 1.0);

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
	final public GeoPoint3D Point3DIn(Region region, Coords coords,
			boolean coords2D) {
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
	final public GeoPoint3D Point3DIn(String label, Region region,
			boolean coords2D) {
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
		GeoPoint3D p = (GeoPoint3D) algo.getP();
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

	public GeoPointND Point3D(String label, Path path, NumberValue param) {

		// try (0,0,0)
		AlgoPoint3DOnPath algo = null;
		if (param == null) {
			algo = new AlgoPoint3DOnPath(cons, label, path, 0, 0, 0);
		} else {
			algo = new AlgoPoint3DOnPath(cons, label, path, 0, 0, 0, param);
		}
		GeoPoint3D p = (GeoPoint3D) algo.getP();

		// try (1,0,0)
		if (!p.isDefined()) {
			p.setCoords(1, 0, 0, 1);
			algo.update();
		}

		// try (random(),0, 0)
		if (!p.isDefined()) {
			p.setCoords(Math.random(), 0, 0, 1);
			algo.update();
		}

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

	public GeoPointND Center(String label, GeoConicND conic) {

		AlgoCenterConic3D algo = new AlgoCenterConic3D(cons, label, conic);
		return algo.getPoint();

	}

	public GeoPointND CenterQuadric(String label, GeoQuadricND quadric) {
		AlgoCenterQuadric algo = new AlgoCenterQuadric(cons, label,
				(GeoQuadric3D) quadric);
		return algo.getPoint();

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

		// when have space as direction, just to say it's not as in 2D
		if (line instanceof GeoLineND && direction instanceof GeoSpace)
			return OrthogonalLine3D(label, point, (GeoLineND) line);

		// when using Locus (via macro) or xOy plane as direction, check if it's
		// only 2D objects, then return 2D line
		if ((!(cons instanceof Construction3D) || direction == ((Construction3D) cons)
				.getXOYPlane())
				&& (point instanceof GeoPoint)
				&& (line instanceof GeoLine)) {
			AlgoOrthoLinePointLine algo = new AlgoOrthoLinePointLine(cons,
					label, (GeoPoint) point, (GeoLine) line);
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

	public GeoVectorND OrthogonalVector3D(String label, GeoLineND line,
			GeoDirectionND direction) {
		AlgoOrthoVectorLineDirection algo = new AlgoOrthoVectorLineDirection(
				cons, label, line, direction);
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
	 * @param labels
	 *            names
	 * @param points
	 *            vertices
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
	 * @param labels
	 *            names
	 * @param points
	 *            vertices
	 * @return the polyhedron
	 */
	final public GeoElement[] Pyramid(String[] labels, GeoPointND[] points) {

		AlgoPolyhedronPointsPyramid algo = new AlgoPolyhedronPointsPyramid(
				cons, labels, points);

		return algo.getOutput();

	}

	final public GeoElement[] Pyramid(String[] labels, GeoPolygon polygon,
			GeoPointND point) {

		AlgoPolyhedronPointsPyramid algo = new AlgoPolyhedronPointsPyramid(
				cons, labels, polygon, point);

		return algo.getOutput();

	}

	final public GeoElement[] Pyramid(String[] labels, GeoPolygon polygon,
			NumberValue height) {
		App.debug("pyramid");
		AlgoPolyhedronPointsPyramid algo = new AlgoPolyhedronPointsPyramid(
				cons, labels, polygon, height);

		return algo.getOutput();
	}

	final public GeoPlane3D Plane3D(String label, double a, double b, double c,
			double d) {
		GeoPlane3D plane = new GeoPlane3D(cons, label, a, b, c, d);
		return plane;
	}

	final public GeoPlane3D DependentPlane3D(String label, Equation equ) {
		AlgoDependentPlane3D algo = new AlgoDependentPlane3D(cons, label, equ);
		return algo.getPlane();
	}

	final public GeoQuadric3D DependentQuadric3D(String label, Equation equ) {
		AlgoDependentQuadric3D algo = new AlgoDependentQuadric3D(cons, label,
				equ);
		return algo.getQuadric();
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
		AlgoQuadric algo = new AlgoConeInfinitePointPointNumber(cons, label,
				origin, secondPoint, angle);
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
			NumberValue height) {
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
		AlgoQuadric algo = new AlgoCylinderInfinitePointVectorNumber(cons,
				label, origin, direction, r);
		return algo.getQuadric();
	}

	final public GeoQuadric3D Cylinder(String label, GeoPointND origin,
			GeoPointND secondPoint, NumberValue r) {
		AlgoQuadric algo = new AlgoCylinderInfinitePointPointNumber(cons,
				label, origin, secondPoint, r);
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

	final public GeoElement[] CylinderLimited(String[] labels,
			GeoConicND bottom, NumberValue height) {
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

		if (!A.isGeoElement3D() && axis == kernel.getXOYPlane()) {
			return kernel.getAlgoDispatcher().Circle(label, (GeoPoint) A,
					radius);
		}

		AlgoCircle3DPointDirection algo = new AlgoCircle3DPointRadiusDirection(
				cons, label, A, radius, axis);
		GeoConic3D circle = algo.getCircle();
		// circle.setToSpecific();
		circle.update();
		kernel.notifyUpdate(circle);
		return circle;

	}

	public GeoConicND Circle3D(String label, GeoPointND A, NumberValue radius) {
		return Circle3D(label, A, radius, kernel.getXOYPlane());
	}

	public GeoConicND Circle3D(String label, GeoPointND A, GeoPointND B,
			GeoDirectionND orientation) {

		if (!((GeoElement) A).isGeoElement3D()
				&& !((GeoElement) B).isGeoElement3D() // 2D geos
				&& orientation == kernel.getXOYPlane()) { // xOy plane is
															// default
															// orientation for
															// 2D objects
			return kernel.getAlgoDispatcher().Circle(label, (GeoPoint) A,
					(GeoPoint) B);
		}

		// at least one 3D geo or specific orientation
		AlgoCircle3DPointDirection algo = new AlgoCircle3DPointPointDirection(
				cons, label, A, B, orientation);
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

	public GeoElement Plane3D(String label, GeoLineND a, GeoLineND b) {
		AlgoPlaneTwoLines algo = new AlgoPlaneTwoLines(cons, label, a, b);
		return (GeoPlane3D) algo.getCoordSys();
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

		if (s.isGeoPolyhedron()) {
			AlgoIntersectPlanePolyhedron algo = new AlgoIntersectPlanePolyhedron(
					cons, labels, (GeoPlane3D) plane, (GeoPolyhedron) s);
			return algo.getOutput();
		}

		return null;
	}



	public GeoElement[] IntersectPath(String[] labels, GeoLineND g,
			GeoSurfaceFinite p) {

		AlgoIntersectPathLinePolygon3D algo;
		if (p instanceof GeoPolygon) {
			algo = new AlgoIntersectPathLinePolygon3D(cons, labels,
					(GeoElement) g, (GeoPolygon) p);
			// Application.debug(algo);
			return algo.getOutput();
		}
		return null;

	}

	public GeoElement[] IntersectPath(String[] labels, GeoPlaneND plane,
			GeoElement p) {

		if (p instanceof GeoPolygon) {
			AlgoIntersectPathPlanePolygon3D algo = new AlgoIntersectPathPlanePolygon3D(
					cons, labels, (GeoPlane3D) plane, (GeoPolygon) p);
			return algo.getOutput();
		}

		return null;

	}

	public GeoElement[] IntersectPath(GeoPlaneND plane, GeoPolygon p) {

		AlgoIntersectPathPlanePolygon3D algo = new AlgoIntersectPathPlanePolygon3D(
				cons, (GeoPlane3D) plane, p);
		algo.update();
		return algo.getOutput();

	}

	public GeoElement[] IntersectRegion(String[] labels, GeoPlaneND plane,
			GeoElement p, int[] outputSizes) {

		if (p.isGeoPolyhedron()) {
			AlgoIntersectRegionPlanePolyhedron algo = new AlgoIntersectRegionPlanePolyhedron(
					cons, labels, (GeoPlane3D) plane, (GeoPolyhedron) p,
					outputSizes);
			return algo.getOutput();
		}

		return null;

	}

	public GeoElement[] IntersectRegion(GeoPlaneND plane, GeoElement p) {

		if (p.isGeoPolyhedron()) {
			AlgoIntersectRegionPlanePolyhedron algo = new AlgoIntersectRegionPlanePolyhedron(
					cons, (GeoPlane3D) plane, (GeoPolyhedron) p);
			algo.update();
			return algo.getOutput();
		}

		return null;

	}

	public GeoConic3D Intersect(String label, GeoPlaneND plane,
			GeoQuadricND quadric) {

		if (quadric instanceof GeoQuadric3DPart) {
			AlgoIntersectPlaneQuadricPart algo = new AlgoIntersectPlaneQuadricPart(
					cons, label, (GeoPlane3D) plane, quadric);
			return algo.getConic();
		}

		AlgoIntersectPlaneQuadric algo = new AlgoIntersectPlaneQuadric(cons,
				label, (GeoPlane3D) plane, quadric);

		return algo.getConic();
	}

	public GeoConicND IntersectQuadricLimited(String label, GeoPlaneND plane,
			GeoQuadricND quadric) {

		AlgoIntersectPlaneQuadric algo = new AlgoIntersectPlaneQuadricLimited(
				cons, label, (GeoPlane3D) plane, quadric);

		return algo.getConic();
	}

	public GeoConicND IntersectQuadricLimited(GeoPlaneND plane,
			GeoQuadricND quadric) {
		AlgoIntersectPlaneQuadric algo = new AlgoIntersectPlaneQuadricLimited(
				cons, (GeoPlane3D) plane, quadric);

		return algo.getConic();
	}

	public GeoConic3D Intersect(GeoPlaneND plane, GeoQuadricND quadric) {

		AlgoIntersectPlaneQuadric algo = new AlgoIntersectPlaneQuadric(cons,
				(GeoPlane3D) plane, quadric);

		return algo.getConic();
	}

	public GeoElement[] IntersectAsCircle(String[] labels,
			GeoQuadricND quadric1, GeoQuadricND quadric2) {

		AlgoIntersectQuadricsAsCircle algo = new AlgoIntersectQuadricsAsCircle(
				cons, labels, quadric1, quadric2);

		return algo.getOutput();
	}

	public GeoElement[] IntersectAsCircle(GeoQuadricND quadric1,
			GeoQuadricND quadric2) {

		AlgoIntersectQuadricsAsCircle algo = new AlgoIntersectQuadricsAsCircle(
				cons, quadric1, quadric2);

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
	final public GeoCurveCartesian3D CurveCartesian3D(NumberValue xcoord,
			NumberValue ycoord, NumberValue zcoord,
			GeoNumeric localVar, NumberValue from, NumberValue to) {
		AlgoCurveCartesian3D algo = new AlgoCurveCartesian3D(cons,
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
		AlgoElement existingAlgo = kernel.getAlgoDispatcher()
				.findExistingIntersectionAlgorithm((GeoElement) g, c);
		if (existingAlgo != null)
			return (AlgoIntersectLineConic3D) existingAlgo;

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectLineConic3D algo = new AlgoIntersectLineConic3D(cons, g, c);
		algo.setPrintedInXML(false);
		kernel.getAlgoDispatcher().addIntersectionAlgorithm(algo); // remember
																	// this
																	// algorithm
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

		return IntersectLineConicSingle(label, g, c,
				(int) index.getDouble() - 1);
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
		AlgoElement existingAlgo = kernel.getAlgoDispatcher()
				.findExistingIntersectionAlgorithm(A, B);
		if (existingAlgo != null)
			return (AlgoIntersectConics3D) existingAlgo;

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectConics3D algo = new AlgoIntersectConics3D(cons, A, B);
		algo.setPrintedInXML(false);
		kernel.getAlgoDispatcher().addIntersectionAlgorithm(algo); // remember
																	// this
																	// algorithm
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
		return IntersectConicsSingle(label, A, B, (int) index.getDouble() - 1);
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
		AlgoElement existingAlgo = kernel.getAlgoDispatcher()
				.findExistingIntersectionAlgorithm((GeoElement) A, B);
		if (existingAlgo != null)
			return (AlgoIntersectLineQuadric3D) existingAlgo;

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectLineQuadric3D algo = new AlgoIntersectLineQuadric3D(cons,
				A, B);
		algo.setPrintedInXML(false);
		kernel.getAlgoDispatcher().addIntersectionAlgorithm(algo); // remember
																	// this
																	// algorithm
		return algo;
	}

	public GeoPointND[] IntersectLineQuadric(String[] labels, GeoLineND A,
			GeoQuadricND B) {
		AlgoIntersectLineQuadric3D algo = getIntersectionAlgorithm(A, B);
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

		return IntersectLineQuadricSingle(label, g, q,
				(int) index.getDouble() - 1);
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
		AlgoElement existingAlgo = kernel.getAlgoDispatcher()
				.findExistingIntersectionAlgorithm((GeoElement) A, B);
		if (existingAlgo != null)
			return (AlgoIntersectPlaneConic) existingAlgo;

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectPlaneConic algo = new AlgoIntersectPlaneConic(cons, A, B);
		algo.setPrintedInXML(false);
		kernel.getAlgoDispatcher().addIntersectionAlgorithm(algo); // remember
																	// this
																	// algorithm
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

	final public GeoElement IntersectPlanes(String label, GeoPlaneND cs1,
			GeoPlaneND cs2) {

		AlgoIntersectPlanes algo = new AlgoIntersectPlanes(cons, label, cs1,
				cs2);
		return algo.getIntersection();
	}

	final public GeoElement IntersectPlanes(GeoPlaneND cs1, GeoPlaneND cs2) {

		AlgoIntersectPlanes algo = new AlgoIntersectPlanes(cons, cs1, cs2);
		return algo.getIntersection();
	}

	public GeoElement ClosestPoint(String label, GeoLineND g, GeoLineND h) {
		AlgoClosestPointLines3D algo = new AlgoClosestPointLines3D(cons, label,
				g, h);
		return algo.getPoint();
	}

	public GeoPoint3D ClosestPoint(String label, Path p, GeoPointND P) {
		AlgoClosestPoint3D algo = new AlgoClosestPoint3D(cons, label, p, P);
		return (GeoPoint3D) algo.getP();
	}

	public GeoPointND ClosestPoint(String label, Region r, GeoPointND P) {
		AlgoClosestPointToRegion3D algo = new AlgoClosestPointToRegion3D(cons,
				label, r, P);
		return algo.getOutputPoint();
	}

	/********************************************************************
	 * MEASURES (lengths, angles)
	 ********************************************************************/

	final public GeoAngle Angle3D(String label, GeoPointND A, GeoPointND B,
			GeoPointND C) {
		AlgoAnglePoints3D algo = new AlgoAnglePoints3D(cons, label, A, B, C);
		GeoAngle angle = algo.getAngle();
		return angle;
	}

	final public GeoAngle Angle3D(String label, GeoPointND A, GeoPointND B,
			GeoPointND C, GeoDirectionND orientation) {
		AlgoAnglePoints3DOrientation algo = new AlgoAnglePoints3DOrientation(
				cons, label, A, B, C, orientation);
		GeoAngle angle = algo.getAngle();
		return angle;
	}

	final public GeoElement[] Angle(String[] labels, GeoPointND B,
			GeoPointND A, GeoNumberValue alpha, GeoDirectionND orientation,
			boolean posOrientation) {
		// this is actually a macro
		String pointLabel = null, angleLabel = null;
		if (labels != null) {
			switch (labels.length) {
			case 2:
				pointLabel = labels[1];

			case 1:
				angleLabel = labels[0];

			default:
			}
		}

		// rotate B around A using angle alpha
		GeoPointND C = (GeoPointND) Rotate3D(pointLabel, (GeoElement) B, alpha,
				A, orientation)[0];

		// create angle according to orientation
		GeoAngle angle;
		if (posOrientation) {
			angle = Angle3D(angleLabel, B, A, C, orientation);
		} else {
			angle = Angle3D(angleLabel, C, A, B, orientation);
		}

		// return angle and new point
		GeoElement[] ret = { angle, (GeoElement) C };
		return ret;
	}

	final public GeoAngle Angle3D(String label, GeoLineND g, GeoLineND h) {
		AlgoAngleLines3D algo = new AlgoAngleLines3D(cons, label, g, h);
		GeoAngle angle = algo.getAngle();
		return angle;
	}

	final public GeoAngle Angle3D(String label, GeoLineND g, GeoLineND h,
			GeoDirectionND orientation) {
		AlgoAngleLines3D algo = new AlgoAngleLines3DOrientation(cons, label, g,
				h, orientation);
		GeoAngle angle = algo.getAngle();
		return angle;
	}

	final public GeoAngle Angle3D(String label, GeoPlaneND p1, GeoPlaneND p2) {
		AlgoAnglePlanes algo = new AlgoAnglePlanes(cons, label,
				(GeoPlane3D) p1, (GeoPlane3D) p2);
		GeoAngle angle = algo.getAngle();
		return angle;
	}

	final public GeoAngle Angle3D(String label, GeoLineND l, GeoPlaneND p) {
		AlgoAngleLinePlane algo = new AlgoAngleLinePlane(cons, label, l,
				(GeoPlane3D) p);
		GeoAngle angle = algo.getAngle();
		return angle;
	}

	public GeoAngle createLineAngle(GeoLineND line1, GeoLineND line2) {
		GeoAngle angle = null;

		// did we get two segments?
		if ((line1 instanceof GeoSegmentND) && (line2 instanceof GeoSegmentND)) {
			// check if the segments have one point in common
			GeoSegmentND a = (GeoSegmentND) line1;
			GeoSegmentND b = (GeoSegmentND) line2;
			// get endpoints
			GeoPointND a1 = a.getStartPoint();
			GeoPointND a2 = a.getEndPoint();
			GeoPointND b1 = b.getStartPoint();
			GeoPointND b2 = b.getEndPoint();

			if (a1 == b1) {
				angle = Angle3D(null, a2, a1, b2);
			} else if (a1 == b2) {
				angle = Angle3D(null, a2, a1, b1);
			} else if (a2 == b1) {
				angle = Angle3D(null, a1, a2, b2);
			} else if (a2 == b2) {
				angle = Angle3D(null, a1, a2, b1);
			}
		}

		if (angle == null) {
			angle = Angle3D(null, line1, line2);
		}

		return angle;
	}

	public GeoAngle createLineAngle(GeoLineND line1, GeoLineND line2,
			GeoDirectionND orientation) {
		GeoAngle angle = null;

		// did we get two segments?
		if ((line1 instanceof GeoSegmentND) && (line2 instanceof GeoSegmentND)) {
			// check if the segments have one point in common
			GeoSegmentND a = (GeoSegmentND) line1;
			GeoSegmentND b = (GeoSegmentND) line2;
			// get endpoints
			GeoPointND a1 = a.getStartPoint();
			GeoPointND a2 = a.getEndPoint();
			GeoPointND b1 = b.getStartPoint();
			GeoPointND b2 = b.getEndPoint();

			if (a1 == b1) {
				angle = Angle3D(null, a2, a1, b2, orientation);
			} else if (a1 == b2) {
				angle = Angle3D(null, a2, a1, b1, orientation);
			} else if (a2 == b1) {
				angle = Angle3D(null, a1, a2, b2, orientation);
			} else if (a2 == b2) {
				angle = Angle3D(null, a1, a2, b1, orientation);
			}
		}

		if (angle == null) {
			angle = Angle3D(null, line1, line2, orientation);
		}

		return angle;
	}

	final public GeoAngle Angle3D(String label, GeoVectorND v, GeoVectorND w) {
		AlgoAngleVectors3D algo = new AlgoAngleVectors3D(cons, label, v, w);
		GeoAngle angle = algo.getAngle();
		return angle;
	}

	final public GeoAngle Angle3D(String label, GeoVectorND v, GeoVectorND w,
			GeoDirectionND orientation) {
		AlgoAngleVectors3D algo = new AlgoAngleVectors3DOrientation(cons,
				label, v, w, orientation);
		GeoAngle angle = algo.getAngle();
		return angle;
	}

	final public GeoElement[] Angles3D(String[] labels, GeoPolygon poly) {
		AlgoAnglePolygon3D algo = new AlgoAnglePolygon3D(cons, labels, poly);
		GeoElement[] angles = algo.getAngles();
		return angles;
	}

	final public GeoElement[] Angles3D(String[] labels, GeoPolygon poly,
			GeoDirectionND orientation) {
		AlgoAnglePolygon3D algo = new AlgoAnglePolygon3DOrientation(cons,
				labels, poly, orientation);
		GeoElement[] angles = algo.getAngles();
		return angles;
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

	final public GeoElement[] ArchimedeanSolid(String[] labels, GeoPointND A,
			GeoPointND B, GeoPointND C, Commands name) {
		AlgoArchimedeanSolidThreePoints algo = new AlgoArchimedeanSolidThreePoints(
				cons, labels, A, B, C, name);
		return algo.getOutput();
	}

	final public GeoElement[] ArchimedeanSolid(String[] labels, GeoPointND A,
			GeoPointND B, Commands name) {

		// create segment A, B
		GeoSegmentND segAB;
		if (A.isGeoElement3D() || B.isGeoElement3D()) {
			AlgoJoinPoints3D algoSegment = new AlgoJoinPoints3D(cons, A, B,
					null, GeoClass.SEGMENT3D);
			cons.removeFromConstructionList(algoSegment);
			segAB = (GeoSegmentND) algoSegment.getCS();
		} else {
			AlgoJoinPointsSegment algoSegment = new AlgoJoinPointsSegment(cons,
					(GeoPoint) A, (GeoPoint) B, null);
			cons.removeFromConstructionList(algoSegment);
			segAB = algoSegment.getSegment();
		}

		// create distance AB
		AlgoDistancePoints algoDistance = new AlgoDistancePoints(cons, A, B);
		cons.removeFromConstructionList(algoDistance);
		GeoNumeric distance = algoDistance.getDistance();

		// create center and radius
		GeoPointND center;
		GeoNumberValue radius;
		switch (name) {
		case Tetrahedron:
		case Octahedron:
		case Icosahedron:
			// center = (A+B)/2
			if (A.isGeoElement3D() || B.isGeoElement3D()) {
				AlgoMidpoint3D algoMidpoint = new AlgoMidpoint3D(cons, A, B);
				cons.removeFromConstructionList(algoMidpoint);
				center = algoMidpoint.getPoint();
			} else {
				AlgoMidpoint algoMidpoint = new AlgoMidpoint(cons,
						(GeoPoint) A, (GeoPoint) B);
				cons.removeFromConstructionList(algoMidpoint);
				center = algoMidpoint.getPoint();
			}

			// radius = distance * sqrt(3)/2
			ExpressionNode expr = new ExpressionNode(kernel, new MyDouble(
					kernel, 3), Operation.SQRT, null);
			expr = expr.divide(2).multiply(distance);
			AlgoDependentNumber exprAlgo = new AlgoDependentNumber(cons, expr,
					false);
			cons.removeFromConstructionList(exprAlgo);
			radius = exprAlgo.getNumber();
			break;

		case Cube:
		default:
			center = B;
			radius = distance;
			break;

		case Dodecahedron:
			// center = ((1-Math.sqrt(5)) * A + (3+Math.sqrt(5)) * B)/4
			ExpressionNode exprSqrt5 = new ExpressionNode(kernel, new MyDouble(
					kernel, 5), Operation.SQRT, null);

			expr = new ExpressionNode(kernel, new MyDouble(kernel, 1),
					Operation.NO_OPERATION, null);
			ExpressionNode exprPoint = new ExpressionNode(kernel, A,
					Operation.NO_OPERATION, null);
			expr = exprPoint.multiply(expr.subtract(exprSqrt5));

			ExpressionNode expr2 = new ExpressionNode(kernel, new MyDouble(
					kernel, 3), Operation.NO_OPERATION, null);
			exprPoint = new ExpressionNode(kernel, B, Operation.NO_OPERATION,
					null);
			expr2 = exprPoint.multiply(expr2.plus(exprSqrt5));

			expr = expr.plus(expr2).divide(4);

			if (A.isGeoElement3D() || B.isGeoElement3D()) {
				AlgoDependentPoint3D exprAlgoPoint = new AlgoDependentPoint3D(
						cons, expr);
				cons.removeFromConstructionList(exprAlgoPoint);
				center = exprAlgoPoint.getPoint3D();
			} else {
				AlgoDependentPoint exprAlgoPoint = new AlgoDependentPoint(cons,
						expr, false);
				cons.removeFromConstructionList(exprAlgoPoint);
				center = exprAlgoPoint.getPoint();
			}

			// radius = distance * sqrt(10 + 2 * sqrt(5))/4)
			expr = new ExpressionNode(kernel, new MyDouble(kernel, 10),
					Operation.NO_OPERATION, null);
			expr2 = new ExpressionNode(kernel, new MyDouble(kernel, 2),
					Operation.NO_OPERATION, null);
			expr = expr.plus(exprSqrt5.multiply(expr2)).sqrt().divide(4)
					.multiply(distance);
			exprAlgo = new AlgoDependentNumber(cons, expr, false);
			cons.removeFromConstructionList(exprAlgo);
			radius = exprAlgo.getNumber();
			break;
		}

		// create a circle around center with radius
		AlgoCircle3DPointRadiusDirection algoCircle = new AlgoCircle3DPointRadiusDirection(
				cons, center, radius, segAB);
		cons.removeFromConstructionList(algoCircle);

		// place the new point on the circle
		Coords cA = A.getInhomCoordsInD3();
		Coords cB = B.getInhomCoordsInD(3);
		Coords AB = cB.sub(cA);
		Coords vn = new Coords(4);
		AB.completeOrthonormalKeepInXOYPlaneIfPossible(vn);
		Coords coords = center.getInhomCoordsInD(3).add(
				vn.mul(radius.getDouble()));

		AlgoPoint3DOnPath algoPoint = new AlgoPoint3DOnPath(cons, null,
				algoCircle.getCircle(), coords.getX(), coords.getY(),
				coords.getZ());

		// create solid
		AlgoArchimedeanSolidThreePoints algo = new AlgoArchimedeanSolidThreePoints(
				cons, labels, A, B, algoPoint.getP(), name);

		return algo.getOutput();

	}

	public GeoNumeric Distance(String label, GeoLineND g, GeoLineND h) {

		AlgoDistanceLines3D algo = new AlgoDistanceLines3D(cons, label, g, h);

		return algo.getDistance();
	}

	public GeoNumeric Distance(String label, GeoPointND point, GeoPlaneND plane) {

		AlgoDistancePointPlane3D algo = new AlgoDistancePointPlane3D(cons,
				label, point, plane);

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
		return new org.geogebra.common.geogebra3D.kernel3D.geos.Geo3DVec(kernel, x,
				y, z);
	}

	final public GeoElement[] Rotate3D(String label, GeoElement geoRot,
			GeoNumberValue phi, GeoPointND center, GeoDirectionND orientation) {
		Transform t = new TransformRotate3D(cons, phi, center, orientation);
		return t.transform(geoRot, label);
	}

	final public GeoElement[] Rotate3D(String label, GeoElement geoRot,
			GeoNumberValue phi, GeoLineND line) {
		Transform t = new TransformRotate3D(cons, phi, line);
		return t.transform(geoRot, label);
	}

	final public GeoElement[] Mirror3D(String label, GeoElement geo,
			GeoPointND p) {
		Transform t = new TransformMirror3D(cons, p);
		return t.transform(geo, label);
	}

	final public GeoElement[] Mirror3D(String label, GeoElement geo,
			GeoLineND line) {
		Transform t = new TransformMirror3D(cons, line);
		return t.transform(geo, label);
	}

	final public GeoElement[] Mirror3D(String label, GeoElement geo,
			GeoCoordSys2D plane) {
		Transform t = new TransformMirror3D(cons, plane);
		return t.transform(geo, label);
	}

	final public GeoElement[] Dilate3D(String label, GeoElement geoDil,
			NumberValue r, GeoPointND S) {

		Transform t = new TransformDilate3D(cons, r, S);
		return t.transform(geoDil, label);

	}

	final public GeoNumeric Volume(String label, HasVolume hasVolume) {
		AlgoVolume algo = new AlgoVolume(cons, label, hasVolume);
		return algo.getVolume();
	}

	final public GeoNumeric OrientedHeight(String label, HasHeight hasHeight) {
		AlgoOrientedHeight algo = new AlgoOrientedHeight(cons, label, hasHeight);
		return algo.getOrientedHeight();
	}

	final public GeoPoint3D[] Corner(String[] labels, GeoConicND conic) {
		AlgoCornerConicSection algo = new AlgoCornerConicSection(cons, labels,
				(GeoConicSection) conic);
		return algo.getCorners();
	}

	final public GeoElement[] RegularPolygon(String[] labels, GeoPointND A,
			GeoPointND B, NumberValue n, GeoDirectionND direction) {
		AlgoPolygonRegular3D algo = new AlgoPolygonRegular3D(cons, labels, A,
				B, n, direction);
		return algo.getOutput();
	}

	public GeoElement[] PolyhedronNet(String[] labels, GeoElement p,
			NumberValue v, GeoPolygon bottomFace, GeoSegmentND[] pivotSegments) {

		AlgoElement algo;

		/*
		 * TODO comment this, uncomment below (when cutting edges work for
		 * pyramid and prism
		 */
		switch (((GeoPolyhedron) p).getType()) {

		case GeoPolyhedron.TYPE_PYRAMID:
			algo = new AlgoPolyhedronNetPyramid(cons, labels,
					(GeoPolyhedron) p, v);
			return algo.getOutput();

		case GeoPolyhedron.TYPE_PRISM:
			algo = new AlgoPolyhedronNetPrism(cons, labels, (GeoPolyhedron) p,
					v);
			return algo.getOutput();
		default:
			algo = new AlgoPolyhedronNetConvex(cons, labels, (GeoPolyhedron) p,
					v, bottomFace, pivotSegments);
			return algo.getOutput();
		}
		/**/

		/*
		 * if (bottomFace == null && pivotSegments == null){
		 * switch(((GeoPolyhedron) p).getType()) {
		 * 
		 * case GeoPolyhedron.TYPE_PYRAMID: algo = new
		 * AlgoPolyhedronNetPyramid(cons, labels, (GeoPolyhedron) p, v); return
		 * algo.getOutput();
		 * 
		 * case GeoPolyhedron.TYPE_PRISM: algo = new
		 * AlgoPolyhedronNetPrism(cons, labels, (GeoPolyhedron) p, v); return
		 * algo.getOutput(); } }
		 * 
		 * algo = new AlgoPolyhedronNetConvex(cons, labels, (GeoPolyhedron) p,
		 * v, bottomFace, pivotSegments); return algo.getOutput();
		 */

	}

	public GeoElement[] PolyhedronConvex(String[] labels, GeoElement[] pointList) {
		AlgoElement algo = new AlgoPolyhedronConvex(cons, labels, pointList);
		return algo.getOutput();
	}

	/**
	 * circle arc from three points
	 */
	final public GeoConicPart3D CircumcircleArc3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C) {
		AlgoConicPartCircumcircle3D algo = new AlgoConicPartCircumcircle3D(
				cons, label, A, B, C, GeoConicPart.CONIC_PART_ARC);
		return algo.getConicPart();
	}

	/**
	 * circle sector from three points
	 */
	final public GeoConicPart3D CircumcircleSector3D(String label,
			GeoPointND A, GeoPointND B, GeoPointND C) {
		AlgoConicPartCircumcircle3D algo = new AlgoConicPartCircumcircle3D(
				cons, label, A, B, C, GeoConicPart.CONIC_PART_SECTOR);
		return algo.getConicPart();
	}

	final public GeoElement[] AngularBisector3D(String[] labels, GeoLineND g,
			GeoLineND h) {
		AlgoAngularBisectorLines3D algo = new AlgoAngularBisectorLines3D(cons,
				labels, g, h);
		GeoLine3D[] lines = algo.getLines();
		return lines;
	}

	final public GeoLine3D AngularBisector3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C) {
		AlgoAngularBisectorPoints3D algo = new AlgoAngularBisectorPoints3D(
				cons, label, A, B, C);
		return algo.getLine();
	}

	final public GeoLine3D AngularBisector3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C, GeoDirectionND orientation) {
		AlgoAngularBisectorPoints3DOrientation algo = new AlgoAngularBisectorPoints3DOrientation(
				cons, label, A, B, C, orientation);
		return algo.getLine();
	}

	final public GeoConicPart3D CircleArcSector3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C, int type) {
		AlgoConicPartCircle3D algo = new AlgoConicPartCircle3D(cons, label, A,
				B, C, type);
		return algo.getConicPart();
	}

	final public GeoConicPartND CircleArcSector3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C, GeoDirectionND orientation, int type) {

		if (((GeoElement) A).isGeoElement3D()
				|| ((GeoElement) B).isGeoElement3D()
				|| ((GeoElement) C).isGeoElement3D()) { // at least one 3D geo
			if (orientation == kernel.getSpace()) { // space is default
													// orientation for 3D
													// objects
				return CircleArcSector3D(null, A, B, C, type);
			}

			// use view orientation
			AlgoConicPartCircle3D algo = new AlgoConicPartCircle3DOrientation(
					cons, label, A, B, C, orientation, type);
			return algo.getConicPart();
		}

		// 2D geos
		if (orientation == kernel.getXOYPlane()) { // xOy plane is default
													// orientation for 2D
													// objects
			return kernel.getAlgoDispatcher().CircleArcSector(label,
					(GeoPoint) A, (GeoPoint) B, (GeoPoint) C, type);
		}

		// use view orientation
		AlgoConicPartCircle3D algo = new AlgoConicPartCircle3DOrientation(cons,
				label, A, B, C, orientation, type);
		return algo.getConicPart();
	}

	@Override
	public GeoLine3D Line3D(String label, ExpressionValue[] coefX,
			ExpressionValue[] coefY, ExpressionValue[] coefZ) {
		MyVec3DNode start = new MyVec3DNode(kernel, coefX[0], coefY[0],
				coefZ[0]);
		MyVec3DNode v = new MyVec3DNode(kernel, coefX[1], coefY[1], coefZ[1]);
		AlgoDependentPoint3D pt = new AlgoDependentPoint3D(cons, start.wrap());
		cons.removeFromConstructionList(pt);
		AlgoDependentVector3D vec = new AlgoDependentVector3D(cons, v.wrap());
		cons.removeFromConstructionList(vec);
		AlgoLinePointVector3D algo = new AlgoLinePointVector3D(cons, label,
				pt.getPoint3D(), vec.getVector3D());
		GeoLine3D g = algo.getLine();
		return g;
	}

	final public GeoConicPartND Semicircle3D(String label, GeoPointND A,
			GeoPointND B, GeoDirectionND orientation) {

		if (((GeoElement) A).isGeoElement3D()
				|| ((GeoElement) B).isGeoElement3D()) { // at least one 3D geo
			// use view orientation
			AlgoSemicircle3D algo = new AlgoSemicircle3D(cons, label, A, B,
					orientation);
			return algo.getSemicircle();
		}

		// 2D geos
		if (orientation == kernel.getXOYPlane()) { // xOy plane is default
													// orientation for 2D
													// objects
			return kernel.getAlgoDispatcher().Semicircle(label, (GeoPoint) A,
					(GeoPoint) B);
		}

		// use view orientation
		AlgoSemicircle3D algo = new AlgoSemicircle3D(cons, label, A, B,
				orientation);
		return algo.getSemicircle();

	}

	/**
	 * tangents to c through P
	 */
	final public GeoElement[] Tangent3D(String[] labels, GeoPointND P,
			GeoConicND c) {
		if (P.isGeoElement3D() || c.isGeoElement3D()) {
			AlgoTangentPoint3D algo = new AlgoTangentPoint3D(cons, labels, P, c);
			return algo.getOutput();
		}

		return kernel.getAlgoDispatcher().Tangent(labels, P, c);
	}

	public GeoElement[] Tangent3D(String[] labels, GeoLineND l, GeoConicND c) {
		if (l.isGeoElement3D() || c.isGeoElement3D()) {
			AlgoTangentLine3D algo = new AlgoTangentLine3D(cons, labels, l, c);
			return algo.getOutput();
		}

		return kernel.getAlgoDispatcher().Tangent(labels, l, c);
	}

	/**
	 * common tangents to c1 and c2 dsun48 [6/26/2011]
	 */
	final public GeoElement[] CommonTangents3D(String[] labels, GeoConicND c1,
			GeoConicND c2) {
		if (c1.isGeoElement3D() || c2.isGeoElement3D()) {
			AlgoCommonTangents3D algo = new AlgoCommonTangents3D(cons, labels,
					c1, c2);
			return algo.getOutput();
		}

		return kernel.getAlgoDispatcher().CommonTangents(labels, c1, c2);
	}

	/**
	 * diameter line conjugate to direction of g relative to c
	 */
	final public GeoElement DiameterLine3D(String label, GeoLineND g,
			GeoConicND c) {

		if (g.isGeoElement3D() || c.isGeoElement3D()) {
			AlgoDiameterLine3D algo = new AlgoDiameterLine3D(cons, label, c, g);
			return (GeoElement) algo.getDiameter();
		}

		return kernel.getAlgoDispatcher().DiameterLine(label, g, c);
	}

	/**
	 * diameter line conjugate to v relative to c
	 */
	final public GeoElement DiameterLine3D(String label, GeoVectorND v,
			GeoConicND c) {

		if (v.isGeoElement3D() || c.isGeoElement3D()) {
			AlgoDiameterVector3D algo = new AlgoDiameterVector3D(cons, label,
					c, v);
			return (GeoElement) algo.getDiameter();
		}

		return kernel.getAlgoDispatcher().DiameterLine(label, v, c);
	}

	final public GeoElement LineBisector3D(String label, GeoSegmentND segment,
			GeoDirectionND orientation) {

		if (!segment.isGeoElement3D() // 2D geo
				&& orientation == kernel.getXOYPlane()) { // xOy plane is
															// default
															// orientation for
															// 2D objects
			return kernel.getAlgoDispatcher().LineBisector(null,
					(GeoSegment) segment);
		}

		AlgoLineBisectorSegmentDirection3D algo = new AlgoLineBisectorSegmentDirection3D(
				cons, label, segment, orientation);
		return algo.getLine();

	}

	final public GeoElement LineBisector3D(String label, GeoPointND a,
			GeoPointND b, GeoDirectionND orientation) {

		if (!a.isGeoElement3D() && !b.isGeoElement3D() // 2D geo
				&& orientation == kernel.getXOYPlane()) { // xOy plane is
															// default
															// orientation for
															// 2D objects
			return kernel.getAlgoDispatcher().LineBisector(label, (GeoPoint) a,
					(GeoPoint) b);
		}

		AlgoLineBisectorTwoPointsDirection3D algo = new AlgoLineBisectorTwoPointsDirection3D(
				cons, label, a, b, orientation);
		return algo.getLine();

	}

	final public GeoConicND Conic3D(String label, GeoPointND[] points) {
		AlgoConicFivePoints3D algo = new AlgoConicFivePoints3D(cons, label,
				points);
		return algo.getConic();
	}

	final public GeoConicND EllipseHyperbola3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C, final int type) {
		AlgoEllipseHyperbolaFociPoint3D algo = new AlgoEllipseHyperbolaFociPoint3D(
				cons, label, A, B, C, type);

		return algo.getConic();
	}

	final public GeoConicND EllipseHyperbola3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C, GeoDirectionND orientation,
			final int type) {

		if (!A.isGeoElement3D() && !B.isGeoElement3D() && !C.isGeoElement3D() // 2D
																				// geo
				&& orientation == kernel.getXOYPlane()) { // xOy plane is
															// default
															// orientation for
															// 2D objects
			return kernel.getAlgoDispatcher().EllipseHyperbola(null, A, B, C,
					type);
		}

		if (orientation == kernel.getSpace()) { // space is default orientation
												// for 2D objects
			return EllipseHyperbola3D(null, A, B, C, type);
		}

		AlgoEllipseHyperbolaFociPoint3DOriented algo = new AlgoEllipseHyperbolaFociPoint3DOriented(
				cons, label, A, B, C, orientation, type);

		return algo.getConic();

	}

	final public GeoConicND Parabola3D(String label, GeoPointND F, GeoLineND l) {
		AlgoParabolaPointLine3D algo = new AlgoParabolaPointLine3D(cons, label,
				F, l);
		return algo.getParabola();
	}

	final public GeoElement Locus3D(String label, GeoPointND Q, GeoPointND P) {
		if (!kernel.getAlgoDispatcher().LocusCheck(P, Q)) {
			return null;
		}

		return (new AlgoLocus3D(cons, label, Q, P)).getLocus();
	}

	public GeoElement Tangent3D(String label, GeoPointND point,
			GeoCurveCartesianND curve) {

		if (curve.isGeoElement3D()) {
			AlgoTangentCurve3D algo = new AlgoTangentCurve3D(cons, label,
					point, (GeoCurveCartesian3D) curve);
			algo.update();
			return algo.getOutput()[0];
		}

		return kernel.Tangent(label, point, (GeoCurveCartesian) curve);

	}

	@Override
	public GeoElement Locus3D(String label, GeoPointND Q, GeoNumeric slider) {
		return new AlgoLocusSlider3D(cons, label, Q, slider).getLocus();
	}

	/**
	 * intersection of polygons 3D
	 * 
	 * @author thilina
	 */
	public GeoElement[] IntersectPolygons(String[] labels,
			GeoPolygon3D inPoly0, GeoPolygon3D inPoly1) {

		AlgoIntersectPathPolygons3D algo = new AlgoIntersectPathPolygons3D(
				cons, labels, inPoly0, inPoly1);

		return algo.getOutput();
	}

	/**
	 * difference of polygons 3D
	 * 
	 * @author thilina
	 */
	public GeoElement[] DifferencePolygons(String[] labels,
			GeoPolygon3D inPoly0, GeoPolygon3D inPoly1) {
		AlgoDifferencePolygons3D algo = new AlgoDifferencePolygons3D(cons,
				labels, inPoly0, inPoly1);
		return algo.getOutput();
	}

	/**
	 * exclusive or normal difference of polygons 3D,
	 * 
	 * @author thilina
	 */

	public GeoElement[] DifferencePolygons(String[] labels,
			GeoPolygon3D inPoly0, GeoPolygon3D inPoly1, GeoBoolean exclusive) {
		AlgoDifferencePolygons3D algo = new AlgoDifferencePolygons3D(cons,
				labels, inPoly0, inPoly1, exclusive);
		return algo.getOutput();
	}

	/**
	 * Union of polygons 3D
	 * 
	 * @author thilina
	 */
	public GeoElement[] UnionPolygons(String[] labels, GeoPolygon3D inPoly0,
			GeoPolygon3D inPoly1) {
		AlgoUnionPolygons3D algo = new AlgoUnionPolygons3D(cons, labels,
				inPoly0, inPoly1);
		return algo.getOutput();
	}

	/**
	 * Intersect points of polygons 3D
	 * 
	 * @author thilina
	 */
	public GeoElement[] IntersectionPoint(String[] labels, GeoPolygon3D poly0,
			GeoPolygon3D poly1) {
		AlgoIntersectPolygons3D algo = new AlgoIntersectPolygons3D(cons,
				labels, poly0, poly1);
		return algo.getOutput();
	}

	public GeoNumeric Distance(String label, GeoPlaneND a, GeoPlaneND b) {
		AlgoDistancePlanes algo = new AlgoDistancePlanes(cons, label, a, b);

		return algo.getDistance();
	}
}
