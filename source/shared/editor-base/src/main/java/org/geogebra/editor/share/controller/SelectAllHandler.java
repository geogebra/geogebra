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

import org.geogebra.editor.share.tree.ArrayNode;
import org.geogebra.editor.share.tree.CharPlaceholderNode;
import org.geogebra.editor.share.tree.CharacterNode;
import org.geogebra.editor.share.tree.FunctionNode;
import org.geogebra.editor.share.tree.Node;
import org.geogebra.editor.share.tree.SequenceNode;

import com.google.j2objc.annotations.Weak;

/**
 * Class to handle Ctrl-A in inputboxes
 *
 * @author laszlo
 */
public class SelectAllHandler {
	@Weak
	private final EditorState editorState;

	/**
	 *
	 * @param editorState {@link EditorState}
	 */
	public SelectAllHandler(EditorState editorState) {
		this.editorState = editorState;
	}

	/**
	 * Select all elements based on the context:
	 * Matrix elements for matrices, coordinates for points, etc.
	 */
	public void execute() {
		SequenceNode root = editorState.getRootNode();
		if (root.isProtected()) {
			selectProtectedContent();
		} else {
			if (isCharPlaceholder(root)) {
				return;
			}
			editorState.selectSubsequence(root, 0, root.size());
		}
	}

	private void selectProtectedContent() {
		Node first = editorState.getRootNode().getChild(0);
		if (first instanceof ArrayNode array) {
			if (array.isMatrix()) {
				editorState.selectUpToRootComponent();
			} else {
				selectListElement(array.getChild(0));
			}
		} else {
			editorState.selectSubsequence(editorState.getCurrentNode(), 0,
					editorState.getCurrentNode().size());
		}
	}

	private boolean isCharPlaceholder(Node selectionStart) {
		return selectionStart instanceof CharPlaceholderNode
				|| (selectionStart instanceof FunctionNode fn
		&& isCharPlaceholder(
				fn.getChild(0)));
	}

	private void selectListElement(SequenceNode sequence) {
		if (getCurrentField() != sequence) {
			selectAllCompositeElement(sequence);
		} else {
			SequenceNode content = sequenceWithoutBrackets(sequence);
			int left = firstSeparatorOnLeft(content);
			int right = firstSeparatorOnRight(content);
			editorState.selectSubsequence(content, left, right);

			if (isCharPlaceholder(editorState.getSelectionStart()) || right < left) {
				editorState.resetSelection();
			}
		}
	}

	private SequenceNode getCurrentField() {
		return editorState.getCurrentNode();
	}

	private SequenceNode sequenceWithoutBrackets(SequenceNode sequence) {
		return sequence.size() == 1 && sequence.getChild(0) instanceof ArrayNode
				? ((ArrayNode) sequence.getChild(0)).getChild(0)
				: sequence;
	}

	private void selectAllCompositeElement(SequenceNode sequence) {
		SequenceNode field = getCurrentField();
		SequenceNode parent = field.getParentSequence();
		while (parent != sequence && parent != null) {
			field = parent;
			parent = parent.getParentSequence();

		}
		editorState.selectSubsequence(field, 0, field.size());
	}

	private int firstSeparatorOnRight(SequenceNode sequence) {
		int offset = editorState.getCurrentOffset();
		if (isSeparatorAt(sequence, offset)) {
			return offset;
		}

		int i = offset;
		while (i < sequence.getArgumentCount() && !isSeparatorAt(sequence, i)) {
			i++;
		}

		return i;
	}

	private boolean isSeparatorAt(SequenceNode sequence, int index) {
		Node argument = sequence.getChild(index);
		return argument instanceof CharacterNode cn && cn.isSeparator();
	}

	private int firstSeparatorOnLeft(SequenceNode sequence) {
		int offset = editorState.getCurrentOffset();
		int charIndex = isSeparatorAt(sequence, offset)
				? offset - 1
				: offset;
		while (charIndex > 0 && !isSeparatorAt(sequence, charIndex)) {
			charIndex--;
		}
		if (charIndex == 0 && isSeparatorAt(sequence, 0)) {
			return 1;
		}
		return charIndex == 0 ? 0 : charIndex + 1;
	}
}
