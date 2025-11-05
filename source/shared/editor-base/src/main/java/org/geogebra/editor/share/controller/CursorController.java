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

import java.util.ArrayList;

import org.geogebra.editor.share.catalog.Tag;
import org.geogebra.editor.share.tree.ArrayNode;
import org.geogebra.editor.share.tree.CharPlaceholderNode;
import org.geogebra.editor.share.tree.CharacterNode;
import org.geogebra.editor.share.tree.FunctionNode;
import org.geogebra.editor.share.tree.InternalNode;
import org.geogebra.editor.share.tree.Node;
import org.geogebra.editor.share.tree.PlaceholderNode;
import org.geogebra.editor.share.tree.SequenceNode;

/**
 * Cursor movement in the expression tree.
 */
public class CursorController {

	/**
	 * Next character &rarr; key.
	 * @param editorState current state
	 * @return whether we moved right
	 */
	public static boolean nextCharacter(EditorState editorState) {
		return nextCharacter(editorState, true);
	}

	/**
	 * Next character &rarr; key.
	 * @param editorState current state
	 * @return whether we moved right
	 */
	public static boolean nextCharacter(EditorState editorState, boolean skipPlaceholders) {
		int currentOffset = editorState.getCurrentOffset();
		SequenceNode currentField = editorState.getCurrentNode();
		if (isLastPlaceholderInProtectedParent(editorState)) {
			if (currentOffset == currentField.size() - 1) {
				return nextField(editorState);
			}
			return false;
		}

		if (currentOffset < currentField.size()) {
			Node node = currentField.getChild(currentOffset);
			return nextCharacterInCurrentField(node, editorState, skipPlaceholders);
		} else {
			return nextField(editorState);
		}
	}

	private static boolean isLastPlaceholderInProtectedParent(EditorState editorState) {
		int currentOffset = editorState.getCurrentOffset();
		SequenceNode currentField = editorState.getCurrentNode();
		return InputController.isFieldSeparatorInSequence(currentField)
				&& currentOffset == currentField.size() - 1
				&& currentField.getChild(currentOffset) instanceof CharPlaceholderNode;
	}

	private static boolean nextCharacterInCurrentField(
			Node node, EditorState editorState, boolean skipPlaceholders) {

		InternalNode internalNode = asInternalNode(node);
		if (internalNode != null && internalNode.hasChildren()) {
			firstField(editorState, internalNode);
		} else {
			editorState.incCurrentOffset();
			if (skipPlaceholders && node instanceof PlaceholderNode) {
				nextCharacter(editorState);
			}
		}
		return true;
	}

	private static InternalNode asInternalNode(Node node) {
		if (node instanceof InternalNode) {
			return (InternalNode) node;
		}
		return null;
	}

	/**
	 * Previous character &larr; key.
	 * @param editorState current state
	 * @return whether the cursor moved
	 */
	public static boolean prevCharacter(EditorState editorState) {
		int currentOffset = editorState.getCurrentOffset();
		SequenceNode currentField = editorState.getCurrentNode();
		if (currentOffset > 0) {
			Node node = currentField.getChild(currentOffset - 1);
			prevCharacterInCurrentField(node, editorState);
			return true;
		} else {
			return prevField(editorState);
		}
	}

	private static void prevCharacterInCurrentField(
			Node node, EditorState editorState) {

		InternalNode internalNode = asInternalNode(node);
		if (internalNode != null && internalNode.hasChildren()) {
			lastField(editorState, internalNode);
		} else {
			editorState.decCurrentOffset();
			if (isPrevCharPlaceholder(editorState) && !editorState.isInFraction()) {
				editorState.decCurrentOffset();
			}
		}
	}

	private static boolean isPrevCharPlaceholder(EditorState editorState) {
		SequenceNode currentField = editorState.getCurrentNode();
		int offset = editorState.getCurrentOffset();
		return offset > 0
				&& currentField.getChild(offset - 1) instanceof CharPlaceholderNode;
	}

	/**
	 * Move to the beginning of the whole expression
	 * @param editorState current state
	 */
	public static void firstField(EditorState editorState) {
		SequenceNode root = editorState.getRootNode();
		firstField(editorState, root.extractLocked());
	}

	/**
	 * Move to the beginning of a subexpression
	 * @param editorState current state
	 * @param node0 subexpression
	 */
	public static void firstField(EditorState editorState,
			InternalNode node0) {
		InternalNode node = node0;
		// surface to first symbol
		while (!(node instanceof SequenceNode)) {
			int current = node.first();
			node = (InternalNode) node.getChild(current);
		}
		editorState.setCurrentNode((SequenceNode) node);
		editorState.setCurrentOffset(0);
	}

