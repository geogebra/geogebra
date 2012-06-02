package geogebra.common.util;

import geogebra.common.main.AbstractApplication;

/**
 * Collection of which languages are official in which countries
 * (only includes languages supported in GeoGebra)
 * 
 * @author michael@geogebra.org
 * http://en.wikipedia.org/wiki/List_of_official_languages
 * http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2
 */
@SuppressWarnings("javadoc")
public enum Language {
	Armenian("hy","hy", "Armenian / \u0540\u0561\u0575\u0565\u0580\u0565\u0576", Country.Armenia),
	Afrikaans ("af","af", "Afrikaans", Country.SouthAfrica),
	Bosnian("bs","bs", "Bosnian / \u0431\u043E\u0441\u0430\u043D\u0441\u043A\u0438", Country.BosniaandHerzegovina),
	Chinese_Simplified("zhCN","zhCN", "Chinese Simplified / \u7B80\u4F53\u4E2D\u6587", Country.China, Country.Singapore),
	Chinese_Traditional("zhTW","zhTW", "Chinese Traditional / \u7E41\u9AD4\u4E2D\u6587", Country.TaiwanProvinceofChina),
	English_US("en", "en",
			"English (US)", Country.UnitedStatesofAmerica,
			Country.AntiguaBarbuda, Country.Bahamas, Country.Barbados,
			Country.Belize, Country.Botswana, Country.Cameroon, Country.Canada,
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
	English_UK("enGB", "enGB", "English (UK)", Country.UnitedKingdom),
	English_Australia("enAU", "enAU", "English (Australia)", Country.Australia),

	// German must be before German_Austria
	German("de","de", "German / Deutsch", Country.Germany, Country.Liechtenstein, Country.Luxembourg, Country.Switzerland, Country.Belgium),
	German_Austria("deAT","deAT", "German (Austria) / Deutsch (\u00D6sterreich)", Country.Austria),
	
	Galician("gl","gl", "Galician / Galego", "Galician"), // fudge to get right flag
	Norwegian_Bokmal("noNO","noNB", "Norwegian / Bokm\u00e5l", Country.Norway ),
	Norwegian_Nynorsk("noNONY","noNN", "Norwegian / Nynorsk", Country.Norway),
	// use Brazilian as the root (ie not ptBR) as there are more speakers
	Portuguese_Brazil("pt","pt", "Portuguese (Brazil) / Portugu\u00EAs (Brasil)", Country.Brazil),
	Portuguese_Portugal("ptPT","ptPT", "Portuguese (Portugal) / Portugu\u00EAs (Portugal)", Country.Portugal, Country.Mozambique, Country.Angola, Country.CapeVerde, Country.GuineaBissau, Country.SaoTomePrincipe, Country.Macau, Country.EastTimor),
	Sinhala("si","si", "Sinhala / \u0DC3\u0DD2\u0D82\u0DC4\u0DBD", Country.SriLanka),
	Albanian("sq","sq", "Albanian / Gjuha Shqipe", Country.Albania),
	Arabic("ar", "ar", "Arabic / \u0627\u0644\u0639\u0631\u0628\u064A\u0629",
			Country.Egypt, Country.Algeria, Country.Bahrain, Country.Chad,
			Country.Comoros, Country.Djibouti, Country.Eritrea, Country.Iraq,
			Country.Jordan, Country.Kuwait, Country.Lebanon, Country.Libya,
			Country.Mauritania, Country.Morocco, Country.Oman,
			Country.Palestine, Country.Qatar, Country.SaudiArabia,
			Country.Somalia, Country.Sudan, Country.Syria, Country.Tunisia,
			Country.UnitedArabEmirates, Country.WesternSahara, Country.Yemen),	Basque("eu","eu", "Basque / Euskara", "basque"), // fudge to get right flag
	Bulgarian("bg","bg", "Bulgarian / \u0431\u044A\u043B\u0433\u0430\u0440\u0441\u043A\u0438 \u0435\u0437\u0438\u043A", Country.Bulgaria),
	Catalan("ca","ca", "Catalan / Catal\u00E0", "catalonia"), // fudge to get right flag
	Croatian("hr","hr", "Croatian / Hrvatska", Country.Croatia, Country.BosniaandHerzegovina),
	Czech("cs","cs", "Czech / \u010Ce\u0161tina", Country.CzechRepublic),
	Danish("da","da", "Danish / Dansk", Country.Denmark),
	Dutch("nl","nl", "Dutch / Nederlands", Country.Netherlands, Country.Belgium, Country.Suriname),
	Estonian("et","et", "Estonian / Eesti keel", Country.Estonia),
	Finnish("fi","fi", "Finnish / Suomi", Country.Finland),
	French("fr", "fr",
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
	Georgian("ka","ka", "Georgian / \u10E5\u10D0\u10E0\u10D7\u10E3\u10DA\u10D8 \u10D4\u10DC\u10D0", Country.Georgia),
	Greek("el","el", "Greek / \u0395\u03BB\u03BB\u03B7\u03BD\u03B9\u03BA\u03AC", Country.Greece, Country.Cyprus),
	Hebrew("iw","iw", "Hebrew / \u05E2\u05B4\u05D1\u05B0\u05E8\u05B4\u05D9\u05EA", Country.Israel),
	Hindi("hi","hi", "Hindi / \u092E\u093E\u0928\u0915 \u0939\u093F\u0928\u094D\u0926\u0940", Country.India),
	Hungarian("hu","hu", "Hungarian / Magyar", Country.Hungary),
	Icelandic("is","is", "Icelandic / \u00CDslenska", Country.Iceland),
	Indonesian("in","in", "Indonesian / Bahasa Indonesia", Country.Indonesia),
	Italian("it","it", "Italian / Italiano", Country.Italy, Country.Switzerland, Country.SanMarino, Country.VaticanCityState),
	Japanese("ja","ja", "Japanese / \u65E5\u672C\u8A9E", Country.Japan),
	Kazakh("kk","kk", "Kazakh / \u049A\u0430\u0437\u0430\u049B \u0442\u0456\u043B\u0456", Country.Kazakhstan),
	Korean("ko","ko", "Korean / \uD55C\uAD6D\uB9D0", Country.KoreaRepublicof, Country.KoreaDemocraticPeoplesRepublicof),
	Lithuanian("lt","lt", "Lithuanian / Lietuvi\u0173 kalba", Country.Lithuania),
	Malayalam("ml","ml", "Malayalam / \u0D2E\u0D32\u0D2F\u0D3E\u0D33\u0D02", Country.India),
	Mongolian("mn","mn", "Mongolian / \u041C\u043E\u043D\u0433\u043E\u043B \u0445\u044D\u043B", Country.Mongolia),
	Macedonian("mk","mk", "Macedonian / \u041C\u0430\u043A\u0435\u0434\u043E\u043D\u0441\u043A\u0438 \u0458\u0430\u0437\u0438\u043A", Country.Macedonia),
	Marathi("mr","mr", "Marathi / \u092E\u0930\u093E\u0920\u0940", Country.India),
	Malay("ms","ms", "Malay / Bahasa Malaysia", Country.Malaysia, Country.Singapore, Country.Indonesia, Country.BruneiDarussalam),
	Nepalese("ne","ne", "Nepalese / \u0928\u0947\u092A\u093E\u0932\u0940", Country.Nepal),
	Persian("fa","fa", "Persian / \u0641\u0627\u0631\u0633\u06CC", Country.IranIslamicRepublicof, Country.Afghanistan, Country.Tajikistan),
	Polish("pl","pl", "Polish / J\u0119zyk polski", Country.Poland),
	Romanian("ro","ro", "Romanian /  Rom\u00E2n\u0103", Country.Romania, Country.MoldovaRepublicof),
	Russian("ru","ru", "Russian / \u0420\u0443\u0441\u0441\u043A\u0438\u0439 \u044F\u0437\u044B\u043A", Country.RussianFederation, Country.Kazakhstan, Country.Belarus, Country.Kyrgyzstan, Country.Tajikistan),
	Serbian("sr","sr", "Serbian / \u0441\u0440\u043F\u0441\u043A\u0438", Country.Serbia, Country.BosniaandHerzegovina),
	Slovakian("sk","sk", "Slovakian / Slovensk\u00FD jazyk", Country.Slovakia),
	Slovenian("sl","sl", "Slovenian / Sloven\u0161\u010Dina", Country.Slovenia),
	Spanish("es", "es", "Spanish / Espa\u00F1ol", Country.Spain,
			Country.EquatorialGuinea, Country.Argentina, Country.Bolivia,
			Country.Chile, Country.Colombia, Country.CostaRica, Country.Cuba,
			Country.DominicanRepublic, Country.ElSalvador, Country.Guatemala,
			Country.Honduras, Country.Mexico, Country.Nicaragua,
			Country.Panama, Country.Paraguay, Country.Ecuador, Country.Peru,
			Country.Uruguay, Country.Venezuela, Country.PuertoRico),
	Swedish("sv","sv", "Swedish / Svenska", Country.Sweden, Country.Finland),
	Tamil("ta","ta", "Tamil / \u0BA4\u0BAE\u0BBF\u0BB4\u0BCD", Country.India, Country.Singapore),
	Filipino("tl","tl", "Filipino", Country.Philippines),
	Thai("th","th", "Thai / \u0E20\u0E32\u0E29\u0E32\u0E44\u0E17\u0E22", Country.Thailand),
	Turkish("tr","tr", "Turkish / T\u00FCrk\u00E7e", Country.Turkey, Country.Cyprus),
	Ukrainian("uk","uk", "Ukrainian / \u0423\u043A\u0440\u0430\u0457\u043D\u0441\u044C\u043A\u0430 \u043C\u043E\u0432\u0430", Country.Ukraine),
	Vietnamese("vi","vi", "Vietnamese / Ti\u1EBFng Vi\u1EC7t", Country.VietNam),
	Welsh("cy","cy", "Welsh / Cymraeg", "wales"), // fudge to get right flag
	Yiddish("ji","ji", "Yiddish / \u05D9\u05D9\u05B4\u05D3\u05D9\u05E9", Country.Israel),
	Interlingua("ia", "ia", "Interlingua", Country.UnitedStatesofAmerica);


	public String localeGWT;
	public String locale;
	public String name;
	// offical counties which speak that language
	public Country[] countries;
	public String flagName;
	
	Language(String locale,String localeGWT, String name, Country ... countries) {
		this.locale = locale;
		this.localeGWT = localeGWT;
		this.name = name;
		this.countries = countries;
		this.flagName = null;
		
	}
	Language(String locale,String localeGWT, String name, String flagName) {
		this.locale = locale;
		this.localeGWT = localeGWT;
		this.name = name;
		this.countries = null;
		this.flagName = flagName;
		
	}
	
	
	/**
	 * @param language 2 letter language, eg en
	 * @param country 2 letter country, eg GB
	 * @return
	 */
	public static String getCountry(AbstractApplication app, String language, String country, boolean useGeoIP) {
		//AbstractApplication.debug(language+" "+country);
		
		Language lang = Language.getLanguage(language);
		
		if (lang.flagName != null) {
			// for languages without a ISO_3166-1_alpha-2 country, eg "wales"
			return lang.flagName;
		}
		
		Country[] c = lang.countries;

		// if eg country = GB, must return English_UK, AT -> German_Austria
		if (country != null) {
			for (Language l : Language.values()) {
				//AbstractApplication.debug(l.toString());
				//if (l.countries != null) AbstractApplication.debug(l.countries[0]);
				if (l.countries != null && l.countries[0].getISO().equals(country))
					return l.countries[0].getISO();
			}
		}
		
		if (useGeoIP) {
   			
       		try {
   						
			String countryFromGeoIP = app.getCountryFromGeoIP();
						
			// fake for testing
			//countryFromGeoIP="IR";
			
			AbstractApplication.debug("country from GeoIP: "+countryFromGeoIP);

			for (int i = 0 ; i < c.length ; i++) {
				//AbstractApplication.debug(c[i].getISO()+" "+countryFromGeoIP);
				if (c[i].getISO().equals(countryFromGeoIP)) {
					return country;
				}
			}	
		} catch (Exception e) {
			AbstractApplication.warn("Getting country code from geoip failed: "+e.getMessage());
		}
       		
       		
		}
		
		// language isn't an official language for country, or error, so use default flag
		return c[0].getISO();
		
		
	}
	private static Language getLanguage(String language) {
		for (Language l : Language.values()) {
			if (l.locale.substring(0, 2).equals(language)) {
				return l;
			}
		}
		AbstractApplication.error("language not recognized: "+language);
		return null;
	}
	public static String getDisplayName(String ggbLangCode) {
		
		// eg change en_GB to enGB
		ggbLangCode = ggbLangCode.replaceAll("_", "");
		
		AbstractApplication.debug("looking for: "+ggbLangCode);
		for (Language l : Language.values()) {
			if (l.locale.equals(ggbLangCode)) {
				return l.name;
			}
		}
		
		AbstractApplication.error("language not found: "+ggbLangCode);
		
		return null;
	}
}
