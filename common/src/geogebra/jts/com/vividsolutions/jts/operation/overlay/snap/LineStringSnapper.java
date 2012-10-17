package com.vividsolutions.jts.operation.overlay.snap;

import com.vividsolutions.jts.geom.*;

/**
 * Snaps the vertices and segments of a {@link LineString} to a set of target snap vertices.
 * A snapping distance tolerance is used to control where snapping is performed.
 *
 * @author Martin Davis
 * @version 1.7
 */
public class LineStringSnapper
{
  private double snapTolerance = 0.0;

  private Coordinate[] srcPts;
  private LineSegment seg = new LineSegment(); // for reuse during snapping
  private boolean isClosed = false;

  /**
   * Creates a new snapper using the points in the given {@link LineString}
   * as source snap points.
   * 
   * @param srcLline a LineString to snap
   * @param snapTolerance the snap tolerance to use
   */
  public LineStringSnapper(LineString srcLline, double snapTolerance)
  {
    this(srcLline.getCoordinates(), snapTolerance);
  }

  /**
   * Creates a new snapper using the given points
   * as source points to be snapped.
   * 
   * @param srcPts the points to snap 
   * @param snapTolerance the snap tolerance to use
   */
  public LineStringSnapper(Coordinate[] srcPts, double snapTolerance)
  {
    this.srcPts = srcPts;
    isClosed = srcPts[0].equals2D(srcPts[srcPts.length - 1]);
    this.snapTolerance = snapTolerance;
  }

  /**
   * Snaps the vertices and segments of the source LineString 
   * to the given set of target snap points.
   * 
   * @param snapPts the vertices to snap to
   * @return a list of the snapped points
   */
  public Coordinate[] snapTo(Coordinate[] snapPts)
  {
    CoordinateList coordList = new CoordinateList(srcPts);

    snapVertices(coordList, snapPts);
    snapSegments(coordList, snapPts);

    Coordinate[] newPts = coordList.toCoordinateArray();
    return newPts;
  }

  /**
   * Snap source vertices to vertices in the target.
   * 
   * @param srcCoords the points to snap
   * @param snapPts the points to snap to
   */
  private void snapVertices(CoordinateList srcCoords, Coordinate[] snapPts)
  {
    // try snapping vertices
    // assume src list has a closing point (is a ring)
    for (int i = 0; i < srcCoords.size() - 1; i++) {
      Coordinate srcPt = (Coordinate) srcCoords.get(i);
      Coordinate snapVert = findSnapForVertex(srcPt, snapPts);
      if (snapVert != null) {
        // update src with snap pt
        srcCoords.set(i, new Coordinate(snapVert));
        // keep final closing point in synch (rings only)
        if (i == 0 && isClosed)
          srcCoords.set(srcCoords.size() - 1, new Coordinate(snapVert));
      }
    }
  }

  private Coordinate findSnapForVertex(Coordinate pt, Coordinate[] snapPts)
  {
    for (int i = 0; i < snapPts.length; i++) {
      // if point is already equal to a src pt, don't snap
      if (pt.equals2D(snapPts[i]))
        return null;
      if (pt.distance(snapPts[i]) < snapTolerance)
        return snapPts[i];
    }
    return null;
  }

  /**
   * Snap segments of the source to nearby snap vertices.
   * Source segments are "cracked" at a snap vertex, and further
   * snapping takes place on the modified list of segments.
   * For each distinct snap vertex, at most one source segment
   * is snapped to.  This prevents "cracking" multiple segments 
   * at the same point, which would almost certainly cause the result to be invalid.
   * 
   * @param srcCoords
   * @param snapPts
   */
  private void snapSegments(CoordinateList srcCoords, Coordinate[] snapPts)
  {
    int distinctPtCount = snapPts.length;

    // check for duplicate snap pts.  
    // Need to do this better - need to check all points for dups (using a Set?)
    if (snapPts[0].equals2D(snapPts[snapPts.length - 1]))
        distinctPtCount = snapPts.length - 1;

    for (int i = 0; i < distinctPtCount; i++) {
      Coordinate snapPt = snapPts[i];
      int index = findSegmentIndexToSnap(snapPt, srcCoords);
      /**
       * If a segment to snap to was found, "crack" it at the snap pt.
       * The new pt is inserted immediately into the src segment list,
       * so that subsequent snapping will take place on the latest segments.
       * Duplicate points are not added.
       */
      if (index >= 0) {
        srcCoords.add(index + 1, new Coordinate(snapPt), false);
      }
    }
  }


  /**
   * Finds a src segment which snaps to (is close to) the given snap point
   * Only one segment is determined - this is to prevent
   * snapping to multiple segments, which would almost certainly cause invalid geometry
   * to be created.
   * (The heuristic approach of snapping is really only appropriate when
   * snap pts snap to a unique spot on the src geometry.)
   *
   * @param snapPt the point to snap to
   * @param srcCoords the source segment coordinates
   * @return the index of the snapped segment
   * @return -1 if no segment snaps
   */
  private int findSegmentIndexToSnap(Coordinate snapPt, CoordinateList srcCoords)
  {
    double minDist = Double.MAX_VALUE;
    int snapIndex = -1;
    for (int i = 0; i < srcCoords.size() - 1; i++) {
      seg.p0 = (Coordinate) srcCoords.get(i);
      seg.p1 = (Coordinate) srcCoords.get(i + 1);

      /**
       * If the snap pt is already in the src list, don't snap
       */
      if (seg.p0.equals2D(snapPt) || seg.p1.equals2D(snapPt))
        return -1;

      double dist = seg.distance(snapPt);
      if (dist < snapTolerance && dist < minDist) {
        minDist = dist;
        snapIndex = i;
      }
    }
    return snapIndex;
  }

}
