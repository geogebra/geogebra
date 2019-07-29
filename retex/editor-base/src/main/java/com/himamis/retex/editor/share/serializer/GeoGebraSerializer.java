package com.himamis.retex.editor.share.serializer;

import com.himamis.retex.editor.share.io.latex.ParseException;
import com.himamis.retex.editor.share.io.latex.Parser;
import com.himamis.retex.editor.share.meta.Tag;
import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * Serializes internal formulas representation into GeoGebra string
 *
 */
public class GeoGebraSerializer implements Serializer {

	@Override
	public String serialize(MathFormula formula) {
		MathSequence sequence = formula.getRootComponent();
		StringBuilder stringBuilder = new StringBuilder();
		serialize(sequence, stringBuilder);
		return stringBuilder.toString();
	}

	private static void serialize(MathComponent mathComponent,
			MathSequence parent, int index,
			StringBuilder stringBuilder) {
		if (mathComponent instanceof MathCharacter) {
			serialize((MathCharacter) mathComponent, parent, index,
					stringBuilder);
		} else if (mathComponent instanceof MathFunction) {
			serialize((MathFunction) mathComponent, stringBuilder);
		} else if (mathComponent instanceof MathArray) {
			serialize((MathArray) mathComponent, stringBuilder);
		} else if (mathComponent instanceof MathSequence) {
			serialize((MathSequence) mathComponent, stringBuilder);
		}
	}

	/**
	 * @param c
	 *            math formula fragment
	 * @return string
	 */
	public static String serialize(MathComponent c) {
		StringBuilder sb = new StringBuilder();
		GeoGebraSerializer.serialize(c, null, 0, sb);
		return sb.toString();
	}

	private static void serialize(MathCharacter mathCharacter,
			MathSequence parent, int index,
			StringBuilder stringBuilder) {
		if (mathCharacter.getUnicode() == Unicode.ZERO_WIDTH_SPACE) {

			if (parent != null && index + 1 < parent.size()) {
				if (needsSpace(parent.getArgument(index + 1))) {
					stringBuilder.append(" ");
				}
			}
			return;
		}
		stringBuilder.append(mathCharacter.getUnicode());
	}

	private static boolean needsSpace(MathComponent argument) {
		// TODO Auto-generated method stub
		return argument instanceof MathArray
				|| (argument instanceof MathFunction
						&& ((MathFunction) argument).getName() != Tag.SUBSCRIPT
						&& ((MathFunction) argument)
								.getName() != Tag.SUPERSCRIPT);
	}

	private static void serialize(MathFunction mathFunction,
			StringBuilder stringBuilder) {
		Tag mathFunctionName = mathFunction.getName();
		switch (mathFunctionName) {
		case SUPERSCRIPT:
			stringBuilder.append("^(");
			serialize(mathFunction.getArgument(0), stringBuilder);
			stringBuilder.append(')');
			break;
		case SUBSCRIPT:
			stringBuilder.append("_{");
			serialize(mathFunction.getArgument(0), stringBuilder);
			// a_{1}sin(x) should be a_{1} sin(x)
			stringBuilder.append("}");
			break;
		case FRAC:
			stringBuilder.append('(');
			serialize(mathFunction.getArgument(0), stringBuilder);
			stringBuilder.append(")/(");
			serialize(mathFunction.getArgument(1), stringBuilder);
			stringBuilder.append(")");
			break;
		case SQRT:
			appendSingleArg("sqrt", mathFunction, stringBuilder, 0);
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
			// Strict control of available functions is needed, so that SUM/ and
			// Prod doesn't work
		case SUM:
			stringBuilder.append("Sum");
			serializeArgs(mathFunction, stringBuilder,
					new int[] { 3, 0, 1, 2 });
			break;
		case PROD:
			stringBuilder.append("Product");
			serializeArgs(mathFunction, stringBuilder,
					new int[] { 3, 0, 1, 2 });
			break;
		case INT:
			stringBuilder.append("Integral");
			serializeArgs(mathFunction, stringBuilder,
					new int[] { 2, 0, 1 });
			break;
		case LIM:
			stringBuilder.append("Limit");
			serializeArgs(mathFunction, stringBuilder,
					new int[] { 2, 0, 1 });
			break;
		case APPLY:
		case APPLY_SQUARE:
			maybeInsertTimes(mathFunction, stringBuilder);
			serialize(mathFunction.getArgument(0), stringBuilder);
			serializeArgs(mathFunction, stringBuilder, 1);
			break;
		case ABS:
			// empty abs() is just ||
			if (mathFunction.getArgument(0).size() == 0) {
				stringBuilder.append("||");
			} else {
				generalFunction(mathFunction, stringBuilder);
			}
			break;
		default:
			generalFunction(mathFunction, stringBuilder);
		}
	}

	private static void appendSingleArg(String name, MathFunction mathFunction,
			StringBuilder stringBuilder, int i) {
		maybeInsertTimes(mathFunction, stringBuilder);
		stringBuilder.append(name);
		stringBuilder.append("(");
		serialize(mathFunction.getArgument(i), stringBuilder);
		stringBuilder.append(')');
	}

	private static void generalFunction(MathFunction mathFunction,
			StringBuilder stringBuilder) {
		maybeInsertTimes(mathFunction, stringBuilder);
		stringBuilder.append(mathFunction.getName().getFunction());
		serializeArgs(mathFunction, stringBuilder, 0);
	}

	private static void serializeArgs(MathFunction mathFunction,
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

	private static void serializeArgs(MathFunction mathFunction,
			StringBuilder stringBuilder, int[] order) {
		for (int i = 0; i < order.length; i++) {
			stringBuilder.append(i == 0 ? "((" : ",(");
			serialize(mathFunction.getArgument(order[i]), stringBuilder);
			stringBuilder.append(")");
		}
		stringBuilder.append(")");
	}

	private static void maybeInsertTimes(MathFunction mathFunction,
			StringBuilder stringBuilder) {
		MathSequence mathSequence = mathFunction.getParent();
		if (mathSequence != null && mathFunction.getParentIndex() > 0) {
			MathComponent mathComponent = mathSequence
					.getArgument(mathFunction.getParentIndex() - 1);
			if (mathComponent instanceof MathCharacter) {
				MathCharacter mathCharacter = (MathCharacter) mathComponent;
				if (mathCharacter.isCharacter() && mathCharacter
						.getUnicode() != Unicode.ZERO_WIDTH_SPACE) {
					stringBuilder.append("*");
				}
			}
			if (mathComponent != null && mathComponent.hasTag(Tag.SUBSCRIPT)) {
				stringBuilder.append("*");
			}
		}
	}

	private static void serialize(MathArray mathArray,
			StringBuilder stringBuilder) {
		String open = mathArray.getOpen().getKey() + "";
		String close = mathArray.getClose().getKey() + "";
		String field = mathArray.getField().getKey() + "";
		String row = mathArray.getRow().getKey() + "";
		if ((Unicode.LFLOOR + "").equals(open)) {
			open = "floor(";
			close = ")";
		} else if ((Unicode.LCEIL + "").equals(open)) {
			open = "ceil(";
			close = ")";
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

	private static void serialize(MathSequence mathSequence,
			StringBuilder stringBuilder) {
		if (mathSequence == null) {
			return;
		}
		for (int i = 0; i < mathSequence.size(); i++) {
			serialize(mathSequence.getArgument(i), mathSequence, i,
					stringBuilder);
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
			e.printStackTrace();
		}
		return formula1 == null ? formula : formula1;
	}
}
