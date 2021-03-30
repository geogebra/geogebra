package com.himamis.retex.editor.share.util;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("javadoc")
public class Unicode {

	public static final char MULTIPLY = '\u00d7';
	public static final char DIVIDE = '\u00f7';
	final public static char MINUS = '\u2212';
	final public static char LESS_EQUAL = '\u2264';
	final public static char GREATER_EQUAL = '\u2265';
	final public static char INFINITY = '\u221e';
	final public static String MINUS_INFINITY_STRING = "-\u221e";
	final public static char SUPERSCRIPT_MINUS = '\u207b';
	final public static String SUPERSCRIPT_MINUS_ONE_STRING = "\u207b\u00b9";
	final public static char SUPERSCRIPT_0 = '\u2070';
	final public static char SUPERSCRIPT_1 = '\u00b9';
	final public static char SUPERSCRIPT_2 = '\u00b2';
	final public static char SUPERSCRIPT_3 = '\u00b3';
	final public static char SUPERSCRIPT_4 = '\u2074';
	final public static char SUPERSCRIPT_5 = '\u2075';
	final public static char SUPERSCRIPT_6 = '\u2076';
	final public static char SUPERSCRIPT_7 = '\u2077';
	final public static char SUPERSCRIPT_8 = '\u2078';
	final public static char SUPERSCRIPT_9 = '\u2079';
	final private static List<Character> SUPERSCRIPTS = Arrays.asList(
			Unicode.SUPERSCRIPT_0, Unicode.SUPERSCRIPT_1,
			Unicode.SUPERSCRIPT_2, Unicode.SUPERSCRIPT_3,
			Unicode.SUPERSCRIPT_4, Unicode.SUPERSCRIPT_5,
			Unicode.SUPERSCRIPT_6, Unicode.SUPERSCRIPT_7,
			Unicode.SUPERSCRIPT_8, Unicode.SUPERSCRIPT_9);
	final public static char ZERO_WIDTH_SPACE = '\u200b';
	final public static char RIGHT_TO_LEFT_MARK = '\u200f';
	final public static String RIGHT_TO_LEFT_UNARY_MINUS_SIGN = "\u200f-\u200f";
	final public static char LEFT_TO_RIGHT_MARK = '\u200e';

	// degrees, minutes, seconds
	final public static char DEGREE_CHAR = '\u00b0';
	final public static char MINUTES = '\'';
	final public static char SECONDS = '\u2033';

	final public static String DEGREE_STRING = Character.toString(DEGREE_CHAR);

	final public static char e_GRAVE = '\u00E8';
	final public static char e_ACUTE = '\u00E9';

	/** Unicode symbol for e */
	final public static char EULER_CHAR = '\u212f';

	// lower case Greek
	public static final char alpha = '\u03B1';
	public static final char beta = '\u03B2';
	public static final char gamma = '\u03B3';
	public static final char delta = '\u03B4';
	public static final char epsilon = '\u03B5';
	public static final char zeta = '\u03B6';
	public static final char eta = '\u03B7';
	public static final char theta = '\u03B8';
	public static final String theta_STRING = theta + "";
	public static final char iota = '\u03B9';
	public static final char kappa = '\u03BA';
	public static final char lambda = '\u03BB';
	public static final String lambda_STRING = lambda + "";
	public static final char mu = '\u03BC';
	public static final char nu = '\u03BD';
	public static final char xi = '\u03BE';
	public static final char omicron = '\u03BF';
	public static final char pi = '\u03C0';
	public static final char rho = '\u03C1';
	public static final char sigmaf = '\u03C2'; // sigma, final form
	public static final char sigma = '\u03C3';
	public static final char tau = '\u03C4';
	public static final char upsilon = '\u03C5';

	/** \\varphi, curly */
	public static final char phi = '\u03C6';
	/** \\phi "straight" */
	public static final char phi_symbol = '\u03D5';

	public static final char chi = '\u03C7';
	public static final char psi = '\u03C8';
	public static final char omega = '\u03C9';

	final public static String EULER_STRING = EULER_CHAR + "";
	final public static String EULER_GAMMA_STRING = "\u212F_\u03B3";
	public static final String ALPHA_BETA_GAMMA = alpha + "" + beta + ""
			+ gamma;
	final public static String PI_STRING = Character.toString(pi);

	// UPPER CASE Greek
	public static final char Alpha = '\u0391';
	public static final char Beta = '\u0392';
	public static final char Gamma = '\u0393';
	public static final char Delta = '\u0394';
	public static final char Epsilon = '\u0395';
	public static final char Zeta = '\u0396';
	public static final char Eta = '\u0397';
	public static final char Theta = '\u0398';
	public static final char Iota = '\u0399';
	public static final char Kappa = '\u039A';
	public static final char Lambda = '\u039B';
	public static final char Mu = '\u039C';
	public static final char Nu = '\u039D';
	public static final char Xi = '\u039E';
	public static final char Omicron = '\u039F';
	public static final char Pi = '\u03A0';
	public static final char Rho = '\u03A1';
	// there is no Sigmaf, and no \u03A2 character either
	public static final char Sigma = '\u03A3';
	public static final char Tau = '\u03A4';
	public static final char Upsilon = '\u03A5';
	public static final char Phi = '\u03A6';
	public static final char Chi = '\u03A7';
	public static final char Psi = '\u03A8';
	public static final char Omega = '\u03A9';

	public static final char INTEGRAL = '\u222b';
	public static final char SQUARE_ROOT = '\u221a';
	public static final char PLUSMINUS = '\u00b1';
	public static final char NOTEQUAL = '\u2260';
	public static final char NOT = '\u00ac';
	public static final char AND = '\u2227';
	public static final char OR = '\u2228';

	/** circled plus, could also use \u22bb */
	public static final char XOR = '\u2295';

	// used by Giac for polar separator instead of ;
	// eg (2;3)
	final public static char MEASURED_ANGLE = '\u2221';

	public static final char PARALLEL = '\u2225';
	public static final char PERPENDICULAR = '\u27c2';
	public static final char IS_ELEMENT_OF = '\u2208';
	public static final char IS_SUBSET_OF = '\u2286';
	public static final char IS_SUBSET_OF_STRICT = '\u2282';
	public static final char COLON_EQUALS = '\u2254';
	// public static final char ANGLE = '\u2220';
	// public static final char ACCENT_ACUTE = '\u00b4';
	// public static final char ACCENT_GRAVE = '\u0060';
	// public static final char ACCENT_CARON = '\u02c7';
	// public static final char ACCENT_CIRCUMFLEX = '\u005e';
	public static final char QUESTEQ = '\u225f';

	final public static char FEMININE_ORDINAL_INDICATOR = '\u00aa';

	// GREEK SMALL LETTER IOTA WITH TONOS
	final public static char IMAGINARY = '\u03af';

	// non-breaking (hard) space
	public static final char NBSP = '\u00a0';

	/* helper Unicode strings for fixing Hungarian translations */
	// These endings will get -re, -nek, -hez:
	final public static String TRANSLATION_FIX_HU_END_E1_STRING = "bcde"
			+ "\u00E9fgi\u00EDjlmnprstvwxz1479'";
	// These endings will get -ra, -nak, -ban, -ba, -hoz:
	final public static String TRANSLATION_FIX_HU_END_O1_STRING = "ahko\u00F3qu\u00FAy368";
	// These endings will get -re, -nek, -ben, -be, -ho(umlaut)z:
	final public static String TRANSLATION_FIX_HU_END_OE1_STRING = "\u00F6\u0151\u00FC\u017125";
	// "-ho(umlaut)z":
	final public static String TRANSLATION_FIX_HU_OE_STRING = "\u00F6";
	final public static String TRANSLATION_FIX_HU_HOEZ_STRING = "h\u00F6z";

	// fractions

	final public static char FRACTION1_8 = '\u215b';
	final public static char FRACTION1_4 = '\u00bc';
	final public static char FRACTION3_8 = '\u215c';
	final public static char FRACTION1_2 = '\u00bd';
	final public static char FRACTION5_8 = '\u215d';
	final public static char FRACTION3_4 = '\u00be';
	final public static char FRACTION7_8 = '\u215e';

	// various characters which hang down below the line
	// gjy with/without accents
	// characters with cedillas
	// some Greek, Russian, Malayalam, Arabic
	public static final String CHARACTERS_WITH_DESCENDERS_STRING = "\u00B5\u1EF3\u0177\u0135"
			+ "\u0157\u0163\u0137\u015F\u0137\u013C\u00E7\u0146\u1EF9\u011F\u011D\u0123"
			+ "\u00FDgjy\u03BE\u03B2\u03C8\u03B3\u03B7\u03C2\u0444\u0449\u0446\u0D71\u0D6C"
			+ "\u0D6B\u0D33\u0D67\u0630\u0648\u0635\u0628\u0631\u064D\u0633\u062E\u064A\u064D";
	public static final char N_DASH = '\u2013';
	public static final char ARABIC_COMMA = '\u066b';
	public static final char ELLIPSIS = '\u2026';
	public static final char SECTION_SIGN = '\u00a7';
	public static final char VECTOR_PRODUCT = '\u2297';
	public static final char OPEN_DOUBLE_QUOTE = '\u201C';
	public static final char CLOSE_DOUBLE_QUOTE = '\u201D';

	// http://www.xe.com/symbols.php
	public static final char CURRENCY_POUND = '\u00a3';
	public static final char CURRENCY_EURO = '\u20ac';
	public static final char CURRENCY_DOLLAR = '$';
	public static final char CURRENCY_YEN = '\u00a5';
	public static final char CURRENCY_WON = '\u20a9';
	public static final char CURRENCY_BAHT = '\u03ef';
	public static final char CURRENCY_DONG = '\u20ab';
	public static final char CURRENCY_SHEKEL = '\u20aa';
	public static final char CURRENCY_TUGHRIK = '\u20ae';
	public static final char CURRENCY_RUPEE = '\u20a8';
	public static final char CURRENCY_INDIAN_RUPEE = '\u20b9';
	public static final String FORTY_FIVE_DEGREES_STRING = "45" + DEGREE_CHAR;

	public static final char IMPLIES = '\u2192';
	public static final char IMPLIED_FROM = '\u2190';

	/** shorter arrow than "IMPLIES" */
	public static final char CAS_OUTPUT_PREFIX = '\u279E';

	public static final char CAS_OUTPUT_PREFIX_RTL = '\u2190';
	public static final char CAS_OUTPUT_KEEPINPUT = '\u2713';
	public static final char CAS_OUTPUT_NUMERIC = '\u2248';
	public static final char MICRO = '\u00b5';

	public static final char LCEIL = '\u2308';
	public static final char RCEIL = '\u2309';
	public static final char LFLOOR = '\u230a';
	public static final char RFLOOR = '\u230b';

	public static final char CENTER_DOT = '\u00b7';
	public static final char BULLET = '\u2022';
	public static final String ASSIGN_STRING = "\u2254";
	public static String currencyList = CURRENCY_BAHT + "" + CURRENCY_DOLLAR
			+ "" + CURRENCY_DONG + "" + CURRENCY_EURO + ""
			+ CURRENCY_INDIAN_RUPEE + "" + CURRENCY_POUND + "" + CURRENCY_RUPEE
			+ "" + CURRENCY_SHEKEL + "" + CURRENCY_TUGHRIK + "" + CURRENCY_WON
			+ "" + CURRENCY_YEN + "";

	/**
	 * @param c
	 *            character
	 * @return whether char is superscript digit
	 */
	final public static boolean isSuperscriptDigit(final char c) {
		return ((c >= Unicode.SUPERSCRIPT_0) && (c <= Unicode.SUPERSCRIPT_9))
				|| (c == Unicode.SUPERSCRIPT_1) || (c == Unicode.SUPERSCRIPT_2)
				|| (c == Unicode.SUPERSCRIPT_3);
	}

	/**
	 * @param num decimal digit
	 * @return digit converted to unicode superscript
	 */
	public static char numberToSuperscript(int num) {
		return SUPERSCRIPTS.get(num);
	}

	/**
	 * @param superscript superscript unicode character
	 * @return unicode character converted to numeric value
	 */
	public static int superscriptToNumber(char superscript) {
		return Unicode.SUPERSCRIPTS.indexOf(superscript);
	}
}
