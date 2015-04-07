package org.geogebra.common.kernel.discrete.signalprocessor.voronoi.statusstructure.binarysearchtreeimpl;

import org.geogebra.common.kernel.discrete.signalprocessor.voronoi.eventqueue.VSiteEvent;
import org.geogebra.common.kernel.discrete.signalprocessor.voronoi.statusstructure.VLinkedNode;

public class VLeafNode extends VLinkedNode implements VNode {
    
    /* ***************************************************** */
    // Variables
    
    public int id = BSTStatusStructure.uniqueid++;
    protected VInternalNode parent;
    
    /* ***************************************************** */
    // Constructor
    
    protected VLeafNode() { }
    public VLeafNode(VSiteEvent _siteevent) {
        super( _siteevent );
    }
    
    /* ***************************************************** */
    // Methods
    
    public VInternalNode getParent() { return parent; }
    public void setParent(VInternalNode _parent) {
        this.parent = _parent;
    }
    
    public boolean isLeafNode() { return true; }
    public boolean isInternalNode() { return false; }
    
    public VLeafNode cloneLeafNode() {
        VLeafNode clone = new VLeafNode(this.siteevent);
        // DO NOT DUPLICATE prev/next values
        // DO NOT DUPLICATE circle events???? (?!)
        return clone;
    }
    
    public VInternalNode getFirstCommonParent(VLeafNode othernode) {
        VInternalNode parent1 = parent;
        VInternalNode parent2 = othernode.parent;
        int depth1 = parent.getDepth();
        int depth2 = othernode.parent.getDepth();
        
        // Go up until at equal depths
        if ( depth1 > depth2 ) {
            do {
                depth1--;
                parent1 = parent1.getParent();
            } while ( depth1 > depth2 );
        } else if ( depth2 > depth1 ) {
            do {
                depth2--;
                parent2 = parent2.getParent();
            } while ( depth2 > depth1 );
        }
        
        // Find common parent from common depth
        while ( parent1!=parent2 ) {
            parent1 = parent1.getParent();
            parent2 = parent2.getParent();
        }

        // Return common parent
        return parent1;
    }
    
    
    /* ***************************************************** */
    // To String Method
    
    public String toString() {
        return "VLeafNode" + id + " (" + siteevent + ")";
    }
    
    /* ***************************************************** */
}
