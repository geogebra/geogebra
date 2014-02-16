package geogebra3D.geogebra.common.geogebra3D.euclidian3D.draw;

import geogebra.common.kernel.arithmetic.FunctionalNVar;

/**
 * Detects the intersection of objects with rays - used for picking points on
 * objects.
 * 
 * @author André Eriksson
 */
public class FindRayIntersection {

	/**
	 * Finds the intersection of a ray with an object.
	 * @param oc any object that contains a triangle octree
	 * @param p0 first point on the ray
	 * @param p1 second point on the ray 
	 * @return the intersection point
	 * @throws Exception if there is no intersection
	 */
	static public float[] findIntersection(OctreeCollection oc, float[] p0, float[] p1) throws Exception {
		float[] isect = null;

		isect = oc.getVisibleTriangleOctree().rayFirstIntersect(p0, p1);

		if (isect == null) { // no intersection
			throw new Exception("No intersection found");
		}

		// find better root using numerical methods with the intersection point
		// as start guess
		if (oc instanceof FunctionalNVar) {
			isect = newtonApprox((FunctionalNVar) oc, p0, p1, isect);
		}

		return isect;
	}

	/**
	 * Approximate the intersection point of a ray with a FunctionalNVar using
	 * Newton's method
	 * 
	 * @param f
	 *            the functional
	 * @param p0
	 *            first point on ray
	 * @param p1
	 *            second point on ray
	 * @param guess
	 *            start guess - a 3D point
	 * @return the closest root to the start guess (hopefully)
	 */
	static private float[] newtonApprox(FunctionalNVar f, float[] p0, float[] p1,
			float[] guess) throws Exception {
		// TODO: attempt to solve the equation f(x,y,z)-g(x,y,z)=0 where
		// f(x,y,z) is
		// the equation of the functional and g(x,y,z) is the equation of the
		// ray
		return null;
	}

}
