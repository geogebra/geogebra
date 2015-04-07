package org.geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.boundaryproblem;

public class VHalfEdge {

    /* ***************************************************** */
    // Variables
    
    public int vertexnumber;
    public boolean isdeleted = false;
    public VVertex vertex;
    
    /* ***************************************************** */
    // Constructors
    
    public VHalfEdge(int _vertexnumber, VVertex _vertex) {
        this.vertexnumber = _vertexnumber;
        this.vertex = _vertex;
    }

    /* ***************************************************** */
}
