/*
 * Created on Jul 6, 2007
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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.scoring.util.DelegateToEdgeTransformer;
import edu.uci.ics.jung.algorithms.scoring.util.VEPair;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.Hypergraph;

/**
 * An abstract class for algorithms that assign scores to vertices based on iterative methods.
 * Generally, any (concrete) subclass will function by creating an instance, and then either calling
 * <code>evaluate</code> (if the user wants to iterate until the algorithms is 'done') or 
 * repeatedly call <code>step</code> (if the user wants to observe the values at each step).
 */
public abstract class AbstractIterativeScorer<V,E,T> implements IterativeContext, VertexScorer<V,T>
{
    /**
     * Maximum number of iterations to use before terminating.  Defaults to 100.
     */
    protected int max_iterations;
    
    /**
     * Minimum change from one step to the next; if all changes are <= tolerance, 
     * no further updates will occur.
     * Defaults to 0.001.
     */
    protected double tolerance;
    
    /**
     * The graph on which the calculations are to be made.
     */
    protected Hypergraph<V,E> graph;
    
    /**
     * The total number of iterations used so far.
     */
    protected int total_iterations;
    
    /**
     * The edge weights used by this algorithm.
     */
    protected Transformer<VEPair<V,E>, ? extends Number> edge_weights;
    
    /**
     * Indicates whether the output and current values are in a 'swapped' state.
     * Intended for internal use only.
     */
    protected boolean output_reversed;
    
    /**
     * The map in which the output values are stored.
     */
    private Map<V, T> output;
    
    /**
     * The map in which the current values are stored.
     */
    private Map<V, T> current_values;
    
    /**
     * A flag representing whether this instance tolerates disconnected graphs.
     * Instances that do not accept disconnected graphs may have unexpected behavior
     * on disconnected graphs; they are not guaranteed to do an explicit check.
     * Defaults to true.
     */
    private boolean accept_disconnected_graph;


    protected boolean hyperedges_are_self_loops = false;

    /**
     * Sets the output value for this vertex.
     * @param v the vertex whose output value is to be set
     * @param value the value to set
     */
    protected void setOutputValue(V v, T value)
    {
        output.put(v, value);
    }
    
    /**
     * Gets the output value for this vertex.
     * @param v the vertex whose output value is to be retrieved
     * @return the output value for this vertex
     */
    protected T getOutputValue(V v)
    {
        return output.get(v);
    }
    
    /**
     * Gets the current value for this vertex
     * @param v the vertex whose current value is to be retrieved
     * @return the current value for this vertex
     */
    protected T getCurrentValue(V v)
    {
        return current_values.get(v);
    }
    
    /**
     * Sets the current value for this vertex.
     * @param v the vertex whose current value is to be set
     * @param value the current value to set
     */
    protected void setCurrentValue(V v, T value)
    {
        current_values.put(v, value);
    }
    
    /**
     * The largest change seen so far among all vertex scores.
     */
    protected double max_delta;
    
    /**
     * Creates an instance for the specified graph and edge weights.
     * @param g the graph for which the instance is to be created
     * @param edge_weights the edge weights for this instance
     */
    public AbstractIterativeScorer(Hypergraph<V,E> g, Transformer<E, ? extends Number> edge_weights)
    {
        this.graph = g;
        this.max_iterations = 100;
        this.tolerance = 0.001;
        this.accept_disconnected_graph = true;
        setEdgeWeights(edge_weights);
    }
    
    /**
     * Creates an instance for the specified graph <code>g</code>.
     * NOTE: This constructor does not set the internal 
     * <code>edge_weights</code> variable.  If this variable is used by 
     * the subclass which invoked this constructor, it must be initialized
     * by that subclass.
     * @param g the graph for which the instance is to be created
     */
    public AbstractIterativeScorer(Hypergraph<V,E> g)
    {
    	this.graph = g;
        this.max_iterations = 100;
        this.tolerance = 0.001;
        this.accept_disconnected_graph = true;
    }
    
    /**
     * Initializes the internal state for this instance.
     */
    protected void initialize()
    {
        this.total_iterations = 0;
        this.max_delta = Double.MIN_VALUE;
        this.output_reversed = true;
        this.current_values = new HashMap<V, T>();
        this.output = new HashMap<V, T>();
    }
    
    /**
     * Steps through this scoring algorithm until a termination condition is reached.
     */
    public void evaluate()
    {
        do
            step();
        while (!done());
    }
    
    /**
     * Returns true if the total number of iterations is greater than or equal to 
     * <code>max_iterations</code>
     * or if the maximum value change observed is less than <code>tolerance</code>.
     */
    public boolean done()
    {
        return total_iterations >= max_iterations || max_delta < tolerance;
    }

    /**
     * Performs one step of this algorithm; updates the state (value) for each vertex.
     */
    public void step()
    {
        swapOutputForCurrent();
        
        for (V v : graph.getVertices())
        {
            double diff = update(v);
            updateMaxDelta(v, diff);
        }
        total_iterations++;
        afterStep();
    }

