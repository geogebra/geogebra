package org.geogebra.common.move.ggtapi.operations;

import java.util.List;

import org.geogebra.common.move.ggtapi.models.ClientInfo;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.models.MaterialRequest;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.move.ggtapi.requests.SyncCallback;
import org.geogebra.common.util.AsyncOperation;

public interface BackendAPI {

	void getItem(String string, MaterialCallbackI materialCallbackI);

	boolean checkAvailable(LogInOperation logInOperation);

	String getLoginUrl();

	boolean parseUserDataFromResponse(GeoGebraTubeUser offline, String loadLastUser);

	/**
	 * @param mat
	 *            {@link Material}
	 * @param cb
	 *            {@link MaterialCallbackI}
	 */
	void deleteMaterial(Material mat, MaterialCallbackI cb);

	void authorizeUser(GeoGebraTubeUser user, LogInOperation logInOperation, boolean automatic);

	void setClient(ClientInfo client);

	void sync(long i, SyncCallback syncCallback);

	boolean isCheckDone();

	void setUserLanguage(String fontStr, String loginToken);

	/**
	 * Share material with particular user and send an email about it.
	 * 
	 * @param material
	 *            material
	 * @param to
	 *            recipient
	 * @param message
	 *            email message
	 * @param cb
	 *            callback
	 */
	void shareMaterial(Material material, String to, String message, MaterialCallbackI cb);

	/**
	 * @param id
	 *            material id
	 * @param favorite
	 *            whether to favorite or unfavorite
	 */
	void favorite(int id, boolean favorite);

	String getUrl();

	/**
	 * Log user out.
	 * 
	 * @param token
	 *            login token
	 */
	void logout(String token);

	/**
	 * Uploads a local saved file (web - localStorage; touch - device) to ggt
	 * 
	 * @param mat
	 *            {@link Material}
	 * @param cb
	 *            {@link MaterialCallbackI}
	 */
	void uploadLocalMaterial(Material mat, MaterialCallbackI cb);

	boolean performCookieLogin(LogInOperation op);

	void performTokenLogin(LogInOperation logInOperation, String token);

	void getUsersMaterials(MaterialCallbackI userMaterialsCB, MaterialRequest.Order order);

	void getFeaturedMaterials(MaterialCallbackI userMaterialsCB);

	void getUsersOwnMaterials(MaterialCallbackI userMaterialsCB, MaterialRequest.Order order);

	void uploadMaterial(String tubeID, String visibility, String text, String base64,
			MaterialCallbackI materialCallback, MaterialType saveType);

	void uploadRenameMaterial(Material material, MaterialCallbackI materialCallback);

	void copy(Material material, String title, MaterialCallbackI materialCallback);

	void setShared(Material material, String groupID, boolean shared,
			AsyncOperation<Boolean> callback);

	void getGroups(String materialID, AsyncOperation<List<String>> asyncOperation);

}
