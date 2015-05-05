package org.geogebra.common.geogebra3D.euclidian3D.plots;

import org.geogebra.common.kernel.Matrix.Coords;

/**
 * A set of collision routines
 * 
 * @author Andre Eriksson
 */
public class Collision {

	/**
	 * Whether or not to use rounding when comparing numbers (to ensure
	 * numerical stability)
	 */
	static final boolean USE_EPSILON_TEST = true;

	/**
	 * Threshold for epsilon comparisons: any number -EPSILON < x < EPSILON is
	 * regarded as 0
	 */
	static final float EPSILON = 1e-5f;

	/**
	 * Gives the axis-aligned bounding box of a triangle
	 * 
	 * @param tri
	 *            the triangle as a set of nine floats
	 * @return the bounding box as {xMin, xMax, yMin, yMax, zMin, zMax}
	 */
	static public float[] triangleBoundingBox(float[] tri) {
		float[] bb = new float[6];
		for (int i = 0; i < 3; i++) {
			boolean s1 = tri[i] < tri[i + 3];
			boolean s2 = tri[i + 3] < tri[i + 6];
			boolean s3 = tri[i] < tri[i + 6];
			bb[i * 2] = s1 ? (s3 ? tri[i] : tri[i + 6]) : (s2 ? tri[i + 3]
					: tri[i + 6]);
			bb[i * 2 + 1] = s1 ? (s2 ? tri[i + 6] : tri[i + 3])
					: (s3 ? tri[i + 6] : tri[i]);
		}
		return bb;
	}

	/**
	 * Gives the axis-aligned bounding box of a segment
	 * 
	 * @param p0
	 *            first segment end point
	 * @param p1
	 *            second segment end point
	 * @return the bounding box as {xMin, xMax, yMin, yMax, zMin, zMax}
	 */
	static public float[] segmentBoundingBox(float[] p0, float[] p1) {
		boolean x = p0[0] < p1[0];
		boolean y = p0[1] < p1[1];
		boolean z = p0[2] < p1[2];
		return new float[] { x ? p0[0] : p1[0], x ? p1[0] : p0[0],
				y ? p0[0] : p1[0], y ? p1[0] : p0[0], z ? p0[0] : p1[0],
				z ? p1[0] : p0[0] };
	}

