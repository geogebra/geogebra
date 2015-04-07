package com.vividsolutions.jts.simplify;

import java.util.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.util.*;

/**
 * Simplifies a {@link Geometry} using the standard Douglas-Peucker algorithm.
 * Ensures that any polygonal geometries returned are valid.
 * Simple lines are not guaranteed to remain simple after simplification.
 * <p>
 * Note that in general D-P does not preserve topology -
 * e.g. polygons can be split, collapse to lines or disappear
 * holes can be created or disappear,
 * and lines can cross.
 * To simplify geometry while preserving topology use {@link TopologyPreservingSimplifier}.
 * (However, using D-P is significantly faster).
 *
 * @version 1.7
 */
public class DouglasPeuckerSimplifier
{

  public static Geometry simplify(Geometry geom, double distanceTolerance)
  {
    DouglasPeuckerSimplifier tss = new DouglasPeuckerSimplifier(geom);
    tss.setDistanceTolerance(distanceTolerance);
    return tss.getResultGeometry();
  }

  private Geometry inputGeom;
  private double distanceTolerance;
  private boolean isEnsureValidTopology = true;
  
  public DouglasPeuckerSimplifier(Geometry inputGeom)
  {
    this.inputGeom = inputGeom;
  }

  /**
   * Sets the distance tolerance for the simplification.
   * All vertices in the simplified geometry will be within this
   * distance of the original geometry.
   * The tolerance value must be non-negative. 
   *
   * @param distanceTolerance the approximation tolerance to use
   */
  public void setDistanceTolerance(double distanceTolerance) {
    if (distanceTolerance < 0.0)
      throw new IllegalArgumentException("Tolerance must be non-negative");
    this.distanceTolerance = distanceTolerance;
  }

  /**
   * Controls whether simplified polygons will be "fixed"
   * to have valid topology.
   * The caller may choose to disable this because:
   * <ul>
   * <li>valid topology is not required
   * <li>fixing topology is a relative expensive operation
   * <li>in some pathological cases the topology fixing operation may either fail or run for too long
   * </ul>
   * 
   * The default is to fix polygon topology.
   * 
   * @param isEnsureValidTopology
   */
  public void setEnsureValid(boolean isEnsureValidTopology)
  {
  	this.isEnsureValidTopology = isEnsureValidTopology;
  }
  
  public Geometry getResultGeometry()
  {
    return (new DPTransformer(isEnsureValidTopology)).transform(inputGeom);
  }

class DPTransformer
    extends GeometryTransformer
{
  private boolean isEnsureValidTopology = true;

	public DPTransformer(boolean isEnsureValidTopology)
	{
		this.isEnsureValidTopology = isEnsureValidTopology;
	}
	
  protected CoordinateSequence transformCoordinates(CoordinateSequence coords, Geometry parent)
  {
    Coordinate[] inputPts = coords.toCoordinateArray();
    Coordinate[] newPts = DouglasPeuckerLineSimplifier.simplify(inputPts, distanceTolerance);
    return factory.getCoordinateSequenceFactory().create(newPts);
  }

  /**
   * Simplifies a polygon, fixing it if required.
   */
  protected Geometry transformPolygon(Polygon geom, Geometry parent) {
    Geometry rawGeom = super.transformPolygon(geom, parent);
    // don't try and correct if the parent is going to do this
    if (parent instanceof MultiPolygon) {
      return rawGeom;
    }
    return createValidArea(rawGeom);
  }

  /**
   * Simplifies a LinearRing.  If the simplification results 
   * in a degenerate ring, remove the component.
   * 
   * @return null if the simplification results in a degenerate ring
   */
  protected Geometry transformLinearRing(LinearRing geom, Geometry parent) 
  {
  	boolean removeDegenerateRings = parent instanceof Polygon;
  	Geometry simpResult = super.transformLinearRing(geom, parent);
  	if (removeDegenerateRings && ! (simpResult instanceof LinearRing))
  		return null;;
  	return simpResult;
  }
  
  /**
   * Simplifies a MultiPolygon, fixing it if required.
   */
  protected Geometry transformMultiPolygon(MultiPolygon geom, Geometry parent) {
    Geometry rawGeom = super.transformMultiPolygon(geom, parent);
    return createValidArea(rawGeom);
  }

  /**
   * Creates a valid area geometry from one that possibly has
   * bad topology (i.e. self-intersections).
   * Since buffer can handle invalid topology, but always returns
   * valid geometry, constructing a 0-width buffer "corrects" the
   * topology.
   * Note this only works for area geometries, since buffer always returns
   * areas.  This also may return empty geometries, if the input
   * has no actual area.
   *
   * @param rawAreaGeom an area geometry possibly containing self-intersections
   * @return a valid area geometry
   */
  private Geometry createValidArea(Geometry rawAreaGeom)
  {
  	if ( isEnsureValidTopology)
  		return rawAreaGeom.buffer(0.0);
  	return rawAreaGeom;
  }
}

}


