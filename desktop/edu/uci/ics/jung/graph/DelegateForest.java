package edu.uci.ics.jung.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.graph.util.TreeUtils;

/**
 * An implementation of <code>Forest<V,E></code> that delegates to a specified <code>DirectedGraph</code>
 * instance.
 * @author Tom Nelson
 *
 * @param <V> the vertex type
 * @param <E> the edge type
 */
@SuppressWarnings("serial")
public class DelegateForest<V,E> extends GraphDecorator<V,E> implements Forest<V,E> 
{
	/**
	 * Creates an instance backed by a new {@code DirectedSparseGraph} instance.
	 */
	public DelegateForest() {
		this(new DirectedSparseGraph<V,E>());
	}

	/**
	 * Creates an instance backed by the input {@code DirectedGraph} i
	 */
	public DelegateForest(DirectedGraph<V,E> delegate) {
		super(delegate);
	}

	/**
	 * Add an edge to the tree, connecting v1, the parent and v2, the child.
	 * v1 must already exist in the tree, and v2 must not already exist
	 * the passed edge must be unique in the tree. Passing an edgeType
	 * other than EdgeType.DIRECTED may cause an illegal argument exception
	 * in the delegate graph.
	 *
	 * @param e a unique edge to add
	 * @param v1 the parent node
	 * @param v2 the child node
	 * @param edgeType should be EdgeType.DIRECTED
	 * @return true if this call mutates the underlying graph
	 * @see edu.uci.ics.jung.graph.Graph#addEdge(java.lang.Object, java.lang.Object, java.lang.Object, edu.uci.ics.jung.graph.util.EdgeType)
	 */
	@Override
	public boolean addEdge(E e, V v1, V v2, EdgeType edgeType) {
		if(delegate.getVertices().contains(v1) == false) {
			throw new IllegalArgumentException("Tree must already contain "+v1);
		}
		if(delegate.getVertices().contains(v2)) {
			throw new IllegalArgumentException("Tree must not already contain "+v2);
		}
		return delegate.addEdge(e, v1, v2, edgeType);
	}

	/**
	 * Add vertex as a root of the tree
	 *
	 * @param vertex the tree root to add
	 * @return true if this call mutates the underlying graph
	 * @see edu.uci.ics.jung.graph.Graph#addVertex(java.lang.Object)
	 */
	@Override
	public boolean addVertex(V vertex) {
		setRoot(vertex);
		return true;
	}

	/**
	 * Removes <code>edge</code> from this tree, and the subtree rooted
	 * at the child vertex incident to <code>edge</code>.
	 * (The subtree is removed to ensure that the tree in which the edge
	 * was found is still a tree rather than a forest.  To change this
	 * behavior so that the
	 * @param edge the edge to remove
	 * @return <code>true</code> iff the tree was modified
	 * @see edu.uci.ics.jung.graph.Hypergraph#removeEdge(java.lang.Object)
	 */
	@Override
	public boolean removeEdge(E edge) {
	    return removeEdge(edge, true);
	}

	/**
	 * Removes <code>edge</code> from this tree.
	 * If <code>remove_subtree</code> is <code>true</code>, removes
	 * the subtree rooted at the child vertex incident to <code>edge</code>.
	 * Otherwise, leaves the subtree intact as a new component tree of this
	 * forest.
	 * @param edge the edge to remove
	 * @param remove_subtree if <code>true</code>, remove the subtree
	 * @return <code>true</code> iff the tree was modified
	 */
	public boolean removeEdge(E edge, boolean remove_subtree)
	{
        if (!delegate.containsEdge(edge))
            return false;
        V child = getDest(edge);
        if (remove_subtree)
            return removeVertex(child);
        else
        {
            delegate.removeEdge(edge);
            return false;
        }
	}

	/**
	 * Removes <code>vertex</code> from this tree, and the subtree
	 * rooted at <code>vertex</code>.
	 * @param vertex the vertex to remove
	 * @return <code>true</code> iff the tree was modified
	 * @see edu.uci.ics.jung.graph.Hypergraph#removeVertex(java.lang.Object)
	 */
	@Override
	public boolean removeVertex(V vertex) {
	    return removeVertex(vertex, true);
	}

	/**
	 * Removes <code>vertex</code> from this tree.
     * If <code>remove_subtrees</code> is <code>true</code>, removes
     * the subtrees rooted at the children of <code>vertex</code>.
     * Otherwise, leaves these subtrees intact as new component trees of this
     * forest.
     * @param vertex the vertex to remove
	 * @param remove_subtrees if <code>true</code>, remove the subtrees
	 * rooted at <code>vertex</code>'s children
	 * @return <code>true</code> iff the tree was modified
	 */
	public boolean removeVertex(V vertex, boolean remove_subtrees)
	{
        if (!delegate.containsVertex(vertex))
            return false;
        if (remove_subtrees)
            for(V v : new ArrayList<V>(delegate.getSuccessors(vertex)))
                removeVertex(v, true);
        return delegate.removeVertex(vertex);
	}

