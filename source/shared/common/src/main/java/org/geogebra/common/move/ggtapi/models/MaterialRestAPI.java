package org.geogebra.common.move.ggtapi.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.main.Localization;
import org.geogebra.common.move.ggtapi.GroupIdentifier;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONTokener;
import org.geogebra.common.move.ggtapi.operations.BackendAPI;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.HttpMethod;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * API connector for the MARVL restful API
 */
public class MaterialRestAPI implements BackendAPI {
	private static final int SEARCH_COUNT = 30;

	/** whether availability check request was sent */
	private boolean availabilityCheckDone = false;
	private final String baseURL;
	private AuthenticationModel model;

	private final Service service;

	public static final String marvlUrl = "https://api.geogebra.org/v1.0";
	public static final String CSRF_COOKIE_NAME = "X-Csrf-Token";

	public static class Tag {
		public static final String PHONE_2D = "ft.phone-2d";
		public static final String PHONE_3D = "ft.phone-3d";
	}

	/**
	 * @param service backend service (Marvl/Mebis)
	 * @param baseURL URL of the API; endpoints append eg. "/materials" to it
	 */
	public MaterialRestAPI(String baseURL, Service service) {
		this.baseURL = baseURL;
		this.service = service;
	}

	/**
	 * @param id item ID
	 * @param callback callback
	 * @return HTTP request
	 */
	public HttpRequest getItem(String id, MaterialCallbackI callback) {
		return performRequest(HttpMethod.GET, "/materials/" + id, null,
				new MaterialCallbackI() {
					@Override
					public void onLoaded(List<Material> result, Pagination meta) {
						if (result.size() == 1 && result.get(0).getType() == MaterialType.ws) {
							getWorksheetItems(result.get(0), callback);
						} else {
							callback.onLoaded(result, meta);
						}
					}

					@Override
					public void onError(Throwable exception) {
						callback.onError(exception);
					}
				});
	}

	@Override
	public String getLoginUrl() {
		return null;
	}

	@Override
	public boolean parseUserDataFromResponse(GeoGebraTubeUser guser, String response) {
		try {
			JSONTokener tokener = new JSONTokener(response);
			JSONObject user = new JSONObject(tokener).getJSONObject("user");
			guser.setUserName(user.getString("displayname"));
			guser.setUserId(user.getInt("id"));
			guser.setIdentifier("");
			guser.setStudent(!"1".equals(user.getString("isTeacher")));
			guser.setLanguage(user.optString("langUi"));
			guser.setJWTToken(user.optString("jwtToken"));
			ArrayList<GroupIdentifier> allGroups = new ArrayList<>();
			addGroups(user, "allClasses", allGroups, GroupIdentifier.GroupCategory.CLASS);
			addGroups(user, "allCourses", allGroups, GroupIdentifier.GroupCategory.COURSE);
			guser.setGroups(allGroups);
			return true;
		} catch (Exception e) {
			Log.warn(e.getMessage());
		}
		return false;
	}

	private void addGroups(JSONObject user, String allClasses,
			ArrayList<GroupIdentifier> allGroups, GroupIdentifier.GroupCategory cat)
			throws JSONException {
		if (user.has(allClasses)) {
			JSONArray classList = user.getJSONArray(allClasses);
			allGroups.addAll(stringList(classList, cat));
		}
	}

	/**
	 * @param classList JSON array
	 * @return Java array
	 * @throws JSONException if array contains objects other than strings
	 */
	private static ArrayList<GroupIdentifier> stringList(JSONArray classList,
			GroupIdentifier.GroupCategory category) throws JSONException {
		ArrayList<GroupIdentifier> groups = new ArrayList<>();
		for (int i = 0; i < classList.length(); i++) {
			groups.add(new GroupIdentifier(classList.getString(i), category));
		}
		return groups;
	}

	/**
	 * @param mat {@link Material}
	 * @param callback {@link MaterialCallbackI}
	 */
	public void deleteMaterial(final Material mat, final MaterialCallbackI callback) {
		String json = service.getDeletionJson(mat.getType());
		HttpMethod method = json == null ? HttpMethod.DELETE : HttpMethod.PATCH;
		performWithAuthentication(method, "/materials/" + mat.getSharingKeySafe(), json,
				new AjaxCallback() {
					@Override
					public void onSuccess(String responseStr) {
						// we don't parse the response here
						ArrayList<Material> mats = new ArrayList<>();
						mats.add(mat);
						callback.onLoaded(mats, null);
					}

					@Override
					public void onError(String error) {
						callback.onError(new Exception(error));
					}
				});

	}

