/* MathCharacter.java
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

package com.himamis.retex.editor.share.model;

import com.himamis.retex.editor.share.meta.MetaCharacter;
import com.himamis.retex.editor.share.model.inspect.Inspecting;
import com.himamis.retex.editor.share.model.traverse.Traversing;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * Character. This class is part of model.
 * <p>
 * x^2
 * x_j
 *
 * @author Bea Petrovicova
 */
public class MathCharacter extends MathComponent {

	public static final String ZERO_WIDTH_JOINER = "\u200d";
	private static final long serialVersionUID = 1L;
	private MetaCharacter meta;

    /**
	 * Use MathFormula.newCharacter(...)
	 * 
	 * @param meta
	 *            meta component
	 */
    public MathCharacter(MetaCharacter meta) {
        this.meta = meta;
    }

    /**
     * Gets parent of this component.
     */
	@Override
	public MathSequence getParent() {
        return (MathSequence) super.getParent();
    }

	@Override
	public MathCharacter copy() {
        return new MathCharacter(meta);
    }

	/**
	 * @return name for tex
	 */
    public String getTexName() {
        return meta.getTexName();
    }

	/**
	 * @return name for tex
	 */
	public String getCasValue() {
		return meta.getName().name();
	}

	/**
	 * For single character return unicode codepoint, for combined returns low surrogate
	 * @return unicode
	 */
	public char getUnicode() {
		return meta.getUnicode();
	}

    /**
	 * @return whether this Is Character.
	 */
    public boolean isCharacter() {
        return meta.getType() == MetaCharacter.CHARACTER;
    }

    /**
	 * @return whether this is operator
	 */
    public boolean isOperator() {
        return meta.getType() == MetaCharacter.OPERATOR;
    }

    /**
	 * @return whether this is a symbol.
	 */
    public boolean isSymbol() {
        return meta.getType() == MetaCharacter.SYMBOL;
    }

	@Override
	public MathComponent traverse(Traversing traversing) {
        return traversing.process(this);
    }

    @Override
    public boolean inspect(Inspecting inspecting) {
        return inspecting.check(this);
    }

	@Override
	public String toString() {
		return meta.getUnicodeString();
	}

	/**
	 * @return whether this is one of ;,:
	 */
	public boolean isSeparator() {
		return meta.getUnicode() == ',' || meta.getUnicode() == ';'
				|| meta.getUnicode() == ':';
	}

	/**
	 * @param metaCharacter
	 *            character model
	 */
	public void setChar(MetaCharacter metaCharacter) {
		this.meta = metaCharacter;
	}

	/**
	 * @return whether this is a word-breaking character (space, operator or separator)
	 */
	@SuppressWarnings("deprecation")
	public boolean isWordBreak() {
		return isOperator() || isSeparator() || Character.isSpace(meta.getUnicode());
	}

	public boolean isUnicodeMulOrDiv() {
		return meta.getUnicode() == Unicode.DIVIDE ||  meta.getUnicode() == Unicode.MULTIPLY;
	}

	public String getUnicodeString() {
		return meta.getUnicodeString();
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
				|| meta.getUnicodeString().endsWith(ZERO_WIDTH_JOINER)) {
			meta = meta.merge(s);
			return true;
		}
		return false;
	}

	private static boolean isModifier(String s) {
		return s.length() == 2 && s.charAt(0) == '\uD83C' && s.charAt(1) >> 4 == 0xDFF;
	}

	public boolean isUnicode(char c) {
		return meta.getUnicode() == c;
	}

	@Override
	public boolean isFieldSeparator() {
		return meta.getUnicode() == ',' || meta.getUnicode() == Unicode.verticalLine;
	}

	public boolean isLetter() {
		return com.himamis.retex.editor.share.input.Character.isLetter(meta.getUnicode());
	}

	public boolean isDigit() {
		return Character.isDigit(meta.getUnicode());
	}
}
