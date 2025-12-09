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
