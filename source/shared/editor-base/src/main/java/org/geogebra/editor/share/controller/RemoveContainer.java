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

import java.util.ArrayList;

import org.geogebra.editor.share.catalog.Tag;
import org.geogebra.editor.share.tree.ArrayNode;
import org.geogebra.editor.share.tree.CharacterNode;
import org.geogebra.editor.share.tree.FunctionNode;
import org.geogebra.editor.share.tree.InternalNode;
import org.geogebra.editor.share.tree.Node;
import org.geogebra.editor.share.tree.PlaceholderNode;
import org.geogebra.editor.share.tree.SequenceNode;
import org.jspecify.annotations.NonNull;

public class RemoveContainer {

	/**
	 * Backspace to remove container
	 * @param editorState the state
	 */
	public static void withBackspace(EditorState editorState) {
		SequenceNode currentNode = editorState.getCurrentNode();
		InternalNode parent = currentNode.getParent();
		if (ArrayNode.isLocked(parent)) {
			return;
		}

		// if parent is function (cursor is at the beginning of the field)
		if (parent instanceof FunctionNode) {
			removeFunction(currentNode, editorState);
		} else if (isParentEmptyArray(currentNode)) {
			deleteContainer(editorState, parent, ((ArrayNode) parent).getChild(0));
		} else if (is1DArrayWithCursorInIt(currentNode.getParent(), currentNode.getParentIndex())) {
			deleteFromRowSequence(editorState);
		}
	}

	private static void removeFunction(SequenceNode currentNode, EditorState editorState) {
		FunctionNode function = (FunctionNode) currentNode.getParent();

		// fraction has operator like behavior
		if (Tag.FRAC == function.getName()) {

			// if second operand is empty sequence
			if (currentNode.getParentIndex() == 1 && currentNode.size() == 0) {
				int size = function.getChild(0).size();
				deleteContainer(editorState, function, function.getChild(0));
				// move after included characters
				editorState.addCurrentOffset(size);
				// if first operand is empty sequence
			} else if (currentNode.getParentIndex() == 1 && function.getChild(0).size() == 0) {
				deleteContainer(editorState, function, currentNode);
			}

		} else if (isGeneral(function.getName())) {
			if (currentNode.getParentIndex() == function.getInsertIndex()) {
				deleteContainer(editorState, function, currentNode);
			}
			// not a fraction, and cursor is right after the sign
		} else {
			if (currentNode.getParentIndex() == 1) {
				removeParenthesesOfFunction(function, editorState);
			} else if (currentNode.getParentIndex() > 1) {
				SequenceNode prev = function.getChild(currentNode.getParentIndex() - 1);
				int len = prev.size();
				for (Node child : currentNode) {
					prev.addChild(child);
				}
				function.removeChild(currentNode.getParentIndex());
				editorState.setCurrentNode(prev);
				editorState.setCurrentOffset(len);
			} else if (CursorController.prevCharacter(editorState)) {
				new InputController(editorState.getCatalog()).bkspCharacter(editorState);
				fuseMathFunction(editorState, function);
			}
		}
	}

	/**
	 * Fuses math function with previous characters
	 * @param editorState editor state
	 * @param functionNode function
	 */
	public static void fuseMathFunction(EditorState editorState, FunctionNode functionNode) {
		if (functionNode.getName() != Tag.APPLY && functionNode.getName() != Tag.APPLY_SQUARE
				|| !functionNode.hasChildren()) {
			return;
		}
		ArrayList<CharacterNode> nodes = new ArrayList<>();
		SequenceNode currentNode = editorState.getCurrentNode();
		int currentOffset = editorState.getCurrentOffset() - 1;
		while (currentOffset >= 0 && currentNode.getChild(currentOffset) instanceof CharacterNode) {
			CharacterNode character = (CharacterNode) currentNode.getChild(currentOffset);
			if (!character.isLetter()) {
				break;
			}
			currentNode.removeChild(currentOffset);
			currentOffset -= 1;
			nodes.add(character);
		}
		if (nodes.isEmpty()) {
			return;
		}
		SequenceNode name = functionNode.getChild(0);
		for (CharacterNode character : nodes) {
			name.addChild(0, character);
		}
		editorState.setCurrentNode(name);
		editorState.setCurrentOffset(nodes.size());
	}

	private static boolean isGeneral(Tag name) {
		return name != Tag.APPLY && name != Tag.APPLY_SQUARE;
	}

	private static boolean isParentEmptyArray(SequenceNode currentField) {
		return currentField.getParent() instanceof ArrayNode
				&& currentField.getParent().size() == 1;
	}

	// if parent is 1DArray or Vector and cursor is at the beginning of
	// intermediate the field
	private static boolean is1DArrayWithCursorInIt(InternalNode container, int parentIndex) {
		if (!(container instanceof ArrayNode)) {
			return false;
		}
		ArrayNode array = (ArrayNode) container;
		return (array.is1DArray() || array.isVector()) && parentIndex > 0 && !ArrayNode.isLocked(array);
	}

	private static void deleteFromRowSequence(EditorState editorState) {
		SequenceNode currentField = editorState.getCurrentNode();
		int index = currentField.getParentIndex();
		ArrayNode parent = (ArrayNode) currentField.getParent();
		SequenceNode field = parent.getChild(index - 1);
		int size = field.size();
		editorState.setCurrentOffset(0);
		while (currentField.size() > 0) {

			Node firstNode = currentField.getChild(0);
			currentField.deleteChild(0);
			field.addChild(field.size(), firstNode);
		}
		parent.deleteChild(index);
		editorState.setCurrentNode(field);
		editorState.setCurrentOffset(size);
	}

