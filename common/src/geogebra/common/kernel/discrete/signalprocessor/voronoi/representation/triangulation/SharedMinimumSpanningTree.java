
package geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.triangulation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;

public class SharedMinimumSpanningTree {
    
    /* ***************************************************** */
    // Comparator for removeEdgesInOrderFromOuterBoundary() method
    
    private static final Comparator<VVertex> VERTEX_COMPARATOR = new Comparator<VVertex>() {
        public int compare(VVertex v1, VVertex v2) {
            if (v1.y < v2.y)      return -1;
            else if (v1.y > v2.y) return 1;
            else if (v1.x < v2.x) return -1;
            else if (v1.x > v2.x) return 1;
            else if (v1 == v2) return 0;
            else {
                throw new RuntimeException("No basis for comparing two apparently identical vertexes - " + v1 + " and " + v2);
            }
        }
    };
    
    private static final Comparator<VHalfEdge> PATH_COMPARATOR = new Comparator<VHalfEdge>() {
        public int compare(VHalfEdge e1, VHalfEdge e2) {
            if      (e1.getLength() < e2.getLength()) return -1;
            else if (e1.getLength() > e2.getLength()) return  1;
            else {
                // Note: duplicates are removed from the TreeMap that uses
                //  this comparator - so we really don't want to edges to
                //  be considered identical (not specified in java documentation)
                if (e1.getY() < e2.getY())      return -1;
                else if (e1.getY() > e2.getY()) return 1;
                else if (e1.getX() < e2.getX()) return -1;
                else if (e1.getX() > e2.getX()) return 1;
                else if (e1 == e2) return 0;
                else {
                    // Consider direction of vector
                    if ( e1.next!=null && e2.next!=null ) {
                        if (e1.next.getY() < e2.next.getY())      return -1;
                        else if (e1.next.getY() > e2.next.getY()) return 1;
                        else if (e1.next.getX() < e2.next.getX()) return -1;
                        else if (e1.next.getX() > e2.next.getX()) return 1;
                        else {
                            throw new RuntimeException("No basis for comparing two apparently identical vectors - " + e1 + " and " + e2);
                        }
                    } else if ( e1.next==null ) {
                        return -1;
                    } else if ( e2.next==null ) {
                        return 1;
                    } else {
                        throw new RuntimeException("No basis for comparing two apparently identical vectors - " + e1 + " and " + e2);
                    }
                }
            }
        }
    };
    
    /* ***************************************************** */
    // Reduce a system from it's outer boundary inward starting from
    //  it's longest length outer boundaries and moving inward
    
    public static int determineMSTUsingPrimsAlgorithm(VVertex startingvertex) {
        VertexList vertexes = new VertexList();
        FuturePathList futurepaths = new FuturePathList();
        int maxpathofminimumspanningtree = -1;
        
        VVertex minimum = startingvertex;
        do {
            // Add minimum to list of vertexes already considered
            vertexes.addVertex(minimum);
            
            // Check no edges point to the newly considered vertex
            Iterator<VHalfEdge> iter = futurepaths.values().iterator();
            while ( iter.hasNext() ) {
                VHalfEdge path = iter.next();
                if ( path.getConnectedVertex()==minimum ) {
                    iter.remove();
                }
            }
            
            // Add the paths from the minimum
            for ( VHalfEdge path : minimum.getEdges() ) {
                if ( vertexes.hasVertexBeenConsidered(path.getConnectedVertex()) ) {
                    continue;
                }
                
                // Otherwise, add the path as a new possibility
                futurepaths.addPath( path );
            }
            
            if ( futurepaths.size()<=0 ) {
                return maxpathofminimumspanningtree;
            }
            
            // Remove the best path
            VHalfEdge nextbestpath = futurepaths.popBestNextPath();
            nextbestpath.shownonminimumspanningtree = true;
            if ( nextbestpath.getLength()>maxpathofminimumspanningtree ) {
                maxpathofminimumspanningtree = nextbestpath.getLength();
            }
            
            // Set next minimum to consider
            minimum = nextbestpath.getConnectedVertex();
        } while ( true );
    }
    
