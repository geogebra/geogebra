package org.geogebra.common.kernel.discrete.delaunay;

import java.util.Comparator;

import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.debug.Log;

/**
 * This class represents a 3D point, with some simple geometric methods
 * (pointLineTest).
 */
public class PointDt {
	public final static int ONSEGMENT = 0;

	/**
	 * + <br>
	 */
	public final static int LEFT = 1;

	/**
	 * +
	 */
	public final static int RIGHT = 2;
	public final static int INFRONTOFA = 3;
	public final static int BEHINDB = 4;
	public final static int ERROR = 5;

	double x;
	double y;

	@Override
	public int hashCode() {

		double[] tempArray = { x, y };

		return java.util.Arrays.hashCode(tempArray);
	}

	/**
	 * Default Constructor. <br>
	 * constructs a 3D point at (0,0,0).
	 */
	public PointDt() {
		this(0, 0);
	}

	/**
	 * constructs a 3D point with a z value of 0.
	 * 
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 */
	public PointDt(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * simple copy constructor
	 * 
	 * @param p
	 *            point
	 */
	public PointDt(PointDt p) {
		x = p.x;
		y = p.y;
	}

	/** @return the x-coordinate of this point. */
	public double x() {
		return x;
	}

	/**
	 * Sets the x coordinate.
	 * 
	 * @param x
	 *            The new x coordinate.
	 */
	public void setX(double x) {
		this.x = x;
	}

	/** @return the y-coordinate of this point. */
	public double y() {
		return y;
	}

	/**
	 * Sets the y coordinate.
	 * 
	 * @param y
	 *            The new y coordinate.
	 */
	public void setY(double y) {
		this.y = y;
	}

	/** @return the z-coordinate of this point. */
	public double z() {
		return 0;
	}

	/**
	 * @param p
	 *            point
	 * @return distance
	 */
	double distance2(PointDt p) {
		return (p.x - x) * (p.x - x) + (p.y - y) * (p.y - y);
	}

	/**
	 * @param px
	 *            x-coord
	 * @param py
	 *            y-coord
	 * @return distance
	 */
	double distance2(double px, double py) {
		return (px - x) * (px - x) + (py - y) * (py - y);
	}

	/**
	 * @param p
	 *            point
	 * @return true if less than this
	 */
	boolean isLess(PointDt p) {
		return ComparePoint.lessThan(x, p.x)
				|| (ComparePoint.equals(x, p.x) && ComparePoint.lessThan(y, p.y));
	}

	/**
	 * @param p
	 *            point
	 * @return true if greater than this
	 */
	boolean isGreater(PointDt p) {
		return ComparePoint.greaterThan(x, p.x)
				|| (ComparePoint.equals(x, p.x) && ComparePoint.greaterThan(y, p.y));
	}

	/**
	 * @return true iff this point [x,y] coordinates are the same as p [x,y]
	 *         coordinates. (the z value is ignored).
	 */
	@Override
	public boolean equals(Object p) {

		if (!(p instanceof PointDt)) {
			return false;
		}

		return ComparePoint.equals(x, ((PointDt) p).x)
				&& ComparePoint.equals(y, ((PointDt) p).y);
	}

	/** @return a String in the [x,y,z] format */
	@Override
	public String toString() {
		return " Pt[" + x + "," + y + ",0]";
	}

	/**
	 * @param p
	 *            point
	 * @return the L2 distance NOTE: 2D only!!!
	 */
	public double distance(PointDt p) {
		return MyMath.length(p.x() - x, p.y() - y);
		//double temp = Math.pow(p.x() - x, 2) + Math.pow(p.y() - y, 2);
		//return Math.sqrt(temp);
	}

	/**
	 * @param p
	 *            point
	 * @return the L2 distance NOTE: 3D only!!!
	 */
	public double distance3D(PointDt p) {
		return MyMath.length(p.x() - x, p.y() - y, p.z());
		// double temp = Math.pow(p.x() - x, 2) + Math.pow(p.y() - y, 2)
		// + Math.pow(p.z() - z, 2);
		// return Math.sqrt(temp);
	}

	String toFileXY() {
		return x + " " + y;
	}

	// pointLineTest
	// ===============
	// simple geometry to make things easy!

	/**
	 * tests the relation between this point (as a 2D [x,y] point) and a 2D
	 * segment a,b (the Z values are ignored), returns one of the following:
	 * LEFT, RIGHT, INFRONTOFA, BEHINDB, ONSEGMENT
	 * 
	 * @param a
	 *            the first point of the segment.
	 * @param b
	 *            the second point of the segment.
	 * @return the value (flag) of the relation between this point and the a,b
	 *         line-segment.
	 */
	public int pointLineTest(PointDt a, PointDt b) {

		double dx = b.x - a.x;
		double dy = b.y - a.y;
		double res = dy * (x - a.x) - dx * (y - a.y);

		if (res < 0) {
			return LEFT;
		}
		if (res > 0) {
			return RIGHT;
		}

		if (dx > 0) {
			if (x < a.x) {
				return INFRONTOFA;
			}
			if (b.x < x) {
				return BEHINDB;
			}
			return ONSEGMENT;
		}
		if (dx < 0) {
			if (x > a.x) {
				return INFRONTOFA;
			}
			if (b.x > x) {
				return BEHINDB;
			}
			return ONSEGMENT;
		}
		if (dy > 0) {
			if (y < a.y) {
				return INFRONTOFA;
			}
			if (b.y < y) {
				return BEHINDB;
			}
			return ONSEGMENT;
		}
		if (dy < 0) {
			if (y > a.y) {
				return INFRONTOFA;
			}
			if (b.y > y) {
				return BEHINDB;
			}
			return ONSEGMENT;
		}
		Log.error("Error, pointLineTest with a=b");
		return ERROR;
	}

	boolean areCollinear(PointDt a, PointDt b) {
		double dx = b.x - a.x;
		double dy = b.y - a.y;
		double res = dy * (x - a.x) - dx * (y - a.y);
		return res == 0;
	}

	/*
	 * public ajSegment Bisector( ajPoint b) { double sx = (x+b.x)/2; double sy
	 * = (y+b.y)/2; double dx = b.x-x; double dy = b.y-y; ajPoint p1 = new
	 * ajPoint(sx-dy,sy+dx); ajPoint p2 = new ajPoint(sx+dy,sy-dx); return new
	 * ajSegment( p1,p2 ); }
	 */

	PointDt circumcenter(PointDt a, PointDt b) {

		double u = ((a.x - b.x) * (a.x + b.x) + (a.y - b.y) * (a.y + b.y))
				/ 2.0f;
		double v = ((b.x - x) * (b.x + x) + (b.y - y) * (b.y + y)) / 2.0f;
		double den = (a.x - b.x) * (b.y - y) - (b.x - x) * (a.y - b.y);
		if (den == 0) {
			Log.debug("circumcenter, degenerate case");
		}
		return new PointDt((u * (b.y - y) - v * (a.y - b.y)) / den,
				(v * (a.x - b.x) - u * (b.x - x)) / den);
	}

	public static Comparator<PointDt> getComparator(int flag) {
		return new ComparePoint(flag);
	}

	public static Comparator<PointDt> getComparator() {
		return new ComparePoint(0);
	}
}