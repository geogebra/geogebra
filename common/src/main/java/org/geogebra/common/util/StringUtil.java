package org.geogebra.common.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Stack;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.cas.giac.CASgiac;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.parser.ParserInfo;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.util.Greek;
import com.himamis.retex.editor.share.util.Unicode;

public class StringUtil extends com.himamis.retex.editor.share.input.Character {

	final static public String mp3Marker = "data:audio/mp3;base64,";
	final static public String pngMarker = "data:image/png;base64,";
	final static public String svgMarker = "data:image/svg+xml;base64,";
	final static public String pdfMarker = "data:application/pdf;base64,";
	final static public String txtMarker = "data:text/plain;charset=utf-8,";
	// Shows as "unknown" in mobile Safari and gives options to copy/share
	final static public String txtMarkerForSafari = "data:application/octet-stream,";
	final static public String jpgMarker = "data:image/jpg;base64,";
	final static public String ggbMarker = "data:application/vnd.geogebra.file;base64,";
	final static public String gifMarker = "data:image/gif;base64,";
	final static public String webmMarker = "data:video/webm;base64,";
	final static public String htmlMarker = "data:text/html;charset=utf-8,";

	// table to convert a nibble to a hex char.
	private static char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	private static StringUtil prototype;

	private static final Object lock = new Object();

	private static StringBuilder sbReplaceExp = new StringBuilder(200);

	/**
	 * @param data
	 *            to convert
	 * @return data as a hex String
	 */
	public static String convertToHex(int data) {
		StringBuilder buf = new StringBuilder();

		buf.append(Character.forDigit((data >> 4) & 0xF, 16));
		buf.append(Character.forDigit((data >> 0) & 0xF, 16));
		buf.append(Character.forDigit((data >> 12) & 0xF, 16));
		buf.append(Character.forDigit((data >> 8) & 0xF, 16));
		buf.append(Character.forDigit((data >> 20) & 0xF, 16));
		buf.append(Character.forDigit((data >> 16) & 0xF, 16));
		buf.append(Character.forDigit((data >> 28) & 0xF, 16));
		buf.append(Character.forDigit((data >> 24) & 0xF, 16));

		return buf.toString();
	}

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
	 * converts unicode to hex String with RGB values
	 * 
	 * @return hex string with \\u prefix
	 * @param c
	 *            unicode char
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

	/**
	 * Convert number into hex.
	 * 
	 * @param i
	 *            number input
	 * @return hex code
	 */
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

	/**
	 * @param col
	 *            color
	 * @return hex string (no prefix)
	 */
	final public static String toHexString(GColor col) {
		byte r = (byte) col.getRed();
		byte g = (byte) col.getGreen();
		byte b = (byte) col.getBlue();

		return toHexString(r, g, b);
	}

