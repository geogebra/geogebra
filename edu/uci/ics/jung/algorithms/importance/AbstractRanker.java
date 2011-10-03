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

import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.map.LazyMap;

import edu.uci.ics.jung.algorithms.util.IterativeProcess;
import edu.uci.ics.jung.graph.Graph;

/**
 * Abstract class for algorithms that rank nodes or edges by some "importance" metric. Provides a common set of
 * services such as:
 * <ul>
 *  <li> storing rank scores</li>
 *  <li> getters and setters for rank scores</li>
 *  <li> computing default edge weights</li>
 *  <li> normalizing default or user-provided edge transition weights </li>
 *  <li> normalizing rank scores</li>
 *  <li> automatic cleanup of decorations</li>
 *  <li> creation of Ranking list</li>
 * <li>print rankings in sorted order by rank</li>
 * </ul>
 * <p>
 * By default, all rank scores are removed from the vertices (or edges) being ranked.
 * @author Scott White
 */
public abstract class AbstractRanker<V,E> extends IterativeProcess {
    private Graph<V,E> mGraph;
    private List<Ranking<?>> mRankings;
    private boolean mRemoveRankScoresOnFinalize;
    private boolean mRankNodes;
    private boolean mRankEdges;
    private boolean mNormalizeRankings;
    protected Map<Object,Map<V, Number>> vertexRankScores = 
    	LazyMap.decorate(
    			new HashMap<Object,Map<V,Number>>(),
    			new Factory<Map<V,Number>>() {
					public Map<V,Number> create() {
						return new HashMap<V,Number>();
					}});
    protected Map<Object,Map<E, Number>> edgeRankScores = 
    	LazyMap.decorate(
    			new HashMap<Object,Map<E,Number>>(),
    			new Factory<Map<E,Number>>() {
					public Map<E,Number> create() {
						return new HashMap<E,Number>();
					}});
    private Map<E,Number> edgeWeights = new HashMap<E,Number>();

    protected void initialize(Graph<V,E> graph, boolean isNodeRanker, 
        boolean isEdgeRanker) {
        if (!isNodeRanker && !isEdgeRanker)
            throw new IllegalArgumentException("Must rank edges, vertices, or both");
        mGraph = graph;
        mRemoveRankScoresOnFinalize = true;
        mNormalizeRankings = true;
        mRankNodes = isNodeRanker;
        mRankEdges = isEdgeRanker;
    }
    
    /**
	 * @return all rankScores
	 */
	public Map<Object,Map<V, Number>> getVertexRankScores() {
		return vertexRankScores;
	}

	public Map<Object,Map<E, Number>> getEdgeRankScores() {
		return edgeRankScores;
	}

    /**
	 * @return the rankScores
	 */
	public Map<V, Number> getVertexRankScores(Object key) {
		return vertexRankScores.get(key);
	}

	public Map<E, Number> getEdgeRankScores(Object key) {
		return edgeRankScores.get(key);
	}

	protected Collection<V> getVertices() {
        return mGraph.getVertices();
    }

	protected int getVertexCount() {
        return mGraph.getVertexCount();
    }

    protected Graph<V,E> getGraph() {
        return mGraph;
    }

    @Override
    public void reset() {
    }

    /**
     * Returns <code>true</code> if this ranker ranks nodes, and 
     * <code>false</code> otherwise.
     */
    public boolean isRankingNodes() {
        return mRankNodes;
    }

    /**
     * Returns <code>true</code> if this ranker ranks edges, and 
     * <code>false</code> otherwise.
     */
    public boolean isRankingEdges() {
        return mRankEdges;
    }
    
    /**
     * Instructs the ranker whether or not it should remove the rank scores from the nodes (or edges) once the ranks
     * have been computed.
     * @param removeRankScoresOnFinalize <code>true</code> if the rank scores are to be removed, <code>false</code> otherwise
     */
    public void setRemoveRankScoresOnFinalize(boolean removeRankScoresOnFinalize) {
        this.mRemoveRankScoresOnFinalize = removeRankScoresOnFinalize;
    }

