package geogebra.main;

import geogebra.common.GeoGebraConstants;
import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.util.Language;
import geogebra.common.util.StringUtil;
import geogebra.common.util.Unicode;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

public class LocalizationD extends Localization {
	
	static final String RB_MENU = "/geogebra/properties/menu";
	static final String RB_COMMAND = "/geogebra/properties/command";
	private static final String RB_ERROR = "/geogebra/properties/error";
	private static final String RB_PLAIN = "/geogebra/properties/plain";
	private static final String RB_SYMBOL = "/geogebra/properties/symbols";
	/** path to javaui properties (without extension) */
	public static final String RB_JAVA_UI = "/geogebra/properties/javaui";
	private static final String RB_COLORS = "/geogebra/properties/colors";
	
	
	private ResourceBundle rbmenu, rbmenuTT, rbcommand, 
	 rbcommandOld,  rberror,
	rbcolors, rbplain, rbplainTT, rbsymbol;

	private Locale currentLocale;
	private Locale tooltipLocale = null;
	private App app;
	
	public LocalizationD() {
	    super(15);
    }

	public void setApp(App app){
		this.app = app;
	}
	
	public void setTooltipFlag() {
		if (tooltipLocale != null) {
			tooltipFlag = true;
		}
	}
	
	@Override
	final public String getCommand(String key) {

		app.initTranslatedCommands();

		try {
			return rbcommand.getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	@Override
	final public String getPlain(String key) {

		if (tooltipFlag) {
			return getPlainTooltip(key);
		}

		if (rbplain == null) {
			initPlainResourceBundle();
		}

		try {
			return rbplain.getString(key);
		} catch (Exception e) {
			return key;
		}
	}
	
	@Override
	final public String getPlainTooltip(String key) {

		if (tooltipLocale == null) {
			return getPlain(key);
		}

		if (rbplainTT == null) {
			initPlainTTResourceBundle();
		}

		try {
			return rbplainTT.getString(key);
		} catch (Exception e) {
			return key;
		}
	}
	
	private void initPlainTTResourceBundle() {
		rbplainTT = MyResourceBundle.createBundle(RB_PLAIN, tooltipLocale);
	}

	@Override
	final public String getMenu(String key) {

		if (tooltipFlag) {
			return getMenuTooltip(key);
		}

		if (rbmenu == null) {
			rbmenu = MyResourceBundle.createBundle(RB_MENU, currentLocale);
		}

		try {
			return rbmenu.getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	final public String getMenuTooltip(String key) {

		if (tooltipLocale == null) {
			return getMenu(key);
		}

		if (rbmenuTT == null) {
			rbmenuTT = MyResourceBundle.createBundle(RB_MENU, tooltipLocale);
		}

		try {
			return rbmenuTT.getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	@Override
	final public String getError(String key) {
		if (rberror == null) {
			rberror = MyResourceBundle.createBundle(RB_ERROR, currentLocale);
		}

		try {
			return rberror.getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	@Override
	final public String getSymbol(int key) {
		if (rbsymbol == null) {
			initSymbolResourceBundle();
		}

		String ret = null;

		try {
			ret = rbsymbol.getString("S." + key);
		} catch (Exception e) {
			// do nothing
		}

		if ("".equals(ret)) {
			return null;
		}
		return ret;
	}

	@Override
	public String getLanguage() {
		return getLocale().getLanguage();
	}

	@Override
	protected String getSyntaxString() {
		return Localization.syntaxStr;
	}

	@Override
	final public String getSymbolTooltip(int key) {
		if (rbsymbol == null) {
			initSymbolResourceBundle();
		}

		String ret = null;

		try {
			ret = rbsymbol.getString("T." + key);
		} catch (Exception e) {
			// do nothing
		}

		if ("".equals(ret)) {
			return null;
		}
		return ret;
	}
	
	private void initPlainResourceBundle() {
		rbplain = MyResourceBundle.createBundle(RB_PLAIN, currentLocale);
		if (rbplain != null) {
			app.getKernel().updateLocalAxesNames();
		}
	}
	
	private void initSymbolResourceBundle() {
		rbsymbol = MyResourceBundle.createBundle(RB_SYMBOL, currentLocale);
	}
	
	@Override
	public void initCommand() {
		if (rbcommand == null) {
			rbcommand = MyResourceBundle
					.createBundle(RB_COMMAND, currentLocale);
		}

	}
	
	private void initColorsResourceBundle() {
		rbcolors = MyResourceBundle.createBundle(RB_COLORS, currentLocale);
	}
	
	private void updateResourceBundles() {
		if (rbmenu != null) {
			rbmenu = MyResourceBundle.createBundle(RB_MENU, currentLocale);
		}
		if (rberror != null) {
			rberror = MyResourceBundle.createBundle(RB_ERROR, currentLocale);
		}
		if (rbplain != null) {
			rbplain = MyResourceBundle.createBundle(RB_PLAIN, currentLocale);
		}
		if (rbcommand != null) {
			rbcommand = MyResourceBundle
					.createBundle(RB_COMMAND, currentLocale);
		}
		if (rbcolors != null) {
			rbcolors = MyResourceBundle.createBundle(RB_COLORS, currentLocale);
		}
		if (rbsymbol != null) {
			rbsymbol = MyResourceBundle.createBundle(RB_SYMBOL, currentLocale);
		}
	}
	
	/**
	 * Returns a locale object that has the same country and/or language as
	 * locale. If the language of locale is not supported an English locale is
	 * returned.
	 */
	private static Locale getClosestSupportedLocale(Locale locale) {
		int size = getSupportedLocales().size();

		// try to find country and variant
		String country = locale.getCountry();
		String variant = locale.getVariant();

		if (country.length() > 0) {
			for (int i = 0; i < size; i++) {
				Locale loc = getSupportedLocales().get(i);
				if (country.equals(loc.getCountry())
						&& variant.equals(loc.getVariant())) {
					// found supported country locale
					return loc;
				}
			}
		}

		// try to find language
		String language = locale.getLanguage();
		for (int i = 0; i < size; i++) {
			Locale loc = getSupportedLocales().get(i);
			if (language.equals(loc.getLanguage())) {
				// found supported country locale
				return loc;
			}
		}

		// we didn't find a matching country or language,
		// so we take English
		return Locale.ENGLISH;
	}
	
	// supported GUI languages (from properties files)
		private static ArrayList<Locale> supportedLocales = null;

		public static ArrayList<Locale> getSupportedLocales() {
			return getSupportedLocales(GeoGebraConstants.IS_PRE_RELEASE);
		}

		public static ArrayList<Locale> getSupportedLocales(boolean prerelease) {

			if (supportedLocales != null) {
				return supportedLocales;
			}

			supportedLocales = new ArrayList<Locale>();

			Language[] languages = Language.values();

			for (int i = 0; i < languages.length; i++) {

				Language language = languages[i];

				if (language.fullyTranslated || prerelease) {

					if (language.locale.length() == 2) {
						// eg "en"
						supportedLocales.add(new Locale(language.locale));
					} else if (language.locale.length() == 4) {
						// eg "enGB" -> "en", "GB"
						supportedLocales.add(new Locale(language.locale.substring(
								0, 2), language.locale.substring(2, 4)));
					} else if (language.locale.length() == 6) {
						// eg "noNONY" -> "no", "NO", "NY"
						supportedLocales.add(new Locale(language.locale.substring(
								0, 2), language.locale.substring(2, 4),
								language.locale.substring(4, 6)));
					}
				}

			}

			return supportedLocales;

		}

		public Locale getLocale() {
			return currentLocale;
		}

		public void setLocale(Locale locale) {
			currentLocale = getClosestSupportedLocale(locale);
			updateResourceBundles();
			
		}
		
		/**
		 * @return whether properties bundles were initiated (at least plain)
		 */
		public boolean propertiesFilesPresent() {
			return rbplain != null;
		}

		public boolean setTooltipLanguage(String s) {
			Locale locale = null;

			for (int i = 0; i < getSupportedLocales().size(); i++) {
				if (getSupportedLocales().get(i).toString().equals(s)) {
					locale = getSupportedLocales().get(i);
					break;
				}
			}

			boolean updateNeeded = (rbplainTT != null) || (rbmenuTT != null);

			rbplainTT = null;
			rbmenuTT = null;

			if (locale == null) {
				tooltipLocale = null;
			} else if (currentLocale.toString().equals(locale.toString())) {
				tooltipLocale = null;
			} else {
				tooltipLocale = locale;
			}
			return updateNeeded;
		}

		public Locale getTooltipLocale() {
			return tooltipLocale;
		}
		
		public String getTooltipLanguageString() {
			if (tooltipLocale == null)
				return null;
			return tooltipLocale.toString();
		}
		
		@Override
		final public String getColor(String key) {

			if (key == null) {
				return "";
			}

			if ((key.length() == 5)
					&& StringUtil.toLowerCase(key).startsWith("gray")) {
				switch (key.charAt(4)) {
				case '0':
					return getColor("white");
				case '1':
					return getPlain("AGray", Unicode.fraction1_8);
				case '2':
					return getPlain("AGray", Unicode.fraction1_4); // silver
				case '3':
					return getPlain("AGray", Unicode.fraction3_8);
				case '4':
					return getPlain("AGray", Unicode.fraction1_2);
				case '5':
					return getPlain("AGray", Unicode.fraction5_8);
				case '6':
					return getPlain("AGray", Unicode.fraction3_4);
				case '7':
					return getPlain("AGray", Unicode.fraction7_8);
				default:
					return getColor("black");
				}
			}

			if (rbcolors == null) {
				initColorsResourceBundle();
			}

			try {
				return rbcolors.getString(StringUtil.toLowerCase(key));
			} catch (Exception e) {
				return key;
			}
		}
		
		@Override
		final public String reverseGetColor(String locColor) {
			String str = StringUtil.removeSpaces(StringUtil.toLowerCase(locColor));
			if (rbcolors == null) {
				initColorsResourceBundle();
			}

			try {

				Enumeration<String> enumer = rbcolors.getKeys();
				while (enumer.hasMoreElements()) {
					String key = enumer.nextElement();
					if (str.equals(StringUtil.removeSpaces(StringUtil
							.toLowerCase(rbcolors.getString(key))))) {
						return key;
					}
				}

				return str;
			} catch (Exception e) {
				return str;
			}
		}
		
		@Override
		protected boolean isCommandChanged() {
			// TODO Auto-generated method stub
			return rbcommandOld != rbcommand;
		}

		@Override
		protected void setCommandChanged(boolean b) {
			rbcommandOld = rbcommand;

		}

		@Override
		protected boolean isCommandNull() {
			return rbcommand == null;
		}

}
