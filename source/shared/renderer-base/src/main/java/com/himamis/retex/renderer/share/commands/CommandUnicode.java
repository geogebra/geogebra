/* CommandUnicode.java
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

package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.HeightDepthAtom;
import com.himamis.retex.renderer.share.JavaFontRenderingAtom;
import com.himamis.retex.renderer.share.TeXLength;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.Unit;
import com.himamis.retex.renderer.share.exception.ParseException;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.font.Font;

public class CommandUnicode extends Command {

	@Override
	public boolean init(TeXParser tp) {
		final String opt1 = tp.getOptionAsString();
		final String opt2 = tp.getOptionAsString();
		final int c = tp.getArgAsCharFromCode();
		if (c == 0) {
			throw new ParseException(tp, "Invalid character in \\unicode: 0.");
		}
		TeXLength[] hd = null;
		Font font = null;
		String fontName = null;

		if (isHD(opt1)) {
			hd = getHD(opt1);
			fontName = opt2;
		} else if (isHD(opt2)) {
			hd = getHD(opt2);
			fontName = opt1;
		}

		if (hd == null) {
			if (!opt1.isEmpty()) {
				fontName = opt1;
			} else if (!opt2.isEmpty()) {
				fontName = opt2;
			}
		}

		if (fontName != null && !fontName.isEmpty()) {
			font = FactoryProvider.getInstance().getFontFactory()
					.createFont(fontName, Font.PLAIN, 10);
			if (!font.canDisplay(c)) {
				final String s = new String(new int[] { c }, 0, 1);
				throw new ParseException(tp, "The font " + fontName
						+ " can't display char " + s + " (code " + c + ")");
			}
		}

		if (font == null) {
			if (hd == null) {
				if (c <= 0xFFFF) {
					final char ch = (char) c;
					if ((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z')
							|| (ch >= 'A' && ch <= 'Z')) {
						tp.convertASCIIChar(ch, true);
					} else {
						tp.convertCharacter(ch, true);
					}
				} else {
					tp.convertCharacter(c);
				}
			} else {
				Atom atom;
				if (c <= 0xFFFF) {
					final char ch = (char) c;
					if ((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z')
							|| (ch >= 'A' && ch <= 'Z')) {
						atom = tp.convertASCIICharToAtom(ch, true);
					} else {
						atom = tp.getCharMapping().getAtom(ch, tp.isMathMode());
						if (atom == null) {
							atom = new JavaFontRenderingAtom(
									String.valueOf(ch));
						}
					}
				} else {
					atom = new JavaFontRenderingAtom(
							new String(new int[] { c }, 0, 1));
				}
				tp.addToConsumer(new HeightDepthAtom(hd[0], hd[1], atom));
			}
			return false;
		}

		String s;
		if (c <= 0xFFFF) {
			s = String.valueOf((char) c);
		} else {
			s = new String(new int[] { c }, 0, 1);
		}
		Atom a = new JavaFontRenderingAtom(s, font);
		if (hd != null) {
			a = new HeightDepthAtom(hd[0], hd[1], a);
		}
		tp.addToConsumer(a);

		return false;
	}

	private static boolean isHD(final String s) {
		if (!s.isEmpty()) {
			for (int i = 0; i < s.length(); ++i) {
				final char c = s.charAt(i);
				if (c == ',' || c == ';') {
					// we've a couple (height, depth)
					return true;
				}
			}
		}
		return false;
	}

	private static TeXLength[] getHD(final String s) {
		final TeXParser tp = new TeXParser();
		tp.setParseString(s);
		tp.skipPureWhites();
		final TeXLength[] hd = new TeXLength[2];
		hd[0] = tp.getLength(Unit.EM);
		tp.skipSeparators(",;");
		hd[1] = tp.getLength(Unit.EM);

		return hd;
	}
}