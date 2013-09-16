package geogebra.move.ggtapi.models;

import geogebra.common.move.ggtapi.models.GeoGebraTubeAPI;
import geogebra.util.HttpRequest;

/**
 * API Interface for GeoGebraTube requests and responses
 * 
 * @author stefan
 * 
 */
public class GeoGebraTubeAPID extends geogebra.common.move.ggtapi.models.GeoGebraTubeAPI
{
	
	
	@Override
	protected geogebra.common.util.HttpRequest createHttpRequest() {
		return new HttpRequest();
	}
	
	/**
	 * Get Singleton GeogebraTubeAPI
	 * 
	 * @return GeogebraTubeAPI singleton
	 */
	public static GeoGebraTubeAPID getInstance() {
		if (instance == null)
		{
			instance = new GeoGebraTubeAPID();
			// TODO: use the test url during development. remove when ready.
			GeoGebraTubeAPI.url = GeoGebraTubeAPI.test_url;
		}
		return (GeoGebraTubeAPID) instance;
	}
}
