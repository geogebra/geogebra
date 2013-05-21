/*
 * @(#)GeneralPath.java	1.59 03/12/19
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

import geogebra.html5.awt.GRectangleW;
import geogebra.html5.kernel.external.Crossings;
import geogebra.html5.kernel.external.Curve;

/**
 * The <code>GeneralPath</code> class represents a geometric path constructed
 * from straight lines, and quadratic and cubic (B&eacute;zier) curves. It can
 * contain multiple subpaths.
 * <p>
 * The winding rule specifies how the interior of a path is determined. There
 * are two types of winding rules: EVEN_ODD and NON_ZERO.
 * <p>
 * An EVEN_ODD winding rule means that enclosed regions of the path alternate
 * between interior and exterior areas as traversed from the outside of the path
 * towards a point inside the region.
 * <p>
 * A NON_ZERO winding rule means that if a ray is drawn in any direction from a
 * given point to infinity and the places where the path intersects the ray are
 * examined, the point is inside of the path if and only if the number of times
 * that the path crosses the ray from left to right does not equal the number of
 * times that the path crosses the ray from right to left.
 *
 * @version 1.59, 12/19/03
 * @author Jim Graham
 */
public final class GeneralPath implements Shape, Cloneable {
	/**
	 * An even-odd winding rule for determining the interior of a path.
	 */
	public static final int WIND_EVEN_ODD = PathIterator.WIND_EVEN_ODD;

	/**
	 * A non-zero winding rule for determining the interior of a path.
	 */
	public static final int WIND_NON_ZERO = PathIterator.WIND_NON_ZERO;

	// For code simplicity, copy these constants to our namespace
	// and cast them to byte constants for easy storage.
	private static final byte SEG_MOVETO = (byte) PathIterator.SEG_MOVETO;
	private static final byte SEG_LINETO = (byte) PathIterator.SEG_LINETO;
	private static final byte SEG_QUADTO = (byte) PathIterator.SEG_QUADTO;
	private static final byte SEG_CUBICTO = (byte) PathIterator.SEG_CUBICTO;
	private static final byte SEG_CLOSE = (byte) PathIterator.SEG_CLOSE;

	byte[] pointTypes;
	float[] pointCoords;
	int numTypes;
	int numCoords;
	int windingRule;

	static final int INIT_SIZE = 20;
	static final int EXPAND_MAX = 500;

	/**
	 * Constructs a new <code>GeneralPath</code> object. If an operation
	 * performed on this path requires the interior of the path to be defined
	 * then the default NON_ZERO winding rule is used.
	 *
	 * @see #WIND_NON_ZERO
	 */
	public GeneralPath() {
		this(WIND_NON_ZERO, INIT_SIZE, INIT_SIZE);
	}

	/**
	 * Constructs a new <code>GeneralPath</code> object with the specified
	 * winding rule to control operations that require the interior of the path
	 * to be defined.
	 *
	 * @param rule
	 *            the winding rule
	 * @see #WIND_EVEN_ODD
	 * @see #WIND_NON_ZERO
	 */
	public GeneralPath(int rule) {
		this(rule, INIT_SIZE, INIT_SIZE);
	}

	/**
	 * Constructs a new <code>GeneralPath</code> object with the specified
	 * winding rule and the specified initial capacity to store path
	 * coordinates. This number is an initial guess as to how many path segments
	 * are in the path, but the storage is expanded as needed to store whatever
	 * path segments are added to this path.
	 *
	 * @param rule
	 *            the winding rule
	 * @param initialCapacity
	 *            the estimate for the number of path segments in the path
	 * @see #WIND_EVEN_ODD
	 * @see #WIND_NON_ZERO
	 */
	public GeneralPath(int rule, int initialCapacity) {
		this(rule, initialCapacity, initialCapacity);
	}

