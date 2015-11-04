package org.geogebra.common.kernel;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.kernel.Matrix.CoordMatrix;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPolygon;
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
import org.geogebra.common.kernel.kernelND.GeoRayND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.kernelND.HasHeight;
import org.geogebra.common.kernel.kernelND.HasVolume;

/**
 * Interface for managing all 3D methods in Kernel. <br/>
 * See Also {@link org.geogebra.common.geogebra3D.kernel3D.algos.Manager3D}.
 * 
 * @author mathieu
 * 
 */
@SuppressWarnings("javadoc")
public interface Manager3DInterface {

	/** Point3D label with cartesian coordinates (x,y,z) */
	public GeoElement Point3D(String label, double x, double y, double z,
			boolean coords2D);

	public GeoElement Point3D(double x, double y, double z, boolean coords2D);

	/**
	 * Point dependent on arithmetic expression with variables, represented by a
	 * tree. e.g. P = (4t, 2s)
	 */
	public GeoElement DependentPoint3D(String label, ExpressionNode root);

	public GeoElement DependentVector3D(String label, ExpressionNode root);

	public GeoElement Vector3D(String label, double x, double y, double z);

	/**
	 * Vector named label from Point P to Q
	 */
	public GeoElement Vector3D(String label, GeoPointND P, GeoPointND Q);

	/** Point in region with cartesian coordinates (x,y,z) */
	public GeoPointND Point3DIn(String label, Region region, Coords coords,
			boolean addToConstruction, boolean coords2D);

	public GeoPointND Point3DIn(Region region, Coords coords, boolean coords2D);

	/** Point in region */
	public GeoPointND Point3DIn(String label, Region region, boolean coords2D);

	/** Point3D on a 1D path with cartesian coordinates (x,y,z) */
	public GeoPointND Point3D(String label, Path path, double x, double y,
			double z, boolean addToConstruction, boolean coords2D);

	/** Point3D on a 1D path without cartesian coordinates */
	public GeoPointND Point3D(String label, Path path, boolean coords2D);

	/** Point3D on a 1D path with path parameter */
	public GeoPointND Point3D(String label, Path path, NumberValue param);

	/**
	 * Midpoint M = (P + Q)/2
	 * 
	 * @param label
	 * @param P
	 * @param Q
	 * @return midpoint
	 */
	public GeoPointND Midpoint(String label, GeoPointND P, GeoPointND Q);

	/**
	 * Midpoint of segment
	 * 
	 * @param label
	 * @param segment
	 * @return midpoint
	 */
	public GeoPointND Midpoint(String label, GeoSegmentND segment);

	/**
	 * Center of conic
	 * 
	 * @param label
	 * @param conic
	 * @return center
	 */
	public GeoPointND Center(String label, GeoConicND conic);

	/**
	 * Center of quadric
	 * 
	 * @param label
	 * @param quadric
	 * @return center
	 */
	public GeoPointND CenterQuadric(String label, GeoQuadricND quadric);

	/** Segment3D label linking points v1 and v2 */
	/*
	 * public GeoSegment3D Segment3D(String label, Ggb3DVector v1, Ggb3DVector
	 * v2){ GeoSegment3D s = new GeoSegment3D(cons,v1,v2); s.setLabel(label);
	 * return s; }
	 */

	/** Segment3D label linking points P1 and P2 */
	public GeoSegmentND Segment3D(String label, GeoPointND P1, GeoPointND P2);

	/** Line3D label linking points P1 and P2 */
	public GeoElement Line3D(String label, GeoPointND P1, GeoPointND P2);

	/** Line3D label through point P and parallel to line l */
	public GeoLineND Line3D(String label, GeoPointND P, GeoLineND l);

	/** Line3D label through point P and parallel to vector v */
	public GeoLineND Line3D(String label, GeoPointND P, GeoVectorND v);

	/** Ray3D label linking points P1 and P2 */
	public GeoRayND Ray3D(String label, GeoPointND P1, GeoPointND P2);

	/** Line3D through point orthogonal to plane */
	public GeoLineND OrthogonalLine3D(String label, GeoPointND point,
			GeoCoordSys2D plane);

	/** Line3D through point orthogonal to line */
	public GeoLineND OrthogonalLine3D(String label, GeoPointND point,
			GeoLineND line);

