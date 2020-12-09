package org.geogebra.common.move.ggtapi.operations;

import java.util.List;

import org.geogebra.common.move.ggtapi.models.AjaxCallback;
import org.geogebra.common.move.ggtapi.models.ClientInfo;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.models.MaterialRequest;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.move.ggtapi.requests.SyncCallback;
import org.geogebra.common.util.AsyncOperation;

/**
 * Common interface for backend connectors.
 */
public interface BackendAPI {

	/**
	 * Return a specific Material by its ID
	 *
	 * @param id
	 *            int ID or sharing key
	 * @param callback
	 *            {@link MaterialCallbackI}
	 */
	void getItem(String id, MaterialCallbackI callback);

	/**
	 * @param logInOperation
	 *            login operation
	 * @return whether the API is available; assume true if not tested
	 */
	boolean checkAvailable(LogInOperation logInOperation);

	/**
	 * TODO get this from somewhere else, makes only sense for Tube API
	 *
	 * @return login API url
	 */
	String getLoginUrl();

	/**
	 * Set properties of user from JSON.
	 *
	 * @param user
	 *            user object
	 * @param loadLastUser
	 *            JSON encoded user data
	 * @return success
	 */
	boolean parseUserDataFromResponse(GeoGebraTubeUser user, String loadLastUser);

	/**
	 * @param mat
	 *            {@link Material}
	 * @param cb
	 *            {@link MaterialCallbackI}
	 */
	void deleteMaterial(Material mat, MaterialCallbackI cb);

	/**
	 * Sends a request to the GeoGebraTube API to check if the login token which
	 * is defined in the specified GeoGebraTubeUser is valid.
	 *
	 * @param user
	 *            The user that should be authorized.
	 * @param logInOperation
	 *            login operation
	 * @param automatic
	 *            true if automatic
	 */
	void authorizeUser(GeoGebraTubeUser user, LogInOperation logInOperation, boolean automatic);

	/**
	 * @param clientInfo
	 *            client information
	 */
	void setClient(ClientInfo clientInfo);

	/**
	 * Synchronize a material.
	 *
	 * @param timestamp
	 *            timestamp
	 * @param syncCallback
	 *            callback
	 */
	void sync(long timestamp, SyncCallback syncCallback);

	/**
	 * @return whether availability check was done
	 */
	boolean isCheckDone();

	/**
	 * @param lang
	 *            user language
	 * @param token
	 *            login token
	 */
	void setUserLanguage(String lang, String token);

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

	/**
	 * TODO only makes sense for Tube API, get it from somewhere else
	 *
	 * @return base URL
	 */
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

	/**
	 * @param op
	 *            login operation
	 * @return whether cookie was found
	 */
	boolean performCookieLogin(LogInOperation op);

	/**
	 * Log user in using either cookie or given token.
	 *
	 * @param logInOperation
	 *            login operation
	 * @param token
	 *            stored token
	 */
	void performTokenLogin(LogInOperation logInOperation, String token);

	/**
	 * @param cb
	 *            {@link MaterialCallbackI}
	 * @param order
	 *            preferred order
	 */
	void getUsersMaterials(MaterialCallbackI cb, MaterialRequest.Order order);

	/**
	 * Returns materials in the given amount and order
	 *
	 * @param callback
	 *            {@link MaterialCallbackI}
	 */
	void getFeaturedMaterials(MaterialCallbackI callback);

	/**
	 * Get materials of currently logged in user
	 *
	 * @param cb
	 *            callback
	 * @param order
	 *            order
	 */
	void getUsersOwnMaterials(MaterialCallbackI cb, MaterialRequest.Order order);

	/**
	 * Get materials shared with currently logged in user
	 *
	 * @param cb
	 *            callback
	 * @param order
	 *            order
	 */
	void getSharedMaterials(final MaterialCallbackI cb,
			MaterialRequest.Order order);

	/**
	 * Uploads the actual opened application to ggt
	 *
	 * @param tubeID
	 *            tube id
	 * @param visibility
	 *            visibility string
	 *
	 * @param filename
	 *            String
	 * @param base64
	 *            base64 string
	 * @param cb
	 *            MaterialCallback
	 * @param type
	 *            material type
	 */
	void uploadMaterial(String tubeID, String visibility, String filename, String base64,
			MaterialCallbackI cb, MaterialType type);

	/**
	 * to rename materials on ggt; TODO no use of base64
	 *
	 * @param material
	 *            {@link Material}
	 * @param callback
	 *            {@link MaterialCallbackI}
	 */
	void uploadRenameMaterial(Material material, MaterialCallbackI callback);

	/**
	 * Copy existing material.
	 *
	 * @param material
	 *            Current material
	 * @param title
	 *            copy title
	 * @param materialCallback
	 *            callback
	 */
	void copy(Material material, String title, MaterialCallbackI materialCallback);

	/**
	 * Make material (not) shared with a group
	 *
	 * @param material
	 *            material
	 * @param groupID
	 *            group identifier
	 * @param shared
	 *            whether to share
	 * @param callback
	 *            callback; gets true if successful
	 */
	void setShared(Material material, String groupID, boolean shared,
			AsyncOperation<Boolean> callback);

	/**
	 * @param materialID
	 *            material ID
	 * @param asyncOperation
	 *            callback; gets list of groups we can share with
	 */
	void getGroups(String materialID, AsyncOperation<List<String>> asyncOperation);

	/**
	 * @param mat
	 *            material
	 * @return true if user owns the given material
	 */
	boolean owns(Material mat);

	/**
	 * @param student
	 *            whether user is a student
	 * @return whether user can share files
	 */
	boolean canUserShare(boolean student);

	/**
	 * @return whether anonymous user can open shared links
	 */
	boolean anonymousOpen();

	/**
	 * Get templates of current user
	 *
	 * @param cb
	 *            callback to process the response and fill the template material list
	 */
	void getTemplateMaterials(final MaterialCallbackI cb);

	/**
	 * Uploads and unzips an H5P file.
	 * @param base64 of the file
	 */
	void uploadAndUnzipH5P(String base64, AjaxCallback callback);
}
