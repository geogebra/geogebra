/*
 * Created on Jul 9, 2005
 *
 * Copyright (c) 2005, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.algorithms.shortestpath;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;

import edu.uci.ics.jung.algorithms.util.BasicMapEntry;
import edu.uci.ics.jung.algorithms.util.MapBinaryHeap;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Hypergraph;

/**
 * <p>Calculates distances in a specified graph, using  
 * Dijkstra's single-source-shortest-path algorithm.  All edge weights
 * in the graph must be nonnegative; if any edge with negative weight is 
 * found in the course of calculating distances, an 
 * <code>IllegalArgumentException</code> will be thrown.
 * (Note: this exception will only be thrown when such an edge would be
 * used to update a given tentative distance;
 * the algorithm does not check for negative-weight edges "up front".)
 * 
 * <p>Distances and partial results are optionally cached (by this instance)
 * for later reference.  Thus, if the 10 closest vertices to a specified source 
 * vertex are known, calculating the 20 closest vertices does not require 
 * starting Dijkstra's algorithm over from scratch.</p>
 * 
 * <p>Distances are stored as double-precision values.  
 * If a vertex is not reachable from the specified source vertex, no 
 * distance is stored.  <b>This is new behavior with version 1.4</b>;
 * the previous behavior was to store a value of 
 * <code>Double.POSITIVE_INFINITY</code>.  This change gives the algorithm
 * an approximate complexity of O(kD log k), where k is either the number of
 * requested targets or the number of reachable vertices (whichever is smaller),
 * and D is the average degree of a vertex.</p>
 * 
 * <p> The elements in the maps returned by <code>getDistanceMap</code> 
 * are ordered (that is, returned 
 * by the iterator) by nondecreasing distance from <code>source</code>.</p>
 * 
 * <p>Users are cautioned that distances calculated should be assumed to
 * be invalidated by changes to the graph, and should invoke <code>reset()</code>
 * when appropriate so that the distances can be recalculated.</p>
 * 
 * @author Joshua O'Madadhain
 * @author Tom Nelson converted to jung2
 */
public class DijkstraDistance<V,E> implements Distance<V>
{
    protected Hypergraph<V,E> g;
    protected Transformer<E,? extends Number> nev;
    protected Map<V,SourceData> sourceMap;   // a map of source vertices to an instance of SourceData
    protected boolean cached;
    protected double max_distance;
    protected int max_targets;
    
    /**
     * <p>Creates an instance of <code>DijkstraShortestPath</code> for 
     * the specified graph and the specified method of extracting weights 
     * from edges, which caches results locally if and only if 
     * <code>cached</code> is <code>true</code>.
     * 
     * @param g     the graph on which distances will be calculated
     * @param nev   the class responsible for returning weights for edges
     * @param cached    specifies whether the results are to be cached
     */
    public DijkstraDistance(Hypergraph<V,E> g, Transformer<E,? extends Number> nev, boolean cached) {
        this.g = g;
        this.nev = nev;
        this.sourceMap = new HashMap<V,SourceData>();
        this.cached = cached;
        this.max_distance = Double.POSITIVE_INFINITY;
        this.max_targets = Integer.MAX_VALUE;
    }
    
    /**
     * <p>Creates an instance of <code>DijkstraShortestPath</code> for 
     * the specified graph and the specified method of extracting weights 
     * from edges, which caches results locally.
     * 
     * @param g     the graph on which distances will be calculated
     * @param nev   the class responsible for returning weights for edges
     */
    public DijkstraDistance(Hypergraph<V,E> g, Transformer<E,? extends Number> nev) {
        this(g, nev, true);
    }
    
    /**
     * <p>Creates an instance of <code>DijkstraShortestPath</code> for 
     * the specified unweighted graph (that is, all weights 1) which
     * caches results locally.
     * 
     * @param g     the graph on which distances will be calculated
     */ 
    @SuppressWarnings("unchecked")
    public DijkstraDistance(Graph<V,E> g) {
        this(g, new ConstantTransformer(1), true);
    }

    /**
     * <p>Creates an instance of <code>DijkstraShortestPath</code> for 
     * the specified unweighted graph (that is, all weights 1) which
     * caches results locally.
     * 
     * @param g     the graph on which distances will be calculated
     * @param cached    specifies whether the results are to be cached
     */ 
    @SuppressWarnings("unchecked")
    public DijkstraDistance(Graph<V,E> g, boolean cached) {
        this(g, new ConstantTransformer(1), cached);
    }
    
