package org.geogebra.web.html5.main;

import java.util.Iterator;
import java.util.MissingResourceException;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.Unicode;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.css.StyleInjector;
import org.geogebra.web.html5.util.MyDictionary;

import com.google.gwt.user.client.ui.RootPanel;

public final class LocalizationW extends Localization {

	public LocalizationW(int dimension) {
		super(dimension, 13);
	}

	/**
	 * eg "en_GB", "es" // remains null until we're sure keys are loaded
	 */
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

	//
	/*
	 * eg __GGB__keysVar.en.command.Ellipse
	 */
	public native String getPropertyNative(String language, String key,
	        String section) /*-{

		if (!$wnd["__GGB__keysVar"]) {
			return "";
		}

		if ($wnd["__GGB__keysVar"][language]) {
			// translated
			return $wnd["__GGB__keysVar"][language][section][key];
		} else if ($wnd["__GGB__keysVar"]["en"]) { // English (probably available)
			return $wnd["__GGB__keysVar"]["en"][section][key];
		} else {
			return "";
		}

	}-*/;

	@Override
	public String getCommand(String key) {

		if (key == null) {
			return "";
		}

		if (language == null) {
			// keys not loaded yet
			return key;
		}

		String ret = getPropertyNative(language, key, "command");

		if (ret == null || "".equals(ret)) {
			App.debug("command key not found: " + key);
			return key;
		}

		return ret;

	}

	/**
	 * @author Rana This method should work for both if the getPlain and
	 *         getPlainTooltip. In the case of getPlainTooltip then getPlain is
	 *         called as secondary languages not supported in HTML5
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

		String ret = getPropertyNative(language, key, "plain");

		if (ret == null || "".equals(ret)) {
			// App.debug("plain key not found: "+key+" "+ret);
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

		String ret = getPropertyNative(language, key, "menu");

		if (ret == null || "".equals(ret)) {
			// App.debug("menu key not found: "+key);
			return key;
		}

		return ret;

	}

	/**
	 * @param key
	 *            String
	 * @param arg0
	 *            String
	 * @return String
	 */
	public String getMenu(String key, String arg0) {
		String str = getMenu(key);
		StringBuffer menuStr = new StringBuffer();
		menuStr.setLength(0);
		boolean found = false;
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (ch == '%') {
				// get number after %
				i++;
				menuStr.append(arg0);
				found = true;
			} else {
				menuStr.append(ch);
			}
		}

		if (!found) {
			/*
			 * If no parameters were found in key, this key is missing for some
			 * reason (maybe it is not added to the ggbtrans database yet). In
			 * this case all parameters are appended to the displayed string to
			 * help the developers.
			 */
			menuStr.append(" ");
			menuStr.append(arg0);
		}

		return menuStr.toString();
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

		String ret = getPropertyNative(language, key, "error");

		if (ret == null || "".equals(ret)) {
			App.debug("error key not found: " + key);
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

		String ret = getPropertyNative(language, "S_" + key, "symbols");

		if (ret == null || "".equals(ret)) {
			App.debug("menu key not found: " + key);
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

		String ret = getPropertyNative(language, "T_" + key, "symbols");

		if (ret == null || "".equals(ret)) {
			App.debug("menu key not found: " + key);
			return null;
		}

		return ret;
	}

	@Override
	public void setTooltipFlag() {
		// secondary languages not supported in HTML5
	}

	@Override
	public String reverseGetColor(String locColor) {
		String str = StringUtil.removeSpaces(StringUtil.toLowerCase(locColor));

		try {

			// Dictionary colorKeysDict =
			// Dictionary.getDictionary("__GGB__colors_"+language);
			MyDictionary colorKeysDict = MyDictionary.getDictionary("colors",
			        language);
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

	/**
	 * Following Java's convention, the return string should only include the
	 * language part of the locale. The assumption here that the "default"
	 * locale is English.
	 */
	@Override
	public String getLanguage() {
		return language == null ? null : language.substring(0, 2);
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

		// secondary languages not supported in HTML5

		return getPlain(key);
	}

	public void setLanguage(String lang) {
		if ("".equals(lang)) {
			language = "en";
		} else {
			language = lang;
		}

		setCommandChanged(true);

		App.debug("keys loaded for language: " + lang);
		App.debug("TODO: reinitialize GUI on language change");

		updateLanguageFlags(lang);

		// For styling on Firefox. (Mainly for rtl-languages.)
		if (rightToLeftReadingOrder) {
			RootPanel.getBodyElement().setAttribute("dir", "rtl");
		} else {
			RootPanel.getBodyElement().setAttribute("dir", "ltr");
		}
		StyleInjector
				.inject(rightToLeftReadingOrder ? GuiResourcesSimple.INSTANCE
						.generalStyleRTL() : GuiResourcesSimple.INSTANCE
						.generalStyleLTR());
		StyleInjector
				.inject(rightToLeftReadingOrder ? GuiResourcesSimple.INSTANCE
						.avStyleRTL() : GuiResourcesSimple.INSTANCE
						.avStyleLTR());
	}

	@Override
	public String getTooltipLanguageString() {

		// secondary languages not supported in HTML5

		return null;

	}

	@Override
	public String getMenuTooltip(String string) {
		// secondary languages not supported in HTML5
		return getMenu(string);
	}

	@Override
	public String getLocaleStr() {
		return language;
	}

}
