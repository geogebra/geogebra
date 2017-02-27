/* DefaultTeXFont.java
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.himamis.retex.renderer.share.character.Character;
import com.himamis.retex.renderer.share.exception.AlphabetRegistrationException;
import com.himamis.retex.renderer.share.exception.FontAlreadyLoadedException;
import com.himamis.retex.renderer.share.exception.ResourceParseException;
import com.himamis.retex.renderer.share.exception.SymbolMappingNotFoundException;
import com.himamis.retex.renderer.share.exception.TextStyleMappingNotFoundException;
import com.himamis.retex.renderer.share.exception.XMLResourceParseException;
import com.himamis.retex.renderer.share.platform.Resource;
import com.himamis.retex.renderer.share.platform.font.Font;

/**
 * The default implementation of the TeXFont-interface. All font information is read from an
 * xml-file.
 */
public class DefaultTeXFont implements TeXFont {

	private static String[] defaultTextStyleMappings;

	/**
	 * No extension part for that kind (TOP,MID,REP or BOT)
	 */
	protected static final int NONE = -1;

	protected final static int NUMBERS = 0;
	protected final static int CAPITALS = 1;
	protected final static int SMALL = 2;
	protected final static int UNICODE = 3;

	// Number of font ids in a single font description file.
	// private static final int NUMBER_OF_FONT_IDS = 256;

	private static Map<String, CharFont[]> textStyleMappings;
	private static Map<String, CharFont> symbolMappings;
	private static FontInfo[] fontInfo = new FontInfo[0];
	private static Map<String, Double> parameters;
	private static Map<String, Number> generalSettings;

	//private static boolean magnificationEnable = true;

	protected static final int TOP = 0, MID = 1, REP = 2, BOT = 3;

	protected static final int WIDTH = 0, HEIGHT = 1, DEPTH = 2, IT = 3;

	public static List<Character.UnicodeBlock> loadedAlphabets = new ArrayList<Character.UnicodeBlock>();
	public static Map<Character.UnicodeBlock, AlphabetRegistration> registeredAlphabets = new HashMap<Character.UnicodeBlock, AlphabetRegistration>();

	protected double factor = 1;

	public boolean isBold = false;
	public boolean isRoman = false;
	public boolean isSs = false;
	public boolean isTt = false;
	public boolean isIt = false;

	static {
		DefaultTeXFontParser parser = new DefaultTeXFontParser();
		// load LATIN block
		loadedAlphabets.add(Character.UnicodeBlock.of('a'));
		// fonts + font descriptions
		fontInfo = parser.parseFontDescriptions(fontInfo);
		// general font parameters
		parameters = parser.parseParameters();
		// text style mappings
		textStyleMappings = parser.parseTextStyleMappings();
		// default text style : style mappings
		defaultTextStyleMappings = parser.parseDefaultTextStyleMappings();
		// symbol mappings
		symbolMappings = parser.parseSymbolMappings();
		// general settings
		generalSettings = parser.parseGeneralSettings();
		generalSettings.put("textfactor", 1);

		// check if mufontid exists
		int muFontId = generalSettings.get(DefaultTeXFontParser.MUFONTID_ATTR).intValue();
		if (muFontId < 0 || muFontId >= fontInfo.length || fontInfo[muFontId] == null) {
			throw new XMLResourceParseException(DefaultTeXFontParser.RESOURCE_NAME,
					DefaultTeXFontParser.GEN_SET_EL, DefaultTeXFontParser.MUFONTID_ATTR,
					"contains an unknown font id!");
		}
	}

	private final double size; // standard size
	
	public static Font getFont(int fontId) {
		FontInfo info = fontInfo[fontId];
		Font font = info.getFont();
		return font;
	}

	public DefaultTeXFont(double pointSize) {
		size = pointSize;
	}

	public DefaultTeXFont(double pointSize, boolean b, boolean rm, boolean ss, boolean tt, boolean it) {
		this(pointSize, 1, b, rm, ss, tt, it);
	}

	public DefaultTeXFont(double pointSize, double f, boolean b, boolean rm, boolean ss, boolean tt, boolean it) {
		size = pointSize;
		factor = f;
		isBold = b;
		isRoman = rm;
		isSs = ss;
		isTt = tt;
		isIt = it;
	}

	public static void addTeXFontDescription(String file) throws ResourceParseException {

		Object in = new Resource().loadResource(file);
		addTeXFontDescription(in, file);
	}

	public static void addTeXFontDescription(Object in, String name) throws ResourceParseException {
		DefaultTeXFontParser dtfp = new DefaultTeXFontParser(in, name);
		fontInfo = dtfp.parseFontDescriptions(fontInfo);
		textStyleMappings.putAll(dtfp.parseTextStyleMappings());
		symbolMappings.putAll(dtfp.parseSymbolMappings());
	}

