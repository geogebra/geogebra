package org.geogebra.common.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.util.Language;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.Unicode;
import org.geogebra.common.util.debug.Log;

public abstract class Localization implements KeyboardLocale {

	/** CAS syntax suffix for keys in command bundle */
	public final static String syntaxCAS = ".SyntaxCAS";
	/** 3D syntax suffix for keys in command bundle */
	public final static String syntax3D = ".Syntax3D";
	/** syntax suffix for keys in command bundle */
	public final static String syntaxStr = ".Syntax";
	/** used when a secondary language is being used for tooltips. */
	protected boolean tooltipFlag = false;
	private String[] fontSizeStrings = null;

	protected Locale currentLocale = Locale.ENGLISH;

	// Giac works to 13 sig digits (for "double" calculations)
	private int maxFigures = 15;
	private int dimension = 2;

	public Localization(int dimension, int maxFigures) {
		this.dimension = dimension;
		this.maxFigures = maxFigures;
	}

	/**
	 * @return localized strings describing font sizes (very small, smaall, ...)
	 */
	public String[] getFontSizeStrings() {
		if (fontSizeStrings == null) {
			fontSizeStrings = new String[] { getPlain("ExtraSmall"),
					getPlain("VerySmall"), getPlain("Small"),
					getPlain("Medium"), getPlain("Large"),
					getPlain("VeryLarge"), getPlain("ExtraLarge") };
		}

		return fontSizeStrings;
	}

	private boolean reverseNameDescription = false;

	/**
	 * For Basque and Hungarian you have to say "A point" instead of "point A"
	 * 
	 * @return whether current alnguage needs revverse order of type and name
	 */
	final public boolean isReverseNameDescriptionLanguage() {
		// for Basque and Hungarian
		return reverseNameDescription;
	}

	// For Hebrew and Arabic. Guy Hed, 25.8.2008
	public boolean rightToLeftReadingOrder = false;

	/**
	 * @return whether current language uses RTL orientation
	 */
	final public boolean isRightToLeftReadingOrder() {
		return rightToLeftReadingOrder;
	}

	/** decimal point (different in eg Arabic) */
	public char unicodeDecimalPoint = '.';
	/** comma (different in Arabic) */
	public char unicodeComma = ','; // \u060c for Arabic comma
	/** zero (different in eg Arabic) */
	public char unicodeZero = '0';

	/**
	 * Text fixer for the Hungarian language
	 * 
	 * @param text
	 *            the translation text to fix
	 * @return the fixed text
	 * @author Zoltan Kovacs <zoltan@geogebra.org>
	 */

