package edu.uci.ics.jung.algorithms.shortestpath;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.functors.ConstantTransformer;
import org.apache.commons.collections15.map.LazyMap;

import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * For the input Graph, creates a MinimumSpanningTree
 * using a variation of Prim's algorithm.
 * 
 * @author Tom Nelson - tomnelson@dev.java.net
 *
 * @param <V>
 * @param <E>
 */
public class MinimumSpanningForest<V,E> {
	
	protected Graph<V,E> graph;
	protected Forest<V,E> forest;
	protected Map<E,Double> weights;
	
	/**
	 * Creates a Forest from the supplied Graph and supplied Factory, which
	 * is used to create a new, empty Forest. If non-null, the supplied root
	 * will be used as the root of the tree/forest. If the supplied root is
	 * null, or not present in the Graph, then an arbitrary Graph vertex
	 * will be selected as the root.
	 * If the Minimum Spanning Tree does not include all vertices of the
	 * Graph, then a leftover vertex is selected as a root, and another
	 * tree is created.
	 * @param graph the input graph
	 * @param factory the factory to use to create the new forest
	 * @param root the vertex of the graph to be used as the root of the forest 
	 * @param weights edge weights
	 */
	public MinimumSpanningForest(Graph<V, E> graph, Factory<Forest<V,E>> factory, 
			V root, Map<E, Double> weights) {
		this(graph, factory.create(), root, weights);
	}
	
	/**
	 * Creates a minimum spanning forest from the supplied graph, populating the
	 * supplied Forest, which must be empty. 
	 * If the supplied root is null, or not present in the Graph,
	 * then an arbitrary Graph vertex will be selected as the root.
	 * If the Minimum Spanning Tree does not include all vertices of the
	 * Graph, then a leftover vertex is selected as a root, and another
	 * tree is created
	 * @param graph the Graph to find MST in
	 * @param forest the Forest to populate. Must be empty
	 * @param root first Tree root, may be null
	 * @param weights edge weights, may be null
	 */
	public MinimumSpanningForest(Graph<V, E> graph, Forest<V,E> forest, 
			V root, Map<E, Double> weights) {
		
		if(forest.getVertexCount() != 0) {
			throw new IllegalArgumentException("Supplied Forest must be empty");
		}
		this.graph = graph;
		this.forest = forest;
		if(weights != null) {
			this.weights = weights;
		}
		Set<E> unfinishedEdges = new HashSet<E>(graph.getEdges());
		if(graph.getVertices().contains(root)) {
			this.forest.addVertex(root);
		}
		updateForest(forest.getVertices(), unfinishedEdges);
	}
	
    /**
     * Creates a minimum spanning forest from the supplied graph, populating the
     * supplied Forest, which must be empty. 
     * If the supplied root is null, or not present in the Graph,
     * then an arbitrary Graph vertex will be selected as the root.
     * If the Minimum Spanning Tree does not include all vertices of the
     * Graph, then a leftover vertex is selected as a root, and another
     * tree is created
     * @param graph the Graph to find MST in
     * @param forest the Forest to populate. Must be empty
     * @param root first Tree root, may be null
     */
    @SuppressWarnings("unchecked")
    public MinimumSpanningForest(Graph<V, E> graph, Forest<V,E> forest, 
            V root) {
        
        if(forest.getVertexCount() != 0) {
            throw new IllegalArgumentException("Supplied Forest must be empty");
        }
        this.graph = graph;
        this.forest = forest;
        this.weights = LazyMap.decorate(new HashMap<E,Double>(),
                new ConstantTransformer(1.0));
        Set<E> unfinishedEdges = new HashSet<E>(graph.getEdges());
        if(graph.getVertices().contains(root)) {
            this.forest.addVertex(root);
        }
        updateForest(forest.getVertices(), unfinishedEdges);
    }
	
	/**
	 * Returns the generated forest.
	 */
	public Forest<V,E> getForest() {
		return forest;
	}
	
	protected void updateForest(Collection<V> tv, Collection<E> unfinishedEdges) {
		double minCost = Double.MAX_VALUE;
		E nextEdge = null;
		V nextVertex = null;
		V currentVertex = null;
		for(E e : unfinishedEdges) {
			
			if(forest.getEdges().contains(e)) continue;
			// find the lowest cost edge, get its opposite endpoint,
			// and then update forest from its Successors
			Pair<V> endpoints = graph.getEndpoints(e);
			V first = endpoints.getFirst();
			V second = endpoints.getSecond();
			if(tv.contains(first) == true && tv.contains(second) == false) {
				if(weights.get(e) < minCost) {
					minCost = weights.get(e);
					nextEdge = e;
					currentVertex = first;
					nextVertex = second;
				}
			}
			if(graph.getEdgeType(e) == EdgeType.UNDIRECTED &&
					tv.contains(second) == true && tv.contains(first) == false) {
				if(weights.get(e) < minCost) {
					minCost = weights.get(e);
					nextEdge = e;
					currentVertex = second;
					nextVertex = first;
				}
			}
		}
		
		if(nextVertex != null && nextEdge != null) {
			unfinishedEdges.remove(nextEdge);
			forest.addEdge(nextEdge, currentVertex, nextVertex);
			updateForest(forest.getVertices(), unfinishedEdges);
		}
		Collection<V> leftovers = new HashSet<V>(graph.getVertices());
		leftovers.removeAll(forest.getVertices());
		if(leftovers.size() > 0) {
			V anotherRoot = leftovers.iterator().next();
			forest.addVertex(anotherRoot);
			updateForest(forest.getVertices(), unfinishedEdges);
		}
	}
}
