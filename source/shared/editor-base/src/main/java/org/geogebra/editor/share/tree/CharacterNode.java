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

package org.geogebra.editor.share.tree;

import org.geogebra.editor.share.catalog.CharacterTemplate;
import org.geogebra.editor.share.tree.inspect.Inspecting;
import org.geogebra.editor.share.tree.traverse.Traversing;
import org.geogebra.editor.share.util.Unicode;

/**
 * Represents a character in the expression tree (letters, digits, operators, symbols).
 * Supports Unicode and combined characters with modifiers.
 */
public class CharacterNode extends Node {

	public static final String ZERO_WIDTH_JOINER = "\u200d";
	private CharacterTemplate template;

	/**
	 * Use MathFormula.newCharacter(...)
	 * @param template character template
	 */
	public CharacterNode(CharacterTemplate template) {
		this.template = template;
	}

	/**
	 * @return name for tex
	 */
	public String getTexName() {
		return template.getTexName();
	}

	/**
	 * @return name for tex
	 */
	public String getCasValue() {
		return template.getTag().name();
	}

	/**
	 * For single character return unicode codepoint, for combined returns low surrogate
	 * @return unicode
	 */
	public char getUnicode() {
		return template.getUnicode();
	}

	/**
	 * @return whether this Is Character.
	 */
	public boolean isCharacter() {
		return template.getType() == CharacterTemplate.TYPE_CHARACTER;
	}

	/**
	 * @return whether this is operator
	 */
	public boolean isOperator() {
		return template.getType() == CharacterTemplate.TYPE_OPERATOR;
	}

	/**
	 * @return whether this is a symbol.
	 */
	public boolean isSymbol() {
		return template.getType() == CharacterTemplate.TYPE_SYMBOL;
	}

	@Override
	public Node traverse(Traversing traversing) {
		return traversing.process(this);
	}

	@Override
	public boolean inspect(Inspecting inspecting) {
		return inspecting.check(this);
	}

	@Override
	public String toString() {
		return template.getUnicodeString();
	}

	/**
	 * @return whether this is one of ;,:
	 */
	public boolean isSeparator() {
		return template.getUnicode() == ',' || template.getUnicode() == ';'
				|| template.getUnicode() == ':';
	}

	/**
	 * @param template character template
	 */
	public void setChar(CharacterTemplate template) {
		this.template = template;
	}

	/**
	 * @return whether this is a word-breaking character (space, operator or separator)
	 */
	@SuppressWarnings("deprecation")
	public boolean isWordBreak() {
		return isOperator() || isSeparator() || Character.isSpace(template.getUnicode());
	}

	public boolean isUnicodeMulOrDiv() {
		return template.getUnicode() == Unicode.DIVIDE
				|| template.getUnicode() == Unicode.MULTIPLY;
	}

	public String getUnicodeString() {
		return template.getUnicodeString();
	}

	/**
	 * Try to merge unicode characters
	 * @param s string to append
	 * @return whether result is a single unicode character
	 */
	public boolean mergeUnicode(String s) {
		if (ZERO_WIDTH_JOINER.equals(s) // zero width joiner
				|| (s.charAt(0) >> 10 == 0x37) // high surrogate
				|| isModifier(s) // modifier
				|| template.getUnicodeString().endsWith(ZERO_WIDTH_JOINER)) {
			template = template.merge(s);
			return true;
		}
		return false;
	}

	private static boolean isModifier(String s) {
		return s.length() == 2 && s.charAt(0) == '\uD83C' && s.charAt(1) >> 4 == 0xDFF;
	}

	/**
	 * Check if this character has a specific unicode value.
	 * @param c unicode value
	 * @return whether it matches
	 */
	public boolean isUnicode(char c) {
		return template.getUnicode() == c;
	}

	@Override
	public boolean isFieldSeparator() {
		return template.getUnicode() == ',' || template.getUnicode() == Unicode.verticalLine;
	}

	public boolean isLetter() {
		return org.geogebra.editor.share.input.Character.isLetter(template.getUnicode());
	}

	public boolean isDigit() {
		return Character.isDigit(template.getUnicode());
	}
}
