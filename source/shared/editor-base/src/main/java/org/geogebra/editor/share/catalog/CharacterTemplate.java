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

package org.geogebra.editor.share.catalog;

/**
 * Template for single characters (operators, symbols, or regular characters).
 */
public class CharacterTemplate extends Template {

	public static final int TYPE_CHARACTER = 1;
	public static final int TYPE_OPERATOR = 2;
	public static final int TYPE_SYMBOL = 3;

	private final int type;
	private final char unicode;
	private final String unicodeString;

	/**
	 * @param texName tex name
	 * @param unicode unicode
	 * @param type CHARACTER / OPERATOR / SYMBOL
	 */
	public CharacterTemplate(String texName, char unicode, int type) {
		this(texName, unicode, type, Character.toString(unicode));
	}

	private CharacterTemplate(String texName, char unicode, int type, String unicodeString) {
		super(Tag.CHAR, texName);
		this.type = type;
		this.unicode = unicode;
		this.unicodeString = unicodeString;
	}

	/**
	 * @return CHARACTER / OPERATOR / SYMBOL
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return Unicode char.
	 */
	public char getUnicode() {
		return unicode;
	}

	/**
	 * Unicode value in String format.
	 * @return unicode string
	 */
	public String getUnicodeString() {
		return unicodeString;
	}

	/**
	 * @param other string to append
	 * @return a character model merged from this and other
	 */
	public CharacterTemplate merge(String other) {
		return new CharacterTemplate(getTexName() + other, unicode, type, unicode + other);
	}
}
