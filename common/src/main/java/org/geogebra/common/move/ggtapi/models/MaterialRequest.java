package org.geogebra.common.move.ggtapi.models;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

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
		id, title, type, description, timestamp, author, author_id, url, url_direct, language,

		thumbnail, featured, likes, width, height, instructions_pre, instructions_post,

		ggbBase64, toolbar, menubar, inputbar, stylebar,

		modified, visibility, favorite, is3d, spreadsheet,

		cas, graphics2, constprot, propcalc, dataanalysis, funcinsp, macro, sharing_key,

		preview_url, elemcnt_applet, appname;
	}

	public enum Filters {
		id, title, search, type, description, timestamp, author, author_url, language, featured,

		likes, inbook, inws, author_id, appname;
	}

	public enum Order {
		id, title, type, description, timestamp, author, language, featured, likes,
		relevance, privacy, created;
	}

	public enum Type {
		asc, desc;
	}

	private static final String api = "1.1.0";
	private Task task = Task.fetch;

	public Fields[] fields = new Fields[] { Fields.id, Fields.title,
			Fields.type, Fields.timestamp, Fields.author, Fields.author_id,
			Fields.url, Fields.url_direct, Fields.thumbnail, Fields.featured,
			Fields.likes, Fields.modified, Fields.visibility, Fields.favorite,
			Fields.sharing_key, Fields.appname };
	public Filters[] filters = { Filters.search };
	public Map<Filters, String> filterMap = new HashMap<>();
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
	public TreeSet<Filters> negFilters = new TreeSet<>();

	/**
	 * Constructor for a Featured Materials Request
	 * 
	 * @param client
	 *            client
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
	 * @param client
	 *            client
	 */
	public MaterialRequest(String query, ClientInfo client) {
		this(client);
		this.filterMap.put(Filters.type, "ggb");
		if (isSearchForId(query)) {
			searchById(client, query);
		} else {
			searchByTerm(client, query);
		}
	}

	private void searchById(ClientInfo client, String query) {
		String id = query.substring(1);
		if (isNotesApp(client.getAppName())) {
			searchNotesById(id);
		} else {
			searchDefaultById(id);
		}
		this.by = Order.timestamp;
	}

	private void searchNotesById(String id) {
		filters = new Filters[] {Filters.id, Filters.appname, Filters.author_id };
		filterMap.put(Filters.id, id);
		filterMap.put(Filters.appname, "notes");
		filterMap.put(Filters.author_id, client.getModel().getUserId() + "");
	}

	private void searchDefaultById(String id) {
		this.filters = new Filters[] { Filters.id };
		this.filterMap.put(Filters.id, id);
	}

	private void searchByTerm(ClientInfo client, String query) {
		if (isNotesApp(client.getAppName())) {
			searchNodesByTerm(query);
		} else {
			searchDefaultByTerm(query);
		}
	}

	private void searchDefaultByTerm(String query) {
		filters = new Filters[] { Filters.search };
		filterMap.put(Filters.search, query);
	}

	private void searchNodesByTerm(String query) {
		filters = new Filters[] {Filters.search};
		filterMap.put(Filters.search, query);
	}

	private boolean isSearchForId(String query) {
		return query != null && query.startsWith("#");
	}

	/**
	 * Constructor for a Request by ID
	 * 
	 * @param id
	 *            id of request
	 * @param client
	 *            client
	 * 
	 * @return material request
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

	@Override
	public String toJSONString(ClientInfo clientInfo) {
		try {
			this.apiJSON.put("-api", MaterialRequest.api);
			this.taskJSON.put("-type", this.task.toString());

			for (int i = 0; i < this.fields.length; i++) {
				JSONObject current = new JSONObject();
				current.put("-name", this.fields[i].toString());
				this.fieldJSON.put(current);
			}

			this.fieldsJSON.put("field", this.fieldJSON);

			for (int i = 0; i < this.filters.length; i++) {
				JSONObject current = new JSONObject();
				current.put("-name", this.filters[i].toString());
				if (this.negFilters.contains(filters[i])) {
					current.put("-comp", "neq");
				}
				if (this.filterMap.get(this.filters[i]) != null) {
					current.put("#text", this.filterMap.get(this.filters[i]));
				}

				this.filterJSON.put(current);
			}

			this.filtersJSON.put("field", this.filterJSON);

			this.orderJSON.put("-by", this.by.toString());
			this.orderJSON.put("-type", this.type.toString());
			this.limitJSON.put("-num", String.valueOf(this.limit));

			this.taskJSON.put("fields", this.fieldsJSON);
			this.taskJSON.put("filters", this.filtersJSON);
			this.taskJSON.put("order", this.orderJSON);
			this.taskJSON.put("limit", this.limitJSON);
			// user may be logged in (e.g. Mebis), but have no token for
			// Materials
			if (this.model != null && model.isLoggedIn()
					&& !StringUtil.empty(model.getLoginToken())) {
				JSONObject login = new JSONObject();
				login.put("-token", model.getLoginToken());
				this.apiJSON.put("login", login);
			}
			if (this.client != null) {
				JSONObject clientJSON = new JSONObject();
				clientJSON.put("-id", clientInfo.getId());
				clientJSON.put("-width", clientInfo.getWidth() + "");
				clientJSON.put("-height", clientInfo.getHeight() + "");
				clientJSON.put("-type", clientInfo.getType());
				clientJSON.put("-language", clientInfo.getLanguage());
				this.apiJSON.put("client", clientJSON);
			}
			this.apiJSON.put("task", this.taskJSON);
			this.requestJSON.put("request", this.apiJSON);
			return this.requestJSON.toString();
		} catch (Exception e) {
			Log.debug("problem building request: " + e.getMessage());
			return null;
		}
	}

	/**
	 * @param userId
	 *            user ID
	 * @param client
	 *            api client
	 * @return request for user's materials
	 */
	public static MaterialRequest forUser(int userId, ClientInfo client) {
		MaterialRequest req = new MaterialRequest(client);
		req.filters = new Filters[] { Filters.author_url, Filters.type };
		req.filterMap.put(Filters.type, "link");
		req.negFilters.add(Filters.type);
		req.filterMap.put(Filters.author_url, userId + "");
		req.by = Order.relevance;
		return req;
	}

	/**
	 * Gets personalized selection of materials (own, favorite, featured).
	 * 
	 * @param client
	 *            api client
	 * @return request
	 */
	public static MaterialRequest forCurrentUser(ClientInfo client) {
		MaterialRequest req = isNotesApp(client.getAppName())
				? createNotesRequest(client)
				: createTypeReqestForUser(client);
		req.by = Order.relevance;
		return req;
	}

	private static boolean isNotesApp(String appName) {
		return "notes".equalsIgnoreCase(appName);
	}

	private static MaterialRequest createNotesRequest(ClientInfo client) {
		MaterialRequest req = new MaterialRequest(client);
		req.filters = new Filters[] { Filters.appname, Filters.author_id };
		req.filterMap.put(Filters.appname, "notes");
		req.filterMap.put(Filters.author_id, client.getModel().getUserId() + "");
		return req;
	}

	private static MaterialRequest createTypeReqestForUser(ClientInfo client) {
		MaterialRequest req = new MaterialRequest(client);
		req.filters = new Filters[]{Filters.type};
		req.filterMap.put(Filters.type, "link");
		req.negFilters.add(Filters.type);
		return req;
	}

	/**
	 * Gets personalized selection of materials (own, favorite, featured).
	 * 
	 * @param client
	 *            api client
	 * @return request for personalized selection; filter just ggbs
	 */
	public static MaterialRequest forCurrentUserGgb(ClientInfo client) {
		MaterialRequest req = new MaterialRequest(client);
		req.filters = new Filters[] { Filters.type };
		req.filterMap.put(Filters.type, "ggb");
		req.by = Order.relevance;
		return req;
	}

	/**
	 * @param client
	 *            client info
	 * @return request for featured materials
	 */
	public static MaterialRequest forFeatured(ClientInfo client) {
		MaterialRequest req = isNotesApp(client.getAppName())
				? createNotesRequest(client)
				: createFeaturedRequest(client);
		req.filterMap.put(Filters.featured, "true");
		req.type = Type.desc;
		return req;
	}

	private static MaterialRequest createFeaturedRequest(ClientInfo client) {
		MaterialRequest req = new MaterialRequest(client);
		req.filters = new Filters[] { Filters.featured, Filters.type };
		req.filterMap.put(Filters.type, "link");
		req.negFilters.add(Filters.type);
		return req;
	}

	/**
	 * @param client
	 *            client info
	 * @return request for featured materials, filter just ggbs
	 */
	public static MaterialRequest forFeaturedGgb(ClientInfo client) {
		MaterialRequest req = new MaterialRequest(client);
		req.filters = new Filters[] { Filters.featured, Filters.type };
		req.filterMap.put(Filters.type, "ggb");
		req.filterMap.put(Filters.featured, "true");
		req.type = Type.desc;
		return req;
	}

	/**
	 * @param client
	 *            client info
	 * @return request for featured materials, filter just ggs
	 */
	public static MaterialRequest forFeaturedGgs(ClientInfo client) {
		MaterialRequest req = new MaterialRequest(client);
		req.filters = new Filters[] { Filters.featured, Filters.type };
		req.filterMap.put(Filters.type, "ggs");
		req.filterMap.put(Filters.featured, "true");
		req.type = Type.desc;
		return req;
	}

	/**
	 * @param client
	 *            API client
	 * @param query
	 *            search term
	 * @return search request
	 */
	public static MaterialRequest searchGgb(ClientInfo client, String query) {
		MaterialRequest req = new MaterialRequest(client);
		req.filters = new Filters[] { Filters.search, Filters.type };
		req.filterMap.put(Filters.search, query);
		req.filterMap.put(Filters.type, "ggb");
		return req;
	}

	/**
	 * @param id
	 *            book id
	 * @param client
	 *            api client
	 * @return request for book's contents
	 */
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

	/**
	 * @param id
	 *            worksheet id
	 * @param client
	 *            api client
	 * @return request for ggb elements of given worksheet
	 */
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

	public int getUserId() {
		return model.getUserId();
	}
}
