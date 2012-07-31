/*
 * @(#)Point.java	1.38 03/12/19
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

package geogebra.web.openjdk.awt.geom;


/**
 * A point representing a location in (x, y) coordinate space, specified in
 * integer precision.
 * 
 * @version 1.38, 12/19/03
 * @author Sami Shaio
 * @since JDK1.0
 */
public class Point extends Point2D {
	/**
	 * The <i>x</i> coordinate. If no <i>x</i> coordinate is set it will default
	 * to 0.
	 * 
	 * @serial
	 * @see #getLocation()
	 * @see #move(int, int)
	 */
	public int x;

	/**
	 * The <i>y</i> coordinate. If no <i>y</i> coordinate is set it will default
	 * to 0.
	 * 
	 * @serial
	 * @see #getLocation()
	 * @see #move(int, int)
	 */
	public int y;

	/*
	 * JDK 1.1 serialVersionUID
	 */
	private static final long serialVersionUID = -5276940640259749850L;

	/**
	 * Constructs and initializes a point at the origin (0,&nbsp;0) of the
	 * coordinate space.
	 * 
	 * @since JDK1.1
	 */
	public Point() {
		this(0, 0);
	}

	/**
	 * Constructs and initializes a point with the same location as the
	 * specified <code>Point</code> object.
	 * 
	 * @param p
	 *            a point
	 * @since JDK1.1
	 */
	public Point(Point p) {
		this(p.x, p.y);
	}

	/**
	 * Constructs and initializes a point at the specified
	 * (<i>x</i>,&nbsp;<i>y</i>) location in the coordinate space.
	 * 
	 * @param x
	 *            the <i>x</i> coordinate
	 * @param y
	 *            the <i>y</i> coordinate
	 */
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns the X coordinate of the point in double precision.
	 * 
	 * @return the X coordinate of the point in double precision
	 */
	public double getX() {
		return x;
	}

	/**
	 * Returns the Y coordinate of the point in double precision.
	 * 
	 * @return the Y coordinate of the point in double precision
	 */
	public double getY() {
		return y;
	}

	/**
	 * Returns the location of this point. This method is included for
	 * completeness, to parallel the <code>getLocation</code> method of
	 * <code>Component</code>.
	 * 
	 * @return a copy of this point, at the same location
	 * @see java.awt.Component#getLocation
	 * @see java.awt.Point#setLocation(java.awt.Point)
	 * @see java.awt.Point#setLocation(int, int)
	 * @since JDK1.1
	 */
	public Point getLocation() {
		return new Point(x, y);
	}

	/**
	 * Sets the location of the point to the specified location. This method is
	 * included for completeness, to parallel the <code>setLocation</code>
	 * method of <code>Component</code>.
	 * 
	 * @param p
	 *            a point, the new location for this point
	 * @see java.awt.Component#setLocation(java.awt.Point)
	 * @see java.awt.Point#getLocation
	 * @since JDK1.1
	 */
	public void setLocation(Point p) {
		setLocation(p.x, p.y);
	}

	/**
	 * Changes the point to have the specified location.
	 * <p>
	 * This method is included for completeness, to parallel the
	 * <code>setLocation</code> method of <code>Component</code>. Its behavior
	 * is identical with <code>move(int,&nbsp;int)</code>.
	 * 
	 * @param x
	 *            the <i>x</i> coordinate of the new location
	 * @param y
	 *            the <i>y</i> coordinate of the new location
	 * @see java.awt.Component#setLocation(int, int)
	 * @see java.awt.Point#getLocation
	 * @see java.awt.Point#move(int, int)
	 * @since JDK1.1
	 */
	public void setLocation(int x, int y) {
		move(x, y);
	}

	/**
	 * Sets the location of this point to the specified double coordinates. The
	 * double values will be rounded to integer values. Any number smaller than
	 * <code>Integer.MIN_VALUE</code> will be reset to <code>MIN_VALUE</code>,
	 * and any number larger than <code>Integer.MAX_VALUE</code> will be reset
	 * to <code>MAX_VALUE</code>.
	 * 
	 * @param x
	 *            the <i>x</i> coordinate of the new location
	 * @param y
	 *            the <i>y</i> coordinate of the new location
	 * @see #getLocation
	 */
	public void setLocation(double x, double y) {
		this.x = (int) Math.floor(x + 0.5);
		this.y = (int) Math.floor(y + 0.5);
	}

	/**
	 * Moves this point to the specified location in the
	 * (<i>x</i>,&nbsp;<i>y</i>) coordinate plane. This method is identical with
	 * <code>setLocation(int,&nbsp;int)</code>.
	 * 
	 * @param x
	 *            the <i>x</i> coordinate of the new location
	 * @param y
	 *            the <i>y</i> coordinate of the new location
	 * @see java.awt.Component#setLocation(int, int)
	 */
	public void move(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Translates this point, at location (<i>x</i>,&nbsp;<i>y</i>), by
	 * <code>dx</code> along the <i>x</i> axis and <code>dy</code> along the
	 * <i>y</i> axis so that it now represents the point (<code>x</code>&nbsp;
	 * <code>+</code>&nbsp;<code>dx</code>, <code>y</code>&nbsp;<code>+</code>
	 * &nbsp;<code>dy</code>).
	 * 
	 * @param dx
	 *            the distance to move this point along the <i>x</i> axis
	 * @param dy
	 *            the distance to move this point along the <i>y</i> axis
	 */
	public void translate(int dx, int dy) {
		this.x += dx;
		this.y += dy;
	}

	/**
	 * Determines whether or not two points are equal. Two instances of
	 * <code>Point2D</code> are equal if the values of their <code>x</code> and
	 * <code>y</code> member fields, representing their position in the
	 * coordinate space, are the same.
	 * 
	 * @param obj
	 *            an object to be compared with this <code>Point2D</code>
	 * @return <code>true</code> if the object to be compared is an instance of
	 *         <code>Point2D</code> and has the same values; <code>false</code>
	 *         otherwise.
	 */
	public boolean equals(Object obj) {
		if (obj instanceof Point) {
			Point pt = (Point) obj;
			return (x == pt.x) && (y == pt.y);
		}
		return super.equals(obj);
	}

	/**
	 * Returns a string representation of this point and its location in the
	 * (<i>x</i>,&nbsp;<i>y</i>) coordinate space. This method is intended to be
	 * used only for debugging purposes, and the content and format of the
	 * returned string may vary between implementations. The returned string may
	 * be empty but may not be <code>null</code>.
	 * 
	 * @return a string representation of this point
	 */
	public String toString() {
		return getClass().getName() + "[x=" + x + ",y=" + y + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.davetrudes.jung.client.util.GWTCloneable#duplicate()
	 */
	//@Override
	//public Object duplicate() {
	//	return new Point(this.x, this.y);
	//}

}