	/**
	 * Constructs a new <code>GeneralPath</code> object from an arbitrary
	 * {@link Shape} object. All of the initial geometry and the winding rule
	 * for this path are taken from the specified <code>Shape</code> object.
	 *
	 * @param s
	 *            the specified <code>Shape</code> object
	 */
	public GeneralPath(Shape s) {
		this(WIND_NON_ZERO, INIT_SIZE, INIT_SIZE);
		PathIterator pi = s.getPathIterator(null);
		setWindingRule(pi.getWindingRule());
		append(pi, false);
	}

	GeneralPath(int windingRule, byte[] pointTypes, int numTypes,
			float[] pointCoords, int numCoords) {

		// used to construct from native

		this.windingRule = windingRule;
		this.pointTypes = pointTypes;
		this.numTypes = numTypes;
		this.pointCoords = pointCoords;
		this.numCoords = numCoords;
	}

	/**
	 * Constructs a new <code>GeneralPath</code> object with the specified
	 * winding rule and the specified initial capacities to store point types
	 * and coordinates. These numbers are an initial guess as to how many path
	 * segments and how many points are to be in the path, but the storage is
	 * expanded as needed to store whatever path segments are added to this
	 * path.
	 *
	 * @param rule
	 *            the winding rule
	 * @param initialTypes
	 *            the estimate for the number of path segments in the path
	 * @param initialCapacity
	 *            the estimate for the number of points
	 * @see #WIND_EVEN_ODD
	 * @see #WIND_NON_ZERO
	 */
	GeneralPath(int rule, int initialTypes, int initialCoords) {
		setWindingRule(rule);
		pointTypes = new byte[initialTypes];
		pointCoords = new float[initialCoords * 2];
	}

	/**
	 * Appends the geometry of the specified {@link PathIterator} object to the
	 * path, possibly connecting the new geometry to the existing path segments
	 * with a line segment. If the <code>connect</code> parameter is
	 * <code>true</code> and the path is not empty then any initial
	 * <code>moveTo</code> in the geometry of the appended <code>Shape</code> is
	 * turned into a <code>lineTo</code> segment. If the destination coordinates
	 * of such a connecting <code>lineTo</code> segment match the ending
	 * coordinates of a currently open subpath then the segment is omitted as
	 * superfluous. The winding rule of the specified <code>Shape</code> is
	 * ignored and the appended geometry is governed by the winding rule
	 * specified for this path.
	 *
	 * @param pi
	 *            the <code>PathIterator</code> whose geometry is appended to
	 *            this path
	 * @param connect
	 *            a boolean to control whether or not to turn an initial
	 *            <code>moveTo</code> segment into a <code>lineTo</code> segment
	 *            to connect the new geometry to the existing path
	 */
	public void append(PathIterator pi, boolean connect) {
		float coords[] = new float[6];
		while (!pi.isDone()) {
			switch (pi.currentSegment(coords)) {
			case SEG_MOVETO:
				if (!connect || numTypes < 1 || numCoords < 2) {
					moveTo(coords[0], coords[1]);
					break;
				}
				if (pointTypes[numTypes - 1] != SEG_CLOSE
						&& pointCoords[numCoords - 2] == coords[0]
						&& pointCoords[numCoords - 1] == coords[1]) {
					// Collapse out initial moveto/lineto
					break;
				}
				// NO BREAK;
			case SEG_LINETO:
				lineTo(coords[0], coords[1]);
				break;
			case SEG_QUADTO:
				quadTo(coords[0], coords[1], coords[2], coords[3]);
				break;
			case SEG_CUBICTO:
				curveTo(coords[0], coords[1], coords[2], coords[3], coords[4],
						coords[5]);
				break;
			case SEG_CLOSE:
				closePath();
				break;
			}
			pi.next();
			connect = false;
		}
	}

