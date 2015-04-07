/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package edu.uci.ics.jung.algorithms.transformation;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.MapTransformer;

import edu.uci.ics.jung.algorithms.blockmodel.VertexPartition;
import edu.uci.ics.jung.graph.Graph;

/**
 * This class transforms a graph with a known vertex partitioning into a graph whose 
 * vertices correspond to the input graph's partitions.  Two vertices in the output graph
 * are connected if and only if there exists at least one edge between vertices in the 
 * corresponding partitions of the input graph.  If the output graph permits parallel edges,
 * there will be an edge connecting two vertices in the new graph for each such 
 * edge connecting constituent vertices in the input graph.
 * 
 * <p>Concept based on Danyel Fisher's <code>GraphCollapser</code> in JUNG 1.x.
 * 
 */
public class VertexPartitionCollapser<V,E,CV,CE> 
{
    protected Factory<Graph<CV,CE>> graph_factory;
    protected Factory<CV> vertex_factory;
    protected Factory<CE> edge_factory;
    protected Map<Set<V>, CV> set_collapsedv;
    
    /**
     * Creates an instance with the specified graph and element factories.
     * @param vertex_factory used to construct the vertices of the new graph
     * @param edge_factory used to construct the edges of the new graph
     * @param graph_factory used to construct the new graph
     */
    public VertexPartitionCollapser(Factory<Graph<CV,CE>> graph_factory, 
            Factory<CV> vertex_factory, Factory<CE> edge_factory)
    {
        this.graph_factory = graph_factory;
        this.vertex_factory = vertex_factory;
        this.edge_factory = edge_factory;
        this.set_collapsedv = new HashMap<Set<V>, CV>();
    }

    /**
     * Creates a new graph whose vertices correspond to the partitions of the supplied graph.
     * @param partitioning
     * @return a new graph whose vertices correspond to the partitions of the supplied graph
     */
    public Graph<CV,CE> collapseVertexPartitions(VertexPartition<V,E> partitioning)
    {
        Graph<V,E> original = partitioning.getGraph();
        Graph<CV, CE> collapsed = graph_factory.create();
        
        // create vertices in new graph corresponding to equivalence sets in the original graph
        for (Set<V> set : partitioning.getVertexPartitions())
        {
            CV cv = vertex_factory.create();
            collapsed.addVertex(vertex_factory.create());
            set_collapsedv.put(set, cv);
        }

        // create edges in new graph corresponding to edges in original graph
        for (E e : original.getEdges())
        {
            Collection<V> incident = original.getIncidentVertices(e);
            Collection<CV> collapsed_vertices = new HashSet<CV>();
            Map<V, Set<V>> vertex_partitions = partitioning.getVertexToPartitionMap();
            // collect the collapsed vertices corresponding to the original incident vertices
            for (V v : incident)
                collapsed_vertices.add(set_collapsedv.get(vertex_partitions.get(v))); 
            // if there's only one collapsed vertex, continue (no edges to create)
            if (collapsed_vertices.size() > 1)
            {
                CE ce = edge_factory.create();
                collapsed.addEdge(ce, collapsed_vertices);
            }
        }
        return collapsed;
    }
    
    /**
     * Returns a transformer from vertex sets in the original graph to collapsed vertices
     * in the transformed graph.
     */
    public Transformer<Set<V>, CV> getSetToCollapsedVertexTransformer()
    {
        return MapTransformer.getInstance(set_collapsedv);
    }
}
