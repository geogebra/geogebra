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
package com.vividsolutions.jts.noding;

import java.util.*;

/**
 * An intersector for the red-blue intersection problem.
 * In this class of line arrangement problem,
 * two disjoint sets of linestrings are provided.
 * It is assumed that within
 * each set, no two linestrings intersect except possibly at their endpoints.
 * Implementations can take advantage of this fact to optimize processing.
 *
 * @author Martin Davis
 * @version 1.10
 */
public abstract class SegmentSetMutualIntersector
{
  protected SegmentIntersector segInt;

  /**
   * Sets the {@link SegmentIntersector} to use with this intersector.
   * The SegmentIntersector will either rocord or add intersection nodes
   * for the input segment strings.
   *
   * @param segInt the segment intersector to use
   */
  public void setSegmentIntersector(SegmentIntersector segInt)
  {
    this.segInt = segInt;
  }

  /**
   * 
   * @param segStrings0 a collection of {@link SegmentString}s to node
   */
  public abstract void setBaseSegments(Collection segStrings);
  
  /**
   * Computes the intersections for two collections of {@link SegmentString}s.
   *
  * @param segStrings1 a collection of {@link SegmentString}s to node
   */
  public abstract void process(Collection segStrings);
}