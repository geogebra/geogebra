package geogebra.common.util;

import geogebra.common.awt.GColor;
import geogebra.common.main.App;

import java.util.Locale;

public class StringUtil {

	// code from freenet
	// http://emu.freenetproject.org/pipermail/cvs/2007-June/040186.html
	// GPL2
	public static String convertToHex(byte[] data) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9)) {
					buf.append((char) ('0' + halfbyte));
				} else {
					buf.append((char) ('a' + (halfbyte - 10)));
				}
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	/**
	 * converts Color to hex String with RGB values
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
		for (int i = 0 ; i < s.length() ; i++) {
			sb.append(toHexString(s.charAt(i)));
		}

		return sb.toString();
	}

	//     table to convert a nibble to a hex char.
	private static char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7',
		'8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/**
	 * Converts the given unicode string 
	 * to an html string where special characters are
	 * converted to <code>&#xxx;</code> sequences (xxx is the unicode value
	 * of the character)
	 * 
	 * @author Markus Hohenwarter
	 */
	final public static String toHTMLString(String str) {
		if (str == null)
			return null;

		StringBuilder sb = new StringBuilder();

		// convert every single character and append it to sb
		int len = str.length();
		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);
			int code = c;

			//  standard characters have code 32 to 126
			if ((code >= 32 && code <= 126)) {
				switch (code) {
				case 60:
					sb.append("&lt;");
					break; //   <
				case 62:
					sb.append("&gt;");
					break; // >

				default:
					//do not convert                
					sb.append(c);
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
					//  convert special character to escaped HTML               
					sb.append("&#");
					sb.append(code);
					sb.append(';');
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Converts the given unicode string 
	 * to a string where special characters are
	 * converted to <code>&#encoding;</code> sequences . 
	 * The resulting string can be used in XML files.
	 */
	public static String encodeXML(String str) {

		StringBuilder sb = new StringBuilder(str.length());

		encodeXML(sb, str);

		return sb.toString();
	}

	/**
	 * Converts the given unicode string 
	 * to a string where special characters are
	 * converted to <code>&#encoding;</code> sequences . 
	 * The resulting string can be used in XML files.
	 */
	public static void encodeXML(StringBuilder sb, String str) {
		if (str == null)
			return;

		//  convert every single character and append it to sb
		int len = str.length();
		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);

			if (c <= '\u001f') {
				// #2399 all apart from U+0009, U+000A, U+000D are invalid in XML
				// none should appear anyway, but encode to be safe

				// eg &#10;
				sb.append("&#");
				sb.append(((int)c)+"");
				sb.append(';');

				if (c != '\n') {
					App.warn("Control character being written to XML: "+((int)c));
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

	protected boolean isRightToLeftChar( char c ) {

		return false; 
	}

	public static StringUtil prototype;
	/**
	 * Replaces special unicode letters (e.g. greek letters)
	 * in str by LaTeX strings.
	 */
	public static synchronized String toLaTeXString(String str,
			boolean convertGreekLetters) {
		int length = str.length();
		sbReplaceExp.setLength(0);
		for (int i = 0; i < length; i++) {
			char c = str.charAt(i);


			// Guy Hed 30.8.2009
			// Fix Hebrew 'undefined' problem in Latex text.
			if( prototype.isRightToLeftChar(c) ) {
				int j = i;
				while( j<length && (prototype.isRightToLeftChar(str.charAt(j)) || str.charAt(j)=='\u00a0'))
					j++;
				for( int k=j-1; k>=i ; k-- )
					sbReplaceExp.append(str.charAt(k));
				sbReplaceExp.append(' ');
				i=j-1;
				continue;
			}
			// Guy Hed 30.8.2009

			switch (c) {
			/*
	     case '(':
	     sbReplaceExp.append("\\left(");
	     break;

	     case ')':
	     sbReplaceExp.append("\\right)");
	     break;
			 */

			// Exponents
			// added by Loïc Le Coq 2009/11/04
			case '\u2070': // ^0
				sbReplaceExp.append("^0");
				break;

			case '\u00b9': // ^1
				sbReplaceExp.append("^1");
				break;
				// end Loïc
			case '\u00b2': // ^2
				sbReplaceExp.append("^2");
				break;

			case '\u00b3': // ^3
				sbReplaceExp.append("^3");
				break;

			case '\u2074': // ^4
				sbReplaceExp.append("^4");
				break;

			case '\u2075': // ^5
				sbReplaceExp.append("^5");
				break;

			case '\u2076': // ^6
				sbReplaceExp.append("^6");
				break;
				// added by Loïc Le Coq 2009/11/04
			case '\u2077': // ^7
				sbReplaceExp.append("^7");
				break;

			case '\u2078': // ^8
				sbReplaceExp.append("^8");
				break;

			case '\u2079': // ^9
				sbReplaceExp.append("^9");
				break;
				// end Loïc Le Coq

			default:
				if (!convertGreekLetters) {
					sbReplaceExp.append(c);
				} else {
					switch (c) {
					// greek letters
					case '\u03b1':
						sbReplaceExp.append("\\alpha");
						break;

					case '\u03b2':
						sbReplaceExp.append("\\beta");
						break;

					case '\u03b3':
						sbReplaceExp.append("\\gamma");
						break;

					case '\u03b4':
						sbReplaceExp.append("\\delta");
						break;

					case '\u03b5':
						sbReplaceExp.append("\\varepsilon");
						break;

					case '\u03b6':
						sbReplaceExp.append("\\zeta");
						break;

					case '\u03b7':
						sbReplaceExp.append("\\eta");
						break;

					case '\u03b8':
						sbReplaceExp.append("\\theta");
						break;

					case '\u03b9':
						sbReplaceExp.append("\\iota");
						break;

					case '\u03ba':
						sbReplaceExp.append("\\kappa");
						break;

					case '\u03bb':
						sbReplaceExp.append("\\lambda");
						break;

					case '\u03bc':
						sbReplaceExp.append("\\mu");
						break;

					case '\u03bd':
						sbReplaceExp.append("\\nu");
						break;

					case '\u03be':
						sbReplaceExp.append("\\xi");
						break;

					case '\u03bf':
						sbReplaceExp.append("o");
						break;

					case '\u03c0':
						sbReplaceExp.append("\\pi");
						break;

					case '\u03c1':
						sbReplaceExp.append("\\rho");
						break;

					case '\u03c3':
						sbReplaceExp.append("\\sigma");
						break;

					case '\u03c4':
						sbReplaceExp.append("\\tau");
						break;

					case '\u03c5':
						sbReplaceExp.append("\\upsilon");
						break;

					case '\u03c6':
						sbReplaceExp.append("\\phi");
						break;

					case '\u03d5':
						sbReplaceExp.append("\\varphi");
						break;

					case '\u03c7':
						sbReplaceExp.append("\\chi");
						break;

					case '\u03c8':
						sbReplaceExp.append("\\psi");
						break;

					case '\u03c9':
						sbReplaceExp.append("\\omega");
						break;

						// GREEK upper case letters				
					case '\u0393':
						sbReplaceExp.append("\\Gamma");
						break;

					case '\u0394':
						sbReplaceExp.append("\\Delta");
						break;

					case '\u0398':
						sbReplaceExp.append("\\Theta");
						break;

					case '\u039b':
						sbReplaceExp.append("\\Lambda");
						break;

					case '\u039e':
						sbReplaceExp.append("\\Xi");
						break;

					case '\u03a0':
						sbReplaceExp.append("\\Pi");
						break;

					case '\u03a3':
						sbReplaceExp.append("\\Sigma");
						break;

					case '\u03a6':
						sbReplaceExp.append("\\Phi");
						break;

					case '\u03a8':
						sbReplaceExp.append("\\Psi");
						break;

					case '\u03a9':
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
	 * returns a string with n instances of s
	 * eg string("hello",2) -> "hellohello";
	 */
	public static String string(String s, int n) {

		if (n == 1) return s; // most common, check first
		if (n < 1) return "";

		if (sb == null)
			sb = new StringBuilder(); 

		sb.setLength(0);

		for (int i = 0 ; i < n ; i++) {
			sb.append(s);
		}

		return sb.toString();
	}

	public static String removeSpaces(String str) {

		if (str == null || str.length() == 0) return "";

		if (sb == null)
			sb = new StringBuilder(); 

		sb.setLength(0);
		char c;

		for (int i = 0 ; i < str.length() ; i++) {
			c = str.charAt(i);
			if (c != ' ')
				sb.append(c);
		}

		return sb.toString();

	}

	/**
	 * Removes spaces from the start and end
	 * Not the same as trim - it removes ASCII control chars eg tab
	 * Michael Borcherds 2007-11-23
	 * @param str
	 */
	public static String trimSpaces(String str) {

		int len = str.length();

		if (len == 0) return "";

		int start = 0;
		while (str.charAt(start) == ' ' && start < len - 1) start++;

		int end = len;
		while (str.charAt(end - 1) == ' ' && end > start) end --;

		if (start == end)
			return "";

		return str.substring(start, end);

	}


	private static StringBuilder sbReplaceExp = new StringBuilder(200);

	public static StringBuilder resetStringBuilder(StringBuilder high) {
		if (high == null) return new StringBuilder();
		high.setLength(0);
		return high;
	}

	public static boolean isNumber(String text) {
		for (int i = 0 ; i < text.length() ; i++) {
			char c = text.charAt(i);
			if (!isDigit(c) && c != App.unicodeDecimalPoint && c != '-') return false; 
		}

		return true;
	}

	/**
	 * Safe implementation of toLowerCase
	 * @param s input string 
	 * @return the <code>String</code>, converted to lowercase.
	 * @see     #toLowerCase(String)
	 */
	protected String toLower(String s) {
		return s.toLowerCase();
	}

	/**
	 * Safe implementation of toLowerCase
	 * @param s input string 
	 * @return the <code>String</code>, converted to lowercase.
	 * @see     #toLowerCase(String)
	 */
	protected String toUpper(String s) {
		return s.toUpperCase();
	}

	/**
	 * important to use this rather than String.toLowerCase() as this is overridden in 
	 * desktop.Application so that it uses  String.toLowerCase(Locale.US)
	 * so that the behavior is well defined whatever language we are running in
	 * NB does cause problems eg in Turkish
	 * @param s input string
	 * @return the <code>String</code>, converted to lowercase.
	 * @see     java.lang.String#toUpperCase(Locale)
	 */
	public static String toLowerCase(String s) {
		return prototype.toLower(s);
	}

	/**
	 * important to use this rather than String.toLowerCase() as this is overridden in 
	 * desktop.Application so that it uses  String.toLowerCase(Locale.US)
	 * so that the behavior is well defined whatever language we are running in
	 * NB does cause problems eg in Turkish
	 * @param s input string
	 * @return the <code>String</code>, converted to lowercase.
	 * @see     java.lang.String#toUpperCase(Locale)
	 */
	public static String toUpperCase(String s) {
		return prototype.toUpper(s);
	}

	public static double parseDouble(String s){
		if("NaN".equals(s))
			return Double.NaN;
		return Double.parseDouble(s);
	}

	public static String repeat(char c, int count) {
		StringBuilder ret = new StringBuilder();
		for(int i=0;i<count;i++)
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
		
		// check roman first (most common)
		if ((ch >= 0x30 && ch <= 0x39)
		|| (ch >= 0x660 && ch <= 0x669)
		|| (ch >= 0x6f0 && ch <= 0x6f9)
		|| (ch >= 0x966 && ch <= 0x96f)
		|| (ch >= 0x9e6 && ch <= 0x9ef)
		|| (ch >= 0xa66 && ch <= 0xa6f)
		|| (ch >= 0xae6 && ch <= 0xaef)
		|| (ch >= 0xb66 && ch <= 0xb6f)
		|| (ch >= 0xbe6 && ch <= 0xbef) // Java (5?) bug: \u0BE6 not recognized by Character.isDigit()
		|| (ch >= 0xc66 && ch <= 0xc6f)
		|| (ch >= 0xce6 && ch <= 0xcef)
		|| (ch >= 0xd66 && ch <= 0xd6f)
		|| (ch >= 0xe50 && ch <= 0xe59)
		|| (ch >= 0xed0 && ch <= 0xed9)
		|| (ch >= 0xf20 && ch <= 0xf29)
		|| (ch >= 0x1040 && ch <= 0x1049)
		|| (ch >= 0x17e0 && ch <= 0x17e9)
		|| (ch >= 0x1810 && ch <= 0x1819)
		|| (ch >= 0x1b50 && ch <= 0x1b59) // not recognized by Java's version of Character.isDigit() !
		|| (ch >= 0x1bb0 && ch <= 0x1bb9) // not recognized by Java's version of Character.isDigit() !
		|| (ch >= 0x1c40 && ch <= 0x1c49) // not recognized by Java's version of Character.isDigit() !
		|| (ch >= 0x1c50 && ch <= 0x1c59) // not recognized by Java's version of Character.isDigit() !
		 || (ch >= 0xa8d0 && ch <= 0xa8d9) // not recognized by Java's version of Character.isDigit() !
// following not handled by GeoGebra's parser
//		|| (ch >= 0x1369 && ch <= 0x1371) // Ethiopic
//		|| (ch >= 0x1946 && ch <= 0x194F) // Limbu
//		|| (ch >= 0xFF10 && ch <= 0xFF19) //"FULL WIDTH" digits
				) {
			return true;
		}
		
		return false;
	}
	
	/*
	public static void test() {
		App.debug("starting test");
		

		for (int c = 0; c < 65536; ++c) {
			//System.out.println("            assert Character.isLetter((char)c) == Unicode.isLetter((char)c) : c;");
//App.debug(c);
			if (Character.isDigit((char)c) != isDigit((char)c)) {
				App.debug("isDigit failed "+c + " "+toHexString((char)c)+Character.isDigit((char)c)+" "+isDigit((char)c));
			}
			if (Character.isLetter((char)c) != isLetter((char)c)) {
				App.debug("isLetter failed "+c + " "+toHexString((char)c)+Character.isDigit((char)c)+" "+isDigit((char)c));
			}
		}
		App.debug("ending test");
		
	}*/


	/** 
	 * Character.isLetter() doesn't work in GWT, see
	 * http://code.google.com/p/google-web-toolkit/issues/detail?id=1983
	 */
	public static boolean isLetter(char c)
	{
		return (isLetter[c/32]&(1<<(c%32)))!=0;
	}

	private final static int[] isLetter = {
		0,0,134217726,134217726,0,69207040,-8388609,-8388609,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,8388607,-65536,-1,-1,-1,262083,16415,0,0,0,67108864,
		-10432,-5,-32769,264241151,-1,-1,-1,-1,-1021,-1,-32769,54525951,65535,
		-131072,41943039,-2,255,0,-65536,460799,0,134217726,2047,-81920,-1,
		-1,3145727,-1677672352,-196608,65535,57344,0,-1,131135,0,0,0,0,0,
		0,0,0,0,0,-16,603979775,-16711680,3,-417824,600178175,-1342177280,
		196611,-423968,57540095,1577058304,1835008,-278560,602799615,65536,
		3,-417824,602799615,-1342177280,131075,-700594200,62899992,0,0,-139296,
		66059775,0,3,-139296,602930687,1073741824,3,-139296,67108351,0,3,
		-58720288,805044223,127,0,-2,917503,127,0,-17816170,537783470,805306463,
		0,1,0,-257,2047,3840,0,0,0,-1,1787,4128768,0,0,-1,-65473,33554431,
		-1,-1,-2080374785,-1,-1,-249,-1,67108863,-129,-1,1031748991,-1,-49793,
		2134736895,-8421571,-32769,2134736895,-1,134217599,0,0,-1,-1,2097151,
		-2,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,8364031,
		134217726,-1,-1,2047,253951,262143,262143,122879,-1,1048575,276824064,
		0,0,-1,-1,16777215,-1,511,0,0,536870911,0,-65536,2047999,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-1,-1,-1,4095,0,0,
		0,0,-1,-1,-1,-1,268435455,-1,-1,67108863,1061158911,-1,-1426112705,
		1073741823,-1,1608515583,265232348,534519807,0,0,0,-2147352576,0,
		0,0,0,1043332228,-470041264,992,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,96,406716416,-2,-1,-528482305,-2,-1,-134217729,-32,-122881,-1,-1,
		32767,16777215,0,-65536,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,4194303,0,0,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,63,0,0,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,8191,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,15,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-49153,-1,2047,0,0,0,0,-1594359681,1602223615,-37,-1,-1,262143,-524288,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,1073741823,-65536,-1,-196609,-1,255,
		268369920,0,0,0,-2162688,-1,-1,-1,536870911,0,134217726,134217726,
		-64,-1,2147483647,486341884,0,
	};

	/**
	 * @param str String
	 * @return true if str matches one of "!=", "<>", Unicode.NOTEQUAL
	 */
	public static boolean isNotEqual(String str) {
		return "!=".equals(str) ||
				"<>".equals(str) ||
				Unicode.NOTEQUAL.equals(str);
	}

	/**
	 * @param str String
	 * @return true if str matches one of "<", ">", "!=", "<>", Unicode.NOTEQUAL
	 */
	public static boolean isInequality(String str) {
		return "<".equals(str) ||
				">".equals(str) ||
				isNotEqual(str);
	}

}