	/**
	 * returns an ordered list of the nodes beginning at the root
	 * and ending at the passed child node, including all intermediate
	 * nodes.
	 * @param child the last node in the path from the root
	 * @return an ordered list of the nodes from root to child
	 */
	public List<V> getPath(V child) {
        if (!delegate.containsVertex(child))
            return null;
		List<V> list = new ArrayList<V>();
		list.add(child);
		V parent = getParent(child);
		while(parent != null) {
			list.add(list.size(), parent);
			parent = getParent(parent);
		}
		return list;
	}

	public V getParent(V child) {
        if (!delegate.containsVertex(child))
            return null;
		Collection<V> parents = delegate.getPredecessors(child);
		if(parents.size() > 0) {
			return parents.iterator().next();
		}
		return null;
	}

	/**
	 * getter for the root of the tree
	 * returns null, as this tree has >1 roots
	 * @return the root
	 */
	public V getRoot() {
		return null;
	}

	/**
	 * adds root as a root of the tree
	 * @param root the initial tree root
	 */
	public void setRoot(V root) {
		delegate.addVertex(root);
	}

	/**
	 * removes a node from the tree, causing all descendants of
	 * the removed node also to be removed
	 * @param orphan the node to remove
	 * @return whether this call mutates the underlying graph
	 */
	public boolean removeChild(V orphan) {
		return removeVertex(orphan);
	}

	/**
	 * computes and returns the depth of the tree from the
	 * root to the passed vertex
	 *
	 * @param v the node who's depth is computed
	 * @return the depth to the passed node.
	 */
	public int getDepth(V v) {
		return getPath(v).size();
	}

	/**
	 * computes and returns the height of the tree
	 *
	 * @return the height
	 */
	public int getHeight() {
		int height = 0;
		for(V v : getVertices()) {
			height = Math.max(height, getDepth(v));
		}
		return height;
	}

	/**
	 * computes and returns whether the passed node is
	 * neither the root, nor a leaf node.
	 * @return <code>true</code> if <code>v</code> is neither a leaf
	 * nor a root
	 */
	public boolean isInternal(V v) {
		return isLeaf(v) == false && isRoot(v) == false;
	}

	/**
	 * Returns true if {@code v} has no child nodes.
	 */
	public boolean isLeaf(V v) {
		return getChildren(v).size() == 0;
	}

	/**
	 * Returns the children of {@code v}.
	 */
	public Collection<V> getChildren(V v) {
		return delegate.getSuccessors(v);
	}

	/**
	 * Returns true if {@code v} has no parent node.
	 */
	public boolean isRoot(V v) {
		return getParent(v) == null;
	}

	@Override
    public int getIncidentCount(E edge)
    {
        return 2;
    }

	@SuppressWarnings("unchecked")
	@Override
	public boolean addEdge(E edge, Collection<? extends V> vertices) {
		Pair<V> pair = null;
		if(vertices instanceof Pair) {
			pair = (Pair<V>)vertices;
		} else {
			pair = new Pair<V>(vertices);
		}
		return addEdge(edge, pair.getFirst(), pair.getSecond());
	}

	/**
	 * Returns the root of each tree of this forest as a {@code Collection}.
	 */
	public Collection<V> getRoots() {
		Collection<V> roots = new HashSet<V>();
		for(V v : delegate.getVertices()) {
			if(delegate.getPredecessorCount(v) == 0) {
				roots.add(v);
			}
		}
		return roots;
	}

	public Collection<Tree<V, E>> getTrees() {
		Collection<Tree<V,E>> trees = new HashSet<Tree<V,E>>();
		for(V v : getRoots()) {
			Tree<V,E> tree = new DelegateTree<V,E>();
			tree.addVertex(v);
			TreeUtils.growSubTree(this, tree, v);
			trees.add(tree);
		}
		return trees;
	}

	/**
	 * Adds {@code tree} to this graph as an element of this forest.
	 *
	 * @param tree the tree to add to this forest as a component
	 */
	public void addTree(Tree<V,E> tree) {
		TreeUtils.addSubTree(this, tree, null, null);
	}

    public int getChildCount(V vertex)
    {
        return delegate.getSuccessorCount(vertex);
    }

    public Collection<E> getChildEdges(V vertex)
    {
        return delegate.getOutEdges(vertex);
    }

    public E getParentEdge(V vertex)
    {
        if (isRoot(vertex))
            return null;
        return delegate.getInEdges(vertex).iterator().next();
    }

}
