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
import java.util.List;
import java.util.Map;

import com.himamis.retex.renderer.share.commands.*;
import com.himamis.retex.renderer.share.dynamic.DynamicAtom;
import com.himamis.retex.renderer.share.exception.ParseException;
import com.himamis.retex.renderer.share.platform.font.Font;
import com.himamis.retex.renderer.share.platform.graphics.Color;

public class Commands {

	private static final Map<String, Command> map = new HashMap<>(700);

	private static final Command dollar = new CommandDollars.Dollar(true,
			TeXConstants.STYLE_TEXT);
	private static final Command dollardollar = new CommandDollars.Dollar(false,
			TeXConstants.STYLE_DISPLAY);

	static {
		map.put("usepackage", new CommandUsePackage());

		map.put("ce", new CommandCE());

		map.put("bond", new CommandBond());

		map.put("hbox", new CommandHBox());

		map.put("cancel", new CommandCancel());

		map.put("bcancel", new CommandBCancel());

		map.put("xcancel", new CommandXCancel());

		map.put("mathchoice", new CommandMathChoice());

		map.put("pod", new CommandPod());

		map.put("bmod", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				final Atom sp = new SpaceAtom(TeXLength.Unit.MU, 5.);
				final RowAtom ra = new RowAtom(sp,
						new RomanAtom(TeXParser.getAtomForLatinStr("mod", true)
								.changeType(TeXConstants.TYPE_BINARY_OPERATOR)),
						sp);
				return ra;
			}
		});
		map.put("pmod", new CommandPMod());

		map.put("mod", new CommandMod());

		map.put("begingroup", new CommandBeginGroup());

		map.put("endgroup", new CommandEndGroup());

		map.put("DeclareMathOperator", new CommandDeclareMathOperator());

		map.put("newcommand", new CommandNewCommand());

		map.put("renewcommand", new CommandRenewCommand());

		map.put("newenvironment", new CommandNewEnvironment());

		map.put("renewenvironment", new CommandRenewEnvironment());

		map.put("left", new CommandLMR.CommandLeft());
		map.put("right", new CommandLMR.CommandRight());
		map.put("middle", new CommandLMR.CommandMiddle());

		// stretchy versions
		map.put("Braket", new CommandBra(Symbols.LANGLE, Symbols.RANGLE));
		map.put("Bra", new CommandBra(Symbols.LANGLE, Symbols.VERT));
		map.put("Ket", new CommandBra(Symbols.VERT, Symbols.RANGLE));
		map.put("Set", new CommandBra(Symbols.LBRACE, Symbols.RBRACE));
		map.put("braket", new CommandBraKet());

		// non-stretchy versions
		map.put("bra", new CommandBra2());
		map.put("ket", new CommandKet());
		map.put("set", new CommandSet());

		map.put("hookrightarrow", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				final RowAtom ra = new RowAtom(3);
				// XXX was -0.169
				ra.add(Symbols.LHOOK, new SpaceAtom(TeXLength.Unit.EM, -0.43),
						Symbols.RIGHTARROW);
				ra.setShape(true);
				return new TypedAtom(TeXConstants.TYPE_RELATION, ra);
			}
		});
		map.put("hookleftarrow", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				final RowAtom ra = new RowAtom(3);
				ra.add(Symbols.LEFTARROW,
						// XXX was -0.169
						new SpaceAtom(TeXLength.Unit.EM, -0.43), Symbols.RHOOK);
				ra.setShape(true);
				return new TypedAtom(TeXConstants.TYPE_RELATION, ra);
			}
		});
		map.put("Longrightarrow", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				final RowAtom ra = new RowAtom(3);
				ra.add(Symbols.BIG_RELBAR,
						new SpaceAtom(TeXLength.Unit.EM, -0.177),
						Symbols.BIG_RIGHTARROW);
				ra.setShape(true);
				return new TypedAtom(TeXConstants.TYPE_RELATION, ra);
			}
		});
		map.put("Longleftarrow", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				final RowAtom ra = new RowAtom(3);
				ra.add(Symbols.BIG_LEFTARROW,
						new SpaceAtom(TeXLength.Unit.EM, -0.177),
						Symbols.BIG_RELBAR);
				ra.setShape(true);
				return new TypedAtom(TeXConstants.TYPE_RELATION, ra);
			}
		});
		map.put("longleftarrow", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				final RowAtom ra = new RowAtom(3);
				ra.add(Symbols.LEFTARROW,
						new SpaceAtom(TeXLength.Unit.EM, -0.206),
						Symbols.MINUS.changeType(TeXConstants.TYPE_RELATION));
				ra.setShape(true);
				return new TypedAtom(TeXConstants.TYPE_RELATION, ra);
			}
		});
		map.put("longrightarrow", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				final RowAtom ra = new RowAtom(3);
				ra.add(Symbols.MINUS.changeType(TeXConstants.TYPE_RELATION),
						new SpaceAtom(TeXLength.Unit.EM, -0.206),
						Symbols.RIGHTARROW);
				ra.setShape(true);
				return new TypedAtom(TeXConstants.TYPE_RELATION, ra);
			}
		});
		map.put("longleftrightarrow", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				final RowAtom ra = new RowAtom(3);
				ra.add(Symbols.LEFTARROW,
						new SpaceAtom(TeXLength.Unit.EM, -0.180),
						Symbols.RIGHTARROW);
				ra.setShape(true);
				return new TypedAtom(TeXConstants.TYPE_RELATION, ra);
			}
		});
		map.put("Longleftrightarrow", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				final RowAtom ra = new RowAtom(3);
				ra.add(Symbols.BIG_LEFTARROW,
						new SpaceAtom(TeXLength.Unit.EM, -0.180),
						Symbols.BIG_RIGHTARROW);
				ra.setShape(true);
				return new TypedAtom(TeXConstants.TYPE_RELATION, ra);
			}
		});
		map.put(" ", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new SpaceAtom();
			}
		});
		map.put("nbsp", map.get(" "));
		map.put("nobreaskspace", map.get(" "));
		map.put("space", map.get(" "));
		map.put("{", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return Symbols.LBRACE;
			}
		});
		map.put("}", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return Symbols.RBRACE;
			}
		});
		map.put("nolimits", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				final Atom a = tp.getLastAtom();
				if (a != null) {
					tp.addToConsumer(
							a.changeLimits(TeXConstants.SCRIPT_NOLIMITS));
				}
				return false;
			}
		});
		map.put("limits", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				final Atom a = tp.getLastAtom();
				if (a != null) {
					tp.addToConsumer(
							a.changeLimits(TeXConstants.SCRIPT_LIMITS));
				}
				return false;
			}
		});
		map.put("normal", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				final Atom a = tp.getLastAtom();
				if (a != null) {
					tp.addToConsumer(
							a.changeLimits(TeXConstants.SCRIPT_NORMAL));
				}
				return false;
			}
		});
		map.put("surd", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new VCenteredAtom(SymbolAtom.get("surdsign"));
			}
		});
		map.put("vcenter", new CommandVCenter());

		map.put("int", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return Symbols.INT;
			}
		});
		map.put("intop", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return Symbols.INTOP;
			}
		});
		map.put("oint", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return Symbols.OINT;
			}
		});
		map.put("iint", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				final Atom integral = Symbols.INT;
				final SpaceAtom six = new SpaceAtom(TeXLength.Unit.MU, -6., 0.,
						0.);
				final SpaceAtom nine = new SpaceAtom(TeXLength.Unit.MU, -9., 0.,
						0.);
				final Atom choice = new MathchoiceAtom(nine, six, six, six);
				final RowAtom ra = new RowAtom(integral, choice, integral);
				ra.lookAtLastAtom = true;
				return ra.changeType(TeXConstants.TYPE_BIG_OPERATOR);
			}
		});
		map.put("iiint", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				final Atom integral = Symbols.INT;
				final SpaceAtom six = new SpaceAtom(TeXLength.Unit.MU, -6., 0.,
						0.);
				final SpaceAtom nine = new SpaceAtom(TeXLength.Unit.MU, -9., 0.,
						0.);
				final Atom choice = new MathchoiceAtom(nine, six, six, six);
				final RowAtom ra = new RowAtom(integral, choice, integral,
						choice, integral);
				ra.lookAtLastAtom = true;
				return ra.changeType(TeXConstants.TYPE_BIG_OPERATOR);
			}
		});
		map.put("iiiint", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				final Atom integral = Symbols.INT;
				final SpaceAtom six = new SpaceAtom(TeXLength.Unit.MU, -6., 0.,
						0.);
				final SpaceAtom nine = new SpaceAtom(TeXLength.Unit.MU, -9., 0.,
						0.);
				final Atom choice = new MathchoiceAtom(nine, six, six, six);
				final RowAtom ra = new RowAtom(integral, choice, integral,
						choice, integral, choice, integral);
				ra.lookAtLastAtom = true;
				return ra.changeType(TeXConstants.TYPE_BIG_OPERATOR);
			}
		});
		map.put("idotsint", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				final Atom integral = Symbols.INT;
				final Atom cdotp = Symbols.CDOTP;
				final SpaceAtom sa = new SpaceAtom(TeXLength.Unit.MU, -1., 0.,
						0.);
				final RowAtom cdots = new RowAtom(cdotp, cdotp, cdotp);
				final RowAtom ra = new RowAtom(integral, sa,
						cdots.changeType(TeXConstants.TYPE_INNER), sa,
						integral);
				ra.lookAtLastAtom = true;
				return ra.changeType(TeXConstants.TYPE_BIG_OPERATOR);
			}
		});
		map.put("lmoustache", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				final Atom at = new BigDelimiterAtom(
						SymbolAtom.get("lmoustache"), 1);
				at.setType(TeXConstants.TYPE_OPENING);
				return at;
			}
		});
		map.put("rmoustache", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				final Atom at = new BigDelimiterAtom(
						SymbolAtom.get("rmoustache"), 1);
				at.setType(TeXConstants.TYPE_CLOSING);
				return at;
			}
		});
		map.put("-", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return BreakMarkAtom.get();
			}
		});
		map.put("frac", new CommandFrac());

		map.put("genfrac", new CommandGenfrac());

		map.put("dfrac", new CommandDFrac());

		map.put("tfrac", new CommandTFrac());
		map.put("dbinom", new CommandDBinom());

		map.put("tbinom", new CommandTBinom());

		map.put("binom", new CommandBinom());

		map.put("over", new CommandOver());

		map.put("buildrel", new CommandBuildRel());

		map.put("choose", new CommandChoose(Symbols.LBRACK, Symbols.RBRACK));
		map.put("brace", new CommandChoose(Symbols.LBRACE, Symbols.RBRACE));
		map.put("bangle", new CommandChoose(Symbols.LANGLE, Symbols.RANGLE));
		map.put("brack", new CommandChoose(Symbols.LSQBRACK, Symbols.RSQBRACK));

		map.put("overwithdelims", new CommandOverwithdelims());
		map.put("atopwithdelims", new CommandATopwithdelims());
		map.put("abovewithdelims", new CommandAbovewithdelims());

		map.put("above", new CommandAbove());
		map.put("atop", new CommandATop());
		map.put("sqrt", new CommandSqrt());

		map.put("fcscore", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				final int arg = tp.getArgAsPositiveInteger();
				tp.addToConsumer(FcscoreAtom.get(arg));
				return false;
			}
		});
		map.put("longdiv", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				final long dividend = tp.getArgAsPositiveInteger();
				final long divisor = tp.getArgAsPositiveInteger();
				tp.addToConsumer(new LongdivAtom(divisor, dividend, tp));
				return false;
			}
		});
		map.put("st", new CommandSt());

		map.put("includegraphics", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				final Map<String, String> arg1 = tp.getOptionAsMap();
				final String arg2 = tp.getArgAsString();
				tp.addToConsumer(new GraphicsAtom(arg2, arg1));
				return false;
			}
		});
		map.put("clap", new CommandClap());

		map.put("rlap", new CommandRLap());

		map.put("llap", new CommandLLap());

		map.put("mathclap", map.get("clap"));
		map.put("mathrlap", map.get("rlap"));
		map.put("mathllap", map.get("llap"));
		map.put("begin", new CommandBE.Begin());
		map.put("end", new CommandBE.End());
		map.put("begin@array", new EnvArray.Begin("array", ArrayAtom.ARRAY));
		map.put("end@array", new EnvArray.End("array"));
		map.put("begin@tabular",
				new EnvArray.Begin("tabular", ArrayAtom.ARRAY));
		map.put("end@tabular", new EnvArray.End("tabular"));
		map.put("\\", new CommandCr("\\"));
		map.put("begin@eqnarray", new EnvArray.Begin("eqnarray",
				ArrayAtom.ARRAY,
				new ArrayOptions(3).addAlignment(TeXConstants.Align.RIGHT)
						.addAlignment(TeXConstants.Align.CENTER)
						.addAlignment(TeXConstants.Align.LEFT).close()));
		map.put("end@eqnarray", new EnvArray.End("eqnarray"));
		map.put("begin@split",
				new EnvArray.Begin("split", ArrayAtom.ARRAY, new ArrayOptions(2)
						.addAlignment(TeXConstants.Align.RIGHT)
						.addAlignment(TeXConstants.Align.LEFT).close()));
		map.put("end@split", new EnvArray.End("split"));
		map.put("begin@cases", new EnvArray.Begin("cases", ArrayAtom.ARRAY,
				new ArrayOptions(3).addAlignment(TeXConstants.Align.LEFT)
						.addSeparator(
								new SpaceAtom(TeXConstants.Muskip.NEGTHIN))
						.addAlignment(TeXConstants.Align.LEFT).close()));
		map.put("end@cases", new EnvArray.End("cases"));
		map.put("matrix", new CommandMatrix() {
		});
		map.put("array", map.get("matrix"));
		map.put("ooalign", new CommandOoalign());
		map.put("pmatrix", new CommandPMatrix());

		map.put("begin@matrix", new EnvArray.Begin("matrix", ArrayAtom.MATRIX,
				ArrayOptions.getEmpty()));
		map.put("end@matrix", new EnvArray.End("matrix"));
		map.put("begin@smallmatrix", new EnvArray.Begin("smallmatrix",
				ArrayAtom.SMALLMATRIX, ArrayOptions.getEmpty()));
		map.put("end@smallmatrix", new EnvArray.End("smallmatrix"));
		map.put("begin@align", new EnvArray.Begin("align", ArrayAtom.ALIGN,
				ArrayOptions.getEmpty()));
		map.put("end@align", new EnvArray.End("align"));
		map.put("begin@aligned", new EnvArray.Begin("aligned",
				ArrayAtom.ALIGNED, ArrayOptions.getEmpty()));
		map.put("end@aligned", new EnvArray.End("aligned"));
		map.put("begin@flalign", new EnvArray.Begin("flalign",
				ArrayAtom.FLALIGN, ArrayOptions.getEmpty()));
		map.put("end@flalign", new EnvArray.End("flalign"));
		map.put("begin@alignat", new EnvArray.Begin("alignat",
				ArrayAtom.ALIGNAT, ArrayOptions.getEmpty()));
		map.put("end@alignat", new EnvArray.End("alignat"));
		map.put("begin@alignedat", new EnvArray.Begin("alignedat",
				ArrayAtom.ALIGNEDAT, ArrayOptions.getEmpty()));
		map.put("end@alignedat", new EnvArray.End("alignedat"));
		map.put("begin@multline",
				new EnvArray.Begin("multline", -1, ArrayOptions.getEmpty()));
		map.put("end@multline", new EnvArray.End("multline"));
		map.put("begin@subarray", new EnvArray.Begin("subarray", -1));
		map.put("end@subarray", new EnvArray.End("subarray"));
		map.put("substack", new CommandSubstack());
		map.put("displaylines", new CommandDisplaylines());
		map.put("begin@gather",
				new EnvArray.Begin("gather", -1, ArrayOptions.getEmpty()));
		map.put("end@gather", new EnvArray.End("gather"));
		map.put("begin@gathered",
				new EnvArray.Begin("gathered", -1, ArrayOptions.getEmpty()));
		map.put("end@gathered", new EnvArray.End("gathered"));
		map.put("begin@pmatrix", new EnvArray.Begin("pmatrix", ArrayAtom.MATRIX,
				ArrayOptions.getEmpty()));
		map.put("end@pmatrix", new EnvArray.End("pmatrix", "lbrack", "rbrack"));
		map.put("begin@bmatrix", new EnvArray.Begin("bmatrix", ArrayAtom.MATRIX,
				ArrayOptions.getEmpty()));
		map.put("end@bmatrix",
				new EnvArray.End("bmatrix", "lsqbrack", "rsqbrack"));
		map.put("begin@vmatrix", new EnvArray.Begin("bmatrix", ArrayAtom.MATRIX,
				ArrayOptions.getEmpty()));
		map.put("end@vmatrix", new EnvArray.End("bmatrix", "vert"));
		map.put("begin@Vmatrix", new EnvArray.Begin("Vmatrix", ArrayAtom.MATRIX,
				ArrayOptions.getEmpty()));
		map.put("end@Vmatrix", new EnvArray.End("Vmatrix", "Vert"));
		map.put(",", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new SpaceAtom(TeXConstants.Muskip.THIN);
			}
		});
		map.put("thinspace", map.get(","));
		map.put(":", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new SpaceAtom(TeXConstants.Muskip.MED);
			}
		});
		map.put("medspace", map.get(":"));
		map.put(">", map.get(":"));
		map.put(";", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new SpaceAtom(TeXConstants.Muskip.THICK);
			}
		});
		map.put("thickspace", map.get(";"));
		map.put("!", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new SpaceAtom(TeXConstants.Muskip.NEGTHIN);
			}
		});
		map.put("negthinspace", map.get("!"));
		map.put("negmedspace", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new SpaceAtom(TeXConstants.Muskip.NEGMED);
			}
		});
		map.put("negthickspace", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new SpaceAtom(TeXConstants.Muskip.NEGTHICK);
			}
		});
		map.put("enspace", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new SpaceAtom(TeXLength.Unit.EM, 0.5, 0., 0.);
			}
		});
		map.put("enskip", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new SpaceAtom(TeXLength.Unit.EM, 0.5, 0., 0.);
			}
		});
		map.put("quad", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new SpaceAtom(TeXLength.Unit.EM, 1., 0., 0.);
			}
		});
		map.put("qquad", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new SpaceAtom(TeXLength.Unit.EM, 2., 0., 0.);
			}
		});
		map.put("Space", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new SpaceAtom(TeXLength.Unit.EM, 3., 0., 0.);
			}
		});
		map.put("textcircled", new CommandTextCircled());

		map.put("romannumeral", new CommandRomNum(false));
		map.put("Romannumeral", new CommandRomNum(true));
		map.put("T", new CommandT());

		map.put("char", new Command0AImpl() {
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
		});
		map.put("unicode", new CommandUnicode());
		map.put("kern", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				tp.addToConsumer(new SpaceAtom(tp.getArgAsLength()));
				return false;
			}
		});
		map.put("Dstrok", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				final RowAtom ra = new RowAtom(
						new SpaceAtom(TeXLength.Unit.EX, -0.1, 0., 0.),
						Symbols.BAR);
				final VRowAtom vra = new VRowAtom(new LapedAtom(ra, 'r'));
				vra.setRaise(TeXLength.Unit.EX, -0.55);
				return new RowAtom(vra,
						new RomanAtom(new CharAtom('D', TextStyle.MATHNORMAL)));
			}
		});
		map.put("dstrok", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				final RowAtom ra = new RowAtom(
						new SpaceAtom(TeXLength.Unit.EX, 0.25, 0., 0.),
						Symbols.BAR);
				final VRowAtom vra = new VRowAtom(new LapedAtom(ra, 'r'));
				vra.setRaise(TeXLength.Unit.EX, -0.1);
				return new RowAtom(vra,
						new RomanAtom(new CharAtom('d', TextStyle.MATHNORMAL)));
			}
		});
		map.put("Hstrok", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				final RowAtom ra = new RowAtom(
						new SpaceAtom(TeXLength.Unit.EX, 0.28, 0., 0.),
						Symbols.TEXTENDASH);
				final VRowAtom vra = new VRowAtom(new LapedAtom(ra, 'r'));
				vra.setRaise(TeXLength.Unit.EX, 0.55);
				return new RowAtom(vra,
						new RomanAtom(new CharAtom('H', TextStyle.MATHNORMAL)));
			}
		});
		map.put("hstrok", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				final RowAtom ra = new RowAtom(
						new SpaceAtom(TeXLength.Unit.EX, -0.1, 0., 0.),
						Symbols.BAR);
				final VRowAtom vra = new VRowAtom(new LapedAtom(ra, 'r'));
				vra.setRaise(TeXLength.Unit.EX, -0.1);
				return new RowAtom(vra,
						new RomanAtom(new CharAtom('h', TextStyle.MATHNORMAL)));
			}
		});
		map.put("smallfrowneq", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				final Atom at = new UnderOverAtom(Symbols.EQUALS,
						Symbols.SMALLFROWN,
						new TeXLength(TeXLength.Unit.MU, -2.), true, true);
				return at.changeType(TeXConstants.TYPE_RELATION);
			}
		});
		map.put("frowneq", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				final Atom at = new UnderOverAtom(Symbols.EQUALS, Symbols.FROWN,
						TeXLength.getZero(), true, true);
				return at.changeType(TeXConstants.TYPE_RELATION);
			}
		});
		map.put("coloncolonapprox",
				new CommandColonFoo.ColonColonFoo("approx"));
		map.put("colonapprox", new CommandColonFoo.ColonFoo("approx"));
		map.put("coloncolonsim", new CommandColonFoo.ColonColonFoo("sim"));
		map.put("colonsim", new CommandColonFoo.ColonFoo("sim"));
		map.put("coloncolon", new CommandColonFoo.ColonColonFoo());
		map.put("coloncolonequals",
				new CommandColonFoo.ColonColonFoo("equals"));
		map.put("colonequals", new CommandColonFoo.ColonFoo("equals"));
		map.put("coloncolonminus", new CommandColonFoo.ColonColonFoo("minus"));
		map.put("colonminus", new CommandColonFoo.ColonFoo("minus"));
		map.put("equalscoloncolon",
				new CommandColonFoo.FooColonColon("equals"));
		map.put("equalscolon", new CommandColonFoo.FooColon("equals"));
		map.put("minuscoloncolon", new CommandColonFoo.FooColonColon("minus"));
		map.put("minuscolon", new CommandColonFoo.FooColon("minus"));
		map.put("simcoloncolon", new CommandColonFoo.FooColonColon("sim"));
		map.put("simcolon", new CommandColonFoo.FooColon("sim"));
		map.put("approxcoloncolon",
				new CommandColonFoo.FooColonColon("approx"));
		map.put("approxcolon", new CommandColonFoo.FooColon("approx"));
		map.put("geoprop", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				final SymbolAtom nd = Symbols.NORMALDOT;
				final RowAtom ddot = new RowAtom(nd,
						new SpaceAtom(TeXLength.Unit.MU, 4., 0., 0.), nd);
				final TeXLength l = new TeXLength(TeXLength.Unit.MU, -3.4);
				Atom at = new UnderOverAtom(Symbols.MINUS, ddot, l, false, ddot,
						l, false);
				return at.changeType(TeXConstants.TYPE_RELATION);
			}
		});
		map.put("ratio", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				final Atom a = new VCenteredAtom(
						Symbols.COLON.changeType(TeXConstants.TYPE_ORDINARY));
				return a.changeType(TeXConstants.TYPE_RELATION);
			}
		});
		map.put("dotminus", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				Atom at = new UnderOverAtom(Symbols.MINUS, Symbols.NORMALDOT,
						new TeXLength(TeXLength.Unit.EX, -0.4), false, true);
				return at.changeType(TeXConstants.TYPE_BINARY_OPERATOR);
			}
		});
		map.put("tiny", new CommandTiny1());

		map.put("Tiny", new CommandTiny2());

		map.put("scriptsize", new CommandScriptSize());

		map.put("footnotesize", new CommandFootnoteSize());

		map.put("small", new CommandSmall());

		map.put("normalsize", new CommandNormalSize());

		map.put("large", new CommandLarge());

		map.put("Large", new CommandLarge2());

		map.put("LARGE", new CommandLarge3());

		map.put("huge", new CommandHuge1());

		map.put("Huge", new CommandHuge2());

		map.put("sc", new CommandSc());

		map.put("hline", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				if (!tp.isArrayMode()) {
					throw new ParseException(tp,
							"The macro \\hline is only available in array mode !");
				}
				return new HlineAtom();
			}
		});
		map.put("cellcolor", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				if (!tp.isArrayMode()) {
					throw new ParseException(tp,
							"The macro \\cellcolor is only available in array mode !");
				}
				final Color c = CommandDefinecolor.getColor(tp);
				tp.addToConsumer(new EnvArray.CellColor(c));
				return false;
			}
		});
		map.put("rowcolor", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				if (!tp.isArrayMode()) {
					throw new ParseException(tp,
							"The macro \\rowcolor is only available in array mode !");
				}
				final Color c = CommandDefinecolor.getColor(tp);
				tp.addToConsumer(new EnvArray.RowColor(c));
				return false;
			}
		});
		map.put("jlmText", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				final String arg = tp.getGroupAsArgument();
				tp.addToConsumer(new JavaFontRenderingAtom(arg, Font.PLAIN));
				return false;
			}
		});
		map.put("jlmTextit", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				final String arg = tp.getGroupAsArgument();
				tp.addToConsumer(new JavaFontRenderingAtom(arg, Font.ITALIC));
				return false;
			}
		});
		map.put("jlmTextbf", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				final String arg = tp.getGroupAsArgument();
				tp.addToConsumer(new JavaFontRenderingAtom(arg, Font.BOLD));
				return false;
			}
		});
		map.put("jlmTextitbf", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				final String arg = tp.getGroupAsArgument();
				tp.addToConsumer(new JavaFontRenderingAtom(arg,
						Font.BOLD | Font.ITALIC));
				return false;
			}
		});
		map.put("jlmExternalFont", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				final String fontname = tp.getArgAsString();
				JavaFontRenderingBox.setFont(fontname);
				return false;
			}
		});
		map.put("jlmDynamic", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				if (DynamicAtom.hasAnExternalConverterFactory()) {
					final char opt = tp.getOptionAsChar();
					final String arg = tp.getGroupAsArgument();
					tp.addToConsumer(new DynamicAtom(arg, opt));

					return false;
				}
				throw new ParseException(tp,
						"No ExternalConverterFactory set !");
			}
		});
		map.put("doteq", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				final Atom at = new UnderOverAtom(Symbols.EQUALS, Symbols.LDOTP,
						new TeXLength(TeXLength.Unit.MU, 3.7), false, true);
				return at.changeType(TeXConstants.TYPE_RELATION);
			}
		});
		map.put("cong", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				final VRowAtom vra = new VRowAtom(Symbols.SIM,
						new SpaceAtom(TeXLength.Unit.MU, 0., 1.5, 0.),
						Symbols.EQUALS);
				vra.setRaise(TeXLength.Unit.MU, -1.);
				return vra.changeType(TeXConstants.TYPE_RELATION);
			}
		});
		map.put("fbox", new CommandFBox());

		map.put("boxed", new CommandBoxed());

		map.put("dbox", new CommandDBox());

		map.put("fcolorbox", new CommandFColorBox());

		map.put("colorbox", new CommandColorBox());

		map.put("textcolor", new CommandTextColor());

		map.put("color", new CommandColor());

		map.put("bgcolor", new CommandBGColor());

		map.put("fgcolor", map.get("textcolor"));
		map.put("definecolor", new CommandDefinecolor());

		map.put("doublebox", new CommandDoubleBox());

		map.put("ovalbox", new CommandOvalBox());

		map.put("cornersize", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				final double cs = tp.getArgAsDecimal();
				tp.addToConsumer(new SetLengthAtom(
						new TeXLength(TeXLength.Unit.NONE, cs), "cornersize"));
				return false;
			}
		});

		map.put("shadowbox", new CommandShadowBox());

		map.put("raisebox", new CommandRaiseBox());

		map.put("raise", new CommandRaise());

		map.put("lower", new CommandLower());

		map.put("moveleft", new CommandMoveLeft());

		map.put("moveright", new CommandMoveRight());

		map.put("resizebox", new CommandResizeBox());

		map.put("scalebox", new CommandScaleBox());

		map.put("reflectbox", new CommandReflectBox());

		map.put("rotatebox", new CommandRotateBox());

		map.put("scriptscriptstyle", new CommandScriptScriptStyle());

		map.put("textstyle", new CommandTextStyle2());

		map.put("scriptstyle", new CommandScriptStyle());

		map.put("displaystyle", new CommandDisplayStyle());

		map.put("Biggr", new CommandBigr(TeXConstants.TYPE_CLOSING, 4));
		map.put("biggr", new CommandBigr(TeXConstants.TYPE_CLOSING, 3));
		map.put("Bigr", new CommandBigr(TeXConstants.TYPE_CLOSING, 2));
		map.put("bigr", new CommandBigr(TeXConstants.TYPE_CLOSING, 1));
		map.put("Biggl", new CommandBigr(TeXConstants.TYPE_OPENING, 4));
		map.put("biggl", new CommandBigr(TeXConstants.TYPE_OPENING, 3));
		map.put("Bigl", new CommandBigr(TeXConstants.TYPE_OPENING, 2));
		map.put("bigl", new CommandBigr(TeXConstants.TYPE_OPENING, 1));
		map.put("Biggm", new CommandBigr(TeXConstants.TYPE_RELATION, 4));
		map.put("biggm", new CommandBigr(TeXConstants.TYPE_RELATION, 3));
		map.put("Bigm", new CommandBigr(TeXConstants.TYPE_RELATION, 2));
		map.put("bigm", new CommandBigr(TeXConstants.TYPE_RELATION, 1));
		map.put("Bigg", new CommandBigg(4));
		map.put("bigg", new CommandBigg(3));
		map.put("Big", new CommandBigg(2));
		map.put("big", new CommandBigg(1));
		map.put("mathstrut", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new PhantomAtom(
						Symbols.LBRACK.changeType(TeXConstants.TYPE_ORDINARY),
						false, true, true);
			}
		});
		map.put("phantom", new CommandPhantom());

		map.put("vphantom", new CommandVPhantom());

		map.put("hphantom", new CommandHPhantom());

		map.put("LaTeX", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new LaTeXAtom();
			}
		});
		map.put("mathcal", new CommandTextStyle(TextStyle.MATHCAL));
		map.put("cal", new CommandTextStyleTeX(TextStyle.MATHCAL));
		map.put("mathfrak", new CommandTextStyle(TextStyle.MATHFRAK));
		map.put("frak", new CommandTextStyleTeX(TextStyle.MATHFRAK));
		map.put("mathbb", new CommandTextStyle(TextStyle.MATHBB));
		map.put("Bbb", new CommandTextStyleTeX(TextStyle.MATHBB));
		map.put("mathscr", new CommandTextStyle(TextStyle.MATHSCR));
		map.put("scr", new CommandTextStyleTeX(TextStyle.MATHSCR));
		map.put("mathds", new CommandTextStyle(TextStyle.MATHDS));
		map.put("oldstylenums", new CommandTextStyle(TextStyle.OLDSTYLENUMS));

		map.put("mathsf", new CommandMathSf());

		map.put("sf", new CommandSf());

		map.put("mathrm", new CommandMathRm());

		map.put("rm", new CommandRm());

		map.put("mathit", new CommandMathIt());

		map.put("mit", map.get("mathit"));

		map.put("it", new CommandIt());

		map.put("mathtt", new CommandMathTt());

		map.put("tt", new CommandTt());

		map.put("mathbf", new CommandMathBf());

		map.put("bf", new CommandBf());

		map.put("bold", new CommandBold());

		map.put("boldsymbol", map.get("bold"));
		map.put("undertilde", new CommandUnderTilde());

		map.put("b", new CommandB());

		map.put("underaccent", new CommandUnderAccent());

		map.put("accentset", new CommandAccentSet());

		map.put("underset", new CommandUnderSet());

		map.put("overset", new CommandOverSet());

		map.put("stackbin", new CommandStackBin());

		map.put("stackrel", new CommandStackRel());

		map.put("questeq", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				final Atom at = new UnderOverAtom(Symbols.EQUALS,
						Symbols.QUESTION, new TeXLength(TeXLength.Unit.MU, 2.5),
						true, true);
				return at.changeType(TeXConstants.TYPE_RELATION);
			}
		});
		map.put("eqdef", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new BuildrelAtom(Symbols.EQUALS, new RomanAtom(
						TeXParser.getAtomForLatinStr("def", true)));
			}
		});
		map.put("shoveleft", new CommandShoveLeft());
		map.put("shoveright", new CommandShoveRight());

		map.put("hdotsfor", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
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
				tp.addToConsumer(new HdotsforAtom(n, x));
				return false;
			}
		});
		map.put("multicolumn", new CommandMulticolumn());

		map.put("intertext", new CommandInterText());

		map.put("cr", new CommandCr("cr"));
		map.put("newline", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				tp.close();
				if (tp.isArrayMode()) {
					tp.addToConsumer(EnvArray.RowSep.get());
					return false;
				}
				throw new ParseException(tp,
						"The macro \\newline must be used in an array");
			}
		});
		map.put("begin@math", new CommandMathStyles.OpenBracket(
				TeXConstants.Opener.BEGIN_MATH));
		map.put("end@math",
				new CommandMathStyles.CloseBracket(
						TeXConstants.Opener.BEGIN_MATH, TeXConstants.STYLE_TEXT,
						"The command \\) doesn't match any \\("));
		map.put("[", new CommandMathStyles.OpenBracket(
				TeXConstants.Opener.B_LSQBRACKET));
		map.put("]", new CommandMathStyles.CloseBracket(
				TeXConstants.Opener.B_LSQBRACKET, TeXConstants.STYLE_DISPLAY,
				"The command \\] doesn't match any \\["));

		map.put("displaymath", new CommandDisplayMath());

		map.put("(", new CommandMathStyles.OpenBracket(
				TeXConstants.Opener.B_LBRACKET));
		map.put(")",
				new CommandMathStyles.CloseBracket(
						TeXConstants.Opener.B_LBRACKET, TeXConstants.STYLE_TEXT,
						"The command \\) doesn't match any \\("));

		map.put("math", new CommandMath());

		map.put("iddots", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new IddotsAtom();
			}
		});
		map.put("ddots", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new DdotsAtom();
			}
		});
		map.put("vdots", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new VdotsAtom();
			}
		});
		map.put("smash", new CommandSmash());

		map.put("joinrel", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new SpaceAtom(TeXLength.Unit.MU, -3, 0, 0)
						.changeType(TeXConstants.TYPE_RELATION);
			}
		});
		map.put("mathclose", new CommandMathClose());

		map.put("mathopen", new CommandMathOpen());

		map.put("mathbin", new CommandMathBin());

		map.put("mathinner", new CommandMathInner());

		map.put("mathord", new CommandMathOrd());

		map.put("mathpunct", new CommandMathPunct());

		map.put("mathop", new CommandMathOp());

		map.put("mathrel", new CommandMathRel());

		map.put("underline", new CommandUnderline());

		map.put("overline", new CommandOverline());

		map.put("overparen", new CommandOverParen());

		map.put("underparen", new CommandUnderParen());

		map.put("overbrack", new CommandOverBrack());

		map.put("underbrack", new CommandUnderBrack());

		map.put("overbrace", new CommandOverBrace());

		map.put("underbrace", new CommandUnderBrace());

		map.put("prescript", new CommandPreScript());

		map.put("sideset", new CommandSideSet());

		map.put("xmapsto", new CommandXMapsTo());

		map.put("xlongequal", new CommandXLongEqual());

		map.put("xrightarrow", new CommandXRightArrow());

		map.put("xleftarrow", new CommandXLeftArrow());

		map.put("xhookleftarrow", new CommandXHookLeftArrow());

		map.put("xhookrightarrow", new CommandXHookRightArrow());

		map.put("xleftrightarrow", new CommandXLeftRightArrow());

		map.put("xrightharpoondown", new CommandXRightHarpoonDown());

		map.put("xrightharpoonup", new CommandXRightHarpoonUp());

		map.put("xleftharpoondown", new CommandXLeftHarpoonDown());

		map.put("xleftharpoonup", new CommandXLeftHarpoonUp());

		map.put("xleftrightharpoons", new CommandXLeftRightHarpoons());

		map.put("xrightleftharpoons", new CommandXRightLeftHarpoons());

		map.put("xrightsmallleftharpoons",
				new CommandXRightSmallLeftHarpoons());

		map.put("xsmallrightleftharpoons",
				new CommandXSmallRightLeftHarpoons());

		map.put("xleftrightarrows", new CommandXLeftRightArrows());

		map.put("xrightleftarrows", new CommandXRightLeftArrows());

		map.put("underleftrightarrow", new CommandUnderLeftRightArrow());

		map.put("underleftarrow", new CommandUnderLeftArrow());

		map.put("underrightarrow", new CommandUnderRightArrow());

		map.put("overleftrightarrow", new CommandOverLeftRightArrow());

		map.put("overleftarrow", new CommandOverLeftArrow());

		map.put("overrightarrow", new CommandOverRightArrow());

		map.put("ogonek", new CommandOgonek());
		map.put("k", new CommandOgonek());

		map.put("tcaron", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new tcaronAtom();
			}
		});
		map.put("Lcaron", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new LCaronAtom(true);
			}
		});
		map.put("lcaron", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new LCaronAtom(false);
			}
		});
		map.put("Tstroke", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new TStrokeAtom(true);
			}
		});
		map.put("tstroke", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new TStrokeAtom(false);
			}
		});
		map.put("IJ", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new IJAtom(true);
			}
		});
		map.put("ij", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new IJAtom(false);
			}
		});
		map.put("cedilla", new CommandCedilla());
		map.put("c", new CommandCedilla());

		map.put("~", new CommandTilde1());

		map.put("tilde", new CommandTilde2());

		map.put("widetilde", new CommandWideTilde());

		map.put("'", new CommandAcute1());

		map.put("acute", new CommandAcute2());

		map.put("skew", new CommandSkew());

		map.put("^", new CommandHat1());

		map.put("hat", new CommandHat2());

		map.put("widehat", new CommandWideHat());

		map.put("\"", new CommandQuotes());

		map.put("ddot", new CommandDDot());

		map.put("dddot", new CommandDDDot());

		map.put("ddddot", new CommandDDDDot());

		map.put("`", new CommandGrave1());

		map.put("grave", new CommandGrave2());

		map.put("=", new CommandEquals());

		map.put("bar", new CommandBar());

		map.put(".", new CommandDot1());

		map.put("dot", new CommandDot2());

		map.put("cyrddot", new CommandCyrDDot());

		map.put("u", new CommandBreve1());

		map.put("breve", new CommandBreve2());

		map.put("v", new CommandCheck());

		map.put("check", new CommandMap());

		map.put("H", new CommandH());

		map.put("t", new CommandT2());

		map.put("r", new CommandR());

		map.put("mathring", new CommandMathRing());

		map.put("U", new CommandU());

		map.put("vec", new CommandVec());

		map.put("accent", new CommandAccent());

		map.put("grkaccent", new CommandGrkAccent());

		map.put("underscore", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new UnderscoreAtom();
			}
		});
		map.put("_", map.get("underscore"));

		map.put("mbox", new CommandMBox());

		map.put("textsuperscript", new CommandTextSuperscript());

		map.put("textsubscript", new CommandTextSubscript());

		map.put("text", new CommandText2());

		map.put("pmb", new CommandPMB());

		map.put("textbf", new CommandTextBf());

		map.put("textit", new CommandTextIt());

		map.put("textrm", new CommandTextRm());

		map.put("textsf", new CommandTextSf());

		map.put("texttt", new CommandTextTt());

		map.put("textsc", new CommandTextSc());

		map.put("operatorname", new CommandOperatorName());

		map.put("sfrac", new CommandSfrac());

		map.put("cfrac", new CommandCFrac());

		map.put("the", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				final String name = tp.getArgAsCommand(true);
				tp.addToConsumer(new TheAtom(name));
				return false;
			}
		});

		map.put("setlength", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				final String name = tp.getArgAsCommand(true);
				TeXLength newLength = tp.getArgAsLength();
				if (newLength == null) {
					throw new ParseException(tp,
							"Invalid length in \\setlength");
				}
				tp.addToConsumer(new SetLengthAtom(newLength, name));
				return false;
			}
		});
		map.put("rule", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				TeXLength r = tp.getOptionAsLength(TeXLength.getZero());
				if (r == null) {
					r = new TeXLength();
				}
				TeXLength w = tp.getArgAsLength();
				if (w == null) {
					throw new ParseException(tp, "Invalid length in \\rule");
				}
				TeXLength h = tp.getArgAsLength();
				if (h == null) {
					throw new ParseException(tp, "Invalid length in \\rule");
				}
				tp.addToConsumer(new RuleAtom(w, h, r));
				return false;
			}
		});
		map.put("vrule", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				final TeXLength[] lengths = tp.getDimensions();
				tp.addToConsumer(new HVruleAtom(lengths[0], lengths[1],
						lengths[2], false));
				return false;
			}
		});
		map.put("hrule", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				final TeXLength[] lengths = tp.getDimensions();
				tp.addToConsumer(new HVruleAtom(lengths[0], lengths[1],
						lengths[2], true));
				return false;
			}
		});
		map.put("textvisiblespace", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				tp.skipPureWhites();
				final Atom a = new HVruleAtom(null,
						new TeXLength(TeXLength.Unit.EX, 0.3), null, false);
				return new RowAtom(new SpaceAtom(TeXLength.Unit.EM, 0.06), a,
						new HVruleAtom(new TeXLength(TeXLength.Unit.EM, 0.3),
								null, null, true),
						a);
			}
		});
		map.put("hspace", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				final TeXLength w = tp.getArgAsLength();
				if (w == null) {
					throw new ParseException(tp, "Invalid length in \\hspace");
				}
				tp.addToConsumer(new SpaceAtom(w.getUnit(), w.getL(), 0., 0.));
				return false;
			}
		});
		map.put("vspace", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				final TeXLength h = tp.getArgAsLength();
				if (h == null) {
					throw new ParseException(tp, "Invalid length in \\vspace");
				}
				tp.addToConsumer(new SpaceAtom(h.getUnit(), 0., h.getL(), 0.));
				return false;
			}
		});
		map.put("degree", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				tp.addToConsumer(SubSupCom.get(SubSupCom.getBase(tp), null,
						Symbols.CIRC));
				return false;
			}
		});
		map.put("sphat", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
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
				vra.setRaise(TeXLength.Unit.EX, raise);
				a = new SmashedAtom(vra);
				tp.addToConsumer(SubSupCom.get(SubSupCom.getBase(tp), null, a));
				return false;
			}
		});
		map.put("spbreve", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				final Atom ex = new SpaceAtom(TeXConstants.Muskip.NEGTHIN);
				Atom a = Symbols.BREVE;
				a = new StyleAtom(TeXConstants.STYLE_DISPLAY, a);
				final VRowAtom vra = new VRowAtom(a);
				vra.setRaise(TeXLength.Unit.EX, 1.1);
				a = new SmashedAtom(vra);
				final RowAtom ra = new RowAtom(ex, a);
				tp.addToConsumer(
						SubSupCom.get(SubSupCom.getBase(tp), null, ra));
				return false;
			}
		});
		map.put("spcheck", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				final Atom a = Symbols.VEE;
				tp.addToConsumer(SubSupCom.get(SubSupCom.getBase(tp), null, a));
				return false;
			}
		});
		map.put("sptilde", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				final Atom a = Symbols.SIM;
				tp.addToConsumer(SubSupCom.get(SubSupCom.getBase(tp), null, a));
				return false;
			}
		});
		map.put("spdot", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				Atom a = Symbols.NORMALDOT;
				a = new StyleAtom(TeXConstants.STYLE_DISPLAY, a);
				final VRowAtom vra = new VRowAtom(a);
				vra.setRaise(TeXLength.Unit.EX, 0.8);
				a = new SmashedAtom(vra);
				tp.addToConsumer(SubSupCom.get(SubSupCom.getBase(tp), null, a));
				return false;
			}
		});
		map.put("spddot", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				Atom a = Symbols.NORMALDOT;
				final RowAtom ra = new RowAtom(a, a);
				a = new StyleAtom(TeXConstants.STYLE_DISPLAY, ra);
				final VRowAtom vra = new VRowAtom(a);
				vra.setRaise(TeXLength.Unit.EX, 0.8);
				a = new SmashedAtom(vra);
				tp.addToConsumer(SubSupCom.get(SubSupCom.getBase(tp), null, a));
				return false;
			}
		});
		map.put("spdddot", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				Atom a = Symbols.NORMALDOT;
				final RowAtom ra = new RowAtom(a, a, a);
				a = new StyleAtom(TeXConstants.STYLE_DISPLAY, ra);
				final VRowAtom vra = new VRowAtom(a);
				vra.setRaise(TeXLength.Unit.EX, 0.8);
				a = new SmashedAtom(vra);
				tp.addToConsumer(SubSupCom.get(SubSupCom.getBase(tp), null, a));
				return false;
			}
		});
		map.put("log", new CommandOpName("log", false));
		map.put("lg", new CommandOpName("lg", false));
		map.put("ln", new CommandOpName("ln", false));
		map.put("lim", new CommandOpName("lim", true));
		map.put("sin", new CommandOpName("sin", false));
		map.put("arcsin", new CommandOpName("arcsin", false));
		map.put("sinh", new CommandOpName("sinh", false));
		map.put("cos", new CommandOpName("cos", false));
		map.put("arccos", new CommandOpName("arccos", false));
		map.put("cosh", new CommandOpName("cosh", false));
		map.put("cot", new CommandOpName("cot", false));
		map.put("arccot", new CommandOpName("arccot", false));
		map.put("coth", new CommandOpName("coth", false));
		map.put("tan", new CommandOpName("tan", false));
		map.put("arctan", new CommandOpName("arctan", false));
		map.put("tanh", new CommandOpName("tanh", false));
		map.put("sec", new CommandOpName("sec", false));
		map.put("arcsec", new CommandOpName("arcsec", false));
		map.put("sech", new CommandOpName("sech", false));
		map.put("csc", new CommandOpName("csc", false));
		map.put("arccsc", new CommandOpName("arccsc", false));
		map.put("csch", new CommandOpName("csch", false));
		map.put("arg", new CommandOpName("arg", false));
		map.put("ker", new CommandOpName("ker", false));
		map.put("dim", new CommandOpName("dim", false));
		map.put("hom", new CommandOpName("hom", false));
		map.put("exp", new CommandOpName("exp", false));
		map.put("deg", new CommandOpName("deg", false));
		map.put("max", new CommandOpName("max", true));
		map.put("min", new CommandOpName("min", true));
		map.put("sup", new CommandOpName("sup", true));
		map.put("inf", new CommandOpName("inf", true));
		map.put("det", new CommandOpName("det", true));
		map.put("Pr", new CommandOpName("Pr", true));
		map.put("gcd", new CommandOpName("gcd", true));
		map.put("limsup", new CommandOpName("lim", "sup", true));
		map.put("liminf", new CommandOpName("lim", "inf", true));
		map.put("injlim", new CommandOpName("inj", "lim", true));
		map.put("projlim", new CommandOpName("proj", "lim", true));
		map.put("varinjlim", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				Atom a = new RomanAtom(
						TeXParser.getAtomForLatinStr("lim", true));
				a = new UnderOverArrowAtom(a, false, false);
				a = a.changeType(TeXConstants.TYPE_BIG_OPERATOR);
				a.type_limits = TeXConstants.SCRIPT_LIMITS;
				tp.addToConsumer(a);
				return false;
			}
		});
		map.put("varprojlim", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				Atom a = new RomanAtom(
						TeXParser.getAtomForLatinStr("lim", true));
				a = new UnderOverArrowAtom(a, true, false);
				a = a.changeType(TeXConstants.TYPE_BIG_OPERATOR);
				a.type_limits = TeXConstants.SCRIPT_LIMITS;
				tp.addToConsumer(a);
				return false;
			}
		});
		map.put("varliminf", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				Atom a = new RomanAtom(
						TeXParser.getAtomForLatinStr("lim", true));
				a = new UnderlinedAtom(a);
				a = a.changeType(TeXConstants.TYPE_BIG_OPERATOR);
				a.type_limits = TeXConstants.SCRIPT_LIMITS;
				tp.addToConsumer(a);
				return false;
			}
		});
		map.put("varlimsup", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				Atom a = new RomanAtom(
						TeXParser.getAtomForLatinStr("lim", true));
				a = new OverlinedAtom(a);
				a = a.changeType(TeXConstants.TYPE_BIG_OPERATOR);
				a.type_limits = TeXConstants.SCRIPT_LIMITS;
				tp.addToConsumer(a);
				return false;
			}
		});
		map.put("with", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return Symbols.WITH
						.changeType(TeXConstants.TYPE_BINARY_OPERATOR);
			}
		});
		map.put("parr", new Command0A() {
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
		});
		map.put("copyright", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				Atom a = new RomanAtom(new CharAtom('c', false));
				a = new RaiseAtom(a, new TeXLength(TeXLength.Unit.EX, 0.2),
						TeXLength.getZero(), TeXLength.getZero());
				return new TextCircledAtom(a);
			}
		});
		map.put("L", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				final Atom a = new RowAtom(SymbolAtom.get("polishlcross"),
						new CharAtom('L', false));
				return new RomanAtom(a);
			}
		});
		map.put("l", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				final Atom a = new RowAtom(SymbolAtom.get("polishlcross"),
						new CharAtom('l', false));
				return new RomanAtom(a);
			}
		});
		map.put("Join", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				Atom a = new LapedAtom(SymbolAtom.get("ltimes"), 'r');
				a = new RowAtom(a, SymbolAtom.get("rtimes"));
				a = a.changeType(TeXConstants.TYPE_BIG_OPERATOR);
				a.type_limits = TeXConstants.SCRIPT_NORMAL;
				return a;
			}
		});
		map.put("notin", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new RowAtom(Symbols.NOT, Symbols.IN);
			}
		});
		map.put("ne", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new RowAtom(Symbols.NOT, Symbols.EQUALS);
			}
		});
		map.put("neq", map.get("ne"));
		map.put("JLaTeXMath", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new RowAtom(new CharAtom('J', TextStyle.MATHBB, true),
						new LaTeXAtom(),
						new CharAtom('M', TextStyle.MATHNORMAL, true),
						new CharAtom('a', TextStyle.MATHNORMAL, true),
						new CharAtom('t', TextStyle.MATHNORMAL, true),
						new CharAtom('h', TextStyle.MATHNORMAL, true));
			}
		});
		map.put("ldots", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				final Atom ldotp = Symbols.LDOTP;
				return new RowAtom(ldotp, ldotp, ldotp)
						.changeType(TeXConstants.TYPE_INNER);
			}
		});
		map.put("dotsc", map.get("ldots"));
		map.put("dots", map.get("ldots"));
		map.put("dotso", map.get("ldots"));
		map.put("cdots", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				final Atom cdotp = Symbols.CDOTP;
				return new RowAtom(cdotp, cdotp, cdotp)
						.changeType(TeXConstants.TYPE_INNER);
			}
		});
		map.put("dotsb", map.get("cdots"));
		map.put("dotsm", map.get("cdots"));
		map.put("dotsi", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				final Atom cdotp = Symbols.CDOTP;
				final RowAtom ra = new RowAtom(cdotp, cdotp, cdotp);
				return new RowAtom(new SpaceAtom(TeXConstants.Muskip.NEGTHIN),
						ra.changeType(TeXConstants.TYPE_INNER));
			}
		});
		map.put("relbar", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new SmashedAtom(Symbols.MINUS)
						.changeType(TeXConstants.TYPE_RELATION);
			}
		});
		map.put("mkern", new Command0AImpl() {
			@Override
			public boolean init(TeXParser tp) {
				final TeXLength len = tp.getArgAsLength();
				tp.addToConsumer(new SpaceAtom(len));
				return false;
			}
		});
		map.put("kern", map.get("mkern"));
		map.put("mspace", map.get("mkern"));
		map.put("hskip", map.get("mkern"));
		map.put("mskip", map.get("mkern"));
		map.put("strut", new Command0A() {
			@Override
			public Atom newI(TeXParser tp) {
				return new SpaceAtom(TeXLength.Unit.PT, 0., 8.6, 3.);
			}
		});
		map.put("iff",
				new Replacement("\\mathrel{\\;\\Longleftrightarrow\\;}"));
		map.put("bowtie", new Replacement(
				"\\mathrel{\\mathrel{\\triangleright}\\joinrel\\mathrel{\\triangleleft}}"));
		map.put("models", new Replacement(
				"\\mathrel{\\mathrel{\\vert}\\joinrel\\equals}"));
		map.put("implies",
				new Replacement("\\mathrel{\\;\\Longrightarrow\\;}"));
		map.put("impliedby",
				new Replacement("\\mathrel{\\;\\Longleftarrow\\;}"));
		map.put("mapsto",
				new Replacement("\\mathrel{\\mapstochar\\rightarrow}"));
		map.put("longmapsto",
				new Replacement("\\mathrel{\\mapstochar\\longrightarrow}"));
		map.put("Mapsto",
				new Replacement("\\mathrel{\\Mapstochar\\Rightarrow}"));
		map.put("mapsfrom",
				new Replacement("\\mathrel{\\leftarrow\\mapsfromchar}"));
		map.put("Mapsfrom",
				new Replacement("\\mathrel{\\Leftarrow\\Mapsfromchar}"));
		map.put("Longmapsto",
				new Replacement("\\mathrel{\\Mapstochar\\Longrightarrow}"));
		map.put("longmapsfrom",
				new Replacement("\\mathrel{\\longleftarrow\\mapsfromchar}"));
		map.put("Longmapsfrom",
				new Replacement("\\mathrel{\\Longleftarrow\\Mapsfromchar}"));
		map.put("arrowvert", new Replacement("\\vert"));
		map.put("Arrowvert", new Replacement("\\Vert"));
		map.put("aa", new Replacement("\\mathring{a}"));
		map.put("AA", new Replacement("\\mathring{A}"));
		map.put("ddag", new Replacement("\\ddagger"));
		map.put("dag", new Replacement("\\dagger"));
		map.put("Doteq", new Replacement("\\doteqdot"));
		map.put("doublecup", new Replacement("\\Cup"));
		map.put("doublecap", new Replacement("\\Cap"));
		map.put("llless", new Replacement("\\lll"));
		map.put("gggtr", new Replacement("\\ggg"));
		map.put("Alpha", new Replacement("\\mathord{\\mathrm{A}}"));
		map.put("Beta", new Replacement("\\mathord{\\mathrm{B}}"));
		map.put("Epsilon", new Replacement("\\mathord{\\mathrm{E}}"));
		map.put("Zeta", new Replacement("\\mathord{\\mathrm{Z}}"));
		map.put("Eta", new Replacement("\\mathord{\\mathrm{H}}"));
		map.put("Iota", new Replacement("\\mathord{\\mathrm{I}}"));
		map.put("Kappa", new Replacement("\\mathord{\\mathrm{K}}"));
		map.put("Mu", new Replacement("\\mathord{\\mathrm{M}}"));
		map.put("Nu", new Replacement("\\mathord{\\mathrm{N}}"));
		map.put("Omicron", new Replacement("\\mathord{\\mathrm{O}}"));
		map.put("Rho", new Replacement("\\mathord{\\mathrm{P}}"));
		map.put("Tau", new Replacement("\\mathord{\\mathrm{T}}"));
		map.put("Chi", new Replacement("\\mathord{\\mathrm{X}}"));
		map.put("hdots", new Replacement("\\ldots"));
		map.put("restriction", new Replacement("\\upharpoonright"));
		map.put("celsius", new Replacement("\\mathord{{}^\\circ\\mathrm{C}}"));
		map.put("micro", new Replacement("\\textmu"));
		map.put("marker", new Replacement(
				"{\\kern{0.25ex}\\rule{0.5ex}{1.2ex}\\kern{0.25ex}}"));
		map.put("hybull", new Replacement("\\rule[0.6ex]{1ex}{0.2ex}"));
		map.put("block", new Replacement("\\rule{1ex}{1.2ex}"));
		map.put("uhblk", new Replacement("\\rule[0.6ex]{1ex}{0.6ex}"));
		map.put("lhblk", new Replacement("\\rule{1ex}{0.6ex}"));
		map.put("lVert", new Replacement("\\Vert"));
		map.put("rVert", new Replacement("\\Vert"));
		map.put("lvert", new Replacement("\\vert"));
		map.put("rvert", new Replacement("\\vert"));
		map.put("copyright",
				new Replacement("\\textcircled{\\raisebox{0.2ex}{c}}"));
		map.put("glj", new Replacement("\\mathbin{\\rlap{>}\\!<}"));
		map.put("gla", new Replacement("\\mathbin{><}"));
		map.put("alef", new Replacement("\\aleph"));
		map.put("alefsym", new Replacement("\\aleph"));
		map.put("And", new Replacement("{\\;\\textampersand\\;}"));
		map.put("and", new Replacement("\\land"));
		map.put("ang", new Replacement("\\angle"));
		map.put("Reals", new Replacement("\\mathbb{R}"));
		map.put("exist", new Replacement("\\exists"));
		map.put("hAar", new Replacement("\\Leftrightarrow"));
		map.put("C", new Replacement("\\mathbb{C}"));
		map.put("Complex", map.get("C"));
		map.put("N", new Replacement("\\mathbb{N}"));
		map.put("natnums", map.get("N"));
		map.put("Q", new Replacement("\\mathbb{Q}"));
		map.put("R", new Replacement("\\mathbb{R}"));
		map.put("real", map.get("R"));
		map.put("reals", map.get("R"));
		map.put("Z", new Replacement("\\mathbb{Z}"));
		map.put("Dagger", new Replacement("\\ddagger"));
		map.put("diamonds", new Replacement("\\diamondsuit"));
		map.put("clubs", new Replacement("\\clubsuit"));
		map.put("hearts", new Replacement("\\heartsuit"));
		map.put("spades", new Replacement("\\spadesuit"));
		map.put("infin", new Replacement("\\infty"));
		map.put("isin", new Replacement("\\in"));
		map.put("plusmn", new Replacement("\\pm"));
		map.put("sube", new Replacement("\\subseteq"));
		map.put("supe", new Replacement("\\supseteq"));
		map.put("sdot", new Replacement("\\cdot"));
		map.put("empty", new Replacement("\\emptyset"));
		map.put("O", map.get("empty"));
		map.put("sub", new Replacement("\\subset"));
		map.put("lang", new Replacement("\\langle"));
		map.put("rang", new Replacement("\\rangle"));
		map.put("bull", new Replacement("\\bullet"));
		map.put("geneuro", new Replacement("\\texteuro"));
		map.put("geneuronarrow", map.get("geneuro"));
		map.put("geneurowide", map.get("geneuro"));

		// TODO: check if this is useful or not
		map.put("jlmXML", new CommandJlmXML());

		// caret for the editor
		map.put("jlmcursor", new CommandJlmCursor());
		// for the editor
		map.put("jlmselection", new CommandJlmSelection());

		// eg
		// \imagebasesixtyfour{40}{36}{data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACgAAAAkCAIAAAB0Xu9BAAAAKUlEQVR42u3NMQEAAAwCIPuX1hbbAwVIn0QsFovFYrFYLBaLxWKx+M4AoNrQEWa6zscAAAAASUVORK5CYII=}
		map.put("imagebasesixtyfour", new CommandImageBase64());

		map.put("&", new Replacement("\\textampersand"));
		map.put("%", new Replacement("\\textpercent"));
		map.put("$", new Replacement("\\textdollar"));
		map.put("dollar", map.get("$"));

	}

	public static AtomConsumer get(final String name) {
		final Command c = map.get(name);
		if (c != null) {
			return c.duplicate();
		}

		return null;
	}

	public static boolean exec(final TeXParser tp, final String name) {
		final Command c = map.get(name);
		if (c != null) {
			tp.cancelPrevPos();
			final AtomConsumer cons = c.duplicate();
			if (cons.init(tp)) {
				tp.addConsumer(cons);
			}
			return true;
		}

		return false;
	}

	static Command getUnsafe(final String name) {
		return map.get(name);
	}

	public static AtomConsumer getDollar() {
		return dollar;
	}

	public static AtomConsumer getDollarDollar() {
		return dollardollar;
	}

	public static void getAll(final List<String> l) {
		for (final String k : map.keySet()) {
			l.add(k);
		}
	}
}
