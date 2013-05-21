/*
 * @(#)Ellipse2D.java	1.16 03/12/19
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
 * The <code>Ellipse2D</code> class describes an ellipse that is defined by a
 * bounding rectangle.
 * <p>
 * This class is only the abstract superclass for all objects which store a 2D
 * ellipse. The actual storage representation of the coordinates is left to the
 * subclass.
 *
 * @version 1.16, 12/19/03
 * @author Jim Graham
 */
public abstract class Ellipse2D extends RectangularShape {
	/**
	 * The <code>Float</code> class defines an ellipse specified in
	 * <code>float</code> precision.
	 */
	public static class Float extends Ellipse2D {
		/**
		 * The x coordinate of the upper left corner of this
		 * <code>Ellipse2D</code>.
		 */
		public float x;

		/**
		 * The y coordinate of the upper left corner of this
		 * <code>Ellipse2D</code>.
		 */
		public float y;

		/**
		 * The overall width of this <code>Ellipse2D</code>.
		 */
		public float width;

		/**
		 * The overall height of this <code>Ellipse2D</code>.
		 */
		public float height;

		/**
		 * Constructs a new <code>Ellipse2D</code>, initialized to location
		 * (0,&nbsp;0) and size (0,&nbsp;0).
		 */
		public Float() {
		}

		/**
		 * Constructs and initializes an <code>Ellipse2D</code> from the
		 * specified coordinates.
		 *
		 * @param x
		 *            ,&nbsp;y the coordinates of the bounding rectangle
		 * @param w
		 *            the width of the bounding rectangle
		 * @param h
		 *            the height of the bounding rectangle
		 */
		public Float(float x, float y, float w, float h) {
			setFrame(x, y, w, h);
		}

		/**
		 * Returns the X coordinate of the upper left corner of this
		 * <code>Ellipse2D</code> in <code>double</code> precision.
		 *
		 * @return the X coordinate of the upper left corner of the bounding
		 *         rectangle of this <code>Ellipse2D</code>.
		 */
		public double getX() {
			return (double) x;
		}

		/**
		 * Returns the Y coordinate of the upper left corner of this
		 * <code>Ellipse2D</code> in <code>double</code> precision.
		 *
		 * @return the Y coordinate of the upper left corner of the bounding
		 *         rectangle of this <code>Ellipse2D</code>.
		 */
		public double getY() {
			return (double) y;
		}

		/**
		 * Returns the overall width of this <code>Ellipse2D</code> in
		 * <code>double</code> precision.
		 *
		 * @return the width of this <code>Ellipse2D</code>.
		 */
		public double getWidth() {
			return (double) width;
		}

		/**
		 * Returns the overall height of this <code>Ellipse2D</code> in
		 * <code>double</code> precision.
		 *
		 * @return the height of this <code>Ellipse2D</code>.
		 */
		public double getHeight() {
			return (double) height;
		}

		/**
		 * Determines whether or not the bounding box of this
		 * <code>Ellipse2D</code> is empty.
		 *
		 * @return <code>true</code> if the bounding rectangle of this
		 *         <code>Ellipse2D</code> is empty; <code>false</code>
		 *         otherwise.
		 */
		public boolean isEmpty() {
			return (width <= 0.0 || height <= 0.0);
		}

		/**
		 * Sets the location and size of this <code>Ellipse2D</code> to the
		 * specified <code>float</code> values.
		 *
		 * @param x
		 *            ,&nbsp;y the specified coordinates to which to set the
		 *            location of the bounding box of this
		 *            <code>Ellipse2D</code>
		 * @param w
		 *            the specified width to which to set the width of this
		 *            <code>Ellipse2D</code>
		 * @param h
		 *            the specified height to which to set the height of the
		 *            <code>Ellipse2D</code>
		 */
		public void setFrame(float x, float y, float w, float h) {
			this.x = x;
			this.y = y;
			this.width = w;
			this.height = h;
		}

		/**
		 * Sets the location and size of this <code>Ellipse2D</code> to the
		 * specified <code>double</code> values.
		 *
		 * @param x
		 *            ,&nbsp;y the specified coordinates to which to set the
		 *            location of the bounding box of this
		 *            <code>Ellipse2D</code>
		 * @param w
		 *            the specified width to which to set the width of this
		 *            <code>Ellipse2D</code>
		 * @param h
		 *            the specified height to which to set the height of this
		 *            <code>Ellipse2D</code>
		 */
		public void setFrame(double x, double y, double w, double h) {
			this.x = (float) x;
			this.y = (float) y;
			this.width = (float) w;
			this.height = (float) h;
		}

