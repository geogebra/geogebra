package com.vividsolutions.jts.noding;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * An interface for classes which support adding nodes to
 * a segment string.
 * 
 * @author Martin Davis
 */
public interface NodableSegmentString
	extends SegmentString
{
  /**
   * Adds an intersection node for a given point and segment to this segment string.
   * 
   * @param intPt the location of the intersection
   * @param segmentIndex the index of the segment containing the intersection
   */
  public void addIntersection(Coordinate intPt, int segmentIndex);
}
