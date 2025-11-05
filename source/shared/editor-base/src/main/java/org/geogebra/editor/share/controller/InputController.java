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
import java.util.Arrays;
import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.editor.share.catalog.ArrayTemplate;
import org.geogebra.editor.share.catalog.CharacterTemplate;
import org.geogebra.editor.share.catalog.FunctionTemplate;
import org.geogebra.editor.share.catalog.Tag;
import org.geogebra.editor.share.catalog.TemplateCatalog;
import org.geogebra.editor.share.editor.EditorFeatures;
import org.geogebra.editor.share.editor.MathField;
import org.geogebra.editor.share.editor.SyntaxAdapter;
import org.geogebra.editor.share.tree.ArrayNode;
import org.geogebra.editor.share.tree.CharPlaceholderNode;
import org.geogebra.editor.share.tree.CharacterNode;
import org.geogebra.editor.share.tree.FunctionNode;
import org.geogebra.editor.share.tree.InternalNode;
import org.geogebra.editor.share.tree.Node;
import org.geogebra.editor.share.tree.PlaceholderNode;
import org.geogebra.editor.share.tree.SequenceNode;
import org.geogebra.editor.share.util.JavaKeyCodes;
import org.geogebra.editor.share.util.Unicode;

import com.google.j2objc.annotations.Weak;

public class InputController {

	public static final char FUNCTION_OPEN_KEY = '('; // probably universal
	public static final char FUNCTION_CLOSE_KEY = ')';
	public static final char DELIMITER_KEY = ';';
	private static final String[] SUFFIX_REPLACEABLE_FUNCTIONS = {"abs", "sqrt"};
	private static final List<Character> ignoreChars = Arrays.asList('{', '}');
	private final TemplateCatalog catalog;

	@Weak
	private MathField mathField;

	private boolean plainTextMode = false;
	private @CheckForNull SyntaxAdapter syntaxAdapter;
	private @CheckForNull EditorFeatures editorFeatures;
	private boolean useSimpleScripts = true;
	private boolean allowAbs = true;

	/**
	 * @param catalog model
	 */
	public InputController(TemplateCatalog catalog) {
		this.catalog = catalog;
	}

	public MathField getMathField() {
		return mathField;
	}

	public void setMathField(MathField mathField) {
		this.mathField = mathField;
	}

	public boolean getPlainTextMode() {
		return plainTextMode;
	}

	public void setPlainTextMode(boolean plainTextMode) {
		this.plainTextMode = plainTextMode;
	}

	public void setSyntaxAdapter(SyntaxAdapter syntaxAdapter) {
		this.syntaxAdapter = syntaxAdapter;
	}

	public void setUseSimpleScripts(boolean useSimpleScripts) {
		this.useSimpleScripts = useSimpleScripts;
	}

	public void setAllowAbs(boolean allowAbs) {
		this.allowAbs = allowAbs;
	}

	static private String getLetter(Node node)
			throws Exception {
		if (!(node instanceof CharacterNode)) {
			throw new Exception("Node is not a character");
		}

		CharacterNode character = (CharacterNode) node;
		if (!character.isCharacter() || !character.isLetter()) {
			throw new Exception("Node is not a character");
		}

		return character.getUnicodeString();
	}

	/**
	 * Insert array.
	 * @param editorState editor state
	 * @param size array size
	 * @param arrayOpenKey array type
	 * @param reverse whether to insert it left of the cursor
	 * @return array
	 */
	private ArrayNode newArray(EditorState editorState, int size,
			char arrayOpenKey, boolean reverse) {
		moveCursorOutOfFunctionName(editorState);
		SequenceNode currentField = editorState.getCurrentNode();
		int currentOffset = editorState.getCurrentOffset();
		ArrayTemplate arrayTemplate = catalog.getArray(arrayOpenKey);
		ArrayNode array = new ArrayNode(arrayTemplate, size);
		int cutPosition = reverse ? findBackwardCutPosition(currentField,
				currentOffset) : currentOffset;
		ArrayList<Node> removed = reverse
				? cut(currentField, cutPosition, currentOffset - 1, editorState, array,
				true)
				: cut(currentField, cutPosition, -1, editorState, array,
				true);

		// add sequence
		SequenceNode field = new SequenceNode();
		array.setChild(0, field);
		insertReverse(field, -1, removed);
		for (int i = 1; i < size; i++) {
			// add sequence
			array.setChild(i, new SequenceNode());
		}
		editorState.resetSelection();
		// set current
		if (reverse) {
			editorState.setCurrentNode(currentField);
			editorState.setCurrentOffset(cutPosition + 1);
		} else {
			editorState.setCurrentNode(field);
			editorState.setCurrentOffset(0);
		}
		return array;
	}

	private static int findBackwardCutPosition(SequenceNode sequence, int position) {
		for (int index = position; index > 0; index--) {
			Node node = sequence.getChild(index - 1);
			if (node instanceof CharacterNode) {
				CharacterNode character = (CharacterNode) node;
				if (character.isUnicode('=') || character.isFieldSeparator()) {
					return index;
				}
			}
		}

		return 0;
	}

	private static void moveCursorOutOfFunctionName(EditorState editorState) {
		SequenceNode currentField = editorState.getCurrentNode();
		if (currentField.getParent() != null
				&& currentField.getParent().hasTag(Tag.APPLY)
				&& currentField.getParentIndex() == 0
				&& editorState.getCurrentOffset() == 0) {
			InternalNode function = currentField.getParent();
			if (function.getParent() instanceof SequenceNode) {
				editorState
						.setCurrentNode((SequenceNode) function.getParent());
				editorState.setCurrentOffset(function.getParentIndex());
			}
		}
	}

