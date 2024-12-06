package com.himamis.retex.editor.share.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.j2objc.annotations.Weak;
import com.himamis.retex.editor.share.editor.MathField;
import com.himamis.retex.editor.share.editor.SyntaxAdapter;
import com.himamis.retex.editor.share.meta.MetaArray;
import com.himamis.retex.editor.share.meta.MetaCharacter;
import com.himamis.retex.editor.share.meta.MetaFunction;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.meta.Tag;
import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathCharPlaceholder;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathPlaceholder;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.share.util.JavaKeyCodes;
import com.himamis.retex.editor.share.util.Unicode;

public class InputController {

	public static final char FUNCTION_OPEN_KEY = '('; // probably universal
	public static final char FUNCTION_CLOSE_KEY = ')';
	public static final char DELIMITER_KEY = ';';
	private static final String[] SUFFIX_REPLACEABLE_FUNCTIONS = {"abs", "sqrt"};
	private static final List<Character> ignoreChars = Arrays.asList('{', '}');

	private final MetaModel metaModel;

	@Weak
	private MathField mathField;

	private boolean plainTextMode = false;
	private SyntaxAdapter formatConverter;
	private boolean useSimpleScripts = true;
	private boolean allowAbs = true;

	/**
	 * @param metaModel model
	 */
	public InputController(MetaModel metaModel) {
		this.metaModel = metaModel;
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

	public void setFormatConverter(SyntaxAdapter formatConverter) {
		this.formatConverter = formatConverter;
	}

	public void setUseSimpleScripts(boolean useSimpleScripts) {
		this.useSimpleScripts = useSimpleScripts;
	}

	public void setAllowAbs(boolean allowAbs) {
		this.allowAbs = allowAbs;
	}

	static private String getLetter(MathComponent component)
			throws Exception {
		if (!(component instanceof MathCharacter)) {
			throw new Exception("Math component is not a character");
		}

		MathCharacter mathCharacter = (MathCharacter) component;
		if (!mathCharacter.isCharacter() || !mathCharacter.isLetter()) {
			throw new Exception("Math component is not a character");
		}

		return mathCharacter.getUnicodeString();
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
		for (int index = currentPosition; index > 0; index --) {
			MathComponent component = currentField.getArgument(index - 1);
			if (component instanceof MathCharacter) {
				MathCharacter character = (MathCharacter) component;
				if (character.isUnicode('=') || character.isFieldSeparator()) {
					return index;
				}
			}
		}

		return 0;
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

		FunctionPower power = getFunctionPower(editorState);

		newBraces(editorState, power, ch);
	}

	private FunctionPower getFunctionPower(EditorState editorState) {
		FunctionPower power = new FunctionPower();
		int initialOffset = editorState.getCurrentOffsetOrSelection();
		MathComponent last = editorState.getCurrentField()
				.getArgument(initialOffset - 1);
		power.script = MathFunction.isScript(last) ? (MathFunction) last
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
		for (String suffix: SUFFIX_REPLACEABLE_FUNCTIONS) {
			if (name.endsWith(suffix)) {
				name = suffix;
			}
		}
		Tag tag = Tag.lookup(name);

		if (ch == FUNCTION_OPEN_KEY && tag != null) {
			if (power.script != null) {
				deleteSingleArg(editorState);
			}
			delCharacters(editorState, name.length());
			newFunction(editorState, name, false,
					power.script);
		} else if ((ch == FUNCTION_OPEN_KEY || ch == '[')
				&& metaModel.isFunction(name)) {
			if (power.script != null) {
				deleteSingleArg(editorState);
			}
			delCharacters(editorState, name.length());
			newFunction(editorState, name, ch == '[', power.script);

		} else {
			int index = editorState.getCurrentOffsetOrSelection();
			MathComponent firstSelection = editorState.getCurrentField().getArgument(index);

			if (firstSelection instanceof MathPlaceholder) {
				editorState.getCurrentField().removeArgument(index);
				MetaArray meta = metaModel.getArray(ch);
				MathArray array = new MathArray(meta, 1);
				MathSequence seq = new MathSequence();
				array.setArgument(0, seq);
				editorState.getCurrentField().addArgument(index, array);
				editorState.setSelectionStart(null);
				editorState.setCurrentField(seq);
				editorState.setCurrentOffset(0);
				return;
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
						|| Tag.CBRT.equals(function.getName())
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
		initArguments(function, offset);

		// pass characters for fraction, factorial, and mixed number only
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

	private void initArguments(MathFunction function, int offset) {
		for (int i = offset; i < function.size(); i++) {
			MathSequence field = new MathSequence();
			function.setArgument(i, field);
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
			nameS.append(
					metaModel.getCharacter(name.charAt(i) + ""));
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
		int currentOffset = editorState.getCurrentOffset();
		MathComponent last = editorState.getCurrentField()
				.getArgument(currentOffset - 1);
		StringBuilder suffix = new StringBuilder(meta.getUnicodeString());

		while (last instanceof MathCharacter) {
			suffix.append(last);
			if (!metaModel.isReverseSuffix(suffix.toString())) {
				suffix.setLength(suffix.length() - 1);
				break;
			}
			currentOffset--;
			last = editorState.getCurrentField()
					.getArgument(currentOffset - 1);
		}

		MetaCharacter merge = metaModel.merge(suffix.reverse().toString());
		if (merge != null) {
			for (int i = 0; i < suffix.length() - 1; i++) {
				editorState.getCurrentField().delArgument(currentOffset);
			}
			editorState.setCurrentOffset(currentOffset);
			editorState.addArgument(merge);
			return;
		}
		if (formatConverter != null) {
			FunctionPower function = getFunctionPower(editorState);
			char unicode = meta.getUnicode();
			if (unicode == ' ' && formatConverter.isFunction(function.name)) {
				newBraces(editorState, function, '(');
				return;
			}

			if (metaModel.isForceBracketAfterFunction()
					&& shouldAddBrackets(function, unicode)) {
				newBraces(editorState, function, '(');

				if (unicode == ' ') {
					return;
				}
			}
		}

		editorState.addArgument(meta);
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
			if (formatConverter.isFunction(name.substring(start))) {
				return true;
			}

			start--;
		}

		return false;
	}

	public String convert(String exp) {
		return formatConverter.convert(exp);
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
		if (RemoveContainer.isParentAnArray(currentField)) {
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
			} else if (ch == parent.getCloseKey() && !MathArray.isLocked(parent)) {
				// in non-protected containers when the closing key is pressed
				// move out of the container
				moveOutOfArray(currentField, currentOffset);
				currentOffset = parent.getParentIndex() + 1;
				currentField = (MathSequence) parent.getParent();
			} else {
				// else just create a new array for the given closing key
				MetaArray array = metaModel.getArrayByCloseKey(ch);
				if (array != null) {
					newArray(editorState, 1, array.getOpenKey(), true);
					return;
				}
			}

			// now functions, braces, apostrophes ...
		} else if (currentField.getParent() != null) {
			MathContainer parent = currentField.getParent();

			if (currentOffset == currentField.size()
					&& parent instanceof MathFunction
					&& ch == ((MathFunction) parent).getClosingBracket()
					&& parent.size() == currentField.getParentIndex() + 1) {

				currentOffset = parent.getParentIndex() + 1;
				currentField = (MathSequence) parent.getParent();

				// if ')' typed at the end of last field of braces ... move
				// after closing character
			} else if (parent instanceof MathFunction
					&& ch == ((MathFunction) parent).getClosingBracket()
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
				currentField = (MathSequence) parent.getParent();
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
		editorState.setCurrentField(currentField);
		editorState.setCurrentOffset(currentOffset);
	}

	private void checkReplaceAbs(MathContainer abs, MathSequence parent) {
		if (abs.getArgument(0) instanceof MathSequence
				&& ((MathSequence) abs.getArgument(0)).size() == 0) {
			int parentIndex = abs.getParentIndex();
			parent.removeArgument(parentIndex);
			MetaCharacter operator = metaModel.getOperator(Unicode.OR + "");
			parent.addArgument(parentIndex, new MathCharacter(operator));
		}
	}

	private void moveOutOfArray(MathSequence currentField, int currentOffset) {
		MathComponent parent = currentField.getParent();
		if (parent.getParent() instanceof MathSequence) {
			int counter = 1;
			while (currentField.size() > currentOffset) {
				MathComponent component = currentField
						.getArgument(currentOffset);
				currentField.delArgument(currentOffset);
				parent.getParent().addArgument(parent.getParentIndex() + counter, component);
				counter++;
			}
		}
	}

	private static void insertReverse(MathContainer parent, int parentIndex,
			ArrayList<MathComponent> removed) {
		for (int j = removed.size() - 1; j >= 0; j--) {
			MathComponent o = removed.get(j);
			int idx = parentIndex + (removed.size() - j);
			parent.addArgument(idx, o);
		}

	}

	private static ArrayList<MathComponent> cut(MathContainer currentField,
			int from, int to, EditorState st, MathComponent array,
			boolean rec) {

		int end = to < 0 ? endToken(from, currentField) : to;
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
		return currentField.replaceArguments(start, end, array);
	}

	private static int endToken(int from, MathContainer currentField) {
		for (int i = from; i < currentField.size(); i++) {
			if (currentField.isFieldSeparator(i)) {
				return i - 1;
			}
		}
		return currentField.size() - 1;
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
	 * Remove character left to the cursor
	 *
	 * @param editorState
	 *            current state
	 */
	public void bkspCharacter(EditorState editorState) {
		int currentOffset = editorState.getCurrentOffsetOrSelection();
		if (currentOffset > 0) {
			MathComponent prev = editorState.getCurrentField()
					.getArgument(currentOffset - 1);
			if (prev instanceof MathArray) {
				MathArray parent = (MathArray) prev;
				extendBrackets(parent, editorState);
			} if (prev instanceof MathFunction) {
				bkspLastFunctionArg((MathFunction) prev, editorState);
			} else {
				deleteSingleArg(editorState);
			}
		} else {
			RemoveContainer.withBackspace(editorState);
		}
	}

	private void bkspLastFunctionArg(MathFunction function, EditorState editorState) {
		MathSequence functionArg = function.getArgument(function.size() - 1);
		if (function.getName() == Tag.APPLY || function.getName() == Tag.APPLY_SQUARE) {
			moveArgumentsAfter(function, editorState, function.getArgument(1));
			bkspCharacter(editorState);
		} else if (isEqFunctionWithPlaceholders(function)) {
			deleteSingleArg(editorState);
		} else if (functionArg != null) {
			editorState.setCurrentField(functionArg);
			functionArg.delArgument(functionArg.size() - 1);
			editorState.setCurrentOffset(functionArg.size());
		} else {
			deleteSingleArg(editorState);
		}
	}

	private boolean isEqFunctionWithPlaceholders(MathFunction function) {
		return function.getName() == Tag.DEF_INT || function.getName() == Tag.SUM_EQ
				|| function.getName() == Tag.PROD_EQ || function.getName() == Tag.LIM_EQ
				|| function.getName() == Tag.ATOMIC_POST || function.getName() == Tag.ATOMIC_PRE
				|| function.getName() == Tag.RECURRING_DECIMAL
				|| function.getName() == Tag.POINT || function.getName() == Tag.POINT_AT;
	}

	private void deleteSingleArg(EditorState editorState) {
		int currentOffset = editorState.getCurrentOffsetOrSelection();
		MathSequence currentField = editorState.getCurrentField();
		if (currentField.isArgumentProtected(currentOffset - 1)) {
			return;
		}

		currentField.delArgument(currentOffset - 1);
		editorState.decCurrentOffset();
		onDelete(editorState, currentField);
	}

	private void onDelete(EditorState editorState, MathSequence currentField) {
		int currentOffset = editorState.getCurrentOffset();
		MathComponent component = currentField.getArgument(currentOffset);
		if (isFieldSeparatorInSequence(currentField) && isFieldSeparatorOrNull(component)) {
			addPlaceholderIfNeeded(currentField, currentOffset);
		}

		if (component instanceof MathFunction) {
			RemoveContainer.fuseMathFunction(editorState, (MathFunction) component);
		}
	}

	static boolean isFieldSeparatorInSequence(MathSequence sequence) {
		for (MathComponent component: sequence) {
			if (component.isFieldSeparator()) {
				return true;
			}
		}

		return false;
	}

	private boolean isFieldSeparatorOrNull(MathComponent component) {
		return component == null || component.isFieldSeparator();
	}

	private void addPlaceholderIfNeeded(MathSequence currentField, int offset) {
		if (isFieldSeparatorOrNull(currentField.getArgument(offset - 1))) {
			currentField.addArgument(offset, new MathCharPlaceholder());
		}
	}

	private static void extendBrackets(MathArray array, EditorState state) {
		moveArgumentsAfter(array, state, array.getArgument(array.size() - 1));
	}

	private static void moveArgumentsAfter(MathComponent lastToKeep,
			EditorState editorState, MathSequence target) {
		int currentOffset = lastToKeep.getParentIndex() + 1;
		MathContainer currentField = lastToKeep.getParent();
		int oldSize = target.size();
		while (currentField.size() > currentOffset
				&& !currentField.isArgumentProtected(currentOffset)) {
			MathComponent component = currentField.getArgument(currentOffset);
			currentField.delArgument(currentOffset);
			target.addArgument(target.size(), component);
		}
		editorState.setCurrentField(target);
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
			if (RemoveContainer.isParentAnArray(currentField)) {
				extendBrackets((MathArray) currentField.getParent(),
						editorState);
			} else {
				RemoveContainer.deleteContainer(editorState);
			}
		}
	}

	private static void delCharacters(EditorState editorState, int length0) {
		int currentOffset = editorState.getCurrentOffsetOrSelection();
		MathSequence currentField = editorState.getCurrentField();
		int length = length0;
		while (length > 0 && currentOffset > 0 && currentField
				.getArgument(currentOffset - 1) instanceof MathCharacter) {

			MathCharacter character = (MathCharacter) currentField
					.getArgument(currentOffset - 1);
			if (character.isOperator() || (character.isSymbol()
					&& !character.isLetter())) {
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
				RemoveContainer.withBackspace(editorState);
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

			if (isProtected(editorState.getSelectionStart())) {
				return true;
			}

			if (MathArray.isLocked(parent)) {
				deleteMatrixElementValue(editorState);
				return true;
			}

			int end, start;
			if (parent == null) {
				// all the formula is selected
				parent = editorState.getRootComponent();
				start = 0;
				end = parent.size() - 1;
			} else {
				start = parent.indexOf(editorState.getSelectionStart());
				end = parent.indexOf(editorState.getSelectionEnd());
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

	private static void deleteMatrixElementValue(EditorState editorState) {
		MathSequence matrixElement = (MathSequence) editorState.getSelectionAnchor().getParent();
		matrixElement.clearArguments();
		editorState.setCurrentOffset(0);
		editorState.setCurrentField(matrixElement);
		editorState.resetSelection();
	}

	/**
	 *
	 * @param component to check.
	 * @return if it is protected.
	 */
	public static boolean isProtected(MathComponent component) {
		if (!(component instanceof MathContainer)) {
			return false;
		}
		return ((MathContainer) component).isProtected();
	}

	/**
	 * @param editorState
	 *            current state
	 * @param ch
	 *            single char
	 * @return whether it was handled
	 */
	public boolean handleChar(EditorState editorState, char ch) {
		// backspace, delete and escape are handled for key down
		if (ch == JavaKeyCodes.VK_BACK_SPACE || ch == JavaKeyCodes.VK_DELETE
				|| ch == JavaKeyCodes.VK_ESCAPE) {
			return true;
		}

		if  (shouldCharBeIgnored(editorState, ch)) {
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

		MetaModel meta = editorState.getMetaModel();

		if (!meta.isFunctionOpenKey(ch) && ch != ',') {
			int currentOffset = editorState.getCurrentOffset();
			MathSequence field = editorState.getCurrentField();

			if (field.getArgument(currentOffset) instanceof MathPlaceholder) {
				editorState.getCurrentField().removeArgument(currentOffset);
			} else if (field.getArgument(currentOffset - 1) instanceof MathPlaceholder) {
				editorState.getCurrentField().removeArgument(currentOffset - 1);
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
			if (meta.isArrayCloseKey(ch)) {
				endField(editorState, ch);
				handled = true;
			} else if (meta.isFunctionOpenKey(ch)) {
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
			} else if (meta.isArrayOpenKey(ch)) {
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
			} else if (ch == Unicode.OVERLINE) {
				newFunction(editorState, "recurringDecimal", false, null);
				handled = true;
			}
		}
		return handled;
	}

	private boolean shouldCharBeIgnored(EditorState editorState, char ch) {
		MathSequence root = editorState.getRootComponent();
		return (root.isProtected() || root.isKeepCommas())
				&& !plainTextMode && ignoreChars.contains(ch);
	}

	private void handleTextModeInsert(EditorState editorState, char ch) {
		deleteSelection(editorState);

		char toInsert = ch;
		if (toInsert == '\"') {
			toInsert = getNextQuote(editorState.getCurrentField(),
					editorState.getCurrentOffset());
		}

		MetaCharacter meta = metaModel.getCharacter("" + toInsert);
		editorState.addArgument(meta);
	}

	private char getNextQuote(MathSequence currentField, int currentOffset) {
		for (int i = currentOffset - 1; i >= 0; i--) {
			MathComponent argument = currentField.getArgument(i);
			if (argument instanceof MathCharacter) {
				MathCharacter ch = (MathCharacter) argument;

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
		MathSequence currentField = state.getCurrentField();
		if (currentField.getParent() != null && currentField.getParent().hasTag(subscript)) {
			CursorController.nextCharacter(state);
		}
	}

	private boolean preventDimensionChange(EditorState editorState) {
		MathContainer parent = editorState.getCurrentField().getParent();
		return MathArray.isLocked(parent) && (((MathArray) parent).getOpenKey() == '('
				|| ((MathArray) parent).getOpenKey() == '{');
	}

	private boolean shouldMoveCursor(EditorState editorState) {
		int offset = editorState.getCurrentOffset();
		MathComponent next = editorState.getCurrentField().getArgument(offset);
		return next != null && next.isFieldSeparator();
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
		if (Tag.ABS.equals(mathFunction.getName()) && isAbsDelimiter(ch)) {
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
		int offset = editorState.getCurrentOffset();
		MathSequence currentField = editorState.getCurrentField();
		if (currentField.getArgument(offset) != null
				&& currentField.getArgument(offset).isFieldSeparator()) {
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
	 *
	 * @param shiftDown
	 *            whether shift is pressed
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
	public void mixedNumber(EditorState state) {
		MetaFunction meta = metaModel.getGeneral(Tag.FRAC);
		MathFunction function = new MathFunction(meta);
		function.setPreventingNestedFractions(true);
		initArguments(function, 0);
		MathComponent prev = state.getCurrentField().getArgument(state.getCurrentOffset() - 1);
		state.getCurrentField().addArgument(state.getCurrentOffset(), function);
		if (prev instanceof MathCharacter && ((MathCharacter) prev).isDigit()) {
			state.setCurrentField(function.getArgument(0));
			state.setCurrentOffset(0);
		}
	}

	public static class FunctionPower {
		/** subscript or superscript*/
		public MathFunction script;
		public String name;
	}

	private boolean isAbsDelimiter(char ch) {
		return allowAbs && ch == '|';
	}
}
