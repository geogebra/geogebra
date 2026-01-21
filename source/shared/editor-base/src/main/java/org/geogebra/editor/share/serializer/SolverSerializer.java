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

package org.geogebra.editor.share.serializer;

import java.util.Arrays;
import java.util.List;

import org.geogebra.editor.share.catalog.Tag;
import org.geogebra.editor.share.tree.ArrayNode;
import org.geogebra.editor.share.tree.CharacterNode;
import org.geogebra.editor.share.tree.FunctionNode;
import org.geogebra.editor.share.tree.InternalNode;
import org.geogebra.editor.share.tree.Node;
import org.geogebra.editor.share.tree.SequenceNode;
import org.geogebra.editor.share.util.Unicode;

public class SolverSerializer extends SerializerAdapter {

	private static final String openingBracket = "[";
	private static final String closingBracket = "]";
	private static final String keysNeedingDots = "{}[]<>";

	private static final String separator = ",";
	private static final List<String> SUPPORTED_FUNCTIONS = Arrays.asList(
			"sin", "cos", "tan", "ln");

	@Override
	void serialize(CharacterNode characterNode, StringBuilder stringBuilder) {
		String serializedChar = (characterNode.isOperator() && characterNode.isUnicodeMulOrDiv())
				? serializeToSolverOperator(characterNode) : characterNode.getUnicodeString();
		stringBuilder.append(serializedChar);
	}

	private String serializeToSolverOperator(CharacterNode characterNode) {
		switch (characterNode.getUnicode()) {
		case Unicode.DIVIDE:
			return ":";
		case Unicode.MULTIPLY:
			return "*";
		default:
			return "";
		}
	}

	@Override
	void serialize(SequenceNode sequence, StringBuilder stringBuilder) {
		if (sequence == null) {
			return;
		}
		for (Node arg: sequence) {
			serialize(arg, stringBuilder);
		}
	}