	@Override
	public final void authorizeUser(final GeoGebraTubeUser user, final LogInOperation op,
			final boolean automatic) {

		HttpRequest request = service.createRequest(model);
		request.setAuth(user.getLoginToken());
		request.sendRequestPost(HttpMethod.GET.name(), baseURL + "/auth", null,
				new AjaxCallback() {
					@Override
					public void onSuccess(String responseStr) {
						try {
							MaterialRestAPI.this.availabilityCheckDone = true;

							// Parse the userdata from the response
							if (!parseUserDataFromResponse(user, responseStr)) {
								op.onEvent(new LoginEvent(user, false, automatic, responseStr));
								return;
							}
							String auth = request.getResponseHeader("Authorization");
							user.setJWTToken(auth.replace("Bearer ", ""));
							op.onEvent(new LoginEvent(user, true, automatic, responseStr));
						} catch (Exception e) {
							Log.error(e.getMessage());
						}
					}

					@Override
					public void onError(String error) {
						Log.error(error);
						MaterialRestAPI.this.availabilityCheckDone = true;

						op.onEvent(new LoginEvent(user, false, automatic, null));
					}
				});
	}

	@Override
	public void setClient(ClientInfo client) {
		this.model = client.getModel();
	}

	@Override
	public boolean isCheckDone() {
		return this.availabilityCheckDone;
	}

	@Override
	public void setUserLanguage(String fontStr, String loginToken) {
		// not supported
	}

	@Override
	public String getUrl() {
		return this.baseURL;
	}

	@Override
	public void logout(String token) {
		// open platform dependent popup
	}

	@Override
	public void uploadLocalMaterial(Material mat, MaterialCallbackI cb) {
		// offline materials not supported
	}

	/**
	 * Combines user's own resources with featured ones (if there's not enough own)
	 * @param callback callback
	 * @param order ordering
	 * @return request that can be canceled
	 */
	public HttpRequest getUsersMaterials(MaterialCallbackI callback, ResourceOrdering order) {
		return getUsersOwnMaterials(new MaterialCallbackI() {
			@Override
			public void onLoaded(List<Material> result, Pagination meta) {
				if (result.size() >= SEARCH_COUNT) {
					callback.onLoaded(result, meta);
				} else {
					getFeaturedMaterials(addTo(result, callback));
				}
			}

			@Override
			public void onError(Throwable exception) {
				getFeaturedMaterials(callback);
			}
		}, order);
	}

	private MaterialCallbackI addTo(List<Material> result, MaterialCallbackI userMaterialsCB) {
		return new MaterialCallbackI() {
			@Override
			public void onLoaded(List<Material> res, Pagination meta) {
				result.addAll(res);
				userMaterialsCB.onLoaded(result, meta);
			}

			@Override
			public void onError(Throwable exception) {
				userMaterialsCB.onError(exception);
			}
		};
	}

	private Pagination parseMaterialCount(String responseStr) throws JSONException {
		JSONTokener jst = new JSONTokener(responseStr);
		Object parsed = jst.nextValue();
		if (parsed instanceof JSONObject && ((JSONObject) parsed).has("from")) {
			int from = ((JSONObject) parsed).getInt("from");
			int to = ((JSONObject) parsed).getInt("to");
			int total = ((JSONObject) parsed).getInt("total");
			return new Pagination(from, to, total);
		}
		return null;
	}

	/**
	 * @param responseStr JSON encoded material or list of materials
	 * @return list of materials
	 * @throws JSONException when structure of JSON is invalid
	 */
	private List<Material> parseMaterials(String responseStr) throws JSONException {
		ArrayList<Material> ret = new ArrayList<>();
		JSONTokener jst = new JSONTokener(responseStr);
		Object parsed = jst.nextValue();
		if (parsed instanceof JSONObject) {
			if (((JSONObject) parsed).has("materials")) {
				JSONArray materials = ((JSONObject) parsed).getJSONArray("materials");
				addAll(materials, ret);
			} else if (((JSONObject) parsed).has("hits")) {
				JSONArray materials = ((JSONObject) parsed).getJSONArray("hits");
				addAll(materials, ret);
			} else {
				Material mat = JSONParserGGT.prototype.toMaterial((JSONObject) parsed);
				ret.add(mat);
			}
		} else if (parsed instanceof JSONArray) {
			addAll((JSONArray) parsed, ret);
		}
		return ret;
	}

