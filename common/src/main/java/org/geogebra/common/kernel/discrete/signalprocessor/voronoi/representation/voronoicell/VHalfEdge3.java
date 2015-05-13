package org.geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.voronoicell;

public class VHalfEdge3 {
    
    public int x;
    public int y;
    
    private VHalfEdge3 next;
    private VHalfEdge3 prev;

    public VHalfEdge3() {
        this( -1 , -1 );
    }
    public VHalfEdge3(int _x, int _y) {
        this.x = _x;
        this.y = _y;
    }

    public void setXY( int _x , int _y ) {
        this.x = _x;
        this.y = _y;
    }
    
    public VHalfEdge3 getNext() { return next; }
    public VHalfEdge3 getPrev() { return prev; }
    public void setNext(VHalfEdge3 _next) {
        this.next = _next;
        _next.prev = this;
    }
    
    public String toString() {
        return "(" + x + "," + y + ") -> " + (next==null?"n/a":"(" + next.x + "," + next.y + ")");
    }
}
