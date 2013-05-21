/*
 * @(#)Area.java	1.16 03/12/19
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

import geogebra.html5.kernel.external.AreaOp;
import geogebra.html5.kernel.external.Crossings;
import geogebra.html5.kernel.external.Curve;

import java.util.Enumeration;
import java.util.Vector;

/**
 * The <code>Area</code> class is a device-independent specification of an
 * arbitrarily-shaped area.  The <code>Area</code> object is defined as an
 * object that performs certain binary CAG (Constructive Area Geometry)
 * operations on other area-enclosing geometries, such as rectangles,
 * ellipses, and polygons. The CAG operations are Add(union), Subtract,
 * Intersect, and ExclusiveOR. For example, an <code>Area</code> can be
 * made up of the area of a rectangle minus the area of an ellipse.
 */
public class Area implements Shape, Cloneable {
    private static Vector EmptyCurves = new Vector();

    private Vector curves;

    /**
     * Default constructor which creates an empty area.
     */
    public Area() {
	curves = EmptyCurves;
    }

    /**
     * The <code>Area</code> class creates an area geometry from the
     * specified {@link Shape} object.  The geometry is explicitly
     * closed, if the <code>Shape</code> is not already closed.  The
     * fill rule (even-odd or winding) specified by the geometry of the
     * <code>Shape</code> is used to determine the resulting enclosed area.
     * @param s  the <code>Shape</code> from which the area is constructed
     */
    public Area(Shape s) {
	if (s instanceof Area) {
	    curves = ((Area) s).curves;
	    return;
	}
	curves = new Vector();
	PathIterator pi = s.getPathIterator(null);
	int windingRule = pi.getWindingRule();
	// coords array is big enough for holding:
	//     coordinates returned from currentSegment (6)
	//     OR
	//         two subdivided quadratic curves (2+4+4=10)
	//         AND
	//             0-1 horizontal splitting parameters
	//             OR
	//             2 parametric equation derivative coefficients
	//     OR
	//         three subdivided cubic curves (2+6+6+6=20)
	//         AND
	//             0-2 horizontal splitting parameters
	//             OR
	//             3 parametric equation derivative coefficients
	double coords[] = new double[23];
	double movx = 0, movy = 0;
	double curx = 0, cury = 0;
	double newx, newy;
	while (!pi.isDone()) {
	    switch (pi.currentSegment(coords)) {
	    case PathIterator.SEG_MOVETO:
		Curve.insertLine(curves, curx, cury, movx, movy);
		curx = movx = coords[0];
		cury = movy = coords[1];
		Curve.insertMove(curves, movx, movy);
		break;
	    case PathIterator.SEG_LINETO:
		newx = coords[0];
		newy = coords[1];
		Curve.insertLine(curves, curx, cury, newx, newy);
		curx = newx;
		cury = newy;
		break;
	    case PathIterator.SEG_QUADTO:
		newx = coords[2];
		newy = coords[3];
		Curve.insertQuad(curves, curx, cury, coords);
		curx = newx;
		cury = newy;
		break;
	    case PathIterator.SEG_CUBICTO:
		newx = coords[4];
		newy = coords[5];
		Curve.insertCubic(curves, curx, cury, coords);
		curx = newx;
		cury = newy;
		break;
	    case PathIterator.SEG_CLOSE:
		Curve.insertLine(curves, curx, cury, movx, movy);
		curx = movx;
		cury = movy;
		break;
	    }
	    pi.next();
	}
	Curve.insertLine(curves, curx, cury, movx, movy);
	AreaOp operator;
	if (windingRule == PathIterator.WIND_EVEN_ODD) {
	    operator = new AreaOp.EOWindOp();
	} else {
	    operator = new AreaOp.NZWindOp();
	}
	curves = operator.calculate(this.curves, EmptyCurves);
    }

    /**
     * Adds the shape of the specified <code>Area</code> to the
     * shape of this <code>Area</code>.
     * Addition is achieved through union.
     * @param   rhs  the <code>Area</code> to be added to the
     *          current shape
     */
    public void add(Area rhs) {
	curves = new AreaOp.AddOp().calculate(this.curves, rhs.curves);
	invalidateBounds();
    }