	/**
	 * Insert matrix.
	 */
	public void newMatrix(EditorState editorState, int columns, int rows) {
		SequenceNode currentField = editorState.getCurrentNode();
		int currentOffset = editorState.getCurrentOffset();
		ArrayTemplate arrayTemplate = catalog.getMatrix();
		ArrayNode matrix = new ArrayNode(arrayTemplate, columns, rows);
		currentField.addChild(currentOffset, matrix);

		// add sequence
		SequenceNode field = new SequenceNode();
		matrix.setChild(0, field);

		for (int i = 1; i < matrix.size(); i++) {
			// add sequence
			matrix.setChild(i, new SequenceNode());
		}

		// set current
		editorState.setCurrentNode(field);
		editorState.setCurrentOffset(0);
	}

	/**
	 * Insert braces (), [], {}, "".
	 * @param editorState editor state
	 * @param ch opening bracket character
	 */
	public void newBraces(EditorState editorState, char ch) {
		if (editorState.hasSelection()) {
			editorState.cursorToSelectionStart();
		}

		FunctionPower power = getFunctionPower(editorState);

		newBraces(editorState, power, ch);
	}

	private static FunctionPower getFunctionPower(EditorState editorState) {
		FunctionPower power = new FunctionPower();
		int initialOffset = editorState.getCurrentOffsetOrSelection();
		Node last = editorState.getCurrentNode()
				.getChild(initialOffset - 1);
		power.script = FunctionNode.isScript(last) ? (FunctionNode) last
				: null;
		if (power.script != null) {
			initialOffset--;
		}
		power.name = ArgumentHelper.readCharacters(editorState,
				initialOffset);
		return power;
	}

	private void newBraces(EditorState editorState, FunctionPower power, char ch) {
		String name = power.name;
		for (String suffix : SUFFIX_REPLACEABLE_FUNCTIONS) {
			if (name.endsWith(suffix)) {
				name = suffix;
			}
		}
		Tag tag = Tag.lookup(name);

		if (tag == Tag.MATRIX) {
			if (power.script != null) {
				deleteSingleArg(editorState);
			}
			delCharacters(editorState, name.length());
			newMatrix(editorState, 1, 1);
			return;
		}

		if (ch == FUNCTION_OPEN_KEY && tag != null) {
			if (power.script != null) {
				deleteSingleArg(editorState);
			}
			delCharacters(editorState, name.length());
			newFunction(editorState, name, false,
					power.script);
		} else if ((ch == FUNCTION_OPEN_KEY || ch == '[')
				&& catalog.isFunction(name)) {
			if (power.script != null) {
				deleteSingleArg(editorState);
			}
			delCharacters(editorState, name.length());
			newFunction(editorState, name, ch == '[', power.script);

		} else {
			int index = editorState.getCurrentOffsetOrSelection();
			Node firstSelection = editorState.getCurrentNode().getChild(index);

			if (firstSelection instanceof PlaceholderNode) {
				editorState.getCurrentNode().removeChild(index);
				ArrayTemplate arrayTemplate = catalog.getArray(ch);
				ArrayNode array = new ArrayNode(arrayTemplate, 1);
				SequenceNode seq = new SequenceNode();
				array.setChild(0, seq);
				editorState.getCurrentNode().addChild(index, array);
				editorState.setSelectionStart(null);
				editorState.setCurrentNode(seq);
				editorState.setCurrentOffset(0);
				return;
			}

			// TODO brace type
			newArray(editorState, 1, ch, false);
		}
	}

	/**
	 * Insert function by name.
	 * @param name function
	 */
	public void newFunction(EditorState editorState, String name) {
		newFunction(editorState, name, false, null);
	}

	/**
	 * Insert function by name.
	 * @param name function
	 */
	public void newFunction(EditorState editorState, String name,
			boolean square, FunctionNode exponent) {
		SequenceNode currentField = editorState.getCurrentNode();
		int currentOffset = editorState.getCurrentOffset();
		// add extra braces for sqrt, nthroot and fraction
		if ("^".equals(name) && currentOffset > 0
				&& editorState.getSelectionEnd() == null) {
			if (currentField
					.getChild(currentOffset - 1) instanceof FunctionNode) {
				FunctionNode function = (FunctionNode) currentField
						.getChild(currentOffset - 1);
				if (Tag.SQRT.equals(function.getName())
						|| Tag.CBRT.equals(function.getName())
						|| Tag.NROOT.equals(function.getName())
						|| Tag.FRAC.equals(function.getName())) {

					currentField.deleteChild(currentOffset - 1);
					// add braces
					ArrayNode array = new ArrayNode(
							catalog.getArray(Tag.REGULAR), 1);
					currentField.addChild(currentOffset - 1, array);
					// add sequence
					SequenceNode field = new SequenceNode();
					array.setChild(0, field);
					field.addChild(0, function);
				}
			}
		}

		// add function
		FunctionNode function;
		Tag tag = Tag.lookup(name);
		final boolean hasSelection = editorState.getSelectionEnd() != null;
		int offset = 0;
		if (tag == Tag.LOG && exponent != null
				&& exponent.getName() == Tag.SUBSCRIPT) {
			function = buildLog(exponent);
			offset = 1;
		} else if (tag != null && exponent == null) {
			FunctionTemplate functionTemplate = catalog.getGeneral(tag);
			function = new FunctionNode(functionTemplate);
		} else {
			offset = 1;
			tag = null; // reset tag if exponent was found
			function = buildCustomFunction(name, square, exponent);
		}
		boolean builtin = tag != null;
		// add sequences
		initArguments(function, offset);

		// pass characters for fraction, factorial, and mixed number only
		if (tag == Tag.FRAC) {
			if (hasSelection) {
				ArrayList<Node> removed = cut(currentField,
						currentOffset, -1, editorState, function, true);
				SequenceNode field = new SequenceNode();
				function.setChild(0, field);
				insertReverse(field, -1, removed);
				editorState.resetSelection();
				editorState.setCurrentNode(function.getChild(1));
				editorState.setCurrentOffset(0);
				return;
			}
			ArgumentHelper.passArgument(editorState, function);
		} else if (tag == Tag.SUPERSCRIPT) {
			if (hasSelection) {
				ArrayNode array = this.newArray(editorState, 1, '(', false);
				editorState.setCurrentNode((SequenceNode) array.getParent());
				editorState.resetSelection();
				editorState.setCurrentOffset(array.getParentIndex() + 1);
				newFunction(editorState, name, square, null);
				return;
			}
		} else {
			if (hasSelection || !builtin) {
				ArrayList<Node> removed = cut(currentField,
						currentOffset, -1, editorState, function, true);
				SequenceNode field = new SequenceNode();
				function.setChild(offset, field);
				insertReverse(field, -1, removed);
				editorState.resetSelection();
				editorState.setCurrentNode(field);
				editorState.setCurrentOffset(hasSelection ? field.size()
						: function.getInitialIndex());
				// editorState.incCurrentOffset();
				return;
			}
		}
		currentOffset = editorState.getCurrentOffset();
		currentField.addChild(currentOffset, function);
		int select = function.getInitialIndex();
		if (function.hasChildren()) {
			// set current sequence
			CursorController.firstField(editorState,
					function.getChild(select));
			editorState.setCurrentOffset(editorState.getCurrentNode().size());
		} else {
			editorState.incCurrentOffset();
		}
	}

