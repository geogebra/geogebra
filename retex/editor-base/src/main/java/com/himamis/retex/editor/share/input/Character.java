package com.himamis.retex.editor.share.input;

public class Character {

	/**
	 * Character.isLetter() doesn't work in GWT, see
	 * http://code.google.com/p/google-web-toolkit/issues/detail?id=1983
	 * 
	 * @param c
	 *            character
	 * @return whether it's a letter
	 */
    public static boolean isLetter(char c) {
        return (c >= '\u0041' && c <= '\u005a') || // upper case (A-Z)
                (c >= '\u0061' && c <= '\u007a') || // lower case (a-z)
                (c == '\u00b7') || // middle dot (for Catalan)
                (c >= '\u00c0' && c <= '\u00d6') || // accentuated letters
                (c >= '\u00d8' && c <= '\u00f6') || // accentuated letters
                (c >= '\u00f8' && c <= '\u01bf') || // accentuated letters
                (c >= '\u01c4' && c <= '\u02a8') || // accentuated letters
                (c >= '\u038e' && c <= '\u03f5') || // Greek
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
                (c >= '\uffa1' && c <= '\uffdc');
    }

	/**
	 * Character.isLetterOrDigit() doesn't work in GWT, see
	 * http://code.google.com/p/google-web-toolkit/issues/detail?id=1983
	 * 
	 * @param c
	 *            character
	 * @return whether it's either letter or digit
	 */
    public static boolean isLetterOrDigit(char c) {
        return java.lang.Character.isDigit(c) || isLetter(c);
    }

	/**
	 * @param string
	 *            character sequence
	 * @return whether all characters are letters
	 */
    public static boolean areLetters(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (!isLetter(string.charAt(i))) {
                return false;
            }
        }
        return true;
    }

	/**
	 * @param string
	 *            character sequence
	 * @return whether all characters are alphanumeric
	 */
    public static boolean areLettersOrDigits(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (!isLetterOrDigit(string.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
