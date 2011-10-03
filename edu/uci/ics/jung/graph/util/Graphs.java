package edu.uci.ics.jung.graph.util;

import java.io.Serializable;
import java.util.Collection;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Tree;
import edu.uci.ics.jung.graph.UndirectedGraph;
/**
 * Provides specialized implementations of <code>GraphDecorator</code>.  Currently these 
 * wrapper types include "synchronized" and "unmodifiable".
 *
 * <p>The methods of this class may each throw a <code>NullPointerException</code>
 * if the graphs or class objects provided to them are null.
 *
 * @author Tom Nelson
 */

public class Graphs {
	
	/**
	 * Returns a synchronized graph backed by the passed argument graph.
	 * @param <V> the vertex type
	 * @param <E> the edge type
	 * @param graph the graph for which a synchronized wrapper is to be created
	 * @return a synchronized graph backed by the passed argument graph
	 */
	public static <V,E> Graph<V,E> synchronizedGraph(Graph<V,E> graph) {
		return new SynchronizedGraph<V,E>(graph);
	}
	
	/**
	 * Returns a synchronized DirectedGraph backed by the passed DirectedGraph.
     * @param <V> the vertex type
     * @param <E> the edge type
     * @param graph the graph for which a synchronized wrapper is to be created
	 * @return a synchronized DirectedGraph backed by the passed DirectedGraph
	 */
	public static <V,E> DirectedGraph<V,E> synchronizedDirectedGraph(DirectedGraph<V,E> graph) {
		return new SynchronizedDirectedGraph<V,E>(graph);
	}
	
	/**
	 * Returns a synchronized UndirectedGraph backed by the passed UndirectedGraph.
     * @param <V> the vertex type
     * @param <E> the edge type
     * @param graph the graph for which a synchronized wrapper is to be created
	 * @return a synchronized UndirectedGraph backed by the passed UndirectedGraph
	 */
	public static <V,E> UndirectedGraph<V,E> synchronizedUndirectedGraph(UndirectedGraph<V,E> graph) {
		return new SynchronizedUndirectedGraph<V,E>(graph);
	}
	
	/**
	 * Returns a synchronized Forest backed by the passed Forest.
     * @param <V> the vertex type
     * @param <E> the edge type
     * @param forest the forest for which a synchronized wrapper is to be created
	 * @return a synchronized Forest backed by the passed Forest
	 */
	public static <V,E> SynchronizedForest<V,E> synchronizedForest(Forest<V,E> forest) {
		return new SynchronizedForest<V,E>(forest);
	}
	
	/**
	 * Returns a synchronized Tree backed by the passed Tree.
     * @param <V> the vertex type
     * @param <E> the edge type
     * @param tree the tree for which a synchronized wrapper is to be created
	 * @return a synchronized Tree backed by the passed Tree
	 */
	public static <V,E> SynchronizedTree<V,E> synchronizedTree(Tree<V,E> tree) {
		return new SynchronizedTree<V,E>(tree);
	}
	
	/**
	 * Returns an unmodifiable Graph backed by the passed Graph.
     * @param <V> the vertex type
     * @param <E> the edge type
     * @param graph the graph for which the unmodifiable wrapper is to be returned
	 * @return an unmodifiable Graph backed by the passed Graph
	 */
	public static <V,E> Graph<V,E> unmodifiableGraph(Graph<V,E> graph) {
		return new UnmodifiableGraph<V,E>(graph);
	}
	
	/**
	 * Returns an unmodifiable <code>DirectedGraph</code> backed by the passed graph.
	 * @param <V> the vertex type
	 * @param <E> the edge type
	 * @param graph the graph for which the unmodifiable wrapper is to be returned
	 * @return an unmodifiable <code>DirectedGraph</code> backed by the passed graph
	 */
	public static <V,E> DirectedGraph<V,E> unmodifiableDirectedGraph(DirectedGraph<V,E> graph) {
		return new UnmodifiableDirectedGraph<V,E>(graph);
	}
	
