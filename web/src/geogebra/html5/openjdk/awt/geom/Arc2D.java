/*
 * @(#)Arc2D.java	1.27 03/12/19
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
 * <CODE>Arc2D</CODE> is the abstract superclass for all objects that store a 2D
 * arc defined by a bounding rectangle, start angle, angular extent (length of
 * the arc), and a closure type (<CODE>OPEN</CODE>, <CODE>CHORD</CODE>, or
 * <CODE>PIE</CODE>).
 * <p>
 * The bounding rectangle defines the outer boundary of the full ellipse of
 * which this arc is a partial section. The angles are specified relative to the
 * non-square extents of the bounding rectangle such that 45 degrees always
 * falls on the line from the center of the ellipse to the upper right corner of
 * the bounding rectangle. As a result, if the bounding rectangle is noticeably
 * longer along one axis than the other, the angles to the start and end of the
 * arc segment will be skewed farther along the longer axis of the bounds.
 * <p>
 * The actual storage representation of the coordinates is left to the subclass.
 *
 * @version 10 Feb 1997
 * @author Jim Graham
 */
public abstract class Arc2D extends RectangularShape {
	/**
	 * This class defines an arc specified in double precision.
	 */
	public static class Double extends Arc2D {
		/**
		 * The x coordinate of the upper left corner of the arc.
		 */
		public double x;

		/**
		 * The y coordinate of the upper left corner of the arc.
		 */
		public double y;

		/**
		 * The overall width of the full ellipse (not considering the angular
		 * extents).
		 */
		public double width;

		/**
		 * The overall height of the full ellipse (not considering the angular
		 * extents).
		 */
		public double height;

		/**
		 * The starting angle of the arc in degrees.
		 */
		public double start;

		/**
		 * The angular extent of the arc in degrees.
		 */
		public double extent;

		/**
		 * Constructs a new OPEN arc, initialized to location (0, 0), size (0,
		 * 0), angular extents (start = 0, extent = 0).
		 */
		public Double() {
			super(OPEN);
		}

		/**
		 * Constructs a new arc, initialized to the specified location, size,
		 * angular extents, and closure type.
		 *
		 * @param x
		 *            ,&nbsp;y The coordinates of the upper left corner of the
		 *            arc. (Specified in double precision.)
		 * @param w
		 *            The overall width of the full ellipse of which this arc is
		 *            a partial section. (Specified in double precision.)
		 * @param h
		 *            The overall height of the full ellipse of which this arc
		 *            is a partial section. (Specified in double precision.)
		 * @param start
		 *            The starting angle of the arc in degrees. (Specified in
		 *            double precision.)
		 * @param extent
		 *            The angular extent of the arc in degrees. (Specified in
		 *            double precision.)
		 * @param type
		 *            The closure type for the arc: {@link #OPEN OPEN},
		 *            {@link #CHORD CHORD}, or {@link #PIE PIE}.
		 */
		public Double(double x, double y, double w, double h, double start,
				double extent, int type) {
			super(type);
			this.x = x;
			this.y = y;
			this.width = w;
			this.height = h;
			this.start = start;
			this.extent = extent;
		}

		/**
		 * Constructs a new arc, initialized to location (0, 0), size (0, 0),
		 * angular extents (start = 0, extent = 0), and the specified closure
		 * type.
		 *
		 * @param type
		 *            The closure type for the arc: {@link #OPEN OPEN},
		 *            {@link #CHORD CHORD}, or {@link #PIE PIE}.
		 */
		public Double(int type) {
			super(type);
		}

		/**
		 * Constructs a new arc, initialized to the specified location, size,
		 * angular extents, and closure type.
		 *
		 * @param ellipseBounds
		 *            The bounding rectangle that defines the outer boundary of
		 *            the full ellipse of which this arc is a partial section.
		 * @param start
		 *            The starting angle of the arc in degrees. (Specified in
		 *            double precision.)
		 * @param extent
		 *            The angular extent of the arc in degrees. (Specified in
		 *            double precision.)
		 * @param type
		 *            The closure type for the arc: {@link #OPEN OPEN},
		 *            {@link #CHORD CHORD}, or {@link #PIE PIE}.
		 */
		public Double(Rectangle2D ellipseBounds, double start, double extent,
				int type) {
			super(type);
			this.x = ellipseBounds.getX();
			this.y = ellipseBounds.getY();
			this.width = ellipseBounds.getWidth();
			this.height = ellipseBounds.getHeight();
			this.start = start;
			this.extent = extent;
		}

		/**
		 * Returns the angular extent of the arc.
		 *
		 * @return A double value that represents the angular extent of the arc
		 *         in degrees.
		 * @see #setAngleExtent
		 */
		public double getAngleExtent() {
			return extent;
		}

		/**
		 * Returns the starting angle of the arc.
		 *
		 * @return a double value that represents the starting angle of the arc
		 *         in degrees.
		 * @see #setAngleStart
		 */
		public double getAngleStart() {
			return start;
		}

		/**
		 * Returns the height of the ellipse of which this arc is a partial
		 * section.
		 *
		 * @return A double value that represents the height of the full ellipse
		 *         of which this arc is a partial section.
		 */
		public double getHeight() {
			return height;
		}

		/**
		 * Returns the width of the ellipse of which this arc is a partial
		 * section.
		 *
		 * @return A double value that represents the width of the full ellipse
		 *         of which this arc is a partial section.
		 */
		public double getWidth() {
			return width;
		}

