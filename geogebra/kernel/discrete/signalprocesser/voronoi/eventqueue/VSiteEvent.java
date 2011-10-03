package geogebra.kernel.discrete.signalprocesser.voronoi.eventqueue;

import geogebra.kernel.discrete.signalprocesser.voronoi.VPoint;

public class VSiteEvent extends VEvent {
    
    /* ***************************************************** */
    // Variables

    private VPoint point;
    
    public double a;
    public double b;
    public double c;
    
    /* ***************************************************** */
    // Constructors
    
    public VSiteEvent(VPoint _point) {
        if ( _point==null ) {
            throw new IllegalArgumentException("Point for siteevent cannot be null");
        }
        this.point = _point;
    }

    /* ***************************************************** */
    // Methods
    
    public void calcParabolaConstants(double sweepline) {
        double yminussweepline = ( point.y - sweepline );
        a = 0.5 / yminussweepline;
        b = -1.0 * point.x / yminussweepline;
        c = (point.x * point.x) / (2.0 * yminussweepline) + 0.5 * yminussweepline;
    }
    
    public int getYValueOfParabola(int x) {
        return (int) (( a * (double)x + b ) * (double)x + c);
    }
    
    public int getYValueOfParabola(double x) {
        return (int) (( a * x + b ) * x + c);
    }
    
    /* ***************************************************** */
    // Abstract Methods
    
    public double getX() { return point.x; }
    public double getY() { return point.y; }
    
    public VPoint getPoint() { return point; }
    
    public boolean isSiteEvent() { return true; }
    
    public boolean isCircleEvent() { return false; }
    
    /* ***************************************************** */
    // To String Method
    
    public String toString() {
        return "VSiteEvent (" + point.x + "," + point.y + ")";
    }
    
    /* ***************************************************** */
}