    /**
     * Implements Dijkstra's single-source shortest-path algorithm for
     * weighted graphs.  Uses a <code>MapBinaryHeap</code> as the priority queue, 
     * which gives this algorithm a time complexity of O(m lg n) (m = # of edges, n = 
     * # of vertices).
     * This algorithm will terminate when any of the following have occurred (in order
     * of priority):
     * <ul>
     * <li> the distance to the specified target (if any) has been found
     * <li> no more vertices are reachable 
     * <li> the specified # of distances have been found, or the maximum distance 
     * desired has been exceeded
     * <li> all distances have been found
     * </ul>
     * 
     * @param source    the vertex from which distances are to be measured
     * @param numDests  the number of distances to measure
     * @param targets   the set of vertices to which distances are to be measured
     */
    protected LinkedHashMap<V,Number> singleSourceShortestPath(V source, Collection<V> targets, int numDests)
    {
        SourceData sd = getSourceData(source);

        Set<V> to_get = new HashSet<V>();
        if (targets != null) {
            to_get.addAll(targets);
            Set<V> existing_dists = sd.distances.keySet();
            for(V o : targets) {
                if (existing_dists.contains(o))
                    to_get.remove(o);
            }
        }
        
        // if we've exceeded the max distance or max # of distances we're willing to calculate, or
        // if we already have all the distances we need, 
        // terminate
        if (sd.reached_max ||
            (targets != null && to_get.isEmpty()) ||
            (sd.distances.size() >= numDests))
        {
            return sd.distances;
        }
        
        while (!sd.unknownVertices.isEmpty() && (sd.distances.size() < numDests || !to_get.isEmpty()))
        {
            Map.Entry<V,Number> p = sd.getNextVertex();
            V v = p.getKey();
            double v_dist = p.getValue().doubleValue();
            to_get.remove(v);
            if (v_dist > this.max_distance) 
            {
                // we're done; put this vertex back in so that we're not including
                // a distance beyond what we specified
                sd.restoreVertex(v, v_dist);
                sd.reached_max = true;
                break;
            }
            sd.dist_reached = v_dist;

            if (sd.distances.size() >= this.max_targets)
            {
                sd.reached_max = true;
                break;
            }
            
            for (E e : getEdgesToCheck(v) )
            {
                for (V w : g.getIncidentVertices(e))
                {
                    if (!sd.distances.containsKey(w))
                    {
                        double edge_weight = nev.transform(e).doubleValue();
                        if (edge_weight < 0)
                            throw new IllegalArgumentException("Edges weights must be non-negative");
                        double new_dist = v_dist + edge_weight;
                        if (!sd.estimatedDistances.containsKey(w))
                        {
                            sd.createRecord(w, e, new_dist);
                        }
                        else
                        {
                            double w_dist = ((Double)sd.estimatedDistances.get(w)).doubleValue();
                            if (new_dist < w_dist) // update tentative distance & path for w
                                sd.update(w, e, new_dist);
                        }
                    }
                }
            }
        }
        return sd.distances;
    }

    protected SourceData getSourceData(V source)
    {
        SourceData sd = sourceMap.get(source);
        if (sd == null)
            sd = new SourceData(source);
        return sd;
    }
    
    /**
     * Returns the set of edges incident to <code>v</code> that should be tested.
     * By default, this is the set of outgoing edges for instances of <code>Graph</code>,
     * the set of incident edges for instances of <code>Hypergraph</code>,
     * and is otherwise undefined.
     */
    protected Collection<E> getEdgesToCheck(V v)
    {
        if (g instanceof Graph)
            return ((Graph<V,E>)g).getOutEdges(v);
        else
            return g.getIncidentEdges(v);

    }

    
    /**
     * Returns the length of a shortest path from the source to the target vertex,
     * or null if the target is not reachable from the source.
     * If either vertex is not in the graph for which this instance
     * was created, throws <code>IllegalArgumentException</code>.
     * 
     * @see #getDistanceMap(Object)
     * @see #getDistanceMap(Object,int)
     */
    public Number getDistance(V source, V target)
    {
        if (g.containsVertex(target) == false)
            throw new IllegalArgumentException("Specified target vertex " + 
                    target + " is not part of graph " + g);
        if (g.containsVertex(source) == false)
            throw new IllegalArgumentException("Specified source vertex " + 
                    source + " is not part of graph " + g);
        
        Set<V> targets = new HashSet<V>();
        targets.add(target);
        Map<V,Number> distanceMap = getDistanceMap(source, targets);
        return distanceMap.get(target);
    }
    

