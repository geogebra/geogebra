/*
 * Copyright (c) 1997, 2011, Oracle and/or its affiliates. All rights reserved.
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
 * or visit www.oracle.com if you need additional information or have any contains
 * questions.
 */

package org.geogebra.ggbjdk.java.awt.geom;

import static java.lang.Math.max;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.ggbjdk.sun.awt.geom.Curve;

/**
 * The <code>CubicCurve2D</code> class defines a cubic parametric curve
 * segment in {@code (x,y)} coordinate space.
 * <p>
 * This class is only the abstract superclass for all objects which
 * store a 2D cubic curve segment.
 * The actual storage representation of the coordinates is left to
 * the subclass.
 *
 * @author      Jim Graham
 * @since 1.2
 */
public abstract class CubicCurve2D implements GShape  {

    /**
     * A cubic parametric curve segment specified with
     * {@code double} coordinates.
     * @since 1.2
     */
    public static class Double extends CubicCurve2D {
        /**
         * The X coordinate of the start point
         * of the cubic curve segment.
         * @since 1.2
         * 
         */
        public double x1;

        /**
         * The Y coordinate of the start point
         * of the cubic curve segment.
         * @since 1.2
         * 
         */
        public double y1;

        /**
         * The X coordinate of the first control point
         * of the cubic curve segment.
         * @since 1.2
         * 
         */
        public double ctrlx1;

        /**
         * The Y coordinate of the first control point
         * of the cubic curve segment.
         * @since 1.2
         * 
         */
        public double ctrly1;

        /**
         * The X coordinate of the second control point
         * of the cubic curve segment.
         * @since 1.2
         * 
         */
        public double ctrlx2;

        /**
         * The Y coordinate of the second control point
         * of the cubic curve segment.
         * @since 1.2
         * 
         */
        public double ctrly2;

        /**
         * The X coordinate of the end point
         * of the cubic curve segment.
         * @since 1.2
         * 
         */
        public double x2;

        /**
         * The Y coordinate of the end point
         * of the cubic curve segment.
         * @since 1.2
         * 
         */
        public double y2;

        /**
         * Constructs and initializes a CubicCurve with coordinates
         * (0, 0, 0, 0, 0, 0, 0, 0).
         * @since 1.2
         */
        public Double() {
        }

        /**
         * Constructs and initializes a {@code CubicCurve2D} from
         * the specified {@code double} coordinates.
         *
         * @param x1 the X coordinate for the start point
         *           of the resulting {@code CubicCurve2D}
         * @param y1 the Y coordinate for the start point
         *           of the resulting {@code CubicCurve2D}
         * @param ctrlx1 the X coordinate for the first control point
         *               of the resulting {@code CubicCurve2D}
         * @param ctrly1 the Y coordinate for the first control point
         *               of the resulting {@code CubicCurve2D}
         * @param ctrlx2 the X coordinate for the second control point
         *               of the resulting {@code CubicCurve2D}
         * @param ctrly2 the Y coordinate for the second control point
         *               of the resulting {@code CubicCurve2D}
         * @param x2 the X coordinate for the end point
         *           of the resulting {@code CubicCurve2D}
         * @param y2 the Y coordinate for the end point
         *           of the resulting {@code CubicCurve2D}
         * @since 1.2
         */
        public Double(double x1, double y1,
                      double ctrlx1, double ctrly1,
                      double ctrlx2, double ctrly2,
                      double x2, double y2)
        {
            setCurve(x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2);
        }

        /**
         * {@inheritDoc}
         * @since 1.2
         */
        @Override
		public double getX1() {
            return x1;
        }

        /**
         * {@inheritDoc}
         * @since 1.2
         */
        @Override
		public double getY1() {
            return y1;
        }

        /**
         * {@inheritDoc}
         * @since 1.2
         */
        @Override
		public Point2D getP1() {
            return new Point2D.Double(x1, y1);
        }

        /**
         * {@inheritDoc}
         * @since 1.2
         */
        @Override
		public double getCtrlX1() {
            return ctrlx1;
        }

        /**
         * {@inheritDoc}
         * @since 1.2
         */
        @Override
		public double getCtrlY1() {
            return ctrly1;
        }

        /**
         * {@inheritDoc}
         * @since 1.2
         */
        @Override
		public Point2D getCtrlP1() {
            return new Point2D.Double(ctrlx1, ctrly1);
        }

