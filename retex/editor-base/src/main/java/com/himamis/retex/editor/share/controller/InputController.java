package com.himamis.retex.editor.share.controller;

import java.util.ArrayList;

import com.google.j2objc.annotations.Weak;
import com.himamis.retex.editor.share.editor.MathField;
import com.himamis.retex.editor.share.meta.MetaArray;
import com.himamis.retex.editor.share.meta.MetaCharacter;
import com.himamis.retex.editor.share.meta.MetaFunction;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.meta.Tag;
import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.share.util.JavaKeyCodes;
import com.himamis.retex.editor.share.util.Unicode;

public class InputController {

	public static final char FUNCTION_OPEN_KEY = '('; // probably universal
	public static final char FUNCTION_CLOSE_KEY = ')';
	public static final char DELIMITER_KEY = ';';

	private MetaModel metaModel;

	@Weak
	private MathField mathField;

	private boolean createFrac = true;

	public InputController(MetaModel metaModel) {
		this.metaModel = metaModel;
	}

	public MathField getMathField() {
		return mathField;
	}

	public void setMathField(MathField mathField) {
		this.mathField = mathField;
	}

	public boolean getCreateFrac() {
		return createFrac;
	}

	public void setCreateFrac(boolean createFrac) {
		this.createFrac = createFrac;
	}

	final static private char getLetter(MathComponent component)
			throws Exception {
		if (!(component instanceof MathCharacter)) {
			throw new Exception("Math component is not a character");
		}

		MathCharacter mathCharacter = (MathCharacter) component;
		if (!mathCharacter.isCharacter()) {
			throw new Exception("Math component is not a character");
		}

		char c = mathCharacter.getUnicode();

		if (!Character.isLetter(c)) {
			throw new Exception("Math component is not a character");
		}

		return c;
	}

	/**
	 * Insert array.
	 *
	 * @param editorState
	 *            editor state
	 * @param size
	 *            array size
	 * @param arrayOpenKey
	 *            array type
	 * @param reverse
	 *            whether to insert it left of the cursor
	 * @return array
	 */
	public MathArray newArray(EditorState editorState, int size,
			char arrayOpenKey, boolean reverse) {
		moveCursorOutOfFunctionName(editorState);
		MathSequence currentField = editorState.getCurrentField();
		int currentOffset = editorState.getCurrentOffset();
		MetaArray meta = metaModel.getArray(arrayOpenKey);
		MathArray array = new MathArray(meta, size);
		int cutPosition = reverse ? findBackwardCutPosition(currentField,
				currentOffset) : currentOffset;
		ArrayList<MathComponent> removed = reverse
				? cut(currentField, cutPosition, currentOffset - 1, editorState, array,
				true)
				: cut(currentField, cutPosition, -1, editorState, array,
				true);

		// add sequence
		MathSequence field = new MathSequence();
		array.setArgument(0, field);
		insertReverse(field, -1, removed);
		for (int i = 1; i < size; i++) {
			// add sequence
			array.setArgument(i, new MathSequence());
		}
		editorState.resetSelection();
		// set current
		if (reverse) {
			editorState.setCurrentField(currentField);
			editorState.setCurrentOffset(cutPosition + 1);
		} else {
			editorState.setCurrentField(field);
			editorState.setCurrentOffset(0);
		}
		return array;
	}

	private static int findBackwardCutPosition(MathSequence currentField, int currentPosition) {
		int index = currentPosition;
		while (index > 0) {
			MathComponent component = currentField.getArgument(index - 1);
			if (component instanceof MathCharacter) {
				MathCharacter character = (MathCharacter) component;
				if ("=".equals(character.getName())) {
					return index;
				}
			}
			index -= 1;
		}
		return index;
	}

	private static void moveCursorOutOfFunctionName(EditorState editorState) {
		MathSequence currentField = editorState.getCurrentField();
		if (currentField.getParent() != null
				&& currentField.getParent().hasTag(Tag.APPLY)
				&& currentField.getParentIndex() == 0
				&& editorState.getCurrentOffset() == 0) {
			MathContainer function = currentField.getParent();
			if (function.getParent() != null
					&& function.getParent() instanceof MathSequence) {
				editorState
						.setCurrentField((MathSequence) function.getParent());
				editorState.setCurrentOffset(function.getParentIndex());
			}
		}
	}

	/**
	 * Insert matrix.
	 */
	public void newMatrix(EditorState editorState, int columns, int rows) {
		MathSequence currentField = editorState.getCurrentField();
		int currentOffset = editorState.getCurrentOffset();
		MetaArray meta = metaModel.getMatrix();
		MathArray matrix = new MathArray(meta, columns, rows);
		currentField.addArgument(currentOffset, matrix);

		// add sequence
		MathSequence field = new MathSequence();
		matrix.setArgument(0, field);

		for (int i = 1; i < matrix.size(); i++) {
			// add sequence
			matrix.setArgument(i, new MathSequence());
		}

		// set current
		editorState.setCurrentField(field);
		editorState.setCurrentOffset(0);
	}