    /**
     * Subtracts the shape of the specified <code>Area</code> from the
     * shape of this <code>Area</code>.
     * @param   rhs  the <code>Area</code> to be subtracted from the
     *		current shape
     */
    public void subtract(Area rhs) {
	curves = new AreaOp.SubOp().calculate(this.curves, rhs.curves);
	invalidateBounds();
    }

    /**
     * Sets the shape of this <code>Area</code> to the intersection of
     * its current shape and the shape of the specified <code>Area</code>.
     * @param   rhs  the <code>Area</code> to be intersected with this
     *		<code>Area</code>
     */
    public void intersect(Area rhs) {
	curves = new AreaOp.IntOp().calculate(this.curves, rhs.curves);
	invalidateBounds();
    }

    /**
     * Sets the shape of this <code>Area</code> to be the combined area
     * of its current shape and the shape of the specified <code>Area</code>,
     * minus their intersection.
     * @param   rhs  the <code>Area</code> to be exclusive ORed with this
     *		<code>Area</code>.
     */
    public void exclusiveOr(Area rhs) {
	curves = new AreaOp.XorOp().calculate(this.curves, rhs.curves);
	invalidateBounds();
    }

    /**
     * Removes all of the geometry from this <code>Area</code> and
     * restores it to an empty area.
     */
    public void reset() {
	curves = new Vector();
	invalidateBounds();
    }

    /**
     * Tests whether this <code>Area</code> object encloses any area.
     * @return    <code>true</code> if this <code>Area</code> object
     * represents an empty area; <code>false</code> otherwise.
     */
    public boolean isEmpty() {
	return (curves.size() == 0);
    }

    /**
     * Tests whether this <code>Area</code> consists entirely of
     * straight edged polygonal geometry.
     * @return    <code>true</code> if the geometry of this
     * <code>Area</code> consists entirely of line segments;
     * <code>false</code> otherwise.
     */
    public boolean isPolygonal() {
	Enumeration enum_ = curves.elements();
	while (enum_.hasMoreElements()) {
	    if (((Curve) enum_.nextElement()).getOrder() > 1) {
		return false;
	    }
	}
	return true;
    }

    /**
     * Tests whether this <code>Area</code> is rectangular in shape.
     * @return    <code>true</code> if the geometry of this
     * <code>Area</code> is rectangular in shape; <code>false</code>
     * otherwise.
     */
    public boolean isRectangular() {
	int size = curves.size();
	if (size == 0) {
	    return true;
	}
	if (size > 3) {
	    return false;
	}
	Curve c1 = (Curve) curves.get(1);
	Curve c2 = (Curve) curves.get(2);
	if (c1.getOrder() != 1 || c2.getOrder() != 1) {
	    return false;
	}
	if (c1.getXTop() != c1.getXBot() || c2.getXTop() != c2.getXBot()) {
	    return false;
	}
	if (c1.getYTop() != c2.getYTop() || c1.getYBot() != c2.getYBot()) {
	    // One might be able to prove that this is impossible...
	    return false;
	}
	return true;
    }

    /**
     * Tests whether this <code>Area</code> is comprised of a single
     * closed subpath.  This method returns <code>true</code> if the
     * path contains 0 or 1 subpaths, or <code>false</code> if the path
     * contains more than 1 subpath.  The subpaths are counted by the
     * number of {@link PathIterator#SEG_MOVETO SEG_MOVETO}  segments
     * that appear in the path.
     * @return    <code>true</code> if the <code>Area</code> is comprised
     * of a single basic geometry; <code>false</code> otherwise.
     */
    public boolean isSingular() {
	if (curves.size() < 3) {
	    return true;
	}
	Enumeration enum_ = curves.elements();
	enum_.nextElement(); // First Order0 "moveto"
	while (enum_.hasMoreElements()) {
	    if (((Curve) enum_.nextElement()).getOrder() == 0) {
		return false;
	    }
	}
	return true;
    }

    private Rectangle2D cachedBounds;
    private void invalidateBounds() {
	cachedBounds = null;
    }
    private Rectangle2D getCachedBounds() {
	if (cachedBounds != null) {
	    return cachedBounds;
	}
	Rectangle2D r = new Rectangle2D.Double();
	if (curves.size() > 0) {
	    Curve c = (Curve) curves.get(0);
	    // First point is always an order 0 curve (moveto)
	    r.setRect(c.getX0(), c.getY0(), 0, 0);
	    for (int i = 1; i < curves.size(); i++) {
		((Curve) curves.get(i)).enlarge(r);
	    }
	}
	return (cachedBounds = r);
    }

