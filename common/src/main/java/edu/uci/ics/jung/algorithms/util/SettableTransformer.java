/*
 * Created on Aug 5, 2007
 *
 * Copyright (c) 2007, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.algorithms.util;

import org.apache.commons.collections15.Transformer;

/**
 * An interface for classes that can set the value to be returned (from <code>transform()</code>)
 * when invoked on a given input.
 * 
 * @author Joshua O'Madadhain
 */
public interface SettableTransformer<I, O> extends Transformer<I, O>
{
    /**
     * Sets the value (<code>output</code>) to be returned by a call to 
     * <code>transform(input)</code>).
     * @param input
     * @param output
     */
    public void set(I input, O output);
}
