/*
 * @(#)Line2D.java	1.28 03/12/19
 *
 * Copyright (c) 1997, 2006, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package geogebra.html5.openjdk.awt.geom;



/**
 * This <code>Line2D</code> represents a line segment in (x,&nbsp;y) coordinate
 * space. This class, like all of the Java 2D API, uses a default coordinate
 * system called <i>user space</i> in which the y-axis values increase downward
 * and x-axis values increase to the right. For more information on the user
 * space coordinate system, see the <a href=
 * "http://java.sun.com/j2se/1.3/docs/guide/2d/spec/j2d-intro.fm2.html#61857">
 * Coordinate Systems</a> section of the Java 2D Programmer's Guide.
 * <p>
 * This class is only the abstract superclass for all objects that store a 2D
 * line segment. The actual storage representation of the coordinates is left to
 * the subclass.
 *
 * @version 1.28, 12/19/03
 * @author Jim Graham
 */
public abstract class Line2D implements Shape, Cloneable {
	/**
	 * A line segment specified with double coordinates.
	 */
	public static class Double extends Line2D {
		/**
		 * The X coordinate of the start point of the line segment.
		 */
		public double x1;

		/**
		 * The Y coordinate of the start point of the line segment.
		 */
		public double y1;

		/**
		 * The X coordinate of the end point of the line segment.
		 */
		public double x2;

		/**
		 * The Y coordinate of the end point of the line segment.
		 */
		public double y2;

		/**
		 * Constructs and initializes a Line with coordinates (0, 0) -> (0, 0).
		 */
		public Double() {
		}

		/**
		 * Constructs and initializes a <code>Line2D</code> from the specified
		 * coordinates.
		 *
		 * @param X1
		 *            ,&nbsp;Y1 the first specified coordinate
		 * @param X2
		 *            ,&nbsp;Y2 the second specified coordinate
		 */
		public Double(double X1, double Y1, double X2, double Y2) {
			setLine(X1, Y1, X2, Y2);
		}

		/**
		 * Constructs and initializes a <code>Line2D</code> from the specified
		 * <code>Point2D</code> objects.
		 *
		 * @param p1
		 *            ,&nbsp;p2 the specified <code>Point2D</code> objects
		 */
		public Double(Point2D p1, Point2D p2) {
			setLine(p1, p2);
		}

		/**
		 * Returns the high-precision bounding box of this <code>Line2D</code>.
		 *
		 * @return a <code>Rectangle2D</code> that is the high-precision
		 *         bounding box of this <code>Line2D</code>.
		 */
		public Rectangle2D getBounds2D() {
			double x, y, w, h;
			if (x1 < x2) {
				x = x1;
				w = x2 - x1;
			} else {
				x = x2;
				w = x1 - x2;
			}
			if (y1 < y2) {
				y = y1;
				h = y2 - y1;
			} else {
				y = y2;
				h = y1 - y2;
			}
			return new Rectangle2D.Double(x, y, w, h);
		}

		/**
		 * Returns the starting <code>Point2D</code> of this <code>Line2D</code>
		 * .
		 *
		 * @return the starting <code>Point2D</code> of this <code>Line2D</code>
		 */
		public Point2D getP1() {
			return new Point2D.Double(x1, y1);
		}

		/**
		 * Returns the end <code>Point2D</code> of this <code>Line2D</code>.
		 *
		 * @return the ending <code>Point2D</code> of this <code>Line2D</code>.
		 */
		public Point2D getP2() {
			return new Point2D.Double(x2, y2);
		}

		/**
		 * Returns the X coordinate of the start point in double precision.
		 *
		 * @return the X coordinate of this <code>Line2D</code> object's
		 *         starting point.
		 */
		public double getX1() {
			return x1;
		}

		/**
		 * Returns the X coordinate of the end point in double precision.
		 *
		 * @return the X coordinate of this <code>Line2D</code> object's ending
		 *         point.
		 */
		public double getX2() {
			return x2;
		}

		/**
		 * Returns the Y coordinate of the start point in double precision.
		 *
		 * @return the X coordinate of this <code>Line2D</code> object's
		 *         starting point.
		 */
		public double getY1() {
			return y1;
		}

		/**
		 * Returns the Y coordinate of the end point in double precision.
		 *
		 * @return the Y coordinate of this <code>Line2D</code> object's
		 *         starting point.
		 */
		public double getY2() {
			return y2;
		}

