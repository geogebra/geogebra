package com.himamis.retex.editor.share.serializer;

import com.himamis.retex.editor.share.editor.SyntaxAdapter;
import com.himamis.retex.editor.share.meta.Tag;
import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * Serializes internal format into TeX format.
 */
public class TeXSerializer extends SerializerAdapter {

	public static final String PLACEHOLDER =
			"{\\bgcolor{#DCDCDC}\\scalebox{1}[1.6]{\\phantom{g}}}";
	private static final String cursor = "\\jlmcursor{0}";
	private static final String cursorBig = "\\jlmcursor{0.9}";
	private static final String selection_start = "\\jlmselection{";
	private static final String selection_end = "}";

	private static final String PLACEHOLDER_INVISIBLE = "\\nbsp ";
	private boolean showPlaceholder = true;
	private boolean lineBreakEnabled = false;

	private static final String[] escapeableSymbols = { "%", "$", "#", "&", "{",
			"}", "_" };
	private static final String[][] replaceableSymbols = { { "~", "^", "\\" },
			{ "\u223C ", "\\^{\\ } ", "\\backslash{}" } };

	private SyntaxAdapter syntaxAdapter;

	public TeXSerializer() {
		// sometimes we don't want to pass a syntax adapter
	}

	public TeXSerializer(SyntaxAdapter syntaxAdapter) {
		this.syntaxAdapter = syntaxAdapter;
	}

	@Override
	public void serialize(MathCharacter mathCharacter,
			StringBuilder stringBuilder) {
		if (mathCharacter.getUnicode() == Unicode.ZERO_WIDTH_SPACE) {
			return;
		}
		// jmathtex v0.7: incompatibility
		if (mathCharacter == currentSelStart) {
			stringBuilder.append(selection_start);
		}
		if ("=".equals(mathCharacter.getName())) {
			stringBuilder.append("\\,=\\,");
		} else if ("@".equals(mathCharacter.getName())) {
			stringBuilder.append("\\@ ");
		} else if (" ".equals(mathCharacter.getName())) {
			stringBuilder.append("\\nbsp ");
		} else if (lineBreakEnabled && 10 == mathCharacter.getName().charAt(0)) {
			stringBuilder.append("\\\\\\vspace{0}");
		} else if ("n".equals(mathCharacter.getName()) && stringBuilder.length() > 0
				&& 'l' == stringBuilder.charAt(stringBuilder.length() - 1)) {
			stringBuilder.setLength((Math.max(stringBuilder.length() - 1, 0)));
			stringBuilder.append("\\mathrm{ln}");
		} else {
			String texName = mathCharacter.getTexName();
			if (isSymbolEscapeable(texName)) {
				// escape special symbols
				stringBuilder.append('\\');
				stringBuilder.append(texName);
			} else {
				stringBuilder.append(replaceSymbol(texName));
			}
		}
		if (mathCharacter == currentSelEnd) {
			stringBuilder.append(selection_end);
		}

		// now removed - instead use \cdot{} rather than \cdot
		// safety space after operator / symbol
		// if (mathCharacter.isOperator() || mathCharacter.isSymbol()) {
		// stringBuilder.append(' ');
		// }
	}

	/**
	 * Enables or disables line break in the editor.
	 * 
	 * @param lineBreaks
	 *            whether to allow linebreaks
	 */
	public void setLineBeakEnabled(boolean lineBreaks) {
		lineBreakEnabled = lineBreaks;
	}

	/**
	 * Enables or disables placeholder in the editor.
	 *
	 * @param placeholder
	 *            whether to show placeholders
	 */
	public void setPlaceholderEnabled(boolean placeholder) {
		showPlaceholder = placeholder;
	}

