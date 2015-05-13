package org.geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.boundaryproblem;
import java.util.ArrayList;

import org.geogebra.common.kernel.discrete.signalprocessor.voronoi.VPoint;

public class VVertex1 extends VPoint {
    
    public static int uniqueid = 1;
    
    public int id = uniqueid++;
    
    private ArrayList<VHalfEdge1> connectedvertexs;
    
    public VVertex1() { super(); }
    public VVertex1(double x, double y) { super(x, y); }
    public VVertex1(VPoint point) { super(point); }
    
    public void clearConnectedVertexs() {
        if ( connectedvertexs!=null ) {
            connectedvertexs.clear();
        }
    }
    
    public void addConnectedVertex(VHalfEdge1 edge) {
        if ( connectedvertexs==null ) {
            connectedvertexs = new ArrayList<VHalfEdge1>();
        }
        connectedvertexs.add( edge );
    }
    
    public ArrayList<VHalfEdge1> getConnectedVertexs() {
        if ( connectedvertexs==null || connectedvertexs.size()<=0 ) {
            return null;
        }
        return connectedvertexs;
    }
    
    public double distanceTo(VVertex1 distance) {
        return Math.sqrt( (x-distance.x)*(x-distance.x) + (y-distance.y)*(y-distance.y) );
    }
    
	public VHalfEdge1 getNextConnectedEdge(int vertexnumber) {
		if (connectedvertexs == null || connectedvertexs.size() <= 0) {
			return null;
		}
		for (VHalfEdge1 edge : connectedvertexs) {
			if (edge.vertexnumber == vertexnumber) {
				return edge;
			}
		}
		return null;
	}
	
	public VHalfEdge1 getNextConnectedEdge(VVertex1 nextvertex) {
		if (connectedvertexs == null || connectedvertexs.size() <= 0) {
			return null;
		}
		for (VHalfEdge1 edge : connectedvertexs) {
			if (edge.vertex == nextvertex) {
				return edge;
			}
		}
		return null;
	}
    
    public VVertex1 getNextConnectedVertex(int vertexnumber) {
        VHalfEdge1 edge = getNextConnectedEdge(vertexnumber);
        return ( edge==null ? null : edge.vertex );
    }
    
    public String getConnectedVertexString() {
        String str = null;
        for ( VHalfEdge1 edge : connectedvertexs ) {
            if ( str==null ) {
                str  = "" + edge.vertexnumber;
            } else {
                str += ", " + edge.vertexnumber;
            }
        }
        return str;
    }
}
