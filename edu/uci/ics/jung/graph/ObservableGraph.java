package edu.uci.ics.jung.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import edu.uci.ics.jung.graph.event.GraphEvent;
import edu.uci.ics.jung.graph.event.GraphEventListener;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * A decorator class for graphs which generates events 
 * 
 * @author Joshua O'Madadhain
 */
@SuppressWarnings("serial")
public class ObservableGraph<V,E> extends GraphDecorator<V,E> {

	List<GraphEventListener<V,E>> listenerList = 
		Collections.synchronizedList(new LinkedList<GraphEventListener<V,E>>());

    /**
     * Creates a new instance based on the provided {@code delegate}.
     */
	public ObservableGraph(Graph<V, E> delegate) {
		super(delegate);
	}
	
	/**
	 * Adds {@code l} as a listener to this graph.
	 */
	public void addGraphEventListener(GraphEventListener<V,E> l) {
		listenerList.add(l);
	}

    /**
     * Removes {@code l} as a listener to this graph.
     */
	public void removeGraphEventListener(GraphEventListener<V,E> l) {
		listenerList.remove(l);
	}

	protected void fireGraphEvent(GraphEvent<V,E> evt) {
		for(GraphEventListener<V,E> listener : listenerList) {
			listener.handleGraphEvent(evt);
		 }
	 }

	/**
	 * @see edu.uci.ics.jung.graph.Hypergraph#addEdge(java.lang.Object, java.util.Collection)
	 */
	@Override
	public boolean addEdge(E edge, Collection<? extends V> vertices) {
		boolean state = super.addEdge(edge, vertices);
		if(state) {
			GraphEvent<V,E> evt = new GraphEvent.Edge<V,E>(delegate, GraphEvent.Type.EDGE_ADDED, edge);
			fireGraphEvent(evt);
		}
		return state;
	}

	/**
	 * @see edu.uci.ics.jung.graph.Graph#addEdge(java.lang.Object, java.lang.Object, java.lang.Object, edu.uci.ics.jung.graph.util.EdgeType)
	 */
	@Override
  public boolean addEdge(E e, V v1, V v2, EdgeType edgeType) {
		boolean state = super.addEdge(e, v1, v2, edgeType);
		if(state) {
			GraphEvent<V,E> evt = new GraphEvent.Edge<V,E>(delegate, GraphEvent.Type.EDGE_ADDED, e);
			fireGraphEvent(evt);
		}
		return state;
	}

	/**
	 * @see edu.uci.ics.jung.graph.Graph#addEdge(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	@Override
  public boolean addEdge(E e, V v1, V v2) {
		boolean state = super.addEdge(e, v1, v2);
		if(state) {
			GraphEvent<V,E> evt = new GraphEvent.Edge<V,E>(delegate, GraphEvent.Type.EDGE_ADDED, e);
			fireGraphEvent(evt);
		}
		return state;
	}

	/**
	 * @see edu.uci.ics.jung.graph.Hypergraph#addVertex(java.lang.Object)
	 */
	@Override
  public boolean addVertex(V vertex) {
		boolean state = super.addVertex(vertex);
		if(state) {
			GraphEvent<V,E> evt = new GraphEvent.Vertex<V,E>(delegate, GraphEvent.Type.VERTEX_ADDED, vertex);
			fireGraphEvent(evt);
		}
		return state;
	}

	/**
	 * @see edu.uci.ics.jung.graph.Hypergraph#removeEdge(java.lang.Object)
	 */
	@Override
  public boolean removeEdge(E edge) {
		boolean state = delegate.removeEdge(edge);
		if(state) {
			GraphEvent<V,E> evt = new GraphEvent.Edge<V,E>(delegate, GraphEvent.Type.EDGE_REMOVED, edge);
			fireGraphEvent(evt);
		}
		return state;
	}

	/**
	 * @see edu.uci.ics.jung.graph.Hypergraph#removeVertex(java.lang.Object)
	 */
	@Override
	public boolean removeVertex(V vertex) {
		// remove all incident edges first, so that the appropriate events will
		// be fired (otherwise they'll be removed inside {@code delegate.removeVertex}
		// and the events will not be fired)
		Collection<E> incident_edges = new ArrayList<E>(delegate.getIncidentEdges(vertex));
		for (E e : incident_edges) 
			this.removeEdge(e);
		
		boolean state = delegate.removeVertex(vertex);
		if(state) {
			GraphEvent<V,E> evt = new GraphEvent.Vertex<V,E>(delegate, GraphEvent.Type.VERTEX_REMOVED, vertex);
			fireGraphEvent(evt);
		}
		return state;
	}

}
