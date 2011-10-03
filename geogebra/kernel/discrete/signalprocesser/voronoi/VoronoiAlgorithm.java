package geogebra.kernel.discrete.signalprocesser.voronoi;
import geogebra.kernel.discrete.signalprocesser.voronoi.eventqueue.EventQueue;
import geogebra.kernel.discrete.signalprocesser.voronoi.eventqueue.VCircleEvent;
import geogebra.kernel.discrete.signalprocesser.voronoi.eventqueue.VEvent;
import geogebra.kernel.discrete.signalprocesser.voronoi.eventqueue.VSiteEvent;
import geogebra.kernel.discrete.signalprocesser.voronoi.representation.RepresentationInterface;
import geogebra.kernel.discrete.signalprocesser.voronoi.statusstructure.AbstractStatusStructure;
import geogebra.kernel.discrete.signalprocesser.voronoi.statusstructure.VLinkedNode;
import geogebra.main.Application;

import java.awt.Graphics2D;
import java.util.Collection;

public class VoronoiAlgorithm {
    
    /* ********************************************************* */
    // Test Main Function
    
    public static void main(String[] args) {
        VoronoiTest.main(args);
    }
    
    private VoronoiAlgorithm() { }
    
    /* ********************************************************* */
    // Generation Algorithm
    
    public static void generateVoronoi(RepresentationInterface datainterface, Collection<VPoint> points) {
        generateVoronoi(datainterface, points, null, null, -1);
    }
    protected static void generateVoronoi(RepresentationInterface datainterface, Collection<VPoint> points, Graphics2D g, VPoint attentiontopoint, int attentiontopos) {
        // Initialise event queue with all site events
        EventQueue eventqueue = new EventQueue();
        VSiteEvent attentiontositeevent = null;
        for ( VPoint point : points ) {
            VSiteEvent newsiteevent = new VSiteEvent( point );
            if ( point==attentiontopoint ) {
                attentiontositeevent = newsiteevent;
            }
            eventqueue.addEvent( newsiteevent );
        }
        
        // Initialise the data interface
        datainterface.beginAlgorithm(points);

        // Reset Debug/Userfriendly ID's debug to newly created nodes/events
        VEvent.uniqueid = 1;
        geogebra.kernel.discrete.signalprocesser.voronoi.statusstructure.binarysearchtreeimpl.BSTStatusStructure.uniqueid = 1;
        
        // Initialise an empty status structure
        AbstractStatusStructure statusstructure = AbstractStatusStructure.createDefaultStatusStructure();
        
        // While the queue is not empty
        VEvent event = null;
        boolean printcalled = false;
        while (!( eventqueue.isEventQueueEmpty() )) {
            // Remove the event with the largest y coord
            event = eventqueue.getAndRemoveFirstEvent();
            
            // Debug Code (note: can just set g to null to parse over)
            if ( g!=null && attentiontositeevent==null && attentiontopos>=0 && printcalled==false ) {
                if ( event!=null && event.getY()>=attentiontopos ) {
                    printcalled = true;
                    statusstructure.print(g, null, attentiontopos);
                    
                    // Close the data interface and return
                    datainterface.endAlgorithm(points, event.getY(), statusstructure.getHeadNode());
                    return;
                }
            }
            
            // If site event, otherwise circle event
            if ( event.isSiteEvent() ) {
                VSiteEvent siteevent = (VSiteEvent) event;
                
                // Debug Code (note: can just set g to null to parse over)
                if ( g!=null && siteevent==attentiontositeevent ) {
                    statusstructure.print(g, siteevent, (int)siteevent.getY());
                    
                    // Close the data interface and return
                    datainterface.endAlgorithm(points, event.getY(), statusstructure.getHeadNode());
                    return;
                }
                
                // If status structure is empty, insert so that the status
                //  structure consists of a single leaf storing the site event
                if ( statusstructure.isStatusStructureEmpty() ) {
                    // Set the root node
                    statusstructure.setRootNode( siteevent );
                    
                    // Also: check for the degrading case (equal y values between
                    //  first and second nodes of queue)
                    VEvent nextevent = eventqueue.getFirstEvent();
                    if ( nextevent!=null && event.getY()==nextevent.getY() ) {
                        // Increment original event by small amount to fix error
                        Application.debug("Please note: easy fix done to prevent degrading case");
                        siteevent.getPoint().y-=0.00000001;
                        
                        /*// Move remove entirely from queue and re-add - changing
                        //  something which the Comparator dependents on results
                        //  in unexpected behaviour
                        nextevent = eventqueue.getAndRemoveFirstEvent();
                        ((VSiteEvent)nextevent).getPoint().y++;
                        eventqueue.addEvent(nextevent);*/
                    }
                    continue;
                }
                
                // Search the status structure for leaf which represents
                //  the arc directly above the site event
                VLinkedNode leafabove = statusstructure.getNodeAboveSiteEvent( siteevent , siteevent.getY() );
                
                // Delete any circle events
                leafabove.removeCircleEvents(eventqueue);
                
                // Insert the new node
                VLinkedNode newnode = statusstructure.insertNode(leafabove, siteevent);
                VLinkedNode prevnode = newnode.getPrev();
                VLinkedNode nextnode = newnode.getNext();
                
                // Determine circle events
                if ( prevnode!=null ) prevnode.addCircleEvent(eventqueue);
                if ( nextnode!=null ) nextnode.addCircleEvent(eventqueue);
                
                // Record event now that new node has been inserted into the tree
                datainterface.siteEvent(
                        prevnode ,
                        newnode ,
                        nextnode );
            } else if ( event.isCircleEvent() ) {
                VCircleEvent circleevent = (VCircleEvent) event;

                // Get linked nodes
                VLinkedNode currnode = circleevent.leafnode;
                VLinkedNode prevnode = currnode.getPrev();
                VLinkedNode nextnode = currnode.getNext();
                
                // Record event before center node is deleted from the tree
                datainterface.circleEvent(
                        prevnode ,
                        currnode ,
                        nextnode ,
                        (int)circleevent.getX(), (int)circleevent.getCenterY()
                        );
                
                // Remove any circle events, before removing node
                currnode.removeCircleEvents(eventqueue);
                
                // Remove event from structure
                statusstructure.removeNode(eventqueue, currnode);
                
                // Remove Circle Events for prev (pi) and next (pk)
                prevnode.removeCircleEvents(eventqueue);
                nextnode.removeCircleEvents(eventqueue);
                
                // Determine circle events for p1,pi,(pjremoved),pk and pi,(pjremoved),pk,p2
                if ( prevnode!=null ) prevnode.addCircleEvent(eventqueue);
                if ( nextnode!=null ) nextnode.addCircleEvent(eventqueue);
            } else {
                throw new RuntimeException("Unknown event; " + event.getClass().getName());
            }
        }
        
        // Debug Code (note: can just set g to null to parse over)
        if ( g!=null && attentiontositeevent==null && attentiontopos>=0 && printcalled==false ) {
            printcalled = true;
            statusstructure.print(g, null, attentiontopos);
        }
        
        // Close the data interface
        if ( event==null ) {
            datainterface.endAlgorithm(points, -1, statusstructure.getHeadNode());
        } else {
            datainterface.endAlgorithm(points, event.getY(), statusstructure.getHeadNode());
        }
    }
    
    /* ********************************************************* */
}