		/**
		 * Sets the location of the endpoints of this <code>Line2D</code> to the
		 * specified double coordinates.
		 *
		 * @param X1
		 *            ,&nbsp;Y1 the first specified coordinate
		 * @param X2
		 *            ,&nbsp;Y2 the second specified coordinate
		 */
		public void setLine(double X1, double Y1, double X2, double Y2) {
			this.x1 = X1;
			this.y1 = Y1;
			this.x2 = X2;
			this.y2 = Y2;
		}
	}

	/**
	 * A line segment specified with float coordinates.
	 */
	public static class Float extends Line2D {
		/**
		 * The X coordinate of the start point of the line segment.
		 */
		public float x1;

		/**
		 * The Y coordinate of the start point of the line segment.
		 */
		public float y1;

		/**
		 * The X coordinate of the end point of the line segment.
		 */
		public float x2;

		/**
		 * The Y coordinate of the end point of the line segment.
		 */
		public float y2;

		/**
		 * Constructs and initializes a Line with coordinates (0, 0) -> (0, 0).
		 */
		public Float() {
		}

		/**
		 * Constructs and initializes a Line from the specified coordinates.
		 *
		 * @param X1
		 *            ,&nbsp;Y1 the first specified coordinates
		 * @param X2
		 *            ,&nbsp;Y2 the second specified coordinates
		 */
		public Float(float X1, float Y1, float X2, float Y2) {
			setLine(X1, Y1, X2, Y2);
		}

		/**
		 * Constructs and initializes a <code>Line2D</code> from the specified
		 * {@link Point2D} objects.
		 *
		 * @param p1
		 *            the first specified <code>Point2D</code>
		 * @param p2
		 *            the second specified <code>Point2D</code>
		 */
		public Float(Point2D p1, Point2D p2) {
			setLine(p1, p2);
		}

		/**
		 * Returns the high-precision bounding box of this <code>Line2D</code>.
		 *
		 * @return a {@link Rectangle2D} that is the high-precision bounding box
		 *         of this <code>Line2D</code>.
		 */
		public Rectangle2D getBounds2D() {
			float x, y, w, h;
			if (x1 < x2) {
				x = x1;
				w = x2 - x1;
			} else {
				x = x2;
				w = x1 - x2;
			}
			if (y1 < y2) {
				y = y1;
				h = y2 - y1;
			} else {
				y = y2;
				h = y1 - y2;
			}
			return new Rectangle2D.Float(x, y, w, h);
		}

		/**
		 * Returns the start point.
		 *
		 * @return the starting <code>Point2D</code> object of this
		 *         <code>Line2D</code>.
		 */
		public Point2D getP1() {
			return new Point2D.Float(x1, y1);
		}

		/**
		 * Returns the end point.
		 *
		 * @return the ending <code>Point2D</code> object of this
		 *         <code>Line2D</code>.
		 */
		public Point2D getP2() {
			return new Point2D.Float(x2, y2);
		}

		/**
		 * Returns the X coordinate of the start point in double precision.
		 *
		 * @return the x coordinate of this <code>Line2D</code> object's
		 *         starting point in double precision.
		 */
		public double getX1() {
			return (double) x1;
		}

		/**
		 * Returns the X coordinate of the end point in double precision.
		 *
		 * @return the x coordinate of this <code>Line2D</code> object's ending
		 *         point in double precision.
		 */
		public double getX2() {
			return (double) x2;
		}

		/**
		 * Returns the Y coordinate of the start point in double precision.
		 *
		 * @return the x coordinate of this <code>Line2D</code> object's
		 *         starting point in double precision.
		 */
		public double getY1() {
			return (double) y1;
		}

		/**
		 * Returns the Y coordinate of the end point in double precision.
		 *
		 * @return the Y coordinate of this <code>Line2D</code> object's ending
		 *         point in double precision.
		 */

		public double getY2() {
			return (double) y2;
		}

		/**
		 * Sets the location of the endpoints of this <code>Line2D</code> to the
		 * specified double coordinates.
		 *
		 * @param X1
		 *            ,&nbsp;Y1 the first specified coordinate
		 * @param X2
		 *            ,&nbsp;Y2 the second specified coordinate
		 */
		public void setLine(double X1, double Y1, double X2, double Y2) {
			this.x1 = (float) X1;
			this.y1 = (float) Y1;
			this.x2 = (float) X2;
			this.y2 = (float) Y2;
		}

		/**
		 * Sets the location of the endpoints of this <code>Line2D</code> to the
		 * specified float coordinates.
		 *
		 * @param X1
		 *            ,&nbsp;Y1 the first specified coordinate
		 * @param X2
		 *            ,&nbsp;Y2 the second specified coordinate
		 */
		public void setLine(float X1, float Y1, float X2, float Y2) {
			this.x1 = X1;
			this.y1 = Y1;
			this.x2 = X2;
			this.y2 = Y2;
		}
	}

