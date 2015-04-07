/*
 * Created on Sep 19, 2005
 *
 * Copyright (c) 2005, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.algorithms.metrics;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.Graph;

/**
 * Calculates some of the measures from Burt's text "Structural Holes: 
 * The Social Structure of Competition".
 * 
 * <p><b>Notes</b>: 
 * <ul>
 * <li/>Each of these measures assumes that each edge has an associated 
 * non-null weight whose value is accessed through the specified 
 * <code>Transformer</code> instance.
 * <li/>Nonexistent edges are treated as edges with weight 0 for purposes 
 * of edge weight calculations.
 * </ul>
 *  
 * <p>Based on code donated by Jasper Voskuilen and 
 * Diederik van Liere of the Department of Information and Decision Sciences
 * at Erasmus University.</p>
 * 
 * @author Joshua O'Madadhain
 * @author Jasper Voskuilen
 * @see "Ronald Burt, Structural Holes: The Social Structure of Competition"
 * @author Tom Nelson - converted to jung2
 */
public class StructuralHoles<V,E> {
	
    protected Transformer<E, ? extends Number> edge_weight;
    protected Graph<V,E> g;
    
    /**
     * Creates a <code>StructuralHoles</code> instance based on the 
     * edge weights specified by <code>nev</code>.
     */
    public StructuralHoles(Graph<V,E> graph, Transformer<E, ? extends Number> nev) 
    {
        this.g = graph;
        this.edge_weight = nev;
    }

    /**
     * Burt's measure of the effective size of a vertex's network.  Essentially, the
     * number of neighbors minus the average degree of those in <code>v</code>'s neighbor set,
     * not counting ties to <code>v</code>.  Formally: 
     * <pre>
     * effectiveSize(v) = v.degree() - (sum_{u in N(v)} sum_{w in N(u), w !=u,v} p(v,w)*m(u,w))
     * </pre>
     * where 
     * <ul>
     * <li/><code>N(a) = a.getNeighbors()</code>
     * <li/><code>p(v,w) =</code> normalized mutual edge weight of v and w
     * <li/><code>m(u,w)</code> = maximum-scaled mutual edge weight of u and w
     * </ul>
     * @see #normalizedMutualEdgeWeight(Object, Object)
     * @see #maxScaledMutualEdgeWeight(Object, Object) 
     */
    public double effectiveSize(V v)
    {
        double result = g.degree(v);
        for(V u : g.getNeighbors(v)) {

            for(V w : g.getNeighbors(u)) {

                if (w != v && w != u)
                    result -= normalizedMutualEdgeWeight(v,w) * 
                              maxScaledMutualEdgeWeight(u,w);
            }
        }
        return result;
    }
    
    /**
     * Returns the effective size of <code>v</code> divided by the number of 
     * alters in <code>v</code>'s network.  (In other words, 
     * <code>effectiveSize(v) / v.degree()</code>.)
     * If <code>v.degree() == 0</code>, returns 0.
     */
    public double efficiency(V v) {
        double degree = g.degree(v);
        
        if (degree == 0)
            return 0;
        else
            return effectiveSize(v) / degree;
    }

    /**
     * Burt's constraint measure (equation 2.4, page 55 of Burt, 1992). Essentially a
     * measure of the extent to which <code>v</code> is invested in people who are invested in
     * other of <code>v</code>'s alters (neighbors).  The "constraint" is characterized
     * by a lack of primary holes around each neighbor.  Formally:
     * <pre>
     * constraint(v) = sum_{w in MP(v), w != v} localConstraint(v,w)
     * </pre>
     * where MP(v) is the subset of v's neighbors that are both predecessors and successors of v. 
     * @see #localConstraint(Object, Object)
     */
    public double constraint(V v) {
        double result = 0;
        for(V w : g.getSuccessors(v)) {

            if (v != w && g.isPredecessor(v,w))
            {
                result += localConstraint(v, w);
            }
        }

        return result;
    }

    
    /**
     * Calculates the hierarchy value for a given vertex.  Returns <code>NaN</code> when
     * <code>v</code>'s degree is 0, and 1 when <code>v</code>'s degree is 1.
     * Formally:
     * <pre>
     * hierarchy(v) = (sum_{v in N(v), w != v} s(v,w) * log(s(v,w))}) / (v.degree() * Math.log(v.degree()) 
     * </pre>
     * where
     * <ul>
     * <li/><code>N(v) = v.getNeighbors()</code> 
     * <li/><code>s(v,w) = localConstraint(v,w) / (aggregateConstraint(v) / v.degree())</code>
     * </ul>
     * @see #localConstraint(Object, Object)
     * @see #aggregateConstraint(Object)
     */
    public double hierarchy(V v)
    {
        double v_degree = g.degree(v);
        
        if (v_degree == 0)
            return Double.NaN;
        if (v_degree == 1)
            return 1;
        
        double v_constraint = aggregateConstraint(v);

        double numerator = 0;
        for (V w : g.getNeighbors(v)) {
        
            if (v != w)
            {
                double sl_constraint = localConstraint(v, w) / (v_constraint / v_degree);
                numerator += sl_constraint * Math.log(sl_constraint);
            }
        }

        return numerator / (v_degree * Math.log(v_degree));
    }

    /**
     * Returns the local constraint on <code>v</code> from a lack of primary holes 
     * around its neighbor <code>v2</code>.
     * Based on Burt's equation 2.4.  Formally:
     * <pre>
     * localConstraint(v1, v2) = ( p(v1,v2) + ( sum_{w in N(v)} p(v1,w) * p(w, v2) ) )^2
     * </pre>
     * where 
     * <ul>
     * <li/><code>N(v) = v.getNeighbors()</code>
     * <li/><code>p(v,w) =</code> normalized mutual edge weight of v and w
     * </ul>
     * @see #normalizedMutualEdgeWeight(Object, Object)
     */
    public double localConstraint(V v1, V v2) 
    {
        double nmew_vw = normalizedMutualEdgeWeight(v1, v2);
        double inner_result = 0;
        for (V w : g.getNeighbors(v1)) {

            inner_result += normalizedMutualEdgeWeight(v1,w) * 
                normalizedMutualEdgeWeight(w,v2);
        }
        return (nmew_vw + inner_result) * (nmew_vw + inner_result);
    }
    
    /**
     * The aggregate constraint on <code>v</code>.  Based on Burt's equation 2.7.  
     * Formally:
     * <pre>
     * aggregateConstraint(v) = sum_{w in N(v)} localConstraint(v,w) * O(w)
     * </pre>
     * where
     * <ul>
     * <li/><code>N(v) = v.getNeighbors()</code>
     * <li/><code>O(w) = organizationalMeasure(w)</code>
     * </ul>
     */
    public double aggregateConstraint(V v)
    {
        double result = 0;
        for (V w : g.getNeighbors(v)) {

            result += localConstraint(v, w) * organizationalMeasure(g, w);
        }
        return result;
    }
    
    /**
     * A measure of the organization of individuals within the subgraph 
     * centered on <code>v</code>.  Burt's text suggests that this is 
     * in some sense a measure of how "replaceable" <code>v</code> is by 
     * some other element of this subgraph.  Should be a number in the
     * closed interval [0,1].
     * 
     * <p>This implementation returns 1.  Users may wish to override this
     * method in order to define their own behavior.</p>
     */
    protected double organizationalMeasure(Graph<V,E> g, V v) {
        return 1.0;
    }
    
   
    /**
     * Returns the proportion of <code>v1</code>'s network time and energy invested
     * in the relationship with <code>v2</code>.  Formally:
     * <pre>
     * normalizedMutualEdgeWeight(a,b) = mutual_weight(a,b) / (sum_c mutual_weight(a,c))
     * </pre>
     * Returns 0 if either numerator or denominator = 0, or if <code>v1 == v2</code>.
     * @see #mutualWeight(Object, Object)
     */
    protected double normalizedMutualEdgeWeight(V v1, V v2)
    {
        if (v1 == v2)
            return 0;
        
        double numerator = mutualWeight(v1, v2);
        
        if (numerator == 0)
            return 0;
        
        double denominator = 0;
        for (V v : g.getNeighbors(v1)) {
            denominator += mutualWeight(v1, v);
        }
        if (denominator == 0)
            return 0;
        
        return numerator / denominator;
    }
    
    /**
     * Returns the weight of the edge from <code>v1</code> to <code>v2</code>
     * plus the weight of the edge from <code>v2</code> to <code>v1</code>;
     * if either edge does not exist, it is treated as an edge with weight 0. 
     * Undirected edges are treated as two antiparallel directed edges (that
     * is, if there is one undirected edge with weight <i>w</i> connecting 
     * <code>v1</code> to <code>v2</code>, the value returned is 2<i>w</i>).
     * Ignores parallel edges; if there are any such, one is chosen at random.
     * Throws <code>NullPointerException</code> if either edge is 
     * present but not assigned a weight by the constructor-specified
     * <code>NumberEdgeValue</code>.
     */
    protected double mutualWeight(V v1, V v2)
    {
        E e12 = g.findEdge(v1,v2);
        E e21 = g.findEdge(v2,v1);
        double w12 = (e12 != null ? edge_weight.transform(e12).doubleValue() : 0);
        double w21 = (e21 != null ? edge_weight.transform(e21).doubleValue() : 0);
        
        return w12 + w21;
    }
    
    /**
     * The marginal strength of v1's relation with contact vertex2.
     * Formally:
     * <pre>
     * normalized_mutual_weight = mutual_weight(a,b) / (max_c mutual_weight(a,c))
     * </pre>
     * Returns 0 if either numerator or denominator is 0, or if <code>v1 == v2</code>.
     * @see #mutualWeight(Object, Object)
     */
    protected double maxScaledMutualEdgeWeight(V v1, V v2)
    {
        if (v1 == v2)
            return 0;

        double numerator = mutualWeight(v1, v2);

        if (numerator == 0)
            return 0;
        
        double denominator = 0;
        for (V w : g.getNeighbors(v1)) {

            if (v2 != w)
                denominator = Math.max(numerator, mutualWeight(v1, w));
        }
        
        if (denominator == 0)
            return 0;
        
        return numerator / denominator;
    }
}