	/** Line3D through point orthogonal to line and direction */
	public GeoLineND OrthogonalLine3D(String label, GeoPointND point,
			GeoDirectionND line, GeoDirectionND direction);

	/** Line3D orthogonal two lines */
	public GeoLineND OrthogonalLine3D(String label, GeoLineND line1,
			GeoLineND line2);

	/** Vector3D orthogonal to plane */
	public GeoVectorND OrthogonalVector3D(String label, GeoCoordSys2D plane);

	/** Vector3D orthogonal to line with direction */
	public GeoVectorND OrthogonalVector3D(String label, GeoLineND line,
			GeoDirectionND direction);

	/** Vector3D unit orthogonal to plane */
	public GeoVectorND UnitOrthogonalVector3D(String label, GeoCoordSys2D plane);

	/**
	 * Polygon3D linking points P1, P2, ...
	 * 
	 * @param label
	 *            name of the polygon
	 * @param points
	 *            vertices of the polygon
	 * @return the polygon
	 */
	public GeoElement[] Polygon3D(String[] label, GeoPointND[] points);

	/**
	 * Polygon3D linking points P1, P2, ...
	 * 
	 * @param label
	 *            name of the polygon
	 * @param points
	 *            vertices of the polygon
	 * @return the polygon
	 */
	public GeoElement[] Polygon3D(String[] label, GeoPointND[] points,
			GeoDirectionND direction);

	/**
	 * Regular polygon with vertices A and B and n total vertices. The labels
	 * name the polygon itself, its segments and points
	 */
	public GeoElement[] RegularPolygon(String[] labels, GeoPointND A,
			GeoPointND B, NumberValue n, GeoDirectionND direction);

	public GeoElement[] PolyLine3D(String[] labels, GeoPointND[] P);

	public GeoElement[] PolyLine3D(String[] labels, GeoList pointList);

	/**
	 * Prism with vertices (last one is first vertex of second parallel face)
	 * 
	 * @param labels
	 *            names
	 * @param points
	 *            vertices
	 * @return the polyhedron
	 */
	public GeoElement[] Prism(String[] labels, GeoPointND[] points);

	/**
	 * Prism with basis and first vertex of second parallel face
	 * 
	 * @param labels
	 * @param polygon
	 * @param point
	 * @return the polyhedron
	 */
	public GeoElement[] Prism(String[] labels, GeoPolygon polygon,
			GeoPointND point);

	/**
	 * Right prism with basis and height
	 * 
	 * @param labels
	 * @param polygon
	 * @param height
	 * @return the polyhedron
	 */
	public GeoElement[] Prism(String[] labels, GeoPolygon polygon,
			NumberValue height);

	/**
	 * Pyramid with vertices (last one as apex)
	 * 
	 * @param labels
	 *            names
	 * @param points
	 *            vertices
	 * @return the polyhedron
	 */
	public GeoElement[] Pyramid(String[] labels, GeoPointND[] points);

	/**
	 * Pyramid with basis and top vertex
	 * 
	 * @param labels
	 *            labels
	 * @param polygon
	 *            bottom face
	 * @param point
	 *            top vertex
	 * @return the polyhedron
	 */
	public GeoElement[] Pyramid(String[] labels, GeoPolygon polygon,
			GeoPointND point);

	/**
	 * pyramid with top point over center of bottom face
	 * 
	 * @param labels
	 * @param polygon
	 * @param height
	 */
	public GeoElement[] Pyramid(String[] labels, GeoPolygon polygon,
			NumberValue height);

	/** Line a x + b y + c z + d = 0 named label */
	public GeoPlaneND Plane3D(String label, double a, double b, double c,
			double d);

	/**
	 * Plane dependent on coefficients of arithmetic expressions with variables,
	 * represented by trees.
	 */
	public GeoPlaneND DependentPlane3D(String label, Equation equ);

	/**
	 * Quadric dependent on coefficients of arithmetic expressions with
	 * variables, represented by trees.
	 */
	public GeoQuadricND DependentQuadric3D(String label, Equation equ);

	/**
	 * Plane named label through point and line
	 */
	public GeoPlaneND Plane3D(String label, GeoPointND point, GeoLineND line);

	/**
	 * Plane named label through point parallel to plane
	 */
	public GeoPlaneND Plane3D(String label, GeoPointND point, GeoCoordSys2D cs);