	/**
	 * Returns an unmodifiable <code>UndirectedGraph</code> backed by the passed graph.
	 * @param <V> the vertex type
	 * @param <E> the edge type
	 * @param graph the graph for which the unmodifiable wrapper is to be returned
	 * @return an unmodifiable <code>UndirectedGraph</code> backed by the passed graph
	 */
	public static <V,E> UndirectedGraph<V,E> unmodifiableUndirectedGraph(UndirectedGraph<V,E> graph) {
		return new UnmodifiableUndirectedGraph<V,E>(graph);
	}
	
	/**
     * Returns an unmodifiable <code>Tree</code> backed by the passed tree.
     * @param <V> the vertex type
     * @param <E> the edge type
     * @param tree the tree for which the unmodifiable wrapper is to be returned
     * @return an unmodifiable <code>Tree</code> backed by the passed tree
	 */
	public static <V,E> UnmodifiableTree<V,E> unmodifiableTree(Tree<V,E> tree) {
		return new UnmodifiableTree<V,E>(tree);
	}
	
    /**
     * Returns an unmodifiable <code>Forest</code> backed by the passed forest.
     * @param <V> the vertex type
     * @param <E> the edge type
     * @param forest the forest for which the unmodifiable wrapper is to be returned
     * @return an unmodifiable <code>Forest</code> backed by the passed forest
     */
	public static <V,E> UnmodifiableForest<V,E> unmodifiableForest(Forest<V,E> forest) {
		return new UnmodifiableForest<V,E>(forest);
	}
	
	
	@SuppressWarnings("serial")
	static abstract class SynchronizedAbstractGraph<V,E> implements Graph<V,E>, Serializable {
		protected Graph<V,E> delegate;

