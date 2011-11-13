/**
 * Copyright (c) 2008, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 * Created on Sep 16, 2008
 * 
 */
package edu.uci.ics.jung.algorithms.scoring;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;

import edu.uci.ics.jung.algorithms.util.MapBinaryHeap;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;

/**
 * Computes betweenness centrality for each vertex and edge in the graph.
 * 
 * @see "Ulrik Brandes: A Faster Algorithm for Betweenness Centrality. Journal of Mathematical Sociology 25(2):163-177, 2001."
 */
public class BetweennessCentrality<V, E> 
	implements VertexScorer<V, Double>, EdgeScorer<E, Double> 
{
	protected Graph<V,E> graph;
	protected Map<V, Double> vertex_scores;
	protected Map<E, Double> edge_scores;
	protected Map<V, BetweennessData> vertex_data;
		
	/**
	 * Calculates betweenness scores based on the all-pairs unweighted shortest paths
	 * in the graph.
	 * @param graph the graph for which the scores are to be calculated
	 */
	@SuppressWarnings("unchecked")
	public BetweennessCentrality(Graph<V, E> graph) 
	{
		initialize(graph);
		computeBetweenness(new LinkedList<V>(), new ConstantTransformer(1));
	}

	/**
	 * Calculates betweenness scores based on the all-pairs weighted shortest paths in the
	 * graph.
	 * 
	 * <p>NOTE: This version of the algorithm may not work correctly on all graphs; we're still
	 * working out the bugs.  Use at your own risk.
	 * @param graph the graph for which the scores are to be calculated
	 * @param edge_weights the edge weights to be used in the path length calculations
	 */
	public BetweennessCentrality(Graph<V, E> graph, 
			Transformer<E, ? extends Number> edge_weights) 
	{
		// reject negative-weight edges up front
		for (E e : graph.getEdges())
		{
			double e_weight = edge_weights.transform(e).doubleValue();
        	if (e_weight < 0)
        		throw new IllegalArgumentException(String.format(
        				"Weight for edge '%s' is < 0: %d", e, e_weight)); 
		}
			
		initialize(graph);
		computeBetweenness(new MapBinaryHeap<V>(new BetweennessComparator()), 
			edge_weights);
	}

	protected void initialize(Graph<V,E> graph)
	{
		this.graph = graph;
		this.vertex_scores = new HashMap<V, Double>();
		this.edge_scores = new HashMap<E, Double>();
		this.vertex_data = new HashMap<V, BetweennessData>();
		
		for (V v : graph.getVertices())
			this.vertex_scores.put(v, 0.0);
		
		for (E e : graph.getEdges())
			this.edge_scores.put(e, 0.0);
	}
	
	protected void computeBetweenness(Queue<V> queue, 
			Transformer<E, ? extends Number> edge_weights)
	{
		for (V v : graph.getVertices())
		{
			// initialize the betweenness data for this new vertex
			for (V s : graph.getVertices()) 
				this.vertex_data.put(s, new BetweennessData());

//			if (v.equals(new Integer(0)))
//				System.out.println("pause");
			
            vertex_data.get(v).numSPs = 1;
            vertex_data.get(v).distance = 0;

            Stack<V> stack = new Stack<V>();
//            Buffer<V> queue = new UnboundedFifoBuffer<V>();
//            queue.add(v);
            queue.offer(v);

            while (!queue.isEmpty()) 
            {
//                V w = queue.remove();
            	V w = queue.poll();
                stack.push(w);
            	BetweennessData w_data = vertex_data.get(w);
                
                for (E e : graph.getOutEdges(w))
                {
                	// TODO (jrtom): change this to getOtherVertices(w, e)
                	V x = graph.getOpposite(w, e);
                	if (x.equals(w))
                		continue;
                	double wx_weight = edge_weights.transform(e).doubleValue();
                	
                	
//                for(V x : graph.getSuccessors(w)) 
//                {
//                	if (x.equals(w))
//                		continue;
                	
                	// FIXME: the other problem is that I need to 
                	// keep putting the neighbors of things we've just 
                	// discovered in the queue, if they're undiscovered or
                	// at greater distance.
                	
                	// FIXME: this is the problem, right here, I think: 
                	// need to update position in queue if distance changes
                	// (which can only happen with weighted edges).
                	// for each outgoing edge e from w, get other end x
                	// if x not already visited (dist x < 0)
                	//   set x's distance to w's dist + edge weight
                	//   add x to queue; pri in queue is x's dist
                	// if w's dist + edge weight < x's dist 
                	//   update x's dist
                	//   update x in queue (MapBinaryHeap)
                	//   clear x's incoming edge list
                	// if w's dist + edge weight = x's dist
                	//   add e to x's incoming edge list
                	
                	BetweennessData x_data = vertex_data.get(x);
                	double x_potential_dist = w_data.distance + wx_weight;
                	
                    if (x_data.distance < 0) 
                    {
//                        queue.add(x);
//                        vertex_data.get(x).distance = vertex_data.get(w).distance + 1;
                    	x_data.distance = x_potential_dist;
                      	queue.offer(x);
                    }
                    
                    // note:
                    // (1) this can only happen with weighted edges
                    // (2) x's SP count and incoming edges are updated below 
                    if (x_data.distance > x_potential_dist)
                    {
                    	x_data.distance = x_potential_dist;
                    	// invalidate previously identified incoming edges
                    	// (we have a new shortest path distance to x)
                    	x_data.incomingEdges.clear(); 
                        // update x's position in queue
                    	((MapBinaryHeap<V>)queue).update(x);
                    }
//                  if (vertex_data.get(x).distance == vertex_data.get(w).distance + 1) 
                    // 
//                    if (x_data.distance == x_potential_dist) 
//                    {
//                        x_data.numSPs += w_data.numSPs;
////                        vertex_data.get(x).predecessors.add(w);
//                        x_data.incomingEdges.add(e);
//                    }
                }
                for (E e: graph.getOutEdges(w))
                {
                	V x = graph.getOpposite(w, e);
                	if (x.equals(w))
                		continue;
                	double e_weight = edge_weights.transform(e).doubleValue();
                	BetweennessData x_data = vertex_data.get(x);
                	double x_potential_dist = w_data.distance + e_weight;
                    if (x_data.distance == x_potential_dist) 
                    {
                        x_data.numSPs += w_data.numSPs;
//                        vertex_data.get(x).predecessors.add(w);
                        x_data.incomingEdges.add(e);
                    }
                }
            }
    		while (!stack.isEmpty()) 
    		{
    		    V x = stack.pop();

//    		    for (V w : vertex_data.get(x).predecessors) 
    		    for (E e : vertex_data.get(x).incomingEdges)
    		    {
    		    	V w = graph.getOpposite(x, e);
    		        double partialDependency = 
    		        	vertex_data.get(w).numSPs / vertex_data.get(x).numSPs *
    		        	(1.0 + vertex_data.get(x).dependency);
    		        vertex_data.get(w).dependency +=  partialDependency;
//    		        E w_x = graph.findEdge(w, x);
//    		        double w_x_score = edge_scores.get(w_x).doubleValue();
//    		        w_x_score += partialDependency;
//    		        edge_scores.put(w_x, w_x_score);
    		        double e_score = edge_scores.get(e).doubleValue();
    		        edge_scores.put(e, e_score + partialDependency);
    		    }
    		    if (!x.equals(v)) 
    		    {
    		    	double x_score = vertex_scores.get(x).doubleValue();
    		    	x_score += vertex_data.get(x).dependency;
    		    	vertex_scores.put(x, x_score);
    		    }
    		}
        }

        if(graph instanceof UndirectedGraph) 
        {
    		for (V v : graph.getVertices()) { 
    			double v_score = vertex_scores.get(v).doubleValue();
    			v_score /= 2.0;
    			vertex_scores.put(v, v_score);
    		}
    		for (E e : graph.getEdges()) {
    			double e_score = edge_scores.get(e).doubleValue();
    			e_score /= 2.0;
    			edge_scores.put(e, e_score);
    		}
        }

        vertex_data.clear();
	}

//	protected void computeWeightedBetweenness(Transformer<E, ? extends Number> edge_weights)
//	{
//		for (V v : graph.getVertices())
//		{
//			// initialize the betweenness data for this new vertex
//			for (V s : graph.getVertices()) 
//				this.vertex_data.put(s, new BetweennessData());
//            vertex_data.get(v).numSPs = 1;
//            vertex_data.get(v).distance = 0;
//
//            Stack<V> stack = new Stack<V>();
////            Buffer<V> queue = new UnboundedFifoBuffer<V>();
//            SortedSet<V> pqueue = new TreeSet<V>(new BetweennessComparator());
////          queue.add(v);
//            pqueue.add(v);
//
////            while (!queue.isEmpty()) 
//            while (!pqueue.isEmpty()) 
//            {
////              V w = queue.remove();
//            	V w = pqueue.first();
//            	pqueue.remove(w);
//                stack.push(w);
//
////                for(V x : graph.getSuccessors(w)) 
//                for (E e : graph.getOutEdges(w))
//                {
//                	// TODO (jrtom): change this to getOtherVertices(w, e)
//                	V x = graph.getOpposite(w, e);
//                	if (x.equals(w))
//                		continue;
//                	double e_weight = edge_weights.transform(e).doubleValue();
//                	
//                    if (vertex_data.get(x).distance < 0) 
//                    {
////                        queue.add(x);
//                    	pqueue.add(v);
////                        vertex_data.get(x).distance = vertex_data.get(w).distance + 1;
//                        vertex_data.get(x).distance = 
//                        	vertex_data.get(w).distance + e_weight;
//                    }
//
////                    if (vertex_data.get(x).distance == vertex_data.get(w).distance + 1) 
//                    if (vertex_data.get(x).distance == 
//                    	vertex_data.get(w).distance + e_weight)
//                    {
//                        vertex_data.get(x).numSPs += vertex_data.get(w).numSPs;
//                        vertex_data.get(x).predecessors.add(w);
//                    }
//                }
//            }
//            updateScores(v, stack);
//        }
//
//        if(graph instanceof UndirectedGraph) 
//            adjustUndirectedScores();
//
//        vertex_data.clear();
//	}
	
	public Double getVertexScore(V v) 
	{
		return vertex_scores.get(v);
	}

	public Double getEdgeScore(E e) 
	{
		return edge_scores.get(e);
	}

    private class BetweennessData 
    {
        double distance;
        double numSPs;
//        List<V> predecessors;
        List<E> incomingEdges;
        double dependency;

        BetweennessData() 
        {
            distance = -1;
            numSPs = 0;
//            predecessors = new ArrayList<V>();
            incomingEdges = new ArrayList<E>();
            dependency = 0;
        }
        
        @Override
        public String toString()
        {
        	return "[d:" + distance + ", sp:" + numSPs + 
        		", p:" + incomingEdges + ", d:" + dependency + "]\n";
//        		", p:" + predecessors + ", d:" + dependency + "]\n";
        }
    }
    
    private class BetweennessComparator implements Comparator<V>
    {
		public int compare(V v1, V v2) 
		{
			return vertex_data.get(v1).distance > vertex_data.get(v2).distance ? 1 : -1;
		}
    }
}
