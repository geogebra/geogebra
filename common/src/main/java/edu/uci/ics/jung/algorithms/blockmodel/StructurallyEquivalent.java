/*
 * Copyright (c) 2004, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 * Created on Jan 28, 2004
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.algorithms.blockmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * Identifies sets of structurally equivalent vertices in a graph. Vertices <i>
 * i</i> and <i>j</i> are structurally equivalent iff the set of <i>i</i>'s
 * neighbors is identical to the set of <i>j</i>'s neighbors, with the
 * exception of <i>i</i> and <i>j</i> themselves. This algorithm finds all
 * sets of equivalent vertices in O(V^2) time.
 * 
 * <p>You can extend this class to have a different definition of equivalence (by
 * overriding <code>isStructurallyEquivalent</code>), and may give it hints for
 * accelerating the process by overriding <code>canPossiblyCompare</code>. 
 * (For example, in a bipartite graph, <code>canPossiblyCompare</code> may 
 * return <code>false</code> for vertices in
 * different partitions. This function should be fast.)
 * 
 * @author Danyel Fisher
 */
public class StructurallyEquivalent<V,E> implements Transformer<Graph<V,E>, VertexPartition<V,E>> 
{
	public VertexPartition<V,E> transform(Graph<V,E> g) 
	{
	    Set<Pair<V>> vertex_pairs = getEquivalentPairs(g);
	    
	    Set<Set<V>> rv = new HashSet<Set<V>>();
        Map<V, Set<V>> intermediate = new HashMap<V, Set<V>>();
        for (Pair<V> p : vertex_pairs)
        {
            Set<V> res = intermediate.get(p.getFirst());
            if (res == null)
                res = intermediate.get(p.getSecond());
            if (res == null)  // we haven't seen this one before
                res = new HashSet<V>();
            res.add(p.getFirst());
            res.add(p.getSecond());
            intermediate.put(p.getFirst(), res);
            intermediate.put(p.getSecond(), res);
        }
        rv.addAll(intermediate.values());

        // pick up the vertices which don't appear in intermediate; they are
        // singletons (equivalence classes of size 1)
        Collection<V> singletons = CollectionUtils.subtract(g.getVertices(),
                intermediate.keySet());
        for (V v : singletons)
        {
            Set<V> v_set = Collections.singleton(v);
            intermediate.put(v, v_set);
            rv.add(v_set);
        }

        return new VertexPartition<V, E>(g, intermediate, rv);
	}

	/**
	 * For each vertex pair v, v1 in G, checks whether v and v1 are fully
	 * equivalent: meaning that they connect to the exact same vertices. (Is
	 * this regular equivalence, or whathaveyou?)
	 * 
	 * Returns a Set of Pairs of vertices, where all the vertices in the inner
	 * Pairs are equivalent.
	 * 
	 * @param g
	 */
	protected Set<Pair<V>> getEquivalentPairs(Graph<V,?> g) {

		Set<Pair<V>> rv = new HashSet<Pair<V>>();
		Set<V> alreadyEquivalent = new HashSet<V>();

		List<V> l = new ArrayList<V>(g.getVertices());

		for (V v1 : l)
		{
			if (alreadyEquivalent.contains(v1))
				continue;

			for (Iterator<V> iterator = l.listIterator(l.indexOf(v1) + 1); iterator.hasNext();) {
			    V v2 = iterator.next();

				if (alreadyEquivalent.contains(v2))
					continue;

				if (!canPossiblyCompare(v1, v2))
					continue;

				if (isStructurallyEquivalent(g, v1, v2)) {
					Pair<V> p = new Pair<V>(v1, v2);
					alreadyEquivalent.add(v2);
					rv.add(p);
				}
			}
		}
		
		return rv;
	}

	/**
	 * Checks whether a pair of vertices are structurally equivalent.
	 * Specifically, whether v1's predecessors are equal to v2's predecessors,
	 * and same for successors.
	 * 
	 * @param g the graph in which the structural equivalence comparison is to take place
	 * @param v1 the vertex to check for structural equivalence to v2
	 * @param v2 the vertex to check for structural equivalence to v1
	 */
	protected boolean isStructurallyEquivalent(Graph<V,?> g, V v1, V v2) {
		
		if( g.degree(v1) != g.degree(v2)) {
			return false;
		}

		Set<V> n1 = new HashSet<V>(g.getPredecessors(v1));
		n1.remove(v2);
		n1.remove(v1);
		Set<V> n2 = new HashSet<V>(g.getPredecessors(v2));
		n2.remove(v1);
		n2.remove(v2);

		Set<V> o1 = new HashSet<V>(g.getSuccessors(v1));
		Set<V> o2 = new HashSet<V>(g.getSuccessors(v2));
		o1.remove(v1);
		o1.remove(v2);
		o2.remove(v1);
		o2.remove(v2);

		// this neglects self-loops and directed edges from 1 to other
		boolean b = (n1.equals(n2) && o1.equals(o2));
		if (!b)
			return b;
		
		// if there's a directed edge v1->v2 then there's a directed edge v2->v1
		b &= ( g.isSuccessor(v1, v2) == g.isSuccessor(v2, v1));
		
		// self-loop check
		b &= ( g.isSuccessor(v1, v1) == g.isSuccessor(v2, v2));

		return b;

	}

	/**
	 * This is a space for optimizations. For example, for a bipartite graph,
	 * vertices from different partitions cannot possibly be compared.
	 * 
	 * @param v1
	 * @param v2
	 */
	protected boolean canPossiblyCompare(V v1, V v2) {
		return true;
	}

}