	@Override
	void serialize(FunctionNode function, StringBuilder sb) {
		Tag functionName = function.getName();
		InternalNode parent = (SequenceNode) function.getParent();
		switch (functionName) {
		case SUPERSCRIPT:
			if (parent != null && !sb.toString().isEmpty()) {
				placeOpeningBracketBeforeBase(function, sb, parent);
				sb.append(functionName.getKey());
				serialize(function.getChild(0), sb);
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
			serialize(function.getChild(0), sb);
			sb.append(closingBracket);
			break;
		case LOG:
			if (function.getChild(0).size() == 0) {
				sb.append(functionName.getKey());
				sb.append(openingBracket);
				sb.append("10"); //default base for log
				sb.append(separator);
				serialize(function.getChild(1), sb);
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
			serialize(function.getChild(0), sb);
			sb.append(separator);
			sb.append("3"); //default nth root for cbrt
			sb.append(closingBracket);
			break;
		case APPLY:
		case APPLY_SQUARE:
			StringBuilder toApply = new StringBuilder();
			serialize(function.getChild(0), toApply);
			sb.append(toApply);
			boolean isKnownFunction = SUPPORTED_FUNCTIONS.contains(toApply.toString());
			sb.append(isKnownFunction ? openingBracket : '(');
			serialize(function.getChild(1), sb);
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

	private void placeOpeningBracketBeforeBase(FunctionNode function, StringBuilder sb,
			InternalNode parent) {
		int baseIndex = parent.indexOf(function) - 1;

		//if i'm part of an array, place the brackets right before my serialization
		if (parent.getChild(baseIndex) instanceof ArrayNode) {
			StringBuilder array = new StringBuilder();
			serialize((ArrayNode) parent.getChild(baseIndex), array);
			sb.insert(sb.length() - array.length(), '[');
			return;
		}

		backtrackToBaseAndPlaceOpeningBracket(parent, baseIndex, 1, sb);
	}

	//first argument is already appended to the SB before the serializing fn so needed a workaround
	private void backtrackToBaseAndPlaceOpeningBracket(InternalNode parents, int baseIndex,
			int backtrackAmount, StringBuilder sb) {

		char [] openingBracketChar = openingBracket.toCharArray();
		char [] closingBracketChar = closingBracket.toCharArray();

		Node character = parents.getChild(baseIndex - backtrackAmount);

		if (character instanceof ArrayNode node) {
			StringBuilder array = new StringBuilder();
			serialize(node, array);
			sb.insert(sb.length() - array.length(), openingBracketChar);
			return;
		}

		if (character instanceof FunctionNode || character instanceof SequenceNode
				|| baseIndex == 0 || character == null) {
			sb.insert(sb.length() - backtrackAmount, openingBracketChar);
			return;
		}

		if (backtrackAmount == sb.length()) {
			sb.insert(0, openingBracketChar);
			return;
		}

		if (character instanceof CharacterNode node && node.isWordBreak()) {
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
	private void serializeAndAppendFnWithTwoArgs(String name, FunctionNode mathfunction,
			String separator, int b, int x, StringBuilder stringBuilder) {
		stringBuilder.append(name);
		stringBuilder.append(openingBracket);
		serialize(mathfunction.getChild(b), stringBuilder);
		stringBuilder.append(separator);
		serialize(mathfunction.getChild(x), stringBuilder);
		stringBuilder.append(closingBracket);
	}

	@Override
	void serialize(ArrayNode arrayNode, StringBuilder stringBuilder) {
		String openKey = String.valueOf(arrayNode.getOpenDelimiter().getCharacter());
		String closeKey = String.valueOf(arrayNode.getCloseDelimiter().getCharacter());
		String open;
		String close;
		char field = arrayNode.getFieldDelimiter().getCharacter();
		char row = arrayNode.getRowDelimiter().getCharacter();

		open  = keysNeedingDots.contains(openKey) ? openKey + "." : openKey;
		close = keysNeedingDots.contains(closeKey) ? "." + closeKey : closeKey;

		if (arrayNode.isMatrix()) {
			stringBuilder.append(open);
		}
		for (int i = 0; i < arrayNode.getRows(); i++) {
			stringBuilder.append(open);
			for (int j = 0; j < arrayNode.getColumns(); j++) {
				serialize(arrayNode.getChild(i, j), stringBuilder);
				stringBuilder.append(field);
			}
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
			stringBuilder.append(close);
			stringBuilder.append(row);
		}
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		if (arrayNode.isMatrix()) {
			stringBuilder.append(close);
		}
	}

	private void generalFunction(FunctionNode functionNode, StringBuilder stringBuilder) {
		stringBuilder.append(functionNode.getName().getKey());
		serializeArgs(functionNode, stringBuilder, 0);
	}

	private void serializeArgs(FunctionNode functionNode,
			StringBuilder stringBuilder, int offset) {
		stringBuilder.append(openingBracket);
		for (int i = offset; i < functionNode.size(); i++) {
			serialize(functionNode.getChild(i), stringBuilder);
			stringBuilder.append(separator);
		}
		if (functionNode.size() > offset) {
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		}
		stringBuilder.append(closingBracket);
	}

	/**
	 * @param stringBuilder StringBuilder
	 * @param functionNode MathFunction
	 * @return True if a mixed number was built
	 */
	@Override
	public boolean buildMixedNumber(StringBuilder stringBuilder, FunctionNode functionNode) {
		//Check if a valid mixed number can be created (e.g.: no 'x')
		if (isMixedNumber(stringBuilder) < 0 || !isValidMixedNumber(functionNode)) {
			return false;
		}

		stringBuilder.insert(isMixedNumber(stringBuilder), openingBracket);
		if (stringBuilder.charAt(stringBuilder.length() - 1) != ' ') {
			stringBuilder.append(" ");
		}
		serialize(functionNode.getChild(0), stringBuilder);
		stringBuilder.append("/");
		serialize(functionNode.getChild(1), stringBuilder);
		stringBuilder.append(closingBracket);
		return true;
	}
}
