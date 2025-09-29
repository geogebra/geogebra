package org.geogebra.common.kernel.discrete.delaunay;

import org.geogebra.common.util.debug.Log;

/**
 * This class represents a 3D triangle in a Triangulation!
 *
 */

public class TriangleDt {
	PointDt a;
	PointDt b;
	PointDt c;
	TriangleDt abnext;
	TriangleDt bcnext;
	TriangleDt canext;
	CircleDt circum;
	int _mc = 0; // modcounter for triangulation fast update.

	boolean halfplane = false; // true iff it is an infinite face.
	// public boolean visitflag;
	boolean _mark = false; // tag - for bfs algorithms
	// private static boolean visitValue=false;
	// public static int _counter = 0;
	// public static int _c2 = 0;

	// public int _id;
	/**
	 * constructs a triangle form 3 point - store it in counterclockwised order.
	 * 
	 * @param A
	 *            first point
	 * @param B
	 *            second point
	 * @param C
	 *            third point
	 */
	public TriangleDt(PointDt A, PointDt B, PointDt C) {
		// visitflag=visitValue;
		a = A;
		int res = C.pointLineTest(A, B);
		if ((res <= PointDt.LEFT) || (res == PointDt.INFRONTOFA)
				|| (res == PointDt.BEHINDB)) {
			b = B;
			c = C;
		} else { // RIGHT
			Log.warn("Warning, ajTriangle(A,B,C) "
					+ "expects points in counterclockwise order." + A + B + C);
			b = C;
			c = B;
		}
		circumcircle();
	}

	/**
	 * creates a half plane using the segment (A,B).
	 * 
	 * @param A
	 *            point A
	 * @param B
	 *            point B
	 */
	public TriangleDt(PointDt A, PointDt B) {
		// visitflag=visitValue;
		a = A;
		b = B;
		halfplane = true;
		// _id = _counter++;
	}

	/**
	 * @return true iff this triangle is actually a half plane.
	 */
	public boolean isHalfplane() {
		return this.halfplane;
	}

	/**
	 * @return the first vertex of this triangle.
	 */
	public PointDt p1() {
		return a;
	}

	/**
	 * @return the second vertex of this triangle.
	 */
	public PointDt p2() {
		return b;
	}

	/**
	 * @return the 3th vertex of this triangle.
	 */
	public PointDt p3() {
		return c;
	}

	/**
	 * @return the consecutive triangle which shares this triangle p1,p2 edge.
	 */
	public TriangleDt next_12() {
		return this.abnext;
	}

	/**
	 * @return the consecutive triangle which shares this triangle p2,p3 edge.
	 */
	public TriangleDt next_23() {
		return this.bcnext;
	}

	/**
	 * @return the consecutive triangle which shares this triangle p3,p1 edge.
	 */
	public TriangleDt next_31() {
		return this.canext;
	}

	/**
	 * @return The bounding rectangle between the minimum and maximum coordinates
	 *         of the triangle
	 */
	public BoundingBox getBoundingBox() {
		PointDt lowerLeft, upperRight;
		lowerLeft = new PointDt(Math.min(a.x(), Math.min(b.x(), c.x())),
				Math.min(a.y(), Math.min(b.y(), c.y())));
		upperRight = new PointDt(Math.max(a.x(), Math.max(b.x(), c.x())),
				Math.max(a.y(), Math.max(b.y(), c.y())));
		return new BoundingBox(lowerLeft, upperRight);
	}

	void switchneighbors(TriangleDt Old, TriangleDt New) {
		if (abnext == Old) {
			abnext = New;
		} else if (bcnext == Old) {
			bcnext = New;
		} else if (canext == Old) {
			canext = New;
		} else {
			Log.debug("Error, switchneighbors can't find Old.");
		}
	}

	TriangleDt neighbor(PointDt p) {
		if (a == p) {
			return canext;
		}
		if (b == p) {
			return abnext;
		}
		if (c == p) {
			return bcnext;
		}
		Log.debug("Error, neighbors can't find p: " + p);
		return null;
	}

	/**
	 * Returns the neighbors that shares the given corner and is not the
	 * previous triangle.
	 * 
	 * @param p
	 *            The given corner
	 * @param prevTriangle
	 *            The previous triangle.
	 * @return The neighbors that shares the given corner and is not the
	 *         previous triangle.
	 * 
	 *         By: Eyal Roth & Doron Ganel.
	 */
	TriangleDt nextNeighbor(PointDt p, TriangleDt prevTriangle) {
		TriangleDt neighbor = null;

		if (a.equals(p)) {
			neighbor = canext;
		}
		if (b.equals(p)) {
			neighbor = abnext;
		}
		if (c.equals(p)) {
			neighbor = bcnext;
		}

		// Udi Schneider: Added a condition check for isHalfPlane. If the
		// current
		// neighbor is a half plane, we also want to move to the next neighbor
		if (neighbor.equals(prevTriangle) || neighbor.isHalfplane()) {
			if (a.equals(p)) {
				neighbor = abnext;
			}
			if (b.equals(p)) {
				neighbor = bcnext;
			}
			if (c.equals(p)) {
				neighbor = canext;
			}
		}

		return neighbor;
	}

