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

import org.geogebra.editor.share.tree.CharacterNode;
import org.geogebra.editor.share.tree.InternalNode;
import org.geogebra.editor.share.tree.Node;
import org.geogebra.editor.share.tree.traverse.Traversing;

public class QuestionMarkRemover implements Traversing {

	@Override
	public Node process(Node node) {
		if (!(node instanceof InternalNode)) {
			return node;
		}

		InternalNode container = (InternalNode) node;

		for (int i = 0; i < container.size(); i++) {
			Node argument = container.getChild(i);
			if (isQuestionMark(argument)) {
				addPlaceholder(container, i);
			}
		}
		return node;
	}

	private void addPlaceholder(InternalNode container, int i) {
		container.removeChild(i);
	}

	private boolean isQuestionMark(Node node) {
		return node instanceof CharacterNode && ((CharacterNode) node).isUnicode('?');
	}

}