		/**
		 * Returns the x coordinate of the upper left corner of the arc.
		 *
		 * @return The x coordinate of arc's upper left coordinate in double
		 *         precision.
		 */
		public double getX() {
			return x;
		}

		/**
		 * Returns the y coordinate of the upper left corner of the arc.
		 *
		 * @return The y coordinate of arc's upper left coordinate in double
		 *         precision.
		 */
		public double getY() {
			return y;
		}

		/**
		 * Determines whether the arc is empty.
		 *
		 * @return <CODE>true</CODE> if the arc is empty, <CODE>false</CODE> if
		 *         it not.
		 */
		public boolean isEmpty() {
			return (width <= 0.0 || height <= 0.0);
		}

		/**
		 * Sets the angular extent of this arc to the specified double value.
		 *
		 * @param angExt
		 *            The angular extent of the arc in degrees.
		 * @see #getAngleExtent
		 */
		public void setAngleExtent(double angExt) {
			this.extent = angExt;
		}

		/**
		 * Sets the starting angle of this arc to the specified double value.
		 *
		 * @param angSt
		 *            The starting angle of the arc in degrees.
		 * @see #getAngleStart
		 */
		public void setAngleStart(double angSt) {
			this.start = angSt;
		}

		/**
		 * Sets the location, size, angular extents, and closure type of this
		 * arc to the specified double values.
		 *
		 * @param x
		 *            ,&nbsp;y The coordinates of the upper left corner of the
		 *            arc.
		 * @param w
		 *            The overall width of the full ellipse of which this arc is
		 *            a partial section.
		 * @param h
		 *            The overall height of the full ellipse of which this arc
		 *            is a partial section.
		 * @param angSt
		 *            The starting angle of the arc in degrees.
		 * @param angExt
		 *            The angular extent of the arc in degrees.
		 * @param closure
		 *            The closure type for the arc: {@link #OPEN OPEN},
		 *            {@link #CHORD CHORD}, or {@link #PIE PIE}.
		 */
		public void setArc(double x, double y, double w, double h,
				double angSt, double angExt, int closure) {
			this.setArcType(closure);
			this.x = x;
			this.y = y;
			this.width = w;
			this.height = h;
			this.start = angSt;
			this.extent = angExt;
		}

		/**
		 * Returns the high-precision bounding box of the arc.
		 *
		 * @param x
		 *            ,&nbsp;y The coordinates of the upper left corner of the
		 *            arc.
		 * @param w
		 *            The overall width of the full ellipse of which this arc is
		 *            a partial section.
		 * @param h
		 *            The overall height of the full ellipse of which this arc
		 *            is a partial section.
		 *
		 * @return The bounding box as a <CODE>Rectangle2D</CODE> object.
		 */
		protected Rectangle2D makeBounds(double x, double y, double w, double h) {
			return new Rectangle2D.Double(x, y, w, h);
		}
	}

	/**
	 * This class defines an arc specified in float precision.
	 */
	public static class Float extends Arc2D {

		/**
		 * The x coordinate of the upper left corner of the arc.
		 */
		public float x;

		/**
		 * The y coordinate of the upper left corner of the arc.
		 */
		public float y;

		/**
		 * The overall width of the full ellipse of which this arc is a partial
		 * section (not considering the angular extents).
		 */
		public float width;

		/**
		 * The overall height of the full ellipse of which this arc is a partial
		 * section (not considering the angular extents).
		 */
		public float height;

		/**
		 * The starting angle of the arc in degrees.
		 */
		public float start;

		/**
		 * The angular extent of the arc in degrees.
		 */
		public float extent;

		/**
		 * Constructs a new OPEN arc, initialized to location (0, 0), size (0,
		 * 0), angular extents (start = 0, extent = 0).
		 */
		public Float() {
			super(OPEN);
		}

		/**
		 * Constructs a new arc, initialized to the specified location, size,
		 * angular extents, and closure type.
		 *
		 * @param x
		 *            ,&nbsp;y The coordinates of the upper left corner of the
		 *            arc. (Specified in float precision.)
		 * @param w
		 *            The overall width of the full ellipse of which this arc is
		 *            a partial section. (Specified in float precision.)
		 * @param h
		 *            The overall height of the full ellipse of which this arc
		 *            is a partial section. (Specified in float precision.)
		 * @param start
		 *            The starting angle of the arc in degrees. (Specified in
		 *            float precision.)
		 * @param extent
		 *            The angular extent of the arc in degrees. (Specified in
		 *            float precision.)
		 * @param type
		 *            The closure type for the arc: {@link #OPEN OPEN},
		 *            {@link #CHORD CHORD}, or {@link #PIE PIE}.
		 */
		public Float(float x, float y, float w, float h, float start,
				float extent, int type) {
			super(type);
			this.x = x;
			this.y = y;
			this.width = w;
			this.height = h;
			this.start = start;
			this.extent = extent;
		}

		/**
		 * Constructs a new arc, initialized to location (0, 0), size (0, 0),
		 * angular extents (start = 0, extent = 0), and the specified closure
		 * type.
		 *
		 * @param type
		 *            The closure type for the arc: {@link #OPEN OPEN},
		 *            {@link #CHORD CHORD}, or {@link #PIE PIE}.
		 */
		public Float(int type) {
			super(type);
		}

