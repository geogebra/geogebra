/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package org.geogebra.desktop.main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Locale;
import java.util.Properties;

import org.geogebra.common.jre.util.Base64;
import org.geogebra.common.main.GeoGebraPreferences;
import org.geogebra.common.util.debug.Log;

/**
 * Class GeoGebraPortablePreferences
 * 
 * Stores user settings and options as a property file. For use in portable
 * GeoGebra on usb and Cd/DVD.
 * 
 * This class is returned by GeoGebraPreferences.getPrefs() instead of
 * GeoGebraPreferences itself, if the file preferences.properties exists in the
 * geogebra.jar-folder. This opens up for three modes: - Normal - Portable
 * GeoGebra - Read-only network share or CD/DVD, where we want same setting for
 * all
 * 
 * geogebra.properties must have one line: is_read_only=true (or false)
 * 
 * A cleaner implementation would be to rewrite GeoGebraPreferences to an
 * abstract class, as an interface for GeoGebraSystemPreferences and
 * GeoGebraPropertyFile, but as there probably never will be a demand for a
 * third class later, I chose a solution which makes it unnecessary to change
 * too many calling classes, by just making this one and do a small rewrite of
 * getPrefs() and add a setPropertyFile() in GeoGebraPreferences which then
 * behaves like a kind of "singleton factory".
 * 
 * This class implements all the commands of GeoGebraPreferences, but stores in
 * propertyfile given on the commandline: --settingsFile=&lt;path&gt;\
 * &lt;filename&gt; (prefs.properties)
 * 
 * Options/ToDo: Might as well store: xml in user.xml ggt in macro.bin to avoid
 * escaping "=", b64 encode/decoding and save some time... Also useful to have
 * the xml in a separate file for editing? On the other hand, this is done
 * automatically in Properties, so not really a problem.
 * 
 * @author Hans-Petter Ulven
 * @version 2010-03-07
 */
public class GeoGebraPortablePreferences extends GeoGebraPreferencesD {

	private final static String ERROR = "Error?"; // For debugging
	private final static String COMMENT = "GeoGebra Portable preferences (GeoGebra settings file)";

	// / --- Properties --- ///
	// use parent class PROPERTY_FILEPATH private static String path=null;
	private static final Properties properties = new Properties();

	private static GeoGebraPortablePreferences singleton = null;

	// / --- Interface --- ///
	private GeoGebraPortablePreferences() {
		// private singleton constructor
	}

	/** @return Singleton preferences */
	public synchronized static GeoGebraPreferencesD getPref() {
		if (singleton == null) {
			singleton = new GeoGebraPortablePreferences();
			singleton.loadPreferences();
		} // if
		return singleton;
	}

	private void loadPreferences() {
		try {
			File propertyfile = GeoGebraPreferencesD.getFile();
			if (propertyfile.exists()) {
				BufferedInputStream fis = new BufferedInputStream(
						new FileInputStream(propertyfile));
				properties.load(fis);
				fis.close();
			} else {
				clearPreferences(); // clean and store a blank one.
			}
		} catch (Exception e) {
			Log.debug("Problem loading settings file...");
			e.printStackTrace();
		}
	}

	private static void storePreferences() {
		if (!get("read_only", "false").equals("true")) {
			try {
				BufferedOutputStream os = new BufferedOutputStream(
						new FileOutputStream(GeoGebraPreferencesD.getFile()));
				properties.store(os, COMMENT);
				os.close();
			} catch (Exception e) {
				Log.debug("Problem with storing of preferences.properties..."
						+ e);
			}
		}
	}

	@Override
	public String loadPreference(String key, String defaultValue) {
		return get(key, defaultValue);
	}

	@Override
	public void savePreference(String key, String value) {
		set(key, value);
	}

	/**
	 * Returns the path of the first file in the file list
	 */
	@Override
	public File getDefaultFilePath() {
		File file = new File(properties.getProperty(APP_FILE_ + "1", ""));
		if (file.exists()) {
			return file.getParentFile();
		}
		return null;
	}

	/**
	 * Returns the default image path
	 */
	@Override
	public File getDefaultImagePath() {
		// image path
		String pathName = properties.getProperty(APP_CURRENT_IMAGE_PATH, null);
		if (pathName != null) {
			return new File(pathName);
		}
		return null;
	}

