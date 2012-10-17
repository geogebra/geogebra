package com.vividsolutions.jts.linearref;

import com.vividsolutions.jts.geom.*;

/**
 * Computes the {@link LinearLocation} for a given length
 * along a linear {@link Geometry}.
 * Negative lengths are measured in reverse from end of the linear geometry.
 * Out-of-range values are clamped.
 */
public class LengthLocationMap
{
  // TODO: cache computed cumulative length for each vertex
  // TODO: support user-defined measures
  // TODO: support measure index for fast mapping to a location

  /**
   * Computes the {@link LinearLocation} for a
   * given length along a linear {@link Geometry}.
   *
   * @param line the linear geometry to use
   * @param length the length index of the location
   * @return the {@link LinearLocation} for the length
   */
  public static LinearLocation getLocation(Geometry linearGeom, double length)
  {
    LengthLocationMap locater = new LengthLocationMap(linearGeom);
    return locater.getLocation(length);
  }

  /**
   * Computes the length for a given {@link LinearLocation}
   * on a linear {@link Geometry}.
   *
   * @param line the linear geometry to use
   * @param loc the {@link LinearLocation} index of the location
   * @return the length for the {@link LinearLocation}
   */
  public static double getLength(Geometry linearGeom, LinearLocation loc)
  {
    LengthLocationMap locater = new LengthLocationMap(linearGeom);
    return locater.getLength(loc);
  }

  private Geometry linearGeom;

  public LengthLocationMap(Geometry linearGeom)
  {
    this.linearGeom = linearGeom;
  }

  /**
   * Compute the {@link LinearLocation} corresponding to a length.
   * Negative lengths are measured in reverse from end of the linear geometry.
   * Out-of-range values are clamped.
   *
   * @param length the length index
   * @return the corresponding LinearLocation
   */
  public LinearLocation getLocation(double length)
  {
    double forwardLength = length;
    if (length < 0.0) {
      double lineLen = linearGeom.getLength();
      forwardLength = lineLen + length;
    }
    return getLocationForward(forwardLength);
  }

  private LinearLocation getLocationForward(double length)
  {
    if (length <= 0.0)
      return new LinearLocation();

    double totalLength = 0.0;

    LinearIterator it = new LinearIterator(linearGeom);
    while (it.hasNext()) {
      if (! it.isEndOfLine()) {
        Coordinate p0 = it.getSegmentStart();
        Coordinate p1 = it.getSegmentEnd();
        double segLen = p1.distance(p0);
        // length falls in this segment
        if (totalLength + segLen > length) {
          double frac = (length - totalLength) / segLen;
          int compIndex = it.getComponentIndex();
          int segIndex = it.getVertexIndex();
          return new LinearLocation(compIndex, segIndex, frac);
        }
        totalLength += segLen;
      }
      it.next();
    }
    // length is longer than line - return end location
    return LinearLocation.getEndLocation(linearGeom);
  }

  public double getLength(LinearLocation loc)
  {
    double totalLength = 0.0;

    LinearIterator it = new LinearIterator(linearGeom);
    while (it.hasNext()) {
      if (! it.isEndOfLine()) {
        Coordinate p0 = it.getSegmentStart();
        Coordinate p1 = it.getSegmentEnd();
        double segLen = p1.distance(p0);
        // length falls in this segment
        if (loc.getComponentIndex() == it.getComponentIndex()
            && loc.getSegmentIndex() == it.getVertexIndex()) {
          return totalLength + segLen * loc.getSegmentFraction();
        }
        totalLength += segLen;
      }
      it.next();
    }
    return totalLength;
  }
}
