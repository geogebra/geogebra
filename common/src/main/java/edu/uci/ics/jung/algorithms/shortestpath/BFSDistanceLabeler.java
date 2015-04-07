/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package edu.uci.ics.jung.algorithms.shortestpath;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.jung.graph.Hypergraph;

/**
 * Labels each node in the graph according to the BFS distance from the start node(s). If nodes are unreachable, then
 * they are assigned a distance of -1.
 * All nodes traversed at step k are marked as predecessors of their successors traversed at step k+1.
 * <p>
 * Running time is: O(m)
 * @author Scott White
 */
public class BFSDistanceLabeler<V, E> {

    private Map<V, Number> distanceDecorator = new HashMap<V,Number>();
    private List<V> mCurrentList;
    private Set<V> mUnvisitedVertices;
    private List<V> mVerticesInOrderVisited;
    private Map<V,HashSet<V>> mPredecessorMap;

	/**
	 * Creates a new BFS labeler for the specified graph and root set
	 * The distances are stored in the corresponding Vertex objects and are of type MutableInteger
	 */
	public BFSDistanceLabeler() {
		mPredecessorMap = new HashMap<V,HashSet<V>>();
	}

    /**
     * Returns the list of vertices visited in order of traversal
     * @return the list of vertices
     */
    public List<V> getVerticesInOrderVisited() {
        return mVerticesInOrderVisited;
    }

    /**
     * Returns the set of all vertices that were not visited
     * @return the list of unvisited vertices
     */
    public Set<V> getUnvisitedVertices() {
        return mUnvisitedVertices;
    }

    /**
     * Given a vertex, returns the shortest distance from any node in the root set to v
     * @param v the vertex whose distance is to be retrieved
     * @return the shortest distance from any node in the root set to v
     */
    public int getDistance(Hypergraph<V,E> g, V v) {
        if (!g.getVertices().contains(v)) {
            throw new IllegalArgumentException("Vertex is not contained in the graph.");
        }

        return distanceDecorator.get(v).intValue();
    }

    /**
     * Returns set of predecessors of the given vertex
     * @param v the vertex whose predecessors are to be retrieved
     * @return the set of predecessors
     */
    public Set<V> getPredecessors(V v) {
        return mPredecessorMap.get(v);
    }

    protected void initialize(Hypergraph<V,E> g, Set<V> rootSet) {
        mVerticesInOrderVisited = new ArrayList<V>();
        mUnvisitedVertices = new HashSet<V>();
        for(V currentVertex : g.getVertices()) {
            mUnvisitedVertices.add(currentVertex);
            mPredecessorMap.put(currentVertex,new HashSet<V>());
        }

        mCurrentList = new ArrayList<V>();
        for(V v : rootSet) {
            distanceDecorator.put(v, new Integer(0));
            mCurrentList.add(v);
            mUnvisitedVertices.remove(v);
            mVerticesInOrderVisited.add(v);
        }
    }

    private void addPredecessor(V predecessor,V sucessor) {
        HashSet<V> predecessors = mPredecessorMap.get(sucessor);
        predecessors.add(predecessor);
    }

    /**
     * Computes the distances of all the node from the starting root nodes. If there is more than one root node
     * the minimum distance from each root node is used as the designated distance to a given node. Also keeps track
     * of the predecessors of each node traversed as well as the order of nodes traversed.
     * @param graph the graph to label
     * @param rootSet the set of starting vertices to traverse from
     */
    public void labelDistances(Hypergraph<V,E> graph, Set<V> rootSet) {

        initialize(graph,rootSet);

        int distance = 1;
        while (true) {
            List<V> newList = new ArrayList<V>();
            for(V currentVertex : mCurrentList) {
            	if(graph.containsVertex(currentVertex)) {
            		for(V next : graph.getSuccessors(currentVertex)) {
            			visitNewVertex(currentVertex,next, distance, newList);
            		}
            	}
            }
            if (newList.size() == 0) break;
            mCurrentList = newList;
            distance++;
        }

        for(V v : mUnvisitedVertices) {
            distanceDecorator.put(v,new Integer(-1));
        }
    }

    /**
     * Computes the distances of all the node from the specified root node. Also keeps track
     * of the predecessors of each node traversed as well as the order of nodes traversed.
     *  @param graph the graph to label
     * @param root the single starting vertex to traverse from
     */
    public void labelDistances(Hypergraph<V,E> graph, V root) {
        labelDistances(graph, Collections.singleton(root));
    }

    private void visitNewVertex(V predecessor, V neighbor, int distance, List<V> newList) {
        if (mUnvisitedVertices.contains(neighbor)) {
            distanceDecorator.put(neighbor, new Integer(distance));
            newList.add(neighbor);
            mVerticesInOrderVisited.add(neighbor);
            mUnvisitedVertices.remove(neighbor);
        }
        int predecessorDistance = distanceDecorator.get(predecessor).intValue();
        int successorDistance = distanceDecorator.get(neighbor).intValue();
        if (predecessorDistance < successorDistance) {
            addPredecessor(predecessor,neighbor);
        }
    }

    /**
     * Returns a map from vertices to minimum distances from the original source(s).
     * Must be called after {@code labelDistances} in order to contain valid data.
     */
    public Map<V, Number> getDistanceDecorator() {
        return distanceDecorator;
    }
}
