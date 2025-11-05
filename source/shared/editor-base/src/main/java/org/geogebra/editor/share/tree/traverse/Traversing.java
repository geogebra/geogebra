/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
*/

package org.geogebra.editor.share.tree.traverse;

import org.geogebra.editor.share.tree.Node;

/**
 * Visitor for the tree of {@link Node}s
 */
@FunctionalInterface
public interface Traversing {

	/**
	 * Process a value locally, without recursion.
	 * @param node value
	 * @return replacement of processed node
	 */
	Node process(Node node);

}
