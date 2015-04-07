package com.vividsolutions.jts.operation.overlay;

import java.util.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.util.*;
import com.vividsolutions.jts.algorithm.*;
import com.vividsolutions.jts.geomgraph.*;
import com.vividsolutions.jts.geomgraph.index.SegmentIntersector;
import com.vividsolutions.jts.operation.GeometryGraphOperation;

/**
 * Tests whether the polygon rings in a {@link GeometryGraph}
 * are consistent.
 * Used for checking if Topology errors are present after noding.
 *
 * @author Martin Davis
 * @version 1.7
 */
public class ConsistentPolygonRingChecker
{
  private PlanarGraph graph;

  public ConsistentPolygonRingChecker(PlanarGraph graph) {
    this.graph = graph;
  }

  public void checkAll()
  {
    check(OverlayOp.INTERSECTION);
    check(OverlayOp.DIFFERENCE);
    check(OverlayOp.UNION);
    check(OverlayOp.SYMDIFFERENCE);
  }

  /**
   * Tests whether the result geometry is consistent
   *
   * @throws TopologyException if inconsistent topology is found
   */
  public void check(int opCode)
  {
    for (Iterator nodeit = graph.getNodeIterator(); nodeit.hasNext(); ) {
      Node node = (Node) nodeit.next();
      testLinkResultDirectedEdges((DirectedEdgeStar) node.getEdges(), opCode);
    }
  }

  private List getPotentialResultAreaEdges(DirectedEdgeStar deStar, int opCode)
  {
//print(System.out);
    List resultAreaEdgeList = new ArrayList();
    for (Iterator it = deStar.iterator(); it.hasNext(); ) {
      DirectedEdge de = (DirectedEdge) it.next();
      if (isPotentialResultAreaEdge(de, opCode) || isPotentialResultAreaEdge(de.getSym(), opCode) )
        resultAreaEdgeList.add(de);
    }
    return resultAreaEdgeList;
  }

  private boolean isPotentialResultAreaEdge(DirectedEdge de, int opCode)
  {
    // mark all dirEdges with the appropriate label
    Label label = de.getLabel();
    if (label.isArea()
        && ! de.isInteriorAreaEdge()
        && OverlayOp.isResultOfOp(
        label.getLocation(0, Position.RIGHT),
        label.getLocation(1, Position.RIGHT),
        opCode)
      ) {
        return true;
//Debug.print("in result "); Debug.println(de);
      }
      return false;
    }

  private final int SCANNING_FOR_INCOMING = 1;
  private final int LINKING_TO_OUTGOING = 2;

  private void testLinkResultDirectedEdges(DirectedEdgeStar deStar, int opCode)
  {
    // make sure edges are copied to resultAreaEdges list
    List ringEdges = getPotentialResultAreaEdges(deStar, opCode);
    // find first area edge (if any) to start linking at
    DirectedEdge firstOut = null;
    DirectedEdge incoming = null;
    int state = SCANNING_FOR_INCOMING;
    // link edges in CCW order
    for (int i = 0; i < ringEdges.size(); i++) {
      DirectedEdge nextOut = (DirectedEdge) ringEdges.get(i);
      DirectedEdge nextIn = nextOut.getSym();

      // skip de's that we're not interested in
      if (! nextOut.getLabel().isArea()) continue;

      // record first outgoing edge, in order to link the last incoming edge
      if (firstOut == null
          && isPotentialResultAreaEdge(nextOut, opCode))
        firstOut = nextOut;
      // assert: sym.isInResult() == false, since pairs of dirEdges should have been removed already

      switch (state) {
      case SCANNING_FOR_INCOMING:
        if (! isPotentialResultAreaEdge(nextIn, opCode)) continue;
        incoming = nextIn;
        state = LINKING_TO_OUTGOING;
        break;
      case LINKING_TO_OUTGOING:
        if (! isPotentialResultAreaEdge(nextOut, opCode)) continue;
        //incoming.setNext(nextOut);
        state = SCANNING_FOR_INCOMING;
        break;
      }
    }
//Debug.print(this);
    if (state == LINKING_TO_OUTGOING) {
//Debug.print(firstOut == null, this);
      if (firstOut == null)
        throw new TopologyException("no outgoing dirEdge found", deStar.getCoordinate());
    }

  }




}