package com.vividsolutions.jts.operation.predicate;

import java.util.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.algorithm.*;
import com.vividsolutions.jts.algorithm.locate.SimplePointInAreaLocator;
import com.vividsolutions.jts.geom.util.*;

/**
 * Optimized implementation of spatial predicate "intersects"
 * for cases where the first {@link Geometry} is a rectangle.
 * <p>
 * As a further optimization,
 * this class can be used directly to test many geometries against a single
 * rectangle.
 *
 * @version 1.7
 */
public class RectangleIntersects {

  /**
   * Crossover size at which brute-force intersection scanning
   * is slower than indexed intersection detection.
   * Must be determined empirically.  Should err on the
   * safe side by making value smaller rather than larger.
   */
  public static final int MAXIMUM_SCAN_SEGMENT_COUNT = 200;

  public static boolean intersects(Polygon rectangle, Geometry b)
  {
    RectangleIntersects rp = new RectangleIntersects(rectangle);
    return rp.intersects(b);
  }

  private Polygon rectangle;
  private Envelope rectEnv;

  /**
   * Create a new intersects computer for a rectangle.
   *
   * @param rectangle a rectangular geometry
   */
  public RectangleIntersects(Polygon rectangle) {
    this.rectangle = rectangle;
    rectEnv = rectangle.getEnvelopeInternal();
  }

  public boolean intersects(Geometry geom)
  {
    if (! rectEnv.intersects(geom.getEnvelopeInternal()))
        return false;
    // test envelope relationships
    EnvelopeIntersectsVisitor visitor = new EnvelopeIntersectsVisitor(rectEnv);
    visitor.applyTo(geom);
    if (visitor.intersects())
      return true;

    // test if any rectangle corner is contained in the target
    ContainsPointVisitor ecpVisitor = new ContainsPointVisitor(rectangle);
    ecpVisitor.applyTo(geom);
    if (ecpVisitor.containsPoint())
      return true;

    // test if any lines intersect
    LineIntersectsVisitor liVisitor = new LineIntersectsVisitor(rectangle);
    liVisitor.applyTo(geom);
    if (liVisitor.intersects())
      return true;

    return false;
  }
}

/**
 * Tests whether it can be concluded
 * that a rectangle intersects a geometry,
 * based on the locations of the envelope(s) of the geometry.
 *
 * @author Martin Davis
 * @version 1.7
 */
class EnvelopeIntersectsVisitor
    extends ShortCircuitedGeometryVisitor
{
  private Envelope rectEnv;
  private boolean intersects = false;

  public EnvelopeIntersectsVisitor(Envelope rectEnv)
  {
    this.rectEnv = rectEnv;
  }

  /**
   * Reports whether it can be concluded that an intersection occurs,
   * or whether further testing is required.
   *
   * @return <code>true</code> if an intersection must occur
   * <code>false</code> if no conclusion can be made
   */
  public boolean intersects() { return intersects; }

  protected void visit(Geometry element)
  {
    Envelope elementEnv = element.getEnvelopeInternal();
    // disjoint
    if (! rectEnv.intersects(elementEnv)) {
      return;
    }
    // fully contained - must intersect
    if (rectEnv.contains(elementEnv)) {
      intersects = true;
      return;
    }
    /**
     * Since the envelopes intersect and the test element is connected,
     * if the test envelope is completely bisected by an edge of the rectangle
     * the element and the rectangle must touch
     * (This is basically an application of the Jordan Curve Theorem).
     * The alternative situation is that
     * the test envelope is "on a corner" of the rectangle envelope,
     * i.e. is not completely bisected.
     * In this case it is not possible to make a conclusion
     * about the presence of an intersection.
     */
    if (elementEnv.getMinX() >= rectEnv.getMinX()
        && elementEnv.getMaxX() <= rectEnv.getMaxX()) {
      intersects = true;
      return;
    }
    if (elementEnv.getMinY() >= rectEnv.getMinY()
        && elementEnv.getMaxY() <= rectEnv.getMaxY()) {
      intersects = true;
      return;
    }
  }

  protected boolean isDone() {
    return intersects == true;
  }
}

/**
 * Tests whether it can be concluded
 * that a geometry contains a corner point of a rectangle.
 *
 * @author Martin Davis
 * @version 1.7
 */
class ContainsPointVisitor
    extends ShortCircuitedGeometryVisitor
{
  private CoordinateSequence rectSeq;
  private Envelope rectEnv;
  private boolean containsPoint = false;

  public ContainsPointVisitor(Polygon rectangle)
  {
    this.rectSeq = rectangle.getExteriorRing().getCoordinateSequence();
    rectEnv = rectangle.getEnvelopeInternal();
  }

  /**
   * Reports whether it can be concluded that a corner
   * point of the rectangle is contained in the geometry,
   * or whether further testing is required.
   *
   * @return <code>true</code> if a corner point is contained
   * <code>false</code> if no conclusion can be made
   */
  public boolean containsPoint() { return containsPoint; }

  protected void visit(Geometry geom)
  {
    if (! (geom instanceof Polygon))
      return;
    Envelope elementEnv = geom.getEnvelopeInternal();
    if (! rectEnv.intersects(elementEnv))
      return;
    // test each corner of rectangle for inclusion
    Coordinate rectPt = new Coordinate();
    for (int i = 0; i < 4; i++) {
      rectSeq.getCoordinate(i, rectPt);
      if (! elementEnv.contains(rectPt))
        continue;
      // check rect point in poly (rect is known not to touch polygon at this point)
      if (SimplePointInAreaLocator.containsPointInPolygon(rectPt, (Polygon) geom)) {
        containsPoint = true;
        return;
      }
    }
  }

  protected boolean isDone() {
    return containsPoint == true;
  }
}

/**
 * Tests whether any line segment of a geometry intersects a given rectangle.
 * Optimizes the algorithm used based on the number of line segments in the
 * test geometry.
 *
 * @author Martin Davis
 * @version 1.7
 */
class LineIntersectsVisitor
    extends ShortCircuitedGeometryVisitor
{
  private Polygon rectangle;
  private CoordinateSequence rectSeq;
  private Envelope rectEnv;
  private boolean intersects = false;

  public LineIntersectsVisitor(Polygon rectangle)
  {
    this.rectangle = rectangle;
    this.rectSeq = rectangle.getExteriorRing().getCoordinateSequence();
    rectEnv = rectangle.getEnvelopeInternal();
  }


  /**
   * Reports whether any segment intersection exists.
   *
   * @return <code>true</code> if a segment intersection exists
   * <code>false</code> if no segment intersection exists
   */
  public boolean intersects() { return intersects; }

  protected void visit(Geometry geom)
  {
    Envelope elementEnv = geom.getEnvelopeInternal();
    if (! rectEnv.intersects(elementEnv))
      return;
    // check if general relate algorithm should be used, since it's faster for large inputs
    if (geom.getNumPoints() > RectangleIntersects.MAXIMUM_SCAN_SEGMENT_COUNT) {
      intersects = rectangle.relate(geom).isIntersects();
      return;
    }
    // if small enough, test for segment intersection directly
    computeSegmentIntersection(geom);
  }

  private void computeSegmentIntersection(Geometry geom)
  {
    // check segment intersection
    // get all lines from geom (e.g. if it's a multi-ring polygon)
    List lines = LinearComponentExtracter.getLines(geom);
    SegmentIntersectionTester si = new SegmentIntersectionTester();
    boolean hasIntersection = si.hasIntersectionWithLineStrings(rectSeq, lines);
    if (hasIntersection) {
      intersects = true;
      return;
    }
  }

  protected boolean isDone() {
    return intersects == true;
  }
}

