package edu.uci.ics.jung.algorithms.shortestpath;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.algorithms.filters.FilterUtils;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Tree;
import edu.uci.ics.jung.graph.util.TreeUtils;

/**
 * For the input Graph, creates a MinimumSpanningTree using a variation of
 * Prim's algorithm.
 * 
 * @author Tom Nelson - tomnelson@dev.java.net
 *
 * @param <V> vertex type
 * @param <E> edge type
 */
@SuppressWarnings("unchecked")
public class MinimumSpanningForest2<V, E> {

	protected Graph<V, E> graph;
	protected Forest<V, E> forest;
	protected Function<E, Double> weights = ignore -> 1.0;

	/**
	 * create a Forest from the supplied Graph and supplied Factory, which is
	 * used to create a new, empty Forest. If non-null, the supplied root will
	 * be used as the root of the tree/forest. If the supplied root is null, or
	 * not present in the Graph, then an arbitrary Graph vertex will be selected
	 * as the root. If the Minimum Spanning Tree does not include all vertices
	 * of the Graph, then a leftover vertex is selected as a root, and another
	 * tree is created
	 * 
	 * @param graph graph
	 * @param factory forest factory
	 * @param weights edge weights
	 */
	public MinimumSpanningForest2(Graph<V, E> graph,
			Supplier<Forest<V, E>> factory,
			Supplier<? extends Graph<V, E>> treeFactory,
			Function<E, Double> weights) {
		this(graph, factory.get(), treeFactory, weights);
	}

	/**
	 * create a forest from the supplied graph, populating the supplied Forest,
	 * which must be empty. If the supplied root is null, or not present in the
	 * Graph, then an arbitrary Graph vertex will be selected as the root. If the
	 * Minimum Spanning Tree does not include all vertices of the Graph, then a
	 * leftover vertex is selected as a root, and another tree is created
	 * 
	 * @param graph
	 *            the Graph to find MST in
	 * @param forest
	 *            the Forest to populate. Must be empty
	 * @param weights
	 *            edge weights, may be null
	 */
	public MinimumSpanningForest2(Graph<V, E> graph, Forest<V, E> forest,
			Supplier<? extends Graph<V, E>> treeFactory,
			Function<E, Double> weights) {

		if (forest.getVertexCount() != 0) {
			throw new IllegalArgumentException("Supplied Forest must be empty");
		}
		this.graph = graph;
		this.forest = forest;
		if (weights != null) {
			this.weights = weights;
		}

		WeakComponentClusterer<V, E> wcc = new WeakComponentClusterer<V, E>();
		Set<Set<V>> component_vertices = wcc.apply(graph);
		Collection<Graph<V, E>> components = FilterUtils
				.createAllInducedSubgraphs(component_vertices, graph);

		for (Graph<V, E> component : components) {
			PrimMinimumSpanningTree<V, E> mst = new PrimMinimumSpanningTree<V, E>(
					treeFactory, this.weights);
			Graph<V, E> subTree = mst.apply(component);
			if (subTree instanceof Tree) {
				TreeUtils.addSubTree(forest, (Tree<V, E>) subTree, null, null);
			}
		}
	}

	/**
	 * Returns the generated forest.
	 */
	public Forest<V, E> getForest() {
		return forest;
	}
}
