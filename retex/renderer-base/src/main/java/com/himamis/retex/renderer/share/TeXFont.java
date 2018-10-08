/* TeXFont.java
 * =========================================================================
 * This file is originally part of the JMathTeX Library - http://jmathtex.sourceforge.net
 *
 * Copyright (C) 2004-2007 Universiteit Gent
 * Copyright (C) 2009 DENIZET Calixte
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

import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.font.Font;

/**
 * The default implementation of the TeXFont-interface. All font information is
 * read from an xml-file.
 */
public class TeXFont {

	/**
	 * No extension part for that kind (TOP,MID,REP or BOT)
	 */
	public static final char NONE = '\0';

	private final static int[] OFFSETS = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
			12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28,
			29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45,
			46, 47, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 58, 59, 60, 61, 62, 63, 64, 0,
			1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
			20, 21, 22, 23, 24, 25, 91, 92, 93, 94, 95, 96, 0, 1, 2, 3, 4, 5, 6,
			7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24,
			25 };
	private final static int[] KINDS = { 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
			3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
			3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3,
			3, 3, 3, 3, 3, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2,
			2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };

	protected final static int NUMBERS = 0;
	protected final static int CAPITALS = 1;
	protected final static int SMALL = 2;
	protected final static int UNICODE = 3;

	public static final FontInfo MUFONT = Configuration.getFonts().cmsy10;
	public static final FontInfo SPACEFONT = Configuration.getFonts().cmr10;

	public static final double TEXTFACTOR = 1.0;
	public static final double SCRIPTFACTOR = 0.7;
	public static final double SCRIPTSCRIPTFACTOR = 0.5;

	private static final double AXISHEIGHT = 0.25;
	private static final double BIGOPSPACING1 = 0.111112;
	private static final double BIGOPSPACING2 = 0.166667;
	private static final double BIGOPSPACING3 = 0.2;
	private static final double BIGOPSPACING4 = 0.6;
	private static final double BIGOPSPACING5 = 0.1;
	private static final double DEFAULTRULETHICKNESS = 0.039999;
	private static final double DENOM1 = 0.685951;
	private static final double DENOM2 = 0.344841;
	private static final double NUM1 = 0.676508;
	private static final double NUM2 = 0.393732;
	private static final double NUM3 = 0.443731;
	private static final double SUB1 = 0.15;
	private static final double SUB2 = 0.247217;
	private static final double SUBDROP = 0.05;
	private static final double SUP1 = 0.412892;
	private static final double SUP2 = 0.362892;
	private static final double SUP3 = 0.288889;
	private static final double SUPDROP = 0.386108;

	protected static final int TOP = 0, MID = 1, REP = 2, BOT = 3;

	protected static final int WIDTH = 0, HEIGHT = 1, DEPTH = 2, IT = 3;

	public static final int SERIF = 0;
	public static final int SANSSERIF = 1;
	public static final int BOLD = 2;
	public static final int ITALIC = 4;
	public static final int ROMAN = 8;
	public static final int TYPEWRITER = 16;

	protected double factor = 1;

	public boolean isBold = false;
	public boolean isRoman = false;
	public boolean isSs = false;
	public boolean isTt = false;
	public boolean isIt = false;

	private final double size; // standard size

	public TeXFont(double pointSize) {
		size = pointSize;
	}

	public TeXFont(double pointSize, boolean b, boolean rm, boolean ss,
			boolean tt, boolean it) {
		this(pointSize, 1, b, rm, ss, tt, it);
	}

	public TeXFont(double pointSize, double f, boolean b, boolean rm,
			boolean ss, boolean tt, boolean it) {
		size = pointSize;
		factor = f;
		isBold = b;
		isRoman = rm;
		isSs = ss;
		isTt = tt;
		isIt = it;
	}

	public TeXFont copy() {
		return new TeXFont(size, factor, isBold, isRoman, isSs, isTt, isIt);
	}

	public TeXFont deriveFont(double size) {
		return new TeXFont(size, factor, isBold, isRoman, isSs, isTt, isIt);
	}

	public TeXFont scaleFont(double factor) {
		return new TeXFont(size, factor, isBold, isRoman, isSs, isTt, isIt);
	}

	public double getScaleFactor() {
		return factor;
	}

	public double getAxisHeight(int style) {
		return AXISHEIGHT * getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	public double getBigOpSpacing1(int style) {
		return BIGOPSPACING1 * getSizeFactor(style)
				* TeXFormula.PIXELS_PER_POINT;
	}

	public double getBigOpSpacing2(int style) {
		return BIGOPSPACING2 * getSizeFactor(style)
				* TeXFormula.PIXELS_PER_POINT;
	}

	public double getBigOpSpacing3(int style) {
		return BIGOPSPACING3 * getSizeFactor(style)
				* TeXFormula.PIXELS_PER_POINT;
	}

	public double getBigOpSpacing4(int style) {
		return BIGOPSPACING4 * getSizeFactor(style)
				* TeXFormula.PIXELS_PER_POINT;
	}

	public double getBigOpSpacing5(int style) {
		return BIGOPSPACING5 * getSizeFactor(style)
				* TeXFormula.PIXELS_PER_POINT;
	}

	private Char getChar(char c, TextStyle[] styles, int style) {
		int kind, offset;
		if (c < OFFSETS.length) {
			kind = KINDS[c];
			offset = OFFSETS[c];
		} else {
			kind = TextStyle.UNICODE;
			offset = c;
		}

		// if the mapping for the character's range, then use the default style
		if (styles[kind] == null) {
			styles = TextStyle.getDefault();
		}
		return getChar(new CharFont((char) (styles[kind].getStart() + offset),
				styles[kind].getFont()), style);
	}

	public Char getChar(char c, int style) {
		return getChar(c, TextStyle.get(TextStyle.MATHNORMAL), style);
	}

	public Char getChar(char c, int textStyle, int style) {
		return getChar(c, TextStyle.get(textStyle), style);
	}

	public Char getChar(CharFont cf0, int style) {
		double fsize = getSizeFactor(style);
		FontInfo info = isBold ? cf0.boldFontInfo : cf0.fontInfo;
		CharFont cf = cf0;
		if (isBold && cf.fontInfo == cf.boldFontInfo) {
			info = info.getBold();
			cf = new CharFont(cf.c, info);
		}
		if (isRoman) {
			info = info.getRoman();
			cf = new CharFont(cf.c, info);
		}
		if (isSs) {
			info = info.getSs();
			cf = new CharFont(cf.c, info);
		}
		if (isTt) {
			info = info.getTt();
			cf = new CharFont(cf.c, info);
		}
		if (isIt) {
			info = info.getIt();
			cf = new CharFont(cf.c, info);
		}
		Font font = info.getFont();
		return new Char(cf.c, font, info, getMetrics(cf, factor * fsize));
	}

	public Char getChar(String symbolName, int style) {
		CharFont obj = Configuration.getFontMapping().get(symbolName);
		if (obj == null) {// no symbol mapping found!

			// XXX
			FactoryProvider.getInstance()
					.debug("no symbol mapping found in getChar()");
			return null;
			// throw new SymbolMappingNotFoundException(symbolName);
		}
		return getChar(obj, style);
	}

	public double getDefaultRuleThickness(int style) {
		return DEFAULTRULETHICKNESS * getSizeFactor(style)
				* TeXFormula.PIXELS_PER_POINT;
	}

	public double getDenom1(int style) {
		return DENOM1 * getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	public double getDenom2(int style) {
		return DENOM2 * getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	public Extension getExtension(Char c, int style) {
		Font f = c.getFont();
		FontInfo info = c.getFontInfo();
		double s = getSizeFactor(style);

		char[] ext = info.getExtension(c.getChar());
		Char[] parts = new Char[ext.length];
		for (int i = 0; i < ext.length; i++) {
			if (ext[i] == NONE) {
				parts[i] = null;
			} else {
				parts[i] = new Char(ext[i], f, info,
						getMetrics(new CharFont(ext[i], info), s));
			}
		}

		return new Extension(parts[TOP], parts[MID], parts[REP], parts[BOT]);
	}

	public double getKern(CharFont left, CharFont right, int style) {
		if (left.fontInfo == right.fontInfo) {
			FontInfo info = left.fontInfo;
			return info.getKern(left.c, right.c,
					getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT);
		}
		return 0;
	}

	public CharFont getLigature(CharFont left, CharFont right) {
		if (left.fontInfo == right.fontInfo) {
			FontInfo info = left.fontInfo;
			return info.getLigature(left.c, right.c);
		}
		return null;
	}

	private static Metrics getMetrics(CharFont cf, double size) {
		FontInfo info = cf.fontInfo;
		double[] m = info.getMetrics(cf.c);
		if (m == null) {
			return new Metrics(1, 1, 0, 0, size * TeXFormula.PIXELS_PER_POINT,
					size);
		}
		return new Metrics(m[WIDTH], m[HEIGHT], m[DEPTH], m[IT],
				size * TeXFormula.PIXELS_PER_POINT, size);
	}

	public Char getNextLarger(Char c, int style) {
		FontInfo info = c.getFontInfo();
		CharFont ch = info.getNextLarger(c.getChar());
		FontInfo newInfo = ch.fontInfo;
		return new Char(ch.c, newInfo.getFont(), ch.fontInfo,
				getMetrics(ch, getSizeFactor(style)));
	}

	public double getNum1(int style) {
		return NUM1 * getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	public double getNum2(int style) {
		return NUM2 * getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	public double getNum3(int style) {
		return NUM3 * getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	public double getQuad(int style, FontInfo info) {
		return info.getQuad(getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT);
	}

	public double getQuad(int style) {
		return getQuad(style, MUFONT);
	}

	public double getSize() {
		return size;
	}

	public double getSkew(CharFont cf, int style) {
		if (cf.fontInfo.skewChar == -1) {
			return 0;
		}
		return getKern(cf, new CharFont(cf.fontInfo.skewChar, cf.fontInfo),
				style);
	}

	public double getSpace(int style) {
		return SPACEFONT
				.getSpace(getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT);
	}

	public double getSub1(int style) {
		return SUB1 * getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	public double getSub2(int style) {
		return SUB2 * getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	public double getSubDrop(int style) {
		return SUBDROP * getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	public double getSup1(int style) {
		return SUP1 * getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	public double getSup2(int style) {
		return SUP2 * getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	public double getSup3(int style) {
		return SUP3 * getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	public double getSupDrop(int style) {
		return SUPDROP * getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	public double getXHeight(int style, FontInfo info) {
		return info
				.getXHeight(getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT);
	}

	public double getEM(int style) {
		return getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	public boolean hasNextLarger(Char c) {
		FontInfo info = c.getFontInfo();
		return (info.getNextLarger(c.getChar()) != null);
	}

	public void setBold(boolean bold) {
		isBold = bold;
	}

	public boolean getBold() {
		return isBold;
	}

	public void setRoman(boolean rm) {
		isRoman = rm;
	}

	public boolean getRoman() {
		return isRoman;
	}

	public void setTt(boolean tt) {
		isTt = tt;
	}

	public boolean getTt() {
		return isTt;
	}

	public void setIt(boolean it) {
		isIt = it;
	}

	public boolean getIt() {
		return isIt;
	}

	public void setSs(boolean ss) {
		isSs = ss;
	}

	public boolean getSs() {
		return isSs;
	}

	public boolean hasSpace(FontInfo info) {
		return info.hasSpace();
	}

	public boolean isExtensionChar(Char c) {
		FontInfo info = c.getFontInfo();
		return info.getExtension(c.getChar()) != null;
	}

	public static double getSizeFactor(int style) {
		if (style < TeXConstants.STYLE_TEXT) {
			return 1;
		} else if (style < TeXConstants.STYLE_SCRIPT) {
			return TEXTFACTOR;
		} else if (style < TeXConstants.STYLE_SCRIPT_SCRIPT) {
			return SCRIPTFACTOR;
		} else {
			return SCRIPTSCRIPTFACTOR;
		}
	}

	public double getMHeight(int style) {
		return TextStyle.getDefault(TextStyle.CAPITALS).getFont().getHeight('M')
				* getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	public double getDefaultXHeight(int style) {
		return SPACEFONT
				.getXHeight(getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT);
	}
}
