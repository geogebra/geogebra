/* UniFontInfo.java
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

/**
 * Contains all the font information for a font.
 */
public class UniFontInfo extends FontInfo {

	private final Map<Character, Character> unicode;

	public UniFontInfo(int size, String path, int xHeight, int space,
			int quad, int skewChar) {
		super(size, path, xHeight, space, quad, skewChar);
		this.unicode = new HashMap<>(size);
	}

	@Override
	public void addKern(final char left, final char right, final double k) {
		super.addKern(get(left), get(right), k);
	}

	@Override
	public double getKern(final char left, final char right,
			final double factor) {
		init();
		if (kern == null) {
			return 0.;
		}
		final char l = unicode.get(left);
		if (kern[l] == null) {
			return 0.;
		}
		return kern[l][unicode.get(right)] * factor;
	}

	@Override
	public void addLigature(final char left, final char right,
			final char ligChar) {
		super.addLigature(get(left), get(right), ligChar);
	}

	@Override
	public CharFont getLigature(final char left, final char right) {
		init();
		if (lig == null) {
			return null;
		}
		final char l = unicode.get(left);
		if (lig[l] == null) {
			return null;
		}
		return lig[l][unicode.get(right)];
	}

	@Override
	public char[] getExtension(final char c) {
		init();
		if (extensions == null) {
			return null;
		}
		return extensions[unicode.get(c)];
	}

	@Override
	public double[] getMetrics(final char c) {
		init();
		return metrics[unicode.get(c)];
	}

	@Override
	public double getWidth(final char c) {
		init();
		return metrics[unicode.get(c)][0];
	}

	@Override
	public double getHeight(final char c) {
		init();
		return metrics[unicode.get(c)][1];
	}

	@Override
	public double getDepth(final char c) {
		init();
		return metrics[unicode.get(c)][2];
	}

	@Override
	public double getItalic(final char c) {
		init();
		return metrics[unicode.get(c)][3];
	}

	@Override
	public CharFont getNextLarger(final char c) {
		init();
		if (nextLarger == null) {
			return null;
		}
		return nextLarger[unicode.get(c)];
	}

	@Override
	public void setExtension(final char c, final char[] ext) {
		super.setExtension(get(c), ext);
	}

	@Override
	public void setMetrics(final char c, final double[] arr) {
		super.setMetrics(get(c), arr);
	}

	@Override
	public void setNextLarger(final char c, final char larger,
			final FontInfo fontLarger) {
		super.setNextLarger(get(c), larger, fontLarger);
	}

	@Override
	public String toString() {
		return "UniFontInfo: " + path;
	}

	private char get(final char c) {
		final Character ch = unicode.get(c);
		if (ch == null) {
			final char s = (char) unicode.size();
			unicode.put(c, s);
			return s;
		}
		return ch.charValue();
	}
}