	/**
	 * Move to the end of the whole expression
	 * @param editorState current state
	 */
	public static void lastField(EditorState editorState) {
		SequenceNode root = editorState.getRootNode();
		lastField(editorState, root.extractLocked());
	}

	/**
	 * @param editorState current state
	 * @param node0 subexpression
	 */
	public static void lastField(EditorState editorState,
			InternalNode node0) {
		InternalNode node = node0;
		// surface to last symbol
		while (!(node instanceof SequenceNode)) {
			int current = node.last();
			node = (InternalNode) node.getChild(current);
		}
		editorState.setCurrentNode((SequenceNode) node);
		if (isLastFieldPlaceholder(editorState.getCurrentNode())) {
			skipLastField(editorState);
		} else {
			editorState.setCurrentOffset(node.size());
		}
	}

	private static void skipLastField(EditorState editorState) {
		SequenceNode currentField = editorState.getCurrentNode();
		editorState.setCurrentOffset(currentField.size() - 1);
	}

	private static boolean isLastFieldPlaceholder(SequenceNode sequence) {
		return sequence
				.getChild(sequence.size() - 1) instanceof CharPlaceholderNode;
	}

	/**
	 * Move cursor to the right.
	 * @param editorState current state
	 * @return whether current node has next field
	 */
	public static boolean nextField(EditorState editorState) {
		return nextField(editorState, editorState.getCurrentNode());
	}

	/**
	 * Move cursor to the right of a node.
	 * @param editorState current state
	 * @param node node where we want the cursor
	 * @return whether node has next field
	 */
	public static boolean nextField(EditorState editorState,
			InternalNode node) {
		// retrieve parent
		InternalNode parent = node.getParent();
		int current = node.getParentIndex();
		if (parent == null) {
			// this node has no parent
			// previous node doesn't exist
			// no-op
			return false;
		} else if (parent instanceof SequenceNode) {
			editorState.setCurrentNode((SequenceNode) parent);
			editorState.setCurrentOffset(node.getParentIndex() + 1);
			return parent.size() > node.getParentIndex();
			// try to find next sibling
		} else if (parent.hasNext(current)) {
			current = parent.getNext(current);
			InternalNode node1 = (InternalNode) parent
					.getChild(current);
			firstField(editorState, node1);
			return true;
			// try to delve down the tree
		} else if (ArrayNode.isLocked(parent)) {
			return false;
		} else {
			return nextField(editorState, parent);
		}
	}

	/**
	 * Find previous field.
	 * @param editorState current state
	 * @return whether the cursor moved
	 */
	public static boolean prevField(EditorState editorState) {
		return prevField(editorState, editorState.getCurrentNode());
	}

	/* Search for previous node */
	private static boolean prevField(EditorState editorState, InternalNode node) {
		// retrieve parent
		InternalNode parentNode = node.getParent();
		int current = node.getParentIndex();

		if (parentNode == null) {
			// this node has no parent
			// previous node doesn't exist
			// no-op
			return false;
		}
		if (parentNode instanceof SequenceNode) {
			editorState.setCurrentNode((SequenceNode) parentNode);
			editorState.setCurrentOffset(node.getParentIndex());
			return true;
			// try to find previous sibling
		} else if (parentNode.hasPrevious(current)) {
			current = parentNode.getPrevious(current);
			InternalNode node1 = (InternalNode) parentNode
					.getChild(current);
			lastField(editorState, node1);
			return true;
			// delve down the tree
		} else if (!ArrayNode.isLocked(parentNode)) {
			return prevField(editorState, parentNode);
		}
		return false;
	}

	/**
	 * Up field.
	 * @param editorState current state
	 * @return whether move up is possible
	 */
	public static boolean upField(EditorState editorState) {
		return upField(editorState, editorState.getCurrentNode());
	}

	/**
	 * Down field.
	 * @param editorState current state
	 * @return whether move down is possible
	 */
	public static boolean downField(EditorState editorState) {
		return downField(editorState, editorState.getCurrentNode());
	}

	/** Up field. */
	private static boolean upField(EditorState editorState, InternalNode node) {
		if (node.getParent() instanceof FunctionNode) {
			Tag name = ((FunctionNode) node.getParent()).getName();
			if (name.equals(Tag.SUBSCRIPT)) {
				return moveOutOfSuperSubScript(editorState);
			}
		}
		if (node instanceof SequenceNode) {
			if (node.getParent() instanceof FunctionNode) {
				FunctionNode function = (FunctionNode) node.getParent();
				int upIndex = function.getUpIndex(node.getParentIndex());
				if (upIndex >= 0) {
					editorState.setCurrentNode(function.getChild(upIndex));
					editorState.setCurrentOffset(0);
					return true;
				}
			}
		}
		if (checkMoveArray(node, editorState, -1)) {
			return true;
		}
		if (node.getParent() != null) {
			return upField(editorState, node.getParent());
		}
		return false;
	}

