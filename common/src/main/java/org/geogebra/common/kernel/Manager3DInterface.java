package org.geogebra.common.kernel;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
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
import org.geogebra.common.kernel.geos.GeoPoly;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoSurfaceFinite;
import org.geogebra.common.kernel.kernelND.Geo3DVecInterface;
import org.geogebra.common.kernel.kernelND.GeoConicND;
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
import org.geogebra.common.kernel.kernelND.GeoRayND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.kernelND.HasHeight;
import org.geogebra.common.kernel.kernelND.HasVolume;
import org.geogebra.common.kernel.matrix.CoordMatrix;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Interface for managing all 3D methods in Kernel. <br>
 * See Also {@link org.geogebra.common.geogebra3D.kernel3D.algos.Manager3D}.
 * 
 * @author mathieu
 * 
 */
@SuppressWarnings("javadoc")
public interface Manager3DInterface {

	/** @return Point3D label with cartesian coordinates (x,y,z) */
	public GeoElement point3D(String label, double x, double y, double z,
			boolean coords2D);

	public GeoPointND point3D(double x, double y, double z, boolean coords2D);

	/**
	 * Point dependent on arithmetic expression with variables, represented by a
	 * tree. e.g. P = (4t, 2s)
	 * 
	 * @return dependent point
	 */
	public GeoPointND dependentPoint3D(ExpressionNode root, boolean addToCons);

	public GeoElement dependentVector3D(ExpressionNode root);

	public GeoElement vector3D(double x, double y, double z);

	/**
	 * Vector named label from Point P to Q
	 * 
	 * @return vector
	 */
	public GeoElement vector3D(String label, GeoPointND P, GeoPointND Q);

	/** @return Point in region with cartesian coordinates (x,y,z) */
	public GeoPointND point3DIn(String label, Region region, Coords coords,
			boolean addToConstruction, boolean coords2D);

	public GeoPointND point3DIn(Region region, Coords coords, boolean coords2D);

	/** @return Point in region */
	public GeoPointND point3DIn(String label, Region region, boolean coords2D);

	/** @return Point3D on a 1D path with cartesian coordinates (x,y,z) */
	public GeoPointND point3D(String label, Path path, double x, double y,
			double z, boolean addToConstruction, boolean coords2D);

	/** @return Point3D on a 1D path without cartesian coordinates */
	public GeoPointND point3D(String label, Path path, boolean coords2D);

	/** @return Point3D on a 1D path with path parameter */
	public GeoPointND point3D(String label, Path path, GeoNumberValue param);

	/**
	 * Midpoint M = (P + Q)/2
	 * 
	 * @param label
	 *            output label
	 * @param P
	 *            P
	 * @param Q
	 *            Q
	 * @return midpoint
	 */
	public GeoPointND midpoint(String label, GeoPointND P, GeoPointND Q);

	/**
	 * Midpoint of segment
	 * 
	 * @param label
	 *            output label
	 * @param segment
	 *            segment
	 * @return midpoint
	 */
	public GeoPointND midpoint(String label, GeoSegmentND segment);

	/**
	 * Center of conic
	 * 
	 * @param label
	 *            output label
	 * @param conic
	 *            conic
	 * @return center
	 */
	public GeoPointND center(String label, GeoConicND conic);

	/**
	 * Center of quadric
	 * 
	 * @param label
	 *            output label
	 * @param quadric
	 *            quadric
	 * @return center
	 */
	public GeoPointND centerQuadric(String label, GeoQuadricND quadric);

	/** @return Segment3D label linking points P1 and P2 */
	public GeoSegmentND segment3D(String label, GeoPointND P1, GeoPointND P2);

	/** @return Line3D label linking points P1 and P2 */
	public GeoElement line3D(String label, GeoPointND P1, GeoPointND P2);

	/** @return Line3D label through point P and parallel to line l */
	public GeoLineND line3D(String label, GeoPointND P, GeoLineND l);

	/** @return Line3D label through point P and parallel to vector v */
	public GeoLineND line3D(String label, GeoPointND P, GeoVectorND v);

	/** @return Ray3D label linking points P1 and P2 */
	public GeoRayND ray3D(String label, GeoPointND P1, GeoPointND P2);

