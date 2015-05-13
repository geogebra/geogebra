package org.geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.boundaryproblem;

public class VHalfEdge1 {

    /* ***************************************************** */
    // Variables
    
    public int vertexnumber;
    public boolean isdeleted = false;
    public VVertex1 vertex;
    
    /* ***************************************************** */
    // Constructors
    
    public VHalfEdge1(int _vertexnumber, VVertex1 _vertex) {
        this.vertexnumber = _vertexnumber;
        this.vertex = _vertex;
    }

    /* ***************************************************** */
}
