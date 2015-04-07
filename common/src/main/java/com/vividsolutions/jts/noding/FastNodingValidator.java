
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
import com.vividsolutions.jts.algorithm.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.util.*;
import com.vividsolutions.jts.io.*;

/**
 * Validates that a collection of {@link SegmentString}s is correctly noded.
 * Indexing is used to improve performance.
 * This class assumes that at least one round of noding has already been performed
 * (which may still leave intersections, due to rounding issues).
 * Does NOT check a-b-a collapse situations. 
 * Also does not check for endpt-interior vertex intersections.
 * This should not be a problem, since the noders should be
 * able to compute intersections between vertices correctly.
 * User may either test the valid condition, or request that a 
 * {@link TopologyException} 
 * be thrown.
 *
 * @version 1.7
 */
public class FastNodingValidator 
{
  private LineIntersector li = new RobustLineIntersector();

  private Collection segStrings;
  private InteriorIntersectionFinder segInt = null;
  private boolean isValid = true;
  
  public FastNodingValidator(Collection segStrings)
  {
    this.segStrings = segStrings;
  }

  /**
   * Checks for an intersection and 
   * reports if one is found.
   * 
   * @return true if the arrangement contains an interior intersection
   */
  public boolean isValid()
  {
  	execute();
  	return isValid;
  }
  
  /**
   * Returns an error message indicating the segments containing
   * the intersection.
   * 
   * @return an error message documenting the intersection location
   */
  public String getErrorMessage()
  {
  	if (isValid) return "no intersections found";
  	
		Coordinate[] intSegs = segInt.getIntersectionSegments();
    return "found non-noded intersection between "
        + WKTWriter.toLineString(intSegs[0], intSegs[1])
        + " and "
        + WKTWriter.toLineString(intSegs[2], intSegs[3]);
  }
  
  /**
   * Checks for an intersection and throws
   * a TopologyException if one is found.
   *
   * @throws TopologyException if an intersection is found
   */
  public void checkValid()
  {
  	execute();
  	if (! isValid)
  		throw new TopologyException(getErrorMessage(), segInt.getInteriorIntersection());
  }

  private void execute()
  {
  	if (segInt != null) 
  		return;
    checkInteriorIntersections();
  }

  private void checkInteriorIntersections()
  {
  	/**
  	 * MD - It may even be reliable to simply check whether 
  	 * end segments (of SegmentStrings) have an interior intersection,
  	 * since noding should have split any true interior intersections already.
  	 */
  	isValid = true;
  	segInt = new InteriorIntersectionFinder(li);
  	MCIndexNoder noder = new MCIndexNoder();
  	noder.setSegmentIntersector(segInt);
  	noder.computeNodes(segStrings);
  	if (segInt.hasIntersection()) {
  		isValid = false;
  		return;
  	}
  }
  
}