	/** @return Line3D through point orthogonal to plane */
	public GeoLineND orthogonalLine3D(String label, GeoPointND point,
			GeoCoordSys2D plane);

	/** @return Line3D through point orthogonal to line */
	public GeoLineND orthogonalLine3D(String label, GeoPointND point,
			GeoLineND line);

	/** @return Line3D through point orthogonal to line and direction */
	public GeoLineND orthogonalLine3D(String label, GeoPointND point,
			GeoDirectionND line, GeoDirectionND direction);

	/** @return Line3D orthogonal two lines */
	public GeoLineND orthogonalLine3D(String label, GeoLineND line1,
			GeoLineND line2);

	/** @return Vector3D orthogonal to plane */
	public GeoVectorND orthogonalVector3D(String label, GeoCoordSys2D plane);

	/** @return Vector3D orthogonal to line with direction */
	public GeoVectorND orthogonalVector3D(String label, GeoLineND line,
			GeoDirectionND direction);

	/** @return Vector3D unit orthogonal to plane */
	public GeoVectorND unitOrthogonalVector3D(String label,
			GeoCoordSys2D plane);

	/**
	 * Polygon3D linking points P1, P2, ...
	 * 
	 * @param label
	 *            name of the polygon
	 * @param points
	 *            vertices of the polygon
	 * @return the polygon
	 */
	public GeoElement[] polygon3D(String[] label, GeoPointND[] points);

	/**
	 * Polygon3D linking points P1, P2, ...
	 * 
	 * @param label
	 *            name of the polygon
	 * @param points
	 *            vertices of the polygon
	 * @return the polygon
	 */
	public GeoElement[] polygon3D(String[] label, GeoPointND[] points,
			GeoDirectionND direction);

	/**
	 * Regular polygon with vertices A and B and n total vertices. The labels
	 * name the polygon itself, its segments and points
	 * 
	 * @return regular polygon
	 */
	public GeoElement[] regularPolygon(String[] labels, GeoPointND A,
			GeoPointND B, GeoNumberValue n, GeoDirectionND direction);

	public GeoElement[] polyLine3D(String label, GeoPointND[] P);

	public GeoElement[] polyLine3D(String label, GeoList pointList);

	/**
	 * Prism with vertices (last one is first vertex of second parallel face)
	 * 
	 * @param labels
	 *            names
	 * @param points
	 *            vertices
	 * @return the polyhedron
	 */
	public GeoElement[] prism(String[] labels, GeoPointND[] points);

	/**
	 * Prism with basis and first vertex of second parallel face
	 * 
	 * @param labels
	 *            output labels
	 * @param polygon
	 *            polygon
	 * @param point
	 *            point
	 * @return the polyhedron
	 */
	public GeoElement[] prism(String[] labels, GeoPolygon polygon,
			GeoPointND point);

	/**
	 * Right prism with basis and height
	 * 
	 * @param labels
	 *            output labels
	 * @param polygon
	 *            polygon
	 * @param height
	 *            height
	 * @return the polyhedron
	 */
	public GeoElement[] prism(String[] labels, GeoPolygon polygon,
			GeoNumberValue height);

	/**
	 * Pyramid with vertices (last one as apex)
	 * 
	 * @param labels
	 *            names
	 * @param points
	 *            vertices
	 * @return the polyhedron
	 */
	public GeoElement[] pyramid(String[] labels, GeoPointND[] points);

	/**
	 * Pyramid with basis and top vertex
	 * 
	 * @param labels
	 *            labels
	 * @param polygon
	 *            bottom face
	 * @param point
	 *            top vertex
	 * @return pyramid, sides and edges
	 */
	public GeoElement[] pyramid(String[] labels, GeoPolygon polygon,
			GeoPointND point);

	/**
	 * pyramid with top point over center of bottom face
	 * 
	 * @param labels
	 *            output labels
	 * @param polygon
	 *            base
	 * @param height
	 *            pyramid height
	 * @return pyramid, sides and edges
	 */
	public GeoElement[] pyramid(String[] labels, GeoPolygon polygon,
			GeoNumberValue height);

