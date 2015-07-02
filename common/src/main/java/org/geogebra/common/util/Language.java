package org.geogebra.common.util;

import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;

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
	// Afrikaans(null, false, "af","af", "Afrikaans", Country.SouthAfrica),
	Armenian(null, "\u0570", true, "hy", "hy",
			"Armenian / \u0540\u0561\u0575\u0565\u0580\u0565\u0576",
			Country.Armenia),

	Albanian(null, null, true, "sq", "sq", "Albanian / Gjuha Shqipe",
			Country.Albania),

	Arabic(null, null, true, "ar", "ar", Unicode.LeftToRightMark + "Arabic"
			+ Unicode.LeftToRightMark + " / " + Unicode.RightToLeftMark
			+ "\u0627\u0644\u0639\u0631\u0628\u064A\u0629"
			+ Unicode.RightToLeftMark, Country.Egypt, Country.Algeria,
			Country.Bahrain, Country.Chad, Country.Comoros, Country.Djibouti,
			Country.Eritrea, Country.Iraq, Country.Jordan, Country.Kuwait,
			Country.Lebanon, Country.Libya, Country.Mauritania, Country.Oman,
			Country.Palestine, Country.Qatar, Country.SaudiArabia,
			Country.Somalia, Country.Sudan, Country.Syria,
			Country.UnitedArabEmirates, Country.WesternSahara, Country.Yemen),

	Arabic_Morocco(
			null,
			null,
			true,
			"arMA",
			"ar_MA",
			Unicode.LeftToRightMark
					+ "Arabic (Morocco)"
					+ Unicode.LeftToRightMark
					+ " / "
					+ Unicode.RightToLeftMark
					+ "\u0627\u0644\u0639\u0631\u0628\u064A\u0629 (\u0627\u0644\u0645\u063A\u0631\u0628)"
					+ Unicode.RightToLeftMark, Country.Morocco),

	Arabic_Tunisia(
			null,
			null,
			true,
			"arTN",
			"ar_TN",
			Unicode.LeftToRightMark
					+ "Arabic (Tunisia)"
					+ Unicode.LeftToRightMark
					+ " / "
					+ Unicode.RightToLeftMark
					+ "\u0627\u0644\u0639\u0631\u0628\u064A\u0629 (\u062A\u0648\u0646\u0633)"
					+ Unicode.RightToLeftMark, Country.Tunisia),

	Basque(Unicode.CURRENCY_EURO, null, true, "eu", "eu", "Basque / Euskara",
			"basque"),
	// fudge to get right flag

	Bosnian(Unicode.CURRENCY_EURO, null, true, "bs", "bs",
			"Bosnian / \u0431\u043E\u0441\u0430\u043D\u0441\u043A\u0438",
			Country.BosniaandHerzegovina),

	Bulgarian(
			Unicode.CURRENCY_EURO,
			null,
			true,
			"bg",
			"bg",
			"Bulgarian / \u0431\u044A\u043B\u0433\u0430\u0440\u0441\u043A\u0438 \u0435\u0437\u0438\u043A",
			Country.Bulgaria),

	Catalan(Unicode.CURRENCY_EURO, null, true, "ca", "ca",
			"Catalan / Catal\u00E0", "catalonia"),
	// fudge to get right flag

	Valencian(Unicode.CURRENCY_EURO, null, true, "caXV", "ca_XV",
			"Catalan (Valencia) / Catal\u00E0 (Valenci\u00E0)", "valencia"),
	// fudge to get right flag

	Chinese_Simplified(null, "\u984F", true, "zhCN", "zh_CN",
			"Chinese Simplified / \u7B80\u4F53\u4E2D\u6587", Country.China,
			Country.Singapore),
			
	Chinese_Traditional(null, "\u984F", true, "zhTW",
			"zh_TW", "Chinese Traditional / \u7E41\u9AD4\u4E2D\u6587",
			Country.TaiwanProvinceofChina),

	Croatian(Unicode.CURRENCY_EURO, null, true, "hr", "hr",
			"Croatian / Hrvatska", Country.Croatia,
			Country.BosniaandHerzegovina),

	Czech(Unicode.CURRENCY_EURO, null, true, "cs", "cs",
			"Czech / \u010Ce\u0161tina",
			Country.CzechRepublic),

	Danish(null, null, true, "da", "da", "Danish / Dansk",
			Country.Denmark),

	Dutch(Unicode.CURRENCY_EURO, null, true, "nl", "nl",
			"Dutch / Nederlands (The Netherlands)",
			Country.Netherlands, Country.Suriname),

	Dutch_Belgium(Unicode.CURRENCY_EURO, null, true, "nlBE", "nl_BE",
			"Dutch / Nederlands (Belgium)",
			Country.Belgium),

	English_US(Unicode.CURRENCY_DOLLAR, null, true, "en", "en", "English (US)",
			Country.UnitedStatesofAmerica, Country.AntiguaBarbuda,
			Country.Bahamas, Country.Barbados, Country.Belize,
			Country.Botswana, Country.Cameroon, Country.Canada,
			Country.Dominica, Country.Eritrea, Country.Ethiopia, Country.Fiji,
			Country.Gambia, Country.Ghana, Country.Grenada, Country.Guyana,
			Country.India, Country.Ireland, Country.Jamaica, Country.Kenya,
			Country.Kiribati, Country.Netherlands, Country.Lesotho,
			Country.Liberia, Country.Malawi, Country.Malta,
			Country.MarshallIslands, Country.Mauritius, Country.Micronesia,
			Country.Namibia, Country.Nauru, Country.Nigeria, Country.Pakistan,
			Country.Palau, Country.PapuaNewGuinea, Country.Philippines,
			Country.Rwanda, Country.StKittsandNevis, Country.SaintLucia,
			Country.StVincenttheGrenadines, Country.Samoa, Country.Seychelles,
			Country.SierraLeone, Country.Singapore, Country.SolomonIslands,
			Country.SouthAfrica, Country.SouthSudan, Country.Sudan,
			Country.Swaziland, Country.Tanzania, Country.Tonga,
			Country.TrinidadTobago, Country.Tuvalu, Country.Uganda,
			Country.Vanuatu, Country.Zambia, Country.Zimbabwe),

	English_UK(Unicode.CURRENCY_POUND, null, true, "enGB", "en_GB",
			"English (UK)",
			Country.UnitedKingdom),

	English_Australia(Unicode.CURRENCY_DOLLAR, null, true, "enAU", "en_AU",
			"English (Australia)",
			Country.Australia),

	// could have esperanto.png for flag (but we don't)
	// Esperanto(null, false, "eo","eo", "Esperanto", "esperanto"),

	Estonian(Unicode.CURRENCY_EURO, null, true, "et", "et",
			"Estonian / Eesti keel", Country.Estonia),

	Filipino(null, null, true, "tl", "tl", "Filipino", Country.Philippines),

	Finnish(Unicode.CURRENCY_EURO, null, true, "fi", "fi", "Finnish / Suomi",
			Country.Finland),

	French(Unicode.CURRENCY_EURO, null, true, "fr", "fr",
			"French / Fran\u00E7ais", Country.France,
			Country.CongoDemocraticRepublicOf, Country.Canada,
			Country.Madagascar, Country.Cameroon, Country.CoteDivoire,
			Country.BurkinaFaso, Country.Niger, Country.Senegal, Country.Mali,
			Country.Rwanda, Country.Belgium, Country.Guinea, Country.Chad,
			Country.Haiti, Country.Burundi, Country.Benin, Country.Switzerland,
			Country.Togo, Country.CentralAfricanRepublic, Country.Congo,
			Country.Gabon, Country.Comoros, Country.EquatorialGuinea,
			Country.Djibouti, Country.Luxembourg, Country.Vanuatu,
			Country.Seychelles, Country.Monaco),

	Galician(Unicode.CURRENCY_EURO, null, true, "gl", "gl",
			"Galician / Galego", "Galician"),
	// fudge to get right flag

	Georgian(null,
			"\u10d8",
			true,
			"ka",
			"ka",
			"Georgian / \u10E5\u10D0\u10E0\u10D7\u10E3\u10DA\u10D8 \u10D4\u10DC\u10D0",
			Country.Georgia),

	// German must be before German_Austria
	German(Unicode.CURRENCY_EURO, null, true, "de", "de", "German / Deutsch",
			Country.Germany,
			Country.Liechtenstein, Country.Luxembourg, Country.Switzerland,
			Country.Belgium),

	German_Austria(Unicode.CURRENCY_EURO, null, true, "deAT", "de_AT",
			"German (Austria) / Deutsch (\u00D6sterreich)", Country.Austria),

	Greek(Unicode.CURRENCY_EURO, null, true, "el", "el",
			"Greek / \u0395\u03BB\u03BB\u03B7\u03BD\u03B9\u03BA\u03AC",
			Country.Greece, Country.Cyprus),

	Hebrew(Unicode.CURRENCY_SHEKEL, "\u05d9", true, "iw", "iw",
			"Hebrew / \u05E2\u05B4\u05D1\u05B0\u05E8\u05B4\u05D9\u05EA",
			Country.Israel),

	Hindi(
			Unicode.CURRENCY_INDIAN_RUPEE,
			"\u0be7",
			true,
			"hi",
			"hi",
			"Hindi / \u092E\u093E\u0928\u0915 \u0939\u093F\u0928\u094D\u0926\u0940",
			Country.India),

	Hungarian(Unicode.CURRENCY_EURO, null, true, "hu", "hu",
			"Hungarian / Magyar", Country.Hungary),

	Icelandic(null, null, true, "is", "is", "Icelandic / \u00CDslenska",
			Country.Iceland),

	Indonesian(null, null, true, "in", "in", "Indonesian / Bahasa Indonesia",
			Country.Indonesia),

	Italian(Unicode.CURRENCY_EURO, null, true, "it", "it",
			"Italian / Italiano", Country.Italy,
			Country.Switzerland, Country.SanMarino, Country.VaticanCityState),

	// Irish(null, false, "ga", "ga", "Irish / Gaeilge", Country.Ireland),

	Japanese(Unicode.CURRENCY_YEN, "\uff9d", true, "ja", "ja",
			"Japanese / \u65E5\u672C\u8A9E",
			Country.Japan),

	// Kannada("\u1103", false, "kn","kn", "Kannada", Country.India),

	Kazakh(null, null, true, "kk", "kk",
			"Kazakh / \u049A\u0430\u0437\u0430\u049B \u0442\u0456\u043B\u0456",
			Country.Kazakhstan),

	Korean(Unicode.CURRENCY_WON, "\u1103", true, "ko", "ko",
			"Korean / \uD55C\uAD6D\uB9D0",
			Country.KoreaRepublicof, Country.KoreaDemocraticPeoplesRepublicof),

	Latvian(Unicode.CURRENCY_EURO, null, true, "lv", "lv",
			"Latvian / Latvie\u0161u valoda",
			Country.Latvia),

	Lithuanian(Unicode.CURRENCY_EURO, null, true, "lt", "lt",
			"Lithuanian / Lietuvi\u0173 kalba",
			Country.Lithuania),

	Malay(null, null, true, "ms", "ms", "Malay / Bahasa Malaysia", Country.Malaysia,
			Country.Singapore, Country.Indonesia, Country.BruneiDarussalam),

	// Malayalam("\u0D2E", false, "ml","ml",
	// "Malayalam / \u0D2E\u0D32\u0D2F\u0D3E\u0D33\u0D02", Country.India),

	Macedonian(
			Unicode.CURRENCY_EURO,
			null,
			true,
			"mk",
			"mk",
			"Macedonian / \u041C\u0430\u043A\u0435\u0434\u043E\u043D\u0441\u043A\u0438 \u0458\u0430\u0437\u0438\u043A",
			Country.Macedonia),

	// Marathi("\u092e", false, "mr","mr",
	// "Marathi / \u092E\u0930\u093E\u0920\u0940", Country.India),

	Mongolian(
			Unicode.CURRENCY_TUGHRIK,
			null,
			true,
			"mn",
			"mn",
			"Mongolian / \u041C\u043E\u043D\u0433\u043E\u043B \u0445\u044D\u043B",
			Country.Mongolia),

	Nepalese(Unicode.CURRENCY_RUPEE, "\u0947", true, "ne", "ne",
			"Nepalese / \u0928\u0947\u092A\u093E\u0932\u0940", Country.Nepal),

	Norwegian_Bokmal(null, null, true, "noNO", "no_NB",
			"Norwegian / Bokm\u00e5l",
			Country.Norway),

	Norwegian_Nynorsk(null, null, true, "noNONY", "no_NN",
			"Norwegian / Nynorsk",
			Country.Norway),

	Persian(null, null, true, "fa", "fa", "Persian / \u0641\u0627\u0631\u0633\u06CC",
			Country.IranIslamicRepublicof, Country.Afghanistan,
			Country.Tajikistan),

	Polish(Unicode.CURRENCY_EURO, null, true, "pl", "pl",
			"Polish / J\u0119zyk polski", Country.Poland),

	// use Brazilian as the root (ie not ptBR) as there are more speakers
	Portuguese_Brazil(null, null, true, "pt", "pt",
			"Portuguese (Brazil) / Portugu\u00EAs (Brasil)", Country.Brazil),

	Portuguese_Portugal(Unicode.CURRENCY_EURO, null, true, "ptPT", "pt_PT",
			"Portuguese (Portugal) / Portugu\u00EAs (Portugal)",
			Country.Portugal, Country.Mozambique, Country.Angola,
			Country.CapeVerde, Country.GuineaBissau, Country.SaoTomePrincipe,
			Country.Macau, Country.EastTimor),

	Romanian(Unicode.CURRENCY_EURO, null, true, "ro", "ro", "Romanian /  Rom\u00E2n\u0103",
			Country.Romania, Country.MoldovaRepublicof),

	Russian(null,
			"\u0439",
			true,
			"ru",
			"ru",
			"Russian / \u0420\u0443\u0441\u0441\u043A\u0438\u0439 \u044F\u0437\u044B\u043A",
			Country.RussianFederation, Country.Kazakhstan, Country.Belarus,
			Country.Kyrgyzstan, Country.Tajikistan),

	Sinhala(Unicode.CURRENCY_RUPEE, "\u0d9a", true, "si", "si",
			"Sinhala / \u0DC3\u0DD2\u0D82\u0DC4\u0DBD", Country.SriLanka),

	Serbian(Unicode.CURRENCY_EURO, null, true, "sr", "sr",
			"Serbian / \u0441\u0440\u043F\u0441\u043A\u0438", Country.Serbia,
			Country.BosniaandHerzegovina),

	Slovak(Unicode.CURRENCY_EURO, null, true, "sk", "sk",
			"Slovak / Slovensk\u00FD jazyk",
			Country.Slovakia),

	Slovenian(Unicode.CURRENCY_EURO, null, true, "sl", "sl",
			"Slovenian / Sloven\u0161\u010Dina",
			Country.Slovenia),

	Spanish(Unicode.CURRENCY_DOLLAR, null, true, "es", "es",
			"Spanish / Espa\u00F1ol",
			Country.EquatorialGuinea, Country.Argentina, Country.Bolivia,
			Country.Chile, Country.Colombia, Country.CostaRica, Country.Cuba,
			Country.DominicanRepublic, Country.ElSalvador, Country.Guatemala,
			Country.Honduras, Country.Mexico, Country.Nicaragua,
			Country.Panama, Country.Paraguay, Country.Ecuador, Country.Peru,
			Country.Venezuela, Country.PuertoRico),

	Spanish_UY(Unicode.CURRENCY_DOLLAR, null, true, "esUY", "es_UY",
			"Spanish / Espa\u00F1ol (Uruguay)",
			Country.Uruguay),

	Spanish_ES(Unicode.CURRENCY_EURO, null, true, "esES", "es_ES",
			"Spanish / Espa\u00F1ol (Espa\u00F1a)", Country.Spain),

	Swedish(Unicode.CURRENCY_EURO, null, true, "sv", "sv", "Swedish / Svenska",
			Country.Sweden,
			Country.Finland),

	Tamil(Unicode.CURRENCY_RUPEE, "\u0be7", true, "ta", "ta",
			"Tamil / \u0BA4\u0BAE\u0BBF\u0BB4\u0BCD", Country.India,
			Country.SriLanka, Country.Singapore),

	// Tajik(null, false, "tg","tg", "Tajik", Country.Tajikistan),

	// Telugu("\u0C24", false, "te","te",
	// "Telugu / \u0C24\u0C46\u0C32\u0C41\u0C17\u0C41", Country.India),

	Thai(Unicode.CURRENCY_BAHT, null, true, "th", "th",
			"Thai / \u0E20\u0E32\u0E29\u0E32\u0E44\u0E17\u0E22",
			Country.Thailand),

	Turkish(Unicode.CURRENCY_EURO, null, true, "tr", "tr", "Turkish / T\u00FCrk\u00E7e",
			Country.Turkey, Country.Cyprus),

	Ukrainian(null, 
			null,
			true,
			"uk",
			"uk",
			"Ukrainian / \u0423\u043A\u0440\u0430\u0457\u043D\u0441\u044C\u043A\u0430 \u043C\u043E\u0432\u0430",
			Country.Ukraine),

	Uyghur(null, null, true, "ug", "ug", "Uyghur", Country.China),

	Vietnamese(Unicode.CURRENCY_DONG, null, true, "vi", "vi",
			"Vietnamese / Ti\u1EBFng Vi\u1EC7t",
			Country.VietNam),

	Welsh(null, null, true, "cy", "cy", "Welsh / Cymraeg",
			"wales"), 
			// fudge to get right flag

	Yiddish(Unicode.CURRENCY_SHEKEL, "\u05d9\u05b4", true, "ji", "ji",
			"Yiddish / \u05D9\u05D9\u05B4\u05D3\u05D9\u05E9", Country.Israel);

	// Interlingua(null, true, "ia", "ia", "Interlingua",
	// Country.UnitedStatesofAmerica);

	public String localeGWT;
	public String locale;
	public String name;
	// official counties which speak that language
	public Country[] countries;
	public String flagName;
	// used to determine whether to put in release versions
	public boolean fullyTranslated;
	/**
	 * test characters to get a font that can display the correct symbols for
	 * each language Also used to register the fonts so that JLaTeXMath can
	 * display other Unicode blocks
	 */
	public String testChar;
	private String currency;

	/**
	 * @param enableInGWT
	 *            currently not used
	 */
	Language(String currency, String testChar, boolean fullyTranslated,
			String locale,
			String localeGWT, String name, Country... countries) {
		this.currency = currency == null ? "$" : currency;
		this.locale = locale;
		this.localeGWT = localeGWT;
		this.name = name;
		this.countries = countries;
		this.flagName = null;
		this.fullyTranslated = fullyTranslated;
		this.testChar = testChar;

	}

	Language(String currency, String testChar, boolean fullyTranslated,
			String locale,
			String localeGWT, String name, String flagName) {
		this.currency = currency == null ? "$" : currency;
		this.locale = locale;
		this.localeGWT = localeGWT;
		this.name = name;
		this.countries = null;
		this.flagName = flagName;
		this.fullyTranslated = fullyTranslated;
		this.testChar = testChar;

	}

	private static String countryFromGeoIP = null;

	/**
	 * @param language
	 *            2 letter language, eg en
	 * @param country
	 *            2 letter country, eg GB
	 * @return
	 */
	public static String getCountry(App app, String language, String country,
			boolean useGeoIP) {

		Language lang = Language.getLanguage(language + country);

		if (lang.flagName != null) {
			// for languages without a ISO_3166-1_alpha-2 country, eg "wales"
			return lang.flagName;
		}

		Country[] c = lang.countries;

		// if eg country = GB, must return English_UK, AT -> German_Austria
		if (country != null) {
			for (Language l : Language.values()) {
				// App.debug("l.toString());
				// if (l.countries != null)
				// AbstractApplication.debug(l.countries[0]);
				if (l.countries != null
						&& l.countries[0].getISO().equals(country))
					return l.countries[0].getISO();
			}
		}

		if (useGeoIP) {

			try {

				if (countryFromGeoIP == null) {
					countryFromGeoIP = app.getCountryFromGeoIP();
				}

				// countryFromGeoIP = "BE";

				// fake for testing
				// countryFromGeoIP="IR";

				App.debug("country from GeoIP: " + countryFromGeoIP);

				for (int i = 0; i < c.length; i++) {
					// AbstractApplication.debug(c[i].getISO()+" "+countryFromGeoIP);
					if (c[i].getISO().equals(countryFromGeoIP)) {
						return countryFromGeoIP;
					}
				}
			} catch (Exception e) {
				Log.warn("Getting country code from geoip failed: "
						+ e.getMessage());
			}

		}

		// language isn't an official language for country, or error, so use
		// default flag
		return c[0].getISO();

	}

	private static Language getLanguage(String language) {
		for (Language l : Language.values()) {
			// language could be "ca" or "caXV"
			if (l.locale.equals(language)) {
				return l;
			}
		}

		for (Language l : Language.values()) {
			if (l.locale.substring(0, 2).equals(language)) {
				return l;
			}
		}

		App.error("language not recognized: " + language);
		return null;
	}

	public static String getDisplayName(String ggbLangCode) {

		// eg change en_GB to enGB
		ggbLangCode = ggbLangCode.replaceAll("_", "");

		App.debug("looking for: " + ggbLangCode);
		for (Language l : Language.values()) {
			if (l.locale.equals(ggbLangCode)
					|| l.localeGWT.replaceAll("_", "").equals(ggbLangCode)) {
				return l.name;
			}
		}

		App.error("language not found: " + ggbLangCode);

		return null;
	}

	/**
	 * 
	 * @param language
	 *            two letter code
	 * @return
	 */
	public static String getTestChar(String language) {
		for (Language l : Language.values()) {
			if (l.locale.startsWith(language)) {
				return l.testChar == null ? "a" : l.testChar;
			}
		}

		App.error("language not found: " + language);
		return "a";
	}

	public static String getClosestGWTSupportedLanguage(String browserLangCode) {
		String normalizedLanguage = StringUtil.toLowerCase(browserLangCode
				.replace("-", "_"));

		if ("he".equals(normalizedLanguage)) {
			normalizedLanguage = "iw";
		} else if ("zh_hans_cn".equals(normalizedLanguage)) {
			normalizedLanguage = "zh_cn";
		} else if ("zh_hant_tw".equals(normalizedLanguage)) {
			normalizedLanguage = "zh_tw";
		}
		// on iOS it's nb_no
		else if (normalizedLanguage.startsWith("nb")) {
			normalizedLanguage = "no_nb";
		} else if ("nn".equals(normalizedLanguage)) {
			normalizedLanguage = "no_nn";
		}

		// browserLangCode example: en-US, en-GB, pt-BR, pt-pt, and de-DE
		for (Language lang : Language.values()) {
			if (lang.localeGWT.toLowerCase().equals(normalizedLanguage)) {
				return lang.localeGWT;
			}
		}
		// look for mother language in the hierarchy ie. the first two
		// characters
		for (Language lang : Language.values()) {
			if (lang.localeGWT.toLowerCase().equals(
					normalizedLanguage.substring(0, 2))) {
				return lang.localeGWT;
			}

		}
		return null;

	}

	/**
	 * @param language
	 * @return the currency belonging to the given language (default Dollar)
	 */
	public static String getCurrency(String language) {
		for (Language l : Language.values()) {
			if (l.localeGWT.equals(language)) {
				return l.currency;
			}
		}
		return Unicode.CURRENCY_DOLLAR;
	}
}
