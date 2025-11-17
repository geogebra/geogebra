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

import java.util.function.Predicate;

import org.geogebra.editor.share.catalog.CharacterTemplate;
import org.geogebra.editor.share.catalog.Tag;
import org.geogebra.editor.share.catalog.TemplateCatalog;
import org.geogebra.editor.share.editor.EditorFeatures;
import org.geogebra.editor.share.editor.MathFieldInternal;
import org.geogebra.editor.share.serializer.GeoGebraSerializer;
import org.geogebra.editor.share.serializer.ScreenReaderSerializer;
import org.geogebra.editor.share.tree.ArrayNode;
import org.geogebra.editor.share.tree.CharacterNode;
import org.geogebra.editor.share.tree.FunctionNode;
import org.geogebra.editor.share.tree.InternalNode;
import org.geogebra.editor.share.tree.Node;
import org.geogebra.editor.share.tree.SequenceNode;

import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class EditorState {

	private final TemplateCatalog catalog;
	private final SelectAllHandler selectAll;
	private SequenceNode rootNode;

	/**
	 * The Node in which the cursor is currently placed
	 */
	private SequenceNode currentNode;
	/**
	 * The index of the cursor in the current Node
	 */
	private int currentOffset;

	private Node currentSelStart;
	private Node currentSelEnd;
	private Node selectionAnchor;

	/**
	 * @param catalog {@link TemplateCatalog}
	 */
	public EditorState(TemplateCatalog catalog) {
		this.catalog = catalog;
		selectAll = new SelectAllHandler(this);
	}

	public SequenceNode getRootNode() {
		return rootNode;
	}

	public void setRootNode(SequenceNode rootNode) {
		this.rootNode = rootNode;
	}

	public SequenceNode getCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(SequenceNode currentNode) {
		this.currentNode = currentNode;
	}

	public int getCurrentOffset() {
		return currentOffset;
	}

	public void setCurrentOffset(int currentOffset) {
		this.currentOffset = Math.max(currentOffset, 0);
	}

	/**
	 * Increase current offset.
	 */
	public void incCurrentOffset() {
		currentOffset++;
	}

	/**
	 * Increase current offset by an amount.
	 * @param size number to add to current offset
	 */
	public void addCurrentOffset(int size) {
		currentOffset += size;
	}

	/**
	 * Decrease current offset.
	 */
	public void decCurrentOffset() {
		if (currentOffset > 0) {
			currentOffset--;
		}
	}

	/**
	 * @param node new argument
	 */
	public void addArgument(Node node) {
		if (currentNode.addChild(currentOffset, node)) {
			incCurrentOffset();
		}
	}

	/**
	 * Add character, consider unicode surrogates
	 * @param characterTemplate new argument
	 */
	public void addArgument(CharacterTemplate characterTemplate) {
		currentOffset = currentOffset + currentNode.addChild(currentOffset, characterTemplate);
	}

	public TemplateCatalog getCatalog() {
		return catalog;
	}

	public Node getSelectionStart() {
		return currentSelStart;
	}

	/**
	 * @return current offset or selection start
	 */
	public int getCurrentOffsetOrSelection() {
		if (currentSelStart != null && currentSelStart.getParent() == currentNode) {
			return currentSelStart.getParentIndex();
		}
		return currentOffset;
	}

	public Node getSelectionEnd() {
		return currentSelEnd;
	}

	public void setSelectionStart(Node selStart) {
		currentSelStart = selStart;
	}

	public void setSelectionEnd(Node selEnd) {
		currentSelEnd = selEnd;
	}

	/**
	 * Extends selection from current cursor position
	 * @param left true to go to the left from cursor
	 */
	public void extendSelection(boolean left) {
		Node cursorField = getCursorField(left);
		if (cursorField == null) {
			return;
		}

		extendSelection(cursorField);
		if (left && currentNode.size() == currentOffset) {
			currentOffset--;
		}
	}

	/**
	 * Extends selection to include a field
	 * @param cursorField newly selected field
	 */
	public void extendSelection(Node cursorField) {
		if (cursorField == null) {
			return;
		}

		if (selectionAnchor == null) {
			if (isGrandparentProtected(cursorField.getParent())
					&& ",".equals(cursorField.toString())) {
				return;
			}
			currentSelStart = cursorField;
			currentSelEnd = cursorField;
			anchor(true);
			return;
		}

		currentSelStart = selectionAnchor;
		// go from selection start to the root until we find common root
		InternalNode commonParent = currentSelStart.getParent();
		while (commonParent != null && !contains(commonParent, cursorField)) {
			currentSelStart = currentSelStart.getParent();
			commonParent = currentSelStart.getParent();
			if (commonParent.isRenderingOwnPlaceholders() && !isMatrix(commonParent)) {
				currentSelStart = commonParent;
				commonParent = currentSelStart.getParent();
			}
		}
		if (commonParent == null) {
			commonParent = rootNode;
		}

		currentSelEnd = cursorField;
		// special case: start is inside end -> select single node
		if (currentSelEnd == commonParent
				|| commonParent instanceof FunctionNode
				&& ((FunctionNode) commonParent).getName() == Tag.FRAC) {
			currentSelStart = commonParent;
			currentSelEnd = commonParent;
			return;
		}

		// go from selection end to the root
		while (currentSelEnd != null
				&& commonParent.indexOf(currentSelEnd) < 0) {
			currentSelEnd = currentSelEnd.getParent();
		}

		// swap start and end when necessary
		int to = commonParent.indexOf(currentSelEnd);
		int from = commonParent.indexOf(currentSelStart);
		if (from > to) {
			int swapIdx = from;
			from = to;
			to = swapIdx;
			Node swap = currentSelStart;
			currentSelStart = currentSelEnd;
			currentSelEnd = swap;
		}
		if (isGrandparentProtected(commonParent)) {
			terminateSelectionAtComma(commonParent, from, to);
		}

	}

	private boolean isMatrix(InternalNode container) {
		return container instanceof ArrayNode && container.hasTag(Tag.MATRIX);
	}

	private void terminateSelectionAtComma(InternalNode commonParent, int from, int to) {
		for (int j = from; j <= to; j++) {
			if (commonParent.isFieldSeparator(j)) {
				if (j == from) {
					currentSelEnd = currentSelStart = null;
				} else {
					currentSelEnd = commonParent.getChild(j - 1);
				}
				if (commonParent == currentNode) {
					currentOffset = j;
				}
			}
		}
	}

	private boolean isGrandparentProtected(InternalNode commonParent) {
		return commonParent != null
				&& commonParent.getParent() != null
				&& commonParent.getParent().getParent() != null
				&& commonParent.getParent().getParent().isProtected();
	}

	/**
	 * Select the whole formula
	 */
	public void selectAll() {
		selectAll.execute();
	}

	/**
	 * Select from cursor position to end of current sub-formula
	 */
	public void selectToStart() {
		if (currentNode == rootNode && currentOffset == 0) {
			return;
		}
		if (selectionAnchor == null) {
			extendSelection(getCursorField(false));
		}
		extendSelection(getCurrentNode().getChild(0));
	}

	/**
	 * Select from cursor position to start of current sub-formula
	 */
	public void selectToEnd() {
		if (currentNode == rootNode && currentOffset == rootNode.size()) {
			return;
		}
		if (selectionAnchor == null) {
			extendSelection(getCursorField(true));
		}
		extendSelection(getCurrentNode().getChild(getCurrentNode().size() - 1));
	}

	/**
	 * @param left whether to search left
	 * @return field directly left or right to the caret
	 */
	public Node getCursorField(boolean left) {
		return getCurrentNode().getChild(
				Math.max(0, Math.min(getCurrentOffset() + (left ? 0 : -1),
						getCurrentNode().size() - 1)));
	}

	private static boolean contains(InternalNode commonParent,
			Node cursorField0) {
		Node cursorField = cursorField0;
		while (cursorField != null) {
			if (cursorField == commonParent) {
				return true;
			}
			cursorField = cursorField.getParent();
		}
		return false;
	}

	/**
	 * Reset selection start/end/anchor pointers (NOT the caret)
	 */
	public void resetSelection() {

		selectionAnchor = null;

		currentSelEnd = null;
		currentSelStart = null;

	}

	/**
	 * @return true part of expression is selected
	 */
	public boolean hasSelection() {
		return currentSelStart != null;
	}

	/**
	 * Update selection anchor (starting point of selection by drag)
	 * @param start whether to anchor the start or the end of selection
	 */
	public void anchor(boolean start) {
		this.selectionAnchor = start ? this.currentSelStart
				: this.currentSelEnd;
	}

	/**
	 * Move cursor to the start of the selection.
	 */
	public void cursorToSelectionStart() {
		if (this.currentSelStart != null) {
			currentNode = getClosestSequenceAncestor(currentSelStart);
			currentOffset = currentSelEnd == currentNode ? 0 : currentSelStart.getParentIndex();
		}
	}

	/**
	 * Move cursor to the end of the selection.
	 */
	public void cursorToSelectionEnd() {
		if (currentSelEnd != null) {
			currentNode = getClosestSequenceAncestor(currentSelEnd);
			currentOffset = currentSelEnd == currentNode
					? rootNode.size() : currentSelEnd.getParentIndex() + 1;
		}
	}

	public Node getSelectionAnchor() {
		return selectionAnchor;
	}

	/**
	 * @return whether cursor is between quotes
	 */
	public boolean isInsideQuotes() {
		InternalNode fieldParent = currentNode;
		while (fieldParent != null) {
			if (fieldParent instanceof ArrayNode
					&& ((ArrayNode) fieldParent).getOpenDelimiter().getCharacter() == '"') {
				return true;
			}
			fieldParent = fieldParent.getParent();
		}
		return false;
	}

	/**
	 * @param er expression reader
	 * @param editorFeatures editor features
	 * @return description of cursor position
	 */
	public String getDescription(ExpressionReader er, EditorFeatures editorFeatures) {
		Node prev = currentNode.getChild(currentOffset - 1);
		Node next = currentNode.getChild(currentOffset);
		StringBuilder sb = new StringBuilder();
		if (currentNode.getParent() == null) {
			if (prev == null) {
				return er
						.localize("start of formula %0",
								ScreenReaderSerializer.fullDescription(
										currentNode, er.getAdapter()))
						.trim();
			}
			if (next == null) {
				return er
						.localize("end of formula %0",
								ScreenReaderSerializer.fullDescription(
										currentNode, er.getAdapter()))
						.trim();
			}
		}
		if (next == null && prev == null) {
			return describeParent(ExpRelation.EMPTY, currentNode.getParent(),
					er);
		}
		if (next == null) {
			sb.append(
					describeParent(ExpRelation.END_OF, currentNode.getParent(),
							er));
			sb.append(" ");
		}
		if (prev != null) {
			sb.append(describePrev(prev, er, editorFeatures));
		} else {
			sb.append(describeParent(ExpRelation.START_OF,
					currentNode.getParent(),
					er));
		}
		sb.append(" ");

		if (next != null) {
			sb.append(describeNext(next, er));
		} else if (endOfFunctionName()) {
			sb.append(
					er.localize(ExpRelation.BEFORE.toString(),
							er.getAdapter().parenthesis("(")));
		}
		return sb.toString().trim();
	}

	/**
	 * @return number of comma symbols before cursor
	 */
	public int countCommasBeforeCurrent() {
		int commas = 0;
		for (int i = 0; i < currentOffset; i++) {
			if (currentNode.isFieldSeparator(i)) {
				commas++;
			}
		}
		return commas;
	}

	/**
	 * @return number of comma symbols after cursor
	 */
	public int countCommasAfterCurrent() {
		int commas = 0;
		for (int i = currentOffset; i < currentNode.size(); i++) {
			if (currentNode.isFieldSeparator(i)) {
				commas++;
			}
		}
		return commas;
	}

	private boolean endOfFunctionName() {
		return currentNode.getParent() instanceof FunctionNode
				&& currentNode.getParent().hasTag(Tag.APPLY)
				&& currentNode.getParentIndex() == 0;
	}

	private String describePrev(Node parent, ExpressionReader er,
			EditorFeatures editorFeatures) {
		if (parent instanceof FunctionNode
				&& Tag.SUPERSCRIPT == ((FunctionNode) parent).getName()) {
			return er.localize(ExpRelation.AFTER.toString(), er.power(
					GeoGebraSerializer.serialize(currentNode
							.getChild(currentNode.indexOf(parent) - 1), editorFeatures),
					GeoGebraSerializer
							.serialize(
									((FunctionNode) parent).getChild(0), editorFeatures)));
		}
		if (parent instanceof CharacterNode) {
			StringBuilder sb = new StringBuilder();
			int i = currentNode.indexOf(parent);
			while (MathFieldInternal.appendChar(sb, currentNode, i, CharacterNode::isCharacter)) {
				i--;
			}
			if (sb.length() > 0 && !isInsideQuotes()) {
				try {
					return er.localize(ExpRelation.AFTER.toString(),
							mathExpression(sb.reverse().toString(), er));
				} catch (Exception e) {
					FactoryProvider.getInstance()
							.debug("Invalid: " + sb.reverse());
				}
			}
		}
		return describe(ExpRelation.AFTER, parent, er);
	}

	private String describeNext(Node parent, ExpressionReader er) {
		if (parent instanceof CharacterNode) {
			StringBuilder sb = new StringBuilder();
			int i = currentNode.indexOf(parent);
			while (MathFieldInternal.appendChar(sb, currentNode, i, CharacterNode::isCharacter)) {
				i++;
			}
			if (sb.length() > 0 && !isInsideQuotes()) {
				try {
					return er.localize(ExpRelation.BEFORE.toString(),
							mathExpression(sb.toString(), er));
				} catch (Exception e) {
					// no math alt text, fall back to reading as is
				}
			}
		}
		return describe(ExpRelation.BEFORE, parent, er);
	}

	private String mathExpression(String math, ExpressionReader er) {
		StringBuilder sb = new StringBuilder(math.length());
		for (int i = 0; i < math.length(); i++) {
			sb.append(er.getAdapter().convertCharacter(math.charAt(i)));
		}
		return sb.toString();
	}

	private static String describe(ExpRelation pattern, Node prev,
			ExpressionReader er) {
		String name = describe(pattern, prev, -1, er);
		if (name != null) {
			return er.localize(pattern.toString(), name);
		}
		return er.localize(pattern.toString(),
				ScreenReaderSerializer.fullDescription(prev, er.getAdapter()));
	}

	private static String describe(ExpRelation pattern, Node prev,
			int index, ExpressionReader er) {
		if (prev instanceof FunctionNode) {
			switch (((FunctionNode) prev).getName()) {
			case FRAC:
				return new String[]{"fraction", "numerator",
						"denominator"}[index + 1];
			case NROOT:
				return new String[]{"root", "index", "radicand"}[index + 1];
			case SQRT:
				return "square root";
			case CBRT:
				return "cube root";
			case SUPERSCRIPT:
				return "superscript";
			case ABS:
				return "absolute value";
			case APPLY:
				if ((index == 1 && pattern == ExpRelation.START_OF)
						|| (index == ((FunctionNode) prev).size() - 1
						&& pattern == ExpRelation.END_OF)) {
					return "parentheses";
				}
				return index >= 0 ? "" : "function";
			default:
				return "function";
			}
		}
		if (prev instanceof ArrayNode) {
			if (((ArrayNode) prev).getOpenDelimiter().getCharacter() == '"') {
				return "quotes";
			}
			switch (pattern) {
			case AFTER:
				return er.getAdapter().parenthesis(")");
			case BEFORE:
				return er.getAdapter().parenthesis("(");
			default:
				return "parentheses";
			}
		}
		return null;
	}

	private String describeParent(ExpRelation pattern, InternalNode parent,
			ExpressionReader er) {
		if (parent instanceof FunctionNode) {
			String name = describe(pattern, parent,
					parent.indexOf(currentNode), er);
			if (name != null && name.isEmpty()) {
				return "";
			}
			return er.localize(pattern.toString(), name);
		} else if (parent instanceof ArrayNode && ((ArrayNode) parent).isMatrix()) {
			int childIndex = parent.indexOf(currentNode);
			int row = childIndex / ((ArrayNode) parent).getColumns() + 1;
			int column = childIndex % ((ArrayNode) parent).getColumns() + 1;
			return er.localize(pattern.toString(), "row " + row + " column " + column);
		}

		return describe(pattern, parent, er);
	}

	/**
	 * @return whether current field is inside a fraction or not.
	 */
	public boolean isInFraction() {
		InternalNode parent = currentNode.getParent();
		return parent != null && parent.hasTag(Tag.FRAC);
	}

	/**
	 * @return Whether fractions inside this function are disallowed
	 */
	public boolean isPreventingNestedFractions() {
		InternalNode parent = currentNode.getParent();
		return parent instanceof FunctionNode
				&& ((FunctionNode) parent).isPreventingNestedFractions();
	}

	/**
	 * @return Whether the current field is inside a recurring decimal or not
	 */
	public boolean isInRecurringDecimal() {
		InternalNode parent = currentNode.getParent();
		return parent != null && parent.hasTag(Tag.RECURRING_DECIMAL);
	}

	/**
	 * @return whether current field is inside a sub/superscript or not.
	 */
	public boolean isInScript() {
		return hasParent(parent -> parent.hasTag(Tag.SUBSCRIPT)
				|| parent.hasTag(Tag.SUPERSCRIPT));
	}

	/**
	 * @return whether current field is inside an input
	 */
	public boolean isInHighlightedPlaceholder() {
		return hasParent(InternalNode::isRenderingOwnPlaceholders);
	}

	private boolean hasParent(Predicate<InternalNode> check) {
		InternalNode parent = currentNode.getParent();
		while (parent != null) {
			if (check.test(parent)) {
				return true;
			}
			parent = parent.getParent();
		}
		return false;
	}

	/**
	 * Select the topmost ancestor that's not root or root's child.
	 */
	public void selectUpToRootComponent() {
		while (currentSelStart != null && currentSelStart.getParent() != null
				&& currentSelStart.getParent().getParent() != getRootNode()) {
			anchor(true);
			currentSelStart = currentSelStart.getParent();
		}

		setSelectionEnd(currentSelStart);
	}

	/**
	 * @param left whether to collapse to the left
	 * @return whether selection was collapsed
	 */
	public boolean updateCursorFromSelection(boolean left) {
		if (left && currentSelStart != null) {
			cursorToSelectionStart();
			return true;
		} else if (currentSelEnd != null) {
			cursorToSelectionEnd();
			return true;
		}
		return false;
	}

	private SequenceNode getClosestSequenceAncestor(Node comp) {
		Node current = comp;
		while (current.getParent() != null && !(current instanceof SequenceNode)) {
			current = current.getParent();
		}
		return current instanceof SequenceNode ? (SequenceNode) current : rootNode;
	}

	public Node getComponentLeftOfCursor() {
		return currentOffset > 0 ? currentNode.getChild(currentOffset - 1) : null;
	}
}