        /**
         * {@inheritDoc}
         * @since 1.2
         */
        @Override
		public double getCtrlX2() {
            return ctrlx2;
        }

        /**
         * {@inheritDoc}
         * @since 1.2
         */
        @Override
		public double getCtrlY2() {
            return ctrly2;
        }

        /**
         * {@inheritDoc}
         * @since 1.2
         */
        @Override
		public Point2D getCtrlP2() {
            return new Point2D.Double(ctrlx2, ctrly2);
        }

        /**
         * {@inheritDoc}
         * @since 1.2
         */
        @Override
		public double getX2() {
            return x2;
        }

        /**
         * {@inheritDoc}
         * @since 1.2
         */
        @Override
		public double getY2() {
            return y2;
        }

        /**
         * {@inheritDoc}
         * @since 1.2
         */
        @Override
		public Point2D getP2() {
            return new Point2D.Double(x2, y2);
        }

        /**
         * {@inheritDoc}
         * @since 1.2
         */
        @Override
		public void setCurve(double x1, double y1,
                             double ctrlx1, double ctrly1,
                             double ctrlx2, double ctrly2,
                             double x2, double y2)
        {
            this.x1     = x1;
            this.y1     = y1;
            this.ctrlx1 = ctrlx1;
            this.ctrly1 = ctrly1;
            this.ctrlx2 = ctrlx2;
            this.ctrly2 = ctrly2;
            this.x2     = x2;
            this.y2     = y2;
        }

        /**
         * {@inheritDoc}
         * @since 1.2
         */
        @Override
		public GRectangle2D getBounds2D() {
            double left   = Math.min(Math.min(x1, x2),
                                     Math.min(ctrlx1, ctrlx2));
            double top    = Math.min(Math.min(y1, y2),
                                     Math.min(ctrly1, ctrly2));
			double right = max(max(x1, x2), max(ctrlx1, ctrlx2));
			double bottom = max(max(y1, y2), max(ctrly1, ctrly2));
            return new Rectangle2D.Double(left, top,
                                          right - left, bottom - top);
        }

     }

    /**
     * This is an abstract class that cannot be instantiated directly.
     * Type-specific implementation subclasses are available for
     * instantiation and provide a number of formats for storing
     * the information necessary to satisfy the various accessor
     * methods below.
     *
     * @see java.awt.geom.CubicCurve2D.Float
     * @see java.awt.geom.CubicCurve2D.Double
     * @since 1.2
     */
    protected CubicCurve2D() {
    }

    /**
     * Returns the X coordinate of the start point in double precision.
     * @return the X coordinate of the start point of the
     *         {@code CubicCurve2D}.
     * @since 1.2
     */
    public abstract double getX1();

    /**
     * Returns the Y coordinate of the start point in double precision.
     * @return the Y coordinate of the start point of the
     *         {@code CubicCurve2D}.
     * @since 1.2
     */
    public abstract double getY1();

    /**
     * Returns the start point.
     * @return a {@code Point2D} that is the start point of
     *         the {@code CubicCurve2D}.
     * @since 1.2
     */
    public abstract Point2D getP1();

    /**
     * Returns the X coordinate of the first control point in double precision.
     * @return the X coordinate of the first control point of the
     *         {@code CubicCurve2D}.
     * @since 1.2
     */
    public abstract double getCtrlX1();

    /**
     * Returns the Y coordinate of the first control point in double precision.
     * @return the Y coordinate of the first control point of the
     *         {@code CubicCurve2D}.
     * @since 1.2
     */
    public abstract double getCtrlY1();

    /**
     * Returns the first control point.
     * @return a {@code Point2D} that is the first control point of
     *         the {@code CubicCurve2D}.
     * @since 1.2
     */
    public abstract Point2D getCtrlP1();

    /**
     * Returns the X coordinate of the second control point
     * in double precision.
     * @return the X coordinate of the second control point of the
     *         {@code CubicCurve2D}.
     * @since 1.2
     */
    public abstract double getCtrlX2();

    /**
     * Returns the Y coordinate of the second control point
     * in double precision.
     * @return the Y coordinate of the second control point of the
     *         {@code CubicCurve2D}.
     * @since 1.2
     */
    public abstract double getCtrlY2();

