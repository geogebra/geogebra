package geogebra.kernel.discrete.signalprocesser.voronoi.statusstructure.doublelinkedlistimpl;

import geogebra.kernel.discrete.signalprocesser.voronoi.VoronoiShared;
import geogebra.kernel.discrete.signalprocesser.voronoi.eventqueue.EventQueue;
import geogebra.kernel.discrete.signalprocesser.voronoi.eventqueue.VSiteEvent;
import geogebra.kernel.discrete.signalprocesser.voronoi.statusstructure.AbstractStatusStructure;
import geogebra.kernel.discrete.signalprocesser.voronoi.statusstructure.VLinkedNode;

/**
 * Implementation *not* working
 */
public class DLinkedListStatusStructure extends AbstractStatusStructure {
    
    /* ***************************************************** */
    // Variables
    
    private VLinkedNode head = null;
    
    /* ***************************************************** */
    // Core Methods
    
    public boolean isStatusStructureEmpty() {
        return ( head==null );
    }
    
    public void setRootNode( VSiteEvent siteevent ) {
        setRootNode( new VLinkedNode(siteevent) );
    }
    protected void setRootNode( VLinkedNode node ) {
        head = node;
    }
    
    public VLinkedNode insertNode(VLinkedNode nodetosplit, VSiteEvent siteevent) {
        VLinkedNode newnode = new VLinkedNode(siteevent);
        
        // Prepare to link new node into linked list...
        VLinkedNode leaf1 = nodetosplit;
        VLinkedNode leaf3 = nodetosplit.cloneLinkedNode();
        VLinkedNode tmp = nodetosplit.getNext();
        
        // Set next variables appropriately (each call sets prev value as well)
        leaf1.setNext( newnode );
        newnode.setNext( leaf3 );
        leaf3.setNext( tmp );
        
        // Return the newly create node
        return newnode;
    }
    
    public void removeNode(EventQueue eventqueue, VLinkedNode toremove) {
        // Unlink Double-Linked List Structure
        if ( toremove.getPrev()==null ) {
            toremove.setNext(null);
        } else {
            toremove.getPrev().setNext( toremove.getNext() );
        }
    }
    
    public VLinkedNode getNodeAboveSiteEvent( double siteevent_x , double sweepline ) {
        if ( head==null ) { return null; }
        //if ( head.getNext()==null ) { return head; }
        
        VLinkedNode curr = head;
        //curr.siteevent.calcParabolaConstants(sweepline);
        while ( curr.getNext()!=null ) {
            VSiteEvent v1 = head.siteevent;
            VSiteEvent v2 = head.getNext().siteevent;
            
            //if ( sweepline>v1.x && sweepline>v2.x ) {
                // Calculate parabolic constants
                v1.calcParabolaConstants(sweepline);
                v2.calcParabolaConstants(sweepline);
                
                // Determine where two parabola meet
                double intersects[] = VoronoiShared.solveQuadratic(v1.a-v2.a, v1.b-v2.b, v1.c-v2.c);
                //double intersects[] = VoronoiShared.solveQuadratic(v2.a-v1.a, v2.b-v1.b, v2.c-v1.c);
                if (!( intersects[0] <= siteevent_x && intersects[0]!=intersects[1] )) {
                    return curr;
                }
            //}
            
            curr=curr.getNext();
        }
        
        return curr;
    }
    
    // Function only used by test functions
    public VLinkedNode getHeadNode() {
        return head;
    }
    
    /* ***************************************************** */
    // Debug toString() Method
    
    public String toString() {
        VLinkedNode node = getHeadNode();
        if ( node==null ) {
            return "| Doubly-linked list is empty";
        } else {
            StringBuffer buffer = new StringBuffer();
            buffer.append("| ");
            boolean isfirst = true;
            do {
                if ( isfirst ) {
                    isfirst = false;
                } else {
                    buffer.append(" -> ");
                }
                buffer.append("Node (" + node.siteevent.getX() + "," + node.siteevent.getY() + ") #" + node.siteevent.getID());
            } while ( (node=node.getNext())!=null );
            return buffer.toString();
        }
    }
    
    /* ***************************************************** */
}
