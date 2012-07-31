package geogebra.common.kernel.discrete.signalprocessor.voronoi.eventqueue;

abstract public class VEvent {
    
    /* ***************************************************** */
    // Static Variables
    
    public static int uniqueid = 1;
            
    /* ***************************************************** */
    // Variables
    
    public final int id = uniqueid++;
    
    /* ***************************************************** */
    // Constructors
    
    public VEvent() { }
    
    /* ***************************************************** */
    // Methods
    
    abstract public double getX();
    abstract public double getY();
    
    abstract public boolean isSiteEvent();
    
    abstract public boolean isCircleEvent();
    
    public String getID() {
        return "EVT-"+id;
    }

    @Override
	public String toString() {
        return "VEvent (" + getID() + ")";
    }
    
    /* ***************************************************** */
}