	/**
	 * Saves the currently set locale.
	 */
	@Override
	public void saveDefaultImagePath(File imgPath) {
		try {
			if (imgPath != null) {
				set(APP_CURRENT_IMAGE_PATH, imgPath.getCanonicalPath());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the default locale
	 */
	@Override
	public Locale getDefaultLocale() {
		// language
		String strLocale = get(APP_LOCALE, null);
		if (strLocale != null) {
			return AppD.getLocale(strLocale);
		}
		return null;
	}

	/**
	 * Saves the currently set locale.
	 */
	@Override
	public void saveDefaultLocale(Locale locale) {
		set(APP_LOCALE, locale.toString());
	}

	/**
	 * Loads the names of the eight last used files from the preferences backing
	 * store.
	 */
	@Override
	public void loadFileList() {
		// load last used files
		for (int i = AppD.MAX_RECENT_FILES; i >= 1; i--) {
			File file = new File(get(APP_FILE_ + i, ""));
			AppD.addToFileList(file);
		}
	}

	/**
	 * Saves the names of the four last used files.
	 */
	@Override
	public void saveFileList() {
		String path;
		try {
			// save last used files
			for (int i = 1; i <= AppD.MAX_RECENT_FILES; i++) {
				File file = AppD.getFromFileList(i - 1);
				if (file != null) {
					path = file.getCanonicalPath();
					set(APP_FILE_ + i, path);
				} else {
					set(APP_FILE_ + i, "");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// If this is the last call to the pref system and is always done on
		// exit
		// this should be enough: (??)
		storePreferences();
	}

	/**
	 * Saves preferences by taking the application's current values. Apparently
	 * no limit on property length! (# preferences: 8192), so no need to split
	 * up in pieces :-) But we have to convert byte[]&rarr;b64&rarr;String
	 */
	@Override
	public void saveXMLPreferences(AppD app) {
		String xml = app.getPreferencesXML();

		set(GeoGebraPreferences.XML_USER_PREFERENCES, xml);

		if (!app.is3D()) { // TODO: implement it in Application3D!
			StringBuilder sb = new StringBuilder();
			app.getKernel().getConstruction().getConstructionDefaults()
					.getDefaultsXML(sb);
			String objectPrefsXML = sb.toString();

			set(GeoGebraPreferences.XML_DEFAULT_OBJECT_PREFERENCES, objectPrefsXML);
		}

		byte[] macrofile = app.getMacroFileAsByteArray();
		String macrostring = Base64.encodeToString(macrofile, false);

		set(TOOLS_FILE_GGT, macrostring);

		storePreferences();
	}

	/**
	 * Loads XML preferences (empty construction with GUI and kernel settings)
	 * and sets application accordingly. This method clears the current
	 * construction in the application. Note: the XML string used is the same as
	 * for ggb files.
	 */
	@Override
	public void loadXMLPreferences(AppD app) {
		app.setWaitCursor();

		// load this preferences xml file in application
		try {
			// load tools from ggt file (byte array)
			// Must convert String--b64-->byte
			String ggtString = get(TOOLS_FILE_GGT, ERROR);
			if (ggtString.equals(ERROR)) {
				Log.debug("problem with getting GGT...");
			} else {
				byte[] ggtFile = Base64.decode(ggtString);
				app.loadMacroFileFromByteArray(ggtFile, true);
			} // if error

			// load preferences xml
			initDefaultXML(app); // This might not have been called before!
			String xml = get(GeoGebraPreferences.XML_USER_PREFERENCES, factoryDefaultXml);
			app.setXML(xml, true);

			if (!app.is3D()) { // TODO: implement it in Application3D!
				String xmlDef = get(GeoGebraPreferences.XML_DEFAULT_OBJECT_PREFERENCES,
						factoryDefaultXml);
				if (!xmlDef.equals(factoryDefaultXml)) {
					boolean eda = app.getKernel().getElementDefaultAllowed();
					app.getKernel().setElementDefaultAllowed(true);
					app.setXML(xmlDef, false);
					app.getKernel().setElementDefaultAllowed(eda);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		app.setDefaultCursor();
	}

	/**
	 * Clears all user preferences.
	 */
	public void clearPreferences() {
		try {
			properties.clear();
			// ggbPrefs.flush();
			storePreferences();
		} catch (Exception e) {
			Log.debug(e + "");
		}
	}

	private static String get(String key, String def) {
		return properties.getProperty(key, def);
	}

	public static void set(String key, String val) {
		properties.setProperty(key, val);
	}

}
