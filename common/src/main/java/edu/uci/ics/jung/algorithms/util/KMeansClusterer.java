/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
/*
 * Created on Aug 9, 2004
 *
 */
package edu.uci.ics.jung.algorithms.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;



/**
 * Groups items into a specified number of clusters, based on their proximity in
 * d-dimensional space, using the k-means algorithm. Calls to
 * <code>cluster</code> will terminate when either of the two following
 * conditions is true:
 * <ul>
 * <li/>the number of iterations is &gt; <code>max_iterations</code> 
 * <li/>none of the centroids has moved as much as <code>convergence_threshold</code>
 * since the previous iteration
 * </ul>
 * 
 * @author Joshua O'Madadhain
 */
public class KMeansClusterer<T>
{
    protected int max_iterations;
    protected double convergence_threshold;
    protected Random rand;

    /**
     * Creates an instance whose termination conditions are set according
     * to the parameters.  
     */
    public KMeansClusterer(int max_iterations, double convergence_threshold)
    {
        this.max_iterations = max_iterations;
        this.convergence_threshold = convergence_threshold;
        this.rand = new Random();
    }

    /**
     * Creates an instance with max iterations of 100 and convergence threshold
     * of 0.001.
     */
    public KMeansClusterer()
    {
        this(100, 0.001);
    }

    /**
     * Returns the maximum number of iterations.
     */
    public int getMaxIterations()
    {
        return max_iterations;
    }

    /**
     * Sets the maximum number of iterations.
     */
    public void setMaxIterations(int max_iterations)
    {
        if (max_iterations < 0)
            throw new IllegalArgumentException("max iterations must be >= 0");

        this.max_iterations = max_iterations;
    }

    /**
     * Returns the convergence threshold.
     */
    public double getConvergenceThreshold()
    {
        return convergence_threshold;
    }

    /**
     * Sets the convergence threshold.
     * @param convergence_threshold
     */
    public void setConvergenceThreshold(double convergence_threshold)
    {
        if (convergence_threshold <= 0)
            throw new IllegalArgumentException("convergence threshold " +
                "must be > 0");

        this.convergence_threshold = convergence_threshold;
    }

    /**
     * Returns a <code>Collection</code> of clusters, where each cluster is
     * represented as a <code>Map</code> of <code>Objects</code> to locations
     * in d-dimensional space.
     * @param object_locations  a map of the Objects to cluster, to
     * <code>double</code> arrays that specify their locations in d-dimensional space.
     * @param num_clusters  the number of clusters to create
     * @throws NotEnoughClustersException
     */
    @SuppressWarnings("unchecked")
    public Collection<Map<T, double[]>> cluster(Map<T, double[]> object_locations, int num_clusters)
    {
        if (object_locations == null || object_locations.isEmpty())
            throw new IllegalArgumentException("'objects' must be non-empty");

        if (num_clusters < 2 || num_clusters > object_locations.size())
            throw new IllegalArgumentException("number of clusters " +
                "must be >= 2 and <= number of objects (" +
                object_locations.size() + ")");


        Set<double[]> centroids = new HashSet<double[]>();

        Object[] obj_array = object_locations.keySet().toArray();
        Set<T> tried = new HashSet<T>();

        // create the specified number of clusters
        while (centroids.size() < num_clusters && tried.size() < object_locations.size())
        {
            T o = (T)obj_array[(int)(rand.nextDouble() * obj_array.length)];
            tried.add(o);
            double[] mean_value = object_locations.get(o);
            boolean duplicate = false;
            for (double[] cur : centroids)
            {
                if (Arrays.equals(mean_value, cur))
                    duplicate = true;
            }
            if (!duplicate)
                centroids.add(mean_value);
        }

        if (tried.size() >= object_locations.size())
            throw new NotEnoughClustersException();

        // put items in their initial clusters
        Map<double[], Map<T, double[]>> clusterMap = assignToClusters(object_locations, centroids);

        // keep reconstituting clusters until either
        // (a) membership is stable, or
        // (b) number of iterations passes max_iterations, or
        // (c) max movement of any centroid is <= convergence_threshold
        int iterations = 0;
        double max_movement = Double.POSITIVE_INFINITY;
        while (iterations++ < max_iterations && max_movement > convergence_threshold)
        {
            max_movement = 0;
            Set<double[]> new_centroids = new HashSet<double[]>();
            // calculate new mean for each cluster
            for (Map.Entry<double[], Map<T, double[]>> entry : clusterMap.entrySet())
            {
                double[] centroid = entry.getKey();
                Map<T, double[]> elements = entry.getValue();
                ArrayList<double[]> locations = new ArrayList<double[]>(elements.values());

                double[] mean = DiscreteDistribution.mean(locations);
                max_movement = Math.max(max_movement,
                    Math.sqrt(DiscreteDistribution.squaredError(centroid, mean)));
                new_centroids.add(mean);
            }

            // TODO: check membership of clusters: have they changed?

            // regenerate cluster membership based on means
            clusterMap = assignToClusters(object_locations, new_centroids);
        }
        return clusterMap.values();
    }

    /**
     * Assigns each object to the cluster whose centroid is closest to the
     * object.
     * @param object_locations  a map of objects to locations
     * @param centroids         the centroids of the clusters to be formed
     * @return a map of objects to assigned clusters
     */
    protected Map<double[], Map<T, double[]>> assignToClusters(Map<T, double[]> object_locations, Set<double[]> centroids)
    {
        Map<double[], Map<T, double[]>> clusterMap = new HashMap<double[], Map<T, double[]>>();
        for (double[] centroid : centroids)
            clusterMap.put(centroid, new HashMap<T, double[]>());

        for (Map.Entry<T, double[]> object_location : object_locations.entrySet())
        {
            T object = object_location.getKey();
            double[] location = object_location.getValue();

            // find the cluster with the closest centroid
            Iterator<double[]> c_iter = centroids.iterator();
            double[] closest = c_iter.next();
            double distance = DiscreteDistribution.squaredError(location, closest);

            while (c_iter.hasNext())
            {
                double[] centroid = c_iter.next();
                double dist_cur = DiscreteDistribution.squaredError(location, centroid);
                if (dist_cur < distance)
                {
                    distance = dist_cur;
                    closest = centroid;
                }
            }
            clusterMap.get(closest).put(object, location);
        }

        return clusterMap;
    }

    /**
     * Sets the seed used by the internal random number generator.
     * Enables consistent outputs.
     */
    public void setSeed(int random_seed)
    {
        this.rand = new Random(random_seed);
    }

    /**
     * An exception that indicates that the specified data points cannot be
     * clustered into the number of clusters requested by the user.
     * This will happen if and only if there are fewer distinct points than
     * requested clusters.  (If there are fewer total data points than
     * requested clusters, <code>IllegalArgumentException</code> will be thrown.)
     *
     * @author Joshua O'Madadhain
     */
    @SuppressWarnings("serial")
    public static class NotEnoughClustersException extends RuntimeException
    {
        @Override
        public String getMessage()
        {
            return "Not enough distinct points in the input data set to form " +
                    "the requested number of clusters";
        }
    }
}