	/**
	 * Plane named label through Point P orthogonal to line l
	 */
	public GeoPlaneND OrthogonalPlane3D(String label, GeoPointND point,
			GeoLineND line);

	public GeoPlaneND OrthogonalPlane3D(String label, GeoPointND point,
			GeoVectorND vector);

	public GeoPlaneND PlaneBisector(String label, GeoPointND point1,
			GeoPointND point2);

	public GeoPlaneND PlaneBisector(String label, GeoSegmentND segment);

	/** Sphere label linking with center o and radius r */
	public GeoElement Sphere(String label, GeoPointND M, NumberValue r);

	/**
	 * Sphere with midpoint M through point P
	 */
	public GeoElement Sphere(String label, GeoPointND M, GeoPointND P);

	/**
	 * Cone
	 */
	public GeoQuadricND Cone(String label, GeoPointND origin,
			GeoVectorND direction, NumberValue angle);

	public GeoQuadricND Cone(String label, GeoPointND origin,
			GeoPointND secondPoint, NumberValue angle);

	public GeoQuadricND Cone(String label, GeoPointND origin, GeoLineND axis,
			NumberValue angle);

	public GeoElement[] ConeLimited(String[] labels, GeoPointND origin,
			GeoPointND secondPoint, NumberValue r);

	public GeoElement[] ConeLimited(String[] labels, GeoConicND bottom,
			NumberValue height);

	/**
	 * Cylinder
	 */
	public GeoQuadricND Cylinder(String label, GeoPointND origin,
			GeoVectorND direction, NumberValue r);

	public GeoQuadricND Cylinder(String label, GeoPointND origin,
			GeoPointND secondPoint, NumberValue r);

	public GeoQuadricND Cylinder(String label, GeoLineND axis, NumberValue r);

	public GeoElement[] CylinderLimited(String[] labels, GeoPointND origin,
			GeoPointND secondPoint, NumberValue r);

	public GeoElement[] CylinderLimited(String[] labels, GeoConicND bottom,
			NumberValue height);

	/**
	 * Limited quadrics
	 */
	public GeoQuadricND QuadricSide(String label, GeoQuadricND quadric);

	public GeoConicND QuadricBottom(String label, GeoQuadricND quadric);

	public GeoConicND QuadricTop(String label, GeoQuadricND quadric);

	/**
	 * circle through points A, B, C
	 */
	public GeoConicND Circle3D(String label, GeoPointND A, GeoPointND B,
			GeoPointND C);

	/**
	 * circle with axis through point
	 */
	public GeoConicND Circle3D(String label, GeoLineND axis, GeoPointND A);

	/**
	 * circle with point, radius, axis
	 */
	public GeoConicND Circle3D(String label, GeoPointND A, NumberValue radius,
			GeoDirectionND axis);

	/**
	 * circle with point, radius, axis orthogonal to xOy plane
	 */
	public GeoConicND Circle3D(String label, GeoPointND A, NumberValue radius);

	public GeoConicND Circle3D(String label, GeoPointND A, GeoPointND B,
			GeoDirectionND axis);

	/**
	 * plane through points A, B, C
	 */
	public GeoElement Plane3D(String label, GeoPointND A, GeoPointND B,
			GeoPointND C);

	/**
	 * plane through lines a, b
	 */
	public GeoElement Plane3D(String label, GeoLineND a, GeoLineND b);

	/**
	 * 
	 * @param label
	 * @param cs2D
	 * @return plane containing the 2D coord sys
	 */
	public GeoPlaneND Plane3D(String label, GeoCoordSys2D cs2D);

	public GeoPlaneND Plane3D(GeoCoordSys2D cs2D);

	// //////////////////////////////////////////////
	// INTERSECTION (POINTS)

	/**
	 * Calculate the intersection of two coord sys (eg line and plane).
	 * 
	 * @param label
	 *            name of the point
	 * @param cs1
	 *            first coord sys
	 * @param cs2
	 *            second coord sys
	 * @return point intersection
	 */
	public GeoElement Intersect(String label, GeoElement cs1, GeoElement cs2);

	/**
	 * Calculate the intersection of the line g with the region of p
	 */

	public GeoElement[] IntersectionPoint(String[] labels, GeoLineND g,
			GeoSurfaceFinite s);


	public GeoElement[] IntersectPath(String[] labels, GeoLineND g,
			GeoSurfaceFinite s);

