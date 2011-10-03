/*
 * Created on Jul 12, 2007
 *
 * Copyright (c) 2007, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.algorithms.scoring.util;

import java.util.Collection;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.scoring.HITS;

/**
 * Methods for assigning values (to be interpreted as prior probabilities) to vertices in the context
 * of random-walk-based scoring algorithms.
 */
public class ScoringUtils
{
    /**
     * Assigns a probability of 1/<code>roots.size()</code> to each of the elements of <code>roots</code>.
     * @param <V> the vertex type
     * @param roots the vertices to be assigned nonzero prior probabilities
     * @return
     */
    public static <V> Transformer<V, Double> getUniformRootPrior(Collection<V> roots)
    {
        final Collection<V> inner_roots = roots;
        Transformer<V, Double> distribution = new Transformer<V, Double>()
        {
            public Double transform(V input)
            {
                if (inner_roots.contains(input))
                    return new Double(1.0 / inner_roots.size());
                else
                    return 0.0;
            }
        };
        
        return distribution;
    }
    
    /**
     * Returns a Transformer that hub and authority values of 1/<code>roots.size()</code> to each 
     * element of <code>roots</code>.
     * @param <V> the vertex type
     * @param roots the vertices to be assigned nonzero scores
     * @return a Transformer that assigns uniform prior hub/authority probabilities to each root
     */
    public static <V> Transformer<V, HITS.Scores> getHITSUniformRootPrior(Collection<V> roots)
    {
        final Collection<V> inner_roots = roots;
        Transformer<V, HITS.Scores> distribution = 
        	new Transformer<V, HITS.Scores>()
        {
            public HITS.Scores transform(V input)
            {
                if (inner_roots.contains(input))
                    return new HITS.Scores(1.0 / inner_roots.size(), 1.0 / inner_roots.size());
                else
                    return new HITS.Scores(0.0, 0.0);
            }
        };
        return distribution;
    }
}