	/**
	 * Computes the segment of intersection between two triangles.
	 * 
	 * @param t1
	 *            the first triangle as a three-Coord vector
	 * @param t2
	 *            the second triangle as a three-Coord vector
	 * @return If the triangles intersect, the two endpoints of the intersection
	 *         segment. Otherwise null
	 */
	static public Coords[] triTriIntersection(Coords[] t1, Coords[] t2) {

		Coords V10 = t1[0];
		Coords V11 = t1[1];
		Coords V12 = t1[2];

		Coords V20 = t2[0];
		Coords V21 = t2[1];
		Coords V22 = t2[2];

		// compute plane equations for the two triangles
		Coords N1 = V11.sub(V10).crossProduct(V12.sub(V10));
		double d1 = -N1.dotproduct(V10);

		Coords N2 = V21.sub(V20).crossProduct(V22.sub(V20));
		double d2 = -N2.dotproduct(V20);

		// compute the signed distances from each vertex to the plane defined by
		// the other triangle
		double dV10 = N2.dotproduct(V10) + d2;
		double dV11 = N2.dotproduct(V11) + d2;
		double dV12 = N2.dotproduct(V12) + d2;

		double dV20 = N1.dotproduct(V20) + d1;
		double dV21 = N1.dotproduct(V21) + d1;
		double dV22 = N1.dotproduct(V22) + d1;

		// reject overlap if all vertices of one triangle have the same sign
		if ((dV10 > 0 && dV11 > 0 && dV12 > 0)
				|| (dV10 < 0 && dV11 < 0 && dV12 < 0))
			return null;

		if ((dV20 > 0 && dV21 > 0 && dV22 > 0)
				|| (dV20 < 0 && dV21 < 0 && dV22 < 0))
			return null;

		if (dV10 == 0 && dV11 == 0 && dV12 == 0) {
			// TODO: handle coplanarity (in this case dij==0 for all i,j)
			return null;
		}

		// TODO: handle one dij being 0

		// make sure that V11 is on the opposite side of the intersection to the
		// other two vertices
		boolean V11side = dV11 < 0;
		if (dV11 < 0 == V11side) {
			// swap V12 and V11
			Coords temp = V11;
			V11 = V12;
			V12 = temp;
		} else if (dV12 < 0 == V11side) {
			// swap V10 and V11
			Coords temp = V11;
			V11 = V12;
			V12 = temp;
		}

		// make sure that V21 is on the opposite side of the intersection to the
		// other two vertices
		boolean V21side = dV11 < 0;
		if (dV21 < 0 == V21side) {
			// swap V22 and V21
			Coords temp = V21;
			V21 = V22;
			V22 = temp;
		} else if (dV22 < 0 == V21side) {
			// swap V20 and V21
			Coords temp = V21;
			V21 = V22;
			V22 = temp;
		}

		// find the equation of the intersection line
		Coords D = N1.crossProduct(N2);
		Coords l = V11.sub(V10);
		Coords l0 = V11;
		Coords p0 = V20;
		Coords n = N2;
		double d = (p0.sub(l0)).dotproduct(n) / (l.dotproduct(n));
		Coords O = l.mul(d).add(l0);

		// find scalar values representing the intersections of the triangle
		// edges and the line
		double pV10 = D.dotproduct(V10.sub(D));
		double pV11 = D.dotproduct(V11.sub(D));
		double pV12 = D.dotproduct(V12.sub(D));
		double pV20 = D.dotproduct(V20.sub(D));
		double pV21 = D.dotproduct(V21.sub(D));
		double pV22 = D.dotproduct(V22.sub(D));

		double t11 = pV10 + (pV10 - pV11) * dV10 / (dV10 - dV11);
		double t12 = pV12 + (pV12 - pV11) * dV12 / (dV12 - dV11);
		double t21 = pV20 + (pV20 - pV21) * dV20 / (dV20 - dV21);
		double t22 = pV22 + (pV22 - pV21) * dV22 / (dV22 - dV21);

		// check overlap

		// make sure ti1 < ti2
		if (t11 > t12) {
			double temp = t11;
			t11 = t12;
			t12 = temp;
		}
		if (t21 > t22) {
			double temp = t21;
			t21 = t22;
			t22 = temp;
		}

		// find the interval
		double tf1, tf2;
		tf1 = tf2 = 0;
		if (t11 < t21) {
			if (t12 < t21) // no overlap
				return null;
			tf1 = t21;
			tf2 = t12;
		} else if (t21 < t11) {
			if (t22 < t11) // no overlap
				return null;
			tf1 = t11;
			tf2 = t22;
		}

		return new Coords[] { O.add(D.mul(tf1)), O.add(D.mul(tf2)) };
	}

	// // Simple methods for vector operations. ////
	static private float[] cross(float[] v1, float[] v2) {
		return new float[] { v1[1] * v2[2] - v1[2] * v2[1],
				v1[2] * v2[0] - v1[0] * v2[2], v1[0] * v2[1] - v1[1] * v2[0] };
	}

	static private float dot(float[] v1, float[] v2) {
		return v1[0] * v2[0] + v1[1] * v2[1] + v1[2] * v2[2];
	}

	static private float[] sub(float[] v1, float[] v2) {
		return new float[] { v1[0] - v2[0], v1[1] - v2[1], v1[2] - v2[2] };
	}

	static private float[] add(float[] v1, float[] v2) {
		return new float[] { v1[0] + v2[0], v1[1] + v2[1], v1[2] + v2[2] };
	}

	static private void multInline(float[] v, float factor) {
		v[0] *= factor;
		v[1] *= factor;
		v[2] *= factor;
	}

	static private float[] mult(float[] v, float factor) {
		return new float[] { v[0] * factor, v[1] * factor, v[2] * factor };
	}