	@Override
	public void serialize(MathSequence sequence, StringBuilder stringBuilder) {
		if (sequence == null) {
			stringBuilder.append("?");
			return;
		}
		if (sequence.isScript(0) && (sequence != mCurrentField || mCurrentOffset > 0)) {
			stringBuilder.append(showPlaceholder ? PLACEHOLDER : PLACEHOLDER_INVISIBLE);
		}
		int lengthBefore = stringBuilder.length();
		boolean addBraces = (sequence.hasChildren() || // {a^b_c}
				sequence.size() > 1 || // {aa}
				(sequence.size() == 1 && letterLength(sequence, 0) > 1) || // {\pi}
				(sequence.size() == 0 && sequence != mCurrentField) || // {\triangleright}
				(sequence.size() == 1 && sequence == mCurrentField)) && // {a|}
				(stringBuilder.length() > 0 && stringBuilder
						.charAt(stringBuilder.length() - 1) != '{');
		if (sequence == currentSelStart) {
			stringBuilder.append(selection_start);
		}
		if (addBraces) {
			// when necessary add curly braces
			stringBuilder.append('{');
		}

		if (sequence.size() == 0) {
			if (sequence == mCurrentField) {
				if (currentSelStart == null) {
					stringBuilder.append(cursorBig);
				}
			} else {
				stringBuilder.append(getPlaceholder(sequence));
			}
		} else {
			if (sequence == mCurrentField) {

				if (mCurrentOffset > 0) {
					serialize(sequence, stringBuilder, 0, mCurrentOffset);
				}
				if (currentSelStart == null) {

					stringBuilder.append(cursor);

				}
				if (mCurrentOffset < sequence.size()) {
					serialize(sequence, stringBuilder, mCurrentOffset,
							sequence.size());
				}
				boolean emptyFormula = stringBuilder
						.substring(lengthBefore, stringBuilder.length()).trim()
						.replace("\\nbsp", "").replace(cursor, "").isEmpty();
				if (emptyFormula) {
					String cursorFix = stringBuilder.toString().replace(cursor,
							cursorBig);
					stringBuilder.setLength(0);
					stringBuilder.append(cursorFix);
				}
			} else {
				serialize(sequence, stringBuilder, 0, sequence.size());
			}
		}

		if (addBraces) {
			// when necessary add curly braces
			stringBuilder.append('}');
		}
		if (sequence == currentSelEnd) {
			stringBuilder.append(selection_end);
		}
	}

	private String getPlaceholder(MathSequence sequence) {
		MathContainer parent = sequence.getParent();
		if (parent == null
				|| (parent instanceof MathArray	&& parent.size() == 1)) {
			return PLACEHOLDER_INVISIBLE;
		}
		if (parent instanceof MathFunction) {
			Tag fn = ((MathFunction) parent).getName();
			if (fn == Tag.APPLY || fn == Tag.LOG) {
				return PLACEHOLDER_INVISIBLE;
			}
		}
		return showPlaceholder ? PLACEHOLDER : PLACEHOLDER_INVISIBLE;
	}

	@Override
	public void serialize(MathFunction function, StringBuilder stringBuilder) {
		if (function == currentSelStart) {
			stringBuilder.append(selection_start);
		}
		switch (function.getName()) {

		case SUPERSCRIPT:
			appendIndex(stringBuilder, function, "^");
			break;
		case SUBSCRIPT:
			appendIndex(stringBuilder, function, "_");
			break;

		case FRAC:
			stringBuilder.append("{");
			stringBuilder.append(function.getTexName());
			stringBuilder.append("{");
			serialize(function.getArgument(0), stringBuilder);
			stringBuilder.append("}{");
			serialize(function.getArgument(1), stringBuilder);
			stringBuilder.append("}}");
			break;
		case SQRT:
		case CBRT:
			stringBuilder.append(function.getTexName());
			stringBuilder.append("{");
			serialize(function.getArgument(0), stringBuilder);
			stringBuilder.append("}");
			break;
		case NROOT:
			stringBuilder.append(function.getTexName());
			stringBuilder.append('[');
			serialize(function.getArgument(0), stringBuilder);
			stringBuilder.append("]{");
			serialize(function.getArgument(1), stringBuilder);
			stringBuilder.append('}');
			break;
		case LOG:
			stringBuilder.append(function.getTexName());
			// only proint log base if nonempty or contains cursor
			if (function.getArgument(0).size() > 0
					|| mCurrentField == function.getArgument(0)) {
				stringBuilder.append("_{");
				serialize(function.getArgument(0), stringBuilder);
				stringBuilder.append('}');
			}
			stringBuilder.append("\\left(");
			serialize(function.getArgument(1), stringBuilder);
			stringBuilder.append("\\right)");
			break;
		case SUM_EQ:
		case PROD_EQ:
		case DEF_INT:
			stringBuilder.append(function.getTexName());
			stringBuilder.append('_');
			serialize(function.getArgument(0), stringBuilder);
			stringBuilder.append('^');
			serialize(function.getArgument(1), stringBuilder);
			stringBuilder.append("{}");
			break;
		case LIM_EQ:
			stringBuilder.append("\\lim_{");
			serialize(function.getArgument(0), stringBuilder);
			stringBuilder.append("} ");
			break;
		case ABS:
			stringBuilder.append("\\left|");
			serialize(function.getArgument(0), stringBuilder);
			stringBuilder.append("\\right|");
			break;
		case FLOOR:
			stringBuilder.append("\\left\\lfloor ");
			serialize(function.getArgument(0), stringBuilder);
			stringBuilder.append("\\right\\rfloor ");
			break;
		case CEIL:
			stringBuilder.append("\\left\\lceil ");
			serialize(function.getArgument(0), stringBuilder);
			stringBuilder.append("\\right\\rceil ");
			break;
		case APPLY:
		case APPLY_SQUARE:
			StringBuilder functionName = new StringBuilder();
			serialize(function.getArgument(0), functionName);

			stringBuilder.append("{");
			if (isFunction(functionName.toString())) {
				stringBuilder.append("{\\mathrm{").append(functionName).append("}}");
			} else {
				stringBuilder.append(functionName);
			}

			serializeArguments(stringBuilder, function, 1);
			break;
		case VEC:
			stringBuilder.append("\\overrightarrow{");
			serialize(function.getArgument(0), stringBuilder);
			stringBuilder.append("}");
			break;
		default:
			stringBuilder.append("{\\mathrm{");
			stringBuilder.append(function.getTexName());
			stringBuilder.append("}");
			serializeArguments(stringBuilder, function, 0);

		}
		if (function == currentSelEnd) {
			stringBuilder.append(selection_end);
		}
	}

