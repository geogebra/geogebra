package edu.uci.ics.jung.algorithms.shortestpath;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * For the input Graph, creates a MinimumSpanningTree
 * using a variation of Prim's algorithm.
 * 
 * @author Tom Nelson - tomnelson@dev.java.net
 *
 * @param <V> the vertex type
 * @param <E> the edge type
 */
@SuppressWarnings("unchecked")
public class PrimMinimumSpanningTree<V,E> implements Transformer<Graph<V,E>,Graph<V,E>> {
	
	protected Factory<? extends Graph<V,E>> treeFactory;
	protected Transformer<E,Double> weights; 
	
	/**
	 * Creates an instance which generates a minimum spanning tree assuming constant edge weights.
	 */
	public PrimMinimumSpanningTree(Factory<? extends Graph<V,E>> factory) {
		this(factory, new ConstantTransformer(1.0));
	}

    /**
     * Creates an instance which generates a minimum spanning tree using the input edge weights.
     */
	public PrimMinimumSpanningTree(Factory<? extends Graph<V,E>> factory, 
			Transformer<E, Double> weights) {
		this.treeFactory = factory;
		if(weights != null) {
			this.weights = weights;
		}
	}
	
	/**
	 * @param graph the Graph to find MST in
	 */
    public Graph<V,E> transform(Graph<V,E> graph) {
		Set<E> unfinishedEdges = new HashSet<E>(graph.getEdges());
		Graph<V,E> tree = treeFactory.create();
		V root = findRoot(graph);
		if(graph.getVertices().contains(root)) {
			tree.addVertex(root);
		} else if(graph.getVertexCount() > 0) {
			// pick an arbitrary vertex to make root
			tree.addVertex(graph.getVertices().iterator().next());
		}
		updateTree(tree, graph, unfinishedEdges);
		
		return tree;
	}
    
    protected V findRoot(Graph<V,E> graph) {
    	for(V v : graph.getVertices()) {
    		if(graph.getInEdges(v).size() == 0) {
    			return v;
    		}
    	}
    	// if there is no obvious root, pick any vertex
    	if(graph.getVertexCount() > 0) {
    		return graph.getVertices().iterator().next();
    	}
    	// this graph has no vertices
    	return null;
    }
	
	protected void updateTree(Graph<V,E> tree, Graph<V,E> graph, Collection<E> unfinishedEdges) {
		Collection<V> tv = tree.getVertices();
		double minCost = Double.MAX_VALUE;
		E nextEdge = null;
		V nextVertex = null;
		V currentVertex = null;
		for(E e : unfinishedEdges) {
			
			if(tree.getEdges().contains(e)) continue;
			// find the lowest cost edge, get its opposite endpoint,
			// and then update forest from its Successors
			Pair<V> endpoints = graph.getEndpoints(e);
			V first = endpoints.getFirst();
			V second = endpoints.getSecond();
			if((tv.contains(first) == true && tv.contains(second) == false)) {
				if(weights.transform(e) < minCost) {
					minCost = weights.transform(e);
					nextEdge = e;
					currentVertex = first;
					nextVertex = second;
				}
			} else if((tv.contains(second) == true && tv.contains(first) == false)) {
				if(weights.transform(e) < minCost) {
					minCost = weights.transform(e);
					nextEdge = e;
					currentVertex = second;
					nextVertex = first;
				}
			}
		}
		
		if(nextVertex != null && nextEdge != null) {
			unfinishedEdges.remove(nextEdge);
			tree.addEdge(nextEdge, currentVertex, nextVertex);
			updateTree(tree, graph, unfinishedEdges);
		}
	}
}
