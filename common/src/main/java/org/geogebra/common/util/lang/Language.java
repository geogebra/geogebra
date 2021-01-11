package org.geogebra.common.util.lang;

import java.util.Locale;

import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Collection of which languages are official in which countries (only includes
 * languages supported in GeoGebra)
 * 
 * @author michael@geogebra.org
 *         http://en.wikipedia.org/wiki/List_of_official_languages
 *         http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2
 */
@SuppressWarnings("javadoc")
public enum Language {

	// need to be in Alphabetical order so they appear in the menu in the right
	// order
	Afrikaans(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, null,
			false, "af", "Afrikaans", Script.LATIN),

	Albanian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, null, true,
			"sq", "Albanian / Gjuha Shqipe", Script.LATIN, ','),

	Amharic(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null,
			"\u134b\u12ed\u120d", false, "am",
			"Amharic / \u0041\u006d\u0061\u0072\u0259\u00f1\u00f1\u0061",
			Script.ETHIOPIAN),

	Arabic(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, null, true,
			"ar",
			Unicode.LEFT_TO_RIGHT_MARK + "Arabic" + Unicode.LEFT_TO_RIGHT_MARK
					+ " / " + Unicode.RIGHT_TO_LEFT_MARK
					+ "\u0627\u0644\u0639\u0631\u0628\u064A\u0629"
					+ Unicode.RIGHT_TO_LEFT_MARK,
			Script.ARABIC),

	Arabic_Morocco(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, null,
			true, "arMA", "ar",
			Unicode.LEFT_TO_RIGHT_MARK + "Arabic (Morocco)"
					+ Unicode.LEFT_TO_RIGHT_MARK + " / "
					+ Unicode.RIGHT_TO_LEFT_MARK
					+ "\u0627\u0644\u0639\u0631\u0628\u064A\u0629"
					+ " (\u0627\u0644\u0645\u063A\u0631\u0628)"
					+ Unicode.RIGHT_TO_LEFT_MARK,
			Script.ARABIC, ','),

	Arabic_Tunisia(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, null,
			true, "arTN", "ar",
			Unicode.LEFT_TO_RIGHT_MARK + "Arabic (Tunisia)"
					+ Unicode.LEFT_TO_RIGHT_MARK + " / "
					+ Unicode.RIGHT_TO_LEFT_MARK
					+ "\u0627\u0644\u0639\u0631\u0628\u064A\u0629 (\u062A\u0648\u0646\u0633)"
					+ Unicode.RIGHT_TO_LEFT_MARK,
			Script.ARABIC, ','),

	Arabic_Syria(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, null,
			false, "arSY", "ar",
			Unicode.LEFT_TO_RIGHT_MARK + "Arabic (Syria)" + Unicode.LEFT_TO_RIGHT_MARK
					+ " / " + Unicode.RIGHT_TO_LEFT_MARK
					+ "\u0627\u0644\u0639\u0631\u0628\u064A\u0629 (\u0633\u0648\u0631\u064A\u0627)"
					+ Unicode.RIGHT_TO_LEFT_MARK,
			Script.ARABIC, ','), // TODO assuming based on other arabic countries

	Armenian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, "\u0570",
			true, "hy",
			"Armenian / \u0540\u0561\u0575\u0565\u0580\u0565\u0576",
			Script.ARMENIAN, ','),

	Azerbaijani(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, null,
			false, "az", "Azerbaijani", Script.LATIN, ','),

	Basque(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", null, true, "eu", "eu",
			"Basque / Euskara", Script.LATIN, ','),

	Bengali(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", null, true, "bn",
			"Basque / Euskara", Script.LATIN),

	Bosnian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", null, true, "bs",
			"Bosnian / \u0431\u043E\u0441\u0430\u043D\u0441\u043A\u0438",
			Script.LATIN),

	Bulgarian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", null, true, "bg",
			"Bulgarian / \u0431\u044A\u043B\u0433\u0430\u0440\u0441\u043A\u0438"
					+ " \u0435\u0437\u0438\u043A",
			Script.CYRILLIC, ','),

	Catalan(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", null, true, "ca", "ca",
			"Catalan / Catal\u00E0", Script.LATIN, ','),

	Valencian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", null, true, "caXV", "ca",
			"Catalan / Catal\u00E0 (Valenci\u00E0)", Script.LATIN, ','),

	Chinese_Simplified(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null,
			"\u984F", true, "zhCN", "zh",
			"Chinese Simplified / \u7B80\u4F53\u4E2D\u6587", Script.HANS, ','),

	Chinese_Traditional(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null,
			"\u984F", true, "zhTW", "zh",
			"Chinese Traditional / \u7E41\u9AD4\u4E2D\u6587", Script.HANT, ','),

	Croatian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", null, true, "hr", 
			"Croatian / Hrvatska", Script.LATIN, ','),

	Czech(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", null, true, "cs",
			"Czech / \u010Ce\u0161tina", Script.LATIN, ','),

	Danish(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, null, true,
			"da", "Danish / Dansk", Script.LATIN, ','),

	Dutch_Belgium(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", null, true, "nl", "nl_BE",
			"Dutch / Nederlands (Belgi\u00eb)", Script.LATIN, ','),

	Dutch(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", null, true, "nlNL", "nl",
			"Dutch / Nederlands (Nederland)", Script.LATIN, ','),

	English_US(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_DOLLAR + "" + "", null, true, "en", 
			"English (US)", Script.LATIN),

	English_UK(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_POUND + "", null, true, "enGB", "en",
			"English (UK)", Script.LATIN, '.'),

	English_Australia(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_DOLLAR + "", null, true, "enAU", "en",
			"English (Australia)", Script.LATIN, '.'),

	Esperanto(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, null,
			false, "eo", "Esperanto", Script.LATIN),

	Estonian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", null, true, "et",
			"Estonian / Eesti keel", Script.LATIN, ','),

	Filipino(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, null, true,
			"tl", "fil", "Filipino", Script.LATIN, ','), //TODO separator?

	Finnish(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", null, true, "fi",
			"Finnish / Suomi", Script.LATIN, ','),

	French(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", null, true, "fr",
			"French / Fran\u00E7ais", Script.LATIN, ','),

	Galician(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", null, true, "gl", "gl",
			"Galician / Galego", Script.LATIN, ','),

	Georgian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, "\u10d8",
			true, "ka",
			"Georgian / \u10E5\u10D0\u10E0\u10D7\u10E3\u10DA\u10D8 \u10D4\u10DC\u10D0",
			Script.GEORGIAN, ','),

	// German must be before German_Austria
	German(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_DOT,
			Unicode.CURRENCY_EURO + "", null, true, "de",
			"German / Deutsch", Script.LATIN, ','),

	German_Austria(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_DOT,
			Unicode.CURRENCY_EURO + "", null, true, "deAT", "de",
			"German / Deutsch (\u00D6sterreich)", Script.LATIN, ','),

	Greek(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", null, true, "el",
			"Greek / \u0395\u03BB\u03BB\u03B7\u03BD\u03B9\u03BA\u03AC",
			Script.GREEK, ','),

	Hebrew(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_SHEKEL + "", "\u05d9", true, "iw", "he",
			"Hebrew / \u05E2\u05B4\u05D1\u05B0\u05E8\u05B4\u05D9\u05EA",
			Script.HEBREW, '.'),

	Hindi(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_INDIAN_RUPEE + "", "\u0be7", true, "hi",
			"Hindi / \u092E\u093E\u0928\u0915 \u0939\u093F\u0928\u094D\u0926\u0940",
			Script.DEVANGARI),

	Hungarian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_DOT,
			Unicode.CURRENCY_EURO + "", null, true, "hu", 
			"Hungarian / Magyar", Script.LATIN, ','),

	Icelandic(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, null,
			true, "is", "Icelandic / \u00CDslenska", Script.LATIN, ','),

	/**
	 * Java "in"
	 * https://www.oracle.com/technetwork/java/javase/java8locales-2095355.html
	 * 
	 * ISO "id" https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
	 * 
	 */
	Indonesian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, null,
			true, "in", "id", "Indonesian / Bahasa Indonesia",
			Script.LATIN, ','),

	Italian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", null, true, "it", 
			"Italian / Italiano", Script.LATIN, ','),

	// Irish(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, null,
	// false,
	// "ga",
	// "ga", "Irish / Gaeilge", Script.LATIN),

	Japanese(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_YEN + "", "\uff9d", true, "ja",
			"Japanese / \u65E5\u672C\u8A9E", Script.JAPANESE),

	Kannada(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, "\u1103",
			true, "kn", "Kannada", Script.KANNADA),

	Kazakh(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, null, true,
			"kk",
			"Kazakh / \u049A\u0430\u0437\u0430\u049B \u0442\u0456\u043B\u0456",
			Script.CYRILLIC, ','),

	Khmer(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, null, true,
			"km",
			"Khmer",
			Script.LATIN),

	Korean(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_WON + "", "\u1103", true, "ko",
			"Korean / \uD55C\uAD6D\uB9D0", Script.KOREAN),

	Latvian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", null, true, "lv",
			"Latvian / Latvie\u0161u valoda", Script.LATIN, ','),

	Lithuanian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", null, true, "lt",
			"Lithuanian / Lietuvi\u0173 kalba", Script.LATIN, ','),

	Malay(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, null, true,
			"ms", "Malay / Bahasa Malaysia", Script.LATIN),

	Malayalam(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, "\u0D2E",
			false, "ml",
			"Malayalam / \u0D2E\u0D32\u0D2F\u0D3E\u0D33\u0D02",
			Script.MALAYALAM),

	Macedonian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", null, true, "mk",
			"Macedonian / \u041C\u0430\u043A\u0435\u0434\u043E\u043D\u0441\u043A\u0438"
					+ " \u0458\u0430\u0437\u0438\u043A",
			Script.CYRILLIC, ','),

	Marathi(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, "\u092e", null,
			false, "mr", "Marathi / \u092E\u0930\u093E\u0920\u0940",
			Script.DEVANGARI),

	Mongolian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_TUGHRIK + "", null, true, "mn",
			"Mongolian / \u041C\u043E\u043D\u0433\u043E\u043B \u0445\u044D\u043B",
			Script.MONG, ','),

	Nepalese(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_RUPEE + "", "\u0947", true, "ne",
			"Nepalese / \u0928\u0947\u092A\u093E\u0932\u0940",
			Script.DEVANGARI),

	Norwegian_Bokmal(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null,
			null, true, "noNO", "nb", "Norwegian / Bokm\u00e5l",
			Script.LATIN, ','),

	Norwegian_Nynorsk(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null,
			null, true, "noNONY", "nn", "Norwegian / Nynorsk",
			Script.LATIN, ','),

	Persian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, null, true,
			"fa", "Persian / \u0641\u0627\u0631\u0633\u06CC",
			Script.ARABIC),

	Polish(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_DOT,
			Unicode.CURRENCY_EURO + "", null, true, "pl",
			"Polish / J\u0119zyk polski", Script.LATIN),

	// use Brazilian as the root (ie not ptBR) as there are more speakers
	Portuguese_Brazil(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null,
			null, true, "pt", "Portuguese / Portugu\u00EAs (Brasil)",
			Script.LATIN, ','),

	Portuguese_Portugal(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", null, true, "ptPT", "pt",
			"Portuguese / Portugu\u00EAs (Portugal)", Script.LATIN, ','),

	Romanian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", null, true, "ro",
			"Romanian /  Rom\u00E2n\u0103", Script.LATIN),

	Russian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, "\u0439",
			true, "ru",
			"Russian / \u0420\u0443\u0441\u0441\u043A\u0438\u0439 \u044F\u0437\u044B\u043A",
			Script.CYRILLIC, ','),

	Sinhala(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_RUPEE + "", "\u0d9a", true, "si",
			"Sinhala / \u0DC3\u0DD2\u0D82\u0DC4\u0DBD", Script.SINHALA),

	Serbian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", null, true, "sr",
			"Serbian / \u0441\u0440\u043F\u0441\u043A\u0438", Script.LATIN, ','),

	Slovak(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", null, true, "sk",
			"Slovak / Slovensk\u00FD jazyk", Script.LATIN),

	Slovenian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", null, true, "sl",
			"Slovenian / Sloven\u0161\u010Dina", Script.LATIN, ','),

	Spanish(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_DOLLAR + "", null, true, "es",
			"Spanish / Espa\u00F1ol", Script.LATIN, ','),

	Spanish_UY(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_DOLLAR + "", null, true, "esUY", "es",
			"Spanish / Espa\u00F1ol (Uruguay)", Script.LATIN, ','),

	Spanish_ES(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", null, true, "esES", "es",
			"Spanish / Espa\u00F1ol (Espa\u00F1a)", Script.LATIN, ','),

	Swedish(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", null, true, "sv", 
			"Swedish / Svenska", Script.LATIN, ','),

	Tamil(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_RUPEE + "", "\u0be7", true, "ta",
			"Tamil / \u0BA4\u0BAE\u0BBF\u0BB4\u0BCD", Script.TAMIL),

	Tajik(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, null, false,
			"tg", "Tajik", Script.LATIN),

	// Telugu("\u0C24", false, "te","te",
	// "Telugu / \u0C24\u0C46\u0C32\u0C41\u0C17\u0C41", Country.India),

	Thai(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_BAHT + "", null, true, "th",
			"Thai / \u0E20\u0E32\u0E29\u0E32\u0E44\u0E17\u0E22", Script.THAI),

	Turkish(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", null, true, "tr",
			"Turkish / T\u00FCrk\u00E7e", Script.LATIN),

	Ukrainian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, null,
			true, "uk",
			"Ukrainian / \u0423\u043A\u0440\u0430\u0457\u043D\u0441\u044C\u043A\u0430"
					+ " \u043C\u043E\u0432\u0430",
			Script.CYRILLIC, ','),

	Uyghur(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, null, true,
			"ug", "Uyghur", Script.ARABIC),

	Uzbek(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, null, true,
			"uz", "Uzbek", Script.CYRILLIC, ','),

	Vietnamese(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_DONG + "", null, true, "vi",
			"Vietnamese / Ti\u1EBFng Vi\u1EC7t", Script.LATIN, ','),

	Welsh(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, null, true,
			"cy", "cy", "Welsh / Cymraeg", Script.LATIN, '.'),

	Xhosa(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, null, true,
			"xh", "Xhosa / isiXhosa", Script.LATIN, ','),

	/**
	 * Java "ji" https://docs.oracle.com/javase/7/docs/api/java/util/Locale.html
	 * 
	 * ISO "yi" https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
	 * 
	 */
	Yiddish(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_SHEKEL + "", "\u05d9\u05b4", true, "ji", "yi",
			"Yiddish / \u05D9\u05D9\u05B4\u05D3\u05D9\u05E9",
			Script.HEBREW, '.');

	// Interlingua(null, true, "ia", "ia", "Interlingua",
	// Country.UnitedStatesofAmerica);
	final public String locale;
	final public String localeISO6391;
	final public Script scriptISO15924;
	final public String name;
	// used to determine whether to put in release versions
	final public boolean fullyTranslated;
	/**
	 * test characters to get a font that can display the correct symbols for
	 * each language Also used to register the fonts so that JLaTeXMath can
	 * display other Unicode blocks
	 */
	final private String testChar;
	final private String currency;

	// https://en.wikipedia.org/wiki/Right_angle
	private int rightAngleStyle = EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE;
	private char decimalSeparator;

	/**
	 */
	Language(int rightAngleStyle, String currency, String testChar,
			boolean fullyTranslated, String locale,
			String localeISO6391, String name, Script scriptISO15924, char decimalSeparator) {
		this.rightAngleStyle = rightAngleStyle;
		this.currency = currency == null ? "$" : currency;
		this.locale = locale;
		this.name = name;
		this.localeISO6391 = localeISO6391;
		this.scriptISO15924 = scriptISO15924;
		this.fullyTranslated = fullyTranslated;
		this.testChar = testChar;
		this.decimalSeparator = decimalSeparator;
	}

	Language(int rightAngleStyle, String currency, String testChar,
			boolean fullyTranslated, String locale,
			String name, Script scriptISO15924) {
		this(rightAngleStyle, currency, testChar, fullyTranslated, locale,
				locale, name, scriptISO15924, '.');
	}

	Language(int rightAngleStyle, String currency, String testChar,
			boolean fullyTranslated, String locale,
			String name, Script scriptISO15924, char decimalSeparator) {
		this(rightAngleStyle, currency, testChar, fullyTranslated, locale,
				locale, name, scriptISO15924, decimalSeparator);
	}

	/**
	 * @param language
	 *            ISO code
	 * @return closest constant
	 */
	final public static Language getLanguage(String language) {
		for (Language l : Language.values()) {
			// language could be "ca" or "caXV"
			if (l.locale.equals(language) || l.localeISO6391.equals(language)) {
				return l;
			}
		}
		for (Language l : Language.values()) {
			if (l.locale.substring(0, 2).equals(language)) {
				return l;
			}
		}
		Log.error("language not recognized: " + language);
		return Language.English_US;
	}

	/**
	 * @param ggbLangCode
	 *            language code
	 * @return display name
	 */
	final public static String getDisplayName(String ggbLangCode) {
		// eg change en_GB to enGB
		String shortLangCode = ggbLangCode.replaceAll("_", "");
		for (Language l : Language.values()) {
			if (l.locale.equals(shortLangCode)
					|| l.getLocaleGWT().replaceAll("_", "")
							.equals(shortLangCode)) {
				return l.name;
			}
		}
		Log.error("language not found: " + shortLangCode);
		return null;
	}

	/**
	 * 
	 * @param language
	 *            two letter code
	 * @return test character for font detection
	 */
	final public static String getTestChar(String language) {
		for (Language l : Language.values()) {
			if (l.locale.startsWith(language)) {
				return l.testChar == null ? "a" : l.testChar;
			}
		}
		Log.error("language not found: " + language);
		return "a";
	}

	/**
	 * @param browserLangCode
	 *            language code
	 * @return closest language supported in the app
	 */
	final public static Language getClosestGWTSupportedLanguage(
			String browserLangCode) {
		String normalizedLanguage = (browserLangCode + "")
				.toLowerCase(Locale.US).replace("_", "-");

		if (normalizedLanguage.startsWith("zh")) {
			return normalizedLanguage.contains("tw")
					|| normalizedLanguage.contains("hant")
							? Language.Chinese_Traditional
							: Language.Chinese_Simplified;
		}
		// on iOS it's nb_no
		else if ("no-no-ny".equals(normalizedLanguage)) {
			return Language.Norwegian_Nynorsk;
		} else if (normalizedLanguage.startsWith("no")) {
			return Language.Norwegian_Bokmal;
		}

		// browserLangCode example: en-US, en-GB, pt-BR, pt-pt, and de-DE
		for (Language lang : Language.values()) {
			if (lang.getLocaleGWT().equalsIgnoreCase(normalizedLanguage)) {
				return lang;
			}
		}
		// look for mother language in the hierarchy ie. the first two
		// characters
		if (normalizedLanguage.length() >= 2) {
			for (Language lang : Language.values()) {
				if (lang.getLocaleGWT()
						.equalsIgnoreCase(normalizedLanguage.substring(0, 2))
						|| lang.locale.equalsIgnoreCase(
								normalizedLanguage.substring(0, 2))) {
					return lang;
				}
			}
		}
		return Language.English_US;
	}

	/**
	 * @param language
	 *            language string
	 * @return the currency belonging to the given language (default Dollar)
	 */
	final public static String getCurrency(String language) {
		for (Language l : Language.values()) {
			if (l.getLocaleGWT().equals(language)) {
				return l.currency;
			}
		}
		return Unicode.CURRENCY_DOLLAR + "";
	}

	final public static int getRightAngleStyle(String language) {
		return getLanguage(language).getRightAngleStyle();
	}

	public static boolean isUsingDecimalComma(String language) {
		return getLanguage(language).decimalSeparator == ',';
	}

	final public int getRightAngleStyle() {
		return this.rightAngleStyle;
	}

	/**
	 * @return whether localized keyboard is supported
	 */
	final public boolean hasTranslatedKeyboard() {
		switch (this) {
		case Chinese_Traditional:
			return false;
		default:
			return true;
		}
	}

	/**
	 * @return locale string for GWT
	 */
	public String getLocaleGWT() {
		String lang = this.localeISO6391.replace("nl_BE", "nl");
		return "nb".equals(lang) || "nn".equals(lang)
				|| this.locale.length() < 3 ? lang
				: lang + "-" + this.locale.substring(2);
	}
}
