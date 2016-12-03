package org.geogebra.common;

/**
 * Application-wide constants (version strings, URLs)
 */
public interface GeoGebraConstants {

	// GeoGebra version
	// DO NOT CHANGE the format of VERSION_STRING (or add commented out version)
	// as it is read by the build system
	// update lines below when this is updated
	/** last build date */
	public static final String BUILD_DATE = "03 December 2016";
	/** complete version string */
	public static final String VERSION_STRING = "5.0.299.0";

	/** proper noun, should NOT be translated / transliterated */
	public static final String APPLICATION_NAME = "GeoGebra";

	public enum Versions {

		DESKTOP("3D"),

		/** GeoGebra Graphing Calculator */
		ANDROID_NATIVE_GRAPHING("a"),

		ANDROID_NATIVE_3D("a3D"),

		ANDROID_WEBVIEW("aw"),

		ANDROID_WEBVIEW_EXAM("exam", true),

		IOS_NATIVE("i"),

		IOS_WEBVIEW("iw"),

		WEB_FOR_DESKTOP("offline"),

		WINDOWS_STORE("win"),

		WEB_FOR_BROWSER_3D("web3d"),

		WEB_FOR_BROWSER_2D("web"),

		WEB_FOR_BROWSER_SIMPLE("webSimple"),

		WEB_APP_FOR_BROWSER_3D("webapp"),

		SMART("s"),

		POWERPOINT("p"),

		NO_CAS("nc");

		private boolean exam = false;
		private String suffix;

		Versions(String suffix, boolean exam) {
			this.suffix = suffix;
			this.exam  = exam;
		}

		Versions(String suffix) {
			this.suffix = suffix;
		}

		public String getVersionString() {

			switch (this) {
			case WEB_FOR_DESKTOP:
				// change 5.0.274.0 to 6.0.274.0
				return VERSION_STRING.replace("5.0.", "6.0.") + "-" + suffix;
			default:
				return VERSION_STRING + "-" + suffix;

			}

		}

		public boolean isAndroidWebview() {
			switch (this) {
				case ANDROID_WEBVIEW:
				case ANDROID_WEBVIEW_EXAM:
					return true;
			}
			return false;
		}



	}

	/**
	 * used by version checker, so that sys admins can disable version checking
	 * for *all* ggb versions with
	 * HKEY_LOCAL_MACHINE/Software/JavaSoft/Prefs/geogebra/version_check_allow =
	 * false
	 * */
	public static final String PREFERENCES_ROOT_GLOBAL = "/geogebra";

	/** eg HKEY_CURRENT_USER/Software/JavaSoft/Prefs/geogebra42/ */
	/** root preferences node */
	public static final String PREFERENCES_ROOT = "/geogebra50";
	/** File format version */
	public static final String XML_FILE_FORMAT = "5.0";

	// This is used for checking if a minor update exists (on each run):
	// DON'T change to https (causes problems)
	public static final String VERSION_URL_MINOR = "http://www.geogebra.org/download/version50.txt";

	// This is used for checking whether a major update exists (monthly):
	// DON'T change to https (causes problems)
	public static final String VERSION_URL = "http://www.geogebra.org/download/version.txt";

	public static final String INSTALLERS_URL = "https://www.geogebra.org/download";

	/** Splash filename -- used for online */
	public static final String SPLASH_STRING = "splash.png";
	// archive
	/** short version, for online archive */
	public static final String SHORT_VERSION_STRING = "5.0";
	// File format versions
	/** XSD for ggb files */
	public static final String GGB_XSD_FILENAME = "ggb.xsd";
	/** XSD for ggt (macro) files */
	public static final String GGT_XSD_FILENAME = "ggt.xsd";
	// URLs
	/** URL of GeoGebraWeb main js file (offline version) */
	// public static final String GEOGEBRA_HTML5_BASE_OFFLINE =
	// "web/web.nocache.js";
	/** URL of GeoGebra jars */
	public final static String GEOGEBRA_ONLINE_ARCHIVE_BASE = "http://jars.geogebra.org/webstart/"
			+ SHORT_VERSION_STRING + "/";
	/** URL of GeoGebra jars, zipped */
	public static final String GEOGEBRA_ONLINE_JARS_ZIP = GEOGEBRA_ONLINE_ARCHIVE_BASE
			+ "geogebra-jars.zip";
	/** update directory, typically on Windows */
	public static final String GEOGEBRA_JARS_UPDATE_DIR = "\\GeoGebra 5.0\\jars\\update";