	private void initArguments(FunctionNode function, int offset) {
		for (int i = offset; i < function.size(); i++) {
			SequenceNode field = new SequenceNode();
			function.setChild(i, field);
		}
	}

	private FunctionNode buildLog(FunctionNode exponent) {
		FunctionTemplate functionTemplate = catalog.getGeneral(Tag.LOG);
		FunctionNode function = new FunctionNode(functionTemplate);
		function.setChild(0, exponent.getChild(0));
		return function;
	}

	private FunctionNode buildCustomFunction(String name, boolean square,
			Node exponent) {
		FunctionTemplate template = catalog.getFunction(name, square);
		SequenceNode nameS = new SequenceNode();
		for (int i = 0; i < name.length(); i++) {
			nameS.append(
					catalog.getCharacter(name.charAt(i) + ""));
		}
		if (exponent != null) {
			nameS.addChild(exponent);
		}
		FunctionNode function = new FunctionNode(template);
		function.setChild(0, nameS);
		return function;
	}

	/**
	 * @param editorState current state
	 * @param scriptTag SUBSCRIPT or SUPERSCRIPT
	 */
	public void newScript(EditorState editorState, Tag scriptTag) {
		SequenceNode currentField = editorState.getCurrentNode();
		if (currentField.size() == 0
				&& currentField.getParent() instanceof FunctionNode
				&& Tag.SUPERSCRIPT == ((FunctionNode) currentField.getParent())
				.getName()
				&& Tag.SUPERSCRIPT == scriptTag) {
			return;
		}
		int currentOffset = editorState.getCurrentOffset();

		int offset = currentOffset;
		while (offset > 0 && currentField
				.getChild(offset - 1) instanceof FunctionNode) {

			FunctionNode function = (FunctionNode) currentField
					.getChild(offset - 1);
			if (scriptTag == function.getName()) {
				editorState.setCurrentNode(function.getChild(0));
				editorState.setCurrentOffset(function.getChild(0).size());
				return;
			}
			if (Tag.SUPERSCRIPT != function.getName()
					&& Tag.SUBSCRIPT != function.getName()) {
				break;
			}
			offset--;
		}
		offset = currentOffset;
		while (offset < currentField.size()
				&& currentField.getChild(offset) instanceof FunctionNode) {

			FunctionNode function = (FunctionNode) currentField
					.getChild(offset);
			if (scriptTag == function.getName()) {
				editorState.setCurrentNode(function.getChild(0));
				editorState.setCurrentOffset(0);
				return;
			}
			if (Tag.SUPERSCRIPT != function.getName()
					&& Tag.SUBSCRIPT != function.getName()) {
				break;
			}
			offset++;
		}
		if (currentOffset > 0 && currentField
				.getChild(currentOffset - 1) instanceof FunctionNode) {
			FunctionNode function = (FunctionNode) currentField
					.getChild(currentOffset - 1);
			if (Tag.SUPERSCRIPT == function.getName() && Tag.SUBSCRIPT == scriptTag) {
				currentOffset--;
			}
		}
		if (currentOffset < currentField.size() && currentField
				.getChild(currentOffset) instanceof FunctionNode) {
			FunctionNode function = (FunctionNode) currentField
					.getChild(currentOffset);
			if (Tag.SUBSCRIPT == function.getName() && Tag.SUPERSCRIPT == scriptTag) {
				currentOffset++;
			}
		}
		editorState.setCurrentOffset(currentOffset);
		newFunction(editorState, scriptTag.getKey());
	}

	/**
	 * Insert operator.
	 */
	public void newOperator(EditorState editorState, char op) {
		CharacterTemplate template = catalog.getOperator("" + op);
		newCharacter(editorState, template);
	}

	/**
	 * Insert symbol.
	 * @param editorState state
	 * @param sy char
	 */
	public void newSymbol(EditorState editorState, char sy) {
		CharacterTemplate template = catalog.getSymbol("" + sy);
		newCharacter(editorState, template);
	}

	/**
	 * Insert character.
	 * @param editorState state
	 * @param ch char
	 */
	public void newCharacter(EditorState editorState, char ch) {
		CharacterTemplate template = catalog.getCharacter("" + ch);
		newCharacter(editorState, template);
	}

