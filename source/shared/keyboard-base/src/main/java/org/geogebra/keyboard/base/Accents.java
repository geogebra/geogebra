package org.geogebra.keyboard.base;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains accents, adds available diacritics to simple letters.
 */
public class Accents {

	public static final String ACCENT_ACUTE = "\u00B4";
	public static final String ACCENT_GRAVE = "\u0060";
	public static final String ACCENT_CARON = "\u02C7";
	public static final String ACCENT_CIRCUMFLEX = "\u005E";

	private static Map<String, String> acute;
	private static Map<String, String> grave;
	private static Map<String, String> caron;
	private static Map<String, String> circumflex;

	private static Map<String, String> getAcuteLetters() {
		if (acute == null) {
			acute = new HashMap<>();
			acute.put("a", "\u00e1");
			acute.put("A", "\u00c1");
			acute.put("c", "\u0107");
			acute.put("C", "\u0106");
			acute.put("e", "\u00e9");
			acute.put("E", "\u00C9");
			acute.put("g", "\u01F5");
			acute.put("G", "\u01F4");
			acute.put("i", "\u00ed");
			acute.put("I", "\u00cd");
			acute.put("k", "\u1E31");
			acute.put("K", "\u1E30");
			acute.put("l", "\u013A");
			acute.put("L", "\u0139");
			acute.put("m", "\u1E3F");
			acute.put("M", "\u1E3E");
			acute.put("n", "\u0144");
			acute.put("N", "\u0143");
			acute.put("o", "\u00f3");
			acute.put("O", "\u00d3");
			acute.put("p", "\u1E55");
			acute.put("P", "\u1E54");
			acute.put("r", "\u0155");
			acute.put("R", "\u0154");
			acute.put("s", "\u015B");
			acute.put("S", "\u015A");
			acute.put("u", "\u00fa");
			acute.put("U", "\u00da");
			acute.put("w", "\u1E83");
			acute.put("W", "\u1E82");
			acute.put("y", "\u00fd");
			acute.put("Y", "\u00dd");
			acute.put("z", "\u017A");
			acute.put("Z", "\u0179");
		}

		return acute;
	}

	private static Map<String, String> getGraveLetters() {
		if (grave == null) {
			grave = new HashMap<>();
			grave.put("a", "\u00e0");
			grave.put("A", "\u00c0");
			grave.put("e", "\u00e8");
			grave.put("E", "\u00C8");
			grave.put("i", "\u00ec");
			grave.put("I", "\u00cc");
			grave.put("n", "\u01F9");
			grave.put("N", "\u01F8");
			grave.put("o", "\u00f2");
			grave.put("O", "\u00d2");
			grave.put("u", "\u00f9");
			grave.put("U", "\u00d9");
			grave.put("w", "\u1E81");
			grave.put("W", "\u1E80");
		}

		return grave;
	}

	private static Map<String, String> getCaronLetters() {
		if (caron == null) {
			caron = new HashMap<>();
			caron.put("a", "\u01CE");
			caron.put("A", "\u01CD");
			caron.put("c", "\u010d");
			caron.put("C", "\u010c");
			caron.put("d", "\u010F");
			caron.put("D", "\u010e");
			caron.put("e", "\u011b");
			caron.put("E", "\u011A");
			caron.put("g", "\u01E7");
			caron.put("G", "\u01E6");
			caron.put("h", "\u021F");
			caron.put("H", "\u021E");
			caron.put("i", "\u01D0");
			caron.put("I", "\u01CF");
			caron.put("j", "\u01F0");
			caron.put("k", "\u01E9");
			caron.put("K", "\u01E8");
			caron.put("l", "\u013E");
			caron.put("L", "\u013D");
			caron.put("n", "\u0148");
			caron.put("N", "\u0147");
			caron.put("o", "\u01D2");
			caron.put("O", "\u01D1");
			caron.put("r", "\u0159");
			caron.put("R", "\u0158");
			caron.put("s", "\u0161");
			caron.put("S", "\u0160");
			caron.put("t", "\u0165");
			caron.put("T", "\u0164");
			caron.put("u", "\u01D4");
			caron.put("U", "\u01D3");
			caron.put("z", "\u017e");
			caron.put("Z", "\u017d");
		}
		
		return caron;
	}

	private static Map<String, String> getCircumflexLetters() {
		if (circumflex == null) {
			circumflex = new HashMap<>();
			circumflex.put("a", "\u00e2");
			circumflex.put("A", "\u00c2");
			circumflex.put("c", "\u0109");
			circumflex.put("C", "\u0108");
			circumflex.put("e", "\u00ea");
			circumflex.put("E", "\u00Ca");
			circumflex.put("g", "\u011D");
			circumflex.put("G", "\u011C");
			circumflex.put("h", "\u0125");
			circumflex.put("H", "\u0124");
			circumflex.put("i", "\u00ee");
			circumflex.put("I", "\u00ce");
			circumflex.put("j", "\u0135");
			circumflex.put("J", "\u0134");
			circumflex.put("o", "\u00f4");
			circumflex.put("O", "\u00d4");
			circumflex.put("s", "\u015D");
			circumflex.put("S", "\u015C");
			circumflex.put("u", "\u00fb");
			circumflex.put("U", "\u00db");
			circumflex.put("w", "\u0175");
			circumflex.put("W", "\u0174");
			circumflex.put("y", "\u0177");
			circumflex.put("Y", "\u0176");
			circumflex.put("z", "\u1E91");
			circumflex.put("Z", "\u1E90");
		}

		return circumflex;
	}

	/**
	 * @param letter
	 *            letter
	 * @return the acute version of letter if it exists, null otherwise.
	 */
	public String getAcuteLetter(String letter) {
		return getAcuteLetters().get(letter);
	}

	/**
	 * @param letter
	 *            letter
	 * @return the grave version of letter if it exists, null otherwise.
	 */
	public String getGraveAccent(String letter) {
		return getGraveLetters().get(letter);
	}

	/**
	 * @param letter
	 *            letter
	 * @return the caron version of letter if it exists, null otherwise.
	 */
	public String getCaronLetter(String letter) {
		return getCaronLetters().get(letter);
	}

	/**
	 * @param letter
	 *            letter
	 * @return the circumflex version of letter if it exists, null otherwise.
	 */
	public String getCircumflexLetter(String letter) {
		return getCircumflexLetters().get(letter);
	}

	/**
	 * @param txt
	 *            input
	 * @return whether input is one of the accent signs
	 */
	public static boolean isAccent(String txt) {
		return ACCENT_GRAVE.equals(txt) || ACCENT_ACUTE.equals(txt)
				|| ACCENT_CIRCUMFLEX.equals(txt) || ACCENT_CARON.equals(txt);
	}

}
