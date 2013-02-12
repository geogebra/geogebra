package geogebra.mobile.utils.ggtapi;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

/**
 * For Generating a JSON String for specific GeoGebratube API Requests
 * 
 * @author Matthias Meisinger
 */
class Request
{
	enum Task
	{
		fetch;
	}

	public enum Fields
	{
		id, title, type, description, timestamp, author, author_url, url, url_direct, language, thumbnail, featured, likes;
	}

	public enum Filters
	{
		id, title, search, type, description, timestamp, author, language, featured, likes;
	}

	public enum Order
	{
		id, title, type, description, timestamp, author, language, featured, likes, relevance;
	}

	public enum Type
	{
		asc, desc;
	}

	private static final String api = "1.0.0";
	private Task task = Task.fetch;

	private Fields[] fields = Fields.values();
	private Filters[] filters = { Filters.search };
	private Order by = Order.relevance;
	private Type type = Type.desc;
	private int limit = GeoGebraTubeAPI.STANDARD_RESULT_QUANTITY;
	private String query;

	/**
	 * Constructor for a Featured Materials Request
	 */
	public Request()
	{
		this.filters = new Filters[] { Filters.featured };
		this.by = Order.likes;
		this.type = Type.desc;
	}

	/**
	 * Constructor for a Search Request
	 * 
	 * @param String
	 *          query
	 */
	public Request(String query)
	{
		this.filters = new Filters[] { Filters.search };
		this.query = query;
	}

	/**
	 * Constructor for a Request by ID
	 * 
	 * @param filters
	 * @param by
	 */
	public Request(int ID)
	{
		this.filters = new Filters[] { Filters.id };
	}

	public String toJSONString()
	{
		// String testString =
		// "{\"request\":{\"-api\":\"1.0.0\",\"task\":{\"-type\":\"fetch\",\"fields\":{\"field\":[{\"-name\":\"id\"},{\"-name\":\"title\"},{\"-name\":\"timestamp\"},{\"-name\":\"author\"},{\"-name\":\"author_url\"},{\"-name\":\"url\"},{\"-name\":\"language\"},{\"-name\":\"featured\"},{\"-name\":\"likes\"}]},\"filters\":{\"field\":[{\"-name\":\"language\",\"#text\":\"en_US\"},{\"-name\": \"featured\"}]},\"order\":{\"-by\":\"timestamp\",\"-type\":\"asc\"},\"limit\":{\"-num\":\"10\"}}}}";
		JSONObject request = new JSONObject();
		JSONObject api = new JSONObject();
		JSONObject task = new JSONObject();
		JSONObject fields = new JSONObject();
		JSONArray field = new JSONArray();

		JSONObject filters = new JSONObject();
		JSONArray filter = new JSONArray();

		JSONObject order = new JSONObject();
		JSONObject limit = new JSONObject();

		api.put("-api", new JSONString(Request.api));
		task.put("-type", new JSONString(this.task.toString()));

		for (int i = 0; i < this.fields.length; i++)
		{
			JSONObject current = new JSONObject();
			current.put("-name", new JSONString(this.fields[i].toString()));
			field.set(i, current);
		}
		fields.put("field", field);

		for (int i = 0; i < this.filters.length; i++)
		{
			JSONObject current = new JSONObject();
			current.put("-name", new JSONString(this.filters[i].toString()));
			
			if(this.filters[i] == Filters.search)
			{
				current.put("#text", new JSONString(this.query));
			}
			filter.set(i, current);
		}
		filters.put("field", filter);

		order.put("-by", new JSONString(this.by.toString()));
		order.put("-type", new JSONString(this.type.toString()));
		limit.put("-num", new JSONString(String.valueOf(this.limit)));

		task.put("fields", fields);
		task.put("filters", filters);
		task.put("order", order);
		task.put("limit", limit);

		api.put("task", task);
		request.put("request", api);

		return request.toString();
	}
}