	/**
	 * Insert character.
	 * @param editorState current state
	 * @param template character template
	 */
	public void newCharacter(EditorState editorState, CharacterTemplate template) {
		int currentOffset = editorState.getCurrentOffset();
		Node last = editorState.getCurrentNode()
				.getChild(currentOffset - 1);
		StringBuilder suffix = new StringBuilder(template.getUnicodeString());

		while (last instanceof CharacterNode) {
			suffix.append(last);
			if (!catalog.isReverseSuffix(suffix.toString())) {
				suffix.setLength(suffix.length() - 1);
				break;
			}
			currentOffset--;
			last = editorState.getCurrentNode()
					.getChild(currentOffset - 1);
		}

		CharacterTemplate merge = catalog.merge(suffix.reverse().toString());
		if (merge != null) {
			for (int i = 0; i < suffix.length() - 1; i++) {
				editorState.getCurrentNode().deleteChild(currentOffset);
			}
			editorState.setCurrentOffset(currentOffset);
			editorState.addArgument(merge);
			return;
		}
		if (syntaxAdapter != null) {
			FunctionPower function = getFunctionPower(editorState);
			char unicode = template.getUnicode();
			if (unicode == ' ' && syntaxAdapter.isFunction(function.name)) {
				newBraces(editorState, function, '(');
				return;
			}

			if (catalog.isForceBracketAfterFunction()
					&& shouldAddBrackets(function, unicode)) {
				newBraces(editorState, function, '(');

				if (unicode == ' ') {
					return;
				}
			}
		}

		editorState.addArgument(template);
	}

	private boolean shouldAddBrackets(FunctionPower function, char unicode) {
		if (unicode == '^' || unicode == '_' || isAbsDelimiter(unicode)
				|| Unicode.isSuperscriptDigit(unicode) || unicode == Unicode.SUPERSCRIPT_MINUS) {
			return false;
		}

		return endsInFunction(function.name) && !endsInFunction(function.name + unicode);
	}

	private boolean endsInFunction(String name) {
		int start = name.length() - 1;

		while (start >= 0) {
			if (syntaxAdapter != null && syntaxAdapter.isFunction(name.substring(start))) {
				return true;
			}

			start--;
		}

		return false;
	}

	/**
	 * Insert field.
	 * @param editorState current state
	 * @param ch bracket
	 */
	private void endField(EditorState editorState, char ch) {
		SequenceNode currentField = editorState.getCurrentNode();
		int currentOffset = editorState.getCurrentOffset();
		// first array specific ...
		if (RemoveContainer.isParentAnArray(currentField)) {
			ArrayNode parent = (ArrayNode) currentField.getParent();

			// if ',' typed within 1DArray or Vector ... add new field
			if (ch == parent.getFieldDelimiter().getCharacter()
					&& (parent.is1DArray() || parent.isVector())) {

				int index = currentField.getParentIndex();
				SequenceNode field = new SequenceNode();
				parent.addChild(index + 1, field);
				while (currentField.size() > currentOffset) {
					Node node = currentField
							.getChild(currentOffset);
					currentField.deleteChild(currentOffset);
					field.addChild(field.size(), node);
				}
				currentField = field;
				currentOffset = 0;

				// if ',' typed at the end of intermediate field of 2DArray or
				// Matrix ... move to next field
			} else if (ch == parent.getFieldDelimiter().getCharacter()
					&& currentOffset == currentField.size()
					&& parent.size() > currentField.getParentIndex() + 1
					&& (currentField.getParentIndex() + 1)
					% parent.getColumns() != 0) {

				currentField = parent
						.getChild(currentField.getParentIndex() + 1);
				currentOffset = 0;

				// if ';' typed at the end of last field ... add new row
			} else if (ch == parent.getRowDelimiter().getCharacter()
					&& currentOffset == currentField.size()
					&& parent.size() == currentField.getParentIndex() + 1) {

				parent.addRow();
				currentField = parent
						.getChild(parent.size() - parent.getColumns());
				currentOffset = 0;

				// if ';' typed at the end of (not last) row ... move to next
				// field
			} else if (ch == parent.getRowDelimiter().getCharacter()
					&& currentOffset == currentField.size()
					&& (currentField.getParentIndex() + 1)
					% parent.getColumns() == 0) {

				currentField = parent
						.getChild(currentField.getParentIndex() + 1);
				currentOffset = 0;
			} else if (ch == parent.getCloseDelimiter().getCharacter() && !ArrayNode.isLocked(
					parent)) {
				// in non-protected containers when the closing key is pressed
				// move out of the container
				moveOutOfArray(currentField, currentOffset);
				currentOffset = parent.getParentIndex() + 1;
				currentField = (SequenceNode) parent.getParent();
			} else {
				// else just create a new array for the given closing key
				ArrayTemplate arrayTemplate = catalog.getArrayByCloseKey(ch);
				if (arrayTemplate != null) {
					newArray(editorState, 1, arrayTemplate.getOpenDelimiter().getCharacter(), true);
					return;
				}
			}

			// now functions, braces, apostrophes ...
		} else if (currentField.getParent() != null) {
			InternalNode parent = currentField.getParent();

			if (currentOffset == currentField.size()
					&& parent instanceof FunctionNode
					&& ch == ((FunctionNode) parent).getClosingBracket()
					&& parent.size() == currentField.getParentIndex() + 1) {

				currentOffset = parent.getParentIndex() + 1;
				currentField = (SequenceNode) parent.getParent();

				// if ')' typed at the end of last field of braces ... move
				// after closing character
			} else if (parent instanceof FunctionNode
					&& ch == ((FunctionNode) parent).getClosingBracket()
					&& parent.size() == currentField.getParentIndex() + 1) {
				ArrayList<Node> removed = cut(currentField,
						currentOffset);
				insertReverse(parent.getParent(), parent.getParentIndex(),
						removed);

				currentOffset = parent.getParentIndex() + 1;
				currentField = (SequenceNode) parent.getParent();

				// if '|' typed at the end of an abs function
				// special case
			} else if (parent instanceof FunctionNode
					&& parent.hasTag(Tag.ABS)
					&& ch == '|'
					&& parent.size() == currentField.getParentIndex() + 1) {
				currentField = (SequenceNode) parent.getParent();
				currentOffset = parent.getParentIndex() + 1;
				checkReplaceAbs(parent, currentField);
			} else {
				if (ch == ',') {
					newCharacter(editorState, ch);
					// return so that the old current field and offset are not
					// set
					return;
				}
				if (ch == FUNCTION_CLOSE_KEY) {
					newArray(editorState, 1, '(', true);
					return;
				}
			}

			// topmost container last ...
		} else {
			if (ch == FUNCTION_CLOSE_KEY) {
				newArray(editorState, 1, '(', true);
				return;
			}
			// if ';' typed and at the top level ... insert delimiter char
			if (ch == DELIMITER_KEY || ch == ',') {
				newCharacter(editorState, ch);
				// return so that the old current field and offset are not set
				return;
				// update();
			}
		}
		editorState.setCurrentNode(currentField);
		editorState.setCurrentOffset(currentOffset);
	}

