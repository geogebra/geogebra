/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
*/

package org.geogebra.editor.share.controller;

import org.geogebra.editor.share.tree.ArrayNode;
import org.geogebra.editor.share.tree.CharPlaceholderNode;
import org.geogebra.editor.share.tree.CharacterNode;
import org.geogebra.editor.share.tree.FunctionNode;
import org.geogebra.editor.share.tree.InternalNode;
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
			setSelectionStart(root);
			setSelectionEnd(root);
		}
	}

	private void selectProtectedContent() {
		Node first = editorState.getRootNode().getChild(0);
		Node selectionStart = editorState.getCurrentNode().getChild(0);

		setSelectionStart(selectionStart);
		if (first instanceof ArrayNode) {
			ArrayNode array = (ArrayNode) first;
			if (array.isMatrix()) {
				editorState.selectUpToRootComponent();
			} else {
				selectListElement(array.getChild(0));
			}
		} else {
			setSelectionEnd(selectionStart);
		}
	}

	private boolean isCharPlaceholder(Node selectionStart) {
		return selectionStart instanceof CharPlaceholderNode
				|| (selectionStart instanceof FunctionNode
		&& isCharPlaceholder(
				((FunctionNode) selectionStart).getChild(0)));
	}

	private void setSelectionStart(Node node) {
		editorState.setSelectionStart(node);
	}

	private void setSelectionEnd(Node node) {
		editorState.setSelectionEnd(node);
	}

	private void selectListElement(SequenceNode sequence) {
		if (getCurrentField() != sequence) {
			selectAllCompositeElement(sequence);
		} else {
			SequenceNode content = sequenceWithoutBrackets(sequence);
			int left = firstSeparatorOnLeft(content);
			setSelectionStart(content.getChild(left));
			int right = firstSeparatorOnRight(content);
			setSelectionEnd(content.getChild(right));

			if (isCharPlaceholder(editorState.getSelectionStart()) || right < left) {
				editorState.setSelectionStart(null);
				editorState.setSelectionEnd(null);
			}
		}
	}

	private Node getCurrentField() {
		return editorState.getCurrentNode();
	}

	private SequenceNode sequenceWithoutBrackets(SequenceNode sequence) {
		return sequence.size() == 1 && sequence.getChild(0) instanceof ArrayNode
				? ((ArrayNode) sequence.getChild(0)).getChild(0)
				: sequence;
	}

	private void selectAllCompositeElement(SequenceNode sequence) {
		Node field = getCurrentField();
		InternalNode parent = field.getParent();
		while (parent != sequence && parent != null) {
			field = parent;
			parent = parent.getParent();

		}
		setSelectionStart(field);
		setSelectionEnd(field);
	}

	private int firstSeparatorOnRight(SequenceNode sequence) {
		int offset = editorState.getCurrentOffset();
		if (isSeparatorAt(sequence, offset)) {
			return offset - 1;
		}

		int i = offset;
		while (i < sequence.getArgumentCount() && !isSeparatorAt(sequence, i)) {
			i++;
		}

		return i - 1;
	}

	private boolean isSeparatorAt(SequenceNode sequence, int index) {
		Node argument = sequence.getChild(index);
		return argument instanceof CharacterNode && ((CharacterNode) argument).isSeparator();
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
