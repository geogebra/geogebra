package com.himamis.retex.editor.share.serializer;

import com.himamis.retex.editor.share.io.latex.ParseException;
import com.himamis.retex.editor.share.io.latex.Parser;
import com.himamis.retex.editor.share.meta.Tag;
import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathCharPlaceholder;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.share.util.Unicode;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

/**
 * Serializes internal formulas representation into GeoGebra string
 *
 */
public class GeoGebraSerializer extends SerializerAdapter {

	private static final GeoGebraSerializer
			defaultSerializer = new GeoGebraSerializer();

	private String leftBracket = "[";
	private String rightBracket = "]";
	private String comma = ",";
	private boolean showPlaceholderAsQuestionmark;

	/**
	 * @param c
	 *            math formula fragment
	 * @return string
	 */
	public static String serialize(MathComponent c) {
		StringBuilder sb = new StringBuilder();
		defaultSerializer.serialize(c, sb);
		return sb.toString();
	}

	@Override
	void serialize(MathCharacter mathCharacter,
			StringBuilder stringBuilder) {
		char unicode = mathCharacter.getUnicode();
		if (unicode == ',' && !isCommaNeeded(mathCharacter)) {
			stringBuilder.append(comma);
		} else {
			stringBuilder.append(mathCharacter.getUnicodeString());
		}
	}

	private boolean isCommaNeeded(MathCharacter token) {
		MathContainer parent = token.getParent();
		int index = token.getParentIndex();
		while (parent != null) {
			if (parent instanceof MathArray) {
				char openKey = ((MathArray) parent).getOpen().getKey();
				return openKey == '(' || openKey == '{';
			}
			if (parent instanceof MathFunction) {
				return (((MathFunction) parent).getName() == Tag.APPLY
						|| ((MathFunction) parent).getName() == Tag.APPLY_SQUARE) && index == 1;
			}
			if (parent instanceof MathSequence && ((MathSequence) parent).isKeepCommas()) {
				return true;
			}
			index = parent.getParentIndex();
			parent = parent.getParent();
		}
		return false;
	}

	@Override
	void serialize(MathFunction mathFunction,
			StringBuilder stringBuilder) {
		Tag mathFunctionName = mathFunction.getName();
		switch (mathFunctionName) {
		case SUPERSCRIPT:
		case SUBSCRIPT:
			StringBuilder scriptArgument = new StringBuilder();
			serialize(mathFunction.getArgument(0), scriptArgument);
			String trimmed = scriptArgument.toString().trim();

			if (!trimmed.isEmpty()) {
				if (mathFunctionName == Tag.SUPERSCRIPT) {
					stringBuilder.append("^(").append(trimmed).append(")");
				} else {
					stringBuilder.append("_{").append(trimmed).append("}");
				}
			}
			break;
		case FRAC:
			if (buildMixedNumber(stringBuilder, mathFunction)) {
				break;
			}
			stringBuilder.append("((");
			serialize(mathFunction.getArgument(0), stringBuilder);
			stringBuilder.append(")/(");
			serialize(mathFunction.getArgument(1), stringBuilder);
			stringBuilder.append("))");
			break;
		case RECURRING_DECIMAL:
			int i = stringBuilder.length() + 1;
			serialize(mathFunction.getArgument(0), stringBuilder);
			while (i < stringBuilder.length()) {
				stringBuilder.insert(i, Unicode.OVERLINE);
				i += 2;
			}
			if (mathFunction.getArgument(0).size() != 0) {
				stringBuilder.append(Unicode.OVERLINE);
			}
			break;
		case LOG:
			if (mathFunction.getArgument(0).size() == 0) {
				appendSingleArg("log", mathFunction, stringBuilder, 1);
				break;
			}
			generalFunction(mathFunction, stringBuilder);
			break;
		case NROOT:
			if (mathFunction.getArgument(0).size() == 0) {
				appendSingleArg("sqrt", mathFunction, stringBuilder, 1);
				break;
			}
			maybeInsertTimes(mathFunction, stringBuilder);
			stringBuilder.append("nroot(");
			serialize(mathFunction.getArgument(1), stringBuilder);
			stringBuilder.append(",");
			serialize(mathFunction.getArgument(0), stringBuilder);
			stringBuilder.append(')');
			break;
		case APPLY:
		case APPLY_SQUARE:
			maybeInsertTimes(mathFunction, stringBuilder);
			serialize(mathFunction.getArgument(0), stringBuilder);
			serializeArgs(mathFunction, stringBuilder, 1);
			break;
		case DEF_INT:
		case SUM_EQ:
		case PROD_EQ:
		case LIM_EQ:
		case VEC:
		case ATOMIC_POST:
		case ATOMIC_PRE:
			stringBuilder.append(mathFunction.getName().getFunction());
			serializeArgs(mathFunction, stringBuilder, 0);
			break;
		case POINT:
		case POINT_AT:
			serializeArgs(mathFunction, stringBuilder, 0);
			break;
		case ABS: // no special handling for || so that invalid input saving works
		default:
			generalFunction(mathFunction, stringBuilder);
		}
	}