		/**
		 * Constructs a new arc, initialized to the specified location, size,
		 * angular extents, and closure type.
		 *
		 * @param ellipseBounds
		 *            The bounding rectangle that defines the outer boundary of
		 *            the full ellipse of which this arc is a partial section.
		 * @param start
		 *            The starting angle of the arc in degrees. (Specified in
		 *            float precision.)
		 * @param extent
		 *            The angular extent of the arc in degrees. (Specified in
		 *            float precision.)
		 * @param type
		 *            The closure type for the arc: {@link #OPEN OPEN},
		 *            {@link #CHORD CHORD}, or {@link #PIE PIE}.
		 */
		public Float(Rectangle2D ellipseBounds, float start, float extent,
				int type) {
			super(type);
			this.x = (float) ellipseBounds.getX();
			this.y = (float) ellipseBounds.getY();
			this.width = (float) ellipseBounds.getWidth();
			this.height = (float) ellipseBounds.getHeight();
			this.start = start;
			this.extent = extent;
		}

		/**
		 * Returns the angular extent of the arc.
		 *
		 * @return A double value that represents the angular extent of the arc
		 *         in degrees.
		 * @see #setAngleExtent
		 */
		public double getAngleExtent() {
			return (double) extent;
		}

		/**
		 * Returns the starting angle of the arc.
		 *
		 * @return A double value that represents the starting angle of the arc
		 *         in degrees.
		 * @see #setAngleStart
		 */
		public double getAngleStart() {
			return (double) start;
		}

		/**
		 * Returns the height of the ellipse of which this arc is a partial
		 * section.
		 *
		 * @return A double value that represents the height of the full ellipse
		 *         of which this arc is a partial section.
		 */
		public double getHeight() {
			return (double) height;
		}

		/**
		 * Returns the width of the ellipse of which this arc is a partial
		 * section.
		 *
		 * @return A double value that represents the width of the full ellipse
		 *         of which this arc is a partial section.
		 */
		public double getWidth() {
			return (double) width;
		}

		/**
		 * Returns the x coordinate of the upper left corner of the arc.
		 *
		 * @return The x coordinate of arc's upper left coordinate in double
		 *         precision.
		 */
		public double getX() {
			return (double) x;
		}

		/**
		 * Returns the y coordinate of the upper left corner of the arc.
		 *
		 * @return The y coordinate of arc's upper left coordinate in double
		 *         precision.
		 */
		public double getY() {
			return (double) y;
		}

		/**
		 * Determines whether the arc is empty.
		 *
		 * @return <CODE>true</CODE> if the arc is empty, <CODE>false</CODE> if
		 *         it is not.
		 */
		public boolean isEmpty() {
			return (width <= 0.0 || height <= 0.0);
		}

		/**
		 * Sets the angular extent of this arc to the specified double value.
		 *
		 * @param angExt
		 *            The angular extent of the arc in degrees.
		 * @see #getAngleExtent
		 */
		public void setAngleExtent(double angExt) {
			this.extent = (float) angExt;
		}

		/**
		 * Sets the starting angle of this arc to the specified double value.
		 *
		 * @param angSt
		 *            The starting angle of the arc in degrees.
		 * @see #getAngleStart
		 */
		public void setAngleStart(double angSt) {
			this.start = (float) angSt;
		}

		/**
		 * Sets the location, size, angular extents, and closure type of this
		 * arc to the specified double values.
		 *
		 * @param x
		 *            ,&nbsp;y The coordinates of the upper left corner of the
		 *            arc.
		 * @param w
		 *            The overall width of the full ellipse of which this arc is
		 *            a partial section.
		 * @param h
		 *            The overall height of the full ellipse of which this arc
		 *            is a partial section.
		 * @param angSt
		 *            The starting angle of the arc in degrees.
		 * @param angExt
		 *            The angular extent of the arc in degrees.
		 * @param closure
		 *            The closure type for the arc: {@link #OPEN OPEN},
		 *            {@link #CHORD CHORD}, or {@link #PIE PIE}.
		 */
		public void setArc(double x, double y, double w, double h,
				double angSt, double angExt, int closure) {
			this.setArcType(closure);
			this.x = (float) x;
			this.y = (float) y;
			this.width = (float) w;
			this.height = (float) h;
			this.start = (float) angSt;
			this.extent = (float) angExt;
		}

		/**
		 * Return the high-precision bounding box of the arc.
		 *
		 * @param x
		 *            ,&nbsp;y The coordinates of the upper left corner of the
		 *            arc.
		 * @param w
		 *            The overall width of the full ellipse of which this arc is
		 *            a partial section.
		 * @param h
		 *            The overall height of the full ellipse of which this arc
		 *            is a partial section.
		 *
		 * @return The bounding box as a <CODE>Rectangle2D</CODE> object.
		 */
		protected Rectangle2D makeBounds(double x, double y, double w, double h) {
			return new Rectangle2D.Float((float) x, (float) y, (float) w,
					(float) h);
		}
	}

	/**
	 * The closure type for an open arc with no path segments connecting the two
	 * ends of the arc segment.
	 */
	public final static int OPEN = 0;

	/**
	 * The closure type for an arc closed by drawing a straight line segment
	 * from the start of the arc segment to the end of the arc segment.
	 */
	public final static int CHORD = 1;

