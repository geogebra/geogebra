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
package com.vividsolutions.jts.operation.buffer;

import java.util.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.algorithm.*;
import com.vividsolutions.jts.geomgraph.*;

/**
 * Computes the raw offset curve for a
 * single {@link Geometry} component (ring, line or point).
 * A raw offset curve line is not noded -
 * it may contain self-intersections (and usually will).
 * The final buffer polygon is computed by forming a topological graph
 * of all the noded raw curves and tracing outside contours.
 * The points in the raw curve are rounded to the required precision model.
 *
 * @version 1.7
 */
public class OffsetCurveBuilder 
{  
  /**
   * The angle quantum with which to approximate a fillet curve
   * (based on the input # of quadrant segments)
   */
  private double filletAngleQuantum;
  
  /**
   * the max error of approximation (distance) between a quad segment and the true fillet curve
   */
  private double maxCurveSegmentError = 0.0;
  
  /**
   * Factor which controls how close curve vertices can be to be snapped
   */
  private static final double CURVE_VERTEX_SNAP_DISTANCE_FACTOR = 1.0E-6;
  
  /**
   * Factor which controls how close curve vertices on inside turns can be to be snapped 
   */
  private static final double INSIDE_TURN_VERTEX_SNAP_DISTANCE_FACTOR = 1.0E-3;
    
  /**
   * Factor which determines how short closing segs can be for round buffers
   */
  private static final int MAX_CLOSING_SEG_FRACTION = 80;
  
  private double distance = 0.0;
  private PrecisionModel precisionModel;
  
  private BufferParameters bufParams;
  
  /**
   * The Closing Segment Factor controls how long
   * "closing segments" are.  Closing segments are added
   * at the middle of inside corners to ensure a smoother
   * boundary for the buffer offset curve. 
   * In some cases (particularly for round joins with default-or-better
   * quantization) the closing segments can be made quite short.
   * This substantially improves performance (due to fewer intersections being created).
   * 
   * A closingSegFactor of 0 results in lines to the corner vertex
   * A closingSegFactor of 1 results in lines halfway to the corner vertex
   * A closingSegFactor of 80 results in lines 1/81 of the way to the corner vertex
   * (this option is reasonable for the very common default situation of round joins
   * and quadrantSegs >= 8)
   * 
   */
  private int closingSegFactor = 1;
  private OffsetCurveVertexList vertexList;
  private LineIntersector li;
  
  public OffsetCurveBuilder(
                PrecisionModel precisionModel,
                BufferParameters bufParams
                )
  {
    this.precisionModel = precisionModel;
    this.bufParams = bufParams;
    
    // compute intersections in full precision, to provide accuracy
    // the points are rounded as they are inserted into the curve line
    li = new RobustLineIntersector();
    filletAngleQuantum = Math.PI / 2.0 / bufParams.getQuadrantSegments();
    
    /**
     * Non-round joins cause issues with short closing segments,
     * so don't use them.  In any case, non-round joins 
     * only really make sense for relatively small buffer distances.
     */
    if (bufParams.getQuadrantSegments() >= 8 
        && bufParams.getJoinStyle() == BufferParameters.JOIN_ROUND)
      closingSegFactor = MAX_CLOSING_SEG_FRACTION;
  }

  /**
   * This method handles single points as well as lines.
   * Lines are assumed to <b>not</b> be closed (the function will not
   * fail for closed lines, but will generate superfluous line caps).
   *
   * @return a List of Coordinate[]
   */
  public List getLineCurve(Coordinate[] inputPts, double distance)
  {
    List lineList = new ArrayList();
    // a zero or negative width buffer of a line/point is empty
    if (distance <= 0.0) return lineList;

    init(distance);
    if (inputPts.length <= 1) {
      switch (bufParams.getEndCapStyle()) {
        case BufferParameters.CAP_ROUND:
          addCircle(inputPts[0], distance);
          break;
        case BufferParameters.CAP_SQUARE:
          addSquare(inputPts[0], distance);
          break;
          // default is for buffer to be empty (e.g. for a butt line cap);
      }
    }
    else
      computeLineBufferCurve(inputPts);
    
//System.out.println(vertexList);
    
    Coordinate[] lineCoord = vertexList.getCoordinates();
    lineList.add(lineCoord);
    return lineList;
  }