	/**
	 * Appends the geometry of the specified <code>Shape</code> object to the
	 * path, possibly connecting the new geometry to the existing path segments
	 * with a line segment. If the <code>connect</code> parameter is
	 * <code>true</code> and the path is not empty then any initial
	 * <code>moveTo</code> in the geometry of the appended <code>Shape</code> is
	 * turned into a <code>lineTo</code> segment. If the destination coordinates
	 * of such a connecting <code>lineTo</code> segment match the ending
	 * coordinates of a currently open subpath then the segment is omitted as
	 * superfluous. The winding rule of the specified <code>Shape</code> is
	 * ignored and the appended geometry is governed by the winding rule
	 * specified for this path.
	 *
	 * @param s
	 *            the <code>Shape</code> whose geometry is appended to this path
	 * @param connect
	 *            a boolean to control whether or not to turn an initial
	 *            <code>moveTo</code> segment into a <code>lineTo</code> segment
	 *            to connect the new geometry to the existing path
	 */
	public void append(Shape s, boolean connect) {
		PathIterator pi = s.getPathIterator(null);
		append(pi, connect);
	}

	/**
	 * Creates a new object of the same class as this object.
	 *
	 * @return a clone of this instance.
	 * @exception OutOfMemoryError
	 *                if there is not enough memory.
	 * @see java.lang.Cloneable
	 * @since 1.2
	 */
	public Object clone() {
		GeneralPath clone = new GeneralPath(this);
		return clone;

		/*
		 * try { GeneralPath copy = (GeneralPath) super.clone(); copy.pointTypes
		 * = (byte[]) pointTypes.clone(); copy.pointCoords = (float[])
		 * pointCoords.clone(); return copy; } catch (Exception e) { // this
		 * shouldn't happen, since we are Cloneable throw new
		 * awt.client.exception.InternalError(); }
		 */
	}



	/**
	 * Closes the current subpath by drawing a straight line back to the
	 * coordinates of the last <code>moveTo</code>. If the path is already
	 * closed then this method has no effect.
	 */
	public void closePath() {
		if (numTypes == 0 || pointTypes[numTypes - 1] != SEG_CLOSE) {
			needRoom(1, 0, true);
			pointTypes[numTypes++] = SEG_CLOSE;
		}
	}

	/**
	 * Tests if the specified coordinates are inside the boundary of this
	 * <code>Shape</code>.
	 *
	 * @param x
	 *            ,&nbsp;y the specified coordinates
	 * @return <code>true</code> if the specified coordinates are inside this
	 *         <code>Shape</code>; <code>false</code> otherwise
	 */
	public boolean contains(double x, double y) {
		if (numTypes < 2) {
			return false;
		}
		int cross = Curve.crossingsForPath(getPathIterator(null), x, y);
		if (windingRule == WIND_NON_ZERO) {
			return (cross != 0);
		} else {
			return ((cross & 1) != 0);
		}
	}

	/**
	 * Tests if the specified rectangular area is inside the boundary of this
	 * <code>Shape</code>.
	 *
	 * @param x
	 *            ,&nbsp;y the specified coordinates
	 * @param w
	 *            the width of the specified rectangular area
	 * @param h
	 *            the height of the specified rectangular area
	 * @return <code>true</code> if this <code>Shape</code> contains the
	 *         specified rectangluar area; <code>false</code> otherwise.
	 */
	public boolean contains(double x, double y, double w, double h) {
		Crossings c = Crossings.findCrossings(getPathIterator(null), x, y, x
				+ w, y + h);
		return (c != null && c.covers(y, y + h));
	}

	/**
	 * Tests if the specified <code>Point2D</code> is inside the boundary of
	 * this <code>Shape</code>.
	 *
	 * @param p
	 *            the specified <code>Point2D</code>
	 * @return <code>true</code> if this <code>Shape</code> contains the
	 *         specified <code>Point2D</code>, <code>false</code> otherwise.
	 */
	public boolean contains(Point2D p) {
		return contains(p.getX(), p.getY());
	}

