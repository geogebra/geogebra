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
 * third class later, I chose a solution which makes it unneccessary to change
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

	/*
	 * don't need, erase, have made protected in parentclass:
	 * 
	 * private String XML_GGB_FACTORY_DEFAULT; // see loadPreferences()
	 * 
	 * // special preference keys (copied from GeoGebraPreferences private final
	 * String XML_USER_PREFERENCES = "xml_user_preferences"; private final
	 * String TOOLS_FILE_GGT = "tools_file_ggt"; private final String APP_LOCALE
	 * = "app_locale"; private final String APP_CURRENT_IMAGE_PATH =
	 * "app_current_image_path"; private final String APP_FILE_ = "app_file_";
	 */
	// / --- --- ///
	private final static String ERROR = "Error?"; // For debugging
	private final static String COMMENT = "GeoGebra Portable preferences (GeoGebra settings file)";

	// / --- Properties --- ///
	// use parent class PROPERTY_FILEPATH private static String path=null;
	private static Properties properties = new Properties();

	private static GeoGebraPortablePreferences singleton = null;

	// / --- Interface --- ///
	/* private singleton constructor */
	private GeoGebraPortablePreferences() {
	}

	/* Singleton getInstance()->getPref() */
	public synchronized static GeoGebraPreferencesD getPref() {
		if (singleton == null) {
			singleton = new GeoGebraPortablePreferences();
			singleton.loadPreferences();
		} // if
		return singleton;
	}// getPref()

	private void loadPreferences() {
		try { // debug("path: "+GeoGebraPreferences.PROPERTY_FILEPATH);
			File propertyfile = GeoGebraPreferencesD.getFile();
			if (propertyfile.exists()) {
				// path=propertyfile.getCanonicalPath();
				BufferedInputStream fis = new BufferedInputStream(
						new FileInputStream(propertyfile));
				properties.load(fis);
				fis.close(); // debug("loadPreferences():");properties.list(System.out);
			} else {
				// debug("Found no settings file...");
				clearPreferences(); // clean and store a blank one.
			} // if
		} catch (Exception e) {
			Log.debug("Problem loading settings file...");
			e.printStackTrace();
		} // try-catch
	}// loadPreferences

	private static void storePreferences() {
		if (!get("read_only", "false").equals("true")) {
			try {
				BufferedOutputStream os = new BufferedOutputStream(
						new FileOutputStream(GeoGebraPreferencesD.getFile()));
				properties.store(os, COMMENT); // Application.debug("storePreferences():
												// ");properties.list(System.out);
				os.close();
			} catch (Exception e) {
				Log.debug("Problem with storing of preferences.properties..."
						+ e.toString());
			} // try-catch
		} // if not read-only. (else do nothing...)
	}// storePreferences()

	// / --- GeoGebraPreferences interface --- ///
	@Override
	public String loadPreference(String key, String defaultValue) { // debug("loadPreferene()
																	// called
																	// with:
																	// "+key+",
																	// "+defaultValue);
		return get(key, defaultValue);
	}// loadPreference(key,def)

	@Override
	public void savePreference(String key, String value) { // debug("savePreferneces()
															// called with:
															// "+key+",
															// "+value);
		set(key, value);
	}// savePreferences(key,val)

	/**
	 * Returns the path of the first file in the file list
	 */
	@Override
	public File getDefaultFilePath() {
		File file = new File(properties.getProperty(APP_FILE_ + "1", "")); // debug("getDeafultFilepath():
																			// "+file.toString());
		if (file.exists()) {
			return file.getParentFile();
		}
		return null;
	}// getDefaultFilePath()

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
	}// saveDefaultImagePath(File)

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
	}// getDefaultLocale()

	/**
	 * Saves the currently set locale.
	 */
	@Override
	public void saveDefaultLocale(Locale locale) {
		// save locale (language)
		set(APP_LOCALE, locale.toString());
	}// saveDefaultLocle(Locale)

	/**
	 * Loads the names of the eight last used files from the preferences backing
	 * store.
	 */
	@Override
	public void loadFileList() {
		// load last four files
		for (int i = AppD.MAX_RECENT_FILES; i >= 1; i--) {
			File file = new File(get(APP_FILE_ + i, "")); // debug("loadFileList()
															// called:
															// "+file.toString());
			AppD.addToFileList(file);
		}
	}// loadFileList()

	/**
	 * Saves the names of the four last used files.
	 */
	@Override
	public void saveFileList() {
		String path;
		try {
			// save last four files
			for (int i = 1; i <= AppD.MAX_RECENT_FILES; i++) {
				File file = AppD.getFromFileList(i - 1);
				if (file != null) {
					path = file.getCanonicalPath(); // debug("saveFilelist():
													// "+path.toString());
					set(APP_FILE_ + i, path);
				} else {
					set(APP_FILE_ + i, "");
				}
			} // debug("list:");properties.list(System.out);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// If this is the last call to the pref system and is always done on
		// exit
		// this should be enough: (??)
		storePreferences();
	}// saveFileList()

	/**
	 * Saves preferences by taking the application's current values. Apparently
	 * no limit on property length! (# preferences: 8192), so no need to split
	 * up in pieces :-) But we have to convert byte[]&rarr;b64&rarr;String
	 */
	@Override
	public void saveXMLPreferences(AppD app) { // debug("saveXMLPreferences(app):");
		// preferences xml
		String xml = app.getPreferencesXML();

		set(XML_USER_PREFERENCES, xml);

		if (!(app.is3D())) // TODO: implement it in Application3D!
		{
			StringBuilder sb = new StringBuilder();
			app.getKernel().getConstruction().getConstructionDefaults()
					.getDefaultsXML(sb);
			String objectPrefsXML = sb.toString();

			set(XML_DEFAULT_OBJECT_PREFERENCES, objectPrefsXML);
		}

		byte[] macrofile = app.getMacroFileAsByteArray();
		String macrostring = Base64.encodeToString(macrofile, false);

		set(TOOLS_FILE_GGT, macrostring);

		// Force writing, "flush": //properties.list(System.out);
		storePreferences();
	}// saveXMLPreferences(Application)

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
			String xml = get(XML_USER_PREFERENCES, factoryDefaultXml);
			app.setXML(xml, true);

			if (!(app.is3D())) // TODO: implement it in Application3D!
			{
				String xmlDef = get(XML_DEFAULT_OBJECT_PREFERENCES,
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
		} // try-catch

		app.setDefaultCursor(); // debug("loadXMLPreferences()
								// called");properties.list(System.out);
	}// loadXMLPreferences(Application)

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

	// / --- Private --- ///
	// get/set with check
	private static String get(String key, String def) {
		if (properties != null) {
			return properties.getProperty(key, def);
		}
		return ERROR;
	}// get()

	public static final void set(String key, String val) {
		if (properties != null) {
			properties.setProperty(key, val);
		}
	}// set()

}// class GeoGebraPortablePreferences