	private void checkReplaceAbs(InternalNode abs, SequenceNode parent) {
		if (abs.getChild(0) instanceof SequenceNode
				&& ((SequenceNode) abs.getChild(0)).size() == 0) {
			int parentIndex = abs.getParentIndex();
			parent.removeChild(parentIndex);
			CharacterTemplate operator = catalog.getOperator(Unicode.OR + "");
			parent.addChild(parentIndex, new CharacterNode(operator));
		}
	}

	private void moveOutOfArray(SequenceNode currentField, int currentOffset) {
		Node parent = currentField.getParent();
		if (parent.getParent() instanceof SequenceNode) {
			int counter = 1;
			while (currentField.size() > currentOffset) {
				Node node = currentField
						.getChild(currentOffset);
				currentField.deleteChild(currentOffset);
				parent.getParent().addChild(parent.getParentIndex() + counter, node);
				counter++;
			}
		}
	}

	private static void insertReverse(InternalNode parent, int parentIndex,
			ArrayList<Node> removed) {
		for (int j = removed.size() - 1; j >= 0; j--) {
			Node o = removed.get(j);
			int idx = parentIndex + removed.size() - j;
			parent.addChild(idx, o);
		}

	}

	private static ArrayList<Node> cut(InternalNode currentField,
			int from, int to, EditorState st, Node array,
			boolean rec) {

		int end = to < 0 ? endToken(from, currentField) : to;
		int start = from;

		if (st.getCurrentNode() == currentField
				&& st.getSelectionEnd() != null) {
			// the root is selected
			if (st.getSelectionEnd().getParent() == null && rec) {
				return cut((SequenceNode) st.getSelectionEnd(), 0, -1, st,
						array, false);
			}
			// deep selection, e.g. a fraction
			if (st.getSelectionEnd().getParent() != currentField && rec) {
				return cut(st.getSelectionEnd().getParent(),
						st.getSelectionStart().getParentIndex(),
						st.getSelectionEnd().getParentIndex(), st, array,
						false);
			}
			// simple case: a part of sequence is selected
			end = currentField.indexOf(st.getSelectionEnd());
			start = currentField.indexOf(st.getSelectionStart());
			if (end < 0 || start < 0) {
				end = currentField.size() - 1;
				start = 0;

			}

		}
		return currentField.replaceChildren(start, end, array);
	}

	private static int endToken(int from, InternalNode currentField) {
		for (int i = from; i < currentField.size(); i++) {
			if (currentField.isFieldSeparator(i)) {
				return i - 1;
			}
		}
		return currentField.size() - 1;
	}

	private static ArrayList<Node> cut(SequenceNode currentField,
			int currentOffset) {
		ArrayList<Node> removed = new ArrayList<>();

		for (int i = currentField.size() - 1; i >= currentOffset; i--) {
			removed.add(currentField.getChild(i));
			currentField.removeChild(i);
		}

		return removed;
	}

	/**
	 * Remove character left to the cursor
	 * @param editorState current state
	 */
	public void bkspCharacter(EditorState editorState) {
		int currentOffset = editorState.getCurrentOffsetOrSelection();
		if (currentOffset > 0) {
			Node prev = editorState.getCurrentNode()
					.getChild(currentOffset - 1);
			if (prev instanceof ArrayNode) {
				ArrayNode parent = (ArrayNode) prev;
				extendBrackets(parent, editorState);
			}
			if (prev instanceof FunctionNode) {
				bkspLastFunctionArg((FunctionNode) prev, editorState);
			} else {
				deleteSingleArg(editorState);
			}
		} else {
			RemoveContainer.withBackspace(editorState);
		}
	}

	private void bkspLastFunctionArg(FunctionNode function, EditorState editorState) {
		SequenceNode functionArg = function.getChild(function.size() - 1);
		if (function.getName() == Tag.APPLY || function.getName() == Tag.APPLY_SQUARE) {
			moveArgumentsAfter(function, editorState, function.getChild(1));
			bkspCharacter(editorState);
		} else if (isEqFunctionWithPlaceholders(function)) {
			deleteSingleArg(editorState);
		} else if (functionArg != null) {
			editorState.setCurrentNode(functionArg);
			functionArg.deleteChild(functionArg.size() - 1);
			editorState.setCurrentOffset(functionArg.size());
		} else {
			deleteSingleArg(editorState);
		}
	}

	private boolean isEqFunctionWithPlaceholders(FunctionNode function) {
		return function.getName() == Tag.DEF_INT || function.getName() == Tag.SUM_EQ
				|| function.getName() == Tag.PROD_EQ || function.getName() == Tag.LIM_EQ
				|| function.getName() == Tag.ATOMIC_POST || function.getName() == Tag.ATOMIC_PRE
				|| function.getName() == Tag.RECURRING_DECIMAL;
	}

	private void deleteSingleArg(EditorState editorState) {
		int currentOffset = editorState.getCurrentOffsetOrSelection();
		SequenceNode currentField = editorState.getCurrentNode();
		if (currentField.isChildProtected(currentOffset - 1)) {
			return;
		}

		currentField.deleteChild(currentOffset - 1);
		editorState.decCurrentOffset();
		onDelete(editorState, currentField);
	}