	private static String translationFixHu(String inputText) {
		String text = inputText;
		// Fixing affixes.

		// We assume that object names are usual object names like "P", "O_1"
		// etc.
		// FIXME: This will not work for longer object names, e.g. "X Triangle",
		// "mypoint". To solve this problem, we should check the whole word and
		// its vowels. Probably hunspell for JNA could help (but it can be
		// too big solution for us), http://dren.dk/hunspell.html.
		// TODO: The used method is not as fast as it could be, so speedup is
		// possible.
		String[] affixesList = { "-ra/-re", "-nak/-nek", "-ba/-be",
				"-ban/-ben", "-hoz/-hez", "-val/-vel" };
		String[] endE2 = { "10", "40", "50", "70", "90" };
		// FIXME: Numbers in endings which greater than 999 are not supported
		// yet.
		// Special endings for -val/-vel:
		String[] endO2 = { "00", "20", "30", "60", "80" };

		for (String affix : affixesList) {
			int match;
			do {
				match = text.indexOf(affix);
				// match > 0 can be assumed because an affix will not start the
				// text
				if ((match > -1) && (match > 0)) {
					// Affix found. Get the previous character.
					String prevChars = translationFixPronouncedPrevChars(text,
							match, 1);
					if (Unicode.translationFixHu_endE1.indexOf(prevChars) > -1) {
						text = translationFixHuAffixChange(text, match, affix,
								"e", prevChars);
					} else if (Unicode.translationFixHu_endO1
							.indexOf(prevChars) > -1) {
						text = translationFixHuAffixChange(text, match, affix,
								"o", prevChars);
					} else if (Unicode.translationFixHu_endOE1
							.indexOf(prevChars) > -1) {
						text = translationFixHuAffixChange(text, match, affix,
								Unicode.translationFixHu_oe, prevChars);
					} else if (match > 1) {
						// Append the previous character.
						// TODO: This could be quicker: to add only the second
						// char beyond prevChars
						prevChars = translationFixPronouncedPrevChars(text,
								match, 2);
						boolean found2 = false;
						for (String last2fit : endE2) {
							if (!found2 && last2fit.equals(prevChars)) {
								text = translationFixHuAffixChange(text, match,
										affix, "e", prevChars);
								found2 = true;
							}
						}

						// Special check for preparing -val/-vel:
						if (!found2) {
							for (String last2fit : endO2) {
								if (!found2 && last2fit.equals(prevChars)) {
									text = translationFixHuAffixChange(text,
											match, affix, "o", prevChars);
									found2 = true;
								}
							}
						}

						if (!found2) {
							// Use heuristics:
							text = translationFixHuAffixChange(text, match,
									affix, "o", prevChars);
						}

					} else {
						// Use heuristics:
						text = translationFixHuAffixChange(text, match, affix,
								"o", prevChars);
					}
				}
			} while (match > -1);
		}

		// Fixing definite article.
		String[] articlesList = { "a(z)", "A(z)" }; // assume they are 3 chars
													// long
		for (String article : articlesList) {
			int match;
			do {
				match = text.indexOf(article);
				if (match > -1) {
					// Article found. Get the next character.
					if (match < text.length() - 5) {
						char checked = Character.toLowerCase(text
								.charAt(match + 5));
						String consonants = "bcdfghjklmnpqrstvwx2346789";
						int match2 = consonants.indexOf(checked);
						String first = text.substring(0, match + 1);
						String last = text.substring(match + 4);
						if (match2 > -1) {
							// removing "(z)" if the next word starts with a
							// consonant
							text = first + last;
						} else {
							// removing "()" otherwise, using "z" instead (next
							// word starts with a vowel)
							text = first + "z" + last;
						}
					}
				}
			} while (match > -1);
		}

		return text;
	}

	/**
	 * Gets the previous "pronounced" characters from text before the match
	 * position for the given length. The returned text will be lowercased.
	 * 
	 * Example: translationFixPrevChars("ABC_{123}", 8, 4) gives "c123"
	 * 
	 * @param text
	 *            the text to pronounce
	 * @param match
	 *            starting position
	 * @param length
	 *            required length for the output
	 * @return lowercased output
	 */
	private static String translationFixPronouncedPrevChars(String text,
			int match, int length) {
		int pos = match;
		String rettext = "";
		int rettextlen = 0;
		String thisChar;
		String ignoredChars = "_{}";

		while ((rettextlen < length) && (pos > 0)) {
			thisChar = text.substring(pos - 1, pos);
			if (ignoredChars.indexOf(thisChar) == -1) {
				rettext = thisChar.toLowerCase() + rettext;
				rettextlen++;
			}
			pos--;
		}
		return rettext;
	}

