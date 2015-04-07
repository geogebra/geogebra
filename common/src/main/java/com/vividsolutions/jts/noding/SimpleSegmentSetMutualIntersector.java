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

import com.vividsolutions.jts.geom.Coordinate;


/**
 * Intersects two sets of {@link SegmentStrings} using 
 * brute-force comparasion.
 *
 * @version 1.7
 */
public class SimpleSegmentSetMutualIntersector
    extends SegmentSetMutualIntersector
{
	private Collection baseSegStrings;

  public SimpleSegmentSetMutualIntersector()
  {
  }
  
  public void setBaseSegments(Collection segStrings)
  {
  	this.baseSegStrings = segStrings;
  }
  
  public void process(Collection segStrings)
  {
    for (Iterator i = baseSegStrings.iterator(); i.hasNext(); ) {
    	SegmentString baseSS = (SegmentString) i.next();
    	for (Iterator j = segStrings.iterator(); j.hasNext(); ) {
      	SegmentString ss = (SegmentString) j.next();
      	intersect(baseSS, ss);
        if (segInt.isDone()) return;
    	}
    }
  }

  private void intersect(SegmentString ss0, SegmentString ss1)
  {
    Coordinate[] pts0 = ss0.getCoordinates();
    Coordinate[] pts1 = ss1.getCoordinates();
    for (int i0 = 0; i0 < pts0.length - 1; i0++) {
      for (int i1 = 0; i1 < pts1.length - 1; i1++) {
        segInt.processIntersections(ss0, i0, ss1, i1);
        if (segInt.isDone()) return;
      }
    }

  }

}