	/**
	 * Sorts two floats (first becomes smallest)
	 * 
	 * @param a
	 * @param b
	 * @return true if the floats were rearranged, otherwise false
	 */
	static private boolean sort(Float a, Float b) {
		if (a > b) {
			float c;
			c = a;
			a = b;
			b = c;
			return true;
		} else
			return false;
	}

	/**
	 * Finds the intersection of two triangles
	 * 
	 * @param t0
	 *            The first triangle as a set of nine floats
	 * @param t1
	 *            The second triangle as a set of nine floats
	 * @param coplanar
	 *            set to true if the two triangles are coplanar
	 * @param intersect
	 *            set to true if the two triangles intersect
	 * @return the intersection as a set of two points
	 */
	static public float[] triTriIntersect(float[] t0, float[] t1,
			Boolean coplanar, Boolean intersect) {

		// get arrays
		float[] v0 = new float[] { t0[0], t0[1], t0[2] };
		float[] v1 = new float[] { t0[3], t0[4], t0[5] };
		float[] v2 = new float[] { t0[6], t0[7], t0[8] };
		float[] u0 = new float[] { t1[0], t1[1], t1[2] };
		float[] u1 = new float[] { t1[3], t1[4], t1[5] };
		float[] u2 = new float[] { t1[6], t1[7], t1[8] };

		// compute plane equation of first triangle
		// plane equation 1: n1 . x + d1 = 0
		float[] n1 = cross(sub(v1, v0), sub(v2, v0));
		float d1 = -dot(n1, v0);

		// compute signed distances to the plane given by the first triangle
		float du0 = dot(n1, u0) + d1;
		float du1 = dot(n1, u1) + d1;
		float du2 = dot(n1, u2) + d1;

		// coplanarity robustness check
		if (USE_EPSILON_TEST) {
			if (Math.abs(du0) < EPSILON)
				du0 = 0.0f;
			if (Math.abs(du1) < EPSILON)
				du1 = 0.0f;
			if (Math.abs(du2) < EPSILON)
				du2 = 0.0f;
		}
		float du0du1 = du0 * du1;
		float du0du2 = du0 * du2;

		if (du0du1 > 0.0f && du0du2 > 0.0f) // if same sign and non-zero
			return null; // no intersection occurs

		// compute plane equation of second triangle
		float[] n2 = cross(sub(u1, u0), sub(u2, u0));
		float d2 = -dot(n2, u0);

		// compute signed distances for of vertices of first triangle
		float dv0 = dot(n2, v0) + d2;
		float dv1 = dot(n2, v1) + d2;
		float dv2 = dot(n2, v2) + d2;

		if (USE_EPSILON_TEST) {
			if (Math.abs(dv0) < EPSILON)
				dv0 = 0.0f;
			if (Math.abs(dv1) < EPSILON)
				dv1 = 0.0f;
			if (Math.abs(dv2) < EPSILON)
				dv2 = 0.0f;
		}

		float dv0dv1 = dv0 * dv1;
		float dv0dv2 = dv0 * dv2;

		if (dv0dv1 > 0.0f && dv0dv2 > 0.0f) // if same sign and non-zero
			return null; // no intersection occurs

		// compute direction of intersection line
		float[] D = cross(n1, n2);

		// compute and index to the largest component of D
		float max = Math.abs(D[0]);
		int index = 0;
		float b = Math.abs(D[1]);
		float c = Math.abs(D[2]);
		if (b > max) {
			max = b;
			index = 1;
		}
		if (c > max) {
			max = c;
			index = 2;
		}

		// simplified projection onto line
		float vp0 = v0[index];
		float vp1 = v1[index];
		float vp2 = v2[index];

		float up0 = u0[index];
		float up1 = u1[index];
		float up2 = u2[index];

		// compute interval for triangle 1
		float[] isectpointA1 = new float[3];
		float[] isectpointA2 = new float[3];
		float[] isect1 = computeIntervalsIntersectionLine(v0, v1, v2, vp0, vp1,
				vp2, dv0, dv1, dv2, dv0dv1, dv0dv2, isectpointA1, isectpointA2,
				coplanar);

		// handle planarity
		if (coplanar) {
			intersect = coplanarTriTri(n1, v0, v1, v2, u0, u1, u2);
			return null;
		}

		// compute interval for triangle 2
		float[] isectpointB1 = new float[3];
		float[] isectpointB2 = new float[3];
		float[] isect2 = computeIntervalsIntersectionLine(u0, u1, u2, up0, up1,
				up2, du0, du1, du2, du0du1, du0du2, isectpointB1, isectpointB2,
				coplanar);

		boolean rev1 = sort(isect1[0], isect1[1]);
		boolean rev2 = sort(isect2[0], isect2[1]);

		if (isect1[1] < isect2[0] || isect2[1] < isect1[0])
			return null;

		// triangles intersect - compute interval
		float[] isectpt1, isectpt2;

		if (isect2[0] < isect1[0]) {
			isectpt1 = rev1 ? isectpointA2 : isectpointA1;

			if (isect2[1] < isect1[1])
				isectpt2 = rev2 ? isectpointB1 : isectpointB2;
			else
				isectpt2 = rev1 ? isectpointA1 : isectpointA2;
		} else {
			isectpt1 = rev2 ? isectpointB2 : isectpointB1;

			if (isect2[1] > isect1[1])
				isectpt2 = rev1 ? isectpointA1 : isectpointA2;
			else
				isectpt2 = rev2 ? isectpointB1 : isectpointB2;
		}

		intersect = true;
		return new float[] { isectpt1[0], isectpt1[1], isectpt1[2],
				isectpt2[0], isectpt2[1], isectpt2[2] };
	}

