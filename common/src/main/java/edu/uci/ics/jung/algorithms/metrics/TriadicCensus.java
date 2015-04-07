/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.algorithms.metrics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.CollectionUtils;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;


/**
 * TriadicCensus is a standard social network tool that counts, for each of the 
 * different possible configurations of three vertices, the number of times
 * that that configuration occurs in the given graph.
 * This may then be compared to the set of expected counts for this particular
 * graph or to an expected sample. This is often used in p* modeling.
 * <p>
 * To use this class, 
 * <pre>
 * long[] triad_counts = TriadicCensus(dg);
 * </pre>
 * where <code>dg</code> is a <code>DirectedGraph</code>.
 * ith element of the array (for i in [1,16]) is the number of 
 * occurrences of the corresponding triad type.
 * (The 0th element is not meaningful; this array is effectively 1-based.)
 * To get the name of the ith triad (e.g. "003"), 
 * look at the global constant array c.TRIAD_NAMES[i]
 * <p>
 * Triads are named as 
 * (number of pairs that are mutually tied)
 * (number of pairs that are one-way tied)
 * (number of non-tied pairs)
 * in the triple. Since there are be only three pairs, there is a finite
 * set of these possible triads.
 * <p>
 * In fact, there are exactly 16, conventionally sorted by the number of 
 * realized edges in the triad:
 * <table>
 * <tr><th>Number</th> <th>Configuration</th> <th>Notes</th></tr>
 * <tr><td>1</td><td>003</td><td>The empty triad</td></tr>
 * <tr><td>2</td><td>012</td><td></td></tr>
 * <tr><td>3</td><td>102</td><td></td></tr>
 * <tr><td>4</td><td>021D</td><td>"Down": the directed edges point away</td></tr>
 * <tr><td>5</td><td>021U</td><td>"Up": the directed edges meet</td></tr>
 * <tr><td>6</td><td>021C</td><td>"Circle": one in, one out</td></tr>
 * <tr><td>7</td><td>111D</td><td>"Down": 021D but one edge is mutual</td></tr>
 * <tr><td>8</td><td>111U</td><td>"Up": 021U but one edge is mutual</td></tr>
 * <tr><td>9</td><td>030T</td><td>"Transitive": two point to the same vertex</td></tr>
 * <tr><td>10</td><td>030C</td><td>"Circle": A->B->C->A</td></tr>
 * <tr><td>11</td><td>201</td><td></td></tr>
 * <tr><td>12</td><td>120D</td><td>"Down": 021D but the third edge is mutual</td></tr>
 * <tr><td>13</td><td>120U</td><td>"Up": 021U but the third edge is mutual</td></tr>
 * <tr><td>14</td><td>120C</td><td>"Circle": 021C but the third edge is mutual</td></tr>
 * <tr><td>15</td><td>210</td><td></td></tr>
 * <tr><td>16</td><td>300</td><td>The complete</td></tr>
 * </table>
 * <p>
 * This implementation takes O( m ), m is the number of edges in the graph. 
 * <br>
 * It is based on 
 * <a href="http://vlado.fmf.uni-lj.si/pub/networks/doc/triads/triads.pdf">
 * A subquadratic triad census algorithm for large sparse networks 
 * with small maximum degree</a>
 * Vladimir Batagelj and Andrej Mrvar, University of Ljubljana
 * Published in Social Networks.
 * @author Danyel Fisher
 * @author Tom Nelson - converted to jung2
 *
 */
public class TriadicCensus {

	// NOTE THAT THIS RETURNS STANDARD 1-16 COUNT!

	// and their types
	public static final String[] TRIAD_NAMES = { "N/A", "003", "012", "102", "021D",
			"021U", "021C", "111D", "111U", "030T", "030C", "201", "120D",
			"120U", "120C", "210", "300" };

	public static final int MAX_TRIADS = TRIAD_NAMES.length;