    /**
     * Returns a {@code Map} from each element {@code t} of {@code targets} to the 
     * shortest-path distance from {@code source} to {@code t}. 
     */
    public Map<V,Number> getDistanceMap(V source, Collection<V> targets)
    {
       if (g.containsVertex(source) == false)
            throw new IllegalArgumentException("Specified source vertex " + 
                    source + " is not part of graph " + g);
       if (targets.size() > max_targets)
            throw new IllegalArgumentException("size of target set exceeds maximum " +
                    "number of targets allowed: " + this.max_targets);
        
        Map<V,Number> distanceMap = 
        	singleSourceShortestPath(source, targets, 
        			Math.min(g.getVertexCount(), max_targets));
        if (!cached)
            reset(source);
        
        return distanceMap;
    }
    
    /**
     * <p>Returns a <code>LinkedHashMap</code> which maps each vertex 
     * in the graph (including the <code>source</code> vertex) 
     * to its distance from the <code>source</code> vertex.
     * The map's iterator will return the elements in order of 
     * increasing distance from <code>source</code>.</p>
     * 
     * <p>The size of the map returned will be the number of 
     * vertices reachable from <code>source</code>.</p>
     * 
     * @see #getDistanceMap(Object,int)
     * @see #getDistance(Object,Object)
     * @param source    the vertex from which distances are measured
     */
    public Map<V,Number> getDistanceMap(V source)
    {
        return getDistanceMap(source, Math.min(g.getVertexCount(), max_targets));
    }
    


    /**
     * <p>Returns a <code>LinkedHashMap</code> which maps each of the closest 
     * <code>numDist</code> vertices to the <code>source</code> vertex 
     * in the graph (including the <code>source</code> vertex) 
     * to its distance from the <code>source</code> vertex.  Throws 
     * an <code>IllegalArgumentException</code> if <code>source</code>
     * is not in this instance's graph, or if <code>numDests</code> is 
     * either less than 1 or greater than the number of vertices in the 
     * graph.</p>
     * 
     * <p>The size of the map returned will be the smaller of 
     * <code>numDests</code> and the number of vertices reachable from
     * <code>source</code>. 
     * 
     * @see #getDistanceMap(Object)
     * @see #getDistance(Object,Object)
     * @param source    the vertex from which distances are measured
     * @param numDests  the number of vertices for which to measure distances
     */
    public LinkedHashMap<V,Number> getDistanceMap(V source, int numDests)
    {

    	if(g.getVertices().contains(source) == false) {
            throw new IllegalArgumentException("Specified source vertex " + 
                    source + " is not part of graph " + g);
    		
    	}
        if (numDests < 1 || numDests > g.getVertexCount())
            throw new IllegalArgumentException("numDests must be >= 1 " + 
                "and <= g.numVertices()");

        if (numDests > max_targets)
            throw new IllegalArgumentException("numDests must be <= the maximum " +
                    "number of targets allowed: " + this.max_targets);
            
        LinkedHashMap<V,Number> distanceMap = 
        	singleSourceShortestPath(source, null, numDests);
                
        if (!cached)
            reset(source);
        
        return distanceMap;        
    }
    
    /**
     * Allows the user to specify the maximum distance that this instance will calculate.
     * Any vertices past this distance will effectively be unreachable from the source, in
     * the sense that the algorithm will not calculate the distance to any vertices which
     * are farther away than this distance.  A negative value for <code>max_dist</code> 
     * will ensure that no further distances are calculated.
     * 
     * <p>This can be useful for limiting the amount of time and space used by this algorithm
     * if the graph is very large.</p>
     * 
     * <p>Note: if this instance has already calculated distances greater than <code>max_dist</code>,
     * and the results are cached, those results will still be valid and available; this limit
     * applies only to subsequent distance calculations.</p>
     * @see #setMaxTargets(int)
     */
    public void setMaxDistance(double max_dist)
    {
        this.max_distance = max_dist;
        for (V v : sourceMap.keySet())
        {
            SourceData sd = sourceMap.get(v);
            sd.reached_max = (this.max_distance <= sd.dist_reached) || (sd.distances.size() >= max_targets);
        }
    }
       
