package geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.boundaryproblem;

import geogebra.common.kernel.discrete.signalprocessor.voronoi.VPoint;
import geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.AbstractRepresentation;
import geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.boundaryproblem.voronoicell.VVoronoiCell;
import geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.voronoicell.VoronoiCellRepresentation;
import geogebra.common.kernel.discrete.signalprocessor.voronoi.statusstructure.VLinkedNode;

import java.util.Arrays;
import java.util.Collection;

public class BoundaryProblemRepresentation extends AbstractRepresentation {
    
    /* ***************************************************** */
    // Static Variables
    
    public static double MIN_ANGLE_TO_ALLOW = 0.0 / 180.0 * Math.PI;
    public static int VORONOICELLAREA_CUTOFF = 8000;
    
    /* ***************************************************** */
    // Variables
    
    private int vertexnumber;
    private Collection<VPoint> vertexpoints;
    private final VoronoiCellRepresentation voronoirepresentation = new VoronoiCellRepresentation();
    
    /* ***************************************************** */
    // Constructor
    
    public BoundaryProblemRepresentation() {
        // do nothing
    }
    
    /* ***************************************************** */
    // Create Point
    
    @Override
	public VPoint createPoint(double x, double y) {
        return new VVoronoiCell(x, y);
    }
    
    /* ***************************************************** */
    // Data/Representation Interface Method
    
    // Executed before the algorithm begins to process (can be used to
    //   initialise any data structures required)
    public void beginAlgorithm(Collection<VPoint> points) {
        // Store list of points
        vertexpoints = points;
        
        // Reset each VVertex
        for ( VPoint point : points ) {
            VVertex vertex = (VVertex) point;
            vertex.clearConnectedVertexs();
        }
        
        // Reset the vertex number back to 1
        vertexnumber = 1;
        
        // Reset voronoi representation (always reset - regardless of USE_VORONOIRATIO)
        voronoirepresentation.beginAlgorithm(points);
    }
    
    // Called to record that a vertex has been found
    public void siteEvent( VLinkedNode n1 , VLinkedNode n2 , VLinkedNode n3 ) {
        // Call for voronoi representation
        if ( VORONOICELLAREA_CUTOFF>0 ) voronoirepresentation.siteEvent(n1, n2, n3);
    }
    public void circleEvent( VLinkedNode n1 , VLinkedNode n2 , VLinkedNode n3 , int circle_x , int circle_y ) {
        // Call for voronoi representation
        if ( VORONOICELLAREA_CUTOFF>0 ) voronoirepresentation.circleEvent(n1, n2, n3, circle_x, circle_y);
        
        // Do calculations for this representation
        VVertex g1 = (VVertex) n1.siteevent.getPoint();
        VVertex g2 = (VVertex) n2.siteevent.getPoint();
        VVertex g3 = (VVertex) n3.siteevent.getPoint();
        
        // If we're not using the voronoi representation, then do remove
        //  triangles if their angle is below MIN_ANGLE_TO_ALLOW
        if ( MIN_ANGLE_TO_ALLOW>0 ) {
            // Determine distances between points
            double[] distances = new double[3];
            distances[0] = g1.distanceTo(g2);
            distances[1] = g2.distanceTo(g3);
            distances[2] = g3.distanceTo(g1);
            Arrays.sort( distances );
            
            // Consider the angle formed by the points
            double a = distances[0];
            double b = distances[1];
            double c = distances[2];
            double angle = Math.acos( (b*b + c*c - a*a) / (2*b*c) );
            
            // If angle is less than a certain amount, then don't add triangle
            if ( angle<MIN_ANGLE_TO_ALLOW ) {
                return;
            }
        }
        
        // Create Vertex between triangular vertex between points (clockwise direction)
        g1.addConnectedVertex( new VHalfEdge( vertexnumber , g2 ) );
        g2.addConnectedVertex( new VHalfEdge( vertexnumber , g3 ) );
        g3.addConnectedVertex( new VHalfEdge( vertexnumber , g1 ) );
        
        // Increment the vertex number
        vertexnumber++;
    }
    