	public GeoElement[] IntersectPath(String[] labels, GeoPlaneND plane,
			GeoElement s);

	public GeoElement[] IntersectPath(GeoPlaneND plane, GeoPolygon p);

	public GeoElement[] IntersectRegion(String[] labels, GeoPlaneND plane,
			GeoElement s, int[] outputSize);

	public GeoElement[] IntersectRegion(GeoPlaneND plane, GeoElement p);

	public GeoElement[] IntersectionPoint(String[] labels, GeoPlaneND p,
			GeoElement s);

	/**
	 * Calculate the intersection of plane and quadric
	 * 
	 * @param label
	 *            name of the point
	 * @param plane
	 * @param quadric
	 * @return conic intersection
	 */
	public GeoConicND Intersect(String label, GeoPlaneND plane,
			GeoQuadricND quadric);

	public GeoConicND IntersectQuadricLimited(String label, GeoPlaneND plane,
			GeoQuadricND quadric);

	public GeoConicND IntersectQuadricLimited(GeoPlaneND plane,
			GeoQuadricND quadric);

	public GeoConicND Intersect(GeoPlaneND plane, GeoQuadricND quadric);

	/**
	 * Calculate the intersection of two quadrics, if it's a conic
	 * 
	 * @param labels
	 * 
	 * @param quadric1
	 * @param quadric2
	 * @return conic intersection
	 */
	public GeoElement[] IntersectAsCircle(String[] labels,
			GeoQuadricND quadric1, GeoQuadricND quadric2);

	/**
	 * Calculate the intersection of two quadrics, if it's a conic
	 * 
	 * @param quadric1
	 * @param quadric2
	 * @return conic intersection
	 */
	public GeoElement[] IntersectAsCircle(GeoQuadricND quadric1,
			GeoQuadricND quadric2);

	// //////////////////////////////////////////////
	// FUNCTIONS (2 VARS)

	public GeoFunctionNVar Function2Var(String label, NumberValue zcoord,
			GeoNumeric localVarU, NumberValue Ufrom, NumberValue Uto,
			GeoNumeric localVarV, NumberValue Vfrom, NumberValue Vto);

	public GeoFunctionNVar Function2Var(String label, GeoFunctionNVar f,
			NumberValue xFrom, NumberValue xTo, NumberValue yFrom,
			NumberValue yTo);

	// //////////////////////////////////////////////
	// 3D CURVE (1 VAR)

	/**
	 * 3D Cartesian curve command: Curve[ <expression x-coord>, <expression
	 * y-coord>, <expression z-coord>, <number-var>, <from>, <to> ]
	 */
	public GeoElement CurveCartesian3D(NumberValue xcoord,
			NumberValue ycoord, NumberValue zcoord, GeoNumeric localVar,
			NumberValue from, NumberValue to);

	// //////////////////////////////////////////////
	// 3D SURFACE (2 VARS)

	/**
	 * 3D Cartesian surface command: Surface[ <expression x-coord>, <expression
	 * y-coord>, <expression z-coord>, <u-var>, <u-from>, <u-to>, <v-var>,
	 * <v-from>, <v-to> ]
	 */
	public GeoElement SurfaceCartesian3D(String label, ExpressionNode point,
			NumberValue xcoord,
			NumberValue ycoord, NumberValue zcoord, GeoNumeric uVar,
			NumberValue uFrom, NumberValue uTo, GeoNumeric vVar,
			NumberValue vFrom, NumberValue vTo);

	// //////////////////////////////////////////////
	// intersection algos

	/**
	 * intersection between 3D line and conic
	 * 
	 * @return two intersection points
	 */
	public GeoPointND[] IntersectLineConic(String[] labels, GeoLineND g,
			GeoConicND c);

	/**
	 * get only one intersection point of two conics that is near to the given
	 * location (xRW, yRW)
	 */
	public GeoPointND IntersectLineConicSingle(String label, GeoLineND g,
			GeoConicND c, double xRW, double yRW, CoordMatrix mat);

	/**
	 * get only one intersection point of two conics
	 */
	public GeoPointND IntersectLineConicSingle(String label, GeoLineND g,
			GeoConicND c, NumberValue index);

	/**
	 * get only one intersection point of two conics
	 */
	public GeoPointND IntersectLineConicSingle(String label, GeoLineND g,
			GeoConicND c, int index);