	private void appendSingleArg(String name, MathFunction mathFunction,
			StringBuilder stringBuilder, int i) {
		maybeInsertTimes(mathFunction, stringBuilder);
		stringBuilder.append(name);
		stringBuilder.append("(");
		serialize(mathFunction.getArgument(i), stringBuilder);
		stringBuilder.append(')');
	}

	private void generalFunction(MathFunction mathFunction,
			StringBuilder stringBuilder) {
		maybeInsertTimes(mathFunction, stringBuilder);
		stringBuilder.append(mathFunction.getName().getFunction());
		serializeArgs(mathFunction, stringBuilder, 0);
	}

	private void serializeArgs(MathFunction mathFunction,
			StringBuilder stringBuilder, int offset) {
		stringBuilder.append(mathFunction.getOpeningBracket());
		for (int i = offset; i < mathFunction.size(); i++) {
			serialize(mathFunction.getArgument(i), stringBuilder);
			stringBuilder.append(',');
		}
		if (mathFunction.size() > offset) {
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		}
		stringBuilder.append(mathFunction.getClosingBracket());
	}

	private static void maybeInsertTimes(MathFunction mathFunction,
			StringBuilder stringBuilder) {
		MathSequence mathSequence = mathFunction.getParent();
		if (mathSequence != null && mathFunction.getParentIndex() > 0) {
			MathComponent mathComponent = mathSequence
					.getArgument(mathFunction.getParentIndex() - 1);
			if (mathComponent instanceof MathCharacter) {
				MathCharacter mathCharacter = (MathCharacter) mathComponent;
				if (!mathCharacter.isWordBreak()) {
					stringBuilder.append(" ");
				}
			}
			if (mathComponent != null && mathComponent.hasTag(Tag.SUBSCRIPT)) {
				stringBuilder.append(" ");
			}
		}
	}

	@Override
	void serialize(MathArray mathArray,
			StringBuilder stringBuilder) {
		char openKey = mathArray.getOpenKey();
		String open;
		String close;
		String field = mathArray.getFieldKey() + "";
		String row = mathArray.getRow().getKey() + "";
		if (Unicode.LFLOOR == openKey) {
			open = "floor(";
			close = ")";
		} else if (Unicode.LCEIL == openKey) {
			open = "ceil(";
			close = ")";
		} else if ('[' == openKey) {
			open = leftBracket;
			close = rightBracket;
		} else {
			open = openKey + "";
			close = mathArray.getClose().getKey() + "";
		}
		if (mathArray.isMatrix()) {
			stringBuilder.append(open);
		}
		for (int i = 0; i < mathArray.rows(); i++) {
			stringBuilder.append(open);
			for (int j = 0; j < mathArray.columns(); j++) {
				serialize(mathArray.getArgument(i, j), stringBuilder);
				stringBuilder.append(field);
			}
			stringBuilder.deleteCharAt(stringBuilder.length() - field.length());
			stringBuilder.append(close);
			stringBuilder.append(row);
		}
		stringBuilder.deleteCharAt(stringBuilder.length() - row.length());
		if (mathArray.isMatrix()) {
			stringBuilder.append(close);
		}
	}