  /**
   * This method handles the degenerate cases of single points and lines,
   * as well as rings.
   *
   * @return a List of Coordinate[]
   */
  public List getRingCurve(Coordinate[] inputPts, int side, double distance)
  {
    List lineList = new ArrayList();
    init(distance);
    if (inputPts.length <= 2)
      return getLineCurve(inputPts, distance);

    // optimize creating ring for for zero distance
    if (distance == 0.0) {
      lineList.add(copyCoordinates(inputPts));
      return lineList;
    }
    computeRingBufferCurve(inputPts, side);
    lineList.add(vertexList.getCoordinates());
    return lineList;
  }

  private static Coordinate[] copyCoordinates(Coordinate[] pts)
  {
    Coordinate[] copy = new Coordinate[pts.length];
    for (int i = 0; i < copy.length; i++) {
      copy[i] = new Coordinate(pts[i]);
    }
    return copy;
  }
  
  private void init(double distance)
  {
    this.distance = distance;
    maxCurveSegmentError = distance * (1 - Math.cos(filletAngleQuantum / 2.0));
    vertexList = new OffsetCurveVertexList();
    vertexList.setPrecisionModel(precisionModel);
    /**
     * Choose the min vertex separation as a small fraction of the offset distance.
     */
    vertexList.setMinimumVertexDistance(distance * CURVE_VERTEX_SNAP_DISTANCE_FACTOR);
  }
  
  /**
   * Use a value which results in a potential distance error which is
   * significantly less than the error due to 
   * the quadrant segment discretization.
   * For QS = 8 a value of 400 is reasonable.
   */
  private static final double SIMPLIFY_FACTOR = 400.0;
  
  /**
   * Computes the distance tolerance to use during input
   * line simplification.
   * 
   * @param distance the buffer distance
   * @return the simplification tolerance
   */
  private double simplifyTolerance(double bufDistance)
  {
    return bufDistance / SIMPLIFY_FACTOR;
  }
  
  private void computeLineBufferCurve(Coordinate[] inputPts)
  {
    double distTol = simplifyTolerance(distance);
    
    //--------- compute points for left side of line
    // Simplify the appropriate side of the line before generating
    Coordinate[] simp1 = BufferInputLineSimplifier.simplify(inputPts, distTol);
    // MD - used for testing only (to eliminate simplification)
//    Coordinate[] simp1 = inputPts;
    
    int n1 = simp1.length - 1;


    initSideSegments(simp1[0], simp1[1], Position.LEFT);
    for (int i = 2; i <= n1; i++) {
      addNextSegment(simp1[i], true);
    }
    addLastSegment();
    // add line cap for end of line
    addLineEndCap(simp1[n1 - 1], simp1[n1]);
    
    //---------- compute points for right side of line
    // Simplify the appropriate side of the line before generating
    Coordinate[] simp2 = BufferInputLineSimplifier.simplify(inputPts, -distTol);
    // MD - used for testing only (to eliminate simplification)
//    Coordinate[] simp2 = inputPts;
    int n2 = simp2.length - 1;
   
    // since we are traversing line in opposite order, offset position is still LEFT
    initSideSegments(simp2[n2], simp2[n2 - 1], Position.LEFT);
    for (int i = n2 - 2; i >= 0; i--) {
      addNextSegment(simp2[i], true);
    }
    addLastSegment();
    // add line cap for start of line
    addLineEndCap(simp2[1], simp2[0]);

    vertexList.closeRing();
  }

  private void OLDcomputeLineBufferCurve(Coordinate[] inputPts)
  {
    int n = inputPts.length - 1;
    
    // compute points for left side of line
    initSideSegments(inputPts[0], inputPts[1], Position.LEFT);
    for (int i = 2; i <= n; i++) {
      addNextSegment(inputPts[i], true);
    }
    addLastSegment();
    // add line cap for end of line
    addLineEndCap(inputPts[n - 1], inputPts[n]);

    // compute points for right side of line
    initSideSegments(inputPts[n], inputPts[n - 1], Position.LEFT);
    for (int i = n - 2; i >= 0; i--) {
      addNextSegment(inputPts[i], true);
    }
    addLastSegment();
    // add line cap for start of line
    addLineEndCap(inputPts[1], inputPts[0]);

    vertexList.closeRing();
  }

  private void computeRingBufferCurve(Coordinate[] inputPts, int side)
  {
    // simplify input line to improve performance
    double distTol = simplifyTolerance(distance);
    // ensure that correct side is simplified
    if (side == Position.RIGHT)
      distTol = -distTol;
    Coordinate[] simp = BufferInputLineSimplifier.simplify(inputPts, distTol);
//    Coordinate[] simp = inputPts;
    
    int n = simp.length - 1;
    initSideSegments(simp[n - 1], simp[0], side);
    for (int i = 1; i <= n; i++) {
      boolean addStartPoint = i != 1;
      addNextSegment(simp[i], addStartPoint);
    }
    vertexList.closeRing();
  }

