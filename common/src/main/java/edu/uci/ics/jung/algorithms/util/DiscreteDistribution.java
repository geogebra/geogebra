/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 * 
 * Created on Feb 18, 2004
 */
package edu.uci.ics.jung.algorithms.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * A utility class for calculating properties of discrete distributions.
 * Generally, these distributions are represented as arrays of 
 * <code>double</code> values, which are assumed to be normalized
 * such that the entries in a single array sum to 1.  
 * 
 * @author Joshua O'Madadhain
 */
public class DiscreteDistribution
{

    /**
     * Returns the Kullback-Leibler divergence between the 
     * two specified distributions, which must have the same
     * number of elements.  This is defined as 
     * the sum over all <code>i</code> of 
     * <code>dist[i] * Math.log(dist[i] / reference[i])</code>.
     * Note that this value is not symmetric; see 
     * <code>symmetricKL</code> for a symmetric variant. 
     * @see #symmetricKL(double[], double[])
     */
    public static double KullbackLeibler(double[] dist, double[] reference)
    {
        double distance = 0;

        checkLengths(dist, reference);

        for (int i = 0; i < dist.length; i++)
        {
            if (dist[i] > 0 && reference[i] > 0)
                distance += dist[i] * Math.log(dist[i] / reference[i]);
        }
        return distance;
    }

    /**
     * Returns <code>KullbackLeibler(dist, reference) + KullbackLeibler(reference, dist)</code>.
     * @see #KullbackLeibler(double[], double[])
     */
    public static double symmetricKL(double[] dist, double[] reference)
    {
        return KullbackLeibler(dist, reference)
                + KullbackLeibler(reference, dist);
    }

    /**
     * Returns the squared difference between the 
     * two specified distributions, which must have the same
     * number of elements.  This is defined as 
     * the sum over all <code>i</code> of the square of 
     * <code>(dist[i] - reference[i])</code>.
     */
    public static double squaredError(double[] dist, double[] reference)
    {
        double error = 0;

        checkLengths(dist, reference);

        for (int i = 0; i < dist.length; i++)
        {
            double difference = dist[i] - reference[i];
            error += difference * difference;
        }
        return error;
    }

    /**
     * Returns the cosine distance between the two 
     * specified distributions, which must have the same number
     * of elements.  The distributions are treated as vectors
     * in <code>dist.length</code>-dimensional space.
     * Given the following definitions
     * <ul>
     * <li/><code>v</code> = the sum over all <code>i</code> of <code>dist[i] * dist[i]</code>
     * <li/><code>w</code> = the sum over all <code>i</code> of <code>reference[i] * reference[i]</code>
     * <li/><code>vw</code> = the sum over all <code>i</code> of <code>dist[i] * reference[i]</code>
     * </ul>
     * the value returned is defined as <code>vw / (Math.sqrt(v) * Math.sqrt(w))</code>.
     */
    public static double cosine(double[] dist, double[] reference)
    {
        double v_prod = 0; // dot product x*x
        double w_prod = 0; // dot product y*y
        double vw_prod = 0; // dot product x*y

        checkLengths(dist, reference);

        for (int i = 0; i < dist.length; i++)
        {
            vw_prod += dist[i] * reference[i];
            v_prod += dist[i] * dist[i];
            w_prod += reference[i] * reference[i];
        }
        // cosine distance between v and w
        return vw_prod / (Math.sqrt(v_prod) * Math.sqrt(w_prod));
    }

    /**
     * Returns the entropy of this distribution.
     * High entropy indicates that the distribution is 
     * close to uniform; low entropy indicates that the
     * distribution is close to a Dirac delta (i.e., if
     * the probability mass is concentrated at a single
     * point, this method returns 0).  Entropy is defined as 
     * the sum over all <code>i</code> of 
     * <code>-(dist[i] * Math.log(dist[i]))</code>
     */
    public static double entropy(double[] dist)
    {
        double total = 0;

        for (int i = 0; i < dist.length; i++)
        {
            if (dist[i] > 0)
                total += dist[i] * Math.log(dist[i]);
        }
        return -total;
    }

    /**
     * Throws an <code>IllegalArgumentException</code> if the two arrays are not of the same length.
     */
    protected static void checkLengths(double[] dist, double[] reference)
    {
        if (dist.length != reference.length)
            throw new IllegalArgumentException("Arrays must be of the same length");
    }

    /**
     * Normalizes, with Lagrangian smoothing, the specified <code>double</code>
     * array, so that the values sum to 1 (i.e., can be treated as probabilities).
     * The effect of the Lagrangian smoothing is to ensure that all entries 
     * are nonzero; effectively, a value of <code>alpha</code> is added to each
     * entry in the original array prior to normalization.
     * @param counts
     * @param alpha
     */
    public static void normalize(double[] counts, double alpha)
    {
        double total_count = 0;

        for (int i = 0; i < counts.length; i++)
            total_count += counts[i];

        for (int i = 0; i < counts.length; i++)
            counts[i] = (counts[i] + alpha)
                    / (total_count + counts.length * alpha);
    }

    /**
     * Returns the mean of the specified <code>Collection</code> of
     * distributions, which are assumed to be normalized arrays of 
     * <code>double</code> values.
     * @see #mean(double[][])
     */
    public static double[] mean(Collection<double[]> distributions)
    {
        if (distributions.isEmpty())
            throw new IllegalArgumentException("Distribution collection must be non-empty");
        Iterator<double[]> iter = distributions.iterator();
        double[] first = iter.next();
        double[][] d_array = new double[distributions.size()][first.length];
        d_array[0] = first;
        for (int i = 1; i < d_array.length; i++)
            d_array[i] = iter.next();
        
        return mean(d_array);
    }
    
    /**
     * Returns the mean of the specified array of distributions,
     * represented as normalized arrays of <code>double</code> values.
     * Will throw an "index out of bounds" exception if the 
     * distribution arrays are not all of the same length.
     */
    public static double[] mean(double[][] distributions)
    {
        double[] d_mean = new double[distributions[0].length];
        for (int j = 0; j < d_mean.length; j++)
            d_mean[j] = 0;
            
        for (int i = 0; i < distributions.length; i++)
            for (int j = 0; j < d_mean.length; j++)
                d_mean[j] += distributions[i][j] / distributions.length;
        
        return d_mean;
    }
    
}