package org.geogebra.common.kernel.discrete.signalprocessor.voronoi.statusstructure.binarysearchtreeimpl;

import org.geogebra.common.kernel.discrete.signalprocessor.voronoi.VoronoiShared;
import org.geogebra.common.kernel.discrete.signalprocessor.voronoi.eventqueue.EventQueue;
import org.geogebra.common.kernel.discrete.signalprocessor.voronoi.eventqueue.VSiteEvent;
import org.geogebra.common.kernel.discrete.signalprocessor.voronoi.statusstructure.AbstractStatusStructure;
import org.geogebra.common.kernel.discrete.signalprocessor.voronoi.statusstructure.VLinkedNode;

public class BSTStatusStructure extends AbstractStatusStructure {
    
    /* ***************************************************** */
    // Variables
    
    public static int uniqueid = 1;
    
    private VNode rootnode = null;
    
    /* ***************************************************** */
    // Core Methods
    
    public boolean isStatusStructureEmpty() {
        return ( rootnode==null );
    }
    
    public VNode getRootNode() { return rootnode; }
    public void setRootNode( VSiteEvent siteevent ) {
        setRootNode( new VLeafNode(siteevent) );
    }
    protected void setRootNode( VNode node ) {
        rootnode = node;
        rootnode.setParent(null);
        if ( rootnode instanceof VInternalNode ) {
            ((VInternalNode)rootnode).setDepthForRootNode();
        }
    }
    
    public VLinkedNode insertNode(VLinkedNode _nodetosplit, VSiteEvent siteevent) {
        VLeafNode nodetosplit = (VLeafNode) _nodetosplit;
        
        // Create new leaf and two new internal nows
        VLeafNode newleaf = new VLeafNode(siteevent);
        VInternalNode top    = new VInternalNode();
        VInternalNode bottom = new VInternalNode();
        
        // Link top to parent node
        if ( nodetosplit.getParent()==null ) {
            setRootNode( top );
        } else {
            if ( nodetosplit.getParent().getLeft()==nodetosplit ) {
                nodetosplit.getParent().setLeft( top );
            } else if ( nodetosplit.getParent().getRight()==nodetosplit ) {
                nodetosplit.getParent().setRight( top );
            } else {
                throw new RuntimeException("Neither child matched suggested parent for attaching new branch - linking error");
            }
        }
        
        // Link Tree
        top.setLeft( bottom );
        top.setRight( nodetosplit.cloneLeafNode() );
        bottom.setLeft( nodetosplit );
        bottom.setRight( newleaf );
        
        // Set internal values
        top   .setSiteEvents(siteevent, nodetosplit.siteevent);
        bottom.setSiteEvents(nodetosplit.siteevent, siteevent);
        
        // Double-linked List
        VLeafNode leaf1 = (VLeafNode)bottom.getLeft();
        VLeafNode leaf3 = (VLeafNode)top.getRight();
        VLeafNode tmp = (VLeafNode)nodetosplit.getNext();
        leaf1.setNext( newleaf );
        newleaf.setNext( leaf3 );
        leaf3.setNext( tmp );
        
        return newleaf;
    }
    
    public void removeNode(EventQueue eventqueue, VLinkedNode _toremove) {
        VLeafNode toremove = (VLeafNode) _toremove;
        VInternalNode parent = toremove.getParent();
        
        // Unlink Double-Linked List Structure
        if ( toremove.getPrev()==null ) {
            toremove.setNext(null);
        } else {
            toremove.getPrev().setNext( toremove.getNext() );
        }
        
        // Determine which branch we're keeping, also update v1/v2 nodes
        VNode tosave;
        if ( parent.getLeft()==toremove ) {
            tosave = parent.getRight();
            
            // Update v2 node
            VInternalNode ces = getPredecessor( parent );
            ces.v2 = parent.v2;
        } else if ( parent.getRight()==toremove ) {
            tosave = parent.getLeft();
            
            // Update v2 node
            VInternalNode ces = getSuccessor( parent );
            ces.v1 = parent.v1;
        } else {
            throw new RuntimeException("Neither child matched suggested parent - linking error");
        }
        
        // Re-link branch so that parent isn't involved anymore
        if ( parent.getParent()==null ) {
            throw new RuntimeException("Parent is null - error; parent=#" + parent.id);
        } else if ( parent.getParent().getLeft()==parent ) {
            parent.getParent().setLeft( tosave );
        } else if ( parent.getParent().getRight()==parent ) {
            parent.getParent().setRight( tosave );
        } else {
            throw new RuntimeException("Neither child matched suggested parent's parent - linking error");
        }
    }
    
    private VInternalNode getSuccessor(VInternalNode x){
        VInternalNode y = x.getParent();
        while (( y!=null )&&( x==y.getRight() )) {
            x = y;
            y = y.getParent();
        }
        return y;
    }
    
    private VInternalNode getPredecessor(VInternalNode x){
        VInternalNode y = x.getParent();
        while (( y!=null )&&( x==y.getLeft() )) {
            x = y;
            y = y.getParent();
        }
        return y;
    }
    
