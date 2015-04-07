package com.vividsolutions.jts.noding.snapround;

import java.util.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.noding.*;
import com.vividsolutions.jts.algorithm.LineIntersector;
import com.vividsolutions.jts.index.chain.*;
import com.vividsolutions.jts.index.*;
import com.vividsolutions.jts.index.strtree.STRtree;
import com.vividsolutions.jts.index.quadtree.Quadtree;

/**
 * "Snaps" all {@link SegmentString}s in a {@link SpatialIndex} containing
 * {@link MonotoneChain}s to a given {@link HotPixel}.
 *
 * @version 1.7
 */
public class MCIndexPointSnapper
{
  public static int nSnaps = 0;

  private Collection monoChains;
  private STRtree index;

  public MCIndexPointSnapper(Collection monoChains, SpatialIndex index) {
    this.monoChains = monoChains;
    this.index = (STRtree) index;
  }

  /**
   * Snaps (nodes) all interacting segments to this hot pixel.
   * The hot pixel may represent a vertex of an edge,
   * in which case this routine uses the optimization
   * of not noding the vertex itself
   *
   * @param hotPixel the hot pixel to snap to
   * @param parentEdge the edge containing the vertex, if applicable, or <code>null</code>
   * @param vertexIndex the index of the vertex, if applicable, or -1
   * @return <code>true</code> if a node was added for this pixel
   */
  public boolean snap(HotPixel hotPixel, SegmentString parentEdge, int vertexIndex)
  {
    final Envelope pixelEnv = hotPixel.getSafeEnvelope();
    final HotPixelSnapAction hotPixelSnapAction = new HotPixelSnapAction(hotPixel, parentEdge, vertexIndex);

    index.query(pixelEnv, new ItemVisitor() {
      public void visitItem(Object item) {
        MonotoneChain testChain = (MonotoneChain) item;
        testChain.select(pixelEnv, hotPixelSnapAction);
      }
    }
    );
    return hotPixelSnapAction.isNodeAdded();
  }

  public boolean snap(HotPixel hotPixel)
  {
    return snap(hotPixel, null, -1);
  }

  public class HotPixelSnapAction
      extends MonotoneChainSelectAction
  {
    private HotPixel hotPixel;
    private SegmentString parentEdge;
    private int vertexIndex;
    private boolean isNodeAdded = false;

    public HotPixelSnapAction(HotPixel hotPixel, SegmentString parentEdge, int vertexIndex)
    {
      this.hotPixel = hotPixel;
      this.parentEdge = parentEdge;
      this.vertexIndex = vertexIndex;
    }

    public boolean isNodeAdded() { return isNodeAdded; }

    public void select(MonotoneChain mc, int startIndex)
    {
    	NodedSegmentString ss = (NodedSegmentString) mc.getContext();
      // don't snap a vertex to itself
      if (parentEdge != null) {
        if (ss == parentEdge && startIndex == vertexIndex)
          return;
      }
//      isNodeAdded = SimpleSnapRounder.addSnappedNode(hotPixel, ss, startIndex);
      isNodeAdded = hotPixel.addSnappedNode(ss, startIndex);
    }

  }

}