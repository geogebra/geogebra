/* SymbolAtom.java
 * =========================================================================
 * This file is originally part of the JMathTeX Library - http://jmathtex.sourceforge.net
 *
 * Copyright (C) 2004-2007 Universiteit Gent
 * Copyright (C) 2009-2018 DENIZET Calixte
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
 * Linking this library statically or dynamically with other modules
 * is making a combined work based on this library. Thus, the terms
 * and conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce
 * an executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under terms
 * of your choice, provided that you also meet, for each linked independent
 * module, the terms and conditions of the license of that module.
 * An independent module is a module which is not derived from or based
 * on this library. If you modify this library, you may extend this exception
 * to your version of the library, but you are not obliged to do so.
 * If you do not wish to do so, delete this exception statement from your
 * version.
 *
 */

/* Modified by Calixte Denizet */

package com.himamis.retex.renderer.share;

import java.util.List;

import com.himamis.retex.renderer.share.platform.FactoryProvider;

/**
 * A box representing a symbol (a non-alphanumeric character).
 */
public class SymbolAtom extends CharSymbol {

	private final CharFont cf;
	private char unicode;

	final public Atom duplicate() {
		SymbolAtom ret = new SymbolAtom(cf, type, unicode);

		ret.unicode = unicode;

		return setFields(ret);
	}

	/**
	 * Constructs a new symbol.
	 *
	 * @param name
	 *            symbol name
	 * @param type
	 *            symbol type constant
	 */
	public SymbolAtom(final CharFont cf, final int type, char unicode) {
		this.cf = cf;
		this.type = type;
		if (type == TeXConstants.TYPE_BIG_OPERATOR) {
			this.type_limits = TeXConstants.SCRIPT_NORMAL;
		}
		this.unicode = unicode;
	}

	public SymbolAtom(final SymbolAtom s, final int type) {
		this(s.cf, type, s.unicode);
	}

	public SymbolAtom(String name, int type, char unicode) {
		this.cf = Configuration.getFontMapping().get(name);
		if (cf == null) {
			FactoryProvider.debugS("missing " + name);
		}

		this.type = type;
		if (type == TeXConstants.TYPE_BIG_OPERATOR) {
			this.type_limits = TeXConstants.SCRIPT_NORMAL;
		}
		this.unicode = unicode;
	}

	public SymbolAtom setUnicode(final char c) {
		this.unicode = c;
		return this;
	}

	/**
	 * Looks up the name in the table and returns the corresponding SymbolAtom
	 * representing the symbol (if it's found).
	 *
	 * @param name
	 *            the name of the symbol
	 * @return a SymbolAtom representing the found symbol
	 */
	public static SymbolAtom get(final String name, final boolean mathMode) {
		SymbolAtom sa = Configuration.getSymbolAtoms().get(name);
		if (!mathMode && sa != null) {
			sa = (SymbolAtom) sa.duplicate();
			sa.mathMode = false;
			sa.type = TeXConstants.TYPE_ORDINARY;
		}
		return sa;
	}

	public static boolean put(final TeXParser tp, final String name) {
		SymbolAtom sa = Configuration.getSymbolAtoms().get(name);
		if (sa == null) {
			return false;
		}
		if (!tp.isMathMode()) {
			sa = (SymbolAtom) sa.duplicate();
			sa.mathMode = false;
			sa.type = TeXConstants.TYPE_ORDINARY;
		}
		tp.addToConsumer(sa);
		tp.cancelPrevPos();

		return true;
	}

	public static SymbolAtom get(final String name) {
		return SymbolAtom.get(name, true);
	}

	public CharFont getCf() {
		return cf;
	}

	@Override
	public Char getChar(TeXEnvironment env) {
		final TeXFont tf = env.getTeXFont();
		final int style = env.getStyle();
		Char c = tf.getChar(getCf(), style);
		if (getType() == TeXConstants.TYPE_BIG_OPERATOR
				&& style < TeXConstants.STYLE_TEXT && tf.hasNextLarger(c)) {
			c = tf.getNextLarger(c, style);
		}
		return c;

		// return env.getTeXFont().getChar(getCf(), env.getStyle());
	}

	public Box getNextLarger(TeXEnvironment env, final double width) {
		final TeXFont tf = env.getTeXFont();
		final int style = env.getStyle();
		Char ch = tf.getChar(getCf(), style);
		while (tf.hasNextLarger(ch)) {
			final Char larger = tf.getNextLarger(ch, style);
			if (larger.getWidth() <= width) {
				ch = larger;
			} else {
				break;
			}
		}
		Box b = new CharBox(ch);
		if (isMathMode() && mustAddItalicCorrection()) {
			b.addToWidth(ch.getItalic());
		}
		return b;
	}

	@Override
	public Box createBox(TeXEnvironment env) {
		TeXFont tf = env.getTeXFont();
		int style = env.getStyle();
		Char c = getChar(env);
		Box cb = new CharBox(c);

		if (getType() == TeXConstants.TYPE_BIG_OPERATOR) {
			final double total = cb.getHeight() + cb.getDepth();
			cb.setShift(-total / 2. - tf.getAxisHeight(style));
			cb = new HorizontalBox(cb);
		}

		if (isMathMode() && mustAddItalicCorrection()) {
			cb.addToWidth(c.getItalic());
		}

		cb.setAtom(this);
		return cb;
	}

	@Override
	public CharFont getCharFont(TeXFont tf) {
		// style doesn't matter here
		return tf.getChar(cf, TeXConstants.STYLE_DISPLAY).getCharFont();
	}

	public SymbolAtom toTextMode() {
		final Atom a = this.duplicate();
		a.mathMode = false;
		a.type = TeXConstants.TYPE_ORDINARY;
		return (SymbolAtom) a;
	}

	@Override
	public Atom changeLimits(final int lim) {
		final Atom a = this.duplicate();
		a.type_limits = lim;
		return a;
	}

	@Override
	public Atom changeType(final int type) {
		final Atom a = this.duplicate();
		a.type = type;
		return a;
	}

	@Override
	public String toString() {
		return "Symbol: " + cf.toString();
	}

	public static void getAll(final List<String> l) {
		for (final String k : Configuration.getSymbolAtoms().keySet()) {
			l.add(k);
		}
	}

	public String getName() {
		return cf.toString();
	}

	public char getUnicode() {
		return unicode;
	}

}
