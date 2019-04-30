/* MhchemParser.java
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

package com.himamis.retex.renderer.share.mhchem;

import java.util.HashSet;
import java.util.Set;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.CharAtom;
import com.himamis.retex.renderer.share.CharMapping;
import com.himamis.retex.renderer.share.EnvArray;
import com.himamis.retex.renderer.share.FractionAtom;
import com.himamis.retex.renderer.share.GroupConsumer;
import com.himamis.retex.renderer.share.MHeightAtom;
import com.himamis.retex.renderer.share.MathCharAtom;
import com.himamis.retex.renderer.share.RowAtom;
import com.himamis.retex.renderer.share.ScriptsAtom;
import com.himamis.retex.renderer.share.SpaceAtom;
import com.himamis.retex.renderer.share.SubSupCom;
import com.himamis.retex.renderer.share.SymbolAtom;
import com.himamis.retex.renderer.share.Symbols;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.TextStyle;
import com.himamis.retex.renderer.share.TextStyleAtom;
import com.himamis.retex.renderer.share.exception.ParseException;

public class MhchemParser extends TeXParser {

	public static enum Arrow {
		left, // <-
		right, // ->
		leftright, // <->
		LeftRight, // <-->
		leftrightHarpoon, // <=>
		leftrightSmallHarpoon, // <=>>
		leftSmallHarpoonRight, // <<=>
	}

	private static enum ElementType {
		none, greek, roman,
	}

	private final static class StopGroupConsumer extends GroupConsumer {

		public StopGroupConsumer() {
			super(TeXConstants.Opener.NONE);
		}

		@Override
		public void add(TeXParser tp, Atom a) {
			tp.stop();
			super.add(tp, a);
		}

		@Override
		public boolean close(TeXParser tp, final TeXConstants.Opener opener) {
			tp.stop();
			return super.close(tp, opener);
		}
	}

	private final static class NormalGroupConsumer extends GroupConsumer {

		public NormalGroupConsumer() {
			super(TeXConstants.Opener.LBRACE);
		}
	}

	private final class ScriptParser extends TeXParser {

		ScriptParser(String parseString, int pos, int line, int col) {
			super(parseString, pos, line, col);
			addConsumer(new StopGroupConsumer());
		}

		public boolean hasNormalGroupConsumer() {
			return stack.peek() instanceof NormalGroupConsumer;
		}

		@Override
		public Atom convertASCIICharToAtom(final char c,
				final boolean oneChar) {
			if (!hasNormalGroupConsumer() && c >= 'a' && c <= 'z') {
				return new MathCharAtom(c, isMathMode());
			}
			return super.convertASCIICharToAtom(c, oneChar);
		}

		@Override
		public void processCommand(final String command) throws ParseException {
			String com = command;
			if (!hasNormalGroupConsumer()) {
				if (isUpperGreek(command)) {
					com = "Up" + Character.toLowerCase(com.charAt(0))
							+ com.substring(1);
				}
			}
			super.processCommand(com);
		}

		@Override
		public void processLBrace() {
			if (hasGroupConsumer(TeXConstants.Opener.LBRACE)) {
				addConsumer(new NormalGroupConsumer());
			} else {
				super.processLBrace();
			}
		}
	}

	private final class NormalParser extends TeXParser {

		NormalParser(String parseString, int pos, int line, int col) {
			super(parseString, pos, line, col);
			addConsumer(new StopGroupConsumer());
		}

		@Override
		public void processLBrace() {
			if (hasGroupConsumer(TeXConstants.Opener.LBRACE)) {

			} else {
				++pos;
				super.processLBrace();
			}
		}
	}

	private CharMapping exponentCM = null;
	private Set<String> lowerGreeks = null;
	private ElementType etype = ElementType.none;

	public MhchemParser(final String parseString) {
		super(parseString);
		addConsumer(new GroupConsumer(TeXConstants.Opener.NONE));
		addToConsumer(CEEmptyAtom.get());
	}

	private CharMapping getExponentCM() {
		if (exponentCM == null) {
			// TODO: maybe use a SoftReference here
			exponentCM = new CharMapping(CharMapping.getDefault());
			exponentCM.putSym('.', "bullet");
		}

		return exponentCM;
	}

	private Set<String> getLowerGreeks() {
		if (lowerGreeks == null) {
			lowerGreeks = new HashSet<String>() {
				{
					add("alpha");
					add("beta");
					add("gamma");
					add("delta");
					add("epsilon");
					add("zeta");
					add("eta");
					add("theta");
					add("iota");
					add("kappa");
					add("lambda");
					add("mu");
					add("nu");
					add("xi");
					add("omicron");
					add("pi");
					add("rho");
					add("varsigma");
					add("sigma");
					add("tau");
					add("upsilon");
					add("phi");
					add("varphi");
					add("chi");
					add("psi");
					add("omega");
				}
			};
		}

		return lowerGreeks;
	}

	public boolean isLowerGreek(final String com) {
		return getLowerGreeks().contains(com);
	}

	public boolean isUpperGreek(final String com) {
		if (com.length() >= 2) {
			final char c = com.charAt(0);
			if (Character.isUpperCase(c)) {
				final String low = Character.toLowerCase(c) + com.substring(1);
				return getLowerGreeks().contains(low);
			}
		}
		return false;
	}

	public Atom getGreek(final String com) {
		final Atom a = SymbolAtom.get(com);
		// if (a == null) {
		// AlphabetManager.get().addBlock(Character.UnicodeBlock.GREEK);
		// return SymbolAtom.get(com);
		// }
		return a;
	}

	public Atom handleGreek(char c, boolean upLower) {
		if (c == '\\') {
			final int spos = pos;
			final String com = getCommand();
			if (isLowerGreek(com)) {
				if (upLower) {
					return getGreek("up" + com);
				}
				return SymbolAtom.get(com);
			} else if (isUpperGreek(com)) {
				final String up = "Up" + Character.toLowerCase(com.charAt(0))
						+ com.substring(1);
				return getGreek(up);
			}
			pos = spos;
		}

		return null;
	}

	@Override
	public void parse() throws ParseException {
		while (pos < len || removeString()) {
			final char c = parseString.charAt(pos);
			switch (c) {
			case '\u0000':
			case '\u0001':
			case '\u0002':
			case '\u0003':
			case '\u0004':
			case '\u0005':
			case '\u0006':
			case '\u0007':
			case '\u0008':
			case '\u0009':
				++pos;
				break;
			case '\n':
				newLine();
				skipWhites();
				break;
			case '\u000B':
			case '\u000C':
			case '\r':
			case '\u000E':
			case '\u000F':
			case '\u0010':
			case '\u0011':
			case '\u0012':
			case '\u0013':
			case '\u0014':
			case '\u0015':
			case '\u0016':
			case '\u0017':
			case '\u0018':
			case '\u0019':
			case '\u001A':
			case '\u001B':
			case '\u001C':
			case '\u001D':
			case '\u001E':
			case '\u001F':
				++pos;
				break;
			case ' ':
				++pos;
				handleSpace();
				break;
			case '!':
				++pos;
				charMapping.replaceUnsafe('!', this);
				break;
			case '\"':
				++pos;
				if (isTextMode()) {
					charMapping.replaceUnsafe('\'', this);
					charMapping.replaceUnsafe('\'', this);
				} else {
					cumSupSymbols(Symbols.APOSTROPHE, Symbols.APOSTROPHE);
				}
				break;
			case '#':
				++pos;
				addToConsumer(new MhchemBondAtom(3));
				break;
			case '$':
				addToConsumer(handleNormal());
				break;
			case '%':
				// We've a comment
				++pos;
				skipUntilCr();
				break;
			case '&':
				close();
				if (isAmpersandAllowed()) {
					++pos;
					addToConsumer(EnvArray.ColSep.get());
				} else {
					throw new ParseException(this,
							"Character '&' is only available in array mode !");
				}
				break;
			case '\'':
				++pos;
				if (isTextMode()) {
					charMapping.replaceUnsafe('\'', this);
				} else {
					// For this kind of syms, need to modify SubSupCom
					cumSupSymbols(Symbols.PRIME);
				}
				break;
			case '(':
				++pos;
				addToConsumer(Symbols.LBRACK);
				break;
			case ')':
				handleElement();
				break;
			case '*':
				++pos;
				addToConsumer(Symbols.CDOT);
				break;
			case '+':
				++pos;
				addToConsumer(Symbols.PLUS);
				break;
			case ',':
				++pos;
				charMapping.replaceUnsafe(c, this);
				break;
			case '-':
				++pos;
				addToConsumer(SymbolAtom.get("textminus"));
				break;
			case '.':
				++pos;
				handlePoint();
				break;
			case '/':
				++pos;
				charMapping.replaceUnsafe(c, this);
				break;
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				handleNumber(c);
				break;
			case ':':
			case ';':
				++pos;
				charMapping.replaceUnsafe(c, this);
				break;
			case '<': {
				if (!handleArrow('<')) {
					++pos;
					boolean ll = false;
					if (pos < len) {
						final char cc = parseString.charAt(pos);
						if (cc == '<') {
							++pos;
							addToConsumer(SymbolAtom.get("ll"));
							ll = true;
						}
					}
					if (!ll) {
						charMapping.replaceUnsafe('<', this);
					}
				}
				break;
			}
			case '=': {
				++pos;
				if (pos < len) {
					final char cc = parseString.charAt(pos);
					if (isElementStart(cc)) {
						addToConsumer(new MhchemBondAtom(2));
					} else {
						addToConsumer(Symbols.EQUALS);
					}
				} else {
					addToConsumer(Symbols.EQUALS);
				}
				break;
			}
			case '>': {
				if (!handleArrow('>')) {
					++pos;
					boolean gg = false;
					if (pos < len) {
						final char cc = parseString.charAt(pos);
						if (cc == '>') {
							++pos;
							addToConsumer(SymbolAtom.get("gg"));
							gg = true;
						}
					}
					if (!gg) {
						charMapping.replaceUnsafe('>', this);
					}
				}
				break;
			}
			case '?':
			case '@':
				++pos;
				charMapping.replaceUnsafe(c, this);
				break;
			case 'A':
			case 'B':
			case 'C':
			case 'D':
			case 'E':
			case 'F':
			case 'G':
			case 'H':
			case 'I':
			case 'J':
			case 'K':
			case 'L':
			case 'M':
			case 'N':
			case 'O':
			case 'P':
			case 'Q':
			case 'R':
			case 'S':
			case 'T':
			case 'U':
			case 'V':
			case 'W':
			case 'X':
			case 'Y':
			case 'Z':
				etype = ElementType.roman;
				handleElement();
				break;
			case '[':
				++pos;
				addToConsumer(Symbols.LSQBRACK);
				break;
			case '\\': {
				final Atom greek = handleGreek('\\', true);
				if (greek != null) {
					addToConsumer(greek);
					etype = ElementType.greek;
					handleElement();
				} else {
					prevpos = pos;
					final String command = getCommand();
					if (!command.isEmpty()) {
						processCommand(command);
					}
				}
				break;
			}
			case ']':
				++pos;
				if (!processRSqBracket()) {
					charMapping.replaceUnsafe(']', this);
				}
				break;
			case '^':
				handleSupAndSub('^', false);
				break;
			case '_': {
				++pos;
				processSubSup('_');
				break;
			}
			case '`':
				++pos;
				if (isTextMode()) {
					charMapping.replaceUnsafe('`', this);
				} else {
					// For this kind of syms, need to modify SubSupCom
					cumSupSymbols(Symbols.BACKPRIME);
				}
				break;
			case 'a':
			case 'b':
			case 'c':
			case 'd':
			case 'e':
			case 'f':
			case 'g':
			case 'h':
			case 'i':
			case 'j':
			case 'k':
			case 'l':
			case 'm':
			case 'n':
			case 'o':
			case 'p':
			case 'q':
			case 'r':
			case 's':
			case 't':
			case 'u':
			case 'v':
			case 'w':
			case 'x':
			case 'y':
			case 'z':
				handleLower(c);
				break;
			case '{':
				processLBrace();
				break;
			case '|':
				++pos;
				charMapping.replaceUnsafe('|', this);
				break;
			case '}':
				++pos;
				processRBrace();
				break;
			case '~':
				++pos;
				addToConsumer(new SpaceAtom());
				break;
			default:
				++pos;
				convertCharacter(c, false);
				break;
			}
		}
	}

	public void handlePoint() {
		if (etype == ElementType.none) {
			charMapping.replaceUnsafe('.', this);
		} else {
			addToConsumer(Symbols.CDOT);
		}
	}

	public void handleLower(final char c) {
		if (pos + 1 < len) {
			final char cc = parseString.charAt(pos + 1);
			if (cc == ' ') {
				++pos;
				addToConsumer(new MathCharAtom(c, true));
				addToConsumer(CEEmptyAtom.get());
				return;
			}
		}
		etype = ElementType.roman;
		handleElement();
	}

	public Atom handleSingleLetterInScript(char c) {
		if (c >= 'a' && c <= 'z') {
			++pos;
			return new MathCharAtom(c, true);
		} else if (c >= 'A' && c <= 'Z') {
			++pos;
			return new CharAtom(c, false);
		}
		return handleGreek(c, false);
	}

	private Atom handleUsualScript(boolean sup) {
		final TeXParser tp = new ScriptParser(parseString, pos, line, col);
		if (sup) {
			tp.setCharMapping(getExponentCM());
		}
		tp.parse();
		final int[] info = tp.getStopInfo();
		pos = info[0];
		line = info[1];
		col = info[2];
		return tp.get();
	}

	private Atom handleNormal() {
		final TeXParser tp = new TeXParser(parseString, pos, line, col);
		tp.addConsumer(new StopGroupConsumer());
		tp.pushMode(false);
		tp.parse();
		final int[] info = tp.getStopInfo();
		pos = info[0];
		line = info[1];
		col = info[2];

		return tp.get();
	}

	public boolean handleSubNum(char c) {
		if (c >= '0' && c <= '9') {
			final RowAtom sub = new RowAtom(new CharAtom(c, false));
			++pos;
			while (pos < len) {
				c = parseString.charAt(pos);
				if (c >= '0' && c <= '9') {
					sub.add(new CharAtom(c, false));
					++pos;
				} else {
					break;
				}
			}
			addToConsumer(
					new ScriptsAtom(MHeightAtom.get(), sub.simplify(), null));
			return true;
		}
		return false;
	}

	public Atom handleSub(char c) {
		if (c == '_') {
			++pos;
			skipPureWhites();
			Atom greek;
			RowAtom sub = new RowAtom();
			boolean stop = false;

			if (pos < len) {
				c = parseString.charAt(pos);
				if (c == '-') {
					sub.add(Symbols.MINUS);
					++pos;
				} else if (c == '+') {
					sub.add(Symbols.PLUS);
					++pos;
					stop = true;
				} else if (c >= 'a' && c <= 'z') {
					sub.add(new MathCharAtom(c, true));
					++pos;
					stop = true;
				} else if (c >= 'A' && c <= 'Z') {
					sub.add(new CharAtom(c, false));
					++pos;
					stop = true;
				} else if ((greek = handleGreek(c, true)) != null) {
					sub.add(greek);
					stop = true;
				} else if (c >= '0' && c <= '9') {
					sub.add(new CharAtom(c, false));
					++pos;
				}
			}

			if (stop) {
				return sub;
			}

			while (pos < len) {
				c = parseString.charAt(pos);
				if (c >= '0' && c <= '9') {
					sub.add(new CharAtom(c, false));
					++pos;
				} else {
					break;
				}
			}

			if (!sub.isEmpty()) {
				return sub;
			}

			return handleUsualScript(false);

		}
		return null;
	}

	public Atom handleSup(char c) {
		if (c == '^') {
			Atom single;
			++pos;
			skipPureWhites();
			if (pos >= len) {
				return null;
			}
			c = parseString.charAt(pos);
			if (c >= '0' && c <= '9') {
				final RowAtom sup = new RowAtom(new CharAtom(c, false));
				++pos;
				while (pos < len) {
					c = parseString.charAt(pos);
					if (c >= '0' && c <= '9') {
						sup.add(new CharAtom(c, false));
						++pos;
					} else {
						break;
					}
				}
				if (c == '+') {
					++pos;
					sup.add(Symbols.PLUS);
				} else if (c == '-' && etype != ElementType.greek) {
					++pos;
					sup.add(Symbols.MINUS);
				}

				return sup.simplify();
			} else if (c == 'x') {
				++pos;
				return SymbolAtom.get("times");
			} else if (c == '+') {
				++pos;
				return Symbols.PLUS;
			} else if (c == '-') {
				++pos;
				return Symbols.MINUS;
			} else if (c == '.') {
				++pos;
				return SymbolAtom.get("bullet");
			} else if ((single = handleSingleLetterInScript(c)) != null) {
				return single;
			}

			return handleUsualScript(true);
		}
		return null;
	}

	public boolean handleSupAndSub(char c, boolean M) {
		Atom sup;
		if ((sup = handleSup(c)) != null) {
			Atom sub = null;
			boolean greekMinus = false;
			if (pos < len) {
				c = parseString.charAt(pos);
				if (c == '-') {
					// sup didn't eat the minus so we've a greek letter before
					++pos;
					greekMinus = true;
				}
				sub = handleSub(c);
			}
			final Atom base = M ? MHeightAtom.get() : SubSupCom.getBase(this);
			addToConsumer(new ScriptsAtom(base, sub, sup));
			if (greekMinus) {
				addToConsumer(SymbolAtom.get("textminus"));
			}

			return true;
		}
		return false;
	}

	private boolean isLBrace(final char c) {
		if (c == '\\' && pos + 1 < len) {
			return parseString.charAt(pos + 1) == '{';
		}
		return false;
	}

	public boolean isElementStart(final char c) {
		return isRomanLetter(c) || (c == '(') || (c == '[') || isLBrace(c);
	}

	public boolean handleElementPart() {
		Atom sup, sub, greek;
		char c = parseString.charAt(pos);
		if (isRomanLetter(c)) {
			++pos;
			addToConsumer(new CharAtom(c, false));
			etype = ElementType.roman;
			return true;
		} else if (handleSubNum(c)) {
			return true;
		} else if (handleSupAndSub(c, true)) {
			return true;
		} else if ((sub = handleSub(c)) != null) {
			addToConsumer(new ScriptsAtom(MHeightAtom.get(), sub, null));
			return true;
		} else if (c == '(') {
			++pos;
			addToConsumer(Symbols.LBRACK);
			return true;
		} else if (c == ')') {
			++pos;
			addToConsumer(Symbols.RBRACK);
			return true;
		} else if (c == '[') {
			++pos;
			addToConsumer(Symbols.LSQBRACK);
			return true;
		} else if (c == ']') {
			++pos;
			if (!processRSqBracket()) {
				addToConsumer(Symbols.RSQBRACK);
				return true;
			}
			return false;
		} else if (c == '\\' && pos + 1 < len) {
			c = parseString.charAt(pos + 1);
			if (c == '{') {
				pos += 2;
				addToConsumer(Symbols.LBRACE);
				return true;
			} else if (c == '}') {
				pos += 2;
				addToConsumer(Symbols.RBRACE);
				return true;
			}
		} else if (c == '-' || c == '+') {
			return handleElementPM(c);
		} else if ((greek = handleGreek(c, true)) != null) {
			addToConsumer(greek);
			etype = ElementType.greek;
			return true;
		}

		return false;
	}

	public void handleElement() {
		while (handleElementPart() && pos < len)
			;
		if (pos < len) {
			final char c = parseString.charAt(pos);
			if (c == '.') {
				++pos;
				handlePoint();
			}
		}
		etype = ElementType.none;
	}

	public boolean handleElementPM(char c) {
		if (c == '-') {
			// Ab- Cd <=> Ab^{-}Cd
			// Ab-Cd <=> Ab -- Cd
			// Ab->Cd <=> Ab --> Cd
			++pos;
			if (pos < len) {
				c = parseString.charAt(pos);
				if (c == ' ') {
					++pos;
					addToConsumer(new ScriptsAtom(MHeightAtom.get(), null,
							Symbols.MINUS));
				} else if (c == '>') {
					++pos;
					handleArrow(Arrow.right);
				} else if (isElementStart(c)) {
					if (etype == ElementType.greek) {
						addToConsumer(SymbolAtom.get("textminus"));
					} else {
						addToConsumer(new MhchemBondAtom(1));
					}
					return true;
				} else {
					addToConsumer(new ScriptsAtom(MHeightAtom.get(), null,
							Symbols.MINUS));
				}
			} else {
				addToConsumer(new ScriptsAtom(MHeightAtom.get(), null,
						Symbols.MINUS));
			}
		} else if (c == '+') {
			++pos;
			addToConsumer(
					new ScriptsAtom(MHeightAtom.get(), null, Symbols.PLUS));
		}
		return false;
	}

	public void handleNumber(final char c) {
		RowAtom ra = new RowAtom(convertASCIICharToAtom(c, false));
		RowAtom num = ra;
		RowAtom den = null;
		while (++pos < len) {
			final char cc = parseString.charAt(pos);
			if (cc >= '0' && cc <= '9') {
				ra.add(convertASCIICharToAtom(cc, false));
			} else if (cc == '/') {
				den = new RowAtom();
				ra = den;
			} else {
				break;
			}
		}

		skipPureWhites();

		if (pos < len) {
			final char cc = parseString.charAt(pos);
			if (isRomanLetter(cc)) {
				if (den != null) {
					addToConsumer(new FractionAtom(num, den));
				} else {
					addToConsumer(num);
				}
			} else {
				addToConsumer(num);
				if (den != null) {
					addToConsumer(SymbolAtom.get("slash"));
					addToConsumer(den);
				}
			}
		} else {
			addToConsumer(num);
			if (den != null) {
				addToConsumer(SymbolAtom.get("slash"));
				addToConsumer(den);
			}
		}
	}

	public void handleSpace() {
		if (pos < len) {
			final char c = parseString.charAt(pos);
			if (c == 'v') {
				if (pos + 1 < len) {
					final char cc = parseString.charAt(pos + 1);
					if (cc == ' ') {
						// arrow down
						pos += 2;
						skipPureWhites();
						addToConsumer(SymbolAtom.get("downarrow"));
						addToConsumer(CEEmptyAtom.get());
						return;
					}
				} else {
					addToConsumer(SymbolAtom.get("downarrow"));
					++pos;
					return;
				}
			} else if (c == '^') {
				if (pos + 1 < len) {
					final char cc = parseString.charAt(pos + 1);
					if (cc == ' ') {
						// arrow up
						pos += 2;
						skipPureWhites();
						addToConsumer(SymbolAtom.get("uparrow"));
						addToConsumer(CEEmptyAtom.get());
						return;
					}
				} else {
					addToConsumer(SymbolAtom.get("uparrow"));
					++pos;
					return;
				}
			} else if (c == '+') {
				++pos;
				addToConsumer(Symbols.PLUS);
				return;
			} else if (c == '-') {
				if (pos + 1 < len) {
					final char cc = parseString.charAt(pos + 1);
					if (cc == ' ') {
						// minus
						pos += 2;
						skipPureWhites();
						addToConsumer(Symbols.MINUS);
						addToConsumer(CEEmptyAtom.get());
					} else if (cc == '>') {
						pos += 2;
						handleArrow(Arrow.right);
						return;
					}
				} else {
					++pos;
					addToConsumer(Symbols.MINUS);
					return;
				}
				return;
			} else if (c == '=') {
				++pos;
				addToConsumer(Symbols.EQUALS);
				return;
			} else if (c == '(' && pos + 2 < len
					&& parseString.charAt(pos + 2) == ')') {
				final char cc = parseString.charAt(pos + 1);
				if (cc == 'v') {
					pos += 3;
					addToConsumer(SymbolAtom.get("downarrow"));
					return;
				} else if (cc == '^') {
					pos += 3;
					addToConsumer(SymbolAtom.get("uparrow"));
					return;
				}
			}
		}
		addToConsumer(CEEmptyAtom.get());
	}

	public boolean handleArrow(final char c) {
		if (pos + 1 < len) {
			if (c == '<') {
				char cc = parseString.charAt(pos + 1);
				if (cc == '-') {
					if (pos + 2 < len) {
						cc = parseString.charAt(pos + 2);
						if (cc == '>') { // <->
							pos += 3;
							handleArrow(Arrow.leftright);
							return true;
						} else if (cc == '-' && pos + 3 < len
								&& parseString.charAt(pos + 3) == '>') {
							// <-->
							pos += 4;
							handleArrow(Arrow.LeftRight);
							return true;
						}
					} // <-
					pos += 2;
					handleArrow(Arrow.left);
					return true;
				} else if (cc == '=') {
					if (pos + 2 < len && parseString.charAt(pos + 2) == '>') {
						if (pos + 3 < len
								&& parseString.charAt(pos + 3) == '>') {
							// <=>>
							pos += 4;
							handleArrow(Arrow.leftrightSmallHarpoon);
							return true;
						} // <=>
						pos += 3;
						handleArrow(Arrow.leftrightHarpoon);
						return true;
					}
				} else if (c == '<' && pos + 3 < len
						&& parseString.charAt(pos + 2) == '='
						&& parseString.charAt(pos + 3) == '>') {
					// <<=>
					pos += 4;
					handleArrow(Arrow.leftSmallHarpoonRight);
					return true;
				}
			} else if (c == '-' && pos + 1 < len
					&& parseString.charAt(pos + 1) == '>') {
				// ->
				pos += 2;
				handleArrow(Arrow.right);
				return true;
			}
		}

		return false;
	}

	public void handleArrow(final Arrow ar) {
		new MhchemArrowConsumer(ar).init(this);
	}

	@Override
	public void processCommand(final String command) throws ParseException {
		Atom greek;
		if (command.equals("text")) {
			final String code = getGroupAsArgument();
			final TeXParser tp = new TeXParser(code);
			tp.setTextMode();
			tp.parse();
			addToConsumer(new TextStyleAtom(tp.get(), TextStyle.MATHNORMAL));
		} else if (command.equals("ca")) {
			addToConsumer(Symbols.SIM.changeType(TeXConstants.TYPE_ORDINARY));
		} else {
			super.processCommand(command);
		}
	}

	@Override
	public void processLBrace() {
		if (hasGroupConsumer()) {
			addToConsumer(handleNormal());
		} else {
			++pos;
			super.processLBrace();
		}
	}
}
