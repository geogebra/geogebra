package geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.triangulation;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;

public class SharedEdgeRemoval {
    
    /* ***************************************************** */
    // Comparator for removeEdgesInOrderFromOuterBoundary() method
    
    private static final Comparator<VHalfEdge> EDGELENGTH_COMPARATOR = new Comparator<VHalfEdge>() {
        public int compare(VHalfEdge e1, VHalfEdge e2) {
            if      (e1.getLength() > e2.getLength()) return -1;
            else if (e1.getLength() < e2.getLength()) return  1;
            else {
                // Note: duplicates are removed from the TreeMap that uses
                //  this comparator - so we really don't want to edges to
                //  be considered identical (not specified in java documentation)
                if (e1.getY() < e2.getY())      return -1;
                else if (e1.getY() > e2.getY()) return 1;
                else if (e1.getX() < e2.getX()) return -1;
                else if (e1.getX() > e2.getX()) return 1;
                else return 0;
            }
        }
    };
    
    /* ***************************************************** */
    // Reduce a system from it's outer boundary inward starting from
    //  it's longest length outer boundaries and moving inward
    
    public static void removeEdgesInOrderFromOuterBoundary(VHalfEdge _outeredge, int length_cutoff) {
        // Collect a list of outer edges
        OrderedEdgeList outeredges = new OrderedEdgeList(_outeredge);
        
        //System.out.println();
        //System.out.println("************************************************");
        //System.out.println("*** removeEdgesInOrderFromOuterBoundary()");
        
        // Remove edges until we can't remove any more
        boolean haschanged;
        int edgesremoved = 0;
        do {
            haschanged = false;
            Iterator<VHalfEdge> iter = outeredges.values().iterator();
            while ( iter.hasNext() ) {
                VHalfEdge edge = iter.next();
                //System.out.println("Considering removal of edge (" + edge.vertex.id + "," + edge.getConnectedVertex().id + ") - length=" + edge.getLength());
                
                // Return if the length of the given edge is less than or equal to
                //  the minimum value allowed (i.e. the cut-off value)
                if ( edge.getLength()<=length_cutoff ) {
                    return;
                }
                
                // Cannot remove if removal would leave only _one_ edge
                //  left coming from the given vertex.
                if ( edge.vertex.getEdges().size()<=2 || edge.getConnectedVertex().getEdges().size()<=2 ) {
                	//System.out.println("Irregular");
                    continue;
                }
                
                // Cannot remove if removal would leave a _point_ where
                //  _two shapes_ are connected
                VHalfEdge inneredge = edge.getConnectedVertex().getEdge(edge.vertex);
                VVertex innertriangletopvertex = inneredge.next.getConnectedVertex();
                if ( innertriangletopvertex.getEdge(TriangulationRepresentation.OUTER_VERTEXNUMBER)!=null ) {
                	//System.out.println("Irregular");
                    continue;
                }
                
                // Remove the edge if it's valid to do so
                iter.remove();
                edgesremoved++;
                removeSingleOuterEdge( edge , outeredges );
                haschanged = true;
                break;
            }
        } while (( haschanged)
        &&
                ( TriangulationRepresentation.MAX_EDGES_TO_REMOVE < 0
                || edgesremoved<TriangulationRepresentation.MAX_EDGES_TO_REMOVE ));
    }
    
    private static void removeSingleOuterEdge(VHalfEdge outeredge, OrderedEdgeList outeredges) {
        //System.out.println("Removing edge (" + outeredge.vertex.id + "," + outeredge.getConnectedVertex().id + ")");
        
        // Get inner halfedge
        VHalfEdge inneredge = outeredge.getConnectedVertex().getEdge(outeredge.vertex);
        //System.out.println("  - Got inner edge (" + inneredge.vertex.id + "," + inneredge.getConnectedVertex().id + ")");
        
        // Remove halfedges (both inner and outer)
        //System.out.println("  - Removing both inner and outer edge");
        if ( outeredge.vertex.removeEdge( outeredge )==false ) {
            throw new RuntimeException("Outer edge not removed successfully");
        }
        if ( inneredge.vertex.removeEdge( inneredge )==false ) {
            throw new RuntimeException("Inner edge not removed successfully");
        }
        
        // Get previous edge
        VHalfEdge previousedge = null;
        //System.out.println("  - Determining previous edge");
        for ( VHalfEdge tmpedge : outeredge.vertex.getEdges() ) {
            //System.out.println("    + Considering (" + tmpedge.vertex.id + "," + tmpedge.next.vertex.id + ")");
            VHalfEdge tmppreviousedge = tmpedge.getConnectedVertex().getEdge(TriangulationRepresentation.OUTER_VERTEXNUMBER);
            if ( tmppreviousedge!=null && tmppreviousedge.next==outeredge ) {
                previousedge = tmppreviousedge;
                break;
            }
        }
        if ( previousedge==null ) {
            throw new RuntimeException("Previous edge was null");
        }
        //System.out.println("    - Found Previous Edge (" + previousedge.vertex.id + "," + previousedge.next.vertex.id + ")");
        
        // Relink and relabel nodes appropriately
        VHalfEdge newouteredge = inneredge.next;
        //System.out.println("  - Set Previous Edge Next to Vertex " + newouteredge.vertex.id);
        previousedge.next = newouteredge;
        do {
            //System.out.println("  + Set Edge (" + newouteredge.vertex.id + "," + newouteredge.next.vertex.id + ") as Outer Edge");
            outeredges.addEdge( newouteredge );
            newouteredge.vertexnumber = TriangulationRepresentation.OUTER_VERTEXNUMBER;
        } while ( (newouteredge=newouteredge.next).next!=inneredge );
        outeredges.addEdge( newouteredge );
        newouteredge.vertexnumber = TriangulationRepresentation.OUTER_VERTEXNUMBER;
        newouteredge.next = outeredge.next;
        
        //System.out.println("  - Set Edge (" + newouteredge.vertex.id + "," + newouteredge.next.vertex.id + ") as Outer Edge and to point to Edge (" + outeredge.next.vertex.id + "," + outeredge.next.next.vertex.id + ")");
        
        // Reset previous edges details
        inneredge.vertexnumber = outeredge.vertexnumber = -99;
        //inneredge.vertex = outeredge.vertex = null;
        inneredge.next = outeredge.next = null;
    }
    
    
    /* ***************************************************** */
    
    private static class OrderedEdgeList extends TreeMap<VHalfEdge,VHalfEdge> {
        
        /* ************************************************* */
        // Constructor
        
		private static final long serialVersionUID = 1L;

		private OrderedEdgeList(VHalfEdge outeredge) {
            super(EDGELENGTH_COMPARATOR);
            addOuterEdges(outeredge);
        }
        
        public void addOuterEdges(VHalfEdge outeredge) {
            // Check is not null and next is not null
            if ( outeredge==null || outeredge.next==null ) {
                return;
            }
            
            // Go round outside of shape
            VHalfEdge curredge = outeredge;
            do {
                super.put(curredge, curredge);
            } while ( (curredge=curredge.next).next!=null && curredge!=outeredge );
       }
        
        /* ************************************************* */
        // Methods
        
        public void addEdge(VHalfEdge edge) {
            super.put(edge, edge);
        }
        
        /* ************************************************* */
    }
    
    /* ***************************************************** */
}