	/**
	 * Tests if the specified <code>Rectangle2D</code> is inside the boundary of
	 * this <code>Shape</code>.
	 *
	 * @param r
	 *            a specified <code>Rectangle2D</code>
	 * @return <code>true</code> if this <code>Shape</code> bounds the specified
	 *         <code>Rectangle2D</code>; <code>false</code> otherwise.
	 */
	public boolean contains(Rectangle2D r) {
		return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	/**
	 * Returns a new transformed <code>Shape</code>.
	 *
	 * @param at
	 *            the <code>AffineTransform</code> used to transform a new
	 *            <code>Shape</code>.
	 * @return a new <code>Shape</code>, transformed with the specified
	 *         <code>AffineTransform</code>.
	 */
	public Shape createTransformedShape(AffineTransform at) {
		GeneralPath gp = (GeneralPath) clone();
		if (at != null) {
			gp.transform(at);
		}
		return gp;
	}

	/**
	 * Adds a curved segment, defined by three new points, to the path by
	 * drawing a B&eacute;zier curve that intersects both the current
	 * coordinates and the coordinates (x3,&nbsp;y3), using the specified points
	 * (x1,&nbsp;y1) and (x2,&nbsp;y2) as B&eacute;zier control points.
	 *
	 * @param x1
	 *            ,&nbsp;y1 the coordinates of the first B&eacute;ezier control
	 *            point
	 * @param x2
	 *            ,&nbsp;y2 the coordinates of the second B&eacute;zier control
	 *            point
	 * @param x3
	 *            ,&nbsp;y3 the coordinates of the final endpoint
	 */
	public void curveTo(float x1, float y1, float x2, float y2,
			float x3, float y3) {
		needRoom(1, 6, true);
		pointTypes[numTypes++] = SEG_CUBICTO;
		pointCoords[numCoords++] = x1;
		pointCoords[numCoords++] = y1;
		pointCoords[numCoords++] = x2;
		pointCoords[numCoords++] = y2;
		pointCoords[numCoords++] = x3;
		pointCoords[numCoords++] = y3;
	}

	/**
	 * Return the bounding box of the path.
	 *
	 * @return a {@link GRectangleW.client.Rectangle} object that bounds the current
	 *         path.
	 */
	public Rectangle getBounds() {
		return getBounds2D().getBounds();
	}

	/**
	 * Returns the bounding box of the path.
	 *
	 * @return a {@link Rectangle2D} object that bounds the current path.
	 */
	public Rectangle2D getBounds2D() {
		float x1, y1, x2, y2;
		int i = numCoords;
		if (i > 0) {
			y1 = y2 = pointCoords[--i];
			x1 = x2 = pointCoords[--i];
			while (i > 0) {
				float y = pointCoords[--i];
				float x = pointCoords[--i];
				if (x < x1)
					x1 = x;
				if (y < y1)
					y1 = y;
				if (x > x2)
					x2 = x;
				if (y > y2)
					y2 = y;
			}
		} else {
			x1 = y1 = x2 = y2 = 0.0f;
		}
		return new Rectangle2D.Float(x1, y1, x2 - x1, y2 - y1);
	}

	/**
	 * Returns the coordinates most recently added to the end of the path as a
	 * {@link Point2D} object.
	 *
	 * @return a <code>Point2D</code> object containing the ending coordinates
	 *         of the path or <code>null</code> if there are no points in the
	 *         path.
	 */
	public Point2D getCurrentPoint() {
		if (numTypes < 1 || numCoords < 2) {
			return null;
		}
		int index = numCoords;
		if (pointTypes[numTypes - 1] == SEG_CLOSE) {
			loop: for (int i = numTypes - 2; i > 0; i--) {
				switch (pointTypes[i]) {
				case SEG_MOVETO:
					break loop;
				case SEG_LINETO:
					index -= 2;
					break;
				case SEG_QUADTO:
					index -= 4;
					break;
				case SEG_CUBICTO:
					index -= 6;
					break;
				case SEG_CLOSE:
					break;
				}
			}
		}
		return new Point2D.Float(pointCoords[index - 2], pointCoords[index - 1]);
	}

	/**
	 * Returns a <code>PathIterator</code> object that iterates along the
	 * boundary of this <code>Shape</code> and provides access to the geometry
	 * of the outline of this <code>Shape</code>. The iterator for this class is
	 * not multi-threaded safe, which means that this <code>GeneralPath</code>
	 * class does not guarantee that modifications to the geometry of this
	 * <code>GeneralPath</code> object do not affect any iterations of that
	 * geometry that are already in process.
	 *
	 * @param at
	 *            an <code>AffineTransform</code>
	 * @return a new <code>PathIterator</code> that iterates along the boundary
	 *         of this <code>Shape</code> and provides access to the geometry of
	 *         this <code>Shape</code>'s outline
	 */
	public PathIterator getPathIterator(AffineTransform at) {
		return new GeneralPathIterator(this, at);
	}

	/**
	 * Returns a <code>PathIterator</code> object that iterates along the
	 * boundary of the flattened <code>Shape</code> and provides access to the
	 * geometry of the outline of the <code>Shape</code>. The iterator for this
	 * class is not multi-threaded safe, which means that this
	 * <code>GeneralPath</code> class does not guarantee that modifications to
	 * the geometry of this <code>GeneralPath</code> object do not affect any
	 * iterations of that geometry that are already in process.
	 *
	 * @param at
	 *            an <code>AffineTransform</code>
	 * @param flatness
	 *            the maximum distance that the line segments used to
	 *            approximate the curved segments are allowed to deviate from
	 *            any point on the original curve
	 * @return a new <code>PathIterator</code> that iterates along the flattened
	 *         <code>Shape</code> boundary.
	 */
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return new FlatteningPathIterator(getPathIterator(at), flatness);
	}

