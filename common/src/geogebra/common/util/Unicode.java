package geogebra.common.util;


public class Unicode {

	final public static char minus = '\u2212';
	final public static char LESS_EQUAL = '\u2264';
	final public static char GREATER_EQUAL = '\u2265';
	final public static char Infinity = '\u221e';
	final public static String MinusInfinity = "-\u221e";
	final public static char Superscript_Minus = '\u207b';
	final public static char Superscript_0 = '\u2070';
	final public static char Superscript_1 = '\u00b9';
	final public static char Superscript_2 = '\u00b2';
	final public static char Superscript_3 = '\u00b3';
	final public static char Superscript_4 = '\u2074';
	final public static char Superscript_5 = '\u2075';
	final public static char Superscript_6 = '\u2076';
	final public static char Superscript_7 = '\u2077';
	final public static char Superscript_8 = '\u2078';
	final public static char Superscript_9 = '\u2079';
	final public static char RightToLeftMark = '\u200f';
	final public static String RightToLeftUnaryMinusSign = "\u200f-\u200f";
	final public static char LeftToRightMark = '\u200e';
	final public static String superscriptMinusOneBracket = "\u207b\u00b9(";
	final public static char degreeChar = '\u00b0';
	final public static String degree = Character.toString(degreeChar);

	final public static char eGrave = '\u00E8';
	final public static char eAcute = '\u00E9';

	/** Unicode symbol for e */
	final public static char eulerChar = '\u212f';
	final public static String EULER_STRING = eulerChar + "";

	/** Unicode symbol for pi */
	final public static char piChar = '\u03c0'; // "\u0435";
	final public static String PI_STRING = Character.toString(piChar);
	public static final String alphaBetaGamma = "\u03b1\u03b2\u03b3";
	// lower case Greek
	public static final String alpha = "\u03B1";
	public static final String beta = "\u03B2";
	public static final String gamma = "\u03B3";
	public static final String delta = "\u03B4";
	public static final String epsilon = "\u03B5";
	public static final String zeta = "\u03B6";
	public static final String eta = "\u03B7";
	public static final String theta = "\u03B8";
	public static final String iota = "\u03B9";
	public static final String kappa = "\u03BA";
	public static final String lambda = "\u03BB";
	public static final String mu = "\u03BC";
	public static final String nu = "\u03BD";
	public static final String xi = "\u03BE";
	public static final String omicron = "\u03BF";
	public static final String pi = "\u03C0";
	public static final String rho = "\u03C1";
	public static final String sigmaf = "\u03C2"; // sigma, final form
	public static final String sigma = "\u03C3";
	public static final String tau = "\u03C4";
	public static final String upsilon = "\u03C5";
	public static final String phi = "\u03C6";
	public static final String phi_symbol = "\u03D5"; // "straight" phi	
	public static final String chi = "\u03C7";
	public static final String psi = "\u03C8";
	public static final String omega = "\u03C9";
	
	// UPPER CASE Greek
	public static final String Alpha = "\u0391";
	public static final String Beta = "\u0392";
	public static final String Gamma = "\u0393";
	public static final String Delta = "\u0394";
	public static final String Epsilon = "\u0395";
	public static final String Zeta = "\u0396";
	public static final String Eta = "\u0397";
	public static final String Theta = "\u0398";
	public static final String Iota = "\u0399";
	public static final String Kappa = "\u039A";
	public static final String Lambda = "\u039B";
	public static final String Mu = "\u039C";
	public static final String Nu = "\u039D";
	public static final String Xi = "\u039E";
	public static final String Omicron = "\u039F";
	public static final String Pi = "\u03A0";
	public static final String Rho = "\u03A1";
	// <!-- there is no Sigmaf, and no \U03A2 character either -->
	public static final String Sigma = "\u03A3";
	public static final String Tau = "\u03A4";
	public static final String Upsilon = "\u03A5";
	public static final String Phi = "\u03A6";
	public static final String Chi = "\u03A7";
	public static final String Psi = "\u03A8";
	public static final String Omega = "\u03A9";
	
	public static final String SQUARE_ROOT = "\u221a";
	public static final String PLUSMINUS = "\u00b1";
	public static final String NOTEQUAL = "\u2260";



	
	final public static char FEMININE_ORDINAL_INDICATOR = '\u00aa';

	final public static String IMAGINARY = "\u03af"; // GREEK SMALL LETTER IOTA
														// WITH TONOS
	public static final String NBSP = "\u00a0"; // non-breaking (hard) space

	/* helper Unicode strings for fixing Hungarian translations */
	// These endings will get -re, -nek, -hez:
	final public static String translationFixHu_endE1 = "bcde\u00E9fgi\u00EDjlmnprstvwxz1479'";
	// These endings will get -ra, -nak, -ban, -ba, -hoz:
	final public static String translationFixHu_endO1 = "ahko\u00F3qu\u00FAy368";
	// These endings will get -re, -nek, -ben, -be, -höz:
	final public static String translationFixHu_endOE1 = "\u00F6\u0151\u00FC\u017125";
	// "-höz":
	final public static String translationFixHu_oe = "\u00F6";
	final public static String translationFixHu_hoez = "h\u00F6z";

	// fractions

	final public static String fraction1_8 = "\u215b";
	final public static String fraction1_4 = "\u00bc";
	final public static String fraction3_8 = "\u215c";
	final public static String fraction1_2 = "\u00bd";
	final public static String fraction5_8 = "\u215d";
	final public static String fraction3_4 = "\u00be";
	final public static String fraction7_8 = "\u215e";
	public static final char multiply = '\u00d7'; // multiply cross
	
	// various characters which hang down below the line
	// gjy with/without accents
	// characters with cedillas
	// some Greek, Russian, Malayalam, Arabic
	public static final String charactersWithDescenders = "\u00B5\u1EF3\u0177\u0135\u0157\u0163\u0137\u015F\u0137\u013C\u00E7\u0146\u1EF9\u011F\u011D\u0123\u00FDgjy\u03BE\u03B2\u03C8\u03B3\u03B7\u03C2\u0444\u0449\u0446\u0D71\u0D6C\u0D6B\u0D33\u0D67\u0630\u0648\u0635\u0628\u0631\u064D\u0633\u062E\u064A\u064D";
	public static final char nDash = '\u2013';

	/*
	 * converts an integer to a unicode superscript string (including minus
	 * sign) eg for use as a power
	 * 
	 * @author Michael
	 */
	final public static String numberToIndex(int i) {

		final StringBuilder sb = new StringBuilder();
		if (i < 0) {
			sb.append(Superscript_Minus); // superscript minus sign
			i = -i;
		}

		if (i == 0) {
			sb.append(Superscript_0); // zero
		} else {
			while (i > 0) {
				switch (i % 10) {
				case 0:
					sb.insert(0, Superscript_0);
					break;
				case 1:
					sb.insert(0, Superscript_1);
					break;
				case 2:
					sb.insert(0, Superscript_2);
					break;
				case 3:
					sb.insert(0, Superscript_3);
					break;
				case 4:
					sb.insert(0, Superscript_4);
					break;
				case 5:
					sb.insert(0, Superscript_5);
					break;
				case 6:
					sb.insert(0, Superscript_6);
					break;
				case 7:
					sb.insert(0, Superscript_7);
					break;
				case 8:
					sb.insert(0, Superscript_8);
					break;
				case 9:
					sb.insert(0, Superscript_9);
					break;

				}
				i = i / 10;
			}
		}

		return sb.toString();
	}

	final public static boolean isSuperscriptDigit(final char c) {
		return ((c >= Superscript_0) && (c <= Superscript_9))
				|| (c == Superscript_1) || (c == Superscript_2)
				|| (c == Superscript_3);
	}


}
