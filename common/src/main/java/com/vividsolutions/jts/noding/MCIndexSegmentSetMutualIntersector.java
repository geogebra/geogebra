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

import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.index.chain.*;
import com.vividsolutions.jts.index.quadtree.Quadtree;
import com.vividsolutions.jts.index.strtree.STRtree;
import com.vividsolutions.jts.noding.SegmentIntersector;
import com.vividsolutions.jts.noding.SegmentString;

/**
 * Intersects two sets of {@link SegmentStrings} using a index based
 * on {@link MonotoneChain}s and a {@link SpatialIndex}.
 *
 * @version 1.7
 */
public class MCIndexSegmentSetMutualIntersector
    extends SegmentSetMutualIntersector
{
  private List monoChains = new ArrayList();
  /*
  * The {@link SpatialIndex} used should be something that supports
  * envelope (range) queries efficiently (such as a {@link Quadtree}
  * or {@link STRtree}.
  */
  private SpatialIndex index= new STRtree();
  private int indexCounter = 0;
  private int processCounter = 0;
  // statistics
  private int nOverlaps = 0;

  public MCIndexSegmentSetMutualIntersector()
  {
  }

  public List getMonotoneChains() { return monoChains; }

  public SpatialIndex getIndex() { return index; }

  
  public void setBaseSegments(Collection segStrings)
  {
    for (Iterator i = segStrings.iterator(); i.hasNext(); ) {
      addToIndex((SegmentString) i.next());
    }
  }
  
  private void addToIndex(SegmentString segStr)
  {
    List segChains = MonotoneChainBuilder.getChains(segStr.getCoordinates(), segStr);
    for (Iterator i = segChains.iterator(); i.hasNext(); ) {
      MonotoneChain mc = (MonotoneChain) i.next();
      mc.setId(indexCounter++);
      index.insert(mc.getEnvelope(), mc);
    }
  }

  public void process(Collection segStrings)
  {
  	processCounter = indexCounter + 1;
  	nOverlaps = 0;
  	monoChains.clear();
    for (Iterator i = segStrings.iterator(); i.hasNext(); ) {
      addToMonoChains((SegmentString) i.next());
    }
    intersectChains();
//    System.out.println("MCIndexBichromaticIntersector: # chain overlaps = " + nOverlaps);
//    System.out.println("MCIndexBichromaticIntersector: # oct chain overlaps = " + nOctOverlaps);
  }

  private void intersectChains()
  {
    MonotoneChainOverlapAction overlapAction = new SegmentOverlapAction(segInt);

    for (Iterator i = monoChains.iterator(); i.hasNext(); ) {
      MonotoneChain queryChain = (MonotoneChain) i.next();
      List overlapChains = index.query(queryChain.getEnvelope());
      for (Iterator j = overlapChains.iterator(); j.hasNext(); ) {
        MonotoneChain testChain = (MonotoneChain) j.next();
        queryChain.computeOverlaps(testChain, overlapAction);
        nOverlaps++;
        if (segInt.isDone()) return;
      }
    }
  }

  private void addToMonoChains(SegmentString segStr)
  {
    List segChains = MonotoneChainBuilder.getChains(segStr.getCoordinates(), segStr);
    for (Iterator i = segChains.iterator(); i.hasNext(); ) {
      MonotoneChain mc = (MonotoneChain) i.next();
      mc.setId(processCounter++);
      monoChains.add(mc);
    }
  }

  public class SegmentOverlapAction
      extends MonotoneChainOverlapAction
  {
    private SegmentIntersector si = null;

    public SegmentOverlapAction(SegmentIntersector si)
    {
      this.si = si;
    }

    public void overlap(MonotoneChain mc1, int start1, MonotoneChain mc2, int start2)
    {
      SegmentString ss1 = (SegmentString) mc1.getContext();
      SegmentString ss2 = (SegmentString) mc2.getContext();
      si.processIntersections(ss1, start1, ss2, start2);
    }

  }
}
