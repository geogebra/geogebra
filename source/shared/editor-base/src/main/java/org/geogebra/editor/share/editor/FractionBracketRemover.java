/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
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
