package org.geogebra.common.move.ggtapi.models;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONString;

/**
 * For Generating a JSON String for specific GeoGebratube API Requests
 * 
 * @author Matthias Meisinger
 */
public class MaterialRequest implements Request {

	enum Task {
		fetch;
	}

	public enum Fields {
		id, title, type, description, timestamp, author, author_id, url,
		url_direct, language, thumbnail, featured, likes, width, height,
		instructions_pre, instructions_post, ggbBase64, toolbar, menubar,
		inputbar, modified, visibility, favorite, is3d, spreadsheet, cas,
		graphics2, constprot, propcalc, dataanalysis, funcinsp, python, macro;
	}

	public enum Filters {
		id, title, search, type, description, timestamp, author, author_url,
		language, featured, likes, inbook, inws;
	}

	public enum Order {
		id, title, type, description, timestamp, author, language, featured,
		likes, relevance;
	}

	public enum Type {
		asc, desc;
	}

	private static final String api = "1.1.0";
	private Task task = Task.fetch;

	public Fields[] fields = new Fields[] { Fields.id, Fields.title,
			Fields.type, Fields.timestamp, Fields.author, Fields.author_id,
			Fields.url, Fields.url_direct, Fields.thumbnail, Fields.featured,
			Fields.likes, Fields.modified, Fields.visibility, Fields.favorite };
	public Filters[] filters = { Filters.search };
	public Map<Filters, String> filterMap = new HashMap<Filters, String>();
	public Order by = Order.relevance;
	public Type type = Type.desc;
	public int limit = GeoGebraTubeAPI.STANDARD_RESULT_QUANTITY;

	private JSONObject requestJSON = new JSONObject();
	private JSONObject apiJSON = new JSONObject();
	private JSONObject taskJSON = new JSONObject();
	private JSONObject fieldsJSON = new JSONObject();
	private JSONArray fieldJSON = new JSONArray();

	private JSONObject filtersJSON = new JSONObject();
	private JSONArray filterJSON = new JSONArray();

	private JSONObject orderJSON = new JSONObject();
	private JSONObject limitJSON = new JSONObject();
	private final AuthenticationModel model;
	private final ClientInfo client;
	public TreeSet<Filters> negFilters = new TreeSet<Filters>();

	/**
	 * Constructor for a Featured Materials Request
	 */
	public MaterialRequest(ClientInfo client) {
		this.client = client;
		this.model = client.getModel();
	}

	/**
	 * Constructor for a Search Request
	 * 
	 * @param query
	 *            search term or #id
	 */
	public MaterialRequest(String query, ClientInfo client) {
		this(client);
		this.filterMap.put(Filters.type, "ggb");
		if (query != null && query.startsWith("#")) {
			this.filters = new Filters[] { Filters.id };
			this.filterMap.put(Filters.id, query.substring(1));
			this.by = Order.timestamp;
		} else {
			this.filters = new Filters[] { Filters.search };
			this.filterMap.put(Filters.search, query);
		}
	}

	/**
	 * Constructor for a Request by ID
	 * 
	 * @param filters
	 * @param by
	 */
	public static MaterialRequest forId(String id, ClientInfo client) {
		MaterialRequest mr = new MaterialRequest(client);
		mr.fields = Fields.values();
		mr.by = Order.id;
		mr.filters = new Filters[] { Filters.id };
		mr.filterMap.put(Filters.type, "ggb");
		mr.filterMap.put(Filters.id, id + "");
		return mr;
	}

