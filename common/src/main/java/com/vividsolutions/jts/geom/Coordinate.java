/*
* The JTS Topology Suite is a collection of Java classes that
* implement the fundamental operations required to validate a given
* geo-spatial data set to a known topological specification.
*
* Copyright (C) 2001 Vivid Solutions
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*
* For more information, contact:
*
*     Vivid Solutions
*     Suite #1A
*     2328 Government Street
*     Victoria BC  V8T 5G5
*     Canada
*
*     (250)385-6040
*     www.vividsolutions.com
 */
package com.vividsolutions.jts.geom;

import java.io.Serializable;
import java.util.Comparator;
import com.vividsolutions.jts.util.Assert;


/**
 * A lightweight class used to store coordinates
 * on the 2-dimensional Cartesian plane.
 * It is distinct from {@link Point}, which is a subclass of {@link Geometry}. 
 * Unlike objects of type {@link Point} (which contain additional
 * information such as an envelope, a precision model, and spatial reference
 * system information), a <code>Coordinate</code> only contains ordinate values
 * and accessor methods. <P>
 *
 * <code>Coordinate</code>s are two-dimensional points, with an additional Z-ordinate. 
 * JTS does not support any operations on the Z-ordinate except the basic accessor functions. 
 * If an Z-ordinate value is not specified or not defined, 
 * constructed coordinates have a Z-ordinate of <code>NaN</code>
 * (which is also the value of <code>NULL_ORDINATE</code>).  
 * The standard comparison functions ignore the Z-ordinate.
 *
 *@version 1.7
 */
public class Coordinate implements Comparable, Cloneable, Serializable {
  private static final long serialVersionUID = 6683108902428366910L;
  
  /**
   * The value used to indicate a null or missing ordinate value.
   * In particular, used for the value of ordinates for dimensions 
   * greater than the defined dimension of a coordinate.
   */
  public static final double NULL_ORDINATE = Double.NaN;
  /**
   *  The x-coordinate.
   */
  public double x;
  /**
   *  The y-coordinate.
   */
  public double y;
  /**
   *  The z-coordinate.
   */
  public double z;