		/**
		 * Returns the high precision bounding box of this
		 * <code>Ellipse2D</code>.
		 *
		 * @return a {@link Rectangle2D} that is the bounding box of this
		 *         <code>Ellipse2D</code>.
		 */
		public Rectangle2D getBounds2D() {
			return new Rectangle2D.Float(x, y, width, height);
		}
	}

	/**
	 * The <code>Double</code> class defines an ellipse specified in
	 * <code>double</code> precision.
	 */
	public static class Double extends Ellipse2D {
		/**
		 * The x coordinate of the upper left corner of this
		 * <code>Ellipse2D</code>.
		 */
		public double x;

		/**
		 * The y coordinate of the upper left corner of this
		 * <code>Ellipse2D</code>.
		 */
		public double y;

		/**
		 * The overall width of this <code>Ellipse2D</code>.
		 */
		public double width;

		/**
		 * The overall height of the <code>Ellipse2D</code>.
		 */
		public double height;

		/**
		 * Constructs a new <code>Ellipse2D</code>, initialized to location
		 * (0,&nbsp;0) and size (0,&nbsp;0).
		 */
		public Double() {
		}

		/**
		 * Constructs and initializes an <code>Ellipse2D</code> from the
		 * specified coordinates.
		 *
		 * @param x
		 *            ,&nbsp;y the coordinates of the bounding rectangle
		 * @param w
		 *            the width of the rectangle
		 * @param h
		 *            the height of the rectangle
		 */
		public Double(double x, double y, double w, double h) {
			setFrame(x, y, w, h);
		}

		/**
		 * Returns the X coordinate of the upper left corner of this
		 * <code>Ellipse2D</code> in <code>double</code> precision.
		 *
		 * @return the X coordinate of the upper left corner of the bounding box
		 *         of this <code>Ellipse2D</code>.
		 */
		public double getX() {
			return x;
		}

		/**
		 * Returns the Y coordinate of the upper left corner of this
		 * <code>Ellipse2D</code> in <code>double</code> precision.
		 *
		 * @return the Y coordinate of the upper left corner of the bounding box
		 *         of this <code>Ellipse2D</code>.
		 */
		public double getY() {
			return y;
		}

		/**
		 * Returns the overall width of this <code>Ellipse2D</code> in
		 * <code>double</code> precision.
		 *
		 * @return the width of this <code>Ellipse2D</code>.
		 */
		public double getWidth() {
			return width;
		}

		/**
		 * Returns the overall height of this <code>Ellipse2D</code> in
		 * <code>double</code> precision.
		 *
		 * @return the height of this <code>Ellipse2D</code>.
		 */
		public double getHeight() {
			return height;
		}

		/**
		 * Determines whether or not the bounding box of this
		 * <code>Ellipse2D</code> is empty.
		 *
		 * @return <code>true</code> if the bounding box of this
		 *         <code>Ellipse2D</code> is empty; <code>false</code>
		 *         otherwise.
		 */
		public boolean isEmpty() {
			return (width <= 0.0 || height <= 0.0);
		}

		/**
		 * Sets the location and size of this <code>Ellipse2D</code> to the
		 * specified <code>double</code> values.
		 *
		 * @param x
		 *            ,&nbsp;y the specified coordinates to which to set the
		 *            location of the bounding box of this
		 *            <code>Ellipse2D</code>
		 * @param w
		 *            the width to which to set the width of this
		 *            <code>Ellipse2D</code>
		 * @param h
		 *            the height to which to set the height of this
		 *            <code>Ellipse2D</code>
		 */
		public void setFrame(double x, double y, double w, double h) {
			this.x = x;
			this.y = y;
			this.width = w;
			this.height = h;
		}

		/**
		 * Returns the high precision bounding box of this
		 * <code>Ellipse2D</code>.
		 *
		 * @return a <code>Rectangle2D</code> that is the bounding box of this
		 *         <code>Ellipse2D</code>.
		 */
		public Rectangle2D getBounds2D() {
			return new Rectangle2D.Double(x, y, width, height);
		}
	}