	/**
	 * Tests if the line segment from (X1,&nbsp;Y1) to (X2,&nbsp;Y2) intersects
	 * the line segment from (X3,&nbsp;Y3) to (X4,&nbsp;Y4).
	 *
	 * @param X1
	 *            ,&nbsp;Y1 the coordinates of the beginning of the first
	 *            specified line segment
	 * @param X2
	 *            ,&nbsp;Y2 the coordinates of the end of the first specified
	 *            line segment
	 * @param X3
	 *            ,&nbsp;Y3 the coordinates of the beginning of the second
	 *            specified line segment
	 * @param X4
	 *            ,&nbsp;Y4 the coordinates of the end of the second specified
	 *            line segment
	 * @return <code>true</code> if the first specified line segment and the
	 *         second specified line segment intersect each other;
	 *         <code>false</code> otherwise.
	 */
	public static boolean linesIntersect(double X1, double Y1, double X2,
			double Y2, double X3, double Y3, double X4, double Y4) {
		return ((relativeCCW(X1, Y1, X2, Y2, X3, Y3)
				* relativeCCW(X1, Y1, X2, Y2, X4, Y4) <= 0) && (relativeCCW(X3,
				Y3, X4, Y4, X1, Y1)
				* relativeCCW(X3, Y3, X4, Y4, X2, Y2) <= 0));
	}

	/**
	 * Returns the distance from a point to a line. The distance measured is the
	 * distance between the specified point and the closest point on the
	 * infinitely-extended line defined by the specified coordinates. If the
	 * specified point intersects the line, this method returns 0.0.
	 *
	 * @param X1
	 *            ,&nbsp;Y1 the coordinates of one point on the specified line
	 * @param X2
	 *            ,&nbsp;Y2 the coordinates of another point on the specified
	 *            line
	 * @param PX
	 *            ,&nbsp;PY the coordinates of the specified point being
	 *            measured against the specified line
	 * @return a double value that is the distance from the specified point to
	 *         the specified line.
	 * @see #ptSegDist(double, double, double, double, double, double)
	 */
	public static double ptLineDist(double X1, double Y1, double X2, double Y2,
			double PX, double PY) {
		return Math.sqrt(ptLineDistSq(X1, Y1, X2, Y2, PX, PY));
	}

	/**
	 * Returns the square of the distance from a point to a line. The distance
	 * measured is the distance between the specified point and the closest
	 * point on the infinitely-extended line defined by the specified
	 * coordinates. If the specified point intersects the line, this method
	 * returns 0.0.
	 *
	 * @param X1
	 *            ,&nbsp;Y1 the coordinates of one point on the specified line
	 * @param X2
	 *            ,&nbsp;Y2 the coordinates of another point on the specified
	 *            line
	 * @param PX
	 *            ,&nbsp;PY the coordinates of the specified point being
	 *            measured against the specified line
	 * @return a double value that is the square of the distance from the
	 *         specified point to the specified line.
	 * @see #ptSegDistSq(double, double, double, double, double, double)
	 */
	public static double ptLineDistSq(double X1, double Y1, double X2,
			double Y2, double PX, double PY) {
		// Adjust vectors relative to X1,Y1
		// X2,Y2 becomes relative vector from X1,Y1 to end of segment
		X2 -= X1;
		Y2 -= Y1;
		// PX,PY becomes relative vector from X1,Y1 to test point
		PX -= X1;
		PY -= Y1;
		double dotprod = PX * X2 + PY * Y2;
		// dotprod is the length of the PX,PY vector
		// projected on the X1,Y1=>X2,Y2 vector times the
		// length of the X1,Y1=>X2,Y2 vector
		double projlenSq = dotprod * dotprod / (X2 * X2 + Y2 * Y2);
		// Distance to line is now the length of the relative point
		// vector minus the length of its projection onto the line
		double lenSq = PX * PX + PY * PY - projlenSq;
		if (lenSq < 0) {
			lenSq = 0;
		}
		return lenSq;
	}

