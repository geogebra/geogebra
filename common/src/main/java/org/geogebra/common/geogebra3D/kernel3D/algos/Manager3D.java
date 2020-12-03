package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.Geo3DVec;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConicPart3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConicSection;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCurveCartesian3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DLimited;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DPart;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoRay3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSegment3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSpace;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import org.geogebra.common.geogebra3D.kernel3D.implicit3D.AlgoIntersectFunctionNVarPlane;
import org.geogebra.common.geogebra3D.kernel3D.implicit3D.AlgoIntersectImplicitSurfacePlane;
import org.geogebra.common.geogebra3D.kernel3D.implicit3D.GeoImplicitSurface;
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
import org.geogebra.common.kernel.algos.AlgoCircleThreePoints;
import org.geogebra.common.kernel.algos.AlgoDependentNumber;
import org.geogebra.common.kernel.algos.AlgoDependentPoint;
import org.geogebra.common.kernel.algos.AlgoDispatcher;
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
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPoly;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoSurfaceFinite;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.kernel.geos.properties.Auxiliary;
import org.geogebra.common.kernel.kernelND.Geo3DVecInterface;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoConicPartND;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoImplicitSurfaceND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.kernelND.HasHeight;
import org.geogebra.common.kernel.kernelND.HasVolume;
import org.geogebra.common.kernel.matrix.CoordMatrix;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;

import com.google.j2objc.annotations.Weak;

/**
 * Class that for manage all 3D methods in AbstractKernel.
 * 
 * @author mathieu
 * 
 */
public class Manager3D implements Manager3DInterface {

	@Weak
	private Kernel kernel;
	@Weak
	private Construction cons;
	private Coords tmpCoords;
	private Coords tmpCoords2;
	private Coords tmpCoords3;

	/**
	 * @param kernel
	 *            parent kernel
	 */
	public Manager3D(Kernel kernel) {
		this.kernel = kernel;
		this.cons = kernel.getConstruction();
	}

	/** Point3D label with cartesian coordinates (x,y,z) */
	@Override
	final public GeoPoint3D point3D(String label, double x, double y, double z,
			boolean coords2D) {
		GeoPoint3D p = new GeoPoint3D(cons);
		if (coords2D) {
			p.setCartesian();
		} else {
			p.setCartesian3D();
		}
		p.setCoords(x, y, z, 1.0);
		p.setLabel(label); // invokes add()

		return p;
	}

	@Override
	final public GeoPoint3D point3D(double x, double y, double z,
			boolean coords2D) {
		GeoPoint3D p = new GeoPoint3D(cons);
		if (coords2D) {
			p.setCartesian();
		} else {
			p.setCartesian3D();
		}
		p.setCoords(x, y, z, 1.0);

		return p;
	}

	/**
	 * Point dependent on arithmetic expression with variables, represented by a
	 * tree. e.g. P = (4t, 2s, 7)
	 */
	@Override
	final public GeoPoint3D dependentPoint3D(ExpressionNode root,
			boolean addToCons) {
		AlgoDependentPoint3D algo = new AlgoDependentPoint3D(cons, root,
				addToCons);
		GeoPoint3D P = algo.getPoint3D();
		P.setCartesian3D();
		return P;
	}

	@Override
	final public GeoVector3D dependentVector3D(ExpressionNode root) {
		AlgoDependentVector3D algo = new AlgoDependentVector3D(cons, root);
		return algo.getVector3D();
	}

	@Override
	final public GeoVector3D vector3D(double x, double y,
			double z) {
		return new GeoVector3D(cons, x, y, z);
	}

	/**
	 * Vector named label from Point P to Q
	 */
	@Override
	final public GeoVector3D vector3D(String label, GeoPointND P,
			GeoPointND Q) {
		AlgoVector3D algo = new AlgoVector3D(cons, P, Q);
		GeoVector3D v = (GeoVector3D) algo.getVector();
		v.setEuclidianVisible(true);
		v.setLabel(label);
		kernel.notifyUpdate(v);
		return v;
	}

	/** Point in region with cartesian coordinates (x,y,z) */
	@Override
	final public GeoPoint3D point3DIn(String label, Region region,
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

		if (coords2D) {
			p.setCartesian();
		} else {
			p.setCartesian3D();
		}
		p.update();

		if (!addToConstruction) {
			cons.setSuppressLabelCreation(oldMacroMode);
		}
		return p;
	}

	/** Point in region with cartesian coordinates (x,y,z) */
	@Override
	final public GeoPoint3D point3DIn(Region region, Coords coords,
			boolean coords2D) {
		AlgoPoint3DInRegion algo = new AlgoPoint3DInRegion(cons, region,
				coords);
		GeoPoint3D p = algo.getP();
		if (coords2D) {
			p.setCartesian();
		} else {
			p.setCartesian3D();
		}
		p.update();
		return p;
	}

	/** Point in region */
	@Override
	final public GeoPoint3D point3DIn(String label, Region region,
			boolean coords2D) {
		return point3DIn(label, region, null, true, coords2D);
	}

