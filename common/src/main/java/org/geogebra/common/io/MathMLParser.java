/*
 * Created on 2003-11-02
 */

package org.geogebra.common.io;

import java.util.HashMap;
import java.util.Objects;

import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.util.Greek;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * Apache 2.0 Licence
 * 
 * Original from http://www.tilman.de/programme/mathparser/download_en.html
 * 
 * Parses Presentation MathML
 * 
 * Alterations by GeoGebra
 * 
 * @author (c) Tilman Walther
 * @author (c) Martin Wilke
 */
public class MathMLParser {
	/**
	 * The place holder for blocks in substitutions. If a substitution contains
	 * a block place holder it is replaced by the LaTeX representation of the
	 * followig block.<br>
	 * Syntax: PH_BLOCKSTART + blockNumber + PH_BLOCKEND, e.g. '#BLOCK1#'.
	 */
	private final static String PH_BLOCK_START = "%BLOCK";
	private final static char PH_BLOCK_END = '%';

	private final static char[] specialCharacters = { '%', '_', '$' };
	private final static char[] leftBraces = { '(', '{', '[' };
	private final static char[] rightBraces = { ')', '{', ']' };

	private HashMap<String, String> substitutions;
	// private StringBuilder result;
	private String strBuf;
	private int pos;
	private boolean wrappedEntities;
	private boolean skipUnknownEntities;
	private boolean geogebraSyntax;

	// temporary variables (declared global for better performance)
	// protected String startTag, endTag;
	private String nextTag;
	private StringBuilder tagBuf = new StringBuilder(200); // used by
															// readNextTag() &
															// getBlockEnd()
	private StringBuilder entity = new StringBuilder(32); // used by
															// replaceEntities()
	private String entitySubst = ""; // used by replaceEntities()
	private boolean closeBracketNext = false;

	private static HashMap<String, String> geogebraMap;
	private static HashMap<String, String> latexMap;

