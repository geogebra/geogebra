/* ParseException.java
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

package com.himamis.retex.renderer.share.exception;

import com.himamis.retex.renderer.share.TeXParser;

/**
 * Signals that an error occured while parsing a string to a formula.
 */
public class ParseException extends JMathTeXException {

	private static final int N = 30;

	private ParseException(String str, Throwable cause) {
		super(str, cause);
	}

	public ParseException(String str) {
		super(str);
	}

	public ParseException(TeXParser tp, String str, Throwable cause) {
		this(getErr(str, tp), cause);
	}

	public ParseException(TeXParser tp, String str) {
		this(getErr(str, tp));
	}

	public ParseException(TeXParser tp, String str, String latexErr) {
		this(!tp.isPartial() ? getErr(str, tp) : setLatexErr(tp, latexErr));
	}

	private static String setLatexErr(TeXParser tp, String err) {
		tp.addString("\\textcolor{red}{" + err + "}", true);
		tp.parse();
		return "";
	}

	private static String getWhites(final int n) {
		final char[] w = new char[n];
		for (int i = 0; i < n; ++i) {
			w[i] = ' ';
		}
		return new String(w);
	}

	private static String getErr(String msg, TeXParser tp) {
		final String parseString = tp.getParsedString();
		final int ppos = tp.getPrevPos();
		final int pos = ppos == -1 ? tp.getPos() : ppos;
		final int line = tp.getLine();
		final int col = ppos == -1 ? tp.getCol() : tp.getPrevCol();
		tp.cancelPrevPos();
		final String[] lines = parseString
				.substring(0, Math.min(pos + N + 2, parseString.length()))
				.split("\n");
		final String current = lines[line - 1];
		final String lineBefore = line >= 2 ? (lines[line - 2] + "\n") : "";
		String arrow = "~~~^";
		String pre;
		int start = col - 1 - N;
		if (start <= 0) {
			start = 0;
			pre = "";
			if (col < 4 /* arrow.length */) {
				switch (col) {
				case 1:
					arrow = "^";
					break;
				case 2:
					arrow = "~^";
					break;
				case 3:
					arrow = "~~^";
					break;
				}
			}
		} else {
			pre = "...";
		}
		String post;
		int end = col - 1 + N + 1;
		if (end >= current.length()) {
			end = current.length();
			post = "";
		} else {
			post = "...";
		}

		final String extract = current.substring(start, end);
		final String whites = getWhites(
				pre.length() + col - start - arrow.length());

		return msg + "\n" + "at line " + line + " and column " + col + ":\n"
				+ lineBefore + pre + extract + post + "\n" + whites + arrow;
	}
}
