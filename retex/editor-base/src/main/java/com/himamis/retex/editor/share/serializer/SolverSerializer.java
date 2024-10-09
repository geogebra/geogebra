package com.himamis.retex.editor.share.serializer;

import java.util.Arrays;
import java.util.List;

import com.himamis.retex.editor.share.meta.Tag;
import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.share.util.Unicode;

public class SolverSerializer extends SerializerAdapter {

	private static final String openingBracket = "[";
	private static final String closingBracket = "]";
	private static final String keysNeedingDots = "{}[]<>";

	private static final String separator = ",";
	private static final List<String> SUPPORTED_FUNCTIONS = Arrays.asList(
			"sin", "cos", "tan", "ln");

	@Override
	void serialize(MathCharacter mathCharacter, StringBuilder stringBuilder) {
		String serializedChar = (mathCharacter.isOperator() && mathCharacter.isUnicodeMulOrDiv())
				? serializeToSolverOperator(mathCharacter) : mathCharacter.getUnicodeString();
		stringBuilder.append(serializedChar);
	}

	private String serializeToSolverOperator(MathCharacter mathCharacter) {
		switch (mathCharacter.getUnicode()) {
		case Unicode.DIVIDE:
			return ":";
		case Unicode.MULTIPLY:
			return "*";
		default:
			return "";
		}
	}

	@Override
	void serialize(MathSequence sequence, StringBuilder stringBuilder) {
		if (sequence == null) {
			return;
		}
		for (MathComponent arg: sequence) {
			serialize(arg, stringBuilder);
		}
	}

	@Override
	void serialize(MathFunction function, StringBuilder sb) {
		Tag functionName = function.getName();
		MathSequence parent = function.getParent();
		switch (functionName) {
		case SUPERSCRIPT:
			if (parent != null && !sb.toString().isEmpty()) {
				placeOpeningBracketBeforeBase(function, sb, parent);
				sb.append(functionName.getFunction());
				serialize(function.getArgument(0), sb);
				sb.append(closingBracket);
			}
			break;
		case FRAC:
			if (buildMixedNumber(sb, function)) {
				break;
			}
			serializeAndAppendFnWithTwoArgs("", function, "/", 0, 1, sb);
			break;
		case RECURRING_DECIMAL:
			sb.append(openingBracket);
			serialize(function.getArgument(0), sb);
			sb.append(closingBracket);
			break;
		case LOG:
			if (function.getArgument(0).size() == 0) {
				sb.append(functionName.getFunction());
				sb.append(openingBracket);
				sb.append("10"); //default base for log
				sb.append(separator);
				serialize(function.getArgument(1), sb);
				sb.append(closingBracket);
				break;
			}
			generalFunction(function, sb);
			break;
		case NROOT: //root[x,n]
			serializeAndAppendFnWithTwoArgs("root", function, separator,
					1, 0, sb);
			break;
		case CBRT: //root[x,n]
			sb.append("root");
			sb.append(openingBracket);
			serialize(function.getArgument(0), sb);
			sb.append(separator);
			sb.append("3"); //default nth root for cbrt
			sb.append(closingBracket);
			break;
		case APPLY:
		case APPLY_SQUARE:
			StringBuilder toApply = new StringBuilder();
			serialize(function.getArgument(0), toApply);
			sb.append(toApply);
			boolean isKnownFunction = SUPPORTED_FUNCTIONS.contains(toApply.toString());
			sb.append(isKnownFunction ? openingBracket : '(');
			serialize(function.getArgument(1), sb);
			sb.append(isKnownFunction ? closingBracket : ')');
			break;
		case SUM_EQ:
		case PROD_EQ:
			serializeArgs(function, sb, 0);
			break;
		case ABS:
		case SQRT: //sqrt[x]
		default:
			generalFunction(function, sb);
		}
	}

	private void placeOpeningBracketBeforeBase(MathFunction function, StringBuilder sb,
			MathSequence parent) {
		int baseIndex = parent.indexOf(function) - 1;

		//if i'm part of an array, place the brackets right before my serialization
		if (parent.getArgument(baseIndex) instanceof MathArray) {
			StringBuilder array = new StringBuilder();
			serialize((MathArray) parent.getArgument(baseIndex), array);
			sb.insert(sb.length() - array.length(), '[');
			return;
		}

		backtrackToBaseAndPlaceOpeningBracket(parent, baseIndex, 1, sb);
	}

