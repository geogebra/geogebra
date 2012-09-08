package geogebra.common.kernel.discrete.delaunay;

import geogebra.common.main.App;

import java.util.Comparator;

/**
 * This class represents a 3D point, with some simple geometric methods
 * (pointLineTest).
 */
public class Point_dt {
	double x, y, z;

	/**
	 * Default Constructor. <br />
	 * constructs a 3D point at (0,0,0).
	 */
	public Point_dt() {
		this (0,0);
	}

	/** 
	 * constructs a 3D point 
	 */
	public Point_dt(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/** constructs a 3D point with a z value of 0. */
	public Point_dt(double x, double y) {
		this(x, y, 0);
	}

	/** simple copy constructor */
	public Point_dt(Point_dt p) {
		x = p.x;
		y = p.y;
		z = p.z;
	}

	/** returns the x-coordinate of this point. */
	public double x() {
		return x;
	};
	
	/**
	 * Sets the x coordinate.
	 * @param x The new x coordinate.
	 */
	public void setX(double x) {
		this.x = x;
	}

	/** returns the y-coordinate of this point. */
	public double y() {
		return y;
	};
	
	/**
	 * Sets the y coordinate.
	 * @param y The new y coordinate.
	 */
	public void setY(double y) {
		this.y = y;
	}

	/** returns the z-coordinate of this point. */
	public double z() {
		return z;
	};
	
	/**
	 * Sets the z coordinate.
	 * @param z The new z coordinate.
	 */
	public void setZ(double Z) {
		this.z = z;
	}

	double distance2(Point_dt p) {
		return (p.x - x) * (p.x - x) + (p.y - y) * (p.y - y);
	}

	double distance2(double px, double py) {
		return (px - x) * (px - x) + (py - y) * (py - y);
	}

	boolean isLess(Point_dt p) {
		return (x < p.x) || ((x == p.x) && (y < p.y));
	}

	boolean isGreater(Point_dt p) {
		return (x > p.x) || ((x == p.x) && (y > p.y));
	}

	/**
	 * return true iff this point [x,y] coordinates are the same as p [x,y]
	 * coordinates. (the z value is ignored).
	 */
	public boolean equals(Point_dt p) {
		return (x == p.x) && (y == p.y);
	}

	/** return a String in the [x,y,z] format */
	public String toString() {
		return (new String(" Pt[" + x + "," + y + "," + z + "]"));
	}

	/** @return the L2 distanse NOTE: 2D only!!! */
	public double distance(Point_dt p) {
		double temp = Math.pow(p.x() - x, 2) + Math.pow(p.y() - y, 2);
		return Math.sqrt(temp);
	}

	/** @return the L2 distanse NOTE: 2D only!!! */
	public double distance3D(Point_dt p) {
		double temp = Math.pow(p.x() - x, 2) + Math.pow(p.y() - y, 2)
				+ Math.pow(p.z() - z, 2);
		return Math.sqrt(temp);
	}

	/** return a String: x y z (used by the save to file - write_tsin method). */
	public String toFile() {
		return ("" + x + " " + y + " " + z);
	}

	String toFileXY() {
		return ("" + x + " " + y);
	}

	// pointLineTest
	// ===============
	// simple geometry to make things easy!
	/** �����a----+----b������ */
	public final static int ONSEGMENT = 0;

	/**
	 * + <br>
	 * �����a---------b������
	 * */
	public final static int LEFT = 1;

	/**
	 * �����a---------b������ <br>
	 * +
	 * */
	public final static int RIGHT = 2;
	/** ��+��a---------b������ */
	public final static int INFRONTOFA = 3;
	/** ������a---------b���+��� */
	public final static int BEHINDB = 4;
	public final static int ERROR = 5;

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
	public int pointLineTest(Point_dt a, Point_dt b) {

		double dx = b.x - a.x;
		double dy = b.y - a.y;
		double res = dy * (x - a.x) - dx * (y - a.y);

		if (res < 0)
			return LEFT;
		if (res > 0)
			return RIGHT;

		if (dx > 0) {
			if (x < a.x)
				return INFRONTOFA;
			if (b.x < x)
				return BEHINDB;
			return ONSEGMENT;
		}
		if (dx < 0) {
			if (x > a.x)
				return INFRONTOFA;
			if (b.x > x)
				return BEHINDB;
			return ONSEGMENT;
		}
		if (dy > 0) {
			if (y < a.y)
				return INFRONTOFA;
			if (b.y < y)
				return BEHINDB;
			return ONSEGMENT;
		}
		if (dy < 0) {
			if (y > a.y)
				return INFRONTOFA;
			if (b.y > y)
				return BEHINDB;
			return ONSEGMENT;
		}
		App.printStacktrace("Error, pointLineTest with a=b");
		return ERROR;
	}

	boolean areCollinear(Point_dt a, Point_dt b) {
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

	Point_dt circumcenter(Point_dt a, Point_dt b) {

		double u = ((a.x - b.x) * (a.x + b.x) + (a.y - b.y) * (a.y + b.y)) / 2.0f;
		double v = ((b.x - x) * (b.x + x) + (b.y - y) * (b.y + y)) / 2.0f;
		double den = (a.x - b.x) * (b.y - y) - (b.x - x) * (a.y - b.y);
		if (den == 0) // oops
			System.out.println("circumcenter, degenerate case");
		return new Point_dt((u * (b.y - y) - v * (a.y - b.y)) / den, (v
				* (a.x - b.x) - u * (b.x - x))
				/ den);
	}

	public static Comparator<Point_dt> getComparator(int flag) {
		return new Compare(flag);
	}

	public static Comparator<Point_dt> getComparator() {
		return new Compare(0);
	}
}

class Compare implements Comparator{
	private int _flag;

	public Compare(int i) {
		_flag = i;
	}

	/** compare between two points. */
	public int compare(Object o1, Object o2) {
		int ans = 0;
		if (o1 != null && o2 != null && o1 instanceof Point_dt
				&& o2 instanceof Point_dt) {
			Point_dt d1 = (Point_dt) o1;
			Point_dt d2 = (Point_dt) o2;
			if (_flag == 0) {
				if (d1.x > d2.x)
					return 1;
				if (d1.x < d2.x)
					return -1;
				// x1 == x2
				if (d1.y > d2.y)
					return 1;
				if (d1.y < d2.y)
					return -1;
			} else if (_flag == 1) {
				if (d1.x > d2.x)
					return -1;
				if (d1.x < d2.x)
					return 1;
				// x1 == x2
				if (d1.y > d2.y)
					return -1;
				if (d1.y < d2.y)
					return 1;
			} else if (_flag == 2) {
				if (d1.y > d2.y)
					return 1;
				if (d1.y < d2.y)
					return -1;
				// y1 == y2
				if (d1.x > d2.x)
					return 1;
				if (d1.x < d2.x)
					return -1;

			} else if (_flag == 3) {
				if (d1.y > d2.y)
					return -1;
				if (d1.y < d2.y)
					return 1;
				// y1 == y2
				if (d1.x > d2.x)
					return -1;
				if (d1.x < d2.x)
					return 1;
			}
		} else {
			if (o1 == null && o2 == null)
				return 0;
			if (o1 == null && o2 != null)
				return 1;
			if (o1 != null && o2 == null)
				return -1;
		}
		return ans;
	}

	public boolean equals(Object ob) {
		return false;
	}
}