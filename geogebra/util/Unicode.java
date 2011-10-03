package geogebra.util;

import java.util.HashMap;
import java.util.Iterator;

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
	
	final public static char FEMININE_ORDINAL_INDICATOR = '\u00aa';
	
	final public static String IMAGINARY = "\u03af"; // GREEK SMALL LETTER IOTA WITH TONOS
	public static final String NBSP = "\u00a0"; // non-breaking (hard) space
	public static char mu = '\u03bc';
	public static char sigma = '\u03c3';
	
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
	
	/*
	 * converts an integer to a unicode superscript string (including minus sign)
	 * eg for use as a power
	 * @author Michael
	 */
	final public static String numberToIndex(int i) {

		StringBuilder sb = new StringBuilder();
		 if (i < 0)
		 {
			 sb.append(Superscript_Minus); // superscript minus sign
			 i = -i;
		 }
		 
		 if (i == 0) sb.append(Superscript_0); // zero     					 
		 else while (i>0) {
			 switch (i%10) {
	            case 0: sb.insert(0, Superscript_0); break;
	            case 1: sb.insert(0, Superscript_1); break;
	            case 2: sb.insert(0, Superscript_2); break;
	            case 3: sb.insert(0, Superscript_3); break;
	            case 4: sb.insert(0, Superscript_4); break;
	            case 5: sb.insert(0, Superscript_5); break;
	            case 6: sb.insert(0, Superscript_6); break;
	            case 7: sb.insert(0, Superscript_7); break;
	            case 8: sb.insert(0, Superscript_8); break;
	            case 9: sb.insert(0, Superscript_9); break;
			 
			 }
			 i = i / 10;
		 }
		 
		 return sb.toString();

	}

	final public static boolean isSuperscriptDigit(char c) {
		return (c >= Superscript_0 && c <= Superscript_9) || c == Superscript_1 || c == Superscript_2 || c == Superscript_3;
	}
	
	static HashMap<String, Character> testCharMap = null;

	public static Character getTestChar(String lang) {
		initCharMap();
				
		return (Character)testCharMap.get(lang);
	}
	
	public static Iterator<String> getCharMapIterator() {
		initCharMap();

		return testCharMap.keySet().iterator();
	}
	
	/*
	 * test characters to get a font that can display the correct symbols for each language
	 * Also used to register the fonts so that JLaTeXMath can display other Unicode blocks
	 */
	private static void initCharMap() {
		if (testCharMap == null) {
			testCharMap = new HashMap<String, Character>();
			testCharMap.put("zh", '\u984F');// Chinese, last CJK unified ideograph in unicode alphabet
			testCharMap.put("ka", '\u10d8'); // Georgian
			testCharMap.put("iw", '\u05ea'); // Hebrew
			testCharMap.put("ji", '\u05ea'); // Yiddish
			testCharMap.put("ja", '\uff9d'); // Japanese
			testCharMap.put("ta", '\u0be7'); // Tamil
			testCharMap.put("pa", '\u0be7'); // Punjabi
			testCharMap.put("hi", '\u0be7'); // Hindi
			testCharMap.put("ur", '\u0be7'); // Urdu
			testCharMap.put("gu", '\u0be7'); // Gujarati
			testCharMap.put("si", '\u0d9a'); // Sinhala
			// Arabic is in standard Java fonts, so we don't need to search for a font
			//testCharMap.put("ar", '\u0681'); // Arabic
			testCharMap.put("ml", '\u0D2E'); // Malayalam
			testCharMap.put("ko", '\u1103'); // Korean, changed from \uD55C (doesn't work in Ubuntu)
			testCharMap.put("ru", '\u0439'); // Russian
			testCharMap.put("mr", '\u092e'); // Marathi
			testCharMap.put("ne", '\u0947'); // Nepalese
			
		}
		
	}
	
}