	/**
	 * @param name function name
	 * @return whether this is a builtin function (sin/cos/...)
	 */
	public boolean isFunction(String name) {
		if (syntaxAdapter == null) {
			return true;
		}

		String trimmed = name.replace(cursor, "");
		if (trimmed.indexOf('^') > 0) {
			trimmed = trimmed.substring(0, trimmed.indexOf('^'));
		}

		return syntaxAdapter.isFunction(trimmed);
	}

	private void serializeArguments(StringBuilder stringBuilder,
			MathFunction function, int offset) {
		stringBuilder.append("\\left");
		stringBuilder.append(function.getOpeningBracket());
		for (int i = offset; i < function.size(); i++) {
			serialize(function.getArgument(i), stringBuilder);
			if (i + 1 < function.size()) {
				stringBuilder.append(",");
			}
		}
		stringBuilder.append("\\right");
		stringBuilder.append(function.getClosingBracket());
		stringBuilder.append("}");
	}

	private void appendIndex(StringBuilder stringBuilder, MathFunction function,
			String idxType) {
		MathSequence parent = function.getParent();
		int index = function.getParentIndex();
		if (index == 0
				|| (index > 0
						&& parent
								.getArgument(index - 1) instanceof MathCharacter
						&& ((MathCharacter) parent.getArgument(index - 1))
								.isOperator())) {
			stringBuilder.append(PLACEHOLDER_INVISIBLE);
		}
		stringBuilder.append(idxType);
		stringBuilder.append('{');
		serialize(function.getArgument(0), stringBuilder);
		stringBuilder.append('}');

	}

	@Override
	public void serialize(MathArray array, StringBuilder stringBuilder) {
		if (this.currentSelStart == array) {
			stringBuilder.append(TeXSerializer.selection_start);
		}
		stringBuilder.append(array.getOpen().getTexName());
		for (int i = 0; i < array.rows(); i++) {
			for (int j = 0; j < array.columns(); j++) {
				serialize(array.getArgument(i, j), stringBuilder);
				if (j + 1 < array.columns()) {
					stringBuilder.append(array.getField().getTexName());
				} else if (i + 1 < array.rows()) {
					stringBuilder.append(array.getRow().getTexName());
				}
			}
		}
		stringBuilder.append(array.getClose().getTexName());
		if (this.currentSelEnd == array) {
			stringBuilder.append(TeXSerializer.selection_end);
		}
	}

	private static int letterLength(MathSequence symbol, int i) {
		if (symbol.getArgument(i) instanceof MathCharacter) {
			return ((MathCharacter) symbol.getArgument(i)).getTexName()
					.length();
		}
		return 2;
	}

	/**
	 * @param ms
	 *            sequence
	 * @return TeX representation of the sequence
	 */
	public static String serialize(MathSequence ms) {
		StringBuilder b = new StringBuilder();
		new TeXSerializer().serialize(ms, b);
		return b.toString();
	}

	private static boolean isSymbolEscapeable(String symbol) {
		for (String escapeableSymbol : escapeableSymbols) {
			if (escapeableSymbol.equals(symbol)) {
				return true;
			}
		}

		return false;
	}

	private static String replaceSymbol(String symbol) {
		for (int i = 0; i < replaceableSymbols[0].length; i++) {
			if (replaceableSymbols[0][i].equals(symbol)) {
				return replaceableSymbols[1][i];
			}
		}
		return symbol;
	}

	public void setSyntaxAdapter(SyntaxAdapter syntaxAdapter) {
		this.syntaxAdapter = syntaxAdapter;
	}
}
