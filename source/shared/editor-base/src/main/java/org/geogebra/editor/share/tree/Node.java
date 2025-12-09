/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.editor.share.tree;

import java.io.Serializable;

import org.geogebra.editor.share.catalog.Tag;
import org.geogebra.editor.share.tree.inspect.Inspectable;
import org.geogebra.editor.share.tree.traverse.Traversable;

import com.google.j2objc.annotations.Weak;

/**
 * This class represents abstract model element.
 */
abstract public class Node implements Traversable, Inspectable, Serializable {

    @Weak
	private InternalNode parent;

    /**
	 * Gets parent of this node.
	 * 
	 * @return parent of this node.
	 */
    public InternalNode getParent() {
        return parent;
    }

    /**
	 * Sets parent of this node.
	 * 
	 * @param container
	 *            parent
	 */
    void setParent(InternalNode container) {
        this.parent = container;
    }

    /**
	 * Gets index of this node within its parent node.
	 * 
	 * @return index within parent
	 */
    public int getParentIndex() {
        if (parent == null) {
            return 0;
        }
        int index = parent.indexOf(this);
        if (index >= 0) {
            return index;
        }

        throw new RuntimeException("Parent reference is not set correctly");
    }

	/**
	 * @param tag
	 *            container tag
	 * @return whether this is a container with given tag
	 */
	public boolean hasTag(Tag tag) {
		return false;
	}

	/**
	 * @return whether this is comma or vertical bar
	 */
	public boolean isFieldSeparator() {
		return false;
	}

	/**
	 * @return next sibling with the same parent
	 */
	public Node nextSibling() {
		if (parent == null) {
			return null;
		}
		int parentIndex = getParentIndex();
		return parentIndex >= parent.size() - 1 ? null : getParent().getChild(parentIndex + 1);
	}
}