    protected void onFinalize(Object e) {}
    
    /**
     * The user datum key used to store the rank score.
     * @return the key
     */
    abstract public Object getRankScoreKey();


    @Override
    protected void finalizeIterations() {
        List<Ranking<?>> sortedRankings = new ArrayList<Ranking<?>>();

        int id = 1;
        if (mRankNodes) {
            for (V currentVertex : getVertices()) {
                Ranking<V> ranking = new Ranking<V>(id,getVertexRankScore(currentVertex),currentVertex);
                sortedRankings.add(ranking);
                if (mRemoveRankScoresOnFinalize) {
                	this.vertexRankScores.get(getRankScoreKey()).remove(currentVertex);
                }
                id++;
                onFinalize(currentVertex);
            }
        }
        if (mRankEdges) {
            for (E currentEdge : mGraph.getEdges()) {

                Ranking<E> ranking = new Ranking<E>(id,getEdgeRankScore(currentEdge),currentEdge);
                sortedRankings.add(ranking);
                if (mRemoveRankScoresOnFinalize) {
                	this.edgeRankScores.get(getRankScoreKey()).remove(currentEdge);
                }
                id++;
                onFinalize(currentEdge);
            }
        }

        mRankings = sortedRankings;
        Collections.sort(mRankings);
    }

    /**
     * Retrieves the list of ranking instances in descending sorted order by rank score
     * If the algorithm is ranking edges, the instances will be of type <code>EdgeRanking</code>, otherwise
     * if the algorithm is ranking nodes the instances will be of type <code>NodeRanking</code>
     * @return  the list of rankings
     */
    public List<Ranking<?>> getRankings() {
        return mRankings;
    }

    /**
     * Return a list of the top k rank scores.
     * @param topKRankings the value of k to use
     * @return list of rank scores
     */
    public List<Double> getRankScores(int topKRankings) {
        List<Double> scores = new ArrayList<Double>();
        int count=1;
        for (Ranking<?> currentRanking : getRankings()) {
            if (count > topKRankings) {
                return scores;
            }
            scores.add(currentRanking.rankScore);
            count++;
        }

        return scores;
    }

    /**
     * Given an edge or node, returns the corresponding rank score. This is a default
     * implementation of getRankScore which assumes the decorations are of type MutableDouble.
     * This method only returns legal values if <code>setRemoveRankScoresOnFinalize(false)</code> was called
     * prior to <code>evaluate()</code>.
     * @return  the rank score value
     */
    public double getVertexRankScore(V v) {
        Number rankScore = vertexRankScores.get(getRankScoreKey()).get(v);
        if (rankScore != null) {
            return rankScore.doubleValue();
        } else {
            throw new RuntimeException("setRemoveRankScoresOnFinalize(false) must be called before evaluate().");
        }
    }
    
    public double getVertexRankScore(V v, Object key) {
    	return vertexRankScores.get(key).get(v).doubleValue();
    }

    public double getEdgeRankScore(E e) {
        Number rankScore = edgeRankScores.get(getRankScoreKey()).get(e);
        if (rankScore != null) {
            return rankScore.doubleValue();
        } else {
            throw new RuntimeException("setRemoveRankScoresOnFinalize(false) must be called before evaluate().");
        }
    }
    
    public double getEdgeRankScore(E e, Object key) {
    	return edgeRankScores.get(key).get(e).doubleValue();
    }

    protected void setVertexRankScore(V v, double rankValue, Object key) {
    	vertexRankScores.get(key).put(v, rankValue);
    }

    protected void setEdgeRankScore(E e, double rankValue, Object key) {
		edgeRankScores.get(key).put(e, rankValue);
    }

    protected void setVertexRankScore(V v, double rankValue) {
    	setVertexRankScore(v,rankValue, getRankScoreKey());
    }