    /**
     * Returns the second control point.
     * @return a {@code Point2D} that is the second control point of
     *         the {@code CubicCurve2D}.
     * @since 1.2
     */
    public abstract Point2D getCtrlP2();

    /**
     * Returns the X coordinate of the end point in double precision.
     * @return the X coordinate of the end point of the
     *         {@code CubicCurve2D}.
     * @since 1.2
     */
    public abstract double getX2();

    /**
     * Returns the Y coordinate of the end point in double precision.
     * @return the Y coordinate of the end point of the
     *         {@code CubicCurve2D}.
     * @since 1.2
     */
    public abstract double getY2();

    /**
     * Returns the end point.
     * @return a {@code Point2D} that is the end point of
     *         the {@code CubicCurve2D}.
     * @since 1.2
     */
    public abstract Point2D getP2();

    /**
     * Sets the location of the end points and control points of this curve
     * to the specified double coordinates.
     *
     * @param x1 the X coordinate used to set the start point
     *           of this {@code CubicCurve2D}
     * @param y1 the Y coordinate used to set the start point
     *           of this {@code CubicCurve2D}
     * @param ctrlx1 the X coordinate used to set the first control point
     *               of this {@code CubicCurve2D}
     * @param ctrly1 the Y coordinate used to set the first control point
     *               of this {@code CubicCurve2D}
     * @param ctrlx2 the X coordinate used to set the second control point
     *               of this {@code CubicCurve2D}
     * @param ctrly2 the Y coordinate used to set the second control point
     *               of this {@code CubicCurve2D}
     * @param x2 the X coordinate used to set the end point
     *           of this {@code CubicCurve2D}
     * @param y2 the Y coordinate used to set the end point
     *           of this {@code CubicCurve2D}
     * @since 1.2
     */
    public abstract void setCurve(double x1, double y1,
                                  double ctrlx1, double ctrly1,
                                  double ctrlx2, double ctrly2,
                                  double x2, double y2);

    /**
     * Sets the location of the end points and control points of this curve
     * to the double coordinates at the specified offset in the specified
     * array.
     * @param coords a double array containing coordinates
     * @param offset the index of <code>coords</code> from which to begin
     *          setting the end points and control points of this curve
     *          to the coordinates contained in <code>coords</code>
     * @since 1.2
     */
    public void setCurve(double[] coords, int offset) {
        setCurve(coords[offset + 0], coords[offset + 1],
                 coords[offset + 2], coords[offset + 3],
                 coords[offset + 4], coords[offset + 5],
                 coords[offset + 6], coords[offset + 7]);
    }

    /**
     * Sets the location of the end points and control points of this curve
     * to the specified <code>Point2D</code> coordinates.
     * @param p1 the first specified <code>Point2D</code> used to set the
     *          start point of this curve
     * @param cp1 the second specified <code>Point2D</code> used to set the
     *          first control point of this curve
     * @param cp2 the third specified <code>Point2D</code> used to set the
     *          second control point of this curve
     * @param p2 the fourth specified <code>Point2D</code> used to set the
     *          end point of this curve
     * @since 1.2
     */
    public void setCurve(Point2D p1, Point2D cp1, Point2D cp2, Point2D p2) {
        setCurve(p1.getX(), p1.getY(), cp1.getX(), cp1.getY(),
                 cp2.getX(), cp2.getY(), p2.getX(), p2.getY());
    }

    /**
     * Sets the location of the end points and control points of this curve
     * to the coordinates of the <code>Point2D</code> objects at the specified
     * offset in the specified array.
     * @param pts an array of <code>Point2D</code> objects
     * @param offset  the index of <code>pts</code> from which to begin setting
     *          the end points and control points of this curve to the
     *          points contained in <code>pts</code>
     * @since 1.2
     */
    public void setCurve(Point2D[] pts, int offset) {
        setCurve(pts[offset + 0].getX(), pts[offset + 0].getY(),
                 pts[offset + 1].getX(), pts[offset + 1].getY(),
                 pts[offset + 2].getX(), pts[offset + 2].getY(),
                 pts[offset + 3].getX(), pts[offset + 3].getY());
    }

    /**
     * Sets the location of the end points and control points of this curve
     * to the same as those in the specified <code>CubicCurve2D</code>.
     * @param c the specified <code>CubicCurve2D</code>
     * @since 1.2
     */
    public void setCurve(CubicCurve2D c) {
        setCurve(c.getX1(), c.getY1(), c.getCtrlX1(), c.getCtrlY1(),
                 c.getCtrlX2(), c.getCtrlY2(), c.getX2(), c.getY2());
    }