	/**
	 * Plane a x + b y + c z + d = 0
	 * 
	 * @return plane
	 */
	public GeoPlaneND plane3D(double a, double b, double c, double d);

	/**
	 * Plane dependent on coefficients of arithmetic expressions with variables,
	 * represented by trees.
	 * 
	 * @return dependent plane
	 */
	public GeoPlaneND dependentPlane3D(Equation equ);

	/**
	 * Quadric dependent on coefficients of arithmetic expressions with
	 * variables, represented by trees.
	 * 
	 * @return dependent quadric
	 */
	public GeoQuadricND dependentQuadric3D(Equation equ);

	/**
	 * Plane named label through point and line
	 * 
	 * @return plane
	 */
	public GeoPlaneND plane3D(String label, GeoPointND point, GeoLineND line);

	/**
	 * Plane named label through point parallel to plane
	 * 
	 * @return plane
	 */
	public GeoPlaneND plane3D(String label, GeoPointND point, GeoCoordSys2D cs);

	/**
	 * Plane named label through Point P orthogonal to line l
	 * 
	 * @return plane
	 */
	public GeoPlaneND orthogonalPlane3D(String label, GeoPointND point,
			GeoLineND line);

	public GeoPlaneND orthogonalPlane3D(String label, GeoPointND point,
			GeoVectorND vector);

	public GeoPlaneND planeBisector(String label, GeoPointND point1,
			GeoPointND point2);

	public GeoPlaneND planeBisector(String label, GeoSegmentND segment);

	/**
	 * Sphere label linking with center o and radius r
	 * 
	 * @return sphere
	 */
	public GeoElement sphere(String label, GeoPointND M, GeoNumberValue r);

	/**
	 * Sphere with midpoint M through point P
	 * 
	 * @return sphere
	 */
	public GeoElement sphere(String label, GeoPointND M, GeoPointND P);

	/**
	 * @return Cone
	 */
	public GeoQuadricND cone(String label, GeoPointND origin,
			GeoVectorND direction, GeoNumberValue angle);

	public GeoQuadricND cone(String label, GeoPointND origin,
			GeoPointND secondPoint, GeoNumberValue angle);

	public GeoQuadricND cone(String label, GeoPointND origin, GeoLineND axis,
			GeoNumberValue angle);

	public GeoElement[] coneLimited(String[] labels, GeoPointND origin,
			GeoPointND secondPoint, GeoNumberValue r);

	public GeoElement[] coneLimited(String[] labels, GeoConicND bottom,
			GeoNumberValue height);

	/**
	 * @return Cylinder
	 */
	public GeoQuadricND cylinder(String label, GeoPointND origin,
			GeoVectorND direction, GeoNumberValue r);

	public GeoQuadricND cylinder(String label, GeoPointND origin,
			GeoPointND secondPoint, GeoNumberValue r);

	public GeoQuadricND cylinder(String label, GeoLineND axis,
			GeoNumberValue r);

	public GeoElement[] cylinderLimited(String[] labels, GeoPointND origin,
			GeoPointND secondPoint, GeoNumberValue r);

	public GeoElement[] cylinderLimited(String[] labels, GeoConicND bottom,
			GeoNumberValue height);

	/**
	 * @return quadric side
	 */
	public GeoQuadricND quadricSide(String label, GeoQuadricND quadric);

	public GeoConicND quadricBottom(String label, GeoQuadricND quadric);

	public GeoConicND quadricTop(String label, GeoQuadricND quadric);

	/**
	 * @return circle through points A, B, C
	 */
	public GeoConicND circle3D(String label, GeoPointND A, GeoPointND B,
			GeoPointND C);

	/**
	 * @return circle with axis through point
	 */
	public GeoConicND circle3D(String label, GeoLineND axis, GeoPointND A);

	/**
	 * @return circle with point, radius, axis
	 */
	public GeoConicND circle3D(String label, GeoPointND A,
			GeoNumberValue radius, GeoDirectionND axis);

	/**
	 * @return circle with point, radius, axis orthogonal to xOy plane
	 */
	public GeoConicND circle3D(String label, GeoPointND A,
			GeoNumberValue radius);

	public GeoConicND circle3D(String label, GeoPointND A, GeoPointND B,
			GeoDirectionND axis);

