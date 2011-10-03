/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package edu.uci.ics.jung.algorithms.importance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.collections15.Buffer;
import org.apache.commons.collections15.buffer.UnboundedFifoBuffer;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;

/**
 * Computes betweenness centrality for each vertex and edge in the graph. The result is that each vertex
 * and edge has a UserData element of type MutableDouble whose key is 'centrality.BetweennessCentrality'.
 * Note: Many social network researchers like to normalize the betweenness values by dividing the values by
 * (n-1)(n-2)/2. The values given here are unnormalized.<p>
 *
 * A simple example of usage is:
 * <pre>
 * BetweennessCentrality ranker = new BetweennessCentrality(someGraph);
 * ranker.evaluate();
 * ranker.printRankings();
 * </pre>
 *
 * Running time is: O(n^2 + nm).
 * @see "Ulrik Brandes: A Faster Algorithm for Betweenness Centrality. Journal of Mathematical Sociology 25(2):163-177, 2001."
 * @author Scott White
 * @author Tom Nelson converted to jung2
 */

public class BetweennessCentrality<V,E> extends AbstractRanker<V,E> {

    public static final String CENTRALITY = "centrality.BetweennessCentrality";

    /**
     * Constructor which initializes the algorithm
     * @param g the graph whose nodes are to be analyzed
     */
    public BetweennessCentrality(Graph<V,E> g) {
        initialize(g, true, true);
    }

    public BetweennessCentrality(Graph<V,E> g, boolean rankNodes) {
        initialize(g, rankNodes, true);
    }

    public BetweennessCentrality(Graph<V,E> g, boolean rankNodes, boolean rankEdges)
    {
        initialize(g, rankNodes, rankEdges);
    }
    
	protected void computeBetweenness(Graph<V,E> graph) {

    	Map<V,BetweennessData> decorator = new HashMap<V,BetweennessData>();
    	Map<V,Number> bcVertexDecorator = 
    		vertexRankScores.get(getRankScoreKey());
    	bcVertexDecorator.clear();
    	Map<E,Number> bcEdgeDecorator = 
    		edgeRankScores.get(getRankScoreKey());
    	bcEdgeDecorator.clear();
        
        Collection<V> vertices = graph.getVertices();
        
        for (V s : vertices) {

            initializeData(graph,decorator);

            decorator.get(s).numSPs = 1;
            decorator.get(s).distance = 0;

            Stack<V> stack = new Stack<V>();
            Buffer<V> queue = new UnboundedFifoBuffer<V>();
            queue.add(s);

            while (!queue.isEmpty()) {
                V v = queue.remove();
                stack.push(v);

                for(V w : getGraph().getSuccessors(v)) {

                    if (decorator.get(w).distance < 0) {
                        queue.add(w);
                        decorator.get(w).distance = decorator.get(v).distance + 1;
                    }

                    if (decorator.get(w).distance == decorator.get(v).distance + 1) {
                        decorator.get(w).numSPs += decorator.get(v).numSPs;
                        decorator.get(w).predecessors.add(v);
                    }
                }
            }
            
            while (!stack.isEmpty()) {
                V w = stack.pop();

                for (V v : decorator.get(w).predecessors) {

                    double partialDependency = (decorator.get(v).numSPs / decorator.get(w).numSPs);
                    partialDependency *= (1.0 + decorator.get(w).dependency);
                    decorator.get(v).dependency +=  partialDependency;
                    E currentEdge = getGraph().findEdge(v, w);
                    double edgeValue = bcEdgeDecorator.get(currentEdge).doubleValue();
                    edgeValue += partialDependency;
                    bcEdgeDecorator.put(currentEdge, edgeValue);
                }
                if (w != s) {
                	double bcValue = bcVertexDecorator.get(w).doubleValue();
                	bcValue += decorator.get(w).dependency;
                	bcVertexDecorator.put(w, bcValue);
                }
            }
        }

        if(graph instanceof UndirectedGraph) {
            for (V v : vertices) { 
            	double bcValue = bcVertexDecorator.get(v).doubleValue();
            	bcValue /= 2.0;
            	bcVertexDecorator.put(v, bcValue);
            }
            for (E e : graph.getEdges()) {
            	double bcValue = bcEdgeDecorator.get(e).doubleValue();
            	bcValue /= 2.0;
            	bcEdgeDecorator.put(e, bcValue);
            }
        }

        for (V vertex : vertices) {
            decorator.remove(vertex);
        }
    }

    private void initializeData(Graph<V,E> g, Map<V,BetweennessData> decorator) {
        for (V vertex : g.getVertices()) {

        	Map<V,Number> bcVertexDecorator = vertexRankScores.get(getRankScoreKey());
        	if(bcVertexDecorator.containsKey(vertex) == false) {
        		bcVertexDecorator.put(vertex, 0.0);
        	}
            decorator.put(vertex, new BetweennessData());
        }
        for (E e : g.getEdges()) {

        	Map<E,Number> bcEdgeDecorator = edgeRankScores.get(getRankScoreKey());
        	if(bcEdgeDecorator.containsKey(e) == false) {
        		bcEdgeDecorator.put(e, 0.0);
        	}
        }
    }
    
    /**
     * the user datum key used to store the rank scores
     * @return the key
     */
    @Override
    public String getRankScoreKey() {
        return CENTRALITY;
    }

    @Override
    public void step() {
        computeBetweenness(getGraph());
    }

    class BetweennessData {
        double distance;
        double numSPs;
        List<V> predecessors;
        double dependency;

        BetweennessData() {
            distance = -1;
            numSPs = 0;
            predecessors = new ArrayList<V>();
            dependency = 0;
        }
    }
}