	/**
	 * Returns the distance from a point to a line segment. The distance
	 * measured is the distance between the specified point and the closest
	 * point between the specified endpoints. If the specified point intersects
	 * the line segment in between the endpoints, this method returns 0.0.
	 *
	 * @param X1
	 *            ,&nbsp;Y1 the coordinates of the beginning of the specified
	 *            line segment
	 * @param X2
	 *            ,&nbsp;Y2 the coordinates of the end of the specified line
	 *            segment
	 * @param PX
	 *            ,&nbsp;PY the coordinates of the specified point being
	 *            measured against the specified line segment
	 * @return a double value that is the distance from the specified point to
	 *         the specified line segment.
	 * @see #ptLineDist(double, double, double, double, double, double)
	 */
	public static double ptSegDist(double X1, double Y1, double X2, double Y2,
			double PX, double PY) {
		return Math.sqrt(ptSegDistSq(X1, Y1, X2, Y2, PX, PY));
	}

	/**
	 * Returns the square of the distance from a point to a line segment. The
	 * distance measured is the distance between the specified point and the
	 * closest point between the specified endpoints. If the specified point
	 * intersects the line segment in between the endpoints, this method returns
	 * 0.0.
	 *
	 * @param X1
	 *            ,&nbsp;Y1 the coordinates of the beginning of the specified
	 *            line segment
	 * @param X2
	 *            ,&nbsp;Y2 the coordinates of the end of the specified line
	 *            segment
	 * @param PX
	 *            ,&nbsp;PY the coordinates of the specified point being
	 *            measured against the specified line segment
	 * @return a double value that is the square of the distance from the
	 *         specified point to the specified line segment.
	 * @see #ptLineDistSq(double, double, double, double, double, double)
	 */
	public static double ptSegDistSq(double X1, double Y1, double X2,
			double Y2, double PX, double PY) {
		// Adjust vectors relative to X1,Y1
		// X2,Y2 becomes relative vector from X1,Y1 to end of segment
		X2 -= X1;
		Y2 -= Y1;
		// PX,PY becomes relative vector from X1,Y1 to test point
		PX -= X1;
		PY -= Y1;
		double dotprod = PX * X2 + PY * Y2;
		double projlenSq;
		if (dotprod <= 0.0) {
			// PX,PY is on the side of X1,Y1 away from X2,Y2
			// distance to segment is length of PX,PY vector
			// "length of its (clipped) projection" is now 0.0
			projlenSq = 0.0;
		} else {
			// switch to backwards vectors relative to X2,Y2
			// X2,Y2 are already the negative of X1,Y1=>X2,Y2
			// to get PX,PY to be the negative of PX,PY=>X2,Y2
			// the dot product of two negated vectors is the same
			// as the dot product of the two normal vectors
			PX = X2 - PX;
			PY = Y2 - PY;
			dotprod = PX * X2 + PY * Y2;
			if (dotprod <= 0.0) {
				// PX,PY is on the side of X2,Y2 away from X1,Y1
				// distance to segment is length of (backwards) PX,PY vector
				// "length of its (clipped) projection" is now 0.0
				projlenSq = 0.0;
			} else {
				// PX,PY is between X1,Y1 and X2,Y2
				// dotprod is the length of the PX,PY vector
				// projected on the X2,Y2=>X1,Y1 vector times the
				// length of the X2,Y2=>X1,Y1 vector
				projlenSq = dotprod * dotprod / (X2 * X2 + Y2 * Y2);
			}
		}
		// Distance to line is now the length of the relative point
		// vector minus the length of its projection onto the line
		// (which is zero if the projection falls outside the range
		// of the line segment).
		double lenSq = PX * PX + PY * PY - projlenSq;
		if (lenSq < 0) {
			lenSq = 0;
		}
		return lenSq;
	}

