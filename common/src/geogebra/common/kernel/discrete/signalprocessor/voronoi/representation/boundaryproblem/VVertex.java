package geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.boundaryproblem;
import geogebra.common.kernel.discrete.signalprocessor.voronoi.VPoint;

import java.util.ArrayList;

public class VVertex extends VPoint {
    
    public static int uniqueid = 1;
    
    public int id = uniqueid++;
    
    private ArrayList<VHalfEdge> connectedvertexs;
    
    public VVertex() { super(); }
    public VVertex(double x, double y) { super(x, y); }
    public VVertex(VPoint point) { super(point); }
    
    public void clearConnectedVertexs() {
        if ( connectedvertexs!=null ) {
            connectedvertexs.clear();
        }
    }
    
    public void addConnectedVertex(VHalfEdge edge) {
        if ( connectedvertexs==null ) {
            connectedvertexs = new ArrayList<VHalfEdge>();
        }
        connectedvertexs.add( edge );
    }
    
    public ArrayList<VHalfEdge> getConnectedVertexs() {
        if ( connectedvertexs==null || connectedvertexs.size()<=0 ) {
            return null;
        }
        return connectedvertexs;
    }
    
    public double distanceTo(VVertex distance) {
        return Math.sqrt( (x-distance.x)*(x-distance.x) + (y-distance.y)*(y-distance.y) );
    }
    
	public VHalfEdge getNextConnectedEdge(int vertexnumber) {
		if (connectedvertexs == null || connectedvertexs.size() <= 0) {
			return null;
		}
		for (VHalfEdge edge : connectedvertexs) {
			if (edge.vertexnumber == vertexnumber) {
				return edge;
			}
		}
		return null;
	}
	
	public VHalfEdge getNextConnectedEdge(VVertex nextvertex) {
		if (connectedvertexs == null || connectedvertexs.size() <= 0) {
			return null;
		}
		for (VHalfEdge edge : connectedvertexs) {
			if (edge.vertex == nextvertex) {
				return edge;
			}
		}
		return null;
	}
    
    public VVertex getNextConnectedVertex(int vertexnumber) {
        VHalfEdge edge = getNextConnectedEdge(vertexnumber);
        return ( edge==null ? null : edge.vertex );
    }
    
    public String getConnectedVertexString() {
        String str = null;
        for ( VHalfEdge edge : connectedvertexs ) {
            if ( str==null ) {
                str  = "" + edge.vertexnumber;
            } else {
                str += ", " + edge.vertexnumber;
            }
        }
        return str;
    }
}