	CircleDt circumcircle() {

		double u = ((a.x - b.x) * (a.x + b.x) + (a.y - b.y) * (a.y + b.y))
				/ 2.0f;
		double v = ((b.x - c.x) * (b.x + c.x) + (b.y - c.y) * (b.y + c.y))
				/ 2.0f;
		double den = (a.x - b.x) * (b.y - c.y) - (b.x - c.x) * (a.y - b.y);
		if (den == 0) {
			circum = new CircleDt(a, Double.POSITIVE_INFINITY);
		} else {
			PointDt cen = new PointDt(
					(u * (b.y - c.y) - v * (a.y - b.y)) / den,
					(v * (a.x - b.x) - u * (b.x - c.x)) / den);
			circum = new CircleDt(cen, cen.distance2(a));
		}
		return circum;
	}

	boolean circumcircleContains(PointDt p) {

		return circum.radius() > circum.center().distance2(p);
	}

	@Override
	public String toString() {
		String res = ""; // +_id+") ";
		res += a.toString() + b.toString();
		if (!halfplane) {
			res += c.toString();
		}
		// res +=c.toString() +" | "+abnext._id+" "+bcnext._id+" "+canext._id;
		return res;
	}

	/**
	 * determinates if this triangle contains the point p.
	 * 
	 * @param p
	 *            the query point
	 * @return true iff p is not null and is inside this triangle (Note: on
	 *         boundary is considered inside!!).
	 */
	public boolean contains(PointDt p) {
		boolean ans = false;
		if (this.halfplane | p == null) {
			return false;
		}

		if (isCorner(p)) {
			return true;
		}

		int a12 = p.pointLineTest(a, b);
		int a23 = p.pointLineTest(b, c);
		int a31 = p.pointLineTest(c, a);

		if ((a12 == PointDt.LEFT && a23 == PointDt.LEFT
				&& a31 == PointDt.LEFT)
				|| (a12 == PointDt.RIGHT && a23 == PointDt.RIGHT
						&& a31 == PointDt.RIGHT)
				|| a12 == PointDt.ONSEGMENT || a23 == PointDt.ONSEGMENT
						|| a31 == PointDt.ONSEGMENT) {
			ans = true;
		}

		return ans;
	}

	/**
	 * determinates if this triangle contains the point p.
	 * 
	 * @param p
	 *            the query point
	 * @return true iff p is not null and is inside this triangle (Note: on
	 *         boundary is considered outside!!).
	 */
	public boolean contains_BoundaryIsOutside(PointDt p) {
		boolean ans = false;
		if (this.halfplane | p == null) {
			return false;
		}

		if (isCorner(p)) {
			return true;
		}

		int a12 = p.pointLineTest(a, b);
		int a23 = p.pointLineTest(b, c);
		int a31 = p.pointLineTest(c, a);

		if ((a12 == PointDt.LEFT && a23 == PointDt.LEFT
				&& a31 == PointDt.LEFT)
				|| (a12 == PointDt.RIGHT && a23 == PointDt.RIGHT
						&& a31 == PointDt.RIGHT)) {
			ans = true;
		}

		return ans;
	}

	/**
	 * Checks if the given point is a corner of this triangle.
	 * 
	 * @param p
	 *            The given point.
	 * @return True iff the given point is a corner of this triangle.
	 * 
	 *         By Eyal Roth &amp; Doron Ganel.
	 */
	public boolean isCorner(PointDt p) {
		return (p.x == a.x & p.y == a.y) | (p.x == b.x & p.y == b.y)
				| (p.x == c.x & p.y == c.y);
	}

	// Doron
	/**
	 * @param arrayPoints
	 *            array of points
	 * @return whether all points are inside circumcircle around the first three
	 */
	public boolean fallInsideCircumcircle(PointDt[] arrayPoints) {
		boolean isInside = false;
		PointDt p1 = this.p1();
		PointDt p2 = this.p2();
		PointDt p3 = this.p3();
		int i = 0;
		while (!isInside && i < arrayPoints.length) {
			PointDt p = arrayPoints[i];
			if (!p.equals(p1) && !p.equals(p2) && !p.equals(p3)) {
				isInside = this.circumcircleContains(p);
			}
			i++;
		}

		return isInside;
	}

	/**
	 * @param index
	 *            index 0 to 2
	 * @return triangle vertex
	 */
	public PointDt getCorner(int index) {
		switch (index) {
		case 0:
			return p1();
		case 1:
			return p2();
		case 2:
			return p3();
		}

		return null;
	}
}
