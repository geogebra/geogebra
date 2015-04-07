package com.vividsolutions.jts.noding;

import com.vividsolutions.jts.geom.Coordinate;
/**
 * Methods for computing and working with octants of the Cartesian plane
 * Octants are numbered as follows:
 * <pre>
 *  \2|1/
 * 3 \|/ 0
 * ---+--
 * 4 /|\ 7
 *  /5|6\
 * <pre>
 * If line segments lie along a coordinate axis, the octant is the lower of the two
 * possible values.
 *
 * @version 1.7
 */
public class Octant {

  /**
   * Returns the octant of a directed line segment (specified as x and y
   * displacements, which cannot both be 0).
   */
  public static int octant(double dx, double dy)
  {
    if (dx == 0.0 && dy == 0.0)
      throw new IllegalArgumentException("Cannot compute the octant for point ( "+ dx + ", " + dy + " )" );

    double adx = Math.abs(dx);
    double ady = Math.abs(dy);

    if (dx >= 0) {
      if (dy >= 0) {
        if (adx >= ady)
          return 0;
        else
          return 1;
      }
      else { // dy < 0
        if (adx >= ady)
          return 7;
        else
          return 6;
      }
    }
    else { // dx < 0
      if (dy >= 0) {
        if (adx >= ady)
          return 3;
        else
          return 2;
      }
      else { // dy < 0
        if (adx >= ady)
          return 4;
        else
          return 5;
      }
    }
  }

  /**
   * Returns the octant of a directed line segment from p0 to p1.
   */
  public static int octant(Coordinate p0, Coordinate p1)
  {
    double dx = p1.x - p0.x;
    double dy = p1.y - p0.y;
    if (dx == 0.0 && dy == 0.0)
      throw new IllegalArgumentException("Cannot compute the octant for two identical points " + p0);
    return octant(dx, dy);
  }

  private Octant() {
  }
}