	private void addAll(JSONArray materials, ArrayList<Material> ret) throws JSONException {
		for (int i = 0; i < materials.length(); i++) {
			Material mat = JSONParserGGT.prototype.toMaterial(materials.getJSONObject(i));
			ret.add(mat);
		}
	}

	/**
	 * Get featured materials
	 * @param callback callback
	 * @return HTTP request
	 */
	public HttpRequest getFeaturedMaterials(MaterialCallbackI callback) {
		return performRequest(HttpMethod.GET, "/search/applets?size=" + SEARCH_COUNT,
				null, callback);
	}

	/**
	 * Get featured materials by tag
	 * @param tag tag
	 * @param callback callback
	 * @return HTTP request
	 */
	public HttpRequest getFeaturedMaterialsByTag(String tag, MaterialCallbackI callback) {
		String endpoint = "/search/materials?"
				+ "size=" + SEARCH_COUNT + "&"
				+ "filter=tag:" + tag;
		return performRequest(HttpMethod.GET, endpoint, null, callback);
	}

	/**
	 * Get materials created by user
	 * @param callback callback
	 * @param order order
	 * @return HTTP request
	 */
	public HttpRequest getUsersOwnMaterials(final MaterialCallbackI callback,
			ResourceOrdering order) {
		if (model == null) {
			callback.onError(new Exception("No user signed in"));
			return UtilFactory.getPrototype().newHttpRequest();
		}

		return performRequest(HttpMethod.GET,
				"/users/" + model.getUserId()
						+ "/materials?limit=50&embed=creator&order="
						+ orderStr(order) + service.getSearchMaterialFilter(),
				null, callback);
	}

	/**
	 * Get materials created by user + shared with user's group
	 * @param callback callback
	 * @param order order
	 * @param offset number of materials to skip
	 */
	public void getUsersAndSharedMaterials(MaterialCallbackI callback, ResourceOrdering order,
			int offset) {
		if (model == null) {
			callback.onError(new Exception("No user signed in"));
			return;
		}

		performRequest(HttpMethod.GET,
				"/users/" + model.getUserId()
						+ "/materials?format=page&type=all&limit=50&offset=" + offset
						+ "&embed=creator&order="
						+ orderStr(order) + service.getSearchMaterialFilter(),
				null, callback);
	}

	private static String orderStr(ResourceOrdering order) {
		switch (order) {
		case modified:
		case created:
			return "-" + order.name();
		default:
		case title:
			return order.name();
		}
	}

	private HttpRequest performRequest(final HttpMethod method, String endpoint, String json,
			final MaterialCallbackI userMaterialsCB) {

		AjaxCallback callback = new AjaxCallback() {
			@Override
			public void onSuccess(String responseStr) {
				try {
					userMaterialsCB
							.onLoaded(parseMaterials(responseStr),
									parseMaterialCount(responseStr));
				} catch (Exception e) {
					Log.debug(e);
					userMaterialsCB.onError(e);
				}
			}

			@Override
			public void onError(String error) {
				userMaterialsCB.onError(new Exception(error));
			}
		};

		if (method == HttpMethod.GET) {
			HttpRequest request = service.createRequest(model);
			request.setContentTypeJson();
			Runnable sendRequest = () -> request.sendRequestPost(
					method.name(), baseURL + endpoint, json, callback);
			if (model != null) {
				model.refreshToken(request, sendRequest);
			} else {
				sendRequest.run();
			}
			return request;
		} else {
			return performWithAuthentication(method, endpoint, json, callback);
		}
	}

	private HttpRequest performWithAuthentication(HttpMethod method, String endpoint,
			String json, AjaxCallback callback) {
		HttpRequest request = service.createRequest(model);
		request.setContentTypeJson();
		model.refreshToken(request, () -> getAndAddCSRFToken(new AjaxCallback() {
			@Override
			public void onSuccess(String token) {
				try {
					request.setRequestCSRFHeader(token);
					request.sendRequestPost(method.name(), baseURL + endpoint, json,
							getCsrfUpdatingCallback(callback));
				} catch (Exception e) {
					callback.onError(e.getMessage());
				}
			}

			@Override
			public void onError(String error) {
				callback.onError(error);
			}
		}));
		return request;
	}

