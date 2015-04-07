package com.vividsolutions.jts.operation.overlay.validate;

import java.util.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.util.*;

/**
 * Generates points offset by a given distance 
 * from both sides of the midpoint of
 * all segments in a {@link Geometry}.
 * Can be used to generate probe points for
 * determining whether a polygonal overlay result
 * is incorrect.
 *
 * @author Martin Davis
 * @version 1.7
 */
public class OffsetPointGenerator
{
  private double offsetDistance;
  private Geometry g;
  private List offsetPts;

  public OffsetPointGenerator(Geometry g, double offsetDistance)
  {
    this.g = g;
    this.offsetDistance = offsetDistance;
  }

  /**
   * Gets the computed offset points.
   *
   * @return List<Coordinate>
   */
  public List getPoints()
  {
    offsetPts = new ArrayList();
    List lines = LinearComponentExtracter.getLines(g);
    for (Iterator i = lines.iterator(); i.hasNext(); ) {
      LineString line = (LineString) i.next();
      extractPoints(line);
    }
    //System.out.println(toMultiPoint(offsetPts));
    return offsetPts;
  }

  private void extractPoints(LineString line)
  {
    Coordinate[] pts = line.getCoordinates();
    for (int i = 0; i < pts.length - 1; i++) {
    	computeOffsetPoints(pts[i], pts[i + 1]);
    }
  }

  /**
   * Generates the two points which are offset from the 
   * midpoint of the segment <tt>(p0, p1)</tt> by the
   * <tt>offsetDistance</tt>.
   * 
   * @param p0 the first point of the segment to offset from
   * @param p1 the second point of the segment to offset from
   */
  private void computeOffsetPoints(Coordinate p0, Coordinate p1)
  {
    double dx = p1.x - p0.x;
    double dy = p1.y - p0.y;
    double len = Math.sqrt(dx * dx + dy * dy);
    // u is the vector that is the length of the offset, in the direction of the segment
    double ux = offsetDistance * dx / len;
    double uy = offsetDistance * dy / len;

    double midX = (p1.x + p0.x) / 2;
    double midY = (p1.y + p0.y) / 2;

    Coordinate offsetLeft = new Coordinate(midX - uy, midY + ux);
    Coordinate offsetRight = new Coordinate(midX + uy, midY - ux);

    offsetPts.add(offsetLeft);
    offsetPts.add(offsetRight);
  }

}