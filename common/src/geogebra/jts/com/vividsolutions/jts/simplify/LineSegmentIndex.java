package com.vividsolutions.jts.simplify;

import java.util.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.index.*;
import com.vividsolutions.jts.index.quadtree.Quadtree;

/**
 * An spatial index on a set of {@link LineSegment}s.
 * Supports adding and removing items.
 *
 * @author Martin Davis
 */
public class LineSegmentIndex
{
  private Quadtree index = new Quadtree();

  public LineSegmentIndex()
  {
  }

  public void add(TaggedLineString line) {
    TaggedLineSegment[] segs = line.getSegments();
    for (int i = 0; i < segs.length; i++) {
      TaggedLineSegment seg = segs[i];
      add(seg);
    }
  }

  public void add(LineSegment seg)
  {
    index.insert(new Envelope(seg.p0, seg.p1), seg);
  }

  public void remove(LineSegment seg)
  {
    index.remove(new Envelope(seg.p0, seg.p1), seg);
  }

  public List query(LineSegment querySeg)
  {
    Envelope env = new Envelope(querySeg.p0, querySeg.p1);

    LineSegmentVisitor visitor = new LineSegmentVisitor(querySeg);
    index.query(env, visitor);
    List itemsFound = visitor.getItems();

//    List listQueryItems = index.query(env);
//    System.out.println("visitor size = " + itemsFound.size()
//                       + "  query size = " + listQueryItems.size());
//    List itemsFound = index.query(env);

    return itemsFound;
  }
}

/**
 * ItemVisitor subclass to reduce volume of query results.
 */
class LineSegmentVisitor
    implements ItemVisitor
{
// MD - only seems to make about a 10% difference in overall time.

  private LineSegment querySeg;
  private ArrayList items = new ArrayList();

  public LineSegmentVisitor(LineSegment querySeg) {
    this.querySeg = querySeg;
  }

  public void visitItem(Object item)
  {
    LineSegment seg = (LineSegment) item;
    if (Envelope.intersects(seg.p0, seg.p1, querySeg.p0, querySeg.p1))
      items.add(item);
  }

  public ArrayList getItems() { return items; }
}
