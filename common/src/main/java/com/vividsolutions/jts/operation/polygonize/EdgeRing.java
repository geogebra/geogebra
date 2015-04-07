
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


package com.vividsolutions.jts.operation.polygonize;

import java.util.*;
import com.vividsolutions.jts.algorithm.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.planargraph.*;

/**
 * Represents a ring of {@link PolygonizeDirectedEdge}s which form
 * a ring of a polygon.  The ring may be either an outer shell or a hole.
 *
 * @version 1.7
 */
public class EdgeRing {

  /**
   * Find the innermost enclosing shell EdgeRing containing the argument EdgeRing, if any.
   * The innermost enclosing ring is the <i>smallest</i> enclosing ring.
   * The algorithm used depends on the fact that:
   * <br>
   *  ring A contains ring B iff envelope(ring A) contains envelope(ring B)
   * <br>
   * This routine is only safe to use if the chosen point of the hole
   * is known to be properly contained in a shell
   * (which is guaranteed to be the case if the hole does not touch its shell)
   *
   * @return containing EdgeRing, if there is one
   * @return null if no containing EdgeRing is found
   */
  public static EdgeRing findEdgeRingContaining(EdgeRing testEr, List shellList)
  {
    LinearRing testRing = testEr.getRing();
    Envelope testEnv = testRing.getEnvelopeInternal();
    Coordinate testPt = testRing.getCoordinateN(0);

    EdgeRing minShell = null;
    Envelope minEnv = null;
    for (Iterator it = shellList.iterator(); it.hasNext(); ) {
      EdgeRing tryShell = (EdgeRing) it.next();
      LinearRing tryRing = tryShell.getRing();
      Envelope tryEnv = tryRing.getEnvelopeInternal();
      if (minShell != null) minEnv = minShell.getRing().getEnvelopeInternal();
      boolean isContained = false;
      // the hole envelope cannot equal the shell envelope
      if (tryEnv.equals(testEnv))
        continue;

//      testPt = ptNotInList(testRing.getCoordinates(), tryRing.getCoordinates());
      testPt = CoordinateArrays.ptNotInList(testRing.getCoordinates(), tryRing.getCoordinates());
      if (tryEnv.contains(testEnv)
          && CGAlgorithms.isPointInRing(testPt, tryRing.getCoordinates()) )
        isContained = true;
      // check if this new containing ring is smaller than the current minimum ring
      if (isContained) {
        if (minShell == null
            || minEnv.contains(tryEnv)) {
          minShell = tryShell;
        }
      }
    }
    return minShell;
  }

  /**
   * Finds a point in a list of points which is not contained in another list of points
   * @param testPts the {@link Coordinate}s to test
   * @param pts an array of {@link Coordinate}s to test the input points against
   * @return a {@link Coordinate} from <code>testPts</code> which is not in <code>pts</code>,
   * @return null if there is no coordinate not in the list
   */
  public static Coordinate ptNotInList(Coordinate[] testPts, Coordinate[] pts)
  {
    for (int i = 0; i < testPts.length; i++) {
      Coordinate testPt = testPts[i];
      if (! isInList(testPt, pts))
          return testPt;
    }
    return null;
  }

  /**
   * Tests whether a given point is in an array of points.
   * Uses a value-based test.
   *
   * @param pt a {@link Coordinate} for the test point
   * @param pts an array of {@link Coordinate}s to test
   * @return <code>true</code> if the point is in the array
   */
  public static boolean isInList(Coordinate pt, Coordinate[] pts)
  {
    for (int i = 0; i < pts.length; i++) {
        if (pt.equals(pts[i]))
            return true;
    }
    return false;
  }
  private GeometryFactory factory;


  private List deList = new ArrayList();

  // cache the following data for efficiency
  private LinearRing ring = null;

  private Coordinate[] ringPts = null;
  private List holes;

  public EdgeRing(GeometryFactory factory)
  {
    this.factory = factory;
  }

  /**
   * Adds a {@link DirectedEdge} which is known to form part of this ring.
   * @param de the {@link DirectedEdge} to add.
   */
  public void add(DirectedEdge de)
  {
    deList.add(de);
  }

  /**
   * Tests whether this ring is a hole.
   * Due to the way the edges in the polyongization graph are linked,
   * a ring is a hole if it is oriented counter-clockwise.
   * @return <code>true</code> if this ring is a hole
   */
  public boolean isHole()
  {
    LinearRing ring = getRing();
    return CGAlgorithms.isCCW(ring.getCoordinates());
  }

  /**
   * Adds a hole to the polygon formed by this ring.
   * @param hole the {@link LinearRing} forming the hole.
   */
  public void addHole(LinearRing hole) {
    if (holes == null)
      holes = new ArrayList();
    holes.add(hole);
  }

  /**
   * Computes the {@link Polygon} formed by this ring and any contained holes.
   *
   * @return the {@link Polygon} formed by this ring and its holes.
   */
  public Polygon getPolygon()
  {
    LinearRing[] holeLR = null;
    if (holes != null) {
      holeLR = new LinearRing[holes.size()];
      for (int i = 0; i < holes.size(); i++) {
        holeLR[i] = (LinearRing) holes.get(i);
      }
    }
    Polygon poly = factory.createPolygon(ring, holeLR);
    return poly;
  }

  /**
   * Tests if the {@link LinearRing} ring formed by this edge ring is topologically valid.
   * @return
   */
  public boolean isValid()
  {
    getCoordinates();
    if (ringPts.length <= 3) return false;
    getRing();
    return ring.isValid();
  }

  /**
   * Computes the list of coordinates which are contained in this ring.
   * The coordinatea are computed once only and cached.
   *
   * @return an array of the {@link Coordinate}s in this ring
   */
  private Coordinate[] getCoordinates()
  {
    if (ringPts == null) {
      CoordinateList coordList = new CoordinateList();
      for (Iterator i = deList.iterator(); i.hasNext(); ) {
        DirectedEdge de = (DirectedEdge) i.next();
        PolygonizeEdge edge = (PolygonizeEdge) de.getEdge();
        addEdge(edge.getLine().getCoordinates(), de.getEdgeDirection(), coordList);
      }
      ringPts = coordList.toCoordinateArray();
    }
    return ringPts;
  }

  /**
   * Gets the coordinates for this ring as a {@link LineString}.
   * Used to return the coordinates in this ring
   * as a valid geometry, when it has been detected that the ring is topologically
   * invalid.
   * @return a {@link LineString} containing the coordinates in this ring
   */
  public LineString getLineString()
  {
    getCoordinates();
    return factory.createLineString(ringPts);
  }

  /**
   * Returns this ring as a {@link LinearRing}, or null if an Exception occurs while
   * creating it (such as a topology problem). Details of problems are written to
   * standard output.
   */
  public LinearRing getRing()
  {
    if (ring != null) return ring;
    getCoordinates();
    if (ringPts.length < 3) System.out.println(ringPts);
    try {
      ring = factory.createLinearRing(ringPts);
    }
    catch (Exception ex) {
      System.out.println(ringPts);
    }
    return ring;
  }

  private static void addEdge(Coordinate[] coords, boolean isForward, CoordinateList coordList)
  {
    if (isForward) {
      for (int i = 0; i < coords.length; i++) {
        coordList.add(coords[i], false);
      }
    }
    else {
      for (int i = coords.length - 1; i >= 0; i--) {
        coordList.add(coords[i], false);
      }
    }
  }
}
