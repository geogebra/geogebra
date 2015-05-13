package org.geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.triangulation;

import java.util.ArrayList;

import org.geogebra.common.kernel.discrete.signalprocessor.voronoi.VPoint;

public class VVertex2 extends VPoint {
    
    /* ***************************************************** */
    // Static Variables
    
    public static int uniqueid = 1;
    
    /* ***************************************************** */
    // Variables
    
    public int id = uniqueid++;
    
    private ArrayList<VHalfEdge2> edges;
    
    /* ***************************************************** */
    // Constructors
    
    public VVertex2() { super(); }
    public VVertex2(double x, double y) { super(x, y); }
    public VVertex2(VPoint point) { super(point); }
    
    /* ***************************************************** */
    // Edge Methods
    
    public boolean hasEdges() {
        return ( edges!=null && edges.size()>0 );
    }
    public void clearEdges() {
        if ( edges!=null ) {
            edges.clear();
        }
    }
    public void addEdge(VHalfEdge2 edge) {
        if ( edges==null ) {
            edges = new ArrayList<VHalfEdge2>();
        }
        edges.add( edge );
    }
    public ArrayList<VHalfEdge2> getEdges() {
        if ( edges==null || edges.size()<=0 ) {
            return null;
        }
        return edges;
    }
    public boolean removeEdge(VHalfEdge2 edge) {
        if ( edges==null ) {
            return false;
        }
        return edges.remove( edge );
    }
    
    /* ***************************************************** */
    // Calculate Distance to Vertex method
    
    public double distanceTo(VVertex2 distance) {
        return Math.sqrt( (x-distance.x)*(x-distance.x) + (y-distance.y)*(y-distance.y) );
    }
    
    /* ***************************************************** */
    // Helper methods

	public VHalfEdge2 getEdge(VVertex2 connectedtovertex) {
		if (edges == null || edges.size() <= 0) {
			return null;
		}
		for (VHalfEdge2 edge : edges) {
			if (edge.next != null && edge.next.vertex == connectedtovertex) {
				return edge;
			}
		}
		return null;
	}
    
	public VHalfEdge2 getEdge(int vertexnumber) {
		if (edges == null || edges.size() <= 0) {
			return null;
		}
		for (VHalfEdge2 edge : edges) {
			// Don't actually need .next in edge.next.vertexnumber, as
			// edge.next.vertexnumber==edge.vertexnumber as long as the
			// system is in a valid state.
			if (edge.next != null && edge.next.vertexnumber == vertexnumber) {
				return edge;
			}
		}
		return null;
	}
    
    public boolean isConnectedTo(VVertex2 connectedtovertex) {
        VHalfEdge2 edge = getEdge(connectedtovertex);
        return ( edge!=null );
    }
    
    /* ***************************************************** */
    // toString() Methods
    
	@Override
	public String toString() {
		return "VVertex (connected to " + getConnectedVertexString() + ")";
	}

	public String getConnectedEdgeString() {
		if (edges == null || edges.size() <= 0) {
			return null;
		}
		String str = null;
		for (VHalfEdge2 edge : edges) {
			if (str == null) {
				str = "" + edge.vertexnumber;
			} else {
				str += ", " + edge.vertexnumber;
			}
		}
		return str;
	}
    
	public String getConnectedVertexString() {
		if (edges == null || edges.size() <= 0) {
			return null;
		}
		String str = null;
		for (VHalfEdge2 edge : edges) {
			if (str == null) {
				str = "" + edge.getConnectedVertex().id;
			} else {
				str += ", " + edge.getConnectedVertex().id;
			}
		}
		return str;
	}
    
    /* ***************************************************** */
}
