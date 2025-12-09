/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package edu.uci.ics.jung.graph.util;

/**
 * A class that is used to link together a graph element and a specific graph.
 * Provides appropriate implementations of <code>hashCode</code> and
 * <code>equals</code>.
 */
public class Context<G, E> {
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
	 * 
	 * @param <G>
	 *            the graph type
	 * @param <E>
	 *            the element type
	 */
	@SuppressWarnings("unchecked")
	public static <G, E> Context<G, E> getInstance(G graph, E element) {
		instance.graph = graph;
		instance.element = element;
		return instance;
	}

	@Override
	public int hashCode() {
		return graph.hashCode() ^ element.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Context)) {
			return false;
		}
		Context context = (Context) o;
		return context.graph.equals(graph) && context.element.equals(element);
	}
}
