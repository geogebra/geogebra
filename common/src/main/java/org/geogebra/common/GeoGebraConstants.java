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
	public static final String BUILD_DATE = "01 December 2020";
	/** complete version string */
	public static final String VERSION_STRING = "5.0.620.0";

	/** proper noun, should NOT be translated / transliterated */
	public static final String APPLICATION_NAME = "GeoGebra";

	/** Download update **/
	public static final String DOWNLOAD_PACKAGE_WIN =
			"https://download.geogebra.org/package/win";

	/** App versions */
	enum Version {
		CAS,
		GRAPHING,
		GRAPHING_3D,
		GEOMETRY,
		SCIENTIFIC,
		SUITE,
		MIXED_REALITY,
		NOTES
	}

	public enum Platform {

		DESKTOP("d"),

		/** GeoGebra Graphing Calculator */
		ANDROID("a"),

		WEB("w"),

		IOS("i"),

		IOS_WEBVIEW("iw"),

		WEB_FOR_BROWSER_2D("w2d"),

		OFFLINE("offline"),

		SMART("smart"),

		POWERPOINT("p");

		private String name;

		Platform(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		/**
		 * @param prerelease
		 *            whether we run prerelease
		 * @return eg X.Y.Zd-prerelease
		 */
		public String getVersionString(boolean prerelease, String appCode) {

			StringBuilder suffix = new StringBuilder(10);
			suffix.append(name);
			if (!"classic".equals(appCode)) {
				suffix.append(appCode);
			}
			if (prerelease) {
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
		 * 
		 * @return true if this is a phone version.
		 */
		public boolean isPhone() {
			return "i".equals(name);
		}

	}

	enum MenuType {
		DEFAULT, EXAM
	}

	String GRAPHING_APPCODE = "graphing";
	String GEOMETRY_APPCODE = "geometry";
	String G3D_APPCODE = "3d";
	String SCIENTIFIC_APPCODE = "scientific";
	String CAS_APPCODE = "cas";
	String SUITE_APPCODE = "suite";
	String CLASSIC_APPCODE = "classic";
	String EVALUATOR_APPCODE = "evaluator";
	String NOTES_APPCODE = "notes";

	String SUITE_SHORT_NAME = "CalculatorSuite.short";

	String SUITE_URL_NAME = "calculator";

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

	/** http prefix */
	public static final String HTTP = "http://";
	/** https prefix */
	public static final String HTTPS = "https://";

	/** GeoGebra URL */
	public final static String GEOGEBRA_WEBSITE = "https://www.geogebra.org/";
	/** 4.2 desktop bug reports */
	public final static String GEOGEBRA_REPORT_BUG_DESKTOP = "https://help.geogebra.org/bugs/?v=5.0";
	/** web bug reports */
	public final static String GEOGEBRA_REPORT_BUG_WEB = "https://help.geogebra.org/bugs/?v=web";
	/** bug reports 8 */
	public final static String GEOGEBRA_REPORT_BUG = "https://help.geogebra.org/bugs/";
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

	public static final String WIDGET_URL = "https://www.geogebra.org/widgetprovider/index/widgettype/";

	public static final String EDIT_URL_BASE = "https://www.geogebra.org/material/edit/id/";

	public static final String CDN_APPS = "https://cdn.geogebra.org/apps/";
}