	/**
	 * @return tag-&gt;geogebra syntax map
	 */
	static synchronized HashMap<String, String> getGeogebraMap() {

		if (geogebraMap == null) {
			geogebraMap = new HashMap<>();

			// Tags:
			geogebraMap.put("<mfrac>", "((%BLOCK1%) / (%BLOCK2%))");
			geogebraMap.put("<msup>", "((%BLOCK1%)^(%BLOCK2%))");
			geogebraMap.put("<msub>", "%BLOCK1%_{%BLOCK2%}"); // TODO _{1} -> _1
																// at
																// the end of
																// parsing
			geogebraMap.put("<msqrt>", "sqrt(%BLOCK1%)");
			geogebraMap.put("<mroot>", "nroot(%BLOCK1%,%BLOCK2%)");
			geogebraMap.put("<mfenced>", "(%BLOCK1%)"); // e.g. binomial
														// coefficient,
														// FIXME
			geogebraMap.put("<mfenced open=\"|\" close=\"|\">",
					"abs(%BLOCK1%)"); // abs. value
			geogebraMap.put("<msubsup>", "(%BLOCK1%_{%BLOCK2%})^(%BLOCK3%)"); // ignored
																				// for
																				// now,
																				// FIXME
																				// (subscripted
																				// variable
																				// powered)
			geogebraMap.put("<munderover>", "%BLOCK1%(%BLOCK2%,%BLOCK3%,"); // ignored
																			// for
																			// now,
																			// FIXME
																			// (subscripted
																			// variable
																			// powered)
			geogebraMap.put("<munder>", "%BLOCK1%"); // ignored for now, FIXME
			geogebraMap.put("<mtable>", "{%BLOCKS%}");
			geogebraMap.put("<mtr>", "{%BLOCKS%}, ");
			geogebraMap.put("<mtd>", "%BLOCK1%, ");

			geogebraMap.put("&#x222B;", "ggbMathmlIntegral");
			geogebraMap.put("&#x2211;", "ggbMathmlSum");
			// Entities
			geogebraMap.put("&dot;", "* ");
			geogebraMap.put("&sdot;", "* ");
			geogebraMap.put("&middot;", "* ");
			geogebraMap.put("&times;", "* ");
			geogebraMap.put("&equals;", " = ");
			geogebraMap.put("&forall;", "# ");
			geogebraMap.put("&exist;", "# ");
			geogebraMap.put("&#x220d;", "# ");
			geogebraMap.put("&lowast;", "* ");
			geogebraMap.put("&minus;", "- ");
			geogebraMap.put("&frasl;", "/ ");
			geogebraMap.put("&ratio;", ": ");
			geogebraMap.put("&lt;", "< ");
			geogebraMap.put("&gt;", "> ");
			geogebraMap.put("&cong;", "# ");
			geogebraMap.put("&InvisibleTimes;", " ");

			// Pfeile
			geogebraMap.put("&harr;", "# ");
			geogebraMap.put("&larr;", "# ");
			geogebraMap.put("&rarr;", "# ");
			geogebraMap.put("&hArr;", "# ");
			geogebraMap.put("&lArr;", "# ");
			geogebraMap.put("&rArr;", "# ");

			// dynamische Zeichen
			geogebraMap.put("&sum;", "# ");
			geogebraMap.put("&prod;", "# ");
			geogebraMap.put("&Integral;", "# ");
			geogebraMap.put("&dd;", "d ");

			for (Greek ch : Greek.values()) {
				geogebraMap.put(ch.getHTML(), ch.unicode + "");
			}

			// griechisches Alphabet ... may be implemented by Java unicode
			// codes.
			// geogebraMap.put("&alpha;", Unicode.alpha + "");
			// geogebraMap.put("&beta;", Unicode.beta + "");
			// geogebraMap.put("&gamma;", Unicode.gamma + "");
			// geogebraMap.put("&delta;", Unicode.delta + "");
			// geogebraMap.put("&epsi;", Unicode.epsilon + "");
			// geogebraMap.put("&eta;", Unicode.eta + "");
			// geogebraMap.put("&iota;", Unicode.iota + "");
			// geogebraMap.put("&kappa;", Unicode.kappa + "");
			// geogebraMap.put("&lambda;", Unicode.lambda + "");
			// geogebraMap.put("&mu;", Unicode.mu + "");
			// geogebraMap.put("&nu;", Unicode.nu + "");
			// geogebraMap.put("&omicron;", Unicode.omicron + "");
			// geogebraMap.put("&pi;", Unicode.pi + "");
			// geogebraMap.put("&theta;", Unicode.theta + "");
			// geogebraMap.put("&rho;", Unicode.rho + "");
			// geogebraMap.put("&sigma;", Unicode.sigma + "");
			// geogebraMap.put("&tau;", Unicode.tau + "");
			// geogebraMap.put("&upsilon;", Unicode.upsilon + "");
			// geogebraMap.put("&phi;", Unicode.phi + "");// \\varphi
			// geogebraMap.put("&chi;", Unicode.chi + "");
			// geogebraMap.put("&omega;", Unicode.omega + "");
			// geogebraMap.put("&xi;", Unicode.xi + "");
			// geogebraMap.put("&psi;", Unicode.psi + "");
			// geogebraMap.put("&zeta;", Unicode.zeta + "");

			geogebraMap.put("&ohgr;", Unicode.omega + "");
			geogebraMap.put("&mgr;", Unicode.mu + "");
			geogebraMap.put("&piv;", Unicode.pi + ""); // \\varpi
			geogebraMap.put("&phiv;", Unicode.phi + "");
			geogebraMap.put("&pgr;", Unicode.pi + "");
			geogebraMap.put("&rgr;", Unicode.rho + "");
			geogebraMap.put("&tgr;", Unicode.tau + "");
			geogebraMap.put("&sigmaf;", Unicode.sigmaf + ""); // \\varsigma

			// geogebraMap.put("&Delta;", Unicode.Delta + "");
			// geogebraMap.put("&Phi;", Unicode.Phi + "");
			// geogebraMap.put("&Gamma;", Unicode.Gamma + "");
			// geogebraMap.put("&Lambda;", Unicode.Lambda + "");
			// geogebraMap.put("&Pi;", Unicode.Pi + "");
			// geogebraMap.put("&Theta;", Unicode.Theta + "");
			// geogebraMap.put("&Sigma;", Unicode.Sigma + "");
			// geogebraMap.put("&Upsilon;", Unicode.Upsilon + "");
			// geogebraMap.put("&Omega;", Unicode.Omega + "");
			// geogebraMap.put("&Xi;", Unicode.Xi + "");
			// geogebraMap.put("&Psi;", Unicode.Psi + "");

			geogebraMap.put("&epsiv;", Unicode.epsilon + "");
			geogebraMap.put("&phgr;", Unicode.phi + "");
			geogebraMap.put("&ggr;", Unicode.gamma + "");
			geogebraMap.put("&eegr;", Unicode.eta + "");
			geogebraMap.put("&igr;", Unicode.iota + "");
			geogebraMap.put("&phgr;", Unicode.phi + "");
			geogebraMap.put("&kgr;", Unicode.kappa + "");
			geogebraMap.put("&lgr;", Unicode.lambda + "");
			geogebraMap.put("&ngr;", Unicode.nu + "");
			geogebraMap.put("&ogr;", Unicode.omega + "");
			geogebraMap.put("&thgr;", Unicode.theta + "");
			geogebraMap.put("&sgr;", Unicode.sigma + "");
			geogebraMap.put("&ugr;", Unicode.upsilon + "");
			geogebraMap.put("&zgr;", Unicode.zeta + "");
			geogebraMap.put("&Agr;", Unicode.Alpha + "");
			geogebraMap.put("&Bgr;", Unicode.Beta + "");
			geogebraMap.put("&KHgr;", Unicode.Chi + "");
			geogebraMap.put("&Egr;", Unicode.Epsilon + "");
			geogebraMap.put("&PHgr;", Unicode.Phi + "");
			geogebraMap.put("&Ggr;", Unicode.Gamma + "");
			geogebraMap.put("&EEgr;", Unicode.Eta + "");
			geogebraMap.put("&Igr;", Unicode.Iota + "");
			geogebraMap.put("&THgr;", Unicode.Theta + "");
			geogebraMap.put("&Kgr;", Unicode.Kappa + "");
			geogebraMap.put("&Lgr;", Unicode.Lambda + "");
			geogebraMap.put("&Mgr;", Unicode.Mu + "");
			geogebraMap.put("&Ngr;", Unicode.Nu + "");
			geogebraMap.put("&Ogr;", Unicode.Omicron + "");
			geogebraMap.put("&Pgr;", Unicode.Pi + "");
			geogebraMap.put("&Rgr;", Unicode.Rho + "");
			geogebraMap.put("&Sgr;", Unicode.Sigma + "");
			geogebraMap.put("&Tgr;", Unicode.Tau + "");
			geogebraMap.put("&Ugr;", Unicode.Upsilon + "");
			geogebraMap.put("&OHgr;", Unicode.Omega + "");
			geogebraMap.put("&Zgr;", Unicode.Zeta + "");

			// Pfeile und andere Operatoren
			geogebraMap.put("&#x2212;", "-");
			geogebraMap.put("&perp;", "# ");
			geogebraMap.put("&sim;", "~ ");
			geogebraMap.put("&prime;", "# ");
			geogebraMap.put("&le;", Unicode.LESS_EQUAL + "");
			geogebraMap.put("&ge;", Unicode.GREATER_EQUAL + "");
			geogebraMap.put("&infin;", Unicode.INFINITY + "");
			geogebraMap.put("&clubs;", "# ");
			geogebraMap.put("&diams;", "# ");
			geogebraMap.put("&hearts;", "# ");
			geogebraMap.put("&spades;", "# ");
			geogebraMap.put("&PlusMinus;", Unicode.PLUSMINUS + "");
			geogebraMap.put("&Prime;", "# ");
			geogebraMap.put("&prop;", "# ");
			geogebraMap.put("&part;", "# ");
			geogebraMap.put("&bull;", "# ");
			geogebraMap.put("&ne;", Unicode.NOTEQUAL + "");
			geogebraMap.put("&equiv;", "# ");
			geogebraMap.put("&asymp;", "# ");
			geogebraMap.put("&hellip;", "... ");
			geogebraMap.put("&VerticalBar;", "# ");
			geogebraMap.put("&crarr;", "# ");
			geogebraMap.put("&alefsym;", "# ");
			geogebraMap.put("&image;", "# "); // ???
			geogebraMap.put("&real;", "# "); // ???
			geogebraMap.put("&weierp;", "# ");
			geogebraMap.put("&otimes;", "# ");
			geogebraMap.put("&oplus;", "# ");
			geogebraMap.put("&empty;", "# ");
			geogebraMap.put("&cap;", "# ");
			geogebraMap.put("&cup;", "# ");
			geogebraMap.put("&sup;", "# ");
			geogebraMap.put("&supe;", "# ");
			geogebraMap.put("&nsub;", "# ");
			geogebraMap.put("&sub;", "# ");
			geogebraMap.put("&sube;", "# ");
			geogebraMap.put("&isin;", "# ");
			geogebraMap.put("&notin;", "# ");
			geogebraMap.put("&ang;", "# ");
			geogebraMap.put("&nabla;", "# ");
			geogebraMap.put("&radic;", "# ");
			geogebraMap.put("&and;", "# ");
			geogebraMap.put("&or;", "# ");
			geogebraMap.put("&and;", "# ");
			geogebraMap.put("&ang;", "# ");
			geogebraMap.put("&angle;", "# ");
			geogebraMap.put("&ap;", "# ");
			geogebraMap.put("&approx;", "# ");
			geogebraMap.put("&bigoplus;", "# ");
			geogebraMap.put("&bigotimes;", "# ");
			geogebraMap.put("&bot;", "# ");
			geogebraMap.put("&bottom;", "# ");
			geogebraMap.put("&cap;", "# ");
			geogebraMap.put("&CirclePlus;", "# ");
			geogebraMap.put("&CircleTimes;", "# ");
			geogebraMap.put("&cong;", "# ");
			geogebraMap.put("&Congruent;", "# ");
			geogebraMap.put("&cup;", "# ");
			geogebraMap.put("&darr;", "# ");
			geogebraMap.put("&dArr;", "# ");
			geogebraMap.put("&Del;", "# ");
			geogebraMap.put("&Del;", "# ");
			geogebraMap.put("&DifferentialD;", "\u2146 ");
			geogebraMap.put("&DoubleLeftArrow;", "# ");
			geogebraMap.put("&DoubleLeftRightArrow;", "# ");
			geogebraMap.put("&DoubleRightArrow;", "# ");
			geogebraMap.put("&DoubleUpArrow;", "# ");
			geogebraMap.put("&downarrow;", "# ");
			geogebraMap.put("&Downarrow;", "# ");
			geogebraMap.put("&DownArrow;", "# ");
			geogebraMap.put("&Element;", "# ");
			geogebraMap.put("&emptyv;", "# ");
			geogebraMap.put("&equiv;", "# ");
			geogebraMap.put("&exist;", "# ");
			geogebraMap.put("&Exist;", "# ");
			geogebraMap.put("&exponentiale;", "\u2147 ");
			geogebraMap.put("&forall;", "# ");
			geogebraMap.put("&ForAll;", "# ");
			geogebraMap.put("&geq;", Unicode.GREATER_EQUAL + "");
			geogebraMap.put("&GreaterEqual;", Unicode.GREATER_EQUAL + "");
			geogebraMap.put("&harr;", "# ");
			geogebraMap.put("&hArr;", "# ");
			geogebraMap.put("&iff;", "# ");
			geogebraMap.put("&Implies;", "# ");
			geogebraMap.put("&in;", "# ");
			geogebraMap.put("&int;", "# ");
			geogebraMap.put("&Integral;", "# ");
			geogebraMap.put("&isin;", "# ");
			geogebraMap.put("&isinv;", "# ");
			geogebraMap.put("&diam;", "# ");
			geogebraMap.put("&diamond;", "# ");
			geogebraMap.put("&lang;", "# ");
			geogebraMap.put("&langle;", "# ");
			geogebraMap.put("&larr;", "# ");
			geogebraMap.put("&lArr;", "# ");
			geogebraMap.put("&LeftAngleBracket;", "# ");
			geogebraMap.put("&Leftarrow;", "# ");
			geogebraMap.put("&LeftArrow;", "# ");
			geogebraMap.put("&leftrightarrow;", "# ");
			geogebraMap.put("&Leftrightarrow;", "# ");
			geogebraMap.put("&LeftRightArrow;", "# ");
			geogebraMap.put("&leq;", Unicode.LESS_EQUAL + "");
			geogebraMap.put("&leq;", Unicode.LESS_EQUAL + "");
			geogebraMap.put("&Longleftrightarrow;", "# ");
			geogebraMap.put("&minus;", "- ");
			geogebraMap.put("&nabla;", "# ");
			geogebraMap.put("&NotElement;", "# ");
			geogebraMap.put("&NotEqual;", Unicode.NOTEQUAL + ""); // ??? is this
																	// good
																	// in LaTeX?
			geogebraMap.put("&notin;", "# ");
			geogebraMap.put("&oplus;", "# ");
			geogebraMap.put("&or;", "# ");
			geogebraMap.put("&otimes;", "# ");
			geogebraMap.put("&part;", "# ");
			geogebraMap.put("&partialD;", "# ");
			geogebraMap.put("&perp;", "# ");
			geogebraMap.put("&prod;", "# ");
			geogebraMap.put("&Product;", "# ");
			geogebraMap.put("&rang;", "# ");
			geogebraMap.put("&rangle;", "# ");
			geogebraMap.put("&rarr;", "# ");
			geogebraMap.put("&rArr;", "# ");
			geogebraMap.put("&RightAngleBracket;", "# ");
			geogebraMap.put("&rightarrow;", "# ");
			geogebraMap.put("&Rightarrow;", "# ");
			geogebraMap.put("&RightArrow;", "# ");
			geogebraMap.put("&sdot;", "* ");
			geogebraMap.put("&sim;", "# ");
			geogebraMap.put("&prop;", "# ");
			geogebraMap.put("&Proportional;", "# ");
			geogebraMap.put("&propto;", "# ");
			geogebraMap.put("&sub;", "# ");
			geogebraMap.put("&sube;", "# ");
			geogebraMap.put("&subE;", "# ");
			geogebraMap.put("&subset;", "# ");
			geogebraMap.put("&subseteq;", "# ");
			geogebraMap.put("&subseteqq;", "# ");
			geogebraMap.put("&SubsetEqual;", "# ");
			geogebraMap.put("&sum;", "# ");
			geogebraMap.put("&Sum;", "# ");
			geogebraMap.put("&sup;", "# ");
			geogebraMap.put("&supe;", "# ");
			geogebraMap.put("&supE;", "# ");
			geogebraMap.put("&Superset;", "# ");
			geogebraMap.put("&SupersetEqual;", "# ");
			geogebraMap.put("&supset;", "# ");
			geogebraMap.put("&supseteq;", "# ");
			geogebraMap.put("&supseteqq;", "# ");
			geogebraMap.put("&Tilde;", "# ");
			geogebraMap.put("&TildeFullEqual;", "# ");
			geogebraMap.put("&TildeTilde;", "# ");
			geogebraMap.put("&tprime;", "\u2034 ");
			geogebraMap.put("&uarr;", "# ");
			geogebraMap.put("&uArr;", "# ");
			geogebraMap.put("&uparrow;", "# ");
			geogebraMap.put("&Uparrow;", "# ");
			geogebraMap.put("&UpArrow;", "# ");
			geogebraMap.put("&UpTee;", "# ");
			geogebraMap.put("&varnothing;", "# ");
			geogebraMap.put("&varpropto;", "# ");
			geogebraMap.put("&vee;", "# ");
			geogebraMap.put("&vprop;", "# ");
			geogebraMap.put("&wedge;", "# ");
			geogebraMap.put("&xoplus;", "# ");
			geogebraMap.put("&xotime;", "# ");
			geogebraMap.put("&Space;", " ");
			geogebraMap.put("&colon;", ":");
			geogebraMap.put("&ApplyFunction;", " ");
			geogebraMap.put("&squ;", " ");
			geogebraMap.put("&#x2212;", "- ");
			geogebraMap.put("&#x2192;", "# ");
			geogebraMap.put("&#x222b;", "# ");
			geogebraMap.put("&#x2061;", "");
		}

		return geogebraMap;
	}