  /**
   *  Constructs a <code>Coordinate</code> at (x,y,z).
   *
   *@param  x  the x-value
   *@param  y  the y-value
   *@param  z  the z-value
   */
  public Coordinate(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   *  Constructs a <code>Coordinate</code> at (0,0,NaN).
   */
  public Coordinate() {
    this(0.0, 0.0);
  }

  /**
   *  Constructs a <code>Coordinate</code> having the same (x,y,z) values as
   *  <code>other</code>.
   *
   *@param  c  the <code>Coordinate</code> to copy.
   */
  public Coordinate(Coordinate c) {
    this(c.x, c.y, c.z);
  }

  /**
   *  Constructs a <code>Coordinate</code> at (x,y,NaN).
   *
   *@param  x  the x-value
   *@param  y  the y-value
   */
  public Coordinate(double x, double y) {
    this(x, y, NULL_ORDINATE);
  }



  /**
   *  Sets this <code>Coordinate</code>s (x,y,z) values to that of <code>other</code>.
   *
   *@param  other  the <code>Coordinate</code> to copy
   */
  public void setCoordinate(Coordinate other) {
    x = other.x;
    y = other.y;
    z = other.z;
  }

  /**
   *  Returns whether the planar projections of the two <code>Coordinate</code>s
   *  are equal.
   *
   *@param  other  a <code>Coordinate</code> with which to do the 2D comparison.
   *@return        <code>true</code> if the x- and y-coordinates are equal; the
   *      z-coordinates do not have to be equal.
   */
  public boolean equals2D(Coordinate other) {
    if (x != other.x) {
      return false;
    }

    if (y != other.y) {
      return false;
    }

    return true;
  }

  /**
   *  Returns <code>true</code> if <code>other</code> has the same values for
   *  the x and y ordinates.
   *  Since Coordinates are 2.5D, this routine ignores the z value when making the comparison.
   *
   *@param  other  a <code>Coordinate</code> with which to do the comparison.
   *@return        <code>true</code> if <code>other</code> is a <code>Coordinate</code>
   *      with the same values for the x and y ordinates.
   */
  public boolean equals(Object other) {
    if (!(other instanceof Coordinate)) {
      return false;
    }
    return equals2D((Coordinate) other);
  }

  /**
   *  Compares this {@link Coordinate} with the specified {@link Coordinate} for order.
   *  This method ignores the z value when making the comparison.
   *  Returns:
   *  <UL>
   *    <LI> -1 : this.x < other.x || ((this.x == other.x) && (this.y <
   *    other.y))
   *    <LI> 0 : this.x == other.x && this.y = other.y
   *    <LI> 1 : this.x > other.x || ((this.x == other.x) && (this.y > other.y))
   *
   *  </UL>
   *  Note: This method assumes that ordinate values
   * are valid numbers.  NaN values are not handled correctly.
   *
   *@param  o  the <code>Coordinate</code> with which this <code>Coordinate</code>
   *      is being compared
   *@return    -1, zero, or 1 as this <code>Coordinate</code>
   *      is less than, equal to, or greater than the specified <code>Coordinate</code>
   */
  public int compareTo(Object o) {
    Coordinate other = (Coordinate) o;

    if (x < other.x) return -1;
    if (x > other.x) return 1;
    if (y < other.y) return -1;
    if (y > other.y) return 1;
    return 0;
  }

  /**
   *  Returns <code>true</code> if <code>other</code> has the same values for x,
   *  y and z.
   *
   *@param  other  a <code>Coordinate</code> with which to do the 3D comparison.
   *@return        <code>true</code> if <code>other</code> is a <code>Coordinate</code>
   *      with the same values for x, y and z.
   */
  public boolean equals3D(Coordinate other) {
    return (x == other.x) && (y == other.y) &&
               ((z == other.z) ||
               (Double.isNaN(z) && Double.isNaN(other.z)));
  }

  /**
   *  Returns a <code>String</code> of the form <I>(x,y,z)</I> .
   *
   *@return    a <code>String</code> of the form <I>(x,y,z)</I>
   */
  public String toString() {
    return "(" + x + ", " + y + ", " + z + ")";
  }

  public Object clone() {
//    try {
//      Coordinate coord = (Coordinate) super.clone();
    	Coordinate coord = (Coordinate) new Coordinate(x, y, z);

      return coord; // return the clone
//    } catch (CloneNotSupportedException e) {
//      Assert.shouldNeverReachHere(
//          "this shouldn't happen because this class is Cloneable");
//
//      return null;
//    }
  }

  /**
   * Computes the 2-dimensional Euclidean distance to another location.
   * The Z-ordinate is ignored.
   * 
   * @param p a point
   * @return the 2-dimensional Euclidean distance between the locations
   */
  public double distance(Coordinate p) {
    double dx = x - p.x;
    double dy = y - p.y;

    return Math.sqrt(dx * dx + dy * dy);
  }

  /**
   * Gets a hashcode for this coordinate.
   * 
   * @return a hashcode for this coordinate
   */
  public int hashCode() {
    //Algorithm from Effective Java by Joshua Bloch [Jon Aquino]
    int result = 17;
    result = 37 * result + hashCode(x);
    result = 37 * result + hashCode(y);
    return result;
  }

  /**
   * Computes a hash code for a double value, using the algorithm from
   * Joshua Bloch's book <i>Effective Java"</i>
   * 
   * @return a hashcode for the double value
   */
  public static int hashCode(double x) {
    long f = Double.doubleToLongBits(x);
    return (int)(f^(f>>>32));
  }


  /**
   * Compares two {@link Coordinate}s, allowing for either a 2-dimensional
   * or 3-dimensional comparison, and handling NaN values correctly.
   */
  public static class DimensionalComparator
      implements Comparator
  {
    /**
     * Compare two <code>double</code>s, allowing for NaN values.
     * NaN is treated as being less than any valid number.
     *
     * @param a a <code>double</code>
     * @param b a <code>double</code>
     * @return -1, 0, or 1 depending on whether a is less than, equal to or greater than b
     */
    public static int compare(double a, double b)
    {
      if (a < b) return -1;
      if (a > b) return 1;

      if (Double.isNaN(a)) {
        if (Double.isNaN(b)) return 0;
        return -1;
      }

      if (Double.isNaN(b)) return 1;
      return 0;
    }

    private int dimensionsToTest = 2;

    /**
     * Creates a comparator for 2 dimensional coordinates.
     */
    public DimensionalComparator()
    {
      this(2);
    }

    /**
     * Creates a comparator for 2 or 3 dimensional coordinates, depending
     * on the value provided.
     *
     * @param dimensionLimit the number of dimensions to test
     */
    public DimensionalComparator(int dimensionsToTest)
    {
      if (dimensionsToTest != 2 && dimensionsToTest != 3)
        throw new IllegalArgumentException("only 2 or 3 dimensions may be specified");
      this.dimensionsToTest = dimensionsToTest;
    }

    /**
     * Compares two {@link Coordinate}s along to the number of
     * dimensions specified.
     *
     * @param o1 a {@link Coordinate}
     * @param o2 a {link Coordinate}
     * @return -1, 0, or 1 depending on whether o1 is less than,
     * equal to, or greater than 02
     *
     */
    public int compare(Object o1, Object o2)
    {
      Coordinate c1 = (Coordinate) o1;
      Coordinate c2 = (Coordinate) o2;

      int compX = compare(c1.x, c2.x);
      if (compX != 0) return compX;

      int compY = compare(c1.y, c2.y);
      if (compY != 0) return compY;

      if (dimensionsToTest <= 2) return 0;

      int compZ = compare(c1.z, c2.z);
      return compZ;
    }
  }

}