    /**
     * 
     */
    protected void swapOutputForCurrent()
    {
        Map<V, T> tmp = output;
        output = current_values;
        current_values = tmp;
        output_reversed = !output_reversed;
    }

    /**
     * Updates the value for <code>v</code>.
     * This is the key 
     * @param v the vertex whose value is to be updated
     * @return
     */
    protected abstract double update(V v);

    protected void updateMaxDelta(V v, double diff)
    {
        max_delta = Math.max(max_delta, diff);
    }
    
    protected void afterStep() {}
    
    public T getVertexScore(V v)
    {
        if (!graph.containsVertex(v))
            throw new IllegalArgumentException("Vertex " + v + " not an element of this graph");
        
        return output.get(v);
    }

    /**
     * Returns the maximum number of iterations that this instance will use.
     * @return the maximum number of iterations that <code>evaluate</code> will use
     * prior to terminating
     */
    public int getMaxIterations()
    {
        return max_iterations;
    }

    /**
     * Returns the number of iterations that this instance has used so far.
     * @return the number of iterations that this instance has used so far
     */
    public int getIterations()
    {
    	return total_iterations;
    }
    
    /**
     * Sets the maximum number of times that <code>evaluate</code> will call <code>step</code>.
     * @param max_iterations the maximum 
     */
    public void setMaxIterations(int max_iterations)
    {
        this.max_iterations = max_iterations;
    }

    /**
     * Gets the size of the largest change (difference between the current and previous values)
     * for any vertex that can be tolerated.  Once all changes are less than this value, 
     * <code>evaluate</code> will terminate.
     * @return the size of the largest change that evaluate() will permit
     */
    public double getTolerance()
    {
        return tolerance;
    }

    /**
     * Sets the size of the largest change (difference between the current and previous values)
     * for any vertex that can be tolerated.
     * @param tolerance the size of the largest change that evaluate() will permit
     */
    public void setTolerance(double tolerance)
    {
        this.tolerance = tolerance;
    }
    
    /**
     * Returns the Transformer that this instance uses to associate edge weights with each edge.
     * @return the Transformer that associates an edge weight with each edge
     */
    public Transformer<VEPair<V,E>, ? extends Number> getEdgeWeights()
    {
        return edge_weights;
    }

    /**
     * Sets the Transformer that this instance uses to associate edge weights with each edge
     * @param edge_weights the Transformer to use to associate an edge weight with each edge
     * @see edu.uci.ics.jung.algorithms.scoring.util.UniformDegreeWeight
     */
    public void setEdgeWeights(Transformer<E, ? extends Number> edge_weights)
    {
        this.edge_weights = new DelegateToEdgeTransformer<V,E>(edge_weights);
    }
    
    /**
     * Gets the edge weight for <code>e</code> in the context of its (incident) vertex <code>v</code>.
     * @param v the vertex incident to e as a context in which the edge weight is to be calculated
     * @param e the edge whose weight is to be returned
     * @return the edge weight for <code>e</code> in the context of its (incident) vertex <code>v</code>
     */
    protected Number getEdgeWeight(V v, E e)
    {
        return edge_weights.transform(new VEPair<V,E>(v,e));
    }
    
    /**
     * Collects the 'potential' from v (its current value) if it has no outgoing edges; this
     * can then be redistributed among the other vertices as a means of normalization.
     * @param v
     */
    protected void collectDisappearingPotential(V v) {}

    /**
     * Specifies whether this instance should accept vertices with no outgoing edges.
     * @param accept true if this instance should accept vertices with no outgoing edges, false otherwise
     */
    public void acceptDisconnectedGraph(boolean accept)
    {
        this.accept_disconnected_graph = accept;
    }
    
    /**
     * Returns true if this instance accepts vertices with no outgoing edges, and false otherwise.
     * @return true if this instance accepts vertices with no outgoing edges, otherwise false
     */
    public boolean isDisconnectedGraphOK()
    {
        return this.accept_disconnected_graph;
    }
    
    /**
     * Specifies whether hyperedges are to be treated as self-loops.  If they
     * are, then potential will flow along a hyperedge a vertex to itself, 
     * just as it does to all other vertices incident to that hyperedge. 
     * @param arg if {@code true}, hyperedges are treated as self-loops
     */
    public void setHyperedgesAreSelfLoops(boolean arg) 
    {
    	this.hyperedges_are_self_loops = arg;
    }

    /**
     * Returns the effective number of vertices incident to this edge.  If
     * the graph is a binary relation or if hyperedges are treated as self-loops,
     * the value returned is {@code graph.getIncidentCount(e)}; otherwise it is
     * {@code graph.getIncidentCount(e) - 1}.
     */
    protected int getAdjustedIncidentCount(E e) 
    {
        return graph.getIncidentCount(e) - (hyperedges_are_self_loops ? 0 : 1);
    }
}
