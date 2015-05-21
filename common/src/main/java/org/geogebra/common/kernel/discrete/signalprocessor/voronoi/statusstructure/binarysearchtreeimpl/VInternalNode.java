package org.geogebra.common.kernel.discrete.signalprocessor.voronoi.statusstructure.binarysearchtreeimpl;

import org.geogebra.common.kernel.discrete.signalprocessor.voronoi.eventqueue.VSiteEvent;

public class VInternalNode implements VNode {
    
    /* ***************************************************** */
    // Variables
    
    public int id = BSTStatusStructure.uniqueid++;
    protected VInternalNode parent;
    
    private int depth = 1;
    
    private VNode left;
    private VNode right;
    
    public VSiteEvent v1;
    public VSiteEvent v2;
    
    public Object halfedge_in;
    public Object halfedge_out;
    
    /* ***************************************************** */
    // Constructors
    
    public VInternalNode() {
        // do nothing
    }
    public VInternalNode(VNode _left, VNode _right) {
		// System.out.println("CREATED :: " + this);
        setLeft( _left );
        setRight( _right );
    }
    
    /* ***************************************************** */
    // Methods
    
    public VInternalNode getParent() { return parent; }
    public void setParent(VInternalNode _parent) {
        this.parent = _parent;
    }
    
    public int getDepth() { return depth; }
    
    public boolean isLeafNode() { return false; }
    public boolean isInternalNode() { return true; }
    
    public VNode getLeft() { return left; }
    public void setLeft(VNode _left) {
        // Unset previous parent
        if ( left!=null ) {
            left.setParent( null );
        }
        
        // Set value and new parent
        this.left = _left;
        _left.setParent( this );
        
        // Correct depth values
        if ( _left instanceof VInternalNode ) {
            correctDepthValues( depth+1 , (VInternalNode)_left );
        }
    }
    
    public VNode getRight() { return right; }
    public void setRight(VNode _right) {
        // Unset previous parent
        if ( right!=null ) {
            right.setParent( null );
        }
        
        // Set value and new parent
        this.right = _right;
        _right.setParent( this );
        
        // Correct depth values
        if ( _right instanceof VInternalNode ) {
            correctDepthValues( depth+1 , (VInternalNode)_right );
        }
    }
    
    public void setDepthForRootNode() {
        correctDepthValues( 1 , this );
    }
    
    // Correct depth values
    private void correctDepthValues( int depth , VInternalNode internalnode ) {
        internalnode.depth = depth;
        if ( internalnode.left instanceof VInternalNode ) {
            correctDepthValues( depth+1 , (VInternalNode)internalnode.left );
        }
        if ( internalnode.right instanceof VInternalNode ) {
            correctDepthValues( depth+1 , (VInternalNode)internalnode.right );
        }
    }
    
    public void setSiteEvents(VSiteEvent _siteevent_left, VSiteEvent _siteevent_right) {
        this.v1 = _siteevent_left;
        this.v2 = _siteevent_right;
    }
    
    /* ***************************************************** */
    // Constructors
    
    public String toString() {
        return "VInternalNode" + id + " (" + v1.getX() + "," + v1.getY() + "), (" + v2.getX() + "," + v2.getY() + ")";
    }
    
    
    /* ***************************************************** */
}
