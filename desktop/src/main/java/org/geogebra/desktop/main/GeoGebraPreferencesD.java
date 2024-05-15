/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package org.geogebra.desktop.main;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Locale;
import java.util.prefs.Preferences;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoGebraPreferences;
import org.geogebra.common.main.GeoGebraPreferencesXML;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.util.UtilD;

/**
 * Stores user settings and options as preferences.
 * 
 * @author Markus Hohenwarter
 * @version May 16, 2007
 */

/*
 * Additions by Hans-Petter Ulven 6 Mars, 2010 Subclass
 * GeoGebraPortablePreferences, for saving prefs to propertyfile Added some
 * constants Small rewrite of getPref() to return subclass singleton instead of
 * this.singleton 7mars: Addition of setPropertyFile(filename) to fascilitate
 * cmdline option --settingsFile (Set in line 263 geogebra.gui.app.GeoGebraFrame
 * before getPref() is called first time.)
 */

public class GeoGebraPreferencesD {

	// Windows -> APPDATA, space in "GeoGebra 5.0"
	// Mac / Linux -> user.home, hidden folder, no space in ".GeoGebra5.0"
	public static final String PREFS_PATH = AppD.WINDOWS
			? (System.getenv("APPDATA") + "/GeoGebra "
					+ GeoGebraConstants.SHORT_VERSION_STRING + "/prefs/")
			: (System.getProperty("user.home") + "/.GeoGebra"
					+ GeoGebraConstants.SHORT_VERSION_STRING + "/prefs/");

	public static final String WINDOWS_USERS_PREFS = PREFS_PATH + "prefs.xml";
	public static final String WINDOWS_OBJECTS_PREFS = PREFS_PATH
			+ "defaults.xml";
	public static final String WINDOWS_MACROS_PREFS = PREFS_PATH + "macros.ggt";

	public static final String AUTHOR = "author";

	public static final String VERSION = "version";
	public static final String VERSION_LAST_CHECK = "version_last_check";
	/**
	 * Allow checking of availability of a newer version
	 */
	public static final String VERSION_CHECK_ALLOW = "version_check_allow";
	/** Last version shown in download notification */
	private static final String VERSION_LAST_NOTIFICATION_SHOWN = "version_notification_shown";

	// picture export dialog
	public static final String EXPORT_PIC_FORMAT = "export_pic_format";
	public static final String EXPORT_PIC_DPI = "export_pic_dpi";

	// print preview dialog
	public static final String PRINT_ORIENTATION = "print_orientation";
	public static final String PRINT_SHOW_SCALE = "print_show_scale";
	public static final String PRINT_SHOW_SCALE2 = "print_show_scale_2";

	// user data
	public static final String USER_LOGIN_TOKEN = "user_login_token";

	// preferences node name for GeoGebra
	private Preferences ggbPrefs;
	private Preferences ggbPrefsSystem;

	private static class GeoGebraPreferencesInstalled extends GeoGebraPreferencesD {

	}

	protected GeoGebraPreferencesD() {

		try {
			if (PROPERTY_FILEPATH == null) {
				ggbPrefs = Preferences.userRoot()
						.node(GeoGebraConstants.PREFERENCES_ROOT);
			}
		} catch (Exception e) {
			// thrown when running unsigned JAR
			ggbPrefs = null;
		}

		try {
			if (PROPERTY_FILEPATH == null && Preferences.systemRoot()
					.nodeExists(GeoGebraConstants.PREFERENCES_ROOT_GLOBAL)) {
				ggbPrefsSystem = Preferences.systemRoot()
						.node(GeoGebraConstants.PREFERENCES_ROOT_GLOBAL);
			} else {
				ggbPrefsSystem = null;
			}
		} catch (Exception e) {
			// thrown when running unsigned JAR
			ggbPrefsSystem = null;
		}

	}

	// Ulven: changed to make available to subclass GeoGebraPortablePreferences
	protected String factoryDefaultXml; // see loadPreferences()

	protected static final String XML_FACTORY_DEFAULT = "xml_factory_default";
	protected static final String TOOLS_FILE_GGT = "tools_file_ggt";
	protected static final String APP_LOCALE = "app_locale";
	protected static final String APP_CURRENT_IMAGE_PATH = "app_current_image_path";
	protected static final String APP_FILE_ = "app_file_";

	/* Ulven 06.03.10 */

	private static String PROPERTY_FILEPATH = null; // full path, null: no
														// property file set

	private static GeoGebraPreferencesInstalled singleton;