	private static synchronized HashMap<String, String> getLatexMap() {

		if (latexMap == null) {
			latexMap = new HashMap<>();

			// Tags:
			latexMap.put("<mfrac>", "\\frac{%BLOCK1%}{%BLOCK2%}");
			latexMap.put("<msup>", "%BLOCK1%^{%BLOCK2%}");
			latexMap.put("<msub>", "%BLOCK1%_{%BLOCK2%}");
			latexMap.put("<msqrt>", "\\sqrt{%BLOCK1%}");
			latexMap.put("<mroot>", "\\sqrt[%BLOCK2%]{%BLOCK1%}");
			latexMap.put("<mfenced>", "\\left(%BLOCK1%\\right)");
			latexMap.put("<msubsup>", "%BLOCK1%_{%BLOCK2%}^{%BLOCK3%}");
			latexMap.put("<munderover>", "%BLOCK1%_{%BLOCK2%}^{%BLOCK3%}");
			latexMap.put("<munder>", "%BLOCK1%_{%BLOCK2%}");
			latexMap.put("<mtable>", "\\matrix{%BLOCKS%}");
			latexMap.put("<mtr>", "%BLOCKS%\\cr");
			latexMap.put("<mtd>", "%BLOCK1%&");

			// Entities
			latexMap.put("&dot;", "\\cdot ");
			latexMap.put("&sdot;", "\\cdot ");
			latexMap.put("&middot;", "\\cdot ");
			latexMap.put("&times;", "\\times ");
			latexMap.put("&equals;", "\\Relbar ");
			latexMap.put("&forall;", "\\forall ");
			latexMap.put("&exist;", "\\exists ");
			latexMap.put("&#x220d;", "\\ni ");
			latexMap.put("&lowast;", "* ");
			latexMap.put("&minus;", "- ");
			latexMap.put("&frasl;", "/ ");
			latexMap.put("&ratio;", ": ");
			latexMap.put("&lt;", "< ");
			latexMap.put("&gt;", "> ");
			latexMap.put("&cong;", "\\cong ");
			latexMap.put("&InvisibleTimes;", " ");

			// Pfeile
			latexMap.put("&harr;", "\\leftrightarrow ");
			latexMap.put("&larr;", "\\leftarrow ");
			latexMap.put("&rarr;", "\\rightarrow ");
			latexMap.put("&hArr;", "\\Leftrightarrow ");
			latexMap.put("&lArr;", "\\Leftarrow ");
			latexMap.put("&rArr;", "\\Rightarrow ");

			// dynamische Zeichen
			latexMap.put("&sum;", "\\sum ");
			latexMap.put("&prod;", "\\prod ");
			latexMap.put("&Integral;", "\\int ");
			latexMap.put("&dd;", "d ");

			// griechisches Alphabet
			latexMap.put("&rgr;", "\\rho ");
			latexMap.put("&mgr;", "\\mu ");
			latexMap.put("&tgr;", "\\tau ");
			latexMap.put("&sigmaf;", "\\varsigma ");
			latexMap.put("&piv;", "\\varpi ");
			latexMap.put("&phiv;", "\\phi");
			latexMap.put("&pgr;", "\\pi ");
			latexMap.put("&ohgr;", "\\omega ");

			for (Greek ch : Greek.values()) {
				latexMap.put(ch.getHTML(), "\\" + ch.getLaTeX());
			}

			// latexMap.put("&alpha;", "\\alpha");
			// latexMap.put("&beta;", "\\beta");
			// latexMap.put("&gamma;", "\\gamma ");
			// latexMap.put("&delta;", "\\delta ");
			// latexMap.put("&epsi;", "\\epsilon ");
			// latexMap.put("&eta;", "\\eta ");
			// latexMap.put("&iota;", "\\iota ");
			// latexMap.put("&kappa;", "\\kappa ");
			// latexMap.put("&lambda;", "\\lambda ");
			// latexMap.put("&mu;", "\\mu ");
			// latexMap.put("&nu;", "\\nu ");
			// latexMap.put("&omicron;", "o ");
			// latexMap.put("&pi;", "\\pi ");
			// latexMap.put("&theta;", "\\theta ");
			// latexMap.put("&rho;", "\\rho ");
			// latexMap.put("&sigma;", "\\sigma ");
			// latexMap.put("&tau;", "\\tau ");
			// latexMap.put("&upsilon;", "\\upsilon ");
			// latexMap.put("&phi;", "\\varphi");
			// latexMap.put("&chi;", "\\chi ");
			// latexMap.put("&omega;", "\\omega ");
			// latexMap.put("&xi;", "\\xi ");
			// latexMap.put("&psi;", "\\psi ");
			// latexMap.put("&zeta;", "\\zeta ");
			// latexMap.put("&Delta;", "\\Delta ");
			// latexMap.put("&Phi;", "\\Phi ");
			// latexMap.put("&Gamma;", "\\Gamma ");
			// latexMap.put("&Lambda;", "\\Lambda ");
			// latexMap.put("&Pi;", "\\Pi ");
			// latexMap.put("&Theta;", "\\Theta ");
			// latexMap.put("&Sigma;", "\\Sigma ");
			// latexMap.put("&Upsilon;", "\\Upsilon ");
			// latexMap.put("&Omega;", "\\Omega ");
			// latexMap.put("&Xi;", "\\Xi ");
			// latexMap.put("&Psi;", "\\Psi ");

			latexMap.put("&epsiv;", "\\epsilon ");
			latexMap.put("&phgr;", "\\phi ");
			latexMap.put("&ggr;", "\\gamma ");
			latexMap.put("&eegr;", "\\eta ");
			latexMap.put("&igr;", "\\iota ");
			latexMap.put("&phgr;", "\\phi ");
			latexMap.put("&kgr;", "\\kappa ");
			latexMap.put("&lgr;", "\\lambda ");
			latexMap.put("&ngr;", "\\nu ");
			latexMap.put("&ogr;", "o ");
			latexMap.put("&thgr;", "\\theta ");
			latexMap.put("&sgr;", "\\sigma ");
			latexMap.put("&ugr;", "\\upsilon ");
			latexMap.put("&zgr;", "\\zeta ");
			latexMap.put("&Agr;", "A ");
			latexMap.put("&Bgr;", "B ");
			latexMap.put("&KHgr;", "X ");
			latexMap.put("&Egr;", "E ");
			latexMap.put("&PHgr;", "\\Phi ");
			latexMap.put("&Ggr;", "\\Gamma ");
			latexMap.put("&EEgr;", "H ");
			latexMap.put("&Igr;", "I ");
			latexMap.put("&THgr;", "\\Theta ");
			latexMap.put("&Kgr;", "K ");
			latexMap.put("&Lgr;", "\\Lambda ");
			latexMap.put("&Mgr;", "M ");
			latexMap.put("&Ngr;", "N ");
			latexMap.put("&Ogr;", "O ");
			latexMap.put("&Pgr;", "\\Pi ");
			latexMap.put("&Rgr;", "P ");
			latexMap.put("&Sgr;", "\\Sigma ");
			latexMap.put("&Tgr;", "T ");
			latexMap.put("&Ugr;", "\\Upsilon ");
			latexMap.put("&OHgr;", "\\Omega ");
			latexMap.put("&Zgr;", "Z ");

			// Pfeile und andere Operatoren
			latexMap.put("&#x2212;", "-");
			latexMap.put("&perp;", "\\bot ");
			latexMap.put("&sim;", "~ ");
			latexMap.put("&prime;", "\\prime ");
			latexMap.put("&le;", "\\le ");
			latexMap.put("&ge;", "\\ge ");
			latexMap.put("&infin;", "\\infty ");
			latexMap.put("&clubs;", "\\clubsuit ");
			latexMap.put("&diams;", "\\diamondsuit ");
			latexMap.put("&hearts;", "\\heartsuit ");
			latexMap.put("&spades;", "\\spadesuit ");
			latexMap.put("&PlusMinus;", "\\pm ");
			latexMap.put("&Prime;", "\\prime\\prime ");
			latexMap.put("&prop;", "\\propto ");
			latexMap.put("&part;", "\\partial ");
			latexMap.put("&bull;", "\\bullet ");
			latexMap.put("&ne;", "\\neq ");
			latexMap.put("&equiv;", "\\equiv ");
			latexMap.put("&asymp;", "\\approx ");
			latexMap.put("&hellip;", "... ");
			latexMap.put("&VerticalBar;", "\\mid ");
			latexMap.put("&crarr;", "\\P ");
			latexMap.put("&alefsym;", "\\aleph ");
			latexMap.put("&image;", "\\Im ");
			latexMap.put("&real;", "\\Re ");
			latexMap.put("&weierp;", "\\wp ");
			latexMap.put("&otimes;", "\\otimes ");
			latexMap.put("&oplus;", "\\oplus ");
			latexMap.put("&empty;", "\\emtyset ");
			latexMap.put("&cap;", "\\cap ");
			latexMap.put("&cup;", "\\cup ");
			latexMap.put("&sup;", "\\supset ");
			latexMap.put("&supe;", "\\seupseteq ");
			latexMap.put("&nsub;", "\\not\\subset ");
			latexMap.put("&sub;", "\\subset ");
			latexMap.put("&sube;", "\\subseteq ");
			latexMap.put("&isin;", "\\in ");
			latexMap.put("&notin;", "\\notin ");
			latexMap.put("&ang;", "\\angle ");
			latexMap.put("&nabla;", "\\nabla ");
			latexMap.put("&radic;", "\\surd ");
			latexMap.put("&and;", "\\wedge ");
			latexMap.put("&or;", "\\vee ");
			latexMap.put("&and;", "\\wedge ");
			latexMap.put("&ang;", "\\angle ");
			latexMap.put("&angle;", "\\angle ");
			latexMap.put("&ap;", "\\approx ");
			latexMap.put("&approx;", "\\approx ");
			latexMap.put("&bigoplus;", "\\oplus ");
			latexMap.put("&bigotimes;", "\\otimes ");
			latexMap.put("&bot;", "\\bot ");
			latexMap.put("&bottom;", "\\bot ");
			latexMap.put("&cap;", "\\cap ");
			latexMap.put("&CirclePlus;", "\\oplus ");
			latexMap.put("&CircleTimes;", "\\otimes ");
			latexMap.put("&cong;", "\\cong ");
			latexMap.put("&Congruent;", "\\equiv ");
			latexMap.put("&cup;", "\\cup ");
			latexMap.put("&darr;", "\\downarrow ");
			latexMap.put("&dArr;", "\\Downarrow ");
			latexMap.put("&Del;", "\\nabla ");
			latexMap.put("&Del;", "\\nabla ");
			latexMap.put("&DifferentialD;", "\u2146 ");
			latexMap.put("&DoubleLeftArrow;", "\\Leftarrow ");
			latexMap.put("&DoubleLeftRightArrow;", "\\Leftrightarrow ");
			latexMap.put("&DoubleRightArrow;", "\\Rightarrow ");
			latexMap.put("&DoubleUpArrow;", "\\Uparrow ");
			latexMap.put("&downarrow;", "\\downarrow ");
			latexMap.put("&Downarrow;", "\\Downarrow ");
			latexMap.put("&DownArrow;", "\\Downarrow ");
			latexMap.put("&Element;", "\\in ");
			latexMap.put("&emptyv;", "\\oslash ");
			latexMap.put("&equiv;", "\\equiv ");
			latexMap.put("&exist;", "\\exists ");
			latexMap.put("&Exist;", "\\exists ");
			latexMap.put("&exponentiale;", "\u2147 ");
			latexMap.put("&forall;", "\\forall ");
			latexMap.put("&ForAll;", "\\forall ");
			latexMap.put("&ge;", "\\geq ");
			latexMap.put("&geq;", "\\geq ");
			latexMap.put("&GreaterEqual;", "\\geq ");
			latexMap.put("&harr;", "\\leftrightarrow ");
			latexMap.put("&hArr;", "\\Leftrightarrow ");
			latexMap.put("&iff;", "\\Leftrightarrow ");
			latexMap.put("&Implies;", "\\Rightarrow ");
			latexMap.put("&in;", "\\in ");
			latexMap.put("&infin;", "\\infty ");
			latexMap.put("&int;", "\\int ");
			latexMap.put("&Integral;", "\\int ");
			latexMap.put("&isin;", "\\in ");
			latexMap.put("&isinv;", "\\in ");
			latexMap.put("&diam;", "\\diamond ");
			latexMap.put("&diamond;", "\\diamond ");
			latexMap.put("&lang;", "\\left\\langle ");
			latexMap.put("&langle;", "\\left\\langle ");
			latexMap.put("&larr;", "\\leftarrow ");
			latexMap.put("&lArr;", "\\Leftarrow ");
			latexMap.put("&le;", "\\leq ");
			latexMap.put("&LeftAngleBracket;", "\\left\\langle ");
			latexMap.put("&Leftarrow;", "\\Leftarrow ");
			latexMap.put("&LeftArrow;", "\\leftarrow ");
			latexMap.put("&leftrightarrow;", "\\leftrightarrow ");
			latexMap.put("&Leftrightarrow;", "\\Leftrightarrow ");
			latexMap.put("&LeftRightArrow;", "\\leftrightarrow ");
			latexMap.put("&leq;", "\\leq ");
			latexMap.put("&leq;", "\\leq ");
			latexMap.put("&Longleftrightarrow;", "\\Longleftrightarrow ");
			latexMap.put("&minus;", "- ");
			latexMap.put("&nabla;", "\\nabla ");
			latexMap.put("&NotElement;", "\\notin ");
			latexMap.put("&NotEqual;", "\\notin ");
			latexMap.put("&notin;", "\\notin ");
			latexMap.put("&oplus;", "\\oplus ");
			latexMap.put("&or;", "\\vee ");
			latexMap.put("&otimes;", "\\otimes ");
			latexMap.put("&part;", "\\partial ");
			latexMap.put("&partialD;", "\\partial ");
			latexMap.put("&perp;", "\\bot ");
			latexMap.put("&prod;", "\\Pi ");
			latexMap.put("&Product;", "\\Pi ");
			latexMap.put("&rang;", "\\right\\rangle ");
			latexMap.put("&rangle;", "\\right\\rangle ");
			latexMap.put("&rarr;", "\\rightarrow ");
			latexMap.put("&rArr;", "\\Rightarrow ");
			latexMap.put("&RightAngleBracket;", "\\right\\rangle ");
			latexMap.put("&rightarrow;", "\\rightarrow ");
			latexMap.put("&Rightarrow;", "\\Rightarrow ");
			latexMap.put("&RightArrow;", "\\rightarrow ");
			latexMap.put("&sdot;", "\\cdot ");
			latexMap.put("&sim;", "\\sim ");
			latexMap.put("&prop;", "\\propto ");
			latexMap.put("&Proportional;", "\\propto ");
			latexMap.put("&propto;", "\\propto ");
			latexMap.put("&sub;", "\\subset ");
			latexMap.put("&sube;", "\\subseteq ");
			latexMap.put("&subE;", "\\subseteq ");
			latexMap.put("&subset;", "\\subset ");
			latexMap.put("&subseteq;", "\\subseteq ");
			latexMap.put("&subseteqq;", "\\subseteq ");
			latexMap.put("&SubsetEqual;", "\\subseteq ");
			latexMap.put("&sum;", "\\Sigma ");
			latexMap.put("&Sum;", "\\Sigma ");
			latexMap.put("&sup;", "\\supset ");
			latexMap.put("&supe;", "\\supseteq ");
			latexMap.put("&supE;", "\\supseteq ");
			latexMap.put("&Superset;", "\\supset");
			latexMap.put("&SupersetEqual;", "\\supseteq ");
			latexMap.put("&supset;", "\\supset ");
			latexMap.put("&supseteq;", "\\supseteq ");
			latexMap.put("&supseteqq;", "\\supseteq ");
			latexMap.put("&Tilde;", "\\sim ");
			latexMap.put("&TildeFullEqual;", "\\cong ");
			latexMap.put("&TildeTilde;", "\\approx ");
			latexMap.put("&tprime;", "\u2034 ");
			latexMap.put("&uarr;", "\\uparrow ");
			latexMap.put("&uArr;", "\\Uparrow ");
			latexMap.put("&uparrow;", "\\uparrow ");
			latexMap.put("&Uparrow;", "\\Uparrow ");
			latexMap.put("&UpArrow;", "\\uparrow ");
			latexMap.put("&UpTee;", "\\bot ");
			latexMap.put("&varnothing;", "\\oslash ");
			latexMap.put("&varpropto;", "\\propto ");
			latexMap.put("&vee;", "\\vee ");
			latexMap.put("&vprop;", "\\propto ");
			latexMap.put("&wedge;", "\\wedge ");
			latexMap.put("&xoplus;", "\\oplus ");
			latexMap.put("&xotime;", "\\otimes ");
			latexMap.put("&Space;", " ");
			latexMap.put("&colon;", ":");
			latexMap.put("&ApplyFunction;", " ");
			latexMap.put("&squ;", " ");
			latexMap.put("&#x2212;", "- ");
			latexMap.put("&#x2192;", "\\to ");
			latexMap.put("&#x222b;", "\\int ");
			latexMap.put("&#x2061;", "");
		}

		return latexMap;
	}

