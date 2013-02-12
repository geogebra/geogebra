package geogebra.mobile.utils.ggtapi;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * Utility Class for Parsing POJO's to JSON and vice versa
 * 
 * @author Matthias Meisinger
 * 
 */
public class JSONparserGGT
{
	public static String parseRequest(Request request)
	{
		// TODO Implement Parser
		// String testString =
		// "{\"request\":{\"-api\":\"1.0.0\",\"task\":{\"-type\":\"fetch\",\"fields\":{\"field\":[{\"-name\":\"id\"},{\"-name\":\"title\"},{\"-name\":\"timestamp\"},{\"-name\":\"author\"},{\"-name\":\"author_url\"},{\"-name\":\"url\"},{\"-name\":\"language\"},{\"-name\":\"featured\"},{\"-name\":\"likes\"}]},\"filters\":{\"field\":[{\"-name\":\"language\",\"#text\":\"en_US\"}]},{\"field\":{\"-name\":\"featured\",\"#text\":\"true\"}}],\"order\":{\"-by\":\"timestamp\",\"-type\":\"asc\"},\"limit\":{\"-num\":\"10\"}}}}";
		String testString = "{\"request\":{\"-api\":\"1.0.0\",\"task\":{\"-type\":\"fetch\",\"fields\":{\"field\":[{\"-name\":\"id\"},{\"-name\":\"title\"},{\"-name\":\"description\"}{\"-name\":\"timestamp\"},{\"-name\":\"author\"},{\"-name\":\"author_url\"},{\"-name\":\"url\"},{\"-name\":\"language\"},{\"-name\":\"featured\"},{\"-name\":\"likes\"}]},\"filters\":{\"field\":[{\"-name\":\"language\",\"#text\":\"en_US\"},{\"-name\": \"featured\"}]},\"order\":{\"-by\":\"timestamp\",\"-type\":\"asc\"},\"limit\":{\"-num\":\"10\"}}}}";

		return testString;
	}

	public static List<Material> parseResponse(String response)
	{
		List<Material> result = new ArrayList<Material>();

		JSONArray materialsArray = null;
		
		if (response != null)
		{
			JSONObject responseObject = JSONParser.parseStrict(response).isObject();
			
			if(responseObject.containsKey("responses"))
			{
				JSONObject materialsObject = responseObject.get("responses").isObject().get("response").isObject();
				
				if(materialsObject.containsKey(("item")))
				{
					materialsArray = materialsObject.get("item").isArray();
				}
				else
				{
					// List is empty
				}
			}
			else if(responseObject.containsKey("error"))
			{
				// Show error
			}
			
		}
		else
		{
			// Response String was null
		}

		if (materialsArray != null)
		{
			for (int i = 0; i < materialsArray.size(); i++)
			{
				JSONObject obj = materialsArray.isArray().get(i).isObject();

				Material.MaterialType type = Material.MaterialType.valueOf(obj.get("type").isString().stringValue());
				int ID = Integer.parseInt(obj.get("id").isString().stringValue());

				Material material = new Material(ID, type);

				material.setTitle(obj.get("title").isString().stringValue());
				material.setDescription(obj.get("description").isString().stringValue());
				material.setTimestamp(Long.parseLong(obj.get("timestamp").isString().stringValue()));
				material.setAuthor(obj.get("author").isString().stringValue());
				material.setAuthorURL(obj.get("author_url").isString().stringValue());
				material.setURL(obj.get("url").isString().stringValue());
				material.setURLdirect(obj.get("url_direct").isString().stringValue());
				material.setThumbnail(obj.get("thumbnail").isString().stringValue());
				material.setLanguage(obj.get("language").isString().stringValue());
				material.setFeatured(Boolean.parseBoolean((obj.get("featured").isString().stringValue())));
				material.setLikes(Integer.parseInt(obj.get("likes").isString().stringValue()));

				result.add(material);
			}
		}
		return result;
	}
}
