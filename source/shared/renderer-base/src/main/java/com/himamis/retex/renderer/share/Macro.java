/* Macro.java
 * =========================================================================
 * This file is part of the JLaTeXMath Library - http://forge.scilab.org/jlatexmath
 *
 * Copyright (C) 2018 DENIZET Calixte
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

package com.himamis.retex.renderer.share;

import java.util.ArrayList;

import com.himamis.retex.renderer.share.exception.ParseException;

public class Macro {

	private final int nargs;
	private final String code;
	private ArrayList<String> chunks = null;
	private ArrayList<Integer> posArgs = null;
	private int totalLength = 0;

	public Macro(final String code, final int nargs) {
		this.code = code;
		this.nargs = nargs;
	}

	public int getNArgs() {
		return nargs;
	}

	public String get(final TeXParser tp, final ArrayList<String> args) {
		if (chunks == null) {
			split(tp);
		}
		int len = totalLength;
		for (String arg : args) {
			len += arg.length();
		}
		final StringBuffer buf = new StringBuffer(len);
		final int s = posArgs.size();
		for (int i = 0; i < s; ++i) {
			buf.append(chunks.get(i));
			buf.append(args.get(posArgs.get(i)));
		}
		buf.append(chunks.get(s));

		return buf.toString();
	}

	public void split(final TeXParser tp) {
		final int len = code.length();
		int pos = 0;
		int fpos = 0;
		chunks = new ArrayList<String>();
		posArgs = new ArrayList<Integer>();
		while (pos < len) {
			char c = code.charAt(pos);
			if (c == '#') {
				++pos;
				if (pos < len) {
					char n = code.charAt(pos++);
					if (n >= '1' && n <= '9') {
						totalLength += (pos - 2) - fpos;
						chunks.add(code.substring(fpos, pos - 2));
						int x = n - '0';
						while (pos < len) {
							n = code.charAt(pos);
							if (n >= '0' && n <= '9') {
								x = 10 * x + n - '0';
								++pos;
							} else {
								break;
							}
						}
						if (x > nargs) {
							// TODO: se referer a la string definissant la
							// macro, plutot
							// qu'a tp pr localiser l'erreur
							throw new ParseException(tp,
									"Argument number greater than the number of arguments");
						}
						--x;
						posArgs.add(x);
						fpos = pos;
					}
				}
			} else if (c == '\\') {
				pos += 2;
			} else if (c == '%') {
				while (++pos < len) {
					c = code.charAt(pos);
					if (c == '\n') {
						++pos;
						break;
					}
				}
			} else {
				++pos;
			}
		}
		totalLength += len - fpos;
		chunks.add(code.substring(fpos, len));
	}
}
