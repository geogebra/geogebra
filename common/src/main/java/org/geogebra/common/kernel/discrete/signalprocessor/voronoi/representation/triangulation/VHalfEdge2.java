package org.geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.triangulation;

public class VHalfEdge2 {

    /* ***************************************************** */
    // Variables
    
    public int vertexnumber;
    public VHalfEdge2 next;
    
    public VVertex2 vertex;
    
    private int length = -1;
    
    public boolean shownonminimumspanningtree = false;
    
    
    /* ***************************************************** */
    // Constructors
    
    public VHalfEdge2(int _vertexnumber, VVertex2 _vertex) {
        this.vertexnumber = _vertexnumber;
        this.vertex = _vertex;
    }
    
    public VHalfEdge2(int _vertexnumber, VVertex2 _vertex, VHalfEdge2 _next) {
        this.vertexnumber = _vertexnumber;
        this.vertex = _vertex;
        this.next = _next;
    }

    /* ***************************************************** */
    // Methods

    public boolean isOuterEdge() {
        return ( vertexnumber==TriangulationRepresentation.OUTER_VERTEXNUMBER );
    }
    
    public VVertex2 getConnectedVertex() {
        return next.vertex;
    }
    
    public int getLength() {
        if ( length==-1 ) {
            length = (int) vertex.distanceTo(next.vertex);
        }
        return length;
    }
    
    public double getX() {
        return vertex.x;
    }
    
    public double getY() {
        return vertex.y;
    }
    
    /* ***************************************************** */
}
