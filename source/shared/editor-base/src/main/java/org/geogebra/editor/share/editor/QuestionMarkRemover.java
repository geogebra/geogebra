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
		return node instanceof CharacterNode cn && cn.isUnicode('?');
	}

}
