package org.geogebra.common.move.ggtapi.models;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.models.MaterialRequest.Order;
import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONTokener;
import org.geogebra.common.move.ggtapi.operations.BackendAPI;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.move.ggtapi.requests.SyncCallback;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.common.util.debug.Log;

public class MarvlAPI implements BackendAPI {
	protected boolean available = true;
	protected boolean availabilityCheckDone = false;
	private String baseURL;
	private AuthenticationModel model;
	private String basicAuth = null; // for test only

	public MarvlAPI(String baseURL) {
		this.baseURL = baseURL;
	}

	@Override
	public void getItem(String string, MaterialCallbackI materialCallbackI) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean checkAvailable(LogInOperation logInOperation) {
		if (!availabilityCheckDone) {
			performCookieLogin(logInOperation);
		}
		return available;
	}

	@Override
	public String getLoginUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean parseUserDataFromResponse(GeoGebraTubeUser guser, String response) {
		try {
			JSONTokener tokener = new JSONTokener(response);
			JSONObject user = new JSONObject(tokener).getJSONObject("user");
			guser.setRealName(user.getString("displayname"));
			guser.setUserName(user.getString("username"));
			guser.setUserId(user.getInt("id"));
			guser.setIdentifier("");
			return true;
		} catch (Exception e) {
			Log.warn(e.getMessage());
		}
		return false;
	}

	@Override
	public void deleteMaterial(Material mat, MaterialCallbackI cb) {
		performRequest("DELETE", "/materials/" + mat.getSharingKeyOrId(), null, cb);
	}

	@Override
	public final void authorizeUser(final GeoGebraTubeUser user, final LogInOperation op,
			final boolean automatic) {
		HttpRequest request = UtilFactory.getPrototype().newHttpRequest();
		if (this.basicAuth != null) {
			request.setAuth(basicAuth);
		}
		request.sendRequestPost("GET",
				baseURL + "/auth",
				null, new AjaxCallback() {
					@Override
					public void onSuccess(String responseStr) {
						System.out.println(responseStr);
						try {
							MarvlAPI.this.availabilityCheckDone = true;

							MarvlAPI.this.available = true;

							// Parse the userdata from the response
							if (!parseUserDataFromResponse(user, responseStr)) {
								op.onEvent(new LoginEvent(user, false, automatic, responseStr));
								return;
							}

							op.onEvent(new LoginEvent(user, true, automatic, responseStr));

							// GeoGebraTubeAPID.this.available = false;
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

					@Override
					public void onError(String error) {
						System.out.println(error);
						MarvlAPI.this.availabilityCheckDone = true;
						MarvlAPI.this.available = false;
						op.onEvent(new LoginEvent(user, false, automatic, null));
					}
				});
	}

	@Override
	public void setClient(ClientInfo client) {
		this.model = client.getModel();
	}

	@Override
	public void sync(long i, SyncCallback syncCallback) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isCheckDone() {
		return this.availabilityCheckDone;
	}

	@Override
	public void setUserLanguage(String fontStr, String loginToken) {
		// TODO Auto-generated method stub
	}

	@Override
	public void shareMaterial(Material material, String to, String message, MaterialCallbackI cb) {
		// TODO Auto-generated method stub
	}

	@Override
	public void favorite(int id, boolean favorite) {
		// TODO Auto-generated method stub
	}

	@Override
	public String getUrl() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public void logout(String token) {
		// TODO not possible right now
	}

	@Override
	public void uploadLocalMaterial(Material mat, MaterialCallbackI cb) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean performCookieLogin(LogInOperation op) {
		this.authorizeUser(new GeoGebraTubeUser(""), op, true);
		return true;
	}

	@Override
	public void performTokenLogin(LogInOperation op, String token) {
		this.authorizeUser(new GeoGebraTubeUser(""), op, true);
	}

	@Override
	public void getUsersMaterials(MaterialCallbackI userMaterialsCB, MaterialRequest.Order order) {
		getUsersOwnMaterials(userMaterialsCB, order);
	}

	protected List<Material> parseMaterials(String responseStr) throws JSONException {
		ArrayList<Material> ret = new ArrayList<>();
		JSONTokener jst = new JSONTokener(responseStr);
		Object parsed = jst.nextValue();
		if (parsed instanceof JSONArray) {
			JSONArray arr = (JSONArray) parsed;
			for (int i = 0; i < arr.length(); i++) {
				Material mat = JSONParserGGT.prototype.toMaterial(arr.getJSONObject(i));
				ret.add(mat);
			}
		} else if (parsed instanceof JSONObject) {
			Material mat = JSONParserGGT.prototype.toMaterial((JSONObject) parsed);
			ret.add(mat);
		}
		return ret;
	}

	@Override
	public void getFeaturedMaterials(MaterialCallbackI userMaterialsCB) {
		// TODO Auto-generated method stub
	}

	@Override
	public void getUsersOwnMaterials(final MaterialCallbackI userMaterialsCB,
			MaterialRequest.Order order) {
		if (model == null) {
			userMaterialsCB.onError(new Exception("No user signed in"));
			return;
		}

		performRequest("GET",
				"/users/" + model.getUserId() + "/materials?limit=20&order=" + orderStr(order),
				null, userMaterialsCB);
	}

	private static String orderStr(Order order) {
		switch (order) {
		case timestamp:
			return "-modified";
		case created:
			return "-" + order.name();
		case title:
		case privacy:
			return order.name();
		default:
			return "title";
		}
	}

	private void performRequest(String method, String endpoint, String json,
			final MaterialCallbackI userMaterialsCB) {
		HttpRequest request = UtilFactory.getPrototype().newHttpRequest();
		request.setContentTypeJson();
		if (this.basicAuth != null) {
			request.setAuth(basicAuth);
		}
		request.sendRequestPost(method, baseURL + endpoint, json, new AjaxCallback() {
			@Override
			public void onSuccess(String responseStr) {
				try {
					userMaterialsCB.onLoaded(parseMaterials(responseStr), null);

					// GeoGebraTubeAPID.this.available = false;
				} catch (Exception e) {
					userMaterialsCB.onError(e);
				}

			}

			@Override
			public void onError(String error) {
				userMaterialsCB.onError(new Exception(error));
			}
		});

	}

	@Override
	public void uploadMaterial(int tubeID, String visibility, String text, String base64,
			MaterialCallbackI materialCallback, MaterialType type) {
		JSONObject request = new JSONObject();
		try {
			request.put("visibility", "S"); // TODO
			request.put("title", text);
			request.put("file", base64);
			request.put("type", type.name()); // TODO
		} catch (JSONException e) {
			materialCallback.onError(e);
		}
		this.performRequest("POST", "/materials", request.toString(), materialCallback);
	}

	@Override
	public void uploadRenameMaterial(Material material, MaterialCallbackI materialCallback) {
		JSONObject request = new JSONObject();
		try {
			request.put("title", material.getTitle());
		} catch (JSONException e) {
			materialCallback.onError(e);
		}
		this.performRequest("PATCH", "/materials/" + material.getSharingKeyOrId(),
				request.toString(), materialCallback);
	}

	@Override
	public void copy(Material material, MaterialCallbackI materialCallback) {
		this.performRequest("POST", "/materials/" + material.getSharingKeyOrId(), null,
				materialCallback);
	}

	public void setBasicAuth(String base64) {
		this.basicAuth = base64;
	}

}
