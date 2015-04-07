package com.vividsolutions.jts.operation.predicate;

import java.util.*;
import com.vividsolutions.jts.algorithm.*;
import com.vividsolutions.jts.geom.*;

/**
 * Tests if any line segments in two sets of {@link CoordinateSequence}s intersect.
 * Optimized for use when at least one input is of small size.
 * Short-circuited to return as soon an intersection is found.
 *
 * @version 1.7
 */
public class SegmentIntersectionTester {

  // for purposes of intersection testing, don't need to set precision model
  private LineIntersector li = new RobustLineIntersector();

  private boolean hasIntersection = false;
  private Coordinate pt00 = new Coordinate();
  private Coordinate pt01 = new Coordinate();
  private Coordinate pt10 = new Coordinate();
  private Coordinate pt11 = new Coordinate();

  public SegmentIntersectionTester() {
  }

  public boolean hasIntersectionWithLineStrings(CoordinateSequence seq, List lines)
  {
    for (Iterator i = lines.iterator(); i.hasNext(); ) {
      LineString line = (LineString) i.next();
      hasIntersection(seq, line.getCoordinateSequence());
      if (hasIntersection)
        break;
    }
    return hasIntersection;
  }

  public boolean hasIntersection(CoordinateSequence seq0, CoordinateSequence seq1) {
    for (int i = 1; i < seq0.size() && ! hasIntersection; i++) {
      seq0.getCoordinate(i - 1, pt00);
      seq0.getCoordinate(i, pt01);
      for (int j = 1; j < seq1.size() && ! hasIntersection; j++) {
        seq1.getCoordinate(j - 1, pt10);
        seq1.getCoordinate(j, pt11);

        li.computeIntersection(pt00, pt01, pt10, pt11);
        if (li.hasIntersection())
          hasIntersection = true;
      }
    }
    return hasIntersection;
  }
}