	/**
	 * get only one intersection point of two conics, near to refPoint
	 */
	public GeoPointND IntersectLineConicSingle(String label, GeoLineND g,
			GeoConicND c, GeoPointND refPoint);

	/**
	 * intersection between two 3D conics
	 * 
	 * @param labels
	 * @param A
	 * @param B
	 * @return 4 intersection points
	 */
	public GeoPointND[] IntersectConics(String[] labels, GeoConicND A,
			GeoConicND B);

	public GeoPointND IntersectConicsSingle(String label, GeoConicND A,
			GeoConicND B, double xRW, double yRW, CoordMatrix mat);

	public GeoPointND IntersectConicsSingle(String label, GeoConicND A,
			GeoConicND B, NumberValue index);

	public GeoPointND IntersectConicsSingle(String label, GeoConicND A,
			GeoConicND B, int index);

	public GeoPointND IntersectConicsSingle(String label, GeoConicND A,
			GeoConicND B, GeoPointND refPoint);

	/**
	 * intersect line/quadric
	 */

	public GeoPointND[] IntersectLineQuadric(String[] labels, GeoLineND A,
			GeoQuadricND B);

	/**
	 * get only one intersection point of two conics that is near to the given
	 * location (xRW, yRW)
	 */
	public GeoPointND IntersectLineQuadricSingle(String label, GeoLineND g,
			GeoQuadricND q, double xRW, double yRW, CoordMatrix4x4 mat);

	/**
	 * get only one intersection point of line and quadric choice depends on
	 * command input
	 */
	public GeoPointND IntersectLineQuadricSingle(String label, GeoLineND g,
			GeoQuadricND q, NumberValue index);

	/**
	 * get only one intersection point of line and quadric choice depends on
	 * command input
	 */
	public GeoPointND IntersectLineQuadricSingle(String label, GeoLineND g,
			GeoQuadricND q, int index);

	public GeoPointND IntersectLineQuadricSingle(String label, GeoLineND g,
			GeoQuadricND q, GeoPointND refPoint);

	/**
	 * intersect plane/conic
	 */
	public GeoPointND[] IntersectPlaneConic(String[] labels, GeoCoordSys2D A,
			GeoConicND B);

	public GeoElement IntersectPlanes(String label, GeoPlaneND cs1,
			GeoPlaneND cs2);

	/**
	 * intersect polygons (boundary)
	 * 
	 * @return intersect points
	 */
	public GeoElement[] IntersectionPoint(String[] labels, GeoPolygon3D poly0,
			GeoPolygon3D poly1);

	/**
	 * intersect polygons (region)
	 */
	public GeoElement[] IntersectPolygons(String[] labels,
			GeoPolygon3D inPoly0, GeoPolygon3D inPoly1);

	/**
	 * Difference polygons (region)
	 */
	public GeoElement[] DifferencePolygons(String[] labels,
			GeoPolygon3D inPoly0, GeoPolygon3D inPoly1);

	/**
	 * Difference polygons or exclusive difference polygons (region)
	 */
	public GeoElement[] DifferencePolygons(String[] labels,
			GeoPolygon3D inPoly0, GeoPolygon3D inPoly1, GeoBoolean exclusive);

	/**
	 * Union polygons (region)
	 */
	public GeoElement[] UnionPolygons(String[] labels, GeoPolygon3D inPoly0,
			GeoPolygon3D inPoly1);

	public GeoElement IntersectPlanes(GeoPlaneND cs1, GeoPlaneND cs2);

	public GeoElement ClosestPoint(String label, GeoLineND g, GeoLineND h);

	public GeoElement ClosestPoint(String label, Path p, GeoPointND P);

	public GeoPointND ClosestPoint(String label, Region r, GeoPointND P);

	public GeoNumeric Distance(String label, GeoLineND g, GeoLineND h);

	public GeoNumeric Distance(String label, GeoPointND point, GeoPlaneND plane);

	public GeoNumeric Distance(String label, GeoPlaneND a, GeoPlaneND b);

	/**
	 * Angle named label between three points
	 */
	public GeoAngle Angle3D(String label, GeoPointND A, GeoPointND B,
			GeoPointND C);

	/**
	 * Angle (oriented) named label between three points
	 */
	public GeoAngle Angle3D(String label, GeoPointND A, GeoPointND B,
			GeoPointND C, GeoDirectionND orientation);