	/**
	 * Changes a set of possible affixes to the right one
	 * 
	 * @param text
	 *            the text to be corrected
	 * @param match
	 *            starting position of possible change
	 * @param affixes
	 *            possible affixes to change
	 * @param affixForm
	 *            abbreviation for the change type ("o"/"a"/"e")
	 * @param prevChars
	 * @return the corrected text
	 */
	private static String translationFixHuAffixChange(String inputText,
			int match, String affixes, String affixForm, String prevChars) {
		String text = inputText;
		String replace = "";

		if ("-ra/-re".equals(affixes)) {
			if ("a".equals(affixForm) || "o".equals(affixForm)) {
				replace = "ra";
			} else {
				replace = "re";
			}
		} else if ("-nak/-nek".equals(affixes)) {
			if ("a".equals(affixForm) || "o".equals(affixForm)) {
				replace = "nak";
			} else {
				replace = "nek";
			}
		} else if ("-ba/-be".equals(affixes)) {
			if ("a".equals(affixForm) || "o".equals(affixForm)) {
				replace = "ba";
			} else {
				replace = "be";
			}
		} else if ("-ban/-ben".equals(affixes)) {
			if ("a".equals(affixForm) || "o".equals(affixForm)) {
				replace = "ban";
			} else {
				replace = "ben";
			}
		} else if ("-hoz/-hez".equals(affixes)) {
			if ("a".equals(affixForm) || "o".equals(affixForm)) {
				replace = "hoz";
			} else if ("e".equals(affixForm)) {
				replace = "hez";
			} else {
				replace = Unicode.translationFixHu_hoez;
			}
		} else if ("-val/-vel".equals(affixes)) {
			if ("a".equals(affixForm) || "o".equals(affixForm)) {
				replace = "val";
			} else {
				replace = "vel";
			}

			// Handling some special cases:
			if (prevChars.length() == 1) {
				// f-fel, l-lel etc.
				String sameChars = "flmnrs";
				// y-nal, 3-mal etc.
				String valVelFrom = sameChars + "y356789";
				String valVelTo = sameChars + "nmtttcc";
				int index = valVelFrom.indexOf(prevChars);
				if (index > -1) {
					replace = valVelTo.charAt(index) + replace.substring(1);
				} else {
					// x-szel, 1-gyel etc.
					String valVelFrom2 = "x14";
					String[] valVelTo2 = { "sz", "gy", "gy" };
					index = valVelFrom2.indexOf(prevChars);
					if (index > -1) {
						replace = valVelTo2[index] + replace.substring(1);
					}
				}
			} else if ((prevChars.length() == 2)
					&& prevChars.substring(1).equals("0")) {
				// (Currently the second part of the conditional is
				// unnecessary.)
				// 00-zal, 10-zel, 30-cal etc.
				// FIXME: A_{00}-val will be replaced to A_{00}-zal currently,
				// because we silently assume that 00 is preceeded by another
				// number.
				String valVelFrom = "013456789";
				String valVelTo = "zzcnnnnnn";
				int index = valVelFrom.indexOf(prevChars.charAt(0));
				if (index > -1) {
					replace = valVelTo.charAt(index) + replace.substring(1);
				} else {
					// 20-szal
					if (prevChars.charAt(0) == '2') {
						replace = "sz" + replace.substring(1);
					}
				}
			}
		}

		if ("".equals(replace)) {
			// No replace.
			return text;
		}
		int affixesLength = affixes.length();
		// Replace.
		text = text.substring(0, match) + "-" + replace
				+ text.substring(match + affixesLength);
		return text;
	}

	/**
	 * Gets translation from "command" bundle
	 * 
	 * @param key
	 *            key
	 * @return translation of given key
	 */

	public abstract String getCommand(String key);

	/**
	 * Gets translation from "plain" bundle
	 * 
	 * @param key
	 *            key
	 * @return translation of given key
	 */

	public final String getPlain(String key) {
		return getMenu(key);
	}

	/**
	 * Returns translation of given key from the "menu" bundle
	 * 
	 * @param key
	 *            key
	 * @return translation for key
	 */

	public abstract String getMenu(String key);

	/**
	 * Returns translation of given key from the "error" bundle
	 * 
	 * @param key
	 *            key
	 * @return translation for key
	 */

	public abstract String getError(String key);

	/**
	 * Returns translation of given key from the "symbol" bundle
	 * 
	 * @param key
	 *            key (either "S.1", "S.2", ... for symbols or "T.1", "T.2" ...
	 *            for tooltips)
	 * @return translation for key
	 */
	public abstract String getSymbol(int key);

	/**
	 * @param colorName
	 *            localized color name
	 * @return internal color name
	 */
	public abstract String reverseGetColor(String colorName);

	/**
	 * Returns translation of a key in colors bundle
	 * 
	 * @param key
	 *            key (color name)
	 * @return localized color name
	 */
	public abstract String getColor(String key);


	/**
	 * Translates the key and replaces "%0" by args[0], "%1" by args[1], etc
	 * 
	 * @version 2008-09-18
	 * @author Michael Borcherds, Markus Hohenwarter
	 * @param key
	 *            key
	 * @param args
	 *            arguments for replacement
	 * @return translated key with replaced %*s
	 */
	final public String getPlainArray(String key, String[] args) {
		String str = getPlain(key);
		StringBuilder sbPlain = new StringBuilder();
		sbPlain.setLength(0);
		boolean found = false;
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (ch == '%') {
				// get number after %
				i++;
				int pos = str.charAt(i) - '0';
				if ((pos >= 0) && (pos < args.length)) {
					// success
					sbPlain.append(args[pos]);
					found = true;
				} else {
					// failed
					sbPlain.append(ch);
				}
			} else {
				sbPlain.append(ch);
			}
		}

