package org.geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.boundaryproblem.voronoicell;

public class VHalfEdge4 {
    
    public int x;
    public int y;
    
    private VHalfEdge4 next;
    private VHalfEdge4 prev;

    public VHalfEdge4() {
        this( -1 , -1 );
    }
    public VHalfEdge4(int _x, int _y) {
        this.x = _x;
        this.y = _y;
    }

    public void setXY( int _x , int _y ) {
        this.x = _x;
        this.y = _y;
    }
    
    public VHalfEdge4 getNext() { return next; }
    public VHalfEdge4 getPrev() { return prev; }
    public void setNext(VHalfEdge4 _next) {
        this.next = _next;
        _next.prev = this;
    }
    
    @Override
	public String toString() {
        return "(" + x + "," + y + ") -> " + (next==null?"n/a":"(" + next.x + "," + next.y + ")");
    }
}