	/**
	 * Insert braces (), [], {}, "".
	 *
	 * @param editorState
	 *            editor state
	 * @param ch
	 *            opening bracket character
	 */
	public void newBraces(EditorState editorState, char ch) {
		if (editorState.hasSelection()) {
			editorState.cursorToSelectionStart();
		}

		int initialOffset = editorState.getCurrentOffset();
		MathComponent last = editorState.getCurrentField()
				.getArgument(initialOffset - 1);
		MathFunction script = MathFunction.isScript(last) ? (MathFunction) last
						: null;
		if (script != null) {
			initialOffset--;
		}
		String casName = ArgumentHelper.readCharacters(editorState,
				initialOffset);

		Tag tag = Tag.lookup(casName);

		if (ch == FUNCTION_OPEN_KEY && tag != null) {
			if (script != null) {
				bkspCharacter(editorState);
			}
			delCharacters(editorState, casName.length());
			newFunction(editorState, casName, false,
					script);
		} else if ((ch == FUNCTION_OPEN_KEY || ch == '[')
				&& metaModel.isFunction(casName)) {
			if (script != null) {
				bkspCharacter(editorState);
			}
			delCharacters(editorState, casName.length());
			newFunction(editorState, casName, ch == '[', script);

		} else {
			String selText = editorState.getSelectedText().trim();
			if (editorState.getSelectionStart() instanceof MathCharacter) {
				if (selText.startsWith("<") && selText.endsWith(">")) {

					deleteSelection(editorState);
					MetaArray meta = metaModel.getArray(ch);
					MathArray array = new MathArray(meta, 1);
					MathSequence seq = new MathSequence();
					array.setArgument(0, seq);
					editorState.getCurrentField()
							.addArgument(editorState.getCurrentOffset(), array);
					editorState.setCurrentField(seq);
					editorState.setCurrentOffset(0);
					return;
				}
			}

			// TODO brace type
			newArray(editorState, 1, ch, false);
		}
	}

	/**
	 * Insert function by name.
	 *
	 * @param name
	 *            function
	 */
	public void newFunction(EditorState editorState, String name) {
		newFunction(editorState, name, false, null);
	}

	/**
	 * Insert function by name.
	 *
	 * @param name
	 *            function
	 */
	public void newFunction(EditorState editorState, String name,
			boolean square, MathFunction exponent) {
		MathSequence currentField = editorState.getCurrentField();
		int currentOffset = editorState.getCurrentOffset();
		// add extra braces for sqrt, nthroot and fraction
		if ("^".equals(name) && currentOffset > 0
				&& editorState.getSelectionEnd() == null) {
			if (currentField
					.getArgument(currentOffset - 1) instanceof MathFunction) {
				MathFunction function = (MathFunction) currentField
						.getArgument(currentOffset - 1);
				if (Tag.SQRT.equals(function.getName())
						|| Tag.NROOT.equals(function.getName())
						|| Tag.FRAC.equals(function.getName())) {

					currentField.delArgument(currentOffset - 1);
					// add braces
					MathArray array = new MathArray(
							metaModel.getArray(Tag.REGULAR), 1);
					currentField.addArgument(currentOffset - 1, array);
					// add sequence
					MathSequence field = new MathSequence();
					array.setArgument(0, field);
					field.addArgument(0, function);
				}
			}
		}

		// add function
		MathFunction function;
		Tag tag = Tag.lookup(name);
		final boolean hasSelection = editorState.getSelectionEnd() != null;
		int offset = 0;
		if (tag == Tag.LOG && exponent != null
				&& exponent.getName() == Tag.SUBSCRIPT) {
			function = buildLog(exponent);
			offset = 1;
		} else if (tag != null && exponent == null) {
			MetaFunction meta = metaModel.getGeneral(tag);
			function = new MathFunction(meta);
		} else {
			offset = 1;
			tag = null; // reset tag if exponent was found
			function = buildCustomFunction(name, square, exponent);
		}
		boolean builtin = tag != null;
		// add sequences
		for (int i = offset; i < function.size(); i++) {
			MathSequence field = new MathSequence();
			function.setArgument(i, field);
		}

		// pass characters for fraction and factorial only
		if (tag == Tag.FRAC) {
			if (hasSelection) {
				ArrayList<MathComponent> removed = cut(currentField,
						currentOffset, -1, editorState, function, true);
				MathSequence field = new MathSequence();
				function.setArgument(0, field);
				insertReverse(field, -1, removed);
				editorState.resetSelection();
				editorState.setCurrentField(function.getArgument(1));
				editorState.setCurrentOffset(0);
				return;
			}
			ArgumentHelper.passArgument(editorState, function);
		} else if (tag == Tag.SUPERSCRIPT) {
			if (hasSelection) {
				MathArray array = this.newArray(editorState, 1, '(', false);
				editorState.setCurrentField((MathSequence) array.getParent());
				editorState.resetSelection();
				editorState.setCurrentOffset(array.getParentIndex() + 1);
				newFunction(editorState, name, square, null);
				return;
			}
		} else {
			if (hasSelection || !builtin) {
				ArrayList<MathComponent> removed = cut(currentField,
						currentOffset, -1, editorState, function, true);
				MathSequence field = new MathSequence();
				function.setArgument(offset, field);
				insertReverse(field, -1, removed);
				editorState.resetSelection();
				editorState.setCurrentField(field);
				editorState.setCurrentOffset(hasSelection ? field.size()
						: function.getInitialIndex());
				// editorState.incCurrentOffset();
				return;
			}
		}
		currentOffset = editorState.getCurrentOffset();
		currentField.addArgument(currentOffset, function);
		int select = function.getInitialIndex();
		if (function.hasChildren()) {
			// set current sequence
			CursorController.firstField(editorState,
					function.getArgument(select));
			editorState.setCurrentOffset(editorState.getCurrentField().size());
		} else {
			editorState.incCurrentOffset();
		}
	}