	/**
     * Returns an array whose ith element (for i in [1,16]) is the number of 
     * occurrences of the corresponding triad type in <code>g</code>.
     * (The 0th element is not meaningful; this array is effectively 1-based.)
	 * 
	 * @param g
	 */
    public static <V,E> long[] getCounts(DirectedGraph<V,E> g) {
        long[] count = new long[MAX_TRIADS];

        List<V> id = new ArrayList<V>(g.getVertices());

		// apply algorithm to each edge, one at at time
		for (int i_v = 0; i_v < g.getVertexCount(); i_v++) {
			V v = id.get(i_v);
			for(V u : g.getNeighbors(v)) {
				int triType = -1;
				if (id.indexOf(u) <= i_v)
					continue;
				Set<V> neighbors = new HashSet<V>(CollectionUtils.union(g.getNeighbors(u), g.getNeighbors(v)));
				neighbors.remove(u);
				neighbors.remove(v);
				if (g.isSuccessor(v,u) && g.isSuccessor(u,v)) {
					triType = 3;
				} else {
					triType = 2;
				}
				count[triType] += g.getVertexCount() - neighbors.size() - 2;
				for (V w : neighbors) {
					if (shouldCount(g, id, u, v, w)) {
						count [ triType ( triCode(g, u, v, w) ) ] ++;
					}
				}
			}
		}
		int sum = 0;
		for (int i = 2; i <= 16; i++) {
			sum += count[i];
		}
		int n = g.getVertexCount();
		count[1] = n * (n-1) * (n-2) / 6 - sum;
		return count;		
	}

	/**
	 * This is the core of the technique in the paper. Returns an int from 0 to
	 * 65 based on: WU -> 32 UW -> 16 WV -> 8 VW -> 4 UV -> 2 VU -> 1
	 * 
	 */
	public static <V,E> int triCode(Graph<V,E> g, V u, V v, V w) {
		int i = 0;
		i += link(g, v, u ) ? 1 : 0;
		i += link(g, u, v ) ? 2 : 0;
		i += link(g, v, w ) ? 4 : 0;
		i += link(g, w, v ) ? 8 : 0;
		i += link(g, u, w ) ? 16 : 0;
		i += link(g, w, u ) ? 32 : 0;
		return i;
	}

	protected static <V,E> boolean link(Graph<V,E> g, V a, V b) {
		return g.isPredecessor(b, a);
	}
	
	
	/**
	 * Simply returns the triCode. 
	 * @param triCode
	 * @return the string code associated with the numeric type
	 */
	public static int triType( int triCode ) {
		return codeToType[ triCode ];
	}

	/**
	 * For debugging purposes, this is copied straight out of the paper which
	 * means that they refer to triad types 1-16.
	 */
	protected static final int[] codeToType = { 1, 2, 2, 3, 2, 4, 6, 8, 2, 6, 5, 7, 3, 8,
			7, 11, 2, 6, 4, 8, 5, 9, 9, 13, 6, 10, 9, 14, 7, 14, 12, 15, 2, 5,
			6, 7, 6, 9, 10, 14, 4, 9, 9, 12, 8, 13, 14, 15, 3, 7, 8, 11, 7, 12,
			14, 15, 8, 14, 13, 15, 11, 15, 15, 16 };

	/**
	 * Make sure we have a canonical ordering: Returns true if u < w, or v < w <
	 * u and v doesn't link to w
	 * 
	 * @param id
	 * @param u
	 * @param v
	 * @param w
	 * @return true if u < w, or if v < w < u and v doesn't link to w; false otherwise
	 */
	protected static <V,E> boolean shouldCount(Graph<V,E> g, List<V> id, V u, V v, V w) {
		int i_u = id.indexOf(u);
		int i_w = id.indexOf(w);
		if (i_u < i_w)
			return true;
		int i_v = id.indexOf(v);
		if ((i_v < i_w) && (i_w < i_u) && (!g.isNeighbor(w,v)))
			return true;
		return false;
	}
}