	/**
	 * Tests if two edges intersect (assuming that the vertices have been
	 * projected onto the axes given by i0 and i1).
	 */
	static private boolean edgeEdgeIntersect(float[] v0, float[] u0,
			float[] u1, int i0, int i1, float ax, float ay) {
		float bx = u0[i0] - u1[i0];
		float by = u0[i1] - u1[i1];
		float cx = v0[i0] - u0[i0];
		float cy = v0[i1] - u0[i1];
		float f = ay * bx - ax * by;
		float d = by * cx - bx * cy;
		if ((f > 0 && d >= 0 && d <= f) || (f < 0 && d <= 0 && d >= f)) {
			float e = ax * cy - ay * cx;
			if (f > 0) {
				if (e >= 0 && e <= f)
					return true;
			} else {
				if (e <= 0 && e >= f)
					return true;
			}
		}
		return false;
	}

	/**
	 * Tests if a segment intersects a triangle (assuming that the vertices have
	 * been projected onto the axes given by i0 and i1).
	 * 
	 * @param p0
	 *            First point of segment.
	 * @param p1
	 *            Second point of segment.
	 * @param t0
	 *            First point of triangle.
	 * @param t1
	 *            Second point of triangle.
	 * @param t2
	 *            Third point of triangle.
	 * @param i0
	 *            First axis index
	 * @param i1
	 *            Second axis index
	 * @return true if there is an intersection, otherwise false
	 */
	static private boolean segmentAgainstTriEdges(float[] p0, float[] p1,
			float[] t0, float[] t1, float[] t2, int i0, int i1) {
		float ax = p1[i0] - p0[i0];
		float ay = p1[i1] - p0[i1];
		// test edge t0,t1
		if (edgeEdgeIntersect(p0, t0, t1, i0, i1, ax, ay))
			return true;
		// test edge t1,t2
		if (edgeEdgeIntersect(p0, t1, t2, i0, i1, ax, ay))
			return true;
		// test edge t2,t1
		if (edgeEdgeIntersect(p0, t2, t0, i0, i1, ax, ay))
			return true;
		return false;
	}

