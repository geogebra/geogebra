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

import javax.annotation.CheckForNull;

import org.geogebra.editor.share.catalog.Tag;
import org.geogebra.editor.share.editor.EditorFeatures;
import org.geogebra.editor.share.io.latex.ParseException;
import org.geogebra.editor.share.io.latex.Parser;
import org.geogebra.editor.share.tree.ArrayNode;
import org.geogebra.editor.share.tree.CharPlaceholderNode;
import org.geogebra.editor.share.tree.CharacterNode;
import org.geogebra.editor.share.tree.Formula;
import org.geogebra.editor.share.tree.FunctionNode;
import org.geogebra.editor.share.tree.InternalNode;
import org.geogebra.editor.share.tree.Node;
import org.geogebra.editor.share.tree.SequenceNode;
import org.geogebra.editor.share.util.Unicode;

import com.himamis.retex.renderer.share.platform.FactoryProvider;

/**
 * Serializes internal formulas representation into GeoGebra string
 */
public class GeoGebraSerializer extends SerializerAdapter {

	private final @CheckForNull EditorFeatures editorFeatures;

	private String leftBracket = "[";
	private String rightBracket = "]";
	private String comma = ",";
	private boolean showPlaceholderAsQuestionmark;
	private boolean useTemplates = true;

	public GeoGebraSerializer(@CheckForNull EditorFeatures editorFeatures) {
		this.editorFeatures = editorFeatures;
	}

	/**
	 * @param c math formula fragment
	 * @param editorFeatures editor feature set
	 * @return string
	 */
	public static String serialize(Node c, @CheckForNull EditorFeatures editorFeatures) {
		return new GeoGebraSerializer(editorFeatures).serialize(c, new StringBuilder()).toString();
	}

	@Override
	void serialize(CharacterNode characterNode,
			StringBuilder stringBuilder) {
		char unicode = characterNode.getUnicode();
		if (unicode == ',' && !isCommaNeeded(characterNode)) {
			stringBuilder.append(comma);
		} else if (shouldAppendDerivative(characterNode)) {
			appendDerivative(characterNode, stringBuilder);
		} else {
			stringBuilder.append(characterNode.getUnicodeString());
		}
	}

	private boolean isCommaNeeded(CharacterNode token) {
		InternalNode parent = token.getParent();
		int index = token.getParentIndex();
		while (parent != null) {
			if (parent instanceof ArrayNode node) {
				char openKey = node.getOpenDelimiter().getCharacter();
				return openKey == '(' || openKey == '{';
			}
			if (parent instanceof FunctionNode node) {
				return (node.getName() == Tag.APPLY
						|| node.getName() == Tag.APPLY_SQUARE) && index == 1;
			}
			if (parent instanceof SequenceNode node && node.isKeepCommas()) {
				return true;
			}
			index = parent.getParentIndex();
			parent = parent.getParent();
		}
		return false;
	}

	/**
	 * When using the special keyboard, entering the keys used for the minutes or seconds should
	 * enable usage of the first or second derivative when appropriate.
	 * @param characterNode character
	 * @return Whether the symbol used for first (') or second ('') derivative should be appended
	 * instead of the unicode symbol for the minutes or seconds.
	 */
	private boolean shouldAppendDerivative(CharacterNode characterNode) {
		InternalNode parent = characterNode.getParent();
		int indexOfPreviousCharacter = parent.indexOf(characterNode) - 1;
		if (!"\u2032\u2033".contains(characterNode.getUnicodeString())
				|| indexOfPreviousCharacter < 0
				|| !(parent.getChild(indexOfPreviousCharacter) instanceof CharacterNode)) {
			return false;
		}
		CharacterNode previous = (CharacterNode) parent.getChild(indexOfPreviousCharacter);
		return previous.isLetter() || "\u2032\u2033'".contains(previous.getUnicodeString());
	}

	/**
	 * Appends the characters used for the first (') or second ('') derivative.
	 * @param characterNode character
	 * @param stringBuilder Output StringBuilder
	 */
	private void appendDerivative(CharacterNode characterNode, StringBuilder stringBuilder) {
		char unicode = characterNode.getUnicode();
		stringBuilder.append(unicode == '\u2032' ? "'" : "''");
	}

