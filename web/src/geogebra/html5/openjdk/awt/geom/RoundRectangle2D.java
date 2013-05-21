/*
 * @(#)RoundRectangle2D.java	1.18 03/12/19
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
 * The <code>RoundRectangle2D</code> class defines a rectangle with
 * rounded corners defined by a location (x,&nbsp;y), a
 * dimension (w&nbsp;x&nbsp;h), and the width and height of an arc
 * with which to round the corners.
 * <p>
 * This class is the abstract superclass for all objects that
 * store a 2D rounded rectangle.
 * The actual storage representation of the coordinates is left to
 * the subclass.
 *
 * @version 1.18, 12/19/03
 * @author	Jim Graham
 */
public abstract class RoundRectangle2D extends RectangularShape {
    /**
     * The <code>Float</code> class defines a rectangle with rounded
     * corners all specified in <code>float</code> coordinates.
     */
    public static class Float extends RoundRectangle2D {
	/**
	 * The X coordinate of this <code>RoundRectangle2D</code>.
	 */
	public float x;

	/**
         * The Y coordinate of this <code>RoundRectangle2D</code>.
	 */
	public float y;

	/**
         * The width of this <code>RoundRectangle2D</code>.
	 */
	public float width;

	/**
         * The height of this <code>RoundRectangle2D</code>.
	 */
	public float height;

	/**
	 * The width of the arc that rounds off the corners.
	 */
	public float arcwidth;

	/**
	 * The height of the arc that rounds off the corners.
	 */
	public float archeight;

	/**
	 * Constructs a new <code>RoundRectangle2D</code>, initialized to
         * location (0.0,&nbsp;0), size (0.0,&nbsp;0.0), and corner arcs
         * of radius 0.0.
	 */
	public Float() {
	}

	/**
	 * Constructs and initializes a <code>RoundRectangle2D</code>
         * from the specified coordinates.
	 * @param x,&nbsp;y the coordinates to which to set the newly
         * constructed <code>RoundRectangle2D</code>
	 * @param w the width to which to set the newly
         * constructed <code>RoundRectangle2D</code>
	 * @param h the height to which to set the newly
         * constructed <code>RoundRectangle2D</code>
         * @param arcw the width of the arc to use to round off the
         * corners of the newly constructed <code>RoundRectangle2D</code>
         * @param arch the height of the arc to use to round off the
         * corners of the newly constructed <code>RoundRectangle2D</code>
	 */
	public Float(float x, float y, float w, float h,
				   float arcw, float arch) {
	    setRoundRect(x, y, w, h, arcw, arch);
	}

	/**
	 * Returns the X coordinate of this <code>RoundRectangle2D</code>
         * in <code>double</code> precision.
         * @return the X coordinate of this <code>RoundRectangle2D</code>.
	 */
	public double getX() {
	    return (double) x;
	}

	/**
	 * Returns the Y coordinate of this <code>RoundRectangle2D</code>
         * in <code>double</code> precision.
         * @return the Y coordinate of this <code>RoundRectangle2D</code>.
         */
	public double getY() {
	    return (double) y;
	}

	/**
	 * Returns the width of this <code>RoundRectangle2D</code>
         * in <code>double</code> precision.
         * @return the width of this <code>RoundRectangle2D</code>.
         */
	public double getWidth() {
	    return (double) width;
	}

	/**
         * Returns the height of this <code>RoundRectangle2D</code>
         * in <code>double</code> precision.
         * @return the height of this <code>RoundRectangle2D</code>.
         */
	public double getHeight() {
	    return (double) height;
	}

	/**
	 * Returns the width of the arc that rounds off the corners.
         * @return the width of the arc that rounds off the corners
         * of this <code>RoundRectangle2D</code>.
	 */
	public double getArcWidth() {
	    return (double) arcwidth;
	}

	/**
         * Returns the height of the arc that rounds off the corners.
         * @return the height of the arc that rounds off the corners
         * of this <code>RoundRectangle2D</code>.
         */
	public double getArcHeight() {
	    return (double) archeight;
	}