    /* ***************************************************** */
    // The below method completes the Minimum Spanning Tree problem, but
    //  also attempts to form groups of points into "Clusters". To do this
    //  it effectively does the MST problem twice over for the best possible
    //  result (the first parse is down to determine is suitable cutt-off
    //  point for the final/second parse)
    
    public static int determineMSTUsingPrimsAlgorithm(VVertex startingvertex, double gradient_diff_before_cluster_cutoff, ArrayList<VCluster> clusters) {
        VertexList vertexes = new VertexList();
        FuturePathList futurepaths = new FuturePathList();
        TreeMap<Integer,Integer> lengths = new TreeMap<Integer,Integer>();
        int maxpathofminimumspanningtree = -1;
        
        VVertex minimum = startingvertex;
        do {
            // Add minimum to list of vertexes already considered
            vertexes.addVertex(minimum);
            
            // Check no edges point to the newly considered vertex
            Iterator<VHalfEdge> iter = futurepaths.values().iterator();
            while ( iter.hasNext() ) {
                VHalfEdge path = iter.next();
                if ( path.getConnectedVertex()==minimum ) {
                    iter.remove();
                }
            }
            
            // Add the paths from the minimum
            for ( VHalfEdge path : minimum.getEdges() ) {
                if ( vertexes.hasVertexBeenConsidered(path.getConnectedVertex()) ) {
                    continue;
                }
                
                // Otherwise, add the path as a new possibility
                futurepaths.addPath( path );
            }
            
            if ( futurepaths.size()<=0 ) break;
            
            // Remove the best path
            VHalfEdge nextbestpath = futurepaths.popBestNextPath();
            if ( nextbestpath.getLength()>maxpathofminimumspanningtree ) {
                maxpathofminimumspanningtree = nextbestpath.getLength();
            }
            
            // Add length of edge to lengths
            Integer length = new Integer(nextbestpath.getLength());
            lengths.put( length , length );
            
            // Set next minimum to consider
            minimum = nextbestpath.getConnectedVertex();
        } while ( true );
        
        // Detemine cluster cut-off
        //int clustercutoff = determineClusterCutOffByDifference(lengths, gradient_diff_before_cluster_cutoff);
        int clustercutoff = determineClusterCutOffByGradient(lengths, 5);
        
        // Do minimum spanning tree again, this time forming clusters
        vertexes.clear();
        futurepaths.clear();
        minimum = startingvertex;
        VCluster currentcluster = new VCluster();
        clusters.add( currentcluster );
        do {
            // Add minimum to current cluster
            currentcluster.add(minimum);
            
            // Add minimum to list of vertexes already considered
            vertexes.addVertex(minimum);
            
            // Check no edges point to the newly considered vertex
            Iterator<VHalfEdge> iter = futurepaths.values().iterator();
            while ( iter.hasNext() ) {
                VHalfEdge path = iter.next();
                if ( path.getConnectedVertex()==minimum ) {
                    iter.remove();
                }
            }
            
            // Add the paths from the minimum
            for ( VHalfEdge path : minimum.getEdges() ) {
                if ( vertexes.hasVertexBeenConsidered(path.getConnectedVertex()) ) {
                    continue;
                }
                
                // Otherwise, add the path as a new possibility
                futurepaths.addPath( path );
            }
            
            if ( futurepaths.size()<=0 ) break;
            
            // Remove the best path
            VHalfEdge nextbestpath = futurepaths.popBestNextPath();
            
            // Consider if path is part of current cluster, or if we
            //  need to create a new cluster
            if ( nextbestpath.getLength()<=clustercutoff ) {
                nextbestpath.shownonminimumspanningtree = true;
            } else {
                currentcluster = new VCluster();
                clusters.add( currentcluster );
            }
            
            // Set next minimum to consider
            minimum = nextbestpath.getConnectedVertex();
        } while ( true );
        
        return maxpathofminimumspanningtree;
    }
    
