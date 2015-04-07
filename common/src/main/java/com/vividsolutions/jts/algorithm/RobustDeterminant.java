

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

/**
 * @version 1.7
 */

/**
 * Implements an algorithm to compute the
 * sign of a 2x2 determinant for double precision values robustly.
 * It is a direct translation of code developed by Olivier Devillers.
 * <p>
 * The original code carries the following copyright notice:
 *
 * <pre>
 *************************************************************************
 * Author : Olivier Devillers
 * Olivier.Devillers@sophia.inria.fr
 * http:/www.inria.fr:/prisme/personnel/devillers/anglais/determinant.html
 **************************************************************************
 *
 **************************************************************************
 *              Copyright (c) 1995  by  INRIA Prisme Project
 *                  BP 93 06902 Sophia Antipolis Cedex, France.
 *                           All rights reserved
 **************************************************************************
 * </pre>
 *
 * @version 1.7
 */
public class RobustDeterminant {

  //public static int callCount = 0; // debugging only

  /**
   * Computes the sign of the determinant of the 2x2 matrix
   * with the given entries, in a robust way.
   * 
   * @return -1 if the determinant is negative,
   * @return  1 if the determinant is positive,
   * @return  0 if the determinant is 0.
   */
  public static int signOfDet2x2(double x1, double y1, double x2, double y2) {
    // returns -1 if the determinant is negative,
    // returns  1 if the determinant is positive,
    // retunrs  0 if the determinant is null.
    int sign;
    double swap;
    double k;
    long count = 0;

    //callCount++; // debugging only

    sign = 1;

    /*
     *  testing null entries
     */
    if ((x1 == 0.0) || (y2 == 0.0)) {
      if ((y1 == 0.0) || (x2 == 0.0)) {
        return 0;
      }
      else if (y1 > 0) {
        if (x2 > 0) {
          return -sign;
        }
        else {
          return sign;
        }
      }
      else {
        if (x2 > 0) {
          return sign;
        }
        else {
          return -sign;
        }
      }
    }
    if ((y1 == 0.0) || (x2 == 0.0)) {
      if (y2 > 0) {
        if (x1 > 0) {
          return sign;
        }
        else {
          return -sign;
        }
      }
      else {
        if (x1 > 0) {
          return -sign;
        }
        else {
          return sign;
        }
      }
    }

    /*
     *  making y coordinates positive and permuting the entries
     */
    /*
     *  so that y2 is the biggest one
     */
    if (0.0 < y1) {
      if (0.0 < y2) {
        if (y1 <= y2) {
          ;
        }
        else {
          sign = -sign;
          swap = x1;
          x1 = x2;
          x2 = swap;
          swap = y1;
          y1 = y2;
          y2 = swap;
        }
      }
      else {
        if (y1 <= -y2) {
          sign = -sign;
          x2 = -x2;
          y2 = -y2;
        }
        else {
          swap = x1;
          x1 = -x2;
          x2 = swap;
          swap = y1;
          y1 = -y2;
          y2 = swap;
        }
      }
    }
    else {
      if (0.0 < y2) {
        if (-y1 <= y2) {
          sign = -sign;
          x1 = -x1;
          y1 = -y1;
        }
        else {
          swap = -x1;
          x1 = x2;
          x2 = swap;
          swap = -y1;
          y1 = y2;
          y2 = swap;
        }
      }
      else {
        if (y1 >= y2) {
          x1 = -x1;
          y1 = -y1;
          x2 = -x2;
          y2 = -y2;
          ;
        }
        else {
          sign = -sign;
          swap = -x1;
          x1 = -x2;
          x2 = swap;
          swap = -y1;
          y1 = -y2;
          y2 = swap;
        }
      }
    }

    /*
     *  making x coordinates positive
     */
    /*
     *  if |x2| < |x1| one can conclude
     */
    if (0.0 < x1) {
      if (0.0 < x2) {
        if (x1 <= x2) {
          ;
        }
        else {
          return sign;
        }
      }
      else {
        return sign;
      }
    }
    else {
      if (0.0 < x2) {
        return -sign;
      }
      else {
        if (x1 >= x2) {
          sign = -sign;
          x1 = -x1;
          x2 = -x2;
          ;
        }
        else {
          return -sign;
        }
      }
    }

    /*
     *  all entries strictly positive   x1 <= x2 and y1 <= y2
     */
    while (true) {
      count = count + 1;
      k = Math.floor(x2 / x1);
      x2 = x2 - k * x1;
      y2 = y2 - k * y1;

      /*
       *  testing if R (new U2) is in U1 rectangle
       */
      if (y2 < 0.0) {
        return -sign;
      }
      if (y2 > y1) {
        return sign;
      }

      /*
       *  finding R'
       */
      if (x1 > x2 + x2) {
        if (y1 < y2 + y2) {
          return sign;
        }
      }
      else {
        if (y1 > y2 + y2) {
          return -sign;
        }
        else {
          x2 = x1 - x2;
          y2 = y1 - y2;
          sign = -sign;
        }
      }
      if (y2 == 0.0) {
        if (x2 == 0.0) {
          return 0;
        }
        else {
          return -sign;
        }
      }
      if (x2 == 0.0) {
        return sign;
      }

      /*
       *  exchange 1 and 2 role.
       */
      k = Math.floor(x1 / x2);
      x1 = x1 - k * x2;
      y1 = y1 - k * y2;

      /*
       *  testing if R (new U1) is in U2 rectangle
       */
      if (y1 < 0.0) {
        return sign;
      }
      if (y1 > y2) {
        return -sign;
      }

      /*
       *  finding R'
       */
      if (x2 > x1 + x1) {
        if (y2 < y1 + y1) {
          return -sign;
        }
      }
      else {
        if (y2 > y1 + y1) {
          return sign;
        }
        else {
          x1 = x2 - x1;
          y1 = y2 - y1;
          sign = -sign;
        }
      }
      if (y1 == 0.0) {
        if (x1 == 0.0) {
          return 0;
        }
        else {
          return sign;
        }
      }
      if (x1 == 0.0) {
        return -sign;
      }
    }

  }

}
