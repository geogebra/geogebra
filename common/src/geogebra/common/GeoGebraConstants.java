package geogebra.common;

public interface GeoGebraConstants {

	// GeoGebra version
	// DO NOT CHANGE the format of VERSION_STRING (or add commented out version)
	// as it is read by the build system
	// update lines below when this is updated
	public static final String BUILD_DATE = "10 December 2011";
	public static final String VERSION_STRING = "4.1.27.0"; // <- update lines
															// below when this
															// is updated
	// current 3D: "4.9.9.0"
	// current ggb42: "4.1.27.0"

	/* start hacks TODO remove it when release candidate */
	public static final String PREFERENCES_ROOT = VERSION_STRING
			.startsWith("4.9") ? "/geogebra50" : "/geogebra42";
	// File format versions
	public static final String XML_FILE_FORMAT = VERSION_STRING
			.startsWith("4.9") ? "5.0" : "4.2";
	/* end hacks */

	public static final String SPLASH_STRING = "splash42beta.png";
	public static final String SHORT_VERSION_STRING = "4.2"; // used for online
																// archive
	public static final boolean CAS_VIEW_ENABLED = true;
	public static final boolean IS_PRE_RELEASE = true; // !VERSION_STRING.endsWith(".0");
	// File format versions
	public static final String GGB_XSD_FILENAME = "ggb.xsd"; // for ggb files
	public static final String GGT_XSD_FILENAME = "ggt.xsd"; // for macro files
	public static final String I2G_FILE_FORMAT = "0.1.20080731";
	// pre-releases and I2G
	public static final boolean DISABLE_I2G = !IS_PRE_RELEASE;
	// URLs
	public final static String GEOGEBRA_ONLINE_ARCHIVE_BASE = "http://jars.geogebra.org/webstart/"
			+ SHORT_VERSION_STRING + "/";
	public final static String GEOGEBRA_ONLINE_WEBSTART_BASE = "http://www.geogebra.org/webstart/"
			+ SHORT_VERSION_STRING + "/";
	public final static String GEOGEBRA_ONLINE_WEBSTART_BASE_ALTERNATIVE = "http://jars.geogebra.org/webstart/"
			+ SHORT_VERSION_STRING + "/";
	public static final String LOADING_GIF = "http://www.geogebra.org/webstart/loading.gif";
	public final static String GEOGEBRA_WEBSITE = "http://www.geogebra.org/";
	public final static String HELP_URL = GEOGEBRA_WEBSITE + "help";
	public final static String GEOGEBRATUBE_WEBSITE = "http://www.geogebratube.org/";
	// max possible heap space for applets in MB
	public final static int MAX_HEAP_SPACE = 512;

}