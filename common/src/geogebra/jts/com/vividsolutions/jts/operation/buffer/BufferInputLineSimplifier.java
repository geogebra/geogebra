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
package com.vividsolutions.jts.operation.buffer;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.*;

/**
 * Simplifies a buffer input line to 
 * remove concavities with shallow depth.
 * <p>
 * The most important benefit of doing this
 * is to reduce the number of points and the complexity of
 * shape which will be buffered.
 * It also reduces the risk of gores created by
 * the quantized fillet arcs (although this issue
 * should be eliminated in any case by the 
 * offset curve generation logic).
 * <p>
 * A key aspect of the simplification is that it
 * affects inside (concave or inward) corners only.  
 * Convex (outward) corners are preserved, since they
 * are required to ensure that the eventual buffer curve
 * lies at the correct distance from the input geometry.
 * <p>
 * Another important heuristic used is that the end segments
 * of the input are never simplified.  This ensures that
 * the client buffer code is able to generate end caps consistently.
 * 
 * @author Martin Davis
 *
 */
public class BufferInputLineSimplifier 
{
  public static Coordinate[] simplify(Coordinate[] inputLine, double distanceTol)
  {
    BufferInputLineSimplifier simp = new BufferInputLineSimplifier(inputLine);
    return simp.simplify(distanceTol);
  }
  
  private Coordinate[] inputLine;
  private double distanceTol;
  private boolean[] isDeleted;
  private int angleOrientation = CGAlgorithms.COUNTERCLOCKWISE;
  
  public BufferInputLineSimplifier(Coordinate[] inputLine) {
    this.inputLine = inputLine;
  }

  /**
   * Simplify the input geometry.
   * If the distance tolerance is positive, 
   * concavities on the LEFT side of the line are simplified.
   * If the supplied distance tolerance is negative,
   * concavities on the RIGHT side of the line are simplified.
   * 
   * @param distanceTol simplification distance tolerance to use
   * @return
   */
  public Coordinate[] simplify(double distanceTol)
  {
    this.distanceTol = Math.abs(distanceTol);
    if (distanceTol < 0)
      angleOrientation = CGAlgorithms.CLOCKWISE;
    
    // rely on fact that boolean array is filled with false value
    isDeleted = new boolean[inputLine.length];
    
    boolean isChanged = false;
    do {
      isChanged = deleteShallowConcavities();
    } while (isChanged);
    
    return collapseLine();
  }
  
  private boolean deleteShallowConcavities()
  {
    /**
     * Do not simplify end line segments of the line string.
     * This ensures that end caps are generated consistently.
     */
    int index = 1;
    int maxIndex = inputLine.length - 1;
    
    int midIndex = findNextValidIndex(index);
    int lastIndex = findNextValidIndex(midIndex);
    
    boolean isChanged = false;
    while (lastIndex < maxIndex) {
      // test triple for shallow concavity
      if (isShallowConcavity(inputLine[index], inputLine[midIndex], inputLine[lastIndex], 
          distanceTol)) {
        isDeleted[midIndex] = true;
        isChanged = true;
      }
      // this needs to be replaced by scanning for next valid pts
      index = lastIndex;
      midIndex = findNextValidIndex(index);
      lastIndex = findNextValidIndex(midIndex);
    }
    return isChanged;
  }
  
  private int findNextValidIndex(int index)
  {
    int next = index + 1;
    while (next < inputLine.length - 1
        && isDeleted[next])
      next++;
    return next;  
  }
  
  private Coordinate[] collapseLine()
  {
    CoordinateList coordList = new CoordinateList();
    for (int i = 0; i < inputLine.length; i++) {
      if (! isDeleted[i])
        coordList.add(inputLine[i]);
    }
//    if (coordList.size() < inputLine.length)      System.out.println("Simplified " + (inputLine.length - coordList.size()) + " pts");
    return coordList.toCoordinateArray();
  }
  
  private boolean isShallowConcavity(Coordinate p0, Coordinate p1, Coordinate p2, double distanceTol)
  {
    int orientation = CGAlgorithms.computeOrientation(p0, p1, p2);
    boolean isAngleToSimplify = (orientation == angleOrientation);
    if (! isAngleToSimplify)
      return false;
    
    double dist = CGAlgorithms.distancePointLine(p1, p0, p2);
    return dist < distanceTol;
  }
}