	/**
	 * Angle named label between lines
	 */
	public GeoAngle Angle3D(String label, GeoLineND g, GeoLineND h);

	/**
	 * Angle named label between lines, oriented
	 */
	public GeoAngle Angle3D(String label, GeoLineND g, GeoLineND h,
			GeoDirectionND orientation);

	/**
	 * Angle named label between planes
	 */
	public GeoAngle Angle3D(String label, GeoPlaneND g, GeoPlaneND h);

	/**
	 * Angle named label between line and plane
	 */
	public GeoAngle Angle3D(String label, GeoLineND l, GeoPlaneND p);

	/**
	 * create angle checking start/end points
	 * 
	 * @param line1
	 * @param line2
	 * @return angle
	 */
	public GeoAngle createLineAngle(GeoLineND line1, GeoLineND line2);

	/**
	 * create (oriented) angle checking start/end points
	 * 
	 * @param line1
	 * @param line2
	 * @param orientation
	 * @return angle
	 */
	public GeoAngle createLineAngle(GeoLineND line1, GeoLineND line2,
			GeoDirectionND orientation);

	/**
	 * Angle named label between vectors
	 */
	public GeoAngle Angle3D(String label, GeoVectorND v, GeoVectorND w);

	/**
	 * Angle (oriented) named label between vectors
	 */
	public GeoAngle Angle3D(String label, GeoVectorND v, GeoVectorND w,
			GeoDirectionND orientation);

	/**
	 * 
	 * @param labels
	 * @param poly
	 * @return angles for the polygon
	 */
	public GeoElement[] Angles3D(String[] labels, GeoPolygon poly);

	/**
	 * Creates a new point C by rotating B around A using angle alpha and a new
	 * angle BAC (for positive orientation) resp. angle CAB (for negative
	 * orientation). The labels[0] is for the angle, labels[1] for the new point
	 */
	public GeoElement[] Angle(String[] labels, GeoPointND B, GeoPointND A,
			GeoNumberValue alpha, GeoDirectionND orientation,
			boolean posOrientation);

	/**
	 * 
	 * @param labels
	 * @param poly
	 * @return angles for the polygon, oriented
	 */
	public GeoElement[] Angles3D(String[] labels, GeoPolygon poly,
			GeoDirectionND orientation);

	/**
	 * Length named label of vector v
	 * 
	 * @return length of the vector
	 */
	public GeoNumeric Length(String label, GeoVectorND v);

	/**
	 * @return cube with A, B for first points and v for direction
	 */
	public GeoElement[] ArchimedeanSolid(String[] labels, GeoPointND A,
			GeoPointND B, GeoDirectionND v, Commands name);

	/**
	 * @return cube with A, B, C for first points
	 */
	public GeoElement[] ArchimedeanSolid(String[] labels, GeoPointND A,
			GeoPointND B, GeoPointND C, Commands name);

	/**
	 * @return cube with A, B, C for first points (C point on an ad hoc circle)
	 */
	public GeoElement[] ArchimedeanSolid(String[] labels, GeoPointND A,
			GeoPointND B, Commands name);

	/********************************************************************
	 * TRANSFORMATIONS
	 ********************************************************************/

	/**
	 * translate geoTrans by vector v
	 */
	public GeoElement[] Translate3D(String label, GeoElement geoTrans,
			GeoVectorND v);

	public Geo3DVec newGeo3DVec(double double1, double double2, double double3);

	/**
	 * rotate about a point + direction
	 * 
	 * @param label
	 * @param geoRot
	 * @param phi
	 * @param Q
	 * @param orientation
	 * @return geo rotated
	 */
	public GeoElement[] Rotate3D(String label, GeoElement geoRot,
			GeoNumberValue phi, GeoPointND Q, GeoDirectionND orientation);

	/**
	 * rotate about line
	 * 
	 * @param label
	 * @param geoRot
	 * @param phi
	 * @param line
	 * @return geo rotated
	 */
	public GeoElement[] Rotate3D(String label, GeoElement geoRot,
			GeoNumberValue phi, GeoLineND line);

	/**
	 * 
	 */
	public GeoElement[] Mirror3D(String label, GeoElement geo, GeoPointND p);

	public GeoElement[] Mirror3D(String label, GeoElement geo, GeoLineND line);

