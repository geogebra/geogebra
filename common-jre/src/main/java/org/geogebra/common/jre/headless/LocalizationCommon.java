package org.geogebra.common.jre.headless;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.geogebra.common.jre.main.LocalizationJre;

/**
 * Common Localization class, used for testing.
 */
public class LocalizationCommon extends LocalizationJre {

	private static final String PROPERTIES_DIR = "org.geogebra.common.jre.properties.";

	/** path to javaui properties (without extension) */
	public static final String RB_JAVA_UI = PROPERTIES_DIR + "javaui";

	private static final String RB_MENU = PROPERTIES_DIR + "menu";
	private static final String RB_COMMAND = PROPERTIES_DIR + "command";
	private static final String RB_ERROR = PROPERTIES_DIR + "error";
	private static final String RB_SYMBOL = PROPERTIES_DIR + "symbols";
	private static final String RB_COLORS = PROPERTIES_DIR + "colors";

	private ResourceBundle.Control control;

	/**
	 * @param dimension 3 for 3D
	 */
	public LocalizationCommon(int dimension) {
		super(dimension);
		setResourceBundleControl(
				ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_PROPERTIES));
	}

	/**
	 * Specify a different ResourceBundle.Control type
	 * to control the loading of the properties files
	 * @param control control
	 */
	public void setResourceBundleControl(ResourceBundle.Control control) {
		this.control = control;
	}

	@Override
	protected ArrayList<Locale> getSupportedLocales() {
		return getSupportedLocales(
				app != null && hasAllLanguages());
	}

	@Override
	protected Locale getCommandLocale() {
		return getLocale();
	}

	@Override
	protected ResourceBundle createBundle(String key, Locale locale) {
		try {
			return ResourceBundle.getBundle(key, locale, control);
		} catch (MissingResourceException mre) {
			// running without resources: load empty bundle
			return new EmptyResourceBundle();
		}
	}

	@Override
	protected String getMenuRessourcePath() {
		return RB_MENU;
	}

	@Override
	protected String getCommandRessourcePath() {
		return RB_COMMAND;
	}

	@Override
	protected String getColorRessourcePath() {
		return RB_COLORS;
	}

	@Override
	protected String getErrorRessourcePath() {
		return RB_ERROR;
	}

	@Override
	protected String getSymbolRessourcePath() {
		return RB_SYMBOL;
	}

	private static class EmptyResourceBundle extends ResourceBundle {
		@Override
		protected Object handleGetObject(String key) {
			throw new MissingResourceException("Not found", getClass().getName(), key);
		}

		@Override
		public Enumeration<String> getKeys() {
			return Collections.emptyEnumeration();
		}
	}
}
