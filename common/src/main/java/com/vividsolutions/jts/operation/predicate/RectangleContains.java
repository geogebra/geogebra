package com.vividsolutions.jts.operation.predicate;

import com.vividsolutions.jts.geom.*;

/**
 * Optimized implementation of spatial predicate "contains"
 * for cases where the first {@link Geometry} is a rectangle.
 * <p>
 * As a further optimization,
 * this class can be used directly to test many geometries against a single
 * rectangle.
 *
 * @version 1.7
 */
public class RectangleContains {

  public static boolean contains(Polygon rectangle, Geometry b)
  {
    RectangleContains rc = new RectangleContains(rectangle);
    return rc.contains(b);
  }

  private Polygon rectangle;
  private Envelope rectEnv;

  /**
   * Create a new contains computer for two geometries.
   *
   * @param rectangle a rectangular geometry
   */
  public RectangleContains(Polygon rectangle) {
    this.rectangle = rectangle;
    rectEnv = rectangle.getEnvelopeInternal();
  }

  public boolean contains(Geometry geom)
  {
    if (! rectEnv.contains(geom.getEnvelopeInternal()))
      return false;
    // check that geom is not contained entirely in the rectangle boundary
    if (isContainedInBoundary(geom))
      return false;
    return true;
  }

  private boolean isContainedInBoundary(Geometry geom)
  {
    // polygons can never be wholely contained in the boundary
    if (geom instanceof Polygon) return false;
    if (geom instanceof Point) return isPointContainedInBoundary((Point) geom);
    if (geom instanceof LineString) return isLineStringContainedInBoundary((LineString) geom);

    for (int i = 0; i < geom.getNumGeometries(); i++) {
      Geometry comp = geom.getGeometryN(i);
      if (! isContainedInBoundary(comp))
        return false;
    }
    return true;
  }

  private boolean isPointContainedInBoundary(Point point)
  {
    return isPointContainedInBoundary(point.getCoordinate());
  }

  /**
   * Tests if a point is contained in the boundary of the target rectangle.
   * 
   * @param pt the point to test
   * @return true if the point is contained in the boundary
   */
  private boolean isPointContainedInBoundary(Coordinate pt)
  {
    /**
     * contains = false iff the point is properly contained in the rectangle.
     * 
     * This code assumes that the point lies in the rectangle envelope
     */ 
    return pt.x == rectEnv.getMinX() 
    		|| pt.x == rectEnv.getMaxX()
    		|| pt.y == rectEnv.getMinY()
    		|| pt.y == rectEnv.getMaxY();
  }

  /**
   * Tests if a linestring is completely contained in the boundary of the target rectangle.
   * @param line the linestring to test
   * @return true if the linestring is contained in the boundary
   */
  private boolean isLineStringContainedInBoundary(LineString line)
  {
    CoordinateSequence seq = line.getCoordinateSequence();
    Coordinate p0 = new Coordinate();
    Coordinate p1 = new Coordinate();
    for (int i = 0; i < seq.size() - 1; i++) {
      seq.getCoordinate(i, p0);
      seq.getCoordinate(i + 1, p1);

      if (! isLineSegmentContainedInBoundary(p0, p1))
        return false;
    }
    return true;
  }

  /**
   * Tests if a line segment is contained in the boundary of the target rectangle.
   * @param p0 an endpoint of the segment
   * @param p1 an endpoint of the segment
   * @return true if the line segment is contained in the boundary
   */
  private boolean isLineSegmentContainedInBoundary(Coordinate p0, Coordinate p1)
  {
    if (p0.equals(p1))
      return isPointContainedInBoundary(p0);

    // we already know that the segment is contained in the rectangle envelope
    if (p0.x == p1.x) {
      if (p0.x == rectEnv.getMinX() ||
          p0.x == rectEnv.getMaxX() )
        return true;
    }
    else if (p0.y == p1.y) {
      if (p0.y == rectEnv.getMinY() ||
          p0.y == rectEnv.getMaxY() )
        return true;
    }
    /**
     * Either
     *   both x and y values are different
     * or
     *   one of x and y are the same, but the other ordinate is not the same as a boundary ordinate
     *
     * In either case, the segment is not wholely in the boundary
     */
    return false;
  }

}