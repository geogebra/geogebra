package geogebra.common.kernel.discrete.signalprocessor.voronoi.eventqueue;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeMap;

public class EventQueue {
    
    /* ********************************************************* */
    // Constants
    
    private static final Comparator<VEvent> PRIORITY_COMPARATOR = new Comparator<VEvent>() {
        public int compare(VEvent p1, VEvent p2) {
            if (p1.getY() < p2.getY())      return -1;
            else if (p1.getY() > p2.getY()) return 1;
            else if (p1.getX() < p2.getX()) return -1;
            else if (p1.getX() > p2.getX()) return 1;
            else if (p1 == p2             ) return 0;
            else {
                // In situation where we have two different events
                //  both at the same coordinate - for site events the same,
                //  treat as equal (i.e. one will be deleted), otherwise treat
                //  as different (with Site Events going first)
                if ( p1.isSiteEvent() && p2.isSiteEvent() ) {
                    return 0;
                } else if ( p1.isCircleEvent() && p2.isCircleEvent() ) {
                    if ( p1.id < p2.id ) return -1;
                    else if ( p1.id > p2.id ) return 1;
                    else return 0;
                } else if ( p1.isSiteEvent() ) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }
    };
    
    /* ********************************************************* */
    // Variables
    
    private TreeMap<VEvent,VEvent> queue;
    
    /* ********************************************************* */
    // Constructor
    
    public EventQueue() {
        queue = new TreeMap<VEvent,VEvent>(PRIORITY_COMPARATOR);
    }
    
    public EventQueue(Collection<VEvent> events) {
        this();
        
        for ( VEvent event : events ) {
            queue.put( event , event );
        }
    }
    
    /* ********************************************************* */
    // Methods
    
    public void addEvent(VEvent event) {
        queue.put(event, event);
    }
    
    public boolean removeEvent(VEvent event) {
        return ( queue.remove(event)!=null );
    }
    
    public VEvent getFirstEvent() {
        if ( queue.size()>0 ) {
            return queue.firstKey();
        } 
        return null;
    }
    
    public VEvent getAndRemoveFirstEvent() {
        VEvent event = queue.firstKey();
        queue.remove(event);
        return event;
    }
    
    public boolean isEventQueueEmpty() {
        return queue.isEmpty();
    }
    
    /* ********************************************************* */
}