    protected void setEdgeRankScore(E e, double rankValue) {
    	setEdgeRankScore(e, rankValue, getRankScoreKey());
    }

    protected void removeVertexRankScore(V v, Object key) {
    	vertexRankScores.get(key).remove(v);
    }

    protected void removeEdgeRankScore(E e, Object key) {
    	edgeRankScores.get(key).remove(e);
    }

    protected void removeVertexRankScore(V v) {
    	vertexRankScores.get(getRankScoreKey()).remove(v);
    }

    protected void removeEdgeRankScore(E e) {
    	edgeRankScores.get(getRankScoreKey()).remove(e);
    }

    protected double getEdgeWeight(E e) {
    	return edgeWeights.get(e).doubleValue();
    }

    protected void setEdgeWeight(E e, double weight) {
    	edgeWeights.put(e, weight);
    }
    
    public void setEdgeWeights(Map<E,Number> edgeWeights) {
    	this.edgeWeights = edgeWeights;
    }

    /**
	 * @return the edgeWeights
	 */
	public Map<E, Number> getEdgeWeights() {
		return edgeWeights;
	}

	protected void assignDefaultEdgeTransitionWeights() {

        for (V currentVertex : getVertices()) {

            Collection<E> outgoingEdges = mGraph.getOutEdges(currentVertex);

            double numOutEdges = outgoingEdges.size();
            for (E currentEdge : outgoingEdges) {
                setEdgeWeight(currentEdge,1.0/numOutEdges);
            }
        }
    }

    protected void normalizeEdgeTransitionWeights() {

        for (V currentVertex : getVertices()) {

        	Collection<E> outgoingEdges = mGraph.getOutEdges(currentVertex);

            double totalEdgeWeight = 0;
            for (E currentEdge : outgoingEdges) {
                totalEdgeWeight += getEdgeWeight(currentEdge);
            }

            for (E currentEdge : outgoingEdges) {
                setEdgeWeight(currentEdge,getEdgeWeight(currentEdge)/totalEdgeWeight);
            }
        }
    }

    protected void normalizeRankings() {
        if (!mNormalizeRankings) {
            return;
        }
        double totalWeight = 0;

        for (V currentVertex : getVertices()) {
            totalWeight += getVertexRankScore(currentVertex);
        }

        for (V currentVertex : getVertices()) {
            setVertexRankScore(currentVertex,getVertexRankScore(currentVertex)/totalWeight);
        }
    }

    /**
     * Print the rankings to standard out in descending order of rank score
     * @param verbose if <code>true</code>, include information about the actual rank order as well as
     * the original position of the vertex before it was ranked
     * @param printScore if <code>true</code>, include the actual value of the rank score
     */
    public void printRankings(boolean verbose,boolean printScore) {
            double total = 0;
            Format formatter = new DecimalFormat("#0.#######");
            int rank = 1;

            for (Ranking<?> currentRanking : getRankings()) {
                double rankScore = currentRanking.rankScore;
                if (verbose) {
                    System.out.print("Rank " + rank + ": ");
                    if (printScore) {
                        System.out.print(formatter.format(rankScore));
                    }
                    System.out.print("\tVertex Id: " + currentRanking.originalPos);
                        System.out.print(" (" + currentRanking.getRanked() + ")");
                    System.out.println();
                } else {
                    System.out.print(rank + "\t");
                     if (printScore) {
                        System.out.print(formatter.format(rankScore));
                    }
                    System.out.println("\t" + currentRanking.originalPos);

                }
                total += rankScore;
                rank++;
            }

            if (verbose) {
                System.out.println("Total: " + formatter.format(total));
            }
    }

    /**
     * Allows the user to specify whether or not s/he wants the rankings to be normalized.
     * In some cases, this will have no effect since the algorithm doesn't allow normalization
     * as an option
     * @param normalizeRankings
     */
    public void setNormalizeRankings(boolean normalizeRankings) {
        mNormalizeRankings = normalizeRankings;
    }
}
