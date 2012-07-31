package geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.simpletriangulation;

import geogebra.common.kernel.discrete.signalprocessor.voronoi.VPoint;
import geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.AbstractRepresentation;
import geogebra.common.kernel.discrete.signalprocessor.voronoi.statusstructure.VLinkedNode;

import java.util.ArrayList;
import java.util.Collection;

public class SimpleTriangulationRepresentation extends AbstractRepresentation {

    /* ***************************************************** */
    // Variables
    
    public final ArrayList<VTriangle> triangles = new ArrayList<VTriangle>();
    
    /* ***************************************************** */
    // Constructor
    
    public SimpleTriangulationRepresentation() {
        // do nothing
    }

    /* ***************************************************** */
    // Create Point

    @Override
	public VPoint createPoint(double x, double y) {
        return new VPoint(x, y);
    }
    
    /* ***************************************************** */
    // Data/Representation Interface Method
    
    // Executed before the algorithm begins to process (can be used to
    //   initialise any data structures required)
    public void beginAlgorithm(Collection<VPoint> points) {
        // Reset the triangle array list
        triangles.clear();
    }
    
    // Called to record that a vertex has been found
    public void siteEvent( VLinkedNode n1 , VLinkedNode n2 , VLinkedNode n3 ) { }
    public void circleEvent( VLinkedNode n1 , VLinkedNode n2 , VLinkedNode n3 , int circle_x , int circle_y ) {
        VTriangle triangle = new VTriangle(circle_x, circle_y);
        triangle.p1 = n1.siteevent.getPoint();
        triangle.p2 = n2.siteevent.getPoint();
        triangle.p3 = n3.siteevent.getPoint();
        triangles.add( triangle );
    }
    
    // Called when the algorithm has finished processing
    public void endAlgorithm(Collection<VPoint> points, double lastsweeplineposition, VLinkedNode headnode) {
        // do nothing
    }
    
    /* ***************************************************** */    
    // Paint Method
    
    /*public void paint(Graphics2D g) {
        for ( VTriangle triangle : triangles ) {
            g.drawLine( (int)triangle.p1.x , (int)triangle.p1.y , (int)triangle.p2.x , (int)triangle.p2.y );
            g.drawLine( (int)triangle.p2.x , (int)triangle.p2.y , (int)triangle.p3.x , (int)triangle.p3.y );
            g.drawLine( (int)triangle.p3.x , (int)triangle.p3.y , (int)triangle.p1.x , (int)triangle.p1.y );
        }
    }*/
    
    /* ***************************************************** */    
}
