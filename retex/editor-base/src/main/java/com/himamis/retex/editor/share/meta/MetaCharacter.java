/* MetaCharacter.java
 * =========================================================================
 * This file is part of the Mirai Math TN - http://mirai.sourceforge.net
 *
 * Copyright (C) 2008-2009 Bea Petrovicova
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 *
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 */

package com.himamis.retex.editor.share.meta;

public class MetaCharacter extends MetaComponent {

    public static final int CHARACTER = 1;
    public static final int OPERATOR = 2;
    public static final int SYMBOL = 3;

    private int type;
	private char unicode;
	private String unicodeString;

	/**
	 * @param texName
	 *            tex name
	 * @param unicode
	 *            unicode
	 * @param type
	 *            CHARACTER / OPERATOR / SYMBOL
	 */
	public MetaCharacter(String texName, char unicode,
			int type) {
		super(Tag.CHAR, texName);
        this.type = type;
		this.unicode = unicode;
		this.unicodeString = Character.toString(unicode);
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
	 *
	 * @return unicode string
	 */
	public String getUnicodeString() {
		return unicodeString;
	}

	/**
	 * @param other string to append
	 * @return a character model merged from this and other
	 */
	public MetaCharacter merge(String other) {
		MetaCharacter ret = new MetaCharacter(getTexName() + other, unicode, type);
		ret.unicodeString = unicodeString + other;
		return ret;
	}
}