	/**
	 * @return plane through points A, B, C
	 */
	public GeoElement plane3D(String label, GeoPointND A, GeoPointND B,
			GeoPointND C);

	/**
	 * @return plane through lines a, b
	 */
	public GeoElement plane3D(String label, GeoLineND a, GeoLineND b);

	/**
	 * @param label
	 *            output label
	 * @param cs2D
	 *            cs2D
	 * @return plane containing the 2D coord sys
	 */
	public GeoPlaneND plane3D(String label, GeoCoordSys2D cs2D);

	public GeoPlaneND plane3D(GeoCoordSys2D cs2D);

	// //////////////////////////////////////////////
	// INTERSECTION (POINTS)

	public GeoElement intersect(String label, GeoLineND cs1, GeoCoordSys2D cs2,
			boolean swapInputs);

	/**
	 * Calculate the intersection of two lines
	 * 
	 * @param label
	 *            name of the point
	 * @param cs1
	 *            first line
	 * @param cs2
	 *            second line
	 * @return point intersection
	 */
	public GeoElement intersect(String label, GeoLineND cs1, GeoLineND cs2);

	/**
	 * Calculate the intersection of the line g with the region of p
	 * 
	 * @return intersection points
	 */
	public GeoElement[] intersectionPoint(String[] labels, GeoLineND g,
			GeoSurfaceFinite s);

	public GeoElement[] intersectPath(String[] labels, GeoLineND g,
			GeoSurfaceFinite s);

	public GeoElement[] intersectPath(String[] labels, GeoPlaneND plane,
			GeoElement s);

	public GeoElement[] intersectPlaneFunctionNVar(String label,
			GeoPlaneND plane, GeoFunctionNVar fun);

	public GeoElement[] intersectPlaneFunctionNVar(GeoPlaneND plane,
			GeoFunctionNVar fun);

	public GeoElement[] intersectPlaneImplicitSurface(GeoPlaneND plane,
			GeoImplicitSurfaceND fun);

	public GeoElement[] intersectPath(GeoPlaneND plane, GeoPolygon p);

	public GeoElement[] intersectRegion(String[] labels, GeoPlaneND plane,
			GeoElement s, int[] outputSize);

	public GeoElement[] intersectRegion(GeoPlaneND plane, GeoElement p);

	public GeoElement[] intersectionPoint(String[] labels, GeoPlaneND p,
			GeoElement s);

	/**
	 * Calculate the intersection of plane and quadric
	 * 
	 * @param label
	 *            name of the point
	 * @param plane
	 *            plane
	 * @param quadric
	 *            quadric
	 * @return conic intersection
	 */
	public GeoConicND intersect(String label, GeoPlaneND plane,
			GeoQuadricND quadric);

	public GeoConicND intersectQuadricLimited(String label, GeoPlaneND plane,
			GeoQuadricND quadric);

	public GeoConicND intersectQuadricLimited(GeoPlaneND plane,
			GeoQuadricND quadric);

	public GeoConicND intersect(GeoPlaneND plane, GeoQuadricND quadric);

	/**
	 * Calculate the intersection of two quadrics, if it's a conic
	 * 
	 * @param labels
	 * 
	 * @param quadric1
	 *            quadric1
	 * @param quadric2
	 *            quadric2
	 * @return conic intersection
	 */
	public GeoElement[] intersectAsCircle(String[] labels,
			GeoQuadricND quadric1, GeoQuadricND quadric2);

	/**
	 * Calculate the intersection of two quadrics, if it's a conic
	 * 
	 * @param quadric1
	 *            quadric1
	 * @param quadric2
	 *            quadric2
	 * @return conic intersection
	 */
	public GeoElement[] intersectAsCircle(GeoQuadricND quadric1,
			GeoQuadricND quadric2);

	// //////////////////////////////////////////////
	// FUNCTIONS (2 VARS)

	public GeoFunctionNVar function2Var(String label, GeoNumberValue zcoord,
			GeoNumeric localVarU, GeoNumberValue Ufrom, GeoNumberValue Uto,
			GeoNumeric localVarV, GeoNumberValue Vfrom, GeoNumberValue Vto);