	/**
	 * Determines whether or not this <code>RoundRectangle2D</code>
         * is empty.
         * @return <code>true</code> if this <code>RoundRectangle2D</code>
         * is empty; <code>false</code> othwerwise.
	 */
	public boolean isEmpty() {
	    return (width <= 0.0f) || (height <= 0.0f);
	}

	/**
	 * Sets the location, size, and arc radii of this
         * <code>RoundRectangle2D</code> to the
	 * specified <code>float</code> values.
         * @param x,&nbsp;y the coordinates to which to set the
         * location of this <code>RoundRectangle2D</code>
         * @param w the width to which to set this
         * <code>RoundRectangle2D</code>
         * @param h the height to which to set this
         * <code>RoundRectangle2D</code>
         * @param arcw the width to which to set the arc of this
         * <code>RoundRectangle2D</code>
         * @param arch the height to which to set the arc of this
         * <code>RoundRectangle2D</code>
	 */
	public void setRoundRect(float x, float y, float w, float h,
				 float arcw, float arch) {
	    this.x = x;
	    this.y = y;
	    this.width = w;
	    this.height = h;
	    this.arcwidth = arcw;
	    this.archeight = arch;
	}

	/**
	 * Sets the location, size, and arc radii of this
         * <code>RoundRectangle2D</code> to the
	 * specified <code>double</code> values.
         * @param x,&nbsp;y the coordinates to which to set the
         * location of this <code>RoundRectangle2D</code>
         * @param w the width to which to set this
         * <code>RoundRectangle2D</code>
         * @param h the height to which to set this
         * <code>RoundRectangle2D</code>
         * @param arcw the width to which to set the arc of this
         * <code>RoundRectangle2D</code>
         * @param arch the height to which to set the arc of this
         * <code>RoundRectangle2D</code>
         */
	public void setRoundRect(double x, double y, double w, double h,
				 double arcw, double arch) {
	    this.x = (float) x;
	    this.y = (float) y;
	    this.width = (float) w;
	    this.height = (float) h;
	    this.arcwidth = (float) arcw;
	    this.archeight = (float) arch;
	}

	/**
	 * Sets this <code>RoundRectangle2D</code> to be the same as the
         * specified <code>RoundRectangle2D</code>.
         * @param rr the specified <code>RoundRectangle2D</code>
	 */
	public void setRoundRect(RoundRectangle2D rr) {
	    this.x = (float) rr.getX();
	    this.y = (float) rr.getY();
	    this.width = (float) rr.getWidth();
	    this.height = (float) rr.getHeight();
	    this.arcwidth = (float) rr.getArcWidth();
	    this.archeight = (float) rr.getArcHeight();
	}

	/**
	 * Returns the high precision bounding box of this
         * <code>RoundRectangle2D</code>.
         * @return a {@link Rectangle2D} that is the bounding
         * box of this <code>RoundRectangle2D</code>.
	 */
	public Rectangle2D getBounds2D() {
	    return new Rectangle2D.Float(x, y, width, height);
	}
    }

    /**
     * The <code>Double</code> class defines a rectangle with rounded
     * corners all specified in <code>double</code> coordinates.
     */
    public static class Double extends RoundRectangle2D {
	/**
         * The X coordinate of this <code>RoundRectangle2D</code>.
         */
	public double x;

	/**
         * The Y coordinate of this <code>RoundRectangle2D</code>.
         */
	public double y;

	/**
         * The width of this <code>RoundRectangle2D</code>.
	 */
	public double width;

	/**
         * The height of this <code>RoundRectangle2D</code>.
	 */
	public double height;

	/**
         * The width of the arc that rounds off the corners.
	 */
	public double arcwidth;

	/**
         * The height of the arc that rounds off the corners.
	 */
	public double archeight;

	/**
         * Constructs a new <code>RoundRectangle2D</code>, initialized to
         * location (0.0,&nbsp;0), size (0.0,&nbsp;0.0), and corner arcs
         * of radius 0.0.
         */
	public Double() {
	}