    /**
     * Returns a high precision bounding {@link Rectangle2D} that
     * completely encloses this <code>Area</code>.
     * <p>
     * The Area class will attempt to return the tightest bounding
     * box possible for the Shape.  The bounding box will not be
     * padded to include the control points of curves in the outline
     * of the Shape, but should tightly fit the actual geometry of
     * the outline itself.
     * @return    the bounding <code>Rectangle2D</code> for the
     * <code>Area</code>.
     */
    public Rectangle2D getBounds2D() {
	return getCachedBounds().getBounds2D();
    }

    /**
     * Returns a bounding {@link Rectangle} that completely encloses
     * this <code>Area</code>.
     * <p>
     * The Area class will attempt to return the tightest bounding
     * box possible for the Shape.  The bounding box will not be
     * padded to include the control points of curves in the outline
     * of the Shape, but should tightly fit the actual geometry of
     * the outline itself.  Since the returned object represents
     * the bounding box with integers, the bounding box can only be
     * as tight as the nearest integer coordinates that encompass
     * the geometry of the Shape.
     * @return    the bounding <code>Rectangle</code> for the
     * <code>Area</code>.
     */
    public Rectangle getBounds() {
	return getCachedBounds().getBounds();
    }

    /**
     * Returns an exact copy of this <code>Area</code> object.
     * @return    Created clone object
     */
    public Object clone() {
	return new Area(this);
    }

    /**
     * Tests whether the geometries of the two <code>Area</code> objects
     * are equal.
     * @param   other  the <code>Area</code> to be compared to this
     *		<code>Area</code>
     * @return  <code>true</code> if the two geometries are equal;
     *		<code>false</code> otherwise.
     */
    public boolean equals(Area other) {
	// REMIND: A *much* simpler operation should be possible...
	// Should be able to do a curve-wise comparison since all Areas
	// should evaluate their curves in the same top-down order.
	if (other == this) {
	    return true;
	}
	if (other == null) {
	    return false;
	}
	Vector c = new AreaOp.XorOp().calculate(this.curves, other.curves);
	return c.isEmpty();
    }

    /**
     * Transforms the geometry of this <code>Area</code> using the specified
     * {@link AffineTransform}.  The geometry is transformed in place, which
     * permanently changes the enclosed area defined by this object.
     * @param t  the transformation used to transform the area
     */
    public void transform(AffineTransform t) {
	// REMIND: A simpler operation can be performed for some types
	// of transform.
	// REMIND: this could be simplified by "breaking out" the
	// PathIterator code from the constructor
	curves = new Area(t.createTransformedShape(this)).curves;
	invalidateBounds();
    }

    /**
     * Creates a new <code>Area</code> object that contains the same
     * geometry as this <code>Area</code> transformed by the specified
     * <code>AffineTransform</code>.  This <code>Area</code> object
     * is unchanged.
     * @param t  the specified <code>AffineTransform</code> used to transform
     *           the new <code>Area</code>
     * @return   a new <code>Area</code> object representing the transformed
     *           geometry.
     */
    public Area createTransformedArea(AffineTransform t) {
	// REMIND: A simpler operation can be performed for some types
	// of transform.
	// REMIND: this could be simplified by "breaking out" the
	// PathIterator code from the constructor
	return new Area(t.createTransformedShape(this));
    }

    /**
     * Tests if a specifed point lies inside the boundary of
     * this <code>Area</code> object.
     * @param     x,&nbsp;y the specified point
     * @return    <code>true</code> if the point lies completely within the
     *            interior of the <code>Area</code>;
     *            <code>false</code> otherwise.
     */
    public boolean contains(double x, double y) {
	if (!getCachedBounds().contains(x, y)) {
	    return false;
	}
	Enumeration enum_ = curves.elements();
	int crossings = 0;
	while (enum_.hasMoreElements()) {
	    Curve c = (Curve) enum_.nextElement();
	    crossings += c.crossingsFor(x, y);
	}
	return ((crossings & 1) == 1);
    }

