package org.geogebra.common.kernel.geos;

import org.geogebra.editor.share.util.Greek;

/**
 * Labels for geoelements per type
 */
public class LabelType {

	/**
	 * Labels for points
	 */
	static final char[] pointLabels = { 'A', 'B', 'C', 'D', 'E', 'F',
            'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
            'T', 'U', 'V', 'W', 'Z' };

	/**
	 * Labels for functions
	 */
	static final char[] functionLabels = { 'f', 'g', 'h', 'p', 'q', 'r',
            's', 't' };

	/**
	 * Labels for lines
	 */
	static final char[] lineLabels = { 'f', 'g', 'h', 'i', 'j', 'k',
            'l', 'm', 'n', 'p', 'q', 'r', 's', 't', 'a', 'b', 'c', 'd', 'e' };

	/**
	 * Labels for vectors
	 */
	static final char[] vectorLabels = { 'u', 'v', 'w', 'a', 'b', 'c',
            'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'p', 'q',
            'r', 's', 't' };

	/**
	 * Labels for conics
	 */
	static final char[] conicLabels = { 'c', 'd', 'e', 'f', 'g', 'h',
            'k', 'p', 'q', 'r', 's', 't' };

	/**
	 * All lowercase labels
	 */
	static final char[] lowerCaseLabels = { 'a', 'b', 'c', 'd', 'e',
            'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w' };

	/**
	 * Labels for integer sliders
	 */
	static final char[] integerLabels = { 'n', 'i', 'j', 'k', 'l',
            'm', };

	/**
	 * Labels for angles in geometry
	 */
	static final char[] greekLowerCaseLabels = Greek.getGreekLowerCaseNoPi();

    // private static final char[] arabicOLD = { '\u0623', '\u0628', '\u062a',
    // '\u062b', '\u062c', '\u062d', '\u062e', '\u062f', '\u0630',
    // '\u0631', '\u0632', '\u0633', '\u0634', '\u0635', '\u0636',
    // '\u0637', '\u0638', '\u0639', '\u063a', '\u0641', '\u0642',
    // '\u0643', '\u0644', '\u0645', '\u0646', '\u0647', // needs this too
    // // '\u0640' (see
    // // later on)
    // '\u0648', '\u064a' };

	/**
	 * Arabic labels
	 */
	static final char[] arabic = { '\u0627', '\u0644', '\u0641',
            '\u0628', '\u062C', '\u062F',

			// needs this too '\u0640' (see LabelManager.getNextIndexedLabel())
            '\u0647',

            '\u0648', '\u0632',
            '\u062D', '\u0637', '\u06CC', '\u06A9', '\u0644', '\u0645',
            '\u0646', '\u0633', '\u0639', '\u0641', '\u0635', '\u0642',
            '\u0631', '\u0634', '\u062A', '\u062B', '\u062E', '\u0630',
            '\u0636', '\u0638', '\u063A', '\u0623', '\u0628', '\u062C',
            '\u062F' };

	/**
	 * Yiddish labels
	 */
	static final char[] yiddish = { '\u05D0', '\u05D1', '\u05D2',
            '\u05D3', '\u05D4', '\u05D5', '\u05D6', '\u05D7', '\u05D8',
            '\u05DB', '\u05DC', '\u05DE', '\u05E0', '\u05E1', '\u05E2',
            '\u05E4', '\u05E6', '\u05E7', '\u05E8', '\u05E9', '\u05EA' };

	/** default labels for planes */
	static final char[] planeLabels = { 'p', 'q', 'r' };
}
