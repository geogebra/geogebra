/* CharMapping.java
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

public final class CharMapping {

	public static final char APOSTROPHE = '\'';
	public static final char GRAVE = '`';
	public static final char DEGREE = '\u00B0';
	public static final char SUPZERO = '\u2070';
	public static final char SUPONE = '\u00B9';
	public static final char SUPTWO = '\u00B2';
	public static final char SUPTHREE = '\u00B3';
	public static final char SUPFOUR = '\u2074';
	public static final char SUPFIVE = '\u2075';
	public static final char SUPSIX = '\u2076';
	public static final char SUPSEVEN = '\u2077';
	public static final char SUPEIGHT = '\u2078';
	public static final char SUPNINE = '\u2079';
	public static final char SUPPLUS = '\u207A';
	public static final char SUPMINUS = '\u207B';
	public static final char SUPEQUAL = '\u207C';
	public static final char SUPLPAR = '\u207D';
	public static final char SUPRPAR = '\u207E';
	public static final char SUPN = '\u207F';
	public static final char SUBZERO = '\u2080';
	public static final char SUBONE = '\u2081';
	public static final char SUBTWO = '\u2082';
	public static final char SUBTHREE = '\u2083';
	public static final char SUBFOUR = '\u2084';
	public static final char SUBFIVE = '\u2085';
	public static final char SUBSIX = '\u2086';
	public static final char SUBSEVEN = '\u2087';
	public static final char SUBEIGHT = '\u2088';
	public static final char SUBNINE = '\u2089';
	public static final char SUBPLUS = '\u208A';
	public static final char SUBMINUS = '\u208B';
	public static final char SUBEQUAL = '\u208C';
	public static final char SUBLPAR = '\u208D';
	public static final char SUBRPAR = '\u208E';

	public interface Mapping {

		void map(TeXParser tp, boolean mathMode);
	}

	public static final class SymbolMapping implements Mapping {

		final char c;
		final SymbolAtom sym;
		final String text;
		SymbolAtom textSym;

		public SymbolMapping(final char c, final SymbolAtom sym,
				final String text) {
			this.c = c;
			this.sym = sym;
			if (sym != null) {
				this.sym.setUnicode(c);
			}

			this.text = text;
		}

		public SymbolMapping(final char c, final SymbolAtom sym) {
			this(c, sym, null);
		}

		public SymbolMapping(final char c, final String sym,
				final String text) {
			this(c, SymbolAtom.get(sym), text);
		}

		public SymbolMapping(final char c, final String sym) {
			this(c, SymbolAtom.get(sym), null);
		}

		@Override
		public void map(TeXParser tp, boolean mathMode) {
			tp.addToConsumer(get(mathMode));
		}

		public SymbolAtom get(final boolean mathMode) {
			if (!mathMode) {
				if (textSym != null) {
					return textSym;
				} else if (text == null) {
					textSym = sym.toTextMode();
					return textSym;
				}
				// we've an unexisting symbol so try to load it via an alphabet
				// UnicodeMapping.get(c);
				textSym = SymbolAtom.get(text, false);
				return textSym;
			}
			return sym;
		}

		public SymbolAtom get(TeXParser tp) {
			return get(tp.isMathMode());
		}
	}

	public static final class FormulaMapping implements Mapping {

		final String sym;
		final String text;
		SymbolAtom textSym;

		public FormulaMapping(final String sym, final String text) {
			this.sym = sym;
			this.text = text;
		}

		public FormulaMapping(final String sym) {
			this(sym, null);
		}

		@Override
		public void map(TeXParser tp, boolean mathMode) {
			if (!mathMode) {
				if (text == null) {
					tp.addString(sym);
				} else {
					if (textSym == null) {
						// We expected to have a symbol and we don't have one...
						// UnicodeMapping.get(text.charAt(0));
						textSym = SymbolAtom.get(text, false);
					}
					tp.addToConsumer(textSym);
				}
			} else {
				tp.addString(sym);
			}
		}
	}

	public static final class SubSupMapping implements Mapping {

		final char c;
		final boolean sup;

		public SubSupMapping(final char c, final boolean sup) {
			this.c = c;
			this.sup = sup;
		}

		@Override
		public void map(final TeXParser tp, final boolean mathMode) {
			if (sup) {
				tp.processSubSup('^');
			} else {
				tp.processSubSup('_');
			}
			if (defaultMappings.hasMapping(c)) {
				tp.addToConsumer(defaultMappings.get(c, tp));
			} else {
				tp.addToConsumer(new CharAtom(c));
			}
		}
	}

	private final static CharMapping defaultMappings = new CharMapping();

	private final Mapping[] mapToSym;

	private CharMapping() {
		mapToSym = new Mapping[65536];
		initMappings();
	}

	public CharMapping(Mapping[] mapToSym) {
		this.mapToSym = mapToSym;
	}

	public CharMapping(CharMapping cm) {
		mapToSym = new Mapping[65536];
		System.arraycopy(cm.mapToSym, 0, mapToSym, 0, cm.mapToSym.length);
	}

	public static CharMapping getDefault() {
		return defaultMappings;
	}

	public void put(final char c, final Mapping m) {
		mapToSym[c] = m;
	}

	public void putSym(final char c, final String s) {
		mapToSym[c] = new SymbolMapping(c, s);
	}

	public void putForm(final char c, final String s) {
		mapToSym[c] = new FormulaMapping(s);
	}

	public void putForm(final char c, final String s, String text) {
		mapToSym[c] = new FormulaMapping(s, text);
	}

	public boolean replace(final char c, final TeXParser tp) {
		return replace(c, tp, tp.isMathMode());
	}

	public boolean replace(final char c, final TeXParser tp,
			final boolean mathMode) {
		final Mapping m = mapToSym[c];
		if (m != null) {
			m.map(tp, mathMode);
			return true;
		}

		return false;
	}

	public boolean hasMapping(final char c) {
		return mapToSym[c] != null;
	}

	public Atom getAtom(final char c, final boolean mathMode) {
		final TeXParser tp = new TeXParser(true);
		final SingleAtomConsumer cons = new SingleAtomConsumer();
		tp.addConsumer(cons);
		if (replace(c, tp, mathMode)) {
			tp.parse();
			return cons.get();
		}
		return null;
	}

	public void replaceUnsafe(final char c, final TeXParser tp) {
		mapToSym[c].map(tp, tp.isMathMode());
	}

	public SymbolAtom get(final char c, final TeXParser tp) {
		return ((SymbolMapping) mapToSym[c]).get(tp);
	}

	// Some mapping char2com don't exists
	// for example U02269-0FE00 => gvertneqq
	// https://www.w3.org/TR/MathML2/bycodes.html

	private void initMappings() {
		put('\u00B0', new Mapping() {
			@Override
			public void map(final TeXParser tp, final boolean mathMode) {
				tp.cumSupSymbols(Symbols.CIRC);
			}
		});
		put(SUPZERO, new SubSupMapping('0', true));
		put(SUPONE, new SubSupMapping('1', true));
		put(SUPTWO, new SubSupMapping('2', true));
		put(SUPTHREE, new SubSupMapping('3', true));
		put(SUPFOUR, new SubSupMapping('4', true));
		put(SUPFIVE, new SubSupMapping('5', true));
		put(SUPSIX, new SubSupMapping('6', true));
		put(SUPSEVEN, new SubSupMapping('7', true));
		put(SUPEIGHT, new SubSupMapping('8', true));
		put(SUPNINE, new SubSupMapping('9', true));
		put(SUPPLUS, new SubSupMapping('+', true));
		put(SUPMINUS, new SubSupMapping('-', true));
		put(SUPEQUAL, new SubSupMapping('=', true));
		put(SUPLPAR, new SubSupMapping('(', true));
		put(SUPRPAR, new SubSupMapping(')', true));
		put(SUPN, new SubSupMapping('n', true));
		put(SUBZERO, new SubSupMapping('0', false));
		put(SUBONE, new SubSupMapping('1', false));
		put(SUBTWO, new SubSupMapping('2', false));
		put(SUBTHREE, new SubSupMapping('3', false));
		put(SUBFOUR, new SubSupMapping('4', false));
		put(SUBFIVE, new SubSupMapping('5', false));
		put(SUBSIX, new SubSupMapping('6', false));
		put(SUBSEVEN, new SubSupMapping('7', false));
		put(SUBEIGHT, new SubSupMapping('8', false));
		put(SUBNINE, new SubSupMapping('9', false));
		put(SUBPLUS, new SubSupMapping('+', false));
		put(SUBMINUS, new SubSupMapping('-', false));
		put(SUBEQUAL, new SubSupMapping('=', false));
		put(SUBLPAR, new SubSupMapping('(', false));
		put(SUBRPAR, new SubSupMapping(')', false));

		putForm('\u00A0', "\\ ");
		putForm('\u00A1', "{!`}");
		putForm('\u00A9', "\\copyright");
		putForm('\u00BC', "\\text{\\sfrac14}");
		putForm('\u00BD', "\\text{\\sfrac12}");
		putForm('\u00BE', "\\text{\\sfrac34}");
		putForm('\u00C0', "\\`A");
		putForm('\u00C1', "\\'A");
		putForm('\u00C2', "\\^A");
		putForm('\u00C3', "\\~A");
		putForm('\u00C4', "\\\"A");
		putForm('\u00C5', "\\r{A}");
		putForm('\u00C7', "\\c{C}");
		putForm('\u00C8', "\\`E");
		putForm('\u00C9', "\\'E");
		putForm('\u00CA', "\\^E");
		putForm('\u00CB', "\\\"E");
		putForm('\u00CC', "\\`I");
		putForm('\u00CD', "\\'I");
		putForm('\u00CE', "\\^I");
		putForm('\u00CF', "\\\"I");
		putForm('\u00D1', "\\~N");
		putForm('\u00D2', "\\`O");
		putForm('\u00D3', "\\'O");
		putForm('\u00D4', "\\^O");
		putForm('\u00D5', "\\~O");
		putForm('\u00D6', "\\\"O");
		putForm('\u00D9', "\\`U");
		putForm('\u00DA', "\\'U");
		putForm('\u00DB', "\\^U");
		putForm('\u00DC', "\\\"U");
		putForm('\u00DD', "\\'Y");
		putForm('\u00E0', "\\`a");
		putForm('\u00E1', "\\'a");
		putForm('\u00E2', "\\^a");
		putForm('\u00E3', "\\~a");
		putForm('\u00E4', "\\\"a");
		putForm('\u00E5', "\\aa");
		putForm('\u00E7', "\\c{c}");
		putForm('\u00E8', "\\`e");
		putForm('\u00E9', "\\'e");
		putForm('\u00EA', "\\^e");
		putForm('\u00EB', "\\\"e");
		putForm('\u00EC', "\\`\\i");
		putForm('\u00ED', "\\'\\i");
		putForm('\u00EE', "\\^\\i");
		putForm('\u00EF', "\\\"\\i");
		putForm('\u00F1', "\\~n");
		putForm('\u00F2', "\\`o");
		putForm('\u00F3', "\\'o");
		putForm('\u00F4', "\\^o");
		putForm('\u00F5', "\\~o");
		putForm('\u00F6', "\\\"o");
		putForm('\u00F9', "\\`u");
		putForm('\u00FA', "\\'u");
		putForm('\u00FB', "\\^u");
		putForm('\u00FC', "\\\"u");
		putForm('\u00FD', "\\'y");
		putForm('\u00FF', "\\\"y");
		putForm('\u0100', "\\=A");
		putForm('\u0101', "\\=a");
		putForm('\u0102', "\\u{A}");
		putForm('\u0103', "\\u{a}");
		putForm('\u0104', "\\k{A}");
		putForm('\u0105', "\\k{a}");
		putForm('\u0106', "\\'C");
		putForm('\u0107', "\\'c");
		putForm('\u0108', "\\^C");
		putForm('\u0109', "\\^c");
		putForm('\u010A', "\\.C");
		putForm('\u010B', "\\.c");
		putForm('\u010C', "\\v{C}");
		putForm('\u010D', "\\v{c}");
		putForm('\u010E', "\\v{D}");
		putForm('\u010F', "{d\\text{'}}");
		putForm('\u0110', "\\Dstrok");
		putForm('\u0111', "\\dstrok");
		putForm('\u0112', "\\=E");
		putForm('\u0113', "\\=e");
		putForm('\u0114', "\\u{E}");
		putForm('\u0115', "\\u{e}");
		putForm('\u0116', "\\.E");
		putForm('\u0117', "\\.e");
		putForm('\u0118', "\\k{E}");
		putForm('\u0119', "\\k{e}");
		putForm('\u011A', "\\v{E}");
		putForm('\u011B', "\\v{e}");
		putForm('\u011C', "\\^G");
		putForm('\u011D', "\\^g");
		putForm('\u011E', "\\u{G}");
		putForm('\u011F', "\\u{g}");
		putForm('\u0120', "\\.G");
		putForm('\u0121', "\\.g");
		putForm('\u0122', "\\underaccent{,}G");
		putForm('\u0123', "\\'g");
		putForm('\u0124', "\\^H");
		putForm('\u0125', "\\^h");
		putForm('\u0126', "\\Hstrok");
		putForm('\u0127', "\\hstrok");
		putForm('\u0128', "\\~I");
		putForm('\u0129', "\\~\\i");
		putForm('\u012A', "\\=I");
		putForm('\u012B', "\\=\\i");
		putForm('\u012C', "\\u{I}");
		putForm('\u012D', "\\u{\\i}");
		putForm('\u012E', "\\k{I}");
		putForm('\u012F', "\\k{i}");
		putForm('\u0130', "\\.I");
		putForm('\u0132', "\\IJ");
		putForm('\u0133', "\\ij");
		putForm('\u0134', "\\^J");
		putForm('\u0135', "\\^\\j");
		putForm('\u0136', "\\underaccent{,}K");
		putForm('\u0137', "\\underaccent{,}k");
		putForm('\u0139', "\\'L");
		putForm('\u013A', "\\'l");
		putForm('\u013B', "\\underaccent{,}L");
		putForm('\u013C', "\\underaccent{,}l");
		putForm('\u013D', "\\Lcaron");
		putForm('\u013E', "\\lcaron");
		putForm('\u013F', "L\\cdot");
		putForm('\u0140', "l\\cdot");
		putForm('\u0141', "\\L");
		putForm('\u0142', "\\l");
		putForm('\u0143', "\\'N");
		putForm('\u0144', "\\'n");
		putForm('\u0145', "\\underaccent{,}N");
		putForm('\u0146', "\\underaccent{,}n");
		putForm('\u0147', "\\v{N}");
		putForm('\u0148', "\\v{n}");
		putForm('\u0149', "{\\text{'}n}");
		putForm('\u014C', "\\=O");
		putForm('\u014D', "\\=o");
		putForm('\u014E', "\\u{O}");
		putForm('\u014F', "\\u{o}");
		putForm('\u0150', "\\H{O}");
		putForm('\u0151', "\\H{o}");
		putForm('\u0154', "\\'R");
		putForm('\u0155', "\\'r");
		putForm('\u0156', "\\underaccent{,}R");
		putForm('\u0157', "\\underaccent{,}r");
		putForm('\u0158', "\\v{R}");
		putForm('\u0159', "\\v{r}");
		putForm('\u015A', "\\'S");
		putForm('\u015B', "\\'s");
		putForm('\u015C', "\\^S");
		putForm('\u015D', "\\^s");
		putForm('\u015E', "\\c{S}");
		putForm('\u015F', "\\c{s}");
		putForm('\u0160', "\\v{S}");
		putForm('\u0161', "\\v{s}");
		putForm('\u0162', "\\c{T}");
		putForm('\u0163', "\\c{t}");
		putForm('\u0164', "\\v{T}");
		putForm('\u0165', "\\tcaron");
		putForm('\u0166', "\\TStroke");
		putForm('\u0167', "\\tStroke");
		putForm('\u0168', "\\~U");
		putForm('\u0169', "\\~u");
		putForm('\u016A', "\\=U");
		putForm('\u016B', "\\=u");
		putForm('\u016C', "\\u{U}");
		putForm('\u016D', "\\u{u}");
		putForm('\u016E', "\\r{U}");
		putForm('\u016F', "\\r{u}");
		putForm('\u0170', "\\H{U}");
		putForm('\u0171', "\\H{u}");
		putForm('\u0172', "\\k{U}");
		putForm('\u0173', "\\k{u}");
		putForm('\u0174', "\\^W");
		putForm('\u0175', "\\^w");
		putForm('\u0176', "\\^Y");
		putForm('\u0177', "\\^y");
		putForm('\u0178', "\\\"Y");
		putForm('\u0179', "\\'Z");
		putForm('\u017A', "\\'z");
		putForm('\u017B', "\\.Z");
		putForm('\u017C', "\\.z");
		putForm('\u017D', "\\v{Z}");
		putForm('\u017E', "\\v{z}");
		putForm('\u02DA', "\\jlatexmathring");
		putForm('\u0300', "\\grave");
		putForm('\u0301', "\\acute");
		putForm('\u0302', "\\hat");
		putForm('\u0303', "\\tilde");
		putForm('\u0304', "\\bar");
		putForm('\u0306', "\\breve");
		putForm('\u0307', "\\dot");
		putForm('\u0308', "\\ddot");
		putForm('\u030B', "\\doubleacute");
		putForm('\u030C', "\\check");
		putForm('\u0386', "\\grkaccent{\u0374}{\\phantom{\u03B9}}\\!\\!A");
		putForm('\u0388', "\\grkaccent{\u0374}{\\phantom{\u03B9}}\u0395");
		putForm('\u0389', "\\grkaccent{\u0374}{\\phantom{\u03B9}}H");
		putForm('\u038A', "\\grkaccent{\u0374}{\\phantom{\u03B9}}\u0399");
		putForm('\u038C', "\\grkaccent{\u0374}{\\phantom{\u03B9}}\\!\u039F");
		putForm('\u038E', "\\grkaccent{\u0374}{\\phantom{\u03B9}}\u03A5");
		putForm('\u038F', "\\grkaccent{\u0374}{\\phantom{\u03B9}}\\!\u03A9");
		putForm('\u0391', "\\Alpha", "\u0391");
		putForm('\u0392', "\\Beta", "\u0392");
		putForm('\u0393', "\\Gamma", "\u0393");
		putForm('\u0394', "\\Delta", "\u0394");
		putForm('\u0395', "\\Epsilon", "\u0395");
		putForm('\u0396', "\\Zeta", "\u0396");
		putForm('\u0397', "\\Eta", "\u0397");
		putForm('\u0398', "\\Theta", "\u0398");
		putForm('\u0399', "\\Iota", "\u0399");
		putForm('\u039A', "\\Kappa", "\u039A");
		putForm('\u039B', "\\Lambda", "\u039B");
		putForm('\u039C', "\\Mu", "\u039C");
		putForm('\u039D', "\\Nu", "\u039D");
		putForm('\u039E', "\\Xi", "\u039E");
		putForm('\u039F', "\\Omicron", "\u039F");
		putForm('\u03A0', "\\Pi", "\u03A0");
		putForm('\u03A1', "\\Rho", "\u03A1");
		putForm('\u03A3', "\\Sigma", "\u03A3");
		putForm('\u03A4', "\\Tau", "\u03A4");
		putForm('\u03A5', "\\Upsilon", "\u03A5");
		putForm('\u03A6', "\\Phi", "\u03A6");
		putForm('\u03A7', "\\Chi", "\u03A7");
		putForm('\u03A8', "\\Psi", "\u03A8");
		putForm('\u03A9', "\\Omega", "\u03A9");
		putForm('\u0400', "\\`\\CYRE");
		putForm('\u0403', "\\'\\CYRK");
		putForm('\u0407', "\\cyrddot\\CYRII");
		putForm('\u040D', "\\`\\CYRI");
		putForm('\u040E', "\\cyrbreve\\CYRU");
		putForm('\u0450', "\\`\\cyre");
		putForm('\u0453', "\\'\\cyrg");
		putForm('\u0457', "\\cyrddot\\dotlessi");
		putForm('\u045C', "\\'\\cyrk");
		putForm('\u045D', "\\`\\cyri");
		putForm('\u045E', "\\cyrbreve\\cyru");
		putForm('\u1F08', "\u2019\u0391");
		putForm('\u1F09', "\u1FFE\u0391");
		putForm('\u1F0A', "\u1FCD\u0391");
		putForm('\u1F0B', "\u1FDD\u0391");
		putForm('\u1F0C', "\u1FCE\u0391");
		putForm('\u1F0D', "\u1FDE\u0391");
		putForm('\u1F0E', "\u1FCF\u0391");
		putForm('\u1F0F', "\u1FDF\u0391");
		putForm('\u1F18', "\u2019\u0395");
		putForm('\u1F19', "\u1FFE\u0395");
		putForm('\u1F1A', "\u1FCD\u0395");
		putForm('\u1F1B', "\u1FDD\u0395");
		putForm('\u1F1C', "\u1FCE\u0395");
		putForm('\u1F1D', "\u1FDE\u0395");
		putForm('\u1F28', "\u2019\u0397");
		putForm('\u1F29', "\u1FFE\u0397");
		putForm('\u1F2A', "\u1FCD\u0397");
		putForm('\u1F2B', "\u1FDD\u0397");
		putForm('\u1F2C', "\u1FCE\u0397");
		putForm('\u1F2D', "\u1FDE\u0397");
		putForm('\u1F2E', "\u1FCF\u0397");
		putForm('\u1F2F', "\u1FDF\u0397");
		putForm('\u1F38', "\u2019\u0399");
		putForm('\u1F39', "\u1FFE\u0399");
		putForm('\u1F3A', "\u1FCD\u0399");
		putForm('\u1F3B', "\u1FDD\u0399");
		putForm('\u1F3C', "\u1FCE\u0399");
		putForm('\u1F3D', "\u1FDE\u0399");
		putForm('\u1F3E', "\u1FCF\u0399");
		putForm('\u1F3F', "\u1FDF\u0399");
		putForm('\u1F48', "\u2019\u039F");
		putForm('\u1F49', "\u1FFE\u039F");
		putForm('\u1F4A', "\u1FCD\u039F");
		putForm('\u1F4B', "\u1FDD\u039F");
		putForm('\u1F4C', "\u1FCE\u039F");
		putForm('\u1F4D', "\u1FDE\u039F");
		putForm('\u1F59', "\u1FFE\u03A5");
		putForm('\u1F5A', "\u1FDD\u03A5");
		putForm('\u1F5B', "\u1FDE\u03A5");
		putForm('\u1F5C', "\u1FDF\u03A5");
		putForm('\u1F68', "\u2019\u03A9");
		putForm('\u1F69', "\u1FFE\u03A9");
		putForm('\u1F6A', "\u1FCD\u03A9");
		putForm('\u1F6B', "\u1FDD\u03A9");
		putForm('\u1F6C', "\u1FCE\u03A9");
		putForm('\u1F6D', "\u1FDE\u03A9");
		putForm('\u1F6E', "\u1FCF\u03A9");
		putForm('\u1F6F', "\u1FDF\u03A9");
		putForm('\u1F71', "\\grkaccent{\u0384}\u03B1");
		putForm('\u1F73', "\\grkaccent{\u0384}\u03B5");
		putForm('\u1F75', "\\grkaccent{\u0384}\u03B7");
		putForm('\u1F77', "\\grkaccent{\u0384}\u03B9");
		putForm('\u1F79', "\\grkaccent{\u0384}\u03BF");
		putForm('\u1F7B', "\\grkaccent{\u0384}\u03C5");
		putForm('\u1F7D', "\\grkaccent{\u0384}\u03C9");
		putForm('\u1F88', "\u2019\u1FBC");
		putForm('\u1F89', "\u1FFE\u1FBC");
		putForm('\u1F8A', "\u1FCD\u1FBC");
		putForm('\u1F8B', "\u1FDD\u1FBC");
		putForm('\u1F8C', "\u1FCE\u1FBC");
		putForm('\u1F8D', "\u1FDE\u1FBC");
		putForm('\u1F8E', "\u1FCF\u1FBC");
		putForm('\u1F8F', "\u1FDF\u1FBC");
		putForm('\u1F98', "\u2019\u1FCC");
		putForm('\u1F99', "\u1FFE\u1FCC");
		putForm('\u1F9A', "\u1FCD\u1FCC");
		putForm('\u1F9B', "\u1FDD\u1FCC");
		putForm('\u1F9C', "\u1FCE\u1FCC");
		putForm('\u1F9D', "\u1FDE\u1FCC");
		putForm('\u1F9E', "\u1FCF\u1FCC");
		putForm('\u1F9F', "\u1FDF\u1FCC");
		putForm('\u1FA8', "\u2019\u1FFC");
		putForm('\u1FA9', "\u1FFE\u1FFC");
		putForm('\u1FAA', "\u1FCD\u1FFC");
		putForm('\u1FAB', "\u1FDD\u1FFC");
		putForm('\u1FAC', "\u1FCE\u1FFC");
		putForm('\u1FAD', "\u1FDE\u1FFC");
		putForm('\u1FAE', "\u1FCF\u1FFC");
		putForm('\u1FAF', "\u1FDF\u1FFC");
		putForm('\u1FB0', "\\u{\u03B1}");
		putForm('\u1FB1', "\\={\u03B1}");
		putForm('\u1FB8', "\\u{\u0391}");
		putForm('\u1FB9', "\\={\u0391}");
		putForm('\u1FBA', "\\grkaccent{\u1FEF}{\\vphantom{\u03B9}}\u0391");
		putForm('\u1FBB',
				"\\grkaccent{\u0374}{\\vphantom{\u03B9}}\\!\\!\u0391");
		putForm('\u1FC8', "\\grkaccent{\u1FEF}{\\vphantom{\u03B9}}\u0395");
		putForm('\u1FC9', "\\grkaccent{\u0374}{\\vphantom{\u03B9}}\u0395");
		putForm('\u1FCA', "\\grkaccent{\u1FEF}{\\vphantom{\u03B9}}\u0397");
		putForm('\u1FCB', "\\grkaccent{\u0374}{\\vphantom{\u03B9}}\u0397");
		putForm('\u1FD0', "\\u{\u03B9}");
		putForm('\u1FD1', "\\={\u03B9}");
		putForm('\u1FD3', "\\grkaccent{\u0385}\u03B9");
		putForm('\u1FD8', "\\u{\u0399}");
		putForm('\u1FD9', "\\={\u0399}");
		putForm('\u1FDA', "\\grkaccent{\u1FEF}{\\phantom{\u03B9}}\u0399");
		putForm('\u1FDB', "\\grkaccent{\u0374}{\\phantom{\u03B9}}\u0399");
		putForm('\u1FE0', "\\u{\u03C5}");
		putForm('\u1FE1', "\\={\u03C5}");
		putForm('\u1FE3', "\\grkaccent{\u0385}\u03C5");
		putForm('\u1FE8', "\\u{\u03A5}");
		putForm('\u1FE9', "\\={\u03A5}");
		putForm('\u1FEA', "\\grkaccent{\u1FEF}{\\phantom{\u03B9}}\u03A5");
		putForm('\u1FEB', "\\grkaccent{\u0374}{\\phantom{\u03B9}}\u03A5");
		putForm('\u1FF8', "\\grkaccent{\u1FEF}{\\vphantom{\u03B9}}\u039F");
		putForm('\u1FF9', "\\grkaccent{\u0374}{\\vphantom{\u03B9}}\\!\u039F");
		putForm('\u1FFA', "\\grkaccent{\u1FEF}{\\vphantom{\u03B9}}\u03A9");
		putForm('\u1FFB', "\\grkaccent{\u0374}{\\vphantom{\u03B9}}\\!\u03A9");
		putForm('\u2002', "\\;");
		putForm('\u2003', "\\quad");
		putForm('\u2004', "\\,");
		putForm('\u2005', "\\:");
		putForm('\u2008', "\\thinspace");
		putForm('\u200B', "\\!");
		putForm('\u200E', " ");
		putForm('\u200F', " ");
		putForm('\u201C', "\\text{``}");
		putForm('\u201D', "\\text{''}");
		putForm('\u2026', "\\ldots");
		putForm('\u2032', "{^\\prime}");
		putForm('\u2033', "{^\\prime}{^\\prime}");
		putForm('\u2034', "{^\\prime}{^\\prime}{^\\prime}");
		putForm('\u203E', "\\mathpunct{\\={\\ }}");
		putForm('\u2043', "\\hybull");
		putForm('\u20D7', "\\vec");
		putForm('\u2100', "\\sfrac{a}{c}");
		putForm('\u2101', "\\sfrac{a}{s}");
		putForm('\u2102', "\\mathbb{C}");
		putForm('\u2103', "\\sideset{^\\circ}{}\\text{C}");
		putForm('\u2105', "\\sfrac{c}{o}");
		putForm('\u2106', "\\sfrac{c}{u}");
		putForm('\u2109', "\\sideset{^\\circ}{}\\text{F}");
		putForm('\u210B', "\\mathscr{H}");
		putForm('\u210C', "\\mathfrak{H}");
		putForm('\u210D', "\\mathbb{H}");
		putForm('\u2110', "\\mathscr{I}");
		putForm('\u2112', "\\mathscr{L}");
		putForm('\u2115', "\\mathbb{N}");
		putForm('\u2119', "\\mathbb{P}");
		putForm('\u211A', "\\mathbb{Q}");
		putForm('\u211B', "\\mathscr{R}");
		putForm('\u211D', "\\mathbb{R}");
		putForm('\u2120', "{{}^{\\text{SM}}}");
		putForm('\u2122', "{{}^{\\text{TM}}}");
		putForm('\u2124', "\\mathbb{Z}");
		putForm('\u2128', "\\mathfrak{Z}");
		putForm('\u212B', "\\text{\\AA}");
		putForm('\u212C', "\\mathscr{B}");
		putForm('\u212D', "\\mathfrak{C}");
		putForm('\u212F', "e");
		putForm('\u2130', "\\mathscr{E}");
		putForm('\u2131', "\\mathscr{F}");
		putForm('\u2133', "\\mathscr{M}");
		putForm('\u2134', "\\mathit{o}");
		putForm('\u214B', "\\parr");
		putForm('\u2153', "\\text{\\sfrac13}");
		putForm('\u2154', "\\text{sfrac23}");
		putForm('\u2155', "\\text{\\sfrac15}");
		putForm('\u2156', "\\text{\\sfrac25}");
		putForm('\u2157', "\\text{\\sfrac35}");
		putForm('\u2158', "\\text{\\sfrac45}");
		putForm('\u2159', "\\text{\\sfrac16}");
		putForm('\u215A', "\\text{\\sfrac56}");
		putForm('\u215B', "\\text{\\sfrac18}");
		putForm('\u215C', "\\text{\\sfrac38}");
		putForm('\u215D', "\\text{\\sfrac58}");
		putForm('\u215E', "\\text{\\sfrac78}");
		putForm('\u215F', "\\text{\\sfrac{1}{\\ }}");
		putForm('\u2160', "\\text{I}");
		putForm('\u2161', "\\text{II}");
		putForm('\u2162', "\\text{III}");
		putForm('\u2163', "\\text{IV}");
		putForm('\u2164', "\\text{V}");
		putForm('\u2165', "\\text{VI}");
		putForm('\u2166', "\\text{VII}");
		putForm('\u2167', "\\text{VIII}");
		putForm('\u2168', "\\text{IX}");
		putForm('\u2169', "\\text{X}");
		putForm('\u216A', "\\text{XI}");
		putForm('\u216B', "\\text{XII}");
		putForm('\u216C', "\\text{L}");
		putForm('\u216D', "\\text{C}");
		putForm('\u216E', "\\text{D}");
		putForm('\u216F', "\\text{M}");
		putForm('\u2170', "\\text{i}");
		putForm('\u2171', "\\text{ii}");
		putForm('\u2172', "\\text{iii}");
		putForm('\u2173', "\\text{iv}");
		putForm('\u2174', "\\text{v}");
		putForm('\u2175', "\\text{vi}");
		putForm('\u2176', "\\text{vii}");
		putForm('\u2177', "\\text{viii}");
		putForm('\u2178', "\\text{ix}");
		putForm('\u2179', "\\text{x}");
		putForm('\u217A', "\\text{xi}");
		putForm('\u217B', "\\text{xii}");
		putForm('\u217C', "\\text{l}");
		putForm('\u217D', "\\text{c}");
		putForm('\u217E', "\\text{d}");
		putForm('\u217F', "\\text{m}");
		putForm('\u21A4', "\\mapsfrom");
		putForm('\u21A6', "\\mapsto");
		putForm('\u21A9', "\\hookleftarrow");
		putForm('\u21AA', "\\hookrightarrow");
		putForm('\u2209', "\\notin");
		putForm('\u2217', "{{}_\\ast}");
		putForm('\u221A', "\\surd");
		putForm('\u222B', "\\int");
		putForm('\u222C', "\\iint");
		putForm('\u222D', "\\iiint");
		putForm('\u222E', "\\oint");
		putForm('\u2236', "\\ratio");
		putForm('\u2237', "\\mathbin{\\ratio\\ratio}");
		putForm('\u2238', "\\dotminus");
		putForm('\u2239', "\\minuscolon");
		putForm('\u223A', "\\geoprop");
		putForm('\u2244', "{\\not\\simeq}");
		putForm('\u2245', "\\cong");
		putForm('\u2250', "\\doteq");
		putForm('\u2254', "\\colonequals");
		putForm('\u2255', "\\equalscolon");
		putForm('\u2258', "\\frowneq");
		putForm('\u2259', "\\stackrel{\\wedge}{=}");
		putForm('\u225A', "\\stackrel{\\vee}{=}");
		putForm('\u225B', "\\stackrel{\\scalebox{0.8}{\\bigstar}}{=}");
		putForm('\u225D', "\\stackrel{\\scalebox{0.75}{\\mathrm{def}}}{=}");
		putForm('\u225E', "\\stackrel{\\scalebox{0.75}{\\mathrm{m}}}{=}");
		putForm('\u225F', "\\questeq");
		putForm('\u2260', "\\neq");
		putForm('\u2262', "{\\not\\equiv}");
		putForm('\u2284', "{\\not\\subset}");
		putForm('\u2285', "{\\not\\supset}");
		putForm('\u22A7', "\\models");
		putForm('\u22C8', "\\bowtie");
		putForm('\u22D8', "\\llless");
		putForm('\u22EE', "\\vdots");
		putForm('\u22EF', "\\cdots");
		putForm('\u22F0', "\\iddots");
		putForm('\u22F1', "\\ddots");
		putForm('\u2460', "\\textcircled{\\texttt{1}}");
		putForm('\u2461', "\\textcircled{\\texttt{2}}");
		putForm('\u2462', "\\textcircled{\\texttt{3}}");
		putForm('\u2463', "\\textcircled{\\texttt{4}}");
		putForm('\u2464', "\\textcircled{\\texttt{5}}");
		putForm('\u2465', "\\textcircled{\\texttt{6}}");
		putForm('\u2466', "\\textcircled{\\texttt{7}}");
		putForm('\u2467', "\\textcircled{\\texttt{8}}");
		putForm('\u2468', "\\textcircled{\\texttt{9}}");
		putForm('\u24B6', "\\textcircled{\\texttt{A}}");
		putForm('\u24B7', "\\textcircled{\\texttt{B}}");
		putForm('\u24B8', "\\textcircled{\\texttt{C}}");
		putForm('\u24B9', "\\textcircled{\\texttt{D}}");
		putForm('\u24BA', "\\textcircled{\\texttt{E}}");
		putForm('\u24BB', "\\textcircled{\\texttt{F}}");
		putForm('\u24BC', "\\textcircled{\\texttt{G}}");
		putForm('\u24BD', "\\textcircled{\\texttt{H}}");
		putForm('\u24BE', "\\textcircled{\\texttt{I}}");
		putForm('\u24BF', "\\textcircled{\\texttt{J}}");
		putForm('\u24C0', "\\textcircled{\\texttt{K}}");
		putForm('\u24C1', "\\textcircled{\\texttt{L}}");
		putForm('\u24C2', "\\textcircled{\\texttt{M}}");
		putForm('\u24C3', "\\textcircled{\\texttt{N}}");
		putForm('\u24C4', "\\textcircled{\\texttt{O}}");
		putForm('\u24C5', "\\textcircled{\\texttt{P}}");
		putForm('\u24C6', "\\textcircled{\\texttt{Q}}");
		putForm('\u24C7', "\\textcircled{\\texttt{R}}");
		putForm('\u24C8', "\\textcircled{\\texttt{S}}");
		putForm('\u24C9', "\\textcircled{\\texttt{T}}");
		putForm('\u24CA', "\\textcircled{\\texttt{U}}");
		putForm('\u24CB', "\\textcircled{\\texttt{V}}");
		putForm('\u24CC', "\\textcircled{\\texttt{W}}");
		putForm('\u24CD', "\\textcircled{\\texttt{X}}");
		putForm('\u24CE', "\\textcircled{\\texttt{Y}}");
		putForm('\u24CF', "\\textcircled{\\texttt{Z}}");
		putForm('\u24D0', "\\textcircled{\\texttt{a}}");
		putForm('\u24D1', "\\textcircled{\\texttt{b}}");
		putForm('\u24D2', "\\textcircled{\\texttt{c}}");
		putForm('\u24D3', "\\textcircled{\\texttt{d}}");
		putForm('\u24D4', "\\textcircled{\\texttt{e}}");
		putForm('\u24D5', "\\textcircled{\\texttt{f}}");
		putForm('\u24D6', "\\textcircled{\\texttt{g}}");
		putForm('\u24D7', "\\textcircled{\\texttt{h}}");
		putForm('\u24D8', "\\textcircled{\\texttt{i}}");
		putForm('\u24D9', "\\textcircled{\\texttt{j}}");
		putForm('\u24DA', "\\textcircled{\\texttt{k}}");
		putForm('\u24DB', "\\textcircled{\\texttt{l}}");
		putForm('\u24DC', "\\textcircled{\\texttt{m}}");
		putForm('\u24DD', "\\textcircled{\\texttt{n}}");
		putForm('\u24DE', "\\textcircled{\\texttt{o}}");
		putForm('\u24DF', "\\textcircled{\\texttt{p}}");
		putForm('\u24E0', "\\textcircled{\\texttt{q}}");
		putForm('\u24E1', "\\textcircled{\\texttt{r}}");
		putForm('\u24E2', "\\textcircled{\\texttt{s}}");
		putForm('\u24E3', "\\textcircled{\\texttt{t}}");
		putForm('\u24E4', "\\textcircled{\\texttt{u}}");
		putForm('\u24E5', "\\textcircled{\\texttt{v}}");
		putForm('\u24E6', "\\textcircled{\\texttt{w}}");
		putForm('\u24E7', "\\textcircled{\\texttt{x}}");
		putForm('\u24E8', "\\textcircled{\\texttt{y}}");
		putForm('\u24E9', "\\textcircled{\\texttt{z}}");
		putForm('\u2580', "\\uhblk");
		putForm('\u2581', "\\lhblk");
		putForm('\u2588', "\\block");
		putForm('\u2591', "\\fgcolor{bfbfbf}{\\block}");
		putForm('\u2592', "\\fgcolor{808080}{\\block}");
		putForm('\u2593', "\\fgcolor{404040}{\\block}");
		putForm('\u25AE', "\\marker");
		putForm('\u27F5', "\\longleftarrow");
		putForm('\u27F6', "\\longrightarrow");
		putForm('\u27F7', "\\longleftrightarrow");
		putForm('\u27F8', "\\Longleftarrow");
		putForm('\u27F9', "\\Longrightarrow");
		putForm('\u27FA', "\\Longleftrightarrow");
		putForm('\u27FB', "\\longmapsfrom");
		putForm('\u27FC', "\\longmapsto");
		putForm('\u27FD', "\\Longmapsfrom");
		putForm('\u27FE', "\\Longmapsto");
		putForm('\u2906', "\\Mapsfrom");
		putForm('\u2907', "\\Mapsto");
		putForm('\u2993', "\\mathbin{\\rlap{<}\\;(}");
		putForm('\u2994', "\\mathbin{\\rlap{>}\\,)}");
		putForm('\u2A0C', "\\iiiint");
		putForm('\u2A74', "\\coloncolonequals");
		putForm('\u2AA4', "\\glj");
		putForm('\u2AA5', "\\gla");
		putForm('\uFB00', "\\text{ff}");
		putForm('\uFB01', "\\text{fi}");
		putForm('\uFB02', "\\text{fl}");
		putForm('\uFB03', "\\text{ffi}");
		putForm('\uFB04', "\\text{ffl}");
		putForm('\u1FBF', "\u1FBF");
		putForm('\u1FEE', "\u0385");
		putForm('\u1FFD', "\u0374");
		putForm('\u220A', "\\in");
		putForm('\u2212', "\\minus");
		putForm('\u2215', "\\slash");
		putForm('\u22C5', "\\cdot");
		putForm('\u25AA', "\\blacksquare");
		putForm('\u27E8', "\\langle");
		putForm('\u27E9', "\\rangle");
	}
}