    // Called when the algorithm has finished processing
    public void endAlgorithm(Collection<VPoint> points, double lastsweeplineposition, VLinkedNode headnode) {
        // Call for voronoi representation
        if ( VORONOICELLAREA_CUTOFF>0 ) voronoirepresentation.endAlgorithm(points, lastsweeplineposition, headnode);
        
        // Determine all areas
        /*TreeMap<Integer,Integer> areas = new TreeMap();
        for ( VPoint point : vertexpoints ) {
            VVoronoiCell voronoicell = (VVoronoiCell) point;
            int area = voronoicell.getAreaOfCell();
            Integer areaobj = new Integer(area);
            if ( area!=-1 && areas.containsKey(areaobj)==false ) {
                areas.put( areaobj , areaobj );
            }
        }
         
        // Print out all areas
        Collection<Integer> values = areas.values();
        for ( Integer value : values ) {
            System.out.println(value);
        }*/
        
        // Reduce the vertex to form boundaries
        for ( VPoint point : vertexpoints ) {
            VVertex vertex = (VVertex) point;
            if ( vertex==null || vertex.getConnectedVertexs()==null ) continue;
            for ( VHalfEdge connectededge : vertex.getConnectedVertexs() ) {
                // Get the previous edge
                VVertex prevvertex = getPreviousVertex( connectededge.vertexnumber , connectededge.vertex , vertex );
                if ( prevvertex==null ) {
                    continue;
                }
                
                // Determine if the previous edge is the very next edge for
                // any other vertex - if so remove that edge (i.e. (prevvertex, vertex)
                // and (vertex, prevvertex)) for both vertexs.
                for ( VHalfEdge connectededge2 : vertex.getConnectedVertexs() ) {
                    // Determine if the edge matches the edge of the other triangle
                    if ( connectededge2.vertex==prevvertex ) {
                        if ( VORONOICELLAREA_CUTOFF>0 ) {
                            VVoronoiCell voronoicell1 = (VVoronoiCell) vertex;
                            VVoronoiCell voronoicell2 = (VVoronoiCell) prevvertex;
                            
                            int area1 = voronoicell1.getAreaOfCell();
                            int area2 = voronoicell2.getAreaOfCell();
                            
                            //if ( area1>8000 && area2>8000 ) {
                            //    break;
                            //}
                            if (( area1>80000 && area2>80000 )||( area1<0 && area2<0 )) {
                                // remove edge
                            } else if ( area1>VORONOICELLAREA_CUTOFF && area2<=VORONOICELLAREA_CUTOFF ) {
                                break;
                            } else if ( area2>VORONOICELLAREA_CUTOFF && area1<=VORONOICELLAREA_CUTOFF ) {
                                break;
                            }
                        }
                        
                        // Remove the edge going in one direction
                        connectededge2.isdeleted = true;
                        
                        // Remove edge doing in the _other_ direction
                        VHalfEdge tmpotheredge = prevvertex.getNextConnectedEdge(connectededge.vertexnumber);
                        tmpotheredge.isdeleted = true;
                        
                        // Break out of the loop - no other edges will match
                        break;
                    }
                }
            }
        }
        
        // Clean up if using voronoi ratio (remove disconnected triangles)
        if ( VORONOICELLAREA_CUTOFF>0 ) {
            boolean haschanged;
            do {
                haschanged = false;
                for ( VPoint point : vertexpoints ) {
                    VVertex vertex = (VVertex) point;
                    if ( vertex==null || vertex.getConnectedVertexs()==null ) continue;
                    for ( VHalfEdge connectededge : vertex.getConnectedVertexs() ) {
                        // If already deleted, continue
                        if ( connectededge.isdeleted ) {
                            continue;
                        }
                        
                        // Check next vertex
                        if ( checkHasConnections(connectededge.vertex, vertex)==false ) {
                            // Set that a change has occurred
                            haschanged = true;
                                    
                            // Remove the edge going in one direction
                            connectededge.isdeleted = true;
                            
                            // Remove edge doing in the _other_ direction
                            VHalfEdge tmpotheredge = connectededge.vertex.getNextConnectedEdge(vertex);
                            tmpotheredge.isdeleted = true;
                        }
                    }
                }
            } while ( haschanged );
        }
    }
    
    private static boolean checkHasConnections( VVertex vertex , VVertex ignore ) {
        for ( VHalfEdge halfedge : vertex.getConnectedVertexs() ) {
            if ( halfedge.isdeleted==false && halfedge.vertex!=ignore ) {
                return true;
            }
        }
        return false;
    }
    
    private static VVertex getPreviousVertex( int vertexnumber , VVertex currpoint , VVertex point ) {
        VVertex prevpoint = null;
        while ( currpoint!=point ) {
            prevpoint = currpoint;
            currpoint = currpoint.getNextConnectedVertex(vertexnumber);
            if ( currpoint==null ) {
                // Disconnected vertex, return null - no previous value
                return null;
                // throw new RuntimeException("Unexpected null value - non-connected vertex");
            }
        }
        return prevpoint;
    }
    
    
    /* ***************************************************** */
    // Paint Method
    
    /*public void paint(Graphics2D g) {
        // Paint for voronoi representation first
        //if ( USE_VORONOIRATIO ) voronoirepresentation.paint(g);
        
        // Paint for this method
        for ( VPoint point : vertexpoints ) {
            VVertex vertex = (VVertex) point;
            if ( vertex==null || vertex.getConnectedVertexs()==null ) continue;
            for ( VHalfEdge edge : vertex.getConnectedVertexs() ) {
                // Continue if the edge is deleted
                if ( edge.isdeleted ) {
                    continue;
                }
                
                // Otherwise, draw the line
                VVertex nextvertex = edge.vertex;
                g.drawLine( (int)vertex.x , (int)vertex.y , (int)nextvertex.x , (int)nextvertex.y );
            }
            // g.drawString(vertex.getConnectedVertexString(), vertex.x+6, vertex.y);
        }
    }
    
    /* ***************************************************** */
}
