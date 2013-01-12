package geogebra.common.kernel.Matrix;

import geogebra.common.kernel.Kernel;

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
	static final public Coords[] nearestPointsFromTwoLines(Coords o1,
			Coords v1, Coords o2, Coords v2) {

		// if v1 and v2 are parallel, return infinite points v1 and v2
		Coords vn = v1.crossProduct(v2);
		if (vn.equalsForKernel(0, Kernel.STANDARD_PRECISION)) {
			// Application.debug("v1="+v1.toString()+"\nv2="+v2.toString());
			return new Coords[] { v1.copyVector().normalize(),
					v2.copyVector().normalize(),
					new Coords(new double[] { Double.NaN, Double.NaN }) };
		}
		// return null;

		// vn.normalize();

		// plane containing o1, v1, vn, with v2 direction
		CoordMatrix plane = new CoordMatrix(4, 4);
		plane.set(v1, 1);
		plane.set(vn, 2);
		plane.set(v2, 3);
		plane.set(o1, 4);
		// projection of o2 on this plane
		Coords[] project2 = o2.projectPlane(plane);

		// plane containing o2, v2, vn, with v1 direction
		plane.set(v2, 1);
		plane.set(vn, 2);
		plane.set(v1, 3);
		plane.set(o2, 4);
		// projection of o2 on this plane
		Coords[] project1 = o1.projectPlane(plane);

		// points in lines coords
		Coords lineCoords = new Coords(new double[] { -project1[1].get(3),
				-project2[1].get(3) });

		return new Coords[] { project1[0], project2[0], lineCoords };
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
	static final public Coords[] intersectLinePlane(CoordMatrix line,
			CoordMatrix plane) {

		Coords v = line.getColumn(1);

		// if v is orthogonal to vn, v is parallel to the plane and so the line
		// is
		// if (Kernel.isEqual(vn.dotproduct(v),0,Kernel.STANDARD_PRECISION))
		// return null;

		// project the origin of the line on the plane (along v direction)
		Coords o = line.getColumn(2);
		return o.projectPlaneThruV(plane, v);
	}

	/**
	 * return the spherical coords of v
	 * 
	 * @param v
	 *            3D vector in cartesian coords
	 * @return the spherical coords of v
	 */
	static final public Coords sphericalCoords(Coords v) {

		double x = v.get(1);
		double y = v.get(2);
		double z = v.get(3);

		// norms
		double n2 = x * x + y * y;
		double n1 = Math.sqrt(n2);
		double norm = Math.sqrt(n2 + z * z);

		// angles
		double a;
		if (n1 == 0)
			a = 0;
		else {
			a = Math.acos(x / n1);
			if (y < 0)
				a *= -1;
		}

		double b;
		if (norm == 0)
			b = 0;
		else {
			b = Math.acos(n1 / norm);
			if (z < 0)
				b *= -1;
		}

		return new Coords(new double[] { norm, a, b });
	}

	/**
	 * return the cartesian coords of v
	 * 
	 * @param v
	 *            3D vector in spherical coords
	 * @return the cartesian coords of v
	 */
	static final public Coords cartesianCoords(Coords v) {

		return cartesianCoords(v.get(1), v.get(2), v.get(3));
	}

	/**
	 * return the cartesian coords of (r,theta,phi)
	 * 
	 * @param r
	 *            spherical radius
	 * @param theta
	 *            (Oz) angle
	 * @param phi
	 *            (xOy) angle
	 * @return the cartesian coords of (r,theta,phi)
	 */
	static final public Coords cartesianCoords(double r, double theta,
			double phi) {

		double z = r * Math.sin(phi);
		double n2 = r * Math.cos(phi);
		double x = n2 * Math.cos(theta);
		double y = n2 * Math.sin(theta);

		return new Coords(new double[] { x, y, z, 0 });
	}

	/**
	 * 
	 * @param origin
	 * @param direction
	 * @param plane
	 * @return (a,b,c) where ax+by+c=0 is an equation of the line in the plane
	 */
	static final public Coords lineEquationVector(Coords origin,
			Coords direction, CoordMatrix plane) {

		Coords o = origin.projectPlane(plane)[1];
		Coords d = direction.projectPlane(plane)[1];
		return lineEquationVector(o, d);
	}

	/**
	 * 
	 * @param origin
	 * @param direction
	 * @return (a,b,c) where ax+by+c=0 is an equation of the line in xOy plane
	 */
	static final public Coords lineEquationVector(Coords origin,
			Coords direction) {

		// if lines is not in the plane, return null
		if (!Kernel.isZero(origin.getZ())
				|| !Kernel.isZero(direction.getZ()))
			return null;

		double x = -direction.getY();
		double y = direction.getX();
		double z = -x * origin.getX() - y * origin.getY();

		return new Coords(x, y, z);
	}

	/**
	 * 
	 * @param plane1
	 * @param plane2
	 * @return {origin, direction} of the line intersection of the two planes
	 */
	static final public Coords[] intersectPlanes(CoordMatrix plane1,
			CoordMatrix plane2) {

		// compute direction vector
		Coords vn1 = plane1.getVz();
		Coords vn2 = plane2.getVz();
		Coords v = vn1.crossProduct(vn2);
		

		// compute origin
		Coords origin;
		if (v.isZero()){ //planes are parallel or equal
			origin = plane1.getOrigin(); //planes are equal
			Coords[] project = origin.projectPlane(plane2);
			if (!Kernel.isZero(project[1].getZ())) //plane are not included: return (0,0,0,0) as origin
				origin = new Coords(4);
		}else{
			// projection of first plane origin on second plane
			// direction orthogonal to v and colinear to first plane
			Coords[] project = plane1.getOrigin().projectPlaneThruV(plane2,
					vn1.crossProduct(v));
			origin =  project[0];
		}

		// return line
		Coords direction = new Coords(4);
		direction.set(v);

		return new Coords[] {origin, direction };
	}

}