	public static void addTeXFontDescription(Object base, Object in, String name)
			throws ResourceParseException {
		DefaultTeXFontParser dtfp = new DefaultTeXFontParser(base, in, name);
		fontInfo = dtfp.parseFontDescriptions(fontInfo);
		dtfp.parseExtraPath();
		textStyleMappings.putAll(dtfp.parseTextStyleMappings());
		symbolMappings.putAll(dtfp.parseSymbolMappings());
	}

	public static void addAlphabet(Character.UnicodeBlock alphabet, Object inlanguage, String language,
			Object insymbols, String symbols, Object inmappings, String mappings)
			throws ResourceParseException {
		if (!loadedAlphabets.contains(alphabet)) {
			addTeXFontDescription(inlanguage, language);
			SymbolAtom.addSymbolAtom(insymbols, symbols);
			TeXFormula.addSymbolMappings(inmappings, mappings);
			loadedAlphabets.add(alphabet);
		}
	}

	public static void addAlphabet(Object base, Character.UnicodeBlock[] alphabet, String language)
			throws ResourceParseException {
		boolean b = false;
		for (int i = 0; !b && i < alphabet.length; i++) {
			b = loadedAlphabets.contains(alphabet[i]) || b;
		}
		if (!b) {
			TeXParser.isLoading = true;
			Object res = new Resource().loadResource(base, language);
			System.out.println(
					"ADDING ALPHABET " + language + ":" + res + "," + base);
			addTeXFontDescription(base, res, language);
			for (int i = 0; i < alphabet.length; i++) {
				loadedAlphabets.add(alphabet[i]);
			}
			System.out.println("ADDED");
			TeXParser.isLoading = false;
		}
	}

	public static void addAlphabet(Character.UnicodeBlock alphabet, String name) {
		String lg = "fonts/" + name + "/language_" + name + ".xml";
		String sym = "fonts/" + name + "/symbols_" + name + ".xml";
		String map = "fonts/" + name + "/mappings_" + name + ".xml";

		Resource resource = new Resource();
		try {
			DefaultTeXFont.addAlphabet(alphabet, resource.loadResource(TeXFormula.class, lg),
					lg, resource.loadResource(TeXFormula.class, sym), sym,
					resource.loadResource(TeXFormula.class, map), map);
		} catch (FontAlreadyLoadedException e) {
		}
	}

	public static void addAlphabet(AlphabetRegistration reg) {
		try {
			if (reg != null) {
				DefaultTeXFont.addAlphabet(reg.getPackage(), reg.getUnicodeBlock(), reg.getTeXFontFileName());
			}
		} catch (FontAlreadyLoadedException e) {
		} catch (AlphabetRegistrationException e) {
			System.err.println(e.toString());
		}
	}

	public static void registerAlphabet(AlphabetRegistration reg) {
		Character.UnicodeBlock[] blocks = reg.getUnicodeBlock();
		for (int i = 0; i < blocks.length; i++) {
			registeredAlphabets.put(blocks[i], reg);
		}
	}

	@Override
	public TeXFont copy() {
		return new DefaultTeXFont(size, factor, isBold, isRoman, isSs, isTt, isIt);
	}

	@Override
	public TeXFont deriveFont(double size) {
		return new DefaultTeXFont(size, factor, isBold, isRoman, isSs, isTt, isIt);
	}

	@Override
	public TeXFont scaleFont(double factor) {
		return new DefaultTeXFont(size, factor, isBold, isRoman, isSs, isTt, isIt);
	}

	@Override
	public double getScaleFactor() {
		return factor;
	}