	/**
	 * The closure type for an arc closed by drawing straight line segments from
	 * the start of the arc segment to the center of the full ellipse and from
	 * that point to the end of the arc segment.
	 */
	public final static int PIE = 2;

	/**
	 * Replaces the Math.IEEERemainder function
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static double remainder(double a, double b) {
		return Math.signum(a)
				* (Math.abs(a) - Math.abs(b)
						* Math.floor(Math.abs(a) / Math.abs(b)));
	}

	/*
	 * Normalizes the specified angle into the range -180 to 180.
	 */
	static double normalizeDegrees(double angle) {
		if (angle > 180.0) {
			if (angle <= (180.0 + 360.0)) {
				angle = angle - 360.0;
			} else {

				angle = remainder(angle, 360.0);
				// IEEEremainder can return -180 here for some input values...
				if (angle == -180.0) {
					angle = 180.0;
				}
			}
		} else if (angle <= -180.0) {
			if (angle > (-180.0 - 360.0)) {
				angle = angle + 360.0;
			} else {
				angle = remainder(angle, 360.0);
				// IEEEremainder can return -180 here for some input values...
				if (angle == -180.0) {
					angle = 180.0;
				}
			}
		}
		return angle;
	}

	private int type;

	/**
	 * This is an abstract class that cannot be instantiated directly.
	 * Type-specific implementation subclasses are available for instantiation
	 * and provide a number of formats for storing the information necessary to
	 * satisfy the various accessor methods below.
	 *
	 * @param type
	 *            The closure type of this arc: {@link #OPEN OPEN},
	 *            {@link #CHORD CHORD}, or {@link #PIE PIE}.
	 * @see java.awt.geom.Arc2D.Float
	 * @see java.awt.geom.Arc2D.Double
	 */
	protected Arc2D(int type) {
		setArcType(type);
	}

	/**
	 * Determines whether or not the specified point is inside the boundary of
	 * the arc.
	 *
	 * @param x
	 *            ,&nbsp;y The coordinates of the point to test. (Specified in
	 *            double precision.)
	 *
	 * @return <CODE>true</CODE> if the point lies within the bound of the arc,
	 *         <CODE>false</CODE> if the point lies outside of the arc's bounds.
	 */
	public boolean contains(double x, double y) {
		// Normalize the coordinates compared to the ellipse
		// having a center at 0,0 and a radius of 0.5.
		double ellw = getWidth();
		if (ellw <= 0.0) {
			return false;
		}
		double normx = (x - getX()) / ellw - 0.5;
		double ellh = getHeight();
		if (ellh <= 0.0) {
			return false;
		}
		double normy = (y - getY()) / ellh - 0.5;
		double distSq = (normx * normx + normy * normy);
		if (distSq >= 0.25) {
			return false;
		}
		double angExt = Math.abs(getAngleExtent());
		if (angExt >= 360.0) {
			return true;
		}
		boolean inarc = containsAngle(-Math.toDegrees(Math.atan2(normy, normx)));
		if (type == PIE) {
			return inarc;
		}
		// CHORD and OPEN behave the same way
		if (inarc) {
			if (angExt >= 180.0) {
				return true;
			}
			// point must be outside the "pie triangle"
		} else {
			if (angExt <= 180.0) {
				return false;
			}
			// point must be inside the "pie triangle"
		}
		// The point is inside the pie triangle iff it is on the same
		// side of the line connecting the ends of the arc as the center.
		double angle = Math.toRadians(-getAngleStart());
		double x1 = Math.cos(angle);
		double y1 = Math.sin(angle);
		angle += Math.toRadians(-getAngleExtent());
		double x2 = Math.cos(angle);
		double y2 = Math.sin(angle);
		boolean inside = (Line2D.relativeCCW(x1, y1, x2, y2, 2 * normx,
				2 * normy)
				* Line2D.relativeCCW(x1, y1, x2, y2, 0, 0) >= 0);
		return inarc ? !inside : inside;
	}

	/**
	 * Determine whether or not the interior of the arc entirely contains the
	 * specified rectangle.
	 *
	 * @param x
	 *            ,&nbsp;y The coordinates of the rectangle's upper left corner.
	 *            (Specified in double precision.)
	 * @param w
	 *            The width of the rectangle. (Specified in double precision.)
	 * @param h
	 *            The height of the rectangle. (Specified in double precision.)
	 *
	 * @return <CODE>true</CODE> if the arc contains the rectangle,
	 *         <CODE>false</CODE> if the arc doesn't contain the rectangle.
	 */
	public boolean contains(double x, double y, double w, double h) {
		return contains(x, y, w, h, null);
	}