    /**
     * Returns the square of the flatness of the cubic curve specified
     * by the indicated control points. The flatness is the maximum distance
     * of a control point from the line connecting the end points.
     *
     * @param x1 the X coordinate that specifies the start point
     *           of a {@code CubicCurve2D}
     * @param y1 the Y coordinate that specifies the start point
     *           of a {@code CubicCurve2D}
     * @param ctrlx1 the X coordinate that specifies the first control point
     *               of a {@code CubicCurve2D}
     * @param ctrly1 the Y coordinate that specifies the first control point
     *               of a {@code CubicCurve2D}
     * @param ctrlx2 the X coordinate that specifies the second control point
     *               of a {@code CubicCurve2D}
     * @param ctrly2 the Y coordinate that specifies the second control point
     *               of a {@code CubicCurve2D}
     * @param x2 the X coordinate that specifies the end point
     *           of a {@code CubicCurve2D}
     * @param y2 the Y coordinate that specifies the end point
     *           of a {@code CubicCurve2D}
     * @return the square of the flatness of the {@code CubicCurve2D}
     *          represented by the specified coordinates.
     * @since 1.2
     */
    public static double getFlatnessSq(double x1, double y1,
                                       double ctrlx1, double ctrly1,
                                       double ctrlx2, double ctrly2,
                                       double x2, double y2) {
		return max(Line2D.ptSegDistSq(x1, y1, x2, y2, ctrlx1, ctrly1),
                        Line2D.ptSegDistSq(x1, y1, x2, y2, ctrlx2, ctrly2));

    }

    /**
     * Returns the flatness of the cubic curve specified
     * by the indicated control points. The flatness is the maximum distance
     * of a control point from the line connecting the end points.
     *
     * @param x1 the X coordinate that specifies the start point
     *           of a {@code CubicCurve2D}
     * @param y1 the Y coordinate that specifies the start point
     *           of a {@code CubicCurve2D}
     * @param ctrlx1 the X coordinate that specifies the first control point
     *               of a {@code CubicCurve2D}
     * @param ctrly1 the Y coordinate that specifies the first control point
     *               of a {@code CubicCurve2D}
     * @param ctrlx2 the X coordinate that specifies the second control point
     *               of a {@code CubicCurve2D}
     * @param ctrly2 the Y coordinate that specifies the second control point
     *               of a {@code CubicCurve2D}
     * @param x2 the X coordinate that specifies the end point
     *           of a {@code CubicCurve2D}
     * @param y2 the Y coordinate that specifies the end point
     *           of a {@code CubicCurve2D}
     * @return the flatness of the {@code CubicCurve2D}
     *          represented by the specified coordinates.
     * @since 1.2
     */
    public static double getFlatness(double x1, double y1,
                                     double ctrlx1, double ctrly1,
                                     double ctrlx2, double ctrly2,
                                     double x2, double y2) {
        return Math.sqrt(getFlatnessSq(x1, y1, ctrlx1, ctrly1,
                                       ctrlx2, ctrly2, x2, y2));
    }

    /**
     * Returns the square of the flatness of the cubic curve specified
     * by the control points stored in the indicated array at the
     * indicated index. The flatness is the maximum distance
     * of a control point from the line connecting the end points.
     * @param coords an array containing coordinates
     * @param offset the index of <code>coords</code> from which to begin
     *          getting the end points and control points of the curve
     * @return the square of the flatness of the <code>CubicCurve2D</code>
     *          specified by the coordinates in <code>coords</code> at
     *          the specified offset.
     * @since 1.2
     */
    public static double getFlatnessSq(double coords[], int offset) {
        return getFlatnessSq(coords[offset + 0], coords[offset + 1],
                             coords[offset + 2], coords[offset + 3],
                             coords[offset + 4], coords[offset + 5],
                             coords[offset + 6], coords[offset + 7]);
    }