	private AjaxCallback getCsrfUpdatingCallback(AjaxCallback callback) {
		return new AjaxCallback() {
			@Override
			public void onSuccess(String response) {
				callback.onSuccess(response);
				model.storeCSRFToken(model.getCookie(CSRF_COOKIE_NAME));
			}

			@Override
			public void onError(String error) {
				callback.onError(error);
			}
		};
	}

	private void getAndAddCSRFToken(AjaxCallback callback) {
		if (!service.requiresCSRF()) {
			callback.onSuccess("");
		} else if (StringUtil.empty(model.getCSRFToken()) && model.getUserId() > -1) {
			requestCsrfTokenCookie(model.getUserId(), callback);
		} else {
			callback.onSuccess(model.getCSRFToken());
		}
	}

	@Override
	public void uploadMaterial(String tubeID, String visibility, String text, String base64,
			MaterialCallbackI materialCallback, MaterialType type, boolean isMultiuser) {
		JSONObject request = new JSONObject();
		try {
			request.put("visibility", visibility); // per docs "S" is the only
			// supported visibility
			request.put("title", text);
			request.put("file", base64);
			if (StringUtil.emptyOrZero(tubeID)) {
				request.put("type", type.toString());
			} else if (service.hasMultiuser()) {
				request.put("multiuser", isMultiuser);
			}
		} catch (JSONException e) {
			materialCallback.onError(e);
		}
		if (!StringUtil.emptyOrZero(tubeID)) {
			performRequest(HttpMethod.PATCH, "/materials/" + tubeID, request.toString(),
					materialCallback);
		} else {
			performRequest(HttpMethod.POST, "/materials", request.toString(), materialCallback);
		}
	}

	private void requestCsrfTokenCookie(int userId, AjaxCallback callback) {
		HttpRequest request = service.createRequest(model);
		request.setContentTypeJson();

		String endpoint = "/users/" + userId + "/token";
		request.sendRequestPost(HttpMethod.POST.name(), baseURL + endpoint, null,
				new AjaxCallback() {
					@Override
					public void onSuccess(String ignore) {
						model.storeCSRFToken(model.getCookie(CSRF_COOKIE_NAME));
						callback.onSuccess(model.getCSRFToken());
					}

					@Override
					public void onError(String error) {
						callback.onError("Could not get CSRF token: " + error);
					}
				});
	}

	/**
	 * @param material renamed material
	 * @param materialCallback callback
	 */
	public void uploadRenameMaterial(Material material, MaterialCallbackI materialCallback) {
		JSONObject request = new JSONObject();
		try {
			request.put("title", material.getTitle());
		} catch (JSONException e) {
			materialCallback.onError(e);
		}
		performRequest(HttpMethod.PATCH, "/materials/" + material.getSharingKeySafe(),
				request.toString(), materialCallback);
	}

	/**
	 * Copy existing material.
	 * @param material Current material
	 * @param title copy title
	 * @param materialCallback callback
	 */
	public void copy(Material material, final String title,
			final MaterialCallbackI materialCallback) {
		performRequest(HttpMethod.POST, "/materials/" + material.getSharingKeySafe(), null,
				new MaterialCallbackI() {

					@Override
					public void onLoaded(List<Material> result, Pagination meta) {
						if (result.size() == 1) {
							result.get(0).setTitle(title);
							uploadRenameMaterial(result.get(0), materialCallback);
						}
					}

					@Override
					public void onError(Throwable exception) {
						materialCallback.onError(exception);
					}
				});
	}

	/**
	 * @param localization localization
	 * @param title original title
	 * @return title with "Copy of" prefix or numeric suffix
	 */
	public static String getCopyTitle(Localization localization, String title) {
		if (title.matches(localization.getPlain("CopyOfA", ".*"))) {
			int i = 2;
			String stem = title;
			if (title.endsWith(")")) {
				String numeric = title.substring(title.lastIndexOf('(') + 1, title.length() - 1);
				try {
					i = Integer.parseInt(numeric) + 1;
					stem = title.substring(0, title.lastIndexOf('(') - 1);
				} catch (RuntimeException e) {
					// ignore
				}
			}
			return stem + " (" + i + ")";
		}
		return localization.getPlain("CopyOfA", title);
	}

