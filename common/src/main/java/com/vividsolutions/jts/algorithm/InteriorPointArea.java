
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
package com.vividsolutions.jts.algorithm;

import com.vividsolutions.jts.geom.*;

/**
 * Computes a point in the interior of an area geometry.
 *
 * <h2>Algorithm</h2>
 * <ul>
 *   <li>Find the intersections between the geometry
 *       and the horizontal bisector of the area's envelope
 *   <li>Pick the midpoint of the largest intersection (the intersections
 *       will be lines and points)
 * </ul>
 *
 * <b>
 * Note: If a fixed precision model is used,
 * in some cases this method may return a point
 * which does not lie in the interior.
 * </b>
 *
 * @version 1.7
 */
public class InteriorPointArea {

  private static double avg(double a, double b)
  {
    return (a + b) / 2.0;
  }

  private GeometryFactory factory;
  private Coordinate interiorPoint = null;
  private double maxWidth = 0.0;

  public InteriorPointArea(Geometry g)
  {
    factory = g.getFactory();
    add(g);
  }
  public Coordinate getInteriorPoint()
  {
    return interiorPoint;
  }

  /**
   * Tests the interior vertices (if any)
   * defined by an areal Geometry for the best inside point.
   * If a component Geometry is not of dimension 2 it is not tested.
   * 
   * @param geom the geometry to add
   */
  private void add(Geometry geom)
  {
    if (geom instanceof Polygon) {
      addPolygon(geom);
    }
    else if (geom instanceof GeometryCollection) {
      GeometryCollection gc = (GeometryCollection) geom;
      for (int i = 0; i < gc.getNumGeometries(); i++) {
        add(gc.getGeometryN(i));
      }
    }
  }

  /**
   * Finds a reasonable point at which to label a Geometry.
   * @param geometry the geometry to analyze
   * @return the midpoint of the largest intersection between the geometry and
   * a line halfway down its envelope
   */
  public void addPolygon(Geometry geometry) {
      LineString bisector = horizontalBisector(geometry);

      Geometry intersections = bisector.intersection(geometry);
      Geometry widestIntersection = widestGeometry(intersections);

      double width = widestIntersection.getEnvelopeInternal().getWidth();
      if (interiorPoint == null || width > maxWidth) {
        interiorPoint = centre(widestIntersection.getEnvelopeInternal());
        maxWidth = width;
      }
  }

  //@return if geometry is a collection, the widest sub-geometry; otherwise,
  //the geometry itself
  protected Geometry widestGeometry(Geometry geometry) {
    if (!(geometry instanceof GeometryCollection)) {
        return geometry;
    }
    return widestGeometry((GeometryCollection) geometry);
  }

  private Geometry widestGeometry(GeometryCollection gc) {
    if (gc.isEmpty()) {
        return gc;
    }

    Geometry widestGeometry = gc.getGeometryN(0);
    for (int i = 1; i < gc.getNumGeometries(); i++) { //Start at 1
        if (gc.getGeometryN(i).getEnvelopeInternal().getWidth() >
            widestGeometry.getEnvelopeInternal().getWidth()) {
            widestGeometry = gc.getGeometryN(i);
        }
    }
    return widestGeometry;
  }

  protected LineString horizontalBisector(Geometry geometry) {
    Envelope envelope = geometry.getEnvelopeInternal();

    // Assert: for areas, minx <> maxx
    double avgY = avg(envelope.getMinY(), envelope.getMaxY());
    return factory.createLineString(new Coordinate[] {
            new Coordinate(envelope.getMinX(), avgY),
            new Coordinate(envelope.getMaxX(), avgY)
        });
  }

  /**
   * Returns the centre point of the envelope.
   * @param envelope the envelope to analyze
   * @return the centre of the envelope
   */
  public Coordinate centre(Envelope envelope) {
      return new Coordinate(avg(envelope.getMinX(),
              envelope.getMaxX()),
          avg(envelope.getMinY(), envelope.getMaxY()));
  }

}
