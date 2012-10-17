package com.vividsolutions.jts.simplify;

import java.util.Iterator;
import java.util.List;

import com.vividsolutions.jts.algorithm.LineIntersector;
import com.vividsolutions.jts.algorithm.RobustLineIntersector;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

/**
 * Simplifies a TaggedLineString, preserving topology
 * (in the sense that no new intersections are introduced).
 * Uses the recursive Douglas-Peucker algorithm.
 *
 * @author Martin Davis
 * @version 1.7
 */
public class TaggedLineStringSimplifier
{
  private static LineIntersector li = new RobustLineIntersector();
  private LineSegmentIndex inputIndex = new LineSegmentIndex();
  private LineSegmentIndex outputIndex = new LineSegmentIndex();
  private TaggedLineString line;
  private Coordinate[] linePts;
  private double distanceTolerance = 0.0;

  public TaggedLineStringSimplifier(LineSegmentIndex inputIndex,
                                     LineSegmentIndex outputIndex)
  {
    this.inputIndex = inputIndex;
    this.outputIndex = outputIndex;
  }

  /**
   * Sets the distance tolerance for the simplification.
   * All vertices in the simplified geometry will be within this
   * distance of the original geometry.
   *
   * @param distanceTolerance the approximation tolerance to use
   */
  public void setDistanceTolerance(double distanceTolerance) {
    this.distanceTolerance = distanceTolerance;
  }

  public void simplify(TaggedLineString line)
  {
    this.line = line;
    linePts = line.getParentCoordinates();
    simplifySection(0, linePts.length - 1, 0);
  }

  private void simplifySection(int i, int j, int depth)
  {
    depth += 1;
    int[] sectionIndex = new int[2];
    if((i+1) == j) {
      LineSegment newSeg = line.getSegment(i);
      line.addToResult(newSeg);
      // leave this segment in the input index, for efficiency
      return;
    }

    boolean isValidToSimplify = true;

    /**
     * Following logic ensures that there is enough points in the output line.
     * If there is already more points than the minimum, there's nothing to check.
     * Otherwise, if in the worst case there wouldn't be enough points,
     * don't flatten this segment (which avoids the worst case scenario)
     */
    if (line.getResultSize() < line.getMinimumSize()) {
      int worstCaseSize = depth + 1;
      if (worstCaseSize < line.getMinimumSize())
        isValidToSimplify = false;
    }

    double[] distance = new double[1];
    int furthestPtIndex = findFurthestPoint(linePts, i, j, distance);
    // flattening must be less than distanceTolerance
    if (distance[0] > distanceTolerance) isValidToSimplify = false;
    // test if flattened section would cause intersection
    LineSegment candidateSeg = new LineSegment();
    candidateSeg.p0 = linePts[i];
    candidateSeg.p1 = linePts[j];
    sectionIndex[0] = i;
    sectionIndex[1] = j;
    if (hasBadIntersection(line, sectionIndex, candidateSeg)) isValidToSimplify = false;

    if (isValidToSimplify) {
      LineSegment newSeg = flatten(i, j);
      line.addToResult(newSeg);
      return;
    }
    simplifySection(i, furthestPtIndex, depth);
    simplifySection(furthestPtIndex, j, depth);
  }

  private int findFurthestPoint(Coordinate[] pts, int i, int j, double[] maxDistance)
  {
    LineSegment seg = new LineSegment();
    seg.p0 = pts[i];
    seg.p1 = pts[j];
    double maxDist = -1.0;
    int maxIndex = i;
    for (int k = i + 1; k < j; k++) {
      Coordinate midPt = pts[k];
      double distance = seg.distance(midPt);
      if (distance > maxDist) {
        maxDist = distance;
        maxIndex = k;
      }
    }
    maxDistance[0] = maxDist;
    return maxIndex;
  }

  private LineSegment flatten(int start, int end)
  {
    // make a new segment for the simplified geometry
    Coordinate p0 = linePts[start];
    Coordinate p1 = linePts[end];
    LineSegment newSeg = new LineSegment(p0, p1);
// update the indexes
    remove(line, start, end);
    outputIndex.add(newSeg);
    return newSeg;
  }

  /**
   * Index of section to be tested for flattening - reusable
   */
  private int[] validSectionIndex = new int[2];


  private boolean hasBadIntersection(TaggedLineString parentLine,
                       int[] sectionIndex,
                       LineSegment candidateSeg)
  {
    if (hasBadOutputIntersection(candidateSeg)) return true;
    if (hasBadInputIntersection(parentLine, sectionIndex, candidateSeg)) return true;
    return false;
  }

  private boolean hasBadOutputIntersection(LineSegment candidateSeg)
  {
    List querySegs = outputIndex.query(candidateSeg);
    for (Iterator i = querySegs.iterator(); i.hasNext(); ) {
      LineSegment querySeg = (LineSegment) i.next();
      if (hasInteriorIntersection(querySeg, candidateSeg)) {
          return true;
      }
    }
    return false;
  }

  private boolean hasBadInputIntersection(TaggedLineString parentLine,
                       int[] sectionIndex,
                       LineSegment candidateSeg)
  {
    List querySegs = inputIndex.query(candidateSeg);
    for (Iterator i = querySegs.iterator(); i.hasNext(); ) {
      TaggedLineSegment querySeg = (TaggedLineSegment) i.next();
      if (hasInteriorIntersection(querySeg, candidateSeg)) {
          if (isInLineSection(parentLine, sectionIndex, querySeg))
            continue;
          return true;
      }
    }
    return false;
  }

  /**
   * Tests whether a segment is in a section of a TaggedLineString
   * @param line
   * @param sectionIndex
   * @param seg
   * @return
   */
  private static boolean isInLineSection(
      TaggedLineString line,
      int[] sectionIndex,
      TaggedLineSegment seg)
  {
    // not in this line
    if (seg.getParent() != line.getParent())
      return false;
    int segIndex = seg.getIndex();
    if (segIndex >= sectionIndex[0] && segIndex < sectionIndex[1])
      return true;
    return false;
  }

  private boolean hasInteriorIntersection(LineSegment seg0, LineSegment seg1)
  {
    li.computeIntersection(seg0.p0, seg0.p1, seg1.p0, seg1.p1);
    return li.isInteriorIntersection();
  }

  /**
   * Remove the segs in the section of the line
   * @param line
   * @param pts
   * @param sectionStartIndex
   * @param sectionEndIndex
   */
  private void remove(TaggedLineString line,
                       int start, int end)
  {
    for (int i = start; i < end; i++) {
      TaggedLineSegment seg = line.getSegment(i);
      inputIndex.remove(seg);
    }
  }
}