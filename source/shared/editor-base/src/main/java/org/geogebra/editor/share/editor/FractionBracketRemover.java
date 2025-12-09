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

package org.geogebra.editor.share.editor;

import org.geogebra.editor.share.catalog.Tag;
import org.geogebra.editor.share.tree.ArrayNode;
import org.geogebra.editor.share.tree.FunctionNode;
import org.geogebra.editor.share.tree.Node;
import org.geogebra.editor.share.tree.SequenceNode;
import org.geogebra.editor.share.tree.traverse.Traversing;

public class FractionBracketRemover implements Traversing {

	@Override
	public Node process(Node node) {
		if (node instanceof ArrayNode
				&& ((ArrayNode) node).getOpenDelimiter().getCharacter() == '('
				&& ((ArrayNode) node).size() == 1) {
			if (isFollowedByScript(node)) {
				return node;
			}
			SequenceNode bracketContent = ((ArrayNode) node).getChild(0);
			if (isFraction(bracketContent.getChild(0)) && bracketContent.size() == 1) {
				Node childNode = bracketContent.getChild(0);
				return childNode.traverse(this);
			}
		}
		return node;
	}

	private boolean isFollowedByScript(Node node) {
		return node.getParentIndex() < node.getParent().size()
				&& node.getParent().isScript(node.getParentIndex() + 1);
	}

	private boolean isFraction(Node argument) {
		return argument instanceof FunctionNode
				&& ((FunctionNode) argument).getName() == Tag.FRAC;
	}
}
