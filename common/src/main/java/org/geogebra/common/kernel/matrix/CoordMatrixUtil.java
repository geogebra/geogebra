package org.geogebra.common.kernel.matrix;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.util.DoubleUtil;

/**
 * @author ggb3D
 * 
 *         Class for algebra utilities
 */
public final class CoordMatrixUtil {

	/**
	 * Return points p1 from line1 and p2 from line2 that are the nearest
	 * possible. Return infinite points if the two lines are parallel.
	 * 
	 * @param o1
	 *            origin of line1
	 * @param v1
	 *            direction of line1
	 * @param o2
	 *            origin of line2
	 * @param v2
	 *            direction of line2
	 * @return {p1,p2,{p1 coord on l1,p2 coord on l2}}
	 */
	static public Coords[] nearestPointsFromTwoLines(Coords o1, Coords v1,
			Coords o2, Coords v2) {

		double[] project1 = new double[4], project2 = new double[4],
				lineCoords = new double[2];

		nearestPointsFromTwoLines(o1, v1, o2, v2, project1, project2,
				lineCoords, new double[4]);

		return new Coords[] { new Coords(project1), new Coords(project2),
				new Coords(lineCoords) };
	}

	/**
	 * Set points from line1 and from line2 that are the nearest possible.
	 * Return infinite points if the two lines are parallel.
	 * 
	 * @param o1
	 *            origin of line1
	 * @param v1
	 *            direction of line1
	 * @param o2
	 *            origin of line2
	 * @param v2
	 *            direction of line2
	 * @param project1
	 *            point on line 1
	 * @param project2
	 *            point on line 2
	 * @param lineCoords
	 *            parameters of each point on each line
	 * @param tmp
	 *            tmp values (length 4)
	 * 
	 */
	static public void nearestPointsFromTwoLines(Coords o1, Coords v1,
			Coords o2, Coords v2, double[] project1, double[] project2,
			double[] lineCoords, double[] tmp) {

		// if v1 and v2 are parallel, return infinite points v1 and v2
		Coords vn = v1.crossProduct(v2);
		if (vn.equalsForKernel(0, Kernel.STANDARD_PRECISION)) {
			// Application.debug("v1="+v1.toString()+"\nv2="+v2.toString());
			v1.copy(project1);
			v2.copy(project2);
			lineCoords[0] = Double.NaN;
			lineCoords[1] = Double.NaN;
			return;
		}

		// plane containing o1, v1, vn, with v2 direction
		// projection of o2 on this plane
		o2.projectPlaneNoCheck(v1, vn, v2, o1, project2, tmp);
		lineCoords[1] = -tmp[2]; // points in lines coords

		// plane containing o2, v2, vn, with v1 direction
		// projection of o1 on this plane
		o1.projectPlaneNoCheck(v2, vn, v1, o2, project1, tmp);
		lineCoords[0] = -tmp[2]; // points in lines coords

	}

	/**
	 * Return the point p intersection of the line and the plane. <br>
	 * Return infinite point (direction of the line) if the line is parallel to
	 * plane.
	 * 
	 * @param line
	 *            the line
	 * @param plane
	 *            the plane
	 * @return two vectors {globalCoords,inPlaneCoords}: the point p
	 *         intersection of the line and the plane, and the original point in
	 *         (plane.vx, plane.vy, line direction, line origin) coords
	 */
	static public Coords[] intersectLinePlane(CoordMatrix line,
			CoordMatrix plane) {

		Coords v = line.getColumn(1);

		// if v is orthogonal to vn, v is parallel to the plane and so the line
		// is
		// if (Kernel.isEqual(vn.dotproduct(v),0,Kernel.STANDARD_PRECISION))
		// return null;

		// project the origin of the line on the plane (along v direction)
		Coords o = line.getColumn(2);
		Coords[] result = new Coords[] { new Coords(4), new Coords(4) };
		o.projectPlaneThruV(plane, v, result[0], result[1]);
		return result;
	}

	/**
	 * return the spherical coords of v
	 * 
	 * @param v
	 *            3D vector in cartesian coords
	 * @param ret
	 *            output param for the spherical coords of v
	 */
	static public void sphericalCoords(Coords v, Coords ret) {

		double x = v.get(1);
		double y = v.get(2);
		double z = v.get(3);

		// norms
		double n2 = x * x + y * y;
		double n1 = Math.sqrt(n2);
		double norm = Math.sqrt(n2 + z * z);

		// angles
		double a;
		if (n1 == 0) {
			a = 0;
		} else {
			a = Math.acos(x / n1);
			if (y < 0) {
				a *= -1;
			}
		}

		double b;
		if (norm == 0) {
			b = 0;
		} else {
			b = Math.acos(n1 / norm);
			if (z < 0) {
				b *= -1;
			}
		}

		ret.setX(norm);
		ret.setY(a);
		ret.setZ(b);

	}

	/**
	 * @param origin
	 *            line origin
	 * @param direction
	 *            line direction
	 * @param plane
	 *            plane
	 * @return (a,b,c) where ax+by+c=0 is an equation of the line in the plane
	 */
	static public Coords lineEquationVector(Coords origin,
			Coords direction, CoordMatrix plane) {

		Coords o = new Coords(4);
		origin.projectPlaneInPlaneCoords(plane, o);
		Coords d = new Coords(4);
		direction.projectPlaneInPlaneCoords(plane, d);
		return lineEquationVector(o, d);
	}

	/**
	 * @param origin
	 *            line origin
	 * @param direction
	 *            line direction
	 * @return (a,b,c) where ax+by+c=0 is an equation of the line in xOy plane
	 */
	static public Coords lineEquationVector(Coords origin,
			Coords direction) {

		// if lines is not in the plane, return null
		if (!DoubleUtil.isZero(origin.getZ()) || !DoubleUtil.isZero(direction.getZ())) {
			return null;
		}

		double x = -direction.getY();
		double y = direction.getX();
		double z = -x * origin.getX() - y * origin.getY();

		return new Coords(x, y, z);
	}

	/**
	 * 
	 * @param plane1
	 *            first plane
	 * @param plane2
	 *            second plane
	 * @return {origin, direction} of the line intersection of the two planes
	 */
	static public Coords[] intersectPlanes(CoordMatrix plane1,
			CoordMatrix plane2) {

		// compute direction vector
		Coords vn1 = plane1.getVz();
		Coords vn2 = plane2.getVz();
		Coords v = vn1.crossProduct(vn2);

		Coords direction = new Coords(4);
		Coords origin = new Coords(4);

		// compute origin
		if (v.isZero()) { // planes are parallel or equal
			origin.set(plane1.getOrigin()); // planes are equal
			plane1.getOrigin().projectPlaneInPlaneCoords(plane2, direction);
			if (!DoubleUtil.isZero(direction.getZ())) { // plane are not included:
													// return (0,0,0,0) as
													// origin
				origin.set(0);
			}
		} else {
			// projection of first plane origin on second plane
			// direction orthogonal to v and colinear to first plane
			plane1.getOrigin().projectPlaneThruV(plane2, vn1.crossProduct4(v),
					origin);
		}

		// return line
		direction.set(v);
		direction.setW(0); // v is Coords(3)

		return new Coords[] { origin, direction };
	}

}
