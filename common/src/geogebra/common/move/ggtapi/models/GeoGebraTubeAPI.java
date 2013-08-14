package geogebra.common.move.ggtapi.models;

/**
 * @author gabor
 * Common base for GeoGebraTubeApi
 */
public class GeoGebraTubeAPI {

	/**
	 * The Standard Result Quantity
	 */
	public static final int STANDARD_RESULT_QUANTITY = 30;

	/**
	 * Secure test url
	 */
	public static final String test_url = "http://test.geogebratube.org:8080/api/json.php";

	/**
	 * Public url (no SSL)
	 * DO NOT CHANGE!
	 */
	public static final String url = "http://www.geogebratube.org/api/json.php";
	/**
	 * Instance of the new GeoGebraTube API D/W/T
	 */
	protected static GeoGebraTubeAPI instance;

}
