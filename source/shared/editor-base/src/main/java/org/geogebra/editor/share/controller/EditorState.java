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

import java.util.Arrays;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
	private int selectionAnchor = -1;
	private SequenceNode selectionParent;

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

	/**
	 * Extends selection from current cursor position
	 * @param left true to go to the left from cursor
	 * @return whether selection changed
	 */
	public boolean extendSelection(boolean left) {
		boolean moved;
		if (selectionParent == null) {
			skipInvisiblePositions(left);
			selectionParent = currentNode;
			selectionAnchor = currentOffset;
		}
		boolean changed;
		do {
			if (left) {
				moved = CursorController.prevCharacter(this);
			} else {
				moved = CursorController.nextCharacter(this);
			}
			changed = extendSelection();
		} while (moved && !changed);
		if (left && currentNode.size() == currentOffset) {
			currentOffset--;
		}
		return moved;
	}

	private void skipInvisiblePositions(boolean left) {
		if (left && currentNode.getChild(currentOffset - 1) instanceof FunctionNode funNode) {
			currentNode = funNode.getChild(funNode.size() - 1);
			currentOffset = currentNode.size();
		}
		if (!left && currentNode.getChild(currentOffset) instanceof FunctionNode funNode) {
			currentNode = funNode.getChild(0);
			currentOffset = 0;
		}
	}

	/**
	 * Extends selection to include a field
	 * @param cursorField newly selected field
	 */
	public void extendSelection(Node cursorField) {
		currentNode = cursorField.getParentSequence();
		currentOffset = cursorField.getParentIndex();
		extendSelection();
	}

	/**
	 * Extends selection to include a field
	 * @return whether selection changed
	 */
	public boolean extendSelection() {
		int startDepth = selectionParent.getDepth();
		int endDepth = currentNode.getDepth();
		int startIndex = -1;
		int endIndex = -1;
		InternalNode startNode = selectionParent;
		InternalNode endNode = currentNode;
		while (startDepth > endDepth) {
			startIndex = startNode.getParentIndex();
			startNode = startNode.getParent();
			startDepth--;
		}
		while (endDepth > startDepth) {
			endIndex = endNode.getParentIndex();
			endNode = endNode.getParent();
			endDepth--;
		}
		while (startNode != endNode) {
			if (startNode.getParent() != null && startNode.getParent().getParent() != null
					&& startNode.getParent().getParent().isProtected()) {
				break;
			}
			startIndex = startNode.getParentIndex();
			startNode = startNode.getParent();
			endIndex = endNode.getParentIndex();
			endNode = endNode.getParent();
		}
		final Node oldStart = currentSelStart;
		final Node oldEnd = currentSelEnd;
		if (startNode != endNode) {
			if (startNode instanceof SequenceNode sequenceNode) {
				if (startNode.getParentIndex() <= endNode.getParentIndex()) {
					selectSubsequence(sequenceNode, 0, sequenceNode.size());
				} else {
					selectSubsequence(sequenceNode, sequenceNode.size(), 0);
				}
			} else {
				SequenceNode parentSequence = startNode.getParentSequence();
				selectSubsequence(parentSequence, 0, parentSequence.size());
			}
			// if we're stuck in protected node, always consider the selection changed
			return true;
		}

		if (!(startNode instanceof SequenceNode)) {
			selectSubsequence(startNode.getParentSequence(),
					startNode.getParentIndex(), startNode.getParentIndex() + 1);
			return didSelectionChange(oldStart, oldEnd);
		}
		int startOffset;
		int endOffset;
		if (startIndex == -1 && endIndex == -1) {
			startOffset = selectionAnchor;
			endOffset = currentOffset;
		} else if (startIndex == -1) {
			startOffset = selectionAnchor;
			endOffset = endIndex >= selectionAnchor ? endIndex + 1 : endIndex;
		} else if (endIndex == -1) {
			endOffset = currentOffset;
			startOffset = startIndex >= currentOffset ? startIndex + 1 : startIndex;
		} else {
			startOffset = Math.min(startIndex, endIndex);
			endOffset = Math.max(startIndex, endIndex) + 1;
		}
		// swap start and end when necessary
		if (startOffset > endOffset) {
			int swap = startOffset;
			startOffset = endOffset;
			endOffset = swap;
		}
		if (startOffset == endOffset) {
			currentSelStart = currentSelEnd = null;
		} else {
			currentSelStart = startNode.getChild(startOffset);
			currentSelEnd = startNode.getChild(endOffset - 1);
		}
		if (isGrandparentProtected(startNode)) {
			terminateSelectionAtComma(startNode, startOffset, endOffset);
			// if we're stuck in protected node, always consider the selection changed
			return true;
		}
		return didSelectionChange(oldStart, oldEnd);
	}

	private boolean didSelectionChange(Node oldStart, Node oldEnd) {
		return oldStart != currentSelStart || oldEnd != currentSelEnd;
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
		if (selectionParent == null) {
			selectionParent = currentNode;
			selectionAnchor = currentOffset;
		}
		currentNode = rootNode;
		currentOffset = 0;
		extendSelection();
	}

	/**
	 * Select from cursor position to start of current sub-formula
	 */
	public void selectToEnd() {
		if (currentNode == rootNode && currentOffset == rootNode.size()) {
			return;
		}
		if (selectionParent == null) {
			selectionParent = currentNode;
			selectionAnchor = currentOffset;
		}
		currentNode = rootNode;
		currentOffset = rootNode.size();
		extendSelection();
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

	/**
	 * @param left whether to move to the left
	 * @return cursor position adjusted by 0 or -1
	 */
	public int getCursorOffset(boolean left) {
		return Math.max(0, Math.min(getCurrentOffset() + (left ? 0 : -1),
						getCurrentNode().size()));
	}

	/**
	 * Reset selection start/end/anchor pointers (NOT the caret)
	 */
	public void resetSelection() {
		selectionAnchor = -1;
		selectionParent = null;
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
	 */
	public void anchor() {
		if (currentSelStart != null) {
			this.selectionAnchor = this.currentSelStart.getParentIndex();
			selectionParent = currentNode;
		}
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

	/**
	 * @return whether cursor is between quotes
	 */
	public boolean isInsideQuotes() {
		InternalNode fieldParent = currentNode;
		while (fieldParent != null) {
			if (fieldParent instanceof ArrayNode node
					&& node.getOpenDelimiter().getCharacter() == '"') {
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
						.localize(ExpRelation.START_FORMULA,
								ScreenReaderSerializer.fullDescription(
										currentNode, er.getAdapter()))
						.trim();
			}
			if (next == null) {
				return er
						.localize(ExpRelation.END_FORMULA,
								ScreenReaderSerializer.fullDescription(
										currentNode, er.getAdapter()))
						.trim();
			}
		}
		if (next == null && prev == null) {
			sb.append(" ");
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
					er.localize(ExpRelation.BEFORE,
							er.getAdapter().getCharacterName('(')));
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

	private String describePrev(Node node, ExpressionReader er,
			EditorFeatures editorFeatures) {
		if (node instanceof FunctionNode functionNode
				&& Tag.SUPERSCRIPT == functionNode.getName()) {
			return er.localize(ExpRelation.AFTER, er.power(
					GeoGebraSerializer.serialize(currentNode
							.getChild(currentNode.indexOf(node) - 1), editorFeatures),
					GeoGebraSerializer
							.serialize(
									functionNode.getChild(0), editorFeatures)));
		}
		if (node instanceof CharacterNode) {
			StringBuilder sb = new StringBuilder();
			int i = currentNode.indexOf(node);
			while (MathFieldInternal.appendChar(sb, currentNode, i, CharacterNode::isCharacter)) {
				i--;
			}
			if (!sb.isEmpty() && !isInsideQuotes()) {
				try {
					return er.localize(ExpRelation.AFTER,
							convertCharacters(sb.reverse().toString(), er));
				} catch (Exception e) {
					FactoryProvider.getInstance()
							.debug("Invalid: " + sb.reverse());
				}
			}
		}
		return describe(ExpRelation.AFTER, node, er);
	}

	private String describeNext(Node node, ExpressionReader er) {
		if (node instanceof CharacterNode) {
			StringBuilder sb = new StringBuilder();
			int i = currentNode.indexOf(node);
			while (MathFieldInternal.appendChar(sb, currentNode, i, CharacterNode::isCharacter)) {
				i++;
			}
			if (!sb.isEmpty() && !isInsideQuotes()) {
				try {
					return er.localize(ExpRelation.BEFORE,
							convertCharacters(sb.toString(), er));
				} catch (Exception e) {
					// no math alt text, fall back to reading as is
				}
			}
		}
		return describe(ExpRelation.BEFORE, node, er);
	}

	private String convertCharacters(String characters, ExpressionReader er) {
		if (characters.length() == 1) {
			return er.getAdapter().getCharacterName(characters.charAt(0));
		}
		StringBuilder sb = new StringBuilder(characters.length());
		for (int i = 0; i < characters.length(); i++) {
			sb.append(er.getAdapter().convertCharacter(characters.charAt(i)));
		}
		return sb.toString();
	}

	private static String describe(ExpRelation pattern, Node prev,
			ExpressionReader er) {
		String name = describe(pattern, prev, -1, er);
		if (name != null) {
			return er.localize(pattern, name);
		}
		return er.localize(pattern,
				ScreenReaderSerializer.fullDescription(prev, er.getAdapter()));
	}

	private static String describe(ExpRelation pattern, Node prev,
			int index, ExpressionReader er) {
		String key = getBaseKey(pattern, prev, index);
		return key == null ? null : er.localize(getPrefix(pattern) + key,
				camelCaseToWords(key)).toLowerCase(Locale.ROOT);
	}

	private static String camelCaseToWords(String key) {
		if ("Abs".equals(key)) {
			return "absolute value";
		}
		return Arrays.stream(key.split("(?=[A-Z])"))
				.map(s -> s.toLowerCase(Locale.ROOT))
				.collect(Collectors.joining(" "));
	}

	private static String getPrefix(ExpRelation rel) {
		return switch (rel) {
			case START_OF, END_OF -> "of.";
			default -> "altText.";
		};
	}

	private static String getBaseKey(ExpRelation pattern, Node prev, int index) {
		if (prev instanceof FunctionNode node) {
			return switch (node.getName()) {
				case FRAC -> new String[]{"Fraction", "Numerator",
						"Denominator"}[index + 1];
				case NROOT -> new String[]{"Root", "Index", "Radicand"}[index + 1];
				case SQRT -> "SquareRoot";
				case CBRT -> "CubeRoot";
				case SUPERSCRIPT -> "Superscript";
				case POINT, POINT_AT -> index == -1 ? parenthesesFor(pattern) : "Coordinate";
				case ABS -> "Abs";
				case APPLY -> {
					if ((index == 1 && pattern == ExpRelation.START_OF)
							|| (index == node.size() - 1 && pattern == ExpRelation.END_OF)
							|| (index == 1 && pattern == ExpRelation.EMPTY)) {
						yield "Parentheses";
					}
					yield index >= 0 ? null : "Function";
				}
				default -> "Function";
			};
		}
		if (prev instanceof ArrayNode node) {
			if (node.getOpenDelimiter().getCharacter() == '"') {
				return "Quotes";
			}
			return parenthesesFor(pattern);
		}
		return null;
	}

	private static String parenthesesFor(ExpRelation relation) {
		return switch (relation) {
			case BEFORE -> "OpenParenthesis";
			case AFTER -> "CloseParenthesis";
			default -> "Parentheses";
		};
	}

	private String describeParent(ExpRelation pattern, InternalNode parent,
			ExpressionReader er) {
		if (parent instanceof FunctionNode) {
			String name = describe(pattern, parent,
					parent.indexOf(currentNode), er);
			if (name == null || name.isEmpty()) {
				return "";
			}
			return er.localize(pattern, name);
		} else if (parent instanceof ArrayNode node && node.isMatrix()) {
			int childIndex = parent.indexOf(currentNode);
			int row = childIndex / node.getColumns() + 1;
			int column = childIndex % node.getColumns() + 1;
			return er.localize(pattern, "row " + row + " column " + column);
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
		return parent instanceof FunctionNode fn
				&& fn.isPreventingNestedFractions();
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
		while (currentNode.getParent() != null && currentNode.getParent().getParent() != null
				&& !currentNode.getParent().getParent().isProtected()) {
			currentNode = currentNode.getParentSequence();
		}
		selectSubsequence(currentNode, 0, currentNode.size());
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
		return current instanceof SequenceNode sn ? sn : rootNode;
	}

	public Node getComponentLeftOfCursor() {
		return currentOffset > 0 ? currentNode.getChild(currentOffset - 1) : null;
	}

	/**
	 * Select subsequence of a given sequence node.
	 * @param sequenceNode internal node
	 * @param from selection start offset (from 0 to {@code sequenceNode.size()})
	 * @param to selection end offset (from 0 to {@code sequenceNode.size()})
	 */
	public void selectSubsequence(SequenceNode sequenceNode, int from, int to) {
		selectionParent = sequenceNode;
		selectionAnchor = from;
		currentOffset = to;
		currentNode = sequenceNode;
		extendSelection();
	}
}