    /**
     * Returns the flatness of the cubic curve specified
     * by the control points stored in the indicated array at the
     * indicated index.  The flatness is the maximum distance
     * of a control point from the line connecting the end points.
     * @param coords an array containing coordinates
     * @param offset the index of <code>coords</code> from which to begin
     *          getting the end points and control points of the curve
     * @return the flatness of the <code>CubicCurve2D</code>
     *          specified by the coordinates in <code>coords</code> at
     *          the specified offset.
     * @since 1.2
     */
    public static double getFlatness(double coords[], int offset) {
        return getFlatness(coords[offset + 0], coords[offset + 1],
                           coords[offset + 2], coords[offset + 3],
                           coords[offset + 4], coords[offset + 5],
                           coords[offset + 6], coords[offset + 7]);
    }

    /**
     * Returns the square of the flatness of this curve.  The flatness is the
     * maximum distance of a control point from the line connecting the
     * end points.
     * @return the square of the flatness of this curve.
     * @since 1.2
     */
    public double getFlatnessSq() {
        return getFlatnessSq(getX1(), getY1(), getCtrlX1(), getCtrlY1(),
                             getCtrlX2(), getCtrlY2(), getX2(), getY2());
    }

    /**
     * Returns the flatness of this curve.  The flatness is the
     * maximum distance of a control point from the line connecting the
     * end points.
     * @return the flatness of this curve.
     * @since 1.2
     */
    public double getFlatness() {
        return getFlatness(getX1(), getY1(), getCtrlX1(), getCtrlY1(),
                           getCtrlX2(), getCtrlY2(), getX2(), getY2());
    }

    /**
     * Subdivides this cubic curve and stores the resulting two
     * subdivided curves into the left and right curve parameters.
     * Either or both of the left and right objects may be the same
     * as this object or null.
     * @param left the cubic curve object for storing for the left or
     * first half of the subdivided curve
     * @param right the cubic curve object for storing for the right or
     * second half of the subdivided curve
     * @since 1.2
     */
    public void subdivide(CubicCurve2D left, CubicCurve2D right) {
        subdivide(this, left, right);
    }

    /**
     * Subdivides the cubic curve specified by the <code>src</code> parameter
     * and stores the resulting two subdivided curves into the
     * <code>left</code> and <code>right</code> curve parameters.
     * Either or both of the <code>left</code> and <code>right</code> objects
     * may be the same as the <code>src</code> object or <code>null</code>.
     * @param src the cubic curve to be subdivided
     * @param left the cubic curve object for storing the left or
     * first half of the subdivided curve
     * @param right the cubic curve object for storing the right or
     * second half of the subdivided curve
     * @since 1.2
     */
    public static void subdivide(CubicCurve2D src,
                                 CubicCurve2D left,
                                 CubicCurve2D right) {
        double x1 = src.getX1();
        double y1 = src.getY1();
        double ctrlx1 = src.getCtrlX1();
        double ctrly1 = src.getCtrlY1();
        double ctrlx2 = src.getCtrlX2();
        double ctrly2 = src.getCtrlY2();
        double x2 = src.getX2();
        double y2 = src.getY2();
        double centerx = (ctrlx1 + ctrlx2) / 2.0;
        double centery = (ctrly1 + ctrly2) / 2.0;
        ctrlx1 = (x1 + ctrlx1) / 2.0;
        ctrly1 = (y1 + ctrly1) / 2.0;
        ctrlx2 = (x2 + ctrlx2) / 2.0;
        ctrly2 = (y2 + ctrly2) / 2.0;
        double ctrlx12 = (ctrlx1 + centerx) / 2.0;
        double ctrly12 = (ctrly1 + centery) / 2.0;
        double ctrlx21 = (ctrlx2 + centerx) / 2.0;
        double ctrly21 = (ctrly2 + centery) / 2.0;
        centerx = (ctrlx12 + ctrlx21) / 2.0;
        centery = (ctrly12 + ctrly21) / 2.0;
        if (left != null) {
            left.setCurve(x1, y1, ctrlx1, ctrly1,
                          ctrlx12, ctrly12, centerx, centery);
        }
        if (right != null) {
            right.setCurve(centerx, centery, ctrlx21, ctrly21,
                           ctrlx2, ctrly2, x2, y2);
        }
    }