	/**
	 * @param r
	 *            red
	 * @param g
	 *            green
	 * @param b
	 *            blue
	 * @return hex string
	 */
	final public static String toHexString(byte r, byte g, byte b) {
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

	/**
	 * @param s
	 *            input text
	 * @return hex string
	 */
	final public static String toHexString(String s) {
		StringBuilder sb = new StringBuilder(s.length() * 6);
		for (int i = 0; i < s.length(); i++) {
			sb.append(toHexString(s.charAt(i)));
		}

		return sb.toString();
	}

	public static String toHTMLString(String title) {
		return toHTMLString(title, true);
	}

	/**
	 * Converts the given unicode string to an html string where special
	 * characters are converted to <code>&#xxx;</code> sequences (xxx is the
	 * unicode value of the character)
	 * 
	 * @author Markus Hohenwarter
	 * @param str
	 *            unicode string
	 * @param encodeLTGT
	 *            whether to encode &lt; &gt;
	 * @return HTML string
	 */
	final public static String toHTMLString(String str, boolean encodeLTGT) {
		if (str == null) {
			return null;
		}

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
					// Firefox is fussy about this one
					case '/':
					sb.append("&#x2F;");
						break;
					case '<':
						sb.append("&lt;");
						break; // <
					case '>':
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
				case '\n':
				case '\r': // replace LF or CR with <br/>
					sb.append("<br/>\n");
					break;

				case '\t': // replace TAB with space
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
	 * 
	 * @param str
	 *            unicode string
	 * @return XML string
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
	 * 
	 * @param sb
	 *            output builder
	 * @param str
	 *            raw string
	 */
	public static void encodeXML(StringBuilder sb, String str) {
		if (str == null) {
			return;
		}

		// convert every single character and append it to sb
		int len = str.length();

		// support for high Unicode characters
		// https://stackoverflow.com/questions/24501020/how-can-i-convert-a-java-string-to-xml-entities-for-versions-of-unicode-beyond-3
		for (int i = 0; i < len; i = str.offsetByCodePoints(i, 1)) {
			int c = str.codePointAt(i);

			if (c <= '\u001f' || c >= 0x10000) {
				// #2399 all apart from U+0009, U+000A, U+000D are invalid in
				// XML
				// none should appear anyway, but encode to be safe

				// eg &#x0A;
				sb.append("&#x");
				sb.append(Integer.toHexString(c));
				sb.append(';');

				if (c <= '\u001f' && c != '\n' && c != '\r') {
					Log.warn("Control character being written to XML: " + c);
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
					sb.append((char) c);
				}
			}
		}
	}

	/**
	 * @param parseString raw parser input
	 * @param decimalComma whether , means decimal (rather than thousand separator)
	 * @return preprocessed input
	 */
	public static String preprocessForParser(String parseString, boolean decimalComma) {
		ParserPreprocessor parserPreprocessor = new ParserPreprocessor(
				new ParserInfo(false, decimalComma));
		return parserPreprocessor.preprocess(parseString);
	}

	/**
	 * @param parseString raw parser input
	 * @param info preprocessing flags
	 * @return preprocessed input
	 */
	public static String preprocessWithInfo(String parseString, ParserInfo info) {
		ParserPreprocessor preprocessor = new ParserPreprocessor(info);
		return preprocessor.preprocess(parseString);
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

	/**
	 * @return platform dependent implementation
	 */
	public static StringUtil getPrototype() {
		return prototype;
	}

	/**
	 * @param p
	 *            prototype
	 */
	public static void setPrototypeIfNull(StringUtil p) {

		synchronized (lock) {
			if (prototype == null) {
				prototype = p;
			}
		}
	}

	/**
	 * Replaces special unicode letters (e.g. greek letters) in str by LaTeX
	 * strings.
	 * 
	 * @param str
	 *            unicode string
	 * @param convertGreekLetters
	 *            whether to convert unicode alpha to \alpha
	 * @return latex string
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
				while (j < length && (prototype.isRightToLeftChar(str.charAt(j))
						|| str.charAt(j) == '\u00a0')) {
					j++;
				}
				for (int k = j - 1; k >= i; k--) {
					sbReplaceExp.append(str.charAt(k));
				}
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
				if (previousChar != '\\') {
					sbReplaceExp.append("\\");
				}
				sbReplaceExp.append("%");
				break;

			/*
			 * not needed for JLaTeXMath and in fact it doesn't work inside
			 * \text{} // Exponents // added by Loic Le Coq 2009/11/04 case
			 * '\u2070': // ^0 sbReplaceExp.append("^0"); break;
			 * 
			 * case '\u00b9': // ^1 sbReplaceExp.append("^1"); break; // end
			 * Loic case '\u00b2': // ^2 sbReplaceExp.append("^2"); break;
			 * 
			 * case '\u00b3': // ^3 sbReplaceExp.append("^3"); break;
			 * 
			 * case '\u2074': // ^4 sbReplaceExp.append("^4"); break;
			 * 
			 * case '\u2075': // ^5 sbReplaceExp.append("^5"); break;
			 * 
			 * case '\u2076': // ^6 sbReplaceExp.append("^6"); break; // added
			 * by Loic Le Coq 2009/11/04 case '\u2077': // ^7
			 * sbReplaceExp.append("^7"); break;
			 * 
			 * case '\u2078': // ^8 sbReplaceExp.append("^8"); break;
			 * 
			 * case '\u2079': // ^9 sbReplaceExp.append("^9"); break; // end
			 * Loic Le Coq
			 */

			default:
				if (!convertGreekLetters) {
					sbReplaceExp.append(c);
				} else {

					if (c == Unicode.phi_symbol) {
						sbReplaceExp.append("\\phi");
					} else if ((c >= Unicode.alpha && c <= Unicode.omega)
							|| (c >= Unicode.Alpha && c <= Unicode.Omega)) {

						// might be null, there are more than 24*2 characters in
						// range eg sigmaf
						String greekLaTeX = Greek.getLaTeX(c);
						if (greekLaTeX != null) {
							sbReplaceExp.append("\\");
							sbReplaceExp.append(greekLaTeX);
						} else {
							sbReplaceExp.append(c);
						}
					} else {
						sbReplaceExp.append(c);
					}
				}
			}
		}
		return sbReplaceExp.toString();
	}

