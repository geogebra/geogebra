package geogebra.common.main;

import geogebra.common.util.StringUtil;
import geogebra.common.util.Unicode;

public abstract class Localization {
	public abstract boolean isReverseNameDescriptionLanguage();
	public abstract boolean isRightToLeftReadingOrder();
	public abstract boolean isUsingLocalizedLabels();
	
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

		for (String affixes : affixesList) {
			int match;
			do {
				match = text.indexOf(affixes);
				// match > 0 can be assumed because an affix will not start the
				// text
				if ((match > -1) && (match > 0)) {
					// Affix found. Get the previous character.
					String prevChars = translationFixPronouncedPrevChars(text,
							match, 1);
					if (Unicode.translationFixHu_endE1.indexOf(prevChars) > -1) {
						text = translationFixHuAffixChange(text, match,
								affixes, "e", prevChars);
					} else if (Unicode.translationFixHu_endO1
							.indexOf(prevChars) > -1) {
						text = translationFixHuAffixChange(text, match,
								affixes, "o", prevChars);
					} else if (Unicode.translationFixHu_endOE1
							.indexOf(prevChars) > -1) {
						text = translationFixHuAffixChange(text, match,
								affixes, Unicode.translationFixHu_oe, prevChars);
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
										affixes, "e", prevChars);
								found2 = true;
							}
						}

						// Special check for preparing -val/-vel:
						if (!found2) {
							for (String last2fit : endO2) {
								if (!found2 && last2fit.equals(prevChars)) {
									text = translationFixHuAffixChange(text,
											match, affixes, "o", prevChars);
									found2 = true;
								}
							}
						}

						if (!found2) {
							// Use heuristics:
							text = translationFixHuAffixChange(text, match,
									affixes, "o", prevChars);
						}

					} else {
						// Use heuristics:
						text = translationFixHuAffixChange(text, match,
								affixes, "o", prevChars);
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
	
	public abstract String getPlain(String key);

	

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

	StringBuilder sbPlain = new StringBuilder();
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
	final public String getPlain(String key, String[] args) {
		String str = getPlain(key);

		sbPlain.setLength(0);
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (ch == '%') {
				// get number after %
				i++;
				int pos = str.charAt(i) - '0';
				if ((pos >= 0) && (pos < args.length)) {
					// success
					sbPlain.append(args[pos]);
				} else {
					// failed
					sbPlain.append(ch);
				}
			} else {
				sbPlain.append(ch);
			}
		}

		return sbPlain.toString();
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

				App.warn("Bad character in key: " + key + "=" + ret);

				// remove bad character
				ret = ret.substring(0, i) + ret.substring(i + 1);

			}
		}

		return ret;

	}
	
	// Michael Borcherds 2008-03-25
		// replace "%0" by arg0
		final public String getPlain(String key, String arg0) {
			String[] ss = { arg0 };
			return getPlain(key, ss);
		}

		// Michael Borcherds 2008-03-25
		// replace "%0" by arg0, "%1" by arg1
		final public String getPlain(String key, String arg0, String arg1) {
			String[] ss = { arg0, arg1 };
			return getPlain(key, ss);
		}

		// Michael Borcherds 2008-03-30
		// replace "%0" by arg0, "%1" by arg1, "%2" by arg2
		final public String getPlain(String key, String arg0, String arg1,
				String arg2) {
			String[] ss = { arg0, arg1, arg2 };
			return getPlain(key, ss);
		}

		// Michael Borcherds 2008-03-30
		// replace "%0" by arg0, "%1" by arg1, "%2" by arg2, "%3" by arg3
		final public String getPlain(String key, String arg0, String arg1,
				String arg2, String arg3) {
			String[] ss = { arg0, arg1, arg2, arg3 };
			return getPlain(key, ss);
		}

		// Michael Borcherds 2008-03-30
		// replace "%0" by arg0, "%1" by arg1, "%2" by arg2, "%3" by arg3, "%4" by
		// arg4
		final public String getPlain(String key, String arg0, String arg1,
				String arg2, String arg3, String arg4) {
			String[] ss = { arg0, arg1, arg2, arg3, arg4 };
			return getPlain(key, ss);
		}
		
		/**
		 * 
		 * @return 2 letter language name, eg "en"
		 */
		public abstract String getLanguage();

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

	
}
