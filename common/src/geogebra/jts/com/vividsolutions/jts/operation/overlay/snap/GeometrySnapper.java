package com.vividsolutions.jts.operation.overlay.snap;

import java.util.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.util.GeometryTransformer;

/**
 * Snaps the vertices and segments of a {@link Geometry} to another Geometry's vertices.
 * Improves robustness for overlay operations, by eliminating
 * nearly parallel edges (which cause problems during noding and intersection calculation).
 *
 * @author Martin Davis
 * @version 1.7
 */
public class GeometrySnapper
{
  private static final double SNAP_PRECISION_FACTOR = 10e-10;

  /**
   * Estimates the snap tolerance for a Geometry, taking into account its precision model.
   * 
   * @param g a Geometry
   * @return the estimated snap tolerance
   */
  public static double computeOverlaySnapTolerance(Geometry g)
  {
		double snapTolerance = computeSizeBasedSnapTolerance(g);
		
		/**
		 * Overlay is carried out in the precision model 
		 * of the two inputs.  
		 * If this precision model is of type FIXED, then the snap tolerance
		 * must reflect the precision grid size.  
		 * Specifically, the snap tolerance should be at least 
		 * the distance from a corner of a precision grid cell
		 * to the centre point of the cell.  
		 */
		PrecisionModel pm = g.getPrecisionModel();
		if (pm.getType() == PrecisionModel.FIXED) {
			double fixedSnapTol = (1 / pm.getScale()) * 2 / 1.415;
			if (fixedSnapTol > snapTolerance)
				snapTolerance = fixedSnapTol;
		}
		return snapTolerance;
  }

  public static double computeSizeBasedSnapTolerance(Geometry g)
  {
    Envelope env = g.getEnvelopeInternal();
    double minDimension = Math.min(env.getHeight(), env.getWidth());
    double snapTol = minDimension * SNAP_PRECISION_FACTOR;
    return snapTol;
  }

  public static double computeOverlaySnapTolerance(Geometry g0, Geometry g1)
  {
    return Math.min(computeOverlaySnapTolerance(g0), computeOverlaySnapTolerance(g1));
  }

  /**
   * Snaps two geometries together with a given tolerance.
   * 
   * @param g0 a geometry to snap
   * @param g1 a geometry to snap
   * @param snapTolerance the tolerance to use
   * @return the snapped geometries
   */
  public static Geometry[] snap(Geometry g0, Geometry g1, double snapTolerance)
  {
    Geometry[] snapGeom = new Geometry[2];
    GeometrySnapper snapper0 = new GeometrySnapper(g0);
    snapGeom[0] = snapper0.snapTo(g1, snapTolerance);

    GeometrySnapper snapper1 = new GeometrySnapper(g1);
    /**
     * Snap the second geometry to the snapped first geometry
     * (this strategy minimizes the number of possible different points in the result)
     */
    snapGeom[1] = snapper1.snapTo(snapGeom[0], snapTolerance);

//    System.out.println(snap[0]);
//    System.out.println(snap[1]);
    return snapGeom;
  }
  
  private Geometry srcGeom;

  /**
   * Creates a new snapper acting on the given geometry
   * 
   * @param srcGeom the geometry to snap
   */
  public GeometrySnapper(Geometry srcGeom)
  {
    this.srcGeom = srcGeom;
  }

  /**
   * Computes the snap tolerance based on the input geometries.
   *
   * @param ringPts
   * @return
   */
  private double computeSnapTolerance(Coordinate[] ringPts)
  {
    double minSegLen = computeMinimumSegmentLength(ringPts);
    // use a small percentage of this to be safe
    double snapTol = minSegLen / 10;
    return snapTol;
  }

  private double computeMinimumSegmentLength(Coordinate[] pts)
  {
    double minSegLen = Double.MAX_VALUE;
    for (int i = 0; i < pts.length - 1; i++) {
      double segLen = pts[i].distance(pts[i + 1]);
      if (segLen < minSegLen)
        minSegLen = segLen;
    }
    return minSegLen;
  }

  /**
   * Snaps the vertices in the component {@link LineString}s
   * of the source geometry
   * to the vertices of the given snap geometry.
   *
   * @param snapGeom a geometry to snap the source to
   * @return a new snapped Geometry
   */
  public Geometry snapTo(Geometry snapGeom, double snapTolerance)
  {
    Coordinate[] snapPts = extractTargetCoordinates(snapGeom);

    SnapTransformer snapTrans = new SnapTransformer(snapTolerance, snapPts);
    return snapTrans.transform(srcGeom);
  }

  public Coordinate[] extractTargetCoordinates(Geometry g)
  {
    // TODO: should do this more efficiently.  Use CoordSeq filter to get points, KDTree for uniqueness & queries
    Set ptSet = new TreeSet();
    Coordinate[] pts = g.getCoordinates();
    for (int i = 0; i < pts.length; i++) {
      ptSet.add(pts[i]);
    }
    return (Coordinate[]) ptSet.toArray(new Coordinate[0]);
  }
}

class SnapTransformer
    extends GeometryTransformer
{
  double snapTolerance;
  Coordinate[] snapPts;

  SnapTransformer(double snapTolerance, Coordinate[] snapPts)
  {
    this.snapTolerance = snapTolerance;
    this.snapPts = snapPts;
  }

  protected CoordinateSequence transformCoordinates(CoordinateSequence coords, Geometry parent)
  {
    Coordinate[] srcPts = coords.toCoordinateArray();
    Coordinate[] newPts = snapLine(srcPts, snapPts);
    return factory.getCoordinateSequenceFactory().create(newPts);
  }

  private Coordinate[] snapLine(Coordinate[] srcPts, Coordinate[] snapPts)
  {
    LineStringSnapper snapper = new LineStringSnapper(srcPts, snapTolerance);
    return snapper.snapTo(snapPts);
  }
}