		if (!found) {
			/*
			 * If no parameters were found in key, this key is missing for some
			 * reason (maybe it is not added to the ggbtrans database yet). In
			 * this case all parameters are appended to the displayed string to
			 * help the developers.
			 */
			for (String arg : args) {
				sbPlain.append(" ");
				sbPlain.append(arg);
			}
		}

		// In some languages we may need some final fixes:
		return translationFix(sbPlain.toString());
	}

	/**
	 * 
	 * 
	 * only letters, numbers and _ allowed in label names check for other
	 * characters in the properties, and remove them
	 * 
	 * @param key
	 *            eg "poly" -> "Name.poly" -> poly -> poly1 as a label
	 * @return "poly" (the suffix is added later)
	 */
	final public String getPlainLabel(String key) {

		String ret = getPlain("Name." + key);

		for (int i = ret.length() - 1; i >= 0; i--) {
			if (!StringUtil.isLetterOrDigitOrUnderscore(ret.charAt(i))) {

				Log.warn("Bad character in key: " + key + "=" + ret);

				// remove bad character
				ret = ret.substring(0, i) + ret.substring(i + 1);

			}
		}

		return ret;

	}

	/** replace "%0" by arg0 */
	final public String getPlain(String key, String... arg0) {
		return getPlainArray(key, arg0);
	}



	/**
	 * 
	 * @return 2 letter language name, eg "en"
	 */
	public String getLanguage() {
		return getLanguage(getLocale());
	}

	/**
	 * @param lang
	 *            two letter language name
	 * @return whether we are currently using given language
	 */
	public boolean languageIs(String lang) {
		return getLanguage().equals(lang);
	}

	/**
	 * In some languages, a properties file cannot completely describe
	 * translations. This method tries to rewrite a text to the correct form.
	 * 
	 * @param text
	 *            the translation text to fix
	 * @return text the fixed text
	 * @author Zoltan Kovacs <zoltan@geogebra.org>
	 */
	public String translationFix(String text) {
		// Currently no other language is supported than Hungarian.
		String lang = getLanguage();
		if (!("hu".equals(lang))) {
			return text;
		}
		return translationFixHu(text);
	}

	private StringBuilder sbOrdinal = new StringBuilder();

	/**
	 * given 1, return eg 1st, 1e, 1:e according to the language
	 * 
	 * http://en.wikipedia.org/wiki/Ordinal_indicator
	 * 
	 * @param n
	 *            number
	 * @return corresponding ordinal number
	 */
	public String getOrdinalNumber(int n) {
		String lang = getLanguage();

		if ("en".equals(lang))
			return getOrdinalNumberEn(n);

		// check here for languages where 1st = 1
		if ("pt".equals(lang) || "ar".equals(lang) || "cy".equals(lang)
				|| "fa".equals(lang) || "ja".equals(lang) || "ko".equals(lang)
				|| "lt".equals(lang) || "mr".equals(lang) || "ms".equals(lang)
				|| "nl".equals(lang) || "si".equals(lang) || "th".equals(lang)
				|| "vi".equals(lang) || "zh".equals(lang)) {
			return n + "";
		}

		if (sbOrdinal == null) {
			sbOrdinal = new StringBuilder();
		} else {
			sbOrdinal.setLength(0);
		}

		// prefixes
		if ("in".equals(lang)) {
			sbOrdinal.append("ke-");
		} else if ("iw".equals(lang)) {
			// prefix and postfix for Hebrew
			sbOrdinal.append("\u200f\u200e");
		}

		sbOrdinal.append(n);

		if ("cs".equals(lang) || "da".equals(lang) || "et".equals(lang)
				|| "eu".equals(lang) || "hr".equals(lang) || "hu".equals(lang)
				|| "is".equals(lang) || "no".equals(lang) || "sk".equals(lang)
				|| "sr".equals(lang) || "tr".equals(lang)) {
			sbOrdinal.append('.');
		} else if ("de".equals(lang)) {
			sbOrdinal.append("th");
		} else if ("fi".equals(lang)) {
			sbOrdinal.append(":s");
		} else if ("el".equals(lang)) {
			sbOrdinal.append('\u03b7');
		} else if ("ro".equals(lang) || "es".equals(lang) || "it".equals(lang)
				|| "pt".equals(lang)) {
			sbOrdinal.append(Unicode.FEMININE_ORDINAL_INDICATOR);
		} else if ("bs".equals(lang) || "sl".equals(lang)) {
			sbOrdinal.append("-ti");
		} else if ("ca".equals(lang)) {

			switch (n) {
			// Catalan (masculine)
			case 0:
				break; // just "0", not "0e" etc
			case 1:
				sbOrdinal.append("r");
				break;
			case 2:
				sbOrdinal.append("n");
				break;
			case 3:
				sbOrdinal.append("r");
				break;
			case 4:
				sbOrdinal.append("t");
				break;
			default:
				sbOrdinal.append("e");
				break;
			}

		} else if ("sq".equals(lang)) {
			sbOrdinal.append("-te");
		} else if ("gl".equals(lang)) {
			sbOrdinal.append("ava");
		} else if ("mk".equals(lang)) {
			sbOrdinal.append("-\u0442\u0438");
		} else if ("ka".equals(lang)) {
			sbOrdinal.append("-\u10d4");
		} else if ("iw".equals(lang)) {
			sbOrdinal.append("\u200e\u200f");
		} else if ("ru".equals(lang) || "uk".equals(lang)) {
			sbOrdinal.append("-\u0433\u043e");
		} else if ("fr".equals(lang)) {
			if (n == 1) {
				sbOrdinal.append("er"); // could also be "re" for feminine...
			} else {
				sbOrdinal.append("e"); // could also be "es" for plural...
			}
		} else if ("sv".equals(lang)) {
			int unitsDigit = n % 10;
			if ((unitsDigit == 1) || (unitsDigit == 2)) {
				sbOrdinal.append(":a");
			} else {
				sbOrdinal.append(":e");
			}
		}

		return sbOrdinal.toString();

	}

	/**
	 * given 1, return eg 1st (English only)
	 * 
	 * http://en.wikipedia.org/wiki/Ordinal_indicator
	 * 
	 * @param n
	 *            number
	 * @return english ordinal number
	 */
	public String getOrdinalNumberEn(int n) {
		/*
		 * http://en.wikipedia.org/wiki/Names_of_numbers_in_English If the tens
		 * digit of a number is 1, then write "th" after the number. For
		 * example: 13th, 19th, 112th, 9,311th. If the tens digit is not equal
		 * to 1, then use the following table: If the units digit is: 0 1 2 3 4
		 * 5 6 7 8 9 write this after the number th st nd rd th th th th th th
		 */

		int tensDigit = (n / 10) % 10;

		if (tensDigit == 1) {
			return n + "th";
		}

		int unitsDigit = n % 10;

		switch (unitsDigit) {
		case 1:
			return n + "st";
		case 2:
			return n + "nd";
		case 3:
			return n + "rd";
		default:
			return n + "th";
		}

	}

	/**
	 * @return rounding menu items
	 */
	public String[] getRoundingMenu() {
		String[] strDecimalSpaces = {
				getPlain("ADecimalPlaces", "0"),
				getPlain("ADecimalPlace", "1"),
				getPlain("ADecimalPlaces", "2"),
				getPlain("ADecimalPlaces", "3"),
				getPlain("ADecimalPlaces", "4"),
				getPlain("ADecimalPlaces", "5"),
				getPlain("ADecimalPlaces", "10"),
				getPlain("ADecimalPlaces", "15"),
				"---", // separator
				getPlain("ASignificantFigures", "3"),
				getPlain("ASignificantFigures", "5"),
				getPlain("ASignificantFigures", "10"),
				getPlain("ASignificantFigures", maxFigures + "") };

		// zero is singular in eg French
		if (!isZeroPlural(getLanguage())) {
			strDecimalSpaces[0] = getPlain("ADecimalPlace", "0");
		}

		return strDecimalSpaces;
	}

	/**
	 * in French, zero is singular, eg 0 dcimale rather than 0 decimal places
	 * 
	 * @param lang
	 *            language code
	 * @return whether 0 is plural
	 */
	public boolean isZeroPlural(String lang) {
		if (lang.startsWith("fr")) {
			return false;
		}
		return true;
	}

	private boolean isAutoCompletePossible = true;

	/**
	 * Returns whether autocomplete should be used at all. Certain languages
	 * make problems with auto complete turned on (e.g. Korean).
	 * 
	 * @return whether autocomplete should be used at all, depending on language
	 */
	final public boolean isAutoCompletePossible() {
		return isAutoCompletePossible;
	}

	// For Persian and Arabic.
	private boolean rightToLeftDigits = false;

	/**
	 * Returns whether current language uses RTL orientation for numbers for
	 * given template. We don't want RTL digits in XML
	 * 
	 * @param tpl
	 *            string templates
	 * @return whether current language uses RTL orientation for numbers for
	 *         given template
	 */
	final public boolean isRightToLeftDigits(StringTemplate tpl) {
		if (!tpl.internationalizeDigits()) {
			return false;
		}
		return rightToLeftDigits;
	}

	/**
	 * Use localized digits.
	 */
	private boolean useLocalizedDigits = false;

	/**
	 * @return If localized digits are used for certain languages (Arabic,
	 *         Hebrew, etc).
	 */
	public boolean isUsingLocalizedDigits() {
		return useLocalizedDigits;
	}

	/**
	 * Updates language flags (RTL, RTL for numbers, reverse word order,
	 * autocomplete possible)
	 * 
	 * @param lang
	 *            language
	 */
	public void updateLanguageFlags(String lang) {

		rightToLeftReadingOrder = rightToLeftReadingOrder(lang);

		// force update
		fontSizeStrings = null;

		// reverseLanguage = "zh".equals(lang); removed Michael Borcherds
		// 2008-03-31
		reverseNameDescription = "eu".equals(lang) || "hu".equals(lang);

		// used for eg axes labels
		// Arabic digits are RTL
		// Persian aren't http://persian.nmelrc.org/persianword/format.htm
		rightToLeftDigits = "ar".equals(lang);

		// Another option:
		// rightToLeftReadingOrder =
		// (Character.getDirectionality(getPlain("Algebra").charAt(1)) ==
		// Character.DIRECTIONALITY_RIGHT_TO_LEFT);

		// turn off auto-complete for Korean
		isAutoCompletePossible = true;// !"ko".equals(lang);

		// defaults
		unicodeDecimalPoint = '.';
		unicodeComma = ',';
		// unicodeThousandsSeparator=',';

		if (isUsingLocalizedDigits()) {
			if (lang.startsWith("ar")) { // Arabic
				unicodeZero = '\u0660'; // Arabic-Indic digit 0
				unicodeDecimalPoint = Unicode.ArabicComma; // Arabic-Indic
															// decimal point
				unicodeComma = '\u060c'; // Arabic comma
				// unicodeThousandsSeparator = '\u066c'; // Arabic Thousands
				// separator
			} else if (lang.startsWith("fa")) { // Persian
				unicodeZero = '\u06f0'; // Persian digit 0 (Extended
				// Arabic-Indic)
				unicodeDecimalPoint = Unicode.ArabicComma; // Arabic comma
				unicodeComma = '\u060c'; // Arabic-Indic decimal point
				// unicodeThousandsSeparator = '\u066c'; // Arabic Thousands
				// separators
			} else if (lang.startsWith("ml")) {
				unicodeZero = '\u0d66'; // Malayalam digit 0
			} else if (lang.startsWith("th")) {
				unicodeZero = '\u0e50'; // Thai digit 0
			} else if (lang.startsWith("ta")) {
				unicodeZero = '\u0be6'; // Tamil digit 0
			} else if (lang.startsWith("sd")) {
				unicodeZero = '\u1bb0'; // Sudanese digit 0
			} else if (lang.startsWith("kh")) {
				unicodeZero = '\u17e0'; // Khmer digit 0
			} else if (lang.startsWith("mn")) {
				unicodeZero = '\u1810'; // Mongolian digit 0
			} else if (lang.startsWith("mm")) {
				unicodeZero = '\u1040'; // Mayanmar digit 0
			} else {
				unicodeZero = '0';
			}
		} else {
			unicodeZero = '0';
		}
	}

	/**
	 * @param language
	 *            language string
	 * @return whether to use LTR
	 */
	public static boolean rightToLeftReadingOrder(String language) {

		String lang = language.substring(0, 2);
		// Guy Hed, 25.8.2008
		// Guy Hed, 26.4.2009 - added Yiddish and Persian as RTL languages
		return ("iw".equals(lang) || "ar".equals(lang) || "fa".equals(lang)
				|| "ji".equals(lang) || "he".equals(lang) || "ug".equals(lang));
	}

	/**
	 * @param key
	 *            command name
	 * @return command syntax TODO check whether getSyntaxString works here
	 */
	public String getCommandSyntax(String key) {
		String command = getCommand(key);
		if (dimension == 3) {
			String key3D = key + Localization.syntax3D;
			String cmdSyntax3D = getCommand(key3D);
			if (!cmdSyntax3D.equals(key3D)) {
				cmdSyntax3D = cmdSyntax3D.replace("[", command + '[');
				return cmdSyntax3D;
			}
		}

		String syntaxString = Localization.syntaxStr;

		String syntax = null;

		if (syntaxString != null) {

			syntax = getCommand(key + syntaxString);

			syntax = syntax.replace("[", command + '[');
		}

		return syntax;
	}

	/**
	 * Use localized labels.
	 */
	private boolean useLocalizedLabels = true;

	/**
	 * @return If localized labels are used for certain languages.
	 */
	public boolean isUsingLocalizedLabels() {
		return useLocalizedLabels;
	}

	/**
	 * Use localized labels for certain languages.
	 * 
	 * @param useLocalizedLabels
	 *            true to make labels of new geos localized
	 */
	public void setUseLocalizedLabels(boolean useLocalizedLabels) {
		this.useLocalizedLabels = useLocalizedLabels;
	}

	/**
	 * Use localized digits for certain languages (Arabic, Hebrew, etc).
	 * 
	 * Calls {@link #updateLanguageFlags(String)} to apply the change, but just
	 * if the new flag differs from the current.
	 * 
	 * @param useLocalizedDigits
	 *            whether localized digits should be used
	 * @param app
	 *            used for callback (update construction)
	 */
	public void setUseLocalizedDigits(boolean useLocalizedDigits, App app) {
		if (this.useLocalizedDigits == useLocalizedDigits) {
			return;
		}

		this.useLocalizedDigits = useLocalizedDigits;
		updateLanguageFlags(getLanguage());
		app.getKernel().updateConstruction();
		app.setUnsaved();

		if (app.getEuclidianView1() != null) {
			app.getEuclidianView1().updateBackground();
		}
	}

	/**
	 * Returns translation of given key from the "symbol" bundle in tooltip
	 * language
	 * 
	 * @param key
	 *            key (either "S.1", "S.2", ... for symbols or "T.1", "T.2" ...
	 *            for tooltips)
	 * @return translation for key in tooltip language
	 */
	public abstract String getSymbolTooltip(int key);

	/**
	 * @param key
	 *            command name
	 * @return CAS syntax
	 */
	public String getCommandSyntaxCAS(String key) {

		String keyCAS = key + syntaxCAS;

		String command = getCommand(key);
		String syntax = getCommand(keyCAS);

		// make sure "PointList.SyntaxCAS" not displayed in dialog
		if (syntax.equals(keyCAS)) {
			syntax = getCommand(key + syntaxStr);
		}

		syntax = syntax.replace("[", command + '[');

		return syntax;
	}

	/**
	 * 
	 * @param key
	 *            (internal) command name to check
	 * @return true if this command has a CAS-specific syntax
	 */
	public boolean isCASCommand(String key) {

		String keyCAS = key + syntaxCAS;

		String syntax = getCommand(keyCAS);

		if (syntax.equals(keyCAS)) {
			return false;
		}

		return true;
	}

	/**
	 * can be over-ridden if required to provide tooltips in another language
	 * 
	 * @param string
	 *            key
	 * @return translation of key from menu bundle in tooltip language
	 */
	public String getMenuTooltip(String key) {

		return getMenu(key);
	}

	/**
	 * @param string
	 *            key
	 * @return translation of key from plain bundle in tooltip language
	 */
	public final String getPlainTooltip(String string) {
		return getMenuTooltip(string);
	}

	/**
	 * Initialize the command bundle (not needed in Web)
	 */
	public abstract void initCommand();

	/**
	 * used to force properties to be read from secondary (tooltip) language if
	 * one has been selected
	 */
	public abstract void setTooltipFlag();

	/**
	 * Stop forcing usage of tooltip locale for translations
	 */
	public void clearTooltipFlag() {
		tooltipFlag = false;
	}

	/**
	 * @return tooltip language (or null where not supported)
	 */
	public String getTooltipLanguageString() {
		return null;
	}

	/**
	 * @return whether language of command bundle changed since we last updated
	 *         translation table and directories
	 */
	protected abstract boolean isCommandChanged();

	/**
	 * @param b
	 *            whether language of command bundle changed since we last
	 *            updated translation table and directories
	 */
	protected abstract void setCommandChanged(boolean b);

	/**
	 * @return whether command translation bundle is null
	 */
	protected abstract boolean isCommandNull();

	/**
	 * turns eg Function.sin into "sin" or (in Spanish) "sen"
	 * 
	 * guaranteed to remove the "Function." from the start even if a key doesn't
	 * exist (or isn't loaded)
	 * 
	 */
	public String getFunction(String key) {
		String ret = getPlain("Function." + key);

		// make sure we don't get strange function names if the properties
		// aren't loaded
		if (ret.startsWith("Function.")) {
			return ret.replace("Function.", "");
		}

		return ret;
	}

	public String getLocaleStr() {
		return getLocale().toString();
	}

	public Locale getLocale() {
		return currentLocale;
	}

	public int getRightAngleStyle() {
		return Language.getRightAngleStyle(getLanguage());
	}

	private HashMap<String, String> translateCommandTable;
	public String getReverseCommand(String command) {
		if (command == null) {
			return null;
		}
		String key = StringUtil.toLowerCase(command);

		String ret = translateCommandTable == null ? key
				: translateCommandTable.get(key);
		if (ret != null)
			return ret;
		// if that fails check internal commands
		for (Commands c : Commands.values()) {
			if (StringUtil.toLowerCase(c.name()).equals(key)) {
				return Commands.englishToInternal(c).name();
			}
		}
		return null;
	}

	public void initTranslateCommand() {
		if (translateCommandTable == null) {
			translateCommandTable = new HashMap<String, String>();
		}
		translateCommandTable.clear();

	}

	public HashMap<String, String> getTranslateCommandTable() {
		return translateCommandTable;
	}

	public String getKeyboardRow(int row) {
		return getPlain("Keyboard.row" + row);
	}

	abstract protected ArrayList<Locale> getSupportedLocales();

	/**
	 * Returns a locale object that has the same country and/or language as
	 * locale. If the language of locale is not supported an English locale is
	 * returned.
	 */
	protected Locale getClosestSupportedLocale(Locale locale) {
		int size = getSupportedLocales().size();

		// try to find country and and language
		String country = getCountry(locale);
		String language = getLanguage(locale);

		if (country.length() > 0) {
			for (int i = 0; i < size; i++) {
				Locale loc = getSupportedLocales().get(i);
				if (country.equals(getCountry(loc))
						&& language.equals(getLanguage(loc))) {
					// found supported country locale
					return loc;
				}
			}
		}

		// try to find only language
		for (int i = 0; i < size; i++) {
			Locale loc = getSupportedLocales().get(i);
			if (language.equals(getLanguage(loc))) {
				// found supported country locale
				return loc;
			}
		}

		// we didn't find a matching country or language,
		// so we take English
		return Locale.ENGLISH;
	}

	public void setLocale(Locale locale) {
		currentLocale = getClosestSupportedLocale(locale);
		updateResourceBundles();
	}

	abstract protected void updateResourceBundles();

	protected abstract String getLanguage(Locale locale);

	protected abstract String getCountry(Locale locale);
}
