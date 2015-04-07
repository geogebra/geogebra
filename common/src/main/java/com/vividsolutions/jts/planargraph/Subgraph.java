package com.vividsolutions.jts.planargraph;

import java.util.*;

/**
 * A subgraph of a {@link PlanarGraph}.
 * A subgraph may contain any subset of {@link Edge}s
 * from the parent graph.
 * It will also automatically contain all {@link DirectedEdge}s
 * and {@link Node}s associated with those edges.
 * No new objects are created when edges are added -
 * all associated components must already exist in the parent graph.
 */
public class Subgraph
{
  protected PlanarGraph parentGraph;
  protected Set edges = new HashSet();
  protected List dirEdges = new ArrayList();
  protected NodeMap nodeMap = new NodeMap();

  /**
   * Creates a new subgraph of the given {@link PlanarGraph}
   *
   * @param parentGraph the parent graph
   */
  public Subgraph(PlanarGraph parentGraph) {
    this.parentGraph = parentGraph;
  }

  /**
   * Gets the {@link PlanarGraph} which this subgraph
   * is part of.
   *
   * @return the parent PlanarGraph
   */
  public PlanarGraph getParent()
  {
    return parentGraph;
  }
  /**
   * Adds an {@link Edge} to the subgraph.
   * The associated {@link DirectedEdge}s and {@link Node}s
   * are also added.
   *
   * @param e the edge to add
   */
  public void add(Edge e)
  {
    if (edges.contains(e)) return;

    edges.add(e);
    dirEdges.add(e.getDirEdge(0));
    dirEdges.add(e.getDirEdge(1));
    nodeMap.add(e.getDirEdge(0).getFromNode());
    nodeMap.add(e.getDirEdge(1).getFromNode());
  }

  /**
   * Returns an {@link Iterator} over the {@link DirectedEdge}s in this graph,
   * in the order in which they were added.
   *
   * @return an iterator over the directed edges
   *
   * @see #add(Edge)
   */
  public Iterator dirEdgeIterator()  {    return dirEdges.iterator();  }

  /**
   * Returns an {@link Iterator} over the {@link Edge}s in this graph,
   * in the order in which they were added.
   *
   * @return an iterator over the edges
   *
   * @see #add(Edge)
   */
  public Iterator edgeIterator()  {    return edges.iterator();  }

  /**
   * Returns an {@link Iterator} over the {@link Node}s in this graph.
   * @return an iterator over the nodes
   */
  public Iterator nodeIterator()  {    return nodeMap.iterator();  }

  /**
   * Tests whether an {@link Edge} is contained in this subgraph
   * @param e the edge to test
   * @return <code>true</code> if the edge is contained in this subgraph
   */
  public boolean contains(Edge e) { return edges.contains(e); }

}