	/**
	 * Returns an indicator of where the specified point (PX,&nbsp;PY) lies with
	 * respect to the line segment from (X1,&nbsp;Y1) to (X2,&nbsp;Y2). The
	 * return value can be either 1, -1, or 0 and indicates in which direction
	 * the specified line must pivot around its first endpoint, (X1,&nbsp;Y1),
	 * in order to point at the specified point (PX,&nbsp;PY).
	 * <p>
	 * A return value of 1 indicates that the line segment must turn in the
	 * direction that takes the positive X axis towards the negative Y axis. In
	 * the default coordinate system used by Java 2D, this direction is
	 * counterclockwise.
	 * <p>
	 * A return value of -1 indicates that the line segment must turn in the
	 * direction that takes the positive X axis towards the positive Y axis. In
	 * the default coordinate system, this direction is clockwise.
	 * <p>
	 * A return value of 0 indicates that the point lies exactly on the line
	 * segment. Note that an indicator value of 0 is rare and not useful for
	 * determining colinearity because of floating point rounding issues.
	 * <p>
	 * If the point is colinear with the line segment, but not between the
	 * endpoints, then the value will be -1 if the point lies
	 * "beyond (X1,&nbsp;Y1)" or 1 if the point lies "beyond (X2,&nbsp;Y2)".
	 *
	 * @param X1
	 *            ,&nbsp;Y1 the coordinates of the beginning of the specified
	 *            line segment
	 * @param X2
	 *            ,&nbsp;Y2 the coordinates of the end of the specified line
	 *            segment
	 * @param PX
	 *            ,&nbsp;PY the coordinates of the specified point to be
	 *            compared with the specified line segment
	 * @return an integer that indicates the position of the third specified
	 *         coordinates with respect to the line segment formed by the first
	 *         two specified coordinates.
	 */
	public static int relativeCCW(double X1, double Y1, double X2, double Y2,
			double PX, double PY) {
		X2 -= X1;
		Y2 -= Y1;
		PX -= X1;
		PY -= Y1;
		double ccw = PX * Y2 - PY * X2;
		if (ccw == 0.0) {
			// The point is colinear, classify based on which side of
			// the segment the point falls on. We can calculate a
			// relative value using the projection of PX,PY onto the
			// segment - a negative value indicates the point projects
			// outside of the segment in the direction of the particular
			// endpoint used as the origin for the projection.
			ccw = PX * X2 + PY * Y2;
			if (ccw > 0.0) {
				// Reverse the projection to be relative to the original X2,Y2
				// X2 and Y2 are simply negated.
				// PX and PY need to have (X2 - X1) or (Y2 - Y1) subtracted
				// from them (based on the original values)
				// Since we really want to get a positive answer when the
				// point is "beyond (X2,Y2)", then we want to calculate
				// the inverse anyway - thus we leave X2 & Y2 negated.
				PX -= X2;
				PY -= Y2;
				ccw = PX * X2 + PY * Y2;
				if (ccw < 0.0) {
					ccw = 0.0;
				}
			}
		}
		return (ccw < 0.0) ? -1 : ((ccw > 0.0) ? 1 : 0);
	}

	/**
	 * This is an abstract class that cannot be instantiated directly.
	 * Type-specific implementation subclasses are available for instantiation
	 * and provide a number of formats for storing the information necessary to
	 * satisfy the various accessory methods below.
	 *
	 * @see java.awt.geom.Line2D.Float
	 * @see java.awt.geom.Line2D.Double
	 */
	protected Line2D() {
	}

	/**
	 * Tests if a specified coordinate is inside the boundary of this
	 * <code>Line2D</code>. This method is required to implement the
	 * {@link Shape} interface, but in the case of <code>Line2D</code> objects
	 * it always returns <code>false</code> since a line contains no area.
	 *
	 * @param x
	 *            ,&nbsp;y the coordinates of the specified point
	 * @return <code>false</code> because a <code>Line2D</code> contains no
	 *         area.
	 */
	public boolean contains(double x, double y) {
		return false;
	}

	/**
	 * Tests if the interior of this <code>Line2D</code> entirely contains the
	 * specified set of rectangular coordinates. This method is required to
	 * implement the <code>Shape</code> interface, but in the case of
	 * <code>Line2D</code> objects it always returns false since a line contains
	 * no area.
	 *
	 * @param x
	 *            ,&nbsp;y the coordinates of the top-left corner of the
	 *            specified rectangular area
	 * @param w
	 *            the width of the specified rectangular area
	 * @param h
	 *            the height of the specified rectangular area
	 * @return <code>false</code> because a <code>Line2D</code> contains no
	 *         area.
	 */
	public boolean contains(double x, double y, double w, double h) {
		return false;
	}

	/**
	 * Tests if a given <code>Point2D</code> is inside the boundary of this
	 * <code>Line2D</code>. This method is required to implement the
	 * <code>Shape</code> interface, but in the case of <code>Line2D</code>
	 * objects it always returns <code>false</code> since a line contains no
	 * area.
	 *
	 * @param p
	 *            the specified <code>Point2D</code> to be tested
	 * @return <code>false</code> because a <code>Line2D</code> contains no
	 *         area.
	 */
	public boolean contains(Point2D p) {
		return false;
	}

	/**
	 * Tests if the interior of this <code>Line2D</code> entirely contains the
	 * specified <code>Rectangle2D</code>. This method is required to implement
	 * the <code>Shape</code> interface, but in the case of <code>Line2D</code>
	 * objects it always returns <code>false</code> since a line contains no
	 * area.
	 *
	 * @param r
	 *            the specified <code>Rectangle2D</code> to be tested
	 * @return <code>false</code> because a <code>Line2D</code> contains no
	 *         area.
	 */
	public boolean contains(Rectangle2D r) {
		return false;
	}

