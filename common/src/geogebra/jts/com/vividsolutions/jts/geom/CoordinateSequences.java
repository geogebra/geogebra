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

import java.util.*;

/**
 * Utility functions for manipulating {@link CoordinateSequence}s
 *
 * @version 1.7
 */
public class CoordinateSequences {

  /**
   * Reverses the coordinates in a sequence in-place.
   */
  public static void reverse(CoordinateSequence seq)
  {
    int last = seq.size() - 1;
    int mid = last / 2;
    for (int i = 0; i <= mid; i++) {
      swap(seq, i, last - i);
    }
  }

  /**
   * Swaps two coordinates in a sequence.
   *
   * @param seq
   * @param i
   * @param j
   */
  public static void swap(CoordinateSequence seq, int i, int j)
  {
    if (i == j) return;
    for (int dim = 0; dim < seq.getDimension(); dim++) {
      double tmp = seq.getOrdinate(i, dim);
      seq.setOrdinate(i, dim, seq.getOrdinate(j, dim));
      seq.setOrdinate(j, dim, tmp);
    }
  }
  
  /**
   * Copies a section of a {@link CoordinateSequence} to another {@link CoordinateSequence}.
   * The sequences must have the same dimension.
   *
   * @param src
   * @param srcPos
   * @param dest
   * @param destPos
   * @param length
   */
  public static void copy(CoordinateSequence src, int srcPos, CoordinateSequence dest, int destPos, int length)
  {
  	for (int i = 0; i < length; i++) {
  		copyCoord(src, srcPos + i, dest, destPos + i);
  	}
  }

  /**
   * Copies a coordinate of a {@link CoordinateSequence} to another {@link CoordinateSequence}.
   * The sequences must have the same dimension.
   * 
   * @param src
   * @param srcPos
   * @param dest
   * @param destPos
   */
  public static void copyCoord(CoordinateSequence src, int srcPos, CoordinateSequence dest, int destPos)
  {
		for (int dim = 0; dim < src.getDimension(); dim++) {
			dest.setOrdinate(destPos, dim, src.getOrdinate(srcPos, dim));
		}
  }
}