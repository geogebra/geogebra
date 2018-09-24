/* Configuration.java
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

public final class Configuration {

	private static final Configuration instance = new Configuration();

	// contains all defined symbols
	private Map<String, SymbolAtom> symbolMapping;
	private Map<String, CharFont> fontMapping;

	static {
		DefaultTeXFontParser parser = new DefaultTeXFontParser();
		// fonts + font descriptions
		TeXFont.fontInfo = parser.parseFontDescriptions(TeXFont.fontInfo);
	}

	private Configuration() {
		symbolMapping = new HashMap<>();
		fontMapping = new HashMap<>();
		addSymbols();
	}

	public static Configuration get() {
		return instance;
	}

	public Map<String, SymbolAtom> getSymbolAtoms() {
		return symbolMapping;
	}

	public Map<String, CharFont> getFontMapping() {
		return fontMapping;
	}

	public FontInfo getFontInfo(Font_ID i) {
		return TeXFont.fontInfo.get(i);
	}

	private void add(String symbol, int type, int ch, Font_ID font) {
		CharFont cf = new CharFont((char) ch, font);
		SymbolAtom sa = new SymbolAtom(cf, type, cf.c);
		fontMapping.put(symbol, cf);
		symbolMapping.put(symbol, sa);
	}

	private void add(String symbol, int type, int ch, Font_ID font,
			char unicode) {
		CharFont cf = new CharFont((char) ch, font);
		SymbolAtom sa = new SymbolAtom(cf, type, unicode);
		fontMapping.put(symbol, cf);
		symbolMapping.put(symbol, sa);
		CharMapping.getDefault().put(unicode,
				new CharMapping.SymbolMapping(unicode, sa));
	}

	private void add(String symbol, int type, int ch, Font_ID font,
			char unicode, String text) {
		CharFont cf = new CharFont((char) ch, font);
		SymbolAtom sa = new SymbolAtom(cf, type, unicode);
		fontMapping.put(symbol, cf);
		symbolMapping.put(symbol, sa);
		CharMapping.getDefault().put(unicode,
				new CharMapping.SymbolMapping(unicode, sa, text));
	}

	private void addSymbols() {
		add("#", TeXConstants.TYPE_ORDINARY, 35, Font_ID.jlm_cmr10, '#');
		add("@", TeXConstants.TYPE_ORDINARY, 64, Font_ID.jlm_cmr10, '@');
		add("faculty", TeXConstants.TYPE_ORDINARY, 33, Font_ID.jlm_cmr10, '!');
		add("textapos", TeXConstants.TYPE_ORDINARY, 39, Font_ID.jlm_cmr10,
				'\'');
		add("lbrack", TeXConstants.TYPE_OPENING, 40, Font_ID.jlm_cmr10, '(');
		add("rbrack", TeXConstants.TYPE_CLOSING, 41, Font_ID.jlm_cmr10, ')');
		add("ast", TeXConstants.TYPE_BINARY_OPERATOR, 164, Font_ID.jlm_cmsy10,
				'*');
		add("plus", TeXConstants.TYPE_BINARY_OPERATOR, 43, Font_ID.jlm_cmr10,
				'+');
		add("comma", TeXConstants.TYPE_PUNCTUATION, 59,
				Font_ID.jlm_cmmi10_unchanged, ',');
		add("minus", TeXConstants.TYPE_BINARY_OPERATOR, 161, Font_ID.jlm_cmsy10,
				'-', "textminus");
		add("slash", TeXConstants.TYPE_ORDINARY, 61,
				Font_ID.jlm_cmmi10_unchanged, '/', "textfractionsolidus");
		add("colon", TeXConstants.TYPE_RELATION, 58, Font_ID.jlm_cmr10, ':');
		add("semicolon", TeXConstants.TYPE_PUNCTUATION, 59, Font_ID.jlm_cmr10,
				';');
		add("lt", TeXConstants.TYPE_RELATION, 60, Font_ID.jlm_cmmi10_unchanged,
				'<');
		add("equals", TeXConstants.TYPE_RELATION, 61, Font_ID.jlm_cmr10, '=');
		add("gt", TeXConstants.TYPE_RELATION, 62, Font_ID.jlm_cmmi10_unchanged,
				'>');
		add("question", TeXConstants.TYPE_ORDINARY, 63, Font_ID.jlm_cmr10, '?');
		add("lsqbrack", TeXConstants.TYPE_OPENING, 91, Font_ID.jlm_cmr10, '[');
		add("rsqbrack", TeXConstants.TYPE_CLOSING, 93, Font_ID.jlm_cmr10, ']');
		add("jlatexmathlapos", TeXConstants.TYPE_ORDINARY, 96,
				Font_ID.jlm_cmr10, '`');
		add("lbrace", TeXConstants.TYPE_OPENING, 102, Font_ID.jlm_cmsy10, '{');
		add("vert", TeXConstants.TYPE_ORDINARY, 106, Font_ID.jlm_cmsy10, '|');
		add("rbrace", TeXConstants.TYPE_CLOSING, 103, Font_ID.jlm_cmsy10, '}');
		add("mathsterling", TeXConstants.TYPE_ORDINARY, 36,
				Font_ID.jlm_cmti10_unchanged, '\u00A3');
		add("yen", TeXConstants.TYPE_ORDINARY, 85, Font_ID.jlm_msam10,
				'\u00A5');
		add("S", TeXConstants.TYPE_ORDINARY, 120, Font_ID.jlm_cmsy10, '\u00A7');
		add("guillemotleft", TeXConstants.TYPE_PUNCTUATION, 33,
				Font_ID.jlm_jlmi10, '\u00AB');
		add("lnot", TeXConstants.TYPE_ORDINARY, 58, Font_ID.jlm_cmsy10,
				'\u00AC');
		add("textregistered", TeXConstants.TYPE_ORDINARY, 114,
				Font_ID.jlm_msam10, '\u00AE');
		add("pm", TeXConstants.TYPE_BINARY_OPERATOR, 167, Font_ID.jlm_cmsy10,
				'\u00B1');
		add("textmu", TeXConstants.TYPE_ORDINARY, 109, Font_ID.jlm_special,
				'\u00B5');
		add("P", TeXConstants.TYPE_ORDINARY, 123, Font_ID.jlm_cmsy10, '\u00B6');
		add("cdot", TeXConstants.TYPE_BINARY_OPERATOR, 162, Font_ID.jlm_cmsy10,
				'\u00B7');
		add("guillemotright", TeXConstants.TYPE_PUNCTUATION, 36,
				Font_ID.jlm_jlmi10, '\u00BB');
		add("questiondown", TeXConstants.TYPE_ORDINARY, 62, Font_ID.jlm_cmr10,
				'\u00BF');
		add("AE", TeXConstants.TYPE_ORDINARY, 192, Font_ID.jlm_cmti10,
				'\u00C6');
		add("times", TeXConstants.TYPE_BINARY_OPERATOR, 163, Font_ID.jlm_cmsy10,
				'\u00D7');
		add("O", TeXConstants.TYPE_ORDINARY, 194, Font_ID.jlm_cmti10, '\u00D8');
		add("ss", TeXConstants.TYPE_ORDINARY, 188, Font_ID.jlm_cmti10,
				'\u00DF');
		add("ae", TeXConstants.TYPE_ORDINARY, 189, Font_ID.jlm_cmti10,
				'\u00E6');
		add("eth", TeXConstants.TYPE_ORDINARY, 103, Font_ID.jlm_msbm10,
				'\u00F0');
		add("div", TeXConstants.TYPE_BINARY_OPERATOR, 165, Font_ID.jlm_cmsy10,
				'\u00F7');
		add("o", TeXConstants.TYPE_ORDINARY, 191, Font_ID.jlm_cmti10, '\u00F8');
		add("dotlessi", TeXConstants.TYPE_ORDINARY, 305, Font_ID.jlm_wnr10,
				'\u0131');
		add("OE", TeXConstants.TYPE_ORDINARY, 193, Font_ID.jlm_cmti10,
				'\u0152');
		add("oe", TeXConstants.TYPE_ORDINARY, 190, Font_ID.jlm_cmti10,
				'\u0153');
		add("\u0374", TeXConstants.TYPE_ACCENT, 884, Font_ID.jlm_fcmrpg,
				'\u0374');
		add("\u0375", TeXConstants.TYPE_ACCENT, 885, Font_ID.jlm_fcmrpg,
				'\u0375');
		add("\u037A", TeXConstants.TYPE_ACCENT, 890, Font_ID.jlm_fcmrpg,
				'\u037A');
		add("\u0384", TeXConstants.TYPE_ACCENT, 900, Font_ID.jlm_fcmrpg,
				'\u0384');
		add("\u0385", TeXConstants.TYPE_ACCENT, 901, Font_ID.jlm_fcmrpg,
				'\u0385');
		add("\u0387", TeXConstants.TYPE_ACCENT, 903, Font_ID.jlm_fcmrpg,
				'\u0387');
		add("\u0390", TeXConstants.TYPE_ORDINARY, 912, Font_ID.jlm_fcmrpg,
				'\u0390');
		add("\u03AA", TeXConstants.TYPE_ORDINARY, 938, Font_ID.jlm_fcmrpg,
				'\u03AA');
		add("\u03AB", TeXConstants.TYPE_ORDINARY, 939, Font_ID.jlm_fcmrpg,
				'\u03AB');
		add("\u03AC", TeXConstants.TYPE_ORDINARY, 940, Font_ID.jlm_fcmrpg,
				'\u03AC');
		add("\u03AD", TeXConstants.TYPE_ORDINARY, 941, Font_ID.jlm_fcmrpg,
				'\u03AD');
		add("\u03AE", TeXConstants.TYPE_ORDINARY, 942, Font_ID.jlm_fcmrpg,
				'\u03AE');
		add("\u03AF", TeXConstants.TYPE_ORDINARY, 943, Font_ID.jlm_fcmrpg,
				'\u03AF');
		add("\u03B0", TeXConstants.TYPE_ORDINARY, 944, Font_ID.jlm_fcmrpg,
				'\u03B0');
		add("alpha", TeXConstants.TYPE_ORDINARY, 174,
				Font_ID.jlm_cmmi10_unchanged, '\u03B1', "\u03B1");
		add("beta", TeXConstants.TYPE_ORDINARY, 175,
				Font_ID.jlm_cmmi10_unchanged, '\u03B2', "\u03B2");
		add("gamma", TeXConstants.TYPE_ORDINARY, 176,
				Font_ID.jlm_cmmi10_unchanged, '\u03B3', "\u03B3");
		add("delta", TeXConstants.TYPE_ORDINARY, 177,
				Font_ID.jlm_cmmi10_unchanged, '\u03B4', "\u03B4");
		add("varepsilon", TeXConstants.TYPE_ORDINARY, 34,
				Font_ID.jlm_cmmi10_unchanged, '\u03B5', "\u03B5");
		add("zeta", TeXConstants.TYPE_ORDINARY, 179,
				Font_ID.jlm_cmmi10_unchanged, '\u03B6', "\u03B6");
		add("eta", TeXConstants.TYPE_ORDINARY, 180,
				Font_ID.jlm_cmmi10_unchanged, '\u03B7', "\u03B7");
		add("theta", TeXConstants.TYPE_ORDINARY, 181,
				Font_ID.jlm_cmmi10_unchanged, '\u03B8', "\u03B8");
		add("iota", TeXConstants.TYPE_ORDINARY, 182,
				Font_ID.jlm_cmmi10_unchanged, '\u03B9', "\u03B9");
		add("kappa", TeXConstants.TYPE_ORDINARY, 183,
				Font_ID.jlm_cmmi10_unchanged, '\u03BA', "\u03BA");
		add("lambda", TeXConstants.TYPE_ORDINARY, 184,
				Font_ID.jlm_cmmi10_unchanged, '\u03BB', "\u03BB");
		add("mu", TeXConstants.TYPE_ORDINARY, 185, Font_ID.jlm_cmmi10_unchanged,
				'\u03BC', "\u03BC");
		add("nu", TeXConstants.TYPE_ORDINARY, 186, Font_ID.jlm_cmmi10_unchanged,
				'\u03BD', "\u03BD");
		add("xi", TeXConstants.TYPE_ORDINARY, 187, Font_ID.jlm_cmmi10_unchanged,
				'\u03BE', "\u03BE");
		add("omicron", TeXConstants.TYPE_ORDINARY, 111,
				Font_ID.jlm_cmmi10_unchanged, '\u03BF', "\u03BF");
		add("pi", TeXConstants.TYPE_ORDINARY, 188, Font_ID.jlm_cmmi10_unchanged,
				'\u03C0', "\u03C0");
		add("rho", TeXConstants.TYPE_ORDINARY, 189,
				Font_ID.jlm_cmmi10_unchanged, '\u03C1', "\u03C1");
		add("varsigma", TeXConstants.TYPE_ORDINARY, 38,
				Font_ID.jlm_cmmi10_unchanged, '\u03C2', "\u03C2");
		add("sigma", TeXConstants.TYPE_ORDINARY, 190,
				Font_ID.jlm_cmmi10_unchanged, '\u03C3', "\u03C3");
		add("tau", TeXConstants.TYPE_ORDINARY, 191,
				Font_ID.jlm_cmmi10_unchanged, '\u03C4', "\u03C4");
		add("upsilon", TeXConstants.TYPE_ORDINARY, 192,
				Font_ID.jlm_cmmi10_unchanged, '\u03C5', "\u03C5");
		add("varphi", TeXConstants.TYPE_ORDINARY, 39,
				Font_ID.jlm_cmmi10_unchanged, '\u03C6', "\u03C6");
		add("chi", TeXConstants.TYPE_ORDINARY, 194,
				Font_ID.jlm_cmmi10_unchanged, '\u03C7', "\u03C7");
		add("psi", TeXConstants.TYPE_ORDINARY, 195,
				Font_ID.jlm_cmmi10_unchanged, '\u03C8', "\u03C8");
		add("omega", TeXConstants.TYPE_ORDINARY, 33,
				Font_ID.jlm_cmmi10_unchanged, '\u03C9', "\u03C9");
		add("\u03CA", TeXConstants.TYPE_ORDINARY, 970, Font_ID.jlm_fcmrpg,
				'\u03CA');
		add("\u03CB", TeXConstants.TYPE_ORDINARY, 971, Font_ID.jlm_fcmrpg,
				'\u03CB');
		add("\u03CC", TeXConstants.TYPE_ORDINARY, 972, Font_ID.jlm_fcmrpg,
				'\u03CC');
		add("\u03CD", TeXConstants.TYPE_ORDINARY, 973, Font_ID.jlm_fcmrpg,
				'\u03CD');
		add("\u03CE", TeXConstants.TYPE_ORDINARY, 974, Font_ID.jlm_fcmrpg,
				'\u03CE');
		add("vartheta", TeXConstants.TYPE_ORDINARY, 35,
				Font_ID.jlm_cmmi10_unchanged, '\u03D1', "\u03D1");
		add("phi", TeXConstants.TYPE_ORDINARY, 193,
				Font_ID.jlm_cmmi10_unchanged, '\u03D5');
		add("varpi", TeXConstants.TYPE_ORDINARY, 36,
				Font_ID.jlm_cmmi10_unchanged, '\u03D6');
		add("\u03D8", TeXConstants.TYPE_ORDINARY, 984, Font_ID.jlm_fcmrpg,
				'\u03D8');
		add("\u03D9", TeXConstants.TYPE_ORDINARY, 985, Font_ID.jlm_fcmrpg,
				'\u03D9');
		add("\u03DA", TeXConstants.TYPE_ORDINARY, 986, Font_ID.jlm_fcmrpg,
				'\u03DA');
		add("\u03DB", TeXConstants.TYPE_ORDINARY, 987, Font_ID.jlm_fcmrpg,
				'\u03DB');
		add("\u03DC", TeXConstants.TYPE_ORDINARY, 988, Font_ID.jlm_fcmrpg,
				'\u03DC');
		add("\u03DD", TeXConstants.TYPE_ORDINARY, 989, Font_ID.jlm_fcmrpg,
				'\u03DD');
		add("\u03DF", TeXConstants.TYPE_ORDINARY, 991, Font_ID.jlm_fcmrpg,
				'\u03DF');
		add("\u03E0", TeXConstants.TYPE_ORDINARY, 992, Font_ID.jlm_fcmrpg,
				'\u03E0');
		add("\u03E1", TeXConstants.TYPE_ORDINARY, 993, Font_ID.jlm_fcmrpg,
				'\u03E1');
		add("varkappa", TeXConstants.TYPE_ORDINARY, 123, Font_ID.jlm_msbm10,
				'\u03F0');
		add("varrho", TeXConstants.TYPE_ORDINARY, 37,
				Font_ID.jlm_cmmi10_unchanged, '\u03F1');
		add("epsilon", TeXConstants.TYPE_ORDINARY, 178,
				Font_ID.jlm_cmmi10_unchanged, '\u03F5');
		add("backepsilon", TeXConstants.TYPE_RELATION, 196, Font_ID.jlm_msbm10,
				'\u03F6');
		add("CYRYO", TeXConstants.TYPE_ORDINARY, 1025, Font_ID.jlm_wnr10,
				'\u0401');
		add("CYRDJE", TeXConstants.TYPE_ORDINARY, 1026, Font_ID.jlm_wnr10,
				'\u0402');
		add("CYRIE", TeXConstants.TYPE_ORDINARY, 1028, Font_ID.jlm_wnr10,
				'\u0404');
		add("CYRDZE", TeXConstants.TYPE_ORDINARY, 1029, Font_ID.jlm_wnr10,
				'\u0405');
		add("CYRII", TeXConstants.TYPE_ORDINARY, 1030, Font_ID.jlm_wnr10,
				'\u0406');
		add("CYRJE", TeXConstants.TYPE_ORDINARY, 1032, Font_ID.jlm_wnr10,
				'\u0408');
		add("CYRLJE", TeXConstants.TYPE_ORDINARY, 1033, Font_ID.jlm_wnr10,
				'\u0409');
		add("CYRNJE", TeXConstants.TYPE_ORDINARY, 1034, Font_ID.jlm_wnr10,
				'\u040A');
		add("CYRTSHE", TeXConstants.TYPE_ORDINARY, 1035, Font_ID.jlm_wnr10,
				'\u040B');
		add("CYRDZHE", TeXConstants.TYPE_ORDINARY, 1039, Font_ID.jlm_wnr10,
				'\u040F');
		add("CYRA", TeXConstants.TYPE_ORDINARY, 1040, Font_ID.jlm_wnr10,
				'\u0410');
		add("CYRB", TeXConstants.TYPE_ORDINARY, 1041, Font_ID.jlm_wnr10,
				'\u0411');
		add("CYRV", TeXConstants.TYPE_ORDINARY, 1042, Font_ID.jlm_wnr10,
				'\u0412');
		add("CYRG", TeXConstants.TYPE_ORDINARY, 1043, Font_ID.jlm_wnr10,
				'\u0413');
		add("CYRD", TeXConstants.TYPE_ORDINARY, 1044, Font_ID.jlm_wnr10,
				'\u0414');
		add("CYRE", TeXConstants.TYPE_ORDINARY, 1045, Font_ID.jlm_wnr10,
				'\u0415');
		add("CYRZH", TeXConstants.TYPE_ORDINARY, 1046, Font_ID.jlm_wnr10,
				'\u0416');
		add("CYRZ", TeXConstants.TYPE_ORDINARY, 1047, Font_ID.jlm_wnr10,
				'\u0417');
		add("CYRI", TeXConstants.TYPE_ORDINARY, 1048, Font_ID.jlm_wnr10,
				'\u0418');
		add("CYRIO", TeXConstants.TYPE_ORDINARY, 1049, Font_ID.jlm_wnr10,
				'\u0419');
		add("CYRK", TeXConstants.TYPE_ORDINARY, 1050, Font_ID.jlm_wnr10,
				'\u041A');
		add("CYRL", TeXConstants.TYPE_ORDINARY, 1051, Font_ID.jlm_wnr10,
				'\u041B');
		add("CYRM", TeXConstants.TYPE_ORDINARY, 1052, Font_ID.jlm_wnr10,
				'\u041C');
		add("CYRN", TeXConstants.TYPE_ORDINARY, 1053, Font_ID.jlm_wnr10,
				'\u041D');
		add("CYRO", TeXConstants.TYPE_ORDINARY, 1054, Font_ID.jlm_wnr10,
				'\u041E');
		add("CYRP", TeXConstants.TYPE_ORDINARY, 1055, Font_ID.jlm_wnr10,
				'\u041F');
		add("CYRR", TeXConstants.TYPE_ORDINARY, 1056, Font_ID.jlm_wnr10,
				'\u0420');
		add("CYRS", TeXConstants.TYPE_ORDINARY, 1057, Font_ID.jlm_wnr10,
				'\u0421');
		add("CYRT", TeXConstants.TYPE_ORDINARY, 1058, Font_ID.jlm_wnr10,
				'\u0422');
		add("CYRU", TeXConstants.TYPE_ORDINARY, 1059, Font_ID.jlm_wnr10,
				'\u0423');
		add("CYRF", TeXConstants.TYPE_ORDINARY, 1060, Font_ID.jlm_wnr10,
				'\u0424');
		add("CYRH", TeXConstants.TYPE_ORDINARY, 1061, Font_ID.jlm_wnr10,
				'\u0425');
		add("CYRC", TeXConstants.TYPE_ORDINARY, 1062, Font_ID.jlm_wnr10,
				'\u0426');
		add("CYRCH", TeXConstants.TYPE_ORDINARY, 1063, Font_ID.jlm_wnr10,
				'\u0427');
		add("CYRSH", TeXConstants.TYPE_ORDINARY, 1064, Font_ID.jlm_wnr10,
				'\u0428');
		add("CYRSHCH", TeXConstants.TYPE_ORDINARY, 1065, Font_ID.jlm_wnr10,
				'\u0429');
		add("CYRHRDSN", TeXConstants.TYPE_ORDINARY, 1066, Font_ID.jlm_wnr10,
				'\u042A');
		add("CYRY", TeXConstants.TYPE_ORDINARY, 1067, Font_ID.jlm_wnr10,
				'\u042B');
		add("CYRSFTSN", TeXConstants.TYPE_ORDINARY, 1068, Font_ID.jlm_wnr10,
				'\u042C');
		add("CYREREV", TeXConstants.TYPE_ORDINARY, 1069, Font_ID.jlm_wnr10,
				'\u042D');
		add("CYRYU", TeXConstants.TYPE_ORDINARY, 1070, Font_ID.jlm_wnr10,
				'\u042E');
		add("CYRYA", TeXConstants.TYPE_ORDINARY, 1071, Font_ID.jlm_wnr10,
				'\u042F');
		add("cyra", TeXConstants.TYPE_ORDINARY, 1072, Font_ID.jlm_wnr10,
				'\u0430');
		add("cyrb", TeXConstants.TYPE_ORDINARY, 1073, Font_ID.jlm_wnr10,
				'\u0431');
		add("cyrv", TeXConstants.TYPE_ORDINARY, 1074, Font_ID.jlm_wnr10,
				'\u0432');
		add("cyrg", TeXConstants.TYPE_ORDINARY, 1075, Font_ID.jlm_wnr10,
				'\u0433');
		add("cyrd", TeXConstants.TYPE_ORDINARY, 1076, Font_ID.jlm_wnr10,
				'\u0434');
		add("cyre", TeXConstants.TYPE_ORDINARY, 1077, Font_ID.jlm_wnr10,
				'\u0435');
		add("cyrzh", TeXConstants.TYPE_ORDINARY, 1078, Font_ID.jlm_wnr10,
				'\u0436');
		add("cyrz", TeXConstants.TYPE_ORDINARY, 1079, Font_ID.jlm_wnr10,
				'\u0437');
		add("cyri", TeXConstants.TYPE_ORDINARY, 1080, Font_ID.jlm_wnr10,
				'\u0438');
		add("cyrio", TeXConstants.TYPE_ORDINARY, 1081, Font_ID.jlm_wnr10,
				'\u0439');
		add("cyrk", TeXConstants.TYPE_ORDINARY, 1082, Font_ID.jlm_wnr10,
				'\u043A');
		add("cyrl", TeXConstants.TYPE_ORDINARY, 1083, Font_ID.jlm_wnr10,
				'\u043B');
		add("cyrm", TeXConstants.TYPE_ORDINARY, 1084, Font_ID.jlm_wnr10,
				'\u043C');
		add("cyrn", TeXConstants.TYPE_ORDINARY, 1085, Font_ID.jlm_wnr10,
				'\u043D');
		add("cyro", TeXConstants.TYPE_ORDINARY, 1086, Font_ID.jlm_wnr10,
				'\u043E');
		add("cyrp", TeXConstants.TYPE_ORDINARY, 1087, Font_ID.jlm_wnr10,
				'\u043F');
		add("cyrr", TeXConstants.TYPE_ORDINARY, 1088, Font_ID.jlm_wnr10,
				'\u0440');
		add("cyrs", TeXConstants.TYPE_ORDINARY, 1089, Font_ID.jlm_wnr10,
				'\u0441');
		add("cyrt", TeXConstants.TYPE_ORDINARY, 1090, Font_ID.jlm_wnr10,
				'\u0442');
		add("cyru", TeXConstants.TYPE_ORDINARY, 1091, Font_ID.jlm_wnr10,
				'\u0443');
		add("cyrf", TeXConstants.TYPE_ORDINARY, 1092, Font_ID.jlm_wnr10,
				'\u0444');
		add("cyrh", TeXConstants.TYPE_ORDINARY, 1093, Font_ID.jlm_wnr10,
				'\u0445');
		add("cyrc", TeXConstants.TYPE_ORDINARY, 1094, Font_ID.jlm_wnr10,
				'\u0446');
		add("cyrch", TeXConstants.TYPE_ORDINARY, 1095, Font_ID.jlm_wnr10,
				'\u0447');
		add("cyrsh", TeXConstants.TYPE_ORDINARY, 1096, Font_ID.jlm_wnr10,
				'\u0448');
		add("cyrshch", TeXConstants.TYPE_ORDINARY, 1097, Font_ID.jlm_wnr10,
				'\u0449');
		add("cyrhrdsn", TeXConstants.TYPE_ORDINARY, 1098, Font_ID.jlm_wnr10,
				'\u044A');
		add("cyry", TeXConstants.TYPE_ORDINARY, 1099, Font_ID.jlm_wnr10,
				'\u044B');
		add("cyrsftsn", TeXConstants.TYPE_ORDINARY, 1100, Font_ID.jlm_wnr10,
				'\u044C');
		add("cyrerev", TeXConstants.TYPE_ORDINARY, 1101, Font_ID.jlm_wnr10,
				'\u044D');
		add("cyryu", TeXConstants.TYPE_ORDINARY, 1102, Font_ID.jlm_wnr10,
				'\u044E');
		add("cyrya", TeXConstants.TYPE_ORDINARY, 1103, Font_ID.jlm_wnr10,
				'\u044F');
		add("cyryo", TeXConstants.TYPE_ORDINARY, 1105, Font_ID.jlm_wnr10,
				'\u0451');
		add("cyrdje", TeXConstants.TYPE_ORDINARY, 1106, Font_ID.jlm_wnr10,
				'\u0452');
		add("cyrie", TeXConstants.TYPE_ORDINARY, 1108, Font_ID.jlm_wnr10,
				'\u0454');
		add("cyrdze", TeXConstants.TYPE_ORDINARY, 1109, Font_ID.jlm_wnr10,
				'\u0455');
		add("cyrii", TeXConstants.TYPE_ORDINARY, 1110, Font_ID.jlm_wnr10,
				'\u0456');
		add("cyrje", TeXConstants.TYPE_ORDINARY, 1112, Font_ID.jlm_wnr10,
				'\u0458');
		add("cyrlje", TeXConstants.TYPE_ORDINARY, 1113, Font_ID.jlm_wnr10,
				'\u0459');
		add("cyrnje", TeXConstants.TYPE_ORDINARY, 1114, Font_ID.jlm_wnr10,
				'\u045A');
		add("cyrtshe", TeXConstants.TYPE_ORDINARY, 1115, Font_ID.jlm_wnr10,
				'\u045B');
		add("cyrdzhe", TeXConstants.TYPE_ORDINARY, 1119, Font_ID.jlm_wnr10,
				'\u045F');
		add("CYRYAT", TeXConstants.TYPE_ORDINARY, 1122, Font_ID.jlm_wnr10,
				'\u0462');
		add("cyryat", TeXConstants.TYPE_ORDINARY, 1123, Font_ID.jlm_wnr10,
				'\u0463');
		add("CYRFITA", TeXConstants.TYPE_ORDINARY, 1138, Font_ID.jlm_wnr10,
				'\u0472');
		add("cyrfita", TeXConstants.TYPE_ORDINARY, 1139, Font_ID.jlm_wnr10,
				'\u0473');
		add("CYRIZH", TeXConstants.TYPE_ORDINARY, 1140, Font_ID.jlm_wnr10,
				'\u0474');
		add("cyrizh", TeXConstants.TYPE_ORDINARY, 1141, Font_ID.jlm_wnr10,
				'\u0475');
		add("\u1F00", TeXConstants.TYPE_ORDINARY, 7936, Font_ID.jlm_fcmrpg,
				'\u1F00');
		add("\u1F01", TeXConstants.TYPE_ORDINARY, 7937, Font_ID.jlm_fcmrpg,
				'\u1F01');
		add("\u1F02", TeXConstants.TYPE_ORDINARY, 7938, Font_ID.jlm_fcmrpg,
				'\u1F02');
		add("\u1F03", TeXConstants.TYPE_ORDINARY, 7939, Font_ID.jlm_fcmrpg,
				'\u1F03');
		add("\u1F04", TeXConstants.TYPE_ORDINARY, 7940, Font_ID.jlm_fcmrpg,
				'\u1F04');
		add("\u1F05", TeXConstants.TYPE_ORDINARY, 7941, Font_ID.jlm_fcmrpg,
				'\u1F05');
		add("\u1F06", TeXConstants.TYPE_ORDINARY, 7942, Font_ID.jlm_fcmrpg,
				'\u1F06');
		add("\u1F07", TeXConstants.TYPE_ORDINARY, 7943, Font_ID.jlm_fcmrpg,
				'\u1F07');
		add("\u1F10", TeXConstants.TYPE_ORDINARY, 7952, Font_ID.jlm_fcmrpg,
				'\u1F10');
		add("\u1F11", TeXConstants.TYPE_ORDINARY, 7953, Font_ID.jlm_fcmrpg,
				'\u1F11');
		add("\u1F12", TeXConstants.TYPE_ORDINARY, 7954, Font_ID.jlm_fcmrpg,
				'\u1F12');
		add("\u1F13", TeXConstants.TYPE_ORDINARY, 7955, Font_ID.jlm_fcmrpg,
				'\u1F13');
		add("\u1F14", TeXConstants.TYPE_ORDINARY, 7956, Font_ID.jlm_fcmrpg,
				'\u1F14');
		add("\u1F15", TeXConstants.TYPE_ORDINARY, 7957, Font_ID.jlm_fcmrpg,
				'\u1F15');
		add("\u1F20", TeXConstants.TYPE_ORDINARY, 7968, Font_ID.jlm_fcmrpg,
				'\u1F20');
		add("\u1F21", TeXConstants.TYPE_ORDINARY, 7969, Font_ID.jlm_fcmrpg,
				'\u1F21');
		add("\u1F22", TeXConstants.TYPE_ORDINARY, 7970, Font_ID.jlm_fcmrpg,
				'\u1F22');
		add("\u1F23", TeXConstants.TYPE_ORDINARY, 7971, Font_ID.jlm_fcmrpg,
				'\u1F23');
		add("\u1F24", TeXConstants.TYPE_ORDINARY, 7972, Font_ID.jlm_fcmrpg,
				'\u1F24');
		add("\u1F25", TeXConstants.TYPE_ORDINARY, 7973, Font_ID.jlm_fcmrpg,
				'\u1F25');
		add("\u1F26", TeXConstants.TYPE_ORDINARY, 7974, Font_ID.jlm_fcmrpg,
				'\u1F26');
		add("\u1F27", TeXConstants.TYPE_ORDINARY, 7975, Font_ID.jlm_fcmrpg,
				'\u1F27');
		add("\u1F30", TeXConstants.TYPE_ORDINARY, 7984, Font_ID.jlm_fcmrpg,
				'\u1F30');
		add("\u1F31", TeXConstants.TYPE_ORDINARY, 7985, Font_ID.jlm_fcmrpg,
				'\u1F31');
		add("\u1F32", TeXConstants.TYPE_ORDINARY, 7986, Font_ID.jlm_fcmrpg,
				'\u1F32');
		add("\u1F33", TeXConstants.TYPE_ORDINARY, 7987, Font_ID.jlm_fcmrpg,
				'\u1F33');
		add("\u1F34", TeXConstants.TYPE_ORDINARY, 7988, Font_ID.jlm_fcmrpg,
				'\u1F34');
		add("\u1F35", TeXConstants.TYPE_ORDINARY, 7989, Font_ID.jlm_fcmrpg,
				'\u1F35');
		add("\u1F36", TeXConstants.TYPE_ORDINARY, 7990, Font_ID.jlm_fcmrpg,
				'\u1F36');
		add("\u1F37", TeXConstants.TYPE_ORDINARY, 7991, Font_ID.jlm_fcmrpg,
				'\u1F37');
		add("\u1F40", TeXConstants.TYPE_ORDINARY, 8000, Font_ID.jlm_fcmrpg,
				'\u1F40');
		add("\u1F41", TeXConstants.TYPE_ORDINARY, 8001, Font_ID.jlm_fcmrpg,
				'\u1F41');
		add("\u1F42", TeXConstants.TYPE_ORDINARY, 8002, Font_ID.jlm_fcmrpg,
				'\u1F42');
		add("\u1F43", TeXConstants.TYPE_ORDINARY, 8003, Font_ID.jlm_fcmrpg,
				'\u1F43');
		add("\u1F44", TeXConstants.TYPE_ORDINARY, 8004, Font_ID.jlm_fcmrpg,
				'\u1F44');
		add("\u1F45", TeXConstants.TYPE_ORDINARY, 8005, Font_ID.jlm_fcmrpg,
				'\u1F45');
		add("\u1F50", TeXConstants.TYPE_ORDINARY, 8016, Font_ID.jlm_fcmrpg,
				'\u1F50');
		add("\u1F51", TeXConstants.TYPE_ORDINARY, 8017, Font_ID.jlm_fcmrpg,
				'\u1F51');
		add("\u1F52", TeXConstants.TYPE_ORDINARY, 8018, Font_ID.jlm_fcmrpg,
				'\u1F52');
		add("\u1F53", TeXConstants.TYPE_ORDINARY, 8019, Font_ID.jlm_fcmrpg,
				'\u1F53');
		add("\u1F54", TeXConstants.TYPE_ORDINARY, 8020, Font_ID.jlm_fcmrpg,
				'\u1F54');
		add("\u1F55", TeXConstants.TYPE_ORDINARY, 8021, Font_ID.jlm_fcmrpg,
				'\u1F55');
		add("\u1F56", TeXConstants.TYPE_ORDINARY, 8022, Font_ID.jlm_fcmrpg,
				'\u1F56');
		add("\u1F57", TeXConstants.TYPE_ORDINARY, 8023, Font_ID.jlm_fcmrpg,
				'\u1F57');
		add("\u1F60", TeXConstants.TYPE_ORDINARY, 8032, Font_ID.jlm_fcmrpg,
				'\u1F60');
		add("\u1F61", TeXConstants.TYPE_ORDINARY, 8033, Font_ID.jlm_fcmrpg,
				'\u1F61');
		add("\u1F62", TeXConstants.TYPE_ORDINARY, 8034, Font_ID.jlm_fcmrpg,
				'\u1F62');
		add("\u1F63", TeXConstants.TYPE_ORDINARY, 8035, Font_ID.jlm_fcmrpg,
				'\u1F63');
		add("\u1F64", TeXConstants.TYPE_ORDINARY, 8036, Font_ID.jlm_fcmrpg,
				'\u1F64');
		add("\u1F65", TeXConstants.TYPE_ORDINARY, 8037, Font_ID.jlm_fcmrpg,
				'\u1F65');
		add("\u1F66", TeXConstants.TYPE_ORDINARY, 8038, Font_ID.jlm_fcmrpg,
				'\u1F66');
		add("\u1F67", TeXConstants.TYPE_ORDINARY, 8039, Font_ID.jlm_fcmrpg,
				'\u1F67');
		add("\u1F70", TeXConstants.TYPE_ORDINARY, 8048, Font_ID.jlm_fcmrpg,
				'\u1F70');
		add("\u1F72", TeXConstants.TYPE_ORDINARY, 8050, Font_ID.jlm_fcmrpg,
				'\u1F72');
		add("\u1F74", TeXConstants.TYPE_ORDINARY, 8052, Font_ID.jlm_fcmrpg,
				'\u1F74');
		add("\u1F76", TeXConstants.TYPE_ORDINARY, 8054, Font_ID.jlm_fcmrpg,
				'\u1F76');
		add("\u1F78", TeXConstants.TYPE_ORDINARY, 8056, Font_ID.jlm_fcmrpg,
				'\u1F78');
		add("\u1F7A", TeXConstants.TYPE_ORDINARY, 8058, Font_ID.jlm_fcmrpg,
				'\u1F7A');
		add("\u1F7C", TeXConstants.TYPE_ORDINARY, 8060, Font_ID.jlm_fcmrpg,
				'\u1F7C');
		add("\u1F80", TeXConstants.TYPE_ORDINARY, 8064, Font_ID.jlm_fcmrpg,
				'\u1F80');
		add("\u1F81", TeXConstants.TYPE_ORDINARY, 8065, Font_ID.jlm_fcmrpg,
				'\u1F81');
		add("\u1F82", TeXConstants.TYPE_ORDINARY, 8066, Font_ID.jlm_fcmrpg,
				'\u1F82');
		add("\u1F83", TeXConstants.TYPE_ORDINARY, 8067, Font_ID.jlm_fcmrpg,
				'\u1F83');
		add("\u1F84", TeXConstants.TYPE_ORDINARY, 8068, Font_ID.jlm_fcmrpg,
				'\u1F84');
		add("\u1F85", TeXConstants.TYPE_ORDINARY, 8069, Font_ID.jlm_fcmrpg,
				'\u1F85');
		add("\u1F86", TeXConstants.TYPE_ORDINARY, 8070, Font_ID.jlm_fcmrpg,
				'\u1F86');
		add("\u1F87", TeXConstants.TYPE_ORDINARY, 8071, Font_ID.jlm_fcmrpg,
				'\u1F87');
		add("\u1F90", TeXConstants.TYPE_ORDINARY, 8080, Font_ID.jlm_fcmrpg,
				'\u1F90');
		add("\u1F91", TeXConstants.TYPE_ORDINARY, 8081, Font_ID.jlm_fcmrpg,
				'\u1F91');
		add("\u1F92", TeXConstants.TYPE_ORDINARY, 8082, Font_ID.jlm_fcmrpg,
				'\u1F92');
		add("\u1F93", TeXConstants.TYPE_ORDINARY, 8083, Font_ID.jlm_fcmrpg,
				'\u1F93');
		add("\u1F94", TeXConstants.TYPE_ORDINARY, 8084, Font_ID.jlm_fcmrpg,
				'\u1F94');
		add("\u1F95", TeXConstants.TYPE_ORDINARY, 8085, Font_ID.jlm_fcmrpg,
				'\u1F95');
		add("\u1F96", TeXConstants.TYPE_ORDINARY, 8086, Font_ID.jlm_fcmrpg,
				'\u1F96');
		add("\u1F97", TeXConstants.TYPE_ORDINARY, 8087, Font_ID.jlm_fcmrpg,
				'\u1F97');
		add("\u1FA0", TeXConstants.TYPE_ORDINARY, 8096, Font_ID.jlm_fcmrpg,
				'\u1FA0');
		add("\u1FA1", TeXConstants.TYPE_ORDINARY, 8097, Font_ID.jlm_fcmrpg,
				'\u1FA1');
		add("\u1FA2", TeXConstants.TYPE_ORDINARY, 8098, Font_ID.jlm_fcmrpg,
				'\u1FA2');
		add("\u1FA3", TeXConstants.TYPE_ORDINARY, 8099, Font_ID.jlm_fcmrpg,
				'\u1FA3');
		add("\u1FA4", TeXConstants.TYPE_ORDINARY, 8100, Font_ID.jlm_fcmrpg,
				'\u1FA4');
		add("\u1FA5", TeXConstants.TYPE_ORDINARY, 8101, Font_ID.jlm_fcmrpg,
				'\u1FA5');
		add("\u1FA6", TeXConstants.TYPE_ORDINARY, 8102, Font_ID.jlm_fcmrpg,
				'\u1FA6');
		add("\u1FA7", TeXConstants.TYPE_ORDINARY, 8103, Font_ID.jlm_fcmrpg,
				'\u1FA7');
		add("\u1FB2", TeXConstants.TYPE_ORDINARY, 8114, Font_ID.jlm_fcmrpg,
				'\u1FB2');
		add("\u1FB3", TeXConstants.TYPE_ORDINARY, 8115, Font_ID.jlm_fcmrpg,
				'\u1FB3');
		add("\u1FB4", TeXConstants.TYPE_ORDINARY, 8116, Font_ID.jlm_fcmrpg,
				'\u1FB4');
		add("\u1FB6", TeXConstants.TYPE_ORDINARY, 8118, Font_ID.jlm_fcmrpg,
				'\u1FB6');
		add("\u1FB7", TeXConstants.TYPE_ORDINARY, 8119, Font_ID.jlm_fcmrpg,
				'\u1FB7');
		add("\u1FBC", TeXConstants.TYPE_ORDINARY, 8124, Font_ID.jlm_fcmrpg,
				'\u1FBC');
		add("\u1FBF", TeXConstants.TYPE_ACCENT, 8127, Font_ID.jlm_fcmrpg,
				'\u1FBD');
		add("\u1FBE", TeXConstants.TYPE_ACCENT, 8126, Font_ID.jlm_fcmrpg,
				'\u1FBE');
		add("\u1FC0", TeXConstants.TYPE_ACCENT, 8128, Font_ID.jlm_fcmrpg,
				'\u1FC0');
		add("\u1FC1", TeXConstants.TYPE_ACCENT, 8129, Font_ID.jlm_fcmrpg,
				'\u1FC1');
		add("\u1FC2", TeXConstants.TYPE_ORDINARY, 8130, Font_ID.jlm_fcmrpg,
				'\u1FC2');
		add("\u1FC3", TeXConstants.TYPE_ORDINARY, 8131, Font_ID.jlm_fcmrpg,
				'\u1FC3');
		add("\u1FC4", TeXConstants.TYPE_ORDINARY, 8132, Font_ID.jlm_fcmrpg,
				'\u1FC4');
		add("\u1FC6", TeXConstants.TYPE_ORDINARY, 8134, Font_ID.jlm_fcmrpg,
				'\u1FC6');
		add("\u1FC7", TeXConstants.TYPE_ORDINARY, 8135, Font_ID.jlm_fcmrpg,
				'\u1FC7');
		add("\u1FCC", TeXConstants.TYPE_ORDINARY, 8140, Font_ID.jlm_fcmrpg,
				'\u1FCC');
		add("\u1FCD", TeXConstants.TYPE_ACCENT, 8141, Font_ID.jlm_fcmrpg,
				'\u1FCD');
		add("\u1FCE", TeXConstants.TYPE_ACCENT, 8142, Font_ID.jlm_fcmrpg,
				'\u1FCE');
		add("\u1FCF", TeXConstants.TYPE_ACCENT, 8143, Font_ID.jlm_fcmrpg,
				'\u1FCF');
		add("\u1FD2", TeXConstants.TYPE_ORDINARY, 8146, Font_ID.jlm_fcmrpg,
				'\u1FD2');
		add("\u1FD6", TeXConstants.TYPE_ORDINARY, 8150, Font_ID.jlm_fcmrpg,
				'\u1FD6');
		add("\u1FD7", TeXConstants.TYPE_ORDINARY, 8151, Font_ID.jlm_fcmrpg,
				'\u1FD7');
		add("\u1FDD", TeXConstants.TYPE_ACCENT, 8157, Font_ID.jlm_fcmrpg,
				'\u1FDD');
		add("\u1FDE", TeXConstants.TYPE_ACCENT, 8158, Font_ID.jlm_fcmrpg,
				'\u1FDE');
		add("\u1FDF", TeXConstants.TYPE_ACCENT, 8159, Font_ID.jlm_fcmrpg,
				'\u1FDF');
		add("\u1FE2", TeXConstants.TYPE_ORDINARY, 8162, Font_ID.jlm_fcmrpg,
				'\u1FE2');
		add("\u1FE4", TeXConstants.TYPE_ORDINARY, 8164, Font_ID.jlm_fcmrpg,
				'\u1FE4');
		add("\u1FE5", TeXConstants.TYPE_ORDINARY, 8165, Font_ID.jlm_fcmrpg,
				'\u1FE5');
		add("\u1FE6", TeXConstants.TYPE_ORDINARY, 8166, Font_ID.jlm_fcmrpg,
				'\u1FE6');
		add("\u1FE7", TeXConstants.TYPE_ORDINARY, 8167, Font_ID.jlm_fcmrpg,
				'\u1FE7');
		add("\u1FED", TeXConstants.TYPE_ACCENT, 8173, Font_ID.jlm_fcmrpg,
				'\u1FED');
		add("\u1FEF", TeXConstants.TYPE_ACCENT, 8175, Font_ID.jlm_fcmrpg,
				'\u1FEF');
		add("\u1FF2", TeXConstants.TYPE_ORDINARY, 8178, Font_ID.jlm_fcmrpg,
				'\u1FF2');
		add("\u1FF3", TeXConstants.TYPE_ORDINARY, 8179, Font_ID.jlm_fcmrpg,
				'\u1FF3');
		add("\u1FF4", TeXConstants.TYPE_ORDINARY, 8180, Font_ID.jlm_fcmrpg,
				'\u1FF4');
		add("\u1FF6", TeXConstants.TYPE_ORDINARY, 8182, Font_ID.jlm_fcmrpg,
				'\u1FF6');
		add("\u1FF7", TeXConstants.TYPE_ORDINARY, 8183, Font_ID.jlm_fcmrpg,
				'\u1FF7');
		add("\u1FFC", TeXConstants.TYPE_ORDINARY, 8188, Font_ID.jlm_fcmrpg,
				'\u1FFC');
		add("\u1FFE", TeXConstants.TYPE_ACCENT, 8190, Font_ID.jlm_fcmrpg,
				'\u1FFE');
		add("textminus", TeXConstants.TYPE_ORDINARY, 45, Font_ID.jlm_cmr10,
				'\u2010');
		add("textendash", TeXConstants.TYPE_ORDINARY, 123, Font_ID.jlm_cmr10,
				'\u2013');
		add("textemdash", TeXConstants.TYPE_ORDINARY, 124, Font_ID.jlm_cmr10,
				'\u2014');
		add("|", TeXConstants.TYPE_ORDINARY, 107, Font_ID.jlm_cmsy10, '\u2016');
		add("\u2019", TeXConstants.TYPE_ACCENT, 8217, Font_ID.jlm_fcmrpg,
				'\u2019');
		add("dagger", TeXConstants.TYPE_BINARY_OPERATOR, 121,
				Font_ID.jlm_cmsy10, '\u2020');
		add("ddagger", TeXConstants.TYPE_BINARY_OPERATOR, 122,
				Font_ID.jlm_cmsy10, '\u2021');
		add("textperthousand", TeXConstants.TYPE_ORDINARY, 37,
				Font_ID.jlm_jlmr10, '\u2030');
		add("textpertenthousand", TeXConstants.TYPE_ORDINARY, 38,
				Font_ID.jlm_jlmr10, '\u2031');
		add("guilsinglleft", TeXConstants.TYPE_PUNCTUATION, 34,
				Font_ID.jlm_jlmi10, '\u2039');
		add("guilsinglright", TeXConstants.TYPE_PUNCTUATION, 35,
				Font_ID.jlm_jlmi10, '\u203A');
		add("euro", TeXConstants.TYPE_ORDINARY, 69, Font_ID.jlm_special,
				'\u20AC');
		add("Eulerconst", TeXConstants.TYPE_ORDINARY, 101,
				Font_ID.jlm_cmmi10_unchanged, '\u2107');
		add("hslash", TeXConstants.TYPE_ORDINARY, 125, Font_ID.jlm_msbm10,
				'\u210F');
		add("Im", TeXConstants.TYPE_ORDINARY, 61, Font_ID.jlm_cmsy10, '\u2111');
		add("ell", TeXConstants.TYPE_ORDINARY, 96, Font_ID.jlm_cmmi10_unchanged,
				'\u2113');
		add("wp", TeXConstants.TYPE_ORDINARY, 125, Font_ID.jlm_cmmi10_unchanged,
				'\u2118');
		add("Re", TeXConstants.TYPE_ORDINARY, 60, Font_ID.jlm_cmsy10, '\u211C');
		add("mho", TeXConstants.TYPE_ORDINARY, 102, Font_ID.jlm_msbm10,
				'\u2127');
		add("Finv", TeXConstants.TYPE_ORDINARY, 96, Font_ID.jlm_msbm10,
				'\u2132');
		add("aleph", TeXConstants.TYPE_ORDINARY, 64, Font_ID.jlm_cmsy10,
				'\u2135');
		add("beth", TeXConstants.TYPE_ORDINARY, 105, Font_ID.jlm_msbm10,
				'\u2136');
		add("gimel", TeXConstants.TYPE_ORDINARY, 106, Font_ID.jlm_msbm10,
				'\u2137');
		add("daleth", TeXConstants.TYPE_ORDINARY, 107, Font_ID.jlm_msbm10,
				'\u2138');
		add("Game", TeXConstants.TYPE_ORDINARY, 97, Font_ID.jlm_msbm10,
				'\u2141');
		add("Yup", TeXConstants.TYPE_BINARY_OPERATOR, 36, Font_ID.jlm_stmary10,
				'\u2144');
		add("leftarrow", TeXConstants.TYPE_RELATION, 195, Font_ID.jlm_cmsy10,
				'\u2190');
		add("uparrow", TeXConstants.TYPE_RELATION, 34, Font_ID.jlm_cmsy10,
				'\u2191');
		add("rightarrow", TeXConstants.TYPE_RELATION, 33, Font_ID.jlm_cmsy10,
				'\u2192');
		add("downarrow", TeXConstants.TYPE_RELATION, 35, Font_ID.jlm_cmsy10,
				'\u2193');
		add("leftrightarrow", TeXConstants.TYPE_RELATION, 36,
				Font_ID.jlm_cmsy10, '\u2194');
		add("updownarrow", TeXConstants.TYPE_RELATION, 108, Font_ID.jlm_cmsy10,
				'\u2195');
		add("nwarrow", TeXConstants.TYPE_RELATION, 45, Font_ID.jlm_cmsy10,
				'\u2196');
		add("nearrow", TeXConstants.TYPE_RELATION, 37, Font_ID.jlm_cmsy10,
				'\u2197');
		add("searrow", TeXConstants.TYPE_RELATION, 38, Font_ID.jlm_cmsy10,
				'\u2198');
		add("swarrow", TeXConstants.TYPE_RELATION, 46, Font_ID.jlm_cmsy10,
				'\u2199');
		add("nleftarrow", TeXConstants.TYPE_RELATION, 56, Font_ID.jlm_msbm10,
				'\u219A');
		add("nrightarrow", TeXConstants.TYPE_RELATION, 57, Font_ID.jlm_msbm10,
				'\u219B');
		add("rightsquigarrow", TeXConstants.TYPE_RELATION, 195,
				Font_ID.jlm_msam10, '\u219D');
		add("twoheadleftarrow", TeXConstants.TYPE_RELATION, 180,
				Font_ID.jlm_msam10, '\u219E');
		add("twoheadrightarrow", TeXConstants.TYPE_RELATION, 179,
				Font_ID.jlm_msam10, '\u21A0');
		add("leftarrowtail", TeXConstants.TYPE_RELATION, 190,
				Font_ID.jlm_msam10, '\u21A2');
		add("rightarrowtail", TeXConstants.TYPE_RELATION, 189,
				Font_ID.jlm_msam10, '\u21A3');
		add("looparrowleft", TeXConstants.TYPE_RELATION, 34, Font_ID.jlm_msam10,
				'\u21AB');
		add("looparrowright", TeXConstants.TYPE_RELATION, 35,
				Font_ID.jlm_msam10, '\u21AC');
		add("leftrightsquigarrow", TeXConstants.TYPE_RELATION, 33,
				Font_ID.jlm_msam10, '\u21AD');
		add("nleftrightarrow", TeXConstants.TYPE_RELATION, 61,
				Font_ID.jlm_msbm10, '\u21AE');
		add("lightning", TeXConstants.TYPE_ORDINARY, 64, Font_ID.jlm_stmary10,
				'\u21AF');
		add("Lsh", TeXConstants.TYPE_RELATION, 193, Font_ID.jlm_msam10,
				'\u21B0');
		add("Rsh", TeXConstants.TYPE_RELATION, 194, Font_ID.jlm_msam10,
				'\u21B1');
		add("curvearrowleft", TeXConstants.TYPE_RELATION, 120,
				Font_ID.jlm_msbm10, '\u21B6');
		add("curvearrowright", TeXConstants.TYPE_RELATION, 121,
				Font_ID.jlm_msbm10, '\u21B7');
		add("leftharpoonup", TeXConstants.TYPE_RELATION, 40,
				Font_ID.jlm_cmmi10_unchanged, '\u21BC');
		add("leftharpoondown", TeXConstants.TYPE_RELATION, 41,
				Font_ID.jlm_cmmi10_unchanged, '\u21BD');
		add("upharpoonright", TeXConstants.TYPE_RELATION, 185,
				Font_ID.jlm_msam10, '\u21BE');
		add("upharpoonleft", TeXConstants.TYPE_RELATION, 187,
				Font_ID.jlm_msam10, '\u21BF');
		add("rightharpoonup", TeXConstants.TYPE_RELATION, 42,
				Font_ID.jlm_cmmi10_unchanged, '\u21C0');
		add("rightharpoondown", TeXConstants.TYPE_RELATION, 43,
				Font_ID.jlm_cmmi10_unchanged, '\u21C1');
		add("downharpoonright", TeXConstants.TYPE_RELATION, 186,
				Font_ID.jlm_msam10, '\u21C2');
		add("downharpoonleft", TeXConstants.TYPE_RELATION, 188,
				Font_ID.jlm_msam10, '\u21C3');
		add("rightleftarrows", TeXConstants.TYPE_RELATION, 192,
				Font_ID.jlm_msam10, '\u21C4');
		add("leftrightarrows", TeXConstants.TYPE_RELATION, 191,
				Font_ID.jlm_msam10, '\u21C6');
		add("leftleftarrows", TeXConstants.TYPE_RELATION, 181,
				Font_ID.jlm_msam10, '\u21C7');
		add("upuparrows", TeXConstants.TYPE_RELATION, 183, Font_ID.jlm_msam10,
				'\u21C8');
		add("rightrightarrows", TeXConstants.TYPE_RELATION, 182,
				Font_ID.jlm_msam10, '\u21C9');
		add("downdownarrows", TeXConstants.TYPE_RELATION, 184,
				Font_ID.jlm_msam10, '\u21CA');
		add("leftrightharpoons", TeXConstants.TYPE_RELATION, 174,
				Font_ID.jlm_msam10, '\u21CB');
		add("rightleftharpoons", TeXConstants.TYPE_RELATION, 173,
				Font_ID.jlm_msam10, '\u21CC');
		add("nLeftarrow", TeXConstants.TYPE_RELATION, 58, Font_ID.jlm_msbm10,
				'\u21CD');
		add("nLeftrightarrow", TeXConstants.TYPE_RELATION, 60,
				Font_ID.jlm_msbm10, '\u21CE');
		add("nRightarrow", TeXConstants.TYPE_RELATION, 59, Font_ID.jlm_msbm10,
				'\u21CF');
		add("Leftarrow", TeXConstants.TYPE_RELATION, 40, Font_ID.jlm_cmsy10,
				'\u21D0');
		add("Uparrow", TeXConstants.TYPE_RELATION, 42, Font_ID.jlm_cmsy10,
				'\u21D1');
		add("Rightarrow", TeXConstants.TYPE_RELATION, 41, Font_ID.jlm_cmsy10,
				'\u21D2');
		add("Downarrow", TeXConstants.TYPE_RELATION, 43, Font_ID.jlm_cmsy10,
				'\u21D3');
		add("Leftrightarrow", TeXConstants.TYPE_RELATION, 44,
				Font_ID.jlm_cmsy10, '\u21D4');
		add("Updownarrow", TeXConstants.TYPE_RELATION, 109, Font_ID.jlm_cmsy10,
				'\u21D5');
		add("Lleftarrow", TeXConstants.TYPE_RELATION, 87, Font_ID.jlm_msam10,
				'\u21DA');
		add("Rrightarrow", TeXConstants.TYPE_RELATION, 86, Font_ID.jlm_msam10,
				'\u21DB');
		add("leftarrowtriangle", TeXConstants.TYPE_RELATION, 126,
				Font_ID.jlm_stmary10, '\u21FD');
		add("rightarrowtriangle", TeXConstants.TYPE_RELATION, 127,
				Font_ID.jlm_stmary10, '\u21FE');
		add("forall", TeXConstants.TYPE_ORDINARY, 56, Font_ID.jlm_cmsy10,
				'\u2200');
		add("complement", TeXConstants.TYPE_ORDINARY, 123, Font_ID.jlm_msam10,
				'\u2201');
		add("partial", TeXConstants.TYPE_ORDINARY, 64,
				Font_ID.jlm_cmmi10_unchanged, '\u2202');
		add("exists", TeXConstants.TYPE_ORDINARY, 57, Font_ID.jlm_cmsy10,
				'\u2203');
		add("nexists", TeXConstants.TYPE_ORDINARY, 64, Font_ID.jlm_msbm10,
				'\u2204');
		add("emptyset", TeXConstants.TYPE_ORDINARY, 59, Font_ID.jlm_cmsy10,
				'\u2205');
		add("nabla", TeXConstants.TYPE_ORDINARY, 114, Font_ID.jlm_cmsy10,
				'\u2207');
		add("in", TeXConstants.TYPE_RELATION, 50, Font_ID.jlm_cmsy10, '\u2208');
		add("ni", TeXConstants.TYPE_RELATION, 51, Font_ID.jlm_cmsy10, '\u220D');
		add("prod", TeXConstants.TYPE_BIG_OPERATOR, 81, Font_ID.jlm_cmex10,
				'\u220F');
		add("coprod", TeXConstants.TYPE_BIG_OPERATOR, 96, Font_ID.jlm_cmex10,
				'\u2210');
		add("sum", TeXConstants.TYPE_BIG_OPERATOR, 80, Font_ID.jlm_cmex10,
				'\u2211');
		add("mp", TeXConstants.TYPE_BINARY_OPERATOR, 168, Font_ID.jlm_cmsy10,
				'\u2213');
		add("dotplus", TeXConstants.TYPE_BINARY_OPERATOR, 117,
				Font_ID.jlm_msam10, '\u2214');
		add("setminus", TeXConstants.TYPE_BINARY_OPERATOR, 110,
				Font_ID.jlm_cmsy10, '\u2216');
		add("circ", TeXConstants.TYPE_BINARY_OPERATOR, 177, Font_ID.jlm_cmsy10,
				'\u2218');
		add("bullet", TeXConstants.TYPE_BINARY_OPERATOR, 178,
				Font_ID.jlm_cmsy10, '\u2219');
		add("propto", TeXConstants.TYPE_RELATION, 47, Font_ID.jlm_cmsy10,
				'\u221D');
		add("infty", TeXConstants.TYPE_ORDINARY, 49, Font_ID.jlm_cmsy10,
				'\u221E');
		add("angle", TeXConstants.TYPE_ORDINARY, 92, Font_ID.jlm_msam10,
				'\u2220');
		add("measuredangle", TeXConstants.TYPE_ORDINARY, 93, Font_ID.jlm_msam10,
				'\u2221');
		add("sphericalangle", TeXConstants.TYPE_ORDINARY, 94,
				Font_ID.jlm_msam10, '\u2222');
		add("shortmid", TeXConstants.TYPE_RELATION, 112, Font_ID.jlm_msbm10,
				'\u2223');
		add("nmid", TeXConstants.TYPE_RELATION, 45, Font_ID.jlm_msbm10,
				'\u2224');
		add("parallel", TeXConstants.TYPE_RELATION, 107, Font_ID.jlm_cmsy10,
				'\u2225');
		add("nshortparallel", TeXConstants.TYPE_RELATION, 47,
				Font_ID.jlm_msbm10, '\u2226');
		add("wedge", TeXConstants.TYPE_BINARY_OPERATOR, 94, Font_ID.jlm_cmsy10,
				'\u2227');
		add("vee", TeXConstants.TYPE_BINARY_OPERATOR, 95, Font_ID.jlm_cmsy10,
				'\u2228');
		add("cap", TeXConstants.TYPE_BINARY_OPERATOR, 92, Font_ID.jlm_cmsy10,
				'\u2229');
		add("cup", TeXConstants.TYPE_BINARY_OPERATOR, 91, Font_ID.jlm_cmsy10,
				'\u222A');
		add("therefore", TeXConstants.TYPE_RELATION, 41, Font_ID.jlm_msam10,
				'\u2234');
		add("because", TeXConstants.TYPE_RELATION, 42, Font_ID.jlm_msam10,
				'\u2235');
		add("sim", TeXConstants.TYPE_RELATION, 187, Font_ID.jlm_cmsy10,
				'\u223C');
		add("backsim", TeXConstants.TYPE_RELATION, 118, Font_ID.jlm_msam10,
				'\u223D');
		add("wr", TeXConstants.TYPE_BINARY_OPERATOR, 111, Font_ID.jlm_cmsy10,
				'\u2240');
		add("nsim", TeXConstants.TYPE_RELATION, 191, Font_ID.jlm_msbm10,
				'\u2241');
		add("eqsim", TeXConstants.TYPE_RELATION, 104, Font_ID.jlm_msbm10,
				'\u2242');
		add("simeq", TeXConstants.TYPE_RELATION, 39, Font_ID.jlm_cmsy10,
				'\u2243');
		add("ncong", TeXConstants.TYPE_RELATION, 192, Font_ID.jlm_msbm10,
				'\u2247');
		add("approx", TeXConstants.TYPE_RELATION, 188, Font_ID.jlm_cmsy10,
				'\u2248');
		add("approxeq", TeXConstants.TYPE_RELATION, 117, Font_ID.jlm_msbm10,
				'\u224A');
		add("asymp", TeXConstants.TYPE_RELATION, 179, Font_ID.jlm_cmsy10,
				'\u224D');
		add("Bumpeq", TeXConstants.TYPE_RELATION, 109, Font_ID.jlm_msam10,
				'\u224E');
		add("bumpeq", TeXConstants.TYPE_RELATION, 108, Font_ID.jlm_msam10,
				'\u224F');
		add("doteqdot", TeXConstants.TYPE_RELATION, 43, Font_ID.jlm_msam10,
				'\u2251');
		add("fallingdotseq", TeXConstants.TYPE_RELATION, 59, Font_ID.jlm_msam10,
				'\u2252');
		add("risingdotseq", TeXConstants.TYPE_RELATION, 58, Font_ID.jlm_msam10,
				'\u2253');
		add("eqcirc", TeXConstants.TYPE_RELATION, 80, Font_ID.jlm_msam10,
				'\u2256');
		add("circeq", TeXConstants.TYPE_RELATION, 36, Font_ID.jlm_msam10,
				'\u2257');
		add("triangleq", TeXConstants.TYPE_RELATION, 44, Font_ID.jlm_msam10,
				'\u225C');
		add("equiv", TeXConstants.TYPE_RELATION, 180, Font_ID.jlm_cmsy10,
				'\u2261');
		add("le", TeXConstants.TYPE_RELATION, 183, Font_ID.jlm_cmsy10,
				'\u2264');
		add("ge", TeXConstants.TYPE_RELATION, 184, Font_ID.jlm_cmsy10,
				'\u2265');
		add("leqq", TeXConstants.TYPE_RELATION, 53, Font_ID.jlm_msam10,
				'\u2266');
		add("geqq", TeXConstants.TYPE_RELATION, 61, Font_ID.jlm_msam10,
				'\u2267');
		add("lvertneqq", TeXConstants.TYPE_RELATION, 161, Font_ID.jlm_msbm10,
				'\u2268');
		add("gneqq", TeXConstants.TYPE_RELATION, 170, Font_ID.jlm_msbm10,
				'\u2269');
		add("ll", TeXConstants.TYPE_RELATION, 191, Font_ID.jlm_cmsy10,
				'\u226A');
		add("between", TeXConstants.TYPE_RELATION, 71, Font_ID.jlm_msam10,
				'\u226C');
		add("nless", TeXConstants.TYPE_RELATION, 165, Font_ID.jlm_msbm10,
				'\u226E');
		add("ngtr", TeXConstants.TYPE_RELATION, 166, Font_ID.jlm_msbm10,
				'\u226F');
		add("nleqslant", TeXConstants.TYPE_RELATION, 173, Font_ID.jlm_msbm10,
				'\u2270');
		add("ngeqslant", TeXConstants.TYPE_RELATION, 174, Font_ID.jlm_msbm10,
				'\u2271');
		add("lesssim", TeXConstants.TYPE_RELATION, 46, Font_ID.jlm_msam10,
				'\u2272');
		add("gtrsim", TeXConstants.TYPE_RELATION, 38, Font_ID.jlm_msam10,
				'\u2273');
		add("lessgtr", TeXConstants.TYPE_RELATION, 55, Font_ID.jlm_msam10,
				'\u2276');
		add("gtrless", TeXConstants.TYPE_RELATION, 63, Font_ID.jlm_msam10,
				'\u2277');
		add("prec", TeXConstants.TYPE_RELATION, 193, Font_ID.jlm_cmsy10,
				'\u227A');
		add("succ", TeXConstants.TYPE_RELATION, 194, Font_ID.jlm_cmsy10,
				'\u227B');
		add("preccurlyeq", TeXConstants.TYPE_RELATION, 52, Font_ID.jlm_msam10,
				'\u227C');
		add("succcurlyeq", TeXConstants.TYPE_RELATION, 60, Font_ID.jlm_msam10,
				'\u227D');
		add("precsim", TeXConstants.TYPE_RELATION, 45, Font_ID.jlm_msam10,
				'\u227E');
		add("succsim", TeXConstants.TYPE_RELATION, 37, Font_ID.jlm_msam10,
				'\u227F');
		add("nprec", TeXConstants.TYPE_RELATION, 167, Font_ID.jlm_msbm10,
				'\u2280');
		add("nsucc", TeXConstants.TYPE_RELATION, 168, Font_ID.jlm_msbm10,
				'\u2281');
		add("subset", TeXConstants.TYPE_RELATION, 189, Font_ID.jlm_cmsy10,
				'\u2282');
		add("supset", TeXConstants.TYPE_RELATION, 190, Font_ID.jlm_cmsy10,
				'\u2283');
		add("subseteq", TeXConstants.TYPE_RELATION, 181, Font_ID.jlm_cmsy10,
				'\u2286');
		add("supseteq", TeXConstants.TYPE_RELATION, 182, Font_ID.jlm_cmsy10,
				'\u2287');
		add("nsubseteq", TeXConstants.TYPE_RELATION, 42, Font_ID.jlm_msbm10,
				'\u2288');
		add("nsupseteq", TeXConstants.TYPE_RELATION, 43, Font_ID.jlm_msbm10,
				'\u2289');
		add("subsetneq", TeXConstants.TYPE_RELATION, 40, Font_ID.jlm_msbm10,
				'\u228A');
		add("supsetneq", TeXConstants.TYPE_RELATION, 41, Font_ID.jlm_msbm10,
				'\u228B');
		add("uplus", TeXConstants.TYPE_BINARY_OPERATOR, 93, Font_ID.jlm_cmsy10,
				'\u228E');
		add("sqsubset", TeXConstants.TYPE_RELATION, 64, Font_ID.jlm_msam10,
				'\u228F');
		add("sqsupset", TeXConstants.TYPE_RELATION, 65, Font_ID.jlm_msam10,
				'\u2290');
		add("sqsubseteq", TeXConstants.TYPE_RELATION, 118, Font_ID.jlm_cmsy10,
				'\u2291');
		add("sqsupseteq", TeXConstants.TYPE_RELATION, 119, Font_ID.jlm_cmsy10,
				'\u2292');
		add("sqcap", TeXConstants.TYPE_BINARY_OPERATOR, 117, Font_ID.jlm_cmsy10,
				'\u2293');
		add("sqcup", TeXConstants.TYPE_BINARY_OPERATOR, 116, Font_ID.jlm_cmsy10,
				'\u2294');
		add("oplus", TeXConstants.TYPE_BINARY_OPERATOR, 169, Font_ID.jlm_cmsy10,
				'\u2295');
		add("ominus", TeXConstants.TYPE_BINARY_OPERATOR, 170,
				Font_ID.jlm_cmsy10, '\u2296');
		add("otimes", TeXConstants.TYPE_BINARY_OPERATOR, 172,
				Font_ID.jlm_cmsy10, '\u2297');
		add("oslash", TeXConstants.TYPE_BINARY_OPERATOR, 174,
				Font_ID.jlm_cmsy10, '\u2298');
		add("odot", TeXConstants.TYPE_BINARY_OPERATOR, 175, Font_ID.jlm_cmsy10,
				'\u2299');
		add("circledcirc", TeXConstants.TYPE_BINARY_OPERATOR, 125,
				Font_ID.jlm_msam10, '\u229A');
		add("circledast", TeXConstants.TYPE_BINARY_OPERATOR, 126,
				Font_ID.jlm_msam10, '\u229B');
		add("circleddash", TeXConstants.TYPE_BINARY_OPERATOR, 196,
				Font_ID.jlm_msam10, '\u229D');
		add("boxplus", TeXConstants.TYPE_BINARY_OPERATOR, 162,
				Font_ID.jlm_msam10, '\u229E');
		add("boxminus", TeXConstants.TYPE_BINARY_OPERATOR, 175,
				Font_ID.jlm_msam10, '\u229F');
		add("boxtimes", TeXConstants.TYPE_BINARY_OPERATOR, 163,
				Font_ID.jlm_msam10, '\u22A0');
		add("boxdot", TeXConstants.TYPE_BINARY_OPERATOR, 58,
				Font_ID.jlm_stmary10, '\u22A1');
		add("vdash", TeXConstants.TYPE_RELATION, 96, Font_ID.jlm_cmsy10,
				'\u22A2');
		add("dashv", TeXConstants.TYPE_RELATION, 97, Font_ID.jlm_cmsy10,
				'\u22A3');
		add("top", TeXConstants.TYPE_ORDINARY, 62, Font_ID.jlm_cmsy10,
				'\u22A4');
		add("perp", TeXConstants.TYPE_RELATION, 63, Font_ID.jlm_cmsy10,
				'\u22A5');
		add("vDash", TeXConstants.TYPE_RELATION, 178, Font_ID.jlm_msam10,
				'\u22A8');
		add("Vdash", TeXConstants.TYPE_RELATION, 176, Font_ID.jlm_msam10,
				'\u22A9');
		add("Vvdash", TeXConstants.TYPE_RELATION, 177, Font_ID.jlm_msam10,
				'\u22AA');
		add("nvdash", TeXConstants.TYPE_RELATION, 48, Font_ID.jlm_msbm10,
				'\u22AC');
		add("nvDash", TeXConstants.TYPE_RELATION, 50, Font_ID.jlm_msbm10,
				'\u22AD');
		add("nVdash", TeXConstants.TYPE_RELATION, 49, Font_ID.jlm_msbm10,
				'\u22AE');
		add("nVDash", TeXConstants.TYPE_RELATION, 51, Font_ID.jlm_msbm10,
				'\u22AF');
		add("lhd", TeXConstants.TYPE_RELATION, 67, Font_ID.jlm_msam10,
				'\u22B2');
		add("rhd", TeXConstants.TYPE_RELATION, 66, Font_ID.jlm_msam10,
				'\u22B3');
		add("trianglelefteq", TeXConstants.TYPE_RELATION, 69,
				Font_ID.jlm_msam10, '\u22B4');
		add("trianglerighteq", TeXConstants.TYPE_RELATION, 68,
				Font_ID.jlm_msam10, '\u22B5');
		add("multimap", TeXConstants.TYPE_RELATION, 40, Font_ID.jlm_msam10,
				'\u22B8');
		add("intercal", TeXConstants.TYPE_BINARY_OPERATOR, 124,
				Font_ID.jlm_msam10, '\u22BA');
		add("bigwedge", TeXConstants.TYPE_BIG_OPERATOR, 86, Font_ID.jlm_cmex10,
				'\u22C0');
		add("bigvee", TeXConstants.TYPE_BIG_OPERATOR, 87, Font_ID.jlm_cmex10,
				'\u22C1');
		add("bigcap", TeXConstants.TYPE_BIG_OPERATOR, 84, Font_ID.jlm_cmex10,
				'\u22C2');
		add("bigcup", TeXConstants.TYPE_BIG_OPERATOR, 83, Font_ID.jlm_cmex10,
				'\u22C3');
		add("diamond", TeXConstants.TYPE_BINARY_OPERATOR, 166,
				Font_ID.jlm_cmsy10, '\u22C4');
		add("star", TeXConstants.TYPE_BINARY_OPERATOR, 63,
				Font_ID.jlm_cmmi10_unchanged, '\u22C6');
		add("divideontimes", TeXConstants.TYPE_BINARY_OPERATOR, 62,
				Font_ID.jlm_msbm10, '\u22C7');
		add("ltimes", TeXConstants.TYPE_BINARY_OPERATOR, 110,
				Font_ID.jlm_msbm10, '\u22C9');
		add("rtimes", TeXConstants.TYPE_BINARY_OPERATOR, 111,
				Font_ID.jlm_msbm10, '\u22CA');
		add("leftthreetimes", TeXConstants.TYPE_BINARY_OPERATOR, 104,
				Font_ID.jlm_msam10, '\u22CB');
		add("rightthreetimes", TeXConstants.TYPE_BINARY_OPERATOR, 105,
				Font_ID.jlm_msam10, '\u22CC');
		add("backsimeq", TeXConstants.TYPE_RELATION, 119, Font_ID.jlm_msam10,
				'\u22CD');
		add("curlyvee", TeXConstants.TYPE_BINARY_OPERATOR, 103,
				Font_ID.jlm_msam10, '\u22CE');
		add("curlywedge", TeXConstants.TYPE_BINARY_OPERATOR, 102,
				Font_ID.jlm_msam10, '\u22CF');
		add("Subset", TeXConstants.TYPE_RELATION, 98, Font_ID.jlm_msam10,
				'\u22D0');
		add("Supset", TeXConstants.TYPE_RELATION, 99, Font_ID.jlm_msam10,
				'\u22D1');
		add("Cap", TeXConstants.TYPE_BINARY_OPERATOR, 101, Font_ID.jlm_msam10,
				'\u22D2');
		add("Cup", TeXConstants.TYPE_BINARY_OPERATOR, 100, Font_ID.jlm_msam10,
				'\u22D3');
		add("pitchfork", TeXConstants.TYPE_RELATION, 116, Font_ID.jlm_msam10,
				'\u22D4');
		add("lessdot", TeXConstants.TYPE_BINARY_OPERATOR, 108,
				Font_ID.jlm_msbm10, '\u22D6');
		add("gtrdot", TeXConstants.TYPE_BINARY_OPERATOR, 109,
				Font_ID.jlm_msbm10, '\u22D7');
		add("ggg", TeXConstants.TYPE_RELATION, 111, Font_ID.jlm_msam10,
				'\u22D9');
		add("gtreqless", TeXConstants.TYPE_RELATION, 82, Font_ID.jlm_msam10,
				'\u22DB');
		add("curlyeqprec", TeXConstants.TYPE_RELATION, 50, Font_ID.jlm_msam10,
				'\u22DE');
		add("curlyeqsucc", TeXConstants.TYPE_RELATION, 51, Font_ID.jlm_msam10,
				'\u22DF');
		add("lnsim", TeXConstants.TYPE_RELATION, 181, Font_ID.jlm_msbm10,
				'\u22E6');
		add("gnsim", TeXConstants.TYPE_RELATION, 182, Font_ID.jlm_msbm10,
				'\u22E7');
		add("precnsim", TeXConstants.TYPE_RELATION, 179, Font_ID.jlm_msbm10,
				'\u22E8');
		add("succnsim", TeXConstants.TYPE_RELATION, 180, Font_ID.jlm_msbm10,
				'\u22E9');
		add("ntriangleleft", TeXConstants.TYPE_RELATION, 54, Font_ID.jlm_msbm10,
				'\u22EA');
		add("ntriangleright", TeXConstants.TYPE_RELATION, 55,
				Font_ID.jlm_msbm10, '\u22EB');
		add("ntrianglelefteq", TeXConstants.TYPE_RELATION, 53,
				Font_ID.jlm_msbm10, '\u22EC');
		add("ntrianglerighteq", TeXConstants.TYPE_RELATION, 52,
				Font_ID.jlm_msbm10, '\u22ED');
		add("inplus", TeXConstants.TYPE_RELATION, 97, Font_ID.jlm_stmary10,
				'\u22F4');
		add("niplus", TeXConstants.TYPE_RELATION, 98, Font_ID.jlm_stmary10,
				'\u22FC');
		add("barwedge", TeXConstants.TYPE_BINARY_OPERATOR, 90,
				Font_ID.jlm_msam10, '\u2305');
		add("doublebarwedge", TeXConstants.TYPE_BINARY_OPERATOR, 91,
				Font_ID.jlm_msam10, '\u2306');
		add("lceil", TeXConstants.TYPE_OPENING, 100, Font_ID.jlm_cmsy10,
				'\u2308');
		add("rceil", TeXConstants.TYPE_CLOSING, 101, Font_ID.jlm_cmsy10,
				'\u2309');
		add("lfloor", TeXConstants.TYPE_OPENING, 98, Font_ID.jlm_cmsy10,
				'\u230A');
		add("rfloor", TeXConstants.TYPE_CLOSING, 99, Font_ID.jlm_cmsy10,
				'\u230B');
		add("ulcorner", TeXConstants.TYPE_OPENING, 112, Font_ID.jlm_msam10,
				'\u231C');
		add("urcorner", TeXConstants.TYPE_CLOSING, 113, Font_ID.jlm_msam10,
				'\u231D');
		add("llcorner", TeXConstants.TYPE_OPENING, 120, Font_ID.jlm_msam10,
				'\u231E');
		add("lrcorner", TeXConstants.TYPE_CLOSING, 121, Font_ID.jlm_msam10,
				'\u231F');
		add("smallfrown", TeXConstants.TYPE_RELATION, 97, Font_ID.jlm_msam10,
				'\u2322');
		add("smallsmile", TeXConstants.TYPE_RELATION, 96, Font_ID.jlm_msam10,
				'\u2323');
		add("langle", TeXConstants.TYPE_OPENING, 104, Font_ID.jlm_cmsy10,
				'\u2329');
		add("rangle", TeXConstants.TYPE_CLOSING, 105, Font_ID.jlm_cmsy10,
				'\u232A');
		add("lmoustache", TeXConstants.TYPE_OPENING, 64, Font_ID.jlm_moustache,
				'\u23B0');
		add("rmoustache", TeXConstants.TYPE_CLOSING, 65, Font_ID.jlm_moustache,
				'\u23B1');
		add("diagup", TeXConstants.TYPE_ORDINARY, 193, Font_ID.jlm_msbm10,
				'\u2571');
		add("diagdown", TeXConstants.TYPE_ORDINARY, 194, Font_ID.jlm_msbm10,
				'\u2572');
		add("blacksquare", TeXConstants.TYPE_ORDINARY, 165, Font_ID.jlm_msam10,
				'\u25A0');
		add("square", TeXConstants.TYPE_ORDINARY, 164, Font_ID.jlm_msam10,
				'\u25A1');
		add("bigtriangleup", TeXConstants.TYPE_BIG_OPERATOR, 129,
				Font_ID.jlm_stmary10, '\u25B3');
		add("blacktriangle", TeXConstants.TYPE_ORDINARY, 78, Font_ID.jlm_msam10,
				'\u25B4');
		add("triangle", TeXConstants.TYPE_ORDINARY, 52, Font_ID.jlm_cmsy10,
				'\u25B5');
		add("blacktriangleright", TeXConstants.TYPE_RELATION, 73,
				Font_ID.jlm_msam10, '\u25B6');
		add("triangleright", TeXConstants.TYPE_BINARY_OPERATOR, 46,
				Font_ID.jlm_cmmi10_unchanged, '\u25B7');
		add("bigtriangledown", TeXConstants.TYPE_BIG_OPERATOR, 128,
				Font_ID.jlm_stmary10, '\u25BD');
		add("blacktriangledown", TeXConstants.TYPE_ORDINARY, 72,
				Font_ID.jlm_msam10, '\u25BE');
		add("triangledown", TeXConstants.TYPE_ORDINARY, 79, Font_ID.jlm_msam10,
				'\u25BF');
		add("blacktriangleleft", TeXConstants.TYPE_RELATION, 74,
				Font_ID.jlm_msam10, '\u25C0');
		add("triangleleft", TeXConstants.TYPE_BINARY_OPERATOR, 47,
				Font_ID.jlm_cmmi10_unchanged, '\u25C1');
		add("lozenge", TeXConstants.TYPE_ORDINARY, 167, Font_ID.jlm_msam10,
				'\u25CA');
		add("boxbar", TeXConstants.TYPE_BINARY_OPERATOR, 57,
				Font_ID.jlm_stmary10, '\u25EB');
		add("bigcirc", TeXConstants.TYPE_BINARY_OPERATOR, 176,
				Font_ID.jlm_cmsy10, '\u25EF');
		add("bigstar", TeXConstants.TYPE_ORDINARY, 70, Font_ID.jlm_msam10,
				'\u2605');
		add("spadesuit", TeXConstants.TYPE_ORDINARY, 196, Font_ID.jlm_cmsy10,
				'\u2660');
		add("heartsuit", TeXConstants.TYPE_ORDINARY, 126, Font_ID.jlm_cmsy10,
				'\u2661');
		add("diamondsuit", TeXConstants.TYPE_ORDINARY, 125, Font_ID.jlm_cmsy10,
				'\u2662');
		add("clubsuit", TeXConstants.TYPE_ORDINARY, 124, Font_ID.jlm_cmsy10,
				'\u2663');
		add("flat", TeXConstants.TYPE_ORDINARY, 91,
				Font_ID.jlm_cmmi10_unchanged, '\u266D');
		add("natural", TeXConstants.TYPE_ORDINARY, 92,
				Font_ID.jlm_cmmi10_unchanged, '\u266E');
		add("sharp", TeXConstants.TYPE_ORDINARY, 93,
				Font_ID.jlm_cmmi10_unchanged, '\u266F');
		add("checkmark", TeXConstants.TYPE_ORDINARY, 88, Font_ID.jlm_msam10,
				'\u2713');
		add("maltese", TeXConstants.TYPE_ORDINARY, 122, Font_ID.jlm_msam10,
				'\u2720');
		add("Lbag", TeXConstants.TYPE_OPENING, 104, Font_ID.jlm_stmary10,
				'\u27C5');
		add("Rbag", TeXConstants.TYPE_CLOSING, 105, Font_ID.jlm_stmary10,
				'\u27C6');
		add("llbracket", TeXConstants.TYPE_OPENING, 106, Font_ID.jlm_stmary10,
				'\u27E6');
		add("rrbracket", TeXConstants.TYPE_CLOSING, 107, Font_ID.jlm_stmary10,
				'\u27E7');
		add("leadsto", TeXConstants.TYPE_RELATION, 195, Font_ID.jlm_msam10,
				'\u27FF');
		add("minuso", TeXConstants.TYPE_BINARY_OPERATOR, 42,
				Font_ID.jlm_stmary10, '\u29B5');
		add("varocircle", TeXConstants.TYPE_BINARY_OPERATOR, 53,
				Font_ID.jlm_stmary10, '\u29BE');
		add("olessthan", TeXConstants.TYPE_BINARY_OPERATOR, 92,
				Font_ID.jlm_stmary10, '\u29C0');
		add("ogreaterthan", TeXConstants.TYPE_BINARY_OPERATOR, 93,
				Font_ID.jlm_stmary10, '\u29C1');
		add("boxslash", TeXConstants.TYPE_BINARY_OPERATOR, 59,
				Font_ID.jlm_stmary10, '\u29C4');
		add("boxbslash", TeXConstants.TYPE_BINARY_OPERATOR, 60,
				Font_ID.jlm_stmary10, '\u29C5');
		add("boxast", TeXConstants.TYPE_BINARY_OPERATOR, 56,
				Font_ID.jlm_stmary10, '\u29C6');
		add("boxcircle", TeXConstants.TYPE_BINARY_OPERATOR, 61,
				Font_ID.jlm_stmary10, '\u29C7');
		add("boxbox", TeXConstants.TYPE_BINARY_OPERATOR, 62,
				Font_ID.jlm_stmary10, '\u29C8');
		add("blacklozenge", TeXConstants.TYPE_ORDINARY, 168, Font_ID.jlm_msam10,
				'\u29EB');
		add("bigodot", TeXConstants.TYPE_BIG_OPERATOR, 74, Font_ID.jlm_cmex10,
				'\u2A00');
		add("bigoplus", TeXConstants.TYPE_BIG_OPERATOR, 76, Font_ID.jlm_cmex10,
				'\u2A01');
		add("bigotimes", TeXConstants.TYPE_BIG_OPERATOR, 78, Font_ID.jlm_cmex10,
				'\u2A02');
		add("biguplus", TeXConstants.TYPE_BIG_OPERATOR, 85, Font_ID.jlm_cmex10,
				'\u2A04');
		add("bigsqcup", TeXConstants.TYPE_BIG_OPERATOR, 70, Font_ID.jlm_cmex10,
				'\u2A06');
		add("amalg", TeXConstants.TYPE_BINARY_OPERATOR, 113, Font_ID.jlm_cmsy10,
				'\u2A3F');
		add("veebar", TeXConstants.TYPE_BINARY_OPERATOR, 89, Font_ID.jlm_msam10,
				'\u2A61');
		add("leqslant", TeXConstants.TYPE_RELATION, 54, Font_ID.jlm_msam10,
				'\u2A7D');
		add("geqslant", TeXConstants.TYPE_RELATION, 62, Font_ID.jlm_msam10,
				'\u2A7E');
		add("lessapprox", TeXConstants.TYPE_RELATION, 47, Font_ID.jlm_msam10,
				'\u2A85');
		add("gtrapprox", TeXConstants.TYPE_RELATION, 39, Font_ID.jlm_msam10,
				'\u2A86');
		add("lneq", TeXConstants.TYPE_RELATION, 175, Font_ID.jlm_msbm10,
				'\u2A87');
		add("gneq", TeXConstants.TYPE_RELATION, 176, Font_ID.jlm_msbm10,
				'\u2A88');
		add("lnapprox", TeXConstants.TYPE_RELATION, 189, Font_ID.jlm_msbm10,
				'\u2A89');
		add("gnapprox", TeXConstants.TYPE_RELATION, 190, Font_ID.jlm_msbm10,
				'\u2A8A');
		add("lesseqqgtr", TeXConstants.TYPE_RELATION, 83, Font_ID.jlm_msam10,
				'\u2A8B');
		add("gtreqqless", TeXConstants.TYPE_RELATION, 84, Font_ID.jlm_msam10,
				'\u2A8C');
		add("eqslantless", TeXConstants.TYPE_RELATION, 48, Font_ID.jlm_msam10,
				'\u2A95');
		add("eqslantgtr", TeXConstants.TYPE_RELATION, 49, Font_ID.jlm_msam10,
				'\u2A96');
		add("gg", TeXConstants.TYPE_RELATION, 192, Font_ID.jlm_cmsy10,
				'\u2AA2');
		add("leftslice", TeXConstants.TYPE_BINARY_OPERATOR, 82,
				Font_ID.jlm_stmary10, '\u2AA6');
		add("rightslice", TeXConstants.TYPE_BINARY_OPERATOR, 83,
				Font_ID.jlm_stmary10, '\u2AA7');
		add("preceq", TeXConstants.TYPE_RELATION, 185, Font_ID.jlm_cmsy10,
				'\u2AAF');
		add("succeq", TeXConstants.TYPE_RELATION, 186, Font_ID.jlm_cmsy10,
				'\u2AB0');
		add("precneqq", TeXConstants.TYPE_RELATION, 185, Font_ID.jlm_msbm10,
				'\u2AB5');
		add("succneqq", TeXConstants.TYPE_RELATION, 186, Font_ID.jlm_msbm10,
				'\u2AB6');
		add("precapprox", TeXConstants.TYPE_RELATION, 119, Font_ID.jlm_msbm10,
				'\u2AB7');
		add("succapprox", TeXConstants.TYPE_RELATION, 118, Font_ID.jlm_msbm10,
				'\u2AB8');
		add("precnapprox", TeXConstants.TYPE_RELATION, 187, Font_ID.jlm_msbm10,
				'\u2AB9');
		add("succnapprox", TeXConstants.TYPE_RELATION, 188, Font_ID.jlm_msbm10,
				'\u2ABA');
		add("subseteqq", TeXConstants.TYPE_RELATION, 106, Font_ID.jlm_msam10,
				'\u2AC5');
		add("supseteqq", TeXConstants.TYPE_RELATION, 107, Font_ID.jlm_msam10,
				'\u2AC6');
		add("subsetneqq", TeXConstants.TYPE_RELATION, 36, Font_ID.jlm_msbm10,
				'\u2ACB');
		add("supsetneqq", TeXConstants.TYPE_RELATION, 37, Font_ID.jlm_msbm10,
				'\u2ACC');
		add("interleave", TeXConstants.TYPE_BINARY_OPERATOR, 89,
				Font_ID.jlm_stmary10, '\u2AF4');
		add("biginterleave", TeXConstants.TYPE_BIG_OPERATOR, 135,
				Font_ID.jlm_stmary10, '\u2AFC');
		add("sslash", TeXConstants.TYPE_BINARY_OPERATOR, 44,
				Font_ID.jlm_stmary10, '\u2AFD');
		add("talloblong", TeXConstants.TYPE_BINARY_OPERATOR, 88,
				Font_ID.jlm_stmary10, '\u2AFE');

		add("ngeq", TeXConstants.TYPE_RELATION, 164, Font_ID.jlm_msbm10);
		add("polishlcross", TeXConstants.TYPE_ORDINARY, 195, Font_ID.jlm_cmr10);
		add("varodot", TeXConstants.TYPE_BINARY_OPERATOR, 50,
				Font_ID.jlm_stmary10);
		add("textpercent", TeXConstants.TYPE_ORDINARY, 37, Font_ID.jlm_cmr10);
		add("thickapprox", TeXConstants.TYPE_RELATION, 116, Font_ID.jlm_msbm10);
		add("surdsign", TeXConstants.TYPE_ORDINARY, 112, Font_ID.jlm_cmsy10);
		add("i", TeXConstants.TYPE_ORDINARY, 179, Font_ID.jlm_cmti10);
		add("subsetplus", TeXConstants.TYPE_RELATION, 100,
				Font_ID.jlm_stmary10);
		add("j", TeXConstants.TYPE_ORDINARY, 180, Font_ID.jlm_cmti10);
		add("smallsetminus", TeXConstants.TYPE_BINARY_OPERATOR, 114,
				Font_ID.jlm_msbm10);
		add("shortrightarrow", TeXConstants.TYPE_RELATION, 33,
				Font_ID.jlm_stmary10);
		add("Delta", TeXConstants.TYPE_ORDINARY, 162, Font_ID.jlm_cmr10);
		add("thicksim", TeXConstants.TYPE_RELATION, 115, Font_ID.jlm_msbm10);
		add("tie", TeXConstants.TYPE_ACCENT, 196, Font_ID.jlm_cmmi10_unchanged);
		add("ldotp", TeXConstants.TYPE_PUNCTUATION, 46, Font_ID.jlm_cmss10);
		add("lacc", TeXConstants.TYPE_ORDINARY, 102, Font_ID.jlm_cmsy10);
		add("check", TeXConstants.TYPE_ACCENT, 183, Font_ID.jlm_cmr10);
		add("digamma", TeXConstants.TYPE_ORDINARY, 122, Font_ID.jlm_msbm10);
		add("bbslash", TeXConstants.TYPE_BINARY_OPERATOR, 45,
				Font_ID.jlm_stmary10);
		add("varsupsetneq", TeXConstants.TYPE_RELATION, 33, Font_ID.jlm_msbm10);
		add("Lambda", TeXConstants.TYPE_ORDINARY, 164, Font_ID.jlm_cmr10);
		add("boxempty", TeXConstants.TYPE_BINARY_OPERATOR, 63,
				Font_ID.jlm_stmary10);
		add("llceil", TeXConstants.TYPE_OPENING, 118, Font_ID.jlm_stmary10);
		add("vartriangleleft", TeXConstants.TYPE_RELATION, 67,
				Font_ID.jlm_msam10);
		add("textdbend", TeXConstants.TYPE_ORDINARY, 126,
				Font_ID.jlm_jlmr10_unchanged);
		add("varPsi", TeXConstants.TYPE_ORDINARY, 170, Font_ID.jlm_cmmi10);
		add("widehat", TeXConstants.TYPE_ACCENT, 98, Font_ID.jlm_cmex10);
		add("unrhd", TeXConstants.TYPE_RELATION, 68, Font_ID.jlm_msam10);
		add("varobslash", TeXConstants.TYPE_BINARY_OPERATOR, 52,
				Font_ID.jlm_stmary10);
		add("nplus", TeXConstants.TYPE_BINARY_OPERATOR, 99,
				Font_ID.jlm_stmary10);
		add("Upsilon", TeXConstants.TYPE_ORDINARY, 168, Font_ID.jlm_cmr10);
		add("ntrianglerighteqslant", TeXConstants.TYPE_RELATION, 115,
				Font_ID.jlm_stmary10);
		add("Phi", TeXConstants.TYPE_ORDINARY, 169, Font_ID.jlm_cmr10);
		add("curlyveedownarrow", TeXConstants.TYPE_RELATION, 78,
				Font_ID.jlm_stmary10);
		add("varcurlyvee", TeXConstants.TYPE_BINARY_OPERATOR, 40,
				Font_ID.jlm_stmary10);
		add("gvertneqq", TeXConstants.TYPE_RELATION, 162, Font_ID.jlm_msbm10);
		add("obar", TeXConstants.TYPE_BINARY_OPERATOR, 90,
				Font_ID.jlm_stmary10);
		add("Vert", TeXConstants.TYPE_ORDINARY, 107, Font_ID.jlm_cmsy10);
		add("jlatexmatharobase", TeXConstants.TYPE_ORDINARY, 64,
				Font_ID.jlm_cmr10);
		add("neg", TeXConstants.TYPE_ORDINARY, 58, Font_ID.jlm_cmsy10);
		add("nsucceq", TeXConstants.TYPE_RELATION, 178, Font_ID.jlm_msbm10);
		add("owns", TeXConstants.TYPE_RELATION, 51, Font_ID.jlm_cmsy10);
		add("shortuparrow", TeXConstants.TYPE_RELATION, 34,
				Font_ID.jlm_stmary10);
		add("lhook", TeXConstants.TYPE_ORDINARY, 44,
				Font_ID.jlm_cmmi10_unchanged);
		add("texteuro", TeXConstants.TYPE_ORDINARY, 101, Font_ID.jlm_special);
		add("llfloor", TeXConstants.TYPE_OPENING, 116, Font_ID.jlm_stmary10);
		add("varcurlywedge", TeXConstants.TYPE_BINARY_OPERATOR, 41,
				Font_ID.jlm_stmary10);
		add("Omega", TeXConstants.TYPE_ORDINARY, 173, Font_ID.jlm_cmr10);
		add("oblong", TeXConstants.TYPE_BINARY_OPERATOR, 96,
				Font_ID.jlm_stmary10);
		add("land", TeXConstants.TYPE_BINARY_OPERATOR, 94, Font_ID.jlm_cmsy10);
		add("fatsemi", TeXConstants.TYPE_BINARY_OPERATOR, 67,
				Font_ID.jlm_stmary10);
		add("subsetpluseq", TeXConstants.TYPE_RELATION, 102,
				Font_ID.jlm_stmary10);
		add("varoslash", TeXConstants.TYPE_BINARY_OPERATOR, 51,
				Font_ID.jlm_stmary10);
		add("merge", TeXConstants.TYPE_BINARY_OPERATOR, 65,
				Font_ID.jlm_stmary10);
		add("Pi", TeXConstants.TYPE_ORDINARY, 166, Font_ID.jlm_cmr10);
		add("leq", TeXConstants.TYPE_RELATION, 183, Font_ID.jlm_cmsy10,
				'\u2264');
		add("obslash", TeXConstants.TYPE_BINARY_OPERATOR, 91,
				Font_ID.jlm_stmary10);
		add("varoplus", TeXConstants.TYPE_BINARY_OPERATOR, 54,
				Font_ID.jlm_stmary10);
		add("varPhi", TeXConstants.TYPE_ORDINARY, 169, Font_ID.jlm_cmmi10);
		add("nsupseteqq", TeXConstants.TYPE_RELATION, 35, Font_ID.jlm_msbm10);
		add("slashdel", TeXConstants.TYPE_OPENING, 47, Font_ID.jlm_cmr10);
		add("nshortmid", TeXConstants.TYPE_RELATION, 46, Font_ID.jlm_msbm10);
		add("ddot", TeXConstants.TYPE_ACCENT, 196, Font_ID.jlm_cmr10);
		add("smallint", TeXConstants.TYPE_BIG_OPERATOR, 115,
				Font_ID.jlm_cmsy10);
		add("bigcurlywedge", TeXConstants.TYPE_BIG_OPERATOR, 131,
				Font_ID.jlm_stmary10);
		add("varOmega", TeXConstants.TYPE_ORDINARY, 173, Font_ID.jlm_cmmi10);
		add("sswarrow", TeXConstants.TYPE_RELATION, 68, Font_ID.jlm_stmary10);
		add("circlearrowright", TeXConstants.TYPE_RELATION, 169,
				Font_ID.jlm_msam10);
		add("Mapsfromchar", TeXConstants.TYPE_RELATION, 124,
				Font_ID.jlm_stmary10);
		add("hat", TeXConstants.TYPE_ACCENT, 94, Font_ID.jlm_cmr10);
		add("hbar", TeXConstants.TYPE_ORDINARY, 126, Font_ID.jlm_msbm10);
		add("lneqq", TeXConstants.TYPE_RELATION, 169, Font_ID.jlm_msbm10);
		add("Psi", TeXConstants.TYPE_ORDINARY, 170, Font_ID.jlm_cmr10);
		add("Theta", TeXConstants.TYPE_ORDINARY, 163, Font_ID.jlm_cmr10);
		add("doubleacute", TeXConstants.TYPE_ACCENT, 125, Font_ID.jlm_cmr10);
		add("varsubsetneqq", TeXConstants.TYPE_RELATION, 38,
				Font_ID.jlm_msbm10);
		add("vartriangle", TeXConstants.TYPE_RELATION, 77, Font_ID.jlm_msam10);
		add("ngeqq", TeXConstants.TYPE_RELATION, 184, Font_ID.jlm_msbm10);
		add("not", TeXConstants.TYPE_RELATION, 54, Font_ID.jlm_cmsy10);
		add("varXi", TeXConstants.TYPE_ORDINARY, 165, Font_ID.jlm_cmmi10);
		add("nleq", TeXConstants.TYPE_RELATION, 163, Font_ID.jlm_msbm10);
		add("rhook", TeXConstants.TYPE_ORDINARY, 45,
				Font_ID.jlm_cmmi10_unchanged);
		add("frown", TeXConstants.TYPE_RELATION, 95,
				Font_ID.jlm_cmmi10_unchanged);
		add("lll", TeXConstants.TYPE_RELATION, 110, Font_ID.jlm_msam10);
		add("varobar", TeXConstants.TYPE_BINARY_OPERATOR, 49,
				Font_ID.jlm_stmary10);
		add("varsubsetneq", TeXConstants.TYPE_RELATION, 195,
				Font_ID.jlm_msbm10);
		add("racc", TeXConstants.TYPE_ORDINARY, 103, Font_ID.jlm_cmsy10);
		add("varowedge", TeXConstants.TYPE_BINARY_OPERATOR, 87,
				Font_ID.jlm_stmary10);
		add("Xi", TeXConstants.TYPE_ORDINARY, 165, Font_ID.jlm_cmr10);
		add("rrfloor", TeXConstants.TYPE_CLOSING, 117, Font_ID.jlm_stmary10);
		add("textdollar", TeXConstants.TYPE_ORDINARY, 36, Font_ID.jlm_cmr10);
		add("trianglerighteqslant", TeXConstants.TYPE_RELATION, 113,
				Font_ID.jlm_stmary10);
		add("curlywedgeuparrow", TeXConstants.TYPE_RELATION, 70,
				Font_ID.jlm_stmary10);
		add("trianglelefteqslant", TeXConstants.TYPE_RELATION, 112,
				Font_ID.jlm_stmary10);
		add("cyrbreve", TeXConstants.TYPE_ACCENT, 774, Font_ID.jlm_wnr10);
		add("nparallel", TeXConstants.TYPE_RELATION, 44, Font_ID.jlm_msbm10);
		add("lor", TeXConstants.TYPE_BINARY_OPERATOR, 95, Font_ID.jlm_cmsy10);
		add("bigsqcap", TeXConstants.TYPE_BIG_OPERATOR, 132,
				Font_ID.jlm_stmary10);
		add("owedge", TeXConstants.TYPE_BINARY_OPERATOR, 95,
				Font_ID.jlm_stmary10);
		add("circledS", TeXConstants.TYPE_ORDINARY, 115, Font_ID.jlm_msam10);
		add("bracevert", TeXConstants.TYPE_ORDINARY, 62, Font_ID.jlm_cmex10);
		add("\u0391", TeXConstants.TYPE_ORDINARY, 913, Font_ID.jlm_fcmrpg);
		add("\u0392", TeXConstants.TYPE_ORDINARY, 914, Font_ID.jlm_fcmrpg);
		add("\u0393", TeXConstants.TYPE_ORDINARY, 915, Font_ID.jlm_fcmrpg);
		add("\u0394", TeXConstants.TYPE_ORDINARY, 916, Font_ID.jlm_fcmrpg);
		add("\u0395", TeXConstants.TYPE_ORDINARY, 917, Font_ID.jlm_fcmrpg);
		add("\u0396", TeXConstants.TYPE_ORDINARY, 918, Font_ID.jlm_fcmrpg);
		add("\u0397", TeXConstants.TYPE_ORDINARY, 919, Font_ID.jlm_fcmrpg);
		add("\u0398", TeXConstants.TYPE_ORDINARY, 920, Font_ID.jlm_fcmrpg);
		add("\u0399", TeXConstants.TYPE_ORDINARY, 921, Font_ID.jlm_fcmrpg);
		add("\u039A", TeXConstants.TYPE_ORDINARY, 922, Font_ID.jlm_fcmrpg);
		add("\u039B", TeXConstants.TYPE_ORDINARY, 923, Font_ID.jlm_fcmrpg);
		add("\u039C", TeXConstants.TYPE_ORDINARY, 924, Font_ID.jlm_fcmrpg);
		add("\u039D", TeXConstants.TYPE_ORDINARY, 925, Font_ID.jlm_fcmrpg);
		add("\u039E", TeXConstants.TYPE_ORDINARY, 926, Font_ID.jlm_fcmrpg);
		add("\u039F", TeXConstants.TYPE_ORDINARY, 927, Font_ID.jlm_fcmrpg);
		add("\u03A0", TeXConstants.TYPE_ORDINARY, 928, Font_ID.jlm_fcmrpg);
		add("\u03A1", TeXConstants.TYPE_ORDINARY, 929, Font_ID.jlm_fcmrpg);
		add("\u03A3", TeXConstants.TYPE_ORDINARY, 931, Font_ID.jlm_fcmrpg);
		add("\u03A4", TeXConstants.TYPE_ORDINARY, 932, Font_ID.jlm_fcmrpg);
		add("\u03A5", TeXConstants.TYPE_ORDINARY, 933, Font_ID.jlm_fcmrpg);
		add("textampersand", TeXConstants.TYPE_ORDINARY, 38, Font_ID.jlm_cmr10);
		add("\u03A6", TeXConstants.TYPE_ORDINARY, 934, Font_ID.jlm_fcmrpg);
		add("\u03A7", TeXConstants.TYPE_ORDINARY, 935, Font_ID.jlm_fcmrpg);
		add("\u03A8", TeXConstants.TYPE_ORDINARY, 936, Font_ID.jlm_fcmrpg);
		add("\u03A9", TeXConstants.TYPE_ORDINARY, 937, Font_ID.jlm_fcmrpg);
		add("curlyveeuparrow", TeXConstants.TYPE_RELATION, 79,
				Font_ID.jlm_stmary10);
		add("\u03B1", TeXConstants.TYPE_ORDINARY, 945, Font_ID.jlm_fcmrpg);
		add("\u03B2", TeXConstants.TYPE_ORDINARY, 946, Font_ID.jlm_fcmrpg);
		add("\u03B3", TeXConstants.TYPE_ORDINARY, 947, Font_ID.jlm_fcmrpg);
		add("\u03B4", TeXConstants.TYPE_ORDINARY, 948, Font_ID.jlm_fcmrpg);
		add("\u03B5", TeXConstants.TYPE_ORDINARY, 949, Font_ID.jlm_fcmrpg);
		add("\u03B6", TeXConstants.TYPE_ORDINARY, 950, Font_ID.jlm_fcmrpg);
		add("\u03B7", TeXConstants.TYPE_ORDINARY, 951, Font_ID.jlm_fcmrpg);
		add("jlatexmathring", TeXConstants.TYPE_ACCENT, 186, Font_ID.jlm_cmr10);
		add("\u03B8", TeXConstants.TYPE_ORDINARY, 952, Font_ID.jlm_fcmrpg);
		add("\u03B9", TeXConstants.TYPE_ORDINARY, 953, Font_ID.jlm_fcmrpg);
		add("\u03BA", TeXConstants.TYPE_ORDINARY, 954, Font_ID.jlm_fcmrpg);
		add("\u03BB", TeXConstants.TYPE_ORDINARY, 955, Font_ID.jlm_fcmrpg);
		add("\u03BC", TeXConstants.TYPE_ORDINARY, 956, Font_ID.jlm_fcmrpg);
		add("\u03BD", TeXConstants.TYPE_ORDINARY, 957, Font_ID.jlm_fcmrpg);
		add("\u03BE", TeXConstants.TYPE_ORDINARY, 958, Font_ID.jlm_fcmrpg);
		add("\u03BF", TeXConstants.TYPE_ORDINARY, 959, Font_ID.jlm_fcmrpg);
		add("\u03C0", TeXConstants.TYPE_ORDINARY, 960, Font_ID.jlm_fcmrpg);
		add("acute", TeXConstants.TYPE_ACCENT, 182, Font_ID.jlm_cmr10);
		add("\u03C1", TeXConstants.TYPE_ORDINARY, 961, Font_ID.jlm_fcmrpg);
		add("\u03C2", TeXConstants.TYPE_ORDINARY, 962, Font_ID.jlm_fcmrpg);
		add("\u03C3", TeXConstants.TYPE_ORDINARY, 963, Font_ID.jlm_fcmrpg);
		add("\u03C4", TeXConstants.TYPE_ORDINARY, 964, Font_ID.jlm_fcmrpg);
		add("\u03C5", TeXConstants.TYPE_ORDINARY, 965, Font_ID.jlm_fcmrpg);
		add("\u03C6", TeXConstants.TYPE_ORDINARY, 966, Font_ID.jlm_fcmrpg);
		add("bindnasrepma", TeXConstants.TYPE_CLOSING, 111,
				Font_ID.jlm_stmary10);
		add("\u03C7", TeXConstants.TYPE_ORDINARY, 967, Font_ID.jlm_fcmrpg);
		add("\u03C8", TeXConstants.TYPE_ORDINARY, 968, Font_ID.jlm_fcmrpg);
		add("\u03C9", TeXConstants.TYPE_ORDINARY, 969, Font_ID.jlm_fcmrpg);
		add("\u03D1", TeXConstants.TYPE_ORDINARY, 977, Font_ID.jlm_fcmrpg);
		add("ogonek", TeXConstants.TYPE_ACCENT, 197, Font_ID.jlm_cmr10);
		add("varTheta", TeXConstants.TYPE_ORDINARY, 163, Font_ID.jlm_cmmi10);
		add("intop", TeXConstants.TYPE_BIG_OPERATOR, 82, Font_ID.jlm_cmex10);
		add("gets", TeXConstants.TYPE_RELATION, 195, Font_ID.jlm_cmsy10);
		add("binampersand", TeXConstants.TYPE_OPENING, 110,
				Font_ID.jlm_stmary10);
		add("Mapstochar", TeXConstants.TYPE_RELATION, 122,
				Font_ID.jlm_stmary10);
		add("bigparallel", TeXConstants.TYPE_BIG_OPERATOR, 134,
				Font_ID.jlm_stmary10);
		add("bar", TeXConstants.TYPE_ACCENT, 185, Font_ID.jlm_cmr10);
		add("prime", TeXConstants.TYPE_ORDINARY, 48, Font_ID.jlm_cmsy10);
		add("centerdot", TeXConstants.TYPE_BINARY_OPERATOR, 166,
				Font_ID.jlm_msam10);
		add("lbag", TeXConstants.TYPE_BINARY_OPERATOR, 74,
				Font_ID.jlm_stmary10);
		add("npreceq", TeXConstants.TYPE_RELATION, 177, Font_ID.jlm_msbm10);
		add("textfractionsolidus", TeXConstants.TYPE_ORDINARY, 47,
				Font_ID.jlm_cmr10);
		add("varLambda", TeXConstants.TYPE_ORDINARY, 164, Font_ID.jlm_cmmi10);
		add("backslash", TeXConstants.TYPE_ORDINARY, 110, Font_ID.jlm_cmsy10);
		add("varsupsetneqq", TeXConstants.TYPE_RELATION, 39,
				Font_ID.jlm_msbm10);
		add("widetilde", TeXConstants.TYPE_ACCENT, 101, Font_ID.jlm_cmex10);
		add("underscore", TeXConstants.TYPE_ORDINARY, 101,
				Font_ID.jlm_cmmi10_unchanged);
		add("jmath", TeXConstants.TYPE_ORDINARY, 124,
				Font_ID.jlm_cmmi10_unchanged);
		add("varotimes", TeXConstants.TYPE_BINARY_OPERATOR, 47,
				Font_ID.jlm_stmary10);
		add("vartriangleright", TeXConstants.TYPE_RELATION, 66,
				Font_ID.jlm_msam10);
		add("nleqq", TeXConstants.TYPE_RELATION, 183, Font_ID.jlm_msbm10);
		add("Bbbk", TeXConstants.TYPE_ORDINARY, 124, Font_ID.jlm_msbm10);
		add("fg", TeXConstants.TYPE_PUNCTUATION, 36, Font_ID.jlm_jlmi10);
		add("curlywedgedownarrow", TeXConstants.TYPE_RELATION, 71,
				Font_ID.jlm_stmary10);
		add("mathring", TeXConstants.TYPE_ACCENT, 186, Font_ID.jlm_cmr10);
		add("Diamond", TeXConstants.TYPE_RELATION, 167, Font_ID.jlm_msam10);
		add("ovee", TeXConstants.TYPE_BINARY_OPERATOR, 94,
				Font_ID.jlm_stmary10);
		add("circlearrowleft", TeXConstants.TYPE_RELATION, 170,
				Font_ID.jlm_msam10);
		add("varpropto", TeXConstants.TYPE_RELATION, 95, Font_ID.jlm_msam10);
		add("shortdownarrow", TeXConstants.TYPE_RELATION, 35,
				Font_ID.jlm_stmary10);
		add("oint", TeXConstants.TYPE_BIG_OPERATOR, 72, Font_ID.jlm_cmex10);
		add("normaldot", TeXConstants.TYPE_ORDINARY, 46, Font_ID.jlm_cmss10,
				'.', "textnormaldot");
		add("lesseqgtr", TeXConstants.TYPE_RELATION, 81, Font_ID.jlm_msam10);
		add("rgroup", TeXConstants.TYPE_CLOSING, 59, Font_ID.jlm_cmex10);
		add("varPi", TeXConstants.TYPE_ORDINARY, 166, Font_ID.jlm_cmmi10);
		add("varoast", TeXConstants.TYPE_BINARY_OPERATOR, 48,
				Font_ID.jlm_stmary10);
		add("dot", TeXConstants.TYPE_ACCENT, 95, Font_ID.jlm_cmr10);
		add("leftrightarrowtriangle", TeXConstants.TYPE_BINARY_OPERATOR, 125,
				Font_ID.jlm_stmary10);
		add("Relbar", TeXConstants.TYPE_RELATION, 61, Font_ID.jlm_cmr10);
		add("varolessthan", TeXConstants.TYPE_BINARY_OPERATOR, 84,
				Font_ID.jlm_stmary10);
		add("fatslash", TeXConstants.TYPE_BINARY_OPERATOR, 72,
				Font_ID.jlm_stmary10);
		add("Ydown", TeXConstants.TYPE_BINARY_OPERATOR, 37,
				Font_ID.jlm_stmary10);
		add("nsubseteqq", TeXConstants.TYPE_RELATION, 34, Font_ID.jlm_msbm10);
		add("backprime", TeXConstants.TYPE_ORDINARY, 56, Font_ID.jlm_msam10);
		add("lq", TeXConstants.TYPE_ORDINARY, 96, Font_ID.jlm_cmti10);
		add("smile", TeXConstants.TYPE_RELATION, 94,
				Font_ID.jlm_cmmi10_unchanged);
		add("arrownot", TeXConstants.TYPE_RELATION, 120, Font_ID.jlm_stmary10);
		add("Box", TeXConstants.TYPE_ORDINARY, 164, Font_ID.jlm_msam10);
		add("supsetpluseq", TeXConstants.TYPE_RELATION, 103,
				Font_ID.jlm_stmary10);
		add("Arrownot", TeXConstants.TYPE_RELATION, 121, Font_ID.jlm_stmary10);
		add("bot", TeXConstants.TYPE_ORDINARY, 63, Font_ID.jlm_cmsy10);
		add("tilde", TeXConstants.TYPE_ACCENT, 126, Font_ID.jlm_cmr10);
		add("og", TeXConstants.TYPE_PUNCTUATION, 33, Font_ID.jlm_jlmi10);
		add("llparenthesis", TeXConstants.TYPE_OPENING, 108,
				Font_ID.jlm_stmary10);
		add("nnwarrow", TeXConstants.TYPE_RELATION, 80, Font_ID.jlm_stmary10);
		add("bigbox", TeXConstants.TYPE_BIG_OPERATOR, 133,
				Font_ID.jlm_stmary10);
		add("breve", TeXConstants.TYPE_ACCENT, 184, Font_ID.jlm_cmr10);
		add("varbigcirc", TeXConstants.TYPE_BINARY_OPERATOR, 76,
				Font_ID.jlm_stmary10);
		add("dbend", TeXConstants.TYPE_ORDINARY, 127,
				Font_ID.jlm_jlmr10_unchanged);
		add("rq", TeXConstants.TYPE_ORDINARY, 39, Font_ID.jlm_cmti10);
		add("varnothing", TeXConstants.TYPE_ORDINARY, 63, Font_ID.jlm_msbm10);
		add("mid", TeXConstants.TYPE_RELATION, 106, Font_ID.jlm_cmsy10);
		add("varovee", TeXConstants.TYPE_BINARY_OPERATOR, 86,
				Font_ID.jlm_stmary10);
		add("vartimes", TeXConstants.TYPE_BINARY_OPERATOR, 66,
				Font_ID.jlm_stmary10);
		add("ssearrow", TeXConstants.TYPE_RELATION, 69, Font_ID.jlm_stmary10);
		add("rbag", TeXConstants.TYPE_BINARY_OPERATOR, 75,
				Font_ID.jlm_stmary10);
		add("varUpsilon", TeXConstants.TYPE_ORDINARY, 168, Font_ID.jlm_cmmi10);
		add("rrparenthesis", TeXConstants.TYPE_CLOSING, 109,
				Font_ID.jlm_stmary10);
		add("to", TeXConstants.TYPE_RELATION, 33, Font_ID.jlm_cmsy10);
		add("jlatexmathsharp", TeXConstants.TYPE_ORDINARY, 35,
				Font_ID.jlm_cmr10);
		add("cdotp", TeXConstants.TYPE_PUNCTUATION, 162, Font_ID.jlm_cmsy10);
		add("baro", TeXConstants.TYPE_BINARY_OPERATOR, 43,
				Font_ID.jlm_stmary10);
		add("bigcurlyvee", TeXConstants.TYPE_BIG_OPERATOR, 130,
				Font_ID.jlm_stmary10);
		add("bignplus", TeXConstants.TYPE_BIG_OPERATOR, 136,
				Font_ID.jlm_stmary10);
		add("varominus", TeXConstants.TYPE_BINARY_OPERATOR, 55,
				Font_ID.jlm_stmary10);
		add("Sigma", TeXConstants.TYPE_ORDINARY, 167, Font_ID.jlm_cmr10);
		add("mapstochar", TeXConstants.TYPE_RELATION, 55, Font_ID.jlm_cmsy10);
		add("varogreaterthan", TeXConstants.TYPE_BINARY_OPERATOR, 85,
				Font_ID.jlm_stmary10);
		add("cyrddot", TeXConstants.TYPE_ACCENT, 776, Font_ID.jlm_wnr10);
		add("ntrianglelefteqslant", TeXConstants.TYPE_RELATION, 114,
				Font_ID.jlm_stmary10);
		add("Yleft", TeXConstants.TYPE_BINARY_OPERATOR, 38,
				Font_ID.jlm_stmary10);
		add("leftrightarroweq", TeXConstants.TYPE_RELATION, 77,
				Font_ID.jlm_stmary10);
		add("imath", TeXConstants.TYPE_ORDINARY, 123,
				Font_ID.jlm_cmmi10_unchanged);
		add("moo", TeXConstants.TYPE_BINARY_OPERATOR, 46, Font_ID.jlm_stmary10);
		add("nnearrow", TeXConstants.TYPE_RELATION, 81, Font_ID.jlm_stmary10);
		add("fatbslash", TeXConstants.TYPE_BINARY_OPERATOR, 73,
				Font_ID.jlm_stmary10);
		add("Yright", TeXConstants.TYPE_BINARY_OPERATOR, 39,
				Font_ID.jlm_stmary10);
		add("shortparallel", TeXConstants.TYPE_RELATION, 113,
				Font_ID.jlm_msbm10);
		add("geq", TeXConstants.TYPE_RELATION, 184, Font_ID.jlm_cmsy10);
		add("varGamma", TeXConstants.TYPE_ORDINARY, 161, Font_ID.jlm_cmmi10);
		add("varSigma", TeXConstants.TYPE_ORDINARY, 167, Font_ID.jlm_cmmi10);
		add("textnormaldot", TeXConstants.TYPE_PUNCTUATION, 46,
				Font_ID.jlm_cmss10);
		add("unlhd", TeXConstants.TYPE_RELATION, 69, Font_ID.jlm_msam10);
		add("Gamma", TeXConstants.TYPE_ORDINARY, 161, Font_ID.jlm_cmr10);
		add("jlatexmathcedilla", TeXConstants.TYPE_ORDINARY, 187,
				Font_ID.jlm_cmti10);
		add("vec", TeXConstants.TYPE_ACCENT, 126, Font_ID.jlm_cmmi10_unchanged);
		add("mapsfromchar", TeXConstants.TYPE_RELATION, 123,
				Font_ID.jlm_stmary10);
		add("shortleftarrow", TeXConstants.TYPE_RELATION, 32,
				Font_ID.jlm_stmary10);
		add("varDelta", TeXConstants.TYPE_ORDINARY, 162, Font_ID.jlm_cmmi10);
		add("rrceil", TeXConstants.TYPE_CLOSING, 119, Font_ID.jlm_stmary10);
		add("lgroup", TeXConstants.TYPE_OPENING, 58, Font_ID.jlm_cmex10);
		add("supsetplus", TeXConstants.TYPE_RELATION, 101,
				Font_ID.jlm_stmary10);
		add("grave", TeXConstants.TYPE_ACCENT, 181, Font_ID.jlm_cmr10);
	}
}
