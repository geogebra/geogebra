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
	public static final String BUILD_DATE = "03 September 2024";
	/** complete version string */

	public static final String VERSION_STRING = "5.2.854.0";
	/** proper noun, should NOT be translated / transliterated */
	public static final String APPLICATION_NAME = "GeoGebra";

	/** App versions */
	enum Version {
		CAS,
		GRAPHING,
		GRAPHING_3D,
		GEOMETRY,
		SCIENTIFIC,
		SUITE,
		MIXED_REALITY,
		NOTES,
		PROBABILITY,
		CLASSIC;

		/**
		 * @return translatable name of this app type
		 */
		public String getTransKey() {
			switch (this) {
			case GRAPHING:
				return "GeoGebraGraphingCalculator";
			case GRAPHING_3D:
				return "GeoGebra3DGrapher";
			case SCIENTIFIC:
				return "GeoGebraScientificCalculator";
			case CAS:
				return "GeoGebraCASCalculator";
			case GEOMETRY:
				return "GeoGebraGeometry";
			case NOTES:
				return "GeoGebraNotes";
			case SUITE:
				return "GeoGebraCalculatorSuite";
			default:
				return null;
			}
		}
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
				// change 5.2.274.0 to 6.0.274.0
				return getVersionString6() + "-" + suffix;
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

		/**
		 * @return true if the current platform is either Android or iOS.
		 */
		public boolean isMobile() {
			return this == ANDROID || this == IOS;
		}
	}

	enum MenuType {
		DEFAULT, EXAM
	}

	String SUITE_APPNAME = "GeoGebraCalculatorSuite";

	String GRAPHING_APPCODE = "graphing";
	String GEOMETRY_APPCODE = "geometry";
	String G3D_APPCODE = "3d";
	String SCIENTIFIC_APPCODE = "scientific";
	String CAS_APPCODE = "cas";
	String SUITE_APPCODE = "suite";
	String CLASSIC_APPCODE = "classic";
	String EVALUATOR_APPCODE = "evaluator";
	String NOTES_APPCODE = "notes";
	String PROBABILITY_APPCODE = "probability";

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
	public static final String VERSION_URL_MINOR =
			"https://download.geogebra.org/installers/5.2/version.txt";

	// This is used for checking whether a major update exists (monthly):
	public static final String VERSION_URL = "https://download.geogebra.org/installers/version.txt";

	public static final String INSTALLERS_URL = "https://www.geogebra.org/download";

	// archive
	/** short version, for online archive */
	public static final String SHORT_VERSION_STRING = "5.2";
	// File format versions
	/** XSD for ggb files */
	public static final String GGB_XSD_FILENAME = "ggb.xsd";
	/** XSD for ggt (macro) files */
	public static final String GGT_XSD_FILENAME = "ggt.xsd";
	// URLs
	/** URL of GeoGebra jars */
	public final static String GEOGEBRA_ONLINE_ARCHIVE_BASE = "https://jars.geogebra.org/webstart/"
			+ SHORT_VERSION_STRING + "/";
	/** URL of GeoGebra jars, zipped */
	public static final String GEOGEBRA_ONLINE_JARS_ZIP = GEOGEBRA_ONLINE_ARCHIVE_BASE
			+ "geogebra-jars.zip";
	/** update directory, typically on Windows */
	public static final String GEOGEBRA_JARS_UPDATE_DIR = "\\GeoGebra 5.2\\jars\\update";

	/** http prefix */
	public static final String HTTP = "http://";
	/** https prefix */
	public static final String HTTPS = "https://";

	/** GeoGebra URL */
	public final static String GEOGEBRA_WEBSITE = "https://www.geogebra.org/";
	/** GeoGebraTube beta URL, used when Feature.TUBE_BETA == true */
	public final static String GEOGEBRA_WEBSITE_BETA = "https://beta.geogebra.org/";

	String GEOGEBRA_HELP_WEBSITE = "https://wiki.geogebra.org/help/";

	/**
	 * minimal precision in LocusEquation: by default rounding is at least 4
	 * decimals (this changes dynamically when zooming in)
	 */
	public static final long PROVER_MIN_PRECISION = 10000;

	/** CSS class name for GeoGebraWeb container tag */
	public static final String GGM_CLASS_NAME = "geogebraweb";
	/** mimetype of GGB files */
	public static final String GGW_MIME_TYPE = "application/vnd.geogebra.file";

	/** license URL */
	public static final String GGB_LICENSE_URL = "https://www.geogebra.org/license";

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
	public static final String REPORT_BUG_URL = "https://www.reddit.com/r/geogebra/";

	public static final String EDIT_URL_BASE = "https://www.geogebra.org/material/edit/id/";

	/**
	 * Get the version string for versions 6.0.*
	 *
	 * @return version string
	 */
	static String getVersionString6() {
		return VERSION_STRING.replace("5.2.", "6.0.");
	}
}