	/**
	 * @param s
	 *            input string
	 * @param n
	 *            number of repetitions
	 * @return a string with n instances of s eg string("hello",2) ->
	 *         "hellohello";
	 */
	public static String string(String s, int n) {

		if (n == 1) {
			return s; // most common, check first
		}
		if (n < 1) {
			return "";
		}

		StringBuilder sb = new StringBuilder(s.length() * n);

		for (int i = 0; i < n; i++) {
			sb.append(s);
		}

		return sb.toString();
	}

	/**
	 * @param str
	 *            input string
	 * @return string without " " chaacters
	 */
	public static String removeSpaces(String str) {
		if (str == null || str.length() == 0) {
			return "";
		}

		StringBuilder sb = new StringBuilder(str.length());

		char c;

		for (int i = 0; i < str.length(); i++) {
			c = str.charAt(i);
			if (c != ' ') {
				sb.append(c);
			}
		}

		return sb.toString();
	}

	/**
	 * @param sb
	 *            string builder
	 * @return sb after reset or new StringBuilder if sb is null
	 */
	public static StringBuilder resetStringBuilder(StringBuilder sb) {
		if (sb == null) {
			return new StringBuilder();
		}
		sb.setLength(0);
		return sb;
	}

	/**
	 * @param text
	 *            input text
	 * @return whether text consists of localized deigits, decimal points and
	 *         minus signs
	 */
	public static boolean isNumber(String text) {

		if (text == null || "".equals(text)) {
			return false;
		}

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (!isDigit(c) && c != '.' && c != Unicode.ARABIC_COMMA
					&& c != '-') {
				return false;
			}
		}

