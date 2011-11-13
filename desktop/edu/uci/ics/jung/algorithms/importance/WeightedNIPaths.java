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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.graph.DirectedGraph;



/**
 * This algorithm measures the importance of nodes based upon both the number and length of disjoint paths that lead
 * to a given node from each of the nodes in the root set. Specifically the formula for measuring the importance of a
 * node is given by: I(t|R) = sum_i=1_|P(r,t)|_{alpha^|p_i|} where alpha is the path decay coefficient, p_i is path i
 * and P(r,t) is a set of maximum-sized node-disjoint paths from r to t.
 * <p>
 * This algorithm uses heuristic breadth-first search to try and find the maximum-sized set of node-disjoint paths
 * between two nodes. As such, it is not guaranteed to give exact answers.
 * <p>
 * A simple example of usage is:
 * <pre>
 * WeightedNIPaths ranker = new WeightedNIPaths(someGraph,2.0,6,rootSet);
 * ranker.evaluate();
 * ranker.printRankings();
 * </pre>
 * 
 * @author Scott White
 * @see "Algorithms for Estimating Relative Importance in Graphs by Scott White and Padhraic Smyth, 2003"
 */
public class WeightedNIPaths<V,E> extends AbstractRanker<V,E> {
    public final static String WEIGHTED_NIPATHS_KEY = "jung.algorithms.importance.WEIGHTED_NIPATHS_KEY";
    private double mAlpha;
    private int mMaxDepth;
    private Set<V> mPriors;
    private Map<E,Number> pathIndices = new HashMap<E,Number>();
    private Map<Object,V> roots = new HashMap<Object,V>();
    private Map<V,Set<Number>> pathsSeenMap = new HashMap<V,Set<Number>>();
    private Factory<V> vertexFactory;
    private Factory<E> edgeFactory;

    /**
     * Constructs and initializes the algorithm.
     * @param graph the graph whose nodes are being measured for their importance
     * @param alpha the path decay coefficient (>= 1); 2 is recommended
     * @param maxDepth the maximal depth to search out from the root set
     * @param priors the root set (starting vertices)
     */
    public WeightedNIPaths(DirectedGraph<V,E> graph, Factory<V> vertexFactory,
    		Factory<E> edgeFactory, double alpha, int maxDepth, Set<V> priors) {
        super.initialize(graph, true,false);
        this.vertexFactory = vertexFactory;
        this.edgeFactory = edgeFactory;
        mAlpha = alpha;
        mMaxDepth = maxDepth;
        mPriors = priors;
        for (V v : graph.getVertices()) {
        	super.setVertexRankScore(v, 0.0);
        }
    }

    protected void incrementRankScore(V v, double rankValue) {
        setVertexRankScore(v, getVertexRankScore(v) + rankValue);
    }

    protected void computeWeightedPathsFromSource(V root, int depth) {

        int pathIdx = 1;

        for (E e : getGraph().getOutEdges(root)) {
            this.pathIndices.put(e, pathIdx);
            this.roots.put(e, root);
            newVertexEncountered(pathIdx, getGraph().getEndpoints(e).getSecond(), root);
            pathIdx++;
        }

        List<E> edges = new ArrayList<E>();

        V virtualNode = vertexFactory.create();
        getGraph().addVertex(virtualNode);
        E virtualSinkEdge = edgeFactory.create();

        getGraph().addEdge(virtualSinkEdge, virtualNode, root);
        edges.add(virtualSinkEdge);

        int currentDepth = 0;
        while (currentDepth <= depth) {

            double currentWeight = Math.pow(mAlpha, -1.0 * currentDepth);
            for (E currentEdge : edges) { 
                incrementRankScore(getGraph().getEndpoints(currentEdge).getSecond(),//
                		currentWeight);
            }

            if ((currentDepth == depth) || (edges.size() == 0)) break;

            List<E> newEdges = new ArrayList<E>();

            for (E currentSourceEdge : edges) { //Iterator sourceEdgeIt = edges.iterator(); sourceEdgeIt.hasNext();) {
                Number sourcePathIndex = this.pathIndices.get(currentSourceEdge);

                // from the currentSourceEdge, get its opposite end
                // then iterate over the out edges of that opposite end
                V newDestVertex = getGraph().getEndpoints(currentSourceEdge).getSecond();
                Collection<E> outs = getGraph().getOutEdges(newDestVertex);
                for (E currentDestEdge : outs) {
                	V destEdgeRoot = this.roots.get(currentDestEdge);
                	V destEdgeDest = getGraph().getEndpoints(currentDestEdge).getSecond();

                    if (currentSourceEdge == virtualSinkEdge) {
                        newEdges.add(currentDestEdge);
                        continue;
                    }
                    if (destEdgeRoot == root) {
                        continue;
                    }
                    if (destEdgeDest == getGraph().getEndpoints(currentSourceEdge).getFirst()) {//currentSourceEdge.getSource()) {
                        continue;
                    }
                    Set<Number> pathsSeen = this.pathsSeenMap.get(destEdgeDest);

                    if (pathsSeen == null) {
                        newVertexEncountered(sourcePathIndex.intValue(), destEdgeDest, root);
                    } else if (roots.get(destEdgeDest) != root) {
                        roots.put(destEdgeDest,root);
                        pathsSeen.clear();
                        pathsSeen.add(sourcePathIndex);
                    } else if (!pathsSeen.contains(sourcePathIndex)) {
                        pathsSeen.add(sourcePathIndex);
                    } else {
                        continue;
                    }

                    this.pathIndices.put(currentDestEdge, sourcePathIndex);
                    this.roots.put(currentDestEdge, root);
                    newEdges.add(currentDestEdge);
                }
            }

            edges = newEdges;
            currentDepth++;
        }

        getGraph().removeVertex(virtualNode);
    }

    private void newVertexEncountered(int sourcePathIndex, V dest, V root) {
        Set<Number> pathsSeen = new HashSet<Number>();
        pathsSeen.add(sourcePathIndex);
        this.pathsSeenMap.put(dest, pathsSeen);
        roots.put(dest, root);
    }

    @Override
    public void step() {
        for (V v : mPriors) {
            computeWeightedPathsFromSource(v, mMaxDepth);
        }

        normalizeRankings();
//        return 0;
    }
    
    /**
     * Given a node, returns the corresponding rank score. This implementation of <code>getRankScore</code> assumes
     * the decoration representing the rank score is of type <code>MutableDouble</code>.
     * @return  the rank score for this node
     */
    @Override
    public String getRankScoreKey() {
        return WEIGHTED_NIPATHS_KEY;
    }

    @Override
    protected void onFinalize(Object udc) {
    	pathIndices.remove(udc);
    	roots.remove(udc);
    	pathsSeenMap.remove(udc);
    }
}
