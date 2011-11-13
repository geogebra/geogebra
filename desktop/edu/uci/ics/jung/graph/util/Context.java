package edu.uci.ics.jung.graph.util;

/**
 * A class that is used to link together a graph element and a specific graph.
 * Provides appropriate implementations of <code>hashCode</code> and <code>equals</code>.
 */
public class Context<G,E> 
{
	@SuppressWarnings("unchecked")
	private static Context instance = new Context();
	
	/**
	 * The graph element which defines this context.
	 */
	public G graph;

	/**
	 * The edge element which defines this context.
	 */
	public E element;
	
	/**
	 * Returns an instance of this type for the specified graph and element.
	 * @param <G> the graph type
	 * @param <E> the element type
	 */
	@SuppressWarnings("unchecked")
	public static <G,E> Context<G,E> getInstance(G graph, E element) {
		instance.graph = graph;
		instance.element = element;
		return instance;
	}
	
	@Override
	public int hashCode() {
		return graph.hashCode() ^ element.hashCode();
	}
	
	@SuppressWarnings("unchecked")
	@Override
    public boolean equals(Object o) {
        if (!(o instanceof Context))
            return false;
        Context context = (Context)o;
        return context.graph.equals(graph) && context.element.equals(element);
    }
}