	public GeoElement[] Mirror3D(String label, GeoElement geo,
			GeoCoordSys2D plane);

	public GeoElement[] Dilate3D(String label, GeoElement geoDil,
			NumberValue r, GeoPointND S);

	/**
	 * 
	 * @param label
	 * @param hasVolume
	 * @return volume of hasVolume
	 */
	public GeoNumeric Volume(String label, HasVolume hasVolume);

	/**
	 * 
	 * @param label
	 * @param hasHeight
	 * @return oriented height of hasHeight
	 */
	public GeoNumeric OrientedHeight(String label, HasHeight hasHeight);

	/**
	 * 
	 * @param labels
	 * @param conic
	 * @return corners for a conic section
	 */
	public GeoElement[] Corner(String[] labels, GeoConicND conic);

	/**
	 * Net of a polyhedron
	 * 
	 * @param labels
	 *            labels
	 * @param p
	 *            polyhedron
	 * @param v
	 *            value "opening" the net
	 * @return net, faces, etc.
	 */
	public GeoElement[] PolyhedronNet(String[] labels, GeoElement p,
			NumberValue v, GeoPolygon bottomFace, GeoSegmentND[] pivotSegments);

	public GeoElement[] PolyhedronConvex(String[] labels, GeoElement[] pointList);

	/**
	 * circle arc from three points
	 */
	public GeoConicPartND CircumcircleArc3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C);

	/**
	 * circle sector from three points
	 */
	public GeoConicPartND CircumcircleSector3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C);

	/**
	 * Angular bisectors of lines g, h
	 */
	public GeoElement[] AngularBisector3D(String[] labels, GeoLineND g,
			GeoLineND h);

	/**
	 * Angular bisectors of points A, B, C
	 */
	public GeoElement AngularBisector3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C);

	/**
	 * Angular bisectors of points A, B, C, oriented
	 */
	public GeoElement AngularBisector3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C, GeoDirectionND orientation);

	/**
	 * circle arc/sector from center and two points on arc
	 */
	public GeoConicPartND CircleArcSector3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C, int type);

	/**
	 * circle arc/sector from center and two points on arc (oriented)
	 */
	public GeoConicPartND CircleArcSector3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C, GeoDirectionND orientation, int type);

	public GeoLineND Line3D(String label, ExpressionValue[] coefX,
			ExpressionValue[] coefY, ExpressionValue[] coefZ);

	/**
	 * semicircle joining A and B, oriented
	 */
	public GeoConicPartND Semicircle3D(String label, GeoPointND A,
			GeoPointND B, GeoDirectionND orientation);

	/**
	 * tangents to c through P
	 */
	public GeoElement[] Tangent3D(String[] labels, GeoPointND P, GeoConicND c);

	public GeoElement[] Tangent3D(String[] labels, GeoLineND l, GeoConicND c);

	public GeoElement[] CommonTangents3D(String[] labels, GeoConicND c1,
			GeoConicND c2);

	public GeoElement DiameterLine3D(String label, GeoLineND g, GeoConicND c);

	public GeoElement DiameterLine3D(String label, GeoVectorND v, GeoConicND c);

	public GeoElement LineBisector3D(String label, GeoSegmentND segment,
			GeoDirectionND orientation);

	public GeoElement LineBisector3D(String label, GeoPointND a, GeoPointND b,
			GeoDirectionND orientation);

	public GeoConicND Conic3D(String label, GeoPointND[] points);

	/**
	 * ellipse with foci A, B passing thorugh C
	 */
	public GeoConicND EllipseHyperbola3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C, final int type);

	/**
	 * ellipse with foci A, B passing thorugh C, oriented
	 */
	public GeoConicND EllipseHyperbola3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C, GeoDirectionND orientation,
			final int type);

	/**
	 * parabola with focus F and line l
	 */
	public GeoConicND Parabola3D(String label, GeoPointND F, GeoLineND l);

	/**
	 * locus line for Q dependent on P. Note: P must be a point on a path.
	 */
	public GeoElement Locus3D(String label, GeoPointND Q, GeoPointND P);

	public GeoElement Locus3D(String label, GeoPointND Q, GeoNumeric slider);

	/*
	 * tangent to parametric curve
	 */
	public GeoElement Tangent3D(String label, GeoPointND point,
			GeoCurveCartesianND curve);

}
