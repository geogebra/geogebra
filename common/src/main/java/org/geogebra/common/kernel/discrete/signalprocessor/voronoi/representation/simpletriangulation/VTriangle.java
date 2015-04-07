package org.geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.simpletriangulation;
import org.geogebra.common.kernel.discrete.signalprocessor.voronoi.VPoint;

public class VTriangle extends VPoint {
    
    public VPoint p1;
    public VPoint p2;
    public VPoint p3;
    
    public VTriangle() { super(); }
    public VTriangle(int x, int y) { super(x, y); }
    public VTriangle(VPoint point) { super(point); }
    
}
