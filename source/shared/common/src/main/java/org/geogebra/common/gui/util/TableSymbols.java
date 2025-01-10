package org.geogebra.common.gui.util;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.parser.function.ParserFunctions;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Arrays of special strings and unicode symbols used when building tables and
 * lists.
 * 
 * @author G Sturr
 *
 */
public class TableSymbols {
	// spaces either side (for multiply when inserted into the input bar)
	private final static String[] FUNCTIONS = {
			" sqrt(x) ",
			" cbrt(x) ",
			" abs(x) ",
			" sgn(x) ",
			" alt((x, y, z))",
			" arg(x) ",
			" conjugate(x) ",
			" floor(x) ",
			" ceil(x) ",
			" round(x) ",
			" log(b,x) ",
			" exp(x) ",
			" ln(x) ",
			" lg(x) ",
			" ld(x) ",
			" sin(x) ",
			" asin(x) ",
			" cos(x) ",
			" acos(x) ",
			" tan(x) ",
			" atan(x) ",
			" sinh(x) ",
			" asinh(x) ",
			" cosh(x) ",
			" acosh(x) ",
			" tanh(x) ",
			" atanh(x) ",
			" sec(x) ",
			" sech(x) ",
			" cosec(x) ",
			" cosech(x) ",
			" cot(x) ",
			" coth(x) ",
			" asind(x) ",
			" acosd(x) ",
			" atand(x) ",
			" atan2(y, x) ",
			" erf(x) ",
			" gamma(x) ",
			" beta(a, b) ",
			" gamma(a, x) ",
			" beta(a, b, x) ",
			" gammaRegularized(a, x) ",
			" betaRegularized(a, b, x) ",
			" psi(x) ",
			" polyGamma(m, x) ",
			" nroot(x, n) ",
			" fractionalPart(x) ",
			" real(x) ", " imaginary(x) ",
			" nPr(n, r) ", "nCr(n, r)",
			" sinIntegral(x) ",
			" cosIntegral(x) ",
			" expIntegral(x) ",
			" random() ",
			" zeta(x) ",
			" Dirac(x) ",
			" Heaviside(x) ",
	};

	// spaces either side (for multiply when inserted into the input bar)
	private final static String[][] FUNCTIONS_GROUPED = {
			{ " random() " },
			{ " sqrt(x) ", " cbrt(x) ", " nroot(x, n) " },
			{ " abs(x) ", " sgn(x) ", " alt((x, y, z)) " },
			{ " arg(x) ", " conjugate(x) ", " real(x) ", " imaginary(x) " },
			{ " floor(x) ", " ceil(x) ", " round(x) ", " fractionalPart(x) " },
			{ " log(b,x) ", " exp(x) ", " ln(x) ", " lg(x) ", " ld(x) " },
			{ " sin(x) ", " cos(x) ", " tan(x) " },
			{ " sec(x) ", " cosec(x) ", " cot(x) " },
			{ " asin(x) ", " acos(x) ", " atan(x) " },
			{ " asind(x) ", " acosd(x) ", " atand(x) " },
			{ " atan2(y, x) " },
			{ " sinh(x) ", " cosh(x) ", " tanh(x) " },
			{ " sech(x) ", " cosech(x) ", " coth(x) " },
			{ " asinh(x) ", " acosh(x) ", " atanh(x) " },
			{ " gamma(x) ", " gamma(a, x) ", " gammaRegularized(a, x) " },
			{ " psi(x) ", " polyGamma(m, x) " },
			{ " beta(a, b) ", " beta(a, b, x) ", " betaRegularized(a, b, x) " },
			{ " erf(x) " },
			{ " nPr(n, r) ", "nCr(n, r)" },
			{ " sinIntegral(x) ", " cosIntegral(x) ", " expIntegral(x) " },
			{ " zeta(x) ", " Dirac(x) ", " Heaviside(x) " }, };