	/**
	 * Generates the substitution table from the default file path in field
	 * SUBSTITUTIONS_FILE.
	 * 
	 * @param geogebraSyntax1
	 *            whether to return GeoGebra
	 * 
	 */
	public MathMLParser(boolean geogebraSyntax1) {
		this.geogebraSyntax = geogebraSyntax1;
		if (geogebraSyntax1) {
			substitutions = getGeogebraMap();
		} else {
			substitutions = getLatexMap();
		}
	}

	/*
	 * Removed by GeoGebra Generates the substitution table from the given file
	 * path.
	 * 
	 * @param substitutionsTable the substitution table.
	 * 
	 * public MathMLParser(HashMap<String, String> substitutionsTable) {
	 * substitutions = substitutionsTable; }
	 */

	/**
	 * TODO berarbeiten (complete MathML blocks only?): Parses MathML code into
	 * LaTeX code using the substitution table genereated by the constructor.
	 * <br>
	 * Only presentation markup can be parsed properly, no use for parsing
	 * content markup.
	 * <p>
	 * For example the presentation markup code
	 * 
	 * <pre>
	 * &lt;mrow&gt;
	 *   &lt;msup&gt;
	 *     &lt;mfenced&gt;
	 *       &lt;mrow&gt;
	 *         &lt;mi&gt;a&lt;/mi&gt;
	 *         &lt;mo&gt;+&lt;/mo&gt;
	 *         &lt;mi&gt;b&lt;/mi&gt;
	 *       &lt;/mrow&gt;
	 *     &lt;/mfenced&gt;
	 *     &lt;mn&gt;2&lt;/mn&gt;
	 *   &lt;/msup&gt;
	 * &lt;/mrow&gt;
	 * </pre>
	 * 
	 * can be parsed by this method, while the equivalent content markup
	 * 
	 * <pre>
	 * &lt;mrow&gt;
	 *   &lt;apply&gt;
	 *     &lt;power/&gt;
	 *     &lt;apply&gt;
	 *       &lt;plus/&gt;
	 *       &lt;ci&gt;a&lt;/ci&gt;
	 *       &lt;ci&gt;b&lt;/ci&gt;
	 *     &lt;/apply&gt;
	 *     &lt;cn&gt;2&lt;/cn&gt;
	 *   &lt;/apply&gt;
	 * &lt;/mrow&gt;
	 * </pre>
	 * 
	 * can not be parsed.
	 * <p>
	 * Both notations of entities can be parsed: The plain MathML notation,
	 * starting with an ampersand sign (e.g. '&amp;equals;'), or the
	 * "HTML wrapped" notation startig with an entity for the ampersand sign
	 * (e.g. '&amp;amp;equals;').
	 * 
	 * @param strBuf0
	 *            a String containig the MathML code to parse
	 * @param wrappedEntities1
	 *            indicates whether the entities in the MathML code are HTML
	 *            wrapped (e.g. '&amp;amp;PlusMinus;'), or not (e.g.
	 *            '&amp;PlusMinus;')
	 * @param skipUnknownEntities1
	 *            skipUnknownEntities
	 * @return a StringBuilder containig the LaTeX representation of the input
	 */
	public String parse(String strBuf0, boolean wrappedEntities1,
			boolean skipUnknownEntities1) {

		// Remove newlines first;
		String strBuf1 = strBuf0.replace('\n', ' ').replace('\r', ' ');
		// now remove coments
		strBuf1 = strBuf1.replaceAll("<!--.*?-->", "");

		// Avoiding bugs due to wrong parsing (quick workarounds)
		strBuf1 = strBuf1.replace("><", "> <");
		// strBuf1 = strBuf1.replace(";&#x", "; &#x");

		// Adding "inferred mrow" to those elements that need it
		// according to W3C and also there in latexMap;
		// but also take care of the possible attributes!
		// As the algorithm itself neglects them,
		// this "quick" solution can do that too.
		strBuf1 = strBuf1.replaceAll("<msqrt.*?>", "<msqrt> <mrow>");
		strBuf1 = strBuf1.replace("</msqrt>", "</mrow> </msqrt>");
		strBuf1 = strBuf1.replaceAll("<mtd.*?>", "<mtd> <mrow>");
		strBuf1 = strBuf1.replace("</mtd>", "</mrow> </mtd>");

		this.strBuf = strBuf1;
		this.wrappedEntities = wrappedEntities1;
		this.skipUnknownEntities = skipUnknownEntities1;

		// usually the MathML input should have more characters as the
		// output
		StringBuilder result = new StringBuilder(strBuf.length());

		pos = 0;
		try {
			while (strBuf.indexOf("<", pos) != -1) {
				parseBlock(getNextTag(), result, true);
				skipFollowingTag();
			}
			// TODO besser result stutzen? -> return new
			// StringBuilder(result) o. result.toString()
			return result.toString();
		} catch (XMLParseException | RuntimeException e) {
			Log.debug(e);
		}
		return null; // TODO statt exception, speter lo(umlaut)schen
	}

	/**
	 * TODO Pseudocode berarbeiten, Algorithmus noch einmal nachvollziehen
	 * Parses a MathML block in strBuf recursively into LaTeX code.
	 * <p>
	 * Pseudocode:
	 * 
	 * <pre>
	 * while (pos &lt;= blockEnd) {
	 *     if (insideOfInnerstBlock) {
	 *         result.append(convertToLatexSyntax(area));
	 *     } else {
	 *         tmpTag = getNextTag(); // pos = pos + tmpTag.length();
	 *         if (substitutionAvailable(tmpTag)) {
	 *             while (substitutionContainsBlock) {
	 *                 addSubstitutionUpToPlaceHolderOfBlockToOutput();
	 *                 parseBlock(getAreaOfNextBlock());
	 *              }
	 *              addRestOfSubstitutionOutPut();
	 *         } else {
	 *             parseBlock(pos, getBlockEndIndex(tmpTag));
	 *         }
	 *         skipClosingTag();
	 *     }
	 * }
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param startTag
	 *            startTag
	 * @param result
	 *            builder to which we append the string
	 * @param appendSpace
	 *            whether space should be appended after the block content
	 * @throws XMLParseException
	 *             if an error occurs while parsing
	 */
	void parseBlock(String startTag, StringBuilder result, boolean appendSpace)
			throws XMLParseException {
		boolean closeBracketNow = this.closeBracketNext;
		this.closeBracketNext = false;
		String endTag = generateEndTag(startTag);

		int blockEnd = getBlockEnd(startTag, endTag);

		String substBuf;
		String blockContent;
		boolean inside = true;

		int blockNumber = 0;
		int prevBlockNumber;

		while (pos <= blockEnd) {

			// scan for subblocks
			int i = pos;
			while ((i <= blockEnd) && (strBuf.charAt(i) != '<')) {
				i++;
			}

			if (!Objects.equals(startTag, endTag) && (i > blockEnd)) {

				// if sure to be at the end of the block hierarchy (inside),
				// append block content to result
				if (inside) {
					blockContent = strBuf.substring(pos, blockEnd + 1);
					result.append(parseBlockContent(blockContent));
					if (appendSpace) {
						result.append(' ');
					}
					pos = pos + blockContent.length();
					blockContent = null;
				} else {
					// if all subblocks have been processed skip to the end
					pos = blockEnd + 1;
				}
			} else {
				// this block has subblocks
				inside = false;
				// if there is a substitution for the next block, write it to
				// 'result'
				if ((substBuf = substitutions.get(startTag)) != null) {

					int phIndex;
					int substIndex = 0;

					// parse subblocks recursively
					while (((phIndex = substBuf.indexOf(PH_BLOCK_START,
							substIndex)) > -1) && (pos - 2 < blockEnd)) {

						// write substitution up to the block marker
						while (substIndex < phIndex) {
							result.append(substBuf.charAt(substIndex));
							substIndex++;
						}
						substIndex += PH_BLOCK_START.length();

						// get number of the block to parse
						int blockNumberIndex = substIndex;
						while (substBuf.charAt(substIndex) != PH_BLOCK_END) {
							substIndex++;
						}

						prevBlockNumber = blockNumber;

						String blockNumberStr = substBuf
								.substring(blockNumberIndex, substIndex);

						if ("S".equals(blockNumberStr)) {
							// keyword is BLOCKS -> parse all inner blocks in
							// order of appearance

							// skip PH_BLOCK_END
							substIndex++;

							// jump to the block to parse
							skipBlocks((1 - prevBlockNumber) - 1);

							// parse subblocks
							while ((strBuf.substring(pos, blockEnd + 1))
									.indexOf('<') != -1) {
								nextTag = getNextTag();
								parseBlock(nextTag, result, true);
								skipFollowingTag();
							}
							if (!appendSpace) {
								result.setLength(result.length() - 1);
							}
						} else {
							// keyword is BLOCK + block number, parse inner
							// blocks in given order

							try {
								blockNumber = Integer.parseInt(blockNumberStr);
							} catch (NumberFormatException nfe) {
								throw new XMLParseException(
										"Parsing error at character " + pos
												+ ": Unparseable block number in substitution.");
							}

							// skip PH_BLOCK_END
							substIndex++;

							// jump to the block to parse
							skipBlocks((blockNumber - prevBlockNumber) - 1);

							// parse subblock
							nextTag = getNextTag();
							parseBlock(nextTag, result, false);

							skipFollowingTag();
						}
					}

					// write (end of) substitution
					while (substIndex < substBuf.length()) {
						result.append(substBuf.charAt(substIndex));
						substIndex++;
					}

					pos = blockEnd + endTag.length();
					if (substitutions.get(startTag).endsWith(",")) {
						this.closeBracketNext = true;
					}
				} else {
					// parse subblocks of nextTag
					while ((strBuf.substring(pos, blockEnd + 1))
							.indexOf('<') != -1) {
						nextTag = getNextTag();
						parseBlock(nextTag, result, true);
						skipFollowingTag();
					}
					// make sure we don't output "x _{1}" but don't remove any
					// non-space character
					if (!appendSpace
							&& result.charAt(result.length() - 1) == ' ') {
						result.setLength(result.length() - 1);
					}
				}
			}
			if (closeBracketNow) {
				result.append(")");
			}
		}
		// TODO Warum braucht 'amayaOut.htm' diese Anweisung? -> 853, 853
		// (<mprescripts/>)
		pos = blockEnd;
	}

