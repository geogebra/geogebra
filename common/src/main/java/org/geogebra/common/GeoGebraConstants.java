package org.geogebra.common;

/**
 * Application-wide constants (version strings, URLs)
 */
public interface GeoGebraConstants {

	// GeoGebra version
	// DO NOT CHANGE the format of VERSION_STRING (or add commented out version)
	// as it is read by the build system
	// and updated automatically by the "Version Bump" task
	/** last build date */
	public static final String BUILD_DATE = "14 August 2019";
	/** complete version string */
	public static final String VERSION_STRING = "5.0.556.0";

	/** proper noun, should NOT be translated / transliterated */
	public static final String APPLICATION_NAME = "GeoGebra";

	public enum Versions {

		DESKTOP("d", "classic"),

		/** GeoGebra Graphing Calculator */
		ANDROID_NATIVE_GRAPHING("a", "graphing"),

		ANDROID_NATIVE_3D("a", "3D"),

		ANDROID_GEOMETRY("a", "geo"),

		ANDROID_NATIVE_SCIENTIFIC("a", "scientific"),

		ANDROID_CAS("a", "cas"),

		WEB_CAS("w", "cas"),

		ANDROID_WEBVIEW("aw", "classic"),

		ANDROID_WEBVIEW_EXAM("aw", "exam"),

		IOS_NATIVE("i", "graphing"),

		IOS_GEOMETRY("i", "geometry"),

        IOS_SCIENTIFIC("i", "scientific"),

		IOS_CAS("i", "cas"),

		IOS_NATIVE_3D("i", "3D"),

		IOS_WEBVIEW("iw", "classic"),

		WEB_FOR_DESKTOP("offline", "classic"),

		WINDOWS_STORE("win", "classic"),

		WEB_FOR_BROWSER_3D("w", "classic"),

		WEB_FOR_BROWSER_2D("w2d", "classic"),

		WEB_FOR_BROWSER_SIMPLE("w", "simple"),

		WEB_GRAPHING("w", "graphing"),

		WEB_GEOMETRY("w", "geometry"),

		WEB_3D_GRAPHING("w", "3D"),

		WEB_GRAPHING_OFFLINE("offline", "graphing"),

		WEB_GEOMETRY_OFFLINE("offline", "geometry"),

		SMART("smart", "classic"),

		POWERPOINT("p", "classic"),

		NO_CAS("nc", "classic"),

		WEB_NOTES("w", "notes");

		private String platform;
		private String appName;

		Versions(String platform, String appName) {
			this.platform = platform;
			this.appName = appName;
		}

		public String getAppName() {
			return appName;
		}

		public String getPlatform() {
			return platform;
		}

		/**
		 * @param prerelease
		 *            whether we run prerelease
		 * @param canary
		 *            whether we run canary
		 * @return eg X.Y.Zd-prerelease
		 */
		public String getVersionString(boolean prerelease, boolean canary) {

			StringBuilder suffix = new StringBuilder(10);
			suffix.append(platform);
			if (!"classic".equals(appName)) {
				suffix.append(appName);
			}
			if (canary) {
				suffix.append("-canary");
			} else if (prerelease) {
				suffix.append("-prerelease");
			}

			// everything except old Java desktop version should be version
			// 6.0.x.x
			switch (this) {
			default:
				// change 5.0.274.0 to 6.0.274.0
				return VERSION_STRING.replace("5.0.", "6.0.") + "-" + suffix;
			case DESKTOP:
				return VERSION_STRING + "-" + suffix;
			}
		}

		/**
		 * @return whether this is android exam app
		 */
		public boolean isAndroidWebview() {
			switch (this) {
			case ANDROID_WEBVIEW:
			case ANDROID_WEBVIEW_EXAM:
				return true;
			}
			return false;
		}

		/**
		 * 
		 * @return true if this is a phone version.
		 */
		public boolean isPhone() {
			return "i".equals(platform);
		}

	}

	/**
	 * used by version checker, so that sys admins can disable version checking
	 * for *all* ggb versions with
	 * HKEY_LOCAL_MACHINE/Software/JavaSoft/Prefs/geogebra/version_check_allow =
	 * false
	 */
	public static final String PREFERENCES_ROOT_GLOBAL = "/geogebra";

	/** eg HKEY_CURRENT_USER/Software/JavaSoft/Prefs/geogebra42/ */
	/** root preferences node */
	public static final String PREFERENCES_ROOT = "/geogebra50";
	/** File format version */
	public static final String XML_FILE_FORMAT = "5.0";

	// This is used for checking if a minor update exists (on each run):
	// DON'T change to https (causes problems)
	public static final String VERSION_URL_MINOR =
			"https://download.geogebra.org/installers/5.0/version.txt";

	// This is used for checking whether a major update exists (monthly):
	// DON'T change to https (causes problems)
	public static final String VERSION_URL = "https://download.geogebra.org/installers/version.txt";

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
	public final static String GEOGEBRA_ONLINE_ARCHIVE_BASE = "https://jars.geogebra.org/webstart/"
			+ SHORT_VERSION_STRING + "/";
	/** URL of GeoGebra jars, zipped */
	public static final String GEOGEBRA_ONLINE_JARS_ZIP = GEOGEBRA_ONLINE_ARCHIVE_BASE
			+ "geogebra-jars.zip";
	/** update directory, typically on Windows */
	public static final String GEOGEBRA_JARS_UPDATE_DIR = "\\GeoGebra 5.0\\jars\\update";

	/** update directory, typically on Windows */
	public static final String GEOGEBRA_THIRD_PARTY_UPDATE_DIR = "\\GeoGebra 5.0"
			+ "\\thirdparty\\update";

	/** GeoGebra URL */
	public final static String GEOGEBRA_WEBSITE = "https://www.geogebra.org/";
	/** 4.2 desktop bug reports */
	public final static String GEOGEBRA_REPORT_BUG_DESKTOP = "https://help.geogebra.org/bugs/?v=5.0";
	/** web bug reports */
	public final static String GEOGEBRA_REPORT_BUG_WEB = "https://help.geogebra.org/bugs/?v=web";
	/** GeoGebraTube beta URL, used when Feature.TUBE_BETA == true */
	public final static String GEOGEBRA_WEBSITE_BETA = "https://beta.geogebra.org/";

	public final static String TUBE_URL_SHORT = "https://ggbm.at/";
	public final static String GEOGEBRA_HELP_WEBSITE = "https://help.geogebra.org/";

	/** max possible heap space for applets in MB */
	public final static int MAX_HEAP_SPACE = 1024;
	/**
	 * minimal precision in LocusEquation: by default rounding is at least 4
	 * decimals (this changes dynamically when zooming in)
	 */
	public static final long PROVER_MIN_PRECISION = 10000;

	/** CSS class name for GeoGebraWeb &article> tag */
	public static final String GGM_CLASS_NAME = "geogebraweb";
	/** mimetype of GGB files */
	public static final String GGW_MIME_TYPE = "application/vnd.geogebra.file";

	/** Splash timeout in miliseconds */
	public static final int SPLASH_DIALOG_DELAY = 1000;

	/** license URL */
	public static final String GGW_ABOUT_LICENSE_URL = "https://www.geogebra"
			+ ".org/license/?action=AboutLicense";

	/**
	 * URL of the webpage to call if a file should be uploaded. If you want to
	 * test GeoGebra Materials uploads on a test server, use a test IP URL
	 * instead, e.g.: "http://140.78.116.131:8082/upload"
	 */
	public static final String uploadURL = "https://www.geogebra.org/upload";
	public static final String uploadURLBeta = "https://beta.geogebra.org/upload";

	public static final String GEOGEBRA_LOADING_PNG = "https://www.geogebra.org/images/GeoGebra_loading.png";
	public static final String APPLET_PLAY_PNG = "https://www.geogebra.org/images/applet_play.png";

	// //////////////////////////////////////////////////////////////////////////
	// AUTHENTICATING WITH GOOGLE
	// ///////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * This app's personal client ID assigned by the Google APIs Console
	 * (http://code.google.com/apis/console).
	 */
	public static final String GOOGLE_CLIENT_ID = "656990710877-g0tjpnhriv39e59f5s5ubs81sv2686m6"
			+ ".apps.googleusercontent.com";

	/**
	 * The Graphing Chrome app's client ID from the Google APIs Console
	 * (http://code.google.com/apis/console).
	 */
	public static final String CHROME_APP_CLIENT_ID =
			"656990710877-3uu4empvnqi7co987usqk0talj3hnt2r.apps.googleusercontent.com";

	// The auth scope being requested. This scope will allow the application to
	// identify who the authenticated user is.
	public static final String PLUS_ME_SCOPE = "https://www.googleapis.com/auth/plus.me";
	public static final String DRIVE_SCOPE = "https://www.googleapis.com/auth/drive.file https://www.googleapis.com/auth/drive.readonly";
	public static final String USERINFO_EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email";
	public static final String USERINFO_PROFILE_SCOPE = "https://www.googleapis.com/auth/userinfo.profile";
	public static final String API_USERINFO = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=";

	public static final String FORUM_URL = "https://help.geogebra.org/";

	public static final String DATA_LOGGING_WEBSOCKET_URL = "//data-logger.geogebra.org";
	public static final String DATA_LOGGING_WEBSOCKET_PORT = "80";
	public static final String DATA_LOGGING_WEBSOCKET_SECURE_PORT = "443";

	public static final String WIDGET_URL = "https://www.geogebra.org/widgetprovider/index/widgettype/";

	public static final String EDIT_URL_BASE = "https://www.geogebra.org/material/edit/id/";

	public static final String CDN_APPS = "https://cdn.geogebra.org/apps/";
}
