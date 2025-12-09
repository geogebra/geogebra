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

package org.geogebra.desktop.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.geogebra.common.util.debug.Log;

/**
 * MyResourceBundle.createBundle() should be used in stead of
 * ResourceBundle.getBundle() because it does not open network connections when
 * used with an applet.
 */

public class MyResourceBundle extends PropertyResourceBundle {

	/**
	 * Creates new bundle from a stream
	 *
	 * @param in
	 *            input stream
	 * @throws IOException
	 *             if problem occurs reading the stream
	 */
	public MyResourceBundle(InputStream in) throws IOException {
		super(new InputStreamReader(in, StandardCharsets.UTF_8));
	}

	/**
	 * Creates bundle given the filename and locale
	 *
	 * @param name
	 *            filename of .properties file (without extension
	 * @param locale
	 *            language locale
	 * @return created bundle
	 */
	final public static ResourceBundle createBundle(String name,
			Locale locale) {
		MyResourceBundle bundle, temp = null;

		// base properties file
		bundle = loadSingleBundleFile(name);
		if (bundle == null) {
			return null;
		}

		// language properties file
		String lang = locale.getLanguage();
		StringBuilder sb = new StringBuilder(name);
		sb.append('_');
		sb.append(lang);
		String fileNameLanguage = sb.toString();

		// load only non-English languages (English has already been loaded as
		// base file)
		if (!"en".equals(lang)) {
			temp = loadSingleBundleFile(fileNameLanguage);
		}

		if (temp != null) {
			temp.setParent(bundle);
			bundle = temp;
		}

		// country and variant properties file
		String country = locale.getCountry();
		if (country.length() > 0) {
			// check for variant
			String variant = locale.getVariant();
			if (variant.length() > 0) {
				// country and variant
				sb.append('_');
				sb.append(country);

				// needed for "no_NO_NY"
				// we need to load "no_NO", not "no"
				temp = loadSingleBundleFile(sb.toString());
				if (temp != null) {
					temp.setParent(bundle);
					bundle = temp;
				}

				sb.append('_');
				sb.append(variant);
			} else {
				// only country
				sb.append('_');
				sb.append(country);
			}

			String fileNameLanguageCountry = sb.toString();
			temp = loadSingleBundleFile(fileNameLanguageCountry);
			if (temp != null) {
				temp.setParent(bundle);
				bundle = temp;
			}
		}
		return bundle;
	}

	/**
	 * Creates bundle from given file, but in single language only
	 *
	 * @param name
	 *            filename (without .properties extension)
	 * @return created bundle
	 */
	public static MyResourceBundle loadSingleBundleFile(String name) {
		ResourceBundle.Control control =
				ResourceBundle.Control.getControl(Control.FORMAT_PROPERTIES);
		String resourceName = control.toResourceName(name, "properties");

		try {
			InputStream in = MyResourceBundle.class
					.getResourceAsStream("/" + resourceName);
			MyResourceBundle ret = new MyResourceBundle(in);
			in.close();
			return ret;
		} catch (Exception e) {
			Log.error("Warning: could not load bundle: " + resourceName);
			// e.printStackTrace();
			return null;
		}
	}

}
