/* Commands.java
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
import java.util.Locale;
import java.util.Map;

import com.himamis.retex.renderer.share.commands.Command;
import com.himamis.retex.renderer.share.commands.Command0A;
import com.himamis.retex.renderer.share.commands.Command0AImpl;
import com.himamis.retex.renderer.share.commands.CommandATop;
import com.himamis.retex.renderer.share.commands.CommandATopwithdelims;
import com.himamis.retex.renderer.share.commands.CommandAbove;
import com.himamis.retex.renderer.share.commands.CommandAbovewithdelims;
import com.himamis.retex.renderer.share.commands.CommandAccent;
import com.himamis.retex.renderer.share.commands.CommandAccentSet;
import com.himamis.retex.renderer.share.commands.CommandAcute1;
import com.himamis.retex.renderer.share.commands.CommandAcute2;
import com.himamis.retex.renderer.share.commands.CommandB;
import com.himamis.retex.renderer.share.commands.CommandBCancel;
import com.himamis.retex.renderer.share.commands.CommandBE;
import com.himamis.retex.renderer.share.commands.CommandBGColor;
import com.himamis.retex.renderer.share.commands.CommandBar;
import com.himamis.retex.renderer.share.commands.CommandBeginGroup;
import com.himamis.retex.renderer.share.commands.CommandBf;
import com.himamis.retex.renderer.share.commands.CommandBigg;
import com.himamis.retex.renderer.share.commands.CommandBigr;
import com.himamis.retex.renderer.share.commands.CommandBinom;
import com.himamis.retex.renderer.share.commands.CommandBold;
import com.himamis.retex.renderer.share.commands.CommandBond;
import com.himamis.retex.renderer.share.commands.CommandBoxed;
import com.himamis.retex.renderer.share.commands.CommandBra;
import com.himamis.retex.renderer.share.commands.CommandBra2;
import com.himamis.retex.renderer.share.commands.CommandBraKet;
import com.himamis.retex.renderer.share.commands.CommandBreve1;
import com.himamis.retex.renderer.share.commands.CommandBreve2;
import com.himamis.retex.renderer.share.commands.CommandBuildRel;
import com.himamis.retex.renderer.share.commands.CommandCE;
import com.himamis.retex.renderer.share.commands.CommandCFrac;
import com.himamis.retex.renderer.share.commands.CommandCancel;
import com.himamis.retex.renderer.share.commands.CommandCedilla;
import com.himamis.retex.renderer.share.commands.CommandCheck;
import com.himamis.retex.renderer.share.commands.CommandChoose;
import com.himamis.retex.renderer.share.commands.CommandClap;
import com.himamis.retex.renderer.share.commands.CommandColonFoo;
import com.himamis.retex.renderer.share.commands.CommandColor;
import com.himamis.retex.renderer.share.commands.CommandColorBox;
import com.himamis.retex.renderer.share.commands.CommandCr;
import com.himamis.retex.renderer.share.commands.CommandCyrDDot;
import com.himamis.retex.renderer.share.commands.CommandDBinom;
import com.himamis.retex.renderer.share.commands.CommandDBox;
import com.himamis.retex.renderer.share.commands.CommandDDDDot;
import com.himamis.retex.renderer.share.commands.CommandDDDot;
import com.himamis.retex.renderer.share.commands.CommandDDot;
import com.himamis.retex.renderer.share.commands.CommandDFrac;
import com.himamis.retex.renderer.share.commands.CommandDeclareMathOperator;
import com.himamis.retex.renderer.share.commands.CommandDefinecolor;
import com.himamis.retex.renderer.share.commands.CommandDisplayMath;
import com.himamis.retex.renderer.share.commands.CommandDisplayStyle;
import com.himamis.retex.renderer.share.commands.CommandDisplaylines;
import com.himamis.retex.renderer.share.commands.CommandDollars;
import com.himamis.retex.renderer.share.commands.CommandDot1;
import com.himamis.retex.renderer.share.commands.CommandDot2;
import com.himamis.retex.renderer.share.commands.CommandDoubleBox;
import com.himamis.retex.renderer.share.commands.CommandEndGroup;
import com.himamis.retex.renderer.share.commands.CommandEquals;
import com.himamis.retex.renderer.share.commands.CommandFBox;
import com.himamis.retex.renderer.share.commands.CommandFColorBox;
import com.himamis.retex.renderer.share.commands.CommandFootnoteSize;
import com.himamis.retex.renderer.share.commands.CommandFrac;
import com.himamis.retex.renderer.share.commands.CommandGenfrac;
import com.himamis.retex.renderer.share.commands.CommandGrave1;
import com.himamis.retex.renderer.share.commands.CommandGrave2;
import com.himamis.retex.renderer.share.commands.CommandGrkAccent;
import com.himamis.retex.renderer.share.commands.CommandH;
import com.himamis.retex.renderer.share.commands.CommandHBox;
import com.himamis.retex.renderer.share.commands.CommandHPhantom;
import com.himamis.retex.renderer.share.commands.CommandHat1;
import com.himamis.retex.renderer.share.commands.CommandHat2;
import com.himamis.retex.renderer.share.commands.CommandHuge1;
import com.himamis.retex.renderer.share.commands.CommandHuge2;
import com.himamis.retex.renderer.share.commands.CommandImageBase64;
import com.himamis.retex.renderer.share.commands.CommandInterText;
import com.himamis.retex.renderer.share.commands.CommandIt;
import com.himamis.retex.renderer.share.commands.CommandJlmCursor;
import com.himamis.retex.renderer.share.commands.CommandJlmSelection;
import com.himamis.retex.renderer.share.commands.CommandJlmXML;
import com.himamis.retex.renderer.share.commands.CommandKet;
import com.himamis.retex.renderer.share.commands.CommandLLap;
import com.himamis.retex.renderer.share.commands.CommandLMR;
import com.himamis.retex.renderer.share.commands.CommandLarge;
import com.himamis.retex.renderer.share.commands.CommandLarge2;
import com.himamis.retex.renderer.share.commands.CommandLarge3;
import com.himamis.retex.renderer.share.commands.CommandLower;
import com.himamis.retex.renderer.share.commands.CommandMBox;
import com.himamis.retex.renderer.share.commands.CommandMap;
import com.himamis.retex.renderer.share.commands.CommandMath;
import com.himamis.retex.renderer.share.commands.CommandMathBf;
import com.himamis.retex.renderer.share.commands.CommandMathBin;
import com.himamis.retex.renderer.share.commands.CommandMathChoice;
import com.himamis.retex.renderer.share.commands.CommandMathClose;
import com.himamis.retex.renderer.share.commands.CommandMathInner;
import com.himamis.retex.renderer.share.commands.CommandMathIt;
import com.himamis.retex.renderer.share.commands.CommandMathOp;
import com.himamis.retex.renderer.share.commands.CommandMathOpen;
import com.himamis.retex.renderer.share.commands.CommandMathOrd;
import com.himamis.retex.renderer.share.commands.CommandMathPunct;
import com.himamis.retex.renderer.share.commands.CommandMathRel;
import com.himamis.retex.renderer.share.commands.CommandMathRing;
import com.himamis.retex.renderer.share.commands.CommandMathRm;
import com.himamis.retex.renderer.share.commands.CommandMathSf;
import com.himamis.retex.renderer.share.commands.CommandMathStyles;
import com.himamis.retex.renderer.share.commands.CommandMathTt;
import com.himamis.retex.renderer.share.commands.CommandMatrix;
import com.himamis.retex.renderer.share.commands.CommandMod;
import com.himamis.retex.renderer.share.commands.CommandMoveLeft;
import com.himamis.retex.renderer.share.commands.CommandMoveRight;
import com.himamis.retex.renderer.share.commands.CommandMulticolumn;
import com.himamis.retex.renderer.share.commands.CommandNewCommand;
import com.himamis.retex.renderer.share.commands.CommandNewEnvironment;
import com.himamis.retex.renderer.share.commands.CommandNormalSize;
import com.himamis.retex.renderer.share.commands.CommandOgonek;
import com.himamis.retex.renderer.share.commands.CommandOoalign;
import com.himamis.retex.renderer.share.commands.CommandOpName;
import com.himamis.retex.renderer.share.commands.CommandOperatorName;
import com.himamis.retex.renderer.share.commands.CommandOvalBox;
import com.himamis.retex.renderer.share.commands.CommandOver;
import com.himamis.retex.renderer.share.commands.CommandOverBrace;
import com.himamis.retex.renderer.share.commands.CommandOverBrack;
import com.himamis.retex.renderer.share.commands.CommandOverLeftArrow;
import com.himamis.retex.renderer.share.commands.CommandOverLeftRightArrow;
import com.himamis.retex.renderer.share.commands.CommandOverParen;
import com.himamis.retex.renderer.share.commands.CommandOverRightArrow;
import com.himamis.retex.renderer.share.commands.CommandOverSet;
import com.himamis.retex.renderer.share.commands.CommandOverline;
import com.himamis.retex.renderer.share.commands.CommandOverwithdelims;
import com.himamis.retex.renderer.share.commands.CommandPMB;
import com.himamis.retex.renderer.share.commands.CommandPMatrix;
import com.himamis.retex.renderer.share.commands.CommandPMod;
import com.himamis.retex.renderer.share.commands.CommandPhantom;
import com.himamis.retex.renderer.share.commands.CommandPod;
import com.himamis.retex.renderer.share.commands.CommandPreScript;
import com.himamis.retex.renderer.share.commands.CommandQuotes;
import com.himamis.retex.renderer.share.commands.CommandR;
import com.himamis.retex.renderer.share.commands.CommandRLap;
import com.himamis.retex.renderer.share.commands.CommandRaise;
import com.himamis.retex.renderer.share.commands.CommandRaiseBox;
import com.himamis.retex.renderer.share.commands.CommandReflectBox;
import com.himamis.retex.renderer.share.commands.CommandRenewCommand;
import com.himamis.retex.renderer.share.commands.CommandRenewEnvironment;
import com.himamis.retex.renderer.share.commands.CommandResizeBox;
import com.himamis.retex.renderer.share.commands.CommandRm;
import com.himamis.retex.renderer.share.commands.CommandRomNum;
import com.himamis.retex.renderer.share.commands.CommandRotateBox;
import com.himamis.retex.renderer.share.commands.CommandSc;
import com.himamis.retex.renderer.share.commands.CommandScaleBox;
import com.himamis.retex.renderer.share.commands.CommandScriptScriptStyle;
import com.himamis.retex.renderer.share.commands.CommandScriptSize;
import com.himamis.retex.renderer.share.commands.CommandScriptStyle;
import com.himamis.retex.renderer.share.commands.CommandSet;
import com.himamis.retex.renderer.share.commands.CommandSf;
import com.himamis.retex.renderer.share.commands.CommandSfrac;
import com.himamis.retex.renderer.share.commands.CommandShadowBox;
import com.himamis.retex.renderer.share.commands.CommandShoveLeft;
import com.himamis.retex.renderer.share.commands.CommandShoveRight;
import com.himamis.retex.renderer.share.commands.CommandSideSet;
import com.himamis.retex.renderer.share.commands.CommandSkew;
import com.himamis.retex.renderer.share.commands.CommandSmall;
import com.himamis.retex.renderer.share.commands.CommandSmash;
import com.himamis.retex.renderer.share.commands.CommandSqrt;
import com.himamis.retex.renderer.share.commands.CommandSt;
import com.himamis.retex.renderer.share.commands.CommandStackBin;
import com.himamis.retex.renderer.share.commands.CommandStackRel;
import com.himamis.retex.renderer.share.commands.CommandSubstack;
import com.himamis.retex.renderer.share.commands.CommandT;
import com.himamis.retex.renderer.share.commands.CommandT2;
import com.himamis.retex.renderer.share.commands.CommandTBinom;
import com.himamis.retex.renderer.share.commands.CommandTFrac;
import com.himamis.retex.renderer.share.commands.CommandText2;
import com.himamis.retex.renderer.share.commands.CommandTextBf;
import com.himamis.retex.renderer.share.commands.CommandTextCircled;
import com.himamis.retex.renderer.share.commands.CommandTextColor;
import com.himamis.retex.renderer.share.commands.CommandTextIt;
import com.himamis.retex.renderer.share.commands.CommandTextRm;
import com.himamis.retex.renderer.share.commands.CommandTextSc;
import com.himamis.retex.renderer.share.commands.CommandTextSf;
import com.himamis.retex.renderer.share.commands.CommandTextStyle;
import com.himamis.retex.renderer.share.commands.CommandTextStyle2;
import com.himamis.retex.renderer.share.commands.CommandTextStyleTeX;
import com.himamis.retex.renderer.share.commands.CommandTextSubscript;
import com.himamis.retex.renderer.share.commands.CommandTextSuperscript;
import com.himamis.retex.renderer.share.commands.CommandTextTt;
import com.himamis.retex.renderer.share.commands.CommandTilde1;
import com.himamis.retex.renderer.share.commands.CommandTilde2;
import com.himamis.retex.renderer.share.commands.CommandTiny1;
import com.himamis.retex.renderer.share.commands.CommandTiny2;
import com.himamis.retex.renderer.share.commands.CommandTt;
import com.himamis.retex.renderer.share.commands.CommandU;
import com.himamis.retex.renderer.share.commands.CommandUnderAccent;
import com.himamis.retex.renderer.share.commands.CommandUnderBrace;
import com.himamis.retex.renderer.share.commands.CommandUnderBrack;
import com.himamis.retex.renderer.share.commands.CommandUnderLeftArrow;
import com.himamis.retex.renderer.share.commands.CommandUnderLeftRightArrow;
import com.himamis.retex.renderer.share.commands.CommandUnderParen;
import com.himamis.retex.renderer.share.commands.CommandUnderRightArrow;
import com.himamis.retex.renderer.share.commands.CommandUnderSet;
import com.himamis.retex.renderer.share.commands.CommandUnderTilde;
import com.himamis.retex.renderer.share.commands.CommandUnderline;
import com.himamis.retex.renderer.share.commands.CommandUnicode;
import com.himamis.retex.renderer.share.commands.CommandVCenter;
import com.himamis.retex.renderer.share.commands.CommandVPhantom;
import com.himamis.retex.renderer.share.commands.CommandVec;
import com.himamis.retex.renderer.share.commands.CommandWideHat;
import com.himamis.retex.renderer.share.commands.CommandWideTilde;
import com.himamis.retex.renderer.share.commands.CommandXCancel;
import com.himamis.retex.renderer.share.commands.CommandXHookLeftArrow;
import com.himamis.retex.renderer.share.commands.CommandXHookRightArrow;
import com.himamis.retex.renderer.share.commands.CommandXLeftArrow;
import com.himamis.retex.renderer.share.commands.CommandXLeftHarpoonDown;
import com.himamis.retex.renderer.share.commands.CommandXLeftHarpoonUp;
import com.himamis.retex.renderer.share.commands.CommandXLeftRightArrow;
import com.himamis.retex.renderer.share.commands.CommandXLeftRightArrows;
import com.himamis.retex.renderer.share.commands.CommandXLeftRightHarpoons;
import com.himamis.retex.renderer.share.commands.CommandXLongEqual;
import com.himamis.retex.renderer.share.commands.CommandXMapsTo;
import com.himamis.retex.renderer.share.commands.CommandXRightArrow;
import com.himamis.retex.renderer.share.commands.CommandXRightHarpoonDown;
import com.himamis.retex.renderer.share.commands.CommandXRightHarpoonUp;
import com.himamis.retex.renderer.share.commands.CommandXRightLeftArrows;
import com.himamis.retex.renderer.share.commands.CommandXRightLeftHarpoons;
import com.himamis.retex.renderer.share.commands.CommandXRightSmallLeftHarpoons;
import com.himamis.retex.renderer.share.commands.CommandXSmallRightLeftHarpoons;
import com.himamis.retex.renderer.share.exception.ParseException;
import com.himamis.retex.renderer.share.platform.font.Font;
import com.himamis.retex.renderer.share.platform.graphics.Color;

public class Commands {

	private static final Map<String, Command> reusableMap = new HashMap<>();

	private static final Command dollar = new CommandDollars.Dollar(true,
			TeXConstants.STYLE_TEXT);
	private static final Command dollardollar = new CommandDollars.Dollar(false,
			TeXConstants.STYLE_DISPLAY);

	private static Command getCommand(String s) {
		Command reusable = getReusableCommand(s);
		if (reusable != null) {
			return reusable;
		}
		return getOneWayCommand(s);
	}

	private static Command getOneWayCommand(String s) {
		Command ret = getOneWayCommandBlock1(s);
		if (ret != null) {
			return ret;
		}
		switch (s) {


		case "sc":
			return new CommandSc();
		case "fbox":
			return new CommandFBox();

		case "boxed":
			return new CommandBoxed();

		case "dbox":
			return new CommandDBox();

		case "fcolorbox":
			return new CommandFColorBox();

		case "colorbox":
			return new CommandColorBox();

		case "fgcolor":
		case "textcolor":
			return new CommandTextColor();

		case "color":
			return new CommandColor();

		case "bgcolor":
			return new CommandBGColor();

		case "definecolor":
			return new CommandDefinecolor();

		case "doublebox":
			return new CommandDoubleBox();

		case "ovalbox":
			return new CommandOvalBox();
		case "shadowbox":
			return new CommandShadowBox();

		case "raisebox":
			return new CommandRaiseBox();

		case "raise":
			return new CommandRaise();

		case "lower":
			return new CommandLower();

		case "moveleft":
			return new CommandMoveLeft();

		case "moveright":
			return new CommandMoveRight();

		case "resizebox":
			return new CommandResizeBox();

		case "scalebox":
			return new CommandScaleBox();

		case "reflectbox":
			return new CommandReflectBox();

		case "rotatebox":
			return new CommandRotateBox();

		case "scriptscriptstyle":
			return new CommandScriptScriptStyle();

		case "textstyle":
			return new CommandTextStyle2();

		case "scriptstyle":
			return new CommandScriptStyle();

		case "displaystyle":
			return new CommandDisplayStyle();

		case "Biggr":
			return new CommandBigr(TeXConstants.TYPE_CLOSING, 4);
		case "biggr":
			return new CommandBigr(TeXConstants.TYPE_CLOSING, 3);
		case "Bigr":
			return new CommandBigr(TeXConstants.TYPE_CLOSING, 2);
		case "bigr":
			return new CommandBigr(TeXConstants.TYPE_CLOSING, 1);
		case "Biggl":
			return new CommandBigr(TeXConstants.TYPE_OPENING, 4);
		case "biggl":
			return new CommandBigr(TeXConstants.TYPE_OPENING, 3);
		case "Bigl":
			return new CommandBigr(TeXConstants.TYPE_OPENING, 2);
		case "bigl":
			return new CommandBigr(TeXConstants.TYPE_OPENING, 1);
		case "Biggm":
			return new CommandBigr(TeXConstants.TYPE_RELATION, 4);
		case "biggm":
			return new CommandBigr(TeXConstants.TYPE_RELATION, 3);
		case "Bigm":
			return new CommandBigr(TeXConstants.TYPE_RELATION, 2);
		case "bigm":
			return new CommandBigr(TeXConstants.TYPE_RELATION, 1);
		case "Bigg":
			return new CommandBigg(4);
		case "bigg":
			return new CommandBigg(3);
		case "Big":
			return new CommandBigg(2);
		case "big":
			return new CommandBigg(1);
		case "mathcal":
			return new CommandTextStyle(TextStyle.MATHCAL);
		case "cal":
			return new CommandTextStyleTeX(TextStyle.MATHCAL);
		case "mathfrak":
			return new CommandTextStyle(TextStyle.MATHFRAK);
		case "frak":
			return new CommandTextStyleTeX(TextStyle.MATHFRAK);
		case "mathbb":
			return new CommandTextStyle(TextStyle.MATHBB);
		case "Bbb":
			return new CommandTextStyleTeX(TextStyle.MATHBB);
		case "mathscr":
			return new CommandTextStyle(TextStyle.MATHSCR);
		case "scr":
			return new CommandTextStyleTeX(TextStyle.MATHSCR);
		case "mathds":
			return new CommandTextStyle(TextStyle.MATHDS);
		case "oldstylenums":
			return new CommandTextStyle(TextStyle.OLDSTYLENUMS);

		case "mathsf":
			return new CommandMathSf();

		case "sf":
			return new CommandSf();

		case "mathrm":
			return new CommandMathRm();

		case "rm":
			return new CommandRm();

		case "mit":
		case "mathit":
			return new CommandMathIt();

		case "it":
			return new CommandIt();

		case "mathtt":
			return new CommandMathTt();

		case "tt":
			return new CommandTt();

		case "mathbf":
			return new CommandMathBf();

		case "bf":
			return new CommandBf();

		case "boldsymbol":
		case "bold":
			return new CommandBold();

		case "undertilde":
			return new CommandUnderTilde();

		case "b":
			return new CommandB();

		case "underaccent":
			return new CommandUnderAccent();

		case "accentset":
			return new CommandAccentSet();

		case "underset":
			return new CommandUnderSet();

		case "overset":
			return new CommandOverSet();

		case "stackbin":
			return new CommandStackBin();

		case "stackrel":
			return new CommandStackRel();
		case "phantom":
			return new CommandPhantom();

		case "vphantom":
			return new CommandVPhantom();

		case "hphantom":
			return new CommandHPhantom();
		case "shoveleft":
			return new CommandShoveLeft();
		case "shoveright":
			return new CommandShoveRight();
		case "begin@math":
			return new CommandMathStyles.OpenBracket(
					TeXConstants.Opener.BEGIN_MATH);
		case "end@math":
			return new CommandMathStyles.CloseBracket(
					TeXConstants.Opener.BEGIN_MATH, TeXConstants.STYLE_TEXT,
					"The command \\) doesn't match any \\(");
		case "[":
			return new CommandMathStyles.OpenBracket(
					TeXConstants.Opener.B_LSQBRACKET);
		case "]":
			return new CommandMathStyles.CloseBracket(
					TeXConstants.Opener.B_LSQBRACKET,
					TeXConstants.STYLE_DISPLAY,
					"The command \\] doesn't match any \\[");

		case "displaymath":
			return new CommandDisplayMath();

		case "(":
			return new CommandMathStyles.OpenBracket(
					TeXConstants.Opener.B_LBRACKET);
		case ")":
			return new CommandMathStyles.CloseBracket(
					TeXConstants.Opener.B_LBRACKET, TeXConstants.STYLE_TEXT,
					"The command \\) doesn't match any \\(");

		case "math":
			return new CommandMath();
		case "multicolumn":
			return new CommandMulticolumn();

		case "intertext":
			return new CommandInterText();

		case "cr":
			return new CommandCr("cr");
		case "smash":
			return new CommandSmash();
		case "mathclose":
			return new CommandMathClose();

		case "mathopen":
			return new CommandMathOpen();

		case "mathbin":
			return new CommandMathBin();

		case "mathinner":
			return new CommandMathInner();

		case "mathord":
			return new CommandMathOrd();

		case "mathpunct":
			return new CommandMathPunct();

		case "mathop":
			return new CommandMathOp();

		case "mathrel":
			return new CommandMathRel();

		case "underline":
			return new CommandUnderline();

		case "overline":
			return new CommandOverline();

		case "overparen":
			return new CommandOverParen();

		case "underparen":
			return new CommandUnderParen();

		case "overbrack":
			return new CommandOverBrack();

		case "underbrack":
			return new CommandUnderBrack();

		case "overbrace":
			return new CommandOverBrace();

		case "underbrace":
			return new CommandUnderBrace();

		case "prescript":
			return new CommandPreScript();

		case "sideset":
			return new CommandSideSet();

		case "xmapsto":
			return new CommandXMapsTo();

		case "xlongequal":
			return new CommandXLongEqual();

		case "xrightarrow":
			return new CommandXRightArrow();

		case "xleftarrow":
			return new CommandXLeftArrow();

		case "xhookleftarrow":
			return new CommandXHookLeftArrow();

		case "xhookrightarrow":
			return new CommandXHookRightArrow();

		case "xleftrightarrow":
			return new CommandXLeftRightArrow();

		case "xrightharpoondown":
			return new CommandXRightHarpoonDown();

		case "xrightharpoonup":
			return new CommandXRightHarpoonUp();

		case "xleftharpoondown":
			return new CommandXLeftHarpoonDown();

		case "xleftharpoonup":
			return new CommandXLeftHarpoonUp();

		case "xleftrightharpoons":
			return new CommandXLeftRightHarpoons();

		case "xrightleftharpoons":
			return new CommandXRightLeftHarpoons();

		case "xrightsmallleftharpoons":
			return new CommandXRightSmallLeftHarpoons();

		case "xsmallrightleftharpoons":
			return new CommandXSmallRightLeftHarpoons();

		case "xleftrightarrows":
			return new CommandXLeftRightArrows();

		case "xrightleftarrows":
			return new CommandXRightLeftArrows();

		case "underleftrightarrow":
			return new CommandUnderLeftRightArrow();

		case "underleftarrow":
			return new CommandUnderLeftArrow();

		case "underrightarrow":
			return new CommandUnderRightArrow();

		case "overleftrightarrow":
			return new CommandOverLeftRightArrow();

		case "overleftarrow":
			return new CommandOverLeftArrow();

		case "overrightarrow":
			return new CommandOverRightArrow();

		case "ogonek":
			return new CommandOgonek();
		case "k":
			return new CommandOgonek();
		case "cedilla":
			return new CommandCedilla();
		case "c":
			return new CommandCedilla();

		case "~":
			return new CommandTilde1();

		case "tilde":
			return new CommandTilde2();

		case "widetilde":
			return new CommandWideTilde();

		case "'":
			return new CommandAcute1();

		case "acute":
			return new CommandAcute2();

		case "skew":
			return new CommandSkew();

		case "^":
			return new CommandHat1();

		case "hat":
			return new CommandHat2();

		case "widehat":
			return new CommandWideHat();

		case "\"":
			return new CommandQuotes();

		case "ddot":
			return new CommandDDot();

		case "dddot":
			return new CommandDDDot();

		case "ddddot":
			return new CommandDDDDot();

		case "`":
			return new CommandGrave1();

		case "grave":
			return new CommandGrave2();

		case "=":
			return new CommandEquals();

		case "bar":
			return new CommandBar();

		case ".":
			return new CommandDot1();

		case "dot":
			return new CommandDot2();

		case "cyrddot":
			return new CommandCyrDDot();

		case "u":
			return new CommandBreve1();

		case "breve":
			return new CommandBreve2();

		case "v":
			return new CommandCheck();

		case "check":
			return new CommandMap();

		case "H":
			return new CommandH();

		case "t":
			return new CommandT2();

		case "r":
			return new CommandR();

		case "mathring":
			return new CommandMathRing();

		case "U":
			return new CommandU();

		case "vec":
			return new CommandVec();

		case "accent":
			return new CommandAccent();

		case "grkaccent":
			return new CommandGrkAccent();
		case "mbox":
			return new CommandMBox();

		case "textsuperscript":
			return new CommandTextSuperscript();

		case "textsubscript":
			return new CommandTextSubscript();

		case "text":
			return new CommandText2();

		case "pmb":
			return new CommandPMB();

		case "textbf":
			return new CommandTextBf();

		case "textit":
			return new CommandTextIt();

		case "textrm":
			return new CommandTextRm();

		case "textsf":
			return new CommandTextSf();

		case "texttt":
			return new CommandTextTt();

		case "textsc":
			return new CommandTextSc();

		case "operatorname":
			return new CommandOperatorName();

		case "sfrac":
			return new CommandSfrac();

		case "cfrac":
			return new CommandCFrac();
		case "log":
		case "lg":
		case "ln":
		case "sin":
		case "arcsin":
		case "sinh":
		case "cos":
		case "arccos":
		case "cosh":
		case "cot":
		case "arccot":
		case "coth":
		case "tan":
		case "arctan":
		case "tanh":
		case "sec":
		case "arcsec":
		case "sech":
		case "csc":
		case "arccsc":
		case "csch":
		case "arg":
		case "ker":
		case "dim":
		case "hom":
		case "exp":
		case "deg":
			return new CommandOpName(s, false);

		case "lim":
		case "max":
		case "min":
		case "sup":
		case "inf":
		case "det":
		case "Pr":
		case "gcd":
			return new CommandOpName(s, true);

		case "limsup":
			return new CommandOpName("lim", "sup", true);
		case "liminf":
			return new CommandOpName("lim", "inf", true);
		case "injlim":
			return new CommandOpName("inj", "lim", true);
		case "projlim":
			return new CommandOpName("proj", "lim", true);

		// XXX
		// case "copyright": return
		// new Replacement("\\textcircled{\\raisebox{0.2ex}{c}}");

		// TODO: check if this is useful or not
		case "jlmXML":
			return new CommandJlmXML();

		// caret for the editor
		case "jlmcursor":
			return new CommandJlmCursor();
		// for the editor
		case "jlmselection":
			return new CommandJlmSelection();

		// eg
		// \imagebasesixtyfour{40}{36}{data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACgAAAAkCAIAAAB0Xu9BAAAAKUlEQVR42u3NMQEAAAwCIPuX1hbbAwVIn0QsFovFYrFYLBaLxWKx+M4AoNrQEWa6zscAAAAASUVORK5CYII=}
		case "imagebasesixtyfour":
			return new CommandImageBase64();

		}
		String replace = getReplacement(s);
		if (replace != null) {
			return new Replacement(replace);
		}
		return null;
	}

	private static Command getOneWayCommandBlock1(String s) {
		switch (s) {
		case "ce":
			return new CommandCE();

		case "bond":
			return new CommandBond();

		case "hbox":
			return new CommandHBox();

		case "cancel":
			return new CommandCancel();

		case "bcancel":
			return new CommandBCancel();

		case "xcancel":
			return new CommandXCancel();

		case "mathchoice":
			return new CommandMathChoice();

		case "pod":
			return new CommandPod();
		case "pmod":
			return new CommandPMod();

		case "mod":
			return new CommandMod();

		case "begingroup":
			return new CommandBeginGroup();

		case "endgroup":
			return new CommandEndGroup();

		case "DeclareMathOperator":
			return new CommandDeclareMathOperator();

		case "newcommand":
			return new CommandNewCommand();

		case "renewcommand":
			return new CommandRenewCommand();

		case "newenvironment":
			return new CommandNewEnvironment();

		case "renewenvironment":
			return new CommandRenewEnvironment();

		case "left":
			return new CommandLMR.CommandLeft();
		case "right":
			return new CommandLMR.CommandRight();
		case "middle":
			return new CommandLMR.CommandMiddle();
		// stretchy versions
		case "Braket":
			return new CommandBra(Symbols.LANGLE, Symbols.RANGLE);
		case "Bra":
			return new CommandBra(Symbols.LANGLE, Symbols.VERT);
		case "Ket":
			return new CommandBra(Symbols.VERT, Symbols.RANGLE);
		case "Set":
			return new CommandBra(Symbols.LBRACE, Symbols.RBRACE);
		case "braket":
			return new CommandBraKet();

		// non-stretchy versions
		case "bra":
			return new CommandBra2();
		case "ket":
			return new CommandKet();
		case "set":
			return new CommandSet();
		case "vcenter":
			return new CommandVCenter();
		case "frac":
			return new CommandFrac();

		case "genfrac":
			return new CommandGenfrac();

		case "dfrac":
			return new CommandDFrac();

		case "tfrac":
			return new CommandTFrac();
		case "dbinom":
			return new CommandDBinom();

		case "tbinom":
			return new CommandTBinom();

		case "binom":
			return new CommandBinom();

		case "over":
			return new CommandOver();

		case "buildrel":
			return new CommandBuildRel();

		case "choose":
			return new CommandChoose(Symbols.LBRACK, Symbols.RBRACK);
		case "brace":
			return new CommandChoose(Symbols.LBRACE, Symbols.RBRACE);
		case "bangle":
			return new CommandChoose(Symbols.LANGLE, Symbols.RANGLE);
		case "brack":
			return new CommandChoose(Symbols.LSQBRACK, Symbols.RSQBRACK);

		case "overwithdelims":
			return new CommandOverwithdelims();
		case "atopwithdelims":
			return new CommandATopwithdelims();
		case "abovewithdelims":
			return new CommandAbovewithdelims();

		case "above":
			return new CommandAbove();
		case "atop":
			return new CommandATop();
		case "sqrt":
			return new CommandSqrt();
		case "st":
			return new CommandSt();
		case "mathclap":
		case "clap":
			return new CommandClap();

		case "mathrlap":
		case "rlap":
			return new CommandRLap();

		case "mathllap":
		case "llap":
			return new CommandLLap();

		case "begin":
			return new CommandBE.Begin();
		case "end":
			return new CommandBE.End();
		case "begin@array":
			return new EnvArray.Begin(ArrayTypes.ARRAY);
		case "end@array":
			return new EnvArray.End(ArrayTypes.ARRAY);
		case "begin@tabular":
			return new EnvArray.Begin(ArrayTypes.TABULAR);
		case "end@tabular":
			return new EnvArray.End(ArrayTypes.TABULAR);
		case "\\":
			return new CommandCr("\\");
		case "begin@eqnarray":
			return new EnvArray.Begin(ArrayTypes.EQNARRAY,
					new ArrayOptions(3).addAlignment(TeXConstants.Align.RIGHT)
							.addAlignment(TeXConstants.Align.CENTER)
							.addAlignment(TeXConstants.Align.LEFT).close());
		case "end@eqnarray":
			return new EnvArray.End(ArrayTypes.EQNARRAY);
		case "begin@split":
			return new EnvArray.Begin(ArrayTypes.SPLIT,
					new ArrayOptions(2).addAlignment(TeXConstants.Align.RIGHT)
							.addAlignment(TeXConstants.Align.LEFT).close());
		case "end@split":
			return new EnvArray.End(ArrayTypes.SPLIT);
		case "begin@cases":
			return new EnvArray.Begin(ArrayTypes.CASES,
					new ArrayOptions(3).addAlignment(TeXConstants.Align.LEFT)
							.addSeparator(
									new SpaceAtom(TeXConstants.Muskip.NEGTHIN))
							.addAlignment(TeXConstants.Align.LEFT).close());
		case "end@cases":
			return new EnvArray.End(ArrayTypes.CASES);

		case "matrix":
		case "array":
			return new CommandMatrix();

		case "ooalign":
			return new CommandOoalign();
		case "pmatrix":
			return new CommandPMatrix();

		case "begin@matrix":
			return new EnvArray.Begin(ArrayTypes.MATRIX,
					ArrayOptions.getEmpty());
		case "end@matrix":
			return new EnvArray.End(ArrayTypes.MATRIX);
		case "begin@smallmatrix":
			return new EnvArray.Begin(ArrayTypes.SMALLMATRIX,
					ArrayOptions.getEmpty());
		case "end@smallmatrix":
			return new EnvArray.End(ArrayTypes.SMALLMATRIX);
		case "begin@align":
			return new EnvArray.Begin(ArrayTypes.ALIGN,
					ArrayOptions.getEmpty());
		case "end@align":
			return new EnvArray.End(ArrayTypes.ALIGN);
		case "begin@aligned":
			return new EnvArray.Begin(ArrayTypes.ALIGNED,
					ArrayOptions.getEmpty());
		case "end@aligned":
			return new EnvArray.End(ArrayTypes.ALIGNED);
		case "begin@flalign":
			return new EnvArray.Begin(ArrayTypes.FLALIGN,
					ArrayOptions.getEmpty());
		case "end@flalign":
			return new EnvArray.End(ArrayTypes.FLALIGN);
		case "begin@alignat":
			return new EnvArray.Begin(ArrayTypes.ALIGNAT,
					ArrayOptions.getEmpty());
		case "end@alignat":
			return new EnvArray.End(ArrayTypes.ALIGNAT);
		case "begin@alignedat":
			return new EnvArray.Begin(ArrayTypes.ALIGNEDAT,
					ArrayOptions.getEmpty());
		case "end@alignedat":
			return new EnvArray.End(ArrayTypes.ALIGNEDAT);
		case "begin@multline":
			return new EnvArray.Begin(ArrayTypes.MULTILINE, ArrayOptions.getEmpty());
		case "end@multline":
			return new EnvArray.End(ArrayTypes.MULTILINE);
		case "begin@subarray":
			return new EnvArray.Begin(ArrayTypes.SUBARRAY);
		case "end@subarray":
			return new EnvArray.End(ArrayTypes.SUBARRAY);
		case "substack":
			return new CommandSubstack();
		case "displaylines":
			return new CommandDisplaylines();
		case "begin@gather":
			return new EnvArray.Begin(ArrayTypes.GATHER, ArrayOptions.getEmpty());
		case "end@gather":
			return new EnvArray.End(ArrayTypes.GATHER);
		case "begin@gathered":
			return new EnvArray.Begin(ArrayTypes.GATHERED, ArrayOptions.getEmpty());
		case "end@gathered":
			return new EnvArray.End(ArrayTypes.GATHERED);
		case "begin@pmatrix":
			return new EnvArray.Begin(ArrayTypes.PMATRIX,
					ArrayOptions.getEmpty());
		case "end@pmatrix":
			return new EnvArray.End(ArrayTypes.PMATRIX, "lbrack", "rbrack");
		case "begin@bmatrix":
			return new EnvArray.Begin(ArrayTypes.BMATRIX,
					ArrayOptions.getEmpty());
		case "end@bmatrix":
			return new EnvArray.End(ArrayTypes.BMATRIX, "lsqbrack", "rsqbrack");
		case "begin@vmatrix":
			return new EnvArray.Begin(ArrayTypes.BMATRIX,
					ArrayOptions.getEmpty());
		case "end@vmatrix":
			return new EnvArray.End(ArrayTypes.BMATRIX, "vert");
		case "begin@Vmatrix":
			return new EnvArray.Begin(ArrayTypes.VMATRIX,
					ArrayOptions.getEmpty());
		case "end@Vmatrix":
			return new EnvArray.End(ArrayTypes.VMATRIX, "Vert");
		case "textcircled":
			return new CommandTextCircled();

		case "romannumeral":
			return new CommandRomNum(false);
		case "Romannumeral":
			return new CommandRomNum(true);
		case "T":
			return new CommandT();
		case "unicode":
			return new CommandUnicode();
		case "coloncolonapprox":
			return new CommandColonFoo.ColonColonFoo("approx");
		case "colonapprox":
			return new CommandColonFoo.ColonFoo("approx");
		case "coloncolonsim":
			return new CommandColonFoo.ColonColonFoo("sim");
		case "colonsim":
			return new CommandColonFoo.ColonFoo("sim");
		case "coloncolon":
			return new CommandColonFoo.ColonColonFoo();
		case "coloncolonequals":
			return new CommandColonFoo.ColonColonFoo("equals");
		case "colonequals":
			return new CommandColonFoo.ColonFoo("equals");
		case "coloncolonminus":
			return new CommandColonFoo.ColonColonFoo("minus");
		case "colonminus":
			return new CommandColonFoo.ColonFoo("minus");
		case "equalscoloncolon":
			return new CommandColonFoo.FooColonColon("equals");
		case "equalscolon":
			return new CommandColonFoo.FooColon("equals");
		case "minuscoloncolon":
			return new CommandColonFoo.FooColonColon("minus");
		case "minuscolon":
			return new CommandColonFoo.FooColon("minus");
		case "simcoloncolon":
			return new CommandColonFoo.FooColonColon("sim");
		case "simcolon":
			return new CommandColonFoo.FooColon("sim");
		case "approxcoloncolon":
			return new CommandColonFoo.FooColonColon("approx");
		case "approxcolon":
			return new CommandColonFoo.FooColon("approx");
		case "tiny":
			return new CommandTiny1();

		case "Tiny":
			return new CommandTiny2();

		case "scriptsize":
			return new CommandScriptSize();

		case "footnotesize":
			return new CommandFootnoteSize();

		case "small":
			return new CommandSmall();

		case "normalsize":
			return new CommandNormalSize();

		case "large":
			return new CommandLarge();

		case "Large":
			return new CommandLarge2();

		case "LARGE":
			return new CommandLarge3();

		case "huge":
			return new CommandHuge1();

		case "Huge":
			return new CommandHuge2();
		}
		return null;
	}

	private static String getReplacement(String s) {
		switch (s) {
		case "&":
			return "\\textampersand";
		case "%":
			return "\\textpercent";

		case "$":
		case "dollar":
			return "\\textdollar";
		case "iff":
			return "\\mathrel{\\;\\Longleftrightarrow\\;}";
		case "bowtie":
			return "\\mathrel{\\mathrel{\\triangleright}\\joinrel\\mathrel{\\triangleleft}}";
		case "models":
			return "\\mathrel{\\mathrel{\\vert}\\joinrel\\equals}";
		case "implies":
			return "\\mathrel{\\;\\Longrightarrow\\;}";
		case "impliedby":
			return "\\mathrel{\\;\\Longleftarrow\\;}";
		case "mapsto":
			return "\\mathrel{\\mapstochar\\rightarrow}";
		case "longmapsto":
			return "\\mathrel{\\mapstochar\\longrightarrow}";
		case "Mapsto":
			return "\\mathrel{\\Mapstochar\\Rightarrow}";
		case "mapsfrom":
			return "\\mathrel{\\leftarrow\\mapsfromchar}";
		case "Mapsfrom":
			return "\\mathrel{\\Leftarrow\\Mapsfromchar}";
		case "Longmapsto":
			return "\\mathrel{\\Mapstochar\\Longrightarrow}";
		case "longmapsfrom":
			return "\\mathrel{\\longleftarrow\\mapsfromchar}";
		case "Longmapsfrom":
			return "\\mathrel{\\Longleftarrow\\Mapsfromchar}";
		case "arrowvert":
			return "\\vert";
		case "Arrowvert":
			return "\\Vert";
		case "aa":
			return "\\mathring{a}";
		case "AA":
			return "\\mathring{A}";
		case "ddag":
			return "\\ddagger";
		case "dag":
			return "\\dagger";
		case "Doteq":
			return "\\doteqdot";
		case "doublecup":
			return "\\Cup";
		case "doublecap":
			return "\\Cap";
		case "llless":
			return "\\lll";
		case "gggtr":
			return "\\ggg";

		case "Alpha":
		case "Beta":
		case "Epsilon":
		case "Zeta":
		case "Eta":
		case "Iota":
		case "Kappa":
		case "Mu":
		case "Nu":
		case "Omicron":
		case "Rho":
		case "Tau":
		case "Chi":
			char ch = s.charAt(0);
			if (ch == 'E' && "Eta".equals(s)) {
				// Eta not Epsilon
				ch = 'H';
			} else if (ch == 'C') {
				// Chi
				ch = 'X';
			} else if (ch == 'R') {
				// Rho
				ch = 'P';
			}
			return "\\mathord{\\mathrm{" + ch + "}}";

		case "hdots":
			return "\\ldots";
		case "restriction":
			return "\\upharpoonright";
		case "celsius":
			return "\\mathord{{}^\\circ\\mathrm{C}}";
		case "micro":
			return "\\textmu";
		case "marker":
			return "{\\kern{0.25ex}\\rule{0.5ex}{1.2ex}\\kern{0.25ex}}";
		case "hybull":
			return "\\rule[0.6ex]{1ex}{0.2ex}";
		case "block":
			return "\\rule{1ex}{1.2ex}";
		case "uhblk":
			return "\\rule[0.6ex]{1ex}{0.6ex}";
		case "lhblk":
			return "\\rule{1ex}{0.6ex}";
		case "lVert":
			return "\\Vert";
		case "rVert":
			return "\\Vert";
		case "lvert":
			return "\\vert";
		case "rvert":
			return "\\vert";
		case "glj":
			return "\\mathbin{\\rlap{>}\\!<}";
		case "gla":
			return "\\mathbin{><}";
		case "alef":
			return "\\aleph";
		case "alefsym":
			return "\\aleph";
		case "And":
			return "{\\;\\textampersand\\;}";
		case "and":
			return "\\land";
		case "ang":
			return "\\angle";
		case "exist":
			return "\\exists";
		case "hAar":
			return "\\Leftrightarrow";

		case "C":
		case "Complex":
		case "N":
		case "natnums":
		case "Q":
		case "R":
		case "Reals":
		case "real":
		case "reals":
		case "Z":
			return "\\mathbb{" + (s.charAt(0) + "").toUpperCase(Locale.US)
					+ "}";
			
		case "Dagger":
			return "\\ddagger";
		case "diamonds":
			return "\\diamondsuit";
		case "clubs":
			return "\\clubsuit";
		case "hearts":
			return "\\heartsuit";
		case "spades":
			return "\\spadesuit";
		case "infin":
			return "\\infty";
		case "isin":
			return "\\in";
		case "plusmn":
			return "\\pm";
		case "sube":
			return "\\subseteq";
		case "supe":
			return "\\supseteq";
		case "sdot":
			return "\\cdot";

		case "empty":
		case "O":
			return "\\emptyset";

		case "sub":
			return "\\subset";
		case "lang":
			return "\\langle";
		case "rang":
			return "\\rangle";
		case "bull":
			return "\\bullet";

		case "geneuro":
		case "geneuronarrow":
		case "geneurowide":
			return "\\texteuro";
		}
		return null;
	}

	private static Command0A getReusableCommand(String s) {

		switch (s) {

		// XXX
		// case "usepackage": return new CommandUsePackage();

		case "bmod":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					final Atom sp = new SpaceAtom(Unit.MU, 5.);
					final RowAtom ra = new RowAtom(sp,
							new RomanAtom(TeXParser
									.getAtomForLatinStr("mod", true).changeType(
											TeXConstants.TYPE_BINARY_OPERATOR)),
							sp);
					return ra;
				}
			};
		case "hookrightarrow":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					final RowAtom ra = new RowAtom(3);
					// XXX was -0.169
					ra.add(Symbols.LHOOK,
							new SpaceAtom(Unit.EM, -0.43),
							Symbols.RIGHTARROW);
					ra.setShape(true);
					return new TypedAtom(TeXConstants.TYPE_RELATION, ra);
				}
			};
		case "hookleftarrow":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					final RowAtom ra = new RowAtom(3);
					ra.add(Symbols.LEFTARROW,
							// XXX was -0.169
							new SpaceAtom(Unit.EM, -0.43),
							Symbols.RHOOK);
					ra.setShape(true);
					return new TypedAtom(TeXConstants.TYPE_RELATION, ra);
				}
			};
		case "Longrightarrow":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					final RowAtom ra = new RowAtom(3);
					ra.add(Symbols.BIG_RELBAR,
							new SpaceAtom(Unit.EM, -0.177),
							Symbols.BIG_RIGHTARROW);
					ra.setShape(true);
					return new TypedAtom(TeXConstants.TYPE_RELATION, ra);
				}
			};
		case "Longleftarrow":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					final RowAtom ra = new RowAtom(3);
					ra.add(Symbols.BIG_LEFTARROW,
							new SpaceAtom(Unit.EM, -0.177),
							Symbols.BIG_RELBAR);
					ra.setShape(true);
					return new TypedAtom(TeXConstants.TYPE_RELATION, ra);
				}
			};
		case "longleftarrow":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					final RowAtom ra = new RowAtom(3);
					ra.add(Symbols.LEFTARROW,
							new SpaceAtom(Unit.EM, -0.206),
							Symbols.MINUS
									.changeType(TeXConstants.TYPE_RELATION));
					ra.setShape(true);
					return new TypedAtom(TeXConstants.TYPE_RELATION, ra);
				}
			};
		case "longrightarrow":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					final RowAtom ra = new RowAtom(3);
					ra.add(Symbols.MINUS.changeType(TeXConstants.TYPE_RELATION),
							new SpaceAtom(Unit.EM, -0.206),
							Symbols.RIGHTARROW);
					ra.setShape(true);
					return new TypedAtom(TeXConstants.TYPE_RELATION, ra);
				}
			};
		case "longleftrightarrow":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					final RowAtom ra = new RowAtom(3);
					ra.add(Symbols.LEFTARROW,
							new SpaceAtom(Unit.EM, -0.180),
							Symbols.RIGHTARROW);
					ra.setShape(true);
					return new TypedAtom(TeXConstants.TYPE_RELATION, ra);
				}
			};
		case "Longleftrightarrow":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					final RowAtom ra = new RowAtom(3);
					ra.add(Symbols.BIG_LEFTARROW,
							new SpaceAtom(Unit.EM, -0.180),
							Symbols.BIG_RIGHTARROW);
					ra.setShape(true);
					return new TypedAtom(TeXConstants.TYPE_RELATION, ra);
				}
			};

		case "nbsp":
		case "nobreaskspace":
		case "space":
		case " ":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new SpaceAtom();
				}
			};

		case "{":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return Symbols.LBRACE;
				}
			};
		case "}":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return Symbols.RBRACE;
				}
			};
		case "nolimits":
			return new Command0AImpl() {
				@Override
				public boolean init(TeXParser tp) {
					final Atom a = tp.getLastAtom();
					if (a != null) {
						tp.addToConsumer(
								a.changeLimits(TeXConstants.SCRIPT_NOLIMITS));
					}
					return false;
				}
			};
		case "limits":
			return new Command0AImpl() {
				@Override
				public boolean init(TeXParser tp) {
					final Atom a = tp.getLastAtom();
					if (a != null) {
						tp.addToConsumer(
								a.changeLimits(TeXConstants.SCRIPT_LIMITS));
					}
					return false;
				}
			};
		case "normal":
			return new Command0AImpl() {
				@Override
				public boolean init(TeXParser tp) {
					final Atom a = tp.getLastAtom();
					if (a != null) {
						tp.addToConsumer(
								a.changeLimits(TeXConstants.SCRIPT_NORMAL));
					}
					return false;
				}
			};
		case "surd":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new VCenteredAtom(SymbolAtom.get("surdsign"));
				}
			};

		case "int":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return Symbols.INT;
				}
			};
		case "intop":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return Symbols.INTOP;
				}
			};
		case "oint":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return Symbols.OINT;
				}
			};
		case "iint":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					final Atom integral = Symbols.INT;
					final SpaceAtom six = new SpaceAtom(Unit.MU, -6.,
							0., 0.);
					final SpaceAtom nine = new SpaceAtom(Unit.MU, -9.,
							0., 0.);
					final Atom choice = new MathchoiceAtom(nine, six, six, six);
					final RowAtom ra = new RowAtom(integral, choice, integral);
					ra.lookAtLastAtom = true;
					return ra.changeType(TeXConstants.TYPE_BIG_OPERATOR);
				}
			};
		case "iiint":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					final Atom integral = Symbols.INT;
					final SpaceAtom six = new SpaceAtom(Unit.MU, -6.,
							0., 0.);
					final SpaceAtom nine = new SpaceAtom(Unit.MU, -9.,
							0., 0.);
					final Atom choice = new MathchoiceAtom(nine, six, six, six);
					final RowAtom ra = new RowAtom(integral, choice, integral,
							choice, integral);
					ra.lookAtLastAtom = true;
					return ra.changeType(TeXConstants.TYPE_BIG_OPERATOR);
				}
			};
		case "iiiint":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					final Atom integral = Symbols.INT;
					final SpaceAtom six = new SpaceAtom(Unit.MU, -6.,
							0., 0.);
					final SpaceAtom nine = new SpaceAtom(Unit.MU, -9.,
							0., 0.);
					final Atom choice = new MathchoiceAtom(nine, six, six, six);
					final RowAtom ra = new RowAtom(integral, choice, integral,
							choice, integral, choice, integral);
					ra.lookAtLastAtom = true;
					return ra.changeType(TeXConstants.TYPE_BIG_OPERATOR);
				}
			};
		case "idotsint":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					final Atom integral = Symbols.INT;
					final Atom cdotp = Symbols.CDOTP;
					final SpaceAtom sa = new SpaceAtom(Unit.MU, -1.,
							0., 0.);
					final RowAtom cdots = new RowAtom(cdotp, cdotp, cdotp);
					final RowAtom ra = new RowAtom(integral, sa,
							cdots.changeType(TeXConstants.TYPE_INNER), sa,
							integral);
					ra.lookAtLastAtom = true;
					return ra.changeType(TeXConstants.TYPE_BIG_OPERATOR);
				}
			};
		case "lmoustache":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					final Atom at = new BigDelimiterAtom(
							SymbolAtom.get("lmoustache"), 1);
					at.setType(TeXConstants.TYPE_OPENING);
					return at;
				}
			};
		case "rmoustache":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					final Atom at = new BigDelimiterAtom(
							SymbolAtom.get("rmoustache"), 1);
					at.setType(TeXConstants.TYPE_CLOSING);
					return at;
				}
			};
		case "-":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return BreakMarkAtom.get();
				}
			};


		case "fcscore":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					final int arg = tp.getArgAsPositiveInteger();
					return FcscoreAtom.get(arg);
				}
			};
		case "longdiv":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					final long dividend = tp.getArgAsPositiveInteger();
					final long divisor = tp.getArgAsPositiveInteger();
					return new LongdivAtom(divisor, dividend, tp);
				}
			};


		case "includegraphics":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					final Map<String, String> arg1 = tp.getOptionAsMap();
					final String arg2 = tp.getArgAsString();
					return new GraphicsAtom(arg2, arg1);
				}
			};



		case "thinspace":
		case ",":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new SpaceAtom(TeXConstants.Muskip.THIN);
				}
			};

		case "medspace":
		case ">":
		case ":":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new SpaceAtom(TeXConstants.Muskip.MED);
				}
			};

		case "thickspace":
		case ";":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new SpaceAtom(TeXConstants.Muskip.THICK);
				}
			};

		case "negthinspace":
		case "!":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new SpaceAtom(TeXConstants.Muskip.NEGTHIN);
				}
			};

		case "negmedspace":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new SpaceAtom(TeXConstants.Muskip.NEGMED);
				}
			};
		case "negthickspace":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new SpaceAtom(TeXConstants.Muskip.NEGTHICK);
				}
			};
		case "enspace":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new SpaceAtom(Unit.EM, 0.5, 0., 0.);
				}
			};
		case "enskip":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new SpaceAtom(Unit.EM, 0.5, 0., 0.);
				}
			};
		case "quad":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new SpaceAtom(Unit.EM, 1., 0., 0.);
				}
			};
		case "qquad":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new SpaceAtom(Unit.EM, 2., 0., 0.);
				}
			};
		case "Space":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new SpaceAtom(Unit.EM, 3., 0., 0.);
				}
			};


		case "char":
			return new Command0AImpl() {
				@Override
				public boolean init(TeXParser tp) {
					final int c = tp.getArgAsCharFromCode();
					if (c == 0) {
						throw new ParseException(tp,
								"Invalid character in \\char: 0.");
					}
					if (c <= 0xFFFF) {
						final char cc = (char) c;
						if ((cc >= '0' && cc <= '9') || (cc >= 'a' && cc <= 'z')
								|| (cc >= 'A' && cc <= 'Z')) {
							tp.convertASCIIChar(cc, true);
						} else {
							tp.convertCharacter(cc, true);
						}
					} else {
						tp.convertCharacter(c);
					}
					return false;
				}
			};

		case "kern":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					return new SpaceAtom(tp.getArgAsLength());
				}
			};
		case "Dstrok":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					final RowAtom ra = new RowAtom(
							new SpaceAtom(Unit.EX, -0.1, 0., 0.),
							Symbols.BAR);
					final VRowAtom vra = new VRowAtom(new LapedAtom(ra, 'r'));
					vra.setRaise(Unit.EX, -0.55);
					return new RowAtom(vra, new RomanAtom(
							new CharAtom('D', TextStyle.MATHNORMAL)));
				}
			};
		case "dstrok":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					final RowAtom ra = new RowAtom(
							new SpaceAtom(Unit.EX, 0.25, 0., 0.),
							Symbols.BAR);
					final VRowAtom vra = new VRowAtom(new LapedAtom(ra, 'r'));
					vra.setRaise(Unit.EX, -0.1);
					return new RowAtom(vra, new RomanAtom(
							new CharAtom('d', TextStyle.MATHNORMAL)));
				}
			};
		case "Hstrok":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					final RowAtom ra = new RowAtom(
							new SpaceAtom(Unit.EX, 0.28, 0., 0.),
							Symbols.TEXTENDASH);
					final VRowAtom vra = new VRowAtom(new LapedAtom(ra, 'r'));
					vra.setRaise(Unit.EX, 0.55);
					return new RowAtom(vra, new RomanAtom(
							new CharAtom('H', TextStyle.MATHNORMAL)));
				}
			};
		case "hstrok":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					final RowAtom ra = new RowAtom(
							new SpaceAtom(Unit.EX, -0.1, 0., 0.),
							Symbols.BAR);
					final VRowAtom vra = new VRowAtom(new LapedAtom(ra, 'r'));
					vra.setRaise(Unit.EX, -0.1);
					return new RowAtom(vra, new RomanAtom(
							new CharAtom('h', TextStyle.MATHNORMAL)));
				}
			};
		case "smallfrowneq":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					final Atom at = new UnderOverAtom(Symbols.EQUALS,
							Symbols.SMALLFROWN,
							new TeXLength(Unit.MU, -2.), true, true);
					return at.changeType(TeXConstants.TYPE_RELATION);
				}
			};
		case "frowneq":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					final Atom at = new UnderOverAtom(Symbols.EQUALS,
							Symbols.FROWN, TeXLength.getZero(), true, true);
					return at.changeType(TeXConstants.TYPE_RELATION);
				}
			};

		case "geoprop":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					final SymbolAtom nd = Symbols.NORMALDOT;
					final RowAtom ddot = new RowAtom(nd,
							new SpaceAtom(Unit.MU, 4., 0., 0.), nd);
					final TeXLength l = new TeXLength(Unit.MU, -3.4);
					Atom at = new UnderOverAtom(Symbols.MINUS, ddot, l, false,
							ddot, l, false);
					return at.changeType(TeXConstants.TYPE_RELATION);
				}
			};
		case "ratio":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					final Atom a = new VCenteredAtom(Symbols.COLON
							.changeType(TeXConstants.TYPE_ORDINARY));
					return a.changeType(TeXConstants.TYPE_RELATION);
				}
			};
		case "dotminus":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					Atom at = new UnderOverAtom(Symbols.MINUS,
							Symbols.NORMALDOT,
							new TeXLength(Unit.EX, -0.4), false,
							true);
					return at.changeType(TeXConstants.TYPE_BINARY_OPERATOR);
				}
			};


		case "hline":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					if (!tp.isArrayMode()) {
						throw new ParseException(tp,
								"The macro \\hline is only available in array mode !");
					}
					return new HlineAtom();
				}
			};
		case "cellcolor":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					if (!tp.isArrayMode()) {
						throw new ParseException(tp,
								"The macro \\cellcolor is only available in array mode !");
					}
					final Color c = CommandDefinecolor.getColor(tp);
					return new EnvArray.CellColor(c);
				}
			};
		case "rowcolor":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					if (!tp.isArrayMode()) {
						throw new ParseException(tp,
								"The macro \\rowcolor is only available in array mode !");
					}
					final Color c = CommandDefinecolor.getColor(tp);
					return new EnvArray.RowColor(c);
				}
			};
		case "jlmText":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					final String arg = tp.getGroupAsArgument();
					return new JavaFontRenderingAtom(arg, Font.PLAIN);
				}
			};
		case "jlmTextit":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					final String arg = tp.getGroupAsArgument();
					return new JavaFontRenderingAtom(arg, Font.ITALIC);
				}
			};
		case "jlmTextbf":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					final String arg = tp.getGroupAsArgument();
					return new JavaFontRenderingAtom(arg, Font.BOLD);
				}
			};
		case "jlmTextitbf":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					final String arg = tp.getGroupAsArgument();
					return new JavaFontRenderingAtom(arg,
							Font.BOLD | Font.ITALIC);
				}
			};
		case "doteq":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					final Atom at = new UnderOverAtom(Symbols.EQUALS,
							Symbols.LDOTP,
							new TeXLength(Unit.MU, 3.7), false, true);
					return at.changeType(TeXConstants.TYPE_RELATION);
				}
			};
		case "cong":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					final VRowAtom vra = new VRowAtom(Symbols.SIM,
							new SpaceAtom(Unit.MU, 0., 1.5, 0.),
							Symbols.EQUALS);
					vra.setRaise(Unit.MU, -1.);
					return vra.changeType(TeXConstants.TYPE_RELATION);
				}
			};


		case "cornersize":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					final double cs = tp.getArgAsDecimal();
					return new SetLengthAtom("cornersize", cs);
				}
			};


		case "mathstrut":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new PhantomAtom(
							Symbols.LBRACK
									.changeType(TeXConstants.TYPE_ORDINARY),
							false, true, true);
				}
			};


		case "LaTeX":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new LaTeXAtom();
				}
			};


		case "questeq":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					final Atom at = new UnderOverAtom(Symbols.EQUALS,
							Symbols.QUESTION,
							new TeXLength(Unit.MU, 2.5), true, true);
					return at.changeType(TeXConstants.TYPE_RELATION);
				}
			};
		case "eqdef":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new BuildrelAtom(Symbols.EQUALS, new RomanAtom(
							TeXParser.getAtomForLatinStr("def", true)));
				}
			};


		case "hdotsfor":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					if (!tp.isArrayMode()) {
						throw new ParseException(tp,
								"The macro \\hdotsfor is only available in array mode !");
					}
					final double x = tp.getOptionAsDecimal(1.);
					final int n = tp.getArgAsPositiveInteger();
					if (n == -1) {
						throw new ParseException(tp,
								"The macro \\hdotsfor requires a positive integer");
					}
					return new HdotsforAtom(n, x);
				}
			};

		case "newline":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					tp.close();
					if (tp.isArrayMode()) {
						return EnvArray.RowSep.get();
					}
					throw new ParseException(tp,
							"The macro \\newline must be used in an array");
				}
			};


		case "iddots":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new IddotsAtom();
				}
			};
		case "ddots":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new DdotsAtom();
				}
			};
		case "vdots":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new VdotsAtom();
				}
			};


		case "joinrel":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new SpaceAtom(Unit.MU, -3, 0, 0)
							.changeType(TeXConstants.TYPE_RELATION);
				}
			};


		case "tcaron":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new tcaronAtom();
				}
			};
		case "Lcaron":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new LCaronAtom(true);
				}
			};
		case "lcaron":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new LCaronAtom(false);
				}
			};
		case "Tstroke":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new TStrokeAtom(true);
				}
			};
		case "tstroke":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new TStrokeAtom(false);
				}
			};
		case "IJ":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new IJAtom(true);
				}
			};
		case "ij":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new IJAtom(false);
				}
			};


		case "_":
		case "underscore":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new UnderscoreAtom();
				}
			};



		case "the":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					final String name = tp.getArgAsCommand(true);
					return new TheAtom(name);
				}
			};

		case "setlength":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					final String name = tp.getArgAsCommand(true);
					TeXLength newLength = tp.getArgAsLength();
					if (newLength == null) {
						throw new ParseException(tp,
								"Invalid length in \\setlength");
					}
					return new SetLengthAtom(name, newLength);
				}
			};
		case "rule":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					TeXLength r = tp.getOptionAsLength(TeXLength.getZero());
					if (r == null) {
						r = new TeXLength();
					}
					TeXLength w = tp.getArgAsLength();
					if (w == null) {
						throw new ParseException(tp,
								"Invalid length in \\rule");
					}
					TeXLength h = tp.getArgAsLength();
					if (h == null) {
						throw new ParseException(tp,
								"Invalid length in \\rule");
					}
					return new RuleAtom(w, h, r);
				}
			};
		case "vrule":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					final TeXLength[] lengths = tp.getDimensions();
					return new HVruleAtom(lengths[0], lengths[1], lengths[2],
							false);
				}
			};
		case "hrule":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					final TeXLength[] lengths = tp.getDimensions();
					return new HVruleAtom(lengths[0], lengths[1], lengths[2],
							true);
				}
			};
		case "textvisiblespace":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					tp.skipPureWhites();
					final Atom a = new HVruleAtom(null,
							new TeXLength(Unit.EX, 0.3), null, false);
					return new RowAtom(new SpaceAtom(Unit.EM, 0.06),
							a,
							new HVruleAtom(
									new TeXLength(Unit.EM, 0.3), null,
									null, true),
							a);
				}
			};
		case "hspace":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					final TeXLength w = tp.getArgAsLength();
					if (w == null) {
						throw new ParseException(tp,
								"Invalid length in \\hspace");
					}
					return new SpaceAtom(w.getUnit(), w.getL(), 0., 0.);
				}
			};
		case "vspace":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					final TeXLength h = tp.getArgAsLength();
					if (h == null) {
						throw new ParseException(tp,
								"Invalid length in \\vspace");
					}
					return new SpaceAtom(h.getUnit(), 0., h.getL(), 0.);
				}
			};
		case "degree":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					return SubSupCom.get(SubSupCom.getBase(tp), null,
							Symbols.CIRC);
				}
			};
		case "sphat":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					Atom a;
					double raise;
					if (tp.isMathMode()) {
						a = Symbols.WIDEHAT;
						raise = 1.1;
					} else {
						a = Symbols.HAT;
						raise = 0.8;
					}
					a = new StyleAtom(TeXConstants.STYLE_DISPLAY, a);
					final VRowAtom vra = new VRowAtom(a);
					vra.setRaise(Unit.EX, raise);
					a = new SmashedAtom(vra);
					return SubSupCom.get(SubSupCom.getBase(tp), null, a);
				}
			};
		case "spbreve":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					final Atom ex = new SpaceAtom(TeXConstants.Muskip.NEGTHIN);
					Atom a = Symbols.BREVE;
					a = new StyleAtom(TeXConstants.STYLE_DISPLAY, a);
					final VRowAtom vra = new VRowAtom(a);
					vra.setRaise(Unit.EX, 1.1);
					a = new SmashedAtom(vra);
					final RowAtom ra = new RowAtom(ex, a);
					return
					SubSupCom.get(SubSupCom.getBase(tp), null, ra);
				}
			};
		case "spcheck":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					final Atom a = Symbols.VEE;
					return SubSupCom.get(SubSupCom.getBase(tp), null, a);
				}
			};
		case "sptilde":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					final Atom a = Symbols.SIM;
					return SubSupCom.get(SubSupCom.getBase(tp), null, a);
				}
			};
		case "spdot":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					Atom a = Symbols.NORMALDOT;
					a = new StyleAtom(TeXConstants.STYLE_DISPLAY, a);
					final VRowAtom vra = new VRowAtom(a);
					vra.setRaise(Unit.EX, 0.8);
					a = new SmashedAtom(vra);
					return SubSupCom.get(SubSupCom.getBase(tp), null, a);
				}
			};
		case "spddot":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					Atom a = Symbols.NORMALDOT;
					final RowAtom ra = new RowAtom(a, a);
					a = new StyleAtom(TeXConstants.STYLE_DISPLAY, ra);
					final VRowAtom vra = new VRowAtom(a);
					vra.setRaise(Unit.EX, 0.8);
					a = new SmashedAtom(vra);
					return SubSupCom.get(SubSupCom.getBase(tp), null, a);
				}
			};
		case "spdddot":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					Atom a = Symbols.NORMALDOT;
					final RowAtom ra = new RowAtom(a, a, a);
					a = new StyleAtom(TeXConstants.STYLE_DISPLAY, ra);
					final VRowAtom vra = new VRowAtom(a);
					vra.setRaise(Unit.EX, 0.8);
					a = new SmashedAtom(vra);
					return SubSupCom.get(SubSupCom.getBase(tp), null, a);
				}
			};

		case "varinjlim":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					Atom a = new RomanAtom(
							TeXParser.getAtomForLatinStr("lim", true));
					a = new UnderOverArrowAtom(a, false, false);
					a = a.changeType(TeXConstants.TYPE_BIG_OPERATOR);
					a.type_limits = TeXConstants.SCRIPT_LIMITS;
					return a;
				}
			};
		case "varprojlim":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					Atom a = new RomanAtom(
							TeXParser.getAtomForLatinStr("lim", true));
					a = new UnderOverArrowAtom(a, true, false);
					a = a.changeType(TeXConstants.TYPE_BIG_OPERATOR);
					a.type_limits = TeXConstants.SCRIPT_LIMITS;
					return a;
				}
			};
		case "varliminf":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					Atom a = new RomanAtom(
							TeXParser.getAtomForLatinStr("lim", true));
					a = new UnderlinedAtom(a);
					a = a.changeType(TeXConstants.TYPE_BIG_OPERATOR);
					a.type_limits = TeXConstants.SCRIPT_LIMITS;
					return a;
				}
			};
		case "varlimsup":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					Atom a = new RomanAtom(
							TeXParser.getAtomForLatinStr("lim", true));
					a = new OverlinedAtom(a);
					a = a.changeType(TeXConstants.TYPE_BIG_OPERATOR);
					a.type_limits = TeXConstants.SCRIPT_LIMITS;
					return a;
				}
			};
		case "with":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return Symbols.WITH
							.changeType(TeXConstants.TYPE_BINARY_OPERATOR);
				}
			};
		case "parr":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					Atom a = new RotateAtom(Symbols.WITH, 180.,
							new HashMap<String, String>() {
								{
									put("origin", "c");
								}
							});
					return a.changeType(TeXConstants.TYPE_BINARY_OPERATOR);
				}
			};
		case "copyright":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					Atom a = new RomanAtom(new CharAtom('c', false));
					a = new RaiseAtom(a, new TeXLength(Unit.EX, 0.2),
							TeXLength.getZero(), TeXLength.getZero());
					return new TextCircledAtom(a);
				}
			};
		case "L":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					final Atom a = new RowAtom(SymbolAtom.get("polishlcross"),
							new CharAtom('L', false));
					return new RomanAtom(a);
				}
			};
		case "l":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					final Atom a = new RowAtom(SymbolAtom.get("polishlcross"),
							new CharAtom('l', false));
					return new RomanAtom(a);
				}
			};
		case "Join":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					Atom a = new LapedAtom(SymbolAtom.get("ltimes"), 'r');
					a = new RowAtom(a, SymbolAtom.get("rtimes"));
					a = a.changeType(TeXConstants.TYPE_BIG_OPERATOR);
					a.type_limits = TeXConstants.SCRIPT_NORMAL;
					return a;
				}
			};
		case "notin":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new RowAtom(Symbols.NOT, Symbols.IN);
				}
			};

		case "neq":
		case "ne":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new RowAtom(Symbols.NOT, Symbols.EQUALS);
				}
			};

		case "JLaTeXMath":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new RowAtom(
							new CharAtom('J', TextStyle.MATHBB, true),
							new LaTeXAtom(),
							new CharAtom('M', TextStyle.MATHNORMAL, true),
							new CharAtom('a', TextStyle.MATHNORMAL, true),
							new CharAtom('t', TextStyle.MATHNORMAL, true),
							new CharAtom('h', TextStyle.MATHNORMAL, true));
				}
			};

		case "dotsc":
		case "dots":
		case "dotso":
		case "ldots":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					final Atom ldotp = Symbols.LDOTP;
					return new RowAtom(ldotp, ldotp, ldotp)
							.changeType(TeXConstants.TYPE_INNER);
				}
			};

		case "dotsb":
		case "dotsm":
		case "cdots":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					final Atom cdotp = Symbols.CDOTP;
					return new RowAtom(cdotp, cdotp, cdotp)
							.changeType(TeXConstants.TYPE_INNER);
				}
			};

		case "dotsi":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					final Atom cdotp = Symbols.CDOTP;
					final RowAtom ra = new RowAtom(cdotp, cdotp, cdotp);
					return new RowAtom(
							new SpaceAtom(TeXConstants.Muskip.NEGTHIN),
							ra.changeType(TeXConstants.TYPE_INNER));
				}
			};
		case "relbar":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new SmashedAtom(Symbols.MINUS)
							.changeType(TeXConstants.TYPE_RELATION);
				}
			};

		// case "kern": XXX
		case "mspace":
		case "hskip":
		case "mskip":
		case "mkern":
			return new Command0AImpl() {
				@Override
				public Atom newI(TeXParser tp) {
					final TeXLength len = tp.getArgAsLength();
					return new SpaceAtom(len);
				}
			};

		case "strut":
			return new Command0A() {
				@Override
				public Atom newI(TeXParser tp) {
					return new SpaceAtom(Unit.PT, 0., 8.6, 3.);
				}
			};

		}

		return null;

	}

	public static AtomConsumer get(final String name) {
		Command r = reusableMap.get(name);
		if (r != null) {
			return r;
		}

		Command c = getReusableCommand(name);

		if (c != null) {
			reusableMap.put(name, c);
			return c;
		}

		return getOneWayCommand(name);
	}

	public static boolean exec(final TeXParser tp, final String name) {
		final Command c = getCommand(name);
		if (c != null) {
			tp.cancelPrevPos();
			if (c.init(tp)) {
				tp.addConsumer(c);
			}
			return true;
		}

		return false;
	}

	static Command getUnsafe(final String name) {
		return getCommand(name);
	}

	public static AtomConsumer getDollar() {
		return dollar;
	}

	public static AtomConsumer getDollarDollar() {
		return dollardollar;
	}
}