	private void onDelete(EditorState editorState, SequenceNode currentField) {
		int currentOffset = editorState.getCurrentOffset();
		Node node = currentField.getChild(currentOffset);
		if (isFieldSeparatorInSequence(currentField) && isFieldSeparatorOrNull(node)) {
			addPlaceholderIfNeeded(currentField, currentOffset);
		}

		if (node instanceof FunctionNode) {
			RemoveContainer.fuseMathFunction(editorState, (FunctionNode) node);
		}
	}

	static boolean isFieldSeparatorInSequence(SequenceNode sequence) {
		for (Node node : sequence) {
			if (node.isFieldSeparator()) {
				return true;
			}
		}

		return false;
	}

	private boolean isFieldSeparatorOrNull(Node node) {
		return node == null || node.isFieldSeparator();
	}

	private void addPlaceholderIfNeeded(SequenceNode currentField, int offset) {
		if (isFieldSeparatorOrNull(currentField.getChild(offset - 1))) {
			currentField.addChild(offset, new CharPlaceholderNode());
		}
	}

	private static void extendBrackets(ArrayNode array, EditorState state) {
		moveArgumentsAfter(array, state, array.getChild(array.size() - 1));
	}

	private static void moveArgumentsAfter(Node lastToKeep,
			EditorState editorState, SequenceNode target) {
		int currentOffset = lastToKeep.getParentIndex() + 1;
		InternalNode currentField = lastToKeep.getParent();
		int oldSize = target.size();
		while (currentField.size() > currentOffset
				&& !currentField.isChildProtected(currentOffset)) {
			Node node = currentField.getChild(currentOffset);
			currentField.deleteChild(currentOffset);
			target.addChild(target.size(), node);
		}
		editorState.setCurrentNode(target);
		editorState.setCurrentOffset(oldSize);

	}

	/**
	 * Delete a character to the right of the cursor
	 * @param editorState current state
	 */
	public void delCharacter(EditorState editorState) {
		int currentOffset = editorState.getCurrentOffset();
		SequenceNode currentField = editorState.getCurrentNode();
		if (currentOffset < currentField.size()) {

			CursorController.nextCharacter(editorState);
			bkspCharacter(editorState);

		} else {
			if (RemoveContainer.isParentAnArray(currentField)) {
				extendBrackets((ArrayNode) currentField.getParent(),
						editorState);
			} else {
				RemoveContainer.deleteContainer(editorState);
			}
		}
	}

	private static void delCharacters(EditorState editorState, int length0) {
		int currentOffset = editorState.getCurrentOffsetOrSelection();
		SequenceNode currentField = editorState.getCurrentNode();
		int length = length0;
		while (length > 0 && currentOffset > 0 && currentField
				.getChild(currentOffset - 1) instanceof CharacterNode) {

			CharacterNode character = (CharacterNode) currentField
					.getChild(currentOffset - 1);
			if (character.isOperator() || (character.isSymbol()
					&& !character.isLetter())) {
				break;
			}
			currentField.deleteChild(currentOffset - 1);
			currentOffset--;
			length--;
		}
		editorState.setCurrentOffset(currentOffset);
	}

	/**
	 * remove characters before and after cursor
	 * @param editorState editor state
	 * @param lengthBeforeCursor number of characters before cursor to delete
	 * @param lengthAfterCursor number of characters after cursor to delete
	 */
	public void removeCharacters(EditorState editorState,
			int lengthBeforeCursor, int lengthAfterCursor) {
		if (lengthBeforeCursor == 0 && lengthAfterCursor == 0) {
			return; // nothing to delete
		}
		SequenceNode seq = editorState.getCurrentNode();
		for (int i = 0; i < lengthBeforeCursor; i++) {
			editorState.decCurrentOffset();
			if (editorState.getCurrentOffset() < 0
					|| editorState.getCurrentOffset() >= seq.size()) {
				RemoveContainer.withBackspace(editorState);
				return;
			}
			seq.deleteChild(editorState.getCurrentOffset());
		}
		for (int i = 0; i < lengthAfterCursor; i++) {
			seq.deleteChild(editorState.getCurrentOffset());
		}
	}

	/**
	 * set ret to characters (no digit) around cursor
	 * @param editorState current state
	 * @param ret builder for the word
	 * @return word length before cursor
	 */
	public static int getWordAroundCursor(EditorState editorState,
			StringBuilder ret) {
		int pos = editorState.getCurrentOffset();
		SequenceNode seq = editorState.getCurrentNode();

		StringBuilder before = new StringBuilder();
		int i;
		for (i = pos - 1; i >= 0; i--) {
			try {
				before.append(getLetter(seq.getChild(i)));
			} catch (Exception e) {
				break;
			}
		}
		int lengthBefore = pos - i - 1;

		StringBuilder after = new StringBuilder();
		for (i = pos; i < seq.size(); i++) {
			try {
				after.append(getLetter(seq.getChild(i)));
			} catch (Exception e) {
				break;
			}
		}
		before.reverse();
		ret.append(before);
		ret.append(after);

		return lengthBefore;

	}

	/**
	 * Delete selection.
	 * @param editorState current state
	 * @return success
	 */
	public static boolean deleteSelection(EditorState editorState) {
		boolean nonempty = false;
		if (editorState.getSelectionStart() != null) {
			InternalNode parent = editorState.getSelectionStart().getParent();

			if (isProtected(editorState.getSelectionStart())) {
				return true;
			}

			if (ArrayNode.isLocked(parent)) {
				deleteMatrixElementValue(editorState);
				return true;
			}

			int end, start;
			if (parent == null) {
				// all the formula is selected
				parent = editorState.getRootNode();
				start = 0;
				end = parent.size() - 1;
			} else {
				start = parent.indexOf(editorState.getSelectionStart());
				end = parent.indexOf(editorState.getSelectionEnd());
			}

			if (end >= 0 && start >= 0) {
				for (int i = end; i >= start; i--) {
					parent.deleteChild(i);
					nonempty = true;
				}

				editorState.setCurrentOffset(start);
				// in most cases no impact; goes to parent node when whole
				// formula selected
				if (parent instanceof SequenceNode) {
					editorState.setCurrentNode((SequenceNode) parent);
				}
			}

		}
		editorState.resetSelection();
		return nonempty;

	}

