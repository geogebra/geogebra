/* Symbols.java
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

public final class Symbols {

	public static final SymbolAtom LBRACK = new SymbolAtom("lbrack",
			TeXConstants.TYPE_OPENING, '(');
	public static final SymbolAtom RBRACK = new SymbolAtom("rbrack",
			TeXConstants.TYPE_CLOSING, ')');
	public static final SymbolAtom LBRACE = new SymbolAtom("lbrace",
			TeXConstants.TYPE_OPENING, '{');
	public static final SymbolAtom RBRACE = new SymbolAtom("rbrace",
			TeXConstants.TYPE_CLOSING, '}');
	public static final SymbolAtom LSQBRACK = new SymbolAtom("lsqbrack",
			TeXConstants.TYPE_OPENING, '[');
	public static final SymbolAtom RSQBRACK = new SymbolAtom("rsqbrack",
			TeXConstants.TYPE_CLOSING, ']');
	public static final SymbolAtom LANGLE = new SymbolAtom("langle",
			TeXConstants.TYPE_OPENING, '\u3008');
	public static final SymbolAtom RANGLE = new SymbolAtom("rangle",
			TeXConstants.TYPE_CLOSING, '\u3009');
	public static final SymbolAtom INT = (SymbolAtom) SymbolAtom.get("intop")
			.changeLimits(TeXConstants.SCRIPT_NOLIMITS);
	public static final SymbolAtom INTOP = SymbolAtom.get("intop");
	public static final SymbolAtom OINT = (SymbolAtom) SymbolAtom.get("oint")
			.changeLimits(TeXConstants.SCRIPT_NOLIMITS);
	public static final SymbolAtom EQUALS = SymbolAtom.get("equals");
	public static final SymbolAtom CIRC = SymbolAtom.get("circ");
	public static final SymbolAtom NORMALDOT = SymbolAtom.get("normaldot");
	public static final SymbolAtom TEXTNORMALDOT = SymbolAtom
			.get("textnormaldot");
	public static final SymbolAtom CDOT = SymbolAtom.get("cdot");
	public static final SymbolAtom CDOTP = SymbolAtom.get("cdotp");
	public static final SymbolAtom LDOTP = SymbolAtom.get("ldotp");
	public static final SymbolAtom DOT = SymbolAtom.get("dot");
	public static final SymbolAtom DDOT = SymbolAtom.get("ddot");
	public static final SymbolAtom NOT = SymbolAtom.get("not");
	public static final SymbolAtom WITH = SymbolAtom.get("&");
	public static final SymbolAtom SIM = SymbolAtom.get("sim");
	public static final SymbolAtom VEE = SymbolAtom.get("vee");
	public static final SymbolAtom VEC = SymbolAtom.get("vec");
	public static final SymbolAtom MINUS = SymbolAtom.get("minus");
	public static final SymbolAtom BAR = SymbolAtom.get("bar");
	public static final SymbolAtom TEXTENDASH = SymbolAtom.get("textendash");
	public static final SymbolAtom HAT = SymbolAtom.get("hat");
	public static final SymbolAtom WIDEHAT = SymbolAtom.get("widehat");
	public static final SymbolAtom BREVE = SymbolAtom.get("breve");
	public static final SymbolAtom QUESTION = SymbolAtom.get("question");
	public static final SymbolAtom SMALLFROWN = SymbolAtom.get("smallfrown");
	public static final SymbolAtom FROWN = SymbolAtom.get("frown");
	public static final SymbolAtom IN = SymbolAtom.get("in");
	public static final SymbolAtom LEFTARROW = SymbolAtom.get("leftarrow");
	public static final SymbolAtom RIGHTARROW = SymbolAtom.get("rightarrow");
	public static final SymbolAtom VERT = SymbolAtom.get("vert");
	public static final SymbolAtom DOUBLE_VERT = SymbolAtom.get("|");
	public static final SymbolAtom ACUTE = SymbolAtom.get("acute");
	public static final SymbolAtom WIDETILDE = SymbolAtom.get("widetilde");
	public static final SymbolAtom TILDE = SymbolAtom.get("tilde");
	public static final SymbolAtom GRAVE = SymbolAtom.get("grave");
	public static final SymbolAtom CHECK = SymbolAtom.get("check");
	public static final SymbolAtom MATHRING = SymbolAtom.get("mathring");
	public static final SymbolAtom APOSTROPHE = SymbolAtom.get("textapos");
	public static final SymbolAtom PRIME = SymbolAtom.get("prime");
	public static final SymbolAtom BACKPRIME = SymbolAtom.get("backprime");
	public static final SymbolAtom SLASH = SymbolAtom.get("slash");
	public static final SymbolAtom TEXTFRACTIONSOLIDUS = SymbolAtom
			.get("textfractionsolidus");
	public static final SymbolAtom SQRT = SymbolAtom.get("surdsign");
	public static final SymbolAtom BIG_RELBAR = SymbolAtom.get("Relbar");
	public static final SymbolAtom BIG_RIGHTARROW = SymbolAtom
			.get("Rightarrow");
	public static final SymbolAtom BIG_LEFTARROW = SymbolAtom.get("Leftarrow");
	public static final SymbolAtom LHOOK = SymbolAtom.get("lhook");
	public static final SymbolAtom RHOOK = SymbolAtom.get("rhook");
	public static final SymbolAtom MAPSTOCHAR = SymbolAtom.get("mapstochar");
	public static final SymbolAtom COLON = SymbolAtom.get("colon");
	public static final SymbolAtom PLUS = SymbolAtom.get("plus");
}