	/**
	 * Determine whether or not the interior of the arc entirely contains the
	 * specified rectangle.
	 *
	 * @param r
	 *            The <CODE>Rectangle2D</CODE> to test.
	 *
	 * @return <CODE>true</CODE> if the arc contains the rectangle,
	 *         <CODE>false</CODE> if the arc doesn't contain the rectangle.
	 */
	public boolean contains(Rectangle2D r) {
		return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight(), r);
	}

	/**
	 * Determines whether or not the specified angle is within the angular
	 * extents of the arc.
	 *
	 * @param angle
	 *            The angle to test. (Specified in double precision.)
	 *
	 * @return <CODE>true</CODE> if the arc contains the angle,
	 *         <CODE>false</CODE> if the arc doesn't contain the angle.
	 */
	public boolean containsAngle(double angle) {
		double angExt = getAngleExtent();
		boolean backwards = (angExt < 0.0);
		if (backwards) {
			angExt = -angExt;
		}
		if (angExt >= 360.0) {
			return true;
		}
		angle = normalizeDegrees(angle) - normalizeDegrees(getAngleStart());
		if (backwards) {
			angle = -angle;
		}
		if (angle < 0.0) {
			angle += 360.0;
		}

		return (angle >= 0.0) && (angle < angExt);
	}

	/**
	 * Returns the angular extent of the arc.
	 *
	 * @return A double value that represents the angular extent of the arc in
	 *         degrees.
	 * @see #setAngleExtent
	 */
	public abstract double getAngleExtent();

	/**
	 * Returns the starting angle of the arc.
	 *
	 * @return A double value that represents the starting angle of the arc in
	 *         degrees.
	 * @see #setAngleStart
	 */
	public abstract double getAngleStart();

	/**
	 * Returns the arc closure type of the arc: {@link #OPEN OPEN},
	 * {@link #CHORD CHORD}, or {@link #PIE PIE}.
	 *
	 * @return One of the integer constant closure types defined in this class.
	 * @see #setArcType
	 */
	public int getArcType() {
		return type;
	}

	/**
	 * Returns the high-precision bounding box of the arc. The bounding box
	 * contains only the part of this <code>Arc2D</code> that is in between the
	 * starting and ending angles and contains the pie wedge, if this
	 * <code>Arc2D</code> has a <code>PIE</code> closure type.
	 * <p>
	 * This method differs from the {@link RectangularShape#getBounds()
	 * getBounds} in that the <code>getBounds</code> method only returns the
	 * bounds of the enclosing ellipse of this <code>Arc2D</code> without
	 * considering the starting and ending angles of this <code>Arc2D</code>.
	 *
	 * @return the <CODE>Rectangle2D</CODE> that represents the arc's bounding
	 *         box.
	 */
	public Rectangle2D getBounds2D() {
		if (isEmpty()) {
			return makeBounds(getX(), getY(), getWidth(), getHeight());
		}
		double x1, y1, x2, y2;
		if (getArcType() == PIE) {
			x1 = y1 = x2 = y2 = 0.0;
		} else {
			x1 = y1 = 1.0;
			x2 = y2 = -1.0;
		}
		double angle = 0.0;
		for (int i = 0; i < 6; i++) {
			if (i < 4) {
				// 0-3 are the four quadrants
				angle += 90.0;
				if (!containsAngle(angle)) {
					continue;
				}
			} else if (i == 4) {
				// 4 is start angle
				angle = getAngleStart();
			} else {
				// 5 is end angle
				angle += getAngleExtent();
			}
			double rads = Math.toRadians(-angle);
			double xe = Math.cos(rads);
			double ye = Math.sin(rads);
			x1 = Math.min(x1, xe);
			y1 = Math.min(y1, ye);
			x2 = Math.max(x2, xe);
			y2 = Math.max(y2, ye);
		}
		double w = getWidth();
		double h = getHeight();
		x2 = (x2 - x1) * 0.5 * w;
		y2 = (y2 - y1) * 0.5 * h;
		x1 = getX() + (x1 * 0.5 + 0.5) * w;
		y1 = getY() + (y1 * 0.5 + 0.5) * h;
		return makeBounds(x1, y1, x2, y2);
	}

	/**
	 * Returns the ending point of the arc. This point is the intersection of
	 * the ray from the center defined by the starting angle plus the angular
	 * extent of the arc and the elliptical boundary of the arc.
	 *
	 * @return A <CODE>Point2D</CODE> object representing the x,y coordinates of
	 *         the ending point of the arc.
	 */
	public Point2D getEndPoint() {
		double angle = Math.toRadians(-getAngleStart() - getAngleExtent());
		double x = getX() + (Math.cos(angle) * 0.5 + 0.5) * getWidth();
		double y = getY() + (Math.sin(angle) * 0.5 + 0.5) * getHeight();
		return new Point2D.Double(x, y);
	}

	/**
	 * Returns an iteration object that defines the boundary of the arc. This
	 * iterator is multithread safe. <code>Arc2D</code> guarantees that
	 * modifications to the geometry of the arc do not affect any iterations of
	 * that geometry that are already in process.
	 *
	 * @param at
	 *            an optional <CODE>AffineTransform</CODE> to be applied to the
	 *            coordinates as they are returned in the iteration, or null if
	 *            the untransformed coordinates are desired.
	 *
	 * @return A <CODE>PathIterator</CODE> that defines the arc's boundary.
	 */
	public PathIterator getPathIterator(AffineTransform at) {
		return new ArcIterator(this, at);
	}

	/**
	 * Returns the starting point of the arc. This point is the intersection of
	 * the ray from the center defined by the starting angle and the elliptical
	 * boundary of the arc.
	 *
	 * @return A <CODE>Point2D</CODE> object representing the x,y coordinates of
	 *         the starting point of the arc.
	 */
	public Point2D getStartPoint() {
		double angle = Math.toRadians(-getAngleStart());
		double x = getX() + (Math.cos(angle) * 0.5 + 0.5) * getWidth();
		double y = getY() + (Math.sin(angle) * 0.5 + 0.5) * getHeight();
		return new Point2D.Double(x, y);
	}

	/**
	 * Determines whether or not the interior of the arc intersects the interior
	 * of the specified rectangle.
	 *
	 * @param x
	 *            ,&nbsp;y The coordinates of the rectangle's upper left corner.
	 *            (Specified in double precision.)
	 * @param w
	 *            The width of the rectangle. (Specified in double precision.)
	 * @param h
	 *            The height of the rectangle. (Specified in double precision.)
	 *
	 * @return <CODE>true</CODE> if the arc intersects the rectangle,
	 *         <CODE>false</CODE> if the arc doesn't intersect the rectangle.
	 */

	public boolean intersects(double x, double y, double w, double h) {

		double aw = getWidth();
		double ah = getHeight();

		if (w <= 0 || h <= 0 || aw <= 0 || ah <= 0) {
			return false;
		}
		double ext = getAngleExtent();
		if (ext == 0) {
			return false;
		}

		double ax = getX();
		double ay = getY();
		double axw = ax + aw;
		double ayh = ay + ah;
		double xw = x + w;
		double yh = y + h;

		// check bbox
		if (x >= axw || y >= ayh || xw <= ax || yh <= ay) {
			return false;
		}

		// extract necessary data
		double axc = getCenterX();
		double ayc = getCenterY();
		Point2D sp = getStartPoint();
		Point2D ep = getEndPoint();
		double sx = sp.getX();
		double sy = sp.getY();
		double ex = ep.getX();
		double ey = ep.getY();

		/*
		 * Try to catch rectangles that intersect arc in areas outside of
		 * rectagle with left top corner coordinates (min(center x, start point
		 * x, end point x), min(center y, start point y, end point y)) and rigth
		 * bottom corner coordinates (max(center x, start point x, end point x),
		 * max(center y, start point y, end point y)). So we'll check axis
		 * segments outside of rectangle above.
		 */
		if (ayc >= y && ayc <= yh) { // 0 and 180
			if ((sx < xw && ex < xw && axc < xw && axw > x && containsAngle(0))
					|| (sx > x && ex > x && axc > x && ax < xw && containsAngle(180))) {
				return true;
			}
		}
		if (axc >= x && axc <= xw) { // 90 and 270
			if ((sy > y && ey > y && ayc > y && ay < yh && containsAngle(90))
					|| (sy < yh && ey < yh && ayc < yh && ayh > y && containsAngle(270))) {
				return true;
			}
		}

		/*
		 * For PIE we should check intersection with pie slices; also we should
		 * do the same for arcs with extent is greater than 180, because we
		 * should cover case of rectangle, which situated between center of arc
		 * and chord, but does not intersect the chord.
		 */
		Rectangle2D rect = new Rectangle2D.Double(x, y, w, h);
		if (type == PIE || Math.abs(ext) > 180) {
			// for PIE: try to find intersections with pie slices
			if (rect.intersectsLine(axc, ayc, sx, sy)
					|| rect.intersectsLine(axc, ayc, ex, ey)) {
				return true;
			}
		} else {
			// for CHORD and OPEN: try to find intersections with chord
			if (rect.intersectsLine(sx, sy, ex, ey)) {
				return true;
			}
		}

		// finally check the rectangle corners inside the arc
		if (contains(x, y) || contains(x + w, y) || contains(x, y + h)
				|| contains(x + w, y + h)) {
			return true;
		}

		return false;
	}

	/**
	 * Sets the angular extent of this arc to the specified double value.
	 *
	 * @param angExt
	 *            The angular extent of the arc in degrees.
	 * @see #getAngleExtent
	 */
	public abstract void setAngleExtent(double angExt);

	/**
	 * Sets the starting angle and angular extent of this arc using two sets of
	 * coordinates. The first set of coordinates is used to determine the angle
	 * of the starting point relative to the arc's center. The second set of
	 * coordinates is used to determine the angle of the end point relative to
	 * the arc's center. The arc will always be non-empty and extend
	 * counterclockwise from the first point around to the second point.
	 *
	 * @param x1
	 *            ,&nbsp;y1 The coordinates of the arc's starting point.
	 * @param x2
	 *            ,&nbsp;y2 The coordinates of the arc's ending point.
	 */
	public void setAngles(double x1, double y1, double x2, double y2) {
		double x = getCenterX();
		double y = getCenterY();
		double w = getWidth();
		double h = getHeight();
		// Note: reversing the Y equations negates the angle to adjust
		// for the upside down coordinate system.
		// Also we should bias atans by the height and width of the oval.
		double ang1 = Math.atan2(w * (y - y1), h * (x1 - x));
		double ang2 = Math.atan2(w * (y - y2), h * (x2 - x));
		ang2 -= ang1;
		if (ang2 <= 0.0) {
			ang2 += Math.PI * 2.0;
		}
		setAngleStart(Math.toDegrees(ang1));
		setAngleExtent(Math.toDegrees(ang2));
	}

	/**
	 * Sets the starting angle and angular extent of this arc using two points.
	 * The first point is used to determine the angle of the starting point
	 * relative to the arc's center. The second point is used to determine the
	 * angle of the end point relative to the arc's center. The arc will always
	 * be non-empty and extend counterclockwise from the first point around to
	 * the second point.
	 *
	 * @param p1
	 *            The <CODE>Point2D</CODE> that defines the arc's starting
	 *            point.
	 * @param p2
	 *            The <CODE>Point2D</CODE> that defines the arc's ending point.
	 */
	public void setAngles(Point2D p1, Point2D p2) {
		setAngles(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}

	/**
	 * Sets the starting angle of this arc to the specified double value.
	 *
	 * @param angSt
	 *            The starting angle of the arc in degrees.
	 * @see #getAngleStart
	 */
	public abstract void setAngleStart(double angSt);

	/**
	 * Sets the starting angle of this arc to the angle that the specified point
	 * defines relative to the center of this arc. The angular extent of the arc
	 * will remain the same.
	 *
	 * @param p
	 *            The <CODE>Point2D</CODE> that defines the starting angle.
	 * @see #getAngleStart
	 */
	public void setAngleStart(Point2D p) {
		// Bias the dx and dy by the height and width of the oval.
		double dx = getHeight() * (p.getX() - getCenterX());
		double dy = getWidth() * (p.getY() - getCenterY());
		setAngleStart(-Math.toDegrees(Math.atan2(dy, dx)));
	}

	/**
	 * Sets this arc to be the same as the specified arc.
	 *
	 * @param a
	 *            The <CODE>Arc2D</CODE> to use to set the arc's values.
	 */
	public void setArc(Arc2D a) {
		setArc(a.getX(), a.getY(), a.getWidth(), a.getHeight(), a
				.getAngleStart(), a.getAngleExtent(), a.type);
	}

	/**
	 * Sets the location, size, angular extents, and closure type of this arc to
	 * the specified double values.
	 *
	 * @param x
	 *            ,&nbsp;y The coordinates of the upper left corner of the arc.
	 * @param w
	 *            The overall width of the full ellipse of which this arc is a
	 *            partial section.
	 * @param h
	 *            The overall height of the full ellipse of which this arc is a
	 *            partial section.
	 * @param angSt
	 *            The starting angle of the arc in degrees.
	 * @param angExt
	 *            The angular extent of the arc in degrees.
	 * @param closure
	 *            The closure type for the arc: {@link #OPEN OPEN},
	 *            {@link #CHORD CHORD}, or {@link #PIE PIE}.
	 */
	public abstract void setArc(double x, double y, double w, double h,
			double angSt, double angExt, int closure);

	/**
	 * Sets the location, size, angular extents, and closure type of this arc to
	 * the specified values.
	 *
	 * @param loc
	 *            The <CODE>Point2D</CODE> representing the coordinates of the
	 *            upper left corner of the arc.
	 * @param size
	 *            The <CODE>Dimension2D</CODE> representing the width and height
	 *            of the full ellipse of which this arc is a partial section.
	 * @param angSt
	 *            The starting angle of the arc in degrees. (Specified in double
	 *            precision.)
	 * @param angExt
	 *            The angular extent of the arc in degrees. (Specified in double
	 *            precision.)
	 * @param closure
	 *            The closure type for the arc: {@link #OPEN OPEN},
	 *            {@link #CHORD CHORD}, or {@link #PIE PIE}.
	 */
	public void setArc(Point2D loc, Dimension2D size, double angSt,
			double angExt, int closure) {
		setArc(loc.getX(), loc.getY(), size.getWidth(), size.getHeight(),
				angSt, angExt, closure);
	}

	/**
	 * Sets the location, size, angular extents, and closure type of this arc to
	 * the specified values.
	 *
	 * @param rect
	 *            The bounding rectangle that defines the outer boundary of the
	 *            full ellipse of which this arc is a partial section.
	 * @param angSt
	 *            The starting angle of the arc in degrees. (Specified in double
	 *            precision.)
	 * @param angExt
	 *            The angular extent of the arc in degrees. (Specified in double
	 *            precision.)
	 * @param closure
	 *            The closure type for the arc: {@link #OPEN OPEN},
	 *            {@link #CHORD CHORD}, or {@link #PIE PIE}.
	 */
	public void setArc(Rectangle2D rect, double angSt, double angExt,
			int closure) {
		setArc(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(),
				angSt, angExt, closure);
	}

	/**
	 * Sets the position, bounds, angular extents, and closure type of this arc
	 * to the specified values. The arc is defined by a center point and a
	 * radius rather than a bounding box for the full ellipse.
	 *
	 * @param x
	 *            ,&nbsp;y The coordinates of the center of the arc. (Specified
	 *            in double precision.)
	 * @param radius
	 *            The radius of the arc. (Specified in double precision.)
	 * @param angSt
	 *            The starting angle of the arc in degrees. (Specified in double
	 *            precision.)
	 * @param angExt
	 *            The angular extent of the arc in degrees. (Specified in double
	 *            precision.)
	 * @param closure
	 *            The closure type for the arc: {@link #OPEN OPEN},
	 *            {@link #CHORD CHORD}, or {@link #PIE PIE}.
	 */
	public void setArcByCenter(double x, double y, double radius, double angSt,
			double angExt, int closure) {
		setArc(x - radius, y - radius, radius * 2.0, radius * 2.0, angSt,
				angExt, closure);
	}

	/**
	 * Sets the position, bounds, and angular extents of this arc to the
	 * specified value. The starting angle of the arc is tangent to the line
	 * specified by points (p1, p2), the ending angle is tangent to the line
	 * specified by points (p2, p3), and the arc has the specified radius.
	 *
	 * @param p1
	 *            The first point that defines the arc. The starting angle of
	 *            the arc is tangent to the line specified by points (p1, p2).
	 * @param p2
	 *            The second point that defines the arc. The starting angle of
	 *            the arc is tangent to the line specified by points (p1, p2).
	 *            The ending angle of the arc is tangent to the line specified
	 *            by points (p2, p3).
	 * @param p3
	 *            The third point that defines the arc. The ending angle of the
	 *            arc is tangent to the line specified by points (p2, p3).
	 * @param radius
	 *            The radius of the arc. (Specified in double precision.)
	 */
	public void setArcByTangent(Point2D p1, Point2D p2, Point2D p3,
			double radius) {
		double ang1 = Math.atan2(p1.getY() - p2.getY(), p1.getX() - p2.getX());
		double ang2 = Math.atan2(p3.getY() - p2.getY(), p3.getX() - p2.getX());
		double diff = ang2 - ang1;
		if (diff > Math.PI) {
			ang2 -= Math.PI * 2.0;
		} else if (diff < -Math.PI) {
			ang2 += Math.PI * 2.0;
		}
		double bisect = (ang1 + ang2) / 2.0;
		double theta = Math.abs(ang2 - bisect);
		double dist = radius / Math.sin(theta);
		double x = p2.getX() + dist * Math.cos(bisect);
		double y = p2.getY() + dist * Math.sin(bisect);
		// REMIND: This needs some work...
		if (ang1 < ang2) {
			ang1 -= Math.PI / 2.0;
			ang2 += Math.PI / 2.0;
		} else {
			ang1 += Math.PI / 2.0;
			ang2 -= Math.PI / 2.0;
		}
		ang1 = Math.toDegrees(-ang1);
		ang2 = Math.toDegrees(-ang2);
		diff = ang2 - ang1;
		if (diff < 0) {
			diff += 360;
		} else {
			diff -= 360;
		}
		setArcByCenter(x, y, radius, ang1, diff, type);
	}

	/**
	 * Sets the closure type of this arc to the specified value:
	 * <CODE>OPEN</CODE>, <CODE>CHORD</CODE>, or <CODE>PIE</CODE>.
	 *
	 * @param type
	 *            The integer constant that represents the closure type of this
	 *            arc: {@link #OPEN}, {@link #CHORD}, or {@link #PIE}.
	 *
	 * @throws IllegalArgumentException
	 *             if <code>type</code> is not 0, 1, or 2.+
	 * @see #getArcType
	 */
	public void setArcType(int type) {
		if (type < OPEN || type > PIE) {
			throw new IllegalArgumentException("invalid type for Arc: " + type);
		}
		this.type = type;
	}

	/**
	 * Sets the location and size of the outer bounds of this arc to the
	 * specified values.
	 *
	 * @param x
	 *            ,&nbsp;y The coordinates of the upper left corner of the arc's
	 *            bounding box. (Specified in double precision.)
	 * @param w
	 *            The width of the arc's bounding box. (Specified in double
	 *            precision.)
	 * @param h
	 *            The height of the arc's bounding box. (Specified in double
	 *            precision.)
	 */
	public void setFrame(double x, double y, double w, double h) {
		setArc(x, y, w, h, getAngleStart(), getAngleExtent(), type);
	}

	private boolean contains(double x, double y, double w, double h,
			Rectangle2D origrect) {
		if (!(contains(x, y) && contains(x + w, y) && contains(x, y + h) && contains(
				x + w, y + h))) {
			return false;
		}
		// If the shape is convex then we have done all the testing
		// we need. Only PIE arcs can be concave and then only if
		// the angular extents are greater than 180 degrees.
		if (type != PIE || Math.abs(getAngleExtent()) <= 180.0) {
			return true;
		}
		// For a PIE shape we have an additional test for the case where
		// the angular extents are greater than 180 degrees and all four
		// rectangular corners are inside the shape but one of the
		// rectangle edges spans across the "missing wedge" of the arc.
		// We can test for this case by checking if the rectangle intersects
		// either of the pie angle segments.
		if (origrect == null) {
			origrect = new Rectangle2D.Double(x, y, w, h);
		}
		double halfW = getWidth() / 2.0;
		double halfH = getHeight() / 2.0;
		double xc = getX() + halfW;
		double yc = getY() + halfH;
		double angle = Math.toRadians(-getAngleStart());
		double xe = xc + halfW * Math.cos(angle);
		double ye = yc + halfH * Math.sin(angle);
		if (origrect.intersectsLine(xc, yc, xe, ye)) {
			return false;
		}
		angle += Math.toRadians(-getAngleExtent());
		xe = xc + halfW * Math.cos(angle);
		ye = yc + halfH * Math.sin(angle);
		return !origrect.intersectsLine(xc, yc, xe, ye);
	}

	/**
	 * Constructs a <code>Rectangle2D</code> of the appropriate precision to
	 * hold the parameters calculated to be the bounding box of this arc.
	 *
	 * @param x
	 *            ,&nbsp;y The coordinates of the upper left corner of the
	 *            bounding box. (Specified in double precision.)
	 * @param w
	 *            The width of the bounding box. (Specified in double
	 *            precision.)
	 * @param h
	 *            The height of the bounding box. (Specified in double
	 *            precision.)
	 * @return a <code>Rectangle2D</code> that is the bounding box of this arc.
	 */
	protected abstract Rectangle2D makeBounds(double x, double y, double w,
			double h);
}
