package org.geogebra.keyboard.base;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains accents, adds available diacritics to simple letters.
 */
public class Accents {

    private static Map<String, String> acute;
    private static Map<String, String> grave;
    private static Map<String, String> caron;
    private static Map<String, String> circumflex;

    static {
        initAcuteLetters();
        initGraveLetters();
        initCaronLetters();
        initCircumflexLetters();
    }

    private static void initAcuteLetters() {
        acute = new HashMap<>();
        acute.put("a", "\u00e1");
        acute.put("A", "\u00c1");
        acute.put("e", "\u00e9");
        acute.put("E", "\u00C9");
        acute.put("i", "\u00ed");
        acute.put("I", "\u00cd");
        acute.put("l", "\u013A");
        acute.put("L", "\u0139");
        acute.put("o", "\u00f3");
        acute.put("O", "\u00d3");
        acute.put("r", "\u0155");
        acute.put("R", "\u0154");
        acute.put("u", "\u00fa");
        acute.put("U", "\u00da");
        acute.put("y", "\u00fd");
        acute.put("Y", "\u00dd");
    }

    private static void initGraveLetters() {
        grave = new HashMap<>();
        grave.put("a", "\u00e0");
        grave.put("A", "\u00c0");
        grave.put("e", "\u00e8");
        grave.put("E", "\u00C8");
        grave.put("i", "\u00ec");
        grave.put("I", "\u00cc");
        grave.put("o", "\u00f2");
        grave.put("O", "\u00d2");
        grave.put("u", "\u00f9");
        grave.put("U", "\u00d9");
    }

    private static void initCaronLetters() {
        caron = new HashMap<>();
        caron.put("c", "\u010d");
        caron.put("C", "\u010c");
        caron.put("d", "\u010F");
        caron.put("D", "\u010e");
        caron.put("e", "\u011b");
        caron.put("E", "\u011A");
        caron.put("l", "\u013E");
        caron.put("L", "\u013D");
        caron.put("n", "\u0148");
        caron.put("N", "\u0147");
        caron.put("r", "\u0159");
        caron.put("R", "\u0158");
        caron.put("s", "\u0161");
        caron.put("S", "\u0160");
        caron.put("t", "\u0165");
        caron.put("T", "\u0164");
        caron.put("z", "\u017e");
        caron.put("Z", "\u017d");
    }

    private static void initCircumflexLetters() {
        circumflex = new HashMap<>();
        circumflex.put("a", "\u00e2");
        circumflex.put("A", "\u00c2");
        circumflex.put("e", "\u00ea");
        circumflex.put("E", "\u00Ca");
        circumflex.put("i", "\u00ee");
        circumflex.put("I", "\u00ce");
        circumflex.put("o", "\u00f4");
        circumflex.put("O", "\u00d4");
        circumflex.put("u", "\u00fb");
        circumflex.put("U", "\u00db");
    }

    /**
     * @param letter letter
     * @return the acute version of letter if it exists, null otherwise.
     */
    public String getAcuteLetter(String letter) {
        return acute.get(letter);
    }

    /**
     * @param letter letter
     * @return the grave version of letter if it exists, null otherwise.
     */
    public String getGraveAccent(String letter) {
        return grave.get(letter);
    }

    /**
     * @param letter letter
     * @return the caron version of letter if it exists, null otherwise.
     */
    public String getCaronLetter(String letter) {
        return caron.get(letter);
    }

    /**
     * @param letter letter
     * @return the circumflex version of letter if it exists, null otherwise.
     */
    public String getCircumflexLetter(String letter) {
        return circumflex.get(letter);
    }
}
