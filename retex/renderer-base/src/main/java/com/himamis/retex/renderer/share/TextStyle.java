/* TextStyle.java
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

import java.util.HashMap;
import java.util.Map;

public final class TextStyle {

	public final static int NONE = -1;
	public final static int MATHNORMAL = 0;
	public final static int MATHFRAK = 1;
	public final static int MATHCAL = 2;
	public final static int MATHBB = 3;
	public final static int MATHSCR = 4;
	public final static int MATHDS = 5;
	public final static int OLDSTYLENUMS = 6;

	public final static int NUMBERS = 0;
	public final static int CAPITALS = 1;
	public final static int SMALL = 2;
	public final static int UNICODE = 3;

	private final static TextStyle[][] styles = new TextStyle[7][4];

	static {
		add(MATHNORMAL, NUMBERS, Configuration.getFonts().cmr10, (char) 48);
		add(MATHNORMAL, CAPITALS, Configuration.getFonts().cmmi10, (char) 65);
		add(MATHNORMAL, SMALL, Configuration.getFonts().cmmi10, (char) 97);
		add(MATHNORMAL, UNICODE, Configuration.getFonts().cmmi10, (char) 0);
		add(MATHFRAK, NUMBERS, Configuration.getFonts().eufm10, (char) 48);
		add(MATHFRAK, CAPITALS, Configuration.getFonts().eufm10, (char) 65);
		add(MATHFRAK, SMALL, Configuration.getFonts().eufm10, (char) 97);
		add(MATHCAL, CAPITALS, Configuration.getFonts().cmsy10, (char) 65);
		add(MATHBB, CAPITALS, Configuration.getFonts().msbm10, (char) 65);
		add(MATHSCR, CAPITALS, Configuration.getFonts().rsfs10, (char) 65);
		add(MATHDS, CAPITALS, Configuration.getFonts().dsrom10, (char) 65);
		add(OLDSTYLENUMS, CAPITALS, Configuration.getFonts().cmmi10, (char) 48);
	}

	private static Map<String, Integer> names = new HashMap<String, Integer>() {
		{
			put("mathnormal", MATHNORMAL);
			put("mathfrak", MATHFRAK);
			put("mathcal", MATHCAL);
			put("mathbb", MATHBB);
			put("mathscr", MATHSCR);
			put("mathds", MATHDS);
			put("oldstylenums", OLDSTYLENUMS);
		}
	};

	private final FontInfo font;
	private final char start;

	private TextStyle(final FontInfo font, final char start) {
		this.font = font;
		this.start = start;
	}

	public FontInfo getFont() {
		return font;
	}

	public char getStart() {
		return start;
	}

	public static TextStyle[] get(final String style) {
		return styles[TextStyle.getStyle(style)];
	}

	public static int getStyle(final String style) {
		final Integer i = names.get(style);
		if (i != null) {
			return i.intValue();
		}
		return MATHNORMAL;
	}

	public static TextStyle[] get(final int style) {
		return styles[style];
	}

	public static TextStyle getDefault(final int kind) {
		return styles[MATHNORMAL][kind];
	}

	public static TextStyle[] getDefault() {
		return styles[MATHNORMAL];
	}

	public static TextStyle get(final int style, final int type) {
		return styles[style][type];
	}

	private static void add(final int style, final int type,
			final FontInfo fontId, final char start) {
		styles[style][type] = new TextStyle(fontId, start);
	}
}