	/** Down field. */
	private static boolean downField(EditorState editorState,
			InternalNode node) {
		if (node.getParent() instanceof FunctionNode) {
			Tag name = ((FunctionNode) node.getParent()).getName();
			if (name.equals(Tag.SUPERSCRIPT)) {
				return moveOutOfSuperSubScript(editorState);
			}
		}
		if (node instanceof SequenceNode) {
			if (node.getParent() instanceof FunctionNode) {
				FunctionNode function = (FunctionNode) node.getParent();
				int downIndex = function
						.getDownIndex(node.getParentIndex());
				SequenceNode downArg = function.getChild(downIndex);
				if (downArg != null) {
					editorState.setCurrentNode(downArg);
					editorState.setCurrentOffset(0);
					return true;
				}
			}

			// matrix goes here
		}
		if (checkMoveArray(node, editorState, +1)) {
			return true;
		}
		if (node.getParent() != null) {
			return downField(editorState, node.getParent());
		}
		return false;
	}

	private static boolean checkMoveArray(Node node,
			EditorState editorState, int rowChange) {
		if (node.getParent() instanceof ArrayNode) {
			ArrayNode function = (ArrayNode) node.getParent();

			if (function.getRows() > 1) {
				int downIndex = node.getParentIndex()
						+ function.getColumns() * rowChange;
				if (downIndex >= 0 && downIndex < function.size()) {
					editorState
							.setCurrentNode(function.getChild(downIndex));
					editorState.resetSelection();
					editorState.setCurrentOffset(0);
					return true;
				}
			}
		}
		return false;
	}

	private static boolean moveOutOfSuperSubScript(EditorState editorState) {
		Node cursorFieldLeft = editorState.getCurrentNode().getChild(
				editorState.getCurrentOffset() - 1);
		Node cursorFieldRight = editorState.getCurrentNode().getChild(
				editorState.getCurrentOffset());
		InternalNode parentFunction = cursorFieldLeft != null ? cursorFieldLeft.getParent()
				: cursorFieldRight != null ? cursorFieldRight.getParent() : null;
		if (parentFunction != null && parentFunction.getChild(0).equals(cursorFieldRight)
				&& cursorFieldLeft == null) {
			prevCharacter(editorState);
			return true;
		}
		if (parentFunction != null && parentFunction.getChild(parentFunction.size() - 1)
				.equals(cursorFieldLeft) && cursorFieldRight == null) {
			return nextCharacter(editorState);
		}
		if (parentFunction == null) {
			return nextCharacter(editorState);
		}
		return false;
	}

	/**
	 * set position in editor state from tree path
	 * @param list tree path
	 * @param ct starting container
	 * @param editorState editor state
	 */
	public static void setPath(ArrayList<Integer> list, InternalNode ct,
			EditorState editorState) {
		InternalNode current = ct;
		int i = list.size() - 1;
		while (i >= 0) {
			int index = list.get(i);
			if (index < current.size()) {
				Node child = current.getChild(index);
				if (child instanceof CharacterNode) {
					editorState.setCurrentNode((SequenceNode) current);
					editorState.setCurrentOffset(index);
					return;
				} else if (child instanceof SequenceNode) {
					current = (SequenceNode) child;
					i--;
				} else {
					i--;
					if (i >= 0) {
						current = (SequenceNode) ((InternalNode) child)
								.getChild(list.get(i));
						i--;
					} else if (current instanceof SequenceNode) {
						editorState.setCurrentNode((SequenceNode) current);
						editorState.setCurrentOffset(index);
					}
				}
			} else if (index == current.size()) {
				editorState.setCurrentNode((SequenceNode) current);
				editorState.setCurrentOffset(index);
				return;
			} else {
				return;
			}
		}

	}

	/**
	 * set position in editor state from tree path, starting at root node
	 * @param list tree path
	 * @param editorState editor state
	 */
	public static void setPath(ArrayList<Integer> list, EditorState editorState) {
		editorState.setCurrentOffset(0);
		setPath(list, editorState.getRootNode(), editorState);
	}

	/**
	 * @param editorState editor state
	 * @return indices of subtrees that contain the cursor (in reversed order)
	 */
	public static ArrayList<Integer> getPath(EditorState editorState) {

		ArrayList<Integer> path = new ArrayList<>();

		path.add(editorState.getCurrentOffset());
		InternalNode field = editorState.getCurrentNode();
		InternalNode parent = field.getParent();
		while (parent != null) {
			path.add(field.getParentIndex());
			field = parent;
			parent = field.getParent();
		}

		return path;
	}
}