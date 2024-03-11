package org.geogebra.common.main;

import org.geogebra.common.util.lang.Language;

/**
 * A class designed to convert integers into their corresponding ordinal numbers, based on the
 * language that is being used
 */
public class OrdinalConverter {

	/**
	 * @param language Language
	 * @param number Integer
	 * @return The ordinal number that is used for the passed language and integer
	 */
	public static String getOrdinalNumber(Language language, int number) {
		switch (language) {
		case Bulgarian:
			return getOrdinalNumberForBulgarian(number);
		case Catalan:
		case Valencian:
			return getOrdinalNumberForCatalan(number);
		case French:
			return getOrdinalNumberForFrench(number);
		case English_Australia:
		case English_UK:
		case English_US:
			return getOrdinalNumberForEnglish(number);
		case Hebrew:
		case Yiddish:
			return getOrdinalNumberForHebrew(number);
		case Indonesian:
			return getOrdinalNumberForIndonesian(number);
		case Swedish:
			return getOrdinalNumberForSwedish(number);
		default:
			return number + "";
		}
	}

	/**
	 * Masculine form
	 * @param number Integer
	 * @return Ordinal number for the Bulgarian Language
	 */
	private static String getOrdinalNumberForBulgarian(int number) {
		switch (number % 10) {
		case 1:
			return number + "-\u0432\u0438";
		case 2:
			return number + "-\u0440\u0438";
		default:
			return number + "-\u0442\u0438";
		}
	}

	/**
	 * Masculine form
	 * @param number Integer
	 * @return Ordinal number for the Catalan language
	 */
	private static String getOrdinalNumberForCatalan(int number) {
		switch (number) {
		case 0:
			return number + "";
		case 1:
			return number + "r";
		case 2:
			return number + "n";
		case 3:
			return number + "r";
		case 4:
			return number + "t";
		default:
			return number + "e";
		}
	}

	/**
	 * <li>All numbers that end with 1 except those ending with 11 --> st</li>
	 * <li>All numbers that end with 2 except those ending with 12 --> nd</li>
	 * <li>All numbers that end with 3 except those ending with 13 --> rd</li>
	 * <li>All others --> th</li>
	 * @param number Integer
	 * @return Corresponding ordinal number for the English language
	 */
	private static String getOrdinalNumberForEnglish(int number) {
		int tensDigit = (number / 10) % 10;

		if (tensDigit == 1) {
			return number + "th";
		}

		int unitsDigit = number % 10;

		switch (unitsDigit) {
		case 1:
			return number + "st";
		case 2:
			return number + "nd";
		case 3:
			return number + "rd";
		default:
			return number + "th";
		}
	}

	private static String getOrdinalNumberForFrench(int number) {
		if (number == 1) {
			return number + "er";
		}
		return number + "e";
	}

	/**
	 * Prefix and Suffix
	 * @param number Integer
	 * @return Corresponding ordinal number for the Hebrew language
	 */
	private static String getOrdinalNumberForHebrew(int number) {
		return "\u200f\u200e" + number + "\u200e\u200f";
	}

	/**
	 * Prefix only
	 * @param number Integer
	 * @return Corresponding ordinal number
	 */
	private static String getOrdinalNumberForIndonesian(int number) {
		return "ke-" + number;
	}

	/**
	 * <li>All numbers that end with 1 or 2, except those ending with 11 and 12 --> :a</li>
	 * <li>All others --> :e</li>
	 * @param number Integer
	 * @return Corresponding ordinal number for the Swedish language
	 */
	private static String getOrdinalNumberForSwedish(int number) {
		int unitsDigit = number % 10;
		int lastTwoDigits = number % 100;

		if (lastTwoDigits == 11 || lastTwoDigits == 12 || (unitsDigit != 1 && unitsDigit != 2)) {
			return number + ":e";
		}
		return number + ":a";
	}

}
