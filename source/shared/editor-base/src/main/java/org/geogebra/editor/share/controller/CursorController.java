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

import static org.geogebra.editor.share.util.IntegralHelper.INTEGRAND;
import static org.geogebra.editor.share.util.IntegralHelper.LOWER_LIMIT;
import static org.geogebra.editor.share.util.IntegralHelper.UPPER_LIMIT;
import static org.geogebra.editor.share.util.IntegralHelper.VARIABLE;

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
import org.geogebra.editor.share.util.IntegralHelper;

/**
 * Cursor movement in the expression tree.
 */
public class CursorController {
	/** Defines which fields are visited while traversing the expression tree. */
	public enum Traversal {
		SELECTABLE_FIELDS, NAVIGABLE_FIELDS,
	}

	/**
	 * Next character &rarr; key.
	 * @param editorState current state
	 * @return whether we moved right
	 */
	public static boolean nextCharacter(EditorState editorState) {
		return nextCharacter(editorState, true, Traversal.NAVIGABLE_FIELDS);
	}

	/**
	 * Move to the next character or field.
	 * @param editorState current state
	 * @param skipPlaceholders whether to skip placeholders
	 * @param traversal fields to visit while moving through the expression tree
	 * @return whether the cursor moved
	 */
	public static boolean nextCharacter(EditorState editorState, boolean skipPlaceholders,
			Traversal traversal) {
		int currentOffset = editorState.getCurrentOffset();
		SequenceNode currentField = editorState.getCurrentNode();
		if (isLastPlaceholderInProtectedParent(editorState)) {
			if (currentOffset == currentField.size() - 1) {
				return nextField(editorState, editorState.getCurrentNode(), traversal);
			}
			return false;
		}

		if (currentOffset < currentField.size()) {
			Node node = currentField.getChild(currentOffset);
			return nextCharacterInCurrentField(node, editorState, skipPlaceholders,
					traversal);
		} else {
			return nextField(editorState, editorState.getCurrentNode(), traversal);
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
			Node node, EditorState editorState, boolean skipPlaceholders,
			Traversal traversal) {

		InternalNode internalNode = asInternalNode(node);
		if (internalNode != null && internalNode.hasChildren()) {
			firstField(editorState, internalNode, traversal);
		} else {
			editorState.incCurrentOffset();
			if (skipPlaceholders && node instanceof PlaceholderNode) {
				nextCharacter(editorState, true, traversal);
			}
		}
		return true;
	}

	private static InternalNode asInternalNode(Node node) {
		if (node instanceof InternalNode internalNode) {
			return internalNode;
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
			return prevField(editorState, editorState.getCurrentNode());
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
	 * Move to the beginning of the whole expression.
	 * @param editorState current state
	 */
	public static void firstField(EditorState editorState) {
		SequenceNode root = editorState.getRootNode();
		firstField(editorState, root.extractLocked(), Traversal.NAVIGABLE_FIELDS);
	}

	/**
	 * Move to the beginning of a subexpression.
	 * @param editorState current state
	 * @param node0 subexpression
	 * @param traversal fields to visit while moving through the expression tree
	 */
	public static void firstField(EditorState editorState, InternalNode node0,
			Traversal traversal) {
		InternalNode node = node0;
		// surface to first symbol
		while (!(node instanceof SequenceNode)) {
			if (IntegralHelper.isIntegral(node)) {
				moveToFirstIntegralField(editorState, (FunctionNode) node, traversal);
				return;
			}
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
	public static void lastField(EditorState editorState, InternalNode node0) {
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
		return sequence.getChild(sequence.size() - 1) instanceof CharPlaceholderNode;
	}

	/**
	 * Move cursor to the right of a node.
	 * @param editorState current state
	 * @param node node where we want the cursor
	 * @param traversal fields to visit while moving through the expression tree
	 * @return whether node has next field
	 */
	public static boolean nextField(EditorState editorState,
			InternalNode node, Traversal traversal) {
		// retrieve parent
		InternalNode parent = node.getParent();
		int current = node.getParentIndex();
		if (parent == null) {
			// this node has no parent
			// previous node doesn't exist
			// no-op
			return false;
		} else if (parent instanceof SequenceNode sequenceNode) {
			editorState.setCurrentNode(sequenceNode);
			editorState.setCurrentOffset(node.getParentIndex() + 1);
			return parent.size() > node.getParentIndex();
			// try to find next sibling
		} else if (IntegralHelper.isIntegral(parent)) {
			return nextIntegralField(editorState, (FunctionNode) parent, current,
					traversal);
		} else if (parent.hasNext(current)) {
			current = parent.getNext(current);
			InternalNode node1 = (InternalNode) parent
					.getChild(current);
			firstField(editorState, node1, traversal);
			return true;
			// try to delve down the tree
		} else if (ArrayNode.isLocked(parent)) {
			return false;
		} else {
			return nextField(editorState, parent, traversal);
		}
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
		if (parentNode instanceof SequenceNode sequenceNode) {
			editorState.setCurrentNode(sequenceNode);
			editorState.setCurrentOffset(node.getParentIndex());
			return true;
			// try to find previous sibling
		} else if (IntegralHelper.isIntegral(parentNode)) {
			return previousIntegralField(editorState, (FunctionNode) parentNode, current);
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
		if (moveVerticallyInIntegral(editorState, node, true)) {
			return true;
		}
		if (isIntegralField(node)) {
			return false;
		}
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
		if (moveVerticallyInIntegral(editorState, node, false)) {
			return true;
		}
		if (isIntegralField(node)) {
			return false;
		}
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

	private static boolean isIntegralField(InternalNode node) {
		return node instanceof SequenceNode sequenceNode
				&& IntegralHelper.isIntegral(sequenceNode.getParent());
	}

	private static boolean moveToIntegralField(EditorState editorState, FunctionNode integral,
			int integralFieldIndex, boolean placeAtFieldEnd, boolean revealLimits) {
		if (revealLimits && IntegralHelper.shouldRevealLimits(integral, integralFieldIndex)) {
			IntegralHelper.revealLimits(integral);
		}
		SequenceNode field = integral.getChild(integralFieldIndex);
		editorState.setCurrentNode(field);
		editorState.setCurrentOffset(placeAtFieldEnd ? field.size() : 0);
		return true;
	}

	private static void moveToFirstIntegralField(EditorState editorState, FunctionNode integralNode,
			Traversal traversal) {
		int fieldIndex = IntegralHelper.hasLimits(integralNode.getName()) && (
				traversal == Traversal.NAVIGABLE_FIELDS
				|| IntegralHelper.shouldRenderLimits(integralNode, null))
				? UPPER_LIMIT : INTEGRAND;
		moveToIntegralField(editorState, integralNode, fieldIndex, false,
				traversal == Traversal.NAVIGABLE_FIELDS);
	}

	private static boolean nextIntegralField(EditorState editorState, FunctionNode function,
			int currentFieldIndex, Traversal traversal) {
		if (traversal == Traversal.SELECTABLE_FIELDS) {
			return nextIntegralFieldForSelection(editorState, function, currentFieldIndex);
		}
		int nextFieldIndex = IntegralHelper.hasLimits(function.getName())
				&& IntegralHelper.isLimit(currentFieldIndex) ? INTEGRAND : currentFieldIndex + 1;
		if (nextFieldIndex < function.size()) {
			return moveToIntegralField(editorState, function, nextFieldIndex, false, true);
		}
		return nextField(editorState, function, traversal);
	}

	private static boolean nextIntegralFieldForSelection(EditorState editorState,
			FunctionNode function, int currentFieldIndex) {
		if (IntegralHelper.shouldRenderLimits(function, null)) {
			if (currentFieldIndex == UPPER_LIMIT) {
				return moveToIntegralField(editorState, function, LOWER_LIMIT, false, false);
			}
			if (currentFieldIndex == LOWER_LIMIT) {
				return moveToIntegralField(editorState, function, INTEGRAND, false, false);
			}
		}
		int nextFieldIndex = currentFieldIndex + 1;
		if (nextFieldIndex < function.size()) {
			return moveToIntegralField(editorState, function, nextFieldIndex, false, false);
		}
		return nextField(editorState, function, Traversal.SELECTABLE_FIELDS);
	}

	private static boolean previousIntegralField(EditorState editorState, FunctionNode function,
			int currentFieldIndex) {
		if (currentFieldIndex == VARIABLE) {
			return moveToIntegralField(editorState, function, INTEGRAND, true, true);
		}
		if (currentFieldIndex == INTEGRAND && IntegralHelper.hasLimits(function.getName())) {
			return moveToIntegralField(editorState, function, UPPER_LIMIT, true, true);
		}
		return prevField(editorState, function);
	}

	private static boolean moveVerticallyInIntegral(EditorState editorState, InternalNode node,
			boolean up) {
		if (node instanceof SequenceNode sequenceNode
				&& sequenceNode.getParent() instanceof FunctionNode function
				&& IntegralHelper.hasLimits(function.getName())) {
			int currentFieldIndex = sequenceNode.getParentIndex();
			if (currentFieldIndex == INTEGRAND && editorState.getCurrentOffset() == 0) {
				return moveToIntegralField(editorState, function,
						up ? UPPER_LIMIT : LOWER_LIMIT, true, true);
			}
			if (currentFieldIndex == UPPER_LIMIT && !up) {
				return moveToIntegralField(editorState, function, LOWER_LIMIT, true, true);
			}
			if (currentFieldIndex == LOWER_LIMIT && up) {
				return moveToIntegralField(editorState, function, UPPER_LIMIT, true, true);
			}
		}
		// If the cursor is just before an integral, navigate to its limits.
		if (node instanceof SequenceNode sequenceNode && sequenceNode.getChild(
				editorState.getCurrentOffset()) instanceof FunctionNode function
				&& IntegralHelper.hasLimits(function.getName())) {
			return moveToIntegralField(editorState, function, up ? UPPER_LIMIT : LOWER_LIMIT,
					false, true);
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
			return nextCharacter(editorState, true, Traversal.NAVIGABLE_FIELDS);
		}
		if (parentFunction == null) {
			return nextCharacter(editorState, true, Traversal.NAVIGABLE_FIELDS);
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
				} else if (child instanceof SequenceNode node) {
					current = node;
					i--;
				} else {
					i--;
					if (i >= 0) {
						current = (SequenceNode) ((InternalNode) child)
								.getChild(list.get(i));
						i--;
					} else if (current instanceof SequenceNode node) {
						editorState.setCurrentNode(node);
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