	/** Set in geogebra.gui.app.GeoGebraFrame before first call to getPref() */
	public static void setPropertyFileName(String pfname) {
		PROPERTY_FILEPATH = pfname;
		Log.debug("Prferences in: " + PROPERTY_FILEPATH);
	}

	/**
	 * @return preferences singleton
	 */
	public synchronized static GeoGebraPreferencesD getPref() {
		if (PROPERTY_FILEPATH != null) {
			return GeoGebraPortablePreferences.getPref();
		}
		if (singleton == null) {
			singleton = new GeoGebraPreferencesInstalled();
		}
		return singleton;
	}

	public static void setLastVersionNotification(long newestVersion) {
		getPref().savePreference(VERSION_LAST_NOTIFICATION_SHOWN, String.valueOf(newestVersion));
	}

	/**
	 * @return last version shown in update notification
	 */
	public static long getLastShownNotificationVersion() {
		try {
			String lastVersion = getPref().loadPreference(VERSION_LAST_NOTIFICATION_SHOWN, "0");
			return Long.parseLong(lastVersion);
		} catch (NumberFormatException e) {
			Log.debug(e);
		}
		return 0;
	}

	public String loadPreference(String key, String defaultValue) {
		return ggbPrefs.get(key, defaultValue);
	}

	/**
	 * Save preference value
	 * @param key key
	 * @param value value
	 */
	public void savePreference(String key, String value) {
		if (key != null && value != null) {
			ggbPrefs.put(key, value);
		}
	}

	/**
	 * Check if system (local machine), then user, allows check version
	 * 
	 * @param defaultValue
	 *            default value (if key doesn't exist)
	 * @return true if system and user allows check version
	 */
	public boolean loadVersionCheckAllow(String defaultValue) {
		// check if system (local machine) allows check version
		boolean systemAllows;
		if (ggbPrefsSystem == null) {
			systemAllows = true;
			Log.info("No system preferences");
		} else {
			systemAllows = Boolean.parseBoolean(ggbPrefsSystem.get(
					GeoGebraPreferencesD.VERSION_CHECK_ALLOW, defaultValue));
		}
		// then check if user allows
		if (systemAllows && ggbPrefs != null) {
			return Boolean.parseBoolean(getPref().loadPreference(
					GeoGebraPreferencesD.VERSION_CHECK_ALLOW, defaultValue));
		}
		// else don't allow
		return false;
	}

	/**
	 * save "versionCheckAllow" value to users preferences
	 * 
	 * @param value
	 *            value
	 */
	public void saveVersionCheckAllow(String value) {
		getPref().savePreference(GeoGebraPreferencesD.VERSION_CHECK_ALLOW,
				value);
	}

	/**
	 * @return the path of the first file in the file list
	 */
	public File getDefaultFilePath() {
		File file = new File(getPref().loadPreference(APP_FILE_ + "1", ""));
		if (file.exists()) {
			return file.getParentFile();
		}
		return null;
	}

	/**
	 * Returns the default image path
	 * 
	 * @return the image path
	 */
	public File getDefaultImagePath() {
		// image path
		String pathName = getPref().loadPreference(APP_CURRENT_IMAGE_PATH,
				null);
		if (pathName != null) {
			return new File(pathName);
		}
		return null;
	}

	/**
	 * Saves the currently set locale.
	 */
	public void saveDefaultImagePath(File imgPath) {
		try {
			if (imgPath != null) {
				getPref().savePreference(APP_CURRENT_IMAGE_PATH,
						imgPath.getCanonicalPath());
			}
		} catch (Exception e) {
			Log.debug(e);
		}
	}

	/**
	 * Returns the default locale
	 * 
	 * @return the locale
	 */
	public Locale getDefaultLocale() {
		// language
		String strLocale = getPref().loadPreference(APP_LOCALE, null);
		if (strLocale != null) {
			return AppD.getLocale(strLocale);
		}
		return null;
	}

	/**
	 * Saves the currently set locale.
	 */
	public void saveDefaultLocale(Locale locale) {
		// save locale (language)
		getPref().savePreference(APP_LOCALE, locale.toString());
	}

	/**
	 * Loads the names of the eight last used files from the preferences backing
	 * store.
	 */
	public void loadFileList() {
		// load last eight files
		for (int i = AppD.MAX_RECENT_FILES; i >= 1; i--) {
			File file = new File(getPref().loadPreference(APP_FILE_ + i, ""));
			AppD.addToFileList(file);
		}
	}

