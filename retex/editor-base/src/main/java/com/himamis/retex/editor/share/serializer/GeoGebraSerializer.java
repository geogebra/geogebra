package com.himamis.retex.editor.share.serializer;

import com.himamis.retex.editor.share.io.latex.ParseException;
import com.himamis.retex.editor.share.io.latex.Parser;
import com.himamis.retex.editor.share.meta.Tag;
import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathPlaceholder;
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
		case MIXED_NUMBER:
			boolean isNegative = mathFunction.getArgument(0).getArgument(0) != null
					&& mathFunction.getArgument(0).getArgument(0).toString().equals("-");
			if (isNegative) {
				stringBuilder.append("-");
			}
			stringBuilder.append("(");
			serialize(mathFunction.getArgument(0), stringBuilder);
			if (isNegative) {
				stringBuilder.deleteCharAt(stringBuilder.lastIndexOf("-"));
			}
			stringBuilder.append("\u2064((");
			serialize(mathFunction.getArgument(1), stringBuilder);
			stringBuilder.append(")/(");
			serialize(mathFunction.getArgument(2), stringBuilder);
			stringBuilder.append(")))");
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
	void serialize(MathPlaceholder placeholder, StringBuilder stringBuilder) {
		//no placeholders
	}

	@Override
	void serialize(MathSequence mathSequence,
			StringBuilder stringBuilder) {
		if (mathSequence == null) {
			return;
		}
		for (MathComponent arg: mathSequence) {
			serialize(arg, stringBuilder);
		}
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
				for (int i = 0; i < root.size(); i++) {
					parts[i] = serialize(root.getArgument(i));
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
	private boolean buildMixedNumber(StringBuilder stringBuilder, MathFunction mathFunction) {
		//Check if a valid mixed number can be created (e.g.: no 'x')
		if (!isValidMixedNumber(mathFunction)) {
			removeInvisiblePlus(mathFunction);
			return false;
		}
		//Check if there is an invisible plus preceeding the fraction or not
		for (int i = 0; i < mathFunction.getArgument(0).getArgumentCount(); i++) {
			if (mathFunction.getArgument(0).getArgument(i).toString().equals("\u2064")) {
				buildMixedNumberByOperator(stringBuilder, mathFunction);
				return true;
			}
		}
		//Check if there is an integer preceeding the fraction or not
		if (isMixedNumber(stringBuilder) < 0) {
			return false;
		}
		stringBuilder.insert(isMixedNumber(stringBuilder), "(");
		stringBuilder.append("\u2064((");
		serialize(mathFunction.getArgument(0), stringBuilder);
		stringBuilder.append(")/(");
		serialize(mathFunction.getArgument(1), stringBuilder);
		stringBuilder.append(")))");
		return true;
	}

	/**
	 * Gets called when the mixed number is created by spotting an invisible plus preceeding the
	 * whole number
	 * @param stringBuilder StringBuilder
	 * @param mathFunction MathFunction
	 */
	private void buildMixedNumberByOperator(StringBuilder stringBuilder,
			MathFunction mathFunction) {
		stringBuilder.append("(");
		int i = 0;
		while (!mathFunction.getArgument(0).getArgument(i).toString().equals("\u2064")) {
			stringBuilder.append(mathFunction.getArgument(0).getArgument(i));
			i++;
		}
		stringBuilder.append("\u2064(("); //mathFunction.getArgument(0).getArgument(i)
		for (int j = i + 1; j < mathFunction.getArgument(0).getArgumentCount(); j++) {
			stringBuilder.append(mathFunction.getArgument(0).getArgument(j));
		}
		stringBuilder.append(")/(");
		serialize(mathFunction.getArgument(1), stringBuilder);
		stringBuilder.append(")))");
	}

	/**
	 * @return Index ( >= 0 ) of where to put an opening parentheses when there is a mixed number in the stringBuilder <br>
	 * -1 If there is no mixed number <br>
	 * Parentheses needed so e.g. 5 1/2 * 3 does not do the multiplication with 3 first <br>
	 */
	private int isMixedNumber(StringBuilder stringBuilder) {
		boolean isMixedNumber = false;
		for (int i = stringBuilder.length() - 1; i >= 0; i--) {
			if (stringBuilder.charAt(i) == ' ' && !isMixedNumber) {
				continue;
			} else if (stringBuilder.charAt(i) >= '0' && stringBuilder.charAt(i) <= '9') {
				isMixedNumber = true;
			} else if (isMixedNumber
					&& " +-+/".contains(Character.toString(stringBuilder.charAt(i)))) {
				return i + 1;
			} else {
				isMixedNumber = false;
				break;
			}
		}
		return isMixedNumber ? 0 : -1;
	}

	/**
	 * Used to determine if a function contains of digits and invisible plus only
	 * (for mixed numbers)
	 * @param mathFunction (Fraction)
	 * @return True if digits (and invisible plus) only are found, false else
	 */
	private boolean isValidMixedNumber(MathFunction mathFunction) {
		String compare = new String();
		for (int i = 0; i < mathFunction.size(); i++) {
			for (int j = 0; j < mathFunction.getArgument(i).size(); j++) {
				compare = mathFunction.getArgument(i).getArgument(j).toString();
				if (compare.compareTo("0") < 0 || compare.compareTo("9") > 0
					&& !compare.equals("\u2064")) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * User entered an invisible plus but no valid mixed number can be created
	 * - remove invisible plus
	 * @param mathFunction MathFunction
	 */
	private void removeInvisiblePlus(MathFunction mathFunction) {
		for (int i = 0; i < mathFunction.size(); i++) {
			for (int j = 0; j < mathFunction.getArgument(i).size(); j++) {
				if (mathFunction.getArgument(i).getArgument(j).toString().equals("\u2064")) {
					mathFunction.getArgument(i).delArgument(j);
				}
			}
		}
	}
}