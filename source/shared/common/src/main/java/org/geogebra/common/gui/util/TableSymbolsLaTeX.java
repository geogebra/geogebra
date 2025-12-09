/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.gui.util;

/**
 * Arrays of LaTeX formula strings used when building tables and lists.
 * 
 * @author G Sturr
 * 
 */
public class TableSymbolsLaTeX {

	public final static String[] miscSymbols = {

			"\\#", "\\&", "\\prime", "\\backprime",

			"\\angle", "\\measuredangle", "\\sphericalangle",

			"\\nabla", "\\neg", "\\nexists",

			"\\varnothing", "\\emptyset", "\\exists", "\\forall", "\\infty",
			"\\surd", "\\top", "\\bot", "\\diagdown", "\\diagup",

			"\\bigstar", "\\lozenge", "\\blacklozenge", "\\square",
			"\\blacksquare", "\\triangle", "\\triangledown", "\\blacktriangle",
			"\\blacktriangledown",

			"\\spadesuit", "\\clubsuit", "\\diamondsuit", "\\heartsuit",
			"\\flat", "\\natural", "\\sharp", "\\textdbend"

	};

	public final static String[] roots_fractions = {

			"\\frac{a}{b}", "x^{a}", "x_{a}", "\\sqrt{x}", "\\sqrt[n]{x}",
			"\\binom{a}{b}",

	};

	public final static String[] sums = {

			"\\sum{ }", "\\sum_{a}^{b}{ }", "\\int{ }", "\\int_{a}^{b}{ }",
			"\\oint{ }", "\\oint_{a}^{b}{ }", "\\lim_{ x \\to \\infty }"

	};

	public final static String[] accents = {

			"\\acute{x}", "\\grave{x}", "\\tilde{x}", "\\bar{x}", "\\breve{x}",
			"\\check{x}", "\\hat{x}", "\\vec{x}", "\\dot{x}", "\\ddot{x}",
			"\\dddot{x}", "\\mathring{x}",

	};

	public final static String[] accentsExtended = {

			"\\overline{xx}", "\\underline{xx}", "\\overbrace{xx}",
			"\\underbrace{xx}", "\\overleftarrow{xx}", "\\underleftarrow{xx}",
			"\\overrightarrow{xx}", "\\underrightarrow{xx}",
			"\\overleftrightarrow{xx}", "\\underleftrightarrow{xx}",

			"\\widetilde{xx} ", "\\widehat{xx}"

	};

	public final static String[] brackets = {

			"\\left(   \\right) ", "\\left [  \\right ] ",
			"\\left\\{  \\right\\} ", "\\left|  \\right| ",
			"\\left\\Vert \\right\\Vert ", "\\left\\langle \\right\\rangle ",
			"\\left\\lceil \\right\\rceil ", "\\left\\lfloor \\right\\rfloor ",
			"\\left\\lgroup \\right\\rgroup ",
			"\\left\\lmoustache \\right\\rmoustache ", "\\shadowbox{xx}",
			"\\fbox{xx}", "\\doublebox{xx}", "\\ovalbox{xx}"
	};

	public final static String[] matrices = {

			"\\begin{array}{} a & b & c \\end{array} ",

			"\\begin{array}{} a \\\\ b \\\\ c \\end{array} ",

			"\\begin{array}{} a & b \\\\ c & d \\\\ \\end{array} ",

			"\\begin{array}{} a & b & c \\\\ d & e & f \\\\ g & h & i \\\\ \\end{array}",

	};

	public final static String[] operators = {

			"\\pm", "\\mp", "\\times", "\\div", "\\cdot", "\\ast", "\\star",
			"\\dagger", "\\ddagger", "\\amalg", "\\cap", "\\cup", "\\uplus",
			"\\sqcap", "\\sqcup", "\\vee", "\\wedge", "\\oplus", "\\ominus",
			"\\otimes", "\\circ", "\\bullet", "\\diamond", "\\lhd", "\\rhd",
			"\\unlhd", "\\unrhd", "\\oslash", "\\odot", "\\bigcirc",
			"\\triangleleft", "\\Diamond", "\\bigtriangleup",
			"\\bigtriangledown", "\\Box", "\\triangleright", "\\setminus",
			"\\wr"

	};

	public final static String[] relations = {

			"\\le", "\\ge", "\\neq", "\\sim", "\\ll", "\\gg", "\\doteq",
			"\\simeq", "\\subset", "\\supset", "\\approx", "\\asymp",
			"\\subseteq", "\\supseteq", "\\cong", "\\smile", "\\sqsubset",
			"\\sqsupset", "\\equiv", "\\frown", "\\sqsubseteq", "\\sqsupseteq",
			"\\propto", "\\bowtie", "\\in", "\\ni", "\\prec", "\\succ",
			"\\vdash", "\\dashv", "\\preceq", "\\succeq", "\\models", "\\perp",
			"\\parallel", "\\|", "\\mid"

	};

	public final static String[] negations = { "\\nmid", "\\nleq", "\\ngeq",
			"\\nsim", "\\ncong", "\\nparallel", "\\not<", "\\not>", "\\not=",
			"\\not\\le", "\\not\\ge", "\\not\\sim", "\\not\\approx",
			"\\not\\cong", "\\not\\equiv", "\\not\\parallel", "\\nless",
			"\\ngtr", "\\lneq", "\\gneq", "\\lnsim", "\\lneqq", "\\gneqq",

	};

	public final static String[] arrows = {

			"\\xleftarrow{xx}", "\\xrightarrow{xx}",

			"\\leftarrow", "\\rightarrow", "\\leftrightarrow",

			"\\Leftarrow", "\\Rightarrow", "\\Leftrightarrow",

			"\\longleftarrow", "\\longrightarrow", "\\longleftrightarrow",

			"\\Longleftarrow", "\\Longrightarrow", "\\Longleftrightarrow",

			"\\mapsto", "\\longmapsto",

			"\\hookleftarrow", "\\hookrightarrow",

			"\\leftharpoonup", "\\leftharpoondown", "\\rightharpoonup",
			"\\rightharpoondown", "\\rightleftharpoons",

			"\\leadsto", "\\uparrow", "\\downarrow", "\\updownarrow",

			"\\Uparrow", "\\Downarrow", "\\Updownarrow",

			"\\nearrow", "\\searrow", "\\swarrow", "\\nwarrow",

	};

}