	private MathFunction buildLog(MathFunction exponent) {
		MetaFunction meta = metaModel.getGeneral(Tag.LOG);
		MathFunction function = new MathFunction(meta);
		function.setArgument(0, exponent.getArgument(0));
		return function;
	}

	private MathFunction buildCustomFunction(String name, boolean square,
			MathComponent exponent) {
		MetaFunction meta = metaModel.getFunction(name, square);
		MathSequence nameS = new MathSequence();
		for (int i = 0; i < name.length(); i++) {
			nameS.addArgument(new MathCharacter(
					metaModel.getCharacter(name.charAt(i) + "")));
		}
		if (exponent != null) {
			nameS.addArgument(exponent);
		}
		MathFunction function = new MathFunction(meta);
		function.setArgument(0, nameS);
		return function;
	}

	/**
	 * @param editorState
	 *            current state
	 * @param scriptTag
	 *            SUBSCRIPT or SUPERSCRIPT
	 */
	public void newScript(EditorState editorState, Tag scriptTag) {
		MathSequence currentField = editorState.getCurrentField();
		if (currentField.size() == 0
				&& currentField.getParent() instanceof MathFunction
				&& Tag.SUPERSCRIPT == ((MathFunction) currentField.getParent())
				.getName()
				&& Tag.SUPERSCRIPT == scriptTag) {
			return;
		}
		int currentOffset = editorState.getCurrentOffset();

		int offset = currentOffset;
		while (offset > 0 && currentField
				.getArgument(offset - 1) instanceof MathFunction) {

			MathFunction function = (MathFunction) currentField
					.getArgument(offset - 1);
			if (scriptTag == function.getName()) {
				editorState.setCurrentField(function.getArgument(0));
				editorState.setCurrentOffset(function.getArgument(0).size());
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
				&& currentField.getArgument(offset) instanceof MathFunction) {

			MathFunction function = (MathFunction) currentField
					.getArgument(offset);
			if (scriptTag == function.getName()) {
				editorState.setCurrentField(function.getArgument(0));
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
				.getArgument(currentOffset - 1) instanceof MathFunction) {
			MathFunction function = (MathFunction) currentField
					.getArgument(currentOffset - 1);
			if (Tag.SUPERSCRIPT == function.getName() && Tag.SUBSCRIPT == scriptTag) {
				currentOffset--;
			}
		}
		if (currentOffset < currentField.size() && currentField
				.getArgument(currentOffset) instanceof MathFunction) {
			MathFunction function = (MathFunction) currentField
					.getArgument(currentOffset);
			if (Tag.SUBSCRIPT == function.getName() && Tag.SUPERSCRIPT == scriptTag) {
				currentOffset++;
			}
		}
		editorState.setCurrentOffset(currentOffset);
		newFunction(editorState, scriptTag.getKey() + "");
	}

	/**
	 * Insert operator.
	 */
	public void newOperator(EditorState editorState, char op) {
		MetaCharacter meta = metaModel.getOperator("" + op);
		newCharacter(editorState, meta);
	}

	/**
	 * Insert symbol.
	 *
	 * @param editorState
	 *            state
	 * @param sy
	 *            char
	 */
	public void newSymbol(EditorState editorState, char sy) {
		MetaCharacter meta = metaModel.getSymbol("" + sy);
		newCharacter(editorState, meta);
	}

	/**
	 * Insert character.
	 *
	 * @param editorState
	 *            state
	 * @param ch
	 *            char
	 */
	public void newCharacter(EditorState editorState, char ch) {
		MetaCharacter meta = metaModel.getCharacter("" + ch);
		newCharacter(editorState, meta);
	}

	/**
	 * Insert character.
	 *
	 * @param editorState
	 *            current state
	 * @param meta
	 *            character
	 */
	public void newCharacter(EditorState editorState, MetaCharacter meta) {
		MathComponent last = editorState.getCurrentField()
				.getArgument(editorState.getCurrentOffset() - 1);

		if (last instanceof MathCharacter) {
			MetaCharacter merge = metaModel
					.merge(((MathCharacter) last).toString(), meta);
			if (merge != null) {
				editorState.getCurrentField().setArgument(
						editorState.getCurrentOffset() - 1,
						new MathCharacter(merge));
				return;
			}
		}
		editorState.addArgument(new MathCharacter(meta));

	}

	/**
	 * Insert field.
	 *
	 * @param editorState
	 *            current state
	 * @param ch
	 *            bracket
	 */
	public void endField(EditorState editorState, char ch) {
		MathSequence currentField = editorState.getCurrentField();
		int currentOffset = editorState.getCurrentOffset();
		// first array specific ...
		if (currentField.getParent() instanceof MathArray) {
			MathArray parent = (MathArray) currentField.getParent();

			// if ',' typed within 1DArray or Vector ... add new field
			if (ch == parent.getFieldKey()
					&& (parent.is1DArray() || parent.isVector())) {

				int index = currentField.getParentIndex();
				MathSequence field = new MathSequence();
				parent.addArgument(index + 1, field);
				while (currentField.size() > currentOffset) {
					MathComponent component = currentField
							.getArgument(currentOffset);
					currentField.delArgument(currentOffset);
					field.addArgument(field.size(), component);
				}
				currentField = field;
				currentOffset = 0;

				// if ',' typed at the end of intermediate field of 2DArray or
				// Matrix ... move to next field
			} else if (ch == parent.getFieldKey()
					&& currentOffset == currentField.size()
					&& parent.size() > currentField.getParentIndex() + 1
					&& (currentField.getParentIndex() + 1)
					% parent.columns() != 0) {

				currentField = parent
						.getArgument(currentField.getParentIndex() + 1);
				currentOffset = 0;

				// if ';' typed at the end of last field ... add new row
			} else if (ch == parent.getRowKey()
					&& currentOffset == currentField.size()
					&& parent.size() == currentField.getParentIndex() + 1) {

				parent.addRow();
				currentField = parent
						.getArgument(parent.size() - parent.columns());
				currentOffset = 0;

				// if ';' typed at the end of (not last) row ... move to next
				// field
			} else if (ch == parent.getRowKey()
					&& currentOffset == currentField.size()
					&& (currentField.getParentIndex() + 1)
					% parent.columns() == 0) {

				currentField = parent
						.getArgument(currentField.getParentIndex() + 1);
				currentOffset = 0;

				// if ']' '}' typed at the end of last field ... move out of
				// array
			} else if (ch == parent.getCloseKey() && parent.isArray()) {

				ArrayList<MathComponent> removed = cut(currentField,
						currentOffset);
				insertReverse(parent.getParent(), parent.getParentIndex(),
						removed);

				currentOffset = parent.getParentIndex() + 1;
				currentField = (MathSequence) parent.getParent();
			} else if ((ch == parent.getCloseKey() && parent.isMatrix())
					&& parent.size() == currentField.getParentIndex() + 1
					&& currentOffset == currentField.size()) {

				currentOffset = parent.getParentIndex() + 1;
				currentField = (MathSequence) parent.getParent();
			}

			// now functions, braces, apostrophes ...
		} else if (currentField.getParent() != null) {
			MathContainer parent = currentField.getParent();

			// if ',' typed at the end of intermediate field of function ...
			// move to next field
			if (ch == ',' && currentOffset == currentField.size()
					&& parent instanceof MathFunction
					&& parent.size() > currentField.getParentIndex() + 1) {

				currentField = (MathSequence) parent
						.getArgument(currentField.getParentIndex() + 1);
				currentOffset = 0;

				// if ')' typed at the end of last field of function ... move
				// after closing character
			} else if (currentOffset == currentField.size()
					&& parent instanceof MathFunction
					&& ch == ((MathFunction) parent).getClosingBracket()
					.charAt(0)
					&& parent.size() == currentField.getParentIndex() + 1) {

				currentOffset = parent.getParentIndex() + 1;
				currentField = (MathSequence) parent.getParent();

				// if ')' typed at the end of last field of braces ... move
				// after closing character
			} else if (parent instanceof MathFunction
					&& ch == ((MathFunction) parent).getClosingBracket()
					.charAt(0)
					&& parent.size() == currentField.getParentIndex() + 1) {
				ArrayList<MathComponent> removed = cut(currentField,
						currentOffset);
				insertReverse(parent.getParent(), parent.getParentIndex(),
						removed);

				currentOffset = parent.getParentIndex() + 1;
				currentField = (MathSequence) parent.getParent();

				// if '|' typed at the end of an abs function
				// special case
			} else if (parent instanceof  MathFunction
					&& parent.hasTag(Tag.ABS)
					&& ch == '|'
					&& parent.size() == currentField.getParentIndex() + 1) {
				currentOffset = parent.getParentIndex() + 1;
				currentField = (MathSequence) parent.getParent();
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
		editorState.setCurrentField(currentField);
		editorState.setCurrentOffset(currentOffset);
	}

	private static void insertReverse(MathContainer parent, int parentIndex,
			ArrayList<MathComponent> removed) {
		for (int j = removed.size() - 1; j >= 0; j--) {
			MathComponent o = removed.get(j);
			int idx = parentIndex + (removed.size() - j);
			parent.addArgument(idx, o);
		}

	}

	private static ArrayList<MathComponent> cut(MathSequence currentField,
			int from, int to, EditorState st, MathComponent array,
			boolean rec) {

		int end = to < 0 ? endToken(currentField) : to;
		int start = from;

		if (st.getCurrentField() == currentField
				&& st.getSelectionEnd() != null) {
			// the root is selected
			if (st.getSelectionEnd().getParent() == null && rec) {
				return cut((MathSequence) st.getSelectionEnd(), 0, -1, st,
						array, false);
			}
			// deep selection, e.g. a fraction
			if (st.getSelectionEnd().getParent() != currentField && rec) {
				return cut((MathSequence) st.getSelectionEnd().getParent(),
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
		ArrayList<MathComponent> removed = new ArrayList<>();
		for (int i = end; i >= start; i--) {
			removed.add(currentField.getArgument(i));
			currentField.removeArgument(i);
		}
		currentField.addArgument(start, array);
		return removed;
	}

	private static int endToken(MathSequence currentField) {
		for (int i = 0; i < currentField.size() - 2; i++) {
			if (match(currentField, i, ", <")) {
				return i - 1;
			}
		}
		return currentField.size() - 1;
	}

	private static boolean match(MathSequence currentField, int i,
			String string) {
		for (int j = 0; j < 3; j++) {
			if (!(string.charAt(j) + "")
					.equals(currentField.getArgument(i + j).toString())) {
				return false;
			}
		}
		return true;
	}

	private static ArrayList<MathComponent> cut(MathSequence currentField,
			int currentOffset) {
		ArrayList<MathComponent> removed = new ArrayList<>();

		for (int i = currentField.size() - 1; i >= currentOffset; i--) {
			removed.add(currentField.getArgument(i));
			currentField.removeArgument(i);
		}

		return removed;
	}

	/**
	 * Insert symbol.
	 *
	 * @param editorState
	 *            current state
	 */
	public void escSymbol(EditorState editorState) {
		editorState.getRootComponent().clearArguments();
		editorState.setCurrentField(editorState.getRootComponent());
		editorState.setCurrentOffset(0);
		editorState.resetSelection();
	}

	/**
	 * Backspace to remove container
	 *
	 * @param editorState
	 *            current state
	 */
	public void bkspContainer(EditorState editorState) {
		MathSequence currentField = editorState.getCurrentField();

		// if parent is function (cursor is at the beginning of the field)
		if (currentField.getParent() instanceof MathFunction) {
			MathFunction parent = (MathFunction) currentField.getParent();

			// fraction has operator like behavior
			if (Tag.FRAC == parent.getName()) {

				// if second operand is empty sequence
				if (currentField.getParentIndex() == 1
						&& currentField.size() == 0) {
					int size = parent.getArgument(0).size();
					delContainer(editorState, parent, parent.getArgument(0));
					// move after included characters
					editorState.addCurrentOffset(size);
					// if first operand is empty sequence
				} else if (currentField.getParentIndex() == 1
						&& parent.getArgument(0).size() == 0) {
					delContainer(editorState, parent, currentField);
				}

			} else if (metaModel.isGeneral(parent.getName())) {
				if (currentField.getParentIndex() == parent.getInsertIndex()) {
					delContainer(editorState, parent, currentField);
				}
				// not a fraction, and cursor is right after the sign
			} else {
				if (currentField.getParentIndex() == 1) {
					removeParenthesesOfFunction(parent, editorState);
				} else if (currentField.getParentIndex() > 1) {
					MathSequence prev = parent
							.getArgument(currentField.getParentIndex() - 1);
					int len = prev.size();
					for (int i = 0; i < currentField.size(); i++) {
						prev.addArgument(currentField.getArgument(i));
					}
					parent.removeArgument(currentField.getParentIndex());
					editorState.setCurrentField(prev);
					editorState.setCurrentOffset(len);
				}
			}

			// if parent are empty array
		} else if (currentField.getParent() instanceof MathArray
				&& currentField.getParent().size() == 1) {

			MathArray parent = (MathArray) currentField.getParent();
			delContainer(editorState, parent, parent.getArgument(0));

			// if parent is 1DArray or Vector and cursor is at the beginning of
			// intermediate the field
		} else if (currentField.getParent() instanceof MathArray
				&& (((MathArray) currentField.getParent()).is1DArray()
				|| ((MathArray) currentField.getParent()).isVector())
				&& currentField.getParentIndex() > 0) {

			int index = currentField.getParentIndex();
			MathArray parent = (MathArray) currentField.getParent();
			MathSequence field = parent.getArgument(index - 1);
			int size = field.size();
			editorState.setCurrentOffset(0);
			while (currentField.size() > 0) {

				MathComponent component = currentField.getArgument(0);
				currentField.delArgument(0);
				field.addArgument(field.size(), component);
			}
			parent.delArgument(index);
			editorState.setCurrentField(field);
			editorState.setCurrentOffset(size);
		}

		// we stop here for now
	}

	private void removeParenthesesOfFunction(MathFunction function, EditorState editorState) {
		MathSequence functionName = function.getArgument(0);
		int offset = calculateOffsetForRemovingExpressionArguments(function, functionName);
		delContainer(editorState, function, functionName);
		editorState.setCurrentOffset(offset);
	}

	private static int calculateOffsetForRemovingExpressionArguments(
			MathComponent expression,
			MathSequence operand) {
		return expression.getParentIndex() + operand.size();
	}

	/**
	 * Delete container, move its content to the parent.
	 *
	 * @param editorState
	 *            current state
	 */
	public static void delContainer(EditorState editorState) {
		MathSequence currentField = editorState.getCurrentField();

		// if parent is function (cursor is at the end of the field)
		if (currentField.getParent() instanceof MathFunction) {
			MathFunction parent = (MathFunction) currentField.getParent();

			// fraction has operator like behavior
			if (Tag.FRAC.equals(parent.getName())) {

				// first operand is current, second operand is empty sequence
				if (currentField.getParentIndex() == 0
						&& parent.getArgument(1).size() == 0) {
					int size = parent.getArgument(0).size();
					delContainer(editorState, parent, currentField);
					// move after included characters
					editorState.addCurrentOffset(size);

					// first operand is current, and first operand is empty
					// sequence
				} else if (currentField.getParentIndex() == 0
						&& (currentField).size() == 0) {
					delContainer(editorState, parent, parent.getArgument(1));
				}
			}

			// if parent are empty braces
		} else if (currentField.getParent() instanceof MathArray
				&& currentField.getParent().size() == 1
				&& currentField.size() == 0) {
			MathArray parent = (MathArray) currentField.getParent();
			int size = parent.getArgument(0).size();
			delContainer(editorState, parent, parent.getArgument(0));
			// move after included characters
			editorState.addCurrentOffset(size);

			// if parent is 1DArray or Vector and cursor is at the end of the
			// field
		} else if (currentField.getParent() instanceof MathArray
				&& (((MathArray) currentField.getParent()).is1DArray()
				|| ((MathArray) currentField.getParent()).isVector())
				&& currentField.getParentIndex() + 1 < currentField.getParent()
				.size()) {

			int index = currentField.getParentIndex();
			MathArray parent = (MathArray) currentField.getParent();
			MathSequence field = parent.getArgument(index + 1);
			int size = currentField.size();
			while (currentField.size() > 0) {

				MathComponent component = currentField.getArgument(0);
				currentField.delArgument(0);
				field.addArgument(field.size(), component);
			}
			parent.delArgument(index);
			editorState.setCurrentField(field);
			editorState.setCurrentOffset(size);
		}

		// we stop here for now
	}

	/**
	 * Remove character left to the cursor
	 *
	 * @param editorState
	 *            current state
	 */
	public void bkspCharacter(EditorState editorState) {
		int currentOffset = editorState.getCurrentOffset();
		if (currentOffset > 0) {
			if (editorState.getCurrentField()
					.getArgument(currentOffset - 1) instanceof MathArray) {

				MathArray parent = (MathArray) editorState.getCurrentField()
						.getArgument(currentOffset - 1);

				extendBrackets(parent, editorState);
			} else {
				editorState.getCurrentField().delArgument(currentOffset - 1);
				editorState.decCurrentOffset();
			}
		} else {
			bkspContainer(editorState);
		}
	}

	private static void extendBrackets(MathArray array,
			EditorState editorState) {
		int currentOffset = array.getParentIndex() + 1;
		MathContainer currentField = array.getParent();
		MathSequence lastArg = array.getArgument(array.size() - 1);
		int oldSize = lastArg.size();
		while (currentField.size() > currentOffset) {

			MathComponent component = currentField.getArgument(currentOffset);
			currentField.delArgument(currentOffset);
			lastArg.addArgument(lastArg.size(), component);
		}
		editorState.setCurrentField(lastArg);
		editorState.setCurrentOffset(oldSize);

	}

	/**
	 * Delete a character to the right of the cursor
	 *
	 * @param editorState
	 *            current state
	 */
	public void delCharacter(EditorState editorState) {
		int currentOffset = editorState.getCurrentOffset();
		MathSequence currentField = editorState.getCurrentField();
		if (currentOffset < currentField.size()) {

			CursorController.nextCharacter(editorState);
			bkspCharacter(editorState);

		} else {
			if (currentField.getParent() instanceof MathArray) {
				extendBrackets((MathArray) currentField.getParent(),
						editorState);
			} else {
				delContainer(editorState);
			}
		}
	}

	private static void delContainer(EditorState editorState,
			MathContainer container, MathSequence operand) {
		if (container.getParent() instanceof MathSequence) {
			// when parent is sequence
			MathSequence parent = (MathSequence) container.getParent();
			int offset = container.getParentIndex();
			// delete container
			parent.delArgument(offset);
			// add content of operand
			while (operand.size() > 0) {
				int lastArgumentIndex = operand.size() - 1;
				MathComponent element = operand.getArgument(lastArgumentIndex);
				operand.delArgument(lastArgumentIndex);
				parent.addArgument(offset, element);
			}
			editorState.setCurrentField(parent);
			editorState.setCurrentOffset(offset);
		}
	}

	private static void delCharacters(EditorState editorState, int length0) {
		int currentOffset = editorState.getCurrentOffset();
		MathSequence currentField = editorState.getCurrentField();
		int length = length0;
		while (length > 0 && currentOffset > 0 && currentField
				.getArgument(currentOffset - 1) instanceof MathCharacter) {

			MathCharacter character = (MathCharacter) currentField
					.getArgument(currentOffset - 1);
			if (character.isOperator() || character.isSymbol()) {
				break;
			}
			currentField.delArgument(currentOffset - 1);
			currentOffset--;
			length--;
		}
		editorState.setCurrentOffset(currentOffset);
	}

	/**
	 * remove characters before and after cursor
	 *
	 * @param editorState
	 *            editor state
	 * @param lengthBeforeCursor
	 *            number of characters before cursor to delete
	 * @param lengthAfterCursor
	 *            number of characters after cursor to delete
	 */
	public void removeCharacters(EditorState editorState,
			int lengthBeforeCursor, int lengthAfterCursor) {
		if (lengthBeforeCursor == 0 && lengthAfterCursor == 0) {
			return; // nothing to delete
		}
		MathSequence seq = editorState.getCurrentField();
		for (int i = 0; i < lengthBeforeCursor; i++) {
			editorState.decCurrentOffset();
			if (editorState.getCurrentOffset() < 0
					|| editorState.getCurrentOffset() >= seq.size()) {
				bkspContainer(editorState);
				return;
			}
			seq.delArgument(editorState.getCurrentOffset());
		}
		for (int i = 0; i < lengthAfterCursor; i++) {
			seq.delArgument(editorState.getCurrentOffset());
		}
	}

	/**
	 * set ret to characters (no digit) around cursor
	 *
	 * @param editorState
	 *            current state
	 * @param ret
	 *            builder for the word
	 * @return word length before cursor
	 */
	public static int getWordAroundCursor(EditorState editorState,
			StringBuilder ret) {
		int pos = editorState.getCurrentOffset();
		MathSequence seq = editorState.getCurrentField();

		StringBuilder before = new StringBuilder();
		int i;
		for (i = pos - 1; i >= 0; i--) {
			try {
				before.append(getLetter(seq.getArgument(i)));
			} catch (Exception e) {
				break;
			}
		}
		int lengthBefore = pos - i - 1;

		StringBuilder after = new StringBuilder();
		for (i = pos; i < seq.size(); i++) {
			try {
				after.append(getLetter(seq.getArgument(i)));
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
	 *
	 * @param editorState
	 *            current state
	 * @return success
	 */
	public static boolean deleteSelection(EditorState editorState) {
		boolean nonempty = false;
		if (editorState.getSelectionStart() != null) {
			MathContainer parent = editorState.getSelectionStart().getParent();
			int end, start;
			if (parent == null) {
				// all the formula is selected
				parent = editorState.getRootComponent();
				start = 0;
				end = parent.size() - 1;
			} else {
				end = parent.indexOf(editorState.getSelectionEnd());
				start = parent.indexOf(editorState.getSelectionStart());
			}
			if (end >= 0 && start >= 0) {
				for (int i = end; i >= start; i--) {
					parent.delArgument(i);
					nonempty = true;
				}

				editorState.setCurrentOffset(start);
				// in most cases no impact; goes to parent node when whole
				// formula selected
				if (parent instanceof MathSequence) {
					editorState.setCurrentField((MathSequence) parent);
				}
			}

		}
		editorState.resetSelection();
		return nonempty;

	}

	/**
	 * @param editorState
	 *            current state
	 * @param ch
	 *            single char
	 * @return whether it was handled
	 */
	public boolean handleChar(EditorState editorState, char ch) {
		boolean allowFrac = createFrac && !editorState.isInsideQuotes();
		// backspace, delete and escape are handled for key down
		if (ch == JavaKeyCodes.VK_BACK_SPACE || ch == JavaKeyCodes.VK_DELETE
				|| ch == JavaKeyCodes.VK_ESCAPE) {
			return true;
		}
		if (ch != '(' && ch != '{' && ch != '[' && ch != '/' && ch != '|'
				&& ch != Unicode.LFLOOR && ch != Unicode.LCEIL && ch != '"') {
			deleteSelection(editorState);
		}
		boolean handled = handleEndBlocks(editorState, ch);

		MetaModel meta = editorState.getMetaModel();
		if (!handled) {
			if (meta.isArrayCloseKey(ch)) {
				endField(editorState, ch);
				handled = true;
			} else if (meta.isFunctionOpenKey(ch)) {
				newBraces(editorState, ch);
				handled = true;
			} else if (allowFrac && ch == '^') {
				newScript(editorState, Tag.SUPERSCRIPT);
				handled = true;
			} else if (allowFrac && ch == '_') {
				newScript(editorState, Tag.SUBSCRIPT);
				handled = true;
			} else if (allowFrac && ch == '/') {
				newFunction(editorState, "frac", false, null);
				handled = true;
			} else if (ch == Unicode.SQUARE_ROOT) {
				newFunction(editorState, "sqrt", false, null);
				handled = true;
			} else if (ch == '|') {
				newFunction(editorState, "abs");
				handled = true;
			} else if (meta.isArrayOpenKey(ch)) {
				newArray(editorState, 1, ch, false);
				handled = true;
			} else if (ch == Unicode.MULTIPLY || ch == Unicode.CENTER_DOT
					|| ch == Unicode.BULLET) {
				newOperator(editorState, '*');
				handled = true;
			} else if (ch == ',' && allowFrac) {
				comma(editorState);
				handled = true;
			} else if (meta.isOperator("" + ch)) {
				newOperator(editorState, ch);
				handled = true;
			} else if (ch == 3 || ch == 22) {
				// invisible characters on MacOS
				handled = true;
			} else if (meta.isSymbol("" + ch)) {
				newSymbol(editorState, ch);
				handled = true;
			} else if (meta.isCharacter("" + ch)) {
				newCharacter(editorState, ch);
				handled = true;
			}
		}
		return handled;
	}

	private boolean handleEndBlocks(EditorState editorState, char ch) {
		MathContainer parent = editorState.getCurrentField().getParent();
		if (editorState.getSelectionStart() == null) {
			if (parent instanceof MathArray) {
				return handleEndMathArray((MathArray) parent, editorState, ch);
			} else if (parent instanceof MathFunction) {
				return handleEndMathFunction((MathFunction) parent, editorState, ch);
			}
		}
		return false;
	}

	private boolean handleEndMathArray(MathArray mathArray, EditorState editorState, char ch) {
		if (ch == '"' && mathArray.getCloseKey() == '"') {
			return handleExit(editorState, ch);
		}
		return false;
	}

	private boolean handleEndMathFunction(MathFunction mathFunction,
			EditorState editorState, char ch) {
		if (Tag.ABS.equals(mathFunction.getName()) && ch == '|') {
			MathSequence currentField = editorState.getCurrentField();
			int offset = editorState.getCurrentOffset();
			MathComponent prevArg = currentField.getArgument(offset - 1);

			// check for eg * + -
			boolean isOperation = prevArg != null && mathField.getMetaModel()
					.isOperator(prevArg + "");
			if (!isOperation) {
				return handleExit(editorState, ch);
			}
		}
		return false;
	}

	private boolean handleExit(EditorState editorState, char ch) {
		MathSequence currentField = editorState.getCurrentField();

		int offset = editorState.getCurrentOffset();

		MathComponent nextArg = currentField.getArgument(offset);

		if (nextArg == null) {
			endField(editorState, ch);
		}
		return nextArg == null;
	}

	private void comma(EditorState editorState) {
		if (trySelectNext(editorState)) {
			return;
		}

		newOperator(editorState, ',');

	}

	/**
	 * Select next argument in suggested command.
	 *
	 * @param editorState
	 *            current state
	 * @return success
	 */
	public static boolean trySelectNext(EditorState editorState) {
		int idx = editorState.getCurrentOffset();
		if (editorState.getSelectionEnd() != null) {
			idx = editorState.getSelectionEnd().getParentIndex() + 1;
		}
		MathSequence field = editorState.getCurrentField();
		if (field.getArgument(idx) instanceof MathCharacter
				&& ",".equals(field.getArgument(idx).toString())
				&& doSelectNext(field, editorState, idx + 1)) {
			return true;
		}
		return false;
	}

	/**
	 * Select first argument in suggested command.
	 *
	 * @param editorState
	 *            current state
	 * @return success
	 */
	public static boolean trySelectFirst(EditorState editorState) {
		int idx = editorState.getCurrentOffset();
		if (editorState.getSelectionEnd() != null) {
			idx = editorState.getSelectionEnd().getParentIndex() + 1;
		}

		MathSequence field = editorState.getCurrentField();
		if (idx == field.size() - 1 && doSelectNext(field, editorState, 0)) {
			return true;
		}
		return false;
	}

	/**
	 * @param args
	 *            text of the form &lt;arg1&gt;&lt;arg2&gt;
	 * @param state
	 *            current state
	 * @param offset
	 *            where to start looking
	 * @return whether successfully selected
	 */
	public static boolean doSelectNext(MathSequence args, EditorState state,
			int offset) {
		int endchar = -1;
		for (int i = offset + 1; i < args.size(); i++) {
			if (args.getArgument(i) instanceof MathCharacter
					&& ((MathCharacter) args.getArgument(i))
					.getUnicode() == '>') {
				endchar = i;
				if (i < args.size() - 1
						&& args.getArgument(i + 1) instanceof MathCharacter
						&& " ".equals(args.getArgument(i + 1).toString())) {
					endchar++;
				}
				break;
			}
		}
		if (endchar > 0) {
			state.setCurrentField(args);
			state.setSelectionStart(args.getArgument(offset));
			state.setSelectionEnd(args.getArgument(endchar));
			state.setCurrentOffset(endchar);
			return true;
		}
		return false;
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
	 *
	 * @param shiftDown
	 *            whether shift is pressed
	 */
	public void handleTab(boolean shiftDown) {
		if (mathField != null) {
			mathField.tab(shiftDown);
		}
	}

	/**
	 * @param editorState
	 *            current state
	 * @return selection as text
	 */
	public static MathSequence getSelectionText(EditorState editorState) {
		if (editorState.getSelectionStart() != null) {
			MathContainer parent = editorState.getSelectionStart().getParent();
			int end, start;
			if (parent == null) {
				// all the formula is selected
				return editorState.getRootComponent();

			}
			MathSequence seq = new MathSequence();
			end = parent.indexOf(editorState.getSelectionEnd());
			start = parent.indexOf(editorState.getSelectionStart());
			if (end >= 0 && start >= 0) {
				for (int i = start; i <= end; i++) {
					seq.addArgument(parent.getArgument(i).copy());
				}

				// editorState.setCurrentOffset(start);
			}
			return seq;
		}
		// editorState.resetSelection();
		return null;
	}

}