  private Coordinate s0, s1, s2;
  private LineSegment seg0 = new LineSegment();
  private LineSegment seg1 = new LineSegment();
  private LineSegment offset0 = new LineSegment();
  private LineSegment offset1 = new LineSegment();
  private int side = 0;

  private void initSideSegments(Coordinate s1, Coordinate s2, int side)
  {
    this.s1 = s1;
    this.s2 = s2;
    this.side = side;
    seg1.setCoordinates(s1, s2);
    computeOffsetSegment(seg1, side, distance, offset1);
  }

  private static double MAX_CLOSING_SEG_LEN = 3.0;

  private void addNextSegment(Coordinate p, boolean addStartPoint)
  {
    // s0-s1-s2 are the coordinates of the previous segment and the current one
    s0 = s1;
    s1 = s2;
    s2 = p;
    seg0.setCoordinates(s0, s1);
    computeOffsetSegment(seg0, side, distance, offset0);
    seg1.setCoordinates(s1, s2);
    computeOffsetSegment(seg1, side, distance, offset1);

    // do nothing if points are equal
    if (s1.equals(s2)) return;

    int orientation = CGAlgorithms.computeOrientation(s0, s1, s2);
    boolean outsideTurn =
          (orientation == CGAlgorithms.CLOCKWISE        && side == Position.LEFT)
      ||  (orientation == CGAlgorithms.COUNTERCLOCKWISE && side == Position.RIGHT);

    if (orientation == 0) { // lines are collinear
    	addCollinear(addStartPoint);
		}
    else if (outsideTurn) 
		{
			addOutsideTurn(orientation, addStartPoint);
		}
    else { // inside turn
			addInsideTurn(orientation, addStartPoint);
    }
  }
  
  private void addCollinear(boolean addStartPoint)
  {
		li.computeIntersection(s0, s1, s1, s2);
		int numInt = li.getIntersectionNum();
		/**
		 * if numInt is < 2, the lines are parallel and in the same direction. In
		 * this case the point can be ignored, since the offset lines will also be
		 * parallel.
		 */
		if (numInt >= 2) {
			/**
			 * segments are collinear but reversing. 
			 * Add an "end-cap" fillet
			 * all the way around to other direction This case should ONLY happen
			 * for LineStrings, so the orientation is always CW. (Polygons can never
			 * have two consecutive segments which are parallel but reversed,
			 * because that would be a self intersection.
			 * 
			 */
			if (bufParams.getJoinStyle() == BufferParameters.JOIN_BEVEL 
					|| bufParams.getJoinStyle() == BufferParameters.JOIN_MITRE) {
				if (addStartPoint) vertexList.addPt(offset0.p1);
				vertexList.addPt(offset1.p0);
			}
			else {
				addFillet(s1, offset0.p1, offset1.p0, CGAlgorithms.CLOCKWISE, distance);
			}
		}
  }
  
  /**
   * Adds the offset points for an outside (convex) turn
   * 
   * @param orientation
   * @param addStartPoint
   */
  private void addOutsideTurn(int orientation, boolean addStartPoint)
  {
  	/**
  	 * If offset endpoints are very close together, just snap them together.
  	 * This avoids problems with computing mitre corners in degenerate cases.
  	 */
    if (offset0.p1.distance(offset1.p0) < distance * CURVE_VERTEX_SNAP_DISTANCE_FACTOR) {
    	vertexList.addPt(offset0.p1);
    	return;
    }
  	
  	
		if (bufParams.getJoinStyle() == BufferParameters.JOIN_MITRE) {
			addMitreJoin(s1, offset0, offset1, distance);
		}
		else if (bufParams.getJoinStyle() == BufferParameters.JOIN_BEVEL){
			addBevelJoin(offset0, offset1);
		}
		else {
		// add a circular fillet connecting the endpoints of the offset segments
		 if (addStartPoint) vertexList.addPt(offset0.p1);
      // TESTING - comment out to produce beveled joins
      addFillet(s1, offset0.p1, offset1.p0, orientation, distance);
      vertexList.addPt(offset1.p0);
		}
  }
  
