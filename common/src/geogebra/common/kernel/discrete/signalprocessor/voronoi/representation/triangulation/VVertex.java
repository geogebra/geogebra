package geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.triangulation;

import geogebra.common.kernel.discrete.signalprocessor.voronoi.VPoint;

import java.util.ArrayList;

public class VVertex extends VPoint {
    
    /* ***************************************************** */
    // Static Variables
    
    public static int uniqueid = 1;
    
    /* ***************************************************** */
    // Variables
    
    public int id = uniqueid++;
    
    private ArrayList<VHalfEdge> edges;
    
    /* ***************************************************** */
    // Constructors
    
    public VVertex() { super(); }
    public VVertex(double x, double y) { super(x, y); }
    public VVertex(VPoint point) { super(point); }
    
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
    public void addEdge(VHalfEdge edge) {
        if ( edges==null ) {
            edges = new ArrayList<VHalfEdge>();
        }
        edges.add( edge );
    }
    public ArrayList<VHalfEdge> getEdges() {
        if ( edges==null || edges.size()<=0 ) {
            return null;
        }
        return edges;
    }
    public boolean removeEdge(VHalfEdge edge) {
        if ( edges==null ) {
            return false;
        }
        return edges.remove( edge );
    }
    
    /* ***************************************************** */
    // Calculate Distance to Vertex method
    
    public double distanceTo(VVertex distance) {
        return Math.sqrt( (x-distance.x)*(x-distance.x) + (y-distance.y)*(y-distance.y) );
    }
    
    /* ***************************************************** */
    // Helper methods

	public VHalfEdge getEdge(VVertex connectedtovertex) {
		if (edges == null || edges.size() <= 0) {
			return null;
		}
		for (VHalfEdge edge : edges) {
			if (edge.next != null && edge.next.vertex == connectedtovertex) {
				return edge;
			}
		}
		return null;
	}
    
	public VHalfEdge getEdge(int vertexnumber) {
		if (edges == null || edges.size() <= 0) {
			return null;
		}
		for (VHalfEdge edge : edges) {
			// Don't actually need .next in edge.next.vertexnumber, as
			// edge.next.vertexnumber==edge.vertexnumber as long as the
			// system is in a valid state.
			if (edge.next != null && edge.next.vertexnumber == vertexnumber) {
				return edge;
			}
		}
		return null;
	}
    
    public boolean isConnectedTo(VVertex connectedtovertex) {
        VHalfEdge edge = getEdge(connectedtovertex);
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
		for (VHalfEdge edge : edges) {
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
		for (VHalfEdge edge : edges) {
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