		private SynchronizedAbstractGraph(Graph<V, E> delegate) {
			if(delegate == null) {
				throw new NullPointerException();
			}
			this.delegate = delegate;
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#getDefaultEdgeType()
		 */
		public EdgeType getDefaultEdgeType()
		{
			return delegate.getDefaultEdgeType();
		}
		
		/**
		 * @see edu.uci.ics.jung.graph.Graph#addEdge(Object, Object, Object, EdgeType)
		 */
		public synchronized boolean addEdge(E e, V v1, V v2, EdgeType edgeType) {
			return delegate.addEdge(e, v1, v2, edgeType);
		}

        /**
         * @see edu.uci.ics.jung.graph.Hypergraph#addEdge(Object, Collection, EdgeType)
         */
		public synchronized boolean addEdge(E e, Collection<? extends V> 
			vertices, EdgeType edgeType) 
		{
			return delegate.addEdge(e, vertices, edgeType);
		}
		
		/**
		 * @see edu.uci.ics.jung.graph.Graph#addEdge(Object, Object, Object)
		 */
		public synchronized boolean addEdge(E e, V v1, V v2) {
			return delegate.addEdge(e, v1, v2);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#addVertex(java.lang.Object)
		 */
		public synchronized boolean addVertex(V vertex) {
			return delegate.addVertex(vertex);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#isIncident(java.lang.Object, java.lang.Object)
		 */
		public synchronized boolean isIncident(V vertex, E edge) {
			return delegate.isIncident(vertex, edge);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#isNeighbor(java.lang.Object, java.lang.Object)
		 */
		public synchronized boolean isNeighbor(V v1, V v2) {
			return delegate.isNeighbor(v1, v2);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#degree(java.lang.Object)
		 */
		public synchronized int degree(V vertex) {
			return delegate.degree(vertex);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#findEdge(java.lang.Object, java.lang.Object)
		 */
		public synchronized E findEdge(V v1, V v2) {
			return delegate.findEdge(v1, v2);
		}

        /**
         * @see edu.uci.ics.jung.graph.Hypergraph#findEdgeSet(java.lang.Object, java.lang.Object)
         */
        public synchronized Collection<E> findEdgeSet(V v1, V v2)
        {
            return delegate.findEdgeSet(v1, v2);
        }
        
		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#getEdges()
		 */
		public synchronized Collection<E> getEdges() {
			return delegate.getEdges();
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#getEdges(EdgeType)
		 */
		public synchronized Collection<E> getEdges(EdgeType edgeType) {
			return delegate.getEdges(edgeType);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#getEndpoints(java.lang.Object)
		 */
		public synchronized Pair<V> getEndpoints(E edge) {
			return delegate.getEndpoints(edge);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#getIncidentEdges(java.lang.Object)
		 */
		public synchronized Collection<E> getIncidentEdges(V vertex) {
			return delegate.getIncidentEdges(vertex);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#getIncidentVertices(java.lang.Object)
		 */
		public synchronized Collection<V> getIncidentVertices(E edge) {
			return delegate.getIncidentVertices(edge);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#getInEdges(java.lang.Object)
		 */
		public synchronized Collection<E> getInEdges(V vertex) {
			return delegate.getInEdges(vertex);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#getNeighbors(java.lang.Object)
		 */
		public synchronized Collection<V> getNeighbors(V vertex) {
			return delegate.getNeighbors(vertex);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#getOpposite(java.lang.Object, java.lang.Object)
		 */
		public synchronized V getOpposite(V vertex, E edge) {
			return delegate.getOpposite(vertex, edge);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#getOutEdges(java.lang.Object)
		 */
		public synchronized Collection<E> getOutEdges(V vertex) {
			return delegate.getOutEdges(vertex);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#getPredecessors(java.lang.Object)
		 */
		public synchronized Collection<V> getPredecessors(V vertex) {
			return delegate.getPredecessors(vertex);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#getSuccessors(java.lang.Object)
		 */
		public synchronized Collection<V> getSuccessors(V vertex) {
			return delegate.getSuccessors(vertex);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#getVertices()
		 */
		public synchronized Collection<V> getVertices() {
			return delegate.getVertices();
		}

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#getEdgeCount()
		 */
		public synchronized int getEdgeCount() {
			return delegate.getEdgeCount();
		}

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#getEdgeCount(EdgeType)
		 */
		public synchronized int getEdgeCount(EdgeType edge_type) 
		{
		    return delegate.getEdgeCount(edge_type);
		}
		
		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#getVertexCount()
		 */
		public synchronized int getVertexCount() {
			return delegate.getVertexCount();
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#inDegree(java.lang.Object)
		 */
		public synchronized int inDegree(V vertex) {
			return delegate.inDegree(vertex);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#getEdgeType(java.lang.Object)
		 */
		public synchronized EdgeType getEdgeType(E edge) {
			return delegate.getEdgeType(edge);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#isPredecessor(java.lang.Object, java.lang.Object)
		 */
		public synchronized boolean isPredecessor(V v1, V v2) {
			return delegate.isPredecessor(v1, v2);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#isSuccessor(java.lang.Object, java.lang.Object)
		 */
		public synchronized boolean isSuccessor(V v1, V v2) {
			return delegate.isSuccessor(v1, v2);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#getNeighborCount(java.lang.Object)
		 */
		public synchronized int getNeighborCount(V vertex) {
			return delegate.getNeighborCount(vertex);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#getPredecessorCount(java.lang.Object)
		 */
		public synchronized int getPredecessorCount(V vertex) {
			return delegate.getPredecessorCount(vertex);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#getSuccessorCount(java.lang.Object)
		 */
		public synchronized int getSuccessorCount(V vertex) {
			return delegate.getSuccessorCount(vertex);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#outDegree(java.lang.Object)
		 */
		public synchronized int outDegree(V vertex) {
			return delegate.outDegree(vertex);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#removeEdge(java.lang.Object)
		 */
		public synchronized boolean removeEdge(E edge) {
			return delegate.removeEdge(edge);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#removeVertex(java.lang.Object)
		 */
		public synchronized boolean removeVertex(V vertex) {
			return delegate.removeVertex(vertex);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#getDest(java.lang.Object)
		 */
		public synchronized V getDest(E directed_edge) {
			return delegate.getDest(directed_edge);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#getSource(java.lang.Object)
		 */
		public synchronized V getSource(E directed_edge) {
			return delegate.getSource(directed_edge);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#isDest(java.lang.Object, java.lang.Object)
		 */
		public synchronized boolean isDest(V vertex, E edge) {
			return delegate.isDest(vertex, edge);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#isSource(java.lang.Object, java.lang.Object)
		 */
		public synchronized boolean isSource(V vertex, E edge) {
			return delegate.isSource(vertex, edge);
		}
		
		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#getIncidentCount(Object)
		 */
        public synchronized int getIncidentCount(E edge)
        {
            return delegate.getIncidentCount(edge);
        }

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#addEdge(java.lang.Object, java.util.Collection)
		 */
		public synchronized boolean addEdge(E hyperedge, Collection<? extends V> vertices) {
			return delegate.addEdge(hyperedge, vertices);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#containsEdge(java.lang.Object)
		 */
		public synchronized boolean containsEdge(E edge) {
			return delegate.containsEdge(edge);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#containsVertex(java.lang.Object)
		 */
		public synchronized boolean containsVertex(V vertex) {
			return delegate.containsVertex(vertex);
		}
        
	}
	
	@SuppressWarnings("serial")
    static class SynchronizedGraph<V,E> extends SynchronizedAbstractGraph<V,E> implements Serializable {
		
		private SynchronizedGraph(Graph<V,E> delegate) {
			super(delegate);
		}
	}
	
	@SuppressWarnings("serial")
    static class SynchronizedUndirectedGraph<V,E> extends SynchronizedAbstractGraph<V,E> 
		implements UndirectedGraph<V,E>, Serializable {
		private SynchronizedUndirectedGraph(UndirectedGraph<V,E> delegate) {
			super(delegate);
		}
	}
	
	@SuppressWarnings("serial")
    static class SynchronizedDirectedGraph<V,E> extends SynchronizedAbstractGraph<V,E> 
		implements DirectedGraph<V,E>, Serializable {
		
		private SynchronizedDirectedGraph(DirectedGraph<V,E> delegate) {
			super(delegate);
		}

		@Override
	    public synchronized V getDest(E directed_edge) {
			return ((DirectedGraph<V,E>)delegate).getDest(directed_edge);
		}

		@Override
    	public synchronized V getSource(E directed_edge) {
			return ((DirectedGraph<V,E>)delegate).getSource(directed_edge);
		}

		@Override
	    public synchronized boolean isDest(V vertex, E edge) {
			return ((DirectedGraph<V,E>)delegate).isDest(vertex, edge);
		}

		@Override
    	public synchronized boolean isSource(V vertex, E edge) {
			return ((DirectedGraph<V,E>)delegate).isSource(vertex, edge);
		}
	}
	
	@SuppressWarnings("serial")
    static class SynchronizedTree<V,E> extends SynchronizedForest<V,E> implements Tree<V,E> {

		/**
		 * Creates a new instance based on the provided {@code delegate}.
		 * @param delegate
		 */
		public SynchronizedTree(Tree<V, E> delegate) {
			super(delegate);
		}

		public synchronized int getDepth(V vertex) {
			return ((Tree<V,E>)delegate).getDepth(vertex);
		}

		public synchronized int getHeight() {
			return ((Tree<V,E>)delegate).getHeight();
		}

		public synchronized V getRoot() {
			return ((Tree<V,E>)delegate).getRoot();
		}
	}
	
	@SuppressWarnings("serial")
    static class SynchronizedForest<V,E> extends SynchronizedDirectedGraph<V,E> implements Forest<V,E> {

        /**
         * Creates a new instance based on the provided {@code delegate}.
         * @param delegate
         */
		public SynchronizedForest(Forest<V, E> delegate) {
			super(delegate);
		}

		public synchronized Collection<Tree<V, E>> getTrees() {
			return ((Forest<V,E>)delegate).getTrees();
		}

        public int getChildCount(V vertex)
        {
            return ((Forest<V,E>)delegate).getChildCount(vertex);
        }

        public Collection<E> getChildEdges(V vertex)
        {
            return ((Forest<V,E>)delegate).getChildEdges(vertex);
        }

        public Collection<V> getChildren(V vertex)
        {
            return ((Forest<V,E>)delegate).getChildren(vertex);
        }

        public V getParent(V vertex)
        {
            return ((Forest<V,E>)delegate).getParent(vertex);
        }

        public E getParentEdge(V vertex)
        {
            return ((Forest<V,E>)delegate).getParentEdge(vertex);
        }
	}
	
	@SuppressWarnings("serial")
	static abstract class UnmodifiableAbstractGraph<V,E> implements Graph<V,E>, Serializable {
		protected Graph<V,E> delegate;


		private UnmodifiableAbstractGraph(Graph<V, E> delegate) {
			if(delegate == null) {
				throw new NullPointerException();
			}
			this.delegate = delegate;
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#getDefaultEdgeType()
		 */
		public EdgeType getDefaultEdgeType()
		{
			return delegate.getDefaultEdgeType();
		}
		
		/**
		 * @see edu.uci.ics.jung.graph.Graph#addEdge(Object, Object, Object, EdgeType)
		 */
		public boolean addEdge(E e, V v1, V v2, EdgeType edgeType) {
			throw new UnsupportedOperationException();
		}

        /**
         * @see edu.uci.ics.jung.graph.Graph#addEdge(Object, Collection, EdgeType)
         */
		public boolean addEdge(E e, Collection<? extends V> vertices, 
			EdgeType edgeType) 
		{
			throw new UnsupportedOperationException();
		}
		
        /**
         * @see edu.uci.ics.jung.graph.Graph#addEdge(Object, Object, Object)
         */
		public boolean addEdge(E e, V v1, V v2) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#addVertex(java.lang.Object)
		 */
		public boolean addVertex(V vertex) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#isIncident(java.lang.Object, java.lang.Object)
		 */
		public boolean isIncident(V vertex, E edge) {
			return delegate.isIncident(vertex, edge);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#isNeighbor(java.lang.Object, java.lang.Object)
		 */
		public boolean isNeighbor(V v1, V v2) {
			return delegate.isNeighbor(v1, v2);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#degree(java.lang.Object)
		 */
		public int degree(V vertex) {
			return delegate.degree(vertex);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#findEdge(java.lang.Object, java.lang.Object)
		 */
		public E findEdge(V v1, V v2) {
			return delegate.findEdge(v1, v2);
		}
        
        /**
         * @see edu.uci.ics.jung.graph.Hypergraph#findEdgeSet(java.lang.Object, java.lang.Object)
         */
        public Collection<E> findEdgeSet(V v1, V v2)
        {
            return delegate.findEdgeSet(v1, v2);
        }

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#getEdges()
		 */
		public Collection<E> getEdges() {
			return delegate.getEdges();
		}

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#getEdgeCount()
		 */
		public int getEdgeCount() {
			return delegate.getEdgeCount();
		}

        /**
         * @see edu.uci.ics.jung.graph.Hypergraph#getEdgeCount(EdgeType)
         */
        public int getEdgeCount(EdgeType edge_type) 
        {
            return delegate.getEdgeCount(edge_type);
        }

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#getVertexCount()
		 */
		public int getVertexCount() {
			return delegate.getVertexCount();
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#getEdges(edu.uci.ics.jung.graph.util.EdgeType)
		 */
		public Collection<E> getEdges(EdgeType edgeType) {
			return delegate.getEdges(edgeType);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#getEndpoints(java.lang.Object)
		 */
		public Pair<V> getEndpoints(E edge) {
			return delegate.getEndpoints(edge);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#getIncidentEdges(java.lang.Object)
		 */
		public Collection<E> getIncidentEdges(V vertex) {
			return delegate.getIncidentEdges(vertex);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#getIncidentVertices(java.lang.Object)
		 */
		public Collection<V> getIncidentVertices(E edge) {
			return delegate.getIncidentVertices(edge);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#getInEdges(java.lang.Object)
		 */
		public Collection<E> getInEdges(V vertex) {
			return delegate.getInEdges(vertex);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#getNeighbors(java.lang.Object)
		 */
		public Collection<V> getNeighbors(V vertex) {
			return delegate.getNeighbors(vertex);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#getOpposite(java.lang.Object, java.lang.Object)
		 */
		public V getOpposite(V vertex, E edge) {
			return delegate.getOpposite(vertex, edge);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#getOutEdges(java.lang.Object)
		 */
		public Collection<E> getOutEdges(V vertex) {
			return delegate.getOutEdges(vertex);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#getPredecessors(java.lang.Object)
		 */
		public Collection<V> getPredecessors(V vertex) {
			return delegate.getPredecessors(vertex);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#getSuccessors(java.lang.Object)
		 */
		public Collection<V> getSuccessors(V vertex) {
			return delegate.getSuccessors(vertex);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#getVertices()
		 */
		public Collection<V> getVertices() {
			return delegate.getVertices();
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#inDegree(java.lang.Object)
		 */
		public int inDegree(V vertex) {
			return delegate.inDegree(vertex);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#getEdgeType(java.lang.Object)
		 */
		public EdgeType getEdgeType(E edge) {
			return delegate.getEdgeType(edge);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#isPredecessor(java.lang.Object, java.lang.Object)
		 */
		public boolean isPredecessor(V v1, V v2) {
			return delegate.isPredecessor(v1, v2);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#isSuccessor(java.lang.Object, java.lang.Object)
		 */
		public boolean isSuccessor(V v1, V v2) {
			return delegate.isSuccessor(v1, v2);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#getNeighborCount(java.lang.Object)
		 */
		public int getNeighborCount(V vertex) {
			return delegate.getNeighborCount(vertex);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#getPredecessorCount(java.lang.Object)
		 */
		public int getPredecessorCount(V vertex) {
			return delegate.getPredecessorCount(vertex);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#getSuccessorCount(java.lang.Object)
		 */
		public int getSuccessorCount(V vertex) {
			return delegate.getSuccessorCount(vertex);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#outDegree(java.lang.Object)
		 */
		public int outDegree(V vertex) {
			return delegate.outDegree(vertex);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#removeEdge(java.lang.Object)
		 */
		public boolean removeEdge(E edge) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#removeVertex(java.lang.Object)
		 */
		public boolean removeVertex(V vertex) {
			throw new UnsupportedOperationException();
		}
		
		/**
		 * @see edu.uci.ics.jung.graph.Graph#getDest(java.lang.Object)
		 */
		public V getDest(E directed_edge) {
			return delegate.getDest(directed_edge);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#getSource(java.lang.Object)
		 */
		public V getSource(E directed_edge) {
			return delegate.getSource(directed_edge);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#isDest(java.lang.Object, java.lang.Object)
		 */
		public boolean isDest(V vertex, E edge) {
			return delegate.isDest(vertex, edge);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Graph#isSource(java.lang.Object, java.lang.Object)
		 */
		public boolean isSource(V vertex, E edge) {
			return delegate.isSource(vertex, edge);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#getIncidentCount(Object)
		 */
        public int getIncidentCount(E edge)
        {
            return delegate.getIncidentCount(edge);
        }

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#addEdge(java.lang.Object, java.util.Collection)
		 */
		public boolean addEdge(E hyperedge, Collection<? extends V> vertices) {
			return delegate.addEdge(hyperedge, vertices);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#containsEdge(java.lang.Object)
		 */
		public boolean containsEdge(E edge) {
			return delegate.containsEdge(edge);
		}

		/**
		 * @see edu.uci.ics.jung.graph.Hypergraph#containsVertex(java.lang.Object)
		 */
		public boolean containsVertex(V vertex) {
			return delegate.containsVertex(vertex);
		}
	}
	
	@SuppressWarnings("serial")
    static class UnmodifiableGraph<V,E> extends UnmodifiableAbstractGraph<V,E> implements Serializable {
		private UnmodifiableGraph(Graph<V,E> delegate) {
			super(delegate);
		}
	}
	
	@SuppressWarnings("serial")
    static class UnmodifiableDirectedGraph<V,E> extends UnmodifiableAbstractGraph<V,E> 
		implements DirectedGraph<V,E>, Serializable {
		private UnmodifiableDirectedGraph(DirectedGraph<V,E> delegate) {
			super(delegate);
		}

		@Override
	    public V getDest(E directed_edge) {
			return ((DirectedGraph<V,E>)delegate).getDest(directed_edge);
		}

		@Override
    	public V getSource(E directed_edge) {
			return ((DirectedGraph<V,E>)delegate).getSource(directed_edge);
		}

		@Override
    	public boolean isDest(V vertex, E edge) {
			return ((DirectedGraph<V,E>)delegate).isDest(vertex, edge);
		}

		@Override
    	public boolean isSource(V vertex, E edge) {
			return ((DirectedGraph<V,E>)delegate).isSource(vertex, edge);
		}
	}
	
	@SuppressWarnings("serial")
    static class UnmodifiableUndirectedGraph<V,E> extends UnmodifiableAbstractGraph<V,E> 
		implements UndirectedGraph<V,E>, Serializable {
		private UnmodifiableUndirectedGraph(UndirectedGraph<V,E> delegate) {
			super(delegate);
		}
	}
	
	@SuppressWarnings("serial")
    static class UnmodifiableForest<V,E> extends UnmodifiableGraph<V,E>
		implements Forest<V,E>, Serializable {
		private UnmodifiableForest(Forest<V,E> delegate) {
			super(delegate);
		}

		public Collection<Tree<V, E>> getTrees() {
			return ((Forest<V,E>)delegate).getTrees();
		}

        public int getChildCount(V vertex)
        {
            return ((Forest<V,E>)delegate).getChildCount(vertex);
        }

        public Collection<E> getChildEdges(V vertex)
        {
            return ((Forest<V,E>)delegate).getChildEdges(vertex);
        }

        public Collection<V> getChildren(V vertex)
        {
            return ((Forest<V,E>)delegate).getChildren(vertex);
        }

        public V getParent(V vertex)
        {
            return ((Forest<V,E>)delegate).getParent(vertex);
        }

        public E getParentEdge(V vertex)
        {
            return ((Forest<V,E>)delegate).getParentEdge(vertex);
        }
	}
	
	@SuppressWarnings("serial")
    static class UnmodifiableTree<V,E> extends UnmodifiableForest<V,E>
	     implements Tree<V,E>, Serializable {
		private UnmodifiableTree(Tree<V,E> delegate) {
			super(delegate);
		}

		public int getDepth(V vertex) {
			return ((Tree<V,E>)delegate).getDepth(vertex);
		}

		public int getHeight() {
			return ((Tree<V,E>)delegate).getHeight();
		}

		public V getRoot() {
			return ((Tree<V,E>)delegate).getRoot();
		}

		@Override
    	public Collection<Tree<V, E>> getTrees() {
			return ((Tree<V,E>)delegate).getTrees();
		}
	}

}
