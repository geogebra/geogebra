/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
	protected String getMenuResourcePath() {
		return RB_MENU;
	}

	@Override
	protected String getCommandResourcePath() {
		return RB_COMMAND;
	}

	@Override
	protected String getColorResourcePath() {
		return RB_COLORS;
	}

	@Override
	protected String getErrorResourcePath() {
		return RB_ERROR;
	}

	@Override
	protected String getSymbolResourcePath() {
		return RB_SYMBOL;
	}

	private static final class EmptyResourceBundle extends ResourceBundle {
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