	/**
	 * This is an abstract class that cannot be instantiated directly.
	 * Type-specific implementation subclasses are available for instantiation
	 * and provide a number of formats for storing the information necessary to
	 * satisfy the various accessor methods below.
	 *
	 * @see java.awt.geom.Ellipse2D.Float
	 * @see java.awt.geom.Ellipse2D.Double
	 */
	protected Ellipse2D() {
	}

	/**
	 * Tests if a specified point is inside the boundary of this
	 * <code>Ellipse2D</code>.
	 *
	 * @param x
	 *            ,&nbsp;y the coordinates to test
	 * @return <code>true</code> if the specified point is contained in this
	 *         ellipse; <code>false</code> otherwise.
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
		return (normx * normx + normy * normy) < 0.25;
	}

	/**
	 * Tests if the interior of this <code>Ellipse2D</code> intersects the
	 * interior of a specified rectangular area.
	 *
	 * @param x
	 *            ,&nbsp;y the coordinates of the upper left corner of the
	 *            specified rectangular area
	 * @param w
	 *            the width of the specified rectangular area
	 * @param h
	 *            the height of the specified rectangluar area
	 * @return <code>true</code> if this <code>Ellipse2D</code> contains the
	 *         specified rectangular area; <code>false</code> otherwise.
	 */
	public boolean intersects(double x, double y, double w, double h) {
		if (w <= 0.0 || h <= 0.0) {
			return false;
		}
		// Normalize the rectangular coordinates compared to the ellipse
		// having a center at 0,0 and a radius of 0.5.
		double ellw = getWidth();
		if (ellw <= 0.0) {
			return false;
		}
		double normx0 = (x - getX()) / ellw - 0.5;
		double normx1 = normx0 + w / ellw;
		double ellh = getHeight();
		if (ellh <= 0.0) {
			return false;
		}
		double normy0 = (y - getY()) / ellh - 0.5;
		double normy1 = normy0 + h / ellh;
		// find nearest x (left edge, right edge, 0.0)
		// find nearest y (top edge, bottom edge, 0.0)
		// if nearest x,y is inside circle of radius 0.5, then intersects
		double nearx, neary;
		if (normx0 > 0.0) {
			// center to left of X extents
			nearx = normx0;
		} else if (normx1 < 0.0) {
			// center to right of X extents
			nearx = normx1;
		} else {
			nearx = 0.0;
		}
		if (normy0 > 0.0) {
			// center above Y extents
			neary = normy0;
		} else if (normy1 < 0.0) {
			// center below Y extents
			neary = normy1;
		} else {
			neary = 0.0;
		}
		return (nearx * nearx + neary * neary) < 0.25;
	}

	/**
	 * Tests if the interior of this <code>Ellipse2D</code> entirely contains
	 * the specified rectangular area.
	 *
	 * @param x
	 *            ,&nbsp;y the coordinates of the upper left corner of the
	 *            specified rectangular area
	 * @param w
	 *            the width of the specified rectangular area
	 * @param h
	 *            the height of the specified rectangular area
	 * @return <code>true</code> if this <code>Ellipse2D</code> contains the
	 *         specified rectangular area; <code>false</code> otherwise.
	 */
	public boolean contains(double x, double y, double w, double h) {
		return (contains(x, y) && contains(x + w, y) && contains(x, y + h) && contains(
				x + w, y + h));
	}

	/**
	 * Returns an iteration object that defines the boundary of this
	 * <code>Ellipse2D</code>. The iterator for this class is multi-threaded
	 * safe, which means that this <code>Ellipse2D</code> class guarantees that
	 * modifications to the geometry of this <code>Ellipse2D</code> object do
	 * not affect any iterations of that geometry that are already in process.
	 *
	 * @param at
	 *            an optional <code>AffineTransform</code> to be applied to the
	 *            coordinates as they are returned in the iteration, or
	 *            <code>null</code> if untransformed coordinates are desired
	 * @return the <code>PathIterator</code> object that returns the geometry of
	 *         the outline of this <code>Ellipse2D</code>, one segment at a
	 *         time.
	 */
	public PathIterator getPathIterator(AffineTransform at) {
		return new EllipseIterator(this, at);
	}
}
