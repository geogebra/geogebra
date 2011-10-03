/*
 * Created on Jul 14, 2007
 *
 * Copyright (c) 2007, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.algorithms.scoring;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.Hypergraph;

/**
 * An abstract class for iterative random-walk-based vertex scoring algorithms 
 * that have a 
 * fixed probability, for each vertex, of 'jumping' to that vertex at each
 * step in the algorithm (rather than following a link out of that vertex).
 *
 * @param <V> the vertex type
 * @param <E> the edge type
 * @param <S> the score type
 */
public abstract class AbstractIterativeScorerWithPriors<V,E,S> extends
        AbstractIterativeScorer<V,E,S> implements VertexScorer<V,S>
{
    /**
     * The prior probability of each vertex being visited on a given 
     * 'jump' (non-link-following) step.
     */
    protected Transformer<V,? extends S> vertex_priors;

    /**
     * The probability of making a 'jump' at each step.
     */
    protected double alpha;

    /**
     * Creates an instance for the specified graph, edge weights, vertex
     * priors, and jump probability.
     * @param g the graph whose vertices are to be assigned scores
     * @param edge_weights the edge weights to use in the score assignment
     * @param vertex_priors the prior probabilities of each vertex being 'jumped' to
     * @param alpha the probability of making a 'jump' at each step
     */
    public AbstractIterativeScorerWithPriors(Hypergraph<V,E> g,
            Transformer<E,? extends Number> edge_weights, 
            Transformer<V,? extends S> vertex_priors, double alpha)
    {
        super(g, edge_weights);
        this.vertex_priors = vertex_priors;
        this.alpha = alpha;
        initialize();
    }

    /**
     * Creates an instance for the specified graph, vertex priors, and jump
     * probability, with edge weights specified by the subclass.
     * @param g the graph whose vertices are to be assigned scores
     * @param vertex_priors the prior probabilities of each vertex being 'jumped' to
     * @param alpha the probability of making a 'jump' at each step
     */
    public AbstractIterativeScorerWithPriors(Hypergraph<V,E> g, 
    		Transformer<V,? extends S> vertex_priors, double alpha)
    {
        super(g);
        this.vertex_priors = vertex_priors;
        this.alpha = alpha;
        initialize();
    }

    /**
     * Initializes the state of this instance.
     */
    @Override
    public void initialize()
    {
        super.initialize();
        // initialize output values to priors
        // (output and current are swapped before each step(), so current will
        // have priors when update()s start happening)
        for (V v : graph.getVertices())
            setOutputValue(v, getVertexPrior(v));
    }
    
    /**
     * Returns the prior probability for <code>v</code>.
     * @param v the vertex whose prior probability is being queried
     * @return the prior probability for <code>v</code>
     */
    protected S getVertexPrior(V v)
    {
        return vertex_priors.transform(v);
    }

    /**
     * Returns a Transformer which maps each vertex to its prior probability.
     * @return a Transformer which maps each vertex to its prior probability
     */
    public Transformer<V, ? extends S> getVertexPriors()
    {
        return vertex_priors;
    }

    /**
     * Returns the probability of making a 'jump' (non-link-following step).
     * @return the probability of making a 'jump' (non-link-following step)
     */
    public double getAlpha()
    {
        return alpha;
    }
}
