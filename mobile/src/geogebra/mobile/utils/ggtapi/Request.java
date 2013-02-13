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

	private JSONObject requestJSON = new JSONObject();
	private JSONObject apiJSON = new JSONObject();
	private JSONObject taskJSON = new JSONObject();
	private JSONObject fieldsJSON = new JSONObject();
	private JSONArray fieldJSON = new JSONArray();

	private JSONObject filtersJSON = new JSONObject();
	private JSONArray filterJSON = new JSONArray();

	private JSONObject orderJSON = new JSONObject();
	private JSONObject limitJSON = new JSONObject();

	private String query;
	private int id;

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
	public Request(int id)
	{
		this.filters = new Filters[] { Filters.id };
		this.id = id;
	}

	public String toJSONString()
	{
		this.apiJSON.put("-api", new JSONString(Request.api));
		this.taskJSON.put("-type", new JSONString(this.task.toString()));

		for (int i = 0; i < this.fields.length; i++)
		{
			JSONObject current = new JSONObject();
			current.put("-name", new JSONString(this.fields[i].toString()));
			this.fieldJSON.set(i, current);
		}
		
		this.fieldsJSON.put("field", this.fieldJSON);

		for (int i = 0; i < this.filters.length; i++)
		{
			JSONObject current = new JSONObject();
			current.put("-name", new JSONString(this.filters[i].toString()));

			if (this.filters[i] == Filters.search)
			{
				current.put("#text", new JSONString(this.query));
			}
			else if (this.filters[i] == Filters.id)
			{
				current.put("#text", new JSONString(String.valueOf(this.id)));
			}

			this.filterJSON.set(i, current);
		}

		this.filtersJSON.put("field", this.filterJSON);

		this.orderJSON.put("-by", new JSONString(this.by.toString()));
		this.orderJSON.put("-type", new JSONString(this.type.toString()));
		this.limitJSON.put("-num", new JSONString(String.valueOf(this.limit)));

		this.taskJSON.put("fields", this.fieldsJSON);
		this.taskJSON.put("filters", this.filtersJSON);
		this.taskJSON.put("order", this.orderJSON);
		this.taskJSON.put("limit", this.limitJSON);

		this.apiJSON.put("task", this.taskJSON);
		this.requestJSON.put("request", this.apiJSON);

		return this.requestJSON.toString();
	}
}