	private static void removeParenthesesOfFunction(FunctionNode function, EditorState editorState) {
		if (editorState.getRootNode().isProtected()) {
			// Protected inputs discard exposed commas. Keep multi-argument functions wrapped,
			// whether arguments occupy separate fields or share a comma-separated field.
			if (function.size() > 2) {
				return;
			}
			for (Node child : function.getChild(1)) {
				if (child instanceof CharacterNode character && character.isUnicode(',')) {
					return;
				}
			}
		}
		SequenceNode functionName = function.getChild(0);
		int nameSize = functionName.size();
		int offset = function.getParentIndex() + nameSize;
		// Unwrap entered content, then discard syntax placeholders before removing the command.
		for (int i = 1; i < function.size(); i++) {
			if (i > 1) {
				functionName.addChild(new CharacterNode(editorState.getCatalog().getCharacter(",")));
			}
			SequenceNode argument = function.getChild(i);
			while (argument.size() > 0) {
				Node child = argument.getChild(0);
				argument.removeChild(0);
				functionName.addChild(child);
			}
		}
		removeUnwrappedPlaceholders(functionName, nameSize);
		deleteContainer(editorState, function, functionName);
		editorState.setCurrentOffset(offset);
	}

	/**
	 * Remove unused syntax placeholders and their adjacent argument separators.
	 * Placeholders only have meaning inside a command; entered arguments remain intact.
	 * @param sequence function name followed by its unwrapped arguments
	 * @param argumentStart first index after the function name
	 */
	private static void removeUnwrappedPlaceholders(
			@NonNull SequenceNode sequence, int argumentStart) {
		for (int i = sequence.size() - 1; i >= argumentStart; i--) {
			if (!(sequence.getChild(i) instanceof PlaceholderNode)) {
				continue;
			}
			sequence.removeChild(i);
			if (sequence.getChild(i) instanceof CharacterNode next && next.isUnicode(',')) {
				sequence.removeChild(i);
			} else if (i > argumentStart
					&& sequence.getChild(i - 1) instanceof CharacterNode previous
					&& previous.isUnicode(',')) {
				sequence.removeChild(i - 1);
				i--;
			}
		}
	}

	/**
	 * Deletes the current field
	 * @param editorState editor state
	 */
	public static void deleteContainer(EditorState editorState) {
		SequenceNode currentField = editorState.getCurrentNode();
		if (ArrayNode.isLocked(currentField.getParent())) {
			return;
		}

		// if parent is function (cursor is at the end of the field)
		if (currentField.getParent() instanceof FunctionNode parent) {

			// fraction has operator like behavior
			if (Tag.FRAC == parent.getName()) {

				// first operand is current, second operand is empty sequence
				if (currentField.getParentIndex() == 0 && parent.getChild(1).size() == 0) {
					int size = parent.getChild(0).size();
					deleteContainer(editorState, parent, currentField);
					// move after included characters
					editorState.addCurrentOffset(size);

					// first operand is current, and first operand is empty
					// sequence
				} else if (currentField.getParentIndex() == 0 && currentField.size() == 0) {
					deleteContainer(editorState, parent, parent.getChild(1));
				}
			} else if (InputController.hasClosingBoundary(parent.getName())) {
				if ((parent.hasTag(Tag.APPLY) || parent.hasTag(Tag.APPLY_SQUARE))
						&& currentField.getParentIndex() == 0) {
					removeParenthesesOfFunction(parent, editorState);
				} else if (currentField.getParentIndex() == parent.size() - 1) {
					CursorController.nextCharacter(editorState);
				}
			}

			// Delete at a closing bracket only moves out, preserving the bracket pair.
		} else if (currentField.getParent() instanceof ArrayNode parent
				&& currentField.getParentIndex() == parent.size() - 1) {
			CursorController.nextCharacter(editorState);

			// if parent is 1DArray or Vector and cursor is at the end of the
			// field
		} else {
			if (currentField.getParent() instanceof ArrayNode parent
					&& (parent.is1DArray() || parent.isVector())
					&& currentField.getParentIndex() + 1 < currentField.getParent().size()) {

				int index = currentField.getParentIndex();
				SequenceNode field = parent.getChild(index + 1);
				int size = currentField.size();
				while (currentField.size() > 0) {

					Node node = currentField.getChild(0);
					currentField.deleteChild(0);
					field.addChild(field.size(), node);
				}
				parent.deleteChild(index);
				editorState.setCurrentNode(field);
				editorState.setCurrentOffset(size);
			}
		}
	}

	private static void deleteContainer(
			EditorState editorState, InternalNode container, SequenceNode operand) {
		if (container.getParent() instanceof SequenceNode parent) {
			// when parent is sequence
			int offset = container.getParentIndex();
			// delete container
			parent.deleteChild(offset);
			// add content of operand
			while (operand.size() > 0) {
				int lastArgumentIndex = operand.size() - 1;
				Node element = operand.getChild(lastArgumentIndex);
				operand.deleteChild(lastArgumentIndex);
				if (!editorState.getRootNode().isProtected() || !",".equals(element.toString())) {
					parent.addChild(offset, element);
				}
			}
			editorState.setCurrentNode(parent);
			editorState.setCurrentOffset(offset);
		}
	}
}