  /**
   * Adds the offset points for an inside (concave) turn
   * 
   * @param orientation
   * @param addStartPoint
   */
  private void addInsideTurn(int orientation, boolean addStartPoint) {
    /**
     * add intersection point of offset segments (if any)
     */
    li.computeIntersection(offset0.p0, offset0.p1, offset1.p0, offset1.p1);
    if (li.hasIntersection()) {
      vertexList.addPt(li.getIntersection(0));
    }
    else {
      /**
       * If no intersection is detected, it means the angle is so small and/or the offset so
       * large that the offsets segments don't intersect. In this case we must
       * add a "closing segment" to make sure the buffer line is continuous
       * and tracks the buffer correctly around the corner. The curve connects
       * the endpoints of the segment offsets to points
       * which lie toward the centre point of the corner.
       * The joining curve will not appear in the final buffer outline, since it
       * is completely internal to the buffer polygon.
       * 
       * The intersection test above is vulnerable to robustness errors; i.e. it
       * may be that the offsets should intersect very close to their endpoints,
       * but aren't reported as such due to rounding. To handle this situation
       * appropriately, we use the following test: If the offset points are very
       * close, don't add closing segments but simply use one of the offset
       * points
       */
      if (offset0.p1.distance(offset1.p0) < distance
          * INSIDE_TURN_VERTEX_SNAP_DISTANCE_FACTOR) {
        vertexList.addPt(offset0.p1);
      } else {
        // add endpoint of this segment offset
        vertexList.addPt(offset0.p1);
        
        // add closing segments of required length
        if (closingSegFactor > 0) {
          Coordinate mid0 = new Coordinate((closingSegFactor * offset0.p1.x + s1.x)/(closingSegFactor + 1), 
              (closingSegFactor*offset0.p1.y + s1.y)/(closingSegFactor + 1));
          vertexList.addPt(mid0);
          Coordinate mid1 = new Coordinate((closingSegFactor*offset1.p0.x + s1.x)/(closingSegFactor + 1), 
             (closingSegFactor*offset1.p0.y + s1.y)/(closingSegFactor + 1));
          vertexList.addPt(mid1);
        }
        else {
          /**
           * This branch is not expected to be used except for testing purposes.
           * It is equivalent to the JTS 1.9 logic for closing segments
           * (which results in very poor performance for large buffer distances)
           */
          vertexList.addPt(s1);
        }
        
        //*/  
        // add start point of next segment offset
        vertexList.addPt(offset1.p0);
      }
    }
  }
  
  /**
   * Add last offset point
   */
  private void addLastSegment()
  {
  	vertexList.addPt(offset1.p1);
  }

  /**
   * Compute an offset segment for an input segment on a given side and at a given distance.
   * The offset points are computed in full double precision, for accuracy.
   *
   * @param seg the segment to offset
   * @param side the side of the segment ({@link Position}) the offset lies on
   * @param distance the offset distance
   * @param offset the points computed for the offset segment
   */
  private void computeOffsetSegment(LineSegment seg, int side, double distance, LineSegment offset)
  {
    int sideSign = side == Position.LEFT ? 1 : -1;
    double dx = seg.p1.x - seg.p0.x;
    double dy = seg.p1.y - seg.p0.y;
    double len = Math.sqrt(dx * dx + dy * dy);
    // u is the vector that is the length of the offset, in the direction of the segment
    double ux = sideSign * distance * dx / len;
    double uy = sideSign * distance * dy / len;
    offset.p0.x = seg.p0.x - uy;
    offset.p0.y = seg.p0.y + ux;
    offset.p1.x = seg.p1.x - uy;
    offset.p1.y = seg.p1.y + ux;
  }

  /**
   * Add an end cap around point p1, terminating a line segment coming from p0
   */
  private void addLineEndCap(Coordinate p0, Coordinate p1)
  {
    LineSegment seg = new LineSegment(p0, p1);

    LineSegment offsetL = new LineSegment();
    computeOffsetSegment(seg, Position.LEFT, distance, offsetL);
    LineSegment offsetR = new LineSegment();
    computeOffsetSegment(seg, Position.RIGHT, distance, offsetR);

    double dx = p1.x - p0.x;
    double dy = p1.y - p0.y;
    double angle = Math.atan2(dy, dx);

    switch (bufParams.getEndCapStyle()) {
      case BufferParameters.CAP_ROUND:
        // add offset seg points with a fillet between them
      	vertexList.addPt(offsetL.p1);
        addFillet(p1, angle + Math.PI / 2, angle - Math.PI / 2, CGAlgorithms.CLOCKWISE, distance);
        vertexList.addPt(offsetR.p1);
        break;
      case BufferParameters.CAP_FLAT:
        // only offset segment points are added
      	vertexList.addPt(offsetL.p1);
      	vertexList.addPt(offsetR.p1);
        break;
      case BufferParameters.CAP_SQUARE:
        // add a square defined by extensions of the offset segment endpoints
        Coordinate squareCapSideOffset = new Coordinate();
        squareCapSideOffset.x = Math.abs(distance) * Math.cos(angle);
        squareCapSideOffset.y = Math.abs(distance) * Math.sin(angle);

        Coordinate squareCapLOffset = new Coordinate(
            offsetL.p1.x + squareCapSideOffset.x,
            offsetL.p1.y + squareCapSideOffset.y);
        Coordinate squareCapROffset = new Coordinate(
            offsetR.p1.x + squareCapSideOffset.x,
            offsetR.p1.y + squareCapSideOffset.y);
        vertexList.addPt(squareCapLOffset);
        vertexList.addPt(squareCapROffset);
        break;

    }
  }
  /**
   * Adds a mitre join connecting the two reflex offset segments.
   * The mitre will be beveled if it exceeds the mitre ratio limit.
   * 
   * @param offset0 the first offset segment
   * @param offset1 the second offset segment
   * @param distance the offset distance
   */
  private void addMitreJoin(Coordinate p, 
  		LineSegment offset0, 
  		LineSegment offset1,
  		double distance)
  {
  	boolean isMitreWithinLimit = true;
  	Coordinate intPt = null;
  
  	/*
  	 * This computation is unstable if the offset segments are nearly collinear, 
  	 * but this case should be eliminated earlier by the check if 
  	 * the offset segment endpoints are almost coincident
  	 */
  	try {
  	 intPt = HCoordinate.intersection(offset0.p0, 
  			offset0.p1, offset1.p0, offset1.p1);
  	 
  	 double mitreRatio = distance <= 0.0 ? 1.0
  			 : intPt.distance(p) / Math.abs(distance);
  	 
  	 if (mitreRatio > bufParams.getMitreLimit())
  		 isMitreWithinLimit = false;
  	}
  	catch (NotRepresentableException ex) {
  		intPt = new Coordinate(0,0);
  		isMitreWithinLimit = false;
  	}
  	
  	if (isMitreWithinLimit) {
  		vertexList.addPt(intPt);
  	}
  	else {
  		addLimitedMitreJoin(offset0, offset1, distance, bufParams.getMitreLimit());
//  		addBevelJoin(offset0, offset1);
  	}
  }
  
  
  /**
   * Adds a limited mitre join connecting the two reflex offset segments.
   * A limited mitre is a mitre which is beveled at the distance
   * determined by the mitre ratio limit.
   * 
   * @param offset0 the first offset segment
   * @param offset1 the second offset segment
   * @param distance the offset distance
   * @param mitreLimit the mitre limit ratio
   */
  private void addLimitedMitreJoin( 
  		LineSegment offset0, 
  		LineSegment offset1,
  		double distance,
  		double mitreLimit)
  {
  	Coordinate basePt = seg0.p1;
  	
  	double ang0 = Angle.angle(basePt, seg0.p0);
  	double ang1 = Angle.angle(basePt, seg1.p1);
  	
  	// oriented angle between segments
  	double angDiff = Angle.angleBetweenOriented(seg0.p0, basePt, seg1.p1);
  	// half of the interior angle
  	double angDiffHalf = angDiff / 2;
  
  	// angle for bisector of the interior angle between the segments
  	double midAng = Angle.normalize(ang0 + angDiffHalf);
  	// rotating this by PI gives the bisector of the reflex angle
  	double mitreMidAng = Angle.normalize(midAng + Math.PI);
  	
  	// the miterLimit determines the distance to the mitre bevel
  	double mitreDist = mitreLimit * distance;
  	// the bevel delta is the difference between the buffer distance
  	// and half of the length of the bevel segment
  	double bevelDelta = mitreDist * Math.abs(Math.sin(angDiffHalf));
  	double bevelHalfLen = distance - bevelDelta;

  	// compute the midpoint of the bevel segment
  	double bevelMidX = basePt.x + mitreDist * Math.cos(mitreMidAng);
  	double bevelMidY = basePt.y + mitreDist * Math.sin(mitreMidAng);
  	Coordinate bevelMidPt = new Coordinate(bevelMidX, bevelMidY);
  	
  	// compute the mitre midline segment from the corner point to the bevel segment midpoint
  	LineSegment mitreMidLine = new LineSegment(basePt, bevelMidPt);
  	
  	// finally the bevel segment endpoints are computed as offsets from 
    // the mitre midline
  	Coordinate bevelEndLeft = mitreMidLine.pointAlongOffset(1.0, bevelHalfLen);
  	Coordinate bevelEndRight = mitreMidLine.pointAlongOffset(1.0, -bevelHalfLen);
  	
  	if (side == Position.LEFT) {
  		vertexList.addPt(bevelEndLeft);
  		vertexList.addPt(bevelEndRight);
  	}
  	else {
  		vertexList.addPt(bevelEndRight);
  		vertexList.addPt(bevelEndLeft);  		
  	}
  }
  
  /**
   * Adds a bevel join connecting the two offset segments
   * around a reflex corner.
   * 
   * @param offset0 the first offset segment
   * @param offset1 the second offset segment
   */
  private void addBevelJoin( 
  		LineSegment offset0, 
  		LineSegment offset1)
  {
		 vertexList.addPt(offset0.p1);
     vertexList.addPt(offset1.p0);				
  }
  
  
  /**
   * Add points for a circular fillet around a reflex corner.
   * Adds the start and end points
   * 
   * @param p base point of curve
   * @param p0 start point of fillet curve
   * @param p1 endpoint of fillet curve
   * @param direction the orientation of the fillet
   * @param radius the radius of the fillet
   */
  private void addFillet(Coordinate p, Coordinate p0, Coordinate p1, int direction, double radius)
  {
    double dx0 = p0.x - p.x;
    double dy0 = p0.y - p.y;
    double startAngle = Math.atan2(dy0, dx0);
    double dx1 = p1.x - p.x;
    double dy1 = p1.y - p.y;
    double endAngle = Math.atan2(dy1, dx1);

    if (direction == CGAlgorithms.CLOCKWISE) {
      if (startAngle <= endAngle) startAngle += 2.0 * Math.PI;
    }
    else {    // direction == COUNTERCLOCKWISE
      if (startAngle >= endAngle) startAngle -= 2.0 * Math.PI;
    }
    vertexList.addPt(p0);
    addFillet(p, startAngle, endAngle, direction, radius);
    vertexList.addPt(p1);
  }

  /**
   * Adds points for a circular fillet arc
   * between two specified angles.  
   * The start and end point for the fillet are not added -
   * the caller must add them if required.
   *
   * @param direction is -1 for a CW angle, 1 for a CCW angle
   * @param radius the radius of the fillet
   */
  private void addFillet(Coordinate p, double startAngle, double endAngle, int direction, double radius)
  {
    int directionFactor = direction == CGAlgorithms.CLOCKWISE ? -1 : 1;

    double totalAngle = Math.abs(startAngle - endAngle);
    int nSegs = (int) (totalAngle / filletAngleQuantum + 0.5);

    if (nSegs < 1) return;    // no segments because angle is less than increment - nothing to do!

    double initAngle, currAngleInc;

    // choose angle increment so that each segment has equal length
    initAngle = 0.0;
    currAngleInc = totalAngle / nSegs;

    double currAngle = initAngle;
    Coordinate pt = new Coordinate();
    while (currAngle < totalAngle) {
      double angle = startAngle + directionFactor * currAngle;
      pt.x = p.x + radius * Math.cos(angle);
      pt.y = p.y + radius * Math.sin(angle);
      vertexList.addPt(pt);
      currAngle += currAngleInc;
    }
  }


  /**
   * Adds a CW circle around a point
   */
  private void addCircle(Coordinate p, double distance)
  {
    // add start point
    Coordinate pt = new Coordinate(p.x + distance, p.y);
    vertexList.addPt(pt);
    addFillet(p, 0.0, 2.0 * Math.PI, -1, distance);
  }

  /**
   * Adds a CW square around a point
   */
  private void addSquare(Coordinate p, double distance)
  {
    // add start point
  	vertexList.addPt(new Coordinate(p.x + distance, p.y + distance));
  	vertexList.addPt(new Coordinate(p.x + distance, p.y - distance));
  	vertexList.addPt(new Coordinate(p.x - distance, p.y - distance));
  	vertexList.addPt(new Coordinate(p.x - distance, p.y + distance));
  	vertexList.addPt(new Coordinate(p.x + distance, p.y + distance));
  }
}