	/**
	 * Jumps to the next tag, reads it into 'startTag' an generates the
	 * corresponding 'endTag'.
	 */
	private String getNextTag() {

		while (strBuf.charAt(pos) != '<') {
			pos++;
		}

		tagBuf.setLength(0);

		while (strBuf.charAt(pos) != '>') {
			tagBuf.append(strBuf.charAt(pos));
			pos++;
		}
		pos++;
		tagBuf.append('>');

		return tagBuf.toString();
	}

	/**
	 * Generates an end tag corresponding to the given 'startTag'.
	 * 
	 * @param startTag
	 *            the start tag to generate an end tag from
	 * @return the end tag for the given start tag
	 */
	String generateEndTag(String startTag) {

		if (startTag.charAt(tagBuf.length() - 2) != '/') {

			if (startTag.indexOf(' ') > -1) {
				// delete parameters of startTag
				return "</" + startTag.substring(1, startTag.indexOf(' '))
						+ ">";
			}

			return "</" + startTag.substring(1, startTag.length());
		}
		// if the tag is self-closing (e.g. "<mprescripts/>"), the endTag is the
		// startTag
		return startTag;
	}

	/**
	 * Skips all characters up to the end of the next tag.
	 */
	void skipFollowingTag() {
		while (strBuf.charAt(pos) != '>') {
			pos++;
		}
		pos++;
	}