		return true;
	}

	/**
	 * important to use this rather than String.toLowerCase() so that it uses
	 * String.toLowerCase(Locale.US) so that the behaviour is well defined
	 * whatever language we are running in NB does cause problems eg in Turkish
	 * 
	 * @param s
	 *            input string
	 * @return the <code>String</code>, converted to lowercase.
	 * @see java.lang.String#toUpperCase(Locale)
	 */
	public static String toLowerCaseUS(String s) {
		return s.toLowerCase(Locale.US);
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
	public static String toUpperCaseUS(String s) {
		return s.toUpperCase(Locale.US);
	}

	/**
	 * Number parser supporting "null", "undefined" and "Infinity"
	 * 
	 * @param s
	 *            string
	 * @return parsed number
	 */
	public static double parseDouble(String s) {
		if (isNaN(s)) {
			return Double.NaN;
		} else if ("Infinity".equals(s)) {
			return Double.POSITIVE_INFINITY;
		} else if ("-Infinity".equals(s)) {
			return Double.NEGATIVE_INFINITY;
		}

		return Double.parseDouble(s);
	}

	/**
	 * @param s
	 *            string
	 * @return whether it is representation of null
	 */
	public static boolean isNaN(String s) {
		return "NaN".equals(s) || "undefined".equals(s) || "null".equals(s);
	}

	/**
	 * @param c
	 *            character
	 * @param count
	 *            number of repetitions
	 * @return repeated char
	 */
	public static String repeat(char c, int count) {
		StringBuilder ret = new StringBuilder();
		for (int i = 0; i < count; i++) {
			ret.append(c);
		}
		return ret.toString();
	}

	/**
	 * @param character
	 *            character
	 * @return whether it's localized digit or underscore
	 */
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
	 * 
	 * Adapted from GWT but uses a better version of isLetter() so works for eg
	 * Greek
	 * 
	 * @param c
	 *            character to test
	 * @return if c is UPPER CASE
	 */
	public static boolean isUpperCase(char c) {
		return Character.toUpperCase(c) == c && isLetter(c);
	}

	/**
	 * 
	 * Adapted from GWT but uses a better version of isLetter() so works for eg
	 * Greek
	 * 
	 * @param c
	 *            character to test
	 * @return if c is lower case
	 */
	public static boolean isLowerCase(char c) {
		return Character.toLowerCase(c) == c && isLetter(c);
	}

	/**
	 * Character.isDigit() doesn't work in GWT, see
	 * http://code.google.com/p/google-web-toolkit/issues/detail?id=1983
	 * 
	 * see also MyDouble.parseDouble()
	 * 
	 * @param ch
	 *            character
	 * @return whether it's a localized digit
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

	/**
	 * @return whether given character is a digit or or '.'
	 */
	public static boolean isDigitOrDot(char ch) {
		return isDigit(ch) || '.' == ch;
	}

	/**
	 * @param str
	 *            String
	 * @return true if str matches one of "!=", "<>", Unicode.NOTEQUAL
	 */
	public static boolean isNotEqual(String str) {
		return "!=".equals(str) || "<>".equals(str)
				|| (Unicode.NOTEQUAL + "").equals(str);
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

	/**
	 * Check that brackets in the string are correct; start checking from the
	 * right.
	 * 
	 * @param parseString
	 *            string
	 * @return error position
	 */
	public static int checkBracketsBackward(String parseString) {
		int curly = 0;
		int square = 0;
		int round = 0;
		Stack<Integer> closingBrackets = new Stack<>();
		boolean comment = false;
		for (int i = parseString.length() - 1; i >= 0; i--) {
			char ch = parseString.charAt(i);

			if (comment && ch != '"') {
				continue;
			}

			switch (ch) {
			default:
				// do nothing
				break;
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
			if (comment && ch != '"') {
				continue;
			}
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
				if (brackets == 0) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param label
	 *            label, may contain bold, italic, indices
	 * @param font
	 *            font
	 * @return ratio of estimated string length and font size
	 */
	public double estimateLengthHTML(String label, GFont font) {
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

	/**
	 * @param label
	 *            label, may contain indices
	 * @param font
	 *            font
	 * @return ratio of estimated string length and font size
	 */
	public double estimateLength(String label, GFont font) {
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
		return bold ? visibleChars * 0.6 * font.getSize()
				: visibleChars * 0.5 * font.getSize();
	}

	/**
	 * @param string
	 *            text
	 * @param font
	 *            font
	 * @return text height; depends on indices
	 */
	public double estimateHeight(String string, GFont font) {
		if (font == null) {
			return 0;
		}
		return string.indexOf('_') > -1 ? font.getSize() * 1.8
				: font.getSize() * 1.4;
	}

	/**
	 * @param sub
	 *            substitution template
	 * @param x0
	 *            replacement for %0
	 * @param x1
	 *            replacement for %1
	 * @param x2
	 *            replacement for %2
	 * @param x3
	 *            replacement for %3
	 * @return string after substitution
	 */
	public static Object format(String sub, double x0, double x1, double x2,
			double x3) {
		return sub.replaceAll("%0", x0 + "").replaceAll("%1", x1 + "")
				.replaceAll("%2", x2 + "").replace("%3", x3 + "");
	}

	/**
	 * @param s
	 *            String to wrap
	 * @param tpl
	 *            String template
	 * @return "exact(" + s + ")" if necessary (for Giac)
	 */
	public static String wrapInExact(String s, StringTemplate tpl) {
		return wrapInExact(0, s, tpl, null);
	}

	/**
	 * @param x
	 *            the number to convert
	 * @param s0
	 *            String to wrap (String representation of x)
	 * @param tpl
	 *            String template (Giac or GiacInternal)
	 * @param kernel
	 *            kernel
	 * @return "exact(" + s + ")" if necessary (for Giac) or convert double into
	 *         a fraction internally (for GiacInternal)
	 */
	public static String wrapInExact(double x, String s0, StringTemplate tpl,
			Kernel kernel) {
		if (s0.startsWith("exact(")) {
			// nothing to do
			return s0;
		}
		String s = s0;
		// if we have eg 6.048554268711413E7
		// convert to exact(6.048554268711413e7)
		if (s.indexOf("E") > -1) {
			s = s.replace("E", "e");
		}

		if (CASgiac.isUndefined(s)) {
			return "undef";
		}

		if (tpl.isNumeric()) {
			return s;
		}

		if ("inf".equals(s)) {
			return s;
		}

		if ("-inf".equals(s)) {
			return s;
		}

		StringBuilder sb1 = new StringBuilder();
		if (tpl == StringTemplate.giacTemplateInternal && kernel != null) {
			// GGB-641 this is just for ProverBotanasMethod
			sb1.append("(");
			long[] l = kernel.doubleToRational(x);
			sb1.append(l[0] + "/" + l[1]);
			sb1.append(')');
		} else {
			sb1.append("exact(");
			sb1.append(s);
			sb1.append(')');
		}

		return sb1.toString();
	}

	/**
	 * @param filename
	 *            input filename
	 * @return filename without leading slash
	 */
	public static String removeLeadingSlash(String filename) {
		if (filename != null && filename.length() != 0
				&& filename.charAt(0) == '/') {
			return filename.substring(1);
		}
		return filename;
	}

	/**
	 * @param color
	 *            color
	 * @return hex string with # prefix
	 */
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
		ArrayList<String> ret = new ArrayList<>();
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
		StringBuilder ret = new StringBuilder();
		Iterator<String> it = tokens.iterator();
		if (it.hasNext()) {
			ret.append(it.next());
		}
		while (it.hasNext()) {
			if (delimiter != null) {
				ret.append(delimiter);
			}
			ret.append(it.next());
		}
		return ret.toString();
	}

	/**
	 * @param str
	 *            number representation
	 * @return number with removed trailing .00...0 and added initial 0 in case
	 *         of ".5"
	 */
	public static String canonicalNumber(String str) {
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

	/**
	 * convert 1.200000000 into 1.2, .23 into 0.23, 1.23000E20 into 1.23E20
	 *
	 * @param str
	 *            number representation
	 * @return number without redundant trailing zeros
	 */
	public static String canonicalNumber2(String str) {
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
	 *            character
	 * @return emulation of Character.isWhiteSpace
	 */
	public static boolean isWhitespace(char c) {
		return c == ' ' || c == '\t' || /* , HORIZONTAL TABULATION. */
				c == '\n' || /* LINE FEED. */
				c == '\u000B' || /* VERTICAL TABULATION. */
				c == '\f' || /* FORM FEED. */
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
	 * Used in DynamicTextProcessor and DynamicTextInputPane
	 * 
	 * @param sb
	 *            output
	 * @param content
	 *            input
	 * @param ret
	 *            alternate between open and closed
	 * @return current quote
	 */
	public static char processQuotes(StringBuilder sb, String content,
			char ret) {
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

	/**
	 * @param str
	 *            input
	 * @return encode unicode as \\uXXXX, escape \',\", \t, \n, \r, \\
	 */
	final public static String toJavaString(String str) {
		if (str == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		// convert every single character and append it to sb
		int len = str.length();
		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);
			int code = c;

			// standard characters have code 32 to 126
			if ((code >= 32 && code <= 126)) {
				switch (code) {
				case '"':
					// replace " with \"
					sb.append("\\\"");
					break;
				case '\'':
					// replace ' with \'
					sb.append("\\'");
					break;
				case '\\':
					// replace \ with \\
					sb.append("\\\\");
					break;

				default:
					// do not convert
					sb.append(c);
				}
			}
			// special characters
			else {
				switch (code) {
				case 10: // CR
					sb.append("\\n");
					break;
				case 13: // LF
					sb.append("\\r");
					break;

				case 9: // replace TAB
					sb.append("\\t"); // space
					break;

				default:
					// convert special character to \u0123 format
					sb.append(toHexString(c));
				}
			}
		}
		return sb.toString();
	}

	/**
	 * @param fileName
	 *            eg "file.png"
	 * @return file extension in lower case eg "png" or "" if there isn't one
	 */
	public static String getFileExtensionStr(String fileName) {
		int dotPos = fileName.lastIndexOf('.');

		if ((dotPos <= 0) || (dotPos == (fileName.length() - 1))) {
			return "";
		}
		return toLowerCaseUS(fileName.substring(dotPos + 1));
	}

	/***
	 * @param fileName
	 *            file name
	 * @return extension
	 */
	public static FileExtensions getFileExtension(String fileName) {
		String ext = getFileExtensionStr(fileName);
		return FileExtensions.get(ext);
	}

	/**
	 * @param fileName
	 *            eg "file.gif"
	 * @return changes eg "file.gif" to "file"
	 */
	public static String removeFileExtension(String fileName) {
		if (fileName == null) {
			return null;
		}
		int dotPos = fileName.lastIndexOf('.');

		if (dotPos <= 0) {
			return fileName;
		}
		return fileName.substring(0, dotPos);
	}

	/**
	 * @param fileName
	 *            eg "file.gif"
	 * @param extension
	 *            eg PNG
	 * @return changes eg "file.gif" to "file.png"
	 */
	public static String changeFileExtension(String fileName,
			FileExtensions extension) {
		if (fileName == null) {
			return null;
		}

		return removeFileExtension(fileName) + "." + extension.toString();
	}

	/**
	 * @param str
	 *            input
	 * @return true if null or empty
	 */
	public static boolean empty(String str) {
		return str == null || str.isEmpty();
	}

	/**
	 * @param c
	 *            last typed char
	 * @param inputText
	 *            input
	 * @return input with inserted degree symbol if needed
	 */
	public static String addDegreeSignIfNumber(char c, String inputText) {
		// return unless digit typed
		if (!StringUtil.isDigit(c)) {
			return inputText;
		}

		// if text already contains degree symbol or variable
		for (int i = 0; i < inputText.length(); i++) {
			if (!StringUtil.isDigit(inputText.charAt(i))) {
				return inputText;
			}
		}

		return (inputText + Unicode.DEGREE_STRING);
	}

	/**
	 * @param c
	 *            0 to 7
	 * @param loc
	 *            localization
	 * @return localized gray color name
	 */
	public static String getGrayString(char c, Localization loc) {
		switch (c) {
		case '0':
			return loc.getColor("white");
		case '1':
			return loc.getPlain("AGray", Unicode.FRACTION1_8 + "");
		case '2':
			return loc.getPlain("AGray", Unicode.FRACTION1_4 + ""); // silver
		case '3':
			return loc.getPlain("AGray", Unicode.FRACTION3_8 + "");
		case '4':
			return loc.getPlain("AGray", Unicode.FRACTION1_2 + "");
		case '5':
			return loc.getPlain("AGray", Unicode.FRACTION5_8 + "");
		case '6':
			return loc.getPlain("AGray", Unicode.FRACTION3_4 + "");
		case '7':
			return loc.getPlain("AGray", Unicode.FRACTION7_8 + "");
		default:
			return loc.getColor("black");
		}
	}

	/**
	 * check if string contains LaTeX codes. If so then wrapping in \text{}
	 * probably isn't desirable eg https://www.geogebra.org/m/FH6NkgCN which has
	 * \int_{0}^{12.03} 2 \; \sqrt{x}\cdot dx = 55.66
	 * 
	 * @param str
	 *            string to check
	 * @return true if str contains any of "\" "^{" "_" (and not "/")
	 */
	public static boolean containsLaTeX(String str) {
		if (str == null || str.contains("/")) {
			return false;
		}

		if (str.contains("\\") || str.contains("^{") || str.contains("_")) {
			return true;
		}

		return false;
	}

	/**
	 * @param str
	 *            input
	 * @return whether input is null or empty after trim
	 */
	public static boolean emptyTrim(String str) {
		if (str == null) {
			return true;
		}
		for (int i = 0; i < str.length(); i++) {
			if (!isWhitespace(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param s
	 *            input string
	 * @return s with \n changed to \cr
	 */
	public static String convertToLaTeX(String s) {
		return s.replaceAll("\n", "\\\\cr ");
	}

	/**
	 * 
	 * @param color
	 *            color name
	 * @return "red" changed to "Red"
	 */
	public static String capitalize(String color) {
		// use localized version of toUpperCase(), not toUpperCase(Locale.US)
		return (color.charAt(0) + "").toUpperCase()
				+ color.substring(1, color.length());
	}

	/**
	 * @param capitalCase
	 *            string starting with uppercase
	 * @return string starting with lowercase
	 */
	public static String uncapitalize(String capitalCase) {
		// use localized version of toUpperCase(), not toUpperCase(Locale.US)
		return (capitalCase.charAt(0) + "").toLowerCase()
				+ capitalCase.substring(1, capitalCase.length());
	}

	/**
	 * @param delimiter
	 *            delimiter
	 * @param objects
	 *            objects to be joined
	 * @return joined string
	 */
	public static String join(String delimiter, Object[] objects) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < objects.length; i++) {
			if (i != 0) {
				sb.append(delimiter);
			}
			sb.append(objects[i]);
		}
		return sb.toString();
	}

	/**
	 * @param delimiter
	 *            delimiter
	 * @param objects
	 *            objects to be joined
	 * @return joined string
	 */
	public static String join(String delimiter, Iterable<?> objects) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (Object o : objects) {
			if (i != 0) {
				sb.append(delimiter);
			}
			sb.append(o);
			i++;
		}
		return sb.toString();
	}

	/**
	 * converts an integer to a unicode SUPERSCRIPT string (including minus
	 * sign) eg for use as a power

	 * @param i0
	 *            number
	 * @return unicode superscript index
	 */
	public static String numberToIndex(int i0) {
		final StringBuilder sb = new StringBuilder();
		numberToIndex(i0, sb);
		return sb.toString();
	}

	/**
	 * converts an integer to a unicode SUPERSCRIPT string (including minus
	 * sign) eg for use as a power, appends result to a string builder

	 * @param i0
	 *            number
	 */
	public static void numberToIndex(int i0, StringBuilder sb) {
		int i = i0;
		if (i < 0) {
			sb.append(Unicode.SUPERSCRIPT_MINUS);
			i = -i;
		}
		int offset = sb.length();
		do {
			sb.insert(offset, Unicode.numberToSuperscript(i % 10));
			i = i / 10;
		} while (i > 0);
	}

	/**
	 * Inverse of numberToIndex
	 * @param power superscript power
	 * @return value
	 */
	public static int indexToNumber(String power) {
		int sign = 1;
		int start = 0;
		if (power.charAt(0) == Unicode.SUPERSCRIPT_MINUS) {
			start = 1;
			sign = -1;
		}

		int val = 0;
		for (int i = start; i < power.length(); i++) {
			char digit = power.charAt(i);
			val = 10 * val + Unicode.superscriptToNumber(digit);
		}

		return val * sign;
	}

	/**
	 * @param symbolsStartValue
	 *            first symbol
	 * @param symbolsNumber
	 *            number of symbols
	 * @return unicode range
	 */
	public static String[] getSetOfSymbols(int symbolsStartValue,
			int symbolsNumber) {
		String[] symbols = new String[symbolsNumber];
		for (int i = 0; i < symbolsNumber; i++) {
			symbols[i] = "" + (char) (symbolsStartValue + i);
		}
		return symbols;
	}

	/**
	 * 
	 * @param text
	 *            string to check
	 * @return true if text is a simple ASCII 7-bit string
	 */
	public static boolean isASCII(String text) {
		if (empty(text)) {
			return true;
		}

		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if (ch > 0x7f) {
				return false;
			}
		}

		return true;
	}

	/**
	 * @param ch
	 *            to check.
	 * @return true iff the character is a currency sign
	 */
	public static boolean isCurrency(char ch) {
		return Unicode.currencyList.indexOf(ch) != -1;
	}

	/**
	 * @param text
	 *            to check.
	 * @return true iff the string is a currency sign
	 */
	public static boolean isCurrency(String text) {
		if (text == null) {
			return false;
		}
		String str = text.trim();
		return str.length() == 1 && isCurrency(str.charAt(0));
	}

	/**
	 * @param text
	 *            tested string
	 * @return whether text is empty or equal to "0"
	 */
	public static boolean emptyOrZero(String text) {
		return empty(text) || "0".equals(text);
	}

	/**
	 * @param text
	 *            ASCII string
	 * @return HTML lines wrapped in divs, empty lines encoded as
	 *         &lt;div>&ltbr>&lt/div>
	 */
	public static String newlinesToHTML(String text) {
		String[] lines = text.split("\n");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < lines.length; i++) {
			sb.append("<div>");
			sb.append(lines[i].isEmpty() ? "<br>" : lines[i]);
			sb.append("</div>");
		}
		return sb.toString();
	}

	/**
	 * @param html
	 *            raw HTML
	 * @return ASCII string; ends of divs and paragraphs replaced by \n
	 */
	public static String htmlToNewlines(String html) {
		return html.replace("<br></div>", "\n").replaceAll("</div>(.)", "\n$1")
				.replace("<br>", "\n").replace("</p>", "\n")
				.replaceAll("</?[^>]+>", "");
	}

	/**
	 * 
	 * @param sb
	 *            StringBuilder
	 * @param ch
	 *            Unicode character to append (might be more than one char eg
	 *            &#x1D5AA)
	 */
	public static void appendUnicode(StringBuilder sb, int ch) {

		if (ch <= 0xffff) {
			sb.append((char) ch);
		} else {

			// eg &#x1D5AA (doesn't fit in a single char)
			char[] chars = Character.toChars(ch);

			for (int i = 0; i < chars.length; i++) {
				sb.append(chars[i]);
			}
		}

	}

	/**
	 * @param value
	 *            the String value to test
	 * @return true if the value contains only numbers, false otherwise/
	 */
	public static boolean isSimpleNumber(String value) {
		return value.matches("[1234567890\\-.]+");
	}

	/**
	 * Used to decide whether to use serif or sans serif eg axis labels
	 *
	 * @param string
	 *            text
	 * @return if string starts with eg \mathrm
	 */
	public static boolean startsWithFormattingCommand(String string) {
		return string != null && string.length() > 6
				&& string.startsWith("$\\math");
	}

        /**
         * Append number formated to fixed number of significant digits to a builder.
         *
         * @param sbFormatSF
         *            output builder
	 * @param x
	 *            number
	 * @param sfa
	 *            format
         */
	public static void appendFormat(StringBuilder sbFormatSF, double x,
			ScientificFormatAdapter sfa) {
		String absStr;
		if (x == 0) {
			// avoid output of "-0.00"
			absStr = sfa.format(0);
		} else if (x > 0) {
			absStr = sfa.format(x);
		} else {
			sbFormatSF.append('-');
			absStr = sfa.format(-x);
		}

		// make sure ".123" is returned as "0.123".
		if (absStr.charAt(0) == '.') {
			sbFormatSF.append('0');
		}
		sbFormatSF.append(absStr);
	}

    /**
     * Append string representation with format +/-ddd.pp (if 3 digits and precision is 2).
     * Too large values are output +/-XXXXX
     *
     * @param val value
     * @param digits
     *            digits length
     * @param precision
     *            decimal precision
     * @param sb where to write result
     */
	public static void toString(double val, int digits, int precision, StringBuilder sb) {
        if (val < 0) {
            sb.append('-');
        } else {
            sb.append('+');
        }
        int v = (int) Math.round(Math.abs(val * Math.pow(10, precision)));
        int[] decimalsList = new int[precision];
        for (int j = precision - 1; j >= 0; j--) {
            decimalsList[j] = v % 10;
            v /= 10;
        }
        int[] digitsList = new int[digits];
        for (int j = digits - 1; j >= 0; j--) {
            digitsList[j] = v % 10;
            v /= 10;
        }
        if (v > 0) { // overflow
            sb.append(StringUtil.repeat('X', digits + 1 + precision));
        } else {
            for (int d : digitsList) {
                sb.append(d);
            }
            sb.append(".");
            for (int d : decimalsList) {
                sb.append(d);
            }
        }
    }

}