    public VLinkedNode getNodeAboveSiteEvent( double siteevent_x , double sweepline ) {
        if ( rootnode==null ) {
            return null;
        } else if ( rootnode.isLeafNode() ) {
            return (VLeafNode)rootnode;
        } else {
            VInternalNode internalnode = (VInternalNode) rootnode;
            do {
                VSiteEvent v1 = internalnode.v1;
                VSiteEvent v2 = internalnode.v2;
                
                // Calculate a, b and c of the parabola
                v1.calcParabolaConstants(sweepline);
                v2.calcParabolaConstants(sweepline);
                
                // Determine where two parabola meet
                double intersects[] = VoronoiShared.solveQuadratic(v2.a-v1.a, v2.b-v1.b, v2.c-v1.c);
                
                // Determine whether to go left or right
                VNode currnode;
                if ( siteevent_x<=intersects[0] ) {
                    currnode = internalnode.getLeft();
                } else {
                    currnode = internalnode.getRight();
                }
                
                // Determine if at a leaf yet
                if ( currnode.isLeafNode() ) {
                    return (VLeafNode) currnode;
                }
                
                // Otherwise, continue
                internalnode = (VInternalNode)currnode;
            } while ( true );
        }
    }
    
    // Function only used by test functions
    public VLinkedNode getHeadNode() {
        if ( rootnode==null ) {
            return null;
        } else if ( rootnode.isLeafNode() ) {
            return (VLinkedNode) rootnode;
        } else {
            VInternalNode internalnode = (VInternalNode) rootnode;
            VNode currnode;
            do {
                currnode = internalnode.getLeft();
                
                // Determine if at a leaf yet
                if ( currnode.isLeafNode() ) {
                    break;
                }
                
                // Otherwise, continue
                internalnode = (VInternalNode)currnode;
            } while ( true );
            
            // Check leaf is the very left
            VLeafNode leafnode = (VLeafNode) currnode;
            if ( leafnode.getPrev()!=null ) {
                throw new RuntimeException("Leftmost element of tree is not leftmost element of doubly-linked list - linking error");
            }
            
            // Return value if correct
            return leafnode;
        }
    }
    
    /* ***************************************************** */
    // Debug toString() Method
    
    public String toString() {
        return "| " + strDoublyLinkedList(-1) + "\n* " + strTreeStructure(rootnode, 1);
    }
    public String strDoublyLinkedList(int sweepline) {
        VLeafNode leafnode = (VLeafNode) getHeadNode();
        if ( leafnode==null ) {
            return "Doubly-linked list is empty";
        } else {
            StringBuffer buffer = new StringBuffer();
            boolean isfirst = true;
            do {
                if ( isfirst ) {
                    isfirst = false;
                } else {
                    buffer.append(" ");
                    if ( sweepline>0 ) {
                        VSiteEvent v1 = leafnode.getPrev().siteevent;
                        VSiteEvent v2 = leafnode.siteevent;
                        
                        v1.calcParabolaConstants(sweepline);
                        v2.calcParabolaConstants(sweepline);
                        
                        // Determine where two parabola meet
                        double intersects[] = VoronoiShared.solveQuadratic(v2.a-v1.a, v2.b-v1.b, v2.c-v1.c);
                        buffer.append("[" + ((int)intersects[0]) + "]");
                    }
                    buffer.append("-> ");
                }
                buffer.append("Leaf (" + leafnode.siteevent.getX() + "," + leafnode.siteevent.getY() + ") #" + leafnode.id + "/" + leafnode.siteevent.getID());
            } while ( (leafnode=(VLeafNode)leafnode.getNext())!=null );
            return buffer.toString();
        }
    }
    private String strTreeStructure(VNode node, int depth) {
        if ( node==null ) {
            return "Tree is empty (null root)";
        } else if ( node instanceof VLeafNode ) {
            VLeafNode leafnode = (VLeafNode) node;
            return "Leaf #" + leafnode.id + "/" + leafnode.siteevent.getID() + " (" + leafnode.siteevent.getX() + "," + leafnode.siteevent.getY() + ") (parent=" + (leafnode.parent==null?"null":leafnode.parent.id) + ",prev=" + (leafnode.getPrev()==null?"null":((VLeafNode)leafnode.getPrev()).id+"/"+leafnode.getPrev().siteevent.getID()) + ",next=" + (leafnode.getNext()==null?"null":((VLeafNode)leafnode.getNext()).id+"/"+leafnode.getNext().siteevent.getID()) + ")";
        } else if ( node instanceof VInternalNode ) {
            VInternalNode internalnode = (VInternalNode) node;
            return "Node #" + internalnode.id + "(v1=" + internalnode.v1.getID() + ",v2=" + internalnode.v2.getID() + ") (parent=" + (internalnode.parent==null?"null":internalnode.parent.id) + "):\n"
                    + printGap(depth) + "* Left " + strTreeStructure(internalnode.getLeft(), depth+1) + "\n"
                    + printGap(depth) + "* Right " + strTreeStructure(internalnode.getRight(), depth+1);
        } else {
            throw new RuntimeException("Unknown node type; " + node.getClass().getName());
        }
    }
    private String printGap(int gap) {
        String tmp = "";
        for ( int x=0 ; x<gap ; x++ ) {
            tmp += "  ";
        }
        return tmp;
    }
    
    /* ***************************************************** */
}
