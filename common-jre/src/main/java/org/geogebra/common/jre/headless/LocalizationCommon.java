package org.geogebra.common.jre.headless;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import org.geogebra.common.jre.main.LocalizationJre;
import org.geogebra.common.main.Feature;

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
				app != null && app.has(Feature.ALL_LANGUAGES));
	}

	@Override
	protected Locale getCommandLocale() {
		return currentLocale;
	}

	@Override
	protected ResourceBundle createBundle(String key, Locale locale) {
		return ResourceBundle.getBundle(key, locale, control);
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

}