	/**
         * Constructs and initializes a <code>RoundRectangle2D</code>
         * from the specified coordinates.
         * @param x,&nbsp;y the coordinates to which to set the newly
         * constructed <code>RoundRectangle2D</code>
         * @param w the width to which to set the newly
         * constructed <code>RoundRectangle2D</code>
         * @param h the height to which to set the newly
         * constructed <code>RoundRectangle2D</code>
         * @param arcw the width of the arc to use to round off the
         * corners of the newly constructed <code>RoundRectangle2D</code>
         * @param arch the height of the arc to use to round off the
         * corners of the newly constructed <code>RoundRectangle2D</code>
         */
	public Double(double x, double y, double w, double h,
		      double arcw, double arch) {
	    setRoundRect(x, y, w, h, arcw, arch);
	}

	/**
         * Returns the X coordinate of this <code>RoundRectangle2D</code>
         * in <code>double</code> precision.
         * @return the X coordinate of this <code>RoundRectangle2D</code>.
         */
	public double getX() {
	    return x;
	}

	/**
         * Returns the Y coordinate of this <code>RoundRectangle2D</code>
         * in <code>double</code> precision.
         * @return the Y coordinate of this <code>RoundRectangle2D</code>.
         */
	public double getY() {
	    return y;
	}

	/**
         * Returns the width of this <code>RoundRectangle2D</code>
         * in <code>double</code> precision.
         * @return the width of this <code>RoundRectangle2D</code>.
         */
	public double getWidth() {
	    return width;
	}

	/**
         * Returns the height of this <code>RoundRectangle2D</code>
         * in <code>double</code> precision.
         * @return the height of this <code>RoundRectangle2D</code>.
         */
	public double getHeight() {
	    return height;
	}

	/**
         * Returns the width of the arc that rounds off the corners.
         * @return the width of the arc that rounds off the corners
         * of this <code>RoundRectangle2D</code>.
         */
	public double getArcWidth() {
	    return arcwidth;
	}

	/**
         * Returns the height of the arc that rounds off the corners.
         * @return the height of the arc that rounds off the corners
         * of this <code>RoundRectangle2D</code>.
         */
	public double getArcHeight() {
	    return archeight;
	}

	/**
         * Determines whether or not this <code>RoundRectangle2D</code>
         * is empty.
         * @return <code>true</code> if this <code>RoundRectangle2D</code>
         * is empty; <code>false</code> othwerwise.
         */
	public boolean isEmpty() {
	    return (width <= 0.0f) || (height <= 0.0f);
	}

	/**
         * Sets the location, size, and arc radii of this
         * <code>RoundRectangle2D</code> to the
         * specified <code>double</code> values.
         * @param x,&nbsp;y the coordinates to which to set the
         * location of this <code>RoundRectangle2D</code>
         * @param w the width to which to set this
         * <code>RoundRectangle2D</code>
         * @param h the height to which to set this
         * <code>RoundRectangle2D</code>
         * @param arcw the width to which to set the arc of this
         * <code>RoundRectangle2D</code>
         * @param arch the height to which to set the arc of this
         * <code>RoundRectangle2D</code>
         */
	public void setRoundRect(double x, double y, double w, double h,
				 double arcw, double arch) {
	    this.x = x;
	    this.y = y;
	    this.width = w;
	    this.height = h;
	    this.arcwidth = arcw;
	    this.archeight = arch;
	}

	/**
         * Sets this <code>RoundRectangle2D</code> to be the same as the
         * specified <code>RoundRectangle2D</code>.
         * @param rr the specified <code>RoundRectangle2D</code>
         */
	public void setRoundRect(RoundRectangle2D rr) {
	    this.x = rr.getX();
	    this.y = rr.getY();
	    this.width = rr.getWidth();
	    this.height = rr.getHeight();
	    this.arcwidth = rr.getArcWidth();
	    this.archeight = rr.getArcHeight();
	}

	/**
         * Returns the high precision bounding box of this
         * <code>RoundRectangle2D</code>.
         * @return a {@link Rectangle2D} that is the bounding
         * box of this <code>RoundRectangle2D</code>.
	 */
	public Rectangle2D getBounds2D() {
	    return new Rectangle2D.Double(x, y, width, height);
	}
    }

