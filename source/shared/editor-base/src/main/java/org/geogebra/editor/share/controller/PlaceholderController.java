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

package org.geogebra.editor.share.controller;

import java.util.List;

import org.geogebra.editor.share.catalog.TemplateCatalog;
import org.geogebra.editor.share.tree.CharacterNode;
import org.geogebra.editor.share.tree.FunctionNode;
import org.geogebra.editor.share.tree.InternalNode;
import org.geogebra.editor.share.tree.PlaceholderNode;

public class PlaceholderController {

	/**
	 * Insert placeholders. It ignores placeholders if there are already some characters present.
	 * @param editorState editor state
	 * @param placeholders the list of placeholders to insert
	 */
	public static void insertPlaceholders(EditorState editorState, List<String> placeholders,
			String command) {
		TemplateCatalog catalog = editorState.getCatalog();
		InternalNode parent = editorState.getCurrentNode().getParent();
		if (parent instanceof FunctionNode node) {
			node.setCommandForSyntax(command);
		}
		int firstPlaceholderOffset = -1;
		for (int i = 0; i < placeholders.size(); i++) {
			if (i != 0) {
				CharacterNode comma = new CharacterNode(catalog.getCharacter(","));
				editorState.addArgument(comma);
			}
			int currentOffset = editorState.getCurrentOffset();
			int currentSize = editorState.getCurrentNode().size();
			if (parent instanceof FunctionNode node) {
				node.getPlaceholders().add(placeholders.get(i));
			}
			if (currentOffset < currentSize) {
				editorState.setCurrentOffset(currentSize);
			} else {
				editorState.addArgument(new PlaceholderNode(placeholders.get(i)));
				if (firstPlaceholderOffset == -1) {
					firstPlaceholderOffset = editorState.getCurrentNode().size() - 1;
				}
			}
		}
		if (firstPlaceholderOffset != -1) {
			editorState.setCurrentOffset(firstPlaceholderOffset);
		}
	}
}