	/**
	 * Decide whether two triangles are coplanar or not
	 * 
	 * @param n
	 *            normal of first triangle
	 * @param t0
	 *            first corner of first triangle
	 * @param t1
	 *            second corner of first triangle
	 * @param t2
	 *            third corner of first triangle
	 * @param u0
	 *            first corner of second triangle
	 * @param u1
	 *            second corner of second triangle
	 * @param u2
	 *            third corner of second triangle
	 * @return true if there is an intersection - otherwise false
	 */
	static private boolean coplanarTriTri(float[] n, float[] t0, float[] t1,
			float[] t2, float[] u0, float[] u1, float[] u2) {
		float[] a = new float[3];
		short i0, i1;
		// project onto the axis-aligned plane that maximizes the area
		// of the triangles, and compute axis indices
		a[0] = Math.abs(n[0]);
		a[1] = Math.abs(n[1]);
		a[2] = Math.abs(n[2]);
		if (a[0] > a[1]) {
			if (a[0] > a[2]) {
				i0 = 1; // a[0] is greatest
				i1 = 2;
			} else {
				i0 = 0; // a[2] is greatest
				i1 = 1;
			}
		} else { // a[0]<=a[1]
			if (a[2] > a[1]) {
				i0 = 0; // a[2] is greatest
				i1 = 1;
			} else {
				i0 = 0; // a[1] is greatest
				i1 = 2;
			}
		}

		// test all edges of triangle 1 against the edges of triangle 2
		if (segmentAgainstTriEdges(t0, t1, u0, u1, u2, i0, i1))
			return true;
		if (segmentAgainstTriEdges(t1, t2, u0, u1, u2, i0, i1))
			return true;
		if (segmentAgainstTriEdges(t2, t0, u0, u1, u2, i0, i1))
			return true;

		// finally, test if tri1 is totally contained in tri2 or vice versa
		if (pointInTri(t0, u0, u1, u2, i0, i1))
			return true;
		if (pointInTri(u0, t0, t1, t2, i0, i1))
			return true;

		return false;
	}

	/**
	 * Check if a point is inside a triangle (when projected onto an
	 * axis-aligned plane)
	 * 
	 * @param p
	 *            the point
	 * @param t0
	 *            first triangle corner
	 * @param t1
	 *            second triangle corner
	 * @param t2
	 *            third triangle corner
	 * @param i0
	 *            first index of plane
	 * @param i1
	 *            second index of plane
	 * @return true if the point in inside the triangle
	 */
	static private boolean pointInTri(float[] p, float[] t0, float[] t1,
			float[] t2, int i0, int i1) {
		float a = t1[i1] - t0[i1];
		float b = -(t1[i0] - t0[i0]);
		float c = -a * t0[i0] - b * t0[i1];
		float d0 = a * p[i0] + b * p[i1] + c;

		a = t2[i1] - t1[i1];
		b = -(t2[i0] - t1[i0]);
		c = -a * t1[i0] - b * t1[i1];
		float d1 = a * p[i0] + b * p[i1] + c;

		a = t0[i1] - t2[i1];
		b = -(t0[i0] - t2[i0]);
		c = -a * t2[i0] - b * t2[i1];
		float d2 = a * p[i0] + b * p[i1] + c;
		if (d0 * d1 > 0.0 && d0 * d2 > 0.0)
			return true;
		return false;
	}

	static private float[] isect2(float[] vt0, float[] vt1, float[] vt2,
			float vv0, float vv1, float vv2, float d0, float d1, float d2,
			float[] isectpoint0, float[] isectpoint1) {

		float tmp = d0 / (d0 - d1);
		float isect0 = vv0 + (vv1 - vv0) * tmp;
		float[] diff = sub(vt1, vt0);
		multInline(diff, tmp);
		isectpoint0 = add(diff, vt0);

		tmp = d0 / (d0 - d2);
		float isect1 = vv0 + (vv2 - vv0) * tmp;
		diff = sub(vt2, vt0);
		multInline(diff, tmp);
		isectpoint1 = add(vt0, diff);

		return new float[] { isect0, isect1 };
	}