    /**
     * This is an abstract class that cannot be instantiated directly.
     * Type-specific implementation subclasses are available for
     * instantiation and provide a number of formats for storing
     * the information necessary to satisfy the various accessor
     * methods below.
     *
     * @see java.awt.geom.RoundRectangle2D.Float
     * @see java.awt.geom.RoundRectangle2D.Double
     */
    protected RoundRectangle2D() {
    }

    /**
     * Gets the width of the arc that rounds off the corners.
     * @return the width of the arc that rounds off the corners
     * of this <code>RoundRectangle2D</code>.
     */
    public abstract double getArcWidth();

    /**
     * Gets the height of the arc that rounds off the corners.
     * @return the height of the arc that rounds off the corners
     * of this <code>RoundRectangle2D</code>.
     */
    public abstract double getArcHeight();

    /**
     * Sets the location, size, and corner radii of this
     * <code>RoundRectangle2D</code> to the specified
     * <code>double</code> values.
     * @param x,&nbsp;y the coordinates to which to set the
     * location of this <code>RoundRectangle2D</code>
     * @param w the width to which to set this
     * <code>RoundRectangle2D</code>
     * @param h the height to which to set this
     * <code>RoundRectangle2D</code>
     * @param arcWidth the width to which to set the arc of this
     * <code>RoundRectangle2D</code>
     * @param arcHeight the height to which to set the arc of this
     * <code>RoundRectangle2D</code>
     */
    public abstract void setRoundRect(double x, double y, double w, double h,
				      double arcWidth, double arcHeight);

    /**
     * Sets this <code>RoundRectangle2D</code> to be the same as the
     * specified <code>RoundRectangle2D</code>.
     * @param rr the specified <code>RoundRectangle2D</code>
     */
    public void setRoundRect(RoundRectangle2D rr) {
	setRoundRect(rr.getX(), rr.getY(), rr.getWidth(), rr.getHeight(),
		     rr.getArcWidth(), rr.getArcHeight());
    }

    /**
     * Sets the location and size of the outer bounds of this
     * <code>RoundRectangle2D</code> to the specified rectangular values.
     * @param x,&nbsp;y the coordinates to which to set the location
     * of this <code>RoundRectangle2D</code>
     * @param w the width to which to set this
     * <code>RoundRectangle2D</code>
     * @param h the height to which to set this
     * <code>RoundRectangle2D</code>
     */
    public void setFrame(double x, double y, double w, double h) {
	setRoundRect(x, y, w, h, getArcWidth(), getArcHeight());
    }

    /**
     * Tests if the specified coordinates are inside the boundary of
     * this <code>RoundRectangle2D</code>.
     * @param x,&nbsp;y the coordinates to test
     * @return <code>true</code> if the specified coordinates are
     * inside the boundary of this <code>RoundRectangle2D</code>;
     * <code>false</code> otherwise.
     */
    public boolean contains(double x, double y) {
	if (isEmpty()) {
	    return false;
	}
	double rrx0 = getX();
	double rry0 = getY();
	double rrx1 = rrx0 + getWidth();
	double rry1 = rry0 + getHeight();
	// Check for trivial rejection - point is outside bounding rectangle
	if (x < rrx0 || y < rry0 || x >= rrx1 || y >= rry1) {
	    return false;
	}
	double aw = Math.min(getWidth(), Math.abs(getArcWidth())) / 2.0;
	double ah = Math.min(getHeight(), Math.abs(getArcHeight())) / 2.0;
	// Check which corner point is in and do circular containment
	// test - otherwise simple acceptance
	if (x >= (rrx0 += aw) && x < (rrx0 = rrx1 - aw)) {
	    return true;
	}
	if (y >= (rry0 += ah) && y < (rry0 = rry1 - ah)) {
	    return true;
	}
	x = (x - rrx0) / aw;
	y = (y - rry0) / ah;
	return (x * x + y * y <= 1.0);
    }

    private int classify(double coord, double left, double right,
			 double arcsize) {
	if (coord < left) {
	    return 0;
	} else if (coord < left + arcsize) {
	    return 1;
	} else if (coord < right - arcsize) {
	    return 2;
	} else if (coord < right) {
	    return 3;
	} else {
	    return 4;
	}
    }