	@Override
	void serialize(MathCharPlaceholder placeholder, StringBuilder sb) {
		if (showPlaceholderAsQuestionmark) {
			sb.append('?');
		}
	}

	@Override
	void serialize(MathSequence mathSequence,
			StringBuilder stringBuilder) {
		if (mathSequence == null) {
			return;
		}
		// print empty fraction as ?/?, but ignore empty scripts and allow sin()
		if (mathSequence.size() == 0 && showPlaceholderAsQuestionmark
				&& (isMatrixEntry(mathSequence) || mathSequence.getParent() == null)) {
			stringBuilder.append('?');
		}
		for (MathComponent arg: mathSequence) {
			serialize(arg, stringBuilder);
		}
	}

	private boolean isMatrixEntry(MathSequence mathSequence) {
		return mathSequence.getParent() instanceof MathArray
				&& mathSequence.getParent().size() > 1;
	}

	/**
	 * @param formula
	 *            original formula
	 * @return formula after stringify + parse
	 */
	public static MathFormula reparse(MathFormula formula) {
		Parser parser = new Parser(formula.getMetaModel());
		MathFormula formula1 = null;
		try {
			formula1 = parser.parse(serialize(formula.getRootComponent()));

		} catch (ParseException e) {
			FactoryProvider.getInstance().debug(e);
		}
		return formula1 == null ? formula : formula1;
	}

	/**
	 * Serialize both [] and () as ()
	 */
	public void forceRoundBrackets() {
		this.leftBracket = "(";
		this.rightBracket = ")";
	}

	public void setComma(String comma) {
		this.comma = comma;
	}

	/**
	 * @param formula formula; may or may not be a matrix
	 * @return serialized matrix entries or [] if not a matrix
	 */
	public String[] serializeMatrixEntries(MathFormula formula) {
		if (formula.getRootComponent().isProtected()
				&& formula.getRootComponent().getArgument(0) instanceof MathArray) {
			MathArray root = (MathArray) formula.getRootComponent().getArgument(0);
			if (root.isMatrix()) {
				String[] parts = new String[root.size()];
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < root.size(); i++) {
					sb.setLength(0);
					serialize(root.getArgument(i), sb);
					parts[i] = sb.toString();
				}
				return parts;
			}
		}
		return new String[0];
	}

	/**
	 * @param stringBuilder StringBuilder
	 * @param mathFunction MathFunction
	 * @return True if a mixed number was built
	 */
	@Override
	public boolean buildMixedNumber(StringBuilder stringBuilder, MathFunction mathFunction) {
		//Check if a valid mixed number can be created (e.g.: no 'x')
		if (isMixedNumber(stringBuilder) < 0 || !isValidMixedNumber(mathFunction)) {
			return false;
		}

		stringBuilder.insert(isMixedNumber(stringBuilder), "(");
		if (stringBuilder.charAt(stringBuilder.length() - 1) != Unicode.INVISIBLE_PLUS) {
			stringBuilder.append(Unicode.INVISIBLE_PLUS);
		}
		stringBuilder.append("(");
		serialize(mathFunction.getArgument(0), stringBuilder);
		stringBuilder.append(")/(");
		serialize(mathFunction.getArgument(1), stringBuilder);
		stringBuilder.append("))");
		return true;
	}

	public void setShowPlaceholderAsQuestionmark(boolean showPlaceholderAsQuestionmark) {
		this.showPlaceholderAsQuestionmark = showPlaceholderAsQuestionmark;
	}
}