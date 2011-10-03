package geogebra.kernel.discrete.signalprocesser.voronoi.representation.simpletriangulation;
import geogebra.kernel.discrete.signalprocesser.voronoi.VPoint;

public class VTriangle extends VPoint {
    
    public VPoint p1;
    public VPoint p2;
    public VPoint p3;
    
    public VTriangle() { super(); }
    public VTriangle(int x, int y) { super(x, y); }
    public VTriangle(VPoint point) { super(point); }
    
}
