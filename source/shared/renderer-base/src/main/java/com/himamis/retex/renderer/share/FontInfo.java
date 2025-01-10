/* FontInfo.java
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

package com.himamis.retex.renderer.share;

import com.himamis.retex.renderer.share.platform.FontAdapter;
import com.himamis.retex.renderer.share.platform.font.Font;

/**
 * Contains all the font information for 1 font.
 */
public class FontInfo {

	/**
	 * Maximum number of character codes in a TeX font.
	 */
	private static final int NUMBER_OF_CHAR_CODES = 256;

	// font
	protected Font font;
	protected final int size;
	protected final String path;
	protected boolean loaded = false;

	protected final double[][] metrics;

	// skew character of the font (used for positioning accents)
	protected final char skewChar;

	// general parameters for this font
	protected final double xHeight;
	protected final double space;
	protected final double quad;

	protected FontInfo bold;
	protected FontInfo roman;
	protected FontInfo ss;
	protected FontInfo tt;
	protected FontInfo it;

	protected CharFont[][] lig;
	protected double[][] kern;
	protected CharFont[] nextLarger;
	protected char[][] extensions;

	// This is the currently loaded character, so that calls to
	// setKern etc do not have to pass it, and allows for better
	// optimization of common code pieces. Should not be used after
	// initMetrics returns
	private char current;

	public FontInfo(int size, String path, int xHeight, int space,
			int quad, int skewChar) {
		this.path = path;
		this.xHeight = xHeight / 1000.;
		this.space = space / 1000.;
		this.quad = quad / 1000.;
		this.skewChar = (char) skewChar;
		this.size = size == 0 ? NUMBER_OF_CHAR_CODES : size;
		this.metrics = new double[this.size][];
	}

	public void setDependencies(FontInfo bold, FontInfo roman, FontInfo ss,
			FontInfo tt, FontInfo it) {
		this.bold = bold == null ? this : bold;
		this.roman = roman == null ? this : roman;
		this.ss = ss == null ? this : ss;
		this.tt = tt == null ? this : tt;
		this.it = it == null ? this : it;
	}

	/**
	 *
	 * @param left
	 *            left character
	 * @param right
	 *            right character
	 * @param k
	 *            kern value
	 */
	public void addKern(final char left, final char right, final double k) {
		if (kern == null) {
			kern = new double[size][];
		}
		if (kern[left] == null) {
			kern[left] = new double[size];
		}
		kern[left][right] = k;
	}

	/**
	 * @param left
	 *            left character
	 * @param right
	 *            right character
	 * @param ligChar
	 *            ligature to replace left and right character
	 */
	public void addLigature(final char left, final char right,
			final char ligChar) {
		if (lig == null) {
			lig = new CharFont[size][];
		}
		if (lig[left] == null) {
			lig[left] = new CharFont[size];
		}
		lig[left][right] = new CharFont(ligChar, this);
	}

	public char[] getExtension(final char c) {
		init();
		if (extensions == null) {
			return null;
		}
		return extensions[c];
	}

	public double getKern(final char left, final char right,
			final double factor) {
		init();
		if (kern == null || kern[left] == null) {
			return 0.;
		}

		return kern[left][right] * factor;
	}

	public CharFont getLigature(final char left, final char right) {
		init();
		if (lig == null || lig[left] == null) {
			return null;
		}
		return lig[left][right];
	}

	public double[] getMetrics(final char c) {
		init();
		return metrics[c];
	}

	public double getWidth(final char c) {
		init();
		return metrics[c][0];
	}

	public double getHeight(final char c) {
		init();
		return metrics[c][1];
	}

	public double getDepth(final char c) {
		init();
		return metrics[c][2];
	}

	public double getItalic(final char c) {
		init();
		return metrics[c][3];
	}

	public CharFont getNextLarger(final char c) {
		init();
		if (nextLarger == null) {
			return null;
		}
		return nextLarger[c];
	}

	/**
	 * @return the skew character of the font (for the correct positioning of
	 *         accents)
	 */
	public double getSkew(final char c, final double factor) {
		init();
		if (skewChar != '\0') {
			return getKern(c, skewChar, factor);
		}
		return 0.;
	}

	public void setExtension(final char c, final char[] ext) {
		if (extensions == null) {
			extensions = new char[size][];
		}
		extensions[c] = ext;
	}

	public void setMetrics(int c, int... metrics) {
		current = (char) c;

		if (metrics.length == 2) {
			setMetrics(current, new double[] {metrics[0] / 1000., metrics[1] / 1000.,
					0, 0});
		} else if (metrics.length == 3) {
			setMetrics(current, new double[] {metrics[0] / 1000., metrics[1] / 1000.,
					metrics[2] / 1000., 0});
		} else {
			setMetrics(current, new double[] {metrics[0] / 1000., metrics[1] / 1000.,
					metrics[2] / 1000., metrics[3] / 1000.});
		}
	}

	public void setNextLarger(FontInfo fi, int nextLarger) {
		setNextLarger(current, (char) nextLarger, fi);
	}

	public void setKern(int... kerns) {
		for (int i = 0; i < kerns.length; i += 2) {
			addKern(current, (char) kerns[i], kerns[i + 1] / 1000.);
		}
	}

	public void setLigatures(int... chars) {
		for (int i = 0; i < chars.length; i += 2) {
			addLigature(current, (char) chars[i], (char) chars[i + 1]);
		}
	}

	public void setExtension(int top, int mid, int rep, int bot) {
		setExtension(current, new char[] {(char) top, (char) mid, (char) rep, (char) bot});
	}

	public void setMetrics(char c, double[] arr) {
		metrics[c] = arr;
	}

	public void setNextLarger(final char c, final char larger,
			final FontInfo fontLarger) {
		if (nextLarger == null) {
			nextLarger = new CharFont[size];
		}
		nextLarger[c] = new CharFont(larger, fontLarger);
	}

	protected final void init() {
		if (!loaded) {
			initMetrics();
			loaded = true;
		}
	}

	protected void initMetrics() {
	}

	public double getQuad(final double factor) {
		return quad * factor;
	}

	public final double getSpace(final double factor) {
		return space * factor;
	}

	public final double getXHeight(final double factor) {
		return xHeight * factor;
	}

	public final boolean hasSpace() {
		return space > TeXFormula.PREC;
	}

	public final char getSkewChar() {
		return skewChar;
	}

	public final FontInfo getBold() {
		return bold;
	}

	public final FontInfo getRoman() {
		return roman;
	}

	public final FontInfo getTt() {
		return tt;
	}

	public final FontInfo getIt() {
		return it;
	}

	public final FontInfo getSs() {
		return ss;
	}

	public final Font getFont() {
		if (font == null) {
			font = new FontAdapter().loadFont(path + ".ttf");
		}
		return font;
	}

	@Override
	public String toString() {
		return "FontInfo: " + path;
	}
}