	/**
	 * Returns the bounding box of this <code>Line2D</code>.
	 *
	 * @return a {@link Rectangle} that is the bounding box of the
	 *         <code>Line2D</code>.
	 */
	public Rectangle getBounds() {
		return getBounds2D().getBounds();
	}

	/**
	 * Returns the starting <code>Point2D</code> of this <code>Line2D</code>.
	 *
	 * @return the starting <code>Point2D</code> of this <code>Line2D</code>.
	 */
	public abstract Point2D getP1();

	/**
	 * Returns the end <code>Point2D</code> of this <code>Line2D</code>.
	 *
	 * @return a <code>Point2D</code> that is the endpoint of this
	 *         <code>Line2D</code>.
	 */
	public abstract Point2D getP2();

	/**
	 * Returns an iteration object that defines the boundary of this
	 * <code>Line2D</code>. The iterator for this class is not multi-threaded
	 * safe, which means that this <code>Line2D</code> class does not guarantee
	 * that modifications to the geometry of this <code>Line2D</code> object do
	 * not affect any iterations of that geometry that are already in process.
	 *
	 * @param at
	 *            the specified {@link AffineTransform}
	 * @return a {@link PathIterator} that defines the boundary of this
	 *         <code>Line2D</code>.
	 */
	public PathIterator getPathIterator(AffineTransform at) {
		return new LineIterator(this, at);
	}

