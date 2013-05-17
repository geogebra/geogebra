package geogebra.html5.main;

import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.util.StringUtil;
import geogebra.common.util.Unicode;
import geogebra.html5.util.MyDictionary;

import java.util.Iterator;
import java.util.MissingResourceException;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.RootPanel;

public class LocalizationW extends Localization {

	public LocalizationW() {
	    super(13);
    }

	/** eg "en_GB", "es"
	// remains null until we're sure keys are loaded*/
	String language = "en";
	
	private boolean commandChanged = true;
	/**
	 * Constants related to internationalization
	 * 
	 */
	public final static String DEFAULT_LANGUAGE = "en";
	public final static String DEFAULT_LOCALE = "default";
	

	/*
	 * The representation of no_NO_NY (Norwegian Nynorsk) is illegal in a BCP47
	 * language tag: it should actually use "nn" (Norwegian Nynorsk) for the
	 * language field
	 * 
	 * @Ref:
	 * https://sites.google.com/site/openjdklocale/design-specification#TOC
	 * -Norwegian
	 */
	public final static String LANGUAGE_NORWEGIAN_NYNORSK = "no_NO_NY"; // Nynorsk
																		// Norwegian
																		// language
																		// Java
																		// Locale
	public final static String LANGUAGE_NORWEGIAN_NYNORSK_BCP47 = "nn"; // Nynorsk
																		// Norwegian
																		// language
																		// BCP47
	public final static String A_DOT = ".";
	public final static String AN_UNDERSCORE = "_";
	
	//
	/*
	 * eg __GGB__keysVar.en.command.Ellipse
	 */
	public native String getPropertyNative(String language, String key, String section) /*-{
		
		//if (!$wnd["__GGB__keysVar"]) {
		//	return "languagenotloaded";
		//}
		
		if ($wnd["__GGB__keysVar"][language]) {
			// translated
			return $wnd["__GGB__keysVar"][language][section][key];
		} else {
			// English (always available)
			return $wnd["__GGB__keysVar"]["en"][section][key];
		}
		
	}-*/;
	
	/**
	 * @author Rana Cross-Referencing properties keys: from old system of
	 *         properties keys' naming convention to new GWt compatible system
	 *         The old naming convention used dots in the syntax of keys in the
	 *         properties files. Since dots are not allowed in syntaxes of
	 *         methods (refer to GWT Constants and ConstantsWithLookup
	 *         interfaces), the new naming convention uses underscore instead of
	 *         dots. And since we are still using the old naming convention in
	 *         passing the key, we need to cross-reference.
	 */
	public static String crossReferencingPropertiesKeys(String key) {

		if (key == null) {
			return "";
		}

		String aStr = null;
		if (key.equals("X->Y")) {
			aStr = "X_Y";
		} else if (key.equals("Y<-X")) {
			aStr = "Y_X";
		} else {
			aStr = key;
		}

		return aStr.replace(A_DOT, AN_UNDERSCORE);
	}
	
	@Override
	public String getCommand(String key) {

		if (key == null) {
			return "";
		}
		
		if (language == null) {
			// keys not loaded yet
			return key;
		}


		String ret = getPropertyNative(language, crossReferencingPropertiesKeys(key), "command");
		
		if (ret == null || "".equals(ret)) {
			App.debug("command key not found: "+key);
			return key;
		}
		
		return ret;

}

	/**
	 * @author Rana This method should work for both if the getPlain and
	 *         getPlainTooltip. In the case of getPlainTooltip, if the
	 *         tooltipFlag is true, then getPlain is called.
	 */
	@Override
	public String getPlain(String key) {

		if (key == null) {
			return "";
		}
		
		if (language == null) {
			// keys not loaded yet
			return key;
		}

		if (language == null) {
			// keys not loaded yet
			return key;
		}

		String ret = getPropertyNative(language, crossReferencingPropertiesKeys(key), "plain");
		
		if (ret == null || "".equals(ret)) {
			App.debug("plain key not found: "+key+" "+ret);
			return key;
		}
		
		return ret;
	}


	/**
	 * @author Rana This method should work for both menu and menu tooltips
	 *         items
	 */
	@Override
	public String getMenu(String key) {

		if (key == null) {
			return "";
		}
		
		if (language == null) {
			// keys not loaded yet
			return key;
		}

		String ret = getPropertyNative(language, crossReferencingPropertiesKeys(key), "menu");
		
		if (ret == null || "".equals(ret)) {
			App.debug("menu key not found: "+key);
			return key;
		}
		
		return ret;
	
	}

	@Override
	public String getError(String key) {

		if (key == null) {
			return "";
		}
		
		if (language == null) {
			// keys not loaded yet
			return key;
		}

		String ret = getPropertyNative(language, crossReferencingPropertiesKeys(key), "error");
		
		if (ret == null || "".equals(ret)) {
			App.debug("error key not found: "+key);
			return key;
		}
		
		return ret;
	}

	@Override
	final public String getSymbol(int key) {

		if (language == null) {
			// keys not loaded yet
			return null;
		}

		String ret = getPropertyNative(language, "S_"+key, "symbols");
		
		if (ret == null || "".equals(ret)) {
			App.debug("menu key not found: "+key);
			return null;
		}
		
		return ret;
	}

	@Override
	final public String getSymbolTooltip(int key) {

		if (language == null) {
			// keys not loaded yet
			return null;
		}

		String ret = getPropertyNative(language, "T_"+key, "symbols");
		
		if (ret == null || "".equals(ret)) {
			App.debug("menu key not found: "+key);
			return null;
		}
		
		return ret;
	}


	/**
	 * @author Rana Since we are not implementing at this stage a secondary
	 *         language for tooltips The default behavior of setTooltipFlag()
	 *         will be to set the member variable tooltipFlag to true
	 */
	@Override
	public void setTooltipFlag() {
		tooltipFlag = true;
	}
	
	@Override
	public String reverseGetColor(String locColor) {
		String str = StringUtil.removeSpaces(StringUtil.toLowerCase(locColor));

		try {

			//Dictionary colorKeysDict = Dictionary.getDictionary("__GGB__colors_"+language);
			MyDictionary colorKeysDict = MyDictionary.getDictionary("colors", language);
			Iterator<String> colorKeysIterator = colorKeysDict.keySet()
			        .iterator();
			while (colorKeysIterator != null && colorKeysIterator.hasNext()) {
				String key = colorKeysIterator.next();
				if (key != null
				        && str.equals(StringUtil.removeSpaces(StringUtil
				                .toLowerCase(this.getColor(key))))) {
					return key;
				}
			}

			return str;
		} catch (MissingResourceException e) {
			return str;
		}
	}

	@Override
	public String getColor(String key) {

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

		return key;

	}

	@Override
	protected String getSyntaxString() {
		return syntaxStr;
	}
	
	/**
	 * Following Java's convention, the return string should only include the
	 * language part of the locale. The assumption here that the "default"
	 * locale is English.
	 */
	@Override
	public String getLanguage() {		
		return language;
	}
	
	@Override
	protected boolean isCommandChanged() {
		return commandChanged;
	}

	@Override
	protected void setCommandChanged(boolean b) {
		commandChanged = b;
	}

	@Override
	protected boolean isCommandNull() {
		return false;
	}

	@Override
	public void initCommand() {
		//
	}
	
	@Override
	public String getPlainTooltip(String key) {

		if (tooltipFlag) {
			return getPlain(key);
		}

		return null;
	}

	public void setLanguage(String lang) {
		if ("".equals(lang)) {
			language = "en";
		} else {
			language = lang;
		}
		
		

		setCommandChanged(true);
		
		App.debug("keys loaded for language: "+lang);
		App.debug("TODO: reinitialize GUI on language change");

		
		updateLanguageFlags(lang);
	    
		//For styling on Firefox. (Mainly for rtl-languages.)
		if (rightToLeftReadingOrder) {
			RootPanel.getBodyElement().setAttribute("dir", "rtl");
		} else {
			RootPanel.getBodyElement().setAttribute("dir", "ltr");
		}
    }
	
	@Override
	public String getTooltipLanguageString() {

		String localeName = LocaleInfo.getCurrentLocale().getLocaleName();
		if (localeName != null && !"".equals(localeName)) {
			if (localeName.equals(LANGUAGE_NORWEGIAN_NYNORSK_BCP47)) {
				return LANGUAGE_NORWEGIAN_NYNORSK;
			}
			return localeName;
		}
		return DEFAULT_LANGUAGE;

	}

	@Override
    public String getMenuTooltip(String string) {
	    // TODO Auto-generated method stub
	    return getMenu(string);
    }

}