	/** update directory, typically on Windows */
	public static final String GEOGEBRA_THIRD_PARTY_UPDATE_DIR = "\\GeoGebra 5.0\\thirdparty\\update";

	/** GeoGebra URL */
	public final static String GEOGEBRA_WEBSITE = "https://www.geogebra.org/";
	/** 4.2 desktop bug reports */
	public final static String GEOGEBRA_REPORT_BUG_DESKTOP = "https://www.geogebra.org/bugs/?v=5.0";
	/** web bug reports */
	public final static String GEOGEBRA_REPORT_BUG_WEB = "https://www.geogebra.org/bugs/?v=web";
	/** GeoGebraTube beta URL, used when Feature.TUBE_BETA == true */
	public final static String GEOGEBRA_WEBSITE_BETA = "https://beta.geogebra.org/";

	public final static String TUBE_URL_SHORT = "https://ggbm.at/";
	public final static String ONENOTE_SHARE_URL = "https://www.geogebra.org/material/onenote/id/";

	/** max possible heap space for applets in MB */
	public final static int MAX_HEAP_SPACE = 1024;

	/** CSS class name for GeoGebraWeb &article> tag */
	public static final String GGM_CLASS_NAME = "geogebraweb";
	/** mimetype of GGB files */
	public static final String GGW_MIME_TYPE = "application/vnd.geogebra.file";

	/** Splash timeout in miliseconds */
	public static final int SPLASH_DIALOG_DELAY = 1000;

	/** license URL */
	public static final String GGW_ABOUT_LICENSE_URL = "https://www.geogebra.org/info/?action=AboutLicense";

	/**
	 * URL of the webpage to call if a file should be uploaded. If you want to
	 * test GeoGebra Materials uploads on a test server, use a test IP URL
	 * instead, e.g.: "http://140.78.116.131:8082/upload"
	 */
	public static final String uploadURL = "https://www.geogebra.org/upload";
	public static final String uploadURLBeta = "https://beta.geogebra.org/upload";

	// //////////////////////////////////////////////////////////////////////////
	// AUTHENTICATING WITH GOOGLE
	// ///////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * This app's personal client ID assigned by the Google APIs Console
	 * (http://code.google.com/apis/console).
	 */
	public static final String GOOGLE_CLIENT_ID = "656990710877.apps.googleusercontent.com";
	public static final String GOOGLE_TEST_CLIENT_ID = "300173001758.apps.googleusercontent.com";

	// The auth scope being requested. This scope will allow the application to
	// identify who the authenticated user is.
	public static final String PLUS_ME_SCOPE = "https://www.googleapis.com/auth/plus.me";
	public static final String DRIVE_SCOPE = "https://www.googleapis.com/auth/drive.file https://www.googleapis.com/auth/drive.readonly";
	public static final String USERINFO_EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email";
	public static final String USERINFO_PROFILE_SCOPE = "https://www.googleapis.com/auth/userinfo.profile";
	public static final String API_USERINFO = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=";

	public static final String FORUM_URL = "https://www.geogebra.org/help";
	public static final String APPLET_FOCUSED_CLASSNAME = "applet-focused";
	public static final String APPLET_UNFOCUSED_CLASSNAME = "applet-unfocused";

	public static final String DATA_LOGGING_WEBSOCKET_URL = "//data-logger.geogebra.org";
	public static final String DATA_LOGGING_WEBSOCKET_PORT = "80";
	public static final String DATA_LOGGING_WEBSOCKET_SECURE_PORT = "443";

	public static final String QUICKSTART_URL = "https://www.geogebra.org/tutorial/";

	public static final String WIDGET_URL = "https://www.geogebra.org/widgetprovider/index/widgettype/";

	public static final String EDIT_URL_BASE = "https://www.geogebra.org/material/edit/id/";

	public static final String PROFILE_URL_BASE = "https://www.geogebra.org/user/profile/id/";





}