	private static void deleteMatrixElementValue(EditorState editorState) {
		SequenceNode matrixElement = (SequenceNode) editorState.getSelectionAnchor().getParent();
		matrixElement.clearChildren();
		editorState.setCurrentOffset(0);
		editorState.setCurrentNode(matrixElement);
		editorState.resetSelection();
	}

	/**
	 * @param node to check.
	 * @return if it is protected.
	 */
	public static boolean isProtected(Node node) {
		if (!(node instanceof InternalNode)) {
			return false;
		}
		return ((InternalNode) node).isProtected();
	}

	/**
	 * @param editorState current state
	 * @param ch single char
	 * @return whether it was handled
	 */
	public boolean handleChar(EditorState editorState, char ch) {
		// backspace, delete and escape are handled for key down
		if (ch == JavaKeyCodes.VK_BACK_SPACE || ch == JavaKeyCodes.VK_DELETE
				|| ch == JavaKeyCodes.VK_ESCAPE) {
			return true;
		}

		if (shouldCharBeIgnored(editorState, ch)) {
			return true;
		}

		if (plainTextMode || editorState.isInsideQuotes()) {
			handleTextModeInsert(editorState, ch);
			return true;
		}

		// Move cursor out of a recurring decimal if the typed character is not a digit
		if (editorState.isInRecurringDecimal() && !Character.isDigit(ch)) {
			CursorController.nextField(editorState);
		}

		TemplateCatalog catalog = editorState.getCatalog();

		if (!catalog.isFunctionOpenKey(ch) && ch != ',') {
			int currentOffset = editorState.getCurrentOffset();
			SequenceNode field = editorState.getCurrentNode();

			if (field.getChild(currentOffset) instanceof PlaceholderNode) {
				editorState.getCurrentNode().removeChild(currentOffset);
			} else if (field.getChild(currentOffset - 1) instanceof PlaceholderNode) {
				editorState.getCurrentNode().removeChild(currentOffset - 1);
				CursorController.prevCharacter(editorState);
			}
		}

		if (ch != '(' && ch != '{' && ch != '[' && ch != '/' && ch != '|'
				&& ch != Unicode.LFLOOR && ch != Unicode.LCEIL && ch != '"') {
			deleteSelection(editorState);
		}
		if (useSimpleScripts) {
			checkScriptExit(ch, editorState);
		}

		boolean handled = handleEndBlocks(editorState, ch);
		if (!handled) {
			if (catalog.isArrayCloseKey(ch)) {
				endField(editorState, ch);
				handled = true;
			} else if (catalog.isFunctionOpenKey(ch)) {
				newBraces(editorState, ch);
				handled = true;
			} else if (ch == '^') {
				newScript(editorState, Tag.SUPERSCRIPT);
				handled = true;
			} else if (Unicode.isSuperscriptDigit(ch)) {
				newScript(editorState, Tag.SUPERSCRIPT);
				newCharacter(editorState, (char) (Unicode.superscriptToNumber(ch) + '0'));
				CursorController.nextCharacter(editorState);
				handled = true;
			} else if (ch == Unicode.SUPERSCRIPT_MINUS) {
				newScript(editorState, Tag.SUPERSCRIPT);
				newCharacter(editorState, '-');
				CursorController.nextCharacter(editorState);
				handled = true;
			} else if (ch == '_') {
				newScript(editorState, Tag.SUBSCRIPT);
				handled = true;
			} else if (ch == '/' || ch == '\u00f7') {
				if (!editorState.isPreventingNestedFractions()) {
					newFunction(editorState, "frac", false, null);
				}
				handled = true;
			} else if (ch == Unicode.INVISIBLE_PLUS) {
				// skip, handled as fraction
				handled = true;
			} else if (ch == Unicode.SQUARE_ROOT) {
				newFunction(editorState, "sqrt", false, null);
				handled = true;
			} else if (isAbsDelimiter(ch)) {
				newFunction(editorState, "abs");
				handled = true;
			} else if (catalog.isArrayOpenKey(ch)) {
				newArray(editorState, 1, ch, false);
				handled = true;
			} else if (ch == Unicode.MULTIPLY || ch == Unicode.CENTER_DOT
					|| ch == Unicode.BULLET) {
				newOperator(editorState, '*');
				handled = true;
			} else if (ch == ',' || (!allowAbs && ch == '|')) {
				if (preventDimensionChange(editorState)) {
					if (shouldMoveCursor(editorState)) {
						CursorController.nextCharacter(editorState);
					}
				} else {
					comma(editorState);
				}
				handled = true;
			} else if (catalog.isOperator("" + ch)) {
				newOperator(editorState, ch);
				handled = true;
			} else if (ch == 3 || ch == 22) {
				// invisible characters on MacOS
				handled = true;
			} else if (catalog.isSymbol("" + ch)) {
				newSymbol(editorState, ch);
				handled = true;
			} else {
				// Always handled as character
				newCharacter(editorState, ch);
				handled = true;
			}
		}
		return handled;
	}

	private boolean shouldCharBeIgnored(EditorState editorState, char ch) {
		SequenceNode root = editorState.getRootNode();
		return (root.isProtected() || root.isKeepCommas())
				&& !plainTextMode && ignoreChars.contains(ch);
	}

	private void handleTextModeInsert(EditorState editorState, char ch) {
		deleteSelection(editorState);

		char toInsert = ch;
		if (toInsert == '\"') {
			toInsert = getNextQuote(editorState.getCurrentNode(),
					editorState.getCurrentOffset());
		}

		CharacterTemplate template = catalog.getCharacter("" + toInsert);
		editorState.addArgument(template);
	}

