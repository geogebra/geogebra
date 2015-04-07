/**
 * Copyright (c) 2008, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 * Created on Jun 7, 2008
 * 
 */
package edu.uci.ics.jung.algorithms.metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.uci.ics.jung.graph.Graph;

/**
 * A class consisting of static methods for calculating graph metrics.
 */
public class Metrics 
{
    /**
     * Returns a <code>Map</code> of vertices to their clustering coefficients.
     * The clustering coefficient cc(v) of a vertex v is defined as follows:
     * <ul>
     * <li/><code>degree(v) == {0,1}</code>: 0
     * <li/><code>degree(v) == n, n &gt;= 2</code>: given S, the set of neighbors
     * of <code>v</code>: cc(v) = (the sum over all w in S of the number of 
     * other elements of w that are neighbors of w) / ((|S| * (|S| - 1) / 2).
     * Less formally, the fraction of <code>v</code>'s neighbors that are also
     * neighbors of each other. 
     * <p><b>Note</b>: This algorithm treats its argument as an undirected graph;
     * edge direction is ignored. 
     * @param graph the graph whose clustering coefficients are to be calculated
     * @see "The structure and function of complex networks, M.E.J. Newman, aps.arxiv.org/abs/cond-mat/0303516"
     */
    public static <V,E> Map<V, Double> clusteringCoefficients(Graph<V,E> graph)
    {
        Map<V,Double> coefficients = new HashMap<V,Double>();
        
        for (V v : graph.getVertices())
        {
            int n = graph.getNeighborCount(v);
            if (n < 2)
                coefficients.put(v, new Double(0));
            else
            {
                // how many of v's neighbors are connected to each other?
                ArrayList<V> neighbors = new ArrayList<V>(graph.getNeighbors(v));
                double edge_count = 0;
                for (int i = 0; i < n; i++)
                {
                    V w = neighbors.get(i);
                    for (int j = i+1; j < n; j++ )
                    {
                        V x = neighbors.get(j);
                        edge_count += graph.isNeighbor(w, x) ? 1 : 0;
                    }
                }
                double possible_edges = (n * (n - 1))/2.0;
                coefficients.put(v, new Double(edge_count / possible_edges));
            }
        }
        
        return coefficients;
    }
}