	/**
	 * Skips (back and forth) a given number of blocks from the actual position.
	 * 
	 * @param blocksToSkip
	 *            the number of blocks to skip
	 */
	void skipBlocks(int blocksToSkip) {

		if (blocksToSkip > 0) {
			for (int i = 0; i < blocksToSkip; i++) {

				String startTag = getNextTag();
				String endTag = generateEndTag(startTag);

				pos = getBlockEnd(startTag, endTag);

				pos = pos + endTag.length();
			}
		} else if (blocksToSkip < 0) {

			for (int i = 0; i > blocksToSkip; i--) {

				int subBlocks = 1;

				while (strBuf.charAt(pos) != '>') {
					pos--;
				}

				tagBuf.setLength(0);

				while (strBuf.charAt(pos) != '<') {
					tagBuf.append(strBuf.charAt(pos));
					pos--;
				}
				tagBuf.append('<');

				tagBuf.reverse();
				String blockEndTag = new String(tagBuf);
				String blockStartTag = new String(tagBuf.deleteCharAt(1));

				do {
					while (strBuf.charAt(pos) != '>') {
						pos--;
					}

					tagBuf.setLength(0);

					while (strBuf.charAt(pos) != '<') {
						tagBuf.append(strBuf.charAt(pos));
						pos--;
					}
					tagBuf.append('<');
					tagBuf.reverse();

					if (tagBuf.indexOf(" ") > -1) {
						tagBuf.delete(tagBuf.indexOf(" "), tagBuf.length() - 1);
					}

					if (tagBuf.toString().equals(blockStartTag)) {
						subBlocks--;
					} else {
						if (tagBuf.toString().equals(blockEndTag)) {
							subBlocks++;
						}
					}
				} while ((subBlocks > 0)
						|| (!(tagBuf.toString().equals(blockStartTag))));
			}
		}
	}

