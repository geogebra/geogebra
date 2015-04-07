package org.geogebra.common.kernel.discrete.signalprocessor.voronoi;

public class VPoint {
    
    /* ***************************************************** */
    // Variables
    
    public double x;
    public double y;
    
    /* ***************************************************** */
    // Constructors
    
    public VPoint() {
        this(-1, -1);
    }
    public VPoint(double inhom, double inhom2) {
        this.x = inhom;
        this.y = inhom2;
    }
    public VPoint(VPoint point) {
        this.x = point.x;
        this.y = point.y;
    }
    
    public double distanceTo(VPoint point) {
        return Math.sqrt((this.x-point.x)*(this.x-point.x) + (this.y-point.y)*(this.y-point.y));
    }
    
    public String toString() {
        return "VPoint (" + x + "," + y + ")";
    }
    
    /* ***************************************************** */
}
