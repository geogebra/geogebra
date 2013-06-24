package geogebra.common.move.ggtapi.models;

/**
 * @author gabor
 * Common base for GeoGebraTubeApi
 */
public class GeoGebraTubeAPI {

	/**
	 * The Standard Result Quantity
	 */
	public static final int STANDARD_RESULT_QUANTITY = 10;

	/**
	 * Secure test url
	 */
//	protected static final String secure_test_url = "https://test.geogebratube.org:8084/api/json.php";

	/**
	 * Public url (no SSL)
	 * DO NOT CHANGE!
	 */
	protected static final String url = "http://www.geogebratube.org/api/json.php";
	/**
	 * Instance of the new GeoGebraTube API D/W/T
	 */
	protected static GeoGebraTubeAPI instance;

}
