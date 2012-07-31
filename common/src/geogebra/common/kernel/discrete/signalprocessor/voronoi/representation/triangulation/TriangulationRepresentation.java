package geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.triangulation;

import geogebra.common.kernel.discrete.signalprocessor.voronoi.VPoint;
import geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.AbstractRepresentation;
import geogebra.common.kernel.discrete.signalprocessor.voronoi.statusstructure.VLinkedNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class TriangulationRepresentation extends AbstractRepresentation {
    
    /* ***************************************************** */
    // Constants
    
    public static final int OUTER_VERTEXNUMBER = -1;
    
    /* ***************************************************** */
    // Static Variables
    
    public static boolean SHOW_INTERNAL_TRIANGLES  = false;
    public static boolean SHOW_EDGE_LENGTHS        = false;
    public static boolean SHOW_DEBUG_INFO          = false;
    public static int     MAX_EDGES_TO_REMOVE     = -1;
    
    public static int     MODE_REDUCE_OUTER_BOUNDARIES    = 1;
    public static int     MODE_GETSTATS_EXCLUDINGMSTSTATS = 2;
    public static int     MODE_DETERMINE_MINSPANNINGTREE  = 3;
    public static int     MODE_DETERMINE_CLUSTERS         = 4;
    
    /* ***************************************************** */
    // Variables
    
    private int mode;
    
    private int vertexnumber;
    public Collection<VPoint> vertexpoints;
    
    private CalcCutOff calccutoff = null;
    private int length_cutoff = -1;
    
    private double gradient_diff_before_cluster_cutoff = 1.2;
    private final ArrayList<VCluster> clusters = new ArrayList<VCluster>();
    
    private boolean update_statistics;
    private int max_length = -1;
    private int min_length = -1;
    private int max_length_of_smallesttriangleedge = -1;
    private int max_length_from_minimumspanningtree = -1;
    
    /* ***************************************************** */
    // Constructor
    
    public TriangulationRepresentation() {
        setReduceOuterBoundariesMode();
    }
    public TriangulationRepresentation(int length_cutoff) {
        this(); setIntegerLengthCutoff( length_cutoff );
    }
    public TriangulationRepresentation(CalcCutOff calccutoff) {
        this(); setCalcCutOff( calccutoff );
    }
    
    /* ***************************************************** */
    // Modes
    
    public int getMode() { return mode; }
    
    public void setReduceOuterBoundariesMode() {
        update_statistics = true;
        mode = MODE_REDUCE_OUTER_BOUNDARIES;
    }

    public void setGetStatsMode() {
        update_statistics = true;
        mode = MODE_DETERMINE_MINSPANNINGTREE;
    }
    
    public void setGetStatsExcludingMSTStatsMode() {
        update_statistics = true;
        mode = MODE_GETSTATS_EXCLUDINGMSTSTATS;
    }
    
    public void setDetermineMinSpanningTreeMode() {
        update_statistics = false;
        mode = MODE_DETERMINE_MINSPANNINGTREE;
    }
    
    public void setDetermineClustersMode() {
        update_statistics = false;
        mode = MODE_DETERMINE_CLUSTERS;
    }
    
    /* ***************************************************** */
    // Getters and Setters
    
    public int calculateLengthCutoff() {
        if ( calccutoff!=null ) {
            return calccutoff.calculateCutOff(this);
        } 
        return length_cutoff;
    }
    
    public int getIntegerLengthCutoff() {
        if ( calccutoff!=null ) {
            throw new RuntimeException("CalcCutOff object registered - length_cutoff variable is ignored");
        }
        return length_cutoff;
    }
    
    public void setIntegerLengthCutoff(int _length_cutoff) {
        if ( calccutoff!=null ) {
            throw new RuntimeException("CalcCutOff object registered - length_cutoff variable is ignored");
        }
        this.length_cutoff = _length_cutoff;
    }
    
    public CalcCutOff getCalcCutOff() { 
    	return calccutoff; 
    }
    
    public void setCalcCutOff( CalcCutOff _calccutoff ) {
        this.calccutoff = _calccutoff;
        this.length_cutoff = -1;
    }
    
    public int getMaxLength() {
        if ( update_statistics ) {
            return max_length;
        }
        throw new RuntimeException("Calculation of statistics are currently disabled");
    }
    
    public int getMinLength() {
        if ( update_statistics ) {
            return min_length;
        } 
        throw new RuntimeException("Calculation of statistics are currently disabled");
    }
    
    public int getMaxLengthOfSmallestTriangleEdge() {
        if ( update_statistics ) {
            return max_length_of_smallesttriangleedge;
        } 
        throw new RuntimeException("Calculation of statistics are currently disabled");
    }
    
    public int getMaxLengthOfMinimumSpanningTree() {
        if ( update_statistics ) {
            return max_length_from_minimumspanningtree;
        } 
        throw new RuntimeException("Calculation of statistics are currently disabled");
    }
    
    /* ***************************************************** */
    // Create Point
    
    @Override
	public VPoint createPoint(double x, double y) {
        return new VVertex(x, y);
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
            vertex.clearEdges();
        }
        
        // Reset length values
        if ( update_statistics ) {
            max_length = -1;
            min_length = -1;
            max_length_of_smallesttriangleedge = -1;
            max_length_from_minimumspanningtree = -1;
        }
        
        // Clear clusters list
        clusters.clear();
        
        // Reset the vertex number back to 1
        vertexnumber = 1;
    }
    
    // Called to record that a vertex has been found
    public void siteEvent( VLinkedNode n1 , VLinkedNode n2 , VLinkedNode n3 ) { }
    public void circleEvent( VLinkedNode n1 , VLinkedNode n2 , VLinkedNode n3 , int circle_x , int circle_y ) {
        // Do calculations for this representation
        VVertex v1 = (VVertex) n1.siteevent.getPoint();
        VVertex v2 = (VVertex) n2.siteevent.getPoint();
        VVertex v3 = (VVertex) n3.siteevent.getPoint();
        
        // Create Vertex between triangular vertex between points (clockwise direction)
        VHalfEdge e1, e2, e3;
        v1.addEdge( e1 = new VHalfEdge( vertexnumber , v1 ) );
        v2.addEdge( e2 = new VHalfEdge( vertexnumber , v2 ) );
        v3.addEdge( e3 = new VHalfEdge( vertexnumber , v3 ) );
        
        // Connect half edges
        e1.next = e2;
        e2.next = e3;
        e3.next = e1;
        
        // Consider side lengths and update side length values
        if ( update_statistics ) {
            int[] lengths = new int[]{ e1.getLength() , e2.getLength() , e3.getLength() };
            Arrays.sort(lengths);
            if ( lengths[2] > max_length )
                max_length = lengths[2];
            if ( min_length < 0 || lengths[0] < min_length )
                min_length = lengths[0];
            if ( lengths[0] > max_length_of_smallesttriangleedge )
                max_length_of_smallesttriangleedge = lengths[0];
        }
        
        // Increment the vertex number
        vertexnumber++;
    }
    
    // Called when the algorithm has finished processing
    public void endAlgorithm(Collection<VPoint> points, double lastsweeplineposition, VLinkedNode headnode) {
        VHalfEdge outeredge = createOuterEdge();
        if ( outeredge!=null ) {
            // Calculate MST before removing edges, as this would affect the result returned
            if ( mode==MODE_GETSTATS_EXCLUDINGMSTSTATS ) {
                // Don't calculate minimum spanning tree
            } else if ( mode==MODE_DETERMINE_CLUSTERS ) {
                max_length_from_minimumspanningtree =
                        SharedMinimumSpanningTree.determineMSTUsingPrimsAlgorithm(outeredge.vertex, gradient_diff_before_cluster_cutoff, clusters);
            } else {
                max_length_from_minimumspanningtree =
                        SharedMinimumSpanningTree.determineMSTUsingPrimsAlgorithm(outeredge.vertex);
            }
            
            // Remove edges, but only if we're not showing the MST
            if ( mode==MODE_REDUCE_OUTER_BOUNDARIES ) {
                int length_cutoff = calculateLengthCutoff();
                SharedEdgeRemoval.removeEdgesInOrderFromOuterBoundary(outeredge, length_cutoff);
            }
        }
    }
    
    private VHalfEdge createOuterEdge() {
        VVertex currvertex  = null;
        VVertex firstvertex = null;
        VHalfEdge firstedge  = null;
        
        // Find an outer edge
        //System.out.println("Finding outer edge");
        outerloop: {
            for ( VPoint point : vertexpoints ) {
                VVertex vertex = (VVertex) point;
                //System.out.println("  - Vertex " + vertex.id);
                
                // Check the vertex has edges
                if ( vertex.hasEdges()==false ) continue;
                
                // Check if the edge is an outer edge
                for ( VHalfEdge edge : vertex.getEdges() ) {
                    // Continue until we find a non-double edge (i.e. an outer edge)
                    if ( edge.getConnectedVertex().isConnectedTo(vertex) ) {
                        //System.out.println("    + Vertex " + edge.getConnectedVertex().id + " connected");
                        continue;
                    }
                    
                    // Otherwise, we're found an outer edge
                    firstvertex = vertex;
                    currvertex  = edge.getConnectedVertex();
                    currvertex.addEdge( firstedge = new VHalfEdge(OUTER_VERTEXNUMBER,currvertex) );
                    //System.out.println("    - Vertex " + edge.getConnectedVertex().id + " NOT connected, adding edge to Vertex " + currvertex.id);
                    break outerloop;
                }
            }
        }
        
        // Return as there are probably less than 3 points, or this
        //  method has already been called!
        if ( currvertex==null ) {
            //throw new RuntimeException("Outer edge not found");
            return null;
        }
        
        // Form an outer edge around the shape
        //System.out.println("Forming outer edge");
        VVertex nextvertex;
        VHalfEdge prevedge = firstedge;
        do {
            // Find next vertex
            nextvertex = null;
            for ( VHalfEdge edge : currvertex.getEdges() ) {
                // Continue until we find a non-double edge (i.e. an outer edge)
                if ( edge.getConnectedVertex().isConnectedTo(currvertex) ) continue;
                
                // Otherwise, we're found an outer edge
                nextvertex = edge.getConnectedVertex();
                break;
            }
            //System.out.println("  - Found next Vertex " + nextvertex.id);
            
            // Check next vertex was found
            if ( nextvertex==null ) {
                throw new RuntimeException("Edge's in invalid state - didn't find next vertex");
            }
            
            // Connect to next vertex
            //System.out.println("  - Added edge to " + nextvertex.id + ", connected Edge from " + prevedge.vertex.id + " to Edge from " + nextvertex.id);
            nextvertex.addEdge( prevedge = new VHalfEdge(OUTER_VERTEXNUMBER,nextvertex, prevedge) );
        } while ( (currvertex=nextvertex)!=firstvertex );
        
        // Connect to edge to final edge
        firstedge.next = prevedge;
        
        // Return first edge create of the outer edge
        //  (doesn't really matter which edge is returned)
        return firstedge;
    }
    
    /* ***************************************************** */
    // Get outer boundary
    
    /**
     * Note: the first point is also readded to the list as the last point
     *  so that a connected set of points is returned for a shape
     */
    public ArrayList<VPoint> getPointsFormingOutterBoundary() {
        // Find an outer edge
        VHalfEdge outeredge = findOuterEdge();
        
        // Check is not null and next is not null
        if ( outeredge==null || outeredge.next==null ) {
            return null;
        }
        
        // Initialise variables
        VHalfEdge curredge = outeredge;
        ArrayList<VPoint> pointlist = new ArrayList<VPoint>();
        do {
            pointlist.add( curredge.vertex );
        } while ( (curredge=curredge.next).next!=null && curredge!=outeredge );
        
        // Add the first/final point as well to close the shape
        if ( curredge==outeredge ) {
            pointlist.add( curredge.vertex );
        }
        
        // Return the point list
        return pointlist;
    }
    
    /* ***************************************************** */
    // Paint Method
    
    /*public void paint(Graphics2D g) {
        if ( mode==MODE_REDUCE_OUTER_BOUNDARIES && SHOW_INTERNAL_TRIANGLES==false ) {
            // Find an outer edge
            VHalfEdge outeredge = findOuterEdge();
            
            // Check is not null and next is not null
            if ( outeredge==null || outeredge.next==null ) {
                return;
            }
            
            // Initialise variables
            VHalfEdge curredge = outeredge;
            do {
                g.drawLine( (int)curredge.getX() , (int)curredge.getY() , (int)curredge.next.getX() , (int)curredge.next.getY() );
                
                // Draw edge lengths
                if ( SHOW_EDGE_LENGTHS ) {
                    VVertex vertex = curredge.vertex;
                    VVertex vertex2 = curredge.getConnectedVertex();
                    g.drawString( Integer.toString(curredge.getLength()) , (int)(vertex.x + (vertex2.x-vertex.x)*0.5 + 3.0) , (int)(vertex.y + (vertex2.y-vertex.y)*0.5 - 3.0));
                }
                
                // Draw caption string for node
                if ( SHOW_DEBUG_INFO ) {
                    VVertex vertex = curredge.vertex;
                    g.drawString(Integer.toString(vertex.id), (int)vertex.x+6, (int)vertex.y);
                }
            } while ( (curredge=curredge.next).next!=null && curredge!=outeredge );
        } else {
            // Paint for this method
            for ( VPoint point : vertexpoints ) {
                VVertex vertex = (VVertex) point;
                
                // Check the vertex has edges
                if ( vertex.hasEdges()==false ) {
                    continue;
                }
                
                // Paint each of those edges
                for ( VHalfEdge edge : vertex.getEdges() ) {
                    // Simple addition to show MST
                    if ( mode!=MODE_REDUCE_OUTER_BOUNDARIES && edge.shownonminimumspanningtree==false ) continue;
                    
                    // Draw the line
                    //if ( edge.next==null) continue;
                    VVertex vertex2 = edge.next.vertex;
                    //if ( vertex2==null ) return;
                    g.drawLine( (int)vertex.x , (int)vertex.y , (int)vertex2.x , (int)vertex2.y );
                    if ( SHOW_EDGE_LENGTHS ) {
                        g.drawString( Integer.toString(edge.getLength()) , (int)(vertex.x + (vertex2.x-vertex.x)*0.5 + 3.0) , (int)(vertex.y + (vertex2.y-vertex.y)*0.5 - 3.0));
                    }
                    //g.fillOval( (int)(vertex.x + (vertex2.x-vertex.x)*0.85 - 3.0) , (int)(vertex.y + (vertex2.y-vertex.y)*0.85 - 3.0) , 6 , 6 );
                }
                
                // Draw caption string for node
                if ( SHOW_DEBUG_INFO ) {
                    g.drawString(Integer.toString(vertex.id), (int)vertex.x+6, (int)vertex.y);
                }
            }
        }
    }
    */
    public VHalfEdge findOuterEdge() {
        for ( VPoint point : vertexpoints ) {
            VVertex vertex = (VVertex) point;
            
            // Check the vertex has edges
            if ( vertex.hasEdges()==false ) {
                continue;
            }
            
            // Paint each of those edges
            for ( VHalfEdge edge : vertex.getEdges() ) {
                if ( edge.isOuterEdge() ) {
                    return edge;
                }
            }
        }
        return null;
    }
    
    /* ***************************************************** */
    
    abstract static public class CalcCutOff {
        
        abstract public int calculateCutOff(TriangulationRepresentation representation);
        
    }
    
    /* ***************************************************** */
}