	public final static String[] ANALYSIS = {
			"\u2211", // N-ARY SUMMATION
			"\u2202", // PARTIAL DIFFERENTIAL
			"\u2207", // NABLA
			"\u0394", // INCREMENT (Greek Delta)
			"\u220F", // N-ARY PRODUCT
			"\u2210", // N-ARY COPRODUCT

			"\u222B", // INTEGRAL
			"\u222C", // DOUBLE INTEGRAL
			"\u222D", // TRIPLE INTEGRAL
			"\u222E", // CONTOUR INTEGRAL
			"\u221E", // INFINITY
	};

	public final static String[] LOGICAL = {
			"\u2200", // FOR ALL
			"\u2203", // THERE EXISTS
			"\u2204", // THERE DOES NOT EXIST

			Unicode.QUESTEQ + "", // Boolean identity \\questeq
			"\u2261", // IDENTICAL TO
			"\u2262", // NOT IDENTICAL TO

			"\u2227", // LOGICAL AND
			"\u2228", // LOGICAL OR
			"\u2295", // \\oplus (xor)
			"\u22bc", // \\barwedge (nand)
			"\u22BB", // \\veebar (xor)

			"\u22A4", // \\top (tautology)
			Unicode.PERPENDICULAR + "", // \\bot (contradiction)
			"\u2201", // COMPLEMENT
			"\u2234", // THEREFORE
			"\u2235", // BECAUSE
	};

	public final static String[] SETS = {
			"\u2205", // EMPTY SET
			"\u2229", // INTERSECTION
			"\u222A", // UNION

			Unicode.IS_ELEMENT_OF + "", // ELEMENT OF
			"\u2209", // NOT AN ELEMENT OF

			Unicode.IS_SUBSET_OF_STRICT + "", // SUBSET OF
			"\u2284", // NOT A SUBSET OF
			Unicode.IS_SUBSET_OF + "", // SUBSET OF OR EQUAL TO
			"\u2288", // NEITHER A SUBSET OF NOR EQUAL TO

			"\u2283", // SUPERSET OF
			"\u2285", // NOT A SUPERSET OF
			"\u2287", // SUPERSET OF OR EQUAL TO
			"\u2289", // NEITHER A SUPERSET OF NOR EQUAL TO

			"\u2102", // DOUBLE-STRUCK CAPITAL C
			"\u2115", // DOUBLE-STRUCK CAPITAL N
			"\u211A", // DOUBLE-STRUCK CAPITAL Q
			"\u211D", // DOUBLE-STRUCK CAPITAL R
			"\u2124", // DOUBLE-STRUCK CAPITAL Z

			"\u2111", // \\Im
			"\u211C", // \\Re
			"\u2118", // \\wp (power set)
			"\u2135", // \\aleph

	};

	public final static String[] OPERATORS = {
			"\u00D7", // \\times
			"\u00F7", // \\div
			"\u2212", // \\minus
			"\u00B7", // \\centerdot
			"\u2218", // \\circ
			"\u2219", // \\bullet

			"\u00B1", // PLUS-OR-MINUS SIGN
			"\u2213", // MINUS-OR-PLUS SIGN
			"\u221A", // SQUARE ROOT

			"\u2260", // \\neq
			"\u2264", // \\leq
			"\u2265", // \\geq
			"\u2248", // \\approx
			"\u223C", // \\sim
			"\u2241", // \\nsim
			"\u2245", // \\cong
			"\u2247", // \\ncong
			"\u221D", // \\propto

			// "\u221F", //RIGHT ANGLE
			"\u2220", // ANGLE
			"\u2221", // MEASURED ANGLE
			"\u2222", // SPHERICAL ANGLE
			// "\u2223", //DIVIDES
			// "\u2224", //DOES NOT DIVIDE

			Unicode.PERPENDICULAR + "", // \\perp
			"\u2225", // PARALLEL TO
			"\u2226", // NOT PARALLEL TO
			// "\u223A", //GEOMETRIC PROPORTION
			"\u2295", // \\oplus
			"\u2296", // \\ominus
			Unicode.VECTOR_PRODUCT + "", // \\otimes
			"\u2298", // \\oslash
			"\u2299", // \\odot
	};

	public final static String[] SUB_SUPERSCRIPTS = {
			// first row
			Unicode.SUPERSCRIPT_0 + "", Unicode.SUPERSCRIPT_1 + "",
			Unicode.SUPERSCRIPT_2 + "", Unicode.SUPERSCRIPT_3 + "",
			Unicode.SUPERSCRIPT_4 + "", Unicode.SUPERSCRIPT_5 + "",
			Unicode.SUPERSCRIPT_6 + "", Unicode.SUPERSCRIPT_7 + "",
			Unicode.SUPERSCRIPT_8 + "", Unicode.SUPERSCRIPT_9 + "",

			// second row
			"\u207A", // SUPERSCRIPT PLUS SIGN
			"\u207B", // SUPERSCRIPT MINUS
			"\u207C", // SUPERSCRIPT EQUALS SIGN
			"\u207D", // SUPERSCRIPT LEFT PARENTHESIS
			"\u207E", // SUPERSCRIPT RIGHT PARENTHESIS
			"\u207F", // SUPERSCRIPT LATIN SMALL LETTER N
			Unicode.DEGREE_STRING, // degree
			"", // blank filler
			"", // blank filler
			"", // blank filler

			// third row
			"\u2080", // SUBSCRIPT ZERO
			"\u2081", // SUBSCRIPT ONE
			"\u2082", // SUBSCRIPT TWO
			"\u2083", // SUBSCRIPT THREE
			"\u2084", // SUBSCRIPT FOUR
			"\u2085", // SUBSCRIPT FIVE
			"\u2086", // SUBSCRIPT SIX
			"\u2087", // SUBSCRIPT SEVEN
			"\u2088", // SUBSCRIPT EIGHT
			"\u2089", // SUBSCRIPT NINE

			// fourth row
			"\u208A", // SUBSCRIPT PLUS SIGN
			"\u208B", // SUBSCRIPT MINUS
			"\u208C", // SUBSCRIPT EQUALS SIGN
			"\u208D", // SUBSCRIPT LEFT PARENTHESIS
			"\u208E", // SUBSCRIPT RIGHT PARENTHESIS
	};

	public final static String[] BASIC_ARROWS = {
			"\u2190", // \\leftarrow
			"\u2191", // \\uparrow
			"\u2192", // \\rightarrow
			"\u2193", // \\downarrow
			"\u2194", // \\leftrightarrow
			"\u2195", // \\updownarrow
			"\u2196", // \\nwarrow
			"\u2197", // \\nearrow
			"\u2198", // \\searrow
			"\u2199", // \\swarrow
			"\u21D0", // \\Leftarrow
			"\u21D1", // \\Uparrow
			"\u21D2", // \\Rightarrow
			"\u21D3", // \\Downarrow
			"\u21D4", // \\Leftrightarrow
			"\u21D5", // \\Updownarrow
	};

	public final static String[] OTHER_ARROWS = {
			"\u21A9", // \\hookleftarrow
			"\u21AA", // \\hookrightarrow
			"\u21AB", // \\looparrowleft
			"\u21AC", // \\looparrowright
			"\u219A", // \\nleftarrow
			"\u219B", // \\nrightarrow
			"\u219D", // \\rightsquigarrow
			"\u219E", // \\twoheadleftarrow
			"\u21A0", // \\twoheadrightarrow
			"\u21A2", // \\leftarrowtail
			"\u21A3", // \\rightarrowtail
			"\u21A6", // \\mapsto

			"\u21AD", // \\leftrightsquigarrow
			"\u21AE", // \\nleftrightarrow
			"\u21B0", // \\Lsh
			"\u21B1", // \\Rsh
			"\u21B6", // \\curvearrowleft
			"\u21B7", // \\curvearrowright
			"\u21BC", // \\leftharpoonup
			"\u21BD", // \\leftharpoondown
			"\u21BE", // \\upharpoonright
			"\u21BF", // \\upharpoonleft
			"\u21C0", // \\rightharpoonup
			"\u21C1", // \\rightharpoondown
			"\u21C2", // \\downharpoonright
			"\u21C3", // \\downharpoonleft
			"\u21C4", // \\rightleftarrows
			"\u21C6", // \\leftrightarrows
			"\u21C7", // \\leftleftarrows
			"\u21C8", // \\upuparrows
			"\u21C9", // \\rightrightarrows
			"\u21CA", // \\downdownarrows
			"\u21CB", // \\leftrightharpoons
			"\u21CC", // \\rightleftharpoons
			"\u21CD", // \\nLeftarrow
			"\u21CE", // \\nLeftrightarrow
			"\u21CF", // \\nRightarrow
			"\u21DA", // \\Lleftarrow
			"\u21DB", // \\Rrightarrow

			/*
			 * not supported in win7 "\u27F5", // \\longleftarrow "\u27F6", //
			 * \\longrightarrow "\u27F7", // \\longleftrightarrow "\u27F8", //
			 * \\Longleftarrow "\u27F9", // \\Longrightarrow "\u27FA", //
			 * \\Longleftrightarrow "\u27FC", // \\longmapsto "\u27FF", //
			 * \\leadsto
			 */
	};

	public final static String[] GEOMETRIC_SHAPES = {
			"\u25EF", // \\bigcirc
			"\u2605", // \\bigstar

			"\u25B3", // \\bigtriangleup
			"\u25B4", // \\blacktriangle
			"\u25B5", // \\triangle
			"\u25B6", // \\blacktriangleright
			"\u25B7", // \\triangleright
			"\u25BD", // \\bigtriangledown
			"\u25BE", // \\blacktriangledown
			"\u25BF", // \\triangledown
			"\u25C0", // \\blacktriangleleft
			"\u25C1", // \\triangleleft

			"\u25CA", // \\Diamond
			// "\u25CA", // \\lozenge

			"\u29EB", // \\blacklozenge

			// "\u25A0", // \\qedsymbol
			"\u25A1", // \\square
			"\u25AA", // \\blacksquare
	};

	public final static String[] GAMES_MUSIC = {
			"\u2660", // \\spadesuit
			"\u2661", // \\heartsuit
			"\u2662", // \\diamondsuit
			"\u2663", // \\clubsuit
			"\u266D", // \\flat
			"\u266E", // \\natural
			"\u266F", // \\sharp
	};

	public final static String[] HAND_POINTERS = {
			"\u261A", // BLACK LEFT POINTING INDEX
			"\u261B", // BLACK RIGHT POINTING INDEX
			"\u261C", // WHITE LEFT POINTING INDEX
			"\u261D", // WHITE UP POINTING INDEX
			"\u261E", // WHITE RIGHT POINTING INDEX
			"\u261F", // WHITE DOWN POINTING INDEX
	};

	public final static String[] CURRENCY = {
			"\u20A0", // EURO-CURRENCY SIGN
			"\u20A1", // COLON SIGN
			"\u20A2", // CRUZEIRO SIGN
			"\u20A3", // FRENCH FRANC SIGN
			"\u20A4", // LIRA SIGN
			"\u20A5", // MILL SIGN
			"\u20A6", // NAIRA SIGN
			"\u20A7", // PESETA SIGN
			"\u20A8", // RUPEE SIGN
			"\u20A9", // WON SIGN
			"\u20AA", // NEW SHEQEL SIGN
			"\u20AB", // DONG SIGN
			"\u20AC", // EURO SIGN
			"\u20AD", // KIP SIGN
			"\u20AE", // TUGRIK SIGN
			"\u20AF", // DRACHMA SIGN
	};

	/**
	 * @param app
	 *            application
	 * @return popup symbol table
	 */
	public static String[][] basicSymbolsMap(Localization app) {
		return new String[][] {
				// LOWERCASE GREEK
				{ Unicode.alpha + "",
						app.getPlain("GreekCharacterA", Unicode.alpha + "") },
				{ Unicode.beta + "",
						app.getPlain("GreekCharacterA", Unicode.beta + "") },
				{ Unicode.gamma + "",
						app.getPlain("GreekCharacterA", Unicode.gamma + "") },
				{ Unicode.delta + "",
						app.getPlain("GreekCharacterA", Unicode.delta + "") },
				{ Unicode.epsilon + "",
						app.getPlain("GreekCharacterA", Unicode.epsilon + "") },
				{ Unicode.zeta + "",
						app.getPlain("GreekCharacterA", Unicode.zeta + "") },
				{ Unicode.eta + "",
						app.getPlain("GreekCharacterA", Unicode.eta + "") },
				{ Unicode.theta + "",
						app.getPlain("GreekCharacterA", Unicode.theta + "") },
				// { "\u03B9" , app.getMenu("Symbol.GreekCharacter" ) + ": " +
				// "\u03B9"}, //lowercaseGreekIOTA
				{ Unicode.kappa + "",
						app.getPlain("GreekCharacterA", Unicode.kappa + "") },
				{ Unicode.lambda + "",
						app.getPlain("GreekCharacterA", Unicode.lambda + "") },
				{ Unicode.mu + "",
						app.getPlain("GreekCharacterA", Unicode.mu + "") },
				// { "\u03BD" , app.getMenu("Symbol.GreekCharacter" ) + ": " +
				// "\u03BD"}, //lowercaseGreekNU
				{ Unicode.xi + "",
						app.getPlain("GreekCharacterA", Unicode.xi + "") },
				// { "\u03BF" , app.getMenu("Symbol.GreekCharacter" ) + ": " +
				// "\u03BF"}, //lowercaseGreekOMICRON
				// { "\u03C0" , app.getMenu("Symbol.GreekCharacter" ) + ": " +
				// "\u03C0"}, //lowercaseGreekPI
				{ Unicode.rho + "",
						app.getPlain("GreekCharacterA", Unicode.rho + "") },
				// { "\u03C2" , app.getMenu("Symbol.GreekCharacter" ) + ": " +
				// "\u03C2"}, //lowercaseGreekFINALSIGMA
				{ Unicode.sigma + "",
						app.getPlain("GreekCharacterA", Unicode.sigma + "") },
				{ Unicode.tau + "",
						app.getPlain("GreekCharacterA", Unicode.tau + "") },
				// { "\u03C5" , app.getMenu("Symbol.GreekCharacter" ) + ": " +
				// "\u03C5"}, //lowercaseGreekUPSILON
				{ Unicode.phi + "", // \varPhi the "loopy" phi
						app.getPlain("GreekCharacterA", Unicode.phi + "") },
				{ Unicode.phi_symbol + "", // \phi "straight" phi
						app.getPlain("GreekCharacterA",
								Unicode.phi_symbol + "") },
				{ Unicode.chi + "",
						app.getPlain("GreekCharacterA", Unicode.chi + "") },
				{ Unicode.psi + "",
						app.getPlain("GreekCharacterA", Unicode.psi + "") },
				{ Unicode.omega + "",
						app.getPlain("GreekCharacterA", Unicode.omega + "") },
				{ Unicode.Gamma + "",
						app.getPlain("GreekCharacterA", Unicode.Gamma + "") },
				{ Unicode.Delta + "",
						app.getPlain("GreekCharacterA", Unicode.Delta + "") },
				{ Unicode.Theta + "",
						app.getPlain("GreekCharacterA", Unicode.Theta + "") },
				// removed - too similar to logical 'and'
				// { "\u039b" , app.getPlain("GreekCharacterA", "\u039b" ) },
				// //uppercaseGreekLAMBDA
				// { "\u039e" , app.getPlain("GreekCharacterA", "\u039e" ) },
				// //uppercaseGreekXI
				// UPPERCASE GREEK
				{ Unicode.Pi + "",
						app.getPlain("GreekCharacterA", Unicode.Pi + "") },
				{ Unicode.Sigma + "",
						app.getPlain("GreekCharacterA", Unicode.Sigma + "") },
				{ Unicode.Phi + "",
						app.getPlain("GreekCharacterA", Unicode.Phi + "") },
				// { "\u03a8" , app.getPlain("GreekCharacterA", "\u03a8" ) },
				// //uppercaseGreekPSI
				{ Unicode.Omega + "",
						app.getPlain("GreekCharacterA", Unicode.Omega + "") },
				{ Unicode.INFINITY + "", app.getMenu("Symbol.Infinity") },
				{ ExpressionNodeConstants.strVECTORPRODUCT,
						app.getMenu("Symbol.VectorProduct") },
				{ ExpressionNodeConstants.strEQUAL_BOOLEAN,
						app.getMenu("Symbol.BooleanEqual") },
				{ ExpressionNodeConstants.strNOT_EQUAL,
						app.getMenu("Symbol.NotEqual") },
				{ ExpressionNodeConstants.strLESS_EQUAL,
						app.getMenu("Symbol.LessThanEqualTo") },
				{ ExpressionNodeConstants.strGREATER_EQUAL,
						app.getMenu("Symbol.GreaterThanEqualTo") },
				{ ExpressionNodeConstants.strNOT,
						app.getMenu("Symbol.Negation") },
				{ ExpressionNodeConstants.strAND, app.getMenu("Symbol.And") },
				{ ExpressionNodeConstants.strOR, app.getMenu("Symbol.Or") },
				{ ExpressionNodeConstants.strIMPLIES,
						app.getMenu("Symbol.Implication") },
				{ ExpressionNodeConstants.strPARALLEL,
						app.getMenu("Symbol.Parallel") },
				{ ExpressionNodeConstants.strPERPENDICULAR,
						app.getMenu("Symbol.Perpendicular") },
				{ ExpressionNodeConstants.strIS_ELEMENT_OF,
						app.getMenu("Symbol.ElementOf") },
				{ ExpressionNodeConstants.strIS_SUBSET_OF,
						app.getMenu("Symbol.Subset") },
				{ ExpressionNodeConstants.strIS_SUBSET_OF_STRICT,
						app.getMenu("Symbol.StrictSubset") },
				// { "\u2220" , app.getMenu("Symbol.Angle" )},
				{ "\u2221", app.getMenu("Symbol.AngleMeasure") },
				{ Unicode.SUPERSCRIPT_2 + "", app.getMenu("Symbol.Square") }, // exponents^2
				{ Unicode.SUPERSCRIPT_3 + "", app.getMenu("Symbol.Cube") }, // exponents^3
				{ Unicode.DEGREE_STRING, app.getMenu("Symbol.Degree") }, // degree
				{ " " + Unicode.IMAGINARY + " ", Unicode.IMAGINARY + "" }, // sqrt(-1)
				{ " " + Unicode.PI_STRING + " ", Unicode.PI_STRING }, // pi
				{ " " + Unicode.EULER_STRING + " ", Unicode.EULER_STRING }, // e
				{ Unicode.NBSP + "", app.getMenu("Symbol.NBSP") }, // non-breaking
																	// space
		};
	}

	/**
	 * @param app
	 *            localization
	 * @param map
	 *            symbol table
	 * @return list of symbols
	 */
	public static String[] basicSymbols(Localization app,
			String[][] map) {

		ArrayList<String> extraSymbols = new ArrayList<>();

		// create a list of special symbols for the current locale
		int index = 1;
		while (app.getSymbol(index) != null) {
			extraSymbols.add(app.getSymbol(index));
			index++;
		}

		// build the array from the basic symbol array and the extra symbol list
		String[] array = new String[map.length + extraSymbols.size()];
		for (int i = 0; i < map.length; i++) {
			array[i] = map[i][0];
		}
		for (int i = 0; i < extraSymbols.size(); i++) {
			array[i + map.length] = extraSymbols.get(i);
		}
		return array;
	}

	/**
	 * @param app
	 *            localization
	 * @param map
	 *            international symbols
	 * @return symbols
	 */
	public static String[] basicSymbolsToolTips(Localization app,
			String[][] map) {

		ArrayList<String> extraTooltips = new ArrayList<>();

		// create a list of special symbols for the current locale
		int index = 1;
		while (app.getSymbol(index) != null) {
			extraTooltips.add(app.getSymbolTooltip(index));
			index++;
		}
		String[] array = basicSymbols(app, map);

		for (int i = 0; i < map.length; i++) {
			array[i] = map[i][1];
		}
		for (int i = 0; i < extraTooltips.size(); i++) {
			array[i + map.length] = extraTooltips.get(i);
		}
		return array;
	}

	/**
	 * convert eg sin(x) into sen(x)
	 * 
	 * @param app
	 *            app
	 * @return translated names eg sin(x) -&gt; sen(x)
	 */
	public static String[] getTranslatedFunctions(App app) {
		ParserFunctions parserFunctions = app.getParserFunctions();
		Localization loc = app.getLocalization();
		return getTranslatedFunctions(loc, parserFunctions);
	}

	/**
	 * convert eg sin(x) into sen(x)
	 *
	 * @param loc localization
	 * @param parserFunctions collection of functions available to parser
	 * @return translated names eg sin(x) -&gt; sen(x)
	 */
	public static String[] getTranslatedFunctions(Localization loc,
			ParserFunctions parserFunctions) {
		ArrayList<String> functions = new ArrayList<>();
		for (String function: FUNCTIONS) {
			String[] strs = function.split("\\(", 2);
			String functionName = strs[0].trim();
			if (parserFunctions.isReserved(functionName)) {
				String translatedFunctionName = loc
						.getMenu(Localization.FUNCTION_PREFIX + functionName);
				if (translatedFunctionName
						.startsWith(Localization.FUNCTION_PREFIX)) {
					// translation not supported for this function
					functions.add(function);
				} else {
					String translated = " " + translatedFunctionName + "(" + strs[1];
					functions.add(translated);
				}
			}
		}

		return toArray(functions);
	}

	/**
	 * convert eg sin(x) into sen(x)
	 * 
	 * @param app
	 *            app
	 * @return translated names eg sin(x) -&gt; sen(x)
	 */
	public static String[][] getTranslatedFunctionsGrouped(App app) {
		ParserFunctions parserFunctions = app.getParserFunctions();
		Localization loc = app.getLocalization();
		List<List<String>> ret = new ArrayList<>();
		for (String[] functionGroup: FUNCTIONS_GROUPED) {
			List<String> group = new ArrayList<>();
			for (String function: functionGroup) {
				String[] strs = function.split("\\(", 2);
				String functionName = strs[0].trim();
				if (parserFunctions.isReserved(functionName)) {
					String translatedFunctionName = loc
							.getMenu(Localization.FUNCTION_PREFIX + functionName);
					if (translatedFunctionName
							.startsWith(Localization.FUNCTION_PREFIX)) {
						group.add(function);
					} else {
						String translated = " " + translatedFunctionName + "(" + strs[1];
						group.add(translated);
					}
				}
			}
			if (!group.isEmpty()) {
				ret.add(group);
			}
		}
		return toArrays(ret);
	}

	private static String[] toArray(List<String> list) {
		return list.toArray(new String[0]);
	}

	private static String[][] toArrays(List<List<String>> lists) {
		String[][] ret = new String[lists.size()][];
		for (int i = 0; i < lists.size(); i++) {
			List<String> list = lists.get(i);
			ret[i] = toArray(list);
		}
		return ret;
	}

	/**
	 * @return greek symbols including varphi, varepsilon etc.
	 */
	public static String[] greekLettersPlusVariants() {
		ArrayList<String> list = new ArrayList<>();
		GeoElement.addAddAllGreekUpperCase(list);
		GeoElement.addAddAllGreekLowerCaseNoPi(list);
		list.add("\u03C6"); // LaTeX /varPhi (loopy phi)
		list.add("\u03B5"); // LaTeX /varEpsilon
		list.add("\u03D1"); // LaTeX /varTheta (curly theta)
		list.add("\u03C2"); // LaTeX /varSigma

		String[] s = new String[list.size()];
		list.toArray(s);

		return s;
	}
}