    /**
     * Allows the user to specify the maximum number of target vertices per source vertex 
     * for which this instance will calculate distances.  Once this threshold is reached, 
     * any further vertices will effectively be unreachable from the source, in
     * the sense that the algorithm will not calculate the distance to any more vertices.  
     * A negative value for <code>max_targets</code> will ensure that no further distances are calculated.
     * 
     * <p>This can be useful for limiting the amount of time and space used by this algorithm
     * if the graph is very large.</p>
     * 
     * <p>Note: if this instance has already calculated distances to a greater number of 
     * targets than <code>max_targets</code>, and the results are cached, those results 
     * will still be valid and available; this limit applies only to subsequent distance 
     * calculations.</p>
     * @see #setMaxDistance(double)
     */
    public void setMaxTargets(int max_targets)
    {
        this.max_targets = max_targets;
        for (V v : sourceMap.keySet())
        {
            SourceData sd = sourceMap.get(v);
            sd.reached_max = (this.max_distance <= sd.dist_reached) || (sd.distances.size() >= max_targets);
        }
    }
    
    /**
     * Clears all stored distances for this instance.  
     * Should be called whenever the graph is modified (edge weights 
     * changed or edges added/removed).  If the user knows that
     * some currently calculated distances are unaffected by a
     * change, <code>reset(V)</code> may be appropriate instead.
     * 
     * @see #reset(Object)
     */
    public void reset()
    {
        sourceMap = new HashMap<V,SourceData>();
    }
        
    /**
     * Specifies whether or not this instance of <code>DijkstraShortestPath</code>
     * should cache its results (final and partial) for future reference.
     * 
     * @param enable    <code>true</code> if the results are to be cached, and
     *                  <code>false</code> otherwise
     */
    public void enableCaching(boolean enable)
    {
        this.cached = enable;
    }
    
    /**
     * Clears all stored distances for the specified source vertex 
     * <code>source</code>.  Should be called whenever the stored distances
     * from this vertex are invalidated by changes to the graph.
     * 
     * @see #reset()
     */
    public void reset(V source)
    {
        sourceMap.put(source, null);
    }

    /**
     * Compares according to distances, so that the BinaryHeap knows how to 
     * order the tree.  
     */
    protected static class VertexComparator<V> implements Comparator<V>
    {
        private Map<V,Number> distances;
        
        protected VertexComparator(Map<V,Number> distances)
        {
            this.distances = distances;
        }

        public int compare(V o1, V o2)
        {
            return ((Double) distances.get(o1)).compareTo((Double) distances.get(o2));
        }
    }
    
    /**
     * For a given source vertex, holds the estimated and final distances, 
     * tentative and final assignments of incoming edges on the shortest path from
     * the source vertex, and a priority queue (ordered by estimated distance)
     * of the vertices for which distances are unknown.
     * 
     * @author Joshua O'Madadhain
     */
    protected class SourceData
    {
        protected LinkedHashMap<V,Number> distances;
        protected Map<V,Number> estimatedDistances;
        protected MapBinaryHeap<V> unknownVertices;
        protected boolean reached_max = false;
        protected double dist_reached = 0;

        protected SourceData(V source)
        {
            distances = new LinkedHashMap<V,Number>();
            estimatedDistances = new HashMap<V,Number>();
            unknownVertices = new MapBinaryHeap<V>(new VertexComparator<V>(estimatedDistances));
            
            sourceMap.put(source, this);
            
            // initialize priority queue
            estimatedDistances.put(source, new Double(0)); // distance from source to itself is 0
            unknownVertices.add(source);
            reached_max = false;
            dist_reached = 0;
        }
        
        protected Map.Entry<V,Number> getNextVertex()
        {
            V v = unknownVertices.remove();
            Double dist = (Double)estimatedDistances.remove(v);
            distances.put(v, dist);
            return new BasicMapEntry<V,Number>(v, dist);
        }
        
        protected void update(V dest, E tentative_edge, double new_dist)
        {
            estimatedDistances.put(dest, new_dist);
            unknownVertices.update(dest);
        }
        
        protected void createRecord(V w, E e, double new_dist)
        {
            estimatedDistances.put(w, new_dist);
            unknownVertices.add(w);
        }
        
        protected void restoreVertex(V v, double dist) 
        {
            estimatedDistances.put(v, dist);
            unknownVertices.add(v);
            distances.remove(v);
        }
    }
}
