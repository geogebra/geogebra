/*
 * Created on Oct 17, 2005
 *
 * Copyright (c) 2005, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.graph;

/**
 * A tagging interface for implementations of <code>Graph</code> 
 * that accept only directed edges.
 * 
 * @author Tom Nelson - tomnelson@dev.java.net
 *
 * @param <V>   type specification for vertices
 * @param <E>   type specification for edges
 */
public interface DirectedGraph<V,E> extends Graph<V,E> {
}
