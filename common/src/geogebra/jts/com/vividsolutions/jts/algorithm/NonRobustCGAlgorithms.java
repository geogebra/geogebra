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
package com.vividsolutions.jts.algorithm;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Non-robust versions of various fundamental Computational Geometric algorithms,
 * <b>FOR TESTING PURPOSES ONLY!</b>.
 * The non-robustness is due to rounding error in floating point computation.
 *
 * @version 1.7
 */
public class NonRobustCGAlgorithms
  extends CGAlgorithms
{
  public NonRobustCGAlgorithms() {
  }

  /**
   * Computes whether a ring defined by an array of {@link Coordinate} is
   * oriented counter-clockwise.
   * <p>
   * This will handle coordinate lists which contain repeated points.
   *
   * @param ring an array of coordinates forming a ring
   * @return <code>true</code> if the ring is oriented counter-clockwise.
   * @throws IllegalArgumentException if the ring is degenerate (does not contain 3 different points)
   */
  public static boolean isPointInRing(Coordinate p, Coordinate[] ring)
  {
    int		i, i1;		// point index; i1 = i-1 mod n
    double	xInt;		// x intersection of e with ray
    int		crossings = 0;	// number of edge/ray crossings
    double	x1,y1,x2,y2;
    int         nPts = ring.length;

	/* For each line edge l = (i-1, i), see if it crosses ray from test point in positive x direction. */
	for (i = 1; i < nPts; i++ ) {
		i1 = i - 1;
		Coordinate p1 = ring[i];
		Coordinate p2 = ring[i1];
		x1 = p1.x - p.x;
		y1 = p1.y - p.y;
		x2 = p2.x - p.x;
		y2 = p2.y - p.y;

		if( ( ( y1 > 0 ) && ( y2 <= 0 ) ) ||
		    ( ( y2 > 0 ) && ( y1 <= 0 ) ) ) {
			/* e straddles x axis, so compute intersection. */
			xInt = (x1 * y2 - x2 * y1) / (y2 - y1);
			//xsave = xInt;
			/* crosses ray if strictly positive intersection. */
			if (0.0 < xInt)
				crossings++;
		}
	}
	/* p is inside if an odd number of crossings. */
	if( (crossings % 2) == 1 )
		return	true;
	else
		return	false;
  }

  /**
   * Computes whether a ring defined by an array of {@link Coordinate} is
   * oriented counter-clockwise.
   * <p>
   * This will handle coordinate lists which contain repeated points.
   *
   * @param ring an array of coordinates forming a ring
   * @return <code>true</code> if the ring is oriented counter-clockwise.
   * @throws IllegalArgumentException if the ring is degenerate (does not contain 3 different points)
   */
  public static boolean isCCW(Coordinate[] ring)
  {
    // # of points without closing endpoint
    int nPts = ring.length - 1;

    // check that this is a valid ring - if not, simply return a dummy value
    if (nPts < 4) return false;

    // algorithm to check if a Ring is stored in CCW order
    // find highest point
    Coordinate hip = ring[0];
    int hii = 0;
    for (int i = 1; i <= nPts; i++) {
      Coordinate p = ring[i];
      if (p.y > hip.y) {
        hip = p;
        hii = i;
      }
    }

    // find different point before highest point
    int iPrev = hii;
    do {
      iPrev = (iPrev - 1) % nPts;
    } while (ring[iPrev].equals(hip) && iPrev != hii);

    // find different point after highest point
    int iNext = hii;
    do {
      iNext = (iNext + 1) % nPts;
    } while (ring[iNext].equals(hip) && iNext != hii);

    Coordinate prev = ring[iPrev];
    Coordinate next = ring[iNext];

    if (prev.equals(hip) || next.equals(hip) || prev.equals(next))
        throw new IllegalArgumentException("degenerate ring (does not contain 3 different points)");

    // translate so that hip is at the origin.
    // This will not affect the area calculation, and will avoid
    // finite-accuracy errors (i.e very small vectors with very large coordinates)
    // This also simplifies the discriminant calculation.
    double prev2x = prev.x - hip.x;
    double prev2y = prev.y - hip.y;
    double next2x = next.x - hip.x;
    double next2y = next.y - hip.y;
    // compute cross-product of vectors hip->next and hip->prev
    // (e.g. area of parallelogram they enclose)
    double disc = next2x * prev2y - next2y * prev2x;
    /* If disc is exactly 0, lines are collinear.  There are two possible cases:
            (1) the lines lie along the x axis in opposite directions
            (2) the line lie on top of one another
      (2) should never happen, so we're going to ignore it!
            (Might want to assert this)
    (1) is handled by checking if next is left of prev ==> CCW
    */
    if (disc == 0.0) {
            // poly is CCW if prev x is right of next x
            return (prev.x > next.x);
    }
    else {
            // if area is positive, points are ordered CCW
            return (disc > 0.0);
    }
  }

  public static int computeOrientation(Coordinate p1, Coordinate p2, Coordinate q) {
        double dx1 = p2.x - p1.x;
        double dy1 = p2.y - p1.y;
        double dx2 = q.x - p2.x;
        double dy2 = q.y - p2.y;
        double det = dx1*dy2 - dx2*dy1;
        if (det > 0.0) return 1;
        if (det < 0.0) return -1;
        return 0;
  }

}