	@Override
	public double getAxisHeight(int style) {
		return getParameter("axisheight") * getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	@Override
	public double getBigOpSpacing1(int style) {
		return getParameter("bigopspacing1") * getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	@Override
	public double getBigOpSpacing2(int style) {
		return getParameter("bigopspacing2") * getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	@Override
	public double getBigOpSpacing3(int style) {
		return getParameter("bigopspacing3") * getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	@Override
	public double getBigOpSpacing4(int style) {
		return getParameter("bigopspacing4") * getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	@Override
	public double getBigOpSpacing5(int style) {
		return getParameter("bigopspacing5") * getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	private Char getChar(char c, CharFont[] cf, int style) {
		int kind, offset;
		if (c >= '0' && c <= '9') {
			kind = NUMBERS;
			offset = c - '0';
		} else if (c >= 'a' && c <= 'z') {
			kind = SMALL;
			offset = c - 'a';
		} else if (c >= 'A' && c <= 'Z') {
			kind = CAPITALS;
			offset = c - 'A';
		} else {
			kind = UNICODE;
			offset = c;
		}

		// if the mapping for the character's range, then use the default style
		if (cf[kind] == null) {
			return getDefaultChar(c, style);
		}
		return getChar(
				new CharFont((char) (cf[kind].c + offset), cf[kind].fontId),
				style);
	}

	@Override
	public Char getChar(char c, String textStyle, int style) throws TextStyleMappingNotFoundException {
		Object mapping = textStyleMappings.get(textStyle);
		if (mapping == null) {
			throw new TextStyleMappingNotFoundException(textStyle);
		}
		return getChar(c, (CharFont[]) mapping, style);
	}

	@Override
	public Char getChar(CharFont cf0, int style) {
		double fsize = getSizeFactor(style);
		int id = isBold ? cf0.boldFontId : cf0.fontId;
		FontInfo info = fontInfo[id];
		CharFont cf = cf0;
		if (isBold && cf.fontId == cf.boldFontId) {
			id = info.getBoldId();
			info = fontInfo[id];
			cf = new CharFont(cf.c, id, style);
		}
		if (isRoman) {
			id = info.getRomanId();
			info = fontInfo[id];
			cf = new CharFont(cf.c, id, style);
		}
		if (isSs) {
			id = info.getSsId();
			info = fontInfo[id];
			cf = new CharFont(cf.c, id, style);
		}
		if (isTt) {
			id = info.getTtId();
			info = fontInfo[id];
			cf = new CharFont(cf.c, id, style);
		}
		if (isIt) {
			id = info.getItId();
			info = fontInfo[id];
			cf = new CharFont(cf.c, id, style);
		}
		Font font = info.getFont();
		return new Char(cf.c, font, id, getMetrics(cf, factor * fsize));
	}

	@Override
	public Char getChar(String symbolName, int style) throws SymbolMappingNotFoundException {
		Object obj = symbolMappings.get(symbolName);
		if (obj == null) {// no symbol mapping found!
			throw new SymbolMappingNotFoundException(symbolName);
		}
		return getChar((CharFont) obj, style);
	}

	@Override
	public Char getDefaultChar(char c, int style) {
		// these default text style mappings will allways exist,
		// because it's checked during parsing
		if (c >= '0' && c <= '9') {
			return getChar(c, defaultTextStyleMappings[NUMBERS], style);
		} else if (c >= 'a' && c <= 'z') {
			return getChar(c, defaultTextStyleMappings[SMALL], style);
		} else {
			return getChar(c, defaultTextStyleMappings[CAPITALS], style);
		}
	}

	@Override
	public double getDefaultRuleThickness(int style) {
		return getParameter("defaultrulethickness") * getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	@Override
	public double getDenom1(int style) {
		return getParameter("denom1") * getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	@Override
	public double getDenom2(int style) {
		return getParameter("denom2") * getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	@Override
	public Extension getExtension(Char c, int style) {
		Font f = c.getFont();
		int fc = c.getFontCode();
		double s = getSizeFactor(style);

		// construct Char for every part
		FontInfo info = fontInfo[fc];
		int[] ext = info.getExtension(c.getChar());
		Char[] parts = new Char[ext.length];
		for (int i = 0; i < ext.length; i++) {
			if (ext[i] == NONE) {
				parts[i] = null;
			} else {
				parts[i] = new Char((char) ext[i], f, fc, getMetrics(new CharFont((char) ext[i], fc), s));
			}
		}

		return new Extension(parts[TOP], parts[MID], parts[REP], parts[BOT]);
	}

	@Override
	public double getKern(CharFont left, CharFont right, int style) {
		if (left.fontId == right.fontId) {
			FontInfo info = fontInfo[left.fontId];
			return info.getKern(left.c, right.c, getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT);
		}
		return 0;
	}

	@Override
	public CharFont getLigature(CharFont left, CharFont right) {
		if (left.fontId == right.fontId) {
			FontInfo info = fontInfo[left.fontId];
			return info.getLigature(left.c, right.c);
		}
		return null;
	}

	private static Metrics getMetrics(CharFont cf, double size) {
		FontInfo info = fontInfo[cf.fontId];
		double[] m = info.getMetrics(cf.c);
		if (m == null) {
			return new Metrics(1, 1, 0, 0, size * TeXFormula.PIXELS_PER_POINT,
					size);
		}
		return new Metrics(m[WIDTH], m[HEIGHT], m[DEPTH], m[IT], size * TeXFormula.PIXELS_PER_POINT, size);
	}

	@Override
	public int getMuFontId() {
		return generalSettings.get(DefaultTeXFontParser.MUFONTID_ATTR).intValue();
	}

	@Override
	public Char getNextLarger(Char c, int style) {
		FontInfo info = fontInfo[c.getFontCode()];
		CharFont ch = info.getNextLarger(c.getChar());
		FontInfo newInfo = fontInfo[ch.fontId];
		return new Char(ch.c, newInfo.getFont(), ch.fontId, getMetrics(ch, getSizeFactor(style)));
	}

	@Override
	public double getNum1(int style) {
		return getParameter("num1") * getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	@Override
	public double getNum2(int style) {
		return getParameter("num2") * getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	@Override
	public double getNum3(int style) {
		return getParameter("num3") * getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	@Override
	public double getQuad(int style, int fontCode) {
		FontInfo info = fontInfo[fontCode];
		return info.getQuad(getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT);
	}

	@Override
	public double getSize() {
		return size;
	}

	@Override
	public double getSkew(CharFont cf, int style) {
		FontInfo info = fontInfo[cf.fontId];
		char skew = info.getSkewChar();
		if (skew == -1) {
			return 0;
		}
		return getKern(cf, new CharFont(skew, cf.fontId), style);
	}

	@Override
	public double getSpace(int style) {
		int spaceFontId = generalSettings.get(DefaultTeXFontParser.SPACEFONTID_ATTR).intValue();
		FontInfo info = fontInfo[spaceFontId];
		return info.getSpace(getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT);
	}

	@Override
	public double getSub1(int style) {
		return getParameter("sub1") * getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	@Override
	public double getSub2(int style) {
		return getParameter("sub2") * getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	@Override
	public double getSubDrop(int style) {
		return getParameter("subdrop") * getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	@Override
	public double getSup1(int style) {
		return getParameter("sup1") * getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	@Override
	public double getSup2(int style) {
		return getParameter("sup2") * getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	@Override
	public double getSup3(int style) {
		return getParameter("sup3") * getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	@Override
	public double getSupDrop(int style) {
		return getParameter("supdrop") * getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	@Override
	public double getXHeight(int style, int fontCode) {
		FontInfo info = fontInfo[fontCode];
		return info.getXHeight(getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT);
	}

	@Override
	public double getEM(int style) {
		return getSizeFactor(style) * TeXFormula.PIXELS_PER_POINT;
	}

	@Override
	public boolean hasNextLarger(Char c) {
		FontInfo info = fontInfo[c.getFontCode()];
		return (info.getNextLarger(c.getChar()) != null);
	}

	@Override
	public void setBold(boolean bold) {
		isBold = bold;
	}

	@Override
	public boolean getBold() {
		return isBold;
	}

	@Override
	public void setRoman(boolean rm) {
		isRoman = rm;
	}

	@Override
	public boolean getRoman() {
		return isRoman;
	}

	@Override
	public void setTt(boolean tt) {
		isTt = tt;
	}

	@Override
	public boolean getTt() {
		return isTt;
	}

	@Override
	public void setIt(boolean it) {
		isIt = it;
	}

	@Override
	public boolean getIt() {
		return isIt;
	}

	@Override
	public void setSs(boolean ss) {
		isSs = ss;
	}

	@Override
	public boolean getSs() {
		return isSs;
	}

	@Override
	public boolean hasSpace(int font) {
		FontInfo info = fontInfo[font];
		return info.hasSpace();
	}

	@Override
	public boolean isExtensionChar(Char c) {
		FontInfo info = fontInfo[c.getFontCode()];
		return info.getExtension(c.getChar()) != null;
	}

//	public static void setMathSizes(double ds, double ts, double ss, double sss) {
//		if (magnificationEnable) {
//			generalSettings.put("scriptfactor", Math.abs(ss / ds));
//			generalSettings.put("scriptscriptfactor", Math.abs(sss / ds));
//			generalSettings.put("textfactor", Math.abs(ts / ds));
//			TeXIcon.defaultSize = Math.abs(ds);
//		}
//	}

//	public static void setMagnification(double mag) {
//		if (magnificationEnable) {
//			TeXIcon.magFactor = mag / 1000f;
//		}
//	}

//	public static void enableMagnification(boolean b) {
//		magnificationEnable = b;
//	}

	private static double getParameter(String parameterName) {
		Object param = parameters.get(parameterName);
		if (param == null) {
			return 0;
		}
		return ((Double) param).doubleValue();
	}

	public static double getSizeFactor(int style) {
		if (style < TeXConstants.STYLE_TEXT) {
			return 1;
		} else if (style < TeXConstants.STYLE_SCRIPT) {
			return generalSettings.get("textfactor").doubleValue();
		} else if (style < TeXConstants.STYLE_SCRIPT_SCRIPT) {
			return generalSettings.get("scriptfactor").doubleValue();
		} else {
			return generalSettings.get("scriptscriptfactor").doubleValue();
		}
	}
}
