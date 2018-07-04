package org.geogebra.common.move.ggtapi.models;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
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

	public MarvlAPI(String baseURL) {
		this.baseURL = baseURL;
	}

	@Override
	public void getItem(String string, MaterialCallbackI materialCallbackI) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean checkAvailable(LogInOperation logInOperation) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getLoginUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean parseUserDataFromResponse(GeoGebraTubeUser offline, String loadLastUser) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void deleteMaterial(Material mat, MaterialCallbackI cb) {
		// TODO Auto-generated method stub

	}

	@Override
	public final void authorizeUser(final GeoGebraTubeUser user, final LogInOperation op,
			final boolean automatic) {
		HttpRequest request = UtilFactory.getPrototype().newHttpRequest();
		request.sendRequestPost("GET",
				baseURL + "/auth",
				null, new AjaxCallback() {
					@Override
					public void onSuccess(String responseStr) {
						try {
							MarvlAPI.this.availabilityCheckDone = true;

							MarvlAPI.this.available = true;

							// Parse the userdata from the response
							if (!parseUserDataFromMarvlResponse(user, responseStr)) {
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
						MarvlAPI.this.availabilityCheckDone = true;
						MarvlAPI.this.available = false;
						op.onEvent(new LoginEvent(user, false, automatic, null));
					}
				});
	}

	protected boolean parseUserDataFromMarvlResponse(GeoGebraTubeUser guser, String response) {
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
	public void setClient(ClientInfo client) {
		// TODO Auto-generated method stub
	}

	@Override
	public void sync(long i, SyncCallback syncCallback) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isCheckDone() {
		// TODO Auto-generated method stub
		return false;
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
		// TODO Auto-generated method stub
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

}