	/**
	 * Returns the fill style winding rule.
	 *
	 * @return an integer representing the current winding rule.
	 * @see #WIND_EVEN_ODD
	 * @see #WIND_NON_ZERO
	 * @see #setWindingRule
	 */
	public int getWindingRule() {
		return windingRule;
	}

	/**
	 * Tests if the interior of this <code>Shape</code> intersects the interior
	 * of a specified set of rectangular coordinates.
	 *
	 * @param x
	 *            ,&nbsp;y the specified coordinates
	 * @param w
	 *            the width of the specified rectangular coordinates
	 * @param h
	 *            the height of the specified rectangular coordinates
	 * @return <code>true</code> if this <code>Shape</code> and the interior of
	 *         the specified set of rectangular coordinates intersect each
	 *         other; <code>false</code> otherwise.
	 */
	public boolean intersects(double x, double y, double w, double h) {
		Crossings c = Crossings.findCrossings(getPathIterator(null), x, y, x
				+ w, y + h);
		return (c == null || !c.isEmpty());
	}

	/**
	 * Tests if the interior of this <code>Shape</code> intersects the interior
	 * of a specified <code>Rectangle2D</code>.
	 *
	 * @param r
	 *            the specified <code>Rectangle2D</code>
	 * @return <code>true</code> if this <code>Shape</code> and the interior of
	 *         the specified <code>Rectangle2D</code> intersect each other;
	 *         <code>false</code> otherwise.
	 */
	public boolean intersects(Rectangle2D r) {
		return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	/**
	 * Adds a point to the path by drawing a straight line from the current
	 * coordinates to the new specified coordinates.
	 *
	 * @param x
	 *            ,&nbsp;y the specified coordinates
	 */
	public void lineTo(float x, float y) {
		needRoom(1, 2, true);
		pointTypes[numTypes++] = SEG_LINETO;
		pointCoords[numCoords++] = x;
		pointCoords[numCoords++] = y;
	}

	/**
	 * Adds a point to the path by moving to the specified coordinates.
	 *
	 * @param x
	 *            ,&nbsp;y the specified coordinates
	 */
	public void moveTo(float x, float y) {
		if (numTypes > 0 && pointTypes[numTypes - 1] == SEG_MOVETO) {
			pointCoords[numCoords - 2] = x;
			pointCoords[numCoords - 1] = y;
		} else {
			needRoom(1, 2, false);
			pointTypes[numTypes++] = SEG_MOVETO;
			pointCoords[numCoords++] = x;
			pointCoords[numCoords++] = y;
		}
	}

	/**
	 * Adds a curved segment, defined by two new points, to the path by drawing
	 * a Quadratic curve that intersects both the current coordinates and the
	 * coordinates (x2,&nbsp;y2), using the specified point (x1,&nbsp;y1) as a
	 * quadratic parametric control point.
	 *
	 * @param x1
	 *            ,&nbsp;y1 the coordinates of the first quadratic control point
	 * @param x2
	 *            ,&nbsp;y2 the coordinates of the final endpoint
	 */
	public void quadTo(float x1, float y1, float x2, float y2) {
		needRoom(1, 4, true);
		pointTypes[numTypes++] = SEG_QUADTO;
		pointCoords[numCoords++] = x1;
		pointCoords[numCoords++] = y1;
		pointCoords[numCoords++] = x2;
		pointCoords[numCoords++] = y2;
	}

	/**
	 * Resets the path to empty. The append position is set back to the
	 * beginning of the path and all coordinates and point types are forgotten.
	 */
	public void reset() {
		numTypes = numCoords = 0;
	}

	/**
	 * Sets the winding rule for this path to the specified value.
	 *
	 * @param rule
	 *            an integer representing the specified winding rule
	 * @exception <code>IllegalArgumentException</code> if <code>rule</code> is
	 *            not either <code>WIND_EVEN_ODD</code> or
	 *            <code>WIND_NON_ZERO</code>
	 * @see #WIND_EVEN_ODD
	 * @see #WIND_NON_ZERO
	 * @see #getWindingRule
	 */
	public void setWindingRule(int rule) {
		if (rule != WIND_EVEN_ODD && rule != WIND_NON_ZERO) {
			throw new IllegalArgumentException("winding rule must be "
					+ "WIND_EVEN_ODD or " + "WIND_NON_ZERO");
		}
		windingRule = rule;
	}

	/**
	 * Transforms the geometry of this path using the specified
	 * {@link AffineTransform}. The geometry is transformed in place, which
	 * permanently changes the boundary defined by this object.
	 *
	 * @param at
	 *            the <code>AffineTransform</code> used to transform the area
	 */
	public void transform(AffineTransform at) {
		at.transform(pointCoords, 0, pointCoords, 0, numCoords / 2);
	}

	private void needRoom(int newTypes, int newCoords, boolean needMove) {
		if (needMove && numTypes == 0) {
			throw new IllegalPathStateException("missing initial moveto "
					+ "in path definition");
		}
		int size = pointCoords.length;
		if (numCoords + newCoords > size) {
			int grow = size;
			if (grow > EXPAND_MAX * 2) {
				grow = EXPAND_MAX * 2;
			}
			if (grow < newCoords) {
				grow = newCoords;
			}
			float[] arr = new float[size + grow];
			System.arraycopy(pointCoords, 0, arr, 0, numCoords);
			pointCoords = arr;
		}
		size = pointTypes.length;
		if (numTypes + newTypes > size) {
			int grow = size;
			if (grow > EXPAND_MAX) {
				grow = EXPAND_MAX;
			}
			if (grow < newTypes) {
				grow = newTypes;
			}
			byte[] arr = new byte[size + grow];
			System.arraycopy(pointTypes, 0, arr, 0, numTypes);
			pointTypes = arr;
		}
	}
}