	/**
	 * Returns an iteration object that defines the boundary of this flattened
	 * <code>Line2D</code>. The iterator for this class is not multi-threaded
	 * safe, which means that this <code>Line2D</code> class does not guarantee
	 * that modifications to the geometry of this <code>Line2D</code> object do
	 * not affect any iterations of that geometry that are already in process.
	 *
	 * @param at
	 *            the specified <code>AffineTransform</code>
	 * @param flatness
	 *            the maximum amount that the control points for a given curve
	 *            can vary from colinear before a subdivided curve is replaced
	 *            by a straight line connecting the endpoints. Since a
	 *            <code>Line2D</code> object is always flat, this parameter is
	 *            ignored.
	 * @return a <code>PathIterator</code> that defines the boundary of the
	 *         flattened <code>Line2D</code>
	 */
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return new LineIterator(this, at);
	}

	/**
	 * Returns the X coordinate of the start point in double precision.
	 *
	 * @return the X coordinate of this <code>Line2D</code> object's starting
	 *         point.
	 */
	public abstract double getX1();

	/**
	 * Returns the X coordinate of the end point in double precision.
	 *
	 * @return the X coordinate of this <code>Line2D</code> object's starting
	 *         point.
	 */
	public abstract double getX2();

	/**
	 * Returns the Y coordinate of the start point in double precision.
	 *
	 * @return the Y coordinate of this <code>Line2D</code> object's starting
	 *         point.
	 */
	public abstract double getY1();

	/**
	 * Returns the Y coordinate of the end point in double precision.
	 *
	 * @return the Y coordinate of this <code>Line2D</code> object's starting
	 *         point.
	 */
	public abstract double getY2();

	/**
	 * Tests if this <code>Line2D</code> intersects the interior of a specified
	 * set of rectangular coordinates.
	 *
	 * @param x
	 *            ,&nbsp;y the coordinates of the top-left corner of the
	 *            specified rectangular area
	 * @param w
	 *            the width of the specified rectangular area
	 * @param h
	 *            the height of the specified rectangular area
	 * @return <code>true</code> if this <code>Line2D</code> intersects the
	 *         interior of the specified set of rectangular coordinates;
	 *         <code>false</code> otherwise.
	 */
	public boolean intersects(double x, double y, double w, double h) {
		return intersects(new Rectangle2D.Double(x, y, w, h));
	}

	/**
	 * Tests if this <code>Line2D</code> intersects the interior of a specified
	 * <code>Rectangle2D</code>.
	 *
	 * @param r
	 *            the specified <code>Rectangle2D</code> to be tested
	 * @return <code>true</code> if this <code>Line2D</code> intersects the
	 *         interior of the specified <code>Rectangle2D</code>;
	 *         <code>false</code> otherwise.
	 */
	public boolean intersects(Rectangle2D r) {
		return r.intersectsLine(getX1(), getY1(), getX2(), getY2());
	}

	/**
	 * Tests if the line segment from (X1,&nbsp;Y1) to (X2,&nbsp;Y2) intersects
	 * this line segment.
	 *
	 * @param X1
	 *            ,&nbsp;Y1 the coordinates of the beginning of the specified
	 *            line segment
	 * @param X2
	 *            ,&nbsp;Y2 the coordinates of the end of the specified line
	 *            segment
	 * @return <true> if this line segment and the specified line segment
	 *         intersect each other; <code>false</code> otherwise.
	 */
	public boolean intersectsLine(double X1, double Y1, double X2, double Y2) {
		return linesIntersect(X1, Y1, X2, Y2, getX1(), getY1(), getX2(),
				getY2());
	}

	/**
	 * Tests if the specified line segment intersects this line segment.
	 *
	 * @param l
	 *            the specified <code>Line2D</code>
	 * @return <code>true</code> if this line segment and the specified line
	 *         segment intersect each other; <code>false</code> otherwise.
	 */
	public boolean intersectsLine(Line2D l) {
		return linesIntersect(l.getX1(), l.getY1(), l.getX2(), l.getY2(),
				getX1(), getY1(), getX2(), getY2());
	}

	/**
	 * Returns the distance from a point to this line. The distance measured is
	 * the distance between the specified point and the closest point on the
	 * infinitely-extended line defined by this <code>Line2D</code>. If the
	 * specified point intersects the line, this method returns 0.0.
	 *
	 * @param PX
	 *            ,&nbsp;PY the coordinates of the specified point being
	 *            measured against this line
	 * @return a double value that is the distance from a specified point to the
	 *         current line.
	 * @see #ptSegDist(double, double)
	 */
	public double ptLineDist(double PX, double PY) {
		return ptLineDist(getX1(), getY1(), getX2(), getY2(), PX, PY);
	}

	/**
	 * Returns the distance from a <code>Point2D</code> to this line. The
	 * distance measured is the distance between the specified point and the
	 * closest point on the infinitely-extended line defined by this
	 * <code>Line2D</code>. If the specified point intersects the line, this
	 * method returns 0.0.
	 *
	 * @param pt
	 *            the specified <code>Point2D</code> being measured
	 * @return a double value that is the distance from a specified
	 *         <code>Point2D</code> to the current line.
	 * @see #ptSegDist(Point2D)
	 */
	public double ptLineDist(Point2D pt) {
		return ptLineDist(getX1(), getY1(), getX2(), getY2(), pt.getX(), pt
				.getY());
	}

	/**
	 * Returns the square of the distance from a point to this line. The
	 * distance measured is the distance between the specified point and the
	 * closest point on the infinitely-extended line defined by this
	 * <code>Line2D</code>. If the specified point intersects the line, this
	 * method returns 0.0.
	 *
	 * @param PX
	 *            ,&nbsp;PY the coordinates of the specified point being
	 *            measured against this line
	 * @return a double value that is the square of the distance from a
	 *         specified point to the current line.
	 * @see #ptSegDistSq(double, double)
	 */
	public double ptLineDistSq(double PX, double PY) {
		return ptLineDistSq(getX1(), getY1(), getX2(), getY2(), PX, PY);
	}

	/**
	 * Returns the square of the distance from a specified <code>Point2D</code>
	 * to this line. The distance measured is the distance between the specified
	 * point and the closest point on the infinitely-extended line defined by
	 * this <code>Line2D</code>. If the specified point intersects the line,
	 * this method returns 0.0.
	 *
	 * @param pt
	 *            the specified <code>Point2D</code> being measured against this
	 *            line
	 * @return a double value that is the square of the distance from a
	 *         specified <code>Point2D</code> to the current line.
	 * @see #ptSegDistSq(Point2D)
	 */
	public double ptLineDistSq(Point2D pt) {
		return ptLineDistSq(getX1(), getY1(), getX2(), getY2(), pt.getX(), pt
				.getY());
	}

	/**
	 * Returns the distance from a point to this line segment. The distance
	 * measured is the distance between the specified point and the closest
	 * point between the current line's endpoints. If the specified point
	 * intersects the line segment in between the endpoints, this method returns
	 * 0.0.
	 *
	 * @param PX
	 *            ,&nbsp;PY the coordinates of the specified point being
	 *            measured against this line segment
	 * @return a double value that is the distance from the specified point to
	 *         the current line segment.
	 * @see #ptLineDist(double, double)
	 */
	public double ptSegDist(double PX, double PY) {
		return ptSegDist(getX1(), getY1(), getX2(), getY2(), PX, PY);
	}

	/**
	 * Returns the distance from a <code>Point2D</code> to this line segment.
	 * The distance measured is the distance between the specified point and the
	 * closest point between the current line's endpoints. If the specified
	 * point intersects the line segment in between the endpoints, this method
	 * returns 0.0.
	 *
	 * @param pt
	 *            the specified <code>Point2D</code> being measured against this
	 *            line segment
	 * @return a double value that is the distance from the specified
	 *         <code>Point2D</code> to the current line segment.
	 * @see #ptLineDist(Point2D)
	 */
	public double ptSegDist(Point2D pt) {
		return ptSegDist(getX1(), getY1(), getX2(), getY2(), pt.getX(), pt
				.getY());
	}

	/**
	 * Returns the square of the distance from a point to this line segment. The
	 * distance measured is the distance between the specified point and the
	 * closest point between the current line's endpoints. If the specified
	 * point intersects the line segment in between the endpoints, this method
	 * returns 0.0.
	 *
	 * @param PX
	 *            ,&nbsp;PY the coordinates of the specified point being
	 *            measured against this line segment
	 * @return a double value that is the square of the distance from the
	 *         specified point to the current line segment.
	 * @see #ptLineDistSq(double, double)
	 */
	public double ptSegDistSq(double PX, double PY) {
		return ptSegDistSq(getX1(), getY1(), getX2(), getY2(), PX, PY);
	}

	/**
	 * Returns the square of the distance from a <code>Point2D</code> to this
	 * line segment. The distance measured is the distance between the specified
	 * point and the closest point between the current line's endpoints. If the
	 * specified point intersects the line segment in between the endpoints,
	 * this method returns 0.0.
	 *
	 * @param pt
	 *            the specified <code>Point2D</code> being measured against this
	 *            line segment.
	 * @return a double value that is the square of the distance from the
	 *         specified <code>Point2D</code> to the current line segment.
	 * @see #ptLineDistSq(Point2D)
	 */
	public double ptSegDistSq(Point2D pt) {
		return ptSegDistSq(getX1(), getY1(), getX2(), getY2(), pt.getX(), pt
				.getY());
	}

	/**
	 * Returns an indicator of where the specified point (PX,&nbsp;PY) lies with
	 * respect to this line segment. See the method comments of
	 * {@link #relativeCCW(double, double, double, double, double, double)} to
	 * interpret the return value.
	 *
	 * @param PX
	 *            ,&nbsp;PY the coordinates of the specified point to be
	 *            compared with the current line segment
	 * @return an integer that indicates the position of the specified
	 *         coordinates with respect to the current line segment.
	 * @see #relativeCCW(double, double, double, double, double, double)
	 */
	public int relativeCCW(double PX, double PY) {
		return relativeCCW(getX1(), getY1(), getX2(), getY2(), PX, PY);
	}

	/**
	 * Returns an indicator of where the specified <code>Point2D</code> lies
	 * with respect to this line segment. See the method comments of
	 * {@link #relativeCCW(double, double, double, double, double, double)} to
	 * interpret the return value.
	 *
	 * @param p
	 *            the specified <code>Point2D</code> to be compared with the
	 *            current line segment
	 * @return an integer that indicates the position of the
	 *         <code>Point2D</code> with respect to the current line segment.
	 * @see #relativeCCW(double, double, double, double, double, double)
	 */
	public int relativeCCW(Point2D p) {
		return relativeCCW(getX1(), getY1(), getX2(), getY2(), p.getX(), p
				.getY());
	}

	/**
	 * Sets the location of the endpoints of this <code>Line2D</code> to the
	 * specified double coordinates.
	 *
	 * @param X1
	 *            ,&nbsp;Y1 the first specified coordinate
	 * @param X2
	 *            ,&nbsp;Y2 the second specified coordinate
	 */
	public abstract void setLine(double X1, double Y1, double X2, double Y2);

	/**
	 * Sets the location of the endpoints of this <code>Line2D</code> to the
	 * same as those endpoints of the specified <code>Line2D</code>.
	 *
	 * @param l
	 *            the specified <code>Line2D</code>
	 */
	public void setLine(Line2D l) {
		setLine(l.getX1(), l.getY1(), l.getX2(), l.getY2());
	}

	/**
	 * Sets the location of the endpoints of this <code>Line2D</code> to the
	 * specified <code>Point2D</code> coordinates.
	 *
	 * @param p1
	 *            ,&nbsp;p2 the specified <code>Point2D</code> objects
	 */
	public void setLine(Point2D p1, Point2D p2) {
		setLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}
}