	private char getNextQuote(SequenceNode currentField, int currentOffset) {
		for (int i = currentOffset - 1; i >= 0; i--) {
			Node argument = currentField.getChild(i);
			if (argument instanceof CharacterNode) {
				CharacterNode ch = (CharacterNode) argument;

				if (ch.isUnicode('\u201c')) {
					return '\u201d';
				} else if (ch.isUnicode('\u201d')) {
					return '\u201c';
				}
			}
		}

		return '\u201c';
	}

	private void checkScriptExit(char ch, EditorState editorState) {
		if (ch == '+' || ch == '-' || ch == '(' || ch == ')' || ch == '=' || ch == '*') {
			exitScript(Tag.SUBSCRIPT, editorState);
		}
		if (ch == '=') {
			exitScript(Tag.SUPERSCRIPT, editorState);
		}
	}

	private void exitScript(Tag subscript, EditorState state) {
		SequenceNode currentField = state.getCurrentNode();
		if (currentField.getParent() != null && currentField.getParent().hasTag(subscript)) {
			CursorController.nextCharacter(state);
		}
	}

	private boolean preventDimensionChange(EditorState editorState) {
		InternalNode parent = editorState.getCurrentNode().getParent();
		return ArrayNode.isLocked(parent) && (
				((ArrayNode) parent).getOpenDelimiter().getCharacter() == '('
						|| ((ArrayNode) parent).getOpenDelimiter().getCharacter() == '{');
	}

	private boolean shouldMoveCursor(EditorState editorState) {
		int offset = editorState.getCurrentOffset();
		Node next = editorState.getCurrentNode().getChild(offset);
		return next != null && next.isFieldSeparator();
	}

	private boolean handleEndBlocks(EditorState editorState, char ch) {
		InternalNode parent = editorState.getCurrentNode().getParent();
		if (editorState.getSelectionStart() == null) {
			if (parent instanceof ArrayNode) {
				return handleEndArrayNode((ArrayNode) parent, editorState, ch);
			} else if (parent instanceof FunctionNode) {
				return handleEndFunctionNode((FunctionNode) parent, editorState, ch);
			}
		}
		return false;
	}

	private boolean handleEndArrayNode(ArrayNode arrayNode, EditorState editorState, char ch) {
		if (ch == '"' && arrayNode.getCloseDelimiter().getCharacter() == '"') {
			return handleExit(editorState, ch);
		}
		return false;
	}

	private boolean handleEndFunctionNode(FunctionNode functionNode,
			EditorState editorState, char ch) {
		if (Tag.ABS.equals(functionNode.getName()) && isAbsDelimiter(ch)) {
			SequenceNode currentField = editorState.getCurrentNode();
			int offset = editorState.getCurrentOffset();
			Node prevArg = currentField.getChild(offset - 1);

			// check for eg * + -
			boolean isOperation = prevArg != null && mathField.getCatalog()
					.isOperator(prevArg + "");
			if (!isOperation) {
				return handleExit(editorState, ch);
			}
		}
		return false;
	}

	private boolean handleExit(EditorState editorState, char ch) {
		SequenceNode currentField = editorState.getCurrentNode();

		int offset = editorState.getCurrentOffset();

		Node nextArg = currentField.getChild(offset);

		if (nextArg == null) {
			endField(editorState, ch);
		}
		return nextArg == null;
	}

	private void comma(EditorState editorState) {
		int offset = editorState.getCurrentOffset();
		SequenceNode currentField = editorState.getCurrentNode();
		if (currentField.getChild(offset) != null
				&& currentField.getChild(offset).isFieldSeparator()) {
			CursorController.nextCharacter(editorState);
			return;
		}

		newOperator(editorState, ',');
	}

	/**
	 * Get content from clipboard and paste it to the field (desktop only).
	 */
	public void paste() {
		if (mathField != null) {
			mathField.paste();
		}
	}

	/**
	 * Copy selection from editor to clipboard.
	 */
	public void copy() {
		if (mathField != null) {
			mathField.copy();
		}
	}

	/**
	 * Handle tab key.
	 * @param shiftDown whether shift is pressed
	 * @return tab handling
	 */
	public boolean handleTab(boolean shiftDown) {
		if (mathField != null) {
			return mathField.getInternal().onTab(shiftDown);
		}
		return true;
	}

	/**
	 * Add mixed number, move to numerator if integer part present
	 * @param state current state
	 */
	public void addMixedNumber(EditorState state) {
		FunctionTemplate template = catalog.getGeneral(Tag.FRAC);
		FunctionNode function = new FunctionNode(template);
		function.setPreventingNestedFractions(true);
		initArguments(function, 0);
		Node prev = state.getCurrentNode().getChild(state.getCurrentOffset() - 1);
		state.getCurrentNode().addChild(state.getCurrentOffset(), function);
		if (prev instanceof CharacterNode && ((CharacterNode) prev).isDigit()) {
			state.setCurrentNode(function.getChild(0));
			state.setCurrentOffset(0);
		}
	}

	/**
	 * @param editorFeatures set of available editor features
	 */
	public void setEditorFeatures(EditorFeatures editorFeatures) {
		this.editorFeatures = editorFeatures;
	}

	/**
	 * @return Set of available editor features
	 */
	public @CheckForNull EditorFeatures getEditorFeatures() {
		return editorFeatures;
	}

	/**
	 * @param exp string in any convertible syntax (LaTeX, MathML, ...)
	 * @return string in editor syntax
	 */
	public String convert(String exp) {
		return syntaxAdapter == null ? exp : syntaxAdapter.convert(exp);
	}

	/**
	 * @return whether mixed numbers are supported
	 */
	public boolean supportsMixedNumbers() {
		return editorFeatures == null || editorFeatures.areMixedNumbersEnabled();
	}

	public static class FunctionPower {
		/** subscript or superscript */
		public FunctionNode script;
		public String name;
	}

	private boolean isAbsDelimiter(char ch) {
		return allowAbs && ch == '|';
	}
}