	/**
	 * @param m material
	 * @param groupID group ID
	 * @param shared whether to share
	 * @param callback callback
	 */
	public void setShared(Material m, GroupIdentifier groupID, boolean shared,
			final AsyncOperation<Boolean> callback) {
		performWithAuthentication(shared ? HttpMethod.POST : HttpMethod.DELETE,
				"/materials/" + m.getSharingKeySafe() + "/groups/"
						+ groupID.name + "?category=" + groupID.getCategory(), null,
				new AjaxCallback() {
					@Override
					public void onSuccess(String responseStr) {
						callback.callback(true);
					}

					@Override
					public void onError(String error) {
						callback.callback(false);
					}
				});
	}

	/**
	 * @param materialID material ID
	 * @param category group category
	 * @param callback get list of groups in given category the material is shared with
	 */
	public void getGroups(String materialID, GroupIdentifier.GroupCategory category,
			AsyncOperation<List<GroupIdentifier>> callback) {
		String path = service.getGroupsEndpoint(materialID, category);
		if (path == null) {
			return;
		}
		HttpRequest request = service.createRequest(model);

		request.sendRequestPost(HttpMethod.GET.name(),
				baseURL + path, null,
				new AjaxCallback() {
					@Override
					public void onSuccess(String responseStr) {
						JSONArray groups;
						try {
							groups = new JSONArray(new JSONTokener(responseStr));
							callback.callback(stringList(groups, category));
						} catch (JSONException e) {
							callback.callback(null);
						}

					}

					@Override
					public void onError(String error) {
						callback.callback(null);
					}
				});
	}

	/**
	 * @param mat material
	 * @return true if user owns the given material
	 */
	public boolean owns(Material mat) {
		if (model == null || !model.isLoggedIn()) {
			return false;
		}
		return mat.getCreator().getId() <= 0
				|| mat.getCreator().getId() == model.getUserId();
	}

	@Override
	public boolean canUserShare(boolean student) {
		return !student;
	}

	@Override
	public boolean anonymousOpen() {
		return false;
	}

	/**
	 * @param templateMaterialsCB template callback
	 */
	public void getTemplateMaterials(final MaterialCallbackI templateMaterialsCB) {
		if (model == null || !model.isLoggedIn()) {
			templateMaterialsCB.onLoaded(new ArrayList<>(), null);
			return;
		}

		performRequest(HttpMethod.GET,
				service.getGgsTemplateEndpoint(model.getUserId()),
				null, templateMaterialsCB);
	}

	/**
	 * Search for materials containing the String query
	 * @param query search String
	 * @param callback {@link MaterialCallbackI}
	 * @return HTTP request
	 */
	public HttpRequest search(String query, MaterialCallbackI callback) {
		String encodedQuery = UtilFactory.getPrototype().newURLEncoder().encode(query);
		return performRequest(HttpMethod.GET, "/search/applets?size="
				+ SEARCH_COUNT + "&query=" + encodedQuery, null, callback);
	}

	/**
	 * Get all ggb elements from a worksheet.
	 * @param parent worksheet
	 * @param materialCallback callback
	 */
	public void getWorksheetItems(Material parent, MaterialCallbackI materialCallback) {
		HttpRequest request = service.createRequest(model);
		request.setContentTypeJson();

		request.sendRequestPost(HttpMethod.GET.name(), baseURL + "/materials/"
				+ parent.getSharingKeySafe(), null, new AjaxCallback() {
			@Override
			public void onSuccess(String responseStr) {
				try {
					JSONObject json = new JSONObject(new JSONTokener(responseStr));
					JSONArray elements = json.getJSONArray("elements");
					ArrayList<Material> materials = new ArrayList<>();
					for (int i = 0; i < elements.length(); i++) {
						JSONObject jsonObject = elements.getJSONObject(i);
						if ("G".equals(jsonObject.optString("type"))) {
							Material mat = JSONParserGGT.worksheetToMaterial(parent, jsonObject);
							materials.add(mat);
						}
					}
					materialCallback.onLoaded(materials, null);
				} catch (Exception e) {
					materialCallback.onError(e);
				}
			}

			@Override
			public void onError(String error) {
				materialCallback.onError(new Exception(error));
			}
		});
	}

	/**
	 * @param material resource
	 * @return actions available for given resource
	 */
	public Collection<ResourceAction> getActions(Material material) {
		return service.getActions(owns(material), model.getLoggedInUser() != null
				&& !model.getLoggedInUser().isStudent());
	}
}
