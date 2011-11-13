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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.jung.graph.DirectedGraph;


/**
 * Algorithm variant of <code>PageRankWithPriors</code> that computes the importance of a node based upon taking fixed-length random
 * walks out from the root set and then computing the stationary probability of being at each node. Specifically, it computes
 * the relative probability that the markov chain will spend at any particular node, given that it start in the root
 * set and ends after k steps.
 * <p>
 * A simple example of usage is:
 * <pre>
 * KStepMarkov ranker = new KStepMarkov(someGraph,rootSet,6,null);
 * ranker.evaluate();
 * ranker.printRankings();
 * </pre>
 * <p>
 *
 * @author Scott White
 * @author Tom Nelson - adapter to jung2
 * @see "Algorithms for Estimating Relative Importance in Graphs by Scott White and Padhraic Smyth, 2003"
 */
public class KStepMarkov<V,E> extends RelativeAuthorityRanker<V,E> {
    public final static String RANK_SCORE = "jung.algorithms.importance.KStepMarkovExperimental.RankScore";
    private final static String CURRENT_RANK = "jung.algorithms.importance.KStepMarkovExperimental.CurrentRank";
    private int mNumSteps;
    HashMap<V,Number> mPreviousRankingsMap;

    /**
     * Construct the algorihm instance and initializes the algorithm.
     * @param graph the graph to be analyzed
     * @param priors the set of root nodes
     * @param k positive integer parameter which controls the relative tradeoff between a distribution "biased" towards
     * R and the steady-state distribution which is independent of where the Markov-process started. Generally values
     * between 4-8 are reasonable
     * @param edgeWeights the weight for each edge 
     */
    public KStepMarkov(DirectedGraph<V,E> graph, Set<V> priors, int k, Map<E,Number> edgeWeights) {
        super.initialize(graph,true,false);
        mNumSteps = k;
        setPriors(priors);
        initializeRankings();
        if (edgeWeights == null) {
            assignDefaultEdgeTransitionWeights();
        } else {
            setEdgeWeights(edgeWeights);
        }
        normalizeEdgeTransitionWeights();
    }

    /**
     * The user datum key used to store the rank scores.
     * @return the key
     */
    @Override
    public String getRankScoreKey() {
        return RANK_SCORE;
    }

    protected void incrementRankScore(V v, double rankValue) {
    	double value = getVertexRankScore(v, RANK_SCORE);
    	value += rankValue;
    	setVertexRankScore(v, value, RANK_SCORE);
    }

    protected double getCurrentRankScore(V v) {
    	return getVertexRankScore(v, CURRENT_RANK);
    }

    protected void setCurrentRankScore(V v, double rankValue) {
    	setVertexRankScore(v, rankValue, CURRENT_RANK);
    }

    protected void initializeRankings() {
         mPreviousRankingsMap = new HashMap<V,Number>();
         for (V v : getVertices()) {
            Set<V> priors = getPriors();
            double numPriors = priors.size();

            if (getPriors().contains(v)) {
                setVertexRankScore(v, 1.0/ numPriors);
                setCurrentRankScore(v, 1.0/ numPriors);
                mPreviousRankingsMap.put(v,1.0/numPriors);
            } else {
                setVertexRankScore(v, 0);
                setCurrentRankScore(v, 0);
                mPreviousRankingsMap.put(v, 0);
            }
        }
     }
    @Override
    public void step() {

        for (int i=0;i<mNumSteps;i++) {
            updateRankings();
            for (V v : getVertices()) {
                double currentRankScore = getCurrentRankScore(v);
                incrementRankScore(v,currentRankScore);
                mPreviousRankingsMap.put(v, currentRankScore);
            }
        }
        normalizeRankings();
    }

    protected void updateRankings() {

        for (V v : getVertices()) {

            Collection<E> incomingEdges = getGraph().getInEdges(v);

            double currentPageRankSum = 0;
            for (E e : incomingEdges) {
                double currentWeight = getEdgeWeight(e);
                currentPageRankSum += 
                	mPreviousRankingsMap.get(getGraph().getOpposite(v,e)).doubleValue()*currentWeight;
            }
            setCurrentRankScore(v,currentPageRankSum);
        }
    }
}
