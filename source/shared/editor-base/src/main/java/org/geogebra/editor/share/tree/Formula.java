/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
*/

package org.geogebra.editor.share.tree;

import org.geogebra.editor.share.catalog.TemplateCatalog;

/**
 * Math editor content
 */
public class Formula {

	private final TemplateCatalog catalog;
	private final SequenceNode rootNode;

	/**
	 * @param catalog catalog
	 * @param rootNode root container
	 */
	public Formula(TemplateCatalog catalog, SequenceNode rootNode) {
		this.catalog = catalog;
		this.rootNode = rootNode;
		rootNode.setParent(null);
	}

	/**
	 * @param catalog catalog
	 */
	public Formula(TemplateCatalog catalog) {
		this(catalog, new SequenceNode());
	}

	/**
	 * @return catalog
	 */
	public TemplateCatalog getCatalog() {
		return catalog;
	}

	/**
	 * @return root node
	 */
	public SequenceNode getRootNode() {
		return rootNode;
	}

	/**
	 * @return whether the content is empty
	 */
	public boolean isEmpty() {
		return rootNode.size() == 0;
	}

}