	public String toJSONString(ClientInfo client) {
		this.apiJSON.put("-api", new JSONString(MaterialRequest.api));
		this.taskJSON.put("-type", new JSONString(this.task.toString()));

		for (int i = 0; i < this.fields.length; i++) {
			JSONObject current = new JSONObject();
			current.put("-name", new JSONString(this.fields[i].toString()));
			this.fieldJSON.set(i, current);
		}

		this.fieldsJSON.put("field", this.fieldJSON);

		for (int i = 0; i < this.filters.length; i++) {
			JSONObject current = new JSONObject();
			current.put("-name", new JSONString(this.filters[i].toString()));
			if (this.negFilters.contains(filters[i])) {
				current.put("-comp", new JSONString("neq"));
			}
			if (this.filterMap.get(this.filters[i]) != null) {
				current.put("#text", new JSONString(this.filterMap
						.get(this.filters[i])));
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
		if (this.model != null && model.isLoggedIn()) {
			JSONObject login = new JSONObject();
			login.put("-token", model.getLoginToken());
			this.apiJSON.put("login", login);
		}
		if (this.client != null) {
			JSONObject clientJSON = new JSONObject();
			clientJSON.put("-id", client.getId());
			clientJSON.put("-width", client.getWidth() + "");
			clientJSON.put("-height", client.getHeight() + "");
			clientJSON.put("-type", client.getType());
			clientJSON.put("-language", client.getLanguage());
			this.apiJSON.put("client", clientJSON);
		}
		this.apiJSON.put("task", this.taskJSON);
		this.requestJSON.put("request", this.apiJSON);
		return this.requestJSON.toString();
	}

	public static MaterialRequest forUser(int userId, ClientInfo client) {
		MaterialRequest req = new MaterialRequest(client);
		req.filters = new Filters[] { Filters.author_url, Filters.type };
		req.filterMap.put(Filters.type, "link");
		req.negFilters.add(Filters.type);
		req.filterMap.put(Filters.author_url, userId + "");
		req.by = Order.relevance;
		return req;
	}

	public static MaterialRequest forCurrentUser(ClientInfo client) {
		MaterialRequest req = new MaterialRequest(client);
		req.filters = new Filters[] { Filters.type };
		req.filterMap.put(Filters.type, "link");
		req.negFilters.add(Filters.type);
		req.by = Order.relevance;
		return req;
	}

	public static MaterialRequest forCurrentUserGgb(ClientInfo client) {
		MaterialRequest req = new MaterialRequest(client);
		req.filters = new Filters[] { Filters.type };
		req.filterMap.put(Filters.type, "ggb");
		req.by = Order.relevance;
		return req;
	}

	public static MaterialRequest forFeatured(ClientInfo client) {
		MaterialRequest req = new MaterialRequest(client);
		req.filters = new Filters[] { Filters.featured, Filters.type };
		req.filterMap.put(Filters.type, "link");
		req.negFilters.add(Filters.type);
		req.filterMap.put(Filters.featured, "true");
		req.type = Type.desc;
		return req;
	}

	public static MaterialRequest forFeaturedGgb(ClientInfo client) {
		MaterialRequest req = new MaterialRequest(client);
		req.filters = new Filters[] { Filters.featured, Filters.type };
		req.filterMap.put(Filters.type, "ggb");
		req.filterMap.put(Filters.featured, "true");
		req.type = Type.desc;
		return req;
	}

	public static MaterialRequest searchGgb(ClientInfo client, String query) {
		MaterialRequest req = new MaterialRequest(client);
		req.filters = new Filters[] { Filters.search, Filters.type };
		req.filterMap.put(Filters.search, query);
		req.filterMap.put(Filters.type, "ggb");
		return req;
	}

	public static MaterialRequest forBook(int id, ClientInfo client) {
		MaterialRequest req = new MaterialRequest(client);
		req.filters = new Filters[] { Filters.inbook, Filters.type };
		req.filterMap.put(Filters.type, "link");
		req.negFilters.add(Filters.type);
		req.filterMap.put(Filters.inbook, id + "");
		req.by = Order.timestamp;
		req.type = Type.desc;
		return req;
	}

	public static MaterialRequest forWorksheet(int id, ClientInfo client) {
		MaterialRequest req = new MaterialRequest(client);
		req.filters = new Filters[] { Filters.inws, Filters.type };
		req.fields = Fields.values();
		req.filterMap.put(Filters.type, "link");
		req.negFilters.add(Filters.type);
		req.filterMap.put(Filters.inws, id + "");
		req.by = Order.timestamp;
		req.type = Type.desc;
		return req;
	}
}
