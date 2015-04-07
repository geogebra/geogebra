package com.vividsolutions.jts.linearref;

import java.util.*;
import com.vividsolutions.jts.geom.*;

/**
 * Builds a linear geometry ({@link LineString} or {@link MultiLineString})
 * incrementally (point-by-point).
 *
 * @version 1.7
 */
public class LinearGeometryBuilder
{
  private GeometryFactory geomFact;
  private List lines = new ArrayList();
  private CoordinateList coordList = null;

  private boolean ignoreInvalidLines = false;
  private boolean fixInvalidLines = false;

  private Coordinate lastPt = null;

  public LinearGeometryBuilder(GeometryFactory geomFact) {
    this.geomFact = geomFact;
  }

  /**
   * Allows invalid lines to be ignored rather than causing Exceptions.
   * An invalid line is one which has only one unique point.
   *
   * @param ignoreShortLines <code>true</code> if short lines are to be ignored
   */
  public void setIgnoreInvalidLines(boolean ignoreInvalidLines)
  {
    this.ignoreInvalidLines = ignoreInvalidLines;
  }

  /**
   * Allows invalid lines to be ignored rather than causing Exceptions.
   * An invalid line is one which has only one unique point.
   *
   * @param ignoreShortLines <code>true</code> if short lines are to be ignored
   */
  public void setFixInvalidLines(boolean fixInvalidLines)
  {
    this.fixInvalidLines = fixInvalidLines;
  }

  /**
   * Adds a point to the current line.
   *
   * @param pt the Coordinate to add
   */
  public void add(Coordinate pt)
  {
    add(pt, true);
  }

  /**
   * Adds a point to the current line.
   *
   * @param pt the Coordinate to add
   */
  public void add(Coordinate pt, boolean allowRepeatedPoints)
  {
    if (coordList == null)
      coordList = new CoordinateList();
    coordList.add(pt, allowRepeatedPoints);
    lastPt = pt;
  }

  public Coordinate getLastCoordinate() { return lastPt; }

  /**
   * Terminate the current LineString.
   */
  public void endLine()
  {
    if (coordList == null) {
      return;
    }
    if (ignoreInvalidLines && coordList.size() < 2) {
      coordList = null;
      return;
    }
    Coordinate[] rawPts = coordList.toCoordinateArray();
    Coordinate[] pts = rawPts;
    if (fixInvalidLines)
      pts = validCoordinateSequence(rawPts);

    coordList = null;
    LineString line = null;
    try {
      line = geomFact.createLineString(pts);
    }
    catch (IllegalArgumentException ex) {
      // exception is due to too few points in line.
      // only propagate if not ignoring short lines
      if (! ignoreInvalidLines)
        throw ex;
    }

    if (line != null) lines.add(line);
  }

  private Coordinate[] validCoordinateSequence(Coordinate[] pts)
  {
    if (pts.length >= 2) return pts;
    Coordinate[] validPts = new Coordinate[] { pts[0], pts[0]};
    return validPts;
  }

  public Geometry getGeometry()
  {
    // end last line in case it was not done by user
    endLine();
    return geomFact.buildGeometry(lines);
  }
}