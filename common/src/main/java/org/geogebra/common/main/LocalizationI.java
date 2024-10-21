package org.geogebra.common.main;

import java.util.Locale;

import com.himamis.retex.editor.share.util.Unicode;

public abstract class LocalizationI {

	/**
	 * eg Function.sin
	 */
	public final static String FUNCTION_PREFIX = "Function.";

	/**
	 * Gets the current locale as a well-formed BCP-47 language tag.
	 * @return current locale
	 * @see Locale#toLanguageTag() toLanguageTag
	 */
	public abstract String getLanguageTag();

	/**
	 * Gets translation from "command" bundle
	 * @param key key
	 * @return translation of given key
	 */
	public abstract String getCommand(String key);

	/**
	 * @param altText eg altText.RightArrow
	 * @return eg "Right Arrow"
	 */
	public String getAltText(String altText) {
		String ret = getMenu(altText);

		// just in case translations not loaded
		if (ret.contains("altText.")) {
			ret = ret.replace("altText.", "");
		}
		return ret;
	}

	/**
	 * @param key menu key
	 * @param default0 return this if lookup failed
	 * @return translation of key
	 */
	public String getMenuDefault(String key, String default0) {
		String ret = getMenu(key);

		if (ret == null || ret.equals(key)) {
			return default0;
		}

		return ret;
	}

	/**
	 * turns eg Function.sin into "sin" or (in Spanish) "sen"
	 *
	 * guaranteed to remove the "Function." from the start even if a key doesn't
	 * exist (or isn't loaded)
	 * @param key eg "sin"
	 * @return eg "sen"
	 */
	public String getFunction(String key) {
		return getFunction(key, true);
	}

	/**
	 * turns eg Function.sin into "sin" or (in Spanish) "sen"
	 *
	 * guaranteed to remove the "Function." from the start even if a key doesn't
	 * exist (or isn't loaded)
	 * @param key eg "sin"
	 * @param changeInverse if false return arcsen rather than sin^-1
	 * @return eg "sen"
	 */
	public String getFunction(String key, boolean changeInverse) {

		// change eg asin into sin^{-1}
		if (changeInverse && key.startsWith("a")) {
			switch (key) {
			case "asin":
				return getFunction("sin")
						+ Unicode.SUPERSCRIPT_MINUS_ONE_STRING;
			case "acos":
				return getFunction("cos")
						+ Unicode.SUPERSCRIPT_MINUS_ONE_STRING;
			case "atan":
				return getFunction("tan")
						+ Unicode.SUPERSCRIPT_MINUS_ONE_STRING;
			case "asinh":
				return getFunction("sinh")
						+ Unicode.SUPERSCRIPT_MINUS_ONE_STRING;
			case "acosh":
				return getFunction("cosh")
						+ Unicode.SUPERSCRIPT_MINUS_ONE_STRING;
			case "atanh":
				return getFunction("tanh")
						+ Unicode.SUPERSCRIPT_MINUS_ONE_STRING;
			}
		}

		String ret = getMenu(FUNCTION_PREFIX + key);

		// make sure we don't get strange function names if the properties
		// aren't loaded
		if (ret.startsWith(FUNCTION_PREFIX)) {
			return ret.substring(FUNCTION_PREFIX.length());
		}

		return ret;
	}

	/**
	 * Returns translation of given key from the "menu" bundle
	 * @param key key
	 * @return translation for key
	 */
	public abstract String getMenu(String key);

	/** @return true if the localized keyboard has latin characters. */
	public boolean isLatinKeyboard() {
		String middleRow = getKeyboardRow(2);
		int first = middleRow.codePointAt(0);
		return !(first < 0 || first > 0x00FF);
	}

	public String getKeyboardRow(int row) {
		return getMenu("Keyboard.row" + row);
	}

	/**
	 * During language switch the actual and preferred language can differ
	 * @return preferred language as a tag
	 */
	public String getPreferredLanguageTag() {
		return getLanguageTag();
	}
}
