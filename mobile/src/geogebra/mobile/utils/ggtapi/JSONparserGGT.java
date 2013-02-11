package geogebra.mobile.utils.ggtapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

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
		String testString = "{\"request\":{\"-api\":\"1.0.0\",\"task\":{\"-type\":\"fetch\",\"fields\":{\"field\":[{\"-name\":\"id\"},{\"-name\":\"title\"},{\"-name\":\"url\"},{\"-name\":\"author\"}]},\"filters\":{\"field\":{\"-name\":\"language\",\"#text\":\"en_US\"}},\"order\":{\"-by\":\"timestamp\",\"-type\":\"desc\"},\"limit\":{\"-num\":\"5\"}}}}";
		return testString;
	}

	public static List<Material> parseResponse(String response)
	{
		List<Material> result = new ArrayList<Material>();

		JSONValue value = JSONParser.parse(response);
		JSONObject materialsObject = value.isObject();

		// FIXME HERE!
		JSONArray materialsArray = materialsObject.get("responses").isObject().get("response").isObject().get("item").isArray();

		if (materialsArray != null)
		{
			for (int i = 0; i < materialsArray.size(); i++)
			{
				JSONObject obj = materialsArray.get(i).isObject();

				int ID = (int) obj.get("ID").isNumber().doubleValue();
				Material.MaterialType type = Material.MaterialType.valueOf(obj.get("type").isString().stringValue());

				Material material = new Material(ID, type);

				material.setTitle(obj.get("title").isString().stringValue());
				material.setTimestamp((long) obj.get("timestamp").isNumber().doubleValue());
				material.setAuthor(obj.get("author").isString().stringValue());
				material.setAuthorURL(obj.get("author_url").isString().stringValue());
				material.setURL(obj.get("url").isString().stringValue());
				material.setURLdirect(obj.get("url_direct").isString().stringValue());

				material.setLanguage(obj.get("language").isString().stringValue());
				material.setFeatured(Boolean.valueOf(obj.get("featured").isString().stringValue()));
				material.setLikes((int) obj.get("likes").isNumber().doubleValue());

				result.add(material);
			}
		}
		return result;
	}
}