    /**
     * Tests if the interior of this <code>RoundRectangle2D</code>
     * intersects the interior of a specified set of rectangular
     * coordinates.
     * @param x,&nbsp;y the coordinates of the upper left corner
     * of the specified set of rectangular coordinates
     * @param w the width of the specified set of rectangular
     * coordinates
     * @param h the height of the specified set of rectangular
     * coordinates
     * @return <code>true</code> if the interior of this
     * <code>RoundRectangle2D</code> intersects the interior of the
     * specified set of rectangular coordinates.
     */
    public boolean intersects(double x, double y, double w, double h) {
	if (isEmpty() || w <= 0 || h <= 0) {
	    return false;
	}
	double rrx0 = getX();
	double rry0 = getY();
	double rrx1 = rrx0 + getWidth();
	double rry1 = rry0 + getHeight();
	// Check for trivial rejection - bounding rectangles do not intersect
	if (x + w <= rrx0 || x >= rrx1 || y + h <= rry0 || y >= rry1) {
	    return false;
	}
	double aw = Math.min(getWidth(), Math.abs(getArcWidth())) / 2.0;
	double ah = Math.min(getHeight(), Math.abs(getArcHeight())) / 2.0;
	int x0class = classify(x, rrx0, rrx1, aw);
	int x1class = classify(x + w, rrx0, rrx1, aw);
	int y0class = classify(y, rry0, rry1, ah);
	int y1class = classify(y + h, rry0, rry1, ah);
	// Trivially accept if any point is inside inner rectangle
	if (x0class == 2 || x1class == 2 || y0class == 2 || y1class == 2) {
	    return true;
	}
	// Trivially accept if either edge spans inner rectangle
	if ((x0class < 2 && x1class > 2) || (y0class < 2 && y1class > 2)) {
	    return true;
	}
	// Since neither edge spans the center, then one of the corners
	// must be in one of the rounded edges.  We detect this case if
	// a [xy]0class is 3 or a [xy]1class is 1.  One of those two cases
	// must be true for each direction.
	// We now find a "nearest point" to test for being inside a rounded
	// corner.
	x = (x1class == 1) ? (x = x + w - (rrx0 + aw)) : (x = x - (rrx1 - aw));
	y = (y1class == 1) ? (y = y + h - (rry0 + ah)) : (y = y - (rry1 - ah));
	x = x / aw;
	y = y / ah;
	return (x * x + y * y <= 1.0);
    }

    /**
     * Tests if the interior of this <code>RoundRectangle2D</code>
     * entirely contains the specified set of rectangular coordinates.
     * @param x,&nbsp;y the coordinates of the specified set of
     * rectangular coordinates
     * @param w the width of the specified set of rectangular
     * coordinates
     * @param h the height of the specified set of rectangular
     * coordinates
     * @return <code>true</code> if the interior of this
     * <code>RoundRectangle2D</code> entirely contains the specified
     * set of rectangular coordinates; <code>false</code> otherwise.
     */
    public boolean contains(double x, double y, double w, double h) {
	if (isEmpty() || w <= 0 || h <= 0) {
	    return false;
	}
	return (contains(x, y) &&
		contains(x + w, y) &&
		contains(x, y + h) &&
		contains(x + w, y + h));
    }

    /**
     * Returns an iteration object that defines the boundary of this
     * <code>RoundRectangle2D</code>.
     * The iterator for this class is multi-threaded safe, which means
     * that this <code>RoundRectangle2D</code> class guarantees that
     * modifications to the geometry of this <code>RoundRectangle2D</code>
     * object do not affect any iterations of that geometry that
     * are already in process.
     * @param at an optional <code>AffineTransform</code> to be applied to
     * the coordinates as they are returned in the iteration, or
     * <code>null</code> if untransformed coordinates are desired
     * @return    the <code>PathIterator</code> object that returns the
     *          geometry of the outline of this
     *          <code>RoundRectangle2D</code>, one segment at a time.
     */
    public PathIterator getPathIterator(AffineTransform at) {
	return new RoundRectIterator(this, at);
    }
}