	/**
	 * Compute the intersection interval if the triangle with a plane
	 * 
	 * @param t0
	 *            first triangle corner
	 * @param t1
	 *            second triangle corner
	 * @param t2
	 *            third triangle corner
	 * @param vv0
	 *            parameter value of projection onto intersection line of first
	 *            corner
	 * @param vv1
	 *            parameter value of projection onto intersection line of second
	 *            corner
	 * @param vv2
	 *            parameter value of projection onto intersection line of third
	 *            corner
	 * @param d0
	 *            distance of first corner to plane
	 * @param d1
	 *            distance of second corner to plane
	 * @param d2
	 *            distance of third corner to plane
	 * @param d0d1
	 *            product of two distances
	 * @param d0d2
	 *            product of two distances
	 * @param isectpoint0
	 *            first point of intersection
	 * @param isectpoint1
	 *            second point of intersection
	 * @param coplanar
	 *            set to true if the triangles are coplanar
	 * @return
	 */
	static private float[] computeIntervalsIntersectionLine(float[] t0,
			float[] t1, float[] t2, float vv0, float vv1, float vv2, float d0,
			float d1, float d2, float d0d1, float d0d2, float[] isectpoint0,
			float[] isectpoint1, Boolean coplanar) {
		float[] isect = null;
		if (d0d1 > 0.0f)
			// we know that d0d2<=0.0
			// that is d0, d1 are on the same side, d2 on the other or on the
			// plane
			isect = isect2(t2, t0, t1, vv2, vv0, vv1, d2, d0, d1, isectpoint0,
					isectpoint1);
		else if (d0d2 > 0.0f)
			// we know that d0d1<=0.0
			isect = isect2(t1, t0, t2, vv1, vv0, vv2, d1, d0, d2, isectpoint0,
					isectpoint1);
		else if (d1 * d2 > 0.0f || d0 != 0.0f)
			// we know that d0d1<=0.0 or that d0!=0.0
			isect = isect2(t0, t1, t2, vv0, vv1, vv2, d0, d1, d2, isectpoint0,
					isectpoint1);
		else if (d1 != 0.0f)
			isect = isect2(t1, t0, t2, vv1, vv0, vv2, d1, d0, d2, isectpoint0,
					isectpoint1);
		else if (d2 != 0.0f)
			isect = isect2(t2, t0, t1, vv2, vv0, vv1, d2, d0, d1, isectpoint0,
					isectpoint1);
		else
			// triangles are coplanar
			coplanar = true;
		coplanar = false;

		return isect;
	}

	/**
	 * Finds the intersection of a ray with a triangle
	 * 
	 * @param p0
	 *            first point on ray
	 * @param p1
	 *            second point on ray
	 * @param t
	 *            the triangle
	 * @param param
	 * @param intersection
	 *            the intersection point
	 * @return -1: degenerate triangle 0: no intersection 1: intersection in I
	 *         2: ray and triangle coplanar
	 */
	static int rayTriIntersect(float[] p0, float[] p1, float[] t, Float param,
			float[] intersection) {
		float[] v0 = new float[] { t[0], t[1], t[2] };
		float[] v1 = new float[] { t[3], t[4], t[5] };
		float[] v2 = new float[] { t[6], t[7], t[8] };

		// get triangle edge vectors and plane normal
		float[] u = sub(v1, v0);
		float[] v = sub(v2, v0);
		float[] n = cross(u, v); // cross product
		if (n[0] == 0 && n[1] == 0 && n[2] == 0) // triangle is degenerate
			return -1; // do not deal with this case

		float[] dir = sub(p1, p0); // ray direction vector
		float[] w0 = sub(p0, v0);
		float a = -dot(n, w0);
		float b = dot(n, dir);
		if (USE_EPSILON_TEST && Math.abs(b) < EPSILON) { // ray is parallel to
															// triangle plane
			if (a == 0) // ray lies in triangle plane
				return 2;
			else
				return 0; // ray disjoint from plane
		}

		// get intersect point of ray with triangle plane
		float r = a / b;
		if (r < 0.0) // ray goes away from triangle
			return 0; // => no intersect
		// for a segment, also test if (r > 1.0) => no intersect

		intersection[0] = p0[0] + r * dir[0]; // intersect point of ray and
												// plane
		intersection[1] = p0[1] + r * dir[1];
		intersection[2] = p0[2] + r * dir[2];

		param = r;

		// is I inside T?
		float uu = dot(u, u);
		float uv = dot(u, v);
		float vv = dot(v, v);
		float[] w = sub(intersection, v0);
		float wu = dot(w, u);
		float wv = dot(w, v);
		float D = uv * uv - uu * vv;

		// get and test parametric coords
		float s, d;
		s = (uv * wv - vv * wu) / D;
		if (s < 0.0 || s > 1.0) // intersection outside
			return 0;
		d = (uv * wu - uu * wv) / D;
		if (d < 0.0 || (s + d) > 1.0) // intersection outside
			return 0;

		return 1; // I is in T
	}