    /**
     * Tests if a specified {@link Point2D} lies inside the boundary of the
     * this <code>Area</code> object.
     * @param     p  the <code>Point2D</code> to test
     * @return    <code>true</code> if the specified <code>Point2D</code>
     *		 lies completely within the interior of the <code>Area</code>;
     *		 <code>false</code> otherwise.
     */
    public boolean contains(Point2D p) {
	return contains(p.getX(), p.getY());
    }

    /**
     * Tests whether or not the interior of this <code>Area</code> object
     * completely contains the specified rectangular area.
     * @param x,&nbsp;y the coordinates of the upper left corner of
     *          the specified rectangular area
     * @param w the width of the specified rectangular area
     * @param h the height of the specified rectangular area
     * @return  <code>true</code> if the specified rectangular area
     *          lies completely within the interior of the <code>Area</code>;
     *          <code>false</code> otherwise.
     */
    public boolean contains(double x, double y, double w, double h) {
	if (w < 0 || h < 0) {
	    return false;
	}
	if (!getCachedBounds().contains(x, y, w, h)) {
	    return false;
	}
	Crossings c = Crossings.findCrossings(curves, x, y, x+w, y+h);
	return (c != null && c.covers(y, y+h));
    }

    /**
     * Tests whether or not the interior of this <code>Area</code> object
     * completely contains the specified <code>Rectangle2D</code>.
     * @param     p  the <code>Rectangle2D</code> to test
     * @return    <code>true</code> if the <code>Rectangle2D</code> lies
     *            completely within the interior of the <code>Area</code>;
     *            <code>false</code> otherwise.
     */
    public boolean contains(Rectangle2D p) {
	return contains(p.getX(), p.getY(), p.getWidth(), p.getHeight());
    }

    /**
     * Tests whether the interior of this <code>Area</code> object
     * intersects the interior of the specified rectangular area.
     * @param x,&nbsp;y the coordinates of the upper left corner of
     *          the specified rectangular area
     * @param w the width of the specified rectangular area
     * @param h the height of teh specified rectangular area
     * @return    <code>true</code> if the interior intersects the specified
     *		rectangular area; <code>false</code> otherwise;
     */
    public boolean intersects(double x, double y, double w, double h) {
	if (w < 0 || h < 0) {
	    return false;
	}
	if (!getCachedBounds().intersects(x, y, w, h)) {
	    return false;
	}
	Crossings c = Crossings.findCrossings(curves, x, y, x+w, y+h);
	return (c == null || !c.isEmpty());
    }

    /**
     * Tests whether the interior of this <code>Area</code> object
     * intersects the interior of the specified <code>Rectangle2D</code>.
     * @param     p  the <code>Rectangle2D</code> to test for intersection
     * @return    <code>true</code> if the interior intersects the
     *			specified <code>Rectangle2D</code>;
     *			<code>false</code> otherwise.
     */
    public boolean intersects(Rectangle2D p) {
	return intersects(p.getX(), p.getY(), p.getWidth(), p.getHeight());
    }

    /**
     * Creates a {@link PathIterator} for the outline of this
     * <code>Area</code> object.  This <code>Area</code> object is unchanged.
     * @param at an optional <code>AffineTransform</code> to be applied to
     * the coordinates as they are returned in the iteration, or
     * <code>null</code> if untransformed coordinates are desired
     * @return    the <code>PathIterator</code> object that returns the
     *		geometry of the outline of this <code>Area</code>, one
     *		segment at a time.
     */
    public PathIterator getPathIterator(AffineTransform at) {
    	return new AreaIterator(curves, at);
    }

    /**
     * Creates a <code>PathIterator</code> for the flattened outline of
     * this <code>Area</code> object.  Only uncurved path segments
     * represented by the SEG_MOVETO, SEG_LINETO, and SEG_CLOSE point
     * types are returned by the iterator.  This <code>Area</code>
     * object is unchanged.
     * @param at an optional <code>AffineTransform</code> to be
     * applied to the coordinates as they are returned in the
     * iteration, or <code>null</code> if untransformed coordinates
     * are desired
     * @param flatness the maximum amount that the control points
     * for a given curve can vary from colinear before a subdivided
     * curve is replaced by a straight line connecting the endpoints
     * @return    the <code>PathIterator</code> object that returns the
     * geometry of the outline of this <code>Area</code>, one segment
     * at a time.
     */
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
    	return new FlatteningPathIterator(getPathIterator(at), flatness);
    }
}


