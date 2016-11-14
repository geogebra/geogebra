package org.geogebra.common.jre.main;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.Unicode;

/**
 * common jre localization
 */
public abstract class LocalizationJre extends Localization {
	/** path to menu */
	public static final String RB_MENU = "/org/geogebra/desktop/properties/menu";
	/** path to commands */
	public static final String RB_COMMAND = "/org/geogebra/desktop/properties/command";
	private static final String RB_ERROR = "/org/geogebra/desktop/properties/error";
	private static final String RB_PLAIN = "/org/geogebra/desktop/properties/plain";
	private static final String RB_SYMBOL = "/org/geogebra/desktop/properties/symbols";
	/** path to javaui properties (without extension) */
	public static final String RB_JAVA_UI = "/org/geogebra/desktop/properties/javaui";
	private static final String RB_COLORS = "/org/geogebra/desktop/properties/colors";

	private ResourceBundle rbmenu, rbmenuTT, rbcommand, rbcommandOld, rberror,
			rbcolors, rbsymbol;

	private Locale tooltipLocale = null;
	private App app;

	/**
	 * @param dimension
	 *            3 for 3D
	 */
	public LocalizationJre(int dimension) {
		super(dimension, 15);
	}

	/**
	 * @param app
	 *            application
	 */
	final public void setApp(App app) {
		this.app = app;
	}

	@Override
	final public void setTooltipFlag() {
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
	final public String getMenu(String key) {

		if (tooltipFlag) {
			return getMenuTooltip(key);
		}

		if (rbmenu == null) {
			rbmenu = createBundle(RB_MENU, currentLocale);
		}

		try {
			return rbmenu.getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	/**
	 * 
	 * @param key
	 *            key name
	 * @param locale
	 *            locale
	 * @return bundle for key & locale
	 */
	abstract protected ResourceBundle createBundle(String key, Locale locale);

	@Override
	final public String getMenuTooltip(String key) {

		if (tooltipLocale == null) {
			return getMenu(key);
		}

		if (rbmenuTT == null) {
			rbmenuTT = createBundle(RB_MENU, tooltipLocale);
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
			rberror = createBundle(RB_ERROR, currentLocale);
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
	final public String getLanguage() {
		return getLocale().getLanguage();
	}

	@Override
	final public String getLocaleStr() {
		return getLocale().toString();
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


	private void initSymbolResourceBundle() {
		rbsymbol = createBundle(RB_SYMBOL, currentLocale);
	}

	@Override
	final public void initCommand() {
		if (rbcommand == null) {
			rbcommand = createBundle(RB_COMMAND, currentLocale);
		}

	}

	private void initColorsResourceBundle() {
		rbcolors = createBundle(RB_COLORS, currentLocale);
	}

	final protected void updateResourceBundles() {
		if (rbmenu != null) {
			rbmenu = createBundle(RB_MENU, currentLocale);
		}
		if (rberror != null) {
			rberror = createBundle(RB_ERROR, currentLocale);
		}

		if (rbcommand != null) {
			rbcommand = createBundle(RB_COMMAND, currentLocale);
		}
		if (rbcolors != null) {
			rbcolors = createBundle(RB_COLORS, currentLocale);
		}
		if (rbsymbol != null) {
			rbsymbol = createBundle(RB_SYMBOL, currentLocale);
		}
	}



	/**
	 * @return whether properties bundles were initiated (at least plain)
	 */
	final public boolean propertiesFilesPresent() {
		return rbmenu != null;
	}

	/**
	 * @param s
	 *            language for tooltips
	 * @return success
	 */
	final public boolean setTooltipLanguage(String s) {
		Locale locale = null;

		for (int i = 0; i < getSupportedLocales().size(); i++) {
			if (getSupportedLocales().get(i).toString().equals(s)) {
				locale = getSupportedLocales().get(i);
				break;
			}
		}

		boolean updateNeeded = (rbmenuTT != null);

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

	/**
	 * @return tootlip loacle
	 */
	final public Locale getTooltipLocale() {
		return tooltipLocale;
	}

	@Override
	final public String getTooltipLanguageString() {
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
	final protected boolean isCommandChanged() {
		// TODO Auto-generated method stub
		return rbcommandOld != rbcommand;
	}

	@Override
	final protected void setCommandChanged(boolean b) {
		rbcommandOld = rbcommand;

	}

	@Override
	final protected boolean isCommandNull() {
		return rbcommand == null;
	}

	/**
	 * 
	 * return East/West as appropriate for eg Hebrew / Arabic
	 * 
	 * return String rather than app.borderEast() so we're not dependent on awt
	 * 
	 * @return "East" or "West"
	 */
	final public String borderEast() {
		// return app.borderEast();
		if (isRightToLeftReadingOrder()) {
			return "West";
		}
		return "East";
	}

	/**
	 * 
	 * return East/West as appropriate for eg Hebrew / Arabic
	 * 
	 * return String rather than app.borderWest() so we're not dependent on awt
	 * 
	 * @return "West" or "East"
	 */
	final public String borderWest() {
		// return app.borderWest();
		if (!isRightToLeftReadingOrder()) {
			return "West";
		}
		return "East";
	}

}