	public GeoFunctionNVar function2Var(String label, GeoFunctionNVar f,
			GeoNumberValue xFrom, GeoNumberValue xTo, GeoNumberValue yFrom,
			GeoNumberValue yTo);

	// //////////////////////////////////////////////
	// 3D CURVE (1 VAR)

	/**
	 * 3D Cartesian curve command: Curve[ &lt;expression x-coord&gt;,
	 * &lt;expression y-coord&gt;, &lt;expression z-coord&gt;,
	 * &lt;number-var&gt;, &lt;from&gt;, &lt;to&gt; ]
	 * 
	 * @return curve
	 */
	public GeoElement curveCartesian3D(GeoNumberValue xcoord,
			GeoNumberValue ycoord, GeoNumberValue zcoord, GeoNumeric localVar,
			GeoNumberValue from, GeoNumberValue to);

	// //////////////////////////////////////////////
	// 3D SURFACE (2 VARS)

	/**
	 * surface of revolution, rotating function around x-axis, from 0 to angle
	 * 
	 * @param function
	 *            x-&gt;y function
	 * @param angle
	 *            angle
	 * @return surface of revolution
	 */
	public GeoElement surfaceOfRevolution(Path function,
			GeoNumberValue angle, GeoLineND line);

	// //////////////////////////////////////////////
	// intersection algos

	/**
	 * intersection between 3D line and conic
	 * 
	 * @return two intersection points
	 */
	public GeoPointND[] intersectLineConic(String[] labels, GeoLineND g,
			GeoConicND c);

	/**
	 * get only one intersection point of two conics that is near to the given
	 * location (xRW, yRW)
	 * 
	 * @return intersection point
	 */
	public GeoPointND intersectLineConicSingle(String label, GeoLineND g,
			GeoConicND c, double xRW, double yRW, CoordMatrix mat);

	public GeoPoint3D intersectLineConicSingle(String label, GeoLineND g,
			GeoConicND c, Coords origin, Coords direction);

	/**
	 * get only one intersection point of two conics
	 * 
	 * @return intersection point
	 */
	public GeoPointND intersectLineConicSingle(String label, GeoLineND g,
			GeoConicND c, NumberValue index);

	/**
	 * get only one intersection point of two conics
	 * 
	 * @return intersection point
	 */
	public GeoPointND intersectLineConicSingle(String label, GeoLineND g,
			GeoConicND c, int index);

	/**
	 * get only one intersection point of two conics, near to refPoint
	 * 
	 * @return intersection point
	 */
	public GeoPointND intersectLineConicSingle(String label, GeoLineND g,
			GeoConicND c, GeoPointND refPoint);

	/**
	 * intersection between two 3D conics
	 * 
	 * @param labels
	 *            output labels
	 * @param A
	 *            A
	 * @param B
	 *            B
	 * @return 4 intersection points
	 */
	public GeoPointND[] intersectConics(String[] labels, GeoConicND A,
			GeoQuadricND B);

	public GeoPointND intersectConicsSingle(String label, GeoConicND A,
			GeoQuadricND B, double xRW, double yRW, CoordMatrix mat);

	public GeoPoint3D intersectConicsSingle(String label, GeoConicND A,
			GeoQuadricND B, Coords origin, Coords direction);

	public GeoPointND intersectConicsSingle(String label, GeoConicND A,
			GeoQuadricND B, NumberValue index);

	public GeoPointND intersectConicsSingle(String label, GeoConicND A,
			GeoQuadricND B, int index);

	public GeoPointND intersectConicsSingle(String label, GeoConicND A,
			GeoQuadricND B, GeoPointND refPoint);

	/**
	 * intersect line/quadric
	 * 
	 * @return intersection points
	 */
	public GeoPointND[] intersectLineQuadric(String[] labels, GeoLineND A,
			GeoQuadricND B);

	/**
	 * get only one intersection point of two conics that is near to the given
	 * location (xRW, yRW)
	 * 
	 * @return intersection point
	 */
	public GeoPointND intersectLineQuadricSingle(String label, GeoLineND g,
			GeoQuadricND q, double xRW, double yRW, CoordMatrix4x4 mat);

	public GeoPoint3D intersectLineQuadricSingle(String label, GeoLineND g,
			GeoQuadricND q, Coords origin, Coords direction);

	/**
	 * get only one intersection point of line and quadric choice depends on
	 * command input
	 * 
	 * @return intersection point
	 */
	public GeoPointND intersectLineQuadricSingle(String label, GeoLineND g,
			GeoQuadricND q, NumberValue index);

	/**
	 * get only one intersection point of line and quadric choice depends on
	 * command input
	 * 
	 * @return intersection point
	 */
	public GeoPointND intersectLineQuadricSingle(String label, GeoLineND g,
			GeoQuadricND q, int index);

	public GeoPointND intersectLineQuadricSingle(String label, GeoLineND g,
			GeoQuadricND q, GeoPointND refPoint);

	/**
	 * intersect plane/conic
	 * 
	 * @return intersection points
	 */
	public GeoPointND[] intersectPlaneConic(String[] labels, GeoCoordSys2D A,
			GeoConicND B);

	public GeoElementND[] intersectPlaneCurve(String[] labels, GeoCoordSys2D A,
			GeoCurveCartesianND B);

	public GeoElement intersectPlanes(String label, GeoPlaneND cs1,
			GeoPlaneND cs2);

	/**
	 * intersect polygons (boundary)
	 * 
	 * @return intersect points
	 */
	public GeoElement[] intersectionPoint(String[] labels, GeoPolygon poly0,
			GeoPolygon poly1);

	/**
	 * @return intersect polygons (region)
	 */
	public GeoElement[] intersectPolygons(String[] labels, GeoPoly inPoly0,
			GeoPoly inPoly1);

	/**
	 * @return Difference polygons (region)
	 */
	public GeoElement[] differencePolygons(String[] labels,
			GeoPolygon inPoly0, GeoPolygon inPoly1);

	/**
	 * @return Difference polygons or exclusive difference polygons (region)
	 */
	public GeoElement[] differencePolygons(String[] labels,
			GeoPolygon inPoly0, GeoPolygon inPoly1, GeoBoolean exclusive);

	/**
	 * @return Union polygons (region)
	 */
	public GeoElement[] unionPolygons(String[] labels, GeoPoly inPoly0,
			GeoPoly inPoly1);

	public GeoElement intersectPlanes(GeoPlaneND cs1, GeoPlaneND cs2);

	public GeoElement closestPoint(String label, GeoLineND g, GeoLineND h);

	public GeoElement closestPoint(String label, Path p, GeoPointND P);

	public GeoPointND closestPoint(String label, Region r, GeoPointND P);

	public GeoNumeric distance(String label, GeoPointND point,
			GeoPlaneND plane);

	public GeoNumeric distance(String label, GeoPlaneND a, GeoPlaneND b);

	/**
	 * @return Angle named label between three points
	 */
	public GeoAngle angle3D(String label, GeoPointND A, GeoPointND B,
			GeoPointND C);

	/**
	 * @return Angle (oriented) named label between three points
	 */
	public GeoAngle angle3D(String label, GeoPointND A, GeoPointND B,
			GeoPointND C, GeoDirectionND orientation);

	/**
	 * @return Angle named label between lines
	 */
	public GeoAngle angle3D(String label, GeoLineND g, GeoLineND h);

	/**
	 * @return Angle named label between lines, oriented
	 */
	public GeoAngle angle3D(String label, GeoLineND g, GeoLineND h,
			GeoDirectionND orientation);

	/**
	 * @return Angle named label between planes
	 */
	public GeoAngle angle3D(String label, GeoPlaneND g, GeoPlaneND h);

	/**
	 * @return Angle named label between line and plane
	 */
	public GeoAngle angle3D(String label, GeoLineND l, GeoPlaneND p);

	/**
	 * create angle checking start/end points
	 * 
	 * @param line1
	 *            line1
	 * @param line2
	 *            line2
	 * @return angle
	 */
	public GeoAngle createLineAngle(GeoLineND line1, GeoLineND line2);

	/**
	 * create (oriented) angle checking start/end points
	 * 
	 * @param line1
	 *            line1
	 * @param line2
	 *            line2
	 * @param orientation
	 *            orientation
	 * @return angle
	 */
	public GeoAngle createLineAngle(GeoLineND line1, GeoLineND line2,
			GeoDirectionND orientation);

	/**
	 * @return Angle named label between vectors
	 */
	public GeoAngle angle3D(String label, GeoVectorND v, GeoVectorND w);

	/**
	 * @return Angle (oriented) named label between vectors
	 */
	public GeoAngle angle3D(String label, GeoVectorND v, GeoVectorND w,
			GeoDirectionND orientation);

	/**
	 * 
	 * @param labels
	 *            output labels
	 * @param poly
	 *            poly
	 * @return angles for the polygon
	 */
	public GeoElement[] angles3D(String[] labels, GeoPolygon poly);

	/**
	 * Creates a new point C by rotating B around A using angle alpha and a new
	 * angle BAC (for positive orientation) resp. angle CAB (for negative
	 * orientation). The labels[0] is for the angle, labels[1] for the new point
	 * 
	 * @return angle
	 */
	public GeoElement[] angle(String[] labels, GeoPointND B, GeoPointND A,
			GeoNumberValue alpha, GeoDirectionND orientation,
			boolean posOrientation);

	/**
	 * 
	 * @param labels
	 *            output labels
	 * @param poly
	 *            poly
	 * @return angles for the polygon, oriented
	 */
	public GeoElement[] angles3D(String[] labels, GeoPolygon poly,
			GeoDirectionND orientation);

	/**
	 * 
	 * @param labels
	 *            output labels
	 * @param poly
	 *            poly
	 * @param internalAngle
	 *            internalAngle
	 * @return angles for the polygon, maybe internal
	 */
	public GeoElement[] angles3D(String[] labels, GeoPolygon poly, boolean internalAngle);

	/**
	 * Length named label of vector v
	 * 
	 * @return length of the vector
	 */
	public GeoNumeric length(String label, GeoVectorND v);

	/**
	 * @return cube with A, B for first points and v for direction
	 */
	public GeoElement[] archimedeanSolid(String[] labels, GeoPointND A,
			GeoPointND B, GeoDirectionND v, Commands name);

	/**
	 * @return cube with A, B, C for first points
	 */
	public GeoElement[] archimedeanSolid(String[] labels, GeoPointND A,
			GeoPointND B, GeoPointND C, Commands name);

	/**
	 * @return cube with A, B, C for first points (C point on an ad hoc circle)
	 */
	public GeoElement[] archimedeanSolid(String[] labels, GeoPointND A,
			GeoPointND B, Commands name);

	public GeoElement[] archimedeanSolid(String[] labels, GeoPolygon poly,
			GeoBoolean isDirect, Commands name);

	/********************************************************************
	 * TRANSFORMATIONS
	 ********************************************************************/

	/**
	 * @return translate geoTrans by vector v
	 */
	public GeoElement[] translate3D(String label, GeoElementND geoTrans,
			GeoVectorND v);

	public Geo3DVecInterface newGeo3DVec(double double1, double double2,
			double double3);

	/**
	 * rotate about a point + direction
	 * 
	 * @param label
	 *            output label
	 * @param geoRot
	 *            geoRot
	 * @param phi
	 *            phi
	 * @param Q
	 *            Q
	 * @param orientation
	 *            orientation
	 * @return geo rotated
	 */
	public GeoElement[] rotate3D(String label, GeoElementND geoRot,
			GeoNumberValue phi, GeoPointND Q, GeoDirectionND orientation);

	/**
	 * rotate about line
	 * 
	 * @param label
	 *            output label
	 * @param geoRot
	 *            geoRot
	 * @param phi
	 *            phi
	 * @param line
	 *            line
	 * @return geo rotated
	 */
	public GeoElement[] rotate3D(String label, GeoElementND geoRot,
			GeoNumberValue phi, GeoLineND line);

	/**
	 * @return mirrored object
	 */
	public GeoElement[] mirror3D(String label, GeoElement geo, GeoPointND p);

	public GeoElement[] mirror3D(String label, GeoElement geo, GeoLineND line);

	public GeoElement[] mirror3D(String label, GeoElement geo,
			GeoCoordSys2D plane);

	public GeoElement[] dilate3D(String label, GeoElement geoDil,
			GeoNumberValue r, GeoPointND S);

	/**
	 * 
	 * @param label
	 *            output label
	 * @param hasVolume
	 *            hasVolume
	 * @return volume of hasVolume
	 */
	public GeoNumeric volume(String label, HasVolume hasVolume);

	/**
	 * 
	 * @param label
	 *            output label
	 * @param hasHeight
	 *            hasHeight
	 * @return oriented height of hasHeight
	 */
	public GeoNumeric orientedHeight(String label, HasHeight hasHeight);

	/**
	 * 
	 * @param labels
	 *            output labels
	 * @param conic
	 *            conic
	 * @return corners for a conic section
	 */
	public GeoElement[] corner(String[] labels, GeoConicND conic);

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
	public GeoElement[] polyhedronNet(String[] labels, GeoElement p,
			NumberValue v, GeoPolygon bottomFace, GeoSegmentND[] pivotSegments);

	public GeoElement[] polyhedronConvex(String[] labels,
			GeoElement[] pointList);

	/**
	 * @return circle arc from three points
	 */
	public GeoConicPartND circumcircleArc3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C);

	/**
	 * @return circle sector from three points
	 */
	public GeoConicPartND circumcircleSector3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C);

	/**
	 * @return Angular bisectors of lines g, h
	 */
	public GeoElement[] angularBisector3D(String[] labels, GeoLineND g,
			GeoLineND h);

	/**
	 * @return Angular bisectors of points A, B, C
	 */
	public GeoElement angularBisector3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C);

	/**
	 * @return Angular bisectors of points A, B, C, oriented
	 */
	public GeoElement angularBisector3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C, GeoDirectionND orientation);

	/**
	 * @return circle arc/sector from center and two points on arc
	 */
	public GeoConicPartND circleArcSector3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C, int type);

	/**
	 * @return circle arc/sector from center and two points on arc (oriented)
	 */
	public GeoConicPartND circleArcSector3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C, GeoDirectionND orientation, int type);

	public GeoLineND line3D(String label, ExpressionValue[] coefX,
			ExpressionValue[] coefY, ExpressionValue[] coefZ);

	/**
	 * @return semicircle joining A and B, oriented
	 */
	public GeoConicPartND semicircle3D(String label, GeoPointND A, GeoPointND B,
			GeoDirectionND orientation);

	/**
	 * @return tangents to c through P
	 */
	public GeoElement[] tangent3D(String[] labels, GeoPointND P, GeoConicND c);

	public GeoElement[] tangent3D(String[] labels, GeoLineND l, GeoConicND c);

	public GeoElement[] commonTangents3D(String[] labels, GeoConicND c1,
			GeoConicND c2);

	public GeoElement diameterLine3D(String label, GeoLineND g, GeoConicND c);

	public GeoElement diameterLine3D(String label, GeoVectorND v, GeoConicND c);

	public GeoElement lineBisector3D(String label, GeoSegmentND segment,
			GeoDirectionND orientation);

	public GeoElement lineBisector3D(String label, GeoPointND a, GeoPointND b,
			GeoDirectionND orientation);

	public GeoConicND conic3D(String label, GeoPointND[] points);

	/**
	 * @return ellipse with foci A, B passing thorugh C
	 */
	public GeoConicND ellipseHyperbola3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C, final int type);

	/**
	 * @return ellipse with foci A, B passing thorugh C, oriented
	 */
	public GeoConicND ellipseHyperbola3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C, GeoDirectionND orientation,
			final int type);

	/**
	 * @return parabola with focus F and line l
	 */
	public GeoConicND parabola3D(String label, GeoPointND F, GeoLineND l);

	/**
	 * @return locus line for Q dependent on P. Note: P must be a point on a
	 *         path.
	 */
	public GeoElement locus3D(String label, GeoPointND Q, GeoPointND P);

	public GeoElement locus3D(String label, GeoPointND Q, GeoNumeric slider);

	/*
	 * tangent to parametric curve
	 */
	public GeoElement tangent3D(String label, GeoPointND point,
			GeoCurveCartesianND curve);

}