	/** Point3D on a 1D path with cartesian coordinates (x,y,z) */
	@Override
	final public GeoPoint3D point3D(String label, Path path, double x, double y,
			double z, boolean addToConstruction, boolean coords2D) {
		boolean oldMacroMode = false;
		if (!addToConstruction) {
			oldMacroMode = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);

		}
		AlgoPoint3DOnPath algo = new AlgoPoint3DOnPath(cons, path, x, y,
				z);
		GeoPoint3D p = (GeoPoint3D) algo.getP();
		if (coords2D) {
			p.setCartesian();
		} else {
			p.setCartesian3D();
		}
		p.setLabel(label);
		if (!addToConstruction) {
			cons.setSuppressLabelCreation(oldMacroMode);
		}
		return p;
	}

	/** Point3D on a 1D path without cartesian coordinates */
	@Override
	final public GeoPoint3D point3D(String label, Path path, boolean coords2D) {
		// try (0,0,0)
		// AlgoPoint3DOnPath algo = new AlgoPoint3DOnPath(cons, label, path, 0,
		// 0, 0);
		// GeoPoint3D p = algo.getP();
		GeoPoint3D p = point3D(label, path, 0, 0, 0, true, coords2D);

		/*
		 * TODO below // try (1,0,0) if (!p.isDefined()) { p.setCoords(1,0,1);
		 * algo.update(); }
		 * 
		 * // try (random(),0) if (!p.isDefined()) {
		 * p.setCoords(Math.random(),0,1); algo.update(); }
		 */

		return p;
	}

	@Override
	public GeoPointND point3D(String label, Path path, GeoNumberValue param) {

		// try (0,0,0)
		AlgoPoint3DOnPath algo = null;
		if (param == null) {
			algo = new AlgoPoint3DOnPath(cons, path, 0, 0, 0);
		} else {
			algo = new AlgoPoint3DOnPath(cons, path, param);
		}
		GeoPointND p = algo.getP();

		// try (1,0,0)
		if (!p.isDefined()) {
			p.setCoords(1, 0, 0, 1);
		}

		// try (random(),0, 0)
		if (!p.isDefined()) {
			p.setCoords(Math.random(), 0, 0, 1);
		}
		p.setLabel(label);
		return p;
	}

	/**
	 * Midpoint M = (P + Q)/2
	 */
	@Override
	final public GeoPoint3D midpoint(String label, GeoPointND P, GeoPointND Q) {
		AlgoMidpoint3D algo = new AlgoMidpoint3D(cons, P, Q);
		GeoPoint3D M = algo.getPoint();
		M.setLabel(label);
		return M;
	}

	@Override
	public GeoPointND midpoint(String label, GeoSegmentND segment) {
		AlgoMidpoint3D algo = new AlgoMidpointSegment3D(cons, segment);
		GeoPoint3D M = algo.getPoint();
		M.setLabel(label);
		return M;
	}

	@Override
	public GeoPointND center(String label, GeoConicND conic) {
		AlgoCenterConic3D algo = new AlgoCenterConic3D(cons, label, conic);
		return algo.getPoint();
	}

	@Override
	public GeoPointND centerQuadric(String label, GeoQuadricND quadric) {
		AlgoCenterQuadric algo = new AlgoCenterQuadric(cons, label,
				(GeoQuadric3D) quadric);
		return algo.getPoint();
	}

	/** Segment3D label linking points v1 and v2 */
	/*
	 * final public GeoSegment3D segment3D(String label, Ggb3DVector v1,
	 * Ggb3DVector v2){ GeoSegment3D s = new GeoSegment3D(cons,v1,v2);
	 * s.setLabel(label); return s; }
	 */

	/** Segment3D label linking points P1 and P2 */
	@Override
	final public GeoSegment3D segment3D(String label, GeoPointND P1,
			GeoPointND P2) {
		AlgoJoinPoints3D algo = new AlgoJoinPoints3D(cons, label, P1, P2,
				GeoClass.SEGMENT3D);
		GeoSegment3D s = (GeoSegment3D) algo.getCS();
		return s;
	}

	/** Line3D label linking points P1 and P2 */
	@Override
	final public GeoLine3D line3D(String label, GeoPointND P1, GeoPointND P2) {
		AlgoJoinPoints3D algo = new AlgoJoinPoints3D(cons, label, P1, P2,
				GeoClass.LINE3D);
		GeoLine3D l = (GeoLine3D) algo.getCS();
		return l;
	}

	@Override
	final public GeoLineND line3D(String label, GeoPointND P, GeoLineND l) {
		AlgoLinePointLine3D algo = new AlgoLinePointLine3D(cons, P, l);
		GeoLineND g = algo.getLine();
		g.setLabel(label);
		return g;
	}

	@Override
	final public GeoLineND line3D(String label, GeoPointND P, GeoVectorND v) {
		AlgoLinePointVector3D algo = new AlgoLinePointVector3D(cons, P,	v);
		GeoLineND g = algo.getLine();
		g.setLabel(label);
		return g;
	}

	/** Ray3D label linking points P1 and P2 */
	@Override
	final public GeoRay3D ray3D(String label, GeoPointND P1, GeoPointND P2) {
		// Application.debug("Kernel3D : Ray3D");
		// AlgoJoinPointsRay3D algo = new AlgoJoinPointsRay3D(cons, label, P1,
		// P2);
		// GeoRay3D l = algo.getRay3D();
		AlgoJoinPoints3D algo = new AlgoJoinPoints3D(cons, label, P1, P2,
				GeoClass.RAY3D);
		GeoRay3D l = (GeoRay3D) algo.getCS();
		return l;
	}

	@Override
	public GeoLineND orthogonalLine3D(String label, GeoPointND point,
			GeoCoordSys2D cs) {
		AlgoOrthoLinePointPlane algo = new AlgoOrthoLinePointPlane(cons, label,
				point, cs);
		return algo.getLine();
	}

	@Override
	public GeoLineND orthogonalLine3D(String label, GeoPointND point,
			GeoLineND line) {
		AlgoOrthoLinePointLine3D algo = new AlgoOrthoLinePointLine3D(cons,
				label, point, line);
		return algo.getLine();
	}

	@Override
	public GeoLineND orthogonalLine3D(String label, GeoPointND point,
			GeoDirectionND line, GeoDirectionND direction) {

		// when have space as direction, just to say it's not as in 2D
		if (line instanceof GeoLineND && direction instanceof GeoSpace) {
			return orthogonalLine3D(label, point, (GeoLineND) line);
		}

		// when using Locus (via macro) or xOy plane as direction, check if it's
		// only 2D objects, then return 2D line
		if ((!(cons.is3D()) || direction == cons.getXOYPlane())
				&& (point instanceof GeoPoint) && (line instanceof GeoLine)) {
			AlgoOrthoLinePointLine algo = new AlgoOrthoLinePointLine(cons,
					label, (GeoPoint) point, (GeoLine) line);
			return algo.getLine();
		}

		AlgoOrthoLinePointDirectionDirection algo = new AlgoOrthoLinePointDirectionDirection(
				cons, label, point, line, direction);
		return algo.getLine();
	}

	@Override
	public GeoLineND orthogonalLine3D(String label, GeoLineND line1,
			GeoLineND line2) {
		AlgoOrthoLineLineLine algo = new AlgoOrthoLineLineLine(cons, label,
				line1, line2);
		return algo.getLine();
	}

	@Override
	public GeoVectorND orthogonalVector3D(String label, GeoCoordSys2D plane) {
		AlgoOrthoVectorPlane algo = new AlgoOrthoVectorPlane(cons, label,
				plane);
		return algo.getVector();
	}

	@Override
	public GeoVectorND orthogonalVector3D(String label, GeoLineND line,
			GeoDirectionND direction) {
		AlgoOrthoVectorLineDirection algo = new AlgoOrthoVectorLineDirection(
				cons, label, line, direction);
		return algo.getVector();
	}

	@Override
	public GeoVectorND unitOrthogonalVector3D(String label,
			GeoCoordSys2D plane) {
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
	@Override
	final public GeoElement[] polygon3D(String[] label, GeoPointND[] points) {

		AlgoPolygon3D algo = new AlgoPolygon3D(cons, label, points, null);

		return algo.getOutput();
	}

	@Override
	final public GeoElement[] polygon3D(String[] label, GeoPointND[] points,
			GeoDirectionND direction) {
		AlgoPolygon algo = new AlgoPolygon3DDirection(cons, label, points,
				direction);

		return algo.getOutput();
	}

	@Override
	final public GeoElement[] polyLine3D(String label, GeoPointND[] P) {
		AlgoPolyLine3D algo = new AlgoPolyLine3D(cons, label, P);
		return algo.getOutput();
	}

	@Override
	final public GeoElement[] polyLine3D(String label, GeoList pointList) {
		AlgoPolyLine3D algo = new AlgoPolyLine3D(cons, label, pointList);
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
	@Override
	final public GeoElement[] prism(String[] labels, GeoPointND[] points) {
		AlgoPolyhedronPointsPrism algo = new AlgoPolyhedronPointsPrism(cons,
				labels, points);

		return algo.getOutput();
	}

	@Override
	final public GeoElement[] prism(String[] labels, GeoPolygon polygon,
			GeoPointND point) {
		AlgoPolyhedronPointsPrism algo = new AlgoPolyhedronPointsPrism(cons,
				labels, polygon, point);

		return algo.getOutput();
	}

	@Override
	final public GeoElement[] prism(String[] labels, GeoPolygon polygon,
			GeoNumberValue height) {
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
	@Override
	final public GeoElement[] pyramid(String[] labels, GeoPointND[] points) {
		AlgoPolyhedronPointsPyramid algo = new AlgoPolyhedronPointsPyramid(cons,
				labels, points);

		return algo.getOutput();
	}

	@Override
	final public GeoElement[] pyramid(String[] labels, GeoPolygon polygon,
			GeoPointND point) {
		AlgoPolyhedronPointsPyramid algo = new AlgoPolyhedronPointsPyramid(cons,
				labels, polygon, point);

		return algo.getOutput();
	}

	@Override
	final public GeoElement[] pyramid(String[] labels, GeoPolygon polygon,
			GeoNumberValue height) {
		AlgoPolyhedronPointsPyramid algo = new AlgoPolyhedronPointsPyramid(cons,
				labels, polygon, height);

		return algo.getOutput();
	}

	@Override
	final public GeoPlane3D plane3D(double a, double b, double c, double d) {
		GeoPlane3D plane = new GeoPlane3D(cons, a, b, c, d);
		if (Double.isNaN(d) || Double.isNaN(c) || Double.isNaN(b)
				|| Double.isNaN(a)) {
			plane.setUndefined();
		}
		return plane;
	}

	@Override
	final public GeoPlane3D dependentPlane3D(Equation equ) {
		AlgoDependentPlane3D algo = new AlgoDependentPlane3D(cons, equ);
		return algo.getPlane();
	}

	@Override
	final public GeoQuadric3D dependentQuadric3D(Equation equ) {
		AlgoDependentQuadric3D algo = new AlgoDependentQuadric3D(cons, equ);
		return algo.getQuadric();
	}

	@Override
	final public GeoPlane3D plane3D(String label, GeoPointND point,
			GeoLineND line) {
		AlgoPlaneThroughPointAndLine algo = new AlgoPlaneThroughPointAndLine(
				cons, label, point, line);
		return algo.getPlane();
	}

	@Override
	final public GeoPlane3D plane3D(String label, GeoPointND point,
			GeoCoordSys2D cs) {
		AlgoPlaneThroughPointAndPlane algo = new AlgoPlaneThroughPointAndPlane(
				cons, label, point, cs);
		return algo.getPlane();
	}

	/**
	 * Plane named label through Point P orthogonal to line l
	 */
	@Override
	final public GeoPlane3D orthogonalPlane3D(String label, GeoPointND point,
			GeoLineND line) {

		return new AlgoOrthoPlanePointLine(cons, label, point, line).getPlane();
	}

	/**
	 * Plane named label through Point P orthogonal to line l
	 */
	@Override
	final public GeoPlane3D orthogonalPlane3D(String label, GeoPointND point,
			GeoVectorND vector) {

		return new AlgoOrthoPlanePointVector(cons, label, point, vector)
				.getPlane();
	}

	@Override
	final public GeoPlane3D planeBisector(String label, GeoPointND point1,
			GeoPointND point2) {

		return new AlgoOrthoPlaneBisectorPointPoint(cons, label, point1, point2)
				.getPlane();
	}

	@Override
	final public GeoPlane3D planeBisector(String label, GeoSegmentND segment) {

		return new AlgoOrthoPlaneBisectorSegment(cons, label, segment)
				.getPlane();
	}

	/** Sphere label linking with center o and radius r */
	@Override
	final public GeoQuadric3D sphere(String label, GeoPointND M,
			GeoNumberValue r) {
		AlgoSpherePointRadius algo = new AlgoSpherePointRadius(cons, M,
				r);
		algo.getSphere().setToSpecific();
		algo.getSphere().setLabel(label);
		return algo.getSphere();
	}

	/**
	 * Sphere with midpoint M through point P
	 */
	@Override
	final public GeoQuadric3D sphere(String label, GeoPointND M, GeoPointND P) {
		AlgoSphereTwoPoints algo = new AlgoSphereTwoPoints(cons, M, P);
		algo.getSphere().setToSpecific();
		algo.getSphere().setLabel(label);
		return algo.getSphere();
	}

	/**
	 * Cone
	 */
	@Override
	final public GeoQuadric3D cone(String label, GeoPointND origin,
			GeoVectorND direction, GeoNumberValue angle) {
		AlgoQuadric algo = new AlgoConeInfinitePointVectorNumber(cons, label,
				origin, direction, angle);
		return algo.getQuadric();
	}

	@Override
	final public GeoQuadric3D cone(String label, GeoPointND origin,
			GeoPointND secondPoint, GeoNumberValue angle) {
		AlgoQuadric algo = new AlgoConeInfinitePointPointNumber(cons, label,
				origin, secondPoint, angle);
		return algo.getQuadric();
	}

	@Override
	final public GeoQuadric3D cone(String label, GeoPointND origin,
			GeoLineND axis, GeoNumberValue angle) {
		AlgoConePointLineAngle algo = new AlgoConePointLineAngle(cons, label,
				origin, axis, angle);
		return algo.getQuadric();
	}

	@Override
	final public GeoElement[] coneLimited(String[] labels, GeoPointND origin,
			GeoPointND secondPoint, GeoNumberValue r) {
		AlgoQuadricLimitedPointPointRadius algo = new AlgoQuadricLimitedPointPointRadiusCone(
				cons, labels, origin, secondPoint, r);
		return algo.getOutput();
	}

	@Override
	final public GeoElement[] coneLimited(String[] labels, GeoConicND bottom,
			GeoNumberValue height) {
		AlgoQuadricLimitedConicHeightCone algo = new AlgoQuadricLimitedConicHeightCone(
				cons, labels, bottom, height);
		algo.update(); // ensure volume is correctly computed
		return algo.getOutput();
	}

	/**
	 * Cylinder
	 */
	@Override
	final public GeoQuadric3D cylinder(String label, GeoPointND origin,
			GeoVectorND direction, GeoNumberValue r) {
		AlgoQuadric algo = new AlgoCylinderInfinitePointVectorNumber(cons,
				label, origin, direction, r);
		return algo.getQuadric();
	}

	@Override
	final public GeoQuadric3D cylinder(String label, GeoPointND origin,
			GeoPointND secondPoint, GeoNumberValue r) {
		AlgoQuadric algo = new AlgoCylinderInfinitePointPointNumber(cons, label,
				origin, secondPoint, r);
		return algo.getQuadric();
	}

	@Override
	final public GeoQuadric3D cylinder(String label, GeoLineND axis,
			GeoNumberValue r) {
		AlgoQuadric algo = new AlgoCylinderAxisRadius(cons, label, axis, r);
		return algo.getQuadric();
	}

	@Override
	final public GeoElement[] cylinderLimited(String[] labels,
			GeoPointND origin, GeoPointND secondPoint, GeoNumberValue r) {
		AlgoQuadricLimitedPointPointRadius algo = new AlgoQuadricLimitedPointPointRadiusCylinder(
				cons, labels, origin, secondPoint, r);
		algo.update(); // ensure volume is correctly computed
		return algo.getOutput();
	}

	@Override
	final public GeoElement[] cylinderLimited(String[] labels,
			GeoConicND bottom, GeoNumberValue height) {
		AlgoQuadricLimitedConicHeightCylinder algo = new AlgoQuadricLimitedConicHeightCylinder(
				cons, labels, bottom, height);
		algo.update(); // ensure volume is correctly computed
		return algo.getOutput();
	}

	@Override
	final public GeoQuadric3DPart quadricSide(String label,
			GeoQuadricND quadric) {
		AlgoQuadric algo = new AlgoQuadricSide(cons,
				(GeoQuadric3DLimited) quadric, false, null);
		algo.getQuadric().setLabel(label);
		return (GeoQuadric3DPart) algo.getQuadric();
	}

	@Override
	final public GeoConic3D quadricBottom(String label, GeoQuadricND quadric) {
		AlgoQuadricEnd algo = new AlgoQuadricEndBottom(cons, label,
				(GeoQuadric3DLimited) quadric);
		return algo.getSection();
	}

	@Override
	final public GeoConic3D quadricTop(String label, GeoQuadricND quadric) {
		AlgoQuadricEnd algo = new AlgoQuadricEndTop(cons, label,
				(GeoQuadric3DLimited) quadric);
		return algo.getSection();
	}

	/**
	 * circle through points A, B, C
	 */
	@Override
	final public GeoConic3D circle3D(String label, GeoPointND A, GeoPointND B,
			GeoPointND C) {
		AlgoCircleThreePoints algo = new AlgoCircle3DThreePoints(cons, A,
				B, C);
		GeoConic3D circle = (GeoConic3D) algo.getCircle();
		// circle.setToSpecific();
		circle.setLabel(label);
		return circle;
	}

	@Override
	public GeoConic3D circle3D(String label, GeoLineND axis, GeoPointND A) {
		AlgoCircle3DAxisPoint algo = new AlgoCircle3DAxisPoint(cons, label,
				axis, A);
		GeoConic3D circle = algo.getCircle();
		// circle.setToSpecific();
		circle.update();
		kernel.notifyUpdate(circle);
		return circle;
	}

	@Override
	public GeoConicND circle3D(String label, GeoPointND A,
			GeoNumberValue radius, GeoDirectionND axis) {

		if (!A.isGeoElement3D() && axis == kernel.getXOYPlane()) {
			return kernel.getAlgoDispatcher().circle(label, A, radius);
		}

		AlgoCircle3DPointDirection algo = new AlgoCircle3DPointRadiusDirection(
				cons, A, radius, axis);
		GeoConic3D circle = algo.getCircle();
		// circle.setToSpecific();
		circle.setLabel(label);
		kernel.notifyUpdate(circle);
		return circle;
	}

	@Override
	public GeoConicND circle3D(String label, GeoPointND A,
			GeoNumberValue radius) {
		return circle3D(label, A, radius, kernel.getXOYPlane());
	}

	@Override
	public GeoConicND circle3D(String label, GeoPointND A, GeoPointND B,
			GeoDirectionND orientation) {

		if (!A.isGeoElement3D() && !B.isGeoElement3D() // 2D geos
				&& orientation == kernel.getXOYPlane()) { // xOy plane is
															// default
															// orientation for
															// 2D objects
			return kernel.getAlgoDispatcher().circle(label, (GeoPoint) A,
					(GeoPoint) B);
		}

		// at least one 3D geo or specific orientation
		AlgoCircle3DPointDirection algo = new AlgoCircle3DPointPointDirection(
				cons, A, B, orientation);
		GeoConic3D circle = algo.getCircle();
		// circle.setToSpecific();
		circle.setLabel(label);
		kernel.notifyUpdate(circle);
		return circle;
	}

	/**
	 * plane through points A, B, C
	 */
	@Override
	final public GeoPlane3D plane3D(String label, GeoPointND A, GeoPointND B,
			GeoPointND C) {
		AlgoPlaneThreePoints algo = new AlgoPlaneThreePoints(cons, label, A, B,
				C);
		GeoPlane3D plane = (GeoPlane3D) algo.getCoordSys();
		return plane;
	}

	@Override
	public GeoElement plane3D(String label, GeoLineND a, GeoLineND b) {
		AlgoPlaneTwoLines algo = new AlgoPlaneTwoLines(cons, label, a, b);
		return (GeoPlane3D) algo.getCoordSys();
	}

	@Override
	final public GeoPlane3D plane3D(String label, GeoCoordSys2D cs2D) {
		AlgoPlaneCS2D algo = new AlgoPlaneCS2D(cons, cs2D);
		GeoPlane3D plane = (GeoPlane3D) algo.getCoordSys();
		plane.setLabel(label);
		return plane;
	}

	@Override
	final public GeoPlane3D plane3D(GeoCoordSys2D cs2D) {
		AlgoPlaneCS2D algo = new AlgoPlaneCS2D(cons, cs2D);
		GeoPlane3D plane = (GeoPlane3D) algo.getCoordSys();
		return plane;
	}

	// //////////////////////////////////////////////
	// INTERSECTION (POINTS)

	@Override
	final public GeoElement intersect(String label, GeoLineND cs1,
			GeoCoordSys2D cs2, boolean swapInputs) {

		AlgoIntersectCoordSys algo = new AlgoIntersectCS1D2D(cons, label, cs1,
				cs2, swapInputs);

		return algo.getIntersection();
	}

	@Override
	final public GeoElement intersect(String label, GeoLineND cs1,
			GeoLineND cs2) {

		AlgoIntersectCoordSys algo = new AlgoIntersectCS1D1D(cons, label, cs1,
				cs2);

		return algo.getIntersection();
	}

	@Override
	public GeoElement[] intersectionPoint(String[] labels, GeoLineND g,
			GeoSurfaceFinite p) {

		if (p instanceof GeoPolygon) {
			AlgoElement algo = new AlgoIntersectLinePolygon3D(cons, labels, g,
					(GeoPolygon) p);

			return algo.getOutput();
		}
		return null;
	}

	@Override
	public GeoElement[] intersectionPoint(String[] labels, GeoPlaneND plane,
			GeoElement s) {

		if (s instanceof GeoPolygon) {
			AlgoIntersectPlanePolygon algo = new AlgoIntersectPlanePolygon(cons,
					labels, (GeoPlane3D) plane, (GeoPolygon) s);
			return algo.getOutput();
		}

		if (s.isGeoPolyhedron()) {
			AlgoIntersectPlanePolyhedron algo = new AlgoIntersectPlanePolyhedron(
					cons, labels, (GeoPlane3D) plane, (GeoPolyhedron) s);
			return algo.getOutput();
		}

		return null;
	}

	@Override
	public GeoElement[] intersectPath(String[] labels, GeoLineND g,
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

	@Override
	public GeoElement[] intersectPath(String[] labels, GeoPlaneND plane,
			GeoElement p) {

		if (p instanceof GeoPolygon) {
			AlgoIntersectPathPlanePolygon3D algo = new AlgoIntersectPathPlanePolygon3D(
					cons, labels, (GeoPlane3D) plane, (GeoPolygon) p);
			return algo.getOutput();
		}

		return null;
	}

	@Override
	public GeoElement[] intersectPlaneFunctionNVar(String label,
			GeoPlaneND plane, GeoFunctionNVar fun) {
		GeoElement[] ret = intersectPlaneFunctionNVar(plane, fun);
		ret[0].setLabel(label);
		return ret;
	}

	@Override
	public GeoElement[] intersectPlaneFunctionNVar(GeoPlaneND plane,
			GeoFunctionNVar fun) {
		AlgoIntersectFunctionNVarPlane algo = new AlgoIntersectFunctionNVarPlane(
				cons, fun, plane);
		return algo.getOutput();
	}

	@Override
	public GeoElement[] intersectPath(GeoPlaneND plane, GeoPolygon p) {

		AlgoIntersectPathPlanePolygon3D algo = new AlgoIntersectPathPlanePolygon3D(
				cons, (GeoPlane3D) plane, p);
		algo.update();
		return algo.getOutput();
	}

	@Override
	public GeoElement[] intersectRegion(String[] labels, GeoPlaneND plane,
			GeoElement p, int[] outputSizes) {

		if (p.isGeoPolyhedron()) {
			AlgoIntersectRegionPlanePolyhedron algo = new AlgoIntersectRegionPlanePolyhedron(
					cons, labels, (GeoPlane3D) plane, (GeoPolyhedron) p,
					outputSizes);
			return algo.getOutput();
		}

		return null;
	}

	@Override
	public GeoElement[] intersectRegion(GeoPlaneND plane, GeoElement p) {

		if (p.isGeoPolyhedron()) {
			AlgoIntersectRegionPlanePolyhedron algo = new AlgoIntersectRegionPlanePolyhedron(
					cons, (GeoPlane3D) plane, (GeoPolyhedron) p);
			algo.update();
			return algo.getOutput();
		}

		return null;
	}

	@Override
	public GeoConic3D intersect(String label, GeoPlaneND plane,
			GeoQuadricND quadric) {
		GeoConic3D ret;
		if (quadric instanceof GeoQuadric3DPart) {
			AlgoIntersectPlaneQuadricPart algo = new AlgoIntersectPlaneQuadricPart(
					cons, (GeoPlane3D) plane, quadric);
			ret = algo.getConic();
		} else {

			AlgoIntersectPlaneQuadric algo = new AlgoIntersectPlaneQuadric(cons,
					plane, quadric);
			ret = algo.getConic();
		}
		ret.setLabel(label);
		return ret;
	}

	@Override
	public GeoConicND intersectQuadricLimited(String label, GeoPlaneND plane,
			GeoQuadricND quadric) {

		AlgoIntersectPlaneQuadric algo = new AlgoIntersectPlaneQuadricLimited(
				cons, label, (GeoPlane3D) plane, quadric);

		return algo.getConic();
	}

	@Override
	public GeoConicND intersectQuadricLimited(GeoPlaneND plane,
			GeoQuadricND quadric) {
		AlgoIntersectPlaneQuadric algo = new AlgoIntersectPlaneQuadricLimited(
				cons, (GeoPlane3D) plane, quadric);

		return algo.getConic();
	}

	@Override
	public GeoConic3D intersect(GeoPlaneND plane, GeoQuadricND quadric) {

		AlgoIntersectPlaneQuadric algo = new AlgoIntersectPlaneQuadric(cons,
				plane, quadric);

		return algo.getConic();
	}

	@Override
	public GeoElement[] intersectAsCircle(String[] labels,
			GeoQuadricND quadric1, GeoQuadricND quadric2) {

		AlgoIntersectQuadricsAsCircle algo = new AlgoIntersectQuadricsAsCircle(
				cons, labels, quadric1, quadric2);

		return algo.getOutput();
	}

	@Override
	public GeoElement[] intersectAsCircle(GeoQuadricND quadric1,
			GeoQuadricND quadric2) {

		AlgoIntersectQuadricsAsCircle algo = new AlgoIntersectQuadricsAsCircle(
				cons, quadric1, quadric2);

		return algo.getOutput();
	}

	// //////////////////////////////////////////////
	// FUNCTIONS (2 VARS)

	@Override
	final public GeoFunctionNVar function2Var(String label,
			GeoNumberValue zcoord, GeoNumeric localVarU, GeoNumberValue Ufrom,
			GeoNumberValue Uto, GeoNumeric localVarV, GeoNumberValue Vfrom,
			GeoNumberValue Vto) {

		AlgoFunctionNVarND algo = new AlgoFunctionNVarND(cons, label,
				new GeoNumberValue[] { zcoord },
				new GeoNumeric[] { localVarU, localVarV },
				new GeoNumberValue[] { Ufrom, Vfrom },
				new GeoNumberValue[] { Uto, Vto });

		return algo.getFunction();
	}

	@Override
	final public GeoFunctionNVar function2Var(String label, GeoFunctionNVar f,
			GeoNumberValue xFrom, GeoNumberValue xTo, GeoNumberValue yFrom,
			GeoNumberValue yTo) {

		AlgoFunctionNVarND algo = new AlgoFunctionNVarND(cons, label, f,
				new GeoNumberValue[] { xFrom, yFrom },
				new GeoNumberValue[] { xTo, yTo });

		return algo.getFunction();
	}

	// //////////////////////////////////////////////
	// 3D CURVE (1 VAR)

	/**
	 * 3D Cartesian curve command: Curve[ &lt;expression x-coord>,
	 * &lt;expression y-coord>, &lt;expression z-coord>, &lt;number-var>,
	 * &lt;from>, &lt;to> ]
	 */
	@Override
	final public GeoCurveCartesian3D curveCartesian3D(GeoNumberValue xcoord,
			GeoNumberValue ycoord, GeoNumberValue zcoord, GeoNumeric localVar,
			GeoNumberValue from, GeoNumberValue to) {
		AlgoCurveCartesian3D algo = new AlgoCurveCartesian3D(cons, null,
				new GeoNumberValue[] { xcoord, ycoord, zcoord }, localVar, from,
				to);
		return (GeoCurveCartesian3D) algo.getCurve();
	}

	// //////////////////////////////////////////////
	// 3D SURFACE (2 VARS)

	@Override
	public GeoElement surfaceOfRevolution(Path function,
			GeoNumberValue angle, GeoLineND line) {
		AlgoSurfaceOfRevolution algo = new AlgoSurfaceOfRevolution(cons,
				function, angle, line);
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
				.findExistingIntersectionAlgorithm(g, c);
		if (existingAlgo != null) {
			return (AlgoIntersectLineConic3D) existingAlgo;
		}

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectLineConic3D algo = new AlgoIntersectLineConic3D(cons, g,
				c);
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
	@Override
	final public GeoPoint3D[] intersectLineConic(String[] labels, GeoLineND g,
			GeoConicND c) {
		AlgoIntersectLineConic3D algo = getIntersectionAlgorithm(g, c);
		algo.setPrintedInXML(true);
		GeoPoint3D[] points = algo.getIntersectionPoints();
		LabelManager.setLabels(labels, points);
		return points;
	}

	/**
	 * get only one intersection point of two conics that is near to the given
	 * location (xRW, yRW)
	 */
	@Override
	final public GeoPoint3D intersectLineConicSingle(String label, GeoLineND g,
			GeoConicND c, double xRW, double yRW, CoordMatrix mat) {

		AlgoIntersectLineConic3D algo = getIntersectionAlgorithm(g, c);

		int index = algo.getClosestPointIndex(xRW, yRW, mat);

		AlgoIntersectSingle3D salgo = new AlgoIntersectSingle3D(label, algo,
				index);
		GeoPoint3D point = salgo.getPoint();
		return point;
	}

	@Override
	final public GeoPoint3D intersectLineConicSingle(String label, GeoLineND g,
			GeoConicND c, Coords origin, Coords direction) {

		AlgoIntersectLineConic3D algo = getIntersectionAlgorithm(g, c);

		int index = algo.getClosestPointIndex(origin, direction);

		AlgoIntersectSingle3D salgo = new AlgoIntersectSingle3D(label, algo,
				index);
		return salgo.getPoint();
	}

	/**
	 * get only one intersection point of two conics choice depends on command
	 * input
	 */
	@Override
	final public GeoPoint3D intersectLineConicSingle(String label, GeoLineND g,
			GeoConicND c, NumberValue index) {

		return intersectLineConicSingle(label, g, c,
				(int) index.getDouble() - 1);
	}

	/**
	 * get only one intersection point of two conics choice depends on command
	 * input
	 */
	@Override
	final public GeoPoint3D intersectLineConicSingle(String label, GeoLineND g,
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
	@Override
	public GeoPoint3D intersectLineConicSingle(String label, GeoLineND g,
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
	 * 
	 * @param A
	 *            conic
	 * @param B
	 *            conic
	 * @return intersect algo
	 */
	private AlgoIntersectConics3D getIntersectionAlgorithmConics(GeoConicND A,
			GeoQuadricND B) {
		AlgoElement existingAlgo = kernel.getAlgoDispatcher()
				.findExistingIntersectionAlgorithm(A, B);
		if (existingAlgo != null) {
			return (AlgoIntersectConics3D) existingAlgo;
		}

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
	@Override
	final public GeoPoint3D[] intersectConics(String[] labels, GeoConicND A,
			GeoQuadricND B) {
		AlgoIntersectConics3D algo = getIntersectionAlgorithmConics(A, B);
		algo.setPrintedInXML(true);
		GeoPoint3D[] points = algo.getIntersectionPoints();
		LabelManager.setLabels(labels, points);
		return points;
	}

	@Override
	final public GeoPoint3D intersectConicsSingle(String label, GeoConicND A,
			GeoQuadricND B, double xRW, double yRW, CoordMatrix mat) {

		AlgoIntersectConics3D algo = getIntersectionAlgorithmConics(A, B);

		int index = algo.getClosestPointIndex(xRW, yRW, mat);
		AlgoIntersectSingle3D salgo = new AlgoIntersectSingle3D(label, algo,
				index);
		GeoPoint3D point = salgo.getPoint();
		return point;
	}

	@Override
	final public GeoPoint3D intersectConicsSingle(String label, GeoConicND A,
			GeoQuadricND B, Coords origin, Coords direction) {

		AlgoIntersectConics3D algo = getIntersectionAlgorithmConics(A, B);

		int index = algo.getClosestPointIndex(origin, direction);
		AlgoIntersectSingle3D salgo = new AlgoIntersectSingle3D(label, algo,
				index);
		GeoPoint3D point = salgo.getPoint();
		return point;
	}

	@Override
	final public GeoPoint3D intersectConicsSingle(String label, GeoConicND A,
			GeoQuadricND B, NumberValue index) {
		return intersectConicsSingle(label, A, B, (int) index.getDouble() - 1);
	}

	@Override
	final public GeoPoint3D intersectConicsSingle(String label, GeoConicND A,
			GeoQuadricND B, int index) {
		AlgoIntersectConics3D algo = getIntersectionAlgorithmConics(A, B); // index
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

	@Override
	final public GeoPoint3D intersectConicsSingle(String label, GeoConicND A,
			GeoQuadricND B, GeoPointND refPoint) {
		AlgoIntersectConics3D algo = getIntersectionAlgorithmConics(A, B); // index
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
	 * intersect line/quadric
	 */
	private AlgoIntersectLineQuadric3D getIntersectionAlgorithm(GeoLineND A,
			GeoQuadricND B) {
		AlgoElement existingAlgo = kernel.getAlgoDispatcher()
				.findExistingIntersectionAlgorithm(A, B);
		if (existingAlgo != null) {
			return (AlgoIntersectLineQuadric3D) existingAlgo;
		}

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectLineQuadric3D algo = new AlgoIntersectLineQuadric3D(cons,
				A, B);
		algo.setPrintedInXML(false);
		kernel.getAlgoDispatcher().addIntersectionAlgorithm(algo); // remember
																	// this
																	// algorithm
		return algo;
	}

	@Override
	public GeoPointND[] intersectLineQuadric(String[] labels, GeoLineND A,
			GeoQuadricND B) {
		AlgoIntersectLineQuadric3D algo = getIntersectionAlgorithm(A, B);
		algo.setPrintedInXML(true);
		GeoPoint3D[] points = algo.getIntersectionPoints();
		LabelManager.setLabels(labels, points);
		return points;
	}

	/**
	 * get only one intersection point of line and quadric choice depends on
	 * command input
	 */
	@Override
	final public GeoPoint3D intersectLineQuadricSingle(String label,
			GeoLineND g, GeoQuadricND q, NumberValue index) {

		return intersectLineQuadricSingle(label, g, q,
				(int) index.getDouble() - 1);
	}

	/**
	 * get only one intersection point of line and quadric choice depends on
	 * command input
	 */
	@Override
	final public GeoPoint3D intersectLineQuadricSingle(String label,
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
	@Override
	final public GeoPoint3D intersectLineQuadricSingle(String label,
			GeoLineND g, GeoQuadricND q, double xRW, double yRW,
			CoordMatrix4x4 mat) {

		AlgoIntersectLineQuadric3D algo = getIntersectionAlgorithm(g, q);

		int index = algo.getClosestPointIndex(xRW, yRW, mat);
		AlgoIntersectSingle3D salgo = new AlgoIntersectSingle3D(label, algo,
				index);
		GeoPoint3D point = salgo.getPoint();
		return point;
	}

	@Override
	final public GeoPoint3D intersectLineQuadricSingle(String label,
			GeoLineND g, GeoQuadricND q, Coords origin, Coords direction) {

		AlgoIntersectLineQuadric3D algo = getIntersectionAlgorithm(g, q);

		int index = algo.getClosestPointIndex(origin, direction);
		AlgoIntersectSingle3D salgo = new AlgoIntersectSingle3D(label, algo,
				index);
		GeoPoint3D point = salgo.getPoint();
		return point;
	}

	@Override
	final public GeoPoint3D intersectLineQuadricSingle(String label,
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
				.findExistingIntersectionAlgorithm(A, B);
		if (existingAlgo != null) {
			return (AlgoIntersectPlaneConic) existingAlgo;
		}

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectPlaneConic algo = new AlgoIntersectPlaneConic(cons, A, B);
		algo.setPrintedInXML(false);
		kernel.getAlgoDispatcher().addIntersectionAlgorithm(algo); // remember
																	// this
																	// algorithm
		return algo;
	}

	private AlgoIntersectPlaneCurve getIntersectionAlgorithmCurve(
			GeoCoordSys2D A, GeoCurveCartesianND B, String[] labels) {
		AlgoElement existingAlgo = kernel.getAlgoDispatcher()
				.findExistingIntersectionAlgorithm(A, B);
		if (existingAlgo != null) {
			return (AlgoIntersectPlaneCurve) existingAlgo;
		}

		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectPlaneCurve algo = new AlgoIntersectPlaneCurve(cons, A, B,
				labels);
		algo.setPrintedInXML(false);
		kernel.getAlgoDispatcher().addIntersectionAlgorithm(algo); // remember
																	// this
																	// algorithm
		return algo;
	}

	@Override
	public GeoPointND[] intersectPlaneConic(String[] labels, GeoCoordSys2D A,
			GeoConicND B) {
		AlgoIntersectPlaneConic algo = getIntersectionAlgorithm(A, B);
		algo.setPrintedInXML(true);
		GeoPoint3D[] points = algo.getIntersectionPoints();
		LabelManager.setLabels(labels, points);
		return points;
	}

	@Override
	public GeoElementND[] intersectPlaneCurve(String[] labels, GeoCoordSys2D A,
			GeoCurveCartesianND B) {
		AlgoIntersectPlaneCurve algo = getIntersectionAlgorithmCurve(A, B,
				labels);
		algo.setPrintedInXML(true);
		GeoElementND[] points = algo.getOutput();

		return points;
	}

	@Override
	final public GeoElement intersectPlanes(String label, GeoPlaneND cs1,
			GeoPlaneND cs2) {

		AlgoIntersectPlanes algo = new AlgoIntersectPlanes(cons, label, cs1,
				cs2);
		return algo.getIntersection();
	}

	@Override
	final public GeoElement intersectPlanes(GeoPlaneND cs1, GeoPlaneND cs2) {

		AlgoIntersectPlanes algo = new AlgoIntersectPlanes(cons, cs1, cs2);
		return algo.getIntersection();
	}

	@Override
	public GeoElement closestPoint(String label, GeoLineND g, GeoLineND h) {
		AlgoClosestPointLines3D algo = new AlgoClosestPointLines3D(cons, label,
				g, h);
		return algo.getPoint();
	}

	@Override
	public GeoPoint3D closestPoint(String label, Path p, GeoPointND P) {
		AlgoClosestPoint3D algo = new AlgoClosestPoint3D(cons, p, P);
		algo.getP().setLabel(label);
		return (GeoPoint3D) algo.getP();
	}

	@Override
	public GeoPointND closestPoint(String label, Region r, GeoPointND P) {
		AlgoClosestPointToRegion3D algo = new AlgoClosestPointToRegion3D(cons,
				label, r, P);
		return algo.getOutputPoint();
	}

	/********************************************************************
	 * MEASURES (lengths, angles)
	 ********************************************************************/

	@Override
	final public GeoAngle angle3D(String label, GeoPointND A, GeoPointND B,
			GeoPointND C) {
		AlgoAnglePoints3D algo = new AlgoAnglePoints3D(cons, A, B, C);
		GeoAngle angle = algo.getAngle();
		angle.setLabel(label);
		return angle;
	}

	@Override
	final public GeoAngle angle3D(String label, GeoPointND A, GeoPointND B,
			GeoPointND C, GeoDirectionND orientation) {
		AlgoAnglePoints3DOrientation algo = new AlgoAnglePoints3DOrientation(
				cons, A, B, C, orientation);
		GeoAngle angle = algo.getAngle();
		angle.setLabel(label);
		return angle;
	}

	@Override
	final public GeoElement[] angle(String[] labels, GeoPointND B, GeoPointND A,
			GeoNumberValue alpha, GeoDirectionND orientation,
			boolean posOrientation) {
		// this is actually a macro
		String pointLabel = null, angleLabel = null;
		if (labels != null) {
			switch (labels.length) {
			case 2:
				angleLabel = labels[0];
				pointLabel = labels[1];
				break;
			case 1:
				angleLabel = labels[0];
				break;
			default:
			}
		}

		// rotate B around A using angle alpha
		GeoPointND C = (GeoPointND) rotate3D(pointLabel, B, alpha,
				A, orientation)[0];

		// create angle according to orientation
		GeoAngle angle;
		if (posOrientation) {
			angle = angle3D(angleLabel, B, A, C, orientation);
		} else {
			angle = angle3D(angleLabel, C, A, B, orientation);
		}

		// return angle and new point
		GeoElement[] ret = { angle, (GeoElement) C };
		return ret;
	}

	@Override
	final public GeoAngle angle3D(String label, GeoLineND g, GeoLineND h) {
		AlgoAngleLines3D algo = new AlgoAngleLines3D(cons, g, h);
		GeoAngle angle = algo.getAngle();
		angle.setLabel(label);
		return angle;
	}

	@Override
	final public GeoAngle angle3D(String label, GeoLineND g, GeoLineND h,
			GeoDirectionND orientation) {
		AlgoAngleLines3D algo = new AlgoAngleLines3DOrientation(cons, g,
				h, orientation);
		GeoAngle angle = algo.getAngle();
		angle.setLabel(label);
		return angle;
	}

	@Override
	final public GeoAngle angle3D(String label, GeoPlaneND p1, GeoPlaneND p2) {
		AlgoAnglePlanes algo = new AlgoAnglePlanes(cons, (GeoPlane3D) p1,
				(GeoPlane3D) p2);
		GeoAngle angle = algo.getAngle();
		angle.setLabel(label);
		return angle;
	}

	@Override
	final public GeoAngle angle3D(String label, GeoLineND l, GeoPlaneND p) {
		AlgoAngleLinePlane algo = new AlgoAngleLinePlane(cons, label, l,
				(GeoPlane3D) p);
		GeoAngle angle = algo.getAngle();
		return angle;
	}

	@Override
	public GeoAngle createLineAngle(GeoLineND line1, GeoLineND line2) {
		GeoAngle angle = null;

		// did we get two segments?
		if ((line1 instanceof GeoSegmentND)
				&& (line2 instanceof GeoSegmentND)) {
			// check if the segments have one point in common
			GeoSegmentND a = (GeoSegmentND) line1;
			GeoSegmentND b = (GeoSegmentND) line2;
			// get endpoints
			GeoPointND a1 = a.getStartPoint();
			GeoPointND a2 = a.getEndPoint();
			GeoPointND b1 = b.getStartPoint();
			GeoPointND b2 = b.getEndPoint();

			if (a1 == b1) {
				angle = angle3D(null, a2, a1, b2);
			} else if (a1 == b2) {
				angle = angle3D(null, a2, a1, b1);
			} else if (a2 == b1) {
				angle = angle3D(null, a1, a2, b2);
			} else if (a2 == b2) {
				angle = angle3D(null, a1, a2, b1);
			}
		}

		if (angle == null) {
			angle = angle3D(null, line1, line2);
		}

		return angle;
	}

	@Override
	public GeoAngle createLineAngle(GeoLineND line1, GeoLineND line2,
			GeoDirectionND orientation) {
		GeoAngle angle = null;

		// did we get two segments?
		if ((line1 instanceof GeoSegmentND)
				&& (line2 instanceof GeoSegmentND)) {
			// check if the segments have one point in common
			GeoSegmentND a = (GeoSegmentND) line1;
			GeoSegmentND b = (GeoSegmentND) line2;
			// get endpoints
			GeoPointND a1 = a.getStartPoint();
			GeoPointND a2 = a.getEndPoint();
			GeoPointND b1 = b.getStartPoint();
			GeoPointND b2 = b.getEndPoint();

			if (a1 == b1) {
				angle = angle3D(null, a2, a1, b2, orientation);
			} else if (a1 == b2) {
				angle = angle3D(null, a2, a1, b1, orientation);
			} else if (a2 == b1) {
				angle = angle3D(null, a1, a2, b2, orientation);
			} else if (a2 == b2) {
				angle = angle3D(null, a1, a2, b1, orientation);
			}
		}

		if (angle == null) {
			angle = angle3D(null, line1, line2, orientation);
		}

		return angle;
	}

	@Override
	final public GeoAngle angle3D(String label, GeoVectorND v, GeoVectorND w) {
		AlgoAngleVectors3D algo = new AlgoAngleVectors3D(cons, v, w);
		GeoAngle angle = algo.getAngle();
		angle.setLabel(label);
		return angle;
	}

	@Override
	final public GeoAngle angle3D(String label, GeoVectorND v, GeoVectorND w,
			GeoDirectionND orientation) {
		AlgoAngleVectors3D algo = new AlgoAngleVectors3DOrientation(cons,
				v, w, orientation);
		GeoAngle angle = algo.getAngle();
		angle.setLabel(label);
		return angle;
	}

	@Override
	final public GeoElement[] angles3D(String[] labels, GeoPolygon poly) {
		AlgoAnglePolygon3D algo = new AlgoAnglePolygon3D(cons, labels, poly);
		GeoElement[] angles = algo.getAngles();
		return angles;
	}

	@Override
	final public GeoElement[] angles3D(String[] labels, GeoPolygon poly,
			GeoDirectionND orientation) {
		AlgoAnglePolygon3D algo = new AlgoAnglePolygon3DOrientation(cons,
				labels, poly, orientation);
		GeoElement[] angles = algo.getAngles();
		return angles;
	}

	@Override
	public GeoElement[] angles3D(String[] labels, GeoPolygon poly, boolean internalAngle) {
		AlgoAnglePolygon3D algo = new AlgoAnglePolygon3D(cons, labels, poly, internalAngle);
		GeoElement[] angles = algo.getAngles();
		return angles;
	}

	/**
	 * Length named label of vector v
	 * 
	 * @param label
	 *            label
	 * @param v
	 *            vector
	 * @return length of the vector
	 */
	@Override
	final public GeoNumeric length(String label, GeoVectorND v) {
		AlgoLengthVector3D algo = new AlgoLengthVector3D(cons, label, v);
		GeoNumeric num = algo.getLength();
		return num;
	}

	@Override
	final public GeoElement[] archimedeanSolid(String[] labels, GeoPointND A,
			GeoPointND B, GeoDirectionND v, Commands name) {
		AlgoArchimedeanSolid algo = new AlgoArchimedeanSolid(cons, labels, A, B,
				v, name);
		return algo.getOutput();
	}

	@Override
	public GeoElement[] archimedeanSolid(String[] labels, GeoPolygon poly,
			GeoBoolean direct, Commands name) {
		AlgoArchimedeanSolid algo = new AlgoArchimedeanSolid(cons, labels, poly,
				direct, name);
		return algo.getOutput();
	}

	@Override
	final public GeoElement[] archimedeanSolid(String[] labels, GeoPointND A,
			GeoPointND B, GeoPointND C, Commands name) {
		AlgoArchimedeanSolidThreePoints algo = new AlgoArchimedeanSolidThreePoints(
				cons, labels, A, B, C, name);
		return algo.getOutput();
	}

	@Override
	final public GeoElement[] archimedeanSolid(String[] labels, GeoPointND A,
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
					(GeoPoint) A, (GeoPoint) B, null, false);
			// cons.removeFromConstructionList(algoSegment);
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
				AlgoMidpoint algoMidpoint = new AlgoMidpoint(cons, (GeoPoint) A,
						(GeoPoint) B);
				cons.removeFromConstructionList(algoMidpoint);
				center = algoMidpoint.getPoint();
			}

			// radius = distance * sqrt(3)/2
			ExpressionNode expr = new ExpressionNode(kernel,
					new MyDouble(kernel, 3), Operation.SQRT, null);
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
			ExpressionNode exprSqrt5 = new ExpressionNode(kernel,
					new MyDouble(kernel, 5), Operation.SQRT, null);

			expr = new ExpressionNode(kernel, new MyDouble(kernel, 1),
					Operation.NO_OPERATION, null);
			ExpressionNode exprPoint = new ExpressionNode(kernel, A,
					Operation.NO_OPERATION, null);

			// order important
			// 2(3,4,5) is a Vector (when re-loaded from XML)
			// (3,4,5)2 is a Point
			expr = (expr.subtract(exprSqrt5)).multiply(exprPoint);

			ExpressionNode expr2 = new ExpressionNode(kernel,
					new MyDouble(kernel, 3), Operation.NO_OPERATION, null);
			exprPoint = new ExpressionNode(kernel, B, Operation.NO_OPERATION,
					null);

			// order important
			// 2(3,4,5) is a Vector (when re-loaded from XML)
			// (3,4,5)2 is a Point
			expr2 = (expr2.plus(exprSqrt5)).multiply(exprPoint);

			expr = expr.plus(expr2).divide(4);

			if (A.isGeoElement3D() || B.isGeoElement3D()) {
				AlgoDependentPoint3D exprAlgoPoint = new AlgoDependentPoint3D(
						cons, expr, false);
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
		createTmpCoords();
		tmpCoords2.setSub3(cB, cA);
		tmpCoords2.completeOrthonormalKeepInXOYPlaneIfPossible3(tmpCoords3);
		tmpCoords3.mulInside(radius.getDouble());
		tmpCoords.setAdd3(center.getInhomCoordsInD(3), tmpCoords3);

		AlgoPoint3DOnPath algoPoint = new AlgoPoint3DOnPath(cons,
				algoCircle.getCircle(), tmpCoords.getX(), tmpCoords.getY(),
				tmpCoords.getZ());
		algoPoint.getP().setLabel(null);
		((GeoElement) algoPoint.getP()).setAuxiliaryObject(Auxiliary.YES_SAVE);

		// create solid
		AlgoArchimedeanSolidThreePoints algo = new AlgoArchimedeanSolidThreePoints(
				cons, labels, A, B, algoPoint.getP(), name);

		return algo.getOutput();
	}

	final private void createTmpCoords() {
		if (tmpCoords == null) {
			tmpCoords = new Coords(3);
			tmpCoords2 = new Coords(3);
			tmpCoords3 = new Coords(3);
		}
	}

	@Override
	public GeoNumeric distance(String label, GeoPointND point,
			GeoPlaneND plane) {
		AlgoDistancePointPlane3D algo = new AlgoDistancePointPlane3D(cons,
				point, plane);
		algo.getDistance().setLabel(label);
		return algo.getDistance();
	}

	/********************************************************************
	 * TRANSFORMATIONS
	 ********************************************************************/

	@Override
	final public GeoElement[] translate3D(String label, GeoElementND geoTrans,
			GeoVectorND v) {
		Transform t = new TransformTranslate3D(cons, v);
		return t.transform(geoTrans, label);
	}

	@Override
	public Geo3DVecInterface newGeo3DVec(double x, double y, double z) {
		return new Geo3DVec(kernel, x, y, z);
	}

	@Override
	final public GeoElement[] rotate3D(String label, GeoElementND geoRot,
			GeoNumberValue phi, GeoPointND center, GeoDirectionND orientation) {
		Transform t = new TransformRotate3D(cons, phi, center, orientation);
		return t.transform(geoRot, label);
	}

	@Override
	final public GeoElement[] rotate3D(String label, GeoElementND geoRot,
			GeoNumberValue phi, GeoLineND line) {
		Transform t = new TransformRotate3D(cons, phi, line);
		return t.transform(geoRot, label);
	}

	@Override
	final public GeoElement[] mirror3D(String label, GeoElement geo,
			GeoPointND p) {
		Transform t = new TransformMirror3D(cons, p);
		return t.transform(geo, label);
	}

	@Override
	final public GeoElement[] mirror3D(String label, GeoElement geo,
			GeoLineND line) {
		Transform t = new TransformMirror3D(cons, line);
		return t.transform(geo, label);
	}

	@Override
	final public GeoElement[] mirror3D(String label, GeoElement geo,
			GeoCoordSys2D plane) {
		Transform t = new TransformMirror3D(cons, plane);
		return t.transform(geo, label);
	}

	@Override
	final public GeoElement[] dilate3D(String label, GeoElement geoDil,
			GeoNumberValue r, GeoPointND S) {

		Transform t = new TransformDilate3D(cons, r, S);
		return t.transform(geoDil, label);
	}

	@Override
	final public GeoNumeric volume(String label, HasVolume hasVolume) {
		AlgoVolume algo = new AlgoVolume(cons, label, hasVolume);
		return algo.getVolume();
	}

	@Override
	final public GeoNumeric orientedHeight(String label, HasHeight hasHeight) {
		AlgoOrientedHeight algo = new AlgoOrientedHeight(cons, hasHeight);
		algo.getOrientedHeight().setLabel(label);
		return algo.getOrientedHeight();
	}

	@Override
	final public GeoPoint3D[] corner(String[] labels, GeoConicND conic) {
		AlgoCornerConicSection algo = new AlgoCornerConicSection(cons, labels,
				(GeoConicSection) conic);
		return algo.getCorners();
	}

	@Override
	final public GeoElement[] regularPolygon(String[] labels, GeoPointND A,
			GeoPointND B, GeoNumberValue n, GeoDirectionND direction) {
		AlgoPolygonRegular3D algo = new AlgoPolygonRegular3D(cons, labels, A, B,
				n, direction);
		return algo.getOutput();
	}

	@Override
	public GeoElement[] polyhedronNet(String[] labels, GeoElement p,
			NumberValue v, GeoPolygon bottomFace,
			GeoSegmentND[] pivotSegments) {

		AlgoElement algo;

		/*
		 * TODO comment this, uncomment below (when cutting edges work for
		 * pyramid and prism
		 */
		switch (((GeoPolyhedron) p).getType()) {

		case GeoPolyhedron.TYPE_PYRAMID:
			algo = new AlgoPolyhedronNetPyramid(cons, labels, (GeoPolyhedron) p,
					v);
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

	@Override
	public GeoElement[] polyhedronConvex(String[] labels,
			GeoElement[] pointList) {
		AlgoElement algo = new AlgoPolyhedronConvex(cons, labels, pointList);
		return algo.getOutput();
	}

	/**
	 * circle arc from three points
	 */
	@Override
	final public GeoConicPart3D circumcircleArc3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C) {
		AlgoConicPartCircumcircle3D algo = new AlgoConicPartCircumcircle3D(cons,
				label, A, B, C, GeoConicNDConstants.CONIC_PART_ARC);
		return algo.getConicPart();
	}

	/**
	 * circle sector from three points
	 */
	@Override
	final public GeoConicPart3D circumcircleSector3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C) {
		AlgoConicPartCircumcircle3D algo = new AlgoConicPartCircumcircle3D(cons,
				label, A, B, C, GeoConicNDConstants.CONIC_PART_SECTOR);
		return algo.getConicPart();
	}

	@Override
	final public GeoElement[] angularBisector3D(String[] labels, GeoLineND g,
			GeoLineND h) {
		AlgoAngularBisectorLines3D algo = new AlgoAngularBisectorLines3D(cons,
				labels, g, h);
		GeoLine3D[] lines = algo.getLines();
		return lines;
	}

	@Override
	final public GeoLine3D angularBisector3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C) {
		AlgoAngularBisectorPoints3D algo = new AlgoAngularBisectorPoints3D(cons,
				label, A, B, C);
		return algo.getLine();
	}

	@Override
	final public GeoLine3D angularBisector3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C, GeoDirectionND orientation) {
		AlgoAngularBisectorPoints3DOrientation algo = new AlgoAngularBisectorPoints3DOrientation(
				cons, label, A, B, C, orientation);
		return algo.getLine();
	}

	@Override
	final public GeoConicPart3D circleArcSector3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C, int type) {
		AlgoConicPartCircle3D algo = new AlgoConicPartCircle3D(cons, label, A,
				B, C, type);
		return algo.getConicPart();
	}

	@Override
	final public GeoConicPartND circleArcSector3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C, GeoDirectionND orientation, int type) {
		if (A.isGeoElement3D()
				|| B.isGeoElement3D() || C.isGeoElement3D()) { // at least one
																// 3D geo
			if (orientation == kernel.getSpace()) { // space is default
													// orientation for 3D
													// objects
				return circleArcSector3D(null, A, B, C, type);
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
			return kernel.getAlgoDispatcher().circleArcSector(label,
					(GeoPoint) A, (GeoPoint) B, (GeoPoint) C, type);
		}

		// use view orientation
		AlgoConicPartCircle3D algo = new AlgoConicPartCircle3DOrientation(cons,
				label, A, B, C, orientation, type);
		return algo.getConicPart();
	}

	@Override
	public GeoLine3D line3D(String label, ExpressionValue[] coefX,
			ExpressionValue[] coefY, ExpressionValue[] coefZ) {
		MyVec3DNode start = new MyVec3DNode(kernel, coefX[0], coefY[0],
				coefZ[0]);
		MyVec3DNode v = new MyVec3DNode(kernel, coefX[1], coefY[1], coefZ[1]);
		AlgoDependentPoint3D pt = new AlgoDependentPoint3D(cons, start.wrap(),
				false);
		AlgoDependentVector3D vec = new AlgoDependentVector3D(cons, v.wrap());
		cons.removeFromConstructionList(vec);
		AlgoLinePointVector3D algo = new AlgoLinePointVector3D(cons,
				pt.getPoint3D(), vec.getVector3D());
		GeoLine3D g = algo.getLine();
		g.setLabel(label);
		return g;
	}

	@Override
	final public GeoConicPartND semicircle3D(String label, GeoPointND A,
			GeoPointND B, GeoDirectionND orientation) {
		if (A.isGeoElement3D() || B.isGeoElement3D()) { // at least one 3D geo
			// use view orientation
			AlgoSemicircle3D algo = new AlgoSemicircle3D(cons, label, A, B,
					orientation);
			return algo.getSemicircle();
		}

		// 2D geos
		if (orientation == kernel.getXOYPlane()) { // xOy plane is default
													// orientation for 2D
													// objects
			return kernel.getAlgoDispatcher().semicircle(label, (GeoPoint) A,
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
	@Override
	final public GeoElement[] tangent3D(String[] labels, GeoPointND P,
			GeoConicND c) {
		if (P.isGeoElement3D() || c.isGeoElement3D()) {
			AlgoTangentPoint3D algo = new AlgoTangentPoint3D(cons, labels, P,
					c);
			return algo.getOutput();
		}

		return kernel.getAlgoDispatcher().tangent(labels, P, c);
	}

	@Override
	public GeoElement[] tangent3D(String[] labels, GeoLineND l, GeoConicND c) {
		if (l.isGeoElement3D() || c.isGeoElement3D()) {
			AlgoTangentLine3D algo = new AlgoTangentLine3D(cons, labels, l, c);
			return algo.getOutput();
		}

		return kernel.getAlgoDispatcher().tangent(labels, l, c);
	}

	/**
	 * common tangents to c1 and c2 dsun48 [6/26/2011]
	 */
	@Override
	final public GeoElement[] commonTangents3D(String[] labels, GeoConicND c1,
			GeoConicND c2) {
		if (c1.isGeoElement3D() || c2.isGeoElement3D()) {
			AlgoCommonTangents3D algo = new AlgoCommonTangents3D(cons, labels,
					c1, c2);
			return algo.getOutput();
		}

		return kernel.getAlgoDispatcher().commonTangents(labels, c1, c2);
	}

	/**
	 * diameter line conjugate to direction of g relative to c
	 */
	@Override
	final public GeoElement diameterLine3D(String label, GeoLineND g,
			GeoConicND c) {

		if (g.isGeoElement3D() || c.isGeoElement3D()) {
			AlgoDiameterLine3D algo = new AlgoDiameterLine3D(cons, label, c, g);
			return (GeoElement) algo.getDiameter();
		}

		return kernel.getAlgoDispatcher().diameterLine(label, g, c);
	}

	/**
	 * diameter line conjugate to v relative to c
	 */
	@Override
	final public GeoElement diameterLine3D(String label, GeoVectorND v,
			GeoConicND c) {

		if (v.isGeoElement3D() || c.isGeoElement3D()) {
			AlgoDiameterVector3D algo = new AlgoDiameterVector3D(cons, label, c,
					v);
			return (GeoElement) algo.getDiameter();
		}

		return kernel.getAlgoDispatcher().diameterLine(label, v, c);
	}

	@Override
	final public GeoElement lineBisector3D(String label, GeoSegmentND segment,
			GeoDirectionND orientation) {

		if (!segment.isGeoElement3D() // 2D geo
				&& orientation == kernel.getXOYPlane()) { // xOy plane is
															// default
															// orientation for
															// 2D objects
			return kernel.getAlgoDispatcher().lineBisector(label,
					(GeoSegment) segment);
		}

		AlgoLineBisectorSegmentDirection3D algo = new AlgoLineBisectorSegmentDirection3D(
				cons, label, segment, orientation);
		return algo.getLine();
	}

	@Override
	final public GeoElement lineBisector3D(String label, GeoPointND a,
			GeoPointND b, GeoDirectionND orientation) {

		if (!a.isGeoElement3D() && !b.isGeoElement3D() // 2D geo
				&& orientation == kernel.getXOYPlane()) { // xOy plane is
															// default
															// orientation for
															// 2D objects
			return kernel.getAlgoDispatcher().lineBisector(label, (GeoPoint) a,
					(GeoPoint) b);
		}

		AlgoLineBisectorTwoPointsDirection3D algo = new AlgoLineBisectorTwoPointsDirection3D(
				cons, label, a, b, orientation);
		return algo.getLine();
	}

	@Override
	final public GeoConicND conic3D(String label, GeoPointND[] points) {
		AlgoConicFivePoints3D algo = new AlgoConicFivePoints3D(cons, points);
		algo.getConic().setLabel(label);
		return algo.getConic();
	}

	@Override
	final public GeoConicND ellipseHyperbola3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C, final int type) {
		AlgoEllipseHyperbolaFociPoint3D algo = new AlgoEllipseHyperbolaFociPoint3D(
				cons, label, A, B, C, type);

		return algo.getConic();
	}

	@Override
	final public GeoConicND ellipseHyperbola3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C, GeoDirectionND orientation,
			final int type) {

		if (!A.isGeoElement3D() && !B.isGeoElement3D() && !C.isGeoElement3D() // 2D
																				// geo
				&& orientation == kernel.getXOYPlane()) { // xOy plane is
															// default
															// orientation for
															// 2D objects
			return kernel.getAlgoDispatcher().ellipseHyperbola(null, A, B, C,
					type);
		}

		if (orientation == kernel.getSpace()) { // space is default orientation
												// for 2D objects
			return ellipseHyperbola3D(null, A, B, C, type);
		}

		AlgoEllipseHyperbolaFociPoint3DOriented algo = new AlgoEllipseHyperbolaFociPoint3DOriented(
				cons, label, A, B, C, orientation, type);

		return algo.getConic();
	}

	@Override
	final public GeoConicND parabola3D(String label, GeoPointND F,
			GeoLineND l) {
		AlgoParabolaPointLine3D algo = new AlgoParabolaPointLine3D(cons, label,
				F, l);
		return algo.getParabola();
	}

	@Override
	final public GeoElement locus3D(String label, GeoPointND Q, GeoPointND P) {
		if (!AlgoDispatcher.locusCheck(P, Q)) {
			return null;
		}

		return (new AlgoLocus3D(cons, label, Q, P)).getLocus();
	}

	@Override
	public GeoElement tangent3D(String label, GeoPointND point,
			GeoCurveCartesianND curve) {

		if (curve.isGeoElement3D()) {
			AlgoTangentCurve3D algo = new AlgoTangentCurve3D(cons, label, point,
					(GeoCurveCartesian3D) curve);
			algo.update();
			return algo.getOutput()[0];
		}

		return kernel.tangent(label, point, (GeoCurveCartesian) curve);
	}

	@Override
	public GeoElement locus3D(String label, GeoPointND Q, GeoNumeric slider) {
		return new AlgoLocusSlider3D(cons, label, Q, slider).getLocus();
	}

	/**
	 * intersection of polygons 3D
	 * 
	 * @author thilina
	 */
	@Override
	public GeoElement[] intersectPolygons(String[] labels, GeoPoly inPoly0,
			GeoPoly inPoly1) {

		AlgoIntersectPathPolygons3D algo = new AlgoIntersectPathPolygons3D(cons,
				labels, inPoly0, inPoly1);

		return algo.getOutput();
	}

	/**
	 * difference of polygons 3D
	 * 
	 * @author thilina
	 */
	@Override
	public GeoElement[] differencePolygons(String[] labels,
			GeoPolygon inPoly0, GeoPolygon inPoly1) {
		AlgoDifferencePolygons3D algo = new AlgoDifferencePolygons3D(cons,
				labels, inPoly0, inPoly1);
		return algo.getOutput();
	}

	/**
	 * exclusive or normal difference of polygons 3D,
	 * 
	 * @author thilina
	 */

	@Override
	public GeoElement[] differencePolygons(String[] labels,
			GeoPolygon inPoly0, GeoPolygon inPoly1, GeoBoolean exclusive) {
		AlgoDifferencePolygons3D algo = new AlgoDifferencePolygons3D(cons,
				labels, inPoly0, inPoly1, exclusive);
		return algo.getOutput();
	}

	/**
	 * Union of polygons 3D
	 * 
	 * @author thilina
	 */
	@Override
	public GeoElement[] unionPolygons(String[] labels, GeoPoly inPoly0,
			GeoPoly inPoly1) {
		AlgoUnionPolygons3D algo = new AlgoUnionPolygons3D(cons, labels,
				inPoly0, inPoly1);
		return algo.getOutput();
	}

	/**
	 * Intersect points of polygons 3D
	 * 
	 * @author thilina
	 */
	@Override
	public GeoElement[] intersectionPoint(String[] labels, GeoPolygon poly0,
			GeoPolygon poly1) {
		AlgoIntersectPolygons3D algo = new AlgoIntersectPolygons3D(cons, labels,
				poly0, poly1);
		return algo.getOutput();
	}

	@Override
	public GeoNumeric distance(String label, GeoPlaneND a, GeoPlaneND b) {
		AlgoDistancePlanes algo = new AlgoDistancePlanes(cons, a, b);
		algo.getDistance().setLabel(label);
		return algo.getDistance();
	}

	@Override
	public GeoElement[] intersectPlaneImplicitSurface(GeoPlaneND plane,
			GeoImplicitSurfaceND surface) {
		return new AlgoIntersectImplicitSurfacePlane(cons,
				(GeoImplicitSurface) surface, plane).getOutput();
	}
}
