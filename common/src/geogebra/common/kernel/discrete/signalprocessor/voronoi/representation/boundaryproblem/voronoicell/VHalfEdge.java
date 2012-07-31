package geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.boundaryproblem.voronoicell;

public class VHalfEdge {
    
    public int x;
    public int y;
    
    private VHalfEdge next;
    private VHalfEdge prev;

    public VHalfEdge() {
        this( -1 , -1 );
    }
    public VHalfEdge(int _x, int _y) {
        this.x = _x;
        this.y = _y;
    }

    public void setXY( int _x , int _y ) {
        this.x = _x;
        this.y = _y;
    }
    
    public VHalfEdge getNext() { return next; }
    public VHalfEdge getPrev() { return prev; }
    public void setNext(VHalfEdge _next) {
        this.next = _next;
        _next.prev = this;
    }
    
    @Override
	public String toString() {
        return "(" + x + "," + y + ") -> " + (next==null?"n/a":"(" + next.x + "," + next.y + ")");
    }
}