	static float[][] AAB_NORMALS = { { -1, 0, 0 }, { 1, 0, 0 }, { 0, -1, 0 },
			{ 0, 1, 0 }, { 0, 0, -1 }, { 0, 0, 1 } };

	/**
	 * Naive ray-box intersection test - speed not necessary at this point
	 * 
	 * @param box
	 *            an axis-aligned bounding box to test against
	 * @param origin
	 *            a point on the ray
	 * @param t
	 *            tangent
	 * @param entry
	 *            the entry point
	 * @param exit
	 *            the exit point
	 * @return true if intersection has occurred, otherwise false
	 */
	public static boolean rayBoxIntersect(float[] box, float[] origin,
			float[] t, float[] entry, float[] exit) {
		float[] params = new float[6];
		boolean[] intersect = new boolean[6];
		float[][] pts = new float[2][];
		float[] p = new float[2];

		// points on sides
		float[][] pp = new float[][] { { box[0], box[2], box[4] },
				{ box[1], box[2], box[4] }, { box[0], box[2], box[4] },
				{ box[0], box[3], box[4] }, { box[0], box[2], box[4] },
				{ box[0], box[2], box[5] } };

		// find parameter points for intersection of all side planes
		for (int i = 0; i < 6; i++) {
			float[] n = AAB_NORMALS[i];

			float dot = dot(n, t);

			// ignore if parallel
			intersect[i] = dot != 0;
			if (intersect[i] == false)
				continue;

			float[] diff = sub(pp[i], origin);
			// otherwise find intersection parameter
			params[i] = dot(n, diff) / dot;
		}

		// find intersection points
		int j = 0;
		for (int i = 0; i < 6; i++) {
			if (intersect[i]) {
				float[] in = add(origin, mult(t, params[i]));
				if (pointBoxIntersect(pts[i], box)) {
					pts[j++] = in;
					p[j] = params[i];
				}
			}
		}

		if (j == 0)
			return false;
		else if (j != 2)
			System.err.println("error");
		else {
			boolean rev = p[0] > p[1];
			entry = rev ? pts[1] : pts[0];
			exit = rev ? pts[0] : pts[1];
		}

		return true;
	}

	private static boolean pointBoxIntersect(float[] bb, float[] pt) {
		return pt[0] > bb[0] && pt[0] < bb[1] && pt[1] > bb[2] && pt[1] < bb[3]
				&& pt[2] > bb[4] && pt[3] < bb[5];
	}

	/**
	 * Finds the intersection of a segment and a triangle, if there is one
	 * 
	 * @param p0
	 *            the first point in the segment
	 * @param p1
	 *            the second point of the segment
	 * @param t
	 *            the triangle
	 * @param intersection
	 *            the intersection point
	 * @return true if there was an intersection, otherwise false
	 */
	public static boolean segmentTriIntersect(float[] p0, float[] p1,
			float[] t, float[] intersection) {
		Float param = new Float(-1);
		if (rayTriIntersect(p0, p1, t, param, intersection) == 1)
			return param > 0.0f && param < 1.0f;
		return false;
	}
}
