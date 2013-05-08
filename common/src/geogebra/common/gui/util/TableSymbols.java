package geogebra.common.gui.util;

import geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.util.Unicode;

import java.util.ArrayList;

/**
 * Arrays of special strings and unicode symbols used when building tables and lists. 
 * 
 * @author G Sturr
 *
 */
public class TableSymbols {

	public final static String[][] basicSymbolsMap(Localization app) {

		String[][] array = {
				{ Unicode.alpha+"" , app.getPlain("GreekCharacterA", Unicode.alpha+"" ) },  //lowercaseGreekALPHA
				{ Unicode.beta+"" , app.getPlain("GreekCharacterA", Unicode.beta+"" ) },  //lowercaseGreekBETA
				{ Unicode.gamma+"" , app.getPlain("GreekCharacterA", Unicode.gamma+"" ) },  //lowercaseGreekGAMMA
				{ Unicode.delta+"" , app.getPlain("GreekCharacterA", Unicode.delta+"" ) },  //lowercaseGreekDELTA
				{ Unicode.epsilon+"" , app.getPlain("GreekCharacterA", Unicode.epsilon+"" ) },  //lowercaseGreekEPSILON
				{ Unicode.zeta+"" , app.getPlain("GreekCharacterA", Unicode.zeta+"" ) },  //lowercaseGreekZETA
				{ Unicode.eta+"" , app.getPlain("GreekCharacterA", Unicode.eta+"" ) },  //lowercaseGreekETA
				{ Unicode.theta+"" , app.getPlain("GreekCharacterA", Unicode.theta+"" ) },  //lowercaseGreekTHETA
				//	{ "\u03B9" , app.getMenu("Symbol.GreekCharacter" ) + ":  " +   "\u03B9"},  //lowercaseGreekIOTA
				{ Unicode.kappa+"" , app.getPlain("GreekCharacterA", Unicode.kappa+"" ) },  //lowercaseGreekKAPPA
				{  Unicode.lambda+"" , app.getPlain("GreekCharacterA", Unicode.lambda+"") },  //lowercaseGreekLAMDA
				{ Unicode.mu+"" , app.getPlain("GreekCharacterA", Unicode.mu+"" ) },  //lowercaseGreekMU
				//	{ "\u03BD" , app.getMenu("Symbol.GreekCharacter" ) + ":  " +   "\u03BD"},  //lowercaseGreekNU
				{ Unicode.xi+"" , app.getPlain("GreekCharacterA", Unicode.xi+"" ) },  //lowercaseGreekXI
				//	{ "\u03BF" , app.getMenu("Symbol.GreekCharacter" ) + ":  " +   "\u03BF"},  //lowercaseGreekOMICRON
				//	{ "\u03C0" , app.getMenu("Symbol.GreekCharacter" ) + ":  " +   "\u03C0"},  //lowercaseGreekPI
				{ Unicode.rho+"" , app.getPlain("GreekCharacterA", Unicode.rho+"" ) },  //lowercaseGreekRHO
				//	{ "\u03C2" , app.getMenu("Symbol.GreekCharacter" ) + ":  " +   "\u03C2"},  //lowercaseGreekFINALSIGMA
				{ Unicode.sigma+"" , app.getPlain("GreekCharacterA", Unicode.sigma+"" ) },  //lowercaseGreekSIGMA
				{ Unicode.tau+"" , app.getPlain("GreekCharacterA", Unicode.tau+"" ) },  //lowercaseGreekTAU
				//	{ "\u03C5" , app.getMenu("Symbol.GreekCharacter" ) + ":  " +   "\u03C5"},  //lowercaseGreekUPSILON
				{ Unicode.phi +"", app.getPlain("GreekCharacterA", Unicode.phi+"" ) },  //lowercaseGreekPHI (\varPhi the "loopy" phi)
				{ Unicode.phi_symbol +"", app.getPlain("GreekCharacterA", Unicode.phi_symbol+"" ) },  //lowercaseGreekPHI \phi the "straight" phi
				{ Unicode.chi+"" , app.getPlain("GreekCharacterA", Unicode.chi+"" ) },  //lowercaseGreekCHI
				{ Unicode.psi+"" , app.getPlain("GreekCharacterA", Unicode.psi+"" ) },  //lowercaseGreekPSI
				{ Unicode.omega+"" , app.getPlain("GreekCharacterA", Unicode.omega+"" ) },  //lowercaseGreekOMEGA
				{ Unicode.Gamma+"" , app.getPlain("GreekCharacterA", Unicode.Gamma+"" ) },  //uppercaseGreekGAMMA
				{ Unicode.Delta+"" , app.getPlain("GreekCharacterA", Unicode.Delta+"" ) },  //uppercaseGreekDELTA
				{ Unicode.Theta+"" , app.getPlain("GreekCharacterA", Unicode.Theta+"" ) },  //uppercaseGreekTHETA
				// removed - too similar to logical 'and'
				//{ "\u039b" , app.getPlain("GreekCharacterA", "\u039b" ) },  //uppercaseGreekLAMBDA
				//{ "\u039e" , app.getPlain("GreekCharacterA", "\u039e" ) },  //uppercaseGreekXI
				{ Unicode.Pi+"" , app.getPlain("GreekCharacterA", Unicode.Pi+"" ) },  //uppercaseGreekPI
				{ Unicode.Sigma+"" , app.getPlain("GreekCharacterA", Unicode.Sigma+"" ) },  //uppercaseGreekSIGMA
				{ Unicode.Phi+"" , app.getPlain("GreekCharacterA", Unicode.Phi+"" ) },  //uppercaseGreekPHI
				//{ "\u03a8" , app.getPlain("GreekCharacterA", "\u03a8" ) },  //uppercaseGreekPSI
				{ Unicode.Omega+"" , app.getPlain("GreekCharacterA", Unicode.Omega+"" ) },  //uppercaseGreekOMEGA
				{ Unicode.Infinity+"" , app.getMenu("Symbol.Infinity" )},   
				{ ExpressionNodeConstants.strVECTORPRODUCT , app.getMenu("Symbol.VectorProduct" )},   
				{ ExpressionNodeConstants.strEQUAL_BOOLEAN , app.getMenu("Symbol.BooleanEqual" )},   
				{ ExpressionNodeConstants.strNOT_EQUAL , app.getMenu("Symbol.NotEqual" )},   
				{ ExpressionNodeConstants.strLESS_EQUAL , app.getMenu("Symbol.LessThanEqualTo" )},   
				{ ExpressionNodeConstants.strGREATER_EQUAL , app.getMenu("Symbol.GreaterThanEqualTo" )},   
				{ ExpressionNodeConstants.strNOT , app.getMenu("Symbol.Negation" )},   
				{ ExpressionNodeConstants.strAND , app.getMenu("Symbol.And" )},   
				{ ExpressionNodeConstants.strOR , app.getMenu("Symbol.Or" )},
				{ ExpressionNodeConstants.strIMPLIES , app.getMenu("Symbol.Implication" )},
				{ ExpressionNodeConstants.strPARALLEL , app.getMenu("Symbol.Parallel" )},   
				{ ExpressionNodeConstants.strPERPENDICULAR , app.getMenu("Symbol.Perpendicular" )},   
				{ ExpressionNodeConstants.strIS_ELEMENT_OF , app.getMenu("Symbol.ElementOf" )},   
				{ ExpressionNodeConstants.strIS_SUBSET_OF , app.getMenu("Symbol.Subset" )},   
				{ ExpressionNodeConstants.strIS_SUBSET_OF_STRICT , app.getMenu("Symbol.StrictSubset" )},   
				//{ "\u2220" , app.getMenu("Symbol.Angle" )},   
				{ "\u2221" , app.getMenu("Symbol.AngleMeasure" )},   
				{ Unicode.Superscript_2+"" , app.getMenu("Symbol.Square" )},   //exponents^2
				{ Unicode.Superscript_3+"" , app.getMenu("Symbol.Cube" )},   //exponents^3
				{ Unicode.degree , app.getMenu("Symbol.Degree" )},   //degree
				{ Unicode.IMAGINARY , Unicode.IMAGINARY },   //sqrt(-1)
				{ Unicode.PI_STRING , Unicode.PI_STRING },   //pi
				{ Unicode.EULER_STRING , Unicode.EULER_STRING },   //e
				{ Unicode.NBSP , app.getMenu("Symbol.NBSP" ) },   // non-breaking space
		};

		return array;

	}

	public final static String[] basicSymbols(Localization app){

		ArrayList<String> extraSymbols = new ArrayList<String>();
		
		// create a list of special symbols for the current locale
		int index = 1;
		while (app.getSymbol(index) != null){
			extraSymbols.add(app.getSymbol(index));
			index++;
		}
		
		// build the array from the basic symbol array and the extra symbol list
		String[] array = new String[basicSymbolsMap(app).length + extraSymbols.size()];
		for(int i=0; i< basicSymbolsMap(app).length; i++){
			array[i] = basicSymbolsMap(app)[i][0];
		}
		for(int i=0; i < extraSymbols.size(); i++){
			array[i + basicSymbolsMap(app).length] = extraSymbols.get(i);
		}
		return array;
	}


	public final static String[] basicSymbolsToolTips(Localization app){

		ArrayList<String> extraTooltips = new ArrayList<String>();
		
		// create a list of special symbols for the current locale
		int index = 1;
		while (app.getSymbol(index) != null){
			extraTooltips.add(app.getSymbolTooltip(index));
			index++;
		}
		String[] array = basicSymbols(app);
		for(int i=0; i< basicSymbolsMap(app).length; i++){
			array[i] = basicSymbolsMap(app)[i][1];
		}
		for(int i=0; i < extraTooltips.size(); i++){
			array[i + basicSymbolsMap(app).length] = extraTooltips.get(i);
		}
		return array;
	}


	/**
	 * convert eg sin(x) into sen(x)
	 * @param app app
	 * @return translated names eg sin(x) -> sen(x)
	 */
	public final static String[] getTranslatedFunctions(App app) {
		
		StringBuilder sb = new StringBuilder();
		
		String[] ret = new String[functions.length];
		for (int i = 0 ; i < functions.length ; i++) {
			String[] strs = functions[i].split("\\(");
			
			String functionName = strs[0].trim();
			String translatedFunctionName = app.getPlain("Function."+functionName);
			if (translatedFunctionName.startsWith("Function.")) {
				// translation not supported for this function
				ret[i] = functions[i];
			} else {
				sb.setLength(0);
				sb.append(' ');
				sb.append(translatedFunctionName);
				sb.append('(');
				sb.append(strs[1]);
				ret[i] = sb.toString();
			}
		}
		
		return ret;
	}

	// spaces either side (for multiply when inserted into the input bar)
	private final static String [] functions = { 	
		" sqrt(x) ",
		" cbrt(x) ",
		" abs(x) ",
		" sgn(x) ",
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
		" real(x) ",
		" imaginary(x) ",
		" sinIntegral(x) ",
		" cosIntegral(x) ",
		" expIntegral(x) ",
		" random() ",
		" zeta(x) ",
	};




	public final static String [] greekLowerCase = {
		"\u03b1", "\u03b2", "\u03b3", "\u03b4", "\u03b5", "\u03b6", "\u03b7", "\u03b8",
		"\u03b9", "\u03ba", "\u03bb", "\u03bc", "\u03bd", "\u03be", "\u03bf", "\u03c0", 
		"\u03c1", "\u03c3", "\u03c4", "\u03c5", "\u03d5", "\u03c7", "\u03c8",
		"\u03c9"
	};


	public final static String [] greekUpperCaseFull = {

		"\u0391",     // GREEK CAPITAL LETTER ALPHA
		"\u0392",     // GREEK CAPITAL LETTER BETA
		"\u0393",     // GREEK CAPITAL LETTER GAMMA
		"\u0394",     // GREEK CAPITAL LETTER DELTA
		"\u0395",     // GREEK CAPITAL LETTER EPSILON
		"\u0396",     // GREEK CAPITAL LETTER ZETA
		"\u0397",     // GREEK CAPITAL LETTER ETA
		"\u0398",     // GREEK CAPITAL LETTER THETA
		"\u0399",     // GREEK CAPITAL LETTER IOTA
		"\u039A",     // GREEK CAPITAL LETTER KAPPA
		"\u039B",     // GREEK CAPITAL LETTER LAMDA
		"\u039C",     // GREEK CAPITAL LETTER MU
		"\u039D",     // GREEK CAPITAL LETTER NU
		"\u039E",     // GREEK CAPITAL LETTER XI
		"\u039F",     // GREEK CAPITAL LETTER OMICRON
		"\u03A0",     // GREEK CAPITAL LETTER PI
		"\u03A1",     // GREEK CAPITAL LETTER RHO
		"\u03A3",     // GREEK CAPITAL LETTER SIGMA
		"\u03A4",     // GREEK CAPITAL LETTER TAU
		"\u03A5",     // GREEK CAPITAL LETTER UPSILON
		"\u03A6",     // GREEK CAPITAL LETTER PHI
		"\u03A7",     // GREEK CAPITAL LETTER CHI
		"\u03A8",     // GREEK CAPITAL LETTER PSI
		"\u03A9",     // GREEK CAPITAL LETTER OMEGA

	};


	public final static String[] greekLettersPlusVariants(){
		ArrayList<String> list = new ArrayList<String>();
		for(int i = 0; i < greekUpperCaseFull.length; i++){
		   list.add(greekUpperCaseFull[i]);
		}
		for(int i = 0; i < greekLowerCase.length; i++){
			   list.add(greekLowerCase[i]);
			}
		list.add("\u03C6");  // LaTeX /varPhi (loopy phi)
		list.add("\u03B5");  // LaTeX /varEpsilon
		list.add("\u03D1");  // LaTeX /varTheta (curly theta)
		list.add("\u03C2");  // LaTeX /varSigma 
		
		String[] s = new String[list.size()];
		list.toArray(s);
		
		return s;
	}
	
	public final static String [] analysis = {

		"\u2211", //N-ARY SUMMATION
		"\u2202", //PARTIAL DIFFERENTIAL
		"\u2207", //NABLA	
		"\u0394", //INCREMENT  (Greek Delta)
		"\u220F", //N-ARY PRODUCT
		"\u2210", //N-ARY COPRODUCT

		"\u222B", //INTEGRAL
		"\u222C", //DOUBLE INTEGRAL
		"\u222D", //TRIPLE INTEGRAL
		"\u222E", //CONTOUR INTEGRAL
		"\u221E", //INFINITY
	};


	public final static String [] logical = {

		"\u2200", //FOR ALL
		"\u2203", //THERE EXISTS
		"\u2204", //THERE DOES NOT EXIST

		"\u225f", //Boolean identity \\stackrel{?}{=}
		"\u2261", //IDENTICAL TO
		"\u2262", //NOT IDENTICAL TO

		"\u2227", //LOGICAL AND
		"\u2228", //LOGICAL OR

		/* unicode XOR, NAND, NOR not supported by JLatex
		 "\u22BB", //XOR
		 "\u22BC", //NAND
		 "\u22BD", //NOR
		 */	

		// use these instead
		"\u2295",    //     \\oplus (xor)
		"\u2305",    //     \\barwedge (nand)	
		"\u2A61",    //     \\veebar  (xor)

		"\u22A4",    //     \\top (tautology)
		"\u22A5",    //     \\bot (contradiction)
		"\u2201", //COMPLEMENT
		"\u2234", //THEREFORE
		"\u2235", //BECAUSE

	};


	public final static String [] sets = {

		"\u2205", //EMPTY SET
		"\u2229", //INTERSECTION
		"\u222A", //UNION

		"\u2208", //ELEMENT OF
		"\u2209", //NOT AN ELEMENT OF

		"\u2282", //SUBSET OF	
		"\u2284", //NOT A SUBSET OF
		"\u2286", //SUBSET OF OR EQUAL TO
		"\u2288", //NEITHER A SUBSET OF NOR EQUAL TO

		"\u2283", //SUPERSET OF
		"\u2285", //NOT A SUPERSET OF
		"\u2287", //SUPERSET OF OR EQUAL TO
		"\u2289", //NEITHER A SUPERSET OF NOR EQUAL TO

		"\u2102" ,      //DOUBLE-STRUCK CAPITAL C
		"\u2115" ,      //DOUBLE-STRUCK CAPITAL N
		"\u211A" ,      //DOUBLE-STRUCK CAPITAL Q
		"\u211D" ,      //DOUBLE-STRUCK CAPITAL R
		"\u2124" ,      //DOUBLE-STRUCK CAPITAL Z

		"\u2111",    //     \\Im
		"\u211C",    //     \\Re
		"\u2118",    //     \\wp  (power set) 
		"\u2135",    //     \\aleph

	};



	public final static String [] operators = {

		"\u00D7",    //     \\times
		"\u00F7",    //     \\div
		"\u2212",    //     \\minus
		"\u00B7",    //     \\centerdot
		"\u2218",    //     \\circ
		"\u2219",    //     \\bullet

		"\u00B1",    //PLUs-OR-MINUS SIGN
		"\u2213",   //MINUS-OR-PLUS SIGN
		"\u221A",   //SQUARE ROOT


		"\u2260",    //     \\neq
		"\u2264",    //     \\leq
		"\u2265",    //     \\geq	
		"\u2248",    //     \\approx
		"\u223C",    //     \\sim
		"\u2241",    //     \\nsim
		"\u2245",    //     \\cong
		"\u2247",    //     \\ncong
		"\u221D",    //     \\propto


		//"\u221F", //RIGHT ANGLE
		"\u2220", //ANGLE
		"\u2221", //MEASURED ANGLE
		"\u2222", //SPHERICAL ANGLE
		//	"\u2223", //DIVIDES
		//	"\u2224", //DOES NOT DIVIDE

		"\u22a5",  //   \\perp
		"\u2225", //PARALLEL TO
		"\u2226", //NOT PARALLEL TO
		//"\u223A", //GEOMETRIC PROPORTION
		"\u2295",    //     \\oplus
		"\u2296",    //     \\ominus
		Unicode.VECTOR_PRODUCT+"",    //     \\otimes
		"\u2298",    //     \\oslash
		"\u2299",    //     \\odot

	};

	public final static String [] sub_superscripts = {

		// first row
		"\u2070",     // SUPERSCRIPT ZERO
		"\u00b9",     // SUPERSCRIPT ONE  
		"\u00b2",     // SUPERSCRIPT TWO
		"\u00b3",     // SUPERSCRIPT THREE
		"\u2074",     // SUPERSCRIPT FOUR
		"\u2075",     // SUPERSCRIPT FIVE
		"\u2076",     // SUPERSCRIPT SIX
		"\u2077",     // SUPERSCRIPT SEVEN
		"\u2078",     // SUPERSCRIPT EIGHT
		"\u2079",     // SUPERSCRIPT NINE

		// second row
		"\u207A",     // SUPERSCRIPT PLUS SIGN
		"\u207B",     // SUPERSCRIPT MINUS
		"\u207C",     // SUPERSCRIPT EQUALS SIGN
		"\u207D",     // SUPERSCRIPT LEFT PARENTHESIS
		"\u207E",     // SUPERSCRIPT RIGHT PARENTHESIS
		"\u207F",     // SUPERSCRIPT LATIN SMALL LETTER N
		"\u00b0",     // degree	
		"", 	//blank filler
		"", 	//blank filler
		"", 	//blank filler

		// third row
		"\u2080",     // SUBSCRIPT ZERO
		"\u2081",     // SUBSCRIPT ONE
		"\u2082",     // SUBSCRIPT TWO
		"\u2083",     // SUBSCRIPT THREE
		"\u2084",     // SUBSCRIPT FOUR
		"\u2085",     // SUBSCRIPT FIVE
		"\u2086",     // SUBSCRIPT SIX
		"\u2087",     // SUBSCRIPT SEVEN
		"\u2088",     // SUBSCRIPT EIGHT
		"\u2089",     // SUBSCRIPT NINE

		// fourth row
		"\u208A",     // SUBSCRIPT PLUS SIGN
		"\u208B",     // SUBSCRIPT MINUS
		"\u208C",     // SUBSCRIPT EQUALS SIGN
		"\u208D",     // SUBSCRIPT LEFT PARENTHESIS
		"\u208E",     // SUBSCRIPT RIGHT PARENTHESIS
	};


	public final static String [] basic_arrows = {


		"\u2190",    //     \\leftarrow
		"\u2191",    //     \\uparrow
		"\u2192",    //     \\rightarrow
		"\u2193",    //     \\downarrow
		"\u2194",    //     \\leftrightarrow
		"\u2195",    //     \\updownarrow
		"\u2196",    //     \\nwarrow
		"\u2197",    //     \\nearrow
		"\u2198",    //     \\searrow
		"\u2199",    //     \\swarrow
		"\u21D0",    //     \\Leftarrow
		"\u21D1",    //     \\Uparrow
		"\u21D2",    //     \\Rightarrow
		"\u21D3",    //     \\Downarrow
		"\u21D4",    //     \\Leftrightarrow
		"\u21D5",    //     \\Updownarrow

	};



	public final static String [] otherArrows = {	

		"\u21A9",    //     \\hookleftarrow
		"\u21AA",    //     \\hookrightarrow
		"\u21AB",    //     \\looparrowleft
		"\u21AC",    //     \\looparrowright
		"\u219A",    //     \\nleftarrow
		"\u219B",    //     \\nrightarrow
		"\u219D",    //     \\rightsquigarrow
		"\u219E",    //     \\twoheadleftarrow
		"\u21A0",    //     \\twoheadrightarrow
		"\u21A2",    //     \\leftarrowtail
		"\u21A3",    //     \\rightarrowtail
		"\u21A6",    //     \\mapsto

		"\u21AD",    //     \\leftrightsquigarrow
		"\u21AE",    //     \\nleftrightarrow
		"\u21B0",    //     \\Lsh
		"\u21B1",    //     \\Rsh
		"\u21B6",    //     \\curvearrowleft
		"\u21B7",    //     \\curvearrowright
		"\u21BC",    //     \\leftharpoonup
		"\u21BD",    //     \\leftharpoondown
		"\u21BE",    //     \\upharpoonright
		"\u21BF",    //     \\upharpoonleft
		"\u21C0",    //     \\rightharpoonup
		"\u21C1",    //     \\rightharpoondown
		"\u21C2",    //     \\downharpoonright
		"\u21C3",    //     \\downharpoonleft
		"\u21C4",    //     \\rightleftarrows
		"\u21C6",    //     \\leftrightarrows
		"\u21C7",    //     \\leftleftarrows
		"\u21C8",    //     \\upuparrows
		"\u21C9",    //     \\rightrightarrows
		"\u21CA",    //     \\downdownarrows
		"\u21CB",    //     \\leftrightharpoons
		"\u21CC",    //     \\rightleftharpoons
		"\u21CD",    //     \\nLeftarrow
		"\u21CE",    //     \\nLeftrightarrow
		"\u21CF",    //     \\nRightarrow
		"\u21DA",    //     \\Lleftarrow
		"\u21DB",    //     \\Rrightarrow

		/* not supported in win7 
		"\u27F5",    //     \\longleftarrow
		"\u27F6",    //     \\longrightarrow
		"\u27F7",    //     \\longleftrightarrow
		"\u27F8",    //     \\Longleftarrow
		"\u27F9",    //     \\Longrightarrow
		"\u27FA",    //     \\Longleftrightarrow
		"\u27FC",    //     \\longmapsto
		"\u27FF",    //     \\leadsto
		 */

	};


	public final static String [] geometricShapes = {	

		"\u25EF",    //     \\bigcirc
		"\u2605",    //     \\bigstar

		"\u25B3",    //     \\bigtriangleup
		"\u25B4",    //     \\blacktriangle
		"\u25B5",    //     \\triangle
		"\u25B6",    //     \\blacktriangleright
		"\u25B7",    //     \\triangleright
		"\u25BD",    //     \\bigtriangledown
		"\u25BE",    //     \\blacktriangledown
		"\u25BF",    //     \\triangledown
		"\u25C0",    //     \\blacktriangleleft
		"\u25C1",    //     \\triangleleft

		"\u25CA",    //     \\Diamond
		//"\u25CA",    //     \\lozenge

		"\u29EB",    //     \\blacklozenge

		//"\u25A0",    //     \\qedsymbol
		"\u25A1",    //     \\square
		"\u25AA",    //     \\blacksquare



	};


	public final static String [] games_music = {

		"\u2660",    //     \\spadesuit
		"\u2661",    //     \\heartsuit
		"\u2662",    //     \\diamondsuit
		"\u2663",    //     \\clubsuit
		"\u266D",    //     \\flat
		"\u266E",    //     \\natural
		"\u266F",    //     \\sharp
	};


	public final static String [] handPointers = {

		"\u261A" ,    //BLACK LEFT POINTING INDEX
		"\u261B" ,    //BLACK RIGHT POINTING INDEX
		"\u261C" ,    //WHITE LEFT POINTING INDEX
		"\u261D" ,    //WHITE UP POINTING INDEX
		"\u261E" ,    //WHITE RIGHT POINTING INDEX
		"\u261F" ,    //WHITE DOWN POINTING INDEX

	};


	// other symbols --- not currently used

	public final static String [] UNICODEotherarrows = {	

		"\u00D7" ,
		"\u219A",      //LEFTWARDS ARROW WITH STROKE
		"\u219B",      //RIGHTWARDS ARROW WITH STROKE
		"\u219C",      //LEFTWARDS WAVE ARROW
		"\u219D",      //RIGHTWARDS WAVE ARROW
		"\u219E",      //LEFTWARDS TWO HEADED ARROW
		"\u219F",      //UPWARDS TWO HEADED ARROW
		"\u21A0",      //RIGHTWARDS TWO HEADED ARROW
		"\u21A1",      //DOWNWARDS TWO HEADED ARROW
		"\u21A2",      //LEFTWARDS ARROW WITH TAIL
		"\u21A3",      //RIGHTWARDS ARROW WITH TAIL
		"\u21A4",      //LEFTWARDS ARROW FROM BAR
		"\u21A5",      //UPWARDS ARROW FROM BAR
		"\u21A6",      //RIGHTWARDS ARROW FROM BAR
		"\u21A7",      //DOWNWARDS ARROW FROM BAR
		"\u21A8",      //UP DOWN ARROW WITH BASE
		"\u21A9",      //LEFTWARDS ARROW WITH HOOK
		"\u21AA",      //RIGHTWARDS ARROW WITH HOOK
		"\u21AB",      //LEFTWARDS ARROW WITH LOOP
		"\u21AC",      //RIGHTWARDS ARROW WITH LOOP
		"\u21AD",      //LEFT RIGHT WAVE ARROW
		"\u21AE",      //LEFT RIGHT ARROW WITH STROKE
		"\u21AF",      //DOWNWARDS ZIGZAG ARROW
		"\u21B0",      //UPWARDS ARROW WITH TIP LEFTWARDS
		"\u21B1",      //UPWARDS ARROW WITH TIP RIGHTWARDS
		"\u21B2",      //DOWNWARDS ARROW WITH TIP LEFTWARDS
		"\u21B3",      //DOWNWARDS ARROW WITH TIP RIGHTWARDS
		"\u21B4",      //RIGHTWARDS ARROW WITH CORNER DOWNWARDS
		"\u21B5",      //DOWNWARDS ARROW WITH CORNER LEFTWARDS

		//"\u21B6",      //ANTICLOCKWISE TOP SEMICIRCLE ARROW
		//"\u21B7",      //CLOCKWISE TOP SEMICIRCLE ARROW

		"\u21B8",      //NORTH WEST ARROW TO LONG BAR
		"\u21B9",      //LEFTWARDS ARROW TO BAR OVER RIGHTWARDS ARROW TO BAR

		//	"\u21BA",      //ANTICLOCKWISE OPEN CIRCLE ARROW
		//	"\u21BB",      //CLOCKWISE OPEN CIRCLE ARROW


		"\u21BC",      //LEFTWARDS HARPOON WITH BARB UPWARDS
		"\u21BD",      //LEFTWARDS HARPOON WITH BARB DOWNWARDS
		"\u21BE",      //UPWARDS HARPOON WITH BARB RIGHTWARDS
		"\u21BF",      //UPWARDS HARPOON WITH BARB LEFTWARDS
		"\u21C0",      //RIGHTWARDS HARPOON WITH BARB UPWARDS
		"\u21C1",      //RIGHTWARDS HARPOON WITH BARB DOWNWARDS
		"\u21C2",      //DOWNWARDS HARPOON WITH BARB RIGHTWARDS
		"\u21C3",      //DOWNWARDS HARPOON WITH BARB LEFTWARDS
		"\u21C4",      //RIGHTWARDS ARROW OVER LEFTWARDS ARROW
		"\u21C5",      //UPWARDS ARROW LEFTWARDS OF DOWNWARDS ARROW
		"\u21C6",      //LEFTWARDS ARROW OVER RIGHTWARDS ARROW
		"\u21C7",      //LEFTWARDS PAIRED ARROWS
		"\u21C8",      //UPWARDS PAIRED ARROWS
		"\u21C9",      //RIGHTWARDS PAIRED ARROWS
		"\u21CA",      //DOWNWARDS PAIRED ARROWS
		"\u21CB",      //LEFTWARDS HARPOON OVER RIGHTWARDS HARPOON
		"\u21CC",      //RIGHTWARDS HARPOON OVER LEFTWARDS HARPOON
		"\u21CD",      //LEFTWARDS DOUBLE ARROW WITH STROKE
		"\u21CE",      //LEFT RIGHT DOUBLE ARROW WITH STROKE
		"\u21CF",      //RIGHTWARDS DOUBLE ARROW WITH STROKE

		"\u21DA",      //LEFTWARDS TRIPLE ARROW
		"\u21DB",      //RIGHTWARDS TRIPLE ARROW
		"\u21DC",      //LEFTWARDS SQUIGGLE ARROW
		"\u21DD",      //RIGHTWARDS SQUIGGLE ARROW
		"\u21DE",      //UPWARDS ARROW WITH DOUBLE STROKE
		"\u21DF",      //DOWNWARDS ARROW WITH DOUBLE STROKE
		"\u21E0",      //LEFTWARDS DASHED ARROW
		"\u21E1",      //UPWARDS DASHED ARROW
		"\u21E2",      //RIGHTWARDS DASHED ARROW
		"\u21E3",      //DOWNWARDS DASHED ARROW
		"\u21E4",      //LEFTWARDS ARROW TO BAR
		"\u21E5",      //RIGHTWARDS ARROW TO BAR
		"\u21E6",      //LEFTWARDS WHITE ARROW
		"\u21E7",      //UPWARDS WHITE ARROW
		"\u21E8",      //RIGHTWARDS WHITE ARROW
		"\u21E9",      //DOWNWARDS WHITE ARROW
		"\u21EA",      //UPWARDS WHITE ARROW FROM BAR
		"\u21EB",      //UPWARDS WHITE ARROW ON PEDESTAL
		"\u21EC",      //UPWARDS WHITE ARROW ON PEDESTAL WITH HORIZONTAL BAR
		"\u21ED",      //UPWARDS WHITE ARROW ON PEDESTAL WITH VERTICAL BAR
		"\u21EE",      //UPWARDS WHITE DOUBLE ARROW
		"\u21EF",      //UPWARDS WHITE DOUBLE ARROW ON PEDESTAL
		"\u21F0",      //RIGHTWARDS WHITE ARROW FROM WALL
		"\u21F1",      //NORTH WEST ARROW TO CORNER
		"\u21F2",      //SOUTH EAST ARROW TO CORNER
		"\u21F3",      //UP DOWN WHITE ARROW

	};



	public final static String [] UNICODEmisc = {


		"\u2639" ,    //WHITE FROWNING FACE
		"\u263A" ,    //WHITE SMILING FACE
		"\u263B" ,    //BLACK SMILING FACE

		"\u260E" ,    //BLACK TELEPHONE
		"\u260F" ,    //WHITE TELEPHONE
		"\u2706",    //TELEPHONE LOCATION SIGN

		"\u2610" ,    //BALLOT BOX
		"\u2611" ,    //BALLOT BOX WITH CHECK
		"\u2612" ,    //BALLOT BOX WITH X
		"\u2613" ,    //SALTIRE
		"\u2619" ,    //REVERSED ROTATED FLORAL HEART BULLET


		"\u2620" ,    //SKULL AND CROSSBONES
		"\u2621" ,    //CAUTION SIGN
		"\u2622" ,    //RADIOACTIVE SIGN
		"\u2623" ,    //BIOHAZARD SIGN
		"\u2624" ,    //CADUCEUS
		"\u2625" ,    //ANKH

		"\u2670" ,    //WEST SYRIAC CROSS
		"\u2671" ,     //EAST SYRIAC CROSS

		"\u2626" ,    //ORTHODOX CROSS
		"\u2627" ,    //CHI RHO
		"\u2628" ,    //CROSS OF LORRAINE
		"\u2629" ,    //CROSS OF JERUSALEM
		"\u262A" ,    //STAR AND CRESCENT
		"\u262B" ,    //FARSI SYMBOL
		"\u262C" ,    //ADI SHAKTI
		"\u262D" ,    //HAMMER AND SICKLE
		"\u262E" ,    //PEACE SYMBOL
		"\u262F" ,    //YIN YANG
		"\u2630" ,    //TRIGRAM FOR HEAVEN
		"\u2631" ,    //TRIGRAM FOR LAKE
		"\u2632" ,    //TRIGRAM FOR FIRE
		"\u2633" ,    //TRIGRAM FOR THUNDER
		"\u2634" ,    //TRIGRAM FOR WIND
		"\u2635" ,    //TRIGRAM FOR WATER
		"\u2636" ,    //TRIGRAM FOR MOUNTAIN
		"\u2637" ,    //TRIGRAM FOR EARTH
		"\u2638" ,    //WHEEL OF DHARMA
	};


	public final static String [] UNICODEweather_astrology = {	

		"\u263C" ,    //WHITE SUN WITH RAYS
		"\u2600" ,    //BLACK SUN WITH RAYS
		"\u2601" ,    //CLOUD
		"\u2602" ,    //UMBRELLA
		"\u2603" ,    //SNOWMAN
		"\u2604" ,    //COMET
		"\u2605" ,    //BLACK STAR
		"\u2606" ,    //WHITE STAR
		"\u2607" ,    //LIGHTNING
		"\u2608" ,    //THUNDERSTORM
		"\u2609" ,    //SUN
		"\u260A" ,    //ASCENDING NODE
		"\u260B" ,    //DESCENDING NODE
		"\u260C" ,    //CONJUNCTION
		"\u260D" ,    //OPPOSITION

		"\u263D" ,    //FIRST QUARTER MOON
		"\u263E" ,    //LAST QUARTER MOON
		"\u263F" ,    //MERCURY
		"\u2640" ,    //FEMALE SIGN
		"\u2641" ,    //EARTH
		"\u2642" ,    //MALE SIGN
		"\u2643" ,    //JUPITER
		"\u2644" ,    //SATURN
		"\u2645" ,    //URANUS
		"\u2646" ,    //NEPTUNE
		"\u2647" ,    //PLUTO
		"\u2648" ,    //ARIES
		"\u2649" ,    //TAURUS
		"\u264A" ,    //GEMINI
		"\u264B" ,    //CANCER
		"\u264C" ,    //LEO
		"\u264D" ,    //VIRGO
		"\u264E" ,    //LIBRA
		"\u264F" ,    //SCORPIUS
		"\u2650" ,    //SAGITTARIUS
		"\u2651" ,    //CAPRICORN
		"\u2652" ,    //AQUARIUS
		"\u2653"     //PISCES

	};


	public final static String [] UNICODEgames_music = {

		"\u2654" ,    //WHITE CHESS KING
		"\u2655" ,    //WHITE CHESS QUEEN
		"\u2656" ,    //WHITE CHESS ROOK
		"\u2657" ,    //WHITE CHESS BISHOP
		"\u2658" ,    //WHITE CHESS KNIGHT
		"\u2659" ,    //WHITE CHESS PAWN
		"\u265A" ,    //BLACK CHESS KING
		"\u265B" ,    //BLACK CHESS QUEEN
		"\u265C" ,    //BLACK CHESS ROOK
		"\u265D" ,    //BLACK CHESS BISHOP
		"\u265E" ,    //BLACK CHESS KNIGHT
		"\u265F" ,    //BLACK CHESS PAWN
		"\u2660" ,    //BLACK SPADE SUIT
		"\u2661" ,    //WHITE HEART SUIT
		"\u2662" ,    //WHITE DIAMOND SUIT
		"\u2663" ,    //BLACK CLUB SUIT
		"\u2664" ,    //WHITE SPADE SUIT
		"\u2665" ,    //BLACK HEART SUIT
		"\u2666" ,    //BLACK DIAMOND SUIT
		"\u2667" ,    //WHITE CLUB SUIT
		"\u2668" ,    //HOT SPRINGS
		"\u2669" ,    //QUARTER NOTE
		"\u266A" ,    //EIGHTH NOTE
		"\u266B" ,    //BEAMED EIGHTH NOTES
		"\u266C" ,    //BEAMED SIXTEENTH NOTES
		"\u266D" ,    //MUSIC FLAT SIGN
		"\u266E" ,    //MUSIC NATURAL SIGN
		"\u266F" ,    //MUSIC SHARP SIGN


	};


	public final static String [] UNICODEwriting = {

		"\u2701",    //UPPER BLADE SCISSORS
		"\u2702",    //BLACK SCISSORS
		"\u2703",    //LOWER BLADE SCISSORS
		"\u2704",    //WHITE SCISSORS

		"\u2707",    //TAPE DRIVE
		"\u2708",    //AIRPLANE
		"\u2709",    //ENVELOPE
		"\u270C",    //VICTORY HAND
		"\u270D",    //WRITING HAND
		"\u270E",    //LOWER RIGHT PENCIL
		"\u270F",    //PENCIL
		"\u2710",    //UPPER RIGHT PENCIL
		"\u2711",    //WHITE NIB
		"\u2712"    //BLACK NIB
	};	



	public final static String [] UNICODEbullets = {
		"\u2713",    //CHECK MARK
		"\u2714",    //HEAVY CHECK MARK
		"\u2715",    //MULTIPLICATION X
		"\u2716",    //HEAVY MULTIPLICATION X
		"\u2717",    //BALLOT X
		"\u2718",    //HEAVY BALLOT X
		"\u2719",    //OUTLINED GREEK CROSS
		"\u271A",    //HEAVY GREEK CROSS
		"\u271B",    //OPEN CENTRE CROSS
		"\u271C",    //HEAVY OPEN CENTRE CROSS
		"\u271D",    //LATIN CROSS
		"\u271E",    //SHADOWED WHITE LATIN CROSS
		"\u271F",    //OUTLINED LATIN CROSS
		"\u2720",    //MALTESE CROSS
		"\u2721",    //STAR OF DAVID
		"\u2722",    //FOUR TEARDROP-SPOKED ASTERISK
		"\u2723",    //FOUR BALLOON-SPOKED ASTERISK
		"\u2724",    //HEAVY FOUR BALLOON-SPOKED ASTERISK
		"\u2725",    //FOUR CLUB-SPOKED ASTERISK
		"\u2726",    //BLACK FOUR POINTED STAR
		"\u2727",    //WHITE FOUR POINTED STAR
		"\u2729",    //STRESS OUTLINED WHITE STAR
		"\u272A",    //CIRCLED WHITE STAR
		"\u272B",    //OPEN CENTRE BLACK STAR
		"\u272C",    //BLACK CENTRE WHITE STAR
		"\u272D",    //OUTLINED BLACK STAR
		"\u272E",    //HEAVY OUTLINED BLACK STAR
		"\u272F",    //PINWHEEL STAR
		"\u2730",    //SHADOWED WHITE STAR
		"\u2731",    //HEAVY ASTERISK
		"\u2732",    //OPEN CENTRE ASTERISK
		"\u2733",    //EIGHT SPOKED ASTERISK
		"\u2734",    //EIGHT POINTED BLACK STAR
		"\u2735",    //EIGHT POINTED PINWHEEL STAR
		"\u2736",    //SIX POINTED BLACK STAR
		"\u2737",    //EIGHT POINTED RECTILINEAR BLACK STAR
		"\u2738",    //HEAVY EIGHT POINTED RECTILINEAR BLACK STAR
		"\u2739",    //TWELVE POINTED BLACK STAR
		"\u273A",    //SIXTEEN POINTED ASTERISK
		"\u273B",    //TEARDROP-SPOKED ASTERISK
		"\u273C",    //OPEN CENTRE TEARDROP-SPOKED ASTERISK
		"\u273D",    //HEAVY TEARDROP-SPOKED ASTERISK
		"\u273E",    //SIX PETALLED BLACK AND WHITE FLORETTE
		"\u273F",    //BLACK FLORETTE
		"\u2740",    //WHITE FLORETTE
		"\u2741",    //EIGHT PETALLED OUTLINED BLACK FLORETTE
		"\u2742",    //CIRCLED OPEN CENTRE EIGHT POINTED STAR
		"\u2743",    //HEAVY TEARDROP-SPOKED PINWHEEL ASTERISK
		"\u2744",    //SNOWFLAKE
		"\u2745",    //TIGHT TRIFOLIATE SNOWFLAKE
		"\u2746",    //HEAVY CHEVRON SNOWFLAKE
		"\u2747",    //SPARKLE
		"\u2748",    //HEAVY SPARKLE
		"\u2749",    //BALLOON-SPOKED ASTERISK
		"\u274A",    //EIGHT TEARDROP-SPOKED PROPELLER ASTERISK
		"\u274B",    //HEAVY EIGHT TEARDROP-SPOKED PROPELLER ASTERISK
		"\u274D",    //SHADOWED WHITE CIRCLE
		"\u274F",    //LOWER RIGHT DROP-SHADOWED WHITE SQUARE
		"\u2750",    //UPPER RIGHT DROP-SHADOWED WHITE SQUARE
		"\u2751",    //LOWER RIGHT SHADOWED WHITE SQUARE
		"\u2752",    //UPPER RIGHT SHADOWED WHITE SQUARE
		"\u2756",    //BLACK DIAMOND MINUS WHITE X

	};



	public final static String [] otherdingbats = {	

		"\u2758",    //LIGHT VERTICAL BAR
		"\u2759",    //MEDIUM VERTICAL BAR
		"\u275A",    //HEAVY VERTICAL BAR
		"\u275B",    //HEAVY SINGLE TURNED COMMA QUOTATION MARK ORNAMENT
		"\u275C",    //HEAVY SINGLE COMMA QUOTATION MARK ORNAMENT
		"\u275D",    //HEAVY DOUBLE TURNED COMMA QUOTATION MARK ORNAMENT
		"\u275E",    //HEAVY DOUBLE COMMA QUOTATION MARK ORNAMENT
		"\u2761",    //CURVED STEM PARAGRAPH SIGN ORNAMENT
		"\u2762",    //HEAVY EXCLAMATION MARK ORNAMENT
		"\u2763",    //HEAVY HEART EXCLAMATION MARK ORNAMENT
		"\u2764",    //HEAVY BLACK HEART
		"\u2765",    //ROTATED HEAVY BLACK HEART BULLET
		"\u2766",    //FLORAL HEART
		"\u2767",    //ROTATED FLORAL HEART BULLET
		"\u2776",    //DINGBAT NEGATIVE CIRCLED DIGIT ONE
		"\u2777",    //DINGBAT NEGATIVE CIRCLED DIGIT TWO
		"\u2778",    //DINGBAT NEGATIVE CIRCLED DIGIT THREE
		"\u2779",    //DINGBAT NEGATIVE CIRCLED DIGIT FOUR
		"\u277A",    //DINGBAT NEGATIVE CIRCLED DIGIT FIVE
		"\u277B",    //DINGBAT NEGATIVE CIRCLED DIGIT SIX
		"\u277C",    //DINGBAT NEGATIVE CIRCLED DIGIT SEVEN
		"\u277D",    //DINGBAT NEGATIVE CIRCLED DIGIT EIGHT
		"\u277E",    //DINGBAT NEGATIVE CIRCLED DIGIT NINE
		"\u277F",    //DINGBAT NEGATIVE CIRCLED NUMBER TEN
		"\u2780",    //DINGBAT CIRCLED SANS-SERIF DIGIT ONE
		"\u2781",    //DINGBAT CIRCLED SANS-SERIF DIGIT TWO
		"\u2782",    //DINGBAT CIRCLED SANS-SERIF DIGIT THREE
		"\u2783",    //DINGBAT CIRCLED SANS-SERIF DIGIT FOUR
		"\u2784",    //DINGBAT CIRCLED SANS-SERIF DIGIT FIVE
		"\u2785",    //DINGBAT CIRCLED SANS-SERIF DIGIT SIX
		"\u2786",    //DINGBAT CIRCLED SANS-SERIF DIGIT SEVEN
		"\u2787",    //DINGBAT CIRCLED SANS-SERIF DIGIT EIGHT
		"\u2788",    //DINGBAT CIRCLED SANS-SERIF DIGIT NINE
		"\u2789",    //DINGBAT CIRCLED SANS-SERIF NUMBER TEN
		"\u278A",    //DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT ONE
		"\u278B",    //DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT TWO
		"\u278C",    //DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT THREE
		"\u278D",    //DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT FOUR
		"\u278E",    //DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT FIVE
		"\u278F",    //DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT SIX
		"\u2790",    //DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT SEVEN
		"\u2791",    //DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT EIGHT
		"\u2792",    //DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT NINE
		"\u2793",    //DINGBAT NEGATIVE CIRCLED SANS-SERIF NUMBER TEN
		"\u2794",    //HEAVY WIDE-HEADED RIGHTWARDS ARROW
		"\u2798",    //HEAVY SOUTH EAST ARROW
		"\u2799",    //HEAVY RIGHTWARDS ARROW
		"\u279A",    //HEAVY NORTH EAST ARROW
		"\u279B",    //DRAFTING POINT RIGHTWARDS ARROW
		"\u279C",    //HEAVY ROUND-TIPPED RIGHTWARDS ARROW
		"\u279D",    //TRIANGLE-HEADED RIGHTWARDS ARROW
		"\u279E",    //HEAVY TRIANGLE-HEADED RIGHTWARDS ARROW
		"\u279F",    //DASHED TRIANGLE-HEADED RIGHTWARDS ARROW
		"\u27A0",    //HEAVY DASHED TRIANGLE-HEADED RIGHTWARDS ARROW
		"\u27A1",    //BLACK RIGHTWARDS ARROW
		"\u27A2",    //THREE-D TOP-LIGHTED RIGHTWARDS ARROWHEAD
		"\u27A3",    //THREE-D BOTTOM-LIGHTED RIGHTWARDS ARROWHEAD
		"\u27A4",    //BLACK RIGHTWARDS ARROWHEAD
		"\u27A5",    //HEAVY BLACK CURVED DOWNWARDS AND RIGHTWARDS ARROW
		"\u27A6",    //HEAVY BLACK CURVED UPWARDS AND RIGHTWARDS ARROW
		"\u27A7",    //SQUAT BLACK RIGHTWARDS ARROW
		"\u27A8",    //HEAVY CONCAVE-POINTED BLACK RIGHTWARDS ARROW
		"\u27A9",    //RIGHT-SHADED WHITE RIGHTWARDS ARROW
		"\u27AA",    //LEFT-SHADED WHITE RIGHTWARDS ARROW
		"\u27AB",    //BACK-TILTED SHADOWED WHITE RIGHTWARDS ARROW
		"\u27AC",    //FRONT-TILTED SHADOWED WHITE RIGHTWARDS ARROW
		"\u27AD",    //HEAVY LOWER RIGHT-SHADOWED WHITE RIGHTWARDS ARROW
		"\u27AE",    //HEAVY UPPER RIGHT-SHADOWED WHITE RIGHTWARDS ARROW
		"\u27AF",    //NOTCHED LOWER RIGHT-SHADOWED WHITE RIGHTWARDS ARROW
		"\u27B1",    //NOTCHED UPPER RIGHT-SHADOWED WHITE RIGHTWARDS ARROW
		"\u27B2",    //CIRCLED HEAVY WHITE RIGHTWARDS ARROW
		"\u27B3",    //WHITE-FEATHERED RIGHTWARDS ARROW
		"\u27B4",    //BLACK-FEATHERED SOUTH EAST ARROW
		"\u27B5",    //BLACK-FEATHERED RIGHTWARDS ARROW
		"\u27B6",    //BLACK-FEATHERED NORTH EAST ARROW
		"\u27B7",    //HEAVY BLACK-FEATHERED SOUTH EAST ARROW
		"\u27B8",    //HEAVY BLACK-FEATHERED RIGHTWARDS ARROW
		"\u27B9",    //HEAVY BLACK-FEATHERED NORTH EAST ARROW
		"\u27BA",    //TEARDROP-BARBED RIGHTWARDS ARROW
		"\u27BB",    //HEAVY TEARDROP-SHANKED RIGHTWARDS ARROW
		"\u27BC",    //WEDGE-TAILED RIGHTWARDS ARROW
		"\u27BD",    //HEAVY WEDGE-TAILED RIGHTWARDS ARROW
		"\u27BE"     //OPEN-OUTLINED RIGHTWARDS ARROW

	};

	public final static String [] UNICODEgeometricShapes = {	

		"\u25A0" ,      //BLACK SQUARE
		"\u25A1" ,      //WHITE SQUARE
		"\u25A2" ,      //WHITE SQUARE WITH ROUNDED CORNERS
		"\u25A3" ,      //WHITE SQUARE CONTAINING BLACK SMALL SQUARE
		"\u25A4" ,      //SQUARE WITH HORIZONTAL FILL
		"\u25A5" ,      //SQUARE WITH VERTICAL FILL
		"\u25A6" ,      //SQUARE WITH ORTHOGONAL CROSSHATCH FILL
		"\u25A7" ,      //SQUARE WITH UPPER LEFT TO LOWER RIGHT FILL
		"\u25A8" ,      //SQUARE WITH UPPER RIGHT TO LOWER LEFT FILL
		"\u25A9" ,      //SQUARE WITH DIAGONAL CROSSHATCH FILL
		"\u25AA" ,      //BLACK SMALL SQUARE
		"\u25AB" ,      //WHITE SMALL SQUARE
		"\u25AC" ,      //BLACK RECTANGLE
		"\u25AD" ,      //WHITE RECTANGLE
		"\u25AE" ,      //BLACK VERTICAL RECTANGLE
		"\u25AF" ,      //WHITE VERTICAL RECTANGLE
		"\u25B0" ,      //BLACK PARALLELOGRAM
		"\u25B1" ,      //WHITE PARALLELOGRAM
		"\u25B2" ,      //BLACK UP-POINTING TRIANGLE
		"\u25B3" ,      //WHITE UP-POINTING TRIANGLE
		"\u25B4" ,      //BLACK UP-POINTING SMALL TRIANGLE
		"\u25B5" ,      //WHITE UP-POINTING SMALL TRIANGLE
		"\u25B6" ,      //BLACK RIGHT-POINTING TRIANGLE
		"\u25B7" ,      //WHITE RIGHT-POINTING TRIANGLE
		"\u25B8" ,      //BLACK RIGHT-POINTING SMALL TRIANGLE
		"\u25B9" ,      //WHITE RIGHT-POINTING SMALL TRIANGLE
		"\u25BA" ,      //BLACK RIGHT-POINTING POINTER
		"\u25BB" ,      //WHITE RIGHT-POINTING POINTER
		"\u25BC" ,      //BLACK DOWN-POINTING TRIANGLE
		"\u25BD" ,      //WHITE DOWN-POINTING TRIANGLE
		"\u25BE" ,      //BLACK DOWN-POINTING SMALL TRIANGLE
		"\u25BF" ,      //WHITE DOWN-POINTING SMALL TRIANGLE
		"\u25C0" ,      //BLACK LEFT-POINTING TRIANGLE
		"\u25C1" ,      //WHITE LEFT-POINTING TRIANGLE
		"\u25C2" ,      //BLACK LEFT-POINTING SMALL TRIANGLE
		"\u25C3" ,      //WHITE LEFT-POINTING SMALL TRIANGLE
		"\u25C4" ,      //BLACK LEFT-POINTING POINTER
		"\u25C5" ,      //WHITE LEFT-POINTING POINTER
		"\u25C6" ,      //BLACK DIAMOND
		"\u25C7" ,      //WHITE DIAMOND
		"\u25C8" ,      //WHITE DIAMOND CONTAINING BLACK SMALL DIAMOND
		"\u25C9" ,      //FISHEYE
		"\u25CA" ,      //LOZENGE
		"\u25CB" ,      //WHITE CIRCLE
		"\u25CC" ,      //DOTTED CIRCLE
		"\u25CD" ,      //CIRCLE WITH VERTICAL FILL
		"\u25CE" ,      //BULLSEYE
		"\u25CF" ,      //BLACK CIRCLE
		"\u25D0" ,      //CIRCLE WITH LEFT HALF BLACK
		"\u25D1" ,      //CIRCLE WITH RIGHT HALF BLACK
		"\u25D2" ,      //CIRCLE WITH LOWER HALF BLACK
		"\u25D3" ,      //CIRCLE WITH UPPER HALF BLACK
		"\u25D4" ,      //CIRCLE WITH UPPER RIGHT QUADRANT BLACK
		"\u25D5" ,      //CIRCLE WITH ALL BUT UPPER LEFT QUADRANT BLACK
		"\u25D6" ,      //LEFT HALF BLACK CIRCLE
		"\u25D7" ,      //RIGHT HALF BLACK CIRCLE
		"\u25D8" ,      //INVERSE BULLET
		"\u25D9" ,      //INVERSE WHITE CIRCLE
		"\u25DA" ,      //UPPER HALF INVERSE WHITE CIRCLE
		"\u25DB" ,      //LOWER HALF INVERSE WHITE CIRCLE
		"\u25DC" ,      //UPPER LEFT QUADRANT CIRCULAR ARC
		"\u25DD" ,      //UPPER RIGHT QUADRANT CIRCULAR ARC
		"\u25DE" ,      //LOWER RIGHT QUADRANT CIRCULAR ARC
		"\u25DF" ,      //LOWER LEFT QUADRANT CIRCULAR ARC
		"\u25E0" ,      //UPPER HALF CIRCLE
		"\u25E1" ,      //LOWER HALF CIRCLE
		"\u25E2" ,      //BLACK LOWER RIGHT TRIANGLE
		"\u25E3" ,      //BLACK LOWER LEFT TRIANGLE
		"\u25E4" ,      //BLACK UPPER LEFT TRIANGLE
		"\u25E5" ,      //BLACK UPPER RIGHT TRIANGLE
		"\u25E6" ,      //WHITE BULLET

		/*
		"\u25E7" ,      //SQUARE WITH LEFT HALF BLACK
		"\u25E8" ,      //SQUARE WITH RIGHT HALF BLACK
		"\u25E9" ,      //SQUARE WITH UPPER LEFT DIAGONAL HALF BLACK
		"\u25EA" ,      //SQUARE WITH LOWER RIGHT DIAGONAL HALF BLACK
		"\u25EB" ,      //WHITE SQUARE WITH VERTICAL BISECTING LINE
		"\u25EC" ,      //WHITE UP-POINTING TRIANGLE WITH DOT
		"\u25ED" ,      //UP-POINTING TRIANGLE WITH LEFT HALF BLACK
		"\u25EE" ,      //UP-POINTING TRIANGLE WITH RIGHT HALF BLACK
		"\u25EF" ,      //LARGE CIRCLE
		"\u25F0" ,      //WHITE SQUARE WITH UPPER LEFT QUADRANT
		"\u25F1" ,      //WHITE SQUARE WITH LOWER LEFT QUADRANT
		"\u25F2" ,      //WHITE SQUARE WITH LOWER RIGHT QUADRANT
		"\u25F3" ,      //WHITE SQUARE WITH UPPER RIGHT QUADRANT
		"\u25F4" ,      //WHITE CIRCLE WITH UPPER LEFT QUADRANT
		"\u25F5" ,      //WHITE CIRCLE WITH LOWER LEFT QUADRANT
		"\u25F6" ,      //WHITE CIRCLE WITH LOWER RIGHT QUADRANT
		"\u25F7" ,      //WHITE CIRCLE WITH UPPER RIGHT QUADRANT
		 */

	};

	public final static String [] letterLikeSymbols = {	


		"\u20D7",    //     \\vec
		"\u210F",    //     \\hbar
		"\u2111",    //     \\Im
		"\u2113",    //     \\ell
		"\u2118",    //     \\wp
		"\u211C",    //     \\Re
		"\u2127",    //     \\mho
		"\u212F",    //     e
		"\u2132",    //     \\Finv
		"\u2135",    //     \\aleph
		"\u2136",    //     \\beth
		"\u2137",    //     \\gimel
		"\u2138",    //     \\daleth
		"\u2141",    //     \\Game


		/*		

		"\u2103" ,      //DEGREE CELSIUS
		"\u2109" ,      //DEGREE FAHRENHEIT
		"\u212A" ,      //KELVIN SIGN
		"\u212B" ,      //ANGSTROM SIGN

		"\u2107" ,      //EULER CONSTANT
		"\u210E" ,      //PLANCK CONSTANT
		"\u210F" ,      //PLANCK CONSTANT OVER TWO PI

		"\u2125" ,      //OUNCE SIGN
		"\u2126" ,      //OHM SIGN
		"\u2127" ,      //INVERTED OHM SIGN

		"\u2102" ,      //DOUBLE-STRUCK CAPITAL C
		"\u210D" ,      //DOUBLE-STRUCK CAPITAL H
		"\u2115" ,      //DOUBLE-STRUCK CAPITAL N
		"\u2119" ,      //DOUBLE-STRUCK CAPITAL P
		"\u211A" ,      //DOUBLE-STRUCK CAPITAL Q
		"\u211D" ,      //DOUBLE-STRUCK CAPITAL R
		"\u2124" ,      //DOUBLE-STRUCK CAPITAL Z

		"\u212D" ,      //BLACK-LETTER CAPITAL C
		"\u210C" ,      //BLACK-LETTER CAPITAL H
		"\u2111" ,      //BLACK-LETTER CAPITAL I
		"\u211C" ,      //BLACK-LETTER CAPITAL R
		"\u2128" ,      //BLACK-LETTER CAPITAL Z

		"\u212F" ,      //SCRIPT SMALL E
		"\u2113" ,      //SCRIPT SMALL L
		"\u2134" ,      //SCRIPT SMALL O
		"\u210A" ,      //SCRIPT SMALL G

		"\u212C" ,      //SCRIPT CAPITAL B	
		"\u2130" ,      //SCRIPT CAPITAL E
		"\u2131" ,      //SCRIPT CAPITAL F
		"\u210B" ,      //SCRIPT CAPITAL H
		"\u2110" ,      //SCRIPT CAPITAL I
		"\u2112" ,      //SCRIPT CAPITAL L
		"\u2133" ,      //SCRIPT CAPITAL M
		"\u2118" ,      //SCRIPT CAPITAL P
		"\u211B" ,      //SCRIPT CAPITAL R

		"\u2132" ,      //TURNED CAPITAL F
		"\u2129" ,      //TURNED GREEK SMALL LETTER IOTA
		"\u213A" ,      //ROTATED CAPITAL Q

		/*
		"\u2114" ,      //L B BAR SYMBOL
		"\u2116" ,      //NUMERO SIGN					
		"\u2117" ,      //SOUND RECORDING COPYRIGHT	
		"\u211E" ,      //PRESCRIPTION TAKE
		"\u211F" ,      //RESPONSE
		"\u2120" ,      //SERVICE MARK
		"\u2121" ,      //TELEPHONE SIGN
		"\u2122" ,      //TRADE MARK SIGN
		"\u2123" ,      //VERSICLE		
		"\u2100" ,      //ACCOUNT OF
		"\u2101" ,      //ADDRESSED TO THE SUBJECT	
		"\u2104" ,      //CENTRE LINE SYMBOL
		"\u2105" ,      //CARE OF
		"\u2106" ,      //CADA UNA
		"\u2108" ,      //SCRUPLE
		 */

		/*
		"\u2135" ,      //ALEF SYMBOL
		"\u2136" ,      //BET SYMBOL
		"\u2137" ,      //GIMEL SYMBOL
		"\u2138" ,      //DALET SYMBOL
		"\u2139" ,      //INFORMATION SOURCE
		"\u212E" ,      //ESTIMATED SYMBOL	
		 */


	};


	public final static String [] currency = {	

		"\u20A0" ,     //EURO-CURRENCY SIGN
		"\u20A1" ,     //COLON SIGN
		"\u20A2" ,     //CRUZEIRO SIGN
		"\u20A3" ,     //FRENCH FRANC SIGN
		"\u20A4" ,     //LIRA SIGN
		"\u20A5" ,     //MILL SIGN
		"\u20A6" ,     //NAIRA SIGN
		"\u20A7" ,     //PESETA SIGN
		"\u20A8" ,     //RUPEE SIGN
		"\u20A9" ,     //WON SIGN
		"\u20AA" ,     //NEW SHEQEL SIGN
		"\u20AB" ,     //DONG SIGN
		"\u20AC" ,     //EURO SIGN
		"\u20AD" ,     //KIP SIGN
		"\u20AE" ,     //TUGRIK SIGN
		"\u20AF" ,     //DRACHMA SIGN

	};




	public final static String [] JLatex = {	

		"\u007D",    //     \\rbrace
		"\u00AC",    //     \\lnot
		"\u00B1",    //     \\pm
		"\u2213",    //     \\mp
		"\u00B7",    //     \\centerdot
		"\u00D7",    //     \\times
		"\u00F0",    //     \\eth
		"\u00F7",    //     \\div

		"\u221A",    //     \\surd
		"\u2202",    //     \\partial
		"\u2207",    //     \\nabla
		"\u220F",    //     \\prod
		"\u2210",    //     \\coprod
		"\u2211",    //     \\sum

		//	"\u222B",    //     \\int
		"\u222B",    //     \\smallint
		"\u222C",    //     \\iint
		"\u222D",    //     \\iiint
		"\u222E",    //     \\oint
		"\u2A0C",    //     \\iiiint

		"\u2205",    //     \\emptyset
		//"\u2205",    //     \\varnothing



		/****  accents
		"\u02DA",    //     \\jlatexmathring
		"\u02DA",    //     \\mathring
		"\u0300",    //     \\grave
		"\u0301",    //     \\acute
		"\u0302",    //     \\hat
		"\u0302",    //     \\widehat
		"\u0303",    //     \\tilde
		"\u0303",    //     \\widetilde
		"\u0304",    //     \\bar
		"\u0306",    //     \\breve
		"\u0307",    //     \\dot
		"\u0308",    //     \\ddot
		"\u030B",    //     \\doubleacute
		"\u030C",    //     \\check
		 */

		"\u03F5",    //     \\epsilon
		"\u03F6",    //     \\backepsilon

		/**** spaces
		"\u2002",    //     \\;
		"\u2002",    //     \\thickspace
		"\u2003",    //     \\quad
		"\u2004",    //     \\,
		"\u2004",    //     \\thinspace
		"\u2005",    //     \\:
		"\u2005",    //     \\medspace
		"\u200B",    //     \\!
		"\u2016",    //     \\|
		 */


		"\u2016",    //     \\lVert
		//"\u2016",    //     \\rVert

		"\u2020",    //     \\dagger
		"\u2021",    //     \\ddagger





		"\u2032",    //     \\&apos;
		"\u2032",    //     \\prime
		"\u2035",    //     \\backprime
		"\u20D7",    //     \\vec
		//"\u210F",    //     \\hbar
		"\u210F",    //     \\hslash


		"\u2113",    //     \\ell
		"\u2118",    //     \\wp

		"\u2111",    //     \\Im
		"\u211C",    //     \\Re
		"\u2127",    //     \\mho
		"\u212F",    //     e
		"\u2132",    //     \\Finv
		"\u2135",    //     \\aleph
		"\u2136",    //     \\beth
		"\u2137",    //     \\gimel
		"\u2138",    //     \\daleth
		"\u2141",    //     \\Game



		"\u2212",    //     \\minus

		"\u2214",    //     \\dotplus

		"\u2215",    //     \\slash
		"\u2216",    //     \\setminus
		"\u2216",    //     \\smallsetminus
		"\u2218",    //     \\circ

		"\u2219",    //     \\bullet
		"\u221E",    //     \\infty



		"\u2220",    //     \\angle
		//"\u2221",    //     \\measuredangle
		"\u2222",    //     \\sphericalangle
		//"\u2223",    //     \\arrowvert
		//"\u2223",    //     \\mid
		//"\u2223",    //     \\shortmid
		"\u2223",    //     \\vert
		//"\u2224",    //     \\nmid
		"\u2224",    //     \\nshortmid
		//"\u2225",    //     \\Arrowvert
		"\u2225",    //     \\parallel
		//"\u2225",    //     \\shortparallel
		"\u2225",    //     \\Vert
		"\u2226",    //     \\nparallel
		//"\u2226",    //     \\nshortparallel


		"\u2229",    //     \\cap
		"\u222A",    //     \\cup








		"\u224E",    //     \\Bumpeq
		"\u224F",    //     \\bumpeq
		"\u2250",    //     \\doteq
		"\u2251",    //     \\doteqdot
		"\u2252",    //     \\fallingdotseq
		"\u2253",    //     \\risingdotseq
		"\u2256",    //     \\eqcirc
		"\u2257",    //     \\circeq
		"\u225C",    //     \\triangleq
		"\u225F",    //     \\stackrel{?}{=}










		"\u2293",    //     \\sqcap
		"\u2294",    //     \\sqcup
		"\u2295",    //     \\oplus
		"\u2296",    //     \\ominus
		Unicode.VECTOR_PRODUCT+"",    //     \\otimes
		"\u2298",    //     \\oslash
		"\u2299",    //     \\odot
		"\u229A",    //     \\circledcirc
		"\u229B",    //     \\circledast
		"\u229D",    //     \\circleddash
		"\u229E",    //     \\boxplus
		"\u229F",    //     \\boxminus
		"\u22A0",    //     \\boxtimes
		"\u22A1",    //     \\boxdot
		"\u22A2",    //     \\vdash
		"\u22A3",    //     \\dashv
		"\u22A4",    //     \\top
		"\u22A5",    //     \\bot
		"\u22A5",    //     \\perp
		"\u22A7",    //     \\models
		"\u22A8",    //     \\vDash
		"\u22A9",    //     \\Vdash
		"\u22AA",    //     \\Vvdash
		"\u22AC",    //     \\nvdash
		"\u22AD",    //     \\nvDash
		"\u22AE",    //     \\nVdash
		"\u22AF",    //     \\nVDash
		"\u22B2",    //     \\lhd
		"\u22B2",    //     \\vartriangleleft
		"\u22B3",    //     \\rhd
		//"\u22B3",    //     \\vartriangleright
		"\u22B4",    //     \\unlhd
		//"\u22B4",    //     \\trianglelefteq
		"\u22B5",    //     \\unrhd
		//"\u22B5",    //     \\trianglerighteq
		"\u22B8",    //     \\multimap
		"\u22BA",    //     \\intercal
		"\u22C0",    //     \\bigwedge
		"\u22C1",    //     \\bigvee
		"\u22C2",    //     \\bigcap
		"\u22C3",    //     \\bigcup
		"\u22C4",    //     \\diamond
		"\u22C5",    //     \\cdot
		"\u22C6",    //     \\star
		"\u22C7",    //     \\divideontimes
		"\u22C8",    //     \\bowtie
		"\u22C9",    //     \\ltimes
		"\u22CA",    //     \\rtimes
		"\u22CB",    //     \\leftthreetimes
		"\u22CC",    //     \\rightthreetimes
		"\u22CD",    //     \\backsimeq
		"\u22CE",    //     \\curlyvee
		"\u22CF",    //     \\curlywedge




		"\u22D2",    //     \\Cap
		"\u22D2",    //     \\doublecap
		"\u22D3",    //     \\Cup
		"\u22D3",    //     \\doublecup
		"\u22D4",    //     \\pitchfork
		"\u22D6",    //     \\lessdot
		"\u22D7",    //     \\gtrdot
		"\u22D8",    //     \\llless
		"\u22D9",    //     \\ggg
		"\u22D9",    //     \\gggtr
		"\u22DB",    //     \\gtreqless
		"\u22DE",    //     \\curlyeqprec
		"\u22DF",    //     \\curlyeqsucc
		"\u22E6",    //     \\lnsim
		"\u22E7",    //     \\gnsim
		"\u22E8",    //     \\precnsim
		"\u22E9",    //     \\succnsim
		"\u22EA",    //     \\ntriangleleft
		"\u22EB",    //     \\ntriangleright
		"\u22EC",    //     \\ntrianglelefteq
		"\u22ED",    //     \\ntrianglerighteq




		"\u2305",    //     \\barwedge
		"\u2306",    //     \\doublebarwedge
		"\u2308",    //     \\lceil
		"\u2309",    //     \\rceil
		"\u230A",    //     \\lfloor
		"\u230B",    //     \\rfloor


		/*** brackets 
		"\u2322",    //     \\smallfrown
		//"\u2323",    //     \\frown
		//"\u2323",    //     \\smallsmile
		"\u2323",    //     \\smile


		"\u2329",    //     \\langle
		"\u232A",    //     \\rangle
		"\u23B0",    //     \\lmoustache
		"\u23B1",    //     \\rmoustache

		 */

		"\u24C8",    //     \\circledS

		"\u2571",    //     \\diagup
		"\u2572",    //     \\diagdown




		"\u2026",    //     \\dots
		//	"\u2026",    //     \\dotsc
		//	"\u2026",    //     \\dotso
		//	"\u2026",    //     \\hdots
		//	"\u2026",    //     \\ldots

		"\u22EE",    //     \\vdots
		"\u22EF",    //     \\cdots
		"\u22EF",    //     \\dotsb
		"\u22F1",    //     \\ddots




		"\u2A3F",    //     \\amalg
		"\u2A61",    //     \\veebar
		"\u2A7D",    //     \\leqslant
		"\u2A7D",    //     \\nleqslant
		"\u2A7E",    //     \\geqslant
		"\u2A7E",    //     \\ngeqslant
		"\u2A85",    //     \\lessapprox
		"\u2A86",    //     \\gtrapprox
		"\u2A87",    //     \\lneq
		"\u2A88",    //     \\gneq
		"\u2A89",    //     \\lnapprox
		"\u2A8A",    //     \\gnapprox
		"\u2A8B",    //     \\lesseqqgtr
		"\u2A8C",    //     \\gtreqqless
		"\u2A95",    //     \\eqslantless
		"\u2A96",    //     \\eqslantgtr
		"\u2AA2",    //     \\gg
		"\u2AAF",    //     \\npreceq
		"\u2AAF",    //     \\preceq
		"\u2AB0",    //     \\nsucceq
		"\u2AB0",    //     \\succeq
		"\u2AB5",    //     \\precneqq
		"\u2AB6",    //     \\succneqq
		"\u2AB7",    //     \\precapprox
		"\u2AB8",    //     \\succapprox
		"\u2AB9",    //     \\precnapprox
		"\u2ABA",    //     \\succnapprox
		"\u2AC5",    //     \\nsubseteqq
		"\u2AC5",    //     \\subseteqq
		"\u2AC6",    //     \\supseteqq
		"\u2ACB",    //     \\subsetneqq
		"\u2ACB",    //     \\varsubsetneqq
		"\u2ACC",    //     \\supsetneqq
		"\u2ACC",    //     \\varsupsetneqq



	};




	public final static String [] displayChars = { 	
		"\u2245", // congruent	
		"\u2261",  // equivalent
		"\u2221",  // angle
		"\u2206"  // triangle
	};

	public final static String [] specialChars = { 	
		"\u00b2",  // exponents ^2 
		"\u00b3",  // exponents ^3 
		"\u00b0", // degree	
		"\u03c0", // pi	
		Unicode.EULER_STRING, // e
		"\u221e", // infinity
		ExpressionNodeConstants.strVECTORPRODUCT, //  "\u2297", // vector product (circled times)
		"sqrt(x)",
		"cbrt(x)",
		"abs(x)",
		"sgn(x)",
		"ln(x)",
		"lg(x)",
		"ld(x)",
		"sin(x)",
		"cos(x)",
		"tan(x)",
		"asin(x)",
		"acos(x)",
		"atan(x)",
		"sinh(x)",
		"cosh(x)",
		"tanh(x)",
		"asinh(x)",
		"acosh(x)",
		"atanh(x)",
		"floor(x)",
		"ceil(x)",
		"round(x)",
		"gamma(x)",
		"random()",
		ExpressionNodeConstants.strEQUAL_BOOLEAN,
		ExpressionNodeConstants.strNOT_EQUAL,
		ExpressionNodeConstants.strLESS_EQUAL,
		ExpressionNodeConstants.strGREATER_EQUAL,
		ExpressionNodeConstants.strNOT,
		ExpressionNodeConstants.strAND,
		ExpressionNodeConstants.strOR, 
		ExpressionNodeConstants.strPARALLEL,
		ExpressionNodeConstants.strPERPENDICULAR,
		ExpressionNodeConstants.strIS_ELEMENT_OF,
		ExpressionNodeConstants.strIS_SUBSET_OF,
		ExpressionNodeConstants.strIS_SUBSET_OF_STRICT,
	};



	public final static String [] symbols = {

		"\u03c0",                // pi	
		Unicode.EULER_STRING,    // e
		"\u00b2",                // exponents ^2 
		"\u00b3",                // exponents ^3 
		"\u00b0",                // degree			
		"\u221e",                // infinity
		ExpressionNodeConstants.strVECTORPRODUCT, //  "\u2297", // vector product (circled times)
		ExpressionNodeConstants.strEQUAL_BOOLEAN,
		ExpressionNodeConstants.strNOT_EQUAL,
		ExpressionNodeConstants.strLESS_EQUAL,
		ExpressionNodeConstants.strGREATER_EQUAL,
		ExpressionNodeConstants.strNOT,
		ExpressionNodeConstants.strAND,
		ExpressionNodeConstants.strOR, 
		ExpressionNodeConstants.strPARALLEL,
		ExpressionNodeConstants.strPERPENDICULAR,
		ExpressionNodeConstants.strIS_ELEMENT_OF,
		ExpressionNodeConstants.strIS_SUBSET_OF,
		ExpressionNodeConstants.strIS_SUBSET_OF_STRICT,
	};


}
