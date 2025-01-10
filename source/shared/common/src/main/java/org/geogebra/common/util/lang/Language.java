package org.geogebra.common.util.lang;

import static com.himamis.retex.editor.share.util.Unicode.ITALIAN_ORDINAL_INDICATOR;
import static com.himamis.retex.editor.share.util.Unicode.SPANISH_ORDINAL_INDICATOR;

import java.util.Locale;

import javax.annotation.CheckForNull;

import org.geogebra.common.main.OrdinalConverter;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Collection of which languages are official in which countries (only includes
 * languages supported in GeoGebra)
 * @author michael@geogebra.org
 * <a href="http://en.wikipedia.org/wiki/List_of_official_languages">list of languages</a>
 * <a href="http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2">ISO 3166-1</a>
 */
@SuppressWarnings("javadoc")
public enum Language {

	// need to be in Alphabetical order so they appear in the menu in the right
	// order
	Afrikaans(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null,
			false, "af", "Afrikaans", Script.LATIN, ""),

	Albanian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, true,
			"sq", "Albanian / Gjuha Shqipe", Script.LATIN, ',', '0', "-te"),

	Amharic(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null,
			false, "am",
			"Amharic / \u0041\u006d\u0061\u0072\u0259\u00f1\u00f1\u0061",
			Script.ETHIOPIAN, ""),

	Arabic(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, true,
			"ar",
			Unicode.LEFT_TO_RIGHT_MARK + "Arabic" + Unicode.LEFT_TO_RIGHT_MARK
					+ " / " + Unicode.RIGHT_TO_LEFT_MARK
					+ "\u0627\u0644\u0639\u0631\u0628\u064A\u0629"
					+ Unicode.RIGHT_TO_LEFT_MARK,
			Script.ARABIC, '.', '\u0660', ""),

	Arabic_Morocco(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null,
			true, "ar",
			Unicode.LEFT_TO_RIGHT_MARK + "Arabic (Morocco)"
					+ Unicode.LEFT_TO_RIGHT_MARK + " / "
					+ Unicode.RIGHT_TO_LEFT_MARK
					+ "\u0627\u0644\u0639\u0631\u0628\u064A\u0629"
					+ " (\u0627\u0644\u0645\u063A\u0631\u0628)"
					+ Unicode.RIGHT_TO_LEFT_MARK,
			Script.ARABIC, ',', '0', "MA", ""),

	Arabic_Tunisia(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null,
			true,  "ar",
			Unicode.LEFT_TO_RIGHT_MARK + "Arabic (Tunisia)"
					+ Unicode.LEFT_TO_RIGHT_MARK + " / "
					+ Unicode.RIGHT_TO_LEFT_MARK
					+ "\u0627\u0644\u0639\u0631\u0628\u064A\u0629 (\u062A\u0648\u0646\u0633)"
					+ Unicode.RIGHT_TO_LEFT_MARK,
			Script.ARABIC, ',', '0', "TN", ""),

	Arabic_Syria(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null,
			false, "ar",
			Unicode.LEFT_TO_RIGHT_MARK + "Arabic (Syria)" + Unicode.LEFT_TO_RIGHT_MARK
					+ " / " + Unicode.RIGHT_TO_LEFT_MARK
					+ "\u0627\u0644\u0639\u0631\u0628\u064A\u0629 (\u0633\u0648\u0631\u064A\u0627)"
					+ Unicode.RIGHT_TO_LEFT_MARK,
			Script.ARABIC, ',', '0', "SY", ""), // TODO assuming based on other arabic countries

	Armenian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null,
			true, "hy",
			"Armenian / \u0540\u0561\u0575\u0565\u0580\u0565\u0576",
			Script.ARMENIAN, ',', ""),

	Azerbaijani(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null,
			false, "az", "Azerbaijani", Script.LATIN, ',', ""),

	Basque(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", true, "eu",
			"Basque / Euskara", Script.LATIN, ',', '0', null, "."),

	Bengali(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", false, "bn",
			"Bengali / \u09AC\u09BE\u0982\u09B2\u09BE", Script.BENGALI, ""),

	Bosnian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", true, "bs",
			"Bosnian / \u0431\u043E\u0441\u0430\u043D\u0441\u043A\u0438",
			Script.LATIN, "-ti"),

	Bulgarian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", true, "bg",
			"Bulgarian / \u0431\u044A\u043B\u0433\u0430\u0440\u0441\u043A\u0438"
					+ " \u0435\u0437\u0438\u043A",
			Script.CYRILLIC, ',', null),

	Catalan(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", true, "ca",
			"Catalan / Catal\u00E0", Script.LATIN, ',', '0', null, null),

	Valencian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", true, "ca",
			"Catalan / Catal\u00E0 (Valenci\u00E0)", Script.LATIN, ',', '0', "XV", null),

	Chinese_Simplified(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null,
			true, "zh",
			"Chinese Simplified / \u7B80\u4F53\u4E2D\u6587", Script.HANS, ',', '0', "CN", ""),

	Chinese_Traditional(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null,
			true, "zh",
			"Chinese Traditional / \u7E41\u9AD4\u4E2D\u6587", Script.HANT, ',', '0', "TW", ""),

	Croatian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", true, "hr",
			"Croatian / Hrvatska", Script.LATIN, ',', "."),

	Czech(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", true, "cs",
			"Czech / \u010Ce\u0161tina", Script.LATIN, ',', "."),

	Danish(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, true,
			"da", "Danish / Dansk", Script.LATIN, ',', "."),

	Dutch_Belgium(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", true, "nl",
			"Dutch / Nederlands (Belgi\u00eb)", Script.LATIN, ',', '0', null, "e"),

	Dutch(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", true, "nl",
			"Dutch / Nederlands (Nederland)", Script.LATIN, ',', '0', "NL", "e"),

	English_US(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_DOLLAR + "", true, "en",
			"English (US)", Script.LATIN, null),

	English_UK(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_POUND + "", true, "en",
			"English (UK)", Script.LATIN, '.', '0', "GB", null),

	English_Australia(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_DOLLAR + "", true, "en",
			"English (Australia)", Script.LATIN, '.', '0', "AU", null),

	Esperanto(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null,
			false, "eo", "Esperanto", Script.LATIN, ""),

	Estonian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", true, "et",
			"Estonian / Eesti keel", Script.LATIN, ',', "."),

	Filipino(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, true,
			"fil", "Filipino", Script.LATIN, ',', '0', null, ""), //TODO separator?

	Finnish(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", true, "fi",
			"Finnish / Suomi", Script.LATIN, ',', ":s"),

	French(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", true, "fr",
			"French / Fran\u00E7ais", Script.LATIN, ',', null),

	Galician(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", true, "gl",
			"Galician / Galego", Script.LATIN, ',', '0', null, "ava"),

	Georgian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null,
			true, "ka",
			"Georgian / \u10E5\u10D0\u10E0\u10D7\u10E3\u10DA\u10D8 \u10D4\u10DC\u10D0",
			Script.GEORGIAN, ',', "-\u10d4"),

	// German must be before German_Austria
	German(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_DOT,
			Unicode.CURRENCY_EURO + "", true, "de",
			"German / Deutsch", Script.LATIN, ',', "."),

	German_Austria(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_DOT,
			Unicode.CURRENCY_EURO + "", true, "de",
			"German / Deutsch (\u00D6sterreich)", Script.LATIN, ',', '0', "AT", "."),

	Greek(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", true, "el",
			"Greek / \u0395\u03BB\u03BB\u03B7\u03BD\u03B9\u03BA\u03AC",
			Script.GREEK, ',', "\u03b7"),

	Hebrew(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_SHEKEL + "", true, "he",
			"Hebrew / \u05E2\u05B4\u05D1\u05B0\u05E8\u05B4\u05D9\u05EA",
			Script.HEBREW, '.', '0', null, null),

	Hindi(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_INDIAN_RUPEE + "", true, "hi",
			"Hindi / \u092E\u093E\u0928\u0915 \u0939\u093F\u0928\u094D\u0926\u0940",
			Script.DEVANGARI, ""),

	Hungarian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_DOT,
			Unicode.CURRENCY_EURO + "", true, "hu",
			"Hungarian / Magyar", Script.LATIN, ',', "."),

	Icelandic(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null,
			true, "is", "Icelandic / \u00CDslenska", Script.LATIN, ',', "."),

	/**
	 * Code "id" from  <a href="https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes">ISO 639-1</a>
	 */
	Indonesian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null,
			true,  "id", "Indonesian / Bahasa Indonesia",
			Script.LATIN, ',', '0', null, null),

	Italian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", true, "it",
			"Italian / Italiano", Script.LATIN, ',',
			Character.toString(ITALIAN_ORDINAL_INDICATOR)),

	Japanese(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_YEN + "", true, "ja",
			"Japanese / \u65E5\u672C\u8A9E", Script.JAPANESE, ""),

	Kannada(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null,
			true, "kn", "Kannada", Script.KANNADA, ""),

	Kazakh(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, true,
			"kk",
			"Kazakh / \u049A\u0430\u0437\u0430\u049B \u0442\u0456\u043B\u0456",
			Script.CYRILLIC, ',', ""),

	Khmer(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, true,
			"km",
			"Khmer",
			Script.LATIN, '.', '\u17e0', ""),

	Korean(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_WON + "", true, "ko",
			"Korean / \uD55C\uAD6D\uB9D0", Script.KOREAN, ""),

	Latvian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", true, "lv",
			"Latvian / Latvie\u0161u valoda", Script.LATIN, ',', ""),

	Lithuanian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", true, "lt",
			"Lithuanian / Lietuvi\u0173 kalba", Script.LATIN, ',', ""),

	Malay(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, true,
			"ms", "Malay / Bahasa Malaysia", Script.LATIN, ""),

	Malayalam(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null,
			false, "ml",
			"Malayalam / \u0D2E\u0D32\u0D2F\u0D3E\u0D33\u0D02",
			Script.MALAYALAM, '.', '\u0d66', ""),

	Macedonian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", true, "mk",
			"Macedonian / \u041C\u0430\u043A\u0435\u0434\u043E\u043D\u0441\u043A\u0438"
					+ " \u0458\u0430\u0437\u0438\u043A",
			Script.CYRILLIC, ',', "-\u0442\u0438"),

	Marathi(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, "\u092e",
			false, "mr", "Marathi / \u092E\u0930\u093E\u0920\u0940",
			Script.DEVANGARI, ""),

	Mongolian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_TUGHRIK + "", true, "mn",
			"Mongolian / \u041C\u043E\u043D\u0433\u043E\u043B \u0445\u044D\u043B",
			Script.CYRILLIC, ',', '\u1810', null, true, ""),

	Mongolian_Traditional(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_TUGHRIK + "", false, "mn",
			"Mongolian Traditional / "
					+ "\u182E\u1823\u1829\u182D\u1823\u182F \u182A\u1822\u1834\u1822\u182D\u180C",
			Script.MONG, ',', '\u1810', null,  false, ""),

	Nepalese(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_RUPEE + "", true, "ne",
			"Nepalese / \u0928\u0947\u092A\u093E\u0932\u0940",
			Script.DEVANGARI, ""),

	Norwegian_Bokmal(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null,
			true, "nb", "Norwegian / Bokm\u00e5l",
			Script.LATIN, ',', '0', null, ""),

	Norwegian_Nynorsk(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null,
			true, "nn", "Norwegian / Nynorsk",
			Script.LATIN, ',', '0', null, ""),

	Persian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, true,
			"fa", "Persian / \u0641\u0627\u0631\u0633\u06CC",
			Script.ARABIC, '.', '\u06f0', ""),

	Polish(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_DOT,
			Unicode.CURRENCY_EURO + "", true, "pl",
			"Polish / J\u0119zyk polski", Script.LATIN, "."),

	// use Brazilian as the root (ie not ptBR) as there are more speakers
	Portuguese_Brazil(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null,
			true, "pt", "Portuguese / Portugu\u00EAs (Brasil)",
			Script.LATIN, ',', Character.toString(ITALIAN_ORDINAL_INDICATOR)),

	Portuguese_Portugal(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", true, "pt",
			"Portuguese / Portugu\u00EAs (Portugal)", Script.LATIN, ',', '0', "PT",
			Character.toString(ITALIAN_ORDINAL_INDICATOR)),

	Romanian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", true, "ro",
			"Romanian /  Rom\u00E2n\u0103", Script.LATIN, "."),

	Russian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null,
			true, "ru",
			"Russian / \u0420\u0443\u0441\u0441\u043A\u0438\u0439 \u044F\u0437\u044B\u043A",
			Script.CYRILLIC, ',', "-\u0433\u043e"),

	Sinhala(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_RUPEE + "", true, "si",
			"Sinhala / \u0DC3\u0DD2\u0D82\u0DC4\u0DBD", Script.SINHALA, ""),

	Serbian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", true, "sr",
			"Serbian / \u0441\u0440\u043F\u0441\u043A\u0438", Script.LATIN, ',', "."),

	Slovak(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", true, "sk",
			"Slovak / Slovensk\u00FD jazyk", Script.LATIN, "."),

	Slovenian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", true, "sl",
			"Slovenian / Sloven\u0161\u010Dina", Script.LATIN, ',', "-ti"),

	Spanish(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_DOLLAR + "", true, "es",
			"Spanish / Espa\u00F1ol", Script.LATIN, ',', SPANISH_ORDINAL_INDICATOR),

	Spanish_UY(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_DOLLAR + "", true, "es",
			"Spanish / Espa\u00F1ol (Uruguay)", Script.LATIN, ',', '0', "UY",
			SPANISH_ORDINAL_INDICATOR),

	Spanish_ES(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", true, "es",
			"Spanish / Espa\u00F1ol (Espa\u00F1a)", Script.LATIN, ',', '0', "ES",
			SPANISH_ORDINAL_INDICATOR),

	Swedish(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", true, "sv",
			"Swedish / Svenska", Script.LATIN, ',', null),

	Tamil(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_RUPEE + "", true, "ta",
			"Tamil / \u0BA4\u0BAE\u0BBF\u0BB4\u0BCD", Script.TAMIL, '.', '\u0be6', ""),

	Tajik(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, false,
			"tg", "Tajik", Script.LATIN, ""),

	// Telugu("\u0C24", false, "te","te",
	// "Telugu / \u0C24\u0C46\u0C32\u0C41\u0C17\u0C41", Country.India),

	Thai(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_BAHT + "", true, "th",
			"Thai / \u0E20\u0E32\u0E29\u0E32\u0E44\u0E17\u0E22", Script.THAI,
			'.', '\u0e50', ""),

	Turkish(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_EURO + "", true, "tr",
			"Turkish / T\u00FCrk\u00E7e", Script.LATIN, "."),

	Ukrainian(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null,
			true, "uk",
			"Ukrainian / \u0423\u043A\u0440\u0430\u0457\u043D\u0441\u044C\u043A\u0430"
					+ " \u043C\u043E\u0432\u0430",
			Script.CYRILLIC, ',', "-\u0433\u043e"),

	Uyghur(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, true,
			"ug", "Uyghur", Script.ARABIC, ""),

	Uzbek(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, true,
			"uz", "Uzbek / O'zbek", Script.CYRILLIC, ',', ""),

	Vietnamese(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_DONG + "", true, "vi",
			"Vietnamese / Ti\u1EBFng Vi\u1EC7t", Script.LATIN, ',', ""),

	Welsh(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, true,
			"cy", "Welsh / Cymraeg", Script.LATIN, '.', '0', null, ""),

	Xhosa(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE, null, true,
			"xh", "Xhosa / isiXhosa", Script.LATIN, ',', ""),

	/**
	 * Code "yi" from <a href="https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes">ISO 639-1</a>
	 */
	Yiddish(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			Unicode.CURRENCY_SHEKEL + "", true, "yi",
			"Yiddish / \u05D9\u05D9\u05B4\u05D3\u05D9\u05E9",
			Script.HEBREW, '.', '0', null, null);

	/** ISO 639 alpha-2 or alpha-3 language code */
	final public String language;
	/** ISO 15924 alpha-4 script */
	final public Script script;
	/** ISO 3166 alpha-2 country code */
	final public String region;
	/** Suppress script tag (see BCP 47) */
	final public boolean suppressScript;
	final private String languageTag;
	final public String name;
	// used to determine whether to put in release versions
	final public boolean fullyTranslated;

	final private String currency;

	// https://en.wikipedia.org/wiki/Right_angle
	private final int rightAngleStyle;
	private final char decimalSeparator;
	private final char unicodeZero;
	private final @CheckForNull String ordinalIndicator;

	/**
	 * Constructs a Language enum.
	 * @param rightAngleStyle right angle style
	 * @param currency localized currency
	 * @param fullyTranslated is fully translated
	 * @param language language ISO 639 2 or 3 letter codes
	 * @param name the name of the language
	 * @param script script ISO 15924 alpha-4
	 * @param decimalSeparator decimal separator
	 * @param unicodeZero unicode zero
	 * @param region region ISO 3166 alpha-2 country code
	 * @param suppressScript suppress script tag in {@link Language#toLanguageTag()}
	 * (see BCP 47 suppress script)
	 * @param ordinalIndicator Ordinal Indicator, suffix used for ordinal numbers
	 */
	Language(int rightAngleStyle, String currency, boolean fullyTranslated,
			@Deprecated String language, String name, Script script,
			char decimalSeparator, char unicodeZero, String region,
			boolean suppressScript, String ordinalIndicator) {
		this.rightAngleStyle = rightAngleStyle;
		this.currency = currency == null ? "$" : currency;
		this.name = name;
		this.language = language;
		this.script = script;
		this.fullyTranslated = fullyTranslated;
		this.decimalSeparator = decimalSeparator;
		this.unicodeZero = unicodeZero;
		this.region = region;
		this.suppressScript = suppressScript;
		this.languageTag = createLanguageTag();
		this.ordinalIndicator = ordinalIndicator;
	}

	Language(int rightAngleStyle, String currency, boolean fullyTranslated,
			String language, String name, Script script,
			char decimalSeparator, char unicodeZero, String region, String ordinalIndicator) {
		this(rightAngleStyle, currency, fullyTranslated, language, name, script,
				decimalSeparator, unicodeZero, region, true, ordinalIndicator);
	}

	Language(int rightAngleStyle, String currency, boolean fullyTranslated,
			@Deprecated String locale, String name, Script script, String ordinalIndicator) {
		this(rightAngleStyle, currency, fullyTranslated, locale, name, script,
				'.', '0', null, true, ordinalIndicator);
	}

	Language(int rightAngleStyle, String currency, boolean fullyTranslated,
			String language, String name, Script script, char decimalSeparator,
			char unicodeZero, String ordinalIndicator) {
		this(rightAngleStyle, currency, fullyTranslated, language, name, script,
				decimalSeparator, unicodeZero, null, true, ordinalIndicator);
	}

	Language(int rightAngleStyle, String currency, boolean fullyTranslated,
			String language, String name, Script script, char decimalSeparator,
			String ordinalIndicator) {
		this(rightAngleStyle, currency, fullyTranslated, language, name, script,
				decimalSeparator, '0', null, true, ordinalIndicator);
	}

	/**
	 * Gets the closest supported language or the default (English_US)
	 * @param language ISO 639 language code
	 * @return closest constant
	 */
	public static Language getLanguage(String language) {
		// First try to match the closest language with no other subtags
		for (Language l : Language.values()) {
			if (l.toLanguageTag().equals(language)) {
				return l;
			}
		}
		// Then try to match first language only
		String languagePart = language.split("-")[0];
		for (Language l : Language.values()) {
			if (l.language.equals(languagePart)) {
				return l;
			}
		}
		Log.error("language not recognized: " + language);
		return Language.English_US;
	}

	/**
	 * @return test character for font detection
	 */
	public String getTestChar() {
		return script.testString == null ? "a" : script.testString;
	}

	/**
	 * Robust string to language conversion, supports both BCP47 tags and Java locales
	 * @param langCode language code
	 * @return closest language supported in the app
	 */
	public static Language fromLanguageTagOrLocaleString(String langCode) {
		if (langCode == null) {
			return English_US;
		}
		String normalizedLanguage = langCode.replace("_", "-");
		String languagePart = normalizedLanguage.split("-")[0].toLowerCase(Locale.US);
		switch (languagePart) {
		case "zh":
			return normalizedLanguage.toLowerCase(Locale.US).contains("tw")
					|| normalizedLanguage.toLowerCase(Locale.US).contains("hant")
					? Language.Chinese_Traditional
					: Language.Chinese_Simplified;
		case "no":
			return "no-no-ny".equalsIgnoreCase(normalizedLanguage)
					? Language.Norwegian_Nynorsk : Norwegian_Bokmal;
		case "tl":
			return Filipino;
		case "iw":
			return Hebrew;
		case "in":
			return Indonesian;
		case "ji":
			return Yiddish;
		default: return getLanguage(normalizedLanguage);
		}
	}

	/**
	 * @param languageTag BCP47 locale string (see values in {@link Language#toLanguageTag()}.
	 * @return the currency belonging to the given language (default Dollar)
	 */
	public static String getCurrency(String languageTag) {
		return getLanguage(languageTag).currency;
	}

	public boolean isUsingDecimalComma() {
		return decimalSeparator == ',';
	}

	final public int getRightAngleStyle() {
		return this.rightAngleStyle;
	}

	final public char getUnicodeZero() {
		return this.unicodeZero;
	}

	final public char getDecimalSeparator() {
		return this.decimalSeparator;
	}

	/**
	 * @return whether localized keyboard is supported
	 */
	final public boolean hasTranslatedKeyboard() {
		return this != Language.Chinese_Traditional;
	}

	/**
	 * @return locale string for GWT
	 */
	public String getLocaleGWT() {
		return toLanguageTag();
	}

	/**
	 * Constructs and returns a properly formatted language tag (as defined in BCP 47)
	 * @return BCP 47 language tag
	 */
	public String toLanguageTag() {
		return languageTag;
	}

	/**
	 * @param number Integer
	 * @return The corresponding ordinal number, depending on the language used
	 */
	public String getOrdinalNumber(int number) {
		if (this.ordinalIndicator != null) {
			return number + this.ordinalIndicator;
		}
		return OrdinalConverter.getOrdinalNumber(this, number);
	}

	private String createLanguageTag() {
		StringBuilder builder = new StringBuilder(language);
		if (script != null && !suppressScript) {
			builder.append("-");
			builder.append(script.iso15924);
		}
		if (region != null) {
			builder.append("-");
			builder.append(region);
		}
		return builder.toString();
	}
}