	@Override
	void serialize(FunctionNode functionNode,
			StringBuilder stringBuilder) {
		Tag mathFunctionName = functionNode.getName();
		switch (mathFunctionName) {
		case SUPERSCRIPT:
		case SUBSCRIPT:
			StringBuilder scriptArgument = new StringBuilder();
			serialize(functionNode.getChild(0), scriptArgument);
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
			if (buildMixedNumber(stringBuilder, functionNode)) {
				break;
			}
			stringBuilder.append("((");
			serialize(functionNode.getChild(0), stringBuilder);
			stringBuilder.append(")/(");
			serialize(functionNode.getChild(1), stringBuilder);
			stringBuilder.append("))");
			break;
		case RECURRING_DECIMAL:
			int i = stringBuilder.length() + 1;
			serialize(functionNode.getChild(0), stringBuilder);
			while (i < stringBuilder.length()) {
				stringBuilder.insert(i, Unicode.OVERLINE);
				i += 2;
			}
			if (functionNode.getChild(0).size() != 0) {
				stringBuilder.append(Unicode.OVERLINE);
			}
			break;
		case LOG:
			if (functionNode.getChild(0).size() == 0) {
				appendSingleArg("log", functionNode, stringBuilder, 1);
				break;
			}
			generalFunction(functionNode, stringBuilder);
			break;
		case NROOT:
			if (functionNode.getChild(0).size() == 0) {
				appendSingleArg("sqrt", functionNode, stringBuilder, 1);
				break;
			}
			maybeInsertTimes(functionNode, stringBuilder);
			stringBuilder.append("nroot(");
			serialize(functionNode.getChild(1), stringBuilder);
			stringBuilder.append(",");
			serialize(functionNode.getChild(0), stringBuilder);
			stringBuilder.append(')');
			break;
		case APPLY:
		case APPLY_SQUARE:
			maybeInsertTimes(functionNode, stringBuilder);
			serialize(functionNode.getChild(0), stringBuilder);
			serializeArgs(functionNode, stringBuilder, 1);
			break;
		case VECTOR:
		case POINT:
		case POINT_AT:
			if (!useTemplates) {
				serializeArgs(functionNode, stringBuilder, 0);
				break;
			}
			//$FALL-THROUGH$
		case DEF_INT:
		case SUM_EQ:
		case PROD_EQ:
		case LIM_EQ:
		case VEC:
		case ATOMIC_POST:
		case ATOMIC_PRE:
			stringBuilder.append(functionNode.getName().getKey());
			serializeArgs(functionNode, stringBuilder, 0);
			break;
		case ABS: // no special handling for || so that invalid input saving works
		default:
			generalFunction(functionNode, stringBuilder);
		}
	}

	private void appendSingleArg(String name, FunctionNode functionNode,
			StringBuilder stringBuilder, int i) {
		maybeInsertTimes(functionNode, stringBuilder);
		stringBuilder.append(name);
		stringBuilder.append("(");
		serialize(functionNode.getChild(i), stringBuilder);
		stringBuilder.append(')');
	}

	private void generalFunction(FunctionNode functionNode,
			StringBuilder stringBuilder) {
		maybeInsertTimes(functionNode, stringBuilder);
		stringBuilder.append(functionNode.getName().getKey());
		serializeArgs(functionNode, stringBuilder, 0);
	}

	private void serializeArgs(FunctionNode functionNode,
			StringBuilder stringBuilder, int offset) {
		stringBuilder.append(functionNode.getOpeningBracket());
		for (int i = offset; i < functionNode.size(); i++) {
			if (functionNode.getChild(i) != null) {
				serialize(functionNode.getChild(i), stringBuilder);
				stringBuilder.append(',');
			}
		}
		if (functionNode.size() > offset) {
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		}
		stringBuilder.append(functionNode.getClosingBracket());
	}

	private static void maybeInsertTimes(FunctionNode functionNode,
			StringBuilder stringBuilder) {
		InternalNode internalNode = functionNode.getParent();
		if (internalNode != null && functionNode.getParentIndex() > 0) {
			Node node = internalNode
					.getChild(functionNode.getParentIndex() - 1);
			if (node instanceof CharacterNode characterNode) {
				if (!characterNode.isWordBreak()) {
					stringBuilder.append(" ");
				}
			}
			if (node != null && node.hasTag(Tag.SUBSCRIPT)) {
				stringBuilder.append(" ");
			}
		}
	}