    /**
     * This method works by finding the longest straight line and returning
     *  the value at the end of that line (i.e. the longest straight line
     *  if you were to plot the graph). This method turns out to be by far
     *  better than other methods for determining a cut-off.
     */
    private static int determineClusterCutOffByGradient(TreeMap<Integer,Integer> sortedvalues, int gradient_error_allowed) {
        //System.out.println();
        //System.out.println("Determining Cluster Cut-off:");
        
        // Define variables
        Iterator<Integer> iter = sortedvalues.values().iterator();
        int prev, curr;
        int prevgrad, currgrad;
        
        // Get the two first values and define the gradient
        if (!( iter.hasNext() )) return -1;
        prev = iter.next().intValue();
        if (!( iter.hasNext() )) return prev;
        curr = iter.next().intValue();
        currgrad = curr - prev;
        
        // Consider the rest of the problem
        int index        = 1;
        int valuesonline = 2;
        int best_valuesonline = -1;
        int best_cutoff       = -1;
        while ( iter.hasNext() ) {
            // Update variables
            index++;
            prev = curr;
            prevgrad = currgrad;
            curr = iter.next().intValue();
            currgrad = curr - prev;
            
            //System.out.println("Previous = " + prev + ", Current = " + curr + "; Prev Gradient = " + prevgrad + ", Curr Gradient = " + currgrad);
            
            // Check if current gradient is within the error allowed
            if ( prevgrad-gradient_error_allowed<=currgrad && currgrad<=prevgrad+gradient_error_allowed ) {
                valuesonline++;
            } else {
                // If we've already found the largest line, then return
                //  the cut-off found (for efficiency)
                if ( valuesonline>=(sortedvalues.size()-index-1) ) {
                    //System.out.println("Returning cut-off of " + prev + " because line length is greater than remaining size (line of length " + valuesonline + ")");
                    return prev;
                }
                
                // Determine if line is the best line found so far
                if ( valuesonline>best_valuesonline ) {
                    //System.out.println("  - new best line of " + valuesonline + " set");
                    best_valuesonline = valuesonline;
                    best_cutoff = prev;
                } else {
                    //System.out.println("  - not best - better line of " + best_valuesonline + " already exists");
                }
                
                // Reset values on line and continue...
                valuesonline = 2;
            }
        }
        
        // Check finally for better line
        if ( valuesonline>best_valuesonline ) {
            //System.out.println("  - new best line of " + valuesonline + " set");
            best_cutoff = prev;
        }
        
        // Return the best cutoff found (i.e. the longest straight line)
        //System.out.println("Returning cut-off of " + best_cutoff + " (line of length " + best_valuesonline + ")");
        return best_cutoff;
    }
    
    
    
    
    /* ***************************************************** */
    
    private static class VertexList extends TreeMap<VVertex,VVertex> {
        
        /* ************************************************* */
        // Constructor
        
		private static final long serialVersionUID = 1L;
		private VertexList() {
            super(VERTEX_COMPARATOR);
        }
        
        /* ************************************************* */
        // Methods
        
        public boolean hasVertexBeenConsidered(VVertex vertex) {
            return ( super.get(vertex)!=null );
        }
        public void addVertex(VVertex vertex) {
            super.put(vertex, vertex);
        }
        
        /* ************************************************* */
    }
    
    private static class FuturePathList extends TreeMap<VHalfEdge,VHalfEdge> {
        
        /* ************************************************* */
        // Constructor
        
		private static final long serialVersionUID = 1L;

		private FuturePathList() {
            super(PATH_COMPARATOR);
        }
        
        /* ************************************************* */
        // Methods
        
        public VHalfEdge popBestNextPath() {
            VHalfEdge next = super.firstKey();
            super.remove(next);
            return next;
        }
        
        public void addPath(VHalfEdge path) {
            super.put(path, path);
        }
        
        /* ************************************************* */
    }
    
    /* ***************************************************** */
}
