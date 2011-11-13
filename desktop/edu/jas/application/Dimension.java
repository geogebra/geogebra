/*
 * $Id: Dimension.java 3058 2010-03-27 11:05:23Z kredel $
 */

package edu.jas.application;


import java.io.Serializable;
import java.util.Arrays;
import java.util.Set;


/**
 * Container for dimension parameters.
 * @author Heinz Kredel
 */
public class Dimension implements Serializable {


    /**
     * Ideal dimension.
     */
    public final int d;


    /**
     * Indices of a maximal independent set (of variables).
     */
    public final Set<Integer> S;


    /**
     * Set of indices of all maximal independent sets (of variables).
     */
    public final Set<Set<Integer>> M;


    /**
     * Names of all variables.
     */
    public final String[] v;


    /**
     * Constructor.
     * @param d ideal dimension.
     * @param S indices of a maximal independent set (of variables)
     * @param M set of indices of all maximal independent sets (of variables)
     * @param v names of all variables
     */
    public Dimension(int d, Set<Integer> S, Set<Set<Integer>> M, String[] v) {
        this.d = d;
        this.S = S;
        this.M = M;
        this.v = v;
    }


    /**
     * String representation of the ideal.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("Dimension( " + d + ", ");
        if (v == null) {
            sb.append("" + S + ", " + M + " )");
            return sb.toString();
        }
        String[] s = new String[S.size()];
        int j = 0;
        for (Integer i : S) {
            s[j] = v[i];
            j++;
        }
        sb.append(Arrays.toString(s) + ", ");
        sb.append("[ ");
        boolean first = true;
        for (Set<Integer> m : M) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            s = new String[m.size()];
            j = 0;
            for (Integer i : m) {
                s[j] = v[i];
                j++;
            }
            sb.append(Arrays.toString(s));
        }
        sb.append(" ] )");
        return sb.toString();
    }
}
