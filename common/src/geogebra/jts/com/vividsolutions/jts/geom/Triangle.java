
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

import com.vividsolutions.jts.algorithm.*;

/**
 * Represents a planar triangle, and provides methods for calculating various
 * properties of triangles.
 *
 * @version 1.7
 */
public class Triangle
{


  /**
   * Tests whether a triangle is acute.
   * A triangle is acute iff all interior angles are acute.
   * This is a strict test - right triangles will return <tt>false</tt>
   * A triangle which is not acute is either right or obtuse.
   * <p>
   * Note: this implementation is not robust for angles very close to 90 degrees.
   * 
   * @param a a vertex of the triangle
   * @param b a vertex of the triangle
   * @param c a vertex of the triangle
   * @return true if the triangle is acute
   */
  public static boolean isAcute(Coordinate a, Coordinate b, Coordinate c)
  {
    if (! Angle.isAcute(a, b, c)) return false;
    if (! Angle.isAcute(b, c, a)) return false;
    if (! Angle.isAcute(c, a, b)) return false;
    return true;
  }

  /**
   * Computes the line which is the perpendicular bisector of the
   * line segment a-b.
   *
   * @param a a point
   * @param b another point
   * @return the perpendicular bisector, as an HCoordinate
   */
  public static HCoordinate perpendicularBisector(Coordinate a, Coordinate b) {
    // returns the perpendicular bisector of the line segment ab
    double dx = b.x - a.x;
    double dy = b.y - a.y;
    HCoordinate l1 = new HCoordinate(a.x + dx / 2.0, a.y + dy / 2.0, 1.0);
    HCoordinate l2 = new HCoordinate(a.x - dy + dx / 2.0, a.y + dx + dy / 2.0, 1.0);
    return new HCoordinate(l1,l2);
  }

  /**
   * Computes the circumcentre of a triangle.
   * The circumcentre is the centre of the circumcircle,
   * the smallest circle which encloses the triangle.
   *
   * @param a a vertx of the triangle
   * @param b a vertx of the triangle
   * @param c a vertx of the triangle
   * @return the circumcentre of the triangle
   */
  public static Coordinate circumcentre(Coordinate a, Coordinate b, Coordinate c)
  {
    // compute the perpendicular bisector of chord ab
    HCoordinate cab = perpendicularBisector(a, b);
    // compute the perpendicular bisector of chord bc
    HCoordinate cbc = perpendicularBisector(b, c);
    // compute the intersection of the bisectors (circle radii)
    HCoordinate hcc = new HCoordinate(cab, cbc);
    Coordinate cc = null;
    try {
      cc = new Coordinate(hcc.getX(), hcc.getY());
    }
    catch (NotRepresentableException ex) {
      // MD - not sure what we can do to prevent this (robustness problem)
      // Idea - can we condition which edges we choose?
      throw new IllegalStateException(ex.getMessage());
    }
    return cc;
  }

  /**
   * Computes the incentre of a triangle.
   * The <i>inCentre</i> of a triangle is the point which is equidistant
   * from the sides of the triangle.
   * It is also the point at which the bisectors
   * of the triangle's angles meet.
   * It is the centre of the triangle's <i>incircle</i>,
   * which is the unique circle that is tangent to each of the triangle's three sides.
    *
   * @param a a vertx of the triangle
   * @param b a vertx of the triangle
   * @param c a vertx of the triangle
   * @return the point which is the incentre of the triangle
   */
  public static Coordinate inCentre(Coordinate a, Coordinate b, Coordinate c)
  {
    // the lengths of the sides, labelled by their opposite vertex
    double len0 = b.distance(c);
    double len1 = a.distance(c);
    double len2 = a.distance(b);
    double circum = len0 + len1 + len2;

    double inCentreX = (len0 * a.x + len1 * b.x +len2 * c.x)  / circum;
    double inCentreY = (len0 * a.y + len1 * b.y +len2 * c.y)  / circum;
    return new Coordinate(inCentreX, inCentreY);
  }

  /**
   * Computes the centroid (centre of mass) of a triangle.
   * This is also the point at which the triangle's three
   * medians intersect (a triangle median is the segment from a vertex of the triangle to the
   * midpoint of the opposite side).
   * The centroid divides each median in a ratio of 2:1.
   * The centroid always lies within the triangle.
   *
   *
   * @param a a vertex of the triangle
   * @param b a vertex of the triangle
   * @param c a vertex of the triangle
   * @return the centroid of the triangle
   */
  public static Coordinate centroid(Coordinate a, Coordinate b, Coordinate c)
  {
    double x = (a.x + b.x + c.x) / 3;
    double y = (a.y + b.y + c.y) / 3;
    return new Coordinate(x, y);
  }

  /**
   * Computes the length of the longest side of a triangle
   *
   * @param a a vertex of the triangle
   * @param b a vertex of the triangle
   * @param c a vertex of the triangle
   * @return the length of the longest side of the triangle
   */
  public static double longestSideLength(Coordinate a, Coordinate b, Coordinate c)
  {
    double lenAB = a.distance(b);
    double lenBC = b.distance(c);
    double lenCA = c.distance(a);
    double maxLen = lenAB;
    if (lenBC > maxLen)
      maxLen = lenBC;
    if (lenCA > maxLen)
      maxLen = lenCA;
    return maxLen;
  }

  /**
   * Computes the point at which the bisector of the angle ABC
   * cuts the segment AC.
   *
   * @param a a vertex of the triangle
   * @param b a vertex of the triangle
   * @param c a vertex of the triangle
   * @return the angle bisector cut point
   */
  public static Coordinate angleBisector(Coordinate a, Coordinate b, Coordinate c)
  {
    /**
     * Uses the fact that the lengths of the parts of the split segment
     * are proportional to the lengths of the adjacent triangle sides
     */
    double len0 = b.distance(a);
    double len2 = b.distance(c);
    double frac = len0 / (len0 + len2);
    double dx = c.x - a.x;
    double dy = c.y - a.y;

    Coordinate splitPt = new Coordinate(a.x + frac * dx,
                                        a.y + frac * dy);
    return splitPt;
  }

  /**
   * Computes the 2D area of a triangle.
   * The area value is always non-negative.
   *
   * @param a a vertex of the triangle
   * @param b a vertex of the triangle
   * @param c a vertex of the triangle
   * @return the area of the triangle
   * 
   * @see signedArea
   */
  public static double area(Coordinate a, Coordinate b, Coordinate c)
  {
    return Math.abs(((c.x - a.x) * (b.y - a.y) - (b.x - a.x) * (c.y - a.y)) / 2);
  }

  /**
   * Computes the signed 2D area of a triangle.
   * The area value is positive if the triangle is oriented CW,
   * and negative if it is oriented CCW.
   * <p>
   * The signed area value can be used to determine point orientation, but 
   * the implementation in this method
   * is susceptible to round-off errors.  
   * Use {@link CGAlgorithms#orientationIndex)} for robust orientation
   * calculation.
   *
   * @param a a vertex of the triangle
   * @param b a vertex of the triangle
   * @param c a vertex of the triangle
   * @return the signed 2D area of the triangle
   * 
   * @see CGAlgorithms#orientationIndex
   */
  public static double signedArea(Coordinate a, Coordinate b, Coordinate c)
  {
		/**
		 * Uses the formula 1/2 * | u x v |
		 * where
		 * 	u,v are the side vectors of the triangle
		 *  x is the vector cross-product
		 * For 2D vectors, this formual simplifies to the expression below
		 */
    return ((c.x - a.x) * (b.y - a.y) - (b.x - a.x) * (c.y - a.y)) / 2;
  }

	/**
	 * Computes the 3D area of a triangle.
	 * The value computed is alway non-negative.
	 * 
   * @param a a vertex of the triangle
   * @param b a vertex of the triangle
   * @param c a vertex of the triangle
   * @return the 3D area of the triangle
	 */
	public static double area3D(Coordinate a, Coordinate b, Coordinate c)
	{
		/**
		 * Uses the formula 1/2 * | u x v |
		 * where
		 * 	u,v are the side vectors of the triangle
		 *  x is the vector cross-product
		 */
		// side vectors u and v
		double ux = b.x - a.x;
		double uy = b.y - a.y;
		double uz = b.z - a.z;
		
		double vx = c.x - a.x;
		double vy = c.y - a.y;
		double vz = c.z - a.z;
		
		// cross-product = u x v 
		double crossx = uy * vz - uz * vy;
		double crossy = uz * vx - ux * vz;
		double crossz = ux * vy - uy * vx;
		
		// tri area = 1/2 * | u x v |
		double absSq = crossx * crossx + crossy * crossy + crossz * crossz;
		double area3D = Math.sqrt(absSq) / 2;
		
		return area3D;
	}
	
	/**
	 * The coordinates of the vertices of the triangle
	 */
  public Coordinate p0, p1, p2;

  /**
   * Creates a new triangle with the given vertices.
   * 
   * @param p0 a vertex
   * @param p1 a vertex
   * @param p2 a vertex
   */
  public Triangle(Coordinate p0, Coordinate p1, Coordinate p2)
  {
    this.p0 = p0;
    this.p1 = p1;
    this.p2 = p2;
  }

  /**
   * Computes the incentre of a triangle.
   * The <i>inCentre</i> of a triangle is the point which is equidistant
   * from the sides of the triangle.
   * It is also the point at which the bisectors
   * of the triangle's angles meet.
   * It is the centre of the triangle's <i>incircle</i>,
   * which is the unique circle that is tangent to each of the triangle's three sides.
   *
   * @return the point which is the inCentre of the triangle
   */
  public Coordinate inCentre()
  {
    return inCentre(p0, p1, p2);
  }


}