	/**
	 * Returns the end index of the block defined by the 'startTag' parameter
	 * skipping all subblocks. The end index is the position of the character
	 * before the closing tag of the block.
	 * 
	 * @param startTag0
	 *            the tag that opened the block
	 * @param endTag
	 *            the end tag to seek
	 * @return the index of the closing tag
	 */
	int getBlockEnd(String startTag0, String endTag) {

		if (!startTag0.equals(endTag)) {

			String startTag = startTag0;

			int pos2 = pos;
			int subBlocks = 1;

			// delete parameters of startTag
			if (startTag.indexOf(' ') > -1) {
				startTag = startTag.substring(0, startTag.indexOf(' ')) + '>';
			}

			do {
				while (strBuf.charAt(pos2) != '<') {
					pos2++;
				}

				tagBuf.setLength(0);

				while (strBuf.charAt(pos2) != '>') {
					tagBuf.append(strBuf.charAt(pos2));
					pos2++;
				}
				tagBuf.append('>');

				if (tagBuf.toString().equals(endTag)) {
					subBlocks--;
				} else {
					if (tagBuf.indexOf(" ") > -1) {
						tagBuf.delete(tagBuf.indexOf(" "), tagBuf.length() - 1);
					}

					if (tagBuf.toString().equals(startTag)) {
						subBlocks++;
					}
				}
			} while ((subBlocks > 0) || (!(tagBuf.toString().equals(endTag))));

			return pos2 - endTag.length();
		}

		return pos - startTag0.length();
	}

	/**
	 * Parses a String into Latex syntax and returns it.
	 * 
	 * @param s
	 *            the string to parse
	 * @return the Latex representation of the given string
	 * @throws XMLParseException
	 *             if HTML wrapped entities were expected but not found
	 */
	String parseBlockContent(String s) throws XMLParseException {

		// TODO hier!

		int sbIndex = 0;
		StringBuilder sb = new StringBuilder(s);

		// replace backslashes
		while ((sbIndex = sb.indexOf("\\", sbIndex)) > -1) {
			sb.insert(sbIndex + 1, "backslash");
			sbIndex = sbIndex + 10;
		}

		// replace braces
		if (!geogebraSyntax) {
			for (int i = 0; i < leftBraces.length; i++) {
				sbIndex = 0;
				while ((sbIndex = sb.indexOf(String.valueOf(leftBraces[i]),
						sbIndex)) > -1) {
					sb.insert(sbIndex, "\\left");
					sbIndex = sbIndex + 6;
				}
			}

			for (int i = 0; i < rightBraces.length; i++) {
				sbIndex = 0;
				while ((sbIndex = sb.indexOf(String.valueOf(rightBraces[i]),
						sbIndex)) > -1) {
					sb.insert(sbIndex, "\\right");
					sbIndex = sbIndex + 7;
				}
			}
		}

		// replace special characters
		for (int i = 0; i < specialCharacters.length; i++) {
			sbIndex = 0;
			while ((sbIndex = sb.indexOf(String.valueOf(specialCharacters[i]),
					sbIndex)) > -1) {
				sb.insert(sbIndex, '\\');
				sbIndex = sbIndex + 2;
			}
		}

		// replace Entities
		sbIndex = 0;
		while ((sbIndex = sb.indexOf("&", sbIndex)) > -1) {

			entity.setLength(0);

			while (sb.charAt(sbIndex) != ';') {
				entity.append(sb.charAt(sbIndex));
				sbIndex++;
			}
			entity.append(';');
			sbIndex++;

			if (wrappedEntities && entity.toString().equals("&amp;")) {

				sb.delete(sbIndex - 4, sbIndex);
				sbIndex = sbIndex - 5;

				entity.setLength(0);

				try {
					while (sb.charAt(sbIndex) != ';') {
						entity.append(sb.charAt(sbIndex));
						sbIndex++;
					}
				} catch (StringIndexOutOfBoundsException sioobe) {
					throw new XMLParseException("Parsing error at character " + pos
							+ ": MathML code is not HTML wrapped.");
				}

				entity.append(';');
				sbIndex++;
			}

			if ((entitySubst = substitutions.get(entity.toString())) != null) {
				sb.delete(sbIndex - entity.length(), sbIndex);
				sbIndex = sbIndex - entity.length();
				sb.insert(sbIndex, entitySubst);
				sbIndex = sbIndex + entitySubst.length();
				sb.insert(sbIndex, " ");
				sbIndex++;
			} else {
				if (skipUnknownEntities) {

					sb.delete(sbIndex - entity.length(), sbIndex);
					sbIndex = sbIndex - entity.length();
					sb.insert(sbIndex, " ");
					sbIndex++;
				} else {
					String entityWorkout = entity.toString();
					if (entityWorkout.startsWith("&#x")) {
						entityWorkout = entityWorkout.substring(3,
								entityWorkout.length() - 1);
					} else if (entityWorkout.startsWith("\\&\\#x")) {
						// not sure whether this is needed any more...
						entityWorkout = entityWorkout.substring(5,
								entityWorkout.length() - 1);
					}
					if (isValidUnicode(entityWorkout)) {
						// assuming our LaTeX parser will know these things
						int hex = Integer.parseInt(entityWorkout, 16);
						Character hexChar = (char) hex;
						sb.replace(sbIndex - entity.length(), sbIndex,
								hexChar.toString());
						sbIndex -= entity.length() - 1;
					} else {
						// old school
						sb.insert(sbIndex - entity.length(), "NOTFOUND:'");
						sbIndex += 10;
						sb.insert(sbIndex, "' ");
						sbIndex += 2;
					}
				}
			}
		}

		// replace '&'
		sbIndex = 0;
		while ((sbIndex = sb.indexOf("&", sbIndex)) > -1) {
			sb.insert(sbIndex, '\\');
			sbIndex = sbIndex + 2;
		}

		// replace '#'
		sbIndex = 0;
		while ((sbIndex = sb.indexOf("#", sbIndex)) > -1) {
			sb.insert(sbIndex, '\\');
			sbIndex = sbIndex + 2;
		}

		return sb.toString().trim();
	}

	/**
	 * Determines whether this is valid Unicode
	 * 
	 * @param vu
	 *            unicode string
	 * @return whether it's valid
	 */
	private static boolean isValidUnicode(String vu) {

		if (vu.length() != 4) {
			return false;
		}

		char[] ca = vu.toLowerCase().toCharArray();

		for (int i = 0; i < 4; i++) {
			if (!Character.isDigit(ca[i]) && (ca[i] < 'a' || ca[i] > 'f')) {
				return false;
			}
		}

		return true;
	}

}
