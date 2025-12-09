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

import org.geogebra.editor.share.tree.ArrayNode;
import org.geogebra.editor.share.tree.CharPlaceholderNode;
import org.geogebra.editor.share.tree.CharacterNode;
import org.geogebra.editor.share.tree.Formula;
import org.geogebra.editor.share.tree.FunctionNode;
import org.geogebra.editor.share.tree.InternalNode;
import org.geogebra.editor.share.tree.Node;
import org.geogebra.editor.share.tree.PlaceholderNode;
import org.geogebra.editor.share.tree.SequenceNode;
import org.geogebra.editor.share.util.Unicode;

public abstract class SerializerAdapter implements Serializer {

	protected InternalNode mCurrentField = null;
	protected Node currentSelStart = null;
	protected Node currentSelEnd = null;
	protected int mCurrentOffset = 0;

	@Override
	public String serialize(Formula formula) {
		return serialize(formula, null, 0, null, null, false);
	}

	/**
	 * @param formula
	 *            formula
	 * @param currentField
	 *            field with cursor
	 * @param currentOffset
	 *            cursor offset
	 * @return serialized formula
	 */
	public String serialize(Formula formula, SequenceNode currentField,
			int currentOffset) {
		return serialize(formula, currentField, currentOffset, null, null, false);
	}

	/**
	 * @param formula
	 *            formula
	 * @param currentField
	 *            field with cursor
	 * @param currentOffset
	 *            cursor offset
	 * @param selStart
	 *            selected area start
	 * @param selEnd
	 *            selected area end
	 * @return serialized formula
	 */
	public String serialize(Formula formula, SequenceNode currentField,
			int currentOffset, Node selStart, Node selEnd, boolean textMode) {
		this.mCurrentField = currentField;
		this.mCurrentOffset = currentOffset;
		this.currentSelEnd = selEnd;
		this.currentSelStart = selStart;
		StringBuilder buffer = new StringBuilder();

		if (textMode) {
			buffer.append("\\text{");
		}
		serialize(formula.getRootNode(), buffer);
		if (textMode) {
			buffer.append("}");
		}

		return buffer.toString();
	}

	/**
	 * @param container
	 *            part of formula
	 * @param currentField
	 *            field with cursor
	 * @param currentOffset
	 *            cursor offset
	 * @return serialized formula
	 */
	public String serialize(InternalNode container, SequenceNode currentField,
							int currentOffset) {
		this.mCurrentField = currentField;
		this.mCurrentOffset = currentOffset;
		return serialize(container, new StringBuilder()).toString();
	}

	/**
	 * @param container
	 *            part of formula
	 * @param stringBuilder
	 *            output string builder
	 * @return stringBuilder for convenience
	 */
	public StringBuilder serialize(Node container, StringBuilder stringBuilder) {
		if (container instanceof CharPlaceholderNode) {
			serialize((CharPlaceholderNode) container, stringBuilder);

		} else if (container instanceof CharacterNode) {
			serialize((CharacterNode) container, stringBuilder);

		} else if (container instanceof PlaceholderNode) {
			serialize((PlaceholderNode) container, stringBuilder);

		} else if (container instanceof SequenceNode) {
			serialize((SequenceNode) container, stringBuilder);

		} else if (container instanceof ArrayNode) {
			serialize((ArrayNode) container, stringBuilder);

		} else if (container instanceof FunctionNode) {
			serialize((FunctionNode) container, stringBuilder);
		}
		return stringBuilder;
	}

	abstract void serialize(CharacterNode characterNode, StringBuilder stringBuilder);

	abstract void serialize(SequenceNode sequence, StringBuilder stringBuilder);

	/**
	 * @param sequence
	 *            math sequence
	 * @param stringBuilder
	 *            builder
	 * @param from
	 *            start index
	 * @param to
	 *            end index
	 */
	public void serialize(SequenceNode sequence, StringBuilder stringBuilder,
			int from, int to) {
		for (int i = from; i < to; i++) {
			serialize(sequence.getChild(i), stringBuilder);
		}
	}

	abstract void serialize(FunctionNode function, StringBuilder stringBuilder);

	abstract void serialize(ArrayNode array, StringBuilder stringBuilder);

	void serialize(PlaceholderNode placeholder, StringBuilder stringBuilder) {
		// only in LaTeX
	}

	abstract boolean buildMixedNumber(StringBuilder stringBuilder, FunctionNode functionNode);

	void serialize(CharPlaceholderNode placeholder, StringBuilder stringBuilder) {
		// only in LaTeX
	}

	/**
	 * Checks if the stringBuilder contains a mixed number e.g. 3 1/2
	 * @param stringBuilder StringBuilder
	 * @return Index &gt;= 0 of where to put opening parentheses if there is a mixed number, -1 else
	 */
	public int isMixedNumber(StringBuilder stringBuilder) {
		boolean isMixedNumber = false;
		for (int i = stringBuilder.length() - 1; i >= 0; i--) {
			if (stringBuilder.charAt(i) == ' ' && !isMixedNumber) {
				continue; // Expecting a space preceding the fraction
			} else if (Character.isDigit(stringBuilder.charAt(i))) {
				isMixedNumber = true; // Only allow digits 0 - 9 here
			} else if (isMixedNumber // Square bracket "[" needed for the SolverSerializer
					&& " +-*/()[]{}=,;".contains(Character.toString(stringBuilder.charAt(i)))) {
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
	 * @param functionNode MathFunction
	 * @return True if digits (and invisible plus) only are found, false else
	 */
	public boolean isValidMixedNumber(FunctionNode functionNode) {
		String compare;
		for (int i = 0; i < functionNode.size(); i++) {
			for (int j = 0; j < functionNode.getChild(i).size(); j++) {
				compare = functionNode.getChild(i).getChild(j).toString();
				if (!Character.isDigit(compare.charAt(0))
						&& !compare.equals(Character.toString(Unicode.INVISIBLE_PLUS))) {
					return false;
				}
			}
		}
		return true;
	}
}
