/*
 * @(#)Point2D.java	1.18 03/12/19
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
 * The <code>Point2D</code> class defines a point representing a location in
 * (x,&nbsp;y) coordinate space.
 * <p>
 * This class is only the abstract superclass for all objects that store a 2D
 * coordinate. The actual storage representation of the coordinates is left to
 * the subclass.
 *
 * @version 1.18, 12/19/03
 * @author Jim Graham
 */
public abstract class Point2D {
	/**
	 * The <code>Double</code> class defines a point specified in
	 * <code>double</code> precision.
	 */
	public static class Double extends Point2D {
		/**
		 * The X coordinate of this <code>Point2D</code>.
		 *
		 * @since 1.2
		 */
		public double x;

		/**
		 * The Y coordinate of this <code>Point2D</code>.
		 *
		 * @since 1.2
		 */
		public double y;

		/**
		 * Constructs and initializes a <code>Point2D</code> with coordinates
		 * (0,&nbsp;0).
		 *
		 * @since 1.2
		 */
		public Double() {
		}

		/**
		 * Constructs and initializes a <code>Point2D</code> with the specified
		 * coordinates.
		 *
		 * @param x
		 *            ,&nbsp;y the coordinates to which to set the newly
		 *            constructed <code>Point2D</code>
		 * @since 1.2
		 */
		public Double(double x, double y) {
			this.x = x;
			this.y = y;
		}

		/**
		 * Returns the X coordinate of this <code>Point2D</code> in
		 * <code>double</code> precision.
		 *
		 * @return the X coordinate of this <code>Point2D</code>.
		 * @since 1.2
		 */
		public double getX() {
			return x;
		}

		/**
		 * Returns the Y coordinate of this <code>Point2D</code> in
		 * <code>double</code> precision.
		 *
		 * @return the Y coordinate of this <code>Point2D</code>.
		 * @since 1.2
		 */
		public double getY() {
			return y;
		}

		/**
		 * Sets the location of this <code>Point2D</code> to the specified
		 * <code>double</code> coordinates.
		 *
		 * @param x
		 *            ,&nbsp;y the coordinates to which to set this
		 *            <code>Point2D</code>
		 * @since 1.2
		 */
		public void setLocation(double x, double y) {
			this.x = x;
			this.y = y;
		}

		/**
		 * Returns a <code>String</code> that represents the value of this
		 * <code>Point2D</code>.
		 *
		 * @return a string representation of this <code>Point2D</code>.
		 * @since 1.2
		 */
		public String toString() {
			return "Point2D.Double[" + x + ", " + y + "]";
		}

		//@Override
		//public Object duplicate() {
		//	return new Point2D.Double(this.x, this.y);
		//}
		
		
	}

	/**
	 * The <code>Float</code> class defines a point specified in float
	 * precision.
	 */
	public static class Float extends Point2D {
		/**
		 * The X coordinate of this <code>Point2D</code>.
		 *
		 * @since 1.2
		 */
		public float x;

		/**
		 * The Y coordinate of this <code>Point2D</code>.
		 *
		 * @since 1.2
		 */
		public float y;

		/**
		 * Constructs and initializes a <code>Point2D</code> with coordinates
		 * (0,&nbsp;0).
		 *
		 * @since 1.2
		 */
		public Float() {
		}

		/**
		 * Constructs and initializes a <code>Point2D</code> with the specified
		 * coordinates.
		 *
		 * @param x
		 *            ,&nbsp;y the coordinates to which to set the newly
		 *            constructed <code>Point2D</code>
		 * @since 1.2
		 */
		public Float(float x, float y) {
			this.x = x;
			this.y = y;
		}

		/**
		 * Returns the X coordinate of this <code>Point2D</code> in
		 * <code>double</code> precision.
		 *
		 * @return the X coordinate of this <code>Point2D</code>.
		 * @since 1.2
		 */
		public double getX() {
			return (double) x;
		}

		/**
		 * Returns the Y coordinate of this <code>Point2D</code> in
		 * <code>double</code> precision.
		 *
		 * @return the Y coordinate of this <code>Point2D</code>.
		 * @since 1.2
		 */
		public double getY() {
			return (double) y;
		}

		/**
		 * Sets the location of this <code>Point2D</code> to the specified
		 * <code>double</code> coordinates.
		 *
		 * @param x
		 *            ,&nbsp;y the coordinates to which to set this
		 *            <code>Point2D</code>
		 * @since 1.2
		 */
		public void setLocation(double x, double y) {
			this.x = (float) x;
			this.y = (float) y;
		}

		/**
		 * Sets the location of this <code>Point2D</code> to the specified
		 * <code>float</code> coordinates.
		 *
		 * @param x
		 *            ,&nbsp;y the coordinates to which to set this
		 *            <code>Point2D</code>
		 * @since 1.2
		 */
		public void setLocation(float x, float y) {
			this.x = x;
			this.y = y;
		}

		/**
		 * Returns a <code>String</code> that represents the value of this
		 * <code>Point2D</code>.
		 *
		 * @return a string representation of this <code>Point2D</code>.
		 * @since 1.2
		 */
		public String toString() {
			return "Point2D.Float[" + x + ", " + y + "]";
		}

		//@Override
		//public Object duplicate() {
		//	return new Point2D.Float(this.x, this.y);
		//}
	}

	/**
	 * Returns the distance between two points.
	 *
	 * @param X1
	 *            ,&nbsp;Y1 the coordinates of the first point
	 * @param X2
	 *            ,&nbsp;Y2 the coordinates of the second point
	 * @return the distance between the two sets of specified coordinates.
	 */
	public static double distance(double X1, double Y1, double X2, double Y2) {
		X1 -= X2;
		Y1 -= Y2;
		return Math.sqrt(X1 * X1 + Y1 * Y1);
	}

	/**
	 * Returns the square of the distance between two points.
	 *
	 * @param X1
	 *            ,&nbsp;Y1 the coordinates of the first point
	 * @param X2
	 *            ,&nbsp;Y2 the coordinates of the second point
	 * @return the square of the distance between the two sets of specified
	 *         coordinates.
	 */
	public static double distanceSq(double X1, double Y1, double X2, double Y2) {
		X1 -= X2;
		Y1 -= Y2;
		return (X1 * X1 + Y1 * Y1);
	}

	/**
	 * This is an abstract class that cannot be instantiated directly.
	 * Type-specific implementation subclasses are available for instantiation
	 * and provide a number of formats for storing the information necessary to
	 * satisfy the various accessor methods below.
	 *
	 * @see java.awt.geom.Point2D.Float
	 * @see java.awt.geom.Point2D.Double
	 * @see java.awt.Point
	 */
	protected Point2D() {
	}

	/**
	 * Returns the distance from this <code>Point2D</code> to a specified point.
	 *
	 * @param PX
	 *            ,&nbsp;PY the coordinates of the specified
	 *            <code>Point2D</code>
	 * @return the distance between this <code>Point2D</code> and a specified
	 *         point.
	 */
	public double distance(double PX, double PY) {
		PX -= getX();
		PY -= getY();
		return Math.sqrt(PX * PX + PY * PY);
	}

	/**
	 * Returns the distance from this <code>Point2D</code> to a specified
	 * <code>Point2D</code>.
	 *
	 * @param pt
	 *            the specified <code>Point2D</code>
	 * @return the distance between this <code>Point2D</code> and the specified
	 *         <code>Point2D</code>.
	 */
	public double distance(Point2D pt) {
		double PX = pt.getX() - this.getX();
		double PY = pt.getY() - this.getY();
		return Math.sqrt(PX * PX + PY * PY);
	}

	/**
	 * Returns the square of the distance from this <code>Point2D</code> to a
	 * specified point.
	 *
	 * @param PX
	 *            ,&nbsp;PY the coordinates of the other point
	 * @return the square of the distance between this <code>Point2D</code> and
	 *         the specified point.
	 */
	public double distanceSq(double PX, double PY) {
		PX -= getX();
		PY -= getY();
		return (PX * PX + PY * PY);
	}

	/**
	 * Returns the square of the distance from this <code>Point2D</code> to a
	 * specified <code>Point2D</code>.
	 *
	 * @param pt
	 *            the specified <code>Point2D</code>
	 * @return the square of the distance between this <code>Point2D</code> to a
	 *         specified <code>Point2D</code>.
	 */
	public double distanceSq(Point2D pt) {
		double PX = pt.getX() - this.getX();
		double PY = pt.getY() - this.getY();
		return (PX * PX + PY * PY);
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
	 * @since 1.2
	 */
	public boolean equals(Object obj) {
		if (obj instanceof Point2D) {
			Point2D p2d = (Point2D) obj;
			return (getX() == p2d.getX()) && (getY() == p2d.getY());
		}
		return super.equals(obj);
	}

	/**
	 * Returns the X coordinate of this <code>Point2D</code> in
	 * <code>double</code> precision.
	 *
	 * @return the X coordinate of this <code>Point2D</code>.
	 * @since 1.2
	 */
	public abstract double getX();

	/**
	 * Returns the Y coordinate of this <code>Point2D</code> in
	 * <code>double</code> precision.
	 *
	 * @return the Y coordinate of this <code>Point2D</code>.
	 * @since 1.2
	 */
	public abstract double getY();

	/**
	 * Sets the location of this <code>Point2D</code> to the specified
	 * <code>double</code> coordinates.
	 *
	 * @param x
	 *            ,&nbsp;y the coordinates of this <code>Point2D</code>
	 * @since 1.2
	 */
	public abstract void setLocation(double x, double y);

	/**
	 * Sets the location of this <code>Point2D</code> to the same coordinates as
	 * the specified <code>Point2D</code> object.
	 *
	 * @param p
	 *            the specified <code>Point2D</code> the which to set this
	 *            <code>Point2D</code>
	 * @since 1.2
	 */
	public void setLocation(Point2D p) {
		setLocation(p.getX(), p.getY());
	}
	
	
}
