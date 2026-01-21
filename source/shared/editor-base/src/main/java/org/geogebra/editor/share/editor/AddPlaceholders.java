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

import org.geogebra.editor.share.tree.ArrayNode;
import org.geogebra.editor.share.tree.CharPlaceholderNode;
import org.geogebra.editor.share.tree.Node;
import org.geogebra.editor.share.tree.PlaceholderNode;
import org.geogebra.editor.share.tree.SequenceNode;

public class AddPlaceholders {
	private final QuestionMarkRemover questionMarkRemover
			= new QuestionMarkRemover();

	/**
	 * Searches and adds possible character placeholders in node.
	 * @param node to add in possible placeholders.
	 */
	public void process(Node node) {
		if (node != null) {
			node.traverse(questionMarkRemover);
		}
		if (node instanceof ArrayNode array) {
			if (array.getColumns() >= 1) {
				for (int i = 0; i < array.size(); i++) {
					SequenceNode sequence = array.getChild(i);
					processSequence(sequence);
				}
			}
		}
	}

	private void processSequence(SequenceNode sequence) {
		int lastPosition = sequence.size() - 1;
		Node first = sequence.getChild(0);
		Node last = sequence.getChild(lastPosition);

		if (isFieldSeparator(first)) {
			addPlaceholder(sequence, 0);
		}

		if (isFieldSeparator(last)) {
			appendPlaceholder(sequence);
		}

		for (int i = 1; i < lastPosition + 1; i++) {
			Node current = sequence.getChild(i);
			Node next = sequence.getChild(i + 1);
			if (isFieldSeparator(current) && isFieldSeparator(next)) {
				addPlaceholder(sequence, i + 1);
			}
		}
	}

	private boolean isFieldSeparator(Node node) {
		return node != null && node.isFieldSeparator();
	}

	private void addPlaceholder(SequenceNode sequence, int i) {
		sequence.addChild(i, newPlaceholder());
	}

	private PlaceholderNode newPlaceholder() {
		return new CharPlaceholderNode();
	}

	private void appendPlaceholder(SequenceNode sequence) {
		sequence.addChild(newPlaceholder());
	}
}