    /**
     * Subdivides the cubic curve specified by the coordinates
     * stored in the <code>src</code> array at indices <code>srcoff</code>
     * through (<code>srcoff</code>&nbsp;+&nbsp;7) and stores the
     * resulting two subdivided curves into the two result arrays at the
     * corresponding indices.
     * Either or both of the <code>left</code> and <code>right</code>
     * arrays may be <code>null</code> or a reference to the same array
     * as the <code>src</code> array.
     * Note that the last point in the first subdivided curve is the
     * same as the first point in the second subdivided curve. Thus,
     * it is possible to pass the same array for <code>left</code>
     * and <code>right</code> and to use offsets, such as <code>rightoff</code>
     * equals (<code>leftoff</code> + 6), in order
     * to avoid allocating extra storage for this common point.
     * @param src the array holding the coordinates for the source curve
     * @param srcoff the offset into the array of the beginning of the
     * the 6 source coordinates
     * @param left the array for storing the coordinates for the first
     * half of the subdivided curve
     * @param leftoff the offset into the array of the beginning of the
     * the 6 left coordinates
     * @param right the array for storing the coordinates for the second
     * half of the subdivided curve
     * @param rightoff the offset into the array of the beginning of the
     * the 6 right coordinates
     * @since 1.2
     */
    public static void subdivide(double src[], int srcoff,
                                 double left[], int leftoff,
                                 double right[], int rightoff) {
        double x1 = src[srcoff + 0];
        double y1 = src[srcoff + 1];
        double ctrlx1 = src[srcoff + 2];
        double ctrly1 = src[srcoff + 3];
        double ctrlx2 = src[srcoff + 4];
        double ctrly2 = src[srcoff + 5];
        double x2 = src[srcoff + 6];
        double y2 = src[srcoff + 7];
        if (left != null) {
            left[leftoff + 0] = x1;
            left[leftoff + 1] = y1;
        }
        if (right != null) {
            right[rightoff + 6] = x2;
            right[rightoff + 7] = y2;
        }
        x1 = (x1 + ctrlx1) / 2.0;
        y1 = (y1 + ctrly1) / 2.0;
        x2 = (x2 + ctrlx2) / 2.0;
        y2 = (y2 + ctrly2) / 2.0;
        double centerx = (ctrlx1 + ctrlx2) / 2.0;
        double centery = (ctrly1 + ctrly2) / 2.0;
        ctrlx1 = (x1 + centerx) / 2.0;
        ctrly1 = (y1 + centery) / 2.0;
        ctrlx2 = (x2 + centerx) / 2.0;
        ctrly2 = (y2 + centery) / 2.0;
        centerx = (ctrlx1 + ctrlx2) / 2.0;
        centery = (ctrly1 + ctrly2) / 2.0;
        if (left != null) {
            left[leftoff + 2] = x1;
            left[leftoff + 3] = y1;
            left[leftoff + 4] = ctrlx1;
            left[leftoff + 5] = ctrly1;
            left[leftoff + 6] = centerx;
            left[leftoff + 7] = centery;
        }
        if (right != null) {
            right[rightoff + 0] = centerx;
            right[rightoff + 1] = centery;
            right[rightoff + 2] = ctrlx2;
            right[rightoff + 3] = ctrly2;
            right[rightoff + 4] = x2;
            right[rightoff + 5] = y2;
        }
    }



    /**
     * {@inheritDoc}
     * @since 1.2
     */
    @Override
	public boolean contains(double x, double y) {
        if (!(x * 0.0 + y * 0.0 == 0.0)) {
            /* Either x or y was infinite or NaN.
             * A NaN always produces a negative response to any test
             * and Infinity values cannot be "inside" any path so
             * they should return false as well.
             */
            return false;
        }
        // We count the "Y" crossings to determine if the point is
        // inside the curve bounded by its closing line.
        double x1 = getX1();
        double y1 = getY1();
        double x2 = getX2();
        double y2 = getY2();
        int crossings =
            (Curve.pointCrossingsForLine(x, y, x1, y1, x2, y2) +
             Curve.pointCrossingsForCubic(x, y,
                                          x1, y1,
                                          getCtrlX1(), getCtrlY1(),
                                          getCtrlX2(), getCtrlY2(),
                                          x2, y2, 0));
        return ((crossings & 1) == 1);
    }

    /**
     * {@inheritDoc}
     * @since 1.2
     */
    public boolean contains(Point2D p) {
        return contains(p.getX(), p.getY());
    }

    /**
     * {@inheritDoc}
     * @since 1.2
     */
    @Override
	public boolean intersects(double x, double y, double w, double h) {
        // Trivially reject non-existant rectangles
        if (w <= 0 || h <= 0) {
            return false;
        }

        int numCrossings = rectCrossings(x, y, w, h);
        // the intended return value is
        // numCrossings != 0 || numCrossings == Curve.RECT_INTERSECTS
        // but if (numCrossings != 0) numCrossings == INTERSECTS won't matter
        // and if !(numCrossings != 0) then numCrossings == 0, so
        // numCrossings != RECT_INTERSECT
        return numCrossings != 0;
    }

    /**
     * {@inheritDoc}
     * @since 1.2
     */
    public boolean intersects(Rectangle2D r) {
        return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    /**
     * {@inheritDoc}
     * @since 1.2
     */
    public boolean contains(double x, double y, double w, double h) {
        if (w <= 0 || h <= 0) {
            return false;
        }

        int numCrossings = rectCrossings(x, y, w, h);
        return !(numCrossings == 0 || numCrossings == Curve.RECT_INTERSECTS);
    }

    private int rectCrossings(double x, double y, double w, double h) {
        int crossings = 0;
        if (!(getX1() == getX2() && getY1() == getY2())) {
            crossings = Curve.rectCrossingsForLine(crossings,
                                                   x, y,
                                                   x+w, y+h,
                                                   getX1(), getY1(),
                                                   getX2(), getY2());
            if (crossings == Curve.RECT_INTERSECTS) {
                return crossings;
            }
        }
        // we call this with the curve's direction reversed, because we wanted
        // to call rectCrossingsForLine first, because it's cheaper.
        return Curve.rectCrossingsForCubic(crossings,
                                           x, y,
                                           x+w, y+h,
                                           getX2(), getY2(),
                                           getCtrlX2(), getCtrlY2(),
                                           getCtrlX1(), getCtrlY1(),
                                           getX1(), getY1(), 0);
    }

    /**
     * {@inheritDoc}
     * @since 1.2
     */
    public boolean contains(Rectangle2D r) {
        return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    /**
     * {@inheritDoc}
     * @since 1.2
     */
    @Override
	public GRectangle getBounds() {
        return getBounds2D().getBounds();
    }

    /**
     * Returns an iteration object that defines the boundary of the
     * shape.
     * The iterator for this class is not multi-threaded safe,
     * which means that this <code>CubicCurve2D</code> class does not
     * guarantee that modifications to the geometry of this
     * <code>CubicCurve2D</code> object do not affect any iterations of
     * that geometry that are already in process.
     * @param at an optional <code>AffineTransform</code> to be applied to the
     * coordinates as they are returned in the iteration, or <code>null</code>
     * if untransformed coordinates are desired
     * @return    the <code>PathIterator</code> object that returns the
     *          geometry of the outline of this <code>CubicCurve2D</code>, one
     *          segment at a time.
     * @since 1.2
     */
    @Override
	public GPathIterator getPathIterator(GAffineTransform at) {
        return new CubicIterator(this, at);
    }

    /**
     * Return an iteration object that defines the boundary of the
     * flattened shape.
     * The iterator for this class is not multi-threaded safe,
     * which means that this <code>CubicCurve2D</code> class does not
     * guarantee that modifications to the geometry of this
     * <code>CubicCurve2D</code> object do not affect any iterations of
     * that geometry that are already in process.
     * @param at an optional <code>AffineTransform</code> to be applied to the
     * coordinates as they are returned in the iteration, or <code>null</code>
     * if untransformed coordinates are desired
     * @param flatness the maximum amount that the control points
     * for a given curve can vary from colinear before a subdivided
     * curve is replaced by a straight line connecting the end points
     * @return    the <code>PathIterator</code> object that returns the
     * geometry of the outline of this <code>CubicCurve2D</code>,
     * one segment at a time.
     * @since 1.2
     */
    public GPathIterator getPathIterator(GAffineTransform at, double flatness) {
        return new FlatteningPathIterator(getPathIterator(at), flatness);
    }
    

	@Override
	public boolean intersects(int x, int y, int w, int h) {
		return intersects((double)x, (double)y, (double)w, (double)h);
	}

	@Override
	public boolean contains(int x, int y) {
		return contains((double)x, (double)y);
	}

	@Override
    public boolean contains(GRectangle2D r) {
        return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

	@Override
    public boolean intersects(GRectangle2D r) {
        return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

}