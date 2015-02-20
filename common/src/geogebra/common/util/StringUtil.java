package geogebra.common.util;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GFont;
import geogebra.common.util.debug.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

public class StringUtil {

	/**
	 * @param data
	 *            to convert
	 * @return data as a hex String
	 */
	public static String convertToHex(byte[] data) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < data.length; i++) {

			buf.append(Character.forDigit((data[i] >> 4) & 0xF, 16));
			buf.append(Character.forDigit((data[i] & 0xF), 16));
		}

		return buf.toString();
	}

	/**
	 * converts Color to hex String with RGB values
	 * 
	 * @return
	 */

	final public static String toHexString(char c) {
		int i = c + 0;

		StringBuilder hexSB = new StringBuilder(8);
		hexSB.append("\\u");
		hexSB.append(hexChar[(i & 0xf000) >>> 12]);
		hexSB.append(hexChar[(i & 0x0f00) >> 8]); // look up low nibble char
		hexSB.append(hexChar[(i & 0xf0) >>> 4]);
		hexSB.append(hexChar[i & 0x0f]); // look up low nibble char
		return hexSB.toString();
	}

	final public static String toHexString(int i) {

		StringBuilder hexSB = new StringBuilder(16);
		hexSB.append(hexChar[(i & 0xf0000000) >>> 28]);
		hexSB.append(hexChar[(i & 0xf000000) >>> 24]);
		hexSB.append(hexChar[(i & 0xf00000) >>> 20]);
		hexSB.append(hexChar[(i & 0xf0000) >>> 16]);
		hexSB.append(hexChar[(i & 0xf000) >>> 12]);
		hexSB.append(hexChar[(i & 0x0f00) >> 8]);
		hexSB.append(hexChar[(i & 0xf0) >>> 4]);
		hexSB.append(hexChar[i & 0x0f]);
		return hexSB.toString();
	}

	final public static String toHexString(GColor col) {
		byte r = (byte) col.getRed();
		byte g = (byte) col.getGreen();
		byte b = (byte) col.getBlue();

		StringBuilder hexSB = new StringBuilder(8);
		// RED
		hexSB.append(hexChar[(r & 0xf0) >>> 4]);
		// look up high nibble char
		hexSB.append(hexChar[r & 0x0f]); // look up low nibble char
		// GREEN
		hexSB.append(hexChar[(g & 0xf0) >>> 4]);
		// look up high nibble char
		hexSB.append(hexChar[g & 0x0f]); // look up low nibble char
		// BLUE
		hexSB.append(hexChar[(b & 0xf0) >>> 4]);
		// look up high nibble char
		hexSB.append(hexChar[b & 0x0f]); // look up low nibble char
		return hexSB.toString();
	}

	final public static String toHexString(String s) {
		StringBuilder sb = new StringBuilder(s.length() * 6);
		for (int i = 0; i < s.length(); i++) {
			sb.append(toHexString(s.charAt(i)));
		}

		return sb.toString();
	}

	// table to convert a nibble to a hex char.
	private static char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String toHTMLString(String title) {
		return toHTMLString(title, true);
	}

	/**
	 * Converts the given unicode string to an html string where special
	 * characters are converted to <code>&#xxx;</code> sequences (xxx is the
	 * unicode value of the character)
	 * 
	 * @author Markus Hohenwarter
	 */
	final public static String toHTMLString(String str, boolean encodeLTGT) {
		if (str == null)
			return null;

		StringBuilder sb = new StringBuilder();

		// convert every single character and append it to sb
		int len = str.length();
		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);
			int code = c;

			// standard characters have code 32 to 126
			if ((code >= 32 && code <= 126)) {

				if (!encodeLTGT) {
					sb.append(c);
				} else {
					switch (code) {
					case 60:
						sb.append("&lt;");
						break; // <
					case 62:
						sb.append("&gt;");
						break; // >

					default:
						// do not convert
						sb.append(c);
					}
				}
			}
			// special characters
			else {
				switch (code) {
				case 10:
				case 13: // replace LF or CR with <br/>
					sb.append("<br/>\n");
					break;

				case 9: // replace TAB with space
					sb.append("&nbsp;"); // space
					break;

				default:
					// convert special character to escaped HTML
					sb.append("&#");
					sb.append(code);
					sb.append(';');
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Converts the given unicode string to a string where special characters
	 * are converted to <code>&#encoding;</code> sequences . The resulting
	 * string can be used in XML files.
	 */
	public static String encodeXML(String str) {

		StringBuilder sb = new StringBuilder(str.length());

		encodeXML(sb, str);

		return sb.toString();
	}

	/**
	 * Converts the given unicode string to a string where special characters
	 * are converted to <code>&#encoding;</code> sequences . The resulting
	 * string can be used in XML files.
	 */
	public static void encodeXML(StringBuilder sb, String str) {
		if (str == null)
			return;

		// convert every single character and append it to sb
		int len = str.length();
		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);

			if (c <= '\u001f') {
				// #2399 all apart from U+0009, U+000A, U+000D are invalid in
				// XML
				// none should appear anyway, but encode to be safe

				// eg &#10;
				sb.append("&#");
				sb.append(((int) c) + "");
				sb.append(';');

				if (c != '\n' && c != 13) {
					Log.warn("Control character being written to XML: "
							+ ((int) c));
				}

			} else {

				switch (c) {
				case '>':
					sb.append("&gt;");
					break;
				case '<':
					sb.append("&lt;");
					break;
				case '"':
					sb.append("&quot;");
					break;
				case '\'':
					sb.append("&apos;");
					break;
				case '&':
					sb.append("&amp;");
					break;

				default:
					sb.append(c);
				}
			}
		}
	}

	/**
	 * Default implementation does not work, overriden in desktop TODO make sure
	 * we override this in Web as well
	 * 
	 * @param c
	 *            character
	 * @return whether it's left to right Unicode character
	 */
	protected boolean isRightToLeftChar(char c) {

		return false;
	}

	public static StringUtil prototype;

	/**
	 * Replaces special unicode letters (e.g. greek letters) in str by LaTeX
	 * strings.
	 */
	public static synchronized String toLaTeXString(String str,
			boolean convertGreekLetters) {
		int length = str.length();
		sbReplaceExp.setLength(0);

		char c = 0;
		char previousChar;

		for (int i = 0; i < length; i++) {
			previousChar = c;
			c = str.charAt(i);

			// Guy Hed 30.8.2009
			// Fix Hebrew 'undefined' problem in Latex text.
			if (prototype.isRightToLeftChar(c)) {
				int j = i;
				while (j < length
						&& (prototype.isRightToLeftChar(str.charAt(j)) || str
								.charAt(j) == '\u00a0'))
					j++;
				for (int k = j - 1; k >= i; k--)
					sbReplaceExp.append(str.charAt(k));
				sbReplaceExp.append(' ');
				i = j - 1;
				continue;
			}
			// Guy Hed 30.8.2009

			switch (c) {
			/*
			 * case '(': sbReplaceExp.append("\\left("); break;
			 * 
			 * case ')': sbReplaceExp.append("\\right)"); break;
			 */

			case '%': // % -> \%
				if (!(previousChar == '\\')) {
					sbReplaceExp.append("\\");
				}
				sbReplaceExp.append("%");
				break;

			/*
			 * not needed for MathQuillGGB / JLaTeXMath and in fact it doesn't
			 * work inside \text{} // Exponents // added by Loïc Le Coq
			 * 2009/11/04 case '\u2070': // ^0 sbReplaceExp.append("^0"); break;
			 * 
			 * case '\u00b9': // ^1 sbReplaceExp.append("^1"); break; // end
			 * Loïc case '\u00b2': // ^2 sbReplaceExp.append("^2"); break;
			 * 
			 * case '\u00b3': // ^3 sbReplaceExp.append("^3"); break;
			 * 
			 * case '\u2074': // ^4 sbReplaceExp.append("^4"); break;
			 * 
			 * case '\u2075': // ^5 sbReplaceExp.append("^5"); break;
			 * 
			 * case '\u2076': // ^6 sbReplaceExp.append("^6"); break; // added
			 * by Loïc Le Coq 2009/11/04 case '\u2077': // ^7
			 * sbReplaceExp.append("^7"); break;
			 * 
			 * case '\u2078': // ^8 sbReplaceExp.append("^8"); break;
			 * 
			 * case '\u2079': // ^9 sbReplaceExp.append("^9"); break; // end
			 * Loïc Le Coq
			 */

			default:
				if (!convertGreekLetters) {
					sbReplaceExp.append(c);
				} else {
					switch (c) {
					// greek letters
					case Unicode.alpha:
						sbReplaceExp.append("\\alpha");
						break;

					case Unicode.beta:
						sbReplaceExp.append("\\beta");
						break;

					case Unicode.gamma:
						sbReplaceExp.append("\\gamma");
						break;

					case Unicode.delta:
						sbReplaceExp.append("\\delta");
						break;

					case Unicode.epsilon:
						sbReplaceExp.append("\\varepsilon");
						break;

					case Unicode.zeta:
						sbReplaceExp.append("\\zeta");
						break;

					case Unicode.eta:
						sbReplaceExp.append("\\eta");
						break;

					case Unicode.theta:
						sbReplaceExp.append("\\theta");
						break;

					case Unicode.iota:
						sbReplaceExp.append("\\iota");
						break;

					case Unicode.kappa:
						sbReplaceExp.append("\\kappa");
						break;

					case Unicode.lambda:
						sbReplaceExp.append("\\lambda");
						break;

					case Unicode.mu:
						sbReplaceExp.append("\\mu");
						break;

					case Unicode.nu:
						sbReplaceExp.append("\\nu");
						break;

					case Unicode.xi:
						sbReplaceExp.append("\\xi");
						break;

					case Unicode.omicron:
						sbReplaceExp.append("\\omicron");
						break;

					case Unicode.pi:
						sbReplaceExp.append("\\pi");
						break;

					case Unicode.rho:
						sbReplaceExp.append("\\rho");
						break;

					case Unicode.sigma:
						sbReplaceExp.append("\\sigma");
						break;

					case Unicode.tau:
						sbReplaceExp.append("\\tau");
						break;

					case Unicode.upsilon:
						sbReplaceExp.append("\\upsilon");
						break;

					case Unicode.phi_symbol:
						sbReplaceExp.append("\\phi");
						break;

					case Unicode.phi:
						sbReplaceExp.append("\\varphi");
						break;

					case Unicode.chi:
						sbReplaceExp.append("\\chi");
						break;

					case Unicode.psi:
						sbReplaceExp.append("\\psi");
						break;

					case Unicode.omega:
						sbReplaceExp.append("\\omega");
						break;

					// GREEK upper case letters

					case Unicode.Alpha:
						sbReplaceExp.append("\\Alpha");
						break;

					case Unicode.Beta:
						sbReplaceExp.append("\\Beta");
						break;

					case Unicode.Gamma:
						sbReplaceExp.append("\\Gamma");
						break;

					case Unicode.Delta:
						sbReplaceExp.append("\\Delta");
						break;

					case Unicode.Epsilon:
						sbReplaceExp.append("\\Epsilon");
						break;

					case Unicode.Zeta:
						sbReplaceExp.append("\\Zeta");
						break;

					case Unicode.Eta:
						sbReplaceExp.append("\\Eta");
						break;

					case Unicode.Theta:
						sbReplaceExp.append("\\Theta");
						break;

					case Unicode.Iota:
						sbReplaceExp.append("\\Iota");
						break;

					case Unicode.Kappa:
						sbReplaceExp.append("\\Kappa");
						break;

					case Unicode.Lambda:
						sbReplaceExp.append("\\Lambda");
						break;

					case Unicode.Mu:
						sbReplaceExp.append("\\Mu");
						break;

					case Unicode.Nu:
						sbReplaceExp.append("\\Nu");
						break;

					case Unicode.Xi:
						sbReplaceExp.append("\\Xi");
						break;

					case Unicode.Omicron:
						sbReplaceExp.append("\\Omicron");
						break;

					case Unicode.Pi:
						sbReplaceExp.append("\\Pi");
						break;

					case Unicode.Rho:
						sbReplaceExp.append("\\Rho");
						break;

					case Unicode.Sigma:
						sbReplaceExp.append("\\Sigma");
						break;

					case Unicode.Tau:
						sbReplaceExp.append("\\Tau");
						break;

					case Unicode.Upsilon:
						sbReplaceExp.append("\\Upsilon");
						break;

					case Unicode.Phi:
						sbReplaceExp.append("\\Phi");
						break;

					case Unicode.Chi:
						sbReplaceExp.append("\\Chi");
						break;

					case Unicode.Psi:
						sbReplaceExp.append("\\Psi");
						break;

					case Unicode.Omega:
						sbReplaceExp.append("\\Omega");
						break;
					default:
						sbReplaceExp.append(c);
					}

				}
			}
		}
		return sbReplaceExp.toString();
	}

	private static StringBuilder sb;

	/*
	 * returns a string with n instances of s eg string("hello",2) ->
	 * "hellohello";
	 */
	public static String string(String s, int n) {

		if (n == 1)
			return s; // most common, check first
		if (n < 1)
			return "";

		if (sb == null)
			sb = new StringBuilder();

		sb.setLength(0);

		for (int i = 0; i < n; i++) {
			sb.append(s);
		}

		return sb.toString();
	}

	public static String removeSpaces(String str) {

		if (str == null || str.length() == 0)
			return "";

		if (sb == null)
			sb = new StringBuilder();

		sb.setLength(0);
		char c;

		for (int i = 0; i < str.length(); i++) {
			c = str.charAt(i);
			if (c != ' ')
				sb.append(c);
		}

		return sb.toString();

	}

	/**
	 * Removes spaces from the start and end Not the same as trim - it removes
	 * ASCII control chars eg tab Michael Borcherds 2007-11-23
	 * 
	 * @param str
	 */
	public static String trimSpaces(String str) {

		int len = str.length();

		if (len == 0)
			return "";

		int start = 0;
		while (str.charAt(start) == ' ' && start < len - 1)
			start++;

		int end = len;
		while (str.charAt(end - 1) == ' ' && end > start)
			end--;

		if (start == end)
			return "";

		return str.substring(start, end);

	}

	private static StringBuilder sbReplaceExp = new StringBuilder(200);

	public static StringBuilder resetStringBuilder(StringBuilder high) {
		if (high == null)
			return new StringBuilder();
		high.setLength(0);
		return high;
	}

	public static boolean isNumber(String text) {
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (!isDigit(c) && c != '.' && c != Unicode.ArabicComma && c != '-')
				return false;
		}

		return true;
	}

	/**
	 * Safe implementation of toLowerCase
	 * 
	 * @param s
	 *            input string
	 * @return the <code>String</code>, converted to lowercase.
	 * @see #toLowerCase(String)
	 */
	protected String toLower(String s) {
		return s.toLowerCase();
	}

	/**
	 * Safe implementation of toLowerCase
	 * 
	 * @param s
	 *            input string
	 * @return the <code>String</code>, converted to lowercase.
	 * @see #toLowerCase(String)
	 */
	protected String toUpper(String s) {
		return s.toUpperCase();
	}

	/**
	 * important to use this rather than String.toLowerCase() as this is
	 * overridden in desktop.Application so that it uses
	 * String.toLowerCase(Locale.US) so that the behavior is well defined
	 * whatever language we are running in NB does cause problems eg in Turkish
	 * 
	 * @param s
	 *            input string
	 * @return the <code>String</code>, converted to lowercase.
	 * @see java.lang.String#toUpperCase(Locale)
	 */
	public static String toLowerCase(String s) {
		return prototype.toLower(s);
	}

	/**
	 * important to use this rather than String.toLowerCase() as this is
	 * overridden in desktop.Application so that it uses
	 * String.toLowerCase(Locale.US) so that the behavior is well defined
	 * whatever language we are running in NB does cause problems eg in Turkish
	 * 
	 * @param s
	 *            input string
	 * @return the <code>String</code>, converted to lowercase.
	 * @see java.lang.String#toUpperCase(Locale)
	 */
	public static String toUpperCase(String s) {
		return prototype.toUpper(s);
	}

	public static double parseDouble(String s) {

		if ("NaN".equals(s) || "undefined".equals(s) || "null".equals(s))
			return Double.NaN;
		else if ("Infinity".equals(s))
			return Double.POSITIVE_INFINITY;
		else if ("-Infinity".equals(s))
			return Double.NEGATIVE_INFINITY;

		return Double.parseDouble(s);
	}

	public static String repeat(char c, int count) {
		StringBuilder ret = new StringBuilder();
		for (int i = 0; i < count; i++)
			ret.append(c);
		return ret.toString();
	}

	/**
	 * Character.isLetterOrDigit() doesn't work in GWT, see
	 * http://code.google.com/p/google-web-toolkit/issues/detail?id=1983
	 */
	public static boolean isLetterOrDigit(char c) {
		if (isDigit(c)) {
			return true;
		}

		return isLetter(c);
	}

	public static boolean isLetterOrDigitOrUnderscore(final char character) {
		switch (character) {
		case '_': // allow underscore as a valid letter in an autocompletion
					// word
			return true;

		default:
			return isLetterOrDigit(character);
		}
	}

	/**
	 * Character.isDigit() doesn't work in GWT, see
	 * http://code.google.com/p/google-web-toolkit/issues/detail?id=1983
	 * 
	 * see also MyDouble.parseDouble()
	 */
	public static boolean isDigit(char ch) {

		// TODO: Maybe this could be more efficient
		// check roman first (most common)
		if ((ch >= '\u0030' && ch <= '\u0039')
				|| (ch >= '\u0660' && ch <= '\u0669')
				|| (ch >= '\u06f0' && ch <= '\u06f9')
				|| (ch >= '\u0966' && ch <= '\u096f')
				|| (ch >= '\u09e6' && ch <= '\u09ef')
				|| (ch >= '\u0a66' && ch <= '\u0a6f')
				|| (ch >= '\u0ae6' && ch <= '\u0aef')
				|| (ch >= '\u0b66' && ch <= '\u0b6f')
				|| (ch >= '\u0be6' && ch <= '\u0bef') // Java (5?) bug: \u0BE6
														// not recognized by
														// Character.isDigit()
				|| (ch >= '\u0c66' && ch <= '\u0c6f')
				|| (ch >= '\u0ce6' && ch <= '\u0cef')
				|| (ch >= '\u0d66' && ch <= '\u0d6f')
				|| (ch >= '\u0e50' && ch <= '\u0e59')
				|| (ch >= '\u0ed0' && ch <= '\u0ed9')
				|| (ch >= '\u0f20' && ch <= '\u0f29')
				|| (ch >= '\u1040' && ch <= '\u1049')
				|| (ch >= '\u17e0' && ch <= '\u17e9')
				|| (ch >= '\u1810' && ch <= '\u1819')
				|| (ch >= '\u1b50' && ch <= '\u1b59') // not recognized by
														// Java's version of
														// Character.isDigit() !
				|| (ch >= '\u1bb0' && ch <= '\u1bb9') // not recognized by
														// Java's version of
														// Character.isDigit() !
				|| (ch >= '\u1c40' && ch <= '\u1c49') // not recognized by
														// Java's version of
														// Character.isDigit() !
				|| (ch >= '\u1c50' && ch <= '\u1c59') // not recognized by
														// Java's version of
														// Character.isDigit() !
				|| (ch >= '\ua8d0' && ch <= '\ua8d9') // not recognized by
														// Java's version of
														// Character.isDigit() !
				// following not handled by GeoGebra's parser
				// || (ch >= 0x1369 && ch <= 0x1371) // Ethiopic
				// || (ch >= 0x1946 && ch <= 0x194F) // Limbu
				// || (ch >= 0xFF10 && ch <= 0xFF19) //"FULL WIDTH" digits
		) {
			return true;
		}

		return false;
	}

	// public static void main(String [] args) {
	// System.out.println("starting test");
	//
	//
	// for (int cc = 0; cc < 65536; ++cc) {
	// char c = (char)cc;
	// if (Character.isLetter(c) != isLetter(c)) {
	// System.out.println("isLetter failed "+c +
	// " "+toHexString(c)+Character.isLetter(c)+" "+isLetter(c));
	// }
	// }
	// for (int cc = 0; cc < 65536; ++cc) {
	// char c = (char)cc;
	// if (Character.isDigit(c) != isDigit(c)) {
	// System.out.println("isDigit failed "+c +
	// " "+toHexString(c)+Character.isDigit(c)+" "+isDigit(c));
	// }
	// }
	// for (int cc = 0; cc < 65536; ++cc) {
	// char c = (char)cc;
	// if (Character.isWhitespace(c) != isWhitespace(c)) {
	// System.out.println("isWhitespace failed "+c +
	// " "+toHexString(c)+Character.isWhitespace(c)+" "+isWhitespace(c));
	// }
	// }
	//
	// boolean start = true;
	//
	// for (int cc = 0; cc < 65536-1; ++cc) {
	// char c = (char)cc;
	// char c2 = (char)(cc+1);
	// if (Character.isLetter(c) != Character.isLetter(c2)) {
	// if (start) {
	// System.out.print(toHexString(c2));
	// } else {
	// System.out.println(" "+toHexString(c));
	// }
	// start = !start;
	// }
	// }
	//
	// System.out.println("ending test");
	//
	// }

	/**
	 * Character.isLetter() doesn't work in GWT, see
	 * http://code.google.com/p/google-web-toolkit/issues/detail?id=1983
	 */
	public static boolean isLetter(char c) {
		// From Parser.jj, compatibility with internationalized Unicode
		// characters
		// TODO: Maybe this could be more efficient
		if ((c >= '\u0041' && c <= '\u005a') || // upper case (A-Z)
				(c >= '\u0061' && c <= '\u007a') || // lower case (a-z)
				(c == '\u00b7') || // middle dot (for Catalan)
				(c >= '\u00c0' && c <= '\u00d6') || // accentuated letters
				(c >= '\u00d8' && c <= '\u00f6') || // accentuated letters
				(c >= '\u00f8' && c <= '\u01bf') || // accentuated letters
				(c >= '\u01c4' && c <= '\u02a8') || // accentuated letters
				(c >= '\u0391' && c <= '\u03f3') || // Greek
				(c >= '\u0401' && c <= '\u0481') || // Cyrillic
				(c >= '\u0490' && c <= '\u04f9') || // Cyrillic
				(c >= '\u0531' && c <= '\u1ffc') || // a lot of signs (Arabic,
													// accentuated, ...)
				(c >= '\u3041' && c <= '\u3357') || // Asian letters
				(c >= '\u4e00' && c <= '\ud7a3') || // Asian letters
				(c >= '\uf71d' && c <= '\ufa2d') || // Asian letters
				(c >= '\ufb13' && c <= '\ufdfb') || // Armenian, Hebrew, Arabic
				(c >= '\ufe80' && c <= '\ufefc') || // Arabic
				(c >= '\uff66' && c <= '\uff9d') || // Katakana
				(c >= '\uffa1' && c <= '\uffdc') // Hangul
		) {
			return true;
		}
		return false;
	}

	/**
	 * @param str
	 *            String
	 * @return true if str matches one of "!=", "<>", Unicode.NOTEQUAL
	 */
	public static boolean isNotEqual(String str) {
		return "!=".equals(str) || "<>".equals(str)
				|| Unicode.NOTEQUAL.equals(str);
	}

	/**
	 * @param str
	 *            String
	 * @return true if str matches one of "<", ">", "!=", "<>", Unicode.NOTEQUAL
	 */
	public static boolean isInequality(String str) {
		return "<".equals(str) || ">".equals(str) || isNotEqual(str);
	}

	/**
	 * Since a_{{{{{{{5}=2 is correct expression, we replace the index by Xs to
	 * obtain a_{XXXXXXX}=2
	 * 
	 * @param text
	 *            text
	 * @return text with replaced {s
	 */
	public static String ignoreIndices(String text) {
		if (text == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder(80);
		boolean ignore = false;
		boolean underscore = false;
		boolean comment = false;
		for (int i = 0; i < text.length(); i++) {

			char ch = text.charAt(i);

			if (comment && ch != '"') {
				sb.append(ch);
				continue;
			}

			if (ch == '"' && !underscore) {
				sb.append(ch);
				comment = !comment;
				continue;
			}

			if (ignore && ch == '}') {
				ignore = false;
			}

			if (!ignore) {
				sb.append(ch);
			} else {
				sb.append('X');
			}

			if (underscore && ch == '{') {
				ignore = true;
			} else if (!ignore) {
				underscore = ch == '_';
			}

		}
		return sb.toString();
	}

	public static int checkBracketsBackward(String parseString) {
		int curly = 0;
		int square = 0;
		int round = 0;
		Stack<Integer> closingBrackets = new Stack<Integer>();
		boolean comment = false;
		for (int i = parseString.length() - 1; i >= 0; i--) {
			char ch = parseString.charAt(i);

			if (comment && ch != '"') {
				continue;
			}

			switch (ch) {
			case '"':
				comment = !comment;
				break;
			case '}':
				closingBrackets.add(i);
				curly++;
				break;
			case '{':
				curly--;
				if (curly < 0) {
					return i;
				}
				closingBrackets.pop();
				break;
			case ']':
				square++;
				closingBrackets.add(i);
				break;
			case '[':
				square--;
				if (square < 0) {
					return i;
				}
				closingBrackets.pop();
				break;
			case ')':
				round++;
				closingBrackets.add(i);
				break;
			case '(':
				round--;
				if (round < 0) {
					return i;
				}
				closingBrackets.pop();
				break;
			}
		}
		if (!closingBrackets.isEmpty()) {
			return closingBrackets.pop();
		}
		return -1;
	}

	public static String fixVerticalBars(String parseString) {
		String ignoredIndices = ignoreIndices(parseString);
		StringBuilder sb = new StringBuilder();

		// When we have <splitter> || , we know that we should separate
		// these bars (i. e., we want absolute value, not OR)
		Set<Character> splitters = new TreeSet<Character>(
				Arrays.asList(new Character[] { Unicode.SQUARE_ROOT, '+', '-',
						'*', '/', '^',
						'=' }));

		// first we iterate from left to right, and then backward
		for (int dir = 0; dir < 2; dir++) {
			boolean comment = false;
			int bars = 0;
			Character lastNonWhitespace = ' ';

			if (dir == 1) {
				parseString = sb.reverse().toString();
				ignoredIndices = ignoreIndices(parseString);
				sb = new StringBuilder();
				splitters = new TreeSet<Character>(
						Arrays.asList(new Character[] { '*', '/', '^', '=',
								Unicode.Superscript_0, Unicode.Superscript_1,
								Unicode.Superscript_2, Unicode.Superscript_3,
								Unicode.Superscript_4, Unicode.Superscript_5,
								Unicode.Superscript_6, Unicode.Superscript_7,
								Unicode.Superscript_8, Unicode.Superscript_9,
								Unicode.Superscript_Minus }));
			}

			int len = ignoredIndices.length();
			for (int i = 0; i < len; i++) {
				Character ch = ignoredIndices.charAt(i);
				sb.append(parseString.charAt(i));

				if (StringUtil.isWhitespace(ch) || (comment && !ch.equals('"'))) {
					continue;
				}

				if (ch.equals('"')) {
					comment = !comment;
				}

				if (ch.equals('|')) {
					// We separate bars if the previous symbol was in splitters
					// or we have ||| and there were an odd number of bars so far
					if (i == 0
							|| (bars % 2 == 1 && i < len - 2
									&& ignoredIndices.charAt(i + 1) == '|' && ignoredIndices
									.charAt(i + 2) == '|')
							|| (i < len - 1
									&& ignoredIndices.charAt(i + 1) == '|' && splitters
										.contains(lastNonWhitespace))) {
						sb.append(' ');
					}
					bars++;
				}

				lastNonWhitespace = ch;
			}
		}

		return sb.reverse().toString();
	}

	/**
	 * Checks whether the text may represent two expressions separated by comma.
	 * Simple check for comma is not possible as (1,1)+{1,1} is a simple
	 * expression.
	 * 
	 * @param evalText
	 *            text to be analyzed
	 * @return true if the text is of the form expression,expression
	 */
	public static boolean representsMultipleExpressions(String evalText) {
		String text = ignoreIndices(evalText);
		int brackets = 0;
		boolean comment = false;
		for (int i = text.length() - 1; i >= 0; i--) {
			char ch = text.charAt(i);
			if (comment && ch != '"')
				continue;
			switch (ch) {
			case '}':
			case ')':
			case ']':
				brackets--;
				break;
			case '{':
			case '(':
			case '[':
				brackets++;
				break;
			case ',':
				if (brackets == 0)
					return true;
			}
		}
		return false;
	}

	/**
	 * @param label
	 *            label, may contain bold, italic, indices
	 * @return ratio of estimated string length and font size
	 */
	public static double estimateLengthHTML(String label, GFont font) {
		String str = label;
		boolean bold = false;
		if (str.startsWith("<i>") && str.endsWith("</i>")) {
			str = str.substring(3, label.length() - 4);
		}
		if (str.startsWith("<b>") && str.endsWith("</b>")) {
			str = str.substring(3, str.length() - 4);
			bold = true;
		}
		if (str.startsWith("<i>") && str.endsWith("</i>")) {
			str = str.substring(3, str.length() - 4);
		}
		return estimateLength(label, bold ? font.deriveFont(GFont.BOLD) : font);
	}

	public static double estimateLength(String label, GFont font) {
		String str = label;
		boolean bold = font.isBold();
		double visibleChars = 0;
		boolean index = false;
		double indexSize = 0.7;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '_') {
				if (i < str.length() - 1 && str.charAt(i + 1) == '{') {
					i++;
					index = true;
				} else {
					visibleChars -= (1 - indexSize); // penalty for 1char index
				}
			} else if (str.charAt(i) == '}') {
				index = false;
			} else {
				visibleChars += index ? indexSize : 1;
			}
		}
		return bold ? visibleChars * 0.6 * font.getSize() : visibleChars * 0.5
				* font.getSize();
	}

	public static double estimateHeight(String string, GFont font) {
		if (font == null) {
			return 0;
		}
		return string.indexOf('_') > -1 ? font.getSize() * 1.8
				: font.getSize() * 1.4;
	}

	public static Object format(String sub, double x0, double x1, double x2,
			double x3) {
		return sub.replaceAll("%0", x0 + "").replaceAll("%1", x1 + "")
				.replaceAll("%2", x2 + "").replace("%3", x3 + "");
	}

	/**
	 * @param s
	 *            String to wrap
	 * @return "exact(" + s + ")" if necessary (for Giac)
	 */
	public static String wrapInExact(String s) {
		if (s.startsWith("exact(")) {
			// nothing to do
			return s;
		}

		// if we have eg 6.048554268711413E7
		// convert to exact(6.048554268711413e7)
		if (s.indexOf("E") > -1) {
			s = s.replace("E", "e");
		}

		if ("?".equals(s) || "undef".equals(s)) {
			return "undef";
		}

		if ("inf".equals(s)) {
			return s;
		}

		if ("-inf".equals(s)) {
			return s;
		}

		StringBuilder sb1 = new StringBuilder();
		sb1.append("exact(");
		sb1.append(s);
		sb1.append(')');

		return sb1.toString();
	}

	/**
	 * @param filename
	 *            input filename
	 * @return filename without leading slash
	 */
	public static String removeLeadingSlash(String filename) {
		if (filename != null && filename.length() != 0
				&& filename.charAt(0) == '/')
			return filename.substring(1);
		return filename;
	}

	public static String toHtmlColor(GColor color) {
		return "#" + toHexString(color);

	}

	/**
	 * Tokenize a string so that every even indices (e.g. 0) of the returned
	 * array should contain a String not containing any letters (or digits), and
	 * every odd incides (e.g. 1) of it should contain a String having only
	 * letters (or digits).
	 * 
	 * @param input
	 *            the input String
	 * @return the tokenized String
	 */
	public static ArrayList<String> wholeWordTokenize(String input) {
		// ArrayList is easier for now as we don't know
		// the length of the returned String yet
		ArrayList<String> ret = new ArrayList<String>();
		Character actChar;
		String actWord = "";

		// 1st, 2nd, 3rd, 4th, ...
		// 0, 1, 2, 3, ...

		// parity of the number of elements already in the ret Array
		// or in other words, whether we are going to add a word
		// in the next step
		boolean odd = false;

		for (int i = 0; i < input.length(); i++) {
			actChar = input.charAt(i);
			// although the syntax might not allow whole words
			// starting with digits, we're going to allow them
			// here, as it is easier and will not change the outcome
			if (isLetterOrDigitOrUnderscore(actChar)) {
				if (odd) {
					actWord += actChar;
				} else {
					ret.add(actWord);
					actWord = "" + actChar;
					odd = true;
				}
			} else {
				if (odd) {
					ret.add(actWord);
					actWord = "" + actChar;
					odd = false;
				} else {
					actWord += actChar;
				}
			}
		}
		ret.add(actWord);

		// the last one should always be a non-word, like the first one
		// but odd should have changed sign in the previous command
		if (odd) {
			ret.add("");
		}
		return ret;
	}

	/**
	 * Join tokens which are in a similar format as StringUtil.wholeWordTokenize
	 * produces... delimiter can be null, or can be a glue string
	 * 
	 * @param tokens
	 *            the input
	 * @param delimiter
	 *            the glue string (optional)
	 * @return the joined String
	 */
	public static String joinTokens(Iterable<String> tokens, String delimiter) {
		String ret = "";
		Iterator<String> it = tokens.iterator();
		if (it.hasNext()) {
			ret += it.next();
		}
		while (it.hasNext()) {
			if (delimiter != null) {
				ret += delimiter;
			}
			ret += it.next();
		}
		return ret;
	}

	public static String cannonicNumber(String str) {
		boolean zerosNeedRemoving = true;
		int index = str.indexOf(".");
		if (index >= 0) {
			for (int k = index + 1; k < str.length(); k++) {
				if (str.charAt(k) != '0') {
					zerosNeedRemoving = false;
					break;
				}
			}
		} else {
			zerosNeedRemoving = false;
		}
		if (zerosNeedRemoving) {
			return index == 0 ? "0" : str.substring(0, index);
		}
		// Reduce can't handle .5*8
		return index == 0 ? "0" + str : str;

	}

	// App.debug(StringUtil.cannonicNumber2("4.3E20"));
	// App.debug(StringUtil.cannonicNumber2("1.203"));
	// App.debug(StringUtil.cannonicNumber2("1.23000000"));
	// App.debug(StringUtil.cannonicNumber2("1000"));
	// App.debug(StringUtil.cannonicNumber2("1.20000000E20"));
	// App.debug(StringUtil.cannonicNumber2("-4.3E20"));
	// App.debug(StringUtil.cannonicNumber2("-1.203"));
	// App.debug(StringUtil.cannonicNumber2("-1.23000000"));
	// App.debug(StringUtil.cannonicNumber2("-1000"));
	// App.debug(StringUtil.cannonicNumber2("-1.20000000E20"));
	// App.debug(StringUtil.cannonicNumber2(".23000000000"));

	/*
	 * convert 1.200000000 into 1.2 convert .23 into 0.23 convert 1.23000E20
	 * into 1.23E20
	 */
	public static String cannonicNumber2(String str) {

		String num = str;
		String exponent = "";

		if (str.indexOf('E') > 0) {
			String[] split = num.split("E");
			exponent = "E" + split[1];
			num = split[0];
		}

		// .23 to 0.23
		if (num.startsWith(".")) {
			num = "0" + num;
		}

		// remove trailing zeros if there's a decimal point
		if (num.indexOf(".") > 0) {
			while (num.endsWith("0")) {
				num = num.substring(0, num.length() - 1);
			}
		}

		return num + exponent;

	}

	/**
	 * @param c
	 * @return emulation of Character.isWhiteSpace
	 */
	public static boolean isWhitespace(char c) {
		return c == ' ' || c == '\u0009' || /* , HORIZONTAL TABULATION. */
		c == '\n' || /* LINE FEED. */
		c == '\u000B' || /* VERTICAL TABULATION. */
		c == '\u000C' || /* FORM FEED. */
		c == '\r' || /* CARRIAGE RETURN. */
		c == '\u001C' || /* FILE SEPARATOR. */
		c == '\u001D' || /* GROUP SEPARATOR. */
		c == '\u001E' || /* RECORD SEPARATOR. */
		c == '\u001F' || /* UNIT SEPARATOR. */
		c == '\u1680' || c == '\u180E' || c == '\u2000' || c == '\u2001'
				|| c == '\u2002' || c == '\u2003' || c == '\u2004'
				|| c == '\u2005' || c == '\u2006' || c == '\u2008'
				|| c == '\u2009' || c == '\u200A' || c == '\u2028'
				|| c == '\u2029' || c == '\u205F' || c == '\u3000';
	}

	/**
	 * Used in DynamicTextProcessor and DynamicTextInputPane, and later also to
	 * support the output of MathQuillGGB
	 * 
	 * @param sb
	 *            output
	 * @param content
	 *            input
	 * @param currentQuote
	 *            alternate between open and closed
	 */
	public static char processQuotes(StringBuilder sb, String content, char ret) {
		char currentQuote = ret;
		if (content.indexOf("\"") == -1) {
			sb.append(content);
			return currentQuote;
		}

		for (int i = 0; i < content.length(); i++) {
			char c = content.charAt(i);
			if (c == '\"') {
				sb.append(currentQuote);

				// flip open <-> closed
				if (currentQuote == Unicode.OPEN_DOUBLE_QUOTE) {
					currentQuote = Unicode.CLOSE_DOUBLE_QUOTE;
				} else {
					currentQuote = Unicode.OPEN_DOUBLE_QUOTE;
				}
			} else {
				// RadioButtonTreeItem uses strings with more than one
				// character for the first time, so this part of code
				// only applies to it (yet)
				sb.append(c);
			}
		}
		return currentQuote;
	}
}