	//first argument is already appended to the SB before the serializing fn so needed a workaround
	private void backtrackToBaseAndPlaceOpeningBracket(MathSequence parents, int baseIndex,
			int backtrackAmount, StringBuilder sb) {

		char [] openingBracketChar = openingBracket.toCharArray();
		char [] closingBracketChar = closingBracket.toCharArray();

		MathComponent character = parents.getArgument(baseIndex - backtrackAmount);

		if (character instanceof MathArray) {
			StringBuilder array = new StringBuilder();
			serialize((MathArray) character, array);
			sb.insert(sb.length() - array.length(), openingBracketChar);
			return;
		}

		if (character instanceof MathFunction || character instanceof MathSequence
				|| baseIndex == 0 || character == null) {
			sb.insert(sb.length() - backtrackAmount, openingBracketChar);
			return;
		}

		if (backtrackAmount == sb.length()) {
			sb.insert(0, openingBracketChar);
			return;
		}

		if (character instanceof MathCharacter && ((MathCharacter) character).isWordBreak()) {
			int lastOpeningBracketIndex = sb.lastIndexOf(openingBracket);
			if (sb.charAt(sb.length() - 1 - backtrackAmount) == closingBracketChar[0]) {
				if (sb.indexOf(closingBracket, lastOpeningBracketIndex)
						== sb.length() - 1 - backtrackAmount) {
					sb.insert(lastOpeningBracketIndex, openingBracketChar, 0, 1);
				} else { //handles nested brackets
					int closingBracketsCount = countOfClosingBrackets(sb);
					int openingBracketIndex = getPlacementIndex(closingBracketsCount, sb);
					sb.insert(openingBracketIndex, openingBracketChar, 0, 1);
				}
			} else {
				sb.insert(sb.length() - backtrackAmount,
						openingBracket.toCharArray(), 0, 1);
			}
		} else {
			backtrackToBaseAndPlaceOpeningBracket(parents, baseIndex, backtrackAmount + 1, sb);
		}
	}

	private int getPlacementIndex(int closingBracketsCount, StringBuilder stringBuilder) {
		String temp = stringBuilder.toString();
		int currentIndex = 0;
		for (int i = closingBracketsCount; i > 0; i--) {
			currentIndex = temp.lastIndexOf(openingBracket);
			temp = temp.substring(0, currentIndex);
		}
		return currentIndex;
	}

	private int countOfClosingBrackets(StringBuilder sb) {
		int countOfClosingBrackets = 0;
		for (char c : sb.toString().toCharArray()) {
			if (c == closingBracket.toCharArray()[0]) {
				countOfClosingBrackets++;
			}
		}
		return countOfClosingBrackets;
	}

	//name[b,x]
	private void serializeAndAppendFnWithTwoArgs(String name, MathFunction mathfunction,
			String separator, int b, int x, StringBuilder stringBuilder) {
		stringBuilder.append(name);
		stringBuilder.append(openingBracket);
		serialize(mathfunction.getArgument(b), stringBuilder);
		stringBuilder.append(separator);
		serialize(mathfunction.getArgument(x), stringBuilder);
		stringBuilder.append(closingBracket);
	}

	@Override
	void serialize(MathArray mathArray, StringBuilder stringBuilder) {
		String openKey = String.valueOf(mathArray.getOpenKey());
		String closeKey = String.valueOf(mathArray.getCloseKey());
		String open;
		String close;
		String field = mathArray.getFieldKey() + "";
		String row = mathArray.getRow().getKey() + "";

		open  = keysNeedingDots.contains(openKey) ? openKey + "." : openKey;
		close = keysNeedingDots.contains(closeKey) ? "." + closeKey : closeKey;

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

	private void generalFunction(MathFunction mathFunction, StringBuilder stringBuilder) {
		stringBuilder.append(mathFunction.getName().getFunction());
		serializeArgs(mathFunction, stringBuilder, 0);
	}

	private void serializeArgs(MathFunction mathFunction,
			StringBuilder stringBuilder, int offset) {
		stringBuilder.append(openingBracket);
		for (int i = offset; i < mathFunction.size(); i++) {
			serialize(mathFunction.getArgument(i), stringBuilder);
			stringBuilder.append(separator);
		}
		if (mathFunction.size() > offset) {
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		}
		stringBuilder.append(closingBracket);
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

		stringBuilder.insert(isMixedNumber(stringBuilder), openingBracket);
		if (stringBuilder.charAt(stringBuilder.length() - 1) != ' ') {
			stringBuilder.append(" ");
		}
		serialize(mathFunction.getArgument(0), stringBuilder);
		stringBuilder.append("/");
		serialize(mathFunction.getArgument(1), stringBuilder);
		stringBuilder.append(closingBracket);
		return true;
	}
}