	/**
	 * Saves the names of the eight last used files.
	 */
	public void saveFileList() {
		try {
			// save last four files
			for (int i = 1; i <= AppD.MAX_RECENT_FILES; i++) {
				File file = AppD.getFromFileList(i - 1);
				if (file != null) {
					getPref().savePreference(APP_FILE_ + i,
							file.getCanonicalPath());
				} else {
					getPref().savePreference(APP_FILE_ + i, "");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inits factory default XML if there are no old preferences or if the
	 * version number changed. The default XML is the preferences XML of this
	 * virgin application.
	 */
	public void initDefaultXML(AppD app) {
		// already initialized?
		if (factoryDefaultXml != null) {
			return;
		}

		// when applet unsigned this may be null
		if (ggbPrefs != null) {
			// get the GeoGebra version with which the preferences were saved
			// (the version number is stored since version 3.9.41)
			String oldVersion = getPref().loadPreference(VERSION, null);

			// current factory defaults possibly available?
			if (oldVersion != null
					&& oldVersion.equals(GeoGebraConstants.VERSION_STRING)) {
				factoryDefaultXml = getPref()
						.loadPreference(XML_FACTORY_DEFAULT, null);
			}
		}

		// if this is an old version or the factory defaults were not saved in
		// the
		// preferences for some reasons, create and store them now (plus: store
		// version string)
		if (factoryDefaultXml == null) {
			factoryDefaultXml = getDefaultPreferences(app);
			if (ggbPrefs != null) {
				ggbPrefs.put(XML_FACTORY_DEFAULT, factoryDefaultXml);
				ggbPrefs.put(VERSION, GeoGebraConstants.VERSION_STRING);
			}
		}
	}

	/**
	 * Saves preferences by taking the application's current values.
	 */
	public void saveXMLPreferences(AppD app) {

		String userPrefsXML = app.getPreferencesXML();
		StringBuilder sb = new StringBuilder();
		app.getKernel().getConstruction().getConstructionDefaults()
				.getDefaultsXML(sb);
		String objectPrefsXML = sb.toString();
		byte[] macros = app.getMacroFileAsByteArray();

		if (isSaveSettingsToFile()) {

			// make sure folder exists
			UtilD.mkdirs(new File(PREFS_PATH));

			UtilD.writeStringToFile(userPrefsXML, WINDOWS_USERS_PREFS);
			UtilD.writeStringToFile(objectPrefsXML, WINDOWS_OBJECTS_PREFS);

			UtilD.writeByteArrayToFile(macros, WINDOWS_MACROS_PREFS);

			return;

		}

		ggbPrefs.put(GeoGebraPreferences.XML_USER_PREFERENCES, userPrefsXML);

		try {
			getPref().savePreference(GeoGebraPreferences.XML_DEFAULT_OBJECT_PREFERENCES,
					objectPrefsXML);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("object defaults too long");
		}

		// store current tools including icon images as ggt file (byte array)
		putByteArray(TOOLS_FILE_GGT, app.getMacroFileAsByteArray());

		try {
			ggbPrefs.flush();
		} catch (Exception e) {
			Log.debug(e + "");
		}
	}

	/**
	 * Breaks up byte array value into pieces and calls
	 * prefs.putByteArray(prefs, key+k, piece_k) for every piece.
	 */
	private void putByteArray(String key, byte[] value) {
		// byte array must not be longer than 3/4 of max value length
		int max_length = (int) Math.floor(Preferences.MAX_VALUE_LENGTH * 0.75);

		// value array is small enough
		if (value == null || value.length < max_length) {
			ggbPrefs.putByteArray(key, value);

			// remove possible old part keys
			int partCount = 0;
			while (true) {
				byte[] temp = ggbPrefs.getByteArray(key + partCount, null);
				if (temp != null) {
					ggbPrefs.remove(key + partCount);
					partCount++;
				} else {
					break;
				}
			}
		}

		// break value array up into smaller pieces
		else {
			// delete key value
			ggbPrefs.remove(key);

			byte[] bytePart = new byte[max_length];
			int pos = 0;
			int partCount = 0;
			while (pos + max_length <= value.length) {
				for (int k = 0; k < max_length; k++, pos++) {
					bytePart[k] = value[pos];
				}

				// put piece key + partCount
				partCount++;
				ggbPrefs.putByteArray(key + partCount, bytePart);
			}

			// write last part
			if (pos < value.length) {
				bytePart = new byte[value.length - pos];

				for (int k = 0; pos < value.length; k++, pos++) {
					bytePart[k] = value[pos];
				}

				// put piece key + partCount
				partCount++;
				ggbPrefs.putByteArray(key + partCount, bytePart);
			}
		}

		try {
			ggbPrefs.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Breaks up byte array value into pieces and calls
	 * prefs.putByteArray(prefs, key+k, piece_k) for every piece.
	 */
	private byte[] getByteArray(String key, byte[] def) {
		byte[] ret = ggbPrefs.getByteArray(key, null);

		if (ret != null) {
			// no parts: return byte array
			return ret;
		}
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int partCount = 1;
			while (true) {
				ret = ggbPrefs.getByteArray(key + partCount, null);
				if (ret != null) {
					bos.write(ret);
					partCount++;
				} else {
					break;
				}
			}
			bos.flush();
			if (bos.size() > 0) {
				ret = bos.toByteArray();
			}
		} catch (Exception e) {
			e.printStackTrace();
			ret = null;
		}

		if (ret != null) {
			return ret;
		}
		return def;
	}

	/**
	 * @return XML preferences
	 */
	public String getXMLPreferences() {
		return getPref().loadPreference(GeoGebraPreferences.XML_USER_PREFERENCES,
				factoryDefaultXml);
	}

	/**
	 * Loads XML preferences (empty construction with GUI and kernel settings)
	 * and sets application accordingly. This method clears the current
	 * construction in the application. Note: the XML string used is the same as
	 * for ggb files.
	 */
	public void loadXMLPreferences(AppD app) {
		app.setWaitCursor();

		if (isSaveSettingsToFile()) {
			Log.debug("Preferences loaded from " + WINDOWS_USERS_PREFS);
			String userPrefsXML = UtilD.loadFileIntoString(WINDOWS_USERS_PREFS);
			String objectPrefsXML = UtilD
					.loadFileIntoString(WINDOWS_OBJECTS_PREFS);

			byte[] ggtFile = UtilD.loadFileIntoByteArray(WINDOWS_MACROS_PREFS);

			if (ggtFile != null) {
				app.loadMacroFileFromByteArray(ggtFile, true);
			}

			if (userPrefsXML != null) {
				app.setXML(userPrefsXML, false);
			} else {
				app.setXML(factoryDefaultXml, false);
			}

			if (objectPrefsXML != null
					&& !objectPrefsXML.equals(factoryDefaultXml)) {
				boolean eda = app.getKernel().getElementDefaultAllowed();
				app.getKernel().setElementDefaultAllowed(true);
				app.getKernel().getConstruction().setIgnoringNewTypes(true);
				app.setXML(objectPrefsXML, false);
				app.getKernel().getConstruction().setIgnoringNewTypes(false);
				app.getKernel().setElementDefaultAllowed(eda);
			}

			app.updateToolBar();
			app.setDefaultCursor();
			return;

		}

		// load this preferences xml file in application
		try {
			// load tools from ggt file (byte array)
			byte[] ggtFile = getByteArray(TOOLS_FILE_GGT, null);
			app.loadMacroFileFromByteArray(ggtFile, true);

			// load preferences xml
			String xml = getPref().loadPreference(GeoGebraPreferences.XML_USER_PREFERENCES,
					factoryDefaultXml);
			app.setXML(xml, false);

			String xmlDef = getPref().loadPreference(
					GeoGebraPreferences.XML_DEFAULT_OBJECT_PREFERENCES, factoryDefaultXml);
			if (!xmlDef.equals(factoryDefaultXml)) {
				boolean eda = app.getKernel().getElementDefaultAllowed();
				app.getKernel().setElementDefaultAllowed(true);
				app.setXML(xmlDef, false);
				app.getKernel().setElementDefaultAllowed(eda);
			}
			app.updateToolBar();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		app.setDefaultCursor();
	}

	/**
	 * Clears all user preferences.
	 */
	public void clearPreferences(App app) {

		if (isSaveSettingsToFile()) {
			try {
				UtilD.delete(new File(WINDOWS_OBJECTS_PREFS));
				UtilD.delete(new File(WINDOWS_USERS_PREFS));
				UtilD.delete(new File(WINDOWS_MACROS_PREFS));
			} catch (Exception e) {
				Log.debug(e);
			}
		}
		try {
			ggbPrefs.clear();
			ggbPrefs.flush();
		} catch (Exception e) {
			Log.debug(e + "");
		}
	}

	private boolean isSaveSettingsToFile() {
		return AppD.WINDOWS || AppD.MAC_OS;
	}

	/**
	 * @return Default preferences
	 */
	private static String getDefaultPreferences(App app) {
		return GeoGebraPreferencesXML.getXML(app);
	}

	public static File getFile() {
		return new File(GeoGebraPreferencesD.PROPERTY_FILEPATH);
	}
}