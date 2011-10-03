/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package edu.uci.ics.jung.algorithms.filters;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.Graph;



/**
 * An interface for classes that return a subset of the input <code>Graph</code>
 * as a <code>Graph</code>.  The <code>Graph</code> returned may be either a
 * new graph or a view into an existing graph; the documentation for the filter
 * must specify which.
 * 
 * @author danyelf
 */
public interface Filter<V,E> extends Transformer<Graph<V,E>, Graph<V,E>>{ }
