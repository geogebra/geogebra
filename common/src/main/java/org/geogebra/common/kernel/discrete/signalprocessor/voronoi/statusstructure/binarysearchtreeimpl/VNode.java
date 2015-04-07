package org.geogebra.common.kernel.discrete.signalprocessor.voronoi.statusstructure.binarysearchtreeimpl;


public interface VNode {

    /* ***************************************************** */
    // Methods
    
    public void setParent(VInternalNode _parent);
    public VInternalNode getParent();
    
    public boolean isLeafNode();
    
    public boolean isInternalNode();
    
    
    /* ***************************************************** */
    
}
