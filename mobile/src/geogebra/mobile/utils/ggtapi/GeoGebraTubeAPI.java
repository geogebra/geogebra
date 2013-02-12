package geogebra.mobile.utils.ggtapi;

import java.util.List;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

/**
 * API Interface for GeoGebraTube requests and responses
 * 
 * @author Matthias Meisinger
 * 
 */
public class GeoGebraTubeAPI
{
	public static final int STANDARD_RESULT_QUANTITY = 10;
	
	private static final String url = "http://geogebratube.org/api/json.php";
	private static GeoGebraTubeAPI instance;

	private RequestBuilder requestBuilder;


	private GeoGebraTubeAPI()
	{
		this.requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
	}

	/**
	 * Search for materials containing the String query
	 * 
	 * @param query
	 *          search String
	 * @param limit
	 *          maximum Number of returned materials
	 * @return List<Item> Search Results in a List of materials
	 */
	public void search(String query, RequestCallback callback)
	{
		performRequest(new Request(query).toJSONString(), callback);
	}

	/**
	 * Returns materials in the given amount and order
	 * 
	 * @return List of materials
	 */
	public void getFeaturedMaterials(RequestCallback callback)
	{
		performRequest(new Request().toJSONString(), callback);
	}

	/**
	 * Returns a String-Array of popular tags fetched from the GGT API
	 * 
	 * @return
	 */
	public String[] getPopularTags()
	{
		// TODO fetch popular tags from the API
		return new String[] { "algebra", "dment", "pythagorean", "circle", "triangle", "functions", "jerzy", "geometry", "trigonometry", "3d" };
	}

	/**
	 * Return a specific Material by its ID
	 * 
	 * @param ID
	 */
	public Material getItem(String ID)
	{
		// TODO Implement fetching Materials by ID
		throw new UnsupportedOperationException();
	}

	/**
	 * Return a list of all Materials from the specified author
	 * 
	 * @param author
	 * @return List of Materials
	 */
	public List<Material> getAuthorsMaterials(String author)
	{
		// TODO Implement fetching Materials from specified author
		throw new UnsupportedOperationException();
	}

	/**
	 * Private method performing the request given by requestString
	 * 
	 * @param requestString
	 *          JSON request String for the GeoGebraTubeAPI
	 * @return the resulting List of Materials
	 * @throws RequestException
	 */
	private void performRequest(String requestString, RequestCallback callback)
	{		
		try
		{
			this.requestBuilder.sendRequest(requestString, callback);
		}
		catch (RequestException e)
		{
			// TODO Handle the error!
			e.printStackTrace();
		}
	}

	/**
	 * Get Singleton GeogebraTubeAPI
	 * 
	 * @return GeogebraTubeAPI singleton
	 */
	public static GeoGebraTubeAPI getInstance()
	{
		if (instance == null)
		{
			instance = new GeoGebraTubeAPI();
		}
		return instance;
	}
}
