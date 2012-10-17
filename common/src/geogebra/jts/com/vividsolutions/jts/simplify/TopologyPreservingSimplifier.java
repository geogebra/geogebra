package com.vividsolutions.jts.simplify;

import java.util.HashMap;
import java.util.Map;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryComponentFilter;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.util.GeometryTransformer;

/**
 * Simplifies a geometry, ensuring that
 * the result is a valid geometry having the
 * same dimension and number of components as the input.
 * The simplification uses a maximum distance difference algorithm
 * similar to the one used in the Douglas-Peucker algorithm.
 * <p>
 * In particular, if the input is an areal geometry
 * ( {@link Polygon} or {@link MultiPolygon} )
 * <ul>
 * <li>The result has the same number of shells and holes (rings) as the input,
 * in the same order
 * <li>The result rings touch at <b>no more</b> than the number of touching point in the input
 * (although they may touch at fewer points)
 * </ul>
 *
 * @author Martin Davis
 * @see DouglasPeuckerSimplifier
 *
 */
public class TopologyPreservingSimplifier
{
  public static Geometry simplify(Geometry geom, double distanceTolerance)
  {
    TopologyPreservingSimplifier tss = new TopologyPreservingSimplifier(geom);
    tss.setDistanceTolerance(distanceTolerance);
    return tss.getResultGeometry();
  }

  private Geometry inputGeom;
  private TaggedLinesSimplifier lineSimplifier = new TaggedLinesSimplifier();
  private Map linestringMap;

  public TopologyPreservingSimplifier(Geometry inputGeom)
  {
    this.inputGeom = inputGeom;
 }

  /**
   * Sets the distance tolerance for the simplification.
   * All vertices in the simplified geometry will be within this
   * distance of the original geometry.
   * The tolerance value must be non-negative.  A tolerance value
   * of zero is effectively a no-op.
   *
   * @param distanceTolerance the approximation tolerance to use
   */
  public void setDistanceTolerance(double distanceTolerance) {
    if (distanceTolerance < 0.0)
      throw new IllegalArgumentException("Tolerance must be non-negative");
    lineSimplifier.setDistanceTolerance(distanceTolerance);
  }

  public Geometry getResultGeometry() {
    linestringMap = new HashMap();
    inputGeom.apply(new LineStringMapBuilderFilter());
    lineSimplifier.simplify(linestringMap.values());
    Geometry result = (new LineStringTransformer()).transform(inputGeom);
    return result;
  }

  class LineStringTransformer
      extends GeometryTransformer
  {
    protected CoordinateSequence transformCoordinates(CoordinateSequence coords, Geometry parent)
    {
    	// for linear components (including rings), simplify the linestring
      if (parent instanceof LineString) {
        TaggedLineString taggedLine = (TaggedLineString) linestringMap.get(parent);
        return createCoordinateSequence(taggedLine.getResultCoordinates());
      }
      // for anything else (e.g. points) just copy the coordinates
      return super.transformCoordinates(coords, parent);
    }
  }

  class LineStringMapBuilderFilter
      implements GeometryComponentFilter
  {
    public void filter(Geometry geom)
    {
      if (geom instanceof LinearRing) {
        TaggedLineString taggedLine = new TaggedLineString((LineString) geom, 4);
        linestringMap.put(geom, taggedLine);
      }
      else if (geom instanceof LineString) {
        TaggedLineString taggedLine = new TaggedLineString((LineString) geom, 2);
        linestringMap.put(geom, taggedLine);
      }
    }
  }

}

