package com.vividsolutions.jts.linearref;

import com.vividsolutions.jts.geom.*;

/**
 * An iterator over the components and coordinates of a linear geometry
 * ({@link LineString}s and {@link MultiLineString}s.
 *
 * The standard usage pattern for a {@link LinearIterator} is:
 *
 * <pre>
 * for (LinearIterator it = new LinearIterator(...); it.hasNext(); it.next()) {
 *   ...
 *   int ci = it.getComponentIndex();   // for example
 *   int vi = it.getVertexIndex();      // for example
 *   ...
 * }
 * </pre>
 *
 * @version 1.7
 */
public class LinearIterator
{
  private static int segmentEndVertexIndex(LinearLocation loc)
  {
    if (loc.getSegmentFraction() > 0.0)
      return loc.getSegmentIndex() + 1;
    return loc.getSegmentIndex();
  }

  private Geometry linear;
  private final int numLines;

  /**
   * Invariant: currentLine <> null if the iterator is pointing at a valid coordinate
   */
  private LineString currentLine;
  private int componentIndex = 0;
  private int vertexIndex = 0;

  /**
   * Creates an iterator initialized to the start of a linear {@link Geometry}
   *
   * @param linear the linear geometry to iterate over
   */
  public LinearIterator(Geometry linear) {
    this(linear, 0, 0);
  }

  /**
   * Creates an iterator starting at
   * a {@link LinearLocation} on a linear {@link Geometry}
   *
   * @param linear the linear geometry to iterate over
   * @param start the location to start at
   */
  public LinearIterator(Geometry linear, LinearLocation start) {
    this(linear, start.getComponentIndex(), segmentEndVertexIndex(start));
  }

  /**
   * Creates an iterator starting at
   * a component and vertex in a linear {@link Geometry}
   *
   * @param linear the linear geometry to iterate over
   * @param componentIndex the component to start at
   * @param vertexIndex the vertex to start at
   */
  public LinearIterator(Geometry linear, int componentIndex, int vertexIndex) {
    this.linear = linear;
    numLines = linear.getNumGeometries();
    this.componentIndex = componentIndex;
    this.vertexIndex = vertexIndex;
    loadCurrentLine();
  }

  private void loadCurrentLine()
  {
    if (componentIndex >= numLines) {
      currentLine = null;
      return;
    }
    currentLine = (LineString) linear.getGeometryN(componentIndex);
  }

  /**
   * Tests whether there are any vertices left to iterator over.
   * @return <code>true</code> if there are more vertices to scan
   */
  public boolean hasNext()
  {
    if (componentIndex >= numLines) return false;
    if (componentIndex == numLines - 1
        && vertexIndex >= currentLine.getNumPoints())
      return false;
    return true;
  }

  /**
   * Moves the iterator ahead to the next vertex and (possibly) linear component.
   */
  public void next()
  {
    if (! hasNext()) return;

    vertexIndex++;
    if (vertexIndex >= currentLine.getNumPoints()) {
      componentIndex++;
      loadCurrentLine();
      vertexIndex = 0;
    }
  }

  /**
   * Checks whether the iterator cursor is pointing to the
   * endpoint of a linestring.
   *
   * @return <code>true</true> if the iterator is at an endpoint
   */
  public boolean isEndOfLine() {
    if (componentIndex >= numLines) return false;
    //LineString currentLine = (LineString) linear.getGeometryN(componentIndex);
    if (vertexIndex < currentLine.getNumPoints() - 1)
      return false;
    return true;
  }

  /**
   * The component index of the vertex the iterator is currently at.
   * @return the current component index
   */
  public int getComponentIndex() { return componentIndex; }

  /**
   * The vertex index of the vertex the iterator is currently at.
   * @return the current vertex index
   */
  public int getVertexIndex() { return vertexIndex; }

  /**
   * Gets the {@link LineString} component the iterator is current at.
   * @return a linestring
   */
  public LineString getLine()  {    return currentLine;  }

  /**
   * Gets the first {@link Coordinate} of the current segment.
   * (the coordinate of the current vertex).
   * @return a {@link Coordinate}
   */
  public Coordinate getSegmentStart() { return currentLine.getCoordinateN(vertexIndex); }

  /**
   * Gets the second {@link Coordinate} of the current segment.
   * (the coordinate of the next vertex).
   * If the iterator is at the end of a line, <code>null</code> is returned.
   *
   * @return a {@link Coordinate} or <code>null</code>
   */
  public Coordinate getSegmentEnd()
  {
    if (vertexIndex < getLine().getNumPoints() - 1)
      return currentLine.getCoordinateN(vertexIndex + 1);
    return null;
  }
}