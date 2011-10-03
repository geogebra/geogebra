/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package edu.uci.ics.jung.algorithms.importance;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * This class provides basic infrastructure for relative authority algorithms that compute the importance of nodes
 * relative to one or more root nodes. The services provided are:
 * <ul>
 * <li>The set of root nodes (priors) is stored and maintained</li>
 * <li>Getters and setters for the prior rank score are provided</li>
 * </ul>
 * 
 * @author Scott White
 */
public abstract class RelativeAuthorityRanker<V,E> extends AbstractRanker<V,E> {
    private Set<V> mPriors;
    /**
     * The default key used for the user datum key corresponding to prior rank scores.
     */

    protected Map<V,Number> priorRankScoreMap = new HashMap<V,Number>();
    /**
     * Cleans up all of the prior rank scores on finalize.
     */
    @Override
    protected void finalizeIterations() {
        super.finalizeIterations();
        priorRankScoreMap.clear();
    }

    /**
     * Retrieves the value of the prior rank score.
     * @param v the root node (prior)
     * @return the prior rank score
     */
    protected double getPriorRankScore(V v) {
    	return priorRankScoreMap.get(v).doubleValue();

    }

    /**
     * Allows the user to specify a value to set for the prior rank score
     * @param v the root node (prior)
     * @param value the score to set to
     */
    public void setPriorRankScore(V v, double value) {
    	this.priorRankScoreMap.put(v, value);
    }

    /**
     * Retrieves the set of priors.
     * @return the set of root nodes (priors)
     */
    protected Set<V> getPriors() { return mPriors; }

    /**
     * Specifies which vertices are root nodes (priors).
     * @param priors the root nodes
     */
    protected void setPriors(Set<V> priors) { mPriors = priors; }
}
