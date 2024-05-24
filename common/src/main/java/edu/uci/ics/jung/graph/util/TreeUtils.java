/*
 * Created on Mar 3, 2007
 *
 * Copyright (c) 2007, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.graph.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Tree;

/**
 * Contains static methods for operating on instances of <code>Tree</code>.
 */
public class TreeUtils {
	/**
	 * Returns the roots of this forest.
	 * 
	 * @param <V>
	 *            the vertex type
	 * @param <E>
	 *            the edge type
	 */
	public static <V, E> List<V> getRoots(Forest<V, E> forest) {
		List<V> roots = new ArrayList<V>();
		for (Tree<V, E> tree : forest.getTrees()) {
			roots.add(tree.getRoot());
		}
		return roots;
	}

	/**
	 * Returns the subtree of <code>tree</code> which is rooted at
	 * <code>root</code> as a <code>Forest&lt;V,E&gt;</code>. The tree returned is an
	 * independent entity, although it uses the same vertex and edge objects.
	 * 
	 * @param <V>
	 *            the vertex type
	 * @param <E>
	 *            the edge type
	 * @param forest
	 *            the tree whose subtree is to be extracted
	 * @param root
	 *            the root of the subtree to be extracted
	 * @return the subtree of <code>tree</code> which is rooted at
	 *         <code>root</code>
	 * @throws IllegalArgumentException
	 *             if root is not in the forest
	 */
	public static <V, E> Tree<V, E> getSubTree(Forest<V, E> forest, V root) {
		if (!forest.containsVertex(root)) {
			throw new IllegalArgumentException(
					"Specified tree does not contain the specified root as a vertex");
		}
		Forest<V, E> subforest = (Forest<V, E>) forest.newInstance();
		subforest.addVertex(root);
		growSubTree(forest, subforest, root);

		return subforest.getTrees().iterator().next();
	}

	/**
	 * Populates <code>subtree</code> with the subtree of <code>tree</code>
	 * which is rooted at <code>root</code>.
	 * 
	 * @param <V>
	 *            the vertex type
	 * @param <E>
	 *            the edge type
	 * @param tree
	 *            the tree whose subtree is to be extracted
	 * @param subTree
	 *            the tree instance which is to be populated with the subtree of
	 *            <code>tree</code>
	 * @param root
	 *            the root of the subtree to be extracted
	 */
	public static <V, E> void growSubTree(Forest<V, E> tree,
			Forest<V, E> subTree, V root) {
		if (tree.getSuccessorCount(root) > 0) {
			Collection<E> edges = tree.getOutEdges(root);
			for (E e : edges) {
				subTree.addEdge(e, tree.getEndpoints(e));
			}
			Collection<V> kids = tree.getSuccessors(root);
			for (V kid : kids) {
				growSubTree(tree, subTree, kid);
			}
		}
	}

	/**
	 * Connects <code>subTree</code> to <code>tree</code> by attaching it as a
	 * child of <code>node</code> with edge <code>connectingEdge</code>.
	 * 
	 * @param <V>
	 *            the vertex type
	 * @param <E>
	 *            the edge type
	 * @param tree
	 *            the tree to which <code>subTree</code> is to be added
	 * @param subTree
	 *            the tree which is to be grafted on to <code>tree</code>
	 * @param node
	 *            the parent of <code>subTree</code> in its new position in
	 *            <code>tree</code>
	 * @param connectingEdge
	 *            the edge used to connect <code>subtree</code>'s root as a
	 *            child of <code>node</code>
	 */
	public static <V, E> void addSubTree(Forest<V, E> tree,
			Forest<V, E> subTree, V node, E connectingEdge) {
		if (node != null && !tree.containsVertex(node)) {
			throw new IllegalArgumentException(
					"Specified tree does not contain the specified node as a vertex");
		}
		V root = subTree.getTrees().iterator().next().getRoot();
		addFromSubTree(tree, subTree, connectingEdge, node, root);
	}

	public static <V, E> void addFromSubTree(Forest<V, E> tree,
			Forest<V, E> subTree, E edge, V parent, V root) {

		// add edge connecting parent and root to tree
		if (edge != null && parent != null) {
			tree.addEdge(edge, parent, root);
		} else {
			tree.addVertex(root);
		}

		Collection<E> outEdges = subTree.getOutEdges(root);
		for (E e : outEdges) {
			V opposite = subTree.getOpposite(root, e);
			addFromSubTree(tree, subTree, e, root, opposite);
		}
	}

	// FIXME: not done or integrated yet
	// private static <V,E> void addChildrenToForest(Forest<V,E> forest,
	// Tree<V,E> tree,
	// V subtree_root)
	// {
	// V parent = tree.getPredecessors(subtree_root).iterator().next();
	// for (E e : tree.getOutEdges(subtree_root))
	// {
	// V child = tree.getOpposite(subtree_root, e);
	// addChildrenToForest(forest, tree, child);
	// }
	// }
}