	@Override
	void serialize(ArrayNode arrayNode,
			StringBuilder stringBuilder) {
		char openKey = arrayNode.getOpenDelimiter().getCharacter();
		String open;
		String close;
		String field = arrayNode.getFieldDelimiter().getCharacter() + "";
		String row = arrayNode.getRowDelimiter().getCharacter() + "";
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
			close = arrayNode.getCloseDelimiter().getCharacter() + "";
		}
		if (arrayNode.isMatrix()) {
			stringBuilder.append(open);
		}
		for (int i = 0; i < arrayNode.getRows(); i++) {
			stringBuilder.append(open);
			for (int j = 0; j < arrayNode.getColumns(); j++) {
				serialize(arrayNode.getChild(i, j), stringBuilder);
				stringBuilder.append(field);
			}
			stringBuilder.deleteCharAt(stringBuilder.length() - field.length());
			stringBuilder.append(close);
			stringBuilder.append(row);
		}
		stringBuilder.deleteCharAt(stringBuilder.length() - row.length());
		if (arrayNode.isMatrix()) {
			stringBuilder.append(close);
		}
	}

	@Override
	void serialize(CharPlaceholderNode placeholder, StringBuilder sb) {
		if (showPlaceholderAsQuestionmark) {
			sb.append('?');
		}
	}

	@Override
	protected void serialize(SequenceNode mathSequence,
			StringBuilder stringBuilder) {
		if (mathSequence == null) {
			return;
		}

		// print empty fraction as ?/?, but ignore empty scripts and allow sin()
		if (mathSequence.size() == 0 && showPlaceholderAsQuestionmark
				&& (isMatrixEntry(mathSequence) || mathSequence.getParent() == null)) {
			stringBuilder.append('?');
		}

		for (Node arg : mathSequence) {
			serialize(arg, stringBuilder);
		}
	}

	private boolean isMatrixEntry(SequenceNode mathSequence) {
		return mathSequence.getParent() instanceof ArrayNode
				&& mathSequence.getParent().size() > 1;
	}

	/**
	 * @param formula original formula
	 * @return formula after stringify + parse
	 */
	public static Formula reparse(Formula formula, EditorFeatures editorFeatures) {
		Parser parser = new Parser(formula.getCatalog());
		Formula formula1 = null;
		try {
			formula1 = parser.parse(serialize(formula.getRootNode(), editorFeatures));

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
	public String[] serializeMatrixEntries(Formula formula) {
		if (formula.getRootNode().isProtected()
				&& formula.getRootNode().getChild(0) instanceof ArrayNode) {
			ArrayNode root = (ArrayNode) formula.getRootNode().getChild(0);
			if (root.isMatrix()) {
				String[] parts = new String[root.size()];
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < root.size(); i++) {
					sb.setLength(0);
					serialize(root.getChild(i), sb);
					parts[i] = sb.toString();
				}
				return parts;
			}
		}
		return new String[0];
	}

	/**
	 * @param stringBuilder StringBuilder
	 * @param functionNode MathFunction
	 * @return True if a mixed number was built
	 */
	@Override
	public boolean buildMixedNumber(StringBuilder stringBuilder, FunctionNode functionNode) {
		//Check if a valid mixed number can be created (e.g.: no 'x')
		if ((editorFeatures != null && !editorFeatures.areMixedNumbersEnabled())
				|| isMixedNumber(stringBuilder) < 0
				|| !isValidMixedNumber(functionNode)) {
			return false;
		}

		stringBuilder.insert(isMixedNumber(stringBuilder), "(");
		if (stringBuilder.charAt(stringBuilder.length() - 1) != Unicode.INVISIBLE_PLUS) {
			stringBuilder.append(Unicode.INVISIBLE_PLUS);
		}
		stringBuilder.append("(");
		serialize(functionNode.getChild(0), stringBuilder);
		stringBuilder.append(")/(");
		serialize(functionNode.getChild(1), stringBuilder);
		stringBuilder.append("))");
		return true;
	}

	public void setShowPlaceholderAsQuestionmark(boolean showPlaceholderAsQuestionmark) {
		this.showPlaceholderAsQuestionmark = showPlaceholderAsQuestionmark;
	}

	public void setUseTemplates(boolean b) {
		this.useTemplates = b;
	}
}