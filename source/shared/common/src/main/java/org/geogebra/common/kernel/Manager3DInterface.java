package org.geogebra.common.kernel;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.algos.AlgoElement;
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
public interface Manager3DInterface {

	/** @return Point3D label with cartesian coordinates (x,y,z) */
	GeoPointND point3D(double x, double y, double z, boolean coords2D);

	/**
	 * Point dependent on arithmetic expression with variables, represented by a
	 * tree. e.g. P = (4t, 2s)
	 * 
	 * @return dependent point
	 */
	GeoPointND dependentPoint3D(ExpressionNode root, boolean addToCons);

	/**
	 * Dependent 3D vector.
	 * @param root defining expression
	 * @return dependent vector
	 */
	GeoElement dependentVector3D(ExpressionNode root);

	/**
	 * Free 3D vector.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param z z-coordinate
	 * @return vector
	 */
	GeoElement vector3D(double x, double y, double z);

	/**
	 * Vector named label from Point P to Q
	 * 
	 * @return vector
	 */
	GeoElement vector3D(String label, GeoPointND P, GeoPointND Q);

	/** @return Point in region with cartesian coordinates (x,y,z) */
	GeoPointND point3DIn(String label, Region region, Coords coords,
			boolean addToConstruction, boolean coords2D);

	/** @return Point in region */
	GeoPointND point3DIn(String label, Region region, boolean coords2D);

	/** @return Point3D on a 1D path with cartesian coordinates (x,y,z) */
	GeoPointND point3D(String label, Path path, double x, double y,
			double z, boolean addToConstruction, boolean coords2D);

	/** @return Point3D on a 1D path without cartesian coordinates */
	GeoPointND point3D(String label, Path path, boolean coords2D);

	/** @return Point3D on a 1D path with path parameter */
	GeoPointND point3D(String label, Path path, GeoNumberValue param);

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
	GeoPointND midpoint(String label, GeoPointND P, GeoPointND Q);

	/**
	 * Midpoint of segment
	 * 
	 * @param label
	 *            output label
	 * @param segment
	 *            segment
	 * @return midpoint
	 */
	GeoPointND midpoint(String label, GeoSegmentND segment);

	/**
	 * Center of conic
	 * 
	 * @param label
	 *            output label
	 * @param conic
	 *            conic
	 * @return center
	 */
	GeoPointND center(String label, GeoConicND conic);

	/**
	 * Center of quadric
	 * 
	 * @param label
	 *            output label
	 * @param quadric
	 *            quadric
	 * @return center
	 */
	GeoPointND centerQuadric(String label, GeoQuadricND quadric);

	/** @return Segment3D label linking points P1 and P2 */
	GeoSegmentND segment3D(String label, GeoPointND P1, GeoPointND P2);

	/** @return Line3D label linking points P1 and P2 */
	GeoElement line3D(String label, GeoPointND P1, GeoPointND P2);

	/** @return Line3D label through point P and parallel to line l */
	GeoLineND line3D(String label, GeoPointND P, GeoLineND l);

	/** @return Line3D label through point P and parallel to vector v */
	GeoLineND line3D(String label, GeoPointND P, GeoVectorND v);

	/** @return Ray3D label linking points P1 and P2 */
	GeoRayND ray3D(String label, GeoPointND P1, GeoPointND P2);

	/** @return Line3D through point orthogonal to plane */
	GeoLineND orthogonalLine3D(String label, GeoPointND point,
			GeoCoordSys2D plane);

	/** @return Line3D through point orthogonal to line */
	GeoLineND orthogonalLine3D(String label, GeoPointND point,
			GeoLineND line);

	/** @return Line3D through point orthogonal to line and direction */
	GeoLineND orthogonalLine3D(String label, GeoPointND point,
			GeoDirectionND line, GeoDirectionND direction);

	/** @return Line3D orthogonal two lines */
	GeoLineND orthogonalLine3D(String label, GeoLineND line1,
			GeoLineND line2);

	/** @return Vector3D orthogonal to plane */
	GeoVectorND orthogonalVector3D(String label, GeoCoordSys2D plane);

	/** @return Vector3D orthogonal to line with direction */
	GeoVectorND orthogonalVector3D(String label, GeoLineND line,
			GeoDirectionND direction);

	/** @return Vector3D unit orthogonal to plane */
	GeoVectorND unitOrthogonalVector3D(String label,
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
	GeoElement[] polygon3D(String[] label, GeoPointND[] points);

	/**
	 * Polygon3D linking points P1, P2, ...
	 * 
	 * @param label
	 *            name of the polygon
	 * @param points
	 *            vertices of the polygon
	 * @return the polygon
	 */
	GeoElement[] polygon3D(String[] label, GeoPointND[] points,
			GeoDirectionND direction);

	/**
	 * Regular polygon with vertices A and B and n total vertices. The labels
	 * name the polygon itself, its segments and points
	 * 
	 * @return regular polygon
	 */
	GeoElement[] regularPolygon(String[] labels, GeoPointND A,
			GeoPointND B, GeoNumberValue n, GeoDirectionND direction);

	/**
	 * Polyline.
	 * @param label output label
	 * @param P vertices
	 * @return polyline
	 */
	GeoElement[] polyLine3D(String label, GeoPointND[] P);

	/**
	 * Polyline.
	 * @param label output label
	 * @param pointList vertices
	 * @return polyline
	 */
	GeoElement[] polyLine3D(String label, GeoList pointList);

	/**
	 * Prism with vertices (last one is first vertex of second parallel face)
	 * 
	 * @param labels
	 *            names
	 * @param points
	 *            vertices
	 * @return the polyhedron
	 */
	GeoElement[] prism(String[] labels, GeoPointND[] points);

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
	GeoElement[] prism(String[] labels, GeoPolygon polygon,
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
	GeoElement[] prism(String[] labels, GeoPolygon polygon,
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
	GeoElement[] pyramid(String[] labels, GeoPointND[] points);

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
	GeoElement[] pyramid(String[] labels, GeoPolygon polygon,
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
	GeoElement[] pyramid(String[] labels, GeoPolygon polygon,
			GeoNumberValue height);

	/**
	 * Plane a x + b y + c z + d = 0
	 * 
	 * @return plane
	 */
	GeoPlaneND plane3D(double a, double b, double c, double d);

	/**
	 * Plane dependent on coefficients of arithmetic expressions with variables,
	 * represented by trees.
	 * 
	 * @return dependent plane
	 */
	GeoPlaneND dependentPlane3D(Equation equ);

	/**
	 * Quadric dependent on coefficients of arithmetic expressions with
	 * variables, represented by trees.
	 * 
	 * @return dependent quadric
	 */
	GeoQuadricND dependentQuadric3D(Equation equ);

	/**
	 * Plane named label through point and line
	 * 
	 * @return plane
	 */
	GeoPlaneND plane3D(String label, GeoPointND point, GeoLineND line);

	/**
	 * Plane named label through point parallel to plane
	 * 
	 * @return plane
	 */
	GeoPlaneND plane3D(String label, GeoPointND point, GeoCoordSys2D cs);

	/**
	 * Plane named label through Point P orthogonal to line l
	 * 
	 * @return plane
	 */
	GeoPlaneND orthogonalPlane3D(String label, GeoPointND point,
			GeoLineND line);

	/**
	 * Plane orthogonal to a vector going through a point.
	 * @param label output label
	 * @param point point
	 * @param vector orthogonal vector
	 * @return the plane
	 */
	GeoPlaneND orthogonalPlane3D(String label, GeoPointND point,
			GeoVectorND vector);

	/**
	 * Plane bisector of a segment given by 2 points.
	 * @param label output label
	 * @param point1 first point
	 * @param point2 second point
	 * @return plane bisector
	 */
	GeoPlaneND planeBisector(String label, GeoPointND point1,
			GeoPointND point2);

	/**
	 * Plane bisector of a segment.
	 * @param label output label
	 * @param segment segment
	 * @return plane bisector
	 */
	GeoPlaneND planeBisector(String label, GeoSegmentND segment);

	/**
	 * Sphere label linking with center o and radius r
	 * 
	 * @return sphere
	 */
	GeoElement sphere(String label, GeoPointND M, GeoNumberValue r);

	/**
	 * Sphere with midpoint M through point P
	 * 
	 * @return sphere
	 */
	GeoElement sphere(String label, GeoPointND M, GeoPointND P);

	/**
	 * @return Cone
	 */
	GeoQuadricND cone(String label, GeoPointND origin,
			GeoVectorND direction, GeoNumberValue angle);

	/**
	 * @return Cone
	 */
	GeoQuadricND cone(String label, GeoPointND origin,
			GeoPointND secondPoint, GeoNumberValue angle);

	/**
	 * @return Cone
	 */
	GeoQuadricND cone(String label, GeoPointND origin, GeoLineND axis,
			GeoNumberValue angle);

	/**
	 * @return Cone
	 */
	GeoElement[] coneLimited(String[] labels, GeoPointND origin,
			GeoPointND secondPoint, GeoNumberValue r);

	/**
	 * @return Cone
	 */
	GeoElement[] coneLimited(String[] labels, GeoConicND bottom,
			GeoNumberValue height);

	/**
	 * @return Cylinder
	 */
	GeoQuadricND cylinder(String label, GeoPointND origin,
			GeoVectorND direction, GeoNumberValue r);

	/**
	 * @return Cylinder
	 */
	GeoQuadricND cylinder(String label, GeoPointND origin,
			GeoPointND secondPoint, GeoNumberValue r);

	/**
	 * @return Cylinder
	 */
	GeoQuadricND cylinder(String label, GeoLineND axis,
			GeoNumberValue r);

	/**
	 * @return Cylinder
	 */
	GeoElement[] cylinderLimited(String[] labels, GeoPointND origin,
			GeoPointND secondPoint, GeoNumberValue r);

	/**
	 * @return Cylinder
	 */
	GeoElement[] cylinderLimited(String[] labels, GeoConicND bottom,
			GeoNumberValue height);

	/**
	 * @return quadric side
	 */
	GeoQuadricND quadricSide(String label, GeoQuadricND quadric);

	/**
	 * Bottom side of a limited quadric (cylinder, cone).
	 * @param label output label
	 * @param quadric quadric
	 * @return bottom side
	 */
	GeoConicND quadricBottom(String label, GeoQuadricND quadric);

	/**
	 * Top side of a limited quadric (cylinder, cone).
	 * @param label output label
	 * @param quadric quadric
	 * @return top side
	 */
	GeoConicND quadricTop(String label, GeoQuadricND quadric);

	/**
	 * @return circle through points A, B, C
	 */
	GeoConicND circle3D(String label, GeoPointND A, GeoPointND B,
			GeoPointND C);

	/**
	 * @return circle with axis through point
	 */
	GeoConicND circle3D(String label, GeoLineND axis, GeoPointND A);

	/**
	 * @return circle with point, radius, axis
	 */
	GeoConicND circle3D(String label, GeoPointND A,
			GeoNumberValue radius, GeoDirectionND axis);

	/**
	 * @return circle with point, radius, axis orthogonal to xOy plane
	 */
	GeoConicND circle3D(String label, GeoPointND A,
			GeoNumberValue radius);

	/**
	 * @param label output label
	 * @param A center
	 * @param B point on circle
	 * @param axis axis
	 * @return circle with given center, point and direction
	 */
	GeoConicND circle3D(String label, GeoPointND A, GeoPointND B,
			GeoDirectionND axis);

	/**
	 * @return plane through points A, B, C
	 */
	GeoElement plane3D(String label, GeoPointND A, GeoPointND B,
			GeoPointND C);

	/**
	 * @return plane through lines a, b
	 */
	GeoElement plane3D(String label, GeoLineND a, GeoLineND b);

	/**
	 * @param label
	 *            output label
	 * @param cs2D
	 *            cs2D
	 * @return plane containing the 2D coord sys
	 */
	GeoPlaneND plane3D(String label, GeoCoordSys2D cs2D);

	/**
	 * @param cs2D coordinate system
	 * @return plane
	 */
	GeoPlaneND plane3D(GeoCoordSys2D cs2D);

	// //////////////////////////////////////////////
	// INTERSECTION (POINTS)

	/**
	 * @param label output label
	 * @param cs1 line
	 * @param cs2 planar object
	 * @param swapInputs whether to swap inputs when printing command string
	 * @return intersection point
	 */
	GeoElement intersect(String label, GeoLineND cs1, GeoCoordSys2D cs2,
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
	GeoElement intersect(String label, GeoLineND cs1, GeoLineND cs2);

	/**
	 * Calculate the intersection of the line g with the region of p
	 * 
	 * @return intersection points
	 */
	GeoElement[] intersectionPoint(String[] labels, GeoLineND g,
			GeoSurfaceFinite s);

	/**
	 * Intersection path between  line and finite planar shape.
	 * @param labels output labels
	 * @param g line
	 * @param s part of a plane
	 * @return intersection path
	 */
	GeoElement[] intersectPath(String[] labels, GeoLineND g,
			GeoSurfaceFinite s);

	/**
	 * Intersection path between plane and a polygon.
	 * @param labels output labels
	 * @param plane plane
	 * @param s any object
	 * @return intersection path if s is a polygon, null otherwise
	 */
	GeoElement[] intersectPath(String[] labels, GeoPlaneND plane,
			GeoElement s);

	/**
	 * Intersection path between a plane and multi-variable function.
	 * @param label output label
	 * @param plane plane
	 * @param fun multi-variable function
	 * @return intersection path
	 */
	GeoElement[] intersectPlaneFunctionNVar(String label,
			GeoPlaneND plane, GeoFunctionNVar fun);

	/**
	 * Intersection path between a plane and multi-variable function.
	 * @param plane plane
	 * @param fun multi-variable function
	 * @return intersection path
	 */
	GeoElement[] intersectPlaneFunctionNVar(GeoPlaneND plane,
			GeoFunctionNVar fun);

	/**
	 * Intersection path between a plane and implicit surface.
	 * @param plane plane
	 * @param surface implicit surface
	 * @return intersection path
	 */
	GeoElement[] intersectPlaneImplicitSurface(GeoPlaneND plane,
			GeoImplicitSurfaceND surface);

	/**
	 * Intersection path between a plane and a polygon.
	 * @param plane plane
	 * @param p polygon
	 * @return intersection path
	 */
	GeoElement[] intersectPath(GeoPlaneND plane, GeoPolygon p);

	/**
	 * @param labels output labels
	 * @param plane plane
	 * @param s other object
	 * @param outputSize number of outputs per object type (polygons, points, segments)
	 * @return intersection of plane with a region
	 */
	GeoElement[] intersectRegion(String[] labels, GeoPlaneND plane,
			GeoElement s, int[] outputSize);

	/**
	 * @param plane plane
	 * @param p other object
	 * @return intersection of plane with a region
	 */
	GeoElement[] intersectRegion(GeoPlaneND plane, GeoElement p);

	/**
	 * Intersection between a plane and a path.
	 * @param labels output labels
	 * @param p plane
	 * @param s path
	 * @return intersection points
	 */
	GeoElement[] intersectionPoint(String[] labels, GeoPlaneND p,
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
	GeoConicND intersect(String label, GeoPlaneND plane,
			GeoQuadricND quadric);

	/**
	 * Intersection conic of a limited quadric and a plane.
	 * @param label output label
	 * @param plane plane
	 * @param quadric quadric
	 * @return intersection conic
	 */
	GeoConicND intersectQuadricLimited(String label, GeoPlaneND plane,
			GeoQuadricND quadric);

	/**
	 * Intersection conic of a limited quadric and a plane.
	 * @param plane plane
	 * @param quadric quadric
	 * @return intersection conic
	 */
	GeoConicND intersectQuadricLimited(GeoPlaneND plane,
			GeoQuadricND quadric);

	/**
	 * Intersection conic of a quadric and a plane.
	 * @param plane plane
	 * @param quadric quadric
	 * @return intersection conic
	 */
	GeoConicND intersect(GeoPlaneND plane, GeoQuadricND quadric);

	/**
	 * Calculate the intersection of two quadrics, if it's a conic
	 * 
	 * @param labels output labels
	 * 
	 * @param quadric1
	 *            quadric1
	 * @param quadric2
	 *            quadric2
	 * @return conic intersection
	 */
	GeoElement[] intersectAsCircle(String[] labels,
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
	GeoElement[] intersectAsCircle(GeoQuadricND quadric1,
			GeoQuadricND quadric2);

	// //////////////////////////////////////////////
	// FUNCTIONS (2 VARS)

	/**
	 * @param label output label
	 * @param zcoord expression for z in terms of u and v
	 * @param localVarU local variable u
	 * @param Ufrom minimum value for u
	 * @param Uto max value for u
	 * @param localVarV local variable v
	 * @param Vfrom  min value for v
	 * @param Vto max value for v
	 * @return function
	 */
	GeoFunctionNVar function2Var(String label, GeoNumberValue zcoord,
			GeoNumeric localVarU, GeoNumberValue Ufrom, GeoNumberValue Uto,
			GeoNumeric localVarV, GeoNumberValue Vfrom, GeoNumberValue Vto);

	/**
	 * @param label output label
	 * @param f expression for f in terms of x and f
	 * @param xFrom minimum value for x
	 * @param xTo max value for x
	 * @param yFrom min value for y
	 * @param yTo max value for y
	 * @return function
	 */
	GeoFunctionNVar function2Var(String label, GeoFunctionNVar f,
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
	GeoElement curveCartesian3D(GeoNumberValue xcoord,
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
	GeoElement surfaceOfRevolution(Path function,
			GeoNumberValue angle, GeoLineND line);

	// //////////////////////////////////////////////
	// intersection algos

	/**
	 * intersection between 3D line and conic
	 * 
	 * @return two intersection points
	 */
	GeoPointND[] intersectLineConic(String[] labels, GeoLineND g,
			GeoConicND c);

	/**
	 * get only one intersection point of two conics that is near to the given
	 * location (xRW, yRW)
	 * 
	 * @return intersection point
	 */
	GeoPointND intersectLineConicSingle(String label, GeoLineND g,
			GeoConicND c, double xRW, double yRW, CoordMatrix mat);

	/**
	 * @param label output label
	 * @param g line
	 * @param c conic
	 * @param origin hitting origin
	 * @param direction hitting direction
	 * @return intersection
	 */
	GeoPoint3D intersectLineConicSingle(String label, GeoLineND g,
			GeoConicND c, Coords origin, Coords direction);

	/**
	 * get only one intersection point of two conics
	 * 
	 * @return intersection point
	 */
	GeoPointND intersectLineConicSingle(String label, GeoLineND g,
			GeoConicND c, NumberValue index);

	/**
	 * get only one intersection point of two conics
	 * 
	 * @return intersection point
	 */
	GeoPointND intersectLineConicSingle(String label, GeoLineND g,
			GeoConicND c, int index);

	/**
	 * get only one intersection point of two conics, near to refPoint
	 * 
	 * @return intersection point
	 */
	GeoPointND intersectLineConicSingle(String label, GeoLineND g,
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
	GeoPointND[] intersectConics(String[] labels, GeoConicND A,
			GeoQuadricND B);

	/**
	 * @param label output label
	 * @param A conic
	 * @param B conic
	 * @param xRW initial guess for x-coordinate
	 * @param yRW initial guess for y-coordinate
	 * @param mat view matrix
	 * @return intersection
	 */
	GeoPointND intersectConicsSingle(String label, GeoConicND A,
			GeoQuadricND B, double xRW, double yRW, CoordMatrix mat);

	/**
	 * @param label output label
	 * @param A conic
	 * @param B conic
	 * @param origin hitting origin
	 * @param direction hitting direction
	 * @return intersection
	 */
	GeoPoint3D intersectConicsSingle(String label, GeoConicND A,
			GeoQuadricND B, Coords origin, Coords direction);

	/**
	 * @param label output label
	 * @param A conic
	 * @param B quadric
	 * @param index index
	 * @return intersection
	 */
	GeoPointND intersectConicsSingle(String label, GeoConicND A,
			GeoQuadricND B, NumberValue index);

	/**
	 * @param label output label
	 * @param A quadric
	 * @param B conic
	 * @param index index
	 * @return intersection
	 */
	GeoPointND intersectConicsSingle(String label, GeoConicND A,
			GeoQuadricND B, int index);

	/**
	 * @param label output label
	 * @param A conic
	 * @param B quadric
	 * @param refPoint reference point
	 * @return intersection
	 */
	GeoPointND intersectConicsSingle(String label, GeoConicND A,
			GeoQuadricND B, GeoPointND refPoint);

	/**
	 * intersect line/quadric
	 * 
	 * @return intersection points
	 */
	GeoPointND[] intersectLineQuadric(String[] labels, GeoLineND A,
			GeoQuadricND B);

	/**
	 * get only one intersection point of two conics that is near to the given
	 * location (xRW, yRW)
	 * 
	 * @return intersection point
	 */
	GeoPointND intersectLineQuadricSingle(String label, GeoLineND g,
			GeoQuadricND q, double xRW, double yRW, CoordMatrix4x4 mat);

	/**
	 * @param label output label
	 * @param g line
	 * @param q quadric
	 * @param origin hitting origin
	 * @param direction hitting direction
	 * @return intersection
	 */
	GeoPoint3D intersectLineQuadricSingle(String label, GeoLineND g,
			GeoQuadricND q, Coords origin, Coords direction);

	/**
	 * get only one intersection point of line and quadric choice depends on
	 * command input
	 * 
	 * @return intersection point
	 */
	GeoPointND intersectLineQuadricSingle(String label, GeoLineND g,
			GeoQuadricND q, NumberValue index);

	/**
	 * get only one intersection point of line and quadric choice depends on
	 * command input
	 * 
	 * @return intersection point
	 */
	GeoPointND intersectLineQuadricSingle(String label, GeoLineND g,
			GeoQuadricND q, int index);

	/**
	 * @param label output label
	 * @param g line
	 * @param q quadric
	 * @param refPoint reference point
	 * @return intersection
	 */
	GeoPointND intersectLineQuadricSingle(String label, GeoLineND g,
			GeoQuadricND q, GeoPointND refPoint);

	/**
	 * intersect plane/conic
	 * 
	 * @return intersection points
	 */
	GeoPointND[] intersectPlaneConic(String[] labels, GeoCoordSys2D A,
			GeoConicND B);

	/**
	 * Intersection of a curve and a planar shape.
	 * @return intersection points
	 */
	GeoElementND[] intersectPlaneCurve(String[] labels, GeoCoordSys2D A,
			GeoCurveCartesianND B);

	/**
	 * Intersection of 2 planes.
	 * @param label output label
	 * @param cs1 first plane
	 * @param cs2 second plane
	 * @return intersection line
	 */
	GeoElement intersectPlanes(String label, GeoPlaneND cs1,
			GeoPlaneND cs2);

	/**
	 * intersect polygons (boundary)
	 * 
	 * @return intersect points
	 */
	GeoElement[] intersectionPoint(String[] labels, GeoPolygon poly0,
			GeoPolygon poly1);

	/**
	 * @return intersect polygons (region)
	 */
	GeoElement[] intersectPolygons(String[] labels, GeoPoly inPoly0,
			GeoPoly inPoly1);

	/**
	 * @return Difference polygons (region)
	 */
	GeoElement[] differencePolygons(String[] labels,
			GeoPolygon inPoly0, GeoPolygon inPoly1);

	/**
	 * @return Difference polygons or exclusive difference polygons (region)
	 */
	GeoElement[] differencePolygons(String[] labels,
			GeoPolygon inPoly0, GeoPolygon inPoly1, GeoBoolean exclusive);

	/**
	 * @return Union polygons (region)
	 */
	GeoElement[] unionPolygons(String[] labels, GeoPoly inPoly0,
			GeoPoly inPoly1);

	/**
	 * Intersection of 2 planes.
	 * @param cs1 first plane
	 * @param cs2 second plane
	 * @return intersection line
	 */
	GeoElement intersectPlanes(GeoPlaneND cs1, GeoPlaneND cs2);

	/**
	 * Closest point to a line on another line.
	 * @param label output label
	 * @param g line where the point should be
	 * @param h other line
	 * @return closest point
	 */
	GeoElement closestPoint(String label, GeoLineND g, GeoLineND h);

	/**
	 * Closest point to another point on a given path.
	 * @param label output label
	 * @param p path
	 * @param point other point
	 * @return closest point
	 */
	GeoElement closestPoint(String label, Path p, GeoPointND point);

	/**
	 * Closest point to another point on a given region.
	 * @param label output label
	 * @param r region
	 * @param point other point
	 * @return closest point
	 */
	GeoPointND closestPoint(String label, Region r, GeoPointND point);

	/**
	 * Distance between a point and a plane.
	 * @param label output label
	 * @param point point
	 * @param plane plane
	 * @return distance
	 */
	GeoNumeric distance(String label, GeoPointND point,
			GeoPlaneND plane);

	/**
	 * Distance between two planes.
	 * @param label output label
	 * @param a first plane
	 * @param b second plane
	 * @return distance
	 */
	GeoNumeric distance(String label, GeoPlaneND a, GeoPlaneND b);

	/**
	 * @return Angle named label between three points
	 */
	GeoAngle angle3D(String label, GeoPointND A, GeoPointND B,
			GeoPointND C);

	/**
	 * @return Angle (oriented) named label between three points
	 */
	GeoAngle angle3D(String label, GeoPointND A, GeoPointND B,
			GeoPointND C, GeoDirectionND orientation);

	/**
	 * @return Angle named label between lines
	 */
	GeoAngle angle3D(String label, GeoLineND g, GeoLineND h);

	/**
	 * @return Angle named label between lines, oriented
	 */
	GeoAngle angle3D(String label, GeoLineND g, GeoLineND h,
			GeoDirectionND orientation);

	/**
	 * @return Angle named label between planes
	 */
	GeoAngle angle3D(String label, GeoPlaneND g, GeoPlaneND h);

	/**
	 * @return Angle named label between line and plane
	 */
	GeoAngle angle3D(String label, GeoLineND l, GeoPlaneND p);

	/**
	 * create angle checking start/end points
	 * 
	 * @param line1
	 *            line1
	 * @param line2
	 *            line2
	 * @return angle
	 */
	GeoAngle createLineAngle(GeoLineND line1, GeoLineND line2);

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
	GeoAngle createLineAngle(GeoLineND line1, GeoLineND line2,
			GeoDirectionND orientation);

	/**
	 * @return Angle named label between vectors
	 */
	GeoAngle angle3D(String label, GeoVectorND v, GeoVectorND w);

	/**
	 * @return Angle (oriented) named label between vectors
	 */
	GeoAngle angle3D(String label, GeoVectorND v, GeoVectorND w,
			GeoDirectionND orientation);

	/**
	 * 
	 * @param labels
	 *            output labels
	 * @param poly
	 *            poly
	 * @return angles for the polygon
	 */
	GeoElement[] angles3D(String[] labels, GeoPolygon poly);

	/**
	 * Creates a new point C by rotating B around A using angle alpha and a new
	 * angle BAC (for positive orientation) resp. angle CAB (for negative
	 * orientation). The labels[0] is for the angle, labels[1] for the new point
	 * 
	 * @return angle
	 */
	GeoElement[] angle(String[] labels, GeoPointND B, GeoPointND A,
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
	GeoElement[] angles3D(String[] labels, GeoPolygon poly,
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
	GeoElement[] angles3D(String[] labels, GeoPolygon poly, boolean internalAngle);

	/**
	 * Length named label of vector v
	 * 
	 * @return length of the vector
	 */
	GeoNumeric length(String label, GeoVectorND v);

	/**
	 * @return cube with A, B for first points and v for direction
	 */
	GeoElement[] archimedeanSolid(String[] labels, GeoPointND A,
			GeoPointND B, GeoDirectionND v, Commands name);

	/**
	 * @return solid with A, B, C for first points
	 */
	GeoElement[] archimedeanSolid(String[] labels, GeoPointND A,
			GeoPointND B, GeoPointND C, Commands name);

	/**
	 * @return solid with A, B, C for first points (C point on an ad hoc circle)
	 */
	GeoElement[] archimedeanSolid(String[] labels, GeoPointND A,
			GeoPointND B, Commands name);

	/**
	 * @param labels output labels
	 * @param poly one face of the solid
	 * @param isDirect flag for direct
	 * @param name solid type
	 * @return resulting elements (solid, faces, edges, vertices)
	 */
	GeoElement[] archimedeanSolid(String[] labels, GeoPolygon poly,
			GeoBoolean isDirect, Commands name);

	/*
	 * TRANSFORMATIONS
	 */

	/**
	 * @return translate geoTrans by vector v
	 */
	GeoElement[] translate3D(String label, GeoElementND geoTrans,
			GeoVectorND v);

	/**
	 * New Geo3DVec instance.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param z z-coordinate
	 * @return 3D vector
	 */
	Geo3DVecInterface newGeo3DVec(double x, double y,
			double z);

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
	GeoElement[] rotate3D(String label, GeoElementND geoRot,
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
	GeoElement[] rotate3D(String label, GeoElementND geoRot,
			GeoNumberValue phi, GeoLineND line);

	/**
	 * @return mirrored object
	 */
	GeoElement[] mirror3D(String label, GeoElement geo, GeoPointND p);

	/**
	 * Mirror object at a line.
	 * @param label output label
	 * @param geo object to mirror
	 * @param line line
	 * @return mirrored object
	 */
	GeoElement[] mirror3D(String label, GeoElement geo, GeoLineND line);

	/**
	 * Mirror object at a plane.
	 * @param label output label
	 * @param geo object to mirror
	 * @param plane plane
	 * @return mirrored object
	 */
	GeoElement[] mirror3D(String label, GeoElement geo,
			GeoCoordSys2D plane);

	/**
	 * @param label output label
	 * @param geoDil object to be dilated
	 * @param r dilation factor
	 * @param S dilation center
	 * @return dilated object
	 */
	GeoElement[] dilate3D(String label, GeoElement geoDil,
			GeoNumberValue r, GeoPointND S);

	/**
	 * 
	 * @param label
	 *            output label
	 * @param hasVolume
	 *            hasVolume
	 * @return volume of hasVolume
	 */
	GeoNumeric volume(String label, HasVolume hasVolume);

	/**
	 * 
	 * @param label
	 *            output label
	 * @param hasHeight
	 *            hasHeight
	 * @return oriented height of hasHeight
	 */
	GeoNumeric orientedHeight(String label, HasHeight hasHeight);

	/**
	 * 
	 * @param labels
	 *            output labels
	 * @param conic
	 *            conic
	 * @return corners for a conic section
	 */
	GeoElement[] corner(String[] labels, GeoConicND conic);

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
	GeoElement[] polyhedronNet(String[] labels, GeoElement p,
			NumberValue v, GeoPolygon bottomFace, GeoSegmentND[] pivotSegments);

	/**
	 * Convex polyhedron form a list of points.
	 * @param labels output labels
	 * @param pointList points
	 * @return polyhedron with faces and edges
	 */
	GeoElement[] polyhedronConvex(String[] labels,
			GeoElement[] pointList);

	/**
	 * @return circle arc from three points
	 */
	GeoConicPartND circumcircleArc3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C);

	/**
	 * @return circle sector from three points
	 */
	GeoConicPartND circumcircleSector3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C);

	/**
	 * @return Angular bisectors of lines g, h
	 */
	GeoElement[] angularBisector3D(String[] labels, GeoLineND g,
			GeoLineND h);

	/**
	 * @return Angular bisectors of points A, B, C
	 */
	GeoElement angularBisector3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C);

	/**
	 * @return Angular bisectors of points A, B, C, oriented
	 */
	GeoElement angularBisector3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C, GeoDirectionND orientation);

	/**
	 * @return circle arc/sector from center and two points on arc
	 */
	GeoConicPartND circleArcSector3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C, int type);

	/**
	 * @return circle arc/sector from center and two points on arc (oriented)
	 */
	GeoConicPartND circleArcSector3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C, GeoDirectionND orientation, int type);

	/**
	 * @param label output label
	 * @param coefX coefficients of linear function t&#8594;x
	 * @param coefY coefficients of linear function t&#8594;y
	 * @param coefZ coefficients of linear function t&#8594;z
	 * @return 3D line
	 */
	GeoLineND line3D(String label, ExpressionValue[] coefX,
			ExpressionValue[] coefY, ExpressionValue[] coefZ);

	/**
	 * @return semicircle joining A and B, oriented
	 */
	GeoConicPartND semicircle3D(String label, GeoPointND A, GeoPointND B,
			GeoDirectionND orientation);

	/**
	 * @return tangents to c through P
	 */
	GeoElement[] tangent3D(String[] labels, GeoPointND P, GeoConicND c);

	/**
	 * Tangent to a conic.
	 * @param labels output labels
	 * @param l direction
	 * @param c conic
	 * @return tangent line
	 */
	GeoElement[] tangent3D(String[] labels, GeoLineND l, GeoConicND c);

	/**
	 * Common tangents to 2 conics.
	 * @param labels output labels
	 * @param c1 first conic
	 * @param c2 second conic
	 * @return tangents
	 */
	GeoElement[] commonTangents3D(String[] labels, GeoConicND c1,
			GeoConicND c2);

	/**
	 * Diameter line.
	 * @param label label
	 * @param g direction
	 * @param c conic
	 * @return diameter line of the conic
	 */
	GeoElement diameterLine3D(String label, GeoLineND g, GeoConicND c);

	/**
	 * Diameter line.
	 * @param label label
	 * @param v direction
	 * @param c conic
	 * @return diameter line of the conic
	 */
	GeoElement diameterLine3D(String label, GeoVectorND v, GeoConicND c);

	/**
	 * Line bisector.
	 * @param label output label
	 * @param segment segment
	 * @param orientation orientation
	 * @return line bisector
	 */
	GeoElement lineBisector3D(String label, GeoSegmentND segment,
			GeoDirectionND orientation);

	/**
	 * Creates segment bisector where the segment is given by two points,
	 * direction of the bisector is given by a line or plane.
	 * @param label output label
	 * @param a segment endpoint
	 * @param b segment endpoint
	 * @param orientation bisector orientation
	 * @return segment bisector
	 */
	GeoElement lineBisector3D(String label, GeoPointND a, GeoPointND b,
			GeoDirectionND orientation);

	/**
	 * Conic from five points.
	 * @param label output label
	 * @param points 5 points
	 * @return conic
	 */
	GeoConicND conic3D(String label, GeoPointND[] points);

	/**
	 * @return ellipse with foci A, B passing through C
	 */
	GeoConicND ellipseHyperbola3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C, int type);

	/**
	 * @return ellipse with foci A, B passing through C, oriented
	 */
	GeoConicND ellipseHyperbola3D(String label, GeoPointND A,
			GeoPointND B, GeoPointND C, GeoDirectionND orientation,
			int type);

	/**
	 * @return parabola with focus F and line l
	 */
	GeoConicND parabola3D(String label, GeoPointND F, GeoLineND l);

	/**
	 * @return locus line for Q dependent on P. Note: P must be a point on a
	 *         path.
	 */
	GeoElement locus3D(String label, GeoPointND Q, GeoPointND P);

	/**
	 * Locus from a slider and a dependent point.
	 * @param label output label
	 * @param Q point
	 * @param slider slider
	 * @return locus
	 */
	GeoElement locus3D(String label, GeoPointND Q, GeoNumeric slider);

	/**
	 * tangent to parametric curve
	 * @param label output label
	 * @param point point on curve
	 * @param curve curve
	 * @return tangent line
	 */
	GeoElement tangent3D(String label, GeoPointND point,
			GeoCurveCartesianND curve);

	/**
	 * Convert line to plane, e.g. x=2y to x=2y+0z
	 * @param geoElement line
	 * @return plane
	 */
	GeoElement lineToPlane(GeoElement geoElement);

	/**
	 * Intersection of multi-variable function and a plane
	 * @param cons construction
	 * @param function function
	 * @param plane plaane
	 * @return intersection algorithm
	 */
	AlgoElement intersectFunctionNVarPlane(Construction cons,
			GeoFunctionNVar function, GeoPlaneND